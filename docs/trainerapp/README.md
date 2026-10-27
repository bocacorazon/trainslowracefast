# TrainerApp — program overview

A personal indoor-training app: control a CycleOps Hammer H1 trainer over
BLE from a Linux desktop, ride with live telemetry (power, heart rate), and
record sessions to FIT. Built to replace Windows-only PerfPRO Studio, and to
become the device-integration leg of the trainslowracefast product vision
(see `docs/product/product_ideas/04_live_training_device_integration.md`).

**Origin:** `ANALYSIS.md` in this directory (imported from
`~/Projects/trainerapp/`, 2026-10-22, rubber-duck pass 1 folded in) — the
feasibility doc that picked Option G (one Kotlin Multiplatform codebase).
The state lines below supersede it.

## What exists (slice roadmap)

| Slice | Scope | Status |
|---|---|---|
| 001 | KMP skeleton: core `TrainerClient` contract, GATT inventory + `ProtocolClassifier`, fake client, Android + Linux launchers | ✅ merged (PR #1–#2) |
| 002 | Linux desktop BLE ERG MVP: discovery, connect, auto-ERG at remembered target (±3 W steps, 50–600 W clamp, coalesce), auto-reconnect, pause, safe disconnect, Compose UI | ✅ **live-verified on the H1** 2026-10-25 (PR #4–#5); see `docs/features/002-desktop-ble-erg-mvp/implementation/` |
| 003 | Multi-sensor telemetry: trainer + COOSPO H808S HR strap concurrently, capability discovery from real GATT inventories, per-data-type source mapping with manual override, per-device reconnect, HR display, disconnect-all | ✅ **live dual-device acceptance 2026-10-26** (PR #7–#15); see `docs/features/003-multi-sensor-telemetry/implementation/` |
| 004 | Ride recording: incremental FIT persistence during the ride | ✅ merged (PR #16); see `docs/increments/004-ride-recording-fit.md` |
| — | HR zones/alerts, Strava upload, ANT+ input transport, Android target | candidates, unplanned |

**Deferred decision:** a web/WASM execution model (Compose → wasm + Web
Bluetooth). Deliberately postponed — Web Bluetooth is flag-gated/experimental
on Linux (our primary platform), the domain core is already
transport-agnostic in `commonMain`, and only the JVM transport layer would
need rewriting. Revisit with a bounded spike, not a conversion.

**Outstanding live checks** (covered by contract tests only, never ridden
live): trainer D6 auto-reconnect (the H1 napping mid-ride was recovered by a
manual connect), Pause, and deliberate-disconnect release. All three need a
second person or a longer solo session.

## Hardware (and its quirks)

- **CycleOps Hammer H1** (2016, advertises as `Hammer 51967`) — direct drive,
  BLE 4.0 + ANT+ FE-C. Sends cadence over ANT+ FE-C (what PerfPRO shows) but
  **not over BLE**: the 003 CPV probe (0x2A65) found no crank-revolution data,
  so BLE cadence is deferred until a dedicated sensor. Sleeps when not
  pedaled — always ask the human to wake it before hardware work.
- **COOSPO H808S HR strap** (advertises as `808S 0004026`) — standard Heart
  Rate service (0x180D / 0x2A37 notify + 0x2A38), plus battery/device-info and
  a COOSPO vendor service (0xFD00, unused). Captured inventory and live wire
  frames: `core/src/commonTest/resources/fixtures/hr-h808s-*`.

### The H1's wire protocol (verified live, 2026-10-25)

The H1 is **not FTMS**. Its ERG control path is the pre-FTMS Wahoo-legacy
protocol on `a026e005-…` (inside the Cycling Power service):

- connect → unlock write `[0x20, 0xEE, 0xFC]`, wait ~700 ms — ERG writes are
  **silently ignored** without this handshake;
- ERG target power = opcode `0x42` + uint16 LE watts (ack indication observed);
- telemetry = Cycling Power Measurement (0x2A63): flags uint16 LE, then
  power sint16 LE at bytes 2..3 (18-byte frames);
- 0x2A65 on this device is Cycling Power Vector (no crank data), **not** speed.

Full record with capture provenance:
`docs/features/002-desktop-ble-erg-mvp/implementation/ble-adapter-notes.md`.

**The lesson that shapes everything here:** fixture tests built from imagined
wire formats pass while real hardware does something else. Every parser test
is built from captured bytes; every hardware-facing claim was proven on the
real device before a slice is called done. Live acceptance rides are the
actual test suite for hardware behavior.

## Stack & layout

One Kotlin Multiplatform codebase (JDK 21, Gradle wrapper — see
`build.gradle.kts` files for pinned versions):

- `core/` — `commonMain` domain (contracts, ERG controller, device registry +
  capability discovery + source mapping, parsers, session orchestration,
  FIT recording): **no BLE/Compose types**; `jvmMain` transport (BlueZ via
  Blue Falcon sdbus) — the only layer a WASM port would rewrite.
- `app-desktop/` — Compose Multiplatform desktop UI (ride screen, devices
  screen), consuming core state flows only.
- `app/` — Android launcher (deferred target). `app-linux/` — console
  diagnostics.

```bash
./gradlew jvmTest              # full test suite
./gradlew :app-desktop:run     # run the desktop app (needs BlueZ)
./gradlew :app:assembleDebug   # Android APK (needs Android SDK)
```

## How this program is run (Hermoso process)

Every slice since 002 goes through the Hermoso lifecycle: grill session →
increment doc (`docs/increments/`) → feature design + verification contract
(sealed under `docs/features/<slug>/design/`) → work graph → construction →
**live acceptance ride**. Evidence (capture notes, acceptance records,
protocol notes) lands in `docs/features/<slug>/implementation/`. Run state
lives in `.hermoso/runs/` (local); worktrees under
`~/Projects/.hermoso-worktrees/<run-id>/`. Completed work merges to `main`
via PR — no long-lived branches.

`docs/features/` and `docs/increments/` stay at the `docs/` root (not under
this directory) on purpose: sealed verification contracts reference those
paths, and moving them breaks re-verification.

## Where things live in this repo

- `docs/product/` — the original workstream: trainslowracefast SaaS research
  (workshops 01–12, glossary, assumption register, kill criteria, LP copy,
  product ideas).
- `docs/trainerapp/` — this program (overview, origin analysis).
- `docs/features/`, `docs/increments/` — trainerapp designs, evidence, and
  increment specs (path-stable).
- `agents/` — research-agent harness (roles, corpora, scripts), built for
  the product research and reusable for validation passes.