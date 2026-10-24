#!/usr/bin/env bash
# Hardware evidence packet for increment 002 (Linux desktop BLE ERG MVP).
# Prints the evidence the rubric judges evaluate: the 5-minute smoke-ride
# checklist, committed implementation evidence, the captured Hammer GATT
# fixture, finalized classifier constants, persisted-settings defaults, and
# a privacy scan for Bluetooth addresses in committed evidence.
set -u
ROOT="$(git rev-parse --show-toplevel 2>/dev/null || (cd "$(dirname "$0")/../../../../.." && pwd))"
cd "$ROOT"
DESIGN_DIR="docs/features/002-desktop-ble-erg-mvp"
INC="docs/increments/002-desktop-ble-erg-mvp.md"
IMPL="$DESIGN_DIR/implementation"
FIXTURE="core/src/commonTest/resources/fixtures/hammer-gatt-inventory.json"
CLASSIFIER="core/src/commonMain/kotlin/com/trainerapp/core/protocol/ProtocolClassifier.kt"
SETTINGS="core/src/commonMain/kotlin/com/trainerapp/core/store/SettingsStore.kt"

echo "== acceptance checklist (increment doc) =="
if [ -f "$INC" ]; then
  awk '/^## Requirements/{f=1} f' "$INC" | sed -n '1,8p'
  grep -n "5-minute" "$INC" | head -3
else
  echo "MISSING: $INC"
fi

echo
echo "== committed implementation evidence =="
if [ -d "$IMPL" ]; then
  find "$IMPL" -type f \( -name '*.md' -o -name '*.json' -o -name '*.log' -o -name '*.txt' \) -printf '%s\t%p\n' 2>/dev/null | sort -k2
  [ -n "$(find "$IMPL" -type f 2>/dev/null)" ] || echo "EMPTY: no evidence files committed"
else
  echo "MISSING: $IMPL directory (WI-E smoke-ride evidence not committed)"
fi

echo
echo "== captured Hammer GATT fixture =="
if [ -f "$FIXTURE" ]; then
  echo "present: $FIXTURE ($(wc -c <"$FIXTURE") bytes)"
  python3 -c "import json;d=json.load(open('$FIXTURE'));print('services:',[s.get('uuid') for s in d.get('services',[])])" 2>/dev/null \
    || echo "UNPARSEABLE: fixture is not valid JSON"
else
  echo "MISSING: $FIXTURE (WI-A hardware capture not committed)"
fi

echo
echo "== classifier constants (must be finalized, not placeholder) =="
if [ -f "$CLASSIFIER" ]; then
  grep -n "CYCLEOPS\|FTMS_SERVICE\|FTMS_CONTROL_POINT\|INDOOR_BIKE_DATA" "$CLASSIFIER" | head -10
  grep -n "placeholder\|TODO\|PLACEHOLDER" "$CLASSIFIER" && echo "WARNING: placeholder markers still present" || echo "no placeholder markers"
else
  echo "MISSING: $CLASSIFIER"
fi

echo
echo "== persisted settings defaults =="
if [ -f "$SETTINGS" ]; then
  grep -n "120\|default" "$SETTINGS" | head -5
else
  echo "MISSING: $SETTINGS (SettingsStore not implemented)"
fi

echo
echo "== privacy scan: Bluetooth addresses in committed evidence/fixture =="
HITS="$(grep -rEn '([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}' "$IMPL" "$FIXTURE" 2>/dev/null || true)"
if [ -n "$HITS" ]; then
  echo "LEAK FOUND:"; echo "$HITS" | head -5
else
  echo "clean: no MAC-formatted strings in committed evidence or fixture"
fi

echo
echo "== end evidence packet =="
