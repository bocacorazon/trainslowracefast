package com.trainerapp.core.ble

import com.monkopedia.bluefalcon.sdbus.SdbusCharacteristic
import com.monkopedia.bluefalcon.sdbus.SdbusEngine
import dev.bluefalcon.core.BlueFalcon
import dev.bluefalcon.core.BluetoothCharacteristic
import dev.bluefalcon.core.BluetoothPeripheral
import dev.bluefalcon.core.BluetoothPeripheralState
import dev.bluefalcon.core.NoOpLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Production [BleBackend] over the Blue Falcon sdbus/BlueZ engine.
 *
 * This is the ONLY file in the project that imports Blue Falcon types;
 * everything else in `core` (domain, protocol, UI) sees the [BleBackend]
 * seam and domain types only.
 *
 * Environment notes (see docs/features/002-desktop-ble-erg-mvp/implementation/ble-adapter-notes.md):
 *  - Requires a running BlueZ stack (system bus org.bluez) and a reachable
 *    HCI adapter (hci0) — verified on the NUC (NucBox-EVO-X2) where the
 *    hardware capture was performed.
 *  - Blue Falcon 3.x does not expose per-characteristic property flags
 *    (write/notify/indicate) through the core interface, so
 *    [discoverServices] reports empty property sets; the client marks
 *    properties verified at runtime (subscribe/indicate attempts).
 *  - Advertisement data (service UUIDs) is likewise not exposed by the
 *    core peripheral type, so [BleScanResult.advertisedServices] is empty.
 */
@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
class BlueFalconBleBackend(
    private val falcon: BlueFalcon = BlueFalcon { engine = SdbusEngine { logger = NoOpLogger } },
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : BleBackend {

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _scanResults = MutableStateFlow<List<BleScanResult>>(emptyList())
    override val scanResults: StateFlow<List<BleScanResult>> = _scanResults

    private val _connectionStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    override val connectionStates: StateFlow<Map<String, Boolean>> = _connectionStates

    private val characteristicFlows = mutableMapOf<String, MutableSharedFlow<ByteArray>>()
    private val flowJobs = mutableMapOf<String, Job>()

    init {
        scope.launch {
            falcon.peripherals.collect { peripherals ->
                _scanResults.value = peripherals
                    .map { p ->
                        BleScanResult(
                            deviceKey = p.uuid,
                            name = p.name,
                            rssi = p.rssi?.toInt(),
                            advertisedServices = emptyList(),
                        )
                    }
                    .sortedBy { it.deviceKey }
            }
        }
        scope.launch {
            while (true) {
                delay(1_000)
                val connected = _connectionStates.value.keys.toList()
                if (connected.isEmpty()) continue
                _connectionStates.value = connected.associateWith { key ->
                    runCatching {
                        falcon.connectionState(peripheral(key)) ==
                            BluetoothPeripheralState.Connected
                    }.getOrDefault(false)
                }
            }
        }
    }

    override suspend fun startScan() {
        falcon.scan()
    }

    override suspend fun stopScan() {
        falcon.stopScanning()
    }

    override suspend fun connect(deviceKey: String) {
        val peripheral = this.peripheral(deviceKey)
        falcon.connect(peripheral)
        _connectionStates.value = _connectionStates.value + (deviceKey to true)
        // Wait for the ACTUAL BlueZ connection before returning: connect() is
        // otherwise fire-and-forget and a immediately-following service
        // discovery races the handshake, yielding an empty GATT (observed in
        // the 003 acceptance ride: trainer classified Unsupported with no
        // telemetry/control path while the UI said riding).
        withTimeoutOrNull(10_000) {
            while (falcon.connectionState(peripheral) != BluetoothPeripheralState.Connected) {
                delay(100)
            }
        }
    }

    override suspend fun disconnect(deviceKey: String) {
        falcon.disconnect(peripheral(deviceKey))
        _connectionStates.value = _connectionStates.value - deviceKey
    }

    override suspend fun discoverServices(deviceKey: String): List<BleServiceInfo> {
        val peripheral = this.peripheral(deviceKey)
        falcon.discoverServices(peripheral)
        // Discovery can return before the GATT is populated (same handshake
        // race as connect) — wait briefly for a non-empty service list rather
        // than classifying the device Unsupported off an empty read.
        withTimeoutOrNull(8_000) {
            while (peripheral.services.isEmpty()) {
                delay(150)
            }
        }
        return peripheral.services.map { s ->
            BleServiceInfo(
                uuid = s.uuid.toString().lowercase(),
                characteristics = s.characteristics.map { c ->
                    BleCharacteristicInfo(uuid = c.uuid.toString().lowercase())
                },
            )
        }
    }

    override suspend fun setNotify(deviceKey: String, charKey: String, enable: Boolean) {
        val peripheral = this.peripheral(deviceKey)
        val c = findCharacteristic(peripheral, charKey)
            ?: throw BleBackendException("characteristic not found: $charKey")
        if (enable) {
            ensureValueFlow(deviceKey, charKey, c)
            falcon.notifyCharacteristic(peripheral, c, true)
        } else {
            falcon.notifyCharacteristic(peripheral, c, false)
        }
    }

    override suspend fun setIndicate(deviceKey: String, charKey: String, enable: Boolean) {
        val peripheral = this.peripheral(deviceKey)
        val c = findCharacteristic(peripheral, charKey)
            ?: throw BleBackendException("characteristic not found: $charKey")
        if (enable) {
            ensureValueFlow(deviceKey, charKey, c)
            falcon.indicateCharacteristic(peripheral, c, true)
        } else {
            falcon.indicateCharacteristic(peripheral, c, false)
        }
    }

    override fun charValues(deviceKey: String, charKey: String): Flow<ByteArray> {
        val key = "$deviceKey/$charKey"
        return characteristicFlows.getOrPut(key) {
            MutableSharedFlow(
                replay = 1,
                extraBufferCapacity = 64,
                onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST,
            )
        }
    }

    override suspend fun readCharacteristic(deviceKey: String, charKey: String): ByteArray {
        val peripheral = this.peripheral(deviceKey)
        val c = findCharacteristic(peripheral, charKey)
            ?: throw BleBackendException("characteristic not found: $charKey")
        falcon.readCharacteristic(peripheral, c)
        return c.value ?: ByteArray(0)
    }

    override suspend fun writeCharacteristic(deviceKey: String, charKey: String, data: ByteArray) {
        val peripheral = this.peripheral(deviceKey)
        val c = findCharacteristic(peripheral, charKey)
            ?: throw BleBackendException("characteristic not found: $charKey")
        falcon.writeCharacteristic(peripheral, c, data)
    }

    private fun ensureValueFlow(deviceKey: String, charKey: String, c: BluetoothCharacteristic) {
        val key = "$deviceKey/$charKey"
        if (flowJobs[key] != null) return
        val sdbus = c as? SdbusCharacteristic
            ?: throw BleBackendException("expected SdbusCharacteristic, got ${c::class.simpleName}")
        val flow = characteristicFlows.getOrPut(key) {
            MutableSharedFlow(
                replay = 1,
                extraBufferCapacity = 64,
                onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST,
            )
        }
        flowJobs[key] = scope.launch {
            sdbus.valueFlow.collect { value ->
                value?.let { flow.emit(it) }
            }
        }
    }

    private fun peripheral(deviceKey: String): BluetoothPeripheral =
        falcon.retrievePeripheral(deviceKey)
            ?: throw BleBackendException("device not found: $deviceKey")

    private fun findCharacteristic(peripheral: BluetoothPeripheral, charKey: String): BluetoothCharacteristic? =
        peripheral.services.asSequence()
            .flatMap { it.characteristics.asSequence() }
            .firstOrNull { it.uuid.toString().lowercase() == charKey }
}
