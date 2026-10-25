# Increment 003 — Multi-Sensor Telemetry (HR + Source Mapping)

Status: design phase (grill session completed 2026-10-25)

## Summary

Extends the live-verified desktop ERG MVP (increment 002, merged via PR #5) from a
single-trainer app to a multi-device trainer session: simultaneous BLE connections to
the trainer **and** sensors, capability discovery per device, a per-data-type
source mapping with auto-assign and manual override, and heart rate on the ride screen.
This is the slice that makes the app **training-usable** per the owner's bar
("HR is a must for training").

Scope stays Linux desktop + BLE. Recording (FIT), HR zones/alerts, ANT+ input, and
Android remain out of scope.

## Requirements (locked in the grill session, 2026-10-25)

1. **Second live device: HR strap.** Connect a COOSPO H808S chest strap (BLE + ANT+
   dual protocol) simultaneously with the trainer. Standard Heart Rate service
   (0x180D, measurement char 0x2A37 notify) is expected; verified in the hardware
   capture item before adapter work — the H808S's actual GATT inventory is a gate,
   same as the H1 was in 002.
2. **HR display only.** Live heart rate in bpm on the ride screen, prominently under
   the target line. Same staleness rules as watts: no data for 3 s → "—", rendered
   red when stale. No zones, no alerts, no max-HR config — later slices.
3. **Cadence: probe, don't promise.** The H1 broadcasts cadence over ANT+ FE-C but
   its BLE Cycling Power Measurement frames carry no crank data (verified from live
   captures: flags 0x1834 has no crank-revolution bits). The hardware capture item
   probes the H1's Cycling Power Vector (0x2A65) for cumulative crank revolutions:
   - If crank revs are present → trainer-derived cadence ships in this slice
     (displayed when pedaling, same stale rules).
   - If not → cadence is deferred to a later slice (dedicated sensor or pedals);
     the core remains cadence-capable.
4. **Capability discovery per device.** The devices screen discovers the data types
   each visible/connected device can provide (trainer/power, control, HR, cadence)
   from its advertised services and GATT inventory — never from the device name or
   UUID mnemonics alone (the 0x2A65-isn't-speed lesson from 002 is codified here).
   Each device row shows capability badges.
5. **Data-source mapping.** Every displayed data type has an assigned source.
   Auto-assign by capability on connect; manual override via a dropdown per data
   type; selections are remembered per data type (like the remembered trainer).
   Exactly one source per data type; connecting a second device offering the same
   type does not silently reassign — the user resolves it.
6. **Simultaneous connections.** Trainer + HR strap (and later cadence) connected
   at the same time over the same BlueZ/sdbus stack. Any device on the devices screen
   is connectable; multiple connections live together.
7. **Auto-connect remembered sources at launch.** With remembered sources, the app
   connects to all of them at startup (trainer first, then sensors) and the ride
   screen is the landing view, exactly as 002 behaved for the trainer alone.
   First-use still requires explicit selection on the devices screen.
8. **Independent per-device reconnect.** Each device has its own reconnect policy
   with backoff (002's schedule). A dropped HR strap never disturbs trainer control
   or other telemetry; the status line names which source is reconnecting
   (e.g. "HR reconnecting (attempt 2)"). No whole-session reconnect banners.
9. **Ride-screen Disconnect drops all sources** (after the trainer's min-load
   release sequence). Per-device disconnect lives on the devices screen.
10. **Speed hidden.** The dead "— km/h" placeholder is removed; speed returns only
    when a real source exists (dedicated speed sensor, or derived data) in a later
    slice. No virtual-speed fabrication.
11. **Acceptance: live ride with both devices.** 5-minute ride with trainer + H808S
    strap worn: auto-connect of both remembered sources, live watts + live HR,
    ±3 W control still working, HR strap power-cycle recovers independently while
    the trainer keeps ERG, source-swap dropdown works. Missing hardware evidence =
    not passed; no pass on fakes. (002's outstanding pause/release live checks stay
    a separate ride, per the owner's call.)

## Out of scope (this slice)

- Recording / FIT export (power + HR tracks) — next slice candidate
- HR zones, alerts, max-HR configuration
- Dedicated cadence sensor support if the 0x2A65 probe fails (capability hooks remain)
- ANT+ input transport (device model supports it; no implementation)
- Android target, WASM/Web Bluetooth (decision deferred pending a bounded spike —
  see the 2026-10-25 architecture discussion; Web Bluetooth is flag-gated on Linux,
  the owner's primary platform)

## Key unknowns (gated by hardware capture)

- The COOSPO H808S GATT inventory (expected standard 0x180D, but 002 taught us to
  capture before coding; some straps add proprietary services or odd flags)
- Whether the H1's 0x2A65 (Cycling Power Vector) carries cumulative crank
  revolutions → decides cadence in-scope vs deferred (requirement 3)
- BlueZ concurrent-connection behavior for trainer + strap on the NUC's adapter
  (Blue Falcon sdbus stack handled one device in 002; two is untested)

## Relationship to increment 002

Increment 002 delivered the Linux desktop BLE ERG MVP, live-verified on the Hammer H1
(PR #5, 2026-10-25): verified Wahoo-legacy ERG protocol (unlock 0x20 0xEE 0xFC, ERG op
0x42 on a026e005), SIG-spec Cycling Power Measurement parsing, bounded scan,
auto-reconnect, remembered trainer. 003 builds on that base; the multi-device-ready
core (device registry, capability discovery, metric-to-source mapping) sketched in
002's requirement 10 becomes real here. 002's pause and deliberate-release live
checks remain outstanding and are NOT folded into this slice's acceptance.

## Grill decisions log (2026-10-25)

| # | Decision |
|---|---|
| D1 | HR strap owned: COOSPO H808S (BLE + ANT+ dual) |
| D2 | HR scope: display only, no zones/alerts |
| D3 | Cadence: probe H1 0x2A65; ship if crank revs present, else defer |
| D4 | Source mapping: auto-assign + manual dropdown + remembered |
| D5 | Recording: excluded — separate slice |
| D6 | Startup: auto-connect all remembered sources |
| D7 | Reconnect: independent per device with backoff |
| D8 | HR display: prominent, under target line, bpm, 3 s stale rules |
| D9 | Devices screen: capability badges, simultaneous connects |
| D10 | Ride-screen Disconnect: drops all sources |
| D11 | Speed: hidden until a real source exists |
| D12 | 002's pause/release live checks: stay separate from this slice |
