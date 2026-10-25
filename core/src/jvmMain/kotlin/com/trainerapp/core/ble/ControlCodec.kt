package com.trainerapp.core.ble

import com.trainerapp.core.domain.TrainerProtocol

/**
 * Encodes control frames for a given protocol. The frame layout is the
 * hardware-gated unknown on the Hammer H1 (see
 * docs/features/002-desktop-ble-erg-mvp/implementation/ble-adapter-notes.md);
 * the smoke-ride item verifies/corrects the CycleOps frames against live
 * hardware by implementing this interface — no adapter rework needed.
 */
interface ControlCodec {
    fun encodeTargetPower(watts: Int, protocol: TrainerProtocol): ByteArray
    fun encodeMinLoad(protocol: TrainerProtocol): ByteArray
}

/**
 * Best-effort codec.
 *
 * FTMS: standard, spec-documented frames on the FTMS Control Point 0x2AD9:
 *   target power = opcode 0x02 (Set Power) + uint16 LE watts
 *   min load     = opcode 0x02 + 0 watts
 *
 * CycleOps proprietary (Hammer H1): **UNVERIFIED** — no public opcode
 * documentation exists (qdomyos-zwift has no CycleOps BLE support; the
 * opcodes were not discoverable from the GATT capture). Frames are a
 * documented best guess:
 *   target power = opcode 0x01 + uint16 LE watts
 *   min load     = opcode 0x02
 * Replace with the live-verified layout in the smoke-ride work item.
 */
object BestEffortSarisCodec : ControlCodec {

    override fun encodeTargetPower(watts: Int, protocol: TrainerProtocol): ByteArray =
        when (protocol) {
            TrainerProtocol.Ftms -> byteArrayOf(0x02, (watts and 0xFF).toByte(), ((watts shr 8) and 0xFF).toByte())
            TrainerProtocol.CycleOpsProprietary -> byteArrayOf(0x01, (watts and 0xFF).toByte(), ((watts shr 8) and 0xFF).toByte())
            else -> ByteArray(0)
        }

    override fun encodeMinLoad(protocol: TrainerProtocol): ByteArray =
        when (protocol) {
            TrainerProtocol.Ftms -> byteArrayOf(0x02, 0, 0)
            TrainerProtocol.CycleOpsProprietary -> byteArrayOf(0x02)
            else -> ByteArray(0)
        }
}
