package com.trainerapp.core.protocol

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InventoryExporterTest {

    @Test
    fun exportContainsServicesAndCharacteristics() {
        val inventory = GattInventory(
            services = listOf(
                GattService(
                    uuid = "00001826-0000-1000-8000-00805F9B34FB",
                    characteristics = listOf(
                        GattCharacteristic(uuid = "00002AD9-0000-1000-8000-00805F9B34FB", properties = setOf("indicate")),
                    ),
                ),
            ),
        )
        val exported = InventoryExporter.export(inventory)
        assertTrue("1826" in exported)
        assertTrue("2AD9" in exported)
        assertTrue("indicate" in exported)
    }

    @Test
    fun exportCarriesNoAddressLikeContent() {
        val inventory = GattInventory(
            services = listOf(GattService(uuid = "00001826-0000-1000-8000-00805F9B34FB")),
        )
        val exported = InventoryExporter.export(inventory).lowercase()
        assertFalse(Regex("([0-9a-f]{2}[:-]){5}[0-9a-f]{2}").containsMatchIn(exported))
    }
}
