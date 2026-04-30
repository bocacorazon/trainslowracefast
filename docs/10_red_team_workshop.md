# Workshop 7 — Red Team & Go / No-Go

*Primary agent: Red Teamer / Skeptic-in-Residence*
*Secondary reviewers: all other agents (Demand & Positioning, Behavior & Product, Integrations & Ecosystem, SaaS Economics, Science & Coaching Credibility, Trust & Platform Risk).*
*Inputs: W1–W6 in full. The W6 §6 inbox lists ten scenarios to walk end-to-end.*

---

## §1. Executive summary

**The decision.** Conditional **GO** to MVP build, with three pre-conditions and a tight set of post-launch kill criteria. The validation work across W1–W6 supports building the product; it does **not** support raising external capital, hiring beyond a single first engineer, or committing to a 24-month plan in writing. The plan is staged: build, validate at exp-4 + first 100 paid, decide again at MVP+90.

**Pre-conditions before MVP build commits (must all be true):**

1. **exp-1 LP test** delivers ≥4% LP→email and ≥10% email→qualified-interview-booking on the Convert ICP copy. (Validates W1 demand at the cheapest possible price.)
2. **exp-2 interview round** (15 Convert candidates) delivers ≥9 of 15 confirming the "easy days, done right" wedge resonates more than a TSS-based summary, and ≥6 of 15 willing to pre-pay $50 for early access.
3. **exp-4 concierge pilot** — the 13-week version locked in W2 — delivers ≥60% Adherence improvement (median per-athlete, weeks 9–12 vs. weeks 1–4) **and** ≥70% week-13 retention with ≥30% NPS.

If any of the three falls below threshold, the project does not advance to MVP build. The founder runway (~$90k personal capital, W4) is not deployed without all three signals.

**Post-launch kill criteria** (consolidated from W1–W6, ranked by likelihood × severity in §3):

- M3 retention < 50% sustained 2 cohorts → kill or pivot (K-W2-2).
- LP→activation < 4% sustained 60 days at scale → channel rebuild required (K-W4-1).
- CAC > $40 sustained 90 days → channel rebuild required (K-W4-3).
- Strava TOS change cuts off the Weekly Review surface → execute Tier 1/2/3 playbook; assess viability at MVP+90 (K-W3-2 + W6 §2c).
- M3 paid-conversion < 6% sustained 2 cohorts → free-tier or wedge problem (K-W4-2).
- Cross-athlete data leak or sensitive-field misuse → P0 (K-W5-3 / K-W6-1 / K-W6-4 — hard rules).

**Ten scenarios walked end-to-end (§2).** Of the ten attack paths examined, **two are existential** (Strava cutoff combined with Garmin denial; medical incident with poor founder response) and **two are likely but survivable** (M3 retention undershoot at first cohort, well-funded competitor entry). The remaining six are manageable with the playbooks already in W3–W6.

**Confidence after W7:** ~70% the business is viable as a single-founder company with one first engineer, founder runway, and the staged go/no-go gates above. Confidence is *lower* than post-W6 because the Red Team work surfaces dependencies (founder bus-factor, durable-vs-trend Convert demand, the joint Strava+Garmin-denial scenario) that are real but cannot be fully de-risked pre-launch.

---

## §2. Primary memo — Red Teamer

The Red Team posture is to argue against the plan with the same rigor used to build it. Each scenario walks the failure path, names the existing mitigations, and states what is the residual risk.

### Scenario 1 — Strava cutoff at MVP+90 days

**Failure path.** Strava ships a TOS change restricting "derived weekly summaries" or revokes our API access citing competitive overlap. ~70% of MVP users authenticated via Strava OAuth (W3); their data pipeline halts.

**Existing mitigations.** W6 §2c playbook: Tier 2 (FIT manual upload) is production-ready at launch; Tier 3 (concierge email-only delivery from coach-shared or FIT-uploaded data) survives a total platform cutoff. 30/90/180-day playbook in place.

**Residual risk.** **Friction collapse.** FIT upload moves the Weekly Review activation funnel from W4's 54% net account→activation to ~30–35% (estimated; never tested). The product still works for highly motivated Convert athletes, but the casual edge of the ICP — the runner who liked the wedge enough to sign up but won't manage manual uploads — churns. Y1 ARR target ($120k / 1k paid) becomes unreachable; revised target would be $40–60k ARR / 350–500 paid.

**Verdict.** **Survivable, not fatal.** Tier 3 is a real floor. The business at 350–500 paid with founder cost structure is still cash-positive (W4 COGS is $0.78/user/mo). It is not the business we wanted, but it is a business.

### Scenario 2 — Strava cutoff AND Garmin denies the partnership

**Failure path.** Same as Scenario 1, plus the Phase-1.5 Garmin partnership (W3) is denied or stalled past the 90-day window. Wahoo / Coros are deferred per W3 scope.

**Existing mitigations.** Tier 2 (FIT) and Tier 3 (concierge) remain. The W3 §6 deferred Wahoo / Coros work could be prioritized at this point — but we will have spent 6+ months without those integrations and will start the integration work cold.

**Residual risk.** **Existential.** Two large compounding losses simultaneously. Activation funnel halves and there is no platform-direct alternative on the runway. The 30-day playbook works; the 90-day playbook turns into a fundraising or wind-down decision.

**Verdict.** **The single largest existential risk.** Probability of co-occurrence is low (Strava action and Garmin denial are partially independent), but consequence is high. The mitigation is **not** to add more integrations pre-MVP — the W3 case for scope discipline still holds. The mitigation is to **start Garmin partnership conversations during exp-4**, not after MVP, so that Phase-1.5 is in motion (or its denial is known) before we are mid-launch.

**New action:** Open Garmin partnership conversation during exp-4 (week 6 of the 13-week pilot). If denied, evaluate Wahoo or Coros direct integration as the Phase-1.5 alternative *before* MVP build commits.

### Scenario 3 — TrainingPeaks (or Intervals.icu) ships a "Convert mode"

**Failure path.** A well-funded incumbent (TrainingPeaks: ~30 engineers, $20M+ ARR) or a beloved single-founder app (Intervals.icu: large, loyal user base) ships a polarized-training-first toggle within 6–12 months of our launch, capturing the Convert wedge with their existing distribution.

**Existing mitigations.** W1 §3 / §6 brand and ICP work: "Train slow, race fast" and "Easy days, done right" are *positioning* commitments, not features. Incumbents adding a toggle does not change their primary surface; the existing TSS-driven dashboards remain dominant for their core users. W5 claims policy is a *cultural* moat — incumbents cannot easily walk back years of intensity-positive marketing without confusing their existing user base.

**Residual risk.** **Likely but survivable.** Probability is high (≥40% over 18 months) that an incumbent ships *something*. But the wedge defends because:

- Our ICP is *converting away from* TSS-based products. The incumbent ships a toggle; the Convert leaves anyway.
- Brand identity (the explicit "no readiness score / no TSS / no maximize-intensity" stance, W5) is hard to clone without alienating the incumbent's core users.
- Distribution: our channel mix (W4) — creator partners aligned with the method, Strava clubs running MAF challenges — does not require winning against the incumbent, only winning *with* the aligned community.

**Verdict.** **Plan to coexist, not to beat.** Treat incumbent toggles as validation, not threat. The kill condition is not "incumbent ships a toggle" but "post-incumbent-launch our M3 retention drops below 50% sustained 2 cohorts" — which is already K-W2-2. No new criterion needed.

### Scenario 4 — M3 retention lands at 40% (the load-bearing W4 assumption fails)

**Failure path.** First-cohort M3 retention undershoots K-W2-2's 60% threshold (revised down from W2's 50% during W4 stress test) and lands at ~40%. The behavior loop (W2) didn't deliver durable engagement.

**Existing mitigations.** W2 §2b loop was designed with multiple reinforcing surfaces (sync→2h digest→Sunday Weekly Review→Aerobic Trend); the four-state Recovery delta wording library (W5) provides additional engagement surfaces. exp-4's 13-week window (W2 §6) was specifically designed to detect M3 cohort behavior at small N before MVP.

**Residual risk.** **Likely.** The W4 confidence was 60% specifically because M3 retention was the load-bearing assumption. Probability of undershoot at first cohort is ~30%. *Survivable* if the diagnosis points to a fixable surface (e.g., the Sunday Weekly Review email is too long; people stop reading); *fatal* if the diagnosis points to the wedge itself (athletes find the easy-days framing too restrictive after the novelty wears off).

**Verdict.** This is the **single highest-probability kill path.** The mitigation is the staged go/no-go: exp-4 must show ≥70% week-13 retention before MVP build commits. If exp-4 retention is below 60% at week-9–13 segment, do not advance.

### Scenario 5 — Channel mix collapses to paid-dominant

**Failure path.** Creator partnerships underperform (creators struggle to authentically integrate the wedge into existing intensity-focused channels); organic + community channels fall short; CAC blows past $40. Founder is forced into paid (Meta / Google) where the Convert ICP is hard to target by interest and CAC structurally lands $60–80.

**Existing mitigations.** W4 channel mix targets (35% creator, 25% organic, 15% community, 10% Strava clubs, 10% coach, 5% paid) explicitly avoid paid-dominant. K-W4-3 fires at $40 CAC sustained 90 days.

**Residual risk.** **Possible but survivable.** Probability ~25%. If creators underperform, the response is *not* to scale paid — the response is to slow down. At 5% CAC growth instead of 35%, Y1 paid lands at ~300–400 instead of 1k; ARR at $40–50k instead of $120k. Founder runway absorbs this. The business still validates at smaller scale.

**Verdict.** **Survivable through deceleration.** No new criterion; K-W4-3 already covers it.

### Scenario 6 — Medical incident with a declared-condition athlete

**Failure path.** An athlete on declared beta-blockers ignored the suppression notice (W5 / W6 conditions-consent surface), interpreted Adherence as a green-light to push intensity, trained hard during a recovery week, and had a cardiac event. Family blames the app.

**Existing mitigations.** W5 claims policy: positive-frame only, never prescriptive, never medical. W6 conditions-consent surface: explicit "I understand" before onboarding completes for declared-condition users; HRmax-derived calculations excluded; Recovery delta suppressed; "talk to your physician" notice. W5 §2c "training is not medical advice" boilerplate in onboarding and footer. Cyber liability insurance does not cover this; product liability does (need to confirm coverage type at MVP).

**Residual risk.** **Possibly existential** — not legally (the disclosures and conditions-consent surface make the legal posture defensible) but **narratively**. Press: "Health app failed to warn user." A single high-visibility incident in the first 12 months can end the brand even with a winning legal case.

**Verdict.** **The second-largest existential risk.** Mitigation is partly already in place (W5 + W6) and partly response posture: a documented incident-response narrative *for medical incidents specifically*. New action below.

**New action:** Pre-write a medical-incident response template and review with counsel before MVP launch. Components: 24-hour outreach to family; public statement template emphasizing the conditions-consent surface and disclaimers; partnership / advisory medical board to issue a contextualizing statement; pause new declared-condition signups during incident review.

### Scenario 7 — Creator-partner viral overclaim

**Failure path.** An aligned podcaster (per W5 creator copy library) deviates from the language guide and on a 100k-listener show says "this app cures overtraining." We end the partnership (W5 K-W5-1 fires) but the overclaim spreads via clips on social.

**Existing mitigations.** W5 §2b creator copy library + partnership termination clause for prohibited claims. K-W5-1 fires at first prohibited claim incident.

**Residual risk.** **Manageable.** Probability ~30% over 18 months that *some* creator deviates; severity is moderate. The response works if it is fast and public.

**Verdict.** **Survivable with a documented response playbook.**

**New action:** Pre-write a creator-overclaim response template: same-day public correction post on our channels, partnership termination announced if material, claims policy linked, no defensive posture toward the creator (no public fight). Review with counsel.

### Scenario 8 — Data breach

**Failure path.** A misconfigured RLS policy or an exposed API endpoint leaks HRV streams or declared-condition data. The breach is detected in production within 24 hours of being live.

**Existing mitigations.** W6 §2b RLS at the database layer; field-name-granular audit logging; pen-testing annually; cyber liability insurance bound at MVP launch; 72h authority disclosure SLA. K-W6-1 is a hard rule.

**Residual risk.** **High consequence, lower probability.** Probability ~10–15% over 18 months of *some* incident; pen-testing reduces this. Brand consequence depends entirely on response: rapid disclosure, transparent post-mortem, clear remediation = recoverable; obfuscation = end of brand.

**Verdict.** **Manageable through the W6 incident-response plan.** No new mitigation needed pre-launch beyond confirming the response plan is rehearsed before pen-test month.

### Scenario 9 — Zone-2 trend cools; cultural moment passes

**Failure path.** Popular interest in zone 2 / MAF / polarized training declines (the trend was driven by Peter Attia / Inigo San Millán media presence, podcast cycles, etc.). New athlete acquisition slows even with mitigations.

**Existing mitigations.** W1 ICP work: the Convert is defined by *training history and intent*, not trend. They are athletes who've been injured by overreaching, who chase aerobic durability for events ≥2 hours, who have read the literature themselves. This segment exists independent of trend cycles.

**Residual risk.** **Slow-burn risk.** Probability of trend cooling is high (≥60% over 36 months); probability that the *segment* disappears is low (≤15%). The acquisition rate slows; the existing user base remains durable.

**Verdict.** **Manageable through deceleration.** Same response as Scenario 5 — slow growth, not death. Founder runway accommodates this.

**Note for plan:** the brand "Train slow, race fast" is *more* durable than the brand "Zone 2 Trainer." The W1 brand decision protects against this scenario. No revision.

### Scenario 10 — Founder bus-factor / runway exhaustion

**Failure path.** Two sub-paths:

- **(a)** Founder personal capital depletes faster than expected (medical event, family emergency, lower-than-modeled Y1 revenue) at month 14. Runway model assumed $90k over 18 months.
- **(b)** Founder is incapacitated or otherwise unable to operate. The first hire (engineer) cannot run the product alone (no marketing or support depth).

**Existing mitigations.**

- (a) W4 conservative case: 500 paid at $60k ARR is cash-positive against $0.78/user/mo COGS plus founder cost. Without external capital, founder must continue to run lean.
- (b) W6 §2c bus-factor mitigation: legal handling instructions documented and stored with a trusted third party; access expansion at MVP-1.

**Residual risk.**

- (a) **Real but bounded.** Mitigation is to revisit the runway model at MVP+90 with actual cohort data; if Y1 revenue is tracking below $40k ARR at month 9, decision is to slow burn (cut non-essential spend) or wind down with grace.
- (b) **Hard to fully mitigate at single-founder scale.** Bus-factor at this stage is inherent to the model.

**Verdict.** **Accept the structural founder dependency.** Make the wind-down posture clean (data export still works, account deletion still works, athletes are not orphaned with their data trapped). This is partially the W3 §3 export commitment.

**New action:** Document a wind-down playbook before MVP launch — what happens to user data, how export is preserved, how subscriptions are paused, communication template. Stored with the same third party that holds bus-factor instructions.

---

## §3. Ranked failure inventory

Failures ranked by **likelihood × severity** over the first 18 months post-MVP. Each row links to the scenario and the existing mitigation source.

| Rank | Failure | Likelihood (18mo) | Severity | Mitigation | Action item |
|---|---|---|---|---|---|
| 1 | M3 retention undershoot (Scenario 4) | ~30% | High | exp-4 13-week pilot must validate ≥70% week-13 retention before MVP build commits | Pre-MVP gate |
| 2 | TrainingPeaks / Intervals "Convert mode" toggle (Scenario 3) | ~40% | Medium | Wedge defends through brand and Convert flow direction; coexist not beat | None new |
| 3 | Creator-partner viral overclaim (Scenario 7) | ~30% | Medium | W5 copy library + termination clause + new response template | Pre-launch |
| 4 | Channel mix collapse to paid-dominant (Scenario 5) | ~25% | Medium | K-W4-3 deceleration response; founder runway absorbs | None new |
| 5 | Strava cutoff alone (Scenario 1) | ~20% | High (revenue), low (existential) | Tier 1/2/3 playbook (W6) | None new |
| 6 | Medical incident with declared-condition athlete (Scenario 6) | ~15% | Existential (narrative) | W5 + W6 surface; new response template + medical advisory pre-MVP | Pre-launch |
| 7 | Data breach (Scenario 8) | ~10–15% | High | W6 RLS + audit + pen-test + insurance + IR plan | None new |
| 8 | Zone-2 trend cools (Scenario 9) | ~60% over 36mo (slow) | Low (segment durable) | W1 brand decision; deceleration | None new |
| 9 | Strava cutoff + Garmin denial (Scenario 2) | ~5–8% | Existential | Tier 2/3; start Garmin partnership during exp-4 | Pre-MVP |
| 10 | Founder runway / bus-factor (Scenario 10) | ~10–20% | Existential | W6 + new wind-down playbook | Pre-launch |

The five highlighted action items (rank 1, 3, 6, 9, 10) are the W7 deliverables that materially change the plan.

---

## §4. Secondary memos (consolidated)

Each agent contributed a residual concern and a mitigation. Consolidated rather than expanded for length.

**Demand & Positioning:** "Convert" durability across trend cycles is reaffirmed (Scenario 9). Concern: the brand "Train slow, race fast" is correct, but the *acquisition copy* must evolve — what worked in 2024–25 (the trend high) won't carry the same conversion in 2027. Plan to refresh acquisition copy quarterly post-launch; treat creator-partnership refresh as part of channel-mix maintenance.

**Behavior & Product Wedge:** M3 retention is the load-bearing variable and exp-4 is the right gate. Concern: exp-4 N is small (~25 athletes per W2 §6); statistical noise at that scale could mask a real signal in either direction. Mitigation: do not interpret exp-4 retention as a binary; if it lands in 60–70% range, design exp-4-extended (10 more athletes, 8 weeks) before committing MVP.

**Integrations & Ecosystem:** Scenario 2 (Strava + Garmin compound) is the right item to escalate. Action: start Garmin partnership conversation in exp-4 week 6 (not later). If denied, evaluate Wahoo and Coros direct as Phase-1.5 alternatives *before* MVP build commits, not after.

**SaaS Economics:** The conservative case (500 paid / $60k ARR Y1) is cash-positive at founder cost structure with no external capital. The aggressive case (1k / $120k) requires the channel mix delivering. Concern: the model assumes founder time is uncompensated through Y1 — if Y1 revenue tracks below $40k ARR at month 9, founder must transition to part-time on the project or wind down. Make this trigger explicit (added to kill criteria below as K-W7-2).

**Science & Coaching Credibility:** The medical-incident scenario (6) is the right escalation. Recommend forming a **Medical Advisory Board** of 2–3 sports-medicine physicians or exercise physiologists pre-MVP. They are not paid employees; they are advisors who agree to (a) review the conditions-consent surface and the suppression rules, (b) provide a contextualizing statement in case of a medical incident, (c) review claims policy quarterly. Honorarium budget: ~$2–4k/yr total.

**Trust & Platform Risk:** No new concerns from Trust beyond the W6 set. Reaffirms that the wind-down playbook (Scenario 10) needs to include Trust commitments — data export remains free during wind-down, account deletion is honored, subscription pause/cancel is automatic.

---

## §5. Founder synthesis — staged go / no-go

### The go / no-go architecture

The decision is **staged, not single-shot**.

**Stage 1 — exp-1 LP test.** Cost: ~$2k (LP build, ~$1.5k paid for traffic). Duration: ~3 weeks.
- Pass: ≥4% LP→email, ≥10% email→qualified-interview-booking on Convert ICP copy.
- Fail: do not advance. Review wedge messaging or kill.

**Stage 2 — exp-2 interview round (15 Convert candidates).** Cost: ~$1k (incentives) + founder time. Duration: ~3 weeks.
- Pass: ≥9 of 15 confirm wedge resonance; ≥6 of 15 willing to pre-pay $50 for early access.
- Fail: do not advance. Wedge is not the buying trigger — revisit ICP or wedge.

**Stage 3 — exp-4 concierge pilot (13 weeks, ~25 athletes).** Cost: founder time + ~$3–5k (delivery infrastructure: Loom, Calendly, Notion, light tooling). The pilot is intentionally non-scalable; the founder writes Weekly Reviews manually.
- Pass: median per-athlete Adherence improvement ≥60% (weeks 9–12 vs 1–4); week-13 retention ≥70%; NPS ≥30.
- Fail: do not advance to MVP build. Either iterate concierge until signal lands, or kill.

**Stage 4 — Garmin partnership conversation.** Started in exp-4 week 6.
- Approved or in serious negotiation: proceed to MVP build.
- Denied or stalled: evaluate Wahoo or Coros direct integration as Phase-1.5; do not advance to MVP build with Strava-only and no platform-direct alternative on the runway.

**Stage 5 — MVP build commits.** Founder runway (~$90k personal capital over 18 months) deploys. Build budget per W4 (~$45k Y1 engineering one-time). Pre-launch deliverables:
- Pen-test selection and scope (W6).
- Cyber liability insurance bound (W6).
- Medical Advisory Board formed (W7).
- Medical-incident response template + creator-overclaim response template + wind-down playbook drafted and stored (W7).
- All hard-rule kill criteria (K-W5-3, K-W6-1, K-W6-4) instrumented for production detection.

**Stage 6 — MVP launch + first 100 paid.** Channel mix per W4. Behavior loop per W2.

**Stage 7 — MVP+90 review.** Hard checkpoint:
- M3 retention ≥60% (revised W4 threshold) on first cohort?
- LP→activation ≥4%?
- CAC ≤$30 blended?
- Strava integration stable, Garmin direct online or imminent?
- Y1 ARR tracking ≥$40k?

If 4 of 5 pass: continue to Y1 plan as scoped.
If 2–3 of 5 pass: scope down to conservative case (500 paid / $60k ARR target); reassess at month 9.
If 0–1 of 5 pass: wind down with grace per Scenario 10 plan.

### Decisions locked

1. **Conditional GO** to MVP build, gated by Stages 1–4 pre-conditions.
2. Three pre-MVP-launch artifacts produced before founder runway deploys: medical-incident response template, creator-overclaim response template, wind-down playbook.
3. Medical Advisory Board (2–3 advisors, ~$2–4k/yr honorarium) formed before MVP launch.
4. Garmin partnership conversation begins in exp-4 week 6 (not after MVP launch).
5. exp-4 retention floor is ≥70% at week 13 to advance; 60–70% triggers exp-4-extended; <60% halts (Behavior §4 #2 adopted).
6. MVP+90 hard checkpoint with 5 metrics; 4-of-5 pass = full plan, 2–3 pass = scope down, 0–1 pass = wind down.
7. Acquisition copy refresh quarterly post-launch (Demand §4 #1 adopted).
8. The hard-rule kill criteria from W5 and W6 (K-W5-3, K-W6-1, K-W6-4) are instrumented at MVP launch, not as a follow-on.

### New entries for the assumption register

- **A-W7-1:** exp-1 LP test delivers ≥4% LP→email and ≥10% email→qualified-interview-booking on Convert ICP copy. *Owner: pre-Stage-1 execution.*
- **A-W7-2:** exp-2 interview round delivers ≥9 of 15 wedge-resonance and ≥6 of 15 pre-pay-for-$50 confirmations. *Owner: pre-Stage-2 execution.*
- **A-W7-3:** Garmin partnership is approved or in serious negotiation by exp-4 week 13; if not, Wahoo or Coros direct is a viable alternative. *Owner: integrations.*
- **A-W7-4:** Medical Advisory Board (2–3 advisors) is formable at honorarium-only compensation (~$2–4k/yr total). *Owner: founder, pre-MVP-launch.*
- **A-W7-5:** Founder personal runway holds for 18 months at the modeled $90k spend; revisited at MVP+90 with actual revenue data. *Owner: founder, ongoing.*

### New entries for the kill-criteria ledger

- **K-W7-1:** Any one of the three pre-conditions (exp-1, exp-2, exp-4) fails → do not deploy founder runway to MVP build. Review or kill.
- **K-W7-2 (Economics):** If Y1 revenue tracks below $40k ARR at month 9 post-launch → founder transitions to part-time on project or initiates wind-down playbook. Do not extend personal runway beyond month-18 modeled spend.
- **K-W7-3 (Red Team):** If 0 or 1 of 5 MVP+90 checkpoint metrics pass → execute wind-down playbook (Scenario 10).
- **K-W7-4 (Red Team):** Compound platform failure — Strava cutoff AND Garmin direct denied — at any point post-launch → reassess viability within 30 days; explicit founder-led decision to continue, scope down, or wind down. Not an automatic kill, but the heaviest decision moment.

### Confidence statement

**~70% confidence the business is viable as a single-founder company with one first engineer, founder runway, and the staged go/no-go gates.**

The remaining 30% breaks down approximately as:
- ~10% the wedge or behavior loop does not produce durable engagement (Scenario 4).
- ~8% compound platform failure or single platform failure with poor mitigation execution (Scenarios 1 + 2).
- ~5% medical-incident or trust-incident narrative ends the brand even with correct response posture (Scenarios 6, 8).
- ~7% founder bus-factor / runway / motivation ends the project for non-product reasons (Scenario 10).

The framing the founder should hold:
- This is a defensible, well-scoped business plan with credible mitigations against the known risks.
- It is *not* a venture-scale plan, and the planning posture should not pretend otherwise. No external capital. No 24-month commitments in writing.
- The single most leveraged decision is the **exp-4 gate**: if the 13-week concierge pilot delivers retention and adherence improvement, the rest of the plan is roughly de-risked. If it does not, no amount of MVP polish will fix it.
- The single most leveraged risk is **Strava + Garmin compound failure**; the partial mitigation is to start the Garmin conversation early (during exp-4, not after launch).

### Out of scope for this validation cycle

The following items are *not* validated by W1–W7 and require additional cycles before commitment:

- Coach product (the monetization expansion path beyond athlete subscriptions).
- B2B/teams products.
- Native mobile applications.
- Wahoo / Coros / Polar / Apple integrations.
- Any geographic expansion beyond English-language markets.
- Any pricing-tier expansion beyond $12/mo single-Pro.

These are tracked as follow-on work and are not blockers to the staged go/no-go decision above.

---

## §6. Closing

Workshops 1–7 produced an internally consistent business plan with explicit pre-launch gates, post-launch kill criteria, and ranked failure paths. The hard rules are instrumented; the existential risks are named; the mitigations are concrete.

The founder's decision is now: execute Stage 1 (exp-1 LP test). That is the next action. The board's collective recommendation is to proceed.
