package com.trainerapp.core.sensor

/**
 * Heart Rate Measurement (0x2A37) per the Bluetooth SIG Heart Rate Service
 * spec, verified byte-exactly against live frames captured from the COOSPO
 * H808S chest strap on 2026-10-25 (fixtures/hr-h808s-frames.txt).
 *
 * Layout:
 *   flags uint8:
 *     bit0 = HR value format (0 = uint8, 1 = uint16 LE)
 *     bit1 = sensor contact supported
 *     bit2 = sensor contact detected
 *     bit3 = energy expended present (uint16 LE)
 *     bit4 = RR-intervals present (uint16 LE each, 1/1024 s)
 *   then: heart rate (uint8 or uint16 LE),
 *   then optional energy expended (uint16 LE),
 *   then optional RR intervals (uint16 LE each, to end of frame).
 */
data class HeartRateMeasurement(
    val flags: Int,
    val heartRateBpm: Int,
    val sensorContactSupported: Boolean,
    val sensorContactDetected: Boolean,
    val energyExpended: Int? = null,
    val rrIntervals: List<Int> = emptyList(),
) {
    companion object {

        fun parse(bytes: ByteArray): HeartRateMeasurement? {
            if (bytes.size < 2) return null
            val flags = bytes[0].toInt() and 0xFF

            val uint16Bpm = flags and 0x01 != 0
            var idx = 1
            val bpm: Int = if (uint16Bpm) {
                if (bytes.size < idx + 2) return null
                val v = u16(bytes, idx)
                idx += 2
                v
            } else {
                val v = bytes[idx].toInt() and 0xFF
                idx += 1
                v
            }

            var energy: Int? = null
            if (flags and 0x08 != 0) {
                if (bytes.size < idx + 2) return null
                energy = u16(bytes, idx)
                idx += 2
            }

            val rr = mutableListOf<Int>()
            if (flags and 0x10 != 0) {
                while (idx + 2 <= bytes.size) {
                    rr += u16(bytes, idx)
                    idx += 2
                }
            }

            return HeartRateMeasurement(
                flags = flags,
                heartRateBpm = bpm,
                sensorContactSupported = flags and 0x02 != 0,
                sensorContactDetected = flags and 0x04 != 0,
                energyExpended = energy,
                rrIntervals = rr,
            )
        }

        private fun u16(bytes: ByteArray, offset: Int): Int =
            ((bytes[offset].toInt() and 0xFF) or ((bytes[offset + 1].toInt() and 0xFF) shl 8))
    }
}
