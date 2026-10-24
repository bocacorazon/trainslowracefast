# Increment 001: Hammer Connection and ERG Control

## Outcome

Prove that a Kotlin Multiplatform application can connect to the user's
first-generation CycleOps Hammer (model 9810F), receive live power telemetry,
and safely change its ERG target on Android and Linux.

This increment is the hardware and BLE architecture gate for the project. It
must identify the protocol exposed by the trainer's installed firmware instead
of assuming that the trainer supports FTMS because it advertises Bluetooth
Smart.

## User story

As a rider, I can select my Hammer, connect to it, see its current power and
connection state, set a target power, and disconnect safely so that I know the
trainer can be controlled by the app before workout, recording, and upload
features are built.

## Supported hardware and platforms

- Trainer: CycleOps Hammer, model 9810F, first generation, January 2017
- Android: one physical phone with BLE support
- Linux: the intended Linux machine with a working BlueZ BLE adapter
- Language: Kotlin only
- Shared code: Kotlin Multiplatform `commonMain`
- Platform code: Kotlin Android and Kotlin/JVM or Kotlin/Native Linux adapters

The Hammer's ANT+ FE-C support is a known fallback, but ANT+ is outside this
increment. The purpose of this increment is to determine and prove its BLE
control path.

## Scope

### In scope

1. Create the minimal Kotlin Multiplatform project and platform launchers.
2. Request the required Android Bluetooth permissions.
3. Scan for nearby BLE devices and display name, address or platform
   identifier, RSSI, and advertised services.
4. Select and connect to a device whose advertised name begins with `HAMMER`.
5. Discover all GATT services and characteristics.
6. Classify the connection as:
   - `FTMS` when service `0x1826` and Control Point `0x2AD9` are available.
   - `CYCLEOPS_PROPRIETARY` when the expected CycleOps/Saris service and
     characteristics are available.
   - `TELEMETRY_ONLY` when power can be read but no supported control path is
     found.
   - `UNSUPPORTED` when neither telemetry nor a known control path is found.
7. Persist or export a sanitized GATT inventory for the physical 9810F.
8. Subscribe to power telemetry and display the latest watts plus the time of
   the last update.
9. For FTMS:
   - Request control through Control Point `0x2AD9`.
   - Verify the response indication and result code.
   - Send Set Target Power through the same control point.
10. If the trainer uses the proprietary CycleOps protocol, implement only the
    minimum commands needed to enter manual-power mode and set target watts.
11. Provide guarded controls for 100 W, 150 W, and 200 W.
12. Show command state: queued, written, acknowledged, applied, failed, or
    timed out.
13. Disconnect explicitly and handle an unexpected disconnect without leaving
    the UI in a connected state.
14. Capture structured diagnostic logs without device addresses or other
    unnecessary identifiers.

### Out of scope

- Workout intervals or automatic target changes
- Start, pause, resume, and stop workout semantics
- Heart-rate and cadence sensors
- Speed, distance, calories, averages, or charts
- Background workouts and foreground-service hardening
- Automatic reconnection
- Trainer calibration or roll-down
- Simulation, slope, or resistance-level modes
- FIT or TCX recording
- Strava authentication or upload
- ANT+ support
- UI styling beyond a functional diagnostic screen

## Safety constraints

- Do not send a control command until service and characteristic discovery is
  complete.
- Never infer protocol solely from the advertised device name.
- Reject targets outside `50..300 W` in this increment.
- Require an explicit tap for every target change; do not repeat commands from
  a timer.
- Serialize GATT operations. Allow only one outstanding write or descriptor
  operation.
- For FTMS, require a successful Request Control response before Set Target
  Power.
- Treat missing, malformed, negative, or non-success control responses as
  failures.
- On timeout or disconnect, stop issuing commands and surface the failure.
- The first control test must be performed while pedaling at low speed and
  must progress from 100 W to 150 W before attempting 200 W.

## Proposed design

### Shared domain API

Keep BLE-library types outside `commonMain`.

```kotlin
interface TrainerClient {
    val state: StateFlow<TrainerState>
    val telemetry: StateFlow<TrainerTelemetry>

    suspend fun scan(): Flow<TrainerCandidate>
    suspend fun connect(candidate: TrainerCandidate)
    suspend fun setTargetPower(watts: Int): ControlResult
    suspend fun disconnect()
}
```

The state model should distinguish at least:

```text
Idle
Scanning
Connecting
DiscoveringServices
Connected(protocol)
Disconnecting
Disconnected(reason)
Failed(error)
```

Use a `TrainerProtocol` sealed type rather than booleans:

```text
Ftms
CycleOpsProprietary
TelemetryOnly
Unsupported
```

### Layers

- `commonMain/domain`: state, telemetry, protocol, errors, and `TrainerClient`
- `commonMain/protocol/ftms`: UUIDs, opcodes, packet encoding, response parsing
- `commonMain/protocol/cycleops`: proprietary packet encoding and parsing if
  required by the captured GATT inventory
- `commonMain/usecase`: scan, connect, set target power, disconnect
- `androidMain/ble`: Android BLE-library adapter and permissions
- Linux platform source set: BlueZ-backed BLE adapter
- Platform apps: one diagnostic screen backed by the shared client

Packet codecs must be pure Kotlin and unit-testable without Bluetooth hardware.
The platform adapters are responsible only for scanning, connection lifecycle,
GATT discovery, subscriptions, and serialized reads/writes.

### Protocol discovery

The implementation must record:

- Advertised device name and service UUIDs
- Discovered service UUIDs
- Characteristic UUIDs and properties
- Descriptor UUIDs
- Whether notifications or indications can be enabled
- Which characteristic produces power data
- Which characteristic accepts control writes

Do not copy GoldenCheetah or QDomyos-Zwift source into the project during the
discovery step. They may be used to understand behavior. If GPL-covered code is
later copied or translated, record that decision explicitly and apply the
required license to the project.

## Delivery sequence

### 1. Project skeleton

- Create KMP modules and Android/Linux launchers.
- Add the selected BLE dependency behind a project-owned adapter.
- Add CI or local tasks for common tests and both platform compilations.
- Render static connection and telemetry states.

Exit condition: both applications compile and launch on physical targets.

### 2. Scan and GATT inventory

- Implement permissions, scanning, Hammer selection, connection, and service
  discovery.
- Render the discovered GATT tree.
- Export the sanitized inventory as JSON.
- Run this on the physical model 9810F on both platforms.

Exit condition: the trainer's actual BLE protocol can be classified from
captured evidence.

### 3. Telemetry

- Select the correct notification source based on discovered services.
- Enable its CCCD and parse power values.
- Display watts and stale-data status.
- Add packet fixtures captured from the trainer to codec tests.

Exit condition: power updates continuously for at least ten minutes on both
platforms without parsing errors or a dropped connection.

### 4. ERG control

- Implement the discovered control protocol as a pure Kotlin codec.
- Serialize control-point operations and validate acknowledgements.
- Add guarded 100 W, 150 W, and 200 W controls.
- Surface protocol failures and timeouts.

Exit condition: each target is acknowledged and physically applied in order on
the real trainer.

### 5. Disconnect and failure behavior

- Implement explicit disconnect.
- Handle trainer power-off and out-of-range disconnects.
- Ensure commands cannot be sent after failure or disconnection.
- Verify a fresh connection can be established after each failure.

Exit condition: all acceptance scenarios below pass.

## Acceptance criteria

1. On Android and Linux, scanning finds the powered-on Hammer without relying
   on a hard-coded device address.
2. The app displays the trainer's complete discovered GATT inventory and
   classifies its protocol from services and characteristics.
3. The captured inventory proves whether this firmware exposes FTMS service
   `0x1826`; the result is committed as a sanitized test fixture.
4. While pedaling, current power updates at the rate emitted by the trainer and
   becomes visibly stale when notifications stop.
5. Tapping 100 W, 150 W, and 200 W in sequence produces an acknowledged
   command and a perceptible corresponding resistance change.
6. The app never reports success from a write callback alone when the protocol
   defines a separate control response.
7. An unsupported or telemetry-only protocol disables the power controls and
   explains why.
8. Turning off the trainer during telemetry moves the app to a disconnected
   state and disables all controls.
9. Turning off the trainer during a control operation reports the operation as
   failed or timed out, never successful.
10. After an explicit or unexpected disconnect, the trainer can be scanned,
    connected, and controlled again without restarting the app.
11. Diagnostic logs contain protocol state and errors but do not persist the
    Bluetooth address.
12. Shared codec and state-machine tests pass, and both platform applications
    compile.

## Test plan

### Common unit tests

- FTMS UUID and opcode definitions
- Little-endian signed target-power encoding
- FTMS response-code parsing for success and every defined failure
- Indoor Bike Data parsing for packets with different flag combinations
- Proprietary CycleOps packet fixtures, if that path is required
- Rejection of malformed and truncated packets
- Target range validation at 49, 50, 300, and 301 W
- State transitions for connect, discovery, control, timeout, and disconnect
- Prevention of concurrent GATT operations

### Platform integration tests

- Permission denied, then granted
- Scan timeout with trainer off
- Trainer found and connected
- Service discovery failure
- Notification subscription failure
- Control write rejected
- Control response timeout
- Explicit disconnect
- Trainer powered off during telemetry
- Trainer powered off during a control request

Use fakes for repeatable automation. Hardware acceptance remains mandatory
because Android BLE, BlueZ, firmware behavior, and resistance application
cannot be validated by mocks.

### Hardware matrix

| Platform | Adapter/device | Scan | Inventory | Power 10 min | 100/150/200 W | Reconnect |
|---|---|---:|---:|---:|---:|---:|
| Android | User's phone |  |  |  |  |  |
| Linux | Intended machine/adapter |  |  |  |  |  |

Record OS version, Bluetooth chipset or adapter, trainer firmware, and observed
protocol with the results.

## Risks and stop conditions

- **No FTMS:** continue only after documenting the proprietary GATT service and
  locating enough protocol evidence to implement target power safely.
- **Telemetry but no BLE control:** stop BLE control work and reassess Android
  feasibility. ANT+ FE-C is proven by the product label but would require
  different hardware and platform scope.
- **BLE library lacks required Linux behavior:** replace only the Linux adapter;
  do not leak library APIs into shared protocol code.
- **Unstable connection:** do not build workout automation on top of it. Capture
  logs and resolve operation ordering, CCCD setup, or firmware issues first.
- **GPL dependency required:** decide whether the application will be GPL before
  incorporating the implementation.

The project is a **go** after this increment only if at least one target
platform can maintain telemetry and apply acknowledged ERG targets reliably.
Cross-platform work continues only if both Android and Linux pass, or if the
platform scope is explicitly reduced.

## Definition of done

- Acceptance criteria are demonstrated on the physical model 9810F.
- Sanitized GATT inventory and packet fixtures are committed.
- Shared protocol and state-machine tests pass.
- Android and Linux builds pass.
- Hardware-matrix results identify trainer firmware and actual BLE protocol.
- Known limitations and the decision to continue, change protocol, or reduce
  platform scope are recorded in this document.

