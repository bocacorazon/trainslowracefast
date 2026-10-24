# trainerapp (KMP skeleton)

Kotlin Multiplatform app to control an indoor bike trainer (CycleOps Hammer H1) and run
workouts, replacing PerfPRO Studio on Linux/Android. See `docs/increments/001-hammer-connection-and-erg-control.md` for the increment spec.

## Layout

- `core/` — shared KMP module
  - `domain/TrainerClient.kt` — `TrainerClient` interface, `TrainerState` (the exact set the UI renders), telemetry, control results. Platform adapters must keep the state flow honest (no false "connected").
  - `protocol/` — GATT inventory model (no device addresses, safe to commit) + `ProtocolClassifier` (FTMS 0x1826/0x2AD9 → controllable; CycleOps 1812/8dc → proprietary control; 2A36-only → telemetry; else unsupported) + `InventoryExporter` (sanitized JSON).
  - `FakeTrainerClient` — in-memory stand-in used by the skeleton UIs and tests.
- `app/` — Android launcher (diagnostic screen over `TrainerClient`).
- `app-linux/` — Linux console launcher (NUC).

## Build

```bash
./gradlew :core:jvmTest          # shared tests (JVM)
./gradlew :app-linux:run         # run the Linux console diagnostic
./gradlew :app:assembleDebug     # Android APK (needs Android SDK, see local.properties)
```

Toolchain: JDK 21 (system), Gradle 8.10.2 (wrapper), Kotlin 2.1.20, AGP 8.7.3, compileSdk 35.

## Protocol notes

The Hammer H1 is a first-gen CycleOps trainer: BLE 4.0 + ANT+ FE-C, measures power and
speed, no cadence. Its BLE GATT layout is not published — the Phase-0 spike captures the
real inventory (sanitized export via `InventoryExporter`) and finalizes the CycleOps UUID
constants in `ProtocolClassifier`.
