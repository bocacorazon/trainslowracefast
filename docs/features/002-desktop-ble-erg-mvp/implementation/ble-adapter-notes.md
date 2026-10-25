# BLE adapter notes — wi-ble-adapter (increment 002)

## What was built

`core/src/jvmMain/kotlin/com/trainerapp/core/ble/` implements the increment-01
`TrainerClient` contract over the Blue Falcon sdbus/BlueZ engine:

| File | Role |
|---|---|
| `BleBackend.kt` | Transport seam (`BleBackend`, scan/service/characteristic info types). The only API the client and tests see. `platformIdFor()` maps the BlueZ address to a stable opaque `platformId` — no address ever reaches the UI. |
| `BlueFalconBleBackend.kt` | **The only file in the project importing Blue Falcon types** (SdbusEngine, BlueFalcon, SdbusCharacteristic). Implements the seam: scan, connect, discovery, notify/indicate, value flows, read/write, connection-state polling. |
| `BlueFalconTrainerClient.kt` | `TrainerClient` over the seam: state machine (Idle→Scanning→Connecting→DiscoveringServices→Connected→Disconnected), telemetry pipeline (0x2A63 notify, 0x2A65 notify-or-poll), control writes with indication-based ack, `requestMinLoad()`, disconnect watcher (never reports "connected" after a drop), stale ticker (design D6/D7). |
| `ControlCodec.kt` | `ControlCodec` interface + `BestEffortSarisCodec` (frames below). |
| `CyclingPowerMeasurement.kt` | Parsers for 0x2A63 (power/cadence/L-R balance per SIG spec) and 0x2A65 (speed, 1/100 m/s → kph). |

Placement note: the task body names `core/src/commonMain/.../ble/`, but the
Blue Falcon dependency is JVM-only, so the adapter lives in `jvmMain` under the
same `core/ble` package. The domain contract and classifier stay in commonMain;
common code cannot see any BLE type.

## Environment (verified on this machine, 2026-10-24)

- BlueZ: hci0 (USB, UP RUNNING), daemon on the **system bus** (`org.bluez`
  resolves to an owner). No extra D-Bus permissions needed to run the stack;
  the user ran the hardware capture via `bluetoothctl` in the same account
  (groups: marcos, adm, cdrom, sudo, dip, video, plugdev, users, lpadmin,
  lxd, docker, render — no explicit `bluetooth` group, and none was needed).
- Dependencies (already in the Gradle module cache; resolved from
  mavenCentral via `dependencyResolutionManagement`):
  `com.monkopedia:blue-falcon-sdbus:1.1.0-3.0.3` (pulls
  `dev.bluefalcon:blue-falcon-core:3.0.3`).
- Blue Falcon 3.x API notes discovered by inspecting the shipped jars
  (`javap`): `BlueFalcon { engine = SdbusEngine { logger = ... } }`;
  `retrievePeripheral()` returns **nullable**; `kotlin.uuid.Uuid` needs
  `@OptIn(ExperimentalUuidApi::class)`; `SdbusCharacteristic.valueFlow`
  (`StateFlow<ByteArray>`) is the value channel for notify/indicate/read.
- `local.properties` (`sdk.dir=/home/marcos/Android`) present in this worktree.

### Blue Falcon 3.x limitations the adapter works around

1. **No property flags in the core API.** `BluetoothCharacteristic` exposes no
   write/notify/indicate flags (BlueZ has them, but 3.0.3 does not surface
   them). `BlueFalconBleBackend.discoverServices` therefore reports empty
   `properties` sets; `BlueFalconTrainerClient` marks properties *verified at
   runtime* — a characteristic is credited with `notify`/`indicate`/`write`
   only when the corresponding operation succeeds — and classifies with those
   verified properties. A trainer whose subscribe genuinely fails classifies
   as Unsupported, which is the honest answer.
2. **No advertisement data** in the core `BluetoothPeripheral` type →
   `TrainerCandidate.advertisedServices` is empty on Linux (device names come
   from BlueZ, which is enough for the devices screen).
3. **Disconnect detection** is a 1 s poll of `falcon.connectionState(...)` in
   the backend (the engine exposes no disconnect flow).

## Control frames — what is verified vs. best-effort

**Verified (spec-documented):**
- FTMS: target power = opcode `0x02` (Set Power) + uint16 LE watts on the
  FTMS Control Point 0x2AD9; min load = `0x02` + 0 watts.
- 0x2A63 / 0x2A65 packet layouts (BLE SIG Cycling Power / Speed
  characteristics) — parser unit-tested.

**Best-effort — UNVERIFIED against hardware (the Hammer H1):**
- The captured first-gen Hammer exposes **no FTMS** (see
  `hardware-capture-notes.md`); control goes to the two Saris vendor
  write+indicate characteristics. Their opcode semantics are not publicly
  documented — qdomyos-zwift has no CycleOps BLE support (ANT+ only), and the
  GATT capture cannot reveal opcodes.
- `BestEffortSarisCodec` guesses:
  - target power: `0x01` + uint16 LE watts (written to
    `ca31a533-a858-4dc7-a650-fdeb6dad4c14` in the vendor service, falling back
    to `a026e005-0a7d-4ab3-97fa-f1500f9feb8b` in the Cycling Power service if
    indicate setup fails there),
  - min load: `0x02`.
- **The smoke-ride item must verify/correct these frames on live hardware.**
  Correction path: implement `ControlCodec` with the real opcodes (or edit
  `BestEffortSarisCodec`) — no adapter or UI changes needed. If the real
  layout differs, the indicate-based ack wait in
  `BlueFalconTrainerClient.controlWrite` may also need the ack pattern
  adjusted (currently: any indication on the control char within
  `ackTimeoutMillis` counts as `Acknowledged`).

## Test status

`./gradlew jvmTest` (root): **41 tests, 0 failures** — includes 17 new
contract tests for `BlueFalconTrainerClient` against `FakeBleBackend`
(scan/platformId, state machine, classification from the committed Hammer
fixture, telemetry power/speed incl. poll fallback and stale flag, control
writes incl. fallback chars and FTMS path, min-load, user + unexpected
disconnect), parser and codec unit tests, and the pre-existing protocol
classifier/exporter suites. `:app:compileDebugKotlin` (Android target of the
changed commonMain) also passes.
