package com.trainerapp.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.trainerapp.core.FakeTrainerClient
import com.trainerapp.core.domain.TrainerCandidate
import com.trainerapp.core.domain.TrainerClient
import com.trainerapp.core.erg.ErgController
import com.trainerapp.core.store.JvmSettingsFile
import com.trainerapp.core.store.SettingsStore
import com.trainerapp.core.store.TrainerIdentity
import kotlinx.coroutines.launch

/**
 * The single wiring point for the trainer client (design boundary: the UI
 * only ever talks to the TrainerClient contract).
 *
 * MVP/fake: FakeTrainerClient. The smoke-ride item flips ONLY this function
 * to the real BLE stack:
 *
 *     BlueFalconTrainerClient(BlueFalconBleBackend())
 *
 * (core/src/jvmMain .../ble/, with the pluggable BestEffortSarisCodec).
 */
fun createTrainerClient(): TrainerClient = FakeTrainerClient()

/** Reconstructs a connectable candidate from the persisted trainer identity. */
private fun TrainerIdentity.toCandidate(): TrainerCandidate =
    TrainerCandidate(name = name, platformId = platformId, rssi = 0)

fun main() = application {
    val scope = rememberCoroutineScope()
    val settingsStore = remember { SettingsStore(JvmSettingsFile.default()) }
    val trainerClient = remember { createTrainerClient() }
    val erg = remember { ErgController(trainerClient, settingsStore, scope) }

    Window(title = "TrainerApp", onCloseRequest = ::exitApplication) {
        TrainerApp(erg, trainerClient, settingsStore)
    }

    // Grill decision D10: subsequent launches auto-connect to the remembered
    // trainer (ERG engages automatically at the remembered wattage).
    val remembered = settingsStore.load().trainer
    if (remembered != null) {
        LaunchedEffect(remembered.platformId) {
            scope.launch { erg.connect(remembered.toCandidate()) }
        }
    }
}