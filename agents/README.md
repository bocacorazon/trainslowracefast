# Operational agents

This directory implements the operational agent framework defined in `docs/12_operational_agents.md`.

## Layout

```
agents/
  roles/        Role definitions (one markdown file per agent)
  corpora/      Reference material loaded into agent context
                  (voice samples, brand guide, glossary)
  scripts/      Harness — agent.sh (cloud) and copilot-agent.sh (interactive)
  outputs/      Agent drafts land here; gitignored
```

## Two ways to invoke an agent

### 1. Programmatic — `scripts/agent.sh` (GitHub Models API)

For batch / scripted calls. Uses the GitHub Models inference endpoint, included with
your Copilot subscription. Auth via `GITHUB_TOKEN` (falls back to `gh auth token`).

```bash
# One-shot from stdin
echo "Draft three subject lines for the LP launch announcement." \
  | agents/scripts/agent.sh content-writer

# From a file, write to a file
agents/scripts/agent.sh outreach-drafter \
  --input briefs/advisor-X.md --output agents/outputs/advisor-X-email.md

# Override model
agents/scripts/agent.sh research-scout --model openai/gpt-4o
```

### 2. Interactive — `scripts/copilot-agent.sh` (Copilot CLI)

For collaborative iterative sessions (essays, research synthesis). Launches `copilot`
in this repo with the role file pre-loaded as opening context.

```bash
agents/scripts/copilot-agent.sh content-writer
```

## Tier discipline

**Cloud tier** (this harness): all agents EXCEPT those processing per-athlete sensitive data.
**Local tier** (Ollama, deferred per K-W7-5): Workout Analyst, Weekly Review Drafter, Support Drafter.

The cloud harness must NEVER be invoked with per-athlete HRV, declared conditions, declared
medications, or tagged context. See `docs/12_operational_agents.md` §2.

## Adding a new role

1. Create `agents/roles/<role-name>.md` with YAML front-matter:
   ```yaml
   ---
   name: content-writer
   model: openai/gpt-4o
   corpora: [voice_samples, brand_guide]
   ---
   ```
2. Below the front-matter, write the system prompt: who the agent is, what it produces,
   constraints, output format.
3. Test: `echo "test prompt" | agents/scripts/agent.sh <role-name>`.

## Available models

The harness routes through `copilot --model <id>`. Use Copilot's model IDs (NOT GitHub
Models / OpenAI namespace IDs):

| Use case | Recommended model | Tier |
|---|---|---|
| Routine drafting (content, outreach, briefs) | `claude-sonnet-4.6` | standard |
| Fast / cheap (Voice Calibrator, classification) | `claude-haiku-4.5` or `gpt-5-mini` | fast |
| Deep synthesis (interview synthesis, strategy) | `claude-opus-4.7` | premium |
| Code generation (Engineering Agent, Spec Writer) | `gpt-5.3-codex` or `gpt-5.2-codex` | standard |
| GPT alternative for variety / fallback | `gpt-5.4` | standard |

Default if a role omits `model:` in front-matter: copilot CLI picks its current default.

## Voice corpus

`corpora/voice_samples.md` is built incrementally by the founder. Initial seeding requires
3–5 short technical essays (see `agent-voice-seed` todo). The Voice Calibrator agent
references this corpus to flag register drift.
