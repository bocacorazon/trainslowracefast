package com.trainerapp.core.ble

import com.trainerapp.core.domain.TrainerProtocol

/** Deterministic codec used by contract tests: known frames, no Saris guessing. */
object TestCodec : ControlCodec {
    override fun encodeTargetPower(watts: Int, protocol: TrainerProtocol): ByteArray =
        if (protocol.controllable) {
            byteArrayOf(0xAA.toByte(), (watts and 0xFF).toByte(), ((watts shr 8) and 0xFF).toByte())
        } else {
            ByteArray(0)
        }

    override fun encodeMinLoad(protocol: TrainerProtocol): ByteArray =
        if (protocol.controllable) byteArrayOf(0xBB.toByte()) else ByteArray(0)

    // No unlock handshake in contract tests: keeps writeLog assertions
    // single-frame; the unlock path is exercised live in the smoke ride.
    override fun encodeUnlock(protocol: TrainerProtocol): ByteArray = ByteArray(0)

    override fun unlockSettleMillis(protocol: TrainerProtocol): Long = 0L
}
