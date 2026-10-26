package com.trainerapp.core.device

import com.trainerapp.core.protocol.GattInventory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Capability classification against the REAL captured inventories
 * committed by the 003 hardware-capture item:
 *  - the Hammer H1 fixture (002 capture) — power + control, NO speed
 *    (the 002 smoke ride proved the speed claim was a 0x2A65 misparse)
 *  - the COOSPO H808S strap fixture (003 capture) — heart rate only
 */
class CapabilityDiscoveryH808STest {

    private fun loadInventory(resource: String): GattInventory {
        val stream = CapabilityDiscoveryH808STest::class.java.classLoader
            .getResourceAsStream(resource)
        assertTrue(stream != null, "$resource must be on the test classpath")
        return kotlinx.serialization.json.Json.decodeFromString(
            GattInventory.serializer(),
            stream!!.readBytes().decodeToString(),
        )
    }

    @Test
    fun captured_h808s_strap_yields_heart_rate_only() {
        val caps = CapabilityDiscovery.discover(
            loadInventory("fixtures/hr-h808s-gatt-inventory.json")
        )
        assertEquals(setOf(DeviceCapability.HEART_RATE), caps)
    }

    @Test
    fun captured_hammer_yields_power_and_control_but_no_speed() {
        val caps = CapabilityDiscovery.discover(
            loadInventory("fixtures/hammer-gatt-inventory.json")
        )
        assertEquals(setOf(DeviceCapability.POWER, DeviceCapability.CONTROL), caps)
        assertTrue(DeviceCapability.SPEED !in caps, "the H1 has no speed source (002 live evidence)")
        assertTrue(DeviceCapability.CADENCE !in caps, "the H1 has no cadence over BLE (003 CPV probe)")
    }
}
