# Hardware capture notes — increment 003 (multi-sensor telemetry)

**Capture date:** 2026-10-25 · **Machine:** NUC (Linux, BlueZ 5.85, Blue Falcon
sdbus stack from 002) · **Method:** bluetoothctl (scan/connect/gatt menu) +
`gdbus monitor --system --dest org.bluez` for raw GattCharacteristic1
PropertiesChanged payloads (the ground-truth wire bytes; the BlueZ busctl
`Value` cache was observed to return zero-padded 22-byte artifacts that do
NOT match the wire — always trust the signal stream).

**Hardware:** COOSPO H808S BLE+ANT+ HR chest strap (advertises as
"808S 0004026") · CycleOps Hammer H1 ("Hammer 51967"), protocol record in
docs/features/002-desktop-ble-erg-mvp/implementation/ble-adapter-notes.md.

## 1. H808S GATT inventory — STANDARD Heart Rate service confirmed

Fixture committed at `core/src/commonTest/resources/fixtures/hr-h808s-gatt-inventory.json`:

- 0x1800 GAP (2A00/2A01/2A04/2AA6, read)
- 0x1801 GATT (no characteristics)
- **0x180D Heart Rate: 0x2A37 measurement (notify, CCCD) + 0x2A38 body sensor
  location (read)** — exactly the standard profile the design assumed
- 0x180F Battery (2A19 read; notify flag observed inconsistently across
  BlueZ sessions — treat as read-only for the MVP, battery not used)
- 0x180A Device Information (six read chars)
- **0xFD00 COOSPO vendor service**: fd09 (notify), fd0a (write-without-response),
  fd15 (notify), fd16 (write-without-response) — not needed for standard HR;
  flagged so nobody mistakes it for a control path

No Bluetooth addresses anywhere in the committed fixtures (privacy scan clean).

## 2. Live 0x2A37 frames — standard layout, byte-verified

10 frames captured while worn (fixture:
`core/src/commonTest/resources/fixtures/hr-h808s-frames.txt`), e.g.:

```
16 4d 06 03        flags 0x16, 77 bpm, RR 0x0306
16 4a 67 03 75 03  flags 0x16, 74 bpm, TWO RR intervals (two beats in window)
```

Layout confirmed: flags byte (0x16 = uint8 bpm, sensor contact supported AND
detected, RR-intervals present) → bpm uint8 → RR intervals uint16 LE
(1/1024 s each), count varies per frame (1–2 observed). The parser and its
tests must use these exact bytes.

## 3. H1 0x2A65 (Cycling Power Vector) cadence probe — cadence DEFERRED

Probe executed during active pedaling (CPM frames in the same window showed
90–126 W, so cranks were turning). 0x2A65 was read repeatedly; **every read
returned the identical 3-byte frame `8e 03 01`**:

- flags 0x8e = 0b1000_1110: **bit 0 (crank revolution data present) is CLEAR**
  — the H1's CPV carries no cumulative crank revolutions and no last-crank-
  event-time, so cadence cannot be derived from it.
- Remaining bytes are a constant vector magnitude; no time-varying crank data.

**Decision per grill D3: cadence is DEFERRED from this slice.** The H1
broadcasts cadence only over ANT+ FE-C. No cadence UI ships; no cadence
parser path is implemented; capability hooks remain for a future CSC/RSC
sensor or power-meter pedals. Recorded honestly — no fabricated cadence.

## 4. Simultaneous connections — CONFIRMED

The strap stayed connected (`Connected: yes`) while the trainer connected
and streamed live 18-byte CPM frames (`34 18 60 00 …`, flags 0x1834,
power sint16 at bytes 2..3 — consistent with the 002 protocol record).
BlueZ on this adapter holds both LE connections concurrently with no
instability observed during the ~2-minute dual-device session.

## 5. CPM frames captured in this session (dual-device evidence)

Sample (notify, while strap also connected):

```
34 18 60 00 c9 05 aa 03 00 00 fc e3 22 01 7d 68 27 00   (96 W)
34 18 7e 00 78 08 ad 03 00 00 93 ec 23 01 6c 6c 27 00   (126 W)
34 18 63 00 e0 09 af 03 00 00 4d f2 24 01 6c 70 27 00   (99 W)
```

Power at bytes 2..3 confirmed live under dual connection — matches the
002-verified parser.
