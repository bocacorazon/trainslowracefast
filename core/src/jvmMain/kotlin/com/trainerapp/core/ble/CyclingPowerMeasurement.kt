package com.trainerapp.core.ble

/**
 * Parsers for the GATT telemetry packets the Hammer H1 emits.
 *
 * 0x2A63 Cycling Power Measurement (BLE SIG Cycling Power Measurement
 * characteristics, flags-then-fields layout):
 *   byte 0: flags — bit0 instantaneous power, bit1 accumulated time,
 *           bit3 cadence, bit4 torque effectiveness, bit5 L/R balance,
 *           bit6 L/R external balance
 *   then, in order: power uint16, time uint32 (1/100 min), cadence uint8,
 *   torque effectiveness uint8, L/R balance uint8 (0.1% of left).
 *
 * 0x2A65 Speed: uint16 LE in 1/100 m/s units.
 */
object CyclingPowerMeasurement {

    data class Parsed(
        val watts: Int?,
        val cadence: Int? = null,
        val leftRightBalance: Int? = null,
    )

    fun parse(bytes: ByteArray): Parsed? {
        if (bytes.isEmpty()) return null
        val flags = bytes[0].toInt() and 0xFF
        if (flags and 0x01 == 0) return null
        if (bytes.size < 3) return null

        val watts = (bytes[1].toInt() and 0xFF) or ((bytes[2].toInt() and 0xFF) shl 8)
        var idx = 3
        if (flags and 0x02 != 0 && bytes.size >= idx + 4) idx += 4 // accumulated time

        var cadence: Int? = null
        if (flags and 0x08 != 0 && bytes.size >= idx + 1) {
            cadence = bytes[idx].toInt() and 0xFF
            idx += 1
        }

        if (flags and 0x10 != 0 && bytes.size >= idx + 1) idx += 1 // torque effectiveness

        var balance: Int? = null
        if (flags and 0x20 != 0 && bytes.size >= idx + 1) {
            balance = bytes[idx].toInt() and 0xFF
        }

        return Parsed(watts = watts, cadence = cadence, leftRightBalance = balance)
    }
}

/** 0x2A65 Speed characteristic: uint16 LE in 1/100 m/s units -> kph. */
object SpeedCharacteristic {

    fun parse(bytes: ByteArray): Double? {
        if (bytes.size < 2) return null
        val mPerS = (((bytes[0].toInt() and 0xFF) or ((bytes[1].toInt() and 0xFF) shl 8)).toLong() * 1L) / 100.0
        return mPerS * 3.6
    }
}
