# Increment 003 — Multi-Sensor Telemetry: Design

Grill session: 2026-10-25 (12 decisions, logged in
`docs/increments/003-multi-sensor-telemetry.md`). Builds on increment 002's
live-verified desktop ERG MVP (PR #5).

## What changes

The app grows from one device (trainer) to a **simultaneous multi-device
session**: trainer + HR strap (+ cadence if the H1 probe succeeds). Three
things become real that 002 only sketched (its requirement 10):

1. **Device registry with capability discovery** — every visible/connected
   device is classified by the data types it can provide, derived from its
   actual GATT inventory (never from names or UUID mnemonics — the 0x2A65
   "speed" lesson is codified here).
2. **Metric-to-source mapping** — every displayed data type has exactly one
   assigned source; auto-assigned by capability, manually overridable,
   remembered.
3. **Per-device connection lifecycle** — independent connect/reconnect/
   disconnect per device, one shared BlueZ/sdbus transport stack.

Plus the slice's headline: **live heart rate on the ride screen**, making the
app training-usable per the owner's bar.

## Hardware gates (same discipline as 002)

- **WI-A hardware capture** (needs the COOSPO H808S strap on, near the NUC):
  capture its GATT inventory and live 0x2A37 Heart Rate Measurement frames;
  confirm standard 0x180D service. Also probe the H1's 0x2A65 Cycling Power
  Vector while pedaling for cumulative crank revolutions (decides cadence
  in-scope vs deferred). Commit both as fixtures BEFORE adapter work.
- **Final live acceptance** (needs trainer + worn strap): the 11-point ride
  checklist in the increment doc.

## Core design

### Domain (commonMain, platform-neutral — all of it survives any future
frontend/transport switch, including WASM)

- `device/DeviceRegistry`: connected-device set; per-device
  `DeviceCapabilities` (data types: `POWER`, `CONTROL`, `HEART_RATE`,
  `CADENCE`, `SPEED`) produced by the existing `ProtocolClassifier` extended
  with HR/CSC/CPV recognition.
- `device/SourceMapper`: data-type → device assignment; auto-assign on
  connect (first capable device wins a free type), manual override, persisted
  per type; connecting a second device with the same capability NEVER
  silently reassigns.
- `sensor/HeartRateMeasurement`: parser for 0x2A37 (flags byte + uint8 bpm
  standard frame; only the standard format is supported — capture decides if
  anything else is needed).
- `sensor/CyclingPowerVector`: parser for 0x2A65 CPV frames; derives cadence
  from cumulative crank revolutions + last crank event time, IF the capture
  shows the H1 sends them. Gated: ships only on evidence.
- `Telemetry` grows `heartRateBpm: Int?` and `cadenceRpm: Int?` with the
  same single stale clock (3 s) covering all readings.
- `SessionController` (new, thin): owns "connect all remembered sources at
  launch" ordering (trainer first — ERG engages, then sensors), and
  ride-screen Disconnect = trainer min-load release then drop-all.

### Transport (jvmMain — the only layer that does NOT survive a WASM port)

- `BlueFalconBleClient` generalized: multiple concurrent device
  connections over the one sdbus backend; per-device `TrainerClient`-style
  session objects. The trainer session is untouched (it is live-verified —
  do not destabilize it); sensor sessions are additive.
- Sensor session: connect, discover, subscribe 0x2A37 notify, parse, feed
  telemetry; independent reconnect loop per device using 002's backoff
  schedule; disconnect cancels its own loop only.

### UI (app-desktop, Compose — shared with any future Compose target)

- **Ride screen**: HR added prominently under the target line ("142 bpm"),
  gray→red when stale, "—" after 3 s; speed line REMOVED (no real source —
  no fabricated metrics on screen). Status line extended to name which
  source is reconnecting ("HR reconnecting (attempt 2)").
- **Devices screen**: capability badges per row (`trainer/power`, `HR`,
  `cadence`), any device connectable simultaneously, per-device disconnect.
  New **data-sources panel**: one dropdown per data type showing connected
  capable devices, reflecting the SourceMapper.
- **Launch**: remembered sources auto-connect; landing view stays the ride
  screen.

### Persistence

`~/.config/trainerapp/settings.json` grows
`sources: { heartRate: <identity>, cadence: <identity?> }` alongside the
existing trainer identity/target/paused fields.

## Explicitly out of scope

Recording/FIT, HR zones and alerts, dedicated cadence *sensor* support
unless the H1 probe fails in which case cadence defers entirely, ANT+ input,
Android, WASM/Web Bluetooth (deferred pending a bounded spike; Linux Web
Bluetooth is flag-gated today).

## Key unknowns → captured evidence decides

| Unknown | Decided by |
|---|---|
| H808S GATT shape (standard 0x180D?) | WI-A capture fixture |
| H1 0x2A65 carries crank revs? | WI-A capture → cadence in/out |
| BlueZ concurrent trainer+strap stability | WI-A + acceptance ride |
