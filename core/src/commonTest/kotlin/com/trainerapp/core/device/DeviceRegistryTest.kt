package com.trainerapp.core.device

import com.trainerapp.core.protocol.GattCharacteristic
import com.trainerapp.core.protocol.GattInventory
import com.trainerapp.core.protocol.GattService
import com.trainerapp.core.store.TrainerIdentity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeviceRegistryTest {

    private fun ftmsTrainerInventory() = GattInventory(
        services = listOf(
            GattService(
                uuid = "00001826-0000-1000-8000-00805F9B34FB",
                characteristics = listOf(
                    GattCharacteristic(uuid = "00002AD9-0000-1000-8000-00805F9B34FB", properties = setOf("writeWithoutResponse", "indicate")),
                    GattCharacteristic(uuid = "00002AD2-0000-1000-8000-00805F9B34FB", properties = setOf("notify")),
                ),
            ),
            GattService(
                uuid = "00001816-0000-1000-8000-00805F9B34FB",
                characteristics = listOf(
                    GattCharacteristic(uuid = "00002A5B-0000-1000-8000-00805F9B34FB", properties = setOf("notify")),
                ),
            ),
            GattService(
                uuid = "0000180D-0000-1000-8000-00805F9B34FB",
                characteristics = listOf(
                    GattCharacteristic(uuid = "00002A37-0000-1000-8000-00805F9B34FB", properties = setOf("notify")),
                ),
            ),
        ),
    )

    private fun powerTelemetryOnlyInventory() = GattInventory(
        services = listOf(
            GattService(
                uuid = "0000180C-0000-1000-8000-00805F9B34FB",
                characteristics = listOf(
                    GattCharacteristic(uuid = "00002A36-0000-1000-8000-00805F9B34FB", properties = setOf("notify")),
                ),
            ),
        ),
    )

    /**
     * The REAL captured first-gen Hammer H1 inventory (committed sanitized
     * fixture from the 2026-10-24 capture): telemetry via Cycling Power
     * 0x2A63 (power + speed, no cadence), control via the Saris vendor path.
     */
    private fun capturedHammerInventory(): GattInventory {
        val stream = DeviceRegistryTest::class.java.classLoader
            .getResourceAsStream("fixtures/hammer-gatt-inventory.json")
        assertTrue(stream != null, "captured fixture must be on the test classpath")
        val json = stream!!.readBytes().decodeToString()
        return kotlinx.serialization.json.Json.decodeFromString(GattInventory.serializer(), json)
    }

    private fun emptyInventory() = GattInventory()

    @Test
    fun fullTrainerInventoryYieldsAllFiveCapabilities() {
        val caps = CapabilityDiscovery.discover(ftmsTrainerInventory())

        assertEquals(
            setOf(
                DeviceCapability.POWER,
                DeviceCapability.SPEED,
                DeviceCapability.CADENCE,
                DeviceCapability.HEART_RATE,
                DeviceCapability.CONTROL,
            ),
            caps,
        )
    }

    @Test
    fun telemetryWithoutControlPathLacksControlCapability() {
        val caps = CapabilityDiscovery.discover(powerTelemetryOnlyInventory())

        // 0x2A36 Indoor Bike Data carries power and speed.
        assertEquals(setOf(DeviceCapability.POWER, DeviceCapability.SPEED), caps)
    }

    @Test
    fun capturedHammerYieldsPowerAndControlButNoSpeed() {
        val caps = CapabilityDiscovery.discover(capturedHammerInventory())

        // The Hammer H1 measures POWER (0x2A63) and exposes the Wahoo-legacy
        // control path — captured live 2026-10-24. It provides NO speed over
        // BLE: the 002 smoke ride proved the earlier "speed" readout was a
        // misparse of Cycling Power Vector (corrected 2026-10-25), and the
        // 003 CPV probe confirmed no cadence data either.
        assertEquals(setOf(DeviceCapability.POWER, DeviceCapability.CONTROL), caps)
    }

    @Test
    fun emptyInventoryYieldsNoCapabilities() {
        assertEquals(emptySet(), CapabilityDiscovery.discover(emptyInventory()))
    }

    @Test
    fun registryTracksDevicesAndSourceMapResolvesMetrics() {
        val registry = DeviceRegistry()
        val trainer = RegisteredDevice(
            identity = TrainerIdentity(name = "Hammer 51967", platformId = "trainer-1"),
            capabilities = CapabilityDiscovery.discover(capturedHammerInventory()),
        )
        registry.register(trainer)
        assertEquals(1, registry.all().size)
        assertEquals(trainer, registry.byCapability(DeviceCapability.CONTROL).single())

        val sources = MetricSourceMap(registry)
        sources.autoAssign()
        assertEquals(trainer, sources.sourceFor(Metric.POWER))
        assertEquals(null, sources.sourceFor(Metric.SPEED))        // the H1 has no speed source (002 live evidence)
        assertEquals(null, sources.sourceFor(Metric.CADENCE))      // the H1 has none
        assertEquals(null, sources.sourceFor(Metric.HEART_RATE))   // the strap (003)
    }

    @Test
    fun sourceMapRejectsDevicesLackingTheCapability() {
        val registry = DeviceRegistry()
        val trainer = RegisteredDevice(
            identity = TrainerIdentity(name = "Hammer 51967", platformId = "trainer-1"),
            capabilities = setOf(DeviceCapability.POWER, DeviceCapability.CONTROL),
        )
        registry.register(trainer)
        val sources = MetricSourceMap(registry)
        assertEquals(false, sources.assign(Metric.HEART_RATE, "trainer-1"))
        assertEquals(null, sources.sourceFor(Metric.HEART_RATE))
        assertEquals(true, sources.assign(Metric.POWER, "trainer-1"))
        assertEquals(trainer, sources.sourceFor(Metric.POWER))
    }
}
