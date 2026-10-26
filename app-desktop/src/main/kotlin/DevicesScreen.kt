package com.trainerapp.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.trainerapp.core.domain.TrainerState
import com.trainerapp.core.erg.ErgController
import com.trainerapp.core.store.SettingsStore
import com.trainerapp.core.store.TrainerIdentity
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

/**
 * The devices screen (grill decision D10 + 003 D9): always reachable, lists
 * scan candidates by name (never a raw device address in the UI), connects
 * ANY device — trainer or sensor — simultaneously, shows per-device
 * capability badges for connected devices (derived from the connect-time
 * classification, never from names), offers per-device disconnect, and a
 * heart-rate source dropdown (the manual override of the source mapping).
 */
@Composable
fun DevicesScreen(
    erg: ErgController,
    trainerClient: TrainerClient,
    settingsStore: SettingsStore,
    session: MultiDeviceSession,
    onConnected: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var candidates by remember { mutableStateOf<List<TrainerCandidate>>(emptyList()) }
    var remembered by remember { mutableStateOf<TrainerIdentity?>(settingsStore.load().trainer) }

    val trainerState by trainerClient.state.collectAsState()
    val sensorStatus by session.sensorStatusText.collectAsState()
    val sensorConnected = sensorStatus == "connected"

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

        // 003: the remembered sensor sources are part of "remembered devices"
        // too (the HR source persists independently of the trainer) — show it,
        // resolving its friendly name from the current scan when possible.
        val rememberedHr = session.rememberedHeartRate()
        if (rememberedHr != null) {
            val hrName = candidates.firstOrNull { it.platformId == rememberedHr }?.name
                ?: session.sensorName
                ?: rememberedHr
            Text(
                "remembered heart rate source: $hrName",
                fontSize = 14.sp,
                color = Color(0xFF616161),
            )
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = { scope.launch { candidates = scan() } }) { Text("Rescan") }
        Spacer(Modifier.height(12.dp))

        // Connected devices with capability badges (003 D9): badges derive
        // from the connect-time classification — trainer protocol/CPM for
        // the trainer, 0x2A37 subscription success for the strap — never
        // from device names. Per-device disconnect lives here.
        val trainer = trainerState as? TrainerState.Connected
        if (trainer != null || sensorConnected) {
            Text("connected", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
        }
        if (trainer != null) {
            val badge = if (trainer.protocol.controllable) {
                "trainer  •  power  •  control"
            } else {
                "power telemetry"
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${remembered?.name ?: "trainer"}   [$badge]",
                    fontSize = 16.sp,
                )
                OutlinedButton(onClick = { scope.launch { erg.disconnect() } }) {
                    Text("Disconnect")
                }
            }
        }
        if (sensorConnected) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("${session.sensorName ?: "sensor"}   [heart rate]", fontSize = 16.sp)
                OutlinedButton(onClick = { scope.launch { session.disconnectSensor() } }) {
                    Text("Disconnect")
                }
            }
            // The heart-rate source dropdown (manual override of the source
            // mapping, 003 D4): lists connected HR-capable devices; the
            // assignment persists across restarts.
            var hrMenuOpen by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("heart rate source:  ", fontSize = 14.sp, color = Color(0xFF616161))
                Box {
                    TextButton(onClick = { hrMenuOpen = true }) {
                        Text(session.sensorName ?: "select")
                    }
                    DropdownMenu(
                        expanded = hrMenuOpen,
                        onDismissRequest = { hrMenuOpen = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(session.sensorName ?: "sensor") },
                            onClick = {
                                hrMenuOpen = false
                                session.rememberHeartRateSource(session.sensorPlatformId)
                            },
                        )
                    }
                }
            }
        }
        if (trainer != null || sensorConnected) {
            Spacer(Modifier.height(8.dp))
            Text("scan results", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
        }

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
                            val rememberedTrainer = settingsStore.load().trainer
                            if (candidate.platformId == rememberedTrainer?.platformId) {
                                // Trainer (re)connect: ERG engages automatically (D3).
                                erg.connect(candidate)
                                onConnected()
                            } else {
                                // Capability-based routing: a sensor connect only
                                // succeeds when the device really offers 0x2A37.
                                val ok = session.connectSensor(candidate)
                                if (ok) {
                                    session.rememberHeartRateSource(candidate.platformId)
                                } else if (candidate.platformId != session.rememberedHeartRate()) {
                                    // Not the remembered strap and no HR service:
                                    // treat as a new trainer candidate (junk fails
                                    // harmlessly in classification).
                                    erg.connect(candidate)
                                    onConnected()
                                }
                            }
                            remembered = settingsStore.load().trainer
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