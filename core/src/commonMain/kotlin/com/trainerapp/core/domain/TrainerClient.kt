package com.trainerapp.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Shared trainer connection state. The UI renders exactly these states;
 * platform adapters must keep this flow honest (never report "connected"
 * after an unexpected disconnect).
 */
sealed interface TrainerState {
    data object Idle : TrainerState
    data object Scanning : TrainerState
    data object Connecting : TrainerState
    data object DiscoveringServices : TrainerState
    data class Connected(val protocol: TrainerProtocol) : TrainerState
    data object Disconnecting : TrainerState
    data class Disconnected(val reason: DisconnectReason) : TrainerState
    data class Failed(val error: TrainerError) : TrainerState
}

enum class DisconnectReason {
    USER_REQUESTED,
    TRAINER_OFF,
    OUT_OF_RANGE,
    TIMEOUT,
}

sealed interface TrainerError {
    data object PermissionDenied : TrainerError
    data object ScanTimeout : TrainerError
    data object DiscoveryFailed : TrainerError
    data object ConnectionFailed : TrainerError
    data object ControlTimeout : TrainerError
    data object UnsupportedProtocol : TrainerError
    data class Unknown(val message: String) : TrainerError
}

/** A device seen during scanning. No address-like identifiers are exposed to the UI. */
data class TrainerCandidate(
    val name: String,
    val platformId: String,
    val rssi: Int,
    val advertisedServices: List<String> = emptyList(),
)

data class TrainerTelemetry(
    val watts: Int? = null,
    val speedKph: Double? = null,
    val heartRateBpm: Int? = null,
    val lastUpdatedAtMillis: Long = 0L,
    val stale: Boolean = false,
)

/**
 * What the trainer exposes, classified from discovered GATT services and
 * characteristics. Never inferred from the advertised device name alone.
 */
sealed interface TrainerProtocol {
    data object Ftms : TrainerProtocol
    data object CycleOpsProprietary : TrainerProtocol
    data object TelemetryOnly : TrainerProtocol
    data object Unsupported : TrainerProtocol

    /** True when this protocol gives the app a way to set target power. */
    val controllable: Boolean
        get() = this is TrainerProtocol.Ftms || this is TrainerProtocol.CycleOpsProprietary
}

/** Result of a control operation, surfaced verbatim to the UI. */
sealed interface ControlResult {
    data object Queued : ControlResult
    data object Written : ControlResult
    data object Acknowledged : ControlResult
    data object Applied : ControlResult
    data object TimedOut : ControlResult
    data class Failed(val reason: String) : ControlResult
}

/**
 * The only trainer-facing API the rest of the app may use.
 * Platform adapters (Android BLE, BlueZ on Linux) implement this.
 */
interface TrainerClient {
    val state: StateFlow<TrainerState>
    val telemetry: StateFlow<TrainerTelemetry>
    suspend fun scan(): Flow<TrainerCandidate>
    suspend fun connect(candidate: TrainerCandidate)
    suspend fun setTargetPower(watts: Int): ControlResult
    suspend fun disconnect()
}
