package com.trainerapp.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trainerapp.core.domain.TrainerCandidate
import com.trainerapp.core.domain.TrainerClient
import com.trainerapp.core.erg.ErgController
import com.trainerapp.core.store.SettingsStore
import com.trainerapp.core.store.TrainerIdentity
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

/**
 * The devices screen (grill decision D10): always reachable, lists scan
 * candidates by name (never a raw device address in the UI), selects/replaces
 * the remembered trainer.
 */
@Composable
fun DevicesScreen(
    erg: ErgController,
    trainerClient: TrainerClient,
    settingsStore: SettingsStore,
    onConnected: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var candidates by remember { mutableStateOf<List<TrainerCandidate>>(emptyList()) }
    var remembered by remember { mutableStateOf<TrainerIdentity?>(settingsStore.load().trainer) }

    // One row per device; anonymous MAC-named junk filtered out (the BLE
    // neighbourhood is full of it); real signal readings first, unknown (0)
    // last. Found live in the smoke ride: unknown-rssi junk (0) outranked
    // the trainer's genuine -69 dBm.
    val macLike = Regex("^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$")
    suspend fun scan(): List<TrainerCandidate> =
        trainerClient.scan().toList()
            .distinctBy { it.platformId }
            .filterNot { macLike.matches(it.name.trim()) }
            .sortedByDescending { if (it.rssi == 0) Int.MIN_VALUE else it.rssi }

    LaunchedEffect(Unit) { candidates = scan() }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Devices", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        remembered?.let {
            Text(
                "remembered trainer: ${it.name} (${it.transport})",
                fontSize = 14.sp,
                color = Color(0xFF616161),
            )
        } ?: Text(
            "no trainer selected yet — scan and pick one",
            fontSize = 14.sp,
            color = Color(0xFF616161),
        )

        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = { scope.launch { candidates = scan() } }) { Text("Rescan") }
        Spacer(Modifier.height(12.dp))

        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            items(candidates.size) { index ->
                val candidate = candidates[index]
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("${candidate.name}   rssi ${candidate.rssi}", fontSize = 16.sp)
                    Button(onClick = {
                        scope.launch {
                            erg.connect(candidate)
                            remembered = settingsStore.load().trainer
                            onConnected()
                        }
                    }) { Text("Connect") }
                }
            }
        }

        if (candidates.isEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(
                "nothing found yet — Rescan",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
            )
        }
    }
}