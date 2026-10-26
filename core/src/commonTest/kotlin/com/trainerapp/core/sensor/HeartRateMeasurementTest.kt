package com.trainerapp.core.sensor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Parser tests built from the REAL wire bytes captured from the COOSPO
 * H808S on 2026-10-25 (fixtures/hr-h808s-frames.txt) — never invented
 * frames (the 002 lesson).
 */
class HeartRateMeasurementTest {

    private fun capturedFrames(): List<Pair<String, ByteArray>> {
        val stream = HeartRateMeasurementTest::class.java.classLoader
            .getResourceAsStream("fixtures/hr-h808s-frames.txt")
        assertTrue(stream != null, "captured H808S frames fixture must be on the test classpath")
        return stream!!.readBytes().decodeToString()
            .lines()
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .map { line ->
                line.trim() to line.trim().split(Regex("\\s+")).map { it.toInt(16).toByte() }.toByteArray()
            }
    }

    @Test
    fun parses_all_captured_h808s_frames_with_expected_bpm() {
        val frames = capturedFrames()
        assertTrue(frames.size >= 10, "expected at least 10 captured frames, got ${frames.size}")

        // Expected bpm decoded from the captured wire bytes (flags 0x16 =
        // uint8 bpm, contact detected, RR present).
        val expectedBpm = listOf(88, 77, 77, 76, 74, 74, 73, 73, 72, 72)
        frames.take(10).forEachIndexed { i, (_, bytes) ->
            val m = HeartRateMeasurement.parse(bytes)!!
            assertEquals(expectedBpm[i], m.heartRateBpm, "frame $i (${bytes.joinToString(" ") { "%02x".format(it) }})")
        }
    }

    @Test
    fun captured_frames_report_sensor_contact_detected() {
        capturedFrames().take(10).forEach { (_, bytes) ->
            val m = HeartRateMeasurement.parse(bytes)!!
            // flags 0x16: bit1 contact supported, bit2 contact detected
            assertTrue(m.sensorContactSupported, "flags ${m.flags}")
            assertTrue(m.sensorContactDetected, "flags ${m.flags}")
            assertNull(m.energyExpended)
        }
    }

    @Test
    fun captured_frame_with_two_rr_intervals_parses_both() {
        // "16 4a 67 03 75 03" — 74 bpm, two RR intervals
        val bytes = byteArrayOf(0x16, 0x4a, 0x67, 0x03, 0x75, 0x03)
        val m = HeartRateMeasurement.parse(bytes)!!
        assertEquals(74, m.heartRateBpm)
        assertEquals(listOf(0x0367, 0x0375), m.rrIntervals)
    }

    @Test
    fun uint16_bpm_format_flag_is_honored() {
        // flags 0x01: uint16 LE bpm; 0x012C = 300 bpm (stress-test format)
        val bytes = byteArrayOf(0x01, 0x2c, 0x01)
        val m = HeartRateMeasurement.parse(bytes)!!
        assertEquals(300, m.heartRateBpm)
        assertTrue(m.rrIntervals.isEmpty())
    }

    @Test
    fun energy_expended_field_is_skipped_before_rr() {
        // flags 0x18: energy present + RR present; bpm 80, energy 0x0064, RR 0x0300
        val bytes = byteArrayOf(0x18, 0x50, 0x64, 0x00, 0x00, 0x03)
        val m = HeartRateMeasurement.parse(bytes)!!
        assertEquals(80, m.heartRateBpm)
        assertEquals(100, m.energyExpended)
        assertEquals(listOf(0x0300), m.rrIntervals)
    }

    @Test
    fun truncated_frames_return_null() {
        assertNull(HeartRateMeasurement.parse(byteArrayOf(0x16)))
        assertNull(HeartRateMeasurement.parse(ByteArray(0)))
        // uint16 bpm flag but only one byte of bpm
        assertNull(HeartRateMeasurement.parse(byteArrayOf(0x01, 0x2c)))
    }
}
