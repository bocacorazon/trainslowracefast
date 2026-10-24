package com.trainerapp.core

import com.trainerapp.core.domain.ControlResult
import com.trainerapp.core.protocol.GattInventory
import com.trainerapp.core.domain.TrainerCandidate
import com.trainerapp.core.domain.TrainerClient
import com.trainerapp.core.domain.TrainerProtocol
import com.trainerapp.core.domain.TrainerState
import com.trainerapp.core.domain.TrainerTelemetry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * In-memory client used by the launcher screens until the real platform
 * adapters (Android BLE / BlueZ) land, and by common tests.
 */
class FakeTrainerClient(
    private val protocol: TrainerProtocol = TrainerProtocol.Ftms,
) : TrainerClient {

    private val _state = MutableStateFlow<TrainerState>(TrainerState.Idle)
    override val state: StateFlow<TrainerState> = _state

    private val _telemetry = MutableStateFlow(TrainerTelemetry())
    override val telemetry: StateFlow<TrainerTelemetry> = _telemetry

    override suspend fun scan(): Flow<TrainerCandidate> = flowOf(
        TrainerCandidate(name = "HAMMER-TRAINER", platformId = "fake-platform-id", rssi = -55),
    )

    override suspend fun connect(candidate: TrainerCandidate) {
        _state.value = TrainerState.Connecting
        _state.value = TrainerState.DiscoveringServices
        _state.value = TrainerState.Connected(protocol)
    }

    override suspend fun setTargetPower(watts: Int): ControlResult {
        if (_state.value !is TrainerState.Connected) return ControlResult.Failed("not connected")
        if (watts !in 50..300) return ControlResult.Failed("out of range 50..300 W")
        if (!(_state.value as TrainerState.Connected).protocol.controllable) {
            return ControlResult.Failed("protocol has no control path")
        }
        return ControlResult.Acknowledged
    }

    override suspend fun disconnect() {
        _state.value = TrainerState.Disconnecting
        _state.value = TrainerState.Disconnected(com.trainerapp.core.domain.DisconnectReason.USER_REQUESTED)
    }
}

/** Inventory the fake client reports after "discovery". */
val FakeTrainerClient.discoveredInventory: GattInventory
    get() = GattInventory()
