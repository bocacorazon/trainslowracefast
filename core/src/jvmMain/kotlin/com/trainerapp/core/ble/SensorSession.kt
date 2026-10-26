package com.trainerapp.core.ble

import com.trainerapp.core.domain.TrainerTelemetry
import com.trainerapp.core.sensor.HeartRateMeasurement
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/** Lifecycle of one sensor (non-trainer) device session. */
sealed interface SensorSessionState {
    data object Disconnected : SensorSessionState
    data object Connecting : SensorSessionState
    data object Connected : SensorSessionState
    data class Reconnecting(val attempt: Int) : SensorSessionState
}

/**
 * One sensor's connection lifecycle over the shared [BleBackend] —
 * CONCURRENT with (and independent of) the trainer session and any other
 * sensor session. Built for the COOSPO H808S strap (standard Heart Rate
 * service 0x180D / 0x2A37, 2026-10-25 capture) but generic over the
 * notify-able telemetry characteristic.
 *
 * Reconnect policy mirrors the 002 trainer schedule (increasing delays,
 * steady state) but is FULLY INDEPENDENT: a dropped/reconnecting strap never
 * touches trainer state (003 requirement: per-device reconnect).
 */
class SensorSession(
    private val backend: BleBackend,
    val deviceKey: String,
    private val telemetryCharacteristicUuid: String,
    dispatcher: CoroutineDispatcher,
    private val timeMillis: () -> Long = { System.currentTimeMillis() },
    private val staleAfterMillis: Long = 3_000,
    private val backoffMillis: LongArray = longArrayOf(1_000, 2_000, 5_000, 10_000, 30_000),
    /** Shorter than the trainer's window: a sensor that is not ready yet
     *  should fail fast so the reconnect loop retries quickly instead of
     *  burning the full trainer window per attempt. */
    private val awaitHandshakeMillis: Long = 3_000,
) {

    /** Owns its coroutines (the 002 client pattern) so [close] stops everything. */
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _state = MutableStateFlow<SensorSessionState>(SensorSessionState.Disconnected)
    val state: StateFlow<SensorSessionState> = _state.asStateFlow()

    private val _telemetry = MutableStateFlow(TrainerTelemetry())
    val telemetry: StateFlow<TrainerTelemetry> = _telemetry.asStateFlow()

    private var collectJob: Job? = null
    private var reconnectJob: Job? = null
    private var watchJob: Job? = null
    private var reconnectAttempt = 0
    private var deliberatelyDisconnected = false

    /**
     * Connects the sensor, discovers its GATT inventory, and subscribes to
     * its telemetry characteristic. Idempotent while connected/connecting.
     */
    suspend fun connect() {
        if (_state.value != SensorSessionState.Disconnected) return
        deliberatelyDisconnected = false
        _state.value = SensorSessionState.Connecting
        try {
            backend.connect(deviceKey, awaitHandshakeMillis)
        } catch (e: Exception) {
            _state.value = SensorSessionState.Disconnected
            scheduleReconnect()
            return
        }
        // Discover and subscribe; a missing characteristic is a hard failure
        // for this session (the device is not a usable source of its type).
        val services = try {
            backend.discoverServices(deviceKey)
        } catch (e: Exception) {
            _state.value = SensorSessionState.Disconnected
            scheduleReconnect()
            return
        }
        val char = services
            .flatMap { it.characteristics }
            .firstOrNull { it.uuid.lowercase() == telemetryCharacteristicUuid.lowercase() }
        if (char == null) {
            _state.value = SensorSessionState.Disconnected
            return // not a telemetry source of this type; no retry loop
        }
        try {
            backend.setNotify(deviceKey, char.uuid, true)
        } catch (e: Exception) {
            _state.value = SensorSessionState.Disconnected
            scheduleReconnect()
            return
        }
        reconnectAttempt = 0
        _state.value = SensorSessionState.Connected

        // Drop watcher (003 ride observation: a power-cycled strap must
        // recover by itself): watch the backend's connection map and enter
        // the reconnect loop the moment this device's link drops.
        watchJob?.cancel()
        watchJob = scope.launch {
            backend.connectionStates.collect { map ->
                if (map[deviceKey] != true &&
                    _state.value == SensorSessionState.Connected
                ) {
                    onUnexpectedDrop()
                }
            }
        }
        collectJob?.cancel()
        collectJob = scope.launch {
            backend.charValues(deviceKey, char.uuid).collect { bytes ->
                val parsed = HeartRateMeasurement.parse(bytes) ?: return@collect
                _telemetry.value = TrainerTelemetry(
                    heartRateBpm = parsed.heartRateBpm,
                    lastUpdatedAtMillis = timeMillis(),
                    stale = false,
                )
            }
        }
        scope.launch {
            while (_state.value is SensorSessionState.Connected) {
                delay((staleAfterMillis / 2).coerceAtLeast(100))
                val t = _telemetry.value
                if (t.lastUpdatedAtMillis > 0 &&
                    timeMillis() - t.lastUpdatedAtMillis > staleAfterMillis
                ) {
                    _telemetry.value = t.copy(stale = true)
                }
            }
        }
    }

    /**
     * Releases the session permanently: cancels the collect loop, the stale
     * ticker, and any in-flight reconnect. No further connect() after close.
     */
    fun close() {
        scope.cancel()
    }

    /** Deliberate disconnect: cancels reconnect loops, drops subscriptions. */
    suspend fun disconnect() {
        deliberatelyDisconnected = true
        reconnectJob?.cancel()
        collectJob?.cancel()
        runCatching {
            backend.setNotify(deviceKey, telemetryCharacteristicUuid, false)
        }
        runCatching { backend.disconnect(deviceKey) }
        _state.value = SensorSessionState.Disconnected
        _telemetry.value = TrainerTelemetry()
    }

    /**
     * Unexpected drop (the backend reported this device disconnected):
     * enter the reconnect loop. Trainer state is untouched by design —
     * this class never references it.
     */
    fun onUnexpectedDrop() {
        if (deliberatelyDisconnected) return
        if (_state.value == SensorSessionState.Disconnected ||
            _state.value is SensorSessionState.Reconnecting
        ) return
        collectJob?.cancel()
        _state.value = SensorSessionState.Disconnected
        scheduleReconnect()
    }

    private fun scheduleReconnect() {
        if (deliberatelyDisconnected) return
        reconnectJob?.cancel()
        val attempt = ++reconnectAttempt
        _state.value = SensorSessionState.Reconnecting(attempt)
        val wait = backoffMillis[(attempt - 1).coerceAtMost(backoffMillis.size - 1)]
        reconnectJob = scope.launch {
            delay(wait)
            if (!deliberatelyDisconnected) connect()
        }
    }
}