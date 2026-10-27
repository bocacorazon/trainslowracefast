# Increment 004 — Ride Recording with Incremental FIT Save

Status: implemented 2026-10-26 (desktop; core encoder shared for the Android target)

## Summary

The ride screen records the ride at 1 Hz (power, heart rate, speed, cumulative
distance) and saves it as a FIT 2.0 file to `~/Rides/trainerapp/`. The file is
flushed to disk **every second** — a whole-file rewrite of header + all records
so far, without the CRC trailer — so a crash mid-ride loses at most one second,
and the file on disk is always at most one trailer short of importable.

## Requirements

1. **Manual start/stop.** "Start recording" on the ride screen; "Stop recording"
   finalizes. No auto-start: recording is an explicit act.
2. **Incremental persistence.** Every sample (1 Hz) is flushed to disk. The
   crash cost of a recording is ≤ 1 s of ride data, never the whole ride.
3. **Crash recovery.** On launch, an in-progress file (valid header, missing
   valid trailer) is surfaced on the ride screen with two choices:
   - **Resume** — the same file; new samples continue at the last record's
     absolute timestamp + 1 and the original records are kept verbatim.
   - **Discard** — delete the file.
4. **Disconnect finalizes.** The ride-screen Disconnect stops any running
   recording (trailer written) BEFORE the trainer's min-load release, so the
   ride is importable even if the disconnect path misbehaves.
5. **Importable output.** A stopped file is a standard FIT 2.0 little-endian
   file: file_id (type=activity), software_version, and records with
   timestamp (absolute FIT epoch seconds), power, heart_rate, speed
   (0.001 m/s), distance (0.01 m cumulative), plus the 2-byte file CRC over
   the whole file (header included, per the FIT spec). Missing sources
   encode as FIT invalid values, never zeros.

## Design

- `core/fit` (commonMain, pure — no I/O, no clock): `FitEncoder` (accumulates
  samples, emits header + messages ± trailer, accepts a `preData` section so a
  resumed file keeps its original records verbatim), `FitCrc` (FIT CRC16 +
  completeness check), `FitDecoder` (parses our own files, complete or
  in-progress, for recovery).
- `app-desktop/RideRecorder.kt` (JVM): owns `~/Rides/trainerapp/`, the 1 Hz
  sample loop (atomic temp-file + move writes), stop/finalize, and
  `findIncomplete()` for the recovery banner.
- `RideScreen.RecordingPanel`: live `recording m:ss → file · saves every second`
  line, the saved-confirmation line, and the incomplete-ride Resume/Discard
  banner.
- Sample source: trainer telemetry (watts, speed) + the merged HR source
  (strap first, then the trainer if it reports HR), polled at 1 Hz.

## Out of scope (next candidates)

- Laps, HR zones, auto-splitting by target change
- Strava / GoldenCheese upload
- ANT+ sources, cadence (the Hammer H1 reports none)
- Recording while the app is closed

## Verification

- `core` FitWriterTest: header layout, data-size field, CRC pass/fail,
  corruption detection, parse round-trip, null→invalid encoding, garbage
  rejection.
- `app-desktop` RideRecorderTest: start→stop produces a complete importable
  file; a crash (no stop) leaves a detectable in-progress ride; resume keeps
  the original start timestamp and appends to the same file; all-null
  snapshots still record.
