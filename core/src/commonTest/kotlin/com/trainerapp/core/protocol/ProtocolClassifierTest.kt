package com.trainerapp.core.protocol

import com.trainerapp.core.domain.TrainerProtocol
import kotlin.test.Test
import kotlin.test.assertEquals

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

    private val cycleopsInventory = GattInventory(
        services = listOf(
            GattService(uuid = "00008DC0-7A3C-4F4E-9D4B-181200008DC0"),
            GattService(
                uuid = "0000180C-0000-1000-8000-00805F9B34FB",
                characteristics = listOf(
                    GattCharacteristic(uuid = "00002A36-0000-1000-8000-00805F9B34FB", properties = setOf("notify")),
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

    @Test
    fun ftmsWhenService1826WithControlPoint2AD9() {
        assertEquals(TrainerProtocol.Ftms, ProtocolClassifier.classify(ftmsInventory))
    }

    @Test
    fun cycleopsProprietaryWhenCycleOpsServicePresent() {
        assertEquals(TrainerProtocol.CycleOpsProprietary, ProtocolClassifier.classify(cycleopsInventory))
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
