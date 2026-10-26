package com.trainerapp.core.ble

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** In-memory [BleBackend] for contract tests of [BlueFalconTrainerClient]. */
class FakeBleBackend : BleBackend {

    val scanResults_ = MutableStateFlow<List<BleScanResult>>(emptyList())
    override val scanResults: StateFlow<List<BleScanResult>> = scanResults_

    val connections_ = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    override val connectionStates: StateFlow<Map<String, Boolean>> = connections_

    /** GATT layout reported by discoverServices, keyed by deviceKey. */
    var services: Map<String, List<BleServiceInfo>> = emptyMap()

    val connectCalls = mutableListOf<String>()
    val disconnectCalls = mutableListOf<String>()
    val stopScanCalls = mutableListOf<Unit>()
    val notifyCalls = mutableListOf<NotifyCall>()
    val indicateCalls = mutableListOf<IndicateCall>()
    val writeLog = mutableListOf<WriteCall>()
    val readLog = mutableListOf<String>()

    /** Characteristic keys whose setNotify(enable=true) throws. */
    val unsupportedNotify = mutableSetOf<String>()
    /** Characteristic keys whose setIndicate(enable=true) throws. */
    val unsupportedIndicate = mutableSetOf<String>()
    /** Values returned by readCharacteristic, keyed by charKey. */
    val readValues = mutableMapOf<String, ByteArray>()
    /** When a write lands on one of these keys, an indication is emitted. */
    val autoIndicateOnWrite = mutableSetOf<String>()

    private val characteristicValues = mutableMapOf<String, MutableStateFlow<ByteArray>>()

    /** The value flow for a characteristic (created on demand). */
    fun valueFlow(charKey: String): MutableStateFlow<ByteArray> =
        characteristicValues.getOrPut(charKey) { MutableStateFlow(ByteArray(0)) }

    override suspend fun startScan() {}

    override suspend fun stopScan() {
        stopScanCalls += Unit
    }

    override suspend fun connect(deviceKey: String, awaitHandshakeMillis: Long) {
        connectCalls += deviceKey
        connections_.value = connections_.value + (deviceKey to true)
    }

    override suspend fun disconnect(deviceKey: String) {
        disconnectCalls += deviceKey
        connections_.value = connections_.value - deviceKey
    }

    override suspend fun discoverServices(deviceKey: String): List<BleServiceInfo> =
        services[deviceKey] ?: emptyList()

    override suspend fun setNotify(deviceKey: String, charKey: String, enable: Boolean) {
        notifyCalls += NotifyCall(deviceKey, charKey, enable)
        if (enable && charKey in unsupportedNotify) {
            throw BleBackendException("notify not supported for $charKey")
        }
    }

    override suspend fun setIndicate(deviceKey: String, charKey: String, enable: Boolean) {
        indicateCalls += IndicateCall(deviceKey, charKey, enable)
        if (enable && charKey in unsupportedIndicate) {
            throw BleBackendException("indicate not supported for $charKey")
        }
    }

    override fun charValues(deviceKey: String, charKey: String): Flow<ByteArray> =
        valueFlow(charKey)

    override suspend fun readCharacteristic(deviceKey: String, charKey: String): ByteArray {
        readLog += charKey
        val v = readValues[charKey] ?: throw BleBackendException("no value for $charKey")
        return v
    }

    override suspend fun writeCharacteristic(deviceKey: String, charKey: String, data: ByteArray) {
        writeLog += WriteCall(deviceKey, charKey, data)
        if (charKey in autoIndicateOnWrite) {
            valueFlow(charKey).value = byteArrayOf(0xFF.toByte())
        }
    }
}
