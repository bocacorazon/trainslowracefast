package com.trainerapp.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trainerapp.core.domain.TrainerClient
import com.trainerapp.core.erg.ErgController
import com.trainerapp.core.store.SettingsStore

enum class Screen { Ride, Devices }

/**
 * Top-level navigation: the Devices screen is always reachable (grill
 * decision D10) — first launch opens there, later launches (with a
 * remembered trainer) open on the ride screen.
 */
@Composable
fun TrainerApp(
    erg: ErgController,
    trainerClient: TrainerClient,
    settingsStore: SettingsStore,
    session: MultiDeviceSession,
) {
    var screen by remember {
        mutableStateOf(if (settingsStore.load().trainer != null) Screen.Ride else Screen.Devices)
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { screen = Screen.Devices }) { Text("Devices") }
            Button(onClick = { screen = Screen.Ride }) { Text("Ride") }
        }
        when (screen) {
            Screen.Devices -> DevicesScreen(
                erg = erg,
                trainerClient = trainerClient,
                settingsStore = settingsStore,
                session = session,
                onConnected = { screen = Screen.Ride },
            )
            Screen.Ride -> RideScreen(
                erg = erg,
                trainerClient = trainerClient,
                hrTelemetry = session.sensorTelemetry,
                hrStatusText = session.sensorStatusText,
                onDisconnectAll = { session.disconnectAll(erg) },
            )
        }
    }
}