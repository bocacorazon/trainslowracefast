# Hardware capture notes — wi-hardware-capture (increment 002)

## Provenance

- **Date:** 2026-10-24
- **Device:** first-generation CycleOps Hammer H1 ("Hammer 51967"), advertising over BLE
- **Method:** live BlueZ session on the target Linux machine (NucBox-EVO-X2, BlueZ
  hci0): `bluetoothctl --timeout 20 scan on` → connect → full GATT attribute dump via
  D-Bus (`org.bluez` GattService1/GattCharacteristic1 objects) → device-info reads →
  disconnect.
- **Fixture:** sanitized inventory committed at
  `core/src/commonTest/resources/fixtures/hammer-gatt-inventory.json`
  (service/characteristic UUIDs and properties only — no device address, no serial).
- **Performed by:** the orchestrator session directly (the dispatched capture worker
  died twice at bootstrap — see the kanban record; capture was time-boxed by the
  trainer's short wake window).

## Device identity (from GATT Device Information service)

- Manufacturer (0x2A29): **Saris**
- Model number (0x2A24): **348**
- Firmware revision (0x2A27): **1**; Software revision (0x2A28): **27.031**
- USB modalias: `usb:v22BBp0014d0140` (Saris/CycleOps VID)

## Captured GATT layout — the important finding

**The Hammer H1 exposes NO FTMS service (no 0x1826, no 0x2AD9).** The increment-001
placeholder constants (the "1812-8dc" vendor guess) are wrong and have been replaced.

Services:

| Service | UUID | Role |
|---|---|---|
| Generic Access | 00001800-... | standard |
| Generic Attribute | 00001801-... | standard (service changed) |
| Device Information | 0000180a-... | standard, read-only strings |
| **Cycling Power** | 00001818-... | telemetry + one vendor control char |
| **Saris vendor** | **c0f4013a-a837-4165-bab9-654ef70747c6** | proprietary control |

Cycling Power service characteristics:

- 0x2A63 Cycling Power Measurement (**notify**) — the power/speed telemetry source
- 0x2A65 Cycling Power Feature (read)
- 0x2A5D Sensor Location (read)
- 0x2A66 Cycling Power Control Point (write+indicate) — standard CPS CP present
- **a026e005-0a7d-4ab3-97fa-f1500f9feb8b (write+indicate)** — Saris vendor
  characteristic inside the standard CPS; a candidate control path

Saris vendor service characteristics:

- **ca31a533-a858-4dc7-a650-fdeb6dad4c14 (write+indicate)** — the primary candidate
  control path

## Control-path conclusion

- Classification: **CYCLEOPS_PROPRIETARY** — confirmed by
  `ProtocolClassifierTest.capturedHammerClassifiesAsCycleOpsProprietary` against the
  committed fixture.
- Two candidate control characteristics exist (ca31a533... and a026e005...), both
  write+indicate. **Their opcode/payload semantics are NOT discoverable from the
  inventory** and were not probed during this capture (the trainer's wake window was
  intentionally kept short).
- **wi-ble-adapter must reverse-engineer the target-power opcode** against live
  hardware (candidate references: the qdomyoszwift project documents Saris/CycleOps
  BLE trainer protocols; GoldenCheetah's ANT+ FE-C path can serve as behavioral
  cross-check). Control opcodes are the next hardware-gated unknown.

## Minimum supported load

**Not determinable from the GATT inventory.** The standard CPS Control Point
(0x2A66) is present, which in principle carries a "Set Cumulative Value" /
calibration-style op set, but no target-power or minimum-load semantics are exposed
at the GATT level. Pause and safe-disconnect behavior must be established during the
wi-ble-adapter hardware session (write a candidate min target, observe flywheel
behavior) and recorded here.

## Privacy

- No device addresses, serial numbers, or session keys appear in the fixture or in
  this note. The device's advertised name ("Hammer 51967") is included as it is
  non-identifying product naming.
