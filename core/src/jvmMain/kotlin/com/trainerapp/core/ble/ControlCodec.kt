package com.trainerapp.core.ble

import com.trainerapp.core.domain.TrainerProtocol

/**
 * Encodes control frames for a given protocol. The Hammer H1 layout was
 * verified live in the 2026-10-25 smoke ride (see
 * docs/features/002-desktop-ble-erg-mvp/implementation/ble-adapter-notes.md)
 * and cross-checked against the qdomyos-zwift Wahoo-legacy implementation
 * (src/devices/wahookickrsnapbike) — the H1 speaks the pre-FTMS
 * "Wahoo extension to the Cycling Power Service" protocol on the
 * a026e005-0a7d-4ab3-97fa-f1500f9feb8b control characteristic.
 */
interface ControlCodec {
    /** ERG-mode target power frame, watts uint16 LE. */
    fun encodeTargetPower(watts: Int, protocol: TrainerProtocol): ByteArray

    /** Frame that requests the trainer's minimum load. */
    fun encodeMinLoad(protocol: TrainerProtocol): ByteArray

    /** Connect-time unlock/init frame; empty when the protocol has none. */
    fun encodeUnlock(protocol: TrainerProtocol): ByteArray

    /** How long to wait after [encodeUnlock] before further writes. */
    fun unlockSettleMillis(protocol: TrainerProtocol): Long
}

/**
 * Frames for the protocols the app supports.
 *
 * FTMS (0x2AD9 Fitness Machine Control Point), per the FTMS spec:
 *   Set Target Power = op code 0x05 + sint16 LE watts
 *   min load        = 0x05 + 0 watts
 *   (also requires Request Control 0x00 first — wired when an FTMS
 *   trainer appears in a later slice; the H1 has no FTMS service)
 *
 * CycleOps proprietary / Hammer H1 = Wahoo-legacy protocol, verified live:
 *   unlock/init    = [0x20, 0xEE, 0xFC], then wait ~700 ms
 *   target power   = [0x42, watts_lo, watts_hi]   (setErgMode, op 66)
 *   min load       = [0x42, 0x00, 0x00]           (ERG target 0 W)
 *   (min-load opcode for "release" was not determinable from the capture;
 *    ERG target 0 W is used instead)
 */
object BestEffortSarisCodec : ControlCodec {

    override fun encodeTargetPower(watts: Int, protocol: TrainerProtocol): ByteArray =
        when (protocol) {
            TrainerProtocol.Ftms -> byteArrayOf(0x05, (watts and 0xFF).toByte(), ((watts shr 8) and 0xFF).toByte())
            TrainerProtocol.CycleOpsProprietary -> byteArrayOf(0x42, (watts and 0xFF).toByte(), ((watts shr 8) and 0xFF).toByte())
            else -> ByteArray(0)
        }

    override fun encodeMinLoad(protocol: TrainerProtocol): ByteArray =
        when (protocol) {
            TrainerProtocol.Ftms -> byteArrayOf(0x05, 0, 0)
            TrainerProtocol.CycleOpsProprietary -> byteArrayOf(0x42, 0, 0)
            else -> ByteArray(0)
        }

    override fun encodeUnlock(protocol: TrainerProtocol): ByteArray =
        when (protocol) {
            TrainerProtocol.CycleOpsProprietary -> byteArrayOf(0x20, 0xEE.toByte(), 0xFC.toByte())
            else -> ByteArray(0)
        }

    override fun unlockSettleMillis(protocol: TrainerProtocol): Long =
        when (protocol) {
            TrainerProtocol.CycleOpsProprietary -> 700L
            else -> 0L
        }
}
