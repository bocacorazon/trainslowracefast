package com.trainerapp.core.device

import com.trainerapp.core.domain.TrainerProtocol
import com.trainerapp.core.protocol.GattInventory
import com.trainerapp.core.protocol.ProtocolClassifier

/** The data types a connected device can provide. */
enum class DeviceCapability { POWER, SPEED, CADENCE, HEART_RATE, CONTROL }

/**
 * Maps a device's discovered GATT inventory to the capabilities it provides.
 *
 * Rules (aligned with the finalized classifier and the live captures):
 *  - CONTROL comes from the classified protocol being controllable
 *    (FTMS control point or the Wahoo-legacy/Saris vendor control path);
 *  - POWER comes from a notifying Cycling Power Measurement (0x2A63) or
 *    Indoor Bike Data (0x2A36) characteristic;
 *  - SPEED comes ONLY from characteristics that actually carry speed:
 *    Indoor Bike Data (0x2A36), a CSC measurement (0x2A5B), or an RSC
 *    measurement (0x2A53). The Hammer H1's 0x2A63 carries power ONLY —
 *    the 002 smoke ride proved its "speed" readout was a misparse of
 *    Cycling Power Vector (0x2A65); a CPM characteristic alone never
 *    implies speed (corrected 2026-10-25);
 *  - CADENCE comes from a notifying CSC (0x2A5B) or RSC (0x2A53)
 *    measurement;
 *  - HEART_RATE comes from a notifying heart-rate measurement (0x2A37) —
 *    verified on the captured COOSPO H808S inventory (003 capture).
 */
object CapabilityDiscovery {

    private const val CSC_MEASUREMENT = "2a5b"
    private const val RSC_MEASUREMENT = "2a53"
    private const val HEART_RATE_MEASUREMENT = "2a37"
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
        if (CYCLING_POWER_MEASUREMENT in notifyingShort) {
            capabilities += DeviceCapability.POWER
        }
        if (INDOOR_BIKE_DATA in notifyingShort) {
            capabilities += DeviceCapability.POWER
            capabilities += DeviceCapability.SPEED
        }
        if (CSC_MEASUREMENT in notifyingShort || RSC_MEASUREMENT in notifyingShort) {
            capabilities += DeviceCapability.SPEED
            capabilities += DeviceCapability.CADENCE
        }
        if (HEART_RATE_MEASUREMENT in notifyingShort) {
            capabilities += DeviceCapability.HEART_RATE
        }
        return capabilities
    }
}
