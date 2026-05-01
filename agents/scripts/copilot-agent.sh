#!/usr/bin/env bash
# Launch Copilot CLI in this repo with a role file pre-loaded as opening context.
# Usage: agents/scripts/copilot-agent.sh <role-name>
set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "usage: $0 <role-name>" >&2
  exit 1
fi

ROLE="$1"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ROLE_FILE="$ROOT/roles/$ROLE.md"

if [[ ! -f "$ROLE_FILE" ]]; then
  echo "error: role not found: $ROLE_FILE" >&2
  exit 1
fi

cd "$ROOT/.."

copilot --prompt "You are operating as the agent defined in $ROLE_FILE. Read that file and any corpora it references in agents/corpora/. Stay in this role for the entire session. Await my instructions."
