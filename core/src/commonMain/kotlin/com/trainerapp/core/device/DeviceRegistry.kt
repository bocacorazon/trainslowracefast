package com.trainerapp.core.device

import com.trainerapp.core.store.TrainerIdentity

/** A device known to the app: identity is transport-agnostic by design. */
data class RegisteredDevice(
    val identity: TrainerIdentity,
    val capabilities: Set<DeviceCapability>,
)

/**
 * Tracks every connected device. This slice only ever registers the trainer,
 * but the registry keeps the next slice (HR strap, cadence sensors) from
 * being a redesign: any number of devices with per-device capabilities.
 */
class DeviceRegistry {

    private val devices = LinkedHashMap<String, RegisteredDevice>()

    /** Registers (or replaces) a device keyed by its platform identity. */
    fun register(device: RegisteredDevice) {
        devices[device.identity.platformId] = device
    }

    fun remove(platformId: String) {
        devices.remove(platformId)
    }

    fun all(): List<RegisteredDevice> = devices.values.toList()

    fun find(platformId: String): RegisteredDevice? = devices[platformId]

    /** Devices able to provide the given capability, in registration order. */
    fun byCapability(capability: DeviceCapability): List<RegisteredDevice> =
        devices.values.filter { capability in it.capabilities }
}

/** A displayed metric and the device that provides it. */
enum class Metric { POWER, SPEED, CADENCE, HEART_RATE }

/**
 * Assigns each metric to a source device. This slice auto-assigns everything
 * to the trainer; the next slice adds the user-selection screen and persists
 * choices. Resolution never silently falls back to a device that lacks the
 * capability: unresolvable metrics return null and the UI shows "—".
 */
class MetricSourceMap(private val registry: DeviceRegistry) {

    private val assignment = mutableMapOf<Metric, String>()

    /**
     * Auto-assign every metric to the first registered device that can
     * provide it. Explicit assignments win over auto-assignment.
     */
    fun autoAssign() {
        for (metric in Metric.entries) {
            val capability = when (metric) {
                Metric.POWER -> DeviceCapability.POWER
                Metric.SPEED -> DeviceCapability.SPEED
                Metric.CADENCE -> DeviceCapability.CADENCE
                Metric.HEART_RATE -> DeviceCapability.HEART_RATE
            }
            val candidate = registry.byCapability(capability).firstOrNull()
            if (candidate != null && metric !in assigned()) {
                assignment[metric] = candidate.identity.platformId
            }
        }
    }

    /** Explicit per-metric source selection (next slice's UI calls this). */
    fun assign(metric: Metric, platformId: String): Boolean {
        val device = registry.find(platformId) ?: return false
        val capability = when (metric) {
            Metric.POWER -> DeviceCapability.POWER
            Metric.SPEED -> DeviceCapability.SPEED
            Metric.CADENCE -> DeviceCapability.CADENCE
            Metric.HEART_RATE -> DeviceCapability.HEART_RATE
        }
        if (capability !in device.capabilities) return false
        assignment[metric] = platformId
        return true
    }

    private fun assigned(): Set<Metric> = assignment.keys

    /** The device providing the metric, or null if unassigned/unresolvable. */
    fun sourceFor(metric: Metric): RegisteredDevice? {
        val platformId = assignment[metric] ?: return null
        return registry.find(platformId)
    }
}
