package com.trainerapp.core.erg

import com.trainerapp.core.domain.ControlResult
import com.trainerapp.core.domain.DisconnectReason
import com.trainerapp.core.domain.TrainerCandidate
import com.trainerapp.core.domain.TrainerClient
import com.trainerapp.core.domain.TrainerState
import com.trainerapp.core.store.AppSettings
import com.trainerapp.core.store.SettingsStore
import com.trainerapp.core.store.TrainerIdentity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** A visible notice that the requested target was clamped to the valid range. */
data class ClampNotice(val requested: Int, val applied: Int)

/** What the link/controller is doing, rendered by the ride screen. */
enum class ErgLink { DISCONNECTED, CONNECTING, RIDING, PAUSED, RECONNECTING }

/**
 * The single source of truth for ERG state. The UI renders this and calls
 * only the controller's operations — it never writes to the trainer itself.
 */
data class ErgState(
    val targetWatts: Int,
    val confirmedWatts: Int? = null,
    val paused: Boolean = false,
    val clampedNotice: ClampNotice? = null,
    val link: ErgLink = ErgLink.DISCONNECTED,
    val reconnectAttempt: Int = 0,
    /** Honest release state for deliberate disconnect: null = not attempted,
     *  true = trainer acknowledged min load, false = release unconfirmed. */
    val releaseConfirmed: Boolean? = null,
)

/**
 * Sole writer of trainer target power (design boundary). Owns the remembered
 * target, the paused flag, requested-vs-confirmed tracking, coalescing, the
 * reconnect policy, and the min-load-then-disconnect sequence.
 *
 * Grill decisions implemented here: D3 (ERG engages automatically at the
 * remembered wattage, default 120 W), D4/D5 (3 W steps, 50–600 W clamp with
 * visible notice, ~300 ms coalescing), D6 (auto-reconnect with increasing
 * delays, target reapplied unless paused), D7 (pause = min load with live
 * telemetry; survives reconnect and restart; edits while paused update the
 * saved target only), D9 (min-load before disconnect, honest release state).
 */
class ErgController(
    private val client: TrainerClient,
    private val store: SettingsStore,
    private val scope: CoroutineScope,
    private val minLoadWatts: Int = DEFAULT_MIN_LOAD_WATTS,
) {

    companion object {
        const val MIN_TARGET_WATTS = 50
        const val MAX_TARGET_WATTS = 600
        const val STEP_WATTS = 3
        const val DEBOUNCE_MS = 300L
        const val DEFAULT_MIN_LOAD_WATTS = 50
        /** Increasing delays, then steady-state (grill decision D6). */
        val RECONNECT_BACKOFF_MS = longArrayOf(1_000, 2_000, 5_000, 10_000, 30_000)
    }

    private val _state = MutableStateFlow(ErgState(targetWatts = AppSettings().targetWatts))
    val state: StateFlow<ErgState> = _state

    private var rememberedCandidate: TrainerCandidate? = null
    private var pendingTarget: Int? = null
    private var writeInFlight = false
    private var debounceJob: Job? = null
    private var reconnectJob: Job? = null
    private var deliberatelyDisconnecting = false
    private var lastLinkWasConnected = false

    init {
        val saved = store.load()
        _state.value = _state.value.copy(targetWatts = saved.targetWatts, paused = saved.paused)
        scope.launch {
            client.state.collect { onTrainerState(it) }
        }
    }

    /** Convenience for the devices screen: the remembered trainer identity. */
    fun rememberedTrainer(): TrainerIdentity? = store.load().trainer

    /**
     * Connects to the selected trainer and engages ERG automatically: at the
     * remembered target when riding, or at the minimum load when paused
     * (pause survives reconnect — D7).
     */
    suspend fun connect(candidate: TrainerCandidate) {
        rememberedCandidate = candidate
        deliberatelyDisconnecting = false
        _state.value = _state.value.copy(link = ErgLink.CONNECTING, releaseConfirmed = null)
        client.connect(candidate)
        // Persist the remembered-trainer identity ONLY when the connect landed
        // on a trainer protocol — a non-trainer device (e.g. a sensor routed
        // here by mistake, observed live in the 003 ride when the strap hit
        // this path and transiently clobbered the remembered Hammer) must
        // never replace the real trainer. Failed/Unsupported connects keep
        // the previous remembered identity.
        val st = client.state.value
        if (st is com.trainerapp.core.domain.TrainerState.Connected &&
            st.protocol != com.trainerapp.core.domain.TrainerProtocol.Unsupported
        ) {
            store.update { it.copy(trainer = TrainerIdentity(candidate.name, candidate.platformId)) }
        }
    }

    /** Sets the target directly (numeric field; clamped with a visible notice). */
    suspend fun setTarget(watts: Int) = applyTarget(watts)

    /** Adjusts the target by a delta (±3 W buttons, ↑/↓ keys). */
    suspend fun adjust(deltaWatts: Int) = applyTarget(_state.value.targetWatts + deltaWatts)

    private suspend fun applyTarget(requested: Int) {
        val clamped = requested.coerceIn(MIN_TARGET_WATTS, MAX_TARGET_WATTS)
        val notice = if (clamped != requested) ClampNotice(requested, clamped) else null
        _state.value = _state.value.copy(targetWatts = clamped, clampedNotice = notice ?: _state.value.clampedNotice)
        store.update { it.copy(targetWatts = clamped) }
        if (_state.value.paused) return // saved target only; Resume applies it (D7)
        if (!isConnected()) return
        pendingTarget = clamped
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(DEBOUNCE_MS)
            writePending()
        }
    }

    /** Pause: request the trainer's minimum load, keep telemetry alive (D7). */
    suspend fun pause() {
        if (!isConnected()) {
            _state.value = _state.value.copy(paused = true)
            store.update { it.copy(paused = true) }
            return
        }
        _state.value = _state.value.copy(paused = true)
        store.update { it.copy(paused = true) }
        writeNow(minLoadWatts)
    }

    /** Resume: reapply the remembered target (the only path that applies it after pause). */
    suspend fun resume() {
        _state.value = _state.value.copy(paused = false)
        store.update { it.copy(paused = false) }
        if (isConnected()) writeNow(_state.value.targetWatts)
    }

    /**
     * Deliberate disconnect: request minimum load first, then release the
     * link; cancels reconnection. The UI never claims release unless the
     * min-load write was acknowledged (D9).
     */
    suspend fun disconnect() {
        deliberatelyDisconnecting = true
        reconnectJob?.cancel()
        debounceJob?.cancel()
        pendingTarget = null
        val acknowledged = if (isConnected()) writeNow(minLoadWatts) else false
        client.disconnect()
        _state.value = _state.value.copy(
            link = ErgLink.DISCONNECTED,
            confirmedWatts = null,
            releaseConfirmed = acknowledged,
        )
    }

    fun clearClampNotice() {
        _state.value = _state.value.copy(clampedNotice = null)
    }

    private fun isConnected(): Boolean =
        client.state.value is TrainerState.Connected

    private suspend fun writeNow(watts: Int): Boolean {
        pendingTarget = watts
        debounceJob?.cancel()
        return writePending()
    }

    /**
     * Single-writer gate: at most one control write in flight; coalesced
     * rapid adjustments send only the final value (D5).
     */
    private suspend fun writePending(): Boolean {
        if (writeInFlight) return false
        val value = pendingTarget ?: return false
        pendingTarget = null
        writeInFlight = true
        val result = try {
            client.setTargetPower(value)
        } finally {
            writeInFlight = false
        }
        val confirmed = result is ControlResult.Acknowledged || result is ControlResult.Applied
        _state.value = _state.value.copy(
            confirmedWatts = if (confirmed) value else null,
        )
        if (!confirmed && _state.value.link == ErgLink.RIDING) {
            // The write failed but the link is alive: retry the pending value once.
            pendingTarget = value
        }
        return confirmed
    }

    private fun onTrainerState(trainerState: TrainerState) {
        when {
            trainerState is TrainerState.Connected -> {
                lastLinkWasConnected = true
                reconnectJob?.cancel()
                val paused = _state.value.paused
                _state.value = _state.value.copy(
                    link = if (paused) ErgLink.PAUSED else ErgLink.RIDING,
                    reconnectAttempt = 0,
                    releaseConfirmed = null,
                )
                // Engagement on connect (D3/D7): remembered target when riding,
                // minimum load when paused. Never a silent no-op.
                scope.launch {
                    if (paused) writeNow(minLoadWatts) else writeNow(_state.value.targetWatts)
                }
            }
            trainerState is TrainerState.Disconnected &&
                trainerState.reason != DisconnectReason.USER_REQUESTED &&
                lastLinkWasConnected && !deliberatelyDisconnecting -> {
                lastLinkWasConnected = false
                startReconnecting()
            }
            trainerState is TrainerState.Disconnected && trainerState.reason == DisconnectReason.USER_REQUESTED -> {
                lastLinkWasConnected = false
                if (!deliberatelyDisconnecting) {
                    _state.value = _state.value.copy(link = ErgLink.DISCONNECTED, confirmedWatts = null)
                }
            }
            else -> Unit
        }
    }

    /**
     * Retry with increasing delays until reconnected or the user disconnects
     * (D6). On reconnect, onTrainerState re-engages ERG honoring pause.
     */
    private fun startReconnecting() {
        reconnectJob?.cancel()
        _state.value = _state.value.copy(link = ErgLink.RECONNECTING, confirmedWatts = null)
        reconnectJob = scope.launch {
            var attempt = 0
            while (!deliberatelyDisconnecting && client.state.value is TrainerState.Disconnected) {
                val candidate = rememberedCandidate ?: break
                val delayMs = RECONNECT_BACKOFF_MS[attempt.coerceAtMost(RECONNECT_BACKOFF_MS.size - 1)]
                attempt++
                _state.value = _state.value.copy(reconnectAttempt = attempt)
                delay(delayMs)
                if (deliberatelyDisconnecting) break
                try {
                    client.connect(candidate)
                } catch (_: Exception) {
                    // Link still down; loop with the next backoff step.
                }
            }
        }
    }
}
