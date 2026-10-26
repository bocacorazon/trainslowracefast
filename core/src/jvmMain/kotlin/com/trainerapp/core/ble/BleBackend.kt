package com.trainerapp.core.ble

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.security.MessageDigest

/**
 * Minimal BLE transport seam the [BlueFalconTrainerClient] programs against.
 *
 * Production: [BlueFalconBleBackend] (Blue Falcon sdbus/BlueZ engine).
 * Tests: a fake implementation (see jvmTest). Keeping this seam inside
 * `core/ble` guarantees no Blue Falcon type leaks into the domain or UI.
 *
 * `deviceKey` is the transport-level device identity (a BlueZ address in
 * production). It is NEVER surfaced to the UI — the client exposes the
 * opaque `platformId` derived from it instead.
 */
interface BleBackend {
    /** Devices seen by the last/current scan. */
    val scanResults: StateFlow<List<BleScanResult>>

    /** deviceKey -> connected, updated as connections are established/dropped. */
    val connectionStates: StateFlow<Map<String, Boolean>>

    suspend fun startScan()
    suspend fun stopScan()

    /**
     * Connects a device. `awaitHandshakeMillis` bounds how long the backend
     * waits for the REAL BlueZ connection before returning (see
     * BlueFalconBleBackend for why waiting matters): trainers want the full
     * window (a fresh trainer needs its GATT cached before discovery); a
     * simple sensor that is not yet ready should fail fast so the session's
     * reconnect loop can retry quickly instead of burning the whole window
     * per attempt.
     */
    suspend fun connect(deviceKey: String, awaitHandshakeMillis: Long = 10_000)
    suspend fun disconnect(deviceKey: String)

    /** Full GATT service discovery for a connected device. */
    suspend fun discoverServices(deviceKey: String): List<BleServiceInfo>

    /** Enable/disable notify on a characteristic. Throws [BleBackendException] if unsupported. */
    suspend fun setNotify(deviceKey: String, charKey: String, enable: Boolean)

    /** Enable/disable indicate on a characteristic. Throws [BleBackendException] if unsupported. */
    suspend fun setIndicate(deviceKey: String, charKey: String, enable: Boolean)

    /**
     * Values the characteristic emits as it notifies/indicates. May replay the
     * last known value on subscription (StateFlow semantics in production).
     */
    fun charValues(deviceKey: String, charKey: String): Flow<ByteArray>

    suspend fun readCharacteristic(deviceKey: String, charKey: String): ByteArray
    suspend fun writeCharacteristic(deviceKey: String, charKey: String, data: ByteArray)
}

/** One device seen during scanning. */
data class BleScanResult(
    val deviceKey: String,
    val name: String?,
    val rssi: Int?,
    val advertisedServices: List<String> = emptyList(),
)

/** One discovered GATT service (characteristic UUIDs lowercased). */
data class BleServiceInfo(
    val uuid: String,
    val characteristics: List<BleCharacteristicInfo> = emptyList(),
)

/** One discovered GATT characteristic. `properties` is whatever the transport verified. */
data class BleCharacteristicInfo(
    val uuid: String,
    val properties: Set<String> = emptySet(),
    val descriptors: List<String> = emptyList(),
)

data class NotifyCall(val deviceKey: String, val charKey: String, val enable: Boolean)
data class IndicateCall(val deviceKey: String, val charKey: String, val enable: Boolean)
data class WriteCall(val deviceKey: String, val charKey: String, val data: ByteArray)

/** Transport-level failure (device vanished, characteristic not supported, ...). */
class BleBackendException(message: String) : Exception(message)

/**
 * Stable, opaque platform identity for a device. Derived from the transport
 * device key (a BlueZ address) so it is stable across scans but carries no
 * address-like information the UI may display.
 */
fun platformIdFor(deviceKey: String): String {
    val digest = MessageDigest.getInstance("SHA-1").digest(deviceKey.toByteArray())
    return "bf-" + digest.take(8).joinToString("") { "%02x".format(it) }
}
