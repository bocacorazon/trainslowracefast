# Kill Criteria Ledger

Numeric thresholds that should stop drift and force honest decisions.

## Action legend

- `KILL` — stop the business thesis or stop the current beachhead
- `PIVOT` — keep the category but change framing, ICP, or product wedge
- `PROCEED` — continue with the current path

---

## Workshop 1 — Demand & Positioning

| ID | Criterion | Threshold | Trigger action | Source | Owner |
|---|---|---|---|---|---|
| K1 | Interviewed target athletes who describe the problem as urgent enough to seek a dedicated solution | < 30% of first 20 interviews -> KILL; 30-50% -> PIVOT framing; > 50% -> PROCEED | KILL / PIVOT / PROCEED | exp-1 founder interviews | W1 |
| K2 | Willingness to pay at target price | < 25% of first 20 qualified interviews accept $12/month conceptually -> KILL | KILL / PROCEED | exp-1 founder interviews | W1 |
| K3 | Landing-page conversion from targeted traffic to email capture | < 2% -> KILL; 2-4% -> PIVOT messaging; > 4% -> PROCEED | KILL / PIVOT / PROCEED | exp-2 LP test | W1 |
| K4 | Email-capture → $5 deposit / pre-order conversion | < 5% -> KILL; > 5% -> PROCEED | KILL / PROCEED | exp-3 deposit test | W1 |
| K5 | Concierge weekly-review pilot 6-week retention (read + engage with review) | < 60% -> KILL; 60-75% -> PIVOT product loop; > 75% -> PROCEED | KILL / PIVOT / PROCEED | exp-4 concierge pilot | W1 / W2 |
| K-W1-5 | Concierge (exp-4) shows behavior change but auto-version cohort retention drops sharply | If concierge produces change AND first auto-cohort 8-week retention < 50% -> product is not a SaaS; reopen W1 ICP and W4 economics | REOPEN | Concierge pilot vs. first paid cohort | W1 / W2 / W4 |
| K-W1-6 | Cost-per-email on cold paid traffic in exp-2 | > $4/email -> blended CAC ≤ $40 not credible; rework W4 economics | REWORK | exp-2 paid CPL tracking | W1 / W4 |
| K-W1-7 | Median WTP from exp-1 interviews | Median < $8/mo -> $12/mo unsupported; revisit pricing in W4 | REPRICE | exp-1 WTP probe | W1 / W4 |

## Workshop 2 — Behavior & Product Wedge

| ID | Criterion | Threshold | Trigger action | Source | Owner |
|---|---|---|---|---|---|
| K-W2-1 | exp-2 LP variant comparison: loop-led messaging vs. data-led messaging on email capture | Loop-led must beat data-led by ≥30% relative; if not -> wedge not legible to cold traffic, reconsider brand frame | REOPEN W1 framing | exp-2 LP A/B | W1 / W2 |
| K-W2-2 | Cap-setting wizard drop-off in exp-4 (start setup → complete a calibration) | > 25% drop-off -> onboarding unviable; redesign cap protocol before exp-2 scale-up | REWORK | exp-4 instrumentation | W2 / W5 |
| K-W2-3 | Recovery-delta wording produces user-reported clinical harm or causes a user to skip prescribed medical care | Any verified incident -> kill feature pending W5/W6 policy | KILL FEATURE | exp-4 + post-launch monitoring | W2 / W5 / W6 |
| K-W2-4 | Activation rate: signup → first complete Weekly Review (≥3 sessions, cap set) within 7 days | < 60% -> halt LP scale-up until onboarding fixed | HALT GTM | exp-4 + first-100 signups | W2 / W4 |
| K-W2-5 | Auto-handoff cohort 8-week retention vs. concierge cohort retention (exp-4 weeks 5–13) | Auto < 50% of concierge -> SaaS thesis broken | PIVOT or KILL | exp-4 auto-handoff cohort | W2 / W4 |

## Workshop 3 — Integrations & Ecosystem

| ID | Criterion | Threshold | Trigger action | Source | Owner |
|---|---|---|---|---|---|
| K-W3-1 | exp-2 LP — device-named copy vs. Strava-first copy on email capture; combined with Strava OAuth completion in exp-4 | If device-named beats Strava-first by ≥40% relative *and* Strava OAuth completion <70% -> revisit framing | REVISIT | exp-2 + exp-4 | W1 / W3 |
| K-W3-2 | Strava TOS change restricting derived weekly summaries to athletes' own data via email or web | Any such change -> P0; accelerate Garmin-direct timeline; consider feature-set pivot | P0 INCIDENT | Quarterly TOS review | W6 / Trust |
| K-W3-3 | Cross-athlete data leak via any provider integration (e.g., shared club misconfiguration) | Any verified incident -> kill the integration immediately, disclose, run incident response | KILL INTEGRATION | Production monitoring | W6 / Trust |
| K-W3-4 | Garmin Activity API partnership pricing | Required minimum >$25k/year or revenue share >5% -> defer Garmin direct to year 2; do not gate MVP | DEFER | Partnership conversation | Founder / W4 |

## Workshop 4 — Pricing, Retention & Distribution Economics

| ID | Criterion | Threshold | Trigger action | Source | Owner |
|---|---|---|---|---|---|
| K-W4-1 | Creator-channel conversion rate (visit → paid) on aligned creators within 90 days | < 1.5% -> rework channel mix toward organic + community | REWORK CHANNELS | Creator partnership tracking | W4 |
| K-W4-2 | Creator + organic + community share of signups by month 6 | < 65% -> channel mix broken; reassess paid scaling and CAC ceiling | REASSESS | Monthly cohort attribution | W4 |
| K-W4-3 | Provisional-cap activation rate vs. calibration-test activation rate | Provisional ≥10pp below calibration -> provisional path degrading the loop; rework | REWORK | exp-4 matched cohort | W2 / W4 |
| K-W4-4 | Y1 unplanned engineering spend (Garmin overruns, parser rewrites, coach view scope creep) | > $60k -> pause features, revisit Y2 plan; no capital raise to cover scope creep | PAUSE FEATURES | Y1 engineering ledger | Founder |
| K-W4-5 | M3 paid retention in first 200 paid cohort | < 45% -> unit economics broken; revisit W2 loop and W5 metric set before scaling | HALT SCALING | First 200 paid cohort | W2 / W4 / W5 |

## Workshop 5 — Science, Safety & Coaching Credibility

| ID | Criterion | Threshold | Trigger action | Source | Owner |
|---|---|---|---|---|---|
| K-W5-1 | Streak threshold (Real Easy Minutes ≥ 80%) on routes athletes didn't choose to make hard | > 50% of athletes break streak weekly on neutral routes -> threshold mis-calibrated | REWORK THRESHOLD | exp-4 first 4 weeks | W2 / W5 |
| K-W5-2 | Creator partnerships ending in Y1 due to prohibited-claim violations | ≥ 3 in Y1 -> claims policy unenforceable at scale; rethink brand-channel strategy | RETHINK STRATEGY | Partnership monitoring | W4 / W5 |
| K-W5-3 (hard rule) | Tagged-context, HRV, or declared medical condition data used outside the athlete's own account without opt-in | Any incident -> P0 breach; disclose and remediate. Must never happen. | P0 BREACH | Production audit | W5 / W6 |

## Workshop 6 — Trust, Privacy & Platform Dependency

| ID | Criterion | Threshold | Trigger action | Source | Owner |
|---|---|---|---|---|---|
| K-W6-1 (hard rule) | Cross-athlete data leak (athlete A's data visible to athlete B's account) | Any verified incident -> P0; public disclosure; root cause + remediation within 72 hours | P0 INCIDENT | Production monitoring + audit log | W6 |
| K-W6-2 | Pen-test critical (P0/P1) vulnerability remediation timeline | Any P0/P1 finding requiring > 14 days to remediate -> halt new feature work; consider delaying MVP launch if pre-launch | HALT FEATURES | Pen-test report | W6 |
| K-W6-3 | Subprocessor material data-handling change (e.g., training AI on customer data, residency change) | Any -> suspend sensitive-data writes within 7 days; replace within 30 days or escalate | SUSPEND / REPLACE | Subprocessor monitoring | W3 / W6 |
| K-W6-4 | Sensitive-field reads outside documented surfaces (self / granted-coach / founder-concierge during exp-4) | Any audit-detected occurrence -> P0 (same handling as K-W5-3) | P0 BREACH | Audit log review | W5 / W6 |
| K-W6-5 | DSAR (data export / deletion) request volume sustained over 3 months | > 1% MAU sustained -> investigate brand-trust root cause; do not normalize | INVESTIGATE | Privacy-ops metrics | W6 |

## Workshop 7 — Red Team & Go / No-Go (gates and post-launch)

| ID | Criterion | Threshold | Trigger action | Source | Owner |
|---|---|---|---|---|---|
| K-W7-1 | Any one of the three pre-MVP gates (exp-1 LP, exp-2 interview, exp-4 concierge) fails its threshold | Any failure | DO NOT deploy founder runway to MVP build; review or kill | Stage gates | W7 |
| K-W7-2 | Y1 revenue tracking | < $40k ARR at month 9 post-launch | Founder transitions to part-time or initiates wind-down; do NOT extend personal runway past month-18 model | Monthly revenue review | W7 |
| K-W7-3 | MVP+90 5-metric checkpoint (M3 retention ≥60%, LP→activation ≥4%, CAC ≤$30, platform stability, ARR ≥$40k) | 0 or 1 of 5 pass | Execute wind-down playbook (Scenario 10) | MVP+90 review | W7 |
| K-W7-4 | Compound platform failure: Strava cutoff AND Garmin direct denied/cut | Both events at any time post-launch | Reassess viability within 30 days; explicit founder-led continue / scope-down / wind-down decision | Platform monitoring | W7 |
| K-W7-5 | Local-tier hardware spend gate | Workstation hardware purchase (M-series Mac ≥32GB or Linux ≥24GB GPU) is forbidden until exp-1 AND exp-2 both pass their respective gates | HOLD spend / PROCEED on hardware after both pass | Founder discipline | W7 |

---

## Workshop 2 — Behavior & Product Wedge

| ID | Criterion | Threshold | Trigger action | Source | Owner |
|---|---|---|---|---|---|
| K-W2-1 | Users who say the proposed adherence review would change how they train next week | < 40% of first 15 qualified prototype reviews -> PIVOT wedge | PIVOT / PROCEED | Prototype interviews | W2 |
| K-W2-2 | Users who prefer a broad all-purpose log over the focused adherence product after seeing both concepts | > 70% -> KILL focused product thesis | KILL / PROCEED | Concept test | W2 |
| K-W2-3 | Weekly return intent among concierge users after 4 weeks | < 50% -> KILL or PIVOT product loop | KILL / PIVOT / PROCEED | Concierge pilot | W2 |

---

## Workshop 3 — Integrations & Ecosystem

| ID | Criterion | Threshold | Trigger action | Source | Owner |
|---|---|---|---|---|---|
| K-W3-1 | Qualified prospects blocked by missing launch integrations | > 50% cite missing integrations as launch blocker -> PIVOT launch scope | PIVOT / PROCEED | Interviews / waitlist survey | W3 |
| K-W3-2 | File-import fallback acceptance among otherwise interested users | < 30% accept file import as temporary fallback -> PIVOT platform-dependency plan | PIVOT / PROCEED | Interviews / pilot onboarding | W3 |
| K-W3-3 | Coach or community channel interest in recommending the product | < 3 of first 15 channel conversations show meaningful interest -> deprioritize channel | DEPRIORITIZE | Founder outreach | W3 |

---

## Workshop 4 — Pricing, Retention & Distribution Economics

| ID | Criterion | Threshold | Trigger action | Source | Owner |
|---|---|---|---|---|---|
| K-W4-1 | Paid conversion from activated free or concierge users | < 10% -> KILL or repackage; 10-20% -> PIVOT pricing / packaging; > 20% -> PROCEED | KILL / PIVOT / PROCEED | Early cohorts | W4 |
| K-W4-2 | First 8-week retention of paid or concierge users | < 60% -> KILL or PIVOT product loop | KILL / PIVOT / PROCEED | Early cohorts | W4 |
| K-W4-3 | Blended CAC hypothesis for the beachhead | If modeled CAC cannot plausibly stay under 12 months of gross margin -> KILL | KILL / PROCEED | Economics model | W4 |

---

## Workshop 5 — Science, Safety & Coaching Credibility

| ID | Criterion | Threshold | Trigger action | Source | Owner |
|---|---|---|---|---|---|
| K-W5-1 | External credibility review from coaches / science-literate reviewers | If a majority of first 5 credible reviewers identify the product as misleading, overly simplistic, or unsafe -> PIVOT or KILL | PIVOT / KILL / PROCEED | Expert review | W5 |
| K-W5-2 | Share of core metrics that cannot be explained clearly without heavy caveats | > 50% of MVP headline metrics fail clarity review -> simplify product | SIMPLIFY / PROCEED | Workshop review | W5 |

---

## Workshop 6 — Trust, Privacy & Platform Dependency

| ID | Criterion | Threshold | Trigger action | Source | Owner |
|---|---|---|---|---|---|
| K-W6-1 | Launch-critical dependency concentration | If a single third-party dependency can disable the core user value with no credible fallback -> PIVOT architecture before launch | PIVOT | Dependency map | W6 |
| K-W6-2 | User trust response to privacy and data-rights posture | If a majority of first 10 privacy-sensitive users still describe the posture as unclear or weak -> do not launch paid sync product | HOLD / PROCEED | User review | W6 |

---

## Workshop 7 — Red Team & Go / No-Go

| ID | Criterion | Threshold | Trigger action | Source | Owner |
|---|---|---|---|---|---|
| K-W7-1 | Residual existential risks after workshops 1-6 | > 3 unresolved existential risks with no owner and no near-term closure path -> KILL or HOLD | KILL / HOLD / PROCEED | Founder synthesis | W7 |
| K-W7-2 | Feature-vs-business verdict | If the red team concludes the product is more likely to be a feature inside an incumbent than a standalone business, and no defensible moat remains -> KILL or radically narrow | KILL / PIVOT / PROCEED | Red-team memo | W7 |
