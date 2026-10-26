package com.trainerapp.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.trainerapp.core.ble.BleBackend
import com.trainerapp.core.ble.BlueFalconBleBackend
import com.trainerapp.core.ble.BlueFalconTrainerClient
import com.trainerapp.core.ble.SensorSession
import com.trainerapp.core.ble.SensorSessionState
import com.trainerapp.core.domain.TrainerTelemetry
import com.trainerapp.core.erg.ErgController
import com.trainerapp.core.store.JvmSettingsFile
import com.trainerapp.core.store.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/** Standard Heart Rate Measurement characteristic (H808S capture, 2026-10-25). */
const val HEART_RATE_MEASUREMENT_UUID = "00002a37-0000-1000-8000-00805f9b34fb"

/**
 * The single wiring point for the multi-device session (design boundary: the
 * composables only ever see domain types, strings, flows and lambdas — never
 * BLE/GATT types). ONE shared [BleBackend] serves the trainer client AND the
 * sensor sessions, so trainer + strap hold concurrent connections over one
 * BlueZ/sdbus stack.
 */
class MultiDeviceSession(
    val trainerClient: BlueFalconTrainerClient,
    private val settingsStore: SettingsStore,
    private val scope: CoroutineScope,
) {
    /** ONE shared backend (the trainer client's) for every session. */
    private val backend: BleBackend = trainerClient.backend

    var sensorSession: SensorSession? = null
        private set

    /** Name of the connected (or last-connected) sensor, for the UI rows. */
    var sensorName: String? = null
        private set

    /** The connected sensor's stable platformId (the persisted HR source). */
    var sensorPlatformId: String? = null
        private set

    /** BLE-free session status text for the UI: connected / reconnecting (attempt N) / … */
    private val _sensorStatusText = MutableStateFlow("")
    val sensorStatusText: StateFlow<String> = _sensorStatusText

    /** The strap's telemetry (domain type) — null while no sensor session exists. */
    val sensorTelemetry: StateFlow<TrainerTelemetry>?
        get() = sensorSession?.telemetry

    private var statusJob: Job? = null

    /**
     * Connects a sensor candidate; true only when it subscribed — i.e. the
     * device really offers the heart-rate measurement (capability-derived:
     * the subscription only succeeds when 0x2A37 is in its GATT inventory).
     */
    suspend fun connectSensor(candidate: com.trainerapp.core.domain.TrainerCandidate): Boolean {
        val key = trainerClient.deviceKeyFor(candidate.platformId) ?: return false
        val existing = sensorSession
        val session = if (existing != null && existing.deviceKey == key) {
            existing.connect()
            existing
        } else {
            existing?.close()
            SensorSession(
                backend = backend,
                deviceKey = key,
                telemetryCharacteristicUuid = HEART_RATE_MEASUREMENT_UUID,
                dispatcher = Dispatchers.Default,
            ).also { it.connect() }
        }
        statusJob?.cancel()
        statusJob = scope.launch {
            session.state.collect { st ->
                _sensorStatusText.value = when (st) {
                    SensorSessionState.Connected -> "connected"
                    SensorSessionState.Connecting -> "connecting"
                    is SensorSessionState.Reconnecting -> "reconnecting (attempt ${st.attempt})"
                    SensorSessionState.Disconnected -> "disconnected"
                }
            }
        }
        val connected = session.state.value == SensorSessionState.Connected
        if (connected) {
            sensorSession = session
            sensorName = candidate.name
            sensorPlatformId = candidate.platformId
        }
        return connected
    }

    /** The strap's deliberate disconnect (per-device; trainer untouched). */
    suspend fun disconnectSensor() {
        sensorSession?.disconnect()
    }

    /** Persists the HR source assignment (the dropdown's manual override). */
    fun rememberHeartRateSource(platformId: String?) {
        settingsStore.update { it.copy(sources = it.sources.copy(heartRate = platformId)) }
    }

    /** The persisted HR source platformId, if any. */
    fun rememberedHeartRate(): String? = settingsStore.load().sources.heartRate

    /** Disconnect-all (grill D10): trainer min-load release FIRST, then the strap. */
    suspend fun disconnectAll(erg: ErgController) {
        erg.disconnect()
        sensorSession?.disconnect()
    }

    /**
     * Launch auto-connect (grill D6): a bounded scan FIRST (it populates the
     * platformId→device maps — a remembered platformId cannot resolve without
     * one), then the trainer (ERG engages automatically), then the remembered
     * HR source. Sensor failures never disturb the trainer.
     */
    suspend fun autoConnectRemembered(erg: ErgController) {
        val settings = settingsStore.load()
        if (settings.trainer == null && settings.sources.heartRate == null) return
        val found = withTimeoutOrNull(15_000) { trainerClient.scan().toList() }
        settings.trainer?.let { remembered ->
            found?.firstOrNull { it.platformId == remembered.platformId }?.let { candidate ->
                erg.connect(candidate)
            }
        }
        settings.sources.heartRate?.let { pid ->
            found?.firstOrNull { it.platformId == pid }?.let { candidate ->
                connectSensor(candidate)
            }
        }
    }
}

fun main() = application {
    val scope = rememberCoroutineScope()
    val settingsStore = remember { SettingsStore(JvmSettingsFile.default()) }
    val trainerClient = remember { BlueFalconTrainerClient(BlueFalconBleBackend()) }
    val erg = remember { ErgController(trainerClient, settingsStore, scope) }
    val session = remember { MultiDeviceSession(trainerClient, settingsStore, scope) }

    Window(title = "TrainerApp", onCloseRequest = ::exitApplication) {
        TrainerApp(
            erg = erg,
            trainerClient = trainerClient,
            settingsStore = settingsStore,
            session = session,
        )
    }

    // Grill decision D6: launch auto-connects the remembered trainer (ERG
    // engages at the remembered wattage) and then the remembered HR strap.
    LaunchedEffect(Unit) {
        scope.launch { session.autoConnectRemembered(erg) }
    }
}