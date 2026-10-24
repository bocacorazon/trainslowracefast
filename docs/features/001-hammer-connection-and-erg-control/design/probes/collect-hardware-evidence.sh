#!/usr/bin/env bash
# Hardware acceptance evidence packet for increment 001 (Hammer BLE + ERG control).
# Prints the evidence the rubric judge evaluates: hardware matrix, committed
# evidence files, GATT inventory fixture state, and a privacy scan for
# Bluetooth addresses in committed evidence.
set -u
ROOT="$(git rev-parse --show-toplevel 2>/dev/null || (cd "$(dirname "$0")/../../../../.." && pwd))"
cd "$ROOT"
DESIGN_DIR="docs/features/001-hammer-connection-and-erg-control"
INC="docs/increments/001-hammer-connection-and-erg-control.md"
IMPL="$DESIGN_DIR/implementation"

echo "== hardware matrix (increment doc) =="
if [ -f "$INC" ]; then
  awk '/Hardware matrix/{f=1;next} f&&/^#/{exit} f' "$INC"
else
  echo "MISSING: $INC"
fi

echo
echo "== committed evidence files =="
if [ -d "$IMPL" ]; then
  find "$IMPL" -type f \( -name '*.md' -o -name '*.json' -o -name '*.log' -o -name '*.txt' \) -printf '%s\t%p\n' 2>/dev/null | sort -k2
  [ -n "$(find "$IMPL" -type f 2>/dev/null)" ] || echo "EMPTY: no evidence files committed"
else
  echo "MISSING: $IMPL directory"
fi

echo
echo "== GATT inventory fixture =="
INV=$(find "$DESIGN_DIR" "$IMPL" -name '*inventory*.json' 2>/dev/null | head -1)
if [ -n "$INV" ]; then
  echo "found: $INV"
  python3 - "$INV" <<'EOF' 2>/dev/null || echo "(unparseable JSON)"
import json, sys
d = json.load(open(sys.argv[1]))
raw = json.dumps(d).lower()
uuids = sorted({u for u in raw.split('"') if len(u) == 4 and u.isalnum() and u.islower() and any(c in u for c in 'abcdef')})
print('short_uuids_present:', [u for u in uuids if u in ('1826', '2ad9', '2a36', '2a37', '8dc', '180c')])
print('ftms_1826_present:', '1826' in raw)
print('control_point_2ad9_present:', '2ad9' in raw)
EOF
else
  echo "MISSING: no *inventory*.json fixture found"
fi

echo
echo "== privacy scan: address-like strings in committed evidence =="
HITS=$(grep -rniE '([0-9a-f]{2}[:-]){5}[0-9a-f]{2}' "$DESIGN_DIR" 2>/dev/null | grep -viE 'example|placeholder|xx:xx')
if [ -n "$HITS" ]; then
  echo "VIOLATION: address-like strings found:"
  echo "$HITS" | head -20
else
  echo "clean: no address-like strings"
fi

exit 0
