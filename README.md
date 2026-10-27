# trainslowracefast

Two workstreams live in this repo:

1. **Product research & validation** (`docs/product/`) — determining whether
   there is a durable business around helping athletes execute
   low-intensity-first training methods more consistently. The current phase
   is business validation.
2. **TrainerApp software** (`core/`, `app*/`, `docs/trainerapp/`) — a working
   Kotlin Multiplatform indoor-training app: control a CycleOps Hammer H1
   trainer over BLE from Linux, ride with live power + heart-rate telemetry,
   record to FIT. Started as a personal PerfPRO Studio replacement; intended
   as the device-integration leg of the product
   (`docs/product/product_ideas/04_live_training_device_integration.md`).

Start here for the software: **`docs/trainerapp/README.md`** — program
overview, slice roadmap, hardware quirks, and the verified H1 wire protocol.

## Directory structure

### Product research (`docs/product/`)

- `01_initial_feasibility_discussion.md`: initial market thesis, wedge, beachhead, MVP, and core risks
- `02_feasibility_validation.md`: workshop-based validation sequence
- `03_agent_framework.md`: advisor-agent framework tailored to this category
- `04_demand_positioning_workshop.md`: Workshop 1 — locks ICP (the Convert), brand frame (*Train slow, race fast*), MVP frame (*Easy days, done right*), and pricing hypothesis ($12/mo)
- `05_behavior_wedge_workshop.md`: Workshop 2 — locks the behavior loop (sync → 2 h deviation digest → Sunday Weekly Review → Aerobic Trend), the 5-metric MVP set, free tier shape (3 Weekly Reviews capped), cap-setting protocol, and the extended exp-4 concierge + auto-handoff pilot
- `06_integrations_workshop.md`: Workshop 3 — locks Strava + FIT as MVP integration scope, Garmin direct as Phase-1.5, web-first delivery, activity-type taxonomy (Run + Ride), and `coach_id` data model for an MVP-1 coach view
- `07_economics_workshop.md`: Workshop 4 — locks pricing ($12/mo or $120/yr at 17% off), 3-review free-tier framing, channel mix targets (blended CAC $25), provisional-cap activation path, COGS stack, and Y1 milestone (1,000 paid users / $120k ARR target)
- `08_science_workshop.md`: Workshop 5 — locks Adherence redefinition (Real Easy Minutes), cap confidence labels, Recovery-delta wording library, edge-case catalog, claims policy, and the "no readiness score" credibility position
- `09_trust_workshop.md`: Workshop 6 — locks sensitive-data classification (HRV / conditions / meds / tagged context as Article 9 / CCPA SPI), encryption + retention policy, RLS-based isolation, coach-view consent + audit, affiliate data isolation, pause-subscription mechanics, conditions-consent surface, and the Strava chokepoint 30/90/180-day playbook
- `10_red_team_workshop.md`: Workshop 7 — walks 10 attack scenarios end-to-end, ranks the failure inventory by likelihood × severity, and produces the staged go/no-go architecture (exp-1 → exp-2 → exp-4 → Garmin → MVP build → MVP launch → MVP+90 review). Conditional GO with five pre-launch artifacts and four new kill criteria.
- `11_execution_plan.md`: operational execution plan — 8 phases from Stage 1 (LP test) to MVP+90 hard checkpoint, with deliverables, gates, kill conditions, budgets, and risk handling. Total time to MVP launch ~9 months; spend before MVP build commits ~$10–15k.
- `12_operational_agents.md`: operational agent framework — companion to `03_agent_framework.md`. Defines the force-multiplier system for a single founder on ~15 hr/week capacity. Two-tier architecture (local Ollama for athlete-data-touching agents; cloud Claude/GPT for everything else). 19 agents across 4 phase groupings + cross-cutting. Engineer-honest voice posture; agents-lead / founder-reviews engineering posture. Includes audit/trust controls, weekly cadence, risk inventory, and rollout order.
- `marketing/lp_copy_v1.md`: first execution artifact — full LP copy for exp-1, including hero, three body sections, screener, footer, design-token recommendations, hero-asset spec for freelancer brief, hero-variant set for week-2 split testing, and open founder-review questions.
- `product_ideas/`: versioned capture of promising product directions (goal-feasibility engine, within-session adherence score, HR spike anomaly correction, live training device integration).
- `_glossary.md`: glossary of acronyms and jargon used across the workshops — training science (TSS, CTL/ATL/TSB, FTP, MAF, HRV…), product/business (ICP, MVP, CAC, ARR, M3 retention…), integrations (OAuth, FIT, TOS, webhooks), privacy/security (GDPR Article 9, CCPA SPI, DPA, DSAR, RLS, KMS), and validation-framework conventions (A-W{n}-{seq}, K-W{n}-{seq}, exp-1/2/4).
- `_platform_risk_log.md`: quarterly review log for provider TOS / API changes
- `_assumption_register.md`: shared hypothesis ledger across workshops
- `_kill_criteria.md`: numeric thresholds for pivot / kill decisions
- `seed.md`, `voice.md`: brand seed and voice guide (corpora sources for the agents)

### TrainerApp software

- `docs/trainerapp/`: program home — **README.md** (overview, slice roadmap,
  hardware + protocol), **ANALYSIS.md** (imported origin feasibility doc),
  **duck-pass-1.json** (rubber-duck pass evidence)
- `docs/increments/`: slice specs (001 skeleton → 002 ERG MVP →
  003 multi-sensor telemetry → 004 FIT recording)
- `docs/features/`: per-slice sealed designs + verification contracts
  (`design/`) and live evidence — capture notes, acceptance records, and the
  verified H1 BLE protocol record (`implementation/`)
- `core/`, `app-desktop/`, `app/`, `app-linux/`: the KMP codebase
  (`commonMain` domain, JVM BlueZ transport, Compose desktop UI, Android +
  console launchers)

## Agents

`agents/` is the operational agent harness: `agents/scripts/agent.sh <role>`
runs a role via `copilot -p` (uses the founder's Copilot subscription; no
separate API budget). Roles in `agents/roles/`, corpora in
`agents/corpora/` (brand guide, voice samples). See `agents/README.md`.
Local-tier (Ollama) deferred per K-W7-5. The framework is defined in
`docs/product/12_operational_agents.md`.