# Operational Agent Framework

*Companion to `03_agent_framework.md`. That document defines **validation agents** — adversarial critics invoked rarely at high-stakes decision points. This document defines **operational agents** — execution partners invoked daily on routine work. They are different systems and stay distinct.*

---

## 1. The constraint and the goal

Single founder, ~15 hours/week realistic capacity (evenings + weekends). The execution plan as written assumes more than that. Without a force multiplier, exp-4 alone consumes the entire weekly budget at 25 athletes; the parallel work (LP, traffic, Garmin outreach, advisory recruitment, content) does not fit.

The goal of this system is **not to do more work**. It is to keep the founder doing only the work that requires the founder, and to delegate everything else to agents with appropriate boundaries.

Three task buckets, mapped to delegation pattern:

| Bucket | Delegation pattern | Founder time per task |
|---|---|---|
| **Founder-only** — brand/positioning judgment, 1:1 trust conversations, athlete-facing personal delivery (Loom recordings), final approvals on legal/medical, partnership decisions, hires | Founder does it; no agent involvement | 100% |
| **Founder-with-draft** — outreach emails, blog posts, screener question wording, Loom *scripts*, proposal language | Agent drafts in founder voice; founder edits and ships | ~10% |
| **Agent-led with founder triage** — research, candidate target lists, data tabulation, competitor monitoring, code generation against precise specs, scheduled metric digests | Agent does it; founder reads a 1-paragraph summary and approves | ~2% |

The system's primary design problem is correctly classifying every task into one of these buckets and routing it. Mis-classification kills trust (athletes get LLM-generated Weekly Reviews) or kills time (founder edits blog posts that didn't need editing).

**Realistic multiplication target:** with the system fully deployed, ~15 hours of founder time should produce the output of ~50–60 hours of unaided work. That's a 3–4× multiplier, not 10×. The bottleneck remains founder review and decision time, not agent throughput.

---

## 2. Architecture: two tiers

Athlete sensitive data (HRV, declared conditions, declared medications, tagged context — all GDPR Article 9 / CCPA SPI per W6) must not flow through cloud LLM APIs. This rule is not negotiable; it is implied by K-W5-3 (hard rule) and K-W6-4.

### Local tier

- **Runtime:** Ollama or LM Studio on the founder's development machine. Target hardware: M-series Mac with ≥32GB RAM, or Linux workstation with ≥24GB discrete GPU VRAM.
- **Models:** Llama 3.1 8B and Qwen 2.5 14B as the working set; Codestral 22B for code-adjacent local work. The 70B tier is not pursued — it is not required for the operational agent set and the hardware cost is unjustifiable pre-validation.
- **Cost:** zero marginal per call; real cost is hardware + electricity (negligible) and inference latency.
- **Quality:** meaningfully weaker than Claude/GPT-4-class on creative writing, comparable on structured tasks (extraction, classification, computation).
- **Used by:** any agent that processes per-athlete data.

#### Hardware acquisition is gated on validation

The local tier is **not required before exp-1 + exp-2 pass their gates.** The founder's current dev machine (32GB RAM, integrated GPU only) can run Llama 3.1 8B comfortably and Qwen 2.5 14B slowly — sufficient for any limited local-tier work that arises pre-validation, though no Phase-3 agent (Workout Analyst, Weekly Review Drafter) fires until the cohort exists.

Workstation hardware purchase (or upgrade) is conditional: triggered only if exp-1 and exp-2 both pass and Phase 3 (concierge cohort) is greenlit. If validation fails, no hardware is spent. This is logged as **K-W7-5** in the kill-criteria register.

### Cloud tier

- **Runtime:** Claude API (Sonnet/Opus) or GPT API.
- **Models:** Claude Sonnet for most tasks; Opus for complex synthesis; GPT for variety / fallback.
- **Cost:** ~$50–200/month at fully deployed scale.
- **Quality:** frontier-class.
- **Used by:** any agent NOT touching per-athlete sensitive data.

### Boundary rules

- **Athlete data never leaves the local tier.** No exceptions. This includes: HR streams, HRV, declared conditions, declared medications, tagged context, GPS, pace/power data tied to athlete_id.
- **Aggregated, de-identified summaries** can flow to cloud (e.g., "median Adherence across cohort: 82%"). Per-athlete data cannot.
- **Interview transcripts** (exp-2) stay local. Founder produces a sanitized summary that goes to cloud for synthesis.
- **Code** containing real athlete data examples must be scrubbed before any cloud agent reviews it. Synthetic examples only.

### Persistent state

This repository is the source of truth. Agents read W1–W11 docs as context; outputs are written to:

- `briefings/YYYY-MM-DD.md` — daily founder briefs.
- `marketing/` — content artifacts (LP copy, essays, outreach drafts).
- `concierge/cohort-N/athletes/` — per-athlete state (local-only directory, gitignored).
- `concierge/cohort-N/weekly/` — Weekly Review scripts (local-only, gitignored).
- `_platform_risk_log.md` — competitor + platform monitor appends.
- `_assumption_register.md`, `_kill_criteria.md` — agents read; founder writes.

No external orchestration platform. CLI scripts + cron + this repo are sufficient through MVP.

---

## 3. Voice posture for a non-writer founder

The founder is an engineer, not a writer. The pretense of literary voice would be inauthentic and would fail under the load of weekly content production. Two adjustments:

### Adjustment A — adopt "engineer-honest" as the brand voice

Direct. Precise. Uses concrete numbers. No "unlock your potential," no "transform your training," no exclamation marks. Comfortable with technical specificity ("5-minute Loom on your last 7 days, with the Five metrics computed") rather than emotional appeals.

This voice fits the brand strategy from W1/W5 better than the lightly-literary register I drafted earlier. The Convert ICP includes engineers, scientists, and analysts who respond to this register. It is also differentiated from the influencer-dominated training-content space, where everything is high-energy / high-claim.

The LP copy v1 should be revised toward this register before exp-1 launches. (Tracked as a follow-up todo: `exec-lp-copy-v2-engineer-voice`.)

### Adjustment B — voice corpus is built incrementally, not pre-loaded

Founder writes 3–5 short pieces in the first 2 weeks (technical posts on aerobic-base training topics, drawing on personal experience). These seed `voice_samples.md`. Content Writer agent then generates new content with this corpus as the few-shot reference.

The corpus refreshes every 2 months: founder picks the 3 best founder-edited drafts of the period and adds them. The corpus stays small (≤15 samples) and curated.

A **Voice Calibrator** agent runs on every Content Writer / Outreach Drafter output, checking against the corpus for register drift; flags anything that has slipped toward generic LLM voice.

---

## 4. Engineering posture: agents lead, founder reviews

This posture is feasible because the founder is a senior engineer who can read code critically, and because LLM coding agents (Claude Code, Cursor, Aider) are now genuinely competent at structured implementation tasks. The risk profile shifts from "agent might write bad code" (low — code review catches it) to "spec might be incomplete" (high — bad spec produces plausible-looking wrong code).

This shift demands three additions:

### Spec Writer agent (cloud)

Upstream of any Engineering Agent task, a Spec Writer takes a feature description and produces:

- Acceptance criteria (numbered, testable).
- Test cases (happy path + edge cases + W6 compliance scenarios).
- Data model changes (schema diff with RLS policies).
- API surface (endpoints, request/response shapes, auth requirements).
- W6 compliance checklist (RLS, audit logging, encryption boundaries, sensitive-field handling).
- Out-of-scope notes (what this task does NOT do).

Spec is reviewed by founder before Engineering Agent starts. A 30-minute spec review prevents 4 hours of wrong-direction code.

### Engineering Agent (cloud, no real data)

Implements against spec. Produces PRs with test coverage. Operates on synthetic data only. Forbidden from reading or writing files containing real athlete data; this is enforced by directory boundaries (Engineering Agent works on `app/` and `tests/`; never touches `concierge/`).

### Security Reviewer (local for sensitive-handling code, cloud otherwise)

Runs on every PR. Inputs: PR diff + W6 spec. Output: pass/fail + issues list. Specifically checks:

- RLS policy presence on new tables.
- Audit log emission on sensitive-field reads.
- Encryption boundary correctness on new sensitive fields.
- No plaintext sensitive data in logs, error messages, or analytics events.
- Webhook validation present on new external entry points.

Combined with mechanical CI (linters, type checkers, dependency scanners), the founder's manual review can focus on logic and architecture rather than mechanical correctness.

---

## 5. Agent inventory

Each entry: scope / inputs / outputs / founder-in-the-loop gate / tier / runtime trigger / phase.

### Always-on (cross-cutting)

| Agent | Tier | Trigger | Founder gate |
|---|---|---|---|
| **Founder Brief** — daily 10-min digest of metrics, kill-criteria signals, calendar, blocked todos, ready todos. Output: 1-page brief at `briefings/YYYY-MM-DD.md`. | Cloud (operational metadata only) | Cron, 7am | Read; decide what to ship today. |
| **Competitor & Platform Monitor** — weekly scan of TrainingPeaks blog, Strava dev blog, Intervals.icu changelog, Garmin Health API news, key endurance-training podcast feeds. | Cloud (public web data) | Cron, Monday 6am | Read; act on flagged items. Appends to `_platform_risk_log.md`. |
| **Risk Watcher** — daily metric scan against kill-criteria thresholds. Alarms when within 20% of any threshold. | Local (touches funnel data; aggregate-only could be cloud, but conservative default) | Cron, hourly | Review alarms in Founder Brief. |
| **Voice Calibrator** — runs on every Content Writer / Outreach Drafter output. Flags register drift. | Cloud | Triggered on agent output | Auto-flags drift; founder reviews flagged items. |

### Phase 1–2 (LP demand + interviews)

| Agent | Tier | Trigger | Founder gate |
|---|---|---|---|
| **Content Writer** — drafts essays, LP variants, screener question wording, social posts. Inputs: brand brief, voice corpus, source material. | Cloud | On-demand (chat) | Edit + ship. |
| **Research Scout** — finds podcast targets, MAF Strava clubs, Convert-shaped communities, candidate creator partners. Outputs ranked target list. | Cloud | On-demand | Pick from list. |
| **Outreach Drafter** — drafts cold emails, follow-ups, sponsorship pitches. Outputs 3 register variants per target. | Cloud | On-demand | Pick + edit + send. |
| **Interview Synthesizer** — given exp-2 interview *summary* (founder-sanitized; raw transcript stays local), scores wedge resonance, extracts quotes, identifies objections. | Local for raw transcript; cloud for sanitized summary | Post-interview | Read; aggregate at exp-2 gate. |

### Phase 3 (exp-4 concierge) — the highest-leverage agents in the system

| Agent | Tier | Trigger | Founder gate |
|---|---|---|---|
| **Workout Analyst** — given a week of activity data per athlete, computes Real Easy Minutes, Drift, Aerobic Trend, weekly distribution; flags anomalies (sudden HR drift, missed week, declared-condition signals); produces structured per-athlete weekly card. | **Local (mandatory)** | Cron, Friday 8pm per athlete | Review card; override any computation; this is the founder's primary athlete-touching surface. |
| **Weekly Review Drafter** — given Workout Analyst card + athlete history + W5 wording library, drafts the 5-min Loom script. | **Local (mandatory)** | Cron, Friday 9pm per athlete | Read script; record Loom in founder voice; never ship without reading. |
| **Cohort Tracker** — weekly aggregate across all exp-4 athletes. De-identified at the per-athlete card level before aggregation. | Local for ingestion; cloud for synthesis | Cron, Sunday 8pm | Read; pivot/persist memo at weeks 4/8/12. |

### Phase 4–5 (Garmin + pre-MVP artifacts)

| Agent | Tier | Trigger | Founder gate |
|---|---|---|---|
| **Partnership Researcher** — maps Garmin Health API + Connect IQ paths, identifies contacts, drafts pitch deck content. | Cloud | On-demand | Send actual outreach. |
| **Legal & Policy Drafter** — drafts Privacy Policy, ToS, response templates from W5/W6/W7 specs. | Cloud | On-demand | Counsel approves; founder ships. |
| **Advisor & Vendor Sourcer** — finds candidate sports-medicine physicians, pen-test vendors, insurance brokers; drafts initial outreach. | Cloud | On-demand | Founder picks + sends. |

### Phase 6–7 (MVP build + launch)

| Agent | Tier | Trigger | Founder gate |
|---|---|---|---|
| **Spec Writer** — feature description → acceptance criteria, test cases, data model, W6 checklist. | Cloud | On-demand | Founder reviews spec before Engineering Agent starts. **High-leverage gate.** |
| **Engineering Agent** — implements against spec. Operates on `app/` and `tests/` only; cannot read `concierge/`. | Cloud (synthetic data only) | Founder-initiated session (Claude Code) | Code review + manual test before merge. |
| **Security Reviewer** — RLS, audit, encryption, sensitive-field-leak checks on every PR. | Local for sensitive-handling code; cloud otherwise | PR-triggered | Address issues before merge. |
| **Support Drafter** — classifies inbound user emails, drafts responses in founder voice, escalates trust/medical/legal items. | **Local (mandatory)** when message contains user data; cloud for FAQ-class | Scheduled inbox poll, post-launch | Read + send. |

---

## 6. Weekly cadence

| Day | Mode | Activities |
|---|---|---|
| **Mon** | Triage (1 hr) | Read Founder Brief + Risk Watcher alarms + Competitor Monitor digest. Update SQL todo status. Decide week's priorities. |
| **Tue–Wed** | Founder-only work (~6 hr total) | 1:1 conversations, athlete relationships, partnership calls, hires, decisions. No drafting, no research — that's batched. |
| **Thu** | Batched drafting + agent runs (~3 hr) | Run Content Writer, Outreach Drafter, Research Scout, Spec Writer queue. Outputs land in `marketing/` and `specs/` for Friday review. |
| **Fri** | Review + ship (~3 hr) | Edit drafts; send; review specs; trigger overnight Engineering Agent runs. Workout Analyst auto-runs at 8pm. |
| **Sat–Sun** | Concierge delivery (~2–3 hr) | Read Weekly Review scripts; record Looms; send. The personal work that cannot be delegated. |

Total: ~15 hr/week. Fits realistic capacity. The cadence is *deliberate isolation of focus modes* — not constant context-switching between drafting, deciding, and reviewing.

---

## 7. Risks of agentic delegation

| Risk | Probability | Mitigation |
|---|---|---|
| **Trust drift** — athletes get LLM-generated Weekly Reviews | High if unmitigated | Weekly Review Drafter outputs *scripts only*. Founder always records Loom. Voice + face = relationship. |
| **Brand voice drift** — drafted content averages toward generic LLM voice over time | Medium | Voice Calibrator on every output; quarterly corpus refresh; founder rewrites flagged items. |
| **Spec drift** — Engineering Agent works against incomplete specs, produces plausible-wrong code | High in early sessions | Spec Writer is mandatory upstream. Founder reviews spec before agent starts. |
| **Audit posture (W6 violation)** — sensitive athlete data leaks to cloud LLM APIs | High consequence, low probability with mitigations | Tier boundaries enforced by directory structure + agent role definitions; audit log on all agent calls; quarterly audit review. |
| **Local model quality gap** — Workout Analyst / Weekly Review Drafter outputs weaker than founder-only would produce | Real | Founder always reviews + edits before shipping. Local quality is "rough draft" tier; founder finalization closes the gap. |
| **Cost** — API spend grows with usage | Low | $50–200/mo budget cap; per-agent monthly limits. |
| **Meta-risk** — founder spends evenings polishing agent infra instead of validating wedge | **High** | Rollout in priority order (§9). Agent system is built only when current bottleneck demands it. Resist pre-emptive infra. |
| **Single point of failure** — Claude/GPT outage or pricing change | Medium | Local tier provides graceful degradation for cloud-tier agents. Cloud agents have fallback prompts that can run on local model with reduced quality. |
| **Founder review fatigue** — too many agent outputs to review = founder approves without reading | High at scale | Hard cap on agent throughput (Workout Analyst runs Friday only; Content Writer rate-limited to 3 drafts/day for review). |

---

## 8. Audit and trust posture

Per W6, the system maintains:

- **Agent call log** — every agent invocation logged with: agent, timestamp, input hash, output hash, tier (local/cloud), duration. Stored at `agent_audit/YYYY-MM/log.jsonl`.
- **Sensitive-field access log** — separate from the agent call log. Any agent that reads sensitive fields (HRV, declared conditions, etc.) emits a record with field-name granularity per W6 §2b.
- **Tier boundary enforcement** — directory permissions: cloud-tier agents have read access to `app/`, `marketing/`, `docs/`, never `concierge/`. Local-tier agents have read access to `concierge/`, no network egress to cloud LLM endpoints during sensitive-data sessions.
- **Quarterly audit review** — founder reads the agent call log monthly; counsel-level audit at MVP launch and annually.

Athletes are told (in concierge consent + post-MVP privacy policy) that automated tooling assists in computing metrics and drafting Weekly Reviews, that no data leaves their local context, and that the founder reviews every output before it reaches them. This disclosure is part of the trust posture, not a footnote.

---

## 9. Rollout order

Build agents in this order. **Stop adding agents the moment we are not capacity-constrained on the next phase.** The meta-risk (§7) is the worst possible failure mode of this initiative.

### Pre-validation (cloud-only — exp-1 and exp-2)

No local-tier setup needed. Current dev machine is sufficient for any incidental local work. Cloud agents handle all pre-validation drafting, research, and synthesis.

| Order | Agent | Tier | Phase served | Effort |
|---|---|---|---|---|
| 1 | **Cloud LLM harness** (Claude API key, thin CLI wrapper, role+corpus loader) | Cloud | Foundation | 1 evening |
| 2 | **Voice corpus seeding** (3–5 founder essays) | Cloud | Foundation | 3–5 evenings |
| 3 | **Founder Brief + Content Writer + Voice Calibrator** | Cloud | Daily ops + Phase 1 | 2 evenings |
| 4 | **Outreach Drafter + Research Scout** | Cloud | Phase 1–2 | 2 evenings |
| 5 | **Interview Synthesizer** (transcripts sanitized by founder before cloud synthesis) | Cloud | Phase 2 | 1 evening |

Total pre-validation setup: ~10 evenings, ~30 hours. Funds and unblocks exp-1 and exp-2 at full velocity without hardware spend.

### Post-validation (local tier added — exp-4 and beyond)

Triggered only if exp-1 + exp-2 pass. Workstation purchase happens here.

| Order | Agent | Tier | Phase served | Effort |
|---|---|---|---|---|
| 6 | **Local-tier setup** (Ollama + Llama 3.1 8B + Qwen 2.5 14B + harness) on new hardware | Local | Phase 3 prerequisite | 1 evening (post hardware arrival) |
| 7 | **Workout Analyst (local)** | Local | Phase 3 — single highest-leverage agent | 5–8 evenings |
| 8 | **Weekly Review Drafter (local)** | Local | Phase 3 | 3–5 evenings |
| 9 | **Cohort Tracker** | Cloud (de-identified aggregates only) | Phase 3 | 2 evenings |
| 10 | **Risk Watcher + Competitor Monitor** | Cloud | Cross-cutting | 2 evenings |
| 11 | **Partnership Researcher + Legal Drafter + Advisor Sourcer** | Cloud | Phase 4–5 | 1 evening each |
| 12 | **Spec Writer + Engineering Agent + Security Reviewer** | Cloud / Local (security-sensitive code review) | Phase 6 | Configured during MVP build kickoff |
| 13 | **Support Drafter (local)** | Local | Phase 7 | Post-launch |

---

## 10. Bootstrap todos

Adding to the SQL todos table:

- `agent-local-setup` — install Ollama, pull Llama 3.1 8B + Qwen 2.5 14B, write simple harness script that takes a prompt + role + corpus and returns output.
- `agent-voice-seed` — founder writes 3–5 short technical pieces on aerobic-base training to seed `voice_samples.md`.
- `agent-founder-brief` — implement daily brief.
- `agent-content-writer` — implement; integrate Voice Calibrator.
- `agent-research-scout` — implement.
- `agent-outreach-drafter` — implement.
- `agent-interview-synthesizer` — implement (deferred to Phase 2).
- `agent-workout-analyst` — implement (local-only); spec must be precise given local model quality.
- `agent-weekly-review-drafter` — implement (local-only).
- `agent-cohort-tracker` — implement.
- `agent-risk-watcher` — implement.
- `agent-competitor-monitor` — implement.
- `agent-partnership-researcher` — implement (deferred to Phase 4).
- `agent-legal-drafter` — implement (deferred to Phase 5).
- `agent-advisor-sourcer` — implement (deferred to Phase 5).
- `agent-spec-writer` — implement (deferred to Phase 6 kickoff).
- `agent-engineering` — configure (deferred to Phase 6 kickoff).
- `agent-security-reviewer` — implement (deferred to Phase 6).
- `agent-support-drafter` — implement (deferred to Phase 7).

Dependencies: all agents depend on `agent-local-setup` and `agent-voice-seed`. Workout Analyst and Weekly Review Drafter additionally depend on a working concierge data ingestion pipeline (which is itself part of `exec-exp4-stack`).

---

## 11. What this system is and is not

**It is** a deliberate operational scaffold for a single founder to ship work that would otherwise require a small team. It accepts honest tradeoffs (local model quality < frontier; founder review remains the throughput cap) in exchange for fitting realistic capacity.

**It is not** an attempt to automate the founder out of the business. The founder remains the point of judgment on every athlete relationship, every brand decision, every claim, every legal artifact, every architecture choice, and every kill-criteria evaluation. Agents do drafting, research, computation, and structured implementation. Founder does deciding, shipping, and the personal delivery that the brand depends on.

**It is not** a replacement for the validation framework. The validation agents (`03_agent_framework.md`) remain the adversarial-critique layer for high-stakes decisions. They are invoked rarely, deliberately, and produce workshop documents. The operational agents are routine.

The two systems are independent. Both are needed.
