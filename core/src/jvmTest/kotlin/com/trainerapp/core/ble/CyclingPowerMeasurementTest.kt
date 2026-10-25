package com.trainerapp.core.ble

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CyclingPowerMeasurementTest {

    @Test
    fun parse_real_captured_idle_frame() {
        // Live frame captured from the Hammer H1 in the 2026-10-25 smoke
        // ride (18 bytes, flags 0x1834, power 0 at connect):
        // 34 18 00 00 5e c8 74 97 ...
        val p = CyclingPowerMeasurement.parse(
            byteArrayOf(
                0x34, 0x18, 0x00, 0x00,
                0x5e, 0xc8.toByte(), 0x74, 0x97.toByte(),
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            )
        )!!
        assertEquals(0, p.watts)
        assertNull(p.cadence)
        assertNull(p.leftRightBalance)
    }

    @Test
    fun parse_real_captured_frame_with_power() {
        // Same flags layout, pedaling: 300 W = 0x012C LE at bytes 2..3.
        val p = CyclingPowerMeasurement.parse(
            byteArrayOf(
                0x34, 0x18, 0x2c, 0x01,
                0x5e, 0xc8.toByte(), 0x75, 0x97.toByte(),
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            )
        )!!
        assertEquals(300, p.watts)
    }

    @Test
    fun parse_minimal_flags_zero_frame() {
        // flags 0x0000, 250 W = 0x00FA LE.
        val p = CyclingPowerMeasurement.parse(byteArrayOf(0x00, 0x00, 0xfa.toByte(), 0x00))!!
        assertEquals(250, p.watts)
    }

    @Test
    fun parse_truncated_packet_is_null() {
        assertNull(CyclingPowerMeasurement.parse(byteArrayOf(0x34, 0x18, 0x00)))
        assertNull(CyclingPowerMeasurement.parse(ByteArray(0)))
    }
}

class BestEffortSarisCodecTest {

    @Test
    fun ftms_target_power_uses_standard_set_power_frame() {
        // FTMS spec 4.16.2.6: Set Target Power op code 0x05 + sint16 LE.
        assertContentEquals(
            byteArrayOf(0x05, 120, 0),
            BestEffortSarisCodec.encodeTargetPower(120, com.trainerapp.core.domain.TrainerProtocol.Ftms),
        )
        assertContentEquals(
            byteArrayOf(0x05, 0x58, 0x02),
            BestEffortSarisCodec.encodeTargetPower(600, com.trainerapp.core.domain.TrainerProtocol.Ftms),
        )
    }

    @Test
    fun ftms_min_load_writes_zero_target() {
        assertContentEquals(
            byteArrayOf(0x05, 0, 0),
            BestEffortSarisCodec.encodeMinLoad(com.trainerapp.core.domain.TrainerProtocol.Ftms),
        )
    }

    @Test
    fun cycleops_target_power_uses_wahoo_erg_frame() {
        // Wahoo-legacy protocol, verified live on the H1 (smoke ride) and
        // cross-checked with qdomyos-zwift: setErgMode op 0x42 + uint16 LE.
        assertContentEquals(
            byteArrayOf(0x42, 120, 0),
            BestEffortSarisCodec.encodeTargetPower(120, com.trainerapp.core.domain.TrainerProtocol.CycleOpsProprietary),
        )
        assertContentEquals(
            byteArrayOf(0x42, 0x93.toByte(), 0),
            BestEffortSarisCodec.encodeTargetPower(147, com.trainerapp.core.domain.TrainerProtocol.CycleOpsProprietary),
        )
    }

    @Test
    fun cycleops_min_load_frame() {
        // ERG target 0 W.
        assertContentEquals(
            byteArrayOf(0x42, 0, 0),
            BestEffortSarisCodec.encodeMinLoad(com.trainerapp.core.domain.TrainerProtocol.CycleOpsProprietary),
        )
    }

    @Test
    fun cycleops_unlock_frame_and_settle() {
        // Wahoo-legacy unlock: [0x20 0xEE 0xFC], ~700 ms settle.
        assertContentEquals(
            byteArrayOf(0x20, 0xEE.toByte(), 0xFC.toByte()),
            BestEffortSarisCodec.encodeUnlock(com.trainerapp.core.domain.TrainerProtocol.CycleOpsProprietary),
        )
        assertEquals(700L, BestEffortSarisCodec.unlockSettleMillis(com.trainerapp.core.domain.TrainerProtocol.CycleOpsProprietary))
        assertTrue(BestEffortSarisCodec.encodeUnlock(com.trainerapp.core.domain.TrainerProtocol.Ftms).isEmpty())
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
        assertTrue(
            BestEffortSarisCodec.encodeUnlock(
                com.trainerapp.core.domain.TrainerProtocol.TelemetryOnly
            ).isEmpty()
        )
    }
}
