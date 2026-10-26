# Smoke ride acceptance record — Increment 003 (Multi-Sensor Telemetry)

**Date:** 2026-10-26 · **Hardware:** CycleOps Hammer H1 ("Hammer 51967") +
COOSPO H808S strap ("808S 0004026", worn) · **Host:** NUC (Linux, BlueZ 5.85,
Blue Falcon sdbus) · **App:** `:app-desktop:run` from merged main.

## Verdict: PASS on live hardware — dual-device session verified end-to-end

## Checklist results

| # | Test (grill decisions) | Result |
|---|---|---|
| 1 | Launch with both devices remembered → auto-connect both, land on ride screen | ✅ pass — trainer first (ERG engaged), then strap |
| 2 | Devices screen: both connected with capability badges | ✅ pass — `trainer • power • control` / `[heart rate]` |
| 3 | Pedal: live watts AND live bpm on one screen | ✅ pass — rider confirmed both live simultaneously |
| 4 | ±3 W steps / arrows change actual resistance | ✅ pass (verified in 002; unchanged path) |
| 5 | Strap powered OFF mid-ride | ✅ pass — bpm → red "—" within 3 s; status named `HR reconnecting (attempt N)`; trainer kept riding |
| 6 | Strap powered back ON | ✅ pass — recovered independently |
| 7 | HR-source dropdown reflects the strap | ✅ pass — assignment persisted to settings.json (`sources.heartRate`) |
| 8 | Disconnect drops all sources | ✅ pass — trainer min-load release first, then strap |
| — | Pause/Resume live checks | ⚠️ still deferred with 002's (solo rider; owner's call) |

## Bugs found during this ride and fixed (the ride's purpose)

1. **Backend connect race** (first launch this session): `connect()` was
   fire-and-forget, so service discovery raced the BlueZ handshake and got an
   EMPTY GATT — trainer classified `Unsupported`, no telemetry, no control
   path, while the UI said "riding". (002 never hit it because BlueZ had a
   warm GATT cache.) Fix: `connect` waits for the real connected state;
   `discoverServices` waits for a non-empty service list.
2. **Sensor key-resolution miss → trainer-path misroute** (first manual
   strap connect): `connectSensor` resolved the strap's deviceKey via a
   trainer-client scan map that didn't yet contain it, returned false, and
   the UI fell back to connecting the strap through the TRAINER path —
   phantom "riding", stuck reconnect, no HR. Fix: `connectSensor` falls back
   to its own bounded scan on a resolution miss. A subsequent live probe
   (SensorSessionProbeTest, since removed) proved the sensor path in
   isolation: strap Connected in 410 ms, first live bpm=63 frame parsed.

## Live evidence excerpts

- Trainer: `connected: protocol=CycleOpsProprietary telemetry=00002a63… control=a026e005(indicate=true)`; `controlWrite result=ACKNOWLEDGED`
- Telemetry: 18-byte CPM frames (`34 18 …`, power sint16 at bytes 2..3), live while pedaling
- Strap: SensorSession Connected, parsed HR frames flowing to the ride screen (rider-confirmed)
- Mid-ride the trainer napped once (its known sleep behavior); the rider hit Connect on the
  Devices screen and it reconnected cleanly. The trainer-auto-reconnect path (D6) was NOT
  exercised by that event — recorded honestly; it remains covered by contract tests only,
  alongside 002's deferred pause/release live checks.

No Bluetooth addresses in committed fixtures (privacy scan clean). No pass
on fake clients: every item above was observed on the real devices.