package com.trainerapp.core.protocol

import com.trainerapp.core.domain.TrainerProtocol
import kotlinx.serialization.Serializable

/** One characteristic of a discovered GATT service. */
@Serializable
data class GattCharacteristic(
    val uuid: String,
    val properties: Set<String> = emptySet(),
    val descriptors: List<String> = emptyList(),
)

/** One discovered GATT service. */
@Serializable
data class GattService(
    val uuid: String,
    val characteristics: List<GattCharacteristic> = emptyList(),
)

/**
 * The complete discovered inventory. Deliberately contains no device address:
 * it is committed to the repository as a test fixture.
 */
@Serializable
data class GattInventory(
    val services: List<GattService> = emptyList(),
)

private fun String.shortUuid(): String =
    split("-").first().trimStart('0').lowercase()

/**
 * Classify a connected trainer from its discovered GATT inventory.
 *
 * Rules (finalized from the live hardware capture of a first-generation
 * CycleOps Hammer H1, 2026-10-24 — see
 * core/src/commonTest/resources/fixtures/hammer-gatt-inventory.json):
 *  - FTMS when service 0x1826 is present with control point 0x2AD9;
 *  - CYCLEOPS_PROPRIETARY when the captured Saris vendor service
 *    c0f4013a-... is present with its write+indicate control characteristic,
 *    or the Saris vendor control characteristic a026e005-... appears inside
 *    the Cycling Power service. NOTE: the captured Hammer exposes NO FTMS —
 *    its control opcodes are proprietary and must be reverse-engineered by
 *    the BLE adapter work item; classification only detects the control
 *    path's presence.
 *  - TELEMETRY_ONLY when power is readable (Cycling Power Measurement
 *    0x2A63 or Indoor Bike Data 0x2A36, notify) but no control path exists;
 *  - UNSUPPORTED otherwise.
 */
object ProtocolClassifier {

    const val FTMS_SERVICE = "1826"
    const val FTMS_CONTROL_POINT = "2ad9"
    const val CYCLING_POWER_SERVICE = "1818"
    const val CYCLING_POWER_MEASUREMENT = "2a63"
    const val INDOOR_BIKE_DATA = "2a36"

    /** Saris/CycleOps vendor service observed on the live Hammer H1. */
    const val CYCLEOPS_VENDOR_SERVICE = "c0f4013a-a837-4165-bab9-654ef70747c6"

    /** Saris vendor control characteristic inside the vendor service (write+indicate). */
    const val CYCLEOPS_VENDOR_CONTROL = "ca31a533-a858-4dc7-a650-fdeb6dad4c14"

    /** Saris vendor control characteristic inside the Cycling Power service (write+indicate). */
    const val CYCLEOPS_POWER_SERVICE_CONTROL = "a026e005-0a7d-4ab3-97fa-f1500f9feb8b"

    fun classify(inventory: GattInventory): TrainerProtocol {
        val ftmsService = inventory.services
            .firstOrNull { it.uuid.shortUuid() == FTMS_SERVICE }
        if (ftmsService != null &&
            ftmsService.characteristics.any { it.uuid.shortUuid() == FTMS_CONTROL_POINT }
        ) {
            return TrainerProtocol.Ftms
        }
        val allCharacteristics = inventory.services.flatMap { it.characteristics }
        val hasVendorService = inventory.services.any {
            it.uuid.lowercase() == CYCLEOPS_VENDOR_SERVICE &&
                it.characteristics.any { c -> c.uuid.lowercase() == CYCLEOPS_VENDOR_CONTROL }
        }
        val hasPowerServiceControl = allCharacteristics.any {
            it.uuid.lowercase() == CYCLEOPS_POWER_SERVICE_CONTROL
        }
        if (hasVendorService || hasPowerServiceControl) {
            return TrainerProtocol.CycleOpsProprietary
        }
        val powerReadable = allCharacteristics.any {
            (it.uuid.shortUuid() == CYCLING_POWER_MEASUREMENT ||
                it.uuid.shortUuid() == INDOOR_BIKE_DATA) && "notify" in it.properties
        }
        if (powerReadable) return TrainerProtocol.TelemetryOnly
        return TrainerProtocol.Unsupported
    }
}
