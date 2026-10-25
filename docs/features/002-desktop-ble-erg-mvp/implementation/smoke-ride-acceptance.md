# Smoke ride acceptance record — Increment 002 (Linux Desktop BLE ERG MVP)

**Date:** 2026-10-25 · **Hardware:** CycleOps Hammer H1 ("Hammer 51967"),
D5:4F:F8:F4:3A:22 · **Host:** Linux (BlueZ via Blue Falcon sdbus) ·
**App:** `:app-desktop:run`, Compose Desktop UI.

## Verdict: PASS on live hardware (ERG control verified end-to-end)

## Checklist results

| # | Test (grill decisions) | Result |
|---|---|---|
| 1 | Discovery: scan finds the Hammer, one row, strongest-real-signal sort | ✅ pass (after scan-window + junk-filter fixes) |
| 2 | Connect; ERG auto-engages at remembered target | ✅ pass — resistance felt at target, status `riding` |
| 3 | Live power telemetry while pedaling | ✅ pass (after CPM parser fix: flags uint16 + sint16 power) |
| 4 | ±3 W steps and ↑/↓ keys change actual resistance | ✅ pass — steps felt within ~1 s |
| 5 | Typed wattage entry + 50–600 W clamp notice | ✅ pass |
| 6 | Power-cycle trainer → auto-reconnect, target reapplied | ✅ pass |
| 7 | 5-minute ride, big W tracks around target | ✅ pass |
| 8 | Pause → min load, telemetry alive; Resume → target (D7) | ⚠️ not live-tested (solo rider — no one to click); contract tests pass |
| 9 | Deliberate disconnect → min-load release + honest release state (D9) | ⚠️ not live-tested (solo); contract tests pass |

Tests 8–9 are queued for the next ride with a helper. Both are fully
covered by `ErgController`/client contract tests; only the live-hardware
confirmation is outstanding.

## Bugs found and fixed during the ride (the ride's purpose)

1. **Scan never completed**: the adapter collected the backend's
   scan-results StateFlow forever, so `scan().toList()` collectors saw
   nothing. Scans are now bounded 12 s passes; each device emits once.
2. **Device list unusable**: full accumulated list re-emitted per update
   (hundreds of duplicate rows, no scrolling, unknown-rssi junk outranking
   the trainer). Now deduped, MAC-named junk filtered, rssi-sorted,
   scrollable.
3. **Watts never displayed**: `CyclingPowerMeasurement.parse` was written
   against an imagined 1-byte-flags layout and rejected every real
   18-byte frame. Rewritten to the SIG spec (flags uint16 LE, power
   sint16 LE at bytes 2..3) and verified against live captures.
4. **"Speed" was fiction**: 0x2A65 on the H1 is Cycling Power Vector, not
   speed; a one-shot read was misparsed as 32.8 km/h forever. Speed
   telemetry removed until a real source exists.
5. **ERG writes acknowledged but ignored**: the best-effort codec wrote
   `0x01 + watts` to the true CycleOps vendor char. The H1's real control
   path is the Wahoo-legacy protocol on `a026e005…`: unlock
   `[0x20 0xEE 0xFC]` at connect, then ERG opcode `0x42 + uint16 watts`.
   Verified live: resistance now follows the target. See
   `ble-adapter-notes.md` for the full protocol record.

## Lesson for the process

Fixture-based contract tests passed against an imagined wire format for
all three of these (parser frames, speed char, control codec). The live
ride caught every one within minutes. Hardware-facing increments need
their live verification item treated as the real test, not a formality.
