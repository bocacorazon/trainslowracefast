package com.trainerapp.core.protocol

import com.trainerapp.core.domain.TrainerProtocol
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProtocolClassifierTest {

    private val ftmsInventory = GattInventory(
        services = listOf(
            GattService(
                uuid = "00001826-0000-1000-8000-00805F9B34FB",
                characteristics = listOf(
                    GattCharacteristic(uuid = "00002AD9-0000-1000-8000-00805F9B34FB", properties = setOf("writeWithoutResponse", "indicate")),
                    GattCharacteristic(uuid = "00002A36-0000-1000-8000-00805F9B34FB", properties = setOf("notify")),
                    GattCharacteristic(uuid = "00002A37-0000-1000-8000-00805F9B34FB", properties = setOf("notify")),
                ),
            ),
        ),
    )

    private val telemetryOnlyInventory = GattInventory(
        services = listOf(
            GattService(
                uuid = "0000180C-0000-1000-8000-00805F9B34FB",
                characteristics = listOf(
                    GattCharacteristic(uuid = "00002A36-0000-1000-8000-00805F9B34FB", properties = setOf("notify")),
                ),
            ),
        ),
    )

    /** Telemetry via the Cycling Power service (0x2A63) without any control path. */
    private val cyclingPowerTelemetryOnlyInventory = GattInventory(
        services = listOf(
            GattService(
                uuid = "00001818-0000-1000-8000-00805F9B34FB",
                characteristics = listOf(
                    GattCharacteristic(uuid = "00002A63-0000-1000-8000-00805F9B34FB", properties = setOf("notify")),
                    GattCharacteristic(uuid = "00002A65-0000-1000-8000-00805F9B34FB", properties = setOf("read")),
                ),
            ),
        ),
    )

    /**
     * The REAL captured inventory of the first-generation CycleOps Hammer H1,
     * committed as a sanitized fixture from the live 2026-10-24 hardware
     * capture. The device exposes NO FTMS: telemetry via Cycling Power,
     * control via two Saris vendor write+indicate characteristics.
     */
    private fun capturedHammerInventory(): GattInventory {
        val stream = ProtocolClassifierTest::class.java.classLoader
            .getResourceAsStream("fixtures/hammer-gatt-inventory.json")
        assertTrue(stream != null, "captured fixture must be on the test classpath")
        val json = stream!!.readBytes().decodeToString()
        return kotlinx.serialization.json.Json.decodeFromString(GattInventory.serializer(), json)
    }

    @Test
    fun ftmsWhenService1826WithControlPoint2AD9() {
        assertEquals(TrainerProtocol.Ftms, ProtocolClassifier.classify(ftmsInventory))
    }

    @Test
    fun capturedHammerClassifiesAsCycleOpsProprietary() {
        val inventory = capturedHammerInventory()
        assertEquals(TrainerProtocol.CycleOpsProprietary, ProtocolClassifier.classify(inventory))
    }

    @Test
    fun capturedHammerHasNoFtmsService() {
        val inventory = capturedHammerInventory()
        assertTrue(
            inventory.services.none { it.uuid.split("-").first().trimStart('0').lowercase() == ProtocolClassifier.FTMS_SERVICE },
            "the captured Hammer H1 exposes no FTMS service — if this fails, the fixture or hardware changed"
        )
    }

    @Test
    fun vendorServiceWithControlCharAloneIsCycleOpsProprietary() {
        val inventory = GattInventory(
            services = listOf(
                GattService(
                    uuid = "c0f4013a-a837-4165-bab9-654ef70747c6",
                    characteristics = listOf(
                        GattCharacteristic(uuid = "ca31a533-a858-4dc7-a650-fdeb6dad4c14", properties = setOf("write", "indicate")),
                    ),
                ),
            ),
        )
        assertEquals(TrainerProtocol.CycleOpsProprietary, ProtocolClassifier.classify(inventory))
    }

    @Test
    fun telemetryOnlyWhenCyclingPowerWithoutControlPath() {
        assertEquals(TrainerProtocol.TelemetryOnly, ProtocolClassifier.classify(cyclingPowerTelemetryOnlyInventory))
    }

    @Test
    fun telemetryOnlyWhenPowerReadableButNoControlPath() {
        assertEquals(TrainerProtocol.TelemetryOnly, ProtocolClassifier.classify(telemetryOnlyInventory))
    }

    @Test
    fun unsupportedWhenNothingRecognized() {
        assertEquals(TrainerProtocol.Unsupported, ProtocolClassifier.classify(GattInventory(services = emptyList())))
    }

    @Test
    fun service1826WithoutControlPointIsNotFtms() {
        val noControlPoint = GattInventory(
            services = listOf(
                GattService(
                    uuid = "00001826-0000-1000-8000-00805F9B34FB",
                    characteristics = listOf(
                        GattCharacteristic(uuid = "00002A36-0000-1000-8000-00805F9B34FB", properties = setOf("notify")),
                    ),
                ),
            ),
        )
        assertEquals(TrainerProtocol.TelemetryOnly, ProtocolClassifier.classify(noControlPoint))
    }

    @Test
    fun controllableReflectsControlPathPresence() {
        assertEquals(true, TrainerProtocol.Ftms.controllable)
        assertEquals(true, TrainerProtocol.CycleOpsProprietary.controllable)
        assertEquals(false, TrainerProtocol.TelemetryOnly.controllable)
        assertEquals(false, TrainerProtocol.Unsupported.controllable)
    }
}
