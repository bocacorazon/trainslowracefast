package com.trainerapp.core.fit

import kotlin.math.roundToLong

/**
 * Minimal FIT 2.0 (little-endian) activity-file encoder for ride recording
 * (increment 004).
 *
 * Pure domain code — no file I/O, no clock, no platform types — so the same
 * writer serves the desktop app today and the Android target later, and is
 * testable on every platform.
 *
 * File layout (little-endian):
 *   12-byte header: [12][0x10][profile LE16][data_size LE32][".FIT"]
 *   messages       : 1-byte header each; definition = 0x40|local, data = local
 *   CRC trailer    : 2-byte CRC16 over header + data region (per the FIT spec,
 *                    the file CRC covers every preceding byte), stored LE
 *
 * Messages written for a new file:
 *   file_id (0):        type=activity, manufacturer=255 (generic), product,
 *                       serial, time_created
 *   software_version:   0.1
 *   record (20):        heart_rate(3), distance(5, 0.01 m cumulative),
 *                       speed(6, 0.001 m/s), power(7), timestamp(253)
 *
 * [preData] is the raw message region of an in-progress file being resumed
 * (crash recovery): it is kept verbatim and new record messages are appended
 * after it.
 */

/** One 1 Hz telemetry sample. [timestamp] is absolute FIT epoch seconds. */
data class ActivitySample(
    val timestamp: Int,
    val watts: Int?,
    val heartRateBpm: Int?,
    val speedKph: Double?,
    val distanceMeters: Double,
)

class FitEncoder(private val preData: ByteArray = ByteArray(0)) {

    private val recordData = mutableListOf<ByteArray>()

    val count: Int get() = recordData.size

    fun add(sample: ActivitySample) {
        recordData.add(encodeRecord(sample))
    }

    /** Full file: header + messages + 2-byte CRC trailer. */
    fun toCompleteFile(): ByteArray {
        val body = header(dataRegion().size) + dataRegion()
        // Per the FIT spec the file CRC covers every preceding byte, header included.
        val crc = FitCrc.crc16(body, 0, body.size)
        return body + byteArrayOf((crc and 0xFF).toByte(), ((crc ushr 8) and 0xFF).toByte())
    }

    /** In-progress file: header + messages, NO CRC (detectable via [FitDecoder]). */
    fun toInProgressFile(): ByteArray = header(dataRegion().size) + dataRegion()

    private fun dataRegion(): ByteArray {
        val base = if (preData.isNotEmpty()) preData else baseMessages()
        val recs = recordData.fold(ByteArray(0)) { acc, b -> acc + b }
        return base + recs
    }

    private fun header(dataSize: Int): ByteArray {
        val h = ByteArray(12)
        h[0] = 12
        h[1] = 0x10 // protocol 2.0
        h[2] = 0x36 // profile 21.2 = 2102, LE16
        h[3] = 0x08
        h[4] = (dataSize and 0xFF).toByte()
        h[5] = ((dataSize shr 8) and 0xFF).toByte()
        h[6] = ((dataSize shr 16) and 0xFF).toByte()
        h[7] = ((dataSize shr 24) and 0xFF).toByte()
        h[8] = '.'.code.toByte()
        h[9] = 'F'.code.toByte()
        h[10] = 'I'.code.toByte()
        h[11] = 'T'.code.toByte()
        return h
    }

    private fun baseMessages(): ByteArray {
        return fileIdMessage() + softwareVersionMessage() + recordDefinition()
    }

    private fun fileIdMessage(): ByteArray {
        val def = definition(
            local = 0,
            global = 0, // file_id
            fields = listOf(
                Triple(0, 1, 0x00), // type, enum
                Triple(1, 1, 0x00), // manufacturer, enum
                Triple(2, 2, 0x84), // product, uint16
                Triple(3, 4, 0x86), // serial_number, uint32
                Triple(4, 4, 0x86), // time_created, uint32
                Triple(5, 2, 0x84), // number, uint16
            ),
        )
        val data = byteArrayOf(
            4, // type = activity
            255.toByte(), // manufacturer = generic
            1, 0, // product = 1
            0, 0, 0, 0, // serial_number
            0, 0, 0, 0, // time_created
            0, 0, // number
        )
        return def + data(0, data)
    }

    private fun softwareVersionMessage(): ByteArray {
        val def = definition(
            local = 1,
            global = 129, // software_version
            fields = listOf(
                Triple(0, 1, 0x02), // major_version, uint8
                Triple(1, 1, 0x02), // minor_version, uint8
                Triple(2, 4, 0x86), // build_number, uint32
            ),
        )
        val data = byteArrayOf(0, 1, 0, 0, 0, 0) // 0.1, build 0
        return def + data(1, data)
    }

    private fun recordDefinition(): ByteArray = definition(
        local = 2,
        global = 20, // record
        fields = listOf(
            Triple(3, 1, 0x02), // heart_rate, uint8
            Triple(5, 4, 0x86), // distance, uint32 (0.01 m)
            Triple(6, 2, 0x84), // speed, uint16 (0.001 m/s)
            Triple(7, 2, 0x84), // power, uint16 (watts)
            Triple(253, 4, 0x86), // timestamp, uint32
        ),
    )

    private fun encodeRecord(s: ActivitySample): ByteArray {
        // FIT speed field: 0.001 m/s units.
        val speed = s.speedKph?.let { (it / 3.6 * 1000.0).roundToLong() }
        val dist = (s.distanceMeters * 100).toLong()
        val data = byteArrayOf(
            (s.heartRateBpm?.coerceIn(0, 255)?.let { it.toByte() } ?: 0xFF.toByte()),
            (dist and 0xFF).toByte(),
            (((dist shr 8) and 0xFF).toByte()),
            (((dist shr 16) and 0xFF).toByte()),
            (((dist shr 24) and 0xFF).toByte()),
            (speed?.let { (it and 0xFF).toByte() } ?: 0xFF.toByte()),
            (speed?.let { (((it shr 8) and 0xFF).toByte()) } ?: 0xFF.toByte()),
            (s.watts?.coerceIn(0, 65_535)?.let { (it and 0xFF).toByte() } ?: 0xFF.toByte()),
            (s.watts?.coerceIn(0, 65_535)?.let { (((it shr 8) and 0xFF).toByte()) } ?: 0xFF.toByte()),
            (s.timestamp and 0xFF).toByte(),
            (((s.timestamp shr 8) and 0xFF).toByte()),
            (((s.timestamp shr 16) and 0xFF).toByte()),
            (((s.timestamp shr 24) and 0xFF).toByte()),
        )
        return data(2, data)
    }

    /** 1-byte definition header + reserved/arch + global LE16 + field count + field defs. */
    private fun definition(local: Int, global: Int, fields: List<Triple<Int, Int, Int>>): ByteArray {
        val out = ByteArray(6 + 3 * fields.size)
        var i = 0
        out[i++] = (0x40 or (local and 0x0F)).toByte()
        out[i++] = 0 // reserved
        out[i++] = 0 // architecture = 0 (little-endian)
        out[i++] = (global and 0xFF).toByte()
        out[i++] = ((global shr 8) and 0xFF).toByte()
        out[i++] = fields.size.toByte()
        for ((defNum, size, base) in fields) {
            out[i++] = defNum.toByte()
            out[i++] = size.toByte()
            out[i++] = base.toByte()
        }
        return out
    }

    /** 1-byte data header + payload. */
    private fun data(local: Int, payload: ByteArray): ByteArray {
        val out = ByteArray(1 + payload.size)
        out[0] = (local and 0x0F).toByte()
        payload.copyInto(out, 1)
        return out
    }
}

/** FIT CRC16 (polynomial 0xA001, initial 0, no final xor). */
object FitCrc {

    fun crc16(data: ByteArray, from: Int, to: Int): Int {
        var crc = 0
        for (i in from until to) {
            // FIT SDK: xor the byte into the FULL 16-bit accumulator, then 8 shifts.
            var c = crc xor (data[i].toInt() and 0xFF)
            repeat(8) {
                c = if (c and 1 != 0) (c ushr 1) xor 0xA001 else c ushr 1
            }
            crc = c
        }
        return crc
    }

    /**
     * True when [bytes] is a complete FIT file: plausible header and a valid
     * CRC over all preceding bytes (header included) matching the 2-byte trailer.
     */
    fun isComplete(bytes: ByteArray): Boolean {
        if (bytes.size < 14) return false
        if (bytes[0].toInt() and 0xFF != 12) return false
        if (bytes[1].toInt() and 0xFF != 0x10) return false
        if (bytes[8] != '.'.code.toByte() || bytes[9] != 'F'.code.toByte() ||
            bytes[10] != 'I'.code.toByte() || bytes[11] != 'T'.code.toByte()
        ) return false
        val dataSize = (bytes[4].toInt() and 0xFF) or
            ((bytes[5].toInt() and 0xFF) shl 8) or
            ((bytes[6].toInt() and 0xFF) shl 16) or
            ((bytes[7].toInt() and 0xFF) shl 24)
        if (bytes.size != dataSize + 12 + 2) return false
        val stored = (bytes[bytes.size - 2].toInt() and 0xFF) or
            ((bytes[bytes.size - 1].toInt() and 0xFF) shl 8)
        // The file CRC covers the header too, so start at byte 0.
        return crc16(bytes, 0, bytes.size - 2) == stored
    }
}

/**
 * Decodes files produced by [FitEncoder] (complete or in progress). Used for
 * crash recovery: an in-progress file has a valid header but no valid CRC
 * trailer — its record messages are still there.
 */
object FitDecoder {

    data class Parsed(
        val lastTimestamp: Int?, // absolute FIT epoch of the last record
        val sampleCount: Int,
        val complete: Boolean,
    )

    /** Null when [bytes] is not even a plausible file header. */
    fun parse(bytes: ByteArray): Parsed? {
        if (bytes.size < 12) return null
        if (bytes[0].toInt() and 0xFF != 12) return null
        if (bytes[8] != '.'.code.toByte() || bytes[9] != 'F'.code.toByte() ||
            bytes[10] != 'I'.code.toByte() || bytes[11] != 'T'.code.toByte()
        ) return null

        val payloadSizes = IntArray(16)
        var pos = 12
        var lastTs: Int? = null
        var count = 0
        while (pos < bytes.size) {
            val head = bytes[pos].toInt() and 0xFF
            if (head and 0x80 != 0) break // compressed-timestamp record: not ours
            if (head and 0x40 != 0) {
                // definition message: 6-byte base + 3 bytes per field
                if (pos + 6 > bytes.size) break
                val local = head and 0x0F
                val numFields = bytes[pos + 5].toInt() and 0xFF
                if (pos + 6 + 3 * numFields > bytes.size) break
                var size = 0
                for (n in 0 until numFields) {
                    size += bytes[pos + 6 + 3 * n + 1].toInt() and 0xFF
                }
                payloadSizes[local] = size
                pos += 6 + 3 * numFields
            } else {
                // data message: 1-byte header + payload sized by its definition
                val local = head and 0x0F
                val size = payloadSizes[local]
                if (size == 0) break
                val payloadStart = pos + 1
                if (payloadStart + size > bytes.size) break // truncated mid-record
                if (local == 2) {
                    // record: timestamp is the last 4 bytes of the payload
                    count++
                    lastTs = (bytes[payloadStart + size - 4].toInt() and 0xFF) or
                        ((bytes[payloadStart + size - 3].toInt() and 0xFF) shl 8) or
                        ((bytes[payloadStart + size - 2].toInt() and 0xFF) shl 16) or
                        ((bytes[payloadStart + size - 1].toInt() and 0xFF) shl 24)
                }
                pos = payloadStart + size
            }
        }
        return Parsed(lastTs, count, FitCrc.isComplete(bytes))
    }
}
