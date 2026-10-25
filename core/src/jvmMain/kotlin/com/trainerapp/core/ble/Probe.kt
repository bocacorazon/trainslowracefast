package com.trainerapp.core.ble

// Compile probe: verifies the Blue Falcon sdbus engine resolves and its
// Kotlin metadata is readable by this project's compiler.
import com.monkopedia.bluefalcon.sdbus.SdbusEngine
import dev.bluefalcon.core.BlueFalcon
import dev.bluefalcon.core.NoOpLogger

fun probeBlueFalcon(): BlueFalcon = BlueFalcon { engine = SdbusEngine { logger = NoOpLogger } }
