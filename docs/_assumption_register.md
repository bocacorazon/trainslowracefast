# Assumption Register

Living list of hypotheses across workshops. Each row carries a status (`open`, `validated`, or `killed`), a pointer to evidence, and the workshop that owns resolution.

## Status legend

- `open` — assumed true for now, not yet proven
- `validated` — supported by evidence
- `killed` — contradicted by evidence and must not silently remain in the plan

---

## Workshop 1 — Demand & Positioning

| ID | Assumption | Status | Evidence / Pointer | Owner |
|---|---|---|---|---|
| A1 | Self-coached endurance athletes who believe in low-intensity methods are willing to pay for adherence and insight layered on top of existing data sources. | open | W1 §2a (Convert segment), §2d. Needs exp-1 interviews and exp-2 LP. | W1 |
| A2 | "Easy days, done right" (MVP headline) and "Train slow, race fast" (brand) are durable frames that survive zone-2 jargon shifts while converting the Convert audience. | open (refined) | W1 §2c locked frames. Needs exp-2 LP A/B test. Supersedes the prior "durable endurance" framing. | W1 |
| A3 | The pain is execution discipline, not lack of access to training data or generic charts. | open | W1 §2a, §2e. Needs exp-1 interview confirmation. | W1 / W2 |
| A4 | Runners, cyclists, and triathletes can share one brand and one core product without creating a muddled offer at MVP stage. | open | W1 §2d/§2e treats them as a single Convert audience. Needs exp-2 channel split data. | W1 |
| A5 | Athletes dissatisfied with intensity-heavy framing perceive current incumbents (especially Intervals.icu) as philosophically misaligned, not just feature-rich alternatives. | open | W1 §2b substitute analysis. Needs exp-1 + exp-5 substitute survey evidence. | W1 |
| A-W1-6 | Adherence visibility alone (no human accountability) can produce sustained behavior change in the Convert at SaaS-defensible retention rates. | open — **highest priority W2 unknown** | Behavior Strategist memo §3. exp-4 concierge pilot is the first test. | W1 / W2 |
| A-W1-7 | The Convert beachhead is large enough to support a $1M+ ARR business on its own, OR the brand can credibly expand to Performance-curious in Phase 2 without diluting the wedge. | open | Economics memo §4. Resolved by W4 sizing model and Y1 production data. | W4 / W7 |
| A-W1-8 | $12/mo Pro pricing is defensible against substitutes (Intervals.icu Supporter $4, TrainingPeaks $11.25, coach $150–400). | open | W1 §2f. exp-1 WTP probe + exp-3 deposit conversion are the first signals. | W1 / W4 |
| A-W1-9 | A free tier capped at **3 Weekly Reviews total** (Rev-2: was "1/week indefinite") converts to paid better than a time-limited full trial of Pro. | open (Rev-2) | W2 §5 #2 — Economics secondary tightened the cap; Behavior + Demand accept. Resolve in W4 packaging tests. | W4 |
| A-W1-10 | The Convert can be reached at blended CAC ≤ $40 with content + community share ≥ 65% of acquisition. | open | W1 §2d targets. exp-2 paid CPL is the first signal; full resolution in W4. | W1 / W4 |

## Workshop 2 — Behavior & Product Wedge

| ID | Assumption | Status | Evidence / Pointer | Owner |
|---|---|---|---|---|
| A-W2-1 | The 5-metric MVP set (Adherence %, Drift, Aerobic Trend, Recovery delta, Weekly distribution) is sufficient to drive behavior change without users demanding TSS / FTP / CTL add-backs. | open | W2 §2b. Tested via exp-4 qualitative + first-100 paid retention. | W2 / W4 |
| A-W2-2 | The 2-hour intensity-creep alert SLA is achievable on Strava + Garmin webhooks for ≥80% of synced workouts. | open | W2 §2b. Resolved in W3 with provider latency data. | W3 |
| A-W2-3 | Cap-setting wizard completion rate ≥75% of signups within 7 days of account creation. | open | W2 §2b cap protocol. Measured in exp-4 + first-100 instrumentation. | W2 / exp-4 |
| A-W2-4 | The auto-version of the loop produces ≥70% of the behavior change observed in the concierge cohort. | open — **gating for SaaS thesis** | W2 §2c #3, exp-4 auto-handoff cohort weeks 5–13. | W2 / W4 |
| A-W2-5 | "Recovery delta" descriptive-only language is interpretable by athletes without triggering clinical-style decisions or harm. | open | W2 §4 #2. Resolved in W5 with wording library + pilot qualitative signal. | W5 |

## Workshop 3 — Integrations & Ecosystem

| ID | Assumption | Status | Evidence / Pointer | Owner |
|---|---|---|---|---|
| A-W3-1 | Strava auto-sync covers ≥80% of Convert-owned devices (Garmin, Wahoo, Coros, Polar) with end-to-end latency ≤90 min P80. | open | W3 §2a/§2c. exp-4 latency instrumentation per W3 §6 #13 is the resolver. | exp-4 |
| A-W3-2 | Garmin Activity API partnership is approvable for our use case at $0–$25k/year. | open | W3 §2b. Resolved by partnership conversation by month 3 post-MVP. | Founder / W4 |
| A-W3-3 | Strava's API Agreement remains permissive for athlete-own derived insight delivery via email/web for the next 12 months. | open — **monitored quarterly** | W3 §4 #1. Logged in `_platform_risk_log.md`. | W6 / Trust |
| A-W3-4 | FIT manual upload UX is sufficient as a Strava-less fallback for ~15% of the Convert at acceptable activation rates. | open | W3 §2b FIT path. Resolved by exp-4 + first-100 signups. | W2 / W3 |
| A-W3-5 | Web push (web app) is sufficient for creep-alert UX on Android/desktop; iOS email-only fallback does not materially hurt retention. | open | W3 §2c #5. Resolved by post-MVP retention segmented by platform. | Post-MVP |

## Workshop 4 — Pricing, Retention & Distribution Economics

| ID | Assumption | Status | Evidence / Pointer | Owner |
|---|---|---|---|---|
| A-W4-1 | M3 retention ≥55% on the W2 loop with the Convert ICP. | open — **gating economic** | W4 §2b retention table. exp-4 + first 200 paid cohort. | exp-4 / W4 |
| A-W4-2 | Free → paid conversion ≥6% within 60 days under the 3-Weekly-Reviews-capped shape. | open | W4 §2b free-tier model. First 1k signups. | First-1k cohort |
| A-W4-3 | Annual mix ≥40% of paid users at $120/yr (17% off). | open | W4 §2b pricing table. First 200 paid cohort. | First-200 cohort |
| A-W4-4 | Creator + organic + community channels carry ≥75% of signups by month 6 at blended CAC ≤$30. | open | W4 §2b CAC table. Monthly cohort attribution. | Y1 ops |
| A-W4-5 | Coach-channel (MVP-1) produces ≥2.5 paid athletes per engaged coach within 90 days. | open | W4 §2b coach economics. Post-MVP coach pilot. | MVP-1 |
| A-W4-6 | Provisional-cap activation path produces activation rate within 10pp of calibration-test path. | open | W4 §4 #1 / §6 #4. Matched-cohort instrumentation. | exp-4 |

## Workshop 5 — Science, Safety & Coaching Credibility

| ID | Assumption | Status | Evidence / Pointer | Owner |
|---|---|---|---|---|
| A-W5-1 | Provisional-cap users complete a decoupling test within 4 weeks at ≥50% rate, unlocking Aerobic Trend. | open | W5 §2b cap protocol. exp-4 + first-100 signups. | exp-4 |
| A-W5-2 | Real Easy Minutes Adherence definition is within ±5 pp of athlete-perceived adherence on the Convert ICP. | open | W5 §2b redefinition. exp-4 qualitative debrief. | exp-4 |
| A-W5-3 | The four-state Recovery delta language is interpretable without prompting clinical-style decisions. | open | W5 §2b state machine. exp-4 qualitative + post-MVP support tickets. | exp-4 / Post-MVP |
| A-W5-4 | Edge-case detection (sensor dropout, optical artifact, illness flag, cardiac drift) catches ≥80% of in-the-wild instances at MVP. | open | W5 §2c catalog. Post-MVP first 6 months; tagged-error-rate. | Post-MVP |
| A-W5-5 | Claims policy is enforceable across creator partners with quarterly review and ≤1 partnership-ending incident per quarter. | open | W5 §2b creator kit. Ongoing partnership monitoring. | Ongoing |

## Workshop 6 — Trust, Privacy & Platform Dependency

| ID | Assumption | Status | Evidence / Pointer | Owner |
|---|---|---|---|---|
| A-W6-1 | PostgreSQL RLS-based multi-tenant isolation prevents cross-athlete leakage at scale (≥10k Pro users) without significant query-performance regression. | open | W6 §2b. Pen-test + load-test at MVP launch. | MVP build |
| A-W6-2 | Strava-revocation 24h soft-delete / 30-day hard-delete window is enforceable operationally without missed deletions. | open | W6 §2b retention table. Post-MVP month-3 audit. | Post-MVP |
| A-W6-3 | EU user share stays below the conservative DPO-appointment threshold (~50k EU users) through Y2. | open | W6 §2c #2. Monitored at user-share milestones. | Ongoing |
| A-W6-4 | Pen-test at MVP launch finds no critical vulnerabilities (zero P0/P1); medium findings remediated within 30 days. | open | W6 §2b. Pen-test vendor selection + remediation. | MVP build |
| A-W6-5 | Subprocessor list remains stable through Y1 (≤1 category-addition per quarter, no payment-processor change). | open | W6 §2c subprocessor matrix. Ongoing. | Ongoing |

## Workshop 7 — Red Team & Go / No-Go

| ID | Assumption | Status | Evidence / Pointer | Owner |
|---|---|---|---|---|
| A-W7-1 | exp-1 LP test delivers ≥4% LP→email and ≥10% email→qualified-interview-booking on Convert ICP copy. | open | W7 Stage 1 gate. Pre-Stage-1 execution. | Founder |
| A-W7-2 | exp-2 interview round (15 Convert candidates) delivers ≥9 of 15 wedge-resonance and ≥6 of 15 pre-pay-for-$50 confirmations. | open | W7 Stage 2 gate. Pre-Stage-2 execution. | Founder |
| A-W7-3 | Garmin partnership is approved or in serious negotiation by exp-4 week 13; otherwise Wahoo or Coros direct is a viable Phase-1.5 alternative. | open | W7 §2 Scenario 2; Stage 4 gate. | Integrations |
| A-W7-4 | Medical Advisory Board (2–3 sports-medicine advisors) is formable at honorarium-only compensation (~$2–4k/yr total). | open | W7 §4 (Science memo). Pre-MVP-launch. | Founder |
| A-W7-5 | Founder personal runway holds for 18 months at modeled ~$90k spend; revisited at MVP+90 with actual revenue. | open | W7 §2 Scenario 10; W4 economics. | Founder ongoing |

---

## Workshop 2 — Behavior & Product Wedge

| ID | Assumption | Status | Evidence / Pointer | Owner |
|---|---|---|---|---|
| A-W2-1 | The most valuable product loop is planned easy intensity vs. actual easy intensity, not broad training-plan management. | open | Needs workflow interviews and prototype reaction. | W2 |
| A-W2-2 | Athletes will return weekly to review adherence and aerobic progress even when no new personal records occur. | open | Needs retention-oriented prototype tests. | W2 / W4 |
| A-W2-3 | Metrics such as HR drift, pace-at-HR, and time-above-cap are understandable enough to drive behavior for the target user. | open | Needs usability and terminology testing. | W2 / W5 |
| A-W2-4 | Rewarding restraint can create a compelling emotional loop despite competing social incentives to go harder. | open | Needs product concept tests and early cohort feedback. | W2 |

---

## Workshop 3 — Integrations & Ecosystem

| ID | Assumption | Status | Evidence / Pointer | Owner |
|---|---|---|---|---|
| A-W3-1 | Launching with a narrow integration set plus file import is sufficient for MVP credibility. | open | Needs feature-priority interviews. | W3 |
| A-W3-2 | Athletes will tolerate this being a companion layer rather than their primary training log. | open | Needs substitution testing. | W3 / W1 |
| A-W3-3 | Coach and community channels can reduce CAC materially without forcing a coach-first product roadmap. | open | Needs channel interviews and small-scale outreach. | W3 / W4 |
| A-W3-4 | Dependence on third-party platform APIs can be managed without existential launch risk. | open | Needs dependency review and fallback planning. | W3 / W6 |

---

## Workshop 4 — Pricing, Retention & Distribution Economics

| ID | Assumption | Status | Evidence / Pointer | Owner |
|---|---|---|---|---|
| A-W4-1 | A paid tier in the $10-15/month range is acceptable for the beachhead if the product clearly improves execution and review quality. | open | Initial pricing hypothesis only. | W4 / W1 |
| A-W4-2 | Free users can be activated enough to prove value without making the paid tier too easy to ignore. | open | Needs packaging experiments. | W4 |
| A-W4-3 | Retention is driven more by weekly review habit and longer-term progress visibility than by daily app usage. | open | Needs early product and interview evidence. | W4 / W2 |
| A-W4-4 | Support and education costs stay reasonable even though methodology education is nuanced. | open | Needs support-model assumptions. | W4 / W5 |

---

## Workshop 5 — Science, Safety & Coaching Credibility

| ID | Assumption | Status | Evidence / Pointer | Owner |
|---|---|---|---|---|
| A-W5-1 | The product can support zone 2, MAF, and polarized training without overstating that they are identical or universally correct. | open | Needs claims framework. | W5 |
| A-W5-2 | The most visible metrics can remain descriptive and useful without crossing into unsafe medical or pseudo-clinical advice. | open | Needs language and product review. | W5 |
| A-W5-3 | Credible coaches and science-literate athletes will view the product as disciplined rather than dogmatic. | open | Needs external review. | W5 |

---

## Workshop 6 — Trust, Privacy & Platform Dependency

| ID | Assumption | Status | Evidence / Pointer | Owner |
|---|---|---|---|---|
| A-W6-1 | Athletes will trust the product with imported training data if privacy, export, and deletion posture are clear and strong. | open | Needs product policy and user feedback. | W6 |
| A-W6-2 | Reasonable fallback modes exist if a major integration weakens or disappears. | open | Needs dependency mapping. | W6 |
| A-W6-3 | AI-generated insights, if introduced later, can remain assistive without creating trust or liability problems. | open | Future-facing; keep explicit. | W6 |
