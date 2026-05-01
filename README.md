# trainslowracefast

Planning repository for an aerobic-first endurance training SaaS.

The current phase is business validation. The goal is to determine whether there is a durable business around helping athletes execute low-intensity-first training methods more consistently.

## Directory structure

- `docs/01_initial_feasibility_discussion.md`: initial market thesis, wedge, beachhead, MVP, and core risks
- `docs/02_feasibility_validation.md`: workshop-based validation sequence
- `docs/03_agent_framework.md`: advisor-agent framework tailored to this category
- `docs/04_demand_positioning_workshop.md`: Workshop 1 — locks ICP (the Convert), brand frame (*Train slow, race fast*), MVP frame (*Easy days, done right*), and pricing hypothesis ($12/mo)
- `docs/05_behavior_wedge_workshop.md`: Workshop 2 — locks the behavior loop (sync → 2 h deviation digest → Sunday Weekly Review → Aerobic Trend), the 5-metric MVP set, free tier shape (3 Weekly Reviews capped), cap-setting protocol, and the extended exp-4 concierge + auto-handoff pilot
- `docs/06_integrations_workshop.md`: Workshop 3 — locks Strava + FIT as MVP integration scope, Garmin direct as Phase-1.5, web-first delivery, activity-type taxonomy (Run + Ride), and `coach_id` data model for an MVP-1 coach view
- `docs/07_economics_workshop.md`: Workshop 4 — locks pricing ($12/mo or $120/yr at 17% off), 3-review free-tier framing, channel mix targets (blended CAC $25), provisional-cap activation path, COGS stack, and Y1 milestone (1,000 paid users / $120k ARR target)
- `docs/08_science_workshop.md`: Workshop 5 — locks Adherence redefinition (Real Easy Minutes), cap confidence labels, Recovery-delta wording library, edge-case catalog, claims policy, and the "no readiness score" credibility position
- `docs/09_trust_workshop.md`: Workshop 6 — locks sensitive-data classification (HRV / conditions / meds / tagged context as Article 9 / CCPA SPI), encryption + retention policy, RLS-based isolation, coach-view consent + audit, affiliate data isolation, pause-subscription mechanics, conditions-consent surface, and the Strava chokepoint 30/90/180-day playbook
- `docs/10_red_team_workshop.md`: Workshop 7 — walks 10 attack scenarios end-to-end, ranks the failure inventory by likelihood × severity, and produces the staged go/no-go architecture (exp-1 → exp-2 → exp-4 → Garmin → MVP build → MVP launch → MVP+90 review). Conditional GO with five pre-launch artifacts and four new kill criteria.
- `docs/11_execution_plan.md`: Operational execution plan — 8 phases from Stage 1 (LP test) to MVP+90 hard checkpoint, with deliverables, gates, kill conditions, budgets, and risk handling. Total time to MVP launch ~9 months; spend before MVP build commits ~$10–15k.
- `marketing/lp_copy_v1.md`: First execution artifact — full LP copy for exp-1, including hero, three body sections, screener, footer, design-token recommendations, hero-asset spec for freelancer brief, hero-variant set for week-2 split testing, and open founder-review questions.
- `docs/12_operational_agents.md`: Operational agent framework — companion to `03_agent_framework.md`. Defines the force-multiplier system for a single founder on ~15 hr/week capacity. Two-tier architecture (local Ollama for athlete-data-touching agents; cloud Claude/GPT for everything else). 19 agents across 4 phase groupings + cross-cutting. Engineer-honest voice posture; agents-lead / founder-reviews engineering posture. Includes audit/trust controls, weekly cadence, risk inventory, and rollout order.
- `docs/product_ideas/`: Versioned product-idea capture for promising directions that emerged during validation and execution work. Current ideas cover a goal-feasibility engine, a within-session adherence score, and HR spike anomaly correction.
- `agents/`: Operational agent harness. `agents/scripts/agent.sh <role>` runs a role via `copilot -p` (uses founder's Copilot subscription; no separate API budget). Roles in `agents/roles/`, corpora in `agents/corpora/` (brand guide, voice samples). See `agents/README.md`. Local-tier (Ollama) deferred per K-W7-5.
- `docs/_glossary.md`: Glossary of acronyms and jargon used across the workshops — covers training science (TSS, CTL/ATL/TSB, FTP, MAF, HRV, etc.), product/business (ICP, MVP, CAC, ARR, M3 retention, etc.), integrations (OAuth, FIT, TOS, webhooks), privacy/security (GDPR Article 9, CCPA SPI, DPA, DSAR, RLS, KMS), infrastructure, and validation-framework conventions (A-W{n}-{seq}, K-W{n}-{seq}, exp-1/2/4).
- `docs/_platform_risk_log.md`: quarterly review log for provider TOS / API changes
- `docs/_assumption_register.md`: shared hypothesis ledger across workshops
- `docs/_kill_criteria.md`: numeric thresholds for pivot / kill decisions
