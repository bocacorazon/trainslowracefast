# Workshop 5 — Science, Safety & Coaching Credibility

*Primary agent: Science & Coaching Credibility Reviewer*
*Secondary reviewers: Behavior Change Product, Demand & Positioning, Trust & Platform Risk Architect*
*Inputs: W1 ICP, W2 (5 metrics, cap protocol, Recovery delta as descriptive-only), W3 (Strava-stream fidelity caveat), W4 (provisional-cap path, Y1 ARR target).*

---

## §1. Executive summary

**Locked decisions**

- **Adherence % is redefined as "Real Easy Minutes / total easy-tagged moving time"**, where Real Easy Minutes excludes (a) stops > 2 minutes and (b) speeds below 3 km/h (run) or 8 km/h (ride). This closes the Goodhart loophole from W2 §2c #1 without adding a competing metric.
- **Cap confidence is a three-state label** visible on every chart and email that uses the cap: *Provisional*, *Data-validated*, *Coach/Lab-validated*. Aerobic Trend remains hidden until at least *Data-validated* (≥4 weeks consistent data **or** one decoupling test).
- **Recovery delta is a four-state machine**: *In-baseline* / *Slightly elevated* / *Slightly low* / *Suppressed*. Wording library is locked (§2b). No imperatives, no diagnoses, no "rest today", no composite "readiness scores".
- **Mandatory onboarding question:** "Are you on heart-rate-affecting medication, or do you have a known cardiac condition?" Yes → Recovery delta suppressed; HR-based Adherence is downgraded with a "talk to your physician" notice; HRmax-derived calculations excluded entirely.
- **Self-tagging recovery context** (opt-in) lets athletes annotate "short sleep / alcohol / illness / stressful day" against any HRV reading. Turns a descriptive number into the athlete's *own* pattern-recognition without us prescribing.
- **Aerobic Trend display rules:** 14-day rolling window, minimum 4 sub-cap data points before display, CV-based "noisy data" label when pace-at-cap variance exceeds 12%.
- **Claims policy adopted: positive-frame only.** Marketing copy says what the product *does* surface ("we show you when you drifted hard"; "we show whether your aerobic engine is improving"); never claims clinical, diagnostic, or therapeutic outcomes. Distributed as a copy library to creator-affiliate partners (W4 §3 #3).
- **LLM-output policy locked at MVP-zero:** no LLM-generated content in any user-facing surface (Weekly Reviews, alerts, captions). When/if added later, all LLM output is bound by the same claims policy and reviewed by a domain reviewer before shipping.
- **Edge-case handling catalog locked** (§2c) covering heat, altitude, illness, sensor error, cardiac drift, medication, pregnancy, arrhythmia, age/seasonality. Each case has a defined handling rule (annotate / filter / suppress / prompt).

**Open / reopened items**

- Adherence redefinition (Real Easy Minutes) needs in-pilot validation; it changes baseline numbers from any prior calculation, so it's locked *before* exp-4 starts logging cohort data.
- Streak mechanic threshold (Behavior §3 #1): an "easy day in green" requires Real Easy Minutes ≥ 80% of easy-tagged moving time. Open to revision after 4 weeks of pilot data if green rate is too punitive or too lenient.
- The Recovery-delta opt-in context tagger needs careful UX so it doesn't drift into self-diagnosis. Surface design happens at MVP build; principles are locked here.

**Confidence after Workshop 5:** ~75% the science posture is defensible at MVP and protects the brand against credibility attacks. Remaining risk is execution: the wording library must be enforced consistently across product copy, marketing, creator partners, and any future AI-generated content.

---

## §2. Primary memo — Science & Coaching Credibility Reviewer

### §2a. Q1: What is the strongest version of the problem in your lane?

A low-intensity training app sits closer to "health-adjacent" territory than a generic training-log because:

1. **Heart rate is the central signal**, and HR interpretation is medication-, condition-, and context-sensitive in ways the athlete may not realize.
2. **The method itself is contested in popular discourse.** Zone 2 / MAF / polarized are real frameworks but are routinely oversimplified by influencers ("180-age and you're done"). Athletes arrive with bad caps inherited from podcasts.
3. **Recovery / HRV metrics are the most common place consumer apps cross the line** from descriptive into prescriptive. "Recover today" and "you're overtrained" are clinical inferences the apps making them are not licensed to provide.
4. **Athletes use these tools to self-coach during medical events** (illness, pregnancy, return from injury). Anything that nudges toward "go anyway" or "rest, you're broken" can cause real harm.
5. **The wedge depends on credibility.** "Train slow, race fast" is built on a real physiology claim — that volume at low intensity drives durable aerobic adaptation. If our metrics or claims undermine that with sloppy science, the brand collapses faster than at competitors who already have wider claim moats.

The honest framing: **science discipline is not a constraint on the product; it is the product's defensibility.** Intervals.icu, TrainingPeaks, and Garmin Connect can throw out approximate metrics because they don't promise a specific physiological frame. We do. The discipline is the wedge.

### §2b. Q2: What proposal best addresses that problem?

#### Cap-setting protocol — confidence labels

| State | Source | What's enabled | Recheck cadence |
|---|---|---|---|
| **Provisional** | MAF 180-age, talk-test-only, or copied from a coach without a session test | Adherence %, Drift minutes, Weekly distribution. **Aerobic Trend is hidden.** Recovery delta available subject to medication suppression rule. | Prompt at week 4 to run a decoupling test |
| **Data-validated** | One completed 30-min decoupling test OR ≥4 weeks of consistent training where Adherence rolling avg is 70–95% (the data itself shows the cap is plausible) | All Five metrics, full Aerobic Trend, benchmarks | Recheck every 90 days |
| **Coach/Lab-validated** | Coach-supplied LT1 or aerobic threshold, or athlete uploads lab test result | All Five metrics with highest-confidence label | Recheck every 180 days |

Visual: every chart, email, and Weekly Review shows a small badge ("Provisional cap" / "Data-validated cap" / "Coach-validated cap"). On hover/tap, "What does this mean?" link explains the limits.

Hard rule: **max-HR-percentage formulas are excluded** as a cap-setting option. They are the largest single source of bad caps in the wild. If an athlete asks for one, the help center explains why and offers the talk test or decoupling test instead.

#### Adherence redefinition — Real Easy Minutes

**Old definition (W2):** % of easy-tagged time at-or-below cap.

**New definition (W5):** Real Easy Minutes / total easy-tagged moving time, where Real Easy Minutes is:

- Time at-or-below cap on easy-tagged sessions
- **Excluding** any continuous stop > 2 minutes (idle at lights, rest stops, photo breaks)
- **Excluding** any time below 3 km/h (run) or 8 km/h (ride) — i.e., effectively walking / coasting
- **Excluding** any time below the cap that is also above an HR plausibility floor (filters sensor dropouts to zero/very-low)

This closes the gaming loop: walking uphill no longer "earns" Adherence credit; long rest stops don't pad the percentage. The athlete sees the same Adherence headline, but it now reflects *real* aerobic time, not stopwatch time.

The streak mechanic ("9 of 10 easy days in green") counts a day as green if Real Easy Minutes ≥ 80% of easy-tagged moving time. The 80% threshold (rather than 95% or 100%) tolerates natural undulation (a steep climb, a brief surge to clear traffic) without breaking the streak.

#### Recovery delta — state machine and wording library

State definitions (computed against athlete's 14-day rolling baseline):

| State | Trigger | What we display |
|---|---|---|
| **In-baseline** | HRV within ±0.5 SD of 14-day baseline | "HRV in your usual range." Plain icon. |
| **Slightly elevated** | HRV > +0.5 SD of baseline | "HRV slightly above baseline. Common context: rest day, lighter week, post-recovery rebound." |
| **Slightly low** | HRV < −0.5 SD of baseline | "HRV slightly below baseline. Common context: short sleep, alcohol, training load, life stress, illness onset, dehydration. *Self-tag context →*" |
| **Suppressed** | Athlete declared medication / cardiac condition; or RHR > +20% above baseline for 3+ days (illness flag) | Recovery delta hidden. "Recovery insight is paused. Talk to your physician about training-readiness signals." |

Hard rules (non-negotiable; enforced in product copy and any future AI-generated text):

- **No imperatives.** Never "rest today", "skip your workout", "go easy", "you should".
- **No diagnoses.** Never "you may be sick", "you are overtrained", "you have not recovered".
- **No composite "readiness" or "training-load" scores.** Garmin and Whoop ship these; they are pseudo-clinical and we don't.
- **No comparisons across people.** HRV is individual; never a leaderboard or peer chart.
- **Always context, never cause.** "Common context can include..." not "this is because...".

Self-tagging context (opt-in): athlete can mark any reading with one or more tags from a fixed vocabulary (sleep < 6h, alcohol, stressful day, ill, post-travel, post-race, menstrual cycle [opt-in], allergies). After 30+ tagged readings, an "Your patterns" view shows aggregates *only* — never causal inference.

#### Aerobic Trend — display rules

- **14-day rolling window minimum.** Single workouts never move the chart.
- **Minimum 4 sub-cap data points** before any trend is displayed. Below that: "Need more data."
- **CV-based noise label.** When the coefficient of variation of pace-at-cap-HR exceeds 12% within the window, display "Noisy data — interpret with care" and dampen the visual emphasis (lighter line, larger confidence band).
- **No regression to "your VO2max"**, "your fitness score", or any external scoring. The chart shows pace at sub-cap HR over time, period.
- **Provenance label.** The chart shows whether the underlying HR streams came from Strava (slight downsampling possible, ±2 pp note) or directly from a manufacturer integration (FIT-grade fidelity). Resolves the W3 §6 push for transparency.

#### Claims policy — what we say

**Approved positive claims** (used in marketing, LP, app copy, creator-partner library):

- "Helps you spend more time training in your aerobic zone."
- "Shows you when an easy day drifted hard, within 2 hours."
- "Tracks your aerobic efficiency over time, against the cap you set."
- "Helps you stick with the training plan you already chose."
- "We don't tell you how to train. We help you actually do the training you chose."

**Prohibited claims** (no exceptions):

- "Improves VO2max / fitness / health."
- "Prevents overtraining / injury / illness."
- "Predicts your race time."
- "Diagnoses fatigue / overtraining / readiness."
- "Replaces your coach / doctor / physiologist."
- Any claim involving cardiovascular health benefits ("better heart health", "lower resting HR for longevity").
- Any claim involving weight management or body composition.
- Any clinical-sounding score ("Body Battery", "Recovery Score", "Readiness").

The library is published as a one-page document in the creator-affiliate kit (W4 §3 #3). Creators who use prohibited claims in promoting the product are flagged and re-trained; repeat violations end the partnership.

#### LLM-output policy

At MVP-zero, no LLM-generated content appears in any user-facing surface — Weekly Reviews, creep digests, in-app explanations are all template-driven, with deterministic numbers and a fixed wording library. When/if an LLM is introduced later (e.g., personalized weekly narrative):

- Output is bound by the same claims policy.
- A domain reviewer (founder + at least one credentialed reviewer) audits 100% of output during the first 30 days post-launch; sampled thereafter at ≥10%.
- Any prohibited-claim slip is treated as a P1 incident (rollback within 24h).

This is not a "we don't have time for AI" position. It is a *policy lock* so the discipline survives a future founder-pressure decision to ship LLM-generated copy quickly.

### §2c. Q3: What failure mode or unresolved unknown remains?

#### Edge-case handling catalog

| Edge case | Detection | Handling |
|---|---|---|
| **Heat / humidity** | Activity-day weather lookup (free APIs); flag if WBGT > moderate | Annotate workout: "Hot day — HR was elevated; this is expected." Don't auto-adjust cap or Adherence; just contextualize the result |
| **Altitude** | GPS elevation > 1500 m or known location flagged | Annotate: "High altitude — HR runs higher at the same effort." No silent adjustment |
| **Illness onset** | RHR > +20% baseline for 3+ days **or** HRV < −1 SD for 3+ days **or** athlete self-tag | Prompt: "Your resting signals look unusual — common context can include illness, life stress, or training load. Tag this so we don't penalize Adherence." Skip-mode: athlete can mark days as "skip from analysis" |
| **Cardiac drift on long efforts** | Within-workout HR rises >5% relative to first-30-min ratio | Annotate the activity; Adherence calculated over the full workout still — this is real drift, not a sensor artifact. Educational note explains what cardiac drift is |
| **Sensor error — chest strap dropout** | Implausible jumps (>20 bpm/sec), sustained zeros, sustained HRmax+ readings | Filter affected segments; exclude from Adherence; show "sensor data noisy in this section" annotation. If >25% of session affected, exclude the workout from rollups |
| **Sensor error — optical wrist artifact** | Run cadence-locked HR signature (HR == cadence ± 5 bpm for >2 min); bike vibration patterns | Filter; exclude from Adherence; gentle suggestion at workout view to use a chest strap |
| **Beta-blockers / HR-affecting meds** | Athlete declared at onboarding | Suppress Recovery delta; downgrade HR-based Adherence with caveat; do not show HRmax-derived calculations; recommend coach-supplied or lab cap |
| **Pregnancy** | Athlete-declared (sensitive; opt-in only, not required) | Cap-recheck prompt at declaration; no claims about training during pregnancy; "talk to your physician about training intensity" notice |
| **Atrial fibrillation / arrhythmia** | Athlete-declared cardiac condition | Suppress Recovery delta; suppress Aerobic Trend (HR-noise dominates); show Adherence with "your HR readings may be noisy due to your declared condition" caveat. Strongly recommend coach-supervised training |
| **Stimulant / caffeine load** | Not detected — out of scope | No handling. Athletes who notice patterns can self-tag context |
| **Aging / seasonal cap drift** | Adherence trend rolling 60-day; if rising while Aerobic Trend stalls, prompt cap-recheck | Suggest decoupling re-test |
| **Talk-test fidelity** | Self-reported only at onboarding | Talk-test cap defaults to *Provisional*; encourage decoupling test for upgrade |
| **Novice misunderstanding** | First-30-day cohort; if Adherence is consistently ≥95% and Aerobic Trend stalls, the cap may be set too high | Auto-prompt cap-recheck after 4 weeks for novice users specifically |

Each row in this table will be encoded as a rule in the analytics pipeline at MVP build time. The catalog is the source of truth.

#### Open / unresolved items

1. **Cap re-test compliance.** Provisional caps are supposed to upgrade within 4 weeks via a decoupling test. If athletes don't run the test (compliance < 50%), Aerobic Trend stays hidden for many users and the "race fast" payoff goes invisible. Tracked as A-W5-1 below; resolved by exp-4 + first-100-signups.
2. **Self-tagging context UX.** Recovery-delta context tagging is a powerful tool for the engaged athlete and a confusing surface for the novice. UX needs careful staging — likely default-off for first 30 days, gentle suggestion afterward. Owned by W2 product surface.
3. **Edge-case detection coverage.** Some cases (heat/altitude annotation, sensor filtering) require external data and ML-style classifiers. MVP coverage is the *high-value* cases (sensor dropout, optical artifact, illness onset, cardiac drift); altitude/heat annotations can be Phase-2.
4. **Creator-partner discipline.** The claims policy is enforceable internally. Externally, a creator's freeform podcast voice may not reliably stay inside the lane. Mitigation: kit + one-pager + quarterly review of partner content. If a partner systematically overclaims, end the partnership (acceptable CAC hit; protects brand).
5. **Provenance and Strava-stream fidelity.** The W3 ±2 pp Adherence margin from Strava-downsampled streams is annotated, but at the cohort level, athletes on Strava-only data systematically read 1–2 pp lower Adherence than athletes on direct Garmin. Documented; not a defect.

---

## §3. Secondary memo — Behavior Change Product Strategist

Two notes.

1. **Real Easy Minutes is correct, but the streak threshold needs piloting.** 80% of moving time is reasonable in theory; in practice, a hilly route may deliver 60% comfortably. Recommendation: ship at 80% but instrument distribution; if 50%+ of athletes break the streak weekly on terrain they can't change, the threshold is too tight. Adjust within first 4 weeks of exp-4. (This is why the W5 confidence is 75% not higher — a tunable.)
2. **Self-tagging context is a behavior-loop multiplier.** Athletes who tag their own patterns build *self-knowledge*, which is a stronger motivator than any chart we ship. Recommend surfacing tagged-context aggregates in the monthly recap ("Your easy days drifted hardest after short-sleep nights"). Reinforces the W2 monthly stage of the loop without crossing into prescription.

**My kill criterion (added):**
- **K-W5-1 (Behavior):** If, in the first 4 weeks of exp-4, more than 50% of athletes break their streak weekly *on routes they did not choose to make hard* (terrain, weather), the Real Easy Minutes threshold is mis-calibrated; rework the streak rule before scaling.

---

## §4. Secondary memo — Demand & Positioning Strategist

Two notes on messaging.

1. **The positive-claim library is on-brand.** "We help you actually do the training you chose" is the most resonant of the bunch and may deserve a hero slot on the LP, alongside or instead of "Easy days, done right". Test as a third LP variant in exp-2.
2. **The "no readiness score" position is a marketing weapon, not a constraint.** Garmin's Body Battery, Whoop's Recovery Score, and Oura's Readiness are *the* category-archetype features in wearable land. Explicitly *not* having one is a credibility flag in our audience: the Convert is suspicious of vibes-based metrics. Recommend a short LP or onboarding paragraph: "We don't ship a 'readiness' score because no one — not us, not Garmin, not your watch — can reliably tell you whether to train today. We show you your data; you and your body decide." This is on-brand for the founder and the wedge.

**My kill criterion (added):**
- **K-W5-2 (Demand):** If creator partners or community channels consistently push the product using prohibited claims (e.g., "improves your VO2max", "predicts your race"), and re-training fails over 30 days, end the partnership and document. If three or more partnerships end this way in Y1, the claims policy is unenforceable at scale and brand strategy must be rethought.

---

## §5. Secondary memo — Trust, Privacy & Platform Risk Architect

Three notes that hand off to W6.

1. **HRV and the medication onboarding question are sensitive personal health data.** They must be stored encrypted at rest, never used for advertising, never shared with creators (the affiliate program shares conversion attribution only — no athlete data — confirmed). W6 will detail retention, deletion, and jurisdictional posture (GDPR Article 9 special category data may apply for HRV in EU; CCPA "sensitive personal information" definitions in CA).
2. **The "what we display under your conditions" page is a Trust feature.** If an athlete declares beta-blockers, they should see — *before* completing onboarding — a plain-language summary: "You'll see Adherence and Drift minutes. You won't see Recovery delta or Aerobic Trend at full confidence. Here's why." This is informed consent applied to product feature suppression. Build into MVP onboarding.
3. **Self-tagging context creates a richer health dataset over time.** This is more sensitive than raw HRV. W6 must specify: athlete can delete any tag at any time; bulk-delete all tagged context; export all tagged context; tags are never used for any analysis other than the athlete's own pattern view. Marketing/research use of aggregated tag data requires explicit opt-in beyond the standard ToS.

**My kill criterion (added):**
- **K-W5-3 (Trust):** If any provenance audit shows tagged-context data, HRV streams, or declared medical conditions used in any analysis or surface beyond the athlete's own account (including aggregate marketing reports without opt-in), the breach is treated as P0; disclose and remediate. This is a hard rule, not a kill criterion in the conditional sense — it must never happen.

---

## §6. Founder synthesis

**Decisions locked**

1. Adherence % redefined as Real Easy Minutes / easy-tagged moving time, with explicit exclusions for stops > 2 min and sub-walking-speed segments.
2. Streak threshold: an "easy day in green" requires Real Easy Minutes ≥ 80% of easy-tagged moving time. Tunable in first 4 weeks of exp-4 (Behavior §3 #1).
3. Cap confidence labels: Provisional / Data-validated / Coach-Lab-validated. Aerobic Trend hidden until ≥ Data-validated.
4. Max-HR-percentage formulas excluded as cap-setting options. MAF gated behind acknowledgment (W2 §4 #1 reaffirmed).
5. Recovery delta state machine: In-baseline / Slightly elevated / Slightly low / Suppressed. Wording library locked. No imperatives, no diagnoses, no readiness composites.
6. Mandatory medication / cardiac onboarding question; suppression rules applied to the at-risk subset.
7. Self-tagging context (opt-in) for Recovery delta; default-off for first 30 days; aggregates surfaced in monthly recap (Behavior §3 #2 adopted).
8. Aerobic Trend display: 14-day window, 4-data-point minimum, CV >12% noise label, provenance label (Strava-downsampled vs. direct).
9. Claims policy locked: positive frame only; prohibited list explicit; published as creator-partner copy library.
10. LLM-output policy: no LLM at MVP; same claims policy applies if added later; domain-reviewer audit on rollout.
11. Edge-case catalog locked (§2c table) — encoded as analytics pipeline rules at build time.
12. "What we display under your conditions" pre-onboarding consent surface for at-risk users (Trust §5 #2 adopted).
13. Marketing position: explicitly "we don't ship a readiness score" as a credibility differentiator (Demand §4 #2 adopted).
14. Test "We help you actually do the training you chose" as a third LP variant in exp-2 (Demand §4 #1 adopted).

**Reopened from earlier workshops**

- W2 Adherence definition tightened (now Real Easy Minutes). Affects exp-4 instrumentation: must compute Adherence both ways for first 4 weeks to compare.
- W2 streak mechanic now specified at 80% threshold; tunable.

**Items pushed to later workshops**

- **W6 (Trust):** GDPR / CCPA classification of HRV and medical-disclosure data; encryption-at-rest spec; retention; jurisdictional handling; affiliate program data-sharing audit; opt-in posture for any aggregated research use of tagged context.
- **W7 (Red Team):** scenarios where the claims discipline breaks under marketing pressure; scenarios where a creator partner publishes a viral overclaim; medical-incident scenarios (athlete on beta-blockers ignores the suppression notice and trains hard, has an event); sensor-failure-cascade scenarios.

**New entries for the assumption register**

- **A-W5-1:** Provisional-cap users complete a decoupling test (cap upgrade) within 4 weeks at ≥50% rate, unlocking Aerobic Trend. *Owner: exp-4 + first-100 signups.*
- **A-W5-2:** Real Easy Minutes definition produces Adherence values that are within ±5 pp of athlete-perceived adherence (qualitative match) on the Convert ICP. *Owner: exp-4 qualitative debrief.*
- **A-W5-3:** The four-state Recovery delta language is interpretable without prompting clinical-style decisions. *Owner: exp-4 qualitative + post-MVP support-ticket review.*
- **A-W5-4:** Edge-case detection (sensor dropout, optical artifact, illness flag, cardiac drift) catches ≥80% of in-the-wild instances at MVP. *Owner: post-MVP first 6 months; instrumented via tagged-error-rate.*
- **A-W5-5:** Claims policy is enforceable across creator partners with quarterly review and ≤1 partnership-ending incident per quarter. *Owner: ongoing.*

**New entries for the kill-criteria ledger**

- K-W5-1 (Behavior): >50% of athletes break the streak weekly on routes they didn't choose to make hard → rework Real Easy Minutes threshold.
- K-W5-2 (Demand): ≥3 creator partnerships end in Y1 due to prohibited-claim violations → claims policy unenforceable at scale; rethink brand-channel strategy.
- K-W5-3 (Trust, hard rule): tagged-context, HRV, or declared medical condition data used outside the athlete's own account without opt-in → P0 breach; disclose and remediate. (Not conditional; must never happen.)

**Workshop 6 inbox (Trust, Privacy & Platform Dependency)**

- HRV and medical-disclosure data classification under GDPR Article 9 and CCPA "sensitive PI"; jurisdictional storage rules.
- Encryption-at-rest spec, key management, audit logging.
- Retention policy: free tier vs. Pro vs. lapsed Pro vs. deleted account.
- Strava and Garmin TOS: medical-adjacent claim language must not appear on platform-rendered surfaces (e.g., we never write descriptive medical context on a Strava activity description).
- Affiliate program data-sharing audit: confirm no athlete data flows to creators beyond conversion attribution.
- Opt-in posture for aggregated research / product-improvement use of tagged context (default off; clear language).
- Pause-subscription data treatment: data preserved, no analytics run during pause.
- Cross-athlete data leak prevention (W3 K-W3-3 still binding); surface and audit policies.
- "What we display under your conditions" consent surface — copy and review with a privacy-counsel-equivalent reviewer.

**Confidence after Workshop 5:** ~75% the science posture is defensible at MVP and protects credibility against critique. The remaining risk is execution — keeping the wording library consistent across product copy, Weekly Reviews, marketing surfaces, and (when added later) any AI-generated content. The discipline is what makes the wedge real; the workshop has now defined it explicitly enough that violations are visible.
