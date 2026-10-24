# Increment 002 — Linux Desktop BLE ERG MVP

Status: design phase (grill session completed 2026-10-24)

## Summary

A Linux desktop application (Compose Multiplatform) that connects to the CycleOps Hammer
H1 over BLE, shows live telemetry, and controls ERG target power. This is an MVP control
slice: usable for a smoke ride, **not yet usable for training** — heart rate and
multi-device source selection are the next slice.

Android is deferred. BLE only; ANT+ deferred. No recording, no uploads, no workouts.

## Requirements (locked in the grill session, 2026-10-24)

1. **Desktop-first.** Compose Multiplatform desktop window on Linux. The Android
   launcher exists from increment 001 but is not a target of this slice.
2. **Real BLE connection.** Discover trainers over BlueZ, connect to the Hammer. The
   control protocol is determined by the trainer's *actual* discovered GATT inventory
   (FTMS 0x1826/0x2AD9 vs CycleOps proprietary), never assumed. Hardware capture is a
   gate for the protocol work.
3. **ERG always on.** On connection, ERG engages automatically at the remembered
   wattage; first-use default **120 W**. There is no "Enable ERG" toggle. The UI
   distinguishes requested target from trainer-confirmed control.
4. **Target adjustment.** −3 W / +3 W buttons; numeric field committing on Enter or
   focus loss (never per keystroke); ↑/↓ keys adjust by 3 W when the field is not
   focused. Range **50–600 W**, clamped to the nearest limit with a visible notice.
   Rapid changes coalesce (~300 ms) so the trainer is not flooded.
5. **Automatic reconnect.** On unexpected drop: retry with increasing delays until
   reconnected or the user presses Disconnect. On reconnect, reapply the target —
   unless paused (pause survives reconnect).
6. **Pause/Resume.** Pause requests the trainer's lowest supported load while keeping
   telemetry connected; Resume reapplies the remembered target. Pause persists across
   reconnects *and* app restarts. Wattage adjustments while paused update the saved
   target but do not apply resistance; only Resume applies it.
7. **Telemetry display.** Large actual power, target wattage, speed in km/h,
   connection/control status. No charts. If no telemetry arrives for **3 s**, readings
   show "—" (stale) and the app attempts recovery; stale display never freezes numbers.
8. **Safe disconnect.** Disconnect and app exit first request the trainer's minimum
   supported load, then release the connection. If that request fails or the link is
   already dead, the UI must not claim resistance was released. Deliberate disconnect
   cancels auto-reconnect.
9. **Devices screen, always available.** Discover trainers, select one, replace the
   remembered device. First launch requires explicit selection; subsequent launches
   auto-connect to the remembered trainer and engage ERG.
10. **Multi-device-ready core.** Device registry, per-device capability discovery
    (power, speed, cadence, HR, control), and a metric-to-source mapping so every
    displayed value has an assigned source. In this slice all metrics map to the
    Hammer; the source-selection UI is the next slice. Device identity is separate
    from transport (BLE now, ANT+ later) so dual-protocol sensors don't force a
    redesign.
11. **Persistence.** Remembered target wattage, remembered trainer, paused state —
    survive app restarts.
12. **Acceptance: 5-minute real-Hammer smoke test.** Connect, live telemetry, ±3 W
    changes felt on the trainer, direct entry, pause/resume, a deliberate Bluetooth
    interruption that recovers automatically, then disconnect with load release.
    Missing hardware evidence = not passed. No pass on the fake client.

## Out of scope (this slice)

- Heart rate display (BLE HR strap) — required before the app is called
  training-usable; next slice
- Cadence sensors and per-metric source-selection UI — next slice
- ANT+ (device support is designed for, not implemented)
- Recording, FIT files, uploads, workout definitions, profiles

## Key unknowns (gated by hardware capture)

- Whether the Hammer exposes FTMS or only the CycleOps proprietary control service
  (classifier constants from increment 001 are placeholders until captured)
- The trainer's minimum supported load and whether a "release load" command exists
  (Pause and safe-disconnect depend on it)
- BlueZ + Blue Falcon sdbus behavior on the NUC (environment spike)

## Relationship to increment 001

Increment 001 delivered the KMP skeleton (merged via PR #1, PR #2): TrainerClient
contract, GATT inventory + ProtocolClassifier, FakeTrainerClient, Android + Linux
launchers. Its remaining planned work items (ble-connection, protocol-codec,
hardware-capture, telemetry-erg, hardware-acceptance) are superseded by this
increment's re-scoped requirements. The hardware-evidence probe script authored in
001 (`docs/features/001-hammer-connection-and-erg-control/design/probes/collect-hardware-evidence.sh`)
carries over as the capture tool for this slice.
