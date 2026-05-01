#!/usr/bin/env bash
# Cloud-tier agent harness using GitHub Copilot CLI in non-interactive mode.
#
# Reads a role definition from agents/roles/<name>.md, loads any referenced corpora,
# combines them with the user prompt, and invokes `copilot -p` for a single response.
# Uses the founder's existing Copilot subscription (no separate API budget).
#
# Tier discipline: CLOUD ONLY. Do not pipe per-athlete HRV, declared conditions,
# declared medications, or tagged context through this script.
# See docs/12_operational_agents.md §2.

set -euo pipefail

DEFAULT_MODEL=""  # empty = use copilot's default (currently Claude Opus class)

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ROLES_DIR="$ROOT/roles"
CORPORA_DIR="$ROOT/corpora"

usage() {
  cat <<EOF
usage: $0 <role> [--model MODEL] [--input FILE] [--output FILE] [--dry-run]

  <role>          name of role file (without .md) in agents/roles/
  --model MODEL   override model id (default from role front-matter, else $DEFAULT_MODEL)
  --input FILE    read user prompt from FILE (default: stdin)
  --output FILE   write response to FILE (default: stdout)
  --dry-run       print assembled system prompt and exit; no API call
EOF
}

if [[ $# -lt 1 ]]; then usage >&2; exit 1; fi

ROLE="$1"; shift
MODEL=""
INPUT=""
OUTPUT=""
DRY_RUN=0

while [[ $# -gt 0 ]]; do
  case "$1" in
    --model)   MODEL="$2"; shift 2 ;;
    --input)   INPUT="$2"; shift 2 ;;
    --output)  OUTPUT="$2"; shift 2 ;;
    --dry-run) DRY_RUN=1; shift ;;
    -h|--help) usage; exit 0 ;;
    *) echo "unknown arg: $1" >&2; usage >&2; exit 1 ;;
  esac
done

ROLE_FILE="$ROLES_DIR/$ROLE.md"
if [[ ! -f "$ROLE_FILE" ]]; then
  echo "error: role not found: $ROLE_FILE" >&2
  exit 1
fi

# Parse YAML front-matter (between leading '---' lines). Extract `model:` and `corpora:`.
FRONT_MATTER=""
BODY=""
if head -n 1 "$ROLE_FILE" | grep -qx -- '---'; then
  FRONT_MATTER="$(awk '/^---$/{c++; next} c==1{print} c==2{exit}' "$ROLE_FILE")"
  BODY="$(awk '/^---$/{c++; next} c==2{print}' "$ROLE_FILE")"
else
  BODY="$(cat "$ROLE_FILE")"
fi

ROLE_MODEL="$(echo "$FRONT_MATTER" | awk -F': *' '/^model:/{print $2; exit}')"
CORPORA_LINE="$(echo "$FRONT_MATTER" | awk -F': *' '/^corpora:/{print $2; exit}')"

# Resolve effective model: CLI > role > default
if [[ -z "$MODEL" ]]; then MODEL="${ROLE_MODEL:-$DEFAULT_MODEL}"; fi

# Build corpus block
CORPUS_BLOCK=""
if [[ -n "$CORPORA_LINE" ]]; then
  CLEAN="${CORPORA_LINE#[}"; CLEAN="${CLEAN%]}"
  IFS=',' read -ra NAMES <<< "$CLEAN"
  for raw in "${NAMES[@]}"; do
    name="$(echo "$raw" | xargs)"
    [[ -z "$name" ]] && continue
    cpath="$CORPORA_DIR/$name.md"
    if [[ ! -f "$cpath" ]]; then
      echo "warning: corpus not found: $cpath" >&2
      continue
    fi
    CORPUS_BLOCK+=$'\n\n---\n\n# Corpus: '"$name"$'\n\n'"$(cat "$cpath")"
  done
fi

SYSTEM="$BODY"
if [[ -n "$CORPUS_BLOCK" ]]; then
  SYSTEM+=$'\n\n---\n\n# Reference corpora'"$CORPUS_BLOCK"
fi

# Read user prompt
if [[ -n "$INPUT" ]]; then
  USER_PROMPT="$(cat "$INPUT")"
else
  if [[ -t 0 ]]; then
    echo "error: no --input and stdin is empty" >&2
    exit 1
  fi
  USER_PROMPT="$(cat)"
fi

# Combine system + user into a single prompt (copilot -p takes one prompt).
COMBINED_PROMPT="# ROLE INSTRUCTIONS

$SYSTEM

---

# TASK

$USER_PROMPT

---

Respond now in the role above. Output only the requested artifact — no preamble, no
commentary about what you are doing, no tool calls. Plain text or markdown only."

if [[ "$DRY_RUN" -eq 1 ]]; then
  echo "# Model: ${MODEL:-<copilot default>}"
  echo "# ----- combined prompt -----"
  echo "$COMBINED_PROMPT"
  exit 0
fi

# Build copilot args. We do NOT pass --allow-all-tools: text-generation roles don't
# need tools, and unapproved tool calls in non-interactive mode will simply be skipped.
COPILOT_ARGS=(--no-color --log-level=none)
if [[ -n "$MODEL" ]]; then
  COPILOT_ARGS+=(--model "$MODEL")
fi

# Call copilot. Model output goes to stdout; the "Changes/Requests/Tokens" footer
# goes to stderr — we leave it visible so the founder sees premium-request usage.
if [[ -n "$OUTPUT" ]]; then
  copilot "${COPILOT_ARGS[@]}" -p "$COMBINED_PROMPT" > "$OUTPUT"
else
  copilot "${COPILOT_ARGS[@]}" -p "$COMBINED_PROMPT"
fi
