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
 * Rules (feature design):
 *  - FTMS when service 0x1826 is present with control point 0x2AD9;
 *  - CYCLEOPS_PROPRIETARY when the expected CycleOps/Saris service is present
 *    (UUID finalized by the hardware capture step; 0x1812-8dc is the
 *    CycleOps/Saris service observed in the wild);
 *  - TELEMETRY_ONLY when power (0x2A36) can be read/notify but no control path;
 *  - UNSUPPORTED otherwise.
 */
object ProtocolClassifier {

    const val FTMS_SERVICE = "1826"
    const val FTMS_CONTROL_POINT = "2ad9"
    const val INDOOR_BIKE_DATA = "2a36"
    const val CYCLEOPS_SERVICE = "1812-8dc"

    fun classify(inventory: GattInventory): TrainerProtocol {
        val ftmsService = inventory.services
            .firstOrNull { it.uuid.shortUuid() == FTMS_SERVICE }
        if (ftmsService != null &&
            ftmsService.characteristics.any { it.uuid.shortUuid() == FTMS_CONTROL_POINT }
        ) {
            return TrainerProtocol.Ftms
        }
        if (inventory.services.any { svc ->
            val u = svc.uuid.lowercase()
            u.contains("1812") && u.contains("8dc")
        }) {
            return TrainerProtocol.CycleOpsProprietary
        }
        val powerReadable = inventory.services.any { service ->
            service.characteristics.any {
                it.uuid.shortUuid() == INDOOR_BIKE_DATA && "notify" in it.properties
            }
        }
        if (powerReadable) return TrainerProtocol.TelemetryOnly
        return TrainerProtocol.Unsupported
    }
}
