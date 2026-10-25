package com.trainerapp.core.ble

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CyclingPowerMeasurementTest {

    @Test
    fun parse_power_only() {
        val p = CyclingPowerMeasurement.parse(byteArrayOf(0x01, 250.toByte(), 0))!!
        assertEquals(250, p.watts)
        assertNull(p.cadence)
        assertNull(p.leftRightBalance)
    }

    @Test
    fun parse_power_with_cadence() {
        // flags: power present (0x01) + cadence present (0x08)
        val p = CyclingPowerMeasurement.parse(byteArrayOf(0x09, 250.toByte(), 0, 90))!!
        assertEquals(250, p.watts)
        assertEquals(90, p.cadence)
    }

    @Test
    fun parse_with_all_optional_fields() {
        // flags 0x3F: power, accumulated time, cadence, torque effectiveness, L/R balance
        // power uint16 LE 300 = 0x012C -> [0x2C, 0x01]; time=0; cadence=5; torque=0; balance=12
        val p = CyclingPowerMeasurement.parse(
            byteArrayOf(0x3F, 0x2C, 0x01, /* time */ 0, 0, 0, 0, /* cadence */ 5, /* torque */ 0, /* balance */ 12)
        )!!
        assertEquals(300, p.watts)
        assertEquals(5, p.cadence)
        assertEquals(12, p.leftRightBalance)
    }

    @Test
    fun parse_without_power_flag_is_null() {
        assertNull(CyclingPowerMeasurement.parse(byteArrayOf(0x00)))
    }

    @Test
    fun parse_truncated_packet_is_null() {
        assertNull(CyclingPowerMeasurement.parse(byteArrayOf(0x01, 250.toByte())))
    }

    @Test
    fun speed_parse() {
        // 0x2A65: uint16 LE in 1/100 m/s units.
        assertEquals(39.24, SpeedCharacteristic.parse(byteArrayOf(0x42, 0x04))!!, 1e-9)
        assertNull(SpeedCharacteristic.parse(byteArrayOf(0x01)))
        assertNull(SpeedCharacteristic.parse(ByteArray(0)))
    }
}

class BestEffortSarisCodecTest {

    @Test
    fun ftms_target_power_uses_standard_set_power_frame() {
        // FTMS Control Point: opcode 0x02 (Set Power) + uint16 LE target.
        assertContentEquals(
            byteArrayOf(0x02, 120, 0),
            BestEffortSarisCodec.encodeTargetPower(120, com.trainerapp.core.domain.TrainerProtocol.Ftms),
        )
        assertContentEquals(
            byteArrayOf(0x02, 0x58, 0x02),
            BestEffortSarisCodec.encodeTargetPower(600, com.trainerapp.core.domain.TrainerProtocol.Ftms),
        )
    }

    @Test
    fun ftms_min_load_writes_zero_target() {
        assertContentEquals(
            byteArrayOf(0x02, 0, 0),
            BestEffortSarisCodec.encodeMinLoad(com.trainerapp.core.domain.TrainerProtocol.Ftms),
        )
    }

    @Test
    fun cycleops_target_power_uses_best_effort_frame() {
        // Best-effort (UNVERIFIED — see ble-adapter-notes.md): opcode 0x01 + uint16 LE watts.
        assertContentEquals(
            byteArrayOf(0x01, 120, 0),
            BestEffortSarisCodec.encodeTargetPower(120, com.trainerapp.core.domain.TrainerProtocol.CycleOpsProprietary),
        )
    }

    @Test
    fun cycleops_min_load_frame() {
        assertContentEquals(
            byteArrayOf(0x02),
            BestEffortSarisCodec.encodeMinLoad(com.trainerapp.core.domain.TrainerProtocol.CycleOpsProprietary),
        )
    }

    @Test
    fun non_controllable_protocol_produces_no_frame() {
        assertTrue(
            BestEffortSarisCodec.encodeTargetPower(
                120,
                com.trainerapp.core.domain.TrainerProtocol.TelemetryOnly
            ).isEmpty()
        )
        assertTrue(
            BestEffortSarisCodec.encodeMinLoad(
                com.trainerapp.core.domain.TrainerProtocol.Unsupported
            ).isEmpty()
        )
    }
}
