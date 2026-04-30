# Workshop 2 — Behavior & Product Wedge

*Primary agent: Behavior Change Product Strategist*
*Secondary reviewers: Demand & Positioning, Science & Credibility, SaaS Economics*
*Inputs: Workshop 1 locks (the Convert ICP, "Train slow, race fast" / "Easy days, done right", $12/mo Pro), Workshop 1 inbox.*

---

## §1. Executive summary

**Locked decisions**

- The product is a **behavior loop**, not an analytics surface. Charts exist only when they drive an action.
- The loop is: **Sync → Deviation digest (≤2 h) → 30-second review → Sunday Weekly Review → Aerobic Trend reward**.
- The **activating moment** is the **first Sunday Weekly Review**. It must be delivered within 7 days of signup or activation is missed.
- The **smallest behavior-driving metric set** is five items (Adherence %, Drift minutes, Aerobic Trend, Recovery delta — descriptive, Weekly distribution). Everything else is suppressed at MVP.
- The **free tier** delivers the Weekly Review for the current + previous week (so the activating moment lands), and locks creep alerts, drift attribution, full history (≥4 weeks), and benchmark protocols behind Pro. This replaces the W1 Economics push for a time-limited trial.
- The **cap-setting protocol** ships with four options (MAF 180-age, talk test, 30-min TT decoupling, coach-supplied) and explicit caveats. No max-HR-percentage formulas.
- The **intensity-creep alert SLA** is **≤2 hours from sync** for Pro users; the underlying SLA target is end-to-end (sync receipt to push delivery), not provider-side.
- **Concierge pilot (exp-4)** is locked at 12 athletes × 6 weeks, with a parallel auto-version cohort beginning week 5 to test the SaaS handoff (this is the test for K-W1-5).

**Open / reopened items**

- A-W1-6 (software-only behavior change retains) remains the gating unknown. exp-4 plus the auto-handoff cohort are the first evidence.
- Goodhart risk on Adherence % (cap gaming, walking uphills) — needs a counter-metric in W5 review.
- HRV / Recovery delta interpretation is dangerous if framed prescriptively. W5 must define the language.
- Cap-setting onboarding is the riskiest step in the funnel; a setup error invalidates every subsequent metric. Owned jointly by W2 (UX) and W5 (validity).

**Confidence after Workshop 2:** ~55% the loop reliably reduces drift in the Convert (up from ~40%). The remaining uncertainty is concentrated in the auto-version handoff and in cap-setting accuracy at scale.

---

## §2. Primary memo — Behavior Change Product Strategist

### §2a. Q1: What is the strongest version of the problem in your lane?

"Going easy is hard" is true but vague. The honest, decomposed version of the failure:

1. **Sensor lag and feel mismatch.** HR lags effort by 30–90 seconds and drifts upward over a workout independent of effort. Athletes who pace by feel run hot; athletes who pace by HR brake constantly and feel like they're not training. Both end up frustrated and abandon the discipline within 4–8 weeks.
2. **Social and identity pressure.** Strava kudos, group runs, and self-image punish slow paces. The Convert intellectually accepts the method but socially defects. This is a **moment-of-decision** problem, not a knowledge problem.
3. **Reward starvation.** TrainingPeaks and Garmin reward intensity (TSS, training load, "productive" labels). Easy work registers as low load, which feels like wasted time. Without a *different* reward signal that pays out for restraint, the athlete reverts.
4. **No deviation feedback.** When an easy day drifts hard, no current tool flags it within hours. By the time the athlete sees a weekly chart, the behavior is normalized. The teachable moment is gone.
5. **Aerobic progress is invisible.** The payoff of low-intensity training is "same pace at lower HR over months" — exactly the metric none of the incumbents surface as a primary view. The athlete cannot see the deal they signed up for.
6. **Cap is wrong, or trusted too much.** A bad cap (max-HR formula, copy-pasted from a podcast) makes every metric meaningless. Athletes don't know they have a bad cap; they only know the method "doesn't work for them."

**The wedge is the *deviation feedback loop* and the *aerobic progress reward.*** Items 4 and 5 are absent in every incumbent. Items 1, 2, 3, 6 are existing pain that the product can address but doesn't own.

### §2b. Q2: What proposal best addresses that problem?

#### The behavior loop (BJ Fogg / hooked-style decomposition)

| Stage | Trigger | Action | Reward | Cost |
|---|---|---|---|---|
| **Per-workout** | Push notification within 2 h of sync: *"Easy run on Tue: 14 min over cap. Tap to review."* | 30-second review screen: HR-vs-cap chart, one-tap reason ("hills", "heat", "felt fine", "wanted to push"), or skip | Adherence % updates immediately; streak counter ("9 of 10 easy days in green") | One tap, ≤30 sec |
| **Per-week (Sunday)** | Weekly Review email + in-app card, delivered Sunday 7am local | Read 60-second narrative: drift hot-spots, aerobic trend slope, one suggested focus for next week | Aerobic Trend chart with new data point; "Easy days done right: 6 of 7" headline | 60–90 sec read |
| **Per-month** | First-of-month "How's the deal going?" recap | Compare aerobic trend month-over-month; rerun cap-validation prompt if drift suggests cap is off | Visible "race fast" payoff: pace at sub-cap HR over time | 2 min |

The loop is intentionally **post-workout, not pre-workout**. Pre-workout pacing tools exist (Garmin alerts, audio cues) and don't change behavior at the level we need; the moment that matters is the **first 24 hours after a violation**, when the athlete still remembers what they were thinking when they pushed.

#### The smallest behavior-driving metric set (the "Five")

Every other metric is hidden by default at MVP. The Five:

1. **Adherence %** — share of *tagged-easy* time spent at-or-below cap. Weekly headline number.
2. **Drift minutes** — absolute minutes above cap on easy-tagged sessions, this week + 4-week trend.
3. **Aerobic Trend** — pace (run) or power (bike) at a fixed sub-cap HR band over time. The "race fast" payoff.
4. **Recovery delta** — HRV or RHR vs. 14-day baseline, displayed descriptively ("elevated", "in range", "low") with no prescription.
5. **Weekly intensity distribution** — minutes in easy / threshold / hard zones (3-bucket polarized view).

Explicitly **not** at MVP: TSS, CTL/ATL/TSB, FTP estimation, VO2max estimates, training-load color labels, race-readiness scores, sleep scores. They exist in incumbents, dilute the wedge, and tempt athletes back into intensity-as-progress thinking.

#### Free vs. Pro alignment with the loop

| Capability | Free | Pro ($12/mo) |
|---|---|---|
| Sync + workout list | ✓ | ✓ |
| Weekly Review (current + previous week) | ✓ | ✓ |
| Adherence %, Drift, Weekly distribution (last 14 days) | ✓ | ✓ |
| Cap-setting wizard | ✓ | ✓ |
| **Real-time creep alerts (≤2 h SLA)** | ✗ | ✓ |
| **Drift attribution** (one-tap reasoning + history) | ✗ | ✓ |
| **Aerobic Trend chart** (≥4 weeks) | ✗ | ✓ |
| **Recovery delta** | ✗ | ✓ |
| **Benchmark protocol** (cap re-test, MAF test, decoupling) | ✗ | ✓ |
| **Full history beyond 14 days** | ✗ | ✓ |

Rationale: free tier delivers the **activating moment** (first Weekly Review) so conversion can be earned, but the *durable* value (real-time correction, multi-week trends, the "race fast" payoff chart) sits on the paid side. This contradicts W1 Economics' time-limited trial proposal and is justified in §4.

#### Intensity-creep alert SLA

- **Target:** push notification within **2 hours** of sync from the device platform.
- **Components:** Strava webhook receipt → workout fetch → analysis → push delivery.
- **Failure modes to instrument:** Strava webhook delay (their commitment is "best effort"), Garmin polling interval, our queue depth, push-token validity.
- **Degraded mode:** if 2 h SLA misses, a digest at 8pm local catches the long tail; never silent.
- **Note on Garmin:** Garmin's HealthAPI is webhook-capable; Connect IQ-only data is polled. We assume webhook for MVP and flag this as a W3 dependency.

#### Cap-setting protocol (the riskiest onboarding step)

Four options at signup, in order of confidence:

1. **Lab/coach-supplied LT1 or aerobic threshold** (highest confidence, smallest population).
2. **30-minute time-trial decoupling test** (moderate confidence; requires a willing athlete and is gated to Pro after the cap is set, since interpretation needs trend support).
3. **Talk test self-calibration** with a 3-workout protocol ("can you speak full sentences? at what HR did that break?").
4. **MAF 180-age formula** as last resort, with explicit caveats and a 4-week recheck prompt.

Max-HR percentage formulas are **excluded**. They are the most common source of bad caps in the wild and the primary way the method "doesn't work" for new adopters.

The wizard ends with a **confidence label** ("provisional", "validated by data", "lab-validated") visible on every chart that uses the cap. This is a W5 / Science & Credibility hand-off but the surface lives in W2.

### §2c. Q3: What failure mode or unresolved unknown remains?

1. **Goodhart on Adherence %.** Athletes can game the cap (walk uphills, stop at lights, take long warmups) to win the streak. Mitigation: pair Adherence % with **Aerobic Trend** as the *real* goal; framing tells athletes "Adherence is the input, Aerobic Trend is the output. Gaming the input does not move the output." A counter-metric (e.g., minutes-of-actual-easy-running) is a W5 design item.
2. **Cap drift.** As fitness improves, the correct cap rises. Without re-testing, Adherence % stays high but Aerobic Trend stalls. Monthly recheck prompts mitigate this; the protocol is locked but the trigger logic is a Pro feature, which means free users can sit on a stale cap. Acceptable trade-off.
3. **Auto-version retention vs. concierge.** This is the K-W1-5 test. The product loop above is *the* auto-version. If the concierge cohort changes behavior and the auto cohort doesn't, the wedge is real but the SaaS is not. exp-4 with parallel handoff is the only honest test.
4. **Notification fatigue.** The loop relies on 1 sync notification + 1 weekly email + 1 monthly recap. If athletes mute notifications, the trigger collapses. Default opt-in to **email-only** for free, push for Pro is a tested option (also a small Pro-conversion lever).
5. **Onboarding cap accuracy at scale.** Self-calibration tests (talk test, 30-min TT) require effort. Drop-off at the cap-setting step is the single biggest activation risk and must be measured in the first 100 signups.
6. **Cross-sport differences.** The loop assumes HR is the primary signal. Cycling power adds richness; swimming has no usable HR. MVP is run + bike with HR; swim is deferred to Workshop 3.

#### exp-4 concierge pilot — locked design

- **N:** 12 athletes (the Convert profile, screened against W1 ICP definition).
- **Duration:** 6 weeks.
- **Data:** Strava read-only OAuth.
- **Manual delivery:** Sunday 7am local. Founder + 1 endurance coach split the cohort 6/6. 60–90 second Loom video + email summary covering the same Five metrics.
- **Cap-setting:** 30-min decoupling test administered in week 1 (high-confidence cap by design, isolates loop variable from cap variable).
- **Primary measures:**
  - Adherence % week 1 vs. week 6 (target Δ ≥ +20 pp).
  - Drift minutes week 1 vs. week 6 (target Δ ≥ −30%).
  - Self-reported behavior-change qualitative score (Likert 1–7).
  - WTP at $12/mo for an automated version of the same review (target ≥ 50% commit, ≥ 30% prepay).
  - NPS at week 6 (target ≥ +30).
- **Auto-handoff cohort (parallel):** at week 5, 6 of the 12 are migrated to the auto-version while the other 6 continue with concierge for 4 more weeks. Compare adherence % stability and engagement (open / click / review reads) across groups. This is the **K-W1-5 instrument**.
- **Cost estimate:** 30 min/athlete/week × 6 weeks × 12 athletes × $80 blended rate ≈ **$1,700** total founder-time-cost. Acceptable for the most important test in the validation phase.

---

## §3. Secondary memo — Demand & Positioning Strategist

The loop above is consistent with the W1 ICP lock; "the Convert" already believes the method and needs execution help, which is exactly what this loop addresses. Three things to flag.

1. **Activating moment alignment is right, but free-tier shape is risky.** I argued in W1 for a time-limited Pro trial. After seeing the loop, I withdraw that. The first Weekly Review is the activating event; if it sits behind a paywall, the LP-to-paid conversion path collapses for cold traffic. Free tier should land the first Weekly Review and let the *trend chart* and *creep alerts* do the conversion work. The Behavior memo's free / Pro split is correct.
2. **Messaging-to-product fidelity.** "Easy days, done right" is the LP headline; the in-app primary metric must be **Adherence %** with the same words ("Easy days done right: 6 of 7 this week"). Any drift between landing-page promise and first-screen language is a measurable LP A/B variable for exp-2.
3. **Substitute defense.** Intervals.icu has the data and the charts but not the loop. The defensible answer to "why not Intervals.icu Supporter at $4?" is **the loop is the product**, not the charts. This requires that the demo and onboarding lead with the loop (Sunday review preview, drift digest sample) before any chart. **Founder change request:** the LP exp-2 must include a 30-second video of a *real* Weekly Review, not a chart screenshot.

**My open risks:** if exp-4 concierge produces strong behavior change but at $40/athlete/week, the LP must not over-promise an automated experience that won't match the concierge feel. Tone discipline.

**Kill criterion (added):**
- **K-W2-1:** If exp-2 LP variants that lead with "the loop" do not outperform variants that lead with "the data" by ≥30% relative on email capture, the wedge is not legible to cold traffic and the brand frame must be reconsidered.

---

## §4. Secondary memo — Science & Credibility Reviewer

The loop is responsible at the *behavior* layer. Three concerns at the *claims* layer.

1. **Cap-setting risk dominates everything.** Every metric in the Five depends on a correct cap. The proposed protocol is acceptable, but the **MAF 180-age** option is the weakest and the most likely to be chosen by lazy onboarders. Recommendation: gate MAF behind an explicit "I understand this is a population estimate, not a personal value" acknowledgment, and force a 4-week recheck. Do not show Aerobic Trend until at least one decoupling test has been completed (or coach-supplied cap entered). For MAF-only users, show Adherence % only, with a "complete a calibration test to unlock progress tracking" upsell.
2. **Recovery delta language must be descriptive, never prescriptive.** "HRV low — consider rest" is a clinical recommendation we cannot make. Acceptable language: "HRV is below your 14-day baseline. Possible context: short sleep, alcohol, illness, training load, life stress." Never "rest today", "skip your workout", or any imperative. This is non-negotiable; medication interactions (beta-blockers, SSRIs, ADHD stimulants) and cardiac conditions create real harm potential. Mandatory onboarding question: "Are you on heart-rate-affecting medication, or do you have a cardiac condition?" If yes, **suppress Recovery delta entirely** and surface a "talk to your physician" notice.
3. **Aerobic Trend display needs confidence intervals or a smoothing window.** Pace-at-sub-cap-HR is noisy. A single hot day (heat, dehydration) can drag the trend visually for a month. Recommendation: 14-day rolling window minimum, and explicit "noisy data" labels when CV exceeds a threshold. We do not want athletes interpreting a 3-data-point dip as "the method isn't working."

**Edge cases that must be in the W5 catalog (already on the W5 inbox):**
- Heat / humidity adjustment (descriptive only)
- Cardiac drift on long workouts (filter or annotate)
- Illness (auto-detect via RHR spike → suggest tagging, never mandating)
- Altitude (annotate; do not adjust silently)
- Sensor error (HR strap dropout, optical wrist artifacts) — must be detected and excluded from Adherence %, not treated as drift

**My kill criteria (added):**
- **K-W2-2:** If concierge-pilot cap-setting drop-off exceeds 25% (athletes who start setup but don't complete a calibration), the onboarding is unviable and the cap protocol must be redesigned before exp-4 scales.
- **K-W2-3:** If any Recovery-delta wording in the MVP triggers user-reported clinical harm or causes one or more athletes to skip prescribed medical care, **kill the feature** until W5/W6 produces a defensible policy.

---

## §5. Secondary memo — SaaS Economics Analyst

Three observations on the loop as SaaS.

1. **Activation event is correctly identified.** Activation = first Weekly Review delivered, with sufficient data (≥3 sessions in week, cap set). This is the leading indicator of M2/M3 retention. **Required:** instrument time-from-signup-to-first-review; if median exceeds 7 days, the activation funnel breaks. Mitigation: at signup, if the athlete has <3 sessions in the prior 7 days, schedule the first review for the *following* Sunday and set expectation explicitly. Don't deliver an empty review.
2. **Free tier shape is acceptable, with one tightening.** Behavior memo proposes "Weekly Review for current + previous week" on free. I'd cap free at **3 Weekly Reviews total** (covering the first ~3 weeks of usage) and then revert to a "create account / upgrade" prompt that *previews* the next review behind a Pro paywall. Athletes who return after 30+ days get a fresh 3-review window. This preserves the W1 Economics concern (indefinite-free dilution) while keeping the W2/W1-Demand position that the activating moment must land on free. Net effect: free tier becomes a 3-week activation funnel, not an indefinite parking spot.
3. **Concierge → auto handoff is the riskiest transition and must be priced honestly.** The W2 design correctly puts a parallel auto-cohort at week 5 of exp-4. If auto-cohort 8-week retention drops below 50% relative to concierge (K-W1-5), the SaaS thesis is weak. **Cost projection:** at scale, infrastructure cost per Pro user is ~$1.20/mo (Strava API + Garmin webhooks + push + storage + email at 12/yr/user × ~$0.10 each); gross margin at $12/mo is ~90%. This is healthy *if and only if* support and refund rates stay below 5%. The cap-setting onboarding is the largest support-load risk.

**My change requests:**
- Add an **8-week cohort comparison** to exp-4 outputs (not just 6 weeks) by extending the auto-handoff cohort from week 5 to week 13.
- Add an **explicit activation-rate kill criterion** (below).
- Free tier as 3 Weekly Reviews capped, not indefinite. (Founder synthesis below adopts this.)

**My kill criteria (added):**
- **K-W2-4:** If exp-4 activation rate (signup → first complete Weekly Review with ≥3 sessions and cap set) is below 60%, the onboarding is broken; do not scale to exp-2 LP traffic until it's fixed.
- **K-W2-5:** If auto-handoff cohort 8-week retention is <50% of concierge cohort retention, the SaaS thesis is broken; PIVOT to a hybrid coach-assisted model or KILL.

---

## §6. Founder synthesis

**Decisions locked**

1. The product is a behavior loop. Sync → Deviation digest (≤2 h SLA) → 30-second review → Sunday Weekly Review → Aerobic Trend reward.
2. Activating moment is the first Sunday Weekly Review, delivered within 7 days of signup.
3. Five metrics at MVP (Adherence %, Drift minutes, Aerobic Trend, Recovery delta — descriptive, Weekly distribution). Everything else suppressed.
4. Free tier delivers **3 Weekly Reviews capped** (Economics §5 #2 adopted), then prompts upgrade. Pro adds creep alerts, drift attribution, full history, Aerobic Trend, Recovery delta, benchmark protocols.
5. Cap-setting protocol with four options; **MAF gated behind an acknowledgment, Aerobic Trend hidden until calibration test completed** (Science §4 #1 adopted).
6. Recovery delta is descriptive only, with mandatory medication / cardiac onboarding question, and is suppressed for at-risk users (Science §4 #2 adopted).
7. Aerobic Trend uses a 14-day rolling window with noise labels (Science §4 #3 adopted).
8. Intensity-creep alert SLA: 2 hours from sync, with an 8pm local digest fallback.
9. exp-4 concierge pilot extended to **13 weeks total** with parallel auto-handoff cohort starting week 5 (Economics §5 #1 adopted; supersedes the original 6-week scope).
10. exp-2 LP must include a real Weekly Review video, not a chart screenshot (Demand §3 #3 adopted).

**Reopened from W1**

- Free-tier shape (was "indefinite, capped"; now "3 Weekly Reviews capped"). A-W1-9 updated accordingly in the register.
- exp-4 scope extended from 6 to 13 weeks; cost estimate revised upward (~$3,200 founder-time-cost). Acceptable.

**Items pushed to later workshops**

- **W3 (Integrations):** validate Strava webhook latency against the 2 h SLA; confirm Garmin HealthAPI webhook scope is sufficient; decide MVP coverage for Wahoo, Coros, FIT manual upload; defer swim entirely.
- **W4 (Economics):** model the 3-review-cap free tier conversion; revisit pricing if exp-4 WTP signal is weak; refine 8-week handoff retention assumption with auto-cohort data.
- **W5 (Science & Credibility):** full edge-case catalog (heat, altitude, illness, sensor error, cardiac drift); cap-setting language and confidence labels; Recovery delta wording library; Goodhart counter-metric design.
- **W6 (Trust):** medication / cardiac data sensitivity; Strava and Garmin TOS review for "weekly review" content type; data retention for athletes who downgrade from Pro to free.

**New entries for the assumption register**

- **A-W2-1:** The 5-metric set is sufficient to drive behavior change in the Convert without users demanding TSS / FTP / etc. as add-backs. *Owner: W2 / W4 retention data.*
- **A-W2-2:** The 2-hour creep-alert SLA is technically achievable on Strava + Garmin webhooks for ≥80% of synced workouts. *Owner: W3.*
- **A-W2-3:** Cap-setting wizard completion rate ≥75% of signups within 7 days. *Owner: exp-4 + first-100-signups instrumentation.*
- **A-W2-4:** Auto-version of the loop produces ≥70% of the behavior change observed in the concierge cohort. *Owner: K-W1-5 / K-W2-5 via exp-4 auto-handoff.*
- **A-W2-5:** "Recovery delta" descriptive language is interpretable by athletes without prompting clinical-style decisions. *Owner: W5 + qualitative pilot signal.*

**New entries for the kill-criteria ledger**

- K-W2-1 (Demand): LP loop-led variant must beat data-led variant by ≥30% relative on email capture.
- K-W2-2 (Science): Cap-setting drop-off >25% in concierge → redesign onboarding before exp-2 scaling.
- K-W2-3 (Science): Any Recovery-delta clinical-harm signal → kill feature pending W5/W6 policy.
- K-W2-4 (Economics): Activation rate <60% (signup → first complete Weekly Review) → halt LP scale-up.
- K-W2-5 (Economics): Auto-handoff cohort 8-week retention <50% of concierge → PIVOT or KILL the SaaS thesis.

**Workshop 3 inbox (Integrations & Ecosystem)**

- Strava webhook latency distribution — does ≥80% of traffic meet the 2 h SLA?
- Garmin HealthAPI scope — is workout-level HR stream available via webhook, or is polling required? What is the polling interval ceiling?
- Wahoo, Coros, Polar, Suunto, Apple — which are MVP-mandatory for the Convert ICP, and which can be deferred to FIT manual upload?
- Activity-type taxonomy mapping across providers (we need run + ride at MVP; everything else suppressed or manual-tagged).
- Coach role: not a launch dependency, but should the data model accept coach-shared accounts at MVP-1 to enable a Phase-2 distribution channel?
- Strava and Garmin TOS — does delivering a "Weekly Review" derived from their data violate any clause? (Hand-off to W6 if so.)

**Confidence after Workshop 2:** ~55% the loop drives behavior change at SaaS-defensible rates (up from ~40% post-W1). The remaining gap is concentrated in three places: (a) auto-handoff retention vs. concierge, (b) cap-setting accuracy in the wild, (c) whether the 5-metric restraint holds against user requests for incumbent-style dashboards. exp-4 with the extended auto cohort addresses (a) directly and gives early signal on (b) and (c).
