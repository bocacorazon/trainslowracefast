package com.trainerapp.desktop

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trainerapp.core.domain.TrainerClient
import com.trainerapp.core.erg.ErgController
import com.trainerapp.core.erg.ErgLink
import kotlinx.coroutines.launch

/**
 * The ride screen (grill decisions D3–D9). Consumes only StateFlows from the
 * core: ErgController.state and the TrainerClient telemetry. No GATT or BLE
 * types cross into this file.
 */
@Composable
fun RideScreen(erg: ErgController, trainerClient: TrainerClient) {
    val scope = rememberCoroutineScope()
    val ergState by erg.state.collectAsState()
    val telemetry by trainerClient.telemetry.collectAsState()

    // Grill decision D8: stale telemetry renders "—", never frozen numbers.
    fun render(v: Any?): String = if (telemetry.stale) "—" else v?.toString() ?: "—"

    var fieldText by remember(ergState.targetWatts) { mutableStateOf(ergState.targetWatts.toString()) }
    var fieldFocused by remember { mutableStateOf(false) }
    val fieldRequester = remember { FocusRequester() }

    fun commitField() {
        val watts = fieldText.trim().toIntOrNull() ?: return
        scope.launch { erg.setTarget(watts) }
    }

    // Grill decision D4: ↑/↓ adjust by 3 W when the numeric field is NOT focused.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .focusRequester(remember { FocusRequester() })
            .focusable()
            .onKeyEvent { event ->
                if (fieldFocused) return@onKeyEvent false
                val arrowUp = event.key == Key.DirectionUp
                val arrowDown = event.key == Key.DirectionDown
                if (event.type == KeyEventType.KeyDown && (arrowUp || arrowDown)) {
                    scope.launch { erg.adjust(if (arrowUp) 3 else -3) }
                    true
                } else {
                    false
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Large actual power — visually dominant (grill decision on telemetry display).
        Text(
            text = "${render(telemetry.watts)} W",
            fontSize = 96.sp,
            fontWeight = FontWeight.Bold,
            color = if (telemetry.stale) Color.Gray else Color(0xFF1B5E20),
        )
        Spacer(Modifier.height(8.dp))

        // Requested target, distinct from actual; shows trainer confirmation
        // state until the two converge (requested-vs-confirmed, D3).
        val confirmedSuffix = when {
            ergState.confirmedWatts == null && ergState.link != ErgLink.DISCONNECTED -> " (unconfirmed)"
            else -> ""
        }
        Text(
            text = "target ${ergState.targetWatts} W$confirmedSuffix",
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0D47A1),
        )
        Spacer(Modifier.height(8.dp))

        Text(
            text = "${render(telemetry.speedKph?.let { "%.1f".format(it) })} km/h",
            fontSize = 24.sp,
        )
        Spacer(Modifier.height(16.dp))

        // Status line: link, reconnect attempts, pause, clamp notice, release honesty.
        val status = buildString {
            append(
                when (ergState.link) {
                    ErgLink.RIDING -> "riding"
                    ErgLink.PAUSED -> "paused"
                    ErgLink.CONNECTING -> "connecting"
                    ErgLink.RECONNECTING -> "reconnecting (attempt ${ergState.reconnectAttempt})"
                    ErgLink.DISCONNECTED -> "disconnected"
                }
            )
            ergState.clampedNotice?.let {
                append("  •  clamped ${it.requested} → ${it.applied} W")
            }
        }
        Text(text = status, fontSize = 14.sp, color = Color(0xFF616161))

        // Honest release state after a deliberate disconnect (grill decision D9).
        ergState.releaseConfirmed?.let { confirmed ->
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (confirmed) {
                    "trainer released to minimum load"
                } else {
                    "RELEASE NOT CONFIRMED — the trainer may still hold resistance"
                },
                fontSize = 14.sp,
                color = if (confirmed) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                fontWeight = FontWeight.Bold,
            )
        }
        ergState.clampedNotice?.let {
            Spacer(Modifier.height(4.dp))
            OutlinedButton(onClick = { erg.clearClampNotice() }) { Text("dismiss clamp notice") }
        }

        Spacer(Modifier.height(24.dp))

        // Adjustment controls (grill decision D4).
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { scope.launch { erg.adjust(-3) } }) { Text("−3 W", fontSize = 20.sp) }
            Button(onClick = { scope.launch { erg.adjust(3) } }) { Text("+3 W", fontSize = 20.sp) }
        }
        Spacer(Modifier.height(12.dp))

        // Numeric field: commits on Enter (ImeAction.Done) or focus loss —
        // never per keystroke (grill decision D4).
        OutlinedTextField(
            value = fieldText,
            onValueChange = { fieldText = it },
            modifier = Modifier
                .width(160.dp)
                .focusRequester(fieldRequester)
                .onFocusChanged { state ->
                    if (fieldFocused && !state.isFocused) commitField()
                    fieldFocused = state.isFocused
                },
            label = { Text("target W") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { commitField() },
            ),
            textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 18.sp),
        )
        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                scope.launch { if (ergState.paused) erg.resume() else erg.pause() }
            }) {
                Text(if (ergState.paused) "Resume" else "Pause", fontSize = 18.sp)
            }
            OutlinedButton(onClick = { scope.launch { erg.disconnect() } }) {
                Text("Disconnect", fontSize = 18.sp)
            }
        }
    }
}