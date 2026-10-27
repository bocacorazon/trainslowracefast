package com.trainerapp.core.fit

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FitWriterTest {

    @Test
    fun crcMatchesFitSdkVectors() {
        // Vectors from fitparse's reference implementation (FIT SDK CRC-16).
        val all = ByteArray(256) { it.toByte() }
        assertEquals(0xBAD3, FitCrc.crc16(all, 0, all.size))
        assertEquals(0xBB3D, FitCrc.crc16("123456789".encodeToByteArray(), 0, 9))
        assertEquals(0, FitCrc.crc16(all, 5, 5), "empty range is 0")
    }

    private val samples = (0..9).map {
        ActivitySample(timestamp = 1_700_000 + it, watts = 200 + it, heartRateBpm = 140, speedKph = 32.0, distanceMeters = it * (32.0 / 3.6))
    }

    private fun encoder(): FitEncoder {
        val e = FitEncoder()
        samples.forEach { e.add(it) }
        return e
    }

    @Test
    fun completeFileHasValidHeaderAndCrc() {
        val file = encoder().toCompleteFile()
        assertEquals(12.toByte(), file[0], "12-byte base header")
        assertEquals(0x10.toByte(), file[1], "protocol 2.0")
        assertTrue(
            file[8] == '.'.code.toByte() && file[9] == 'F'.code.toByte() &&
                file[10] == 'I'.code.toByte() && file[11] == 'T'.code.toByte()
        )
        assertTrue(FitCrc.isComplete(file))
    }

    @Test
    fun truncatedFileIsIncomplete() {
        val file = encoder().toCompleteFile()
        assertFalse(FitCrc.isComplete(file.copyOf(file.size - 2)), "dropping the 2-byte CRC trailer makes it incomplete")
        val inProgress = encoder().toInProgressFile()
        assertFalse(FitCrc.isComplete(inProgress), "in-progress file has no trailer")
        assertTrue(inProgress.size < file.size)
    }

    @Test
    fun decoderRecoversCountAndLastTimestamp() {
        val file = encoder().toCompleteFile()
        val parsed = FitDecoder.parse(file)
        assertNotNull(parsed)
        assertTrue(parsed.complete)
        assertEquals(10, parsed.sampleCount)
        assertEquals(1_700_009, parsed.lastTimestamp)
    }

    @Test
    fun resumeKeepsOldDataAndAppendsNew() {
        // First session writes 5 samples in progress.
        val first = FitEncoder()
        samples.take(5).forEach { first.add(it) }
        val crashed = first.toInProgressFile()
        assertFalse(FitCrc.isComplete(crashed))

        // Second session resumes from the raw message region and adds the rest.
        val resumed = FitEncoder(crashed.copyOfRange(12, crashed.size))
        samples.drop(5).forEach { resumed.add(it) }
        val done = resumed.toCompleteFile()
        assertTrue(FitCrc.isComplete(done))
        val parsed = FitDecoder.parse(done)
        assertNotNull(parsed)
        assertEquals(10, parsed.sampleCount)
        assertEquals(1_700_009, parsed.lastTimestamp)
    }

    @Test
    fun nullTelemetryEncodesAsFitInvalidValues() {
        val e = FitEncoder()
        e.add(ActivitySample(timestamp = 0, watts = null, heartRateBpm = null, speedKph = null, distanceMeters = 0.0))
        val file = e.toCompleteFile()
        assertTrue(FitCrc.isComplete(file))
        val parsed = FitDecoder.parse(file)
        assertNotNull(parsed)
        assertEquals(1, parsed.sampleCount)
    }

    @Test
    fun parseRejectsGarbage() {
        assertNull(FitDecoder.parse(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)))
        assertNull(FitDecoder.parse(byteArrayOf(0x0E)))
    }
}
