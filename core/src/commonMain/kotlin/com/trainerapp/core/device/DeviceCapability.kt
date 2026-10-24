package com.trainerapp.core.device

import com.trainerapp.core.domain.TrainerProtocol
import com.trainerapp.core.protocol.GattInventory
import com.trainerapp.core.protocol.ProtocolClassifier

/** The data types a connected device can provide. */
enum class DeviceCapability { POWER, SPEED, CADENCE, HEART_RATE, CONTROL }

/**
 * Maps a device's discovered GATT inventory to the capabilities it provides.
 *
 * Rules (aligned with the finalized classifier and the 2026-10-24 Hammer
 * capture):
 *  - CONTROL comes from the classified protocol being controllable
 *    (FTMS control point or the Saris vendor control path);
 *  - POWER/SPEED come from a notifying Indoor Bike Data (0x2A36) or
 *    Cycling Power Measurement (0x2A63) characteristic — the Hammer H1
 *    reports power AND speed through 0x2A63 — or from an FTMS trainer;
 *  - CADENCE comes from a notifying CSC measurement (0x2A5B);
 *  - HEART_RATE comes from a notifying heart-rate measurement (0x2A37).
 */
object CapabilityDiscovery {

    private const val CSC_MEASUREMENT = "2a5b"
    private const val HEART_RATE_MEASUREMENT = "2a37"
    // Same values as the finalized classifier constants (wi-hardware-capture
    // branch); duplicated here so the parallel branches stay independent until
    // construction integrate merges them.
    private const val CYCLING_POWER_MEASUREMENT = "2a63"
    private const val INDOOR_BIKE_DATA = "2a36"

    private fun String.shortUuid(): String =
        split("-").first().trimStart('0').lowercase()

    fun discover(inventory: GattInventory): Set<DeviceCapability> {
        val capabilities = mutableSetOf<DeviceCapability>()
        val protocol = ProtocolClassifier.classify(inventory)
        if (protocol.controllable) capabilities += DeviceCapability.CONTROL
        if (protocol == TrainerProtocol.Ftms) {
            capabilities += DeviceCapability.POWER
            capabilities += DeviceCapability.SPEED
        }
        val characteristics = inventory.services.flatMap { it.characteristics }
        val notifying = characteristics.filter { "notify" in it.properties }
        val notifyingShort = notifying.map { it.uuid.shortUuid() }
        if (CYCLING_POWER_MEASUREMENT in notifyingShort ||
            INDOOR_BIKE_DATA in notifyingShort
        ) {
            capabilities += DeviceCapability.POWER
            capabilities += DeviceCapability.SPEED
        }
        if (CSC_MEASUREMENT in notifyingShort) {
            capabilities += DeviceCapability.SPEED
            capabilities += DeviceCapability.CADENCE
        }
        if (HEART_RATE_MEASUREMENT in notifyingShort) {
            capabilities += DeviceCapability.HEART_RATE
        }
        return capabilities
    }
}
