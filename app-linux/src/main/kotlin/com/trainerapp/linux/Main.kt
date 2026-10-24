package com.trainerapp.linux

import com.trainerapp.core.FakeTrainerClient
import com.trainerapp.core.domain.TrainerCandidate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Skeleton console diagnostic for the Linux machine (NUC).
 * Prints shared TrainerState transitions and telemetry; the real BlueZ
 * adapter replaces [FakeTrainerClient] in the next work item.
 */
fun main() = runBlocking {
    val client = FakeTrainerClient()
    val ui = launch {
        client.state.collectLatest { println("[state] $it") }
    }
    val telemetry = launch {
        client.telemetry.collectLatest { t ->
            if (t.watts != null) println("[watts] ${t.watts} W (stale=${t.stale})")
        }
    }

    println("== trainer diagnostic (skeleton, fake client) ==")
    client.scan().collectLatest { c -> println("[scan] ${c.name} rssi=${c.rssi}") }
    client.connect(TrainerCandidate("HAMMER-TRAINER", "fake", -55))
    println("[control] 100 W -> ${client.setTargetPower(100)}")
    println("[control] 150 W -> ${client.setTargetPower(150)}")
    println("[control] 200 W -> ${client.setTargetPower(200)}")
    client.disconnect()

    delay(200)
    ui.cancel()
    telemetry.cancel()
    println("== done ==")
}
