package com.trainerapp.core.ble

/**
 * Parser for the GATT telemetry packets the Hammer H1 emits.
 *
 * 0x2A63 Cycling Power Measurement per the Bluetooth SIG Cycling Power
 * Service spec: flags uint16 LE, then Instantaneous Power sint16 LE at a
 * FIXED offset (bytes 2..3) — present regardless of which optional fields
 * the flags announce. Optional fields (pedal balance, accumulated torque,
 * extreme magnitudes, crank data, …) follow, in flag order.
 *
 * The Hammer H1 reports power only (no cadence — WI-A hardware capture);
 * optional-field walking is deliberately omitted until a slice needs it.
 *
 * Format verified against live frames captured in the 2026-10-25 smoke
 * ride: 18-byte notifications `34 18 00 00 5e c8 …` (flags 0x1834).
 */
object CyclingPowerMeasurement {

    data class Parsed(
        val watts: Int,
        val cadence: Int? = null,
        val leftRightBalance: Int? = null,
    )

    fun parse(bytes: ByteArray): Parsed? {
        if (bytes.size < 4) return null
        val raw = ((bytes[2].toInt() and 0xFF) or ((bytes[3].toInt() and 0xFF) shl 8))
        val watts = if (raw > 0x7FFF) raw - 0x10000 else raw
        return Parsed(watts = watts)
    }
}
