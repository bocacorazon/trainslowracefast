package com.trainerapp.core.ble

import com.trainerapp.core.domain.ControlResult
import com.trainerapp.core.domain.DisconnectReason
import com.trainerapp.core.domain.TrainerCandidate
import com.trainerapp.core.domain.TrainerClient
import com.trainerapp.core.domain.TrainerProtocol
import com.trainerapp.core.domain.TrainerState
import com.trainerapp.core.domain.TrainerTelemetry
import com.trainerapp.core.protocol.GattCharacteristic
import com.trainerapp.core.protocol.GattInventory
import com.trainerapp.core.protocol.GattService
import com.trainerapp.core.protocol.ProtocolClassifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Linux/BlueZ [TrainerClient] implementation over [BleBackend] (Blue Falcon
 * sdbus engine in production, a fake in contract tests).
 *
 * All Blue Falcon types stay in [BlueFalconBleBackend]; this class only sees
 * the transport seam and domain types.
 *
 * Control path resolution (from the captured Hammer H1 layout):
 *  - FTMS: control point 0x2AD9 (standard frame via the codec)
 *  - CycleOps: vendor control char ca31a533-... (Saris vendor service),
 *    falling back to a026e005-... (inside the Cycling Power service)
 */
class BlueFalconTrainerClient(
    private val backend: BleBackend,
    private val codec: ControlCodec = BestEffortSarisCodec,
    private val timeMillis: () -> Long = { System.currentTimeMillis() },
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val ackTimeoutMillis: Long = 1_000,
    private val staleAfterMillis: Long = 3_000,
    private val telemetryPollIntervalMillis: Long = 1_000,
) : TrainerClient {

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _state = MutableStateFlow<TrainerState>(TrainerState.Idle)
    override val state: StateFlow<TrainerState> = _state

    private val _telemetry = MutableStateFlow(TrainerTelemetry())
    override val telemetry: StateFlow<TrainerTelemetry> = _telemetry

    private val platformIdToDeviceKey = ConcurrentHashMap<String, String>()

    @Volatile
    private var activeKey: String? = null
    @Volatile
    private var telemetryChar: String? = null
    @Volatile
    private var speedChar: String? = null
    @Volatile
    private var speedCharNotified: Boolean = false
    @Volatile
    private var controlChar: String? = null
    @Volatile
    private var controlIndicates: Boolean = false
    @Volatile
    private var protocol: TrainerProtocol = TrainerProtocol.Unsupported

    private val ackSeq = AtomicLong(0)
    private var sessionJobs: List<Job> = emptyList()

    override suspend fun scan(): Flow<TrainerCandidate> = flow {
        _state.value = TrainerState.Scanning
        backend.startScan()
        try {
            backend.scanResults.collect { results ->
                for (r in results) {
                    val pid = platformIdFor(r.deviceKey)
                    platformIdToDeviceKey[pid] = r.deviceKey
                    emit(
                        TrainerCandidate(
                            name = r.name ?: "Unknown",
                            platformId = pid,
                            rssi = r.rssi ?: 0,
                            advertisedServices = r.advertisedServices,
                        )
                    )
                }
            }
        } finally {
            backend.stopScan()
            if (_state.value is TrainerState.Scanning) {
                _state.value = TrainerState.Idle
            }
        }
    }

    override suspend fun connect(candidate: TrainerCandidate) {
        val key = platformIdToDeviceKey[candidate.platformId]
            ?: run {
                _state.value = TrainerState.Failed(
                    com.trainerapp.core.domain.TrainerError.ConnectionFailed
                )
                return
            }
        _state.value = TrainerState.Connecting
        try {
            backend.connect(key)
        } catch (e: Exception) {
            _state.value = TrainerState.Failed(
                com.trainerapp.core.domain.TrainerError.ConnectionFailed
            )
            return
        }
        activeKey = key
        _state.value = TrainerState.DiscoveringServices
        try {
            val services = backend.discoverServices(key)

            // Telemetry: subscribe to Cycling Power Measurement (0x2A63) or
            // Indoor Bike Data (0x2A36) notify.
            telemetryChar = null
            speedChar = null
            val verified = HashMap<String, MutableSet<String>>()
            for (short in listOf(ProtocolClassifier.CYCLING_POWER_MEASUREMENT, ProtocolClassifier.INDOOR_BIKE_DATA)) {
                val c = findChar(services, short) ?: continue
                try {
                    backend.setNotify(key, c.uuid, true)
                    telemetryChar = c.uuid
                    verified.getOrPut(c.uuid) { mutableSetOf() } += "notify"
                    break
                } catch (e: Exception) {
                    // try the next telemetry characteristic
                }
            }

            // Speed: 0x2A65 — prefer notify, fall back to polling (handled in the session).
            val speed = findChar(services, ProtocolClassifier.SPEED)
            if (speed != null) {
                try {
                    backend.setNotify(key, speed.uuid, true)
                    speedChar = speed.uuid
                    speedCharNotified = true
                    verified.getOrPut(speed.uuid) { mutableSetOf() } += "notify"
                } catch (e: Exception) {
                    speedChar = speed.uuid
                    speedCharNotified = false
                }
            }

            // Control: FTMS control point, or the CycleOps vendor chars (priority order).
            val inventory0 = toGattInventory(services)
            val candidateProtocol = ProtocolClassifier.classify(inventory0)
            val controlCandidates = when (candidateProtocol) {
                TrainerProtocol.Ftms -> listOf(ProtocolClassifier.FTMS_CONTROL_POINT)
                is TrainerProtocol.CycleOpsProprietary -> listOf(
                    ProtocolClassifier.CYCLEOPS_VENDOR_CONTROL,
                    ProtocolClassifier.CYCLEOPS_POWER_SERVICE_CONTROL,
                )

                else -> emptyList()
            }
            controlChar = null
            controlIndicates = false
            val candidates = controlCandidates.mapNotNull { findChar(services, it) }
            for (c in candidates) {
                if (controlChar != null) break
                try {
                    backend.setIndicate(key, c.uuid, true)
                    controlChar = c.uuid
                    controlIndicates = true
                    val s = verified.getOrPut(c.uuid) { mutableSetOf() }
                    s += "indicate"
                    s += "write"
                } catch (e: Exception) {
                    // try the next candidate
                }
            }
            // Fall back to plain writes (no indication -> Written, never Acknowledged)
            // so e.g. an FTMS control point without indicate still gets a control path.
            if (controlChar == null && candidates.isNotEmpty()) {
                controlChar = candidates.first().uuid
            }

            val finalInventory = mergeVerifiedProperties(inventory0, verified)
            val finalProtocol = ProtocolClassifier.classify(finalInventory)
            protocol = finalProtocol
            _state.value = TrainerState.Connected(finalProtocol)
            startSessionJobs(key, services)
        } catch (e: Exception) {
            activeKey = null
            _state.value = TrainerState.Failed(
                com.trainerapp.core.domain.TrainerError.DiscoveryFailed
            )
            runCatching { backend.disconnect(key) }
        }
    }

    override suspend fun setTargetPower(watts: Int): ControlResult {
        val st = _state.value
        if (st !is TrainerState.Connected) return ControlResult.Failed("not connected")
        if (!st.protocol.controllable) return ControlResult.Failed("protocol has no control path")
        return controlWrite(codec.encodeTargetPower(watts, st.protocol))
    }

    /**
     * Requests the trainer's minimum supported load. The Hammer's min-load
     * opcode was not determinable from the GATT capture; this writes the
     * codec's best-effort min-load frame. Verify on hardware (smoke-ride).
     */
    suspend fun requestMinLoad(): ControlResult {
        val st = _state.value
        if (st !is TrainerState.Connected) return ControlResult.Failed("not connected")
        if (!st.protocol.controllable) return ControlResult.Failed("protocol has no control path")
        return controlWrite(codec.encodeMinLoad(st.protocol))
    }

    private suspend fun controlWrite(frame: ByteArray): ControlResult {
        if (frame.isEmpty()) return ControlResult.Failed("no control frame for protocol")
        val key = activeKey ?: return ControlResult.Failed("not connected")
        val char = controlChar ?: return ControlResult.Failed("no control characteristic")
        return try {
            val before = ackSeq.get()
            backend.writeCharacteristic(key, char, frame)
            val acked = if (controlIndicates) {
                withTimeoutOrNull(ackTimeoutMillis) {
                    while (ackSeq.get() <= before) {
                        delay(20)
                    }
                } != null
            } else {
                false
            }
            if (acked) ControlResult.Acknowledged else ControlResult.Written
        } catch (e: Exception) {
            ControlResult.Failed(e.message ?: "control write failed")
        }
    }

    override suspend fun disconnect() {
        val key = activeKey ?: return
        _state.value = TrainerState.Disconnecting
        stopSessionJobs()
        val t = telemetryChar
        val s = speedChar
        val c = controlChar
        runCatching { if (t != null) backend.setNotify(key, t, false) }
        runCatching { if (s != null) backend.setNotify(key, s, false) }
        runCatching { if (c != null) backend.setIndicate(key, c, false) }
        runCatching { backend.disconnect(key) }
        activeKey = null
        telemetryChar = null
        speedChar = null
        speedCharNotified = false
        controlChar = null
        controlIndicates = false
        protocol = TrainerProtocol.Unsupported
        _state.value = TrainerState.Disconnected(DisconnectReason.USER_REQUESTED)
    }

    private fun startSessionJobs(key: String, services: List<BleServiceInfo>) {
        stopSessionJobs()
        val jobs = mutableListOf<Job>()

        // Telemetry pipeline.
        telemetryChar?.let { t ->
            jobs += scope.launch {
                backend.charValues(key, t).collect { v ->
                    val p = CyclingPowerMeasurement.parse(v) ?: return@collect
                    updateTelemetry { it.copy(watts = p.watts, lastUpdatedAtMillis = timeMillis(), stale = false) }
                }
            }
        }

        // Speed pipeline: notify if it was enabled, else poll.
        speedChar?.let { s ->
            if (speedCharNotified) {
                jobs += scope.launch {
                    backend.charValues(key, s).collect { v ->
                        val spd = SpeedCharacteristic.parse(v) ?: return@collect
                        updateTelemetry { it.copy(speedKph = spd, lastUpdatedAtMillis = timeMillis(), stale = false) }
                    }
                }
            } else {
                jobs += scope.launch {
                    while (true) {
                        delay(telemetryPollIntervalMillis)
                        val v = runCatching { backend.readCharacteristic(key, s) }.getOrNull() ?: continue
                        val spd = SpeedCharacteristic.parse(v) ?: continue
                        updateTelemetry { it.copy(speedKph = spd, lastUpdatedAtMillis = timeMillis(), stale = false) }
                    }
                }
            }
        }

        // Control indication tracker (write + indicate -> ack).
        controlChar?.let { c ->
            jobs += scope.launch {
                backend.charValues(key, c).collect { ackSeq.incrementAndGet() }
            }
        }

        // Stale ticker: flags telemetry stale when no data arrives in time.
        jobs += scope.launch {
            while (true) {
                delay(staleAfterMillis / 2)
                val t = _telemetry.value
                if (t.lastUpdatedAtMillis > 0 && timeMillis() - t.lastUpdatedAtMillis > staleAfterMillis) {
                    updateTelemetry { it.copy(stale = true) }
                }
            }
        }

        // Disconnect watcher: never report "connected" after a drop.
        jobs += scope.launch {
            backend.connectionStates.collect { map ->
                if (map[key] != true && _state.value is TrainerState.Connected) {
                    stopSessionJobs()
                    activeKey = null
                    telemetryChar = null
                    speedChar = null
                    controlChar = null
                    protocol = TrainerProtocol.Unsupported
                    _state.value = TrainerState.Disconnected(DisconnectReason.OUT_OF_RANGE)
                }
            }
        }

        sessionJobs = jobs
    }

    private fun stopSessionJobs() {
        sessionJobs.forEach { it.cancel() }
        sessionJobs = emptyList()
    }

    private fun updateTelemetry(transform: (TrainerTelemetry) -> TrainerTelemetry) {
        _telemetry.value = transform(_telemetry.value)
    }

    /** Cancel all background work. Call when the client is disposed. */
    fun close() {
        stopSessionJobs()
        scope.cancel()
    }

    private fun findChar(services: List<BleServiceInfo>, target: String): BleCharacteristicInfo? =
        services.asSequence()
            .flatMap { it.characteristics.asSequence() }
            .firstOrNull { c ->
                c.uuid == target || ProtocolClassifier.shortUuid(c.uuid) == target
            }

    private fun toGattInventory(services: List<BleServiceInfo>): GattInventory = GattInventory(
        services = services.map { s ->
            GattService(
                uuid = s.uuid,
                characteristics = s.characteristics.map {
                    GattCharacteristic(uuid = it.uuid, properties = it.properties, descriptors = it.descriptors)
                },
            )
        }
    )

    private fun mergeVerifiedProperties(
        inventory: GattInventory,
        verified: Map<String, Set<String>>,
    ): GattInventory = GattInventory(
        services = inventory.services.map { s ->
            GattService(
                uuid = s.uuid,
                characteristics = s.characteristics.map { c ->
                    c.copy(properties = c.properties + (verified[c.uuid] ?: emptySet()))
                },
            )
        }
    )
}
