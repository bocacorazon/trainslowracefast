# Design — Increment 002: Linux Desktop BLE ERG MVP

Run: `run-dda354b4819e8f9066e1d86e` · Feature: `002-desktop-ble-erg-mvp`
Requirements source: grill session 2026-10-24 (see `docs/increments/002-desktop-ble-erg-mvp.md`).

## 1. Problem

Replace the remaining gap between the increment-001 skeleton and a real ride: a Linux
desktop app that connects to the CycleOps Hammer H1 over BLE, shows live power/speed,
and controls ERG target power with the reconnect/pause/safety behaviors locked in the
grill session. MVP scope — not training-usable until heart rate lands in the next slice.

## 2. Decisions from the grill session (authoritative)

| # | Decision |
|---|----------|
| D1 | Linux desktop only; Android deferred. Compose Multiplatform desktop window. |
| D2 | BLE first (Hammer's built-in Bluetooth); ANT+ deferred but designed for. |
| D3 | ERG engages automatically on connect at remembered wattage; default 120 W; no toggle. |
| D4 | ±3 W buttons + numeric field (commit on Enter/focus loss) + ↑/↓ keys (when field unfocused). |
| D5 | Target range 50–600 W, clamped with visible notice; rapid clicks coalesce ~300 ms. |
| D6 | Auto-reconnect with increasing delays until success or user Disconnect; target reapplied unless paused. |
| D7 | Pause = lowest supported load + telemetry kept; Resume reapplies target; pause survives reconnect and restart; edits while paused update saved target only. |
| D8 | Telemetry stale after 3 s → "—" + recovery attempt; never frozen numbers. |
| D9 | Disconnect/exit requests minimum load first; UI never claims release it can't confirm. |
| D10 | Devices screen always available; first launch explicit selection, then auto-connect. |
| D11 | Core is multi-device: registry, capability discovery, metric→source mapping (all map to Hammer this slice). |
| D12 | Acceptance = 5-min real-Hammer smoke test; no fake-client pass; missing hardware evidence = not passed. |
| D13 | BLE-only for the MVP including the HR follow-on slice. |

## 3. Alternatives considered

### UI toolkit (desktop)
- **Compose Multiplatform (chosen)** — same language and state-flow idioms as the shared
  core; the Android frontend (when revisited) shares composables; large-numeric ride
  screen is trivial layout.
- JavaFX — mature on desktop, but drags a second UI toolkit into the KMP build and buys
  nothing for the future Android target.
- Swing — zero new deps, but ancient ergonomics and no path to Android reuse.

### BLE stack (Linux JVM)
- **Blue Falcon, sdbus/BlueZ engine (chosen)** — Kotlin-native API over D-Bus; same
  library later serves the Android engine; keeps BLE code in the KMP core module.
- Raw D-Bus (kotlin-dbus / gdbus hand-rolled) — full control, but re-implements GATT
  caching, property change signals, and reconnect semantics that Blue Falcon already has.
- noble/node backend — battle-tested, but forces a JS sidecar process and a wire
  protocol between the app and BLE.

### Control protocol
- **Classify from captured GATT inventory (chosen)** — FTMS 0x1826/0x2AD9 if present,
  else the CycleOps proprietary path. The increment-001 `ProtocolClassifier` already
  encodes the rule; hardware capture finalizes the CycleOps UUID constants.
- Assume FTMS only — rejected: the H1 is a 2016 first-gen device; its BLE layout is not
  published. Assuming is exactly what the classifier was built to avoid.

## 4. Architecture

```
core (KMP, from increment 001)
  domain/        TrainerClient contract, TrainerState, Telemetry, ControlResult
  protocol/      GattInventory, ProtocolClassifier (finalized by capture), Exporter
  + device/      NEW  DeviceRegistry, DeviceCapabilities, MetricSourceMap
  + erg/         NEW  ErgController (target state machine: ride / paused / reconnecting)
  + store/       NEW  SettingsStore (remembered trainer, wattage, paused flag)
  + ble/         NEW  BlueFalconTrainerClient (implements TrainerClient over Blue Falcon)

app-desktop (NEW, Compose Multiplatform desktop)
  ride screen    large actual power, target, speed, status; −3/+3, field, ↑/↓,
                 Pause/Resume, Disconnect
  devices screen discover → select → remember

app (Android, from 001)   untouched, still FakeTrainerClient
app-linux (console)       kept as diagnostic; superseded by app-desktop for real use
```

### New core components

- **`DeviceRegistry`** — tracks every connected device; a device is (identity,
  transport, capabilities), identity ≠ transport so an ANT+/BLE dual sensor is one
  device with two transports later.
- **Capability discovery** — after connect, discovered GATT services map to a
  capability set: {power, speed, cadence, hr, control}. Reuses the increment-001
  GattInventory; `ProtocolClassifier` decides control.
- **`MetricSourceMap`** — metric → device assignment. This slice: auto-assign
  everything to the Hammer. Next slice adds the selection UI and persistence of
  choices.
- **`ErgController`** — owns target wattage + paused flag + confirmation state.
  Guarantees: single in-flight target write with coalescing (≥300 ms debounce),
  clamp 50–600 W with notice, reapply-on-reconnect honoring pause, min-load-then-
  disconnect sequence. Exposes requested vs confirmed to the UI.
- **`SettingsStore`** — small persisted settings (JSON in `~/.config/trainerapp/`):
  remembered trainer identity, last wattage (default 120), paused flag.

### Reconnect state machine (ErgController)

```
Riding ──drop──▶ Reconnecting ──ok──▶ Riding (reapply target)      [if was Riding]
   │                 │
   │                 └── user Disconnect ──▶ Disconnected (no retry)
   ▲
Paused ──drop──▶ Reconnecting ──ok──▶ Paused (target NOT reapplied)
```

Backoff: increasing delays (e.g. 1 s, 2 s, 5 s, 10 s, then 30 s steady-state), surfaced
in the status line. Disconnect cancels retrying.

## 5. Work plan (work items, gated)

1. **WI-A Hardware capture (gate).** Run the 001 probe script against the live Hammer
   on the NUC: full GATT inventory + min-load behavior. Output: sanitized inventory
   committed as fixture; finalize `ProtocolClassifier` constants. *Blocks WI-B/C.*
2. **WI-B BlueZ BLE adapter.** `ble/BlueFalconTrainerClient` implementing
   `TrainerClient` over Blue Falcon (sdbus engine): scan, connect, service discovery,
   telemetry notify, control write. Proven by a console test on the NUC against the
   real Hammer. *Depends on WI-A for protocol constants.*
3. **WI-C ErgController + settings.** Target state machine, clamp/debounce/coalesce,
   reconnect policy, pause persistence, min-load disconnect. Unit-tested against
   fakes in common tests. *Protocol-independent parts can start early.*
4. **WI-D Desktop UI (Compose).** Ride screen + devices screen wired to
   ErgController/DeviceRegistry/MetricSourceMap via the shared state flows.
   *Depends on WI-B, WI-C.*
5. **WI-E 5-minute smoke test.** The acceptance ride on the real Hammer: connect →
   telemetry → ±3 W felt → direct entry → pause/resume → BT interruption recovery →
   disconnect releases load. Evidence recorded (screens/log excepts); no fake pass.

## 6. Verification sketch

- **Mechanical (CI-able):** common unit tests for ErgController (clamp, debounce,
  reapply-on-reconnect, pause-survives-restart, min-load ordering), classifier tests
  updated with captured fixtures, adapter contract tests against a fake Blue Falcon
  wrapper.
- **Rubric (hardware):** WI-E smoke test judged against the acceptance list —
  hardware evidence required; verdict "pass" impossible without it.
- Gherkin publication for the reconnection/pause flows (headless-simulable subset).

## 7. Risks

| Risk | Mitigation |
|------|------------|
| Hammer exposes no FTMS, proprietary path undocumented | WI-A gate; if neither control path exists, stop and re-evaluate with the user before burning the slice |
| Min-load request unsupported | WI-A verifies; fallback = explicit "release failed" UI state (D9) |
| Blue Falcon sdbus instability on NUC | isolated adapter module; console harness (WI-B) isolates UI from BLE bugs |
| Compose desktop rendering on this Linux box | early smoke window in WI-D start |

## 8. Out of scope

Heart rate, cadence sensors, source-selection UI, ANT+, recording/FIT/upload,
workouts. See increment doc.
