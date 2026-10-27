<!-- Imported 2026-10-27 from ~/Projects/trainerapp/ANALYSIS.md (v1, 2026-10-22,
     rubber-duck pass 1 folded in) — the origin document that launched the
     trainerapp workstream in this repo. Unmodified import; the live program
     state is docs/trainerapp/README.md; the duck-pass evidence referenced in
     §6 is alongside: duck-pass-1.json. The original directory also contained
     scratch (duck1.py, duck1.log) — process scratch, not carried over. -->

# Personal Trainer Control App — Feasibility & Effort Analysis

Date: 2026-10-22 · Status: v1 (rubber-duck pass 1 folded in)

## 1. Requirements

Replace PerfPRO Studio (Windows-only) with software the user controls:

1. Control the trainer: set resistance/target power up and down (erg mode).
2. Show live stats of the ongoing workout (power, HR, cadence, speed, time, distance).
3. Start / pause / stop the workout.
4. Record the session and upload it to a fitness service — target: something tapiriik
   syncs (i.e. Strava as the entry point; tapiriik then syncs to the rest).
5. Platforms: Linux (primary) and/or Android (secondary).
6. Scope: explicitly NOT a metrics-analysis app. Minimal by design.

Hardware: modern smart trainer with BLE + ANT+ radios. **No CompuTrainer serial
support needed** (user clarification, 2026-10-22).

Trainer identified (2026-10-23): **CycleOps Hammer, first generation (2016,
"H1", later H2 / rebranded Saris)** — direct-drive, up to 2000W / 20% grade,
±3% accuracy, **native ANT+ FE-C + BLE 4.0** (dual-protocol, Zwift-compatible;
no USB data port, no bridge involved). It measures power and speed but
**NOT cadence** — cadence stat needs a separate sensor or is dropped.
Consequence for the plan: nothing changes — GC drives it on Linux (ANT+ via
USB dongle or BLE FTMS), and the Android app can drive it directly over BLE.
The Phase-0 spike now has a concrete target: discover the FTMS service
(0x1826) on the Hammer and write the target-power characteristic.

## 2. Verified facts (2026-10-22)

### GoldenCheetah (~/Projects/GoldenCheetah)
- Actively maintained: 500 commits since Jan 2026; last commit 2026-10-20.
- C++20, qmake build, requires Qt >= 6.5.3 (INSTALL-LINUX documents Qt 6.8.3).
- GPL v2 (or later). ~365k LOC total.
- Everything the requirements need already exists and is maintained:
  - **BLE trainer control**: `src/Train/BT40Device.cpp` (1412 LOC) — Qt
    QLowEnergyController client supporting Wahoo Kickr protocol, Kurt InRide,
    and generic **FTMS** (FIT Machine Service, the modern BLE standard for
    trainers): `setLoadErg(watts)`, `setGradient()`, `setMode()` (erg/speed/gradient).
  - **ANT+ FE-C**: `src/ANT/` (~8k LOC core), USB ANT+ dongle on Linux
    (optional libusb dependency).
  - **Controller abstraction**: `RealtimeController` (805 LOC) —
    start/pause/stop, setLoad/setGradient/setMode, calibration.
  - **Workout engine**: `ErgFileBase` (810) + `ErgFile` (2248) parse
    erg/workout files (watts/gradient vs time, zones); `WorkoutWidget` (3153)
    is the workout player UI.
  - **Telemetry model**: `RealtimeData` (1566) — watts, speed, cadence, HR,
    distance, time, plus derived values.
  - **Live UI**: `TrainSidebar` (3528) + realtime plots; "Train" is a view in
    the main window.
  - **Recording/export**: realtime sessions save as activities;
    `FitRideFile::writeRideFile` → FIT export.
  - **Strava upload**: `src/Cloud/Strava.cpp` (1006 LOC) — OAuth
    refresh-token flow, multipart FIT upload to `/api/v3/uploads`, plus
    activity download. Ships a Strava client_id (83); client_secret is a
    build-time define (placeholder default → register your own Strava app,
    ~2 min, free).
- Stripped core relevant to the requirements ≈ 14k LOC — but it is tightly
  coupled to GC's Context/DataSource/Settings architecture, so "extract these
  14k lines" is not as clean as the line count suggests.

### Tapiriik
- Sync-only service (Garmin Connect, Strava, TrainingPeaks, Runkeeper,
  RideWithGPS, Dropbox, …). **Not an upload target for apps.** The upload
  destination is Strava (or TP/Garmin); tapiriik does the rest of the
  synchronization.

### Strava API (as of 2026)
- Standard OAuth flow still available to individuals; "solo developer" tier
  exists (2026-04 changelog). Refresh-token pattern (6h access tokens).
- API base URL is migrating www.strava.com/api/v3 → api.strava.com
  (changelog 2026-10) — pin the new base in any new client.
- Duck claimed `activity:write` may need manual API review; the public
  docs don't support that (it's a standard authorize-time scope), but
  verify at implementation time — token persistence + refresh rotation is
  the real implementation cost, not the scope.

### Existing alternatives (no-build options)
- **Kinomap** (Android, free unlimited tier): controls trainer resistance by
  workout stage, structured workouts, Strava sync, 220+ brands. Not open
  source; video-centric (not the minimalist the user wants, but a zero-cost
  interim phone option).
- **qdomyos-zwift** (GPL-3.0, 882★, pushed 2026-10-22): cross-platform
  (Linux/Win/Android/iOS) BLE bridge that exposes a trainer to any app as a
  Wahoo Kickr over FTMS; has a WebSocket API; reference-quality BLE FTMS code
  on Android (Kotlin/JNI). (Its CompuTrainer driver is irrelevant here.)
- GC itself on Linux already replicates the PerfPRO Studio role (workout
  driving + erg + upload), minus the iOS remote app.

### Language reality check
- Go (user's preferred language): **no mature BLE stack on Linux**
  (gatttool shell-out / dbus-via-godbus = fragile). Go is fine for a sidecar
  (workout server, FIT writer, Strava uploader, WebSocket remote), but the
  trainer link is the heart of this app and it shouldn't be built on a
  fragile BLE layer.
- C++/Qt: GC's protocol code is directly reusable (GPL-compatible).
- Kotlin/Android: well-trodden path for BLE FTMS (FTMS UUIDs: control point
  0x2AD6, target power 0x2AC6, indoor bike data 0x2AD2; Nordic's Android
  BLE library).

## 3. Options & effort estimates

Assumptions: part-time (~10–15 h/week), single developer, personal use.

### A. Run GC on Linux as-is — **1–3 days**
- Build from the v3.8 release tag (2026-09-20) or the 2026-10-20 snapshot,
  not bare master. Effort: install Qt6 + deps (WebEngine, gsl, libical,
  libusb), qmake build, configure trainer + Strava. Zero code.
- Coverage: 100% of requirements on Linux (BLE FTMS + ANT+ FE-C, erg
  workouts, live stats, pause/stop, FIT export, Strava upload).
- Cost: heavy app, many unwanted metrics, dated UI. No Android story.
- This de-risks everything else: it's the requirements oracle.

### B. Fork GC, strip to trainer-only — **4–8 weeks part-time, then ongoing**
- Realistic stripping touches Train/Core/Gui/FileIO (300k+ LOC of coupling).
- Upstream is very active (500 commits/10 months) → permanent merge burden.
- Not recommended unless "owning the codebase" is the dominant goal.

### C. New small C++/Qt app reusing GC's protocol files — **6–10 weeks to v1**
- Duck pass (gemma-4, 2026-10-22) flagged this as the under-estimated option:
  BT40Device.cpp drags CalibrationData → RealtimeData → ErgFileBase →
  GoldenCheetah.h → Context/Settings — extraction into a fresh app means a
  month-scale dependency/linker battle before the first BLE packet.
- Practical variant: **fork GC and strip** (Option B done right) — GC
  already builds, so you prune UI/metrics instead of untangling includes.
  Cost: permanent upstream-merge burden against a very active project.
- Write new (either way): app shell, one-screen UI (target power, current/avg
  power, HR, cadence, time, distance, next segment), workout state machine
  (start/pause/resume/stop), BLE reconnect handling, 1 Hz FIT logging
  (port GC's FitRideFile writer — do NOT hand-roll FIT from the spec),
  Strava OAuth (one-time browser flow → stored refresh token).
- Main risk: BLE GATT edge cases (pairing, disconnect/reconnect,
  notification flow) — the usual schedule slippage.
- Verdict: the longest route; only worth it if an owned Linux C++ codebase
  is itself the goal.

### D. New app in Go — **not recommended as the main app**
- Go BLE on Linux is the weak link (no mature stack).
- Go *is* right for a remote-control sidecar: WebSocket/HTTP server over
  the workout state (~200–500 LOC) + FIT/Strava pipeline. See F.

### E. Android-first Kotlin app (phone is the trainer master) — **6–12 weeks part-time**
- Trainer is BLE-native → phone talks directly to the trainer; no PC
  required during the ride.
- Scope: scan/connect (BLE scan permissions: Nearby Devices + location on
  old Android), FTMS notifications (power/cadence/speed/time), set target
  power, workout runner (interval list + state machine), big-number UI
  (power/HR/time/next), pause/stop, 1 Hz FIT log (reuse a reference
  implementation, e.g. port GC's FitRideFile logic — don't hand-roll),
  Strava upload over Wi-Fi, **foreground service + wake lock** (without
  these Android kills the recording/BLE link when the screen dims —
  non-negotiable for a phone-on-handlebars app), reconnect handling.
- Cost (duck-flagged, accepted): Kotlin is new to the user (Go > Python >
  JS), and Android BLE is fragmented across chipsets with a real debugging
  tax. This is the **highest-risk option** and should be last, not an
  alternative to the Linux path.
- ANT+ unavailable on Android (no radio) — acceptable since the trainer is
  BLE.

### G. Kotlin Multiplatform: one core + two Compose frontends (user's chosen direction) — **~10–12 weeks part-time, gated by a 2–3 day spike**
Single codebase: `commonMain` core (workout engine, telemetry, FIT, Strava)
+ `androidApp` + `desktopApp` (Compose Multiplatform). Verified stack,
2026-10-22:
- **BLE**: **Blue Falcon** (Reedyuk/blue-falcon, 489★, Apache-2.0, pushed
  2026-10-17, v3.0) — KMP BLE with an Android engine *and* a BlueZ D-Bus
  engine (`blue-falcon-sdbus`) targeting `linuxX64`/`linuxArm64`/`jvm`.
  Full GATT: scan w/ UUID filter, connect, discovery, read/write,
  notifications, descriptors, bonding. JVM target needs JDK 17+; Linux
  needs BlueZ ≥5.50 + user in the `bluetooth` group. Kable (1202★, active)
  underpins the Android side.
- **FIT**: **fit-kotlin-sdk** (io.github.glandais, Maven Central, KMP,
  "reads and writes Garmin FIT files", profile 21.218, all commonMain).
- **UI**: Compose Multiplatform — JetBrains badges desktop (incl. Linux)
  and Android as production-ready (2026).
- **HTTP/Strava**: ktor or OkHttp in commonMain (OAuth one-time flow,
  multipart upload, refresh-token persistence).
- **ANT+ (optional, Linux only)**: usb4java over the USB ANT+ stick.
- GC's C++ code drops from "reused" to "reference": FTMS UUID table
  (Ftms.h), Wahoo-Kickr protocol quirks (if the trainer is a Kickr),
  FIT field layout. No license entanglement.
- Effort breakdown (10–15 h/wk, first Kotlin project):
  - Phase 0 — spike (2–3 days): KMP skeleton, Blue Falcon wired on both,
    scan → connect → live power notifications from the real trainer on the
    NUC and the phone. **Go/no-go gate.**
  - Phase 1 (wk 2–4) — core: set target power, workout state machine
    (start/pause/resume/stop, interval list, next segment), 1 Hz telemetry.
  - Phase 2 (wk 4–6) — record + upload: fit-kotlin-sdk writer, Strava
    OAuth + upload.
  - Phase 3 (wk 6–8) — UI: big-number screen, workout picker, settings
    (shared Compose, platform chrome where needed).
  - Phase 4 (wk 8–10) — hardening: Android foreground service + wake lock,
    reconnect, screen-off, pairing, real-ride debugging.
  - Buffer (wk 10–12) — Kotlin learning-curve slippage.
- Known quirks: BlueZ has no GATT-cache refresh (reconnect = re-discover),
  MTU read-only; Blue Falcon is third-party (fallback if it disappoints:
  RemoteBLE agent pattern — an agent owns the radio, the app drives it over
  IP with a Kable-compatible API).
- Why this beats "C++ for Linux + Kotlin for Android": workout engine,
  FIT, and Strava are written once, not twice; one language; GC stays a
  reference instead of a codebase to merge or untangle.

### F. Hybrid, PerfPRO-Remote model: Linux box = master, Android = remote — **2–3 weeks on top of A**
- GC (or the C app) on Linux drives the trainer; ~200–500 LOC Go
  WebSocket sidecar exposes workout state + commands; small remote UI
  (Kotlin, or a phone browser page).
- Only worth it if the PC must stay master (course/video mode) or the
  trainer were ANT+ only. With a BLE trainer, E is simpler.

## 4. Recommendation (phased)

Direction chosen (2026-10-22): **Option G — Kotlin Multiplatform, one core,
two Compose frontends.** The stack is verified and every risky component
(BLE on both platforms, FIT read/write, desktop UI) has a maintained library.

1. **Now, in parallel (1–3 days): build & run GC on Linux.** Stays the
   requirements oracle and the interim trainer app while the build proceeds —
   it's cheap insurance and keeps you training.
2. **Phase 0 spike (2–3 days): the go/no-go.** Scaffold the KMP project,
   wire Blue Falcon (sdbus engine on the NUC, Android engine on the phone),
   and get scan → connect → live power notifications from the real trainer on
   both. If this works, the rest of the plan is just feature work.
   Fallback if BlueZ/Blue Falcon disappoints on the NUC: RemoteBLE agent
   pattern (agent owns the radio, app talks over IP).
3. **Phases 1–4 (weeks 2–12): build the app** per the G breakdown.
4. **Phone interim during the build:** Kinomap free tier (or the trainer's
   own app) — no longer a fallback, just coverage.

Overall effort verdict for Option G: **~10–12 weeks part-time (≈3 months
wall-clock) to a v1 on both platforms**, with the first 2–3 days as a
hard gate. That's the cost of "my own app, both platforms, one codebase" —
and notably better than the 10–18 weeks of the split C++/Kotlin approach,
because the core is written once.

## 5. Open questions (for the user)

1. ~~Exact trainer model~~ — resolved: CycleOps Hammer H1 (2016), ANT+ FE-C + BLE 4.0, no cadence.
2. Which Linux machine (NUC .89? which distro?) — for the GC build.
3. Does the user already own a USB ANT+ stick (likely, since PerfPRO drives
   it via ANT)? If not, the NUC can use BLE instead — no dongle needed.
4. Strava as the upload entry point OK (tapiriik does the fan-out)?
5. Personal-only or intended to be shared/published? (Affects license
   decision: any reuse of GC code in a published app must stay GPL2+.)
6. Cadence: add a BLE/ANT+ cadence sensor, or drop the stat?

## 6. Rubber-duck log

- **Pass 1** — gemma-4-31b (Mac Studio 192.168.10.52:8080), adversarial
  frame, 246.6 s, 3038 prompt / 1486 completion tokens, finish=stop,
  verdict **FLAWED**, 6 findings (full text: `duck-pass-1.json`).
  - Accepted & folded: Android estimate optimistic → 6–12 wks; GC code
    coupling deeper than extraction suggests → 6–10 wks / fork-and-strip
    variant; foreground service + wake lock added to E scope; FIT
    "reuse a reference, don't hand-roll" added; recommendation ordering
    hardened (Kinomap as default end-state, go/no-go checkpoint, E last).
  - Partially accepted: Strava scope-review friction not supported by the
    public docs — kept as "verify at implementation", added the 2026 API
    base-URL migration note instead.
  - Note: gemma-4-31b is a different model from the working model, and had
    no conversation context — clean-context + model diversity both apply.
