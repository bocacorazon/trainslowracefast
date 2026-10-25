#!/bin/bash
# Verification-time probe for increment 003 (multi-sensor telemetry).
# Verifies the committed hardware-capture evidence for the COOSPO H808S HR
# strap and the H1 0x2A65 (Cycling Power Vector) cadence probe:
#   1. fixture present + parseable
#   2. service/characteristic list printed (real capture, not a template)
#   3. capture provenance recorded in the implementation notes
#   4. 0x2A65 probe outcome recorded either way (cadence in or deferred)
#   5. privacy scan: no Bluetooth addresses in committed evidence
# The capture itself is produced by the wi-hardware-capture work item
# (requires the physical strap and trainer near the NUC); this probe checks
# what was committed.
set -u
REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null)"
if [ -z "$REPO_ROOT" ]; then echo "FAIL: not a git repository"; exit 1; fi
FIXTURE="$REPO_ROOT/core/src/commonTest/resources/fixtures/hr-h808s-gatt-inventory.json"
CPV_NOTES="$REPO_ROOT/docs/features/003-multi-sensor-telemetry/implementation/hardware-capture-notes.md"
FAILED=0

echo "=== 1. H808S fixture ==="
if [ -f "$FIXTURE" ]; then
  python3 - "$FIXTURE" << 'PYEOF'
import json, sys
try:
    inv = json.load(open(sys.argv[1]))
    svcs = inv.get("services", [])
    print(f"parseable: yes ({len(svcs)} services)")
    for s in svcs:
        print("  service", s.get("uuid"))
        for c in s.get("characteristics", []):
            print("    char", c.get("uuid"), c.get("properties"))
    hr = [s for s in svcs if str(s.get("uuid", "")).startswith("0000180d")]
    if not hr:
        print("WARN: no Heart Rate service (0x180D) found in fixture")
    else:
        print("heart rate service present: yes")
except Exception as e:
    print("FAIL: fixture not parseable:", e); sys.exit(1)
PYEOF
  [ $? -ne 0 ] && FAILED=1
else
  echo "FAIL: fixture missing: $FIXTURE"; FAILED=1
fi

echo "=== 2. Capture provenance + 0x2A65 probe outcome ==="
if [ -f "$CPV_NOTES" ]; then
  echo "capture notes present: yes"
  grep -qiE "202[0-9]-[0-9]{2}-[0-9]{2}" "$CPV_NOTES" && echo "provenance date: present" || { echo "FAIL: no capture date in notes"; FAILED=1; }
  if grep -qiE "0x2A65|2a65" "$CPV_NOTES" && grep -qiE "crank|cadence" "$CPV_NOTES"; then
    echo "0x2A65 probe outcome: recorded"
    grep -qiE "deferred|not present|absent" "$CPV_NOTES" && echo "  -> cadence: deferred (recorded honestly)"
    grep -qiE "crank revolutions present|derived cadence|cadence ships" "$CPV_NOTES" && echo "  -> cadence: in-slice (recorded)"
  else
    echo "FAIL: 0x2A65 probe outcome not recorded in notes"; FAILED=1
  fi
else
  echo "FAIL: capture notes missing: $CPV_NOTES"; FAILED=1
fi

echo "=== 3. Privacy scan (no Bluetooth addresses in fixtures) ==="
PRIV_HITS=$(grep -rniE "([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}" "$REPO_ROOT/core/src/commonTest/resources/fixtures/" 2>/dev/null | wc -l)
if [ "$PRIV_HITS" -eq 0 ]; then echo "privacy scan: clean"; else echo "FAIL: $PRIV_HITS Bluetooth-address-like strings in fixtures"; FAILED=1; fi

echo "=== 4. Parser tests built from captured bytes ==="
if grep -rq "hr-h808s" "$REPO_ROOT/core/src" 2>/dev/null; then
  echo "fixture-referencing tests/notes: present"
else
  echo "WARN: no source reference to the H808S fixture found yet"
fi

if [ "$FAILED" -eq 0 ]; then echo "PROBE RESULT: PASS"; else echo "PROBE RESULT: FAIL"; exit 1; fi
