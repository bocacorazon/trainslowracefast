package com.trainerapp.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.trainerapp.core.FakeTrainerClient
import com.trainerapp.core.domain.TrainerClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking

/**
 * Skeleton diagnostic screen: renders the shared TrainerState and telemetry.
 * The real BLE adapter replaces [FakeTrainerClient] in the next work item.
 */
class MainActivity : AppCompatActivity() {

    private val client: TrainerClient = FakeTrainerClient()
    private val scope = CoroutineScope(Dispatchers.Main)
    private var uiJob: Job? = null
    private lateinit var stateView: TextView
    private lateinit var logView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestBluetoothPermissionsIfNeeded()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }
        stateView = TextView(this).apply {
            textSize = 18f
            text = "state: Idle"
        }
        logView = TextView(this).apply { textSize = 12f }

        fun button(label: String, action: suspend () -> Unit) = Button(this).apply {
            text = label
            setOnClickListener { scope.launch { action() } }
        }

        root.addView(stateView)
        root.addView(button("Scan") { client.scan().collectLatest { log("found: ${it.name} rssi=${it.rssi}") } })
        root.addView(button("Connect") { runBlocking { client.connect(com.trainerapp.core.domain.TrainerCandidate("HAMMER-TRAINER", "fake", -55)) } })
        root.addView(button("100 W") { log("setTarget: ${client.setTargetPower(100)}") })
        root.addView(button("150 W") { log("setTarget: ${client.setTargetPower(150)}") })
        root.addView(button("200 W") { log("setTarget: ${client.setTargetPower(200)}") })
        root.addView(button("Disconnect") { runBlocking { client.disconnect() } })
        root.addView(ScrollView(this).apply { addView(logView) })

        setContentView(root)
    }

    override fun onStart() {
        super.onStart()
        uiJob = scope.launch {
            client.state.collectLatest { state ->
                stateView.text = "state: $state"
                log("state -> $state")
            }
        }
    }

    override fun onStop() {
        uiJob?.cancel()
        super.onStop()
    }

    private fun log(line: String) {
        logView.append(line + "\n")
    }

    private fun requestBluetoothPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val missing = listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
                .filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
            if (missing.isNotEmpty()) requestPermissions(missing.toTypedArray(), 1)
        }
    }
}
