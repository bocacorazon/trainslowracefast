# Execution Plan — Stage 1 to MVP Launch

This document formalizes the staged execution plan derived from W7's go/no-go architecture. It is operational, not analytical. Each phase has a deliverable list, an explicit gate, and a kill condition.

The phases are sequential; Stage 4 (Garmin outreach) runs in parallel with exp-4. The plan assumes single founder + (eventually) one engineer, no external capital, ~$90k personal runway over 18 months.

**Total time from kickoff to MVP launch: ~9 months** (3 + 3 + 13 + 16 weeks).
**Total spend before MVP build commits: ~$10–15k** (LP, interviews, concierge tooling, advisory + counsel deposits).
**Spend deployed only after all three gates pass.** Founder runway does not deploy to MVP build until Stages 1–4 are clean.

---

## Phase 1 — exp-1: LP demand test

**Duration:** ~3 weeks. **Budget:** ~$1.5–2k. **Owner:** founder.

**Deliverables**

1. Landing page live at a primary domain (`trainslowracefast.com` or equivalent). Three sections: problem (intensity creep), wedge (Five metrics, no TSS), email capture + waitlist + 2-question intent survey ("what watch do you use?" / "have you tried zone 2 / MAF / polarized before?").
2. Tracking instrumented: Plausible (or equivalent privacy-first analytics), email capture in ConvertKit / Buttondown, UTM-tagged source attribution.
3. Three traffic sources executed: 1 sponsored podcast spot in the aerobic-training space, 1 organic post on r/AdvancedRunning + r/Velo, 1 outreach round to 3–5 MAF-method Strava clubs.
4. Booking page (Calendly) for qualified-interview-booking, gated behind a 2-question screener.

**Gate (K-W7-1):** ≥4% LP→email AND ≥10% email→qualified-interview-booking, sustained over the test window. Minimum N: ~2k visits.

**Kill condition:** if LP→email <2% or email→booking <5%, halt and revisit the wedge messaging before considering a re-test.

**Out of scope:** any pricing surface, any product mockup beyond a single hero image, any waitlist position commitment.

---

## Phase 2 — exp-2: Convert-candidate interviews

**Duration:** ~3 weeks (overlaps with the back end of Phase 1). **Budget:** ~$1k incentives. **Owner:** founder.

**Deliverables**

1. 15 booked Convert candidates from the exp-1 pool, screened for: ≥1 endurance event in the last 12 months, ≥6 months of training history, has tried OR is curious about an aerobic-first method.
2. Standardized 30-minute interview script: warm-up (5 min training history) → wedge probe (10 min on intensity creep, what they wish their current tool showed) → mockup reactions (10 min: Five-metric mockup vs TSS dashboard, Sunday Weekly Review prototype) → pre-pay ask (5 min).
3. Pre-pay flow: $50 for a guaranteed slot in the 13-week pilot, fully refundable on request, processed via Stripe Checkout (no account creation).
4. Synthesized findings document: wedge resonance score per candidate (1–5), specific quotable language, objections raised, pricing reaction, pre-pay outcome.

**Gate (K-W7-1):** ≥9 of 15 score wedge resonance ≥4, AND ≥6 of 15 pre-pay $50.

**Kill condition:** if pre-pay <4 of 15, the wedge is not the buying trigger — halt and revisit ICP or wedge before considering a re-test.

**Out of scope:** any feature roadmap promises, any commitment beyond the 13-week pilot.

---

## Phase 3 — exp-4: Concierge pilot

**Duration:** 13 weeks. **Budget:** ~$3–5k tooling + founder time. **Owner:** founder.

**Deliverables**

1. Cohort of ~25 athletes recruited from exp-2 pre-payers + exp-1 waitlist (top of qualified-interview-booking funnel).
2. Tooling stack: Calendly (intake), Notion or Airtable (athlete data), Loom (Weekly Review delivery), Postmark (email delivery), spreadsheet (Five-metric computation), Stripe (pilot fee + subscription test).
3. Manual weekly cadence per athlete: pull last-7-days data (Strava OAuth via app template OR FIT email submission), compute Adherence (Real Easy Minutes), Drift, Aerobic Trend in spreadsheet, record 5-min Loom Weekly Review, send via Postmark.
4. Concierge consent surface: explicit opt-in that founder may review training data manually, time-bounded to pilot, logged.
5. Conditions-consent surface (W6) implemented even at concierge — declared medications/conditions affect what's shown.
6. Weekly pilot review: per-athlete adherence trend, retention status, qualitative feedback log, pivot-or-persist memo at weeks 4, 8, 12.
7. Week-13 NPS survey + structured exit interview (15–20 of 25 minimum response).
8. Week-13 conversion offer: $12/mo or $120/yr Pro at MVP launch, 50% lifetime discount for pilot completers.

**Gate (K-W7-1):** median per-athlete Adherence improvement ≥60% (weeks 9–12 vs 1–4), week-13 retention ≥70% of starting cohort, NPS ≥30.

**Kill condition:** if any of the three lands materially below threshold (<50% Adherence improvement, <60% retention, NPS <15), halt MVP build commit. Either iterate concierge (Behavior §4 #2 exp-4-extended path) or kill.

**Out of scope:** any product code, any automation of the Weekly Review pipeline, any growth experiments.

---

## Phase 4 — Garmin partnership outreach (parallel with Phase 3)

**Starts:** exp-4 week 6. **Duration:** ongoing through exp-4 week 13. **Owner:** founder.

**Deliverables**

1. Garmin Health API and Connect IQ partnership tracks researched; identify the right contact path (typically via Garmin Health Developer portal + direct sales-engineering relationship).
2. Initial pitch deck: who we are, who the Convert ICP is (Garmin's installed base overlap), how our claims policy aligns with Garmin's medical-data posture, partnership ask (read API access + co-marketing path).
3. First outreach + follow-up cadence (5 contacts max, 4-week response window per contact).
4. Decision memo by exp-4 week 13: approved / in serious negotiation / denied / no response. If denied or no response → evaluate Wahoo or Coros direct integration as Phase-1.5 alternative before MVP build commits (W7 Scenario 2 mitigation).

**Gate (K-W7-4 mitigation):** Garmin approved or in serious negotiation, OR a viable alternative direct integration (Wahoo or Coros) scoped before Stage 5.

**Out of scope:** any commercial commitments, any contract signing pre-MVP. Goal is to know whether the Phase-1.5 path is real or needs a substitute.

---

## Phase 5 — Pre-MVP-launch artifacts (parallel with Phases 3 + 4)

**Starts:** exp-4 week 1. **Owner:** founder. **Budget:** ~$5–8k.

These are slow-moving and must not be left to the post-gate scramble.

**Deliverables**

1. **Medical Advisory Board.** 2–3 sports-medicine physicians or exercise physiologists recruited at $2–4k/yr total honorarium. Engagement scope: review conditions-consent surface, review claims policy quarterly, contextualizing statement availability in case of medical incident. Cold-outreach list: published authors in zone-2 / aerobic-base space + endurance-medicine clinicians + university-affiliated exercise physiology faculty.
2. **Counsel relationship.** Privacy + product-liability lawyer retained (~$2–3k initial). Drafts: Privacy Policy, Terms of Service, medical-incident response template (W7 Scenario 6), creator-overclaim response template (W7 Scenario 7), wind-down playbook (W7 Scenario 10).
3. **Pen-test vendor shortlist.** 3 vendors quoted, scope drafted (auth, RLS, webhook validation, sensitive-field encryption boundaries). Engagement deferred to MVP build phase. Budget reserve: $5–10k Y1.
4. **Cyber liability insurance quotes.** Hiscox / Chubb / Embroker or equivalent; binding deferred to MVP launch. Budget: ~$2k/yr.
5. **Subprocessor inventory + DPA collection.** AWS, Stripe, Postmark, Plausible, Cloudflare, Sentry — DPAs in hand or queued for signing at MVP build.
6. **Public Trust page draft** — subprocessor list, encryption posture, retention schedule, DSAR contact.

**Gate:** all six items either complete or in active execution by exp-4 week 13.

---

## Phase 6 — MVP build (Stage 5)

**Starts:** only after Phases 1–5 gates clean. **Duration:** ~16 weeks. **Budget:** ~$45k Y1 engineering one-time per W4 (founder + first engineer).

**Deliverables (build scope per W3 + W6)**

1. Auth + accounts (email + Google SSO).
2. Strava OAuth integration (read + activity:read_all only, never write); webhook subscriber + validation; 24h soft-delete / 30d hard-delete on revocation.
3. FIT manual upload pipeline (drag-drop + email submission).
4. Activity + route data model: provider provenance, normalized activity type, time-series stream layer, purpose-limited route-data layer, route display on athlete-owned activity detail, and derived metric runs.
5. Five MVP metrics computation engine: Adherence (Real Easy Minutes), Drift, Aerobic Trend, Recovery delta (descriptive only), Weekly intensity distribution.
6. Behavior loop: post-sync digest (≤2h), Sunday Weekly Review email + in-app, Aerobic Trend reward surface.
7. Cap-setting protocol UI (provisional / data-validated / coach-lab-validated three-state).
8. Conditions-consent surface (declared medications / cardiac conditions).
9. Free tier (capped at 3 Weekly Reviews); Stripe Checkout for $12/mo + $120/yr Pro.
10. coach_id data model from day 1; no coach UI at MVP.
11. PostgreSQL RLS on all athlete tables; envelope encryption for sensitive fields (HRV, declared conditions, declared medications, tagged context, full route traces); KMS-managed keys.
12. Audit log on all sensitive-field reads, separate audit-only database, 24-month retention.
13. One-click data export (CSV summaries + raw FIT bundle + stored route traces / provider streams where available).
14. Granular delete (workout, route trace, recovery tag, HRV history, declared conditions, account).
15. Pen-test executed and remediated before launch (gate K-W6-2).
16. Cyber liability bound; advisory board statement-availability confirmed; response templates loaded.

### MVP data model baseline

The activity schema must preserve enough context for route-aware interpretation without turning MVP into a generic analytics platform. Route display is table stakes on an activity-detail screen, but route data is more important as future signal: terrain normalization, repeated-route comparison, route-choice fairness, indoor/outdoor classification, drift-location correlation, and eventual weather/headwind context.

| Layer | Purpose | MVP fields / behavior | Future use |
|---|---|---|---|
| `activities` | Canonical workout record | athlete_id, provider, provider_activity_id, source (`strava`, `fit_upload`, later `garmin`), start_time, duration, distance, elevation_gain, sport_type_raw, sport_type_normalized, primary/supportive classification, privacy/deletion state | Deduplication, provider fallback, cross-training substitution research |
| `activity_streams` | Time-aligned physiological and performance data | time offset, HR, pace/speed, power, cadence, altitude, distance, sensor/source provenance; raw values preserved and corrections layered separately | Recompute Adherence when caps change; detect drift, HR artifacts, terrain-linked excursions |
| `route_traces` | Purpose-limited precise location layer | encoded polyline or point series, privacy-zone redaction status, route hash, start/end redaction, storage consent/version; never exposed to coaches/affiliates by default | Route display; repeated-route comparison; terrain/grade normalization; ML features from athlete-own route history |
| `route_features` | Lower-risk derived route context | grade distribution, climb segments, surface/indoor flag where known, stop density, route difficulty fingerprint | Normalize metrics for terrain; distinguish bad execution from unavoidable terrain |
| `environment_context` | External context joined by time/location | initially empty or manual notes; later weather/wind/temperature provider joins by route segment and timestamp | Explain drift clusters: heat, headwind, climb, exposed section |
| `metric_runs` | Auditable derived outputs | metric name, version, input provenance, cap version, computed_at, output JSON; never freeze Adherence permanently | Recompute metrics after cap changes, algorithm changes, route-normalization updates |
| `annotations` | Human/athlete explanations | one-tap reasons (`hills`, `heat`, `headwind`, `felt fine`, `wanted to push`), coach/founder notes, anomaly flags | Label training data for better rules and eventual first-party ML |

MVP uses route data in two constrained ways: show the athlete their own route on the activity detail view, and support simple terrain/context annotations around HR drift. First-party ML can later use route-derived features to improve normalization and advice, but raw route traces are not sent to external LLM APIs, used for third-party model training, sold, published, or exposed in social/segment-style features.

**Gate:** pen-test passed (zero P0/P1 unremediated); all hard-rule kill criteria (K-W5-3, K-W6-1, K-W6-4) instrumented for production detection; concierge cohort at week-13+ migrated to MVP and Pro-converted.

---

## Phase 7 — MVP launch + first 100 paid

**Starts:** post Phase-6 gate. **Duration:** ~90 days to checkpoint.

**Deliverables**

1. Public launch: Trust page live, Privacy Policy + ToS live, response templates locked, advisory board listed.
2. Channel mix activation per W4 targets: creator partnerships (35%), organic content (25%), community (15%), Strava clubs (10%), coach referral (10%), paid (5%).
3. First-100-paid concierge-disclosure overlay (founder may continue manual review during initial cohorts).
4. Weekly metrics dashboard: signups, activation %, M3 retention by cohort, CAC by channel, Strava integration health, sensitive-field audit summary.
5. Quarterly platform-risk review log opened (`_platform_risk_log.md`).

---

## Phase 8 — MVP+90 hard checkpoint (K-W7-3)

**Trigger:** day 90 post-launch. **Owner:** founder.

**Five metrics evaluated**

1. M3 retention ≥60% on first cohort (K-W2-2 threshold).
2. LP→activation ≥4% sustained (K-W4-1 threshold).
3. CAC ≤$30 blended (tighter than K-W4-3 kill threshold of $40 for buffer).
4. Strava integration stable; Garmin direct online or imminent.
5. Y1 ARR tracking ≥$40k (above K-W7-2 wind-down trigger).

**Decision matrix**

- 4–5 of 5 pass → continue full Y1 plan as scoped.
- 2–3 of 5 pass → scope down to conservative case (500 paid / $60k ARR Y1 target); reassess at month 9.
- 0–1 of 5 pass → execute wind-down playbook (W7 Scenario 10).

**Month-9 follow-up checkpoint** (K-W7-2): if Y1 revenue tracking <$40k ARR → founder transitions to part-time on project or initiates wind-down. Do not extend personal runway past month-18 modeled spend.

---

## Risk handling during execution

These are the live risks that need active management *during* execution, not just monitoring:

| Risk | Active management during execution |
|---|---|
| **Strava cutoff** | Monitor Strava dev-blog + TOS quarterly. FIT upload functional from MVP day 1. |
| **Garmin denial** | Phase 4 outreach starts at exp-4 week 6 — early enough to pivot to Wahoo/Coros before MVP commits. |
| **Medical incident during concierge** | Conditions-consent surface implemented even at concierge phase. Counsel-reviewed response template ready by exp-4 week 13. |
| **Creator overclaim** | Creator copy library distributed at first partnership; partnership-termination clause in every agreement. |
| **M3 retention undershoot** | exp-4 weeks 9–12 segment is the diagnostic — extend exp-4 by 8 weeks with 10 more athletes if retention lands 60–70%. |
| **Founder runway depletion** | Monthly burn review. If burn exceeds modeled $5k/mo for 2 consecutive months, cut non-essential spend. |
| **Founder bus-factor** | Wind-down playbook + legal handling instructions stored with trusted third party before MVP launch. |

---

## Operational tracking

- SQL `todos` table tracks discrete deliverables with status (pending / in_progress / done / blocked) and dependencies.
- Each phase creates a checkpoint markdown when complete, summarizing actuals vs plan.
- Assumption register and kill-criteria ledger updated as evidence accumulates.
- Weekly founder review: 1 hour, every Friday, on the trackable metrics for the active phase.
