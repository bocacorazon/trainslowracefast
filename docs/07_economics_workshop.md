# Workshop 4 — Pricing, Retention & Distribution Economics

*Primary agent: SaaS Economics Analyst*
*Secondary reviewers: Demand & Positioning, Behavior Change Product, Integrations & Ecosystem*
*Inputs: W1 ($12/mo lock, $40 CAC target), W2 (loop, 5 metrics, 3-Weekly-Reviews-capped free tier, exp-4 design), W3 (provider cost stack, Strava-first scope).*

---

## §1. Executive summary

**Locked decisions**

- **Pricing baseline confirmed:** $12/mo or **$120/yr (17% off)**. Annual discount tightened from W1's 25% (was $108) to 17% to protect LTV; revisits if exp-1 WTP signal is weak (K-W1-7 still binding).
- **Free-tier shape locked at MVP:** 3 Weekly Reviews capped (W2). Designed so the *fourth* review is the upgrade prompt with a locked preview. Instrumented to A/B against a 14-day full-Pro trial once signup volume reaches 1,000.
- **Activation definition (binding):** signup → Strava OAuth (or FIT upload) → cap set → ≥3 sessions in window → first Weekly Review delivered. Window extended from 7 to **14 days** to absorb the "low-frequency week" tail (Behavior §4 adopted).
- **Provisional cap path adopted at activation:** athletes can complete activation with a provisional MAF cap (Aerobic Trend remains hidden until calibration test, per W2/W5). Removes cap-setting as the largest funnel block (Behavior §4 #1).
- **Channel mix Y1 target:** 35% creator partnerships, 25% organic content/SEO, 15% community/forums, 10% Strava clubs, 10% coach-led (MVP-1), 5% paid test budget. Blended CAC target **$25** (well inside the W1 $40 ceiling).
- **Y1 milestone goal:** 1,000 paid users by month 12, ~$120k ARR. This is a viability proof, not a venture target.
- **Coach channel (MVP-1) is a real channel, not a savior.** Modeled CAC ≈ $43/paid athlete; treated as one of five contributors, not a multiplier.
- **Mobile app stays deferred.** Web-first decision from W3 holds; iOS app reconsidered only if Y1 retention shows >10pp gap by platform.

**Open / reopened items**

- Annual discount (17% vs. 25%) is the largest pricing lever; resolved by exp-1 WTP plus exp-3 deposit conversion.
- M3 retention is the gating retention milestone; W2 loop must deliver ≥55% to clear LTV/CAC ≥3x.
- Garmin partnership cost (W3 K-W3-4 capped at $25k/yr) is the largest single line item in the COGS model; ARR/COGS sensitivity below.
- Coach onboarding cost: founder time only at MVP-1; if it scales beyond ~30 coaches it needs a tool.

**Confidence after Workshop 4:** ~60% the unit economics work at the W2 retention assumption. Confidence drops to ~40% if M3 retention falls below 50%. The W2 loop is the load-bearing assumption for the entire model.

---

## §2. Primary memo — SaaS Economics Analyst

### §2a. Q1: What is the strongest version of the problem in your lane?

A niche endurance SaaS at $12/mo has three economic enemies:

1. **Substitute price-anchoring.** Intervals.icu Supporter is $4/mo and has more charts than we'll ever ship. If our wedge degrades into "another analytics app," the price ceiling collapses to $5–7/mo and the business doesn't clear costs.
2. **Fitness-app retention curves.** Consumer fitness apps shed 50–70% of users in the first 90 days. The W2 loop must beat this curve, not match it. If we land at industry M3 retention (~40%), LTV is ~$48 — below any defensible CAC.
3. **The "feature, not a business" trap.** If we acquire users one-by-one through paid ads at fitness-app CACs ($60–150), we lose money on every signup regardless of price. The economics work *only if* organic, creator, community, and coach channels carry ≥85% of signups.

The honest framing: **economics work or fail on M3 retention and CAC composition, not on price.** Pricing has roughly a 1.5x sensitivity range ($9–$15) within which the model can survive; retention has a 3x range that decides viability outright.

### §2b. Q2: What proposal best addresses that problem?

#### Pricing

| Plan | Price | vs. previous | Notes |
|---|---|---|---|
| Free | $0 | unchanged | 3 Weekly Reviews capped; current+last 2 weeks adherence; cap-set wizard; full data export |
| Pro monthly | $12/mo | unchanged | All Five metrics, creep alerts, drift attribution, Aerobic Trend, benchmarks, full history |
| Pro annual | **$120/yr (17% off)** | **was $108 (25% off)** | Higher LTV per annual; still meaningfully discounted vs monthly |
| Coach seat | $0 | new | Free read-only, MVP-1 only; gated to coach-supplied list of consenting athletes |

Rationale for moving annual to 17%: the 25% discount in W1 was set to drive conversion, but it left $144/year of full-price headroom on the table for athletes who would convert at any annual discount. 17% ($120) preserves a clear discount, lifts LTV by ~$12 per annual converter, and still beats TrainingPeaks ($135/yr) on price. Reverts to 25% if exp-1 WTP median lands below $10/mo.

Pricing stress test (full year, 1k Pro mix 40% annual / 60% monthly, M3 = 55%, M6 = 40%):

| Price point | Annual mix LTV | Monthly mix LTV | Blended LTV | At $25 CAC | At $40 CAC |
|---|---|---|---|---|---|
| $9/$99/yr (24% off) | $124 | $52 | $81 | 3.2x | 2.0x |
| **$12/$120/yr (17% off)** | **$165** | **$70** | **$108** | **4.3x** | **2.7x** |
| $15/$150/yr (17% off) | $208 | $87 | $135 | 5.4x | 3.4x |

LTVs assume 91% gross margin (see §2d) and a 2-year amortization cap on annuals.

Reading the table: $12 holds against the W1 $40 CAC at marginal LTV/CAC (2.7x), and works comfortably at the achievable $25. $15 looks better on paper but the conversion-rate hit at premium pricing in this niche is unmodeled and likely 25–40%; net revenue is probably worse. **$12 stays.**

#### Free-tier model

- 3 Weekly Reviews capped (covers ~3 weeks of athlete history).
- Week 4 review preview is locked behind a "Continue with Pro" prompt, with the Aerobic Trend chart unlocked as the visible carrot.
- Free → paid conversion target: **6%** within 60 days of activation. Industry analog: Spotify free→premium is ~46% but with a different value calculus; Notion-style limited-feature free is 2–5%; ours is closer to "limited-time-on-key-feature" at 4–8%.
- Returning lapsed users get a **fresh 3-review window** every 90 days (sympathy mechanic; preserves trust at no margin cost; instrumented).
- Alternative shape (14-day full-Pro trial) is **not** at MVP but is the next experiment once the funnel stabilizes; both shapes have defenders.

#### Activation funnel (target rates)

| Step | Target rate | Cumulative |
|---|---|---|
| LP visit → email signup | 4% | 4% |
| Email → account creation | 60% | 2.4% |
| Account → Strava OAuth (or FIT upload) | 75% | 1.8% |
| OAuth → cap set (any path, incl. provisional MAF) | 90% | 1.6% |
| Cap set → ≥3 sessions within 14 days | 85% | 1.4% |
| Sessions → first Weekly Review delivered | 95% | 1.3% |

Net: LP visit → activation = **1.3%**, account creation → activation = **54%** (just under the K-W2-4 60% threshold).

The 60% bar is met if either (a) OAuth completion rises to 80%+ via better UX, or (b) cap-set completion rises above 90% via the provisional path. Both are operational, not architectural. K-W2-4 stays as the binding kill criterion through exp-4.

#### Retention assumptions and LTV math

| Cohort milestone | Assumption | Source |
|---|---|---|
| M1 (activation → 30 days) | 75% | Healthy fitness-SaaS norm; behavior loop tailwind |
| M3 | **55%** | The gating number; exp-4 must support |
| M6 | 40% | Conservative against fitness-app norm |
| M12 | 28% | Industry baseline less ~5pp for niche |

Annual converter LTV (assumes 35% renewal at year 2, 25% at year 3): **$165 contribution.**
Monthly converter LTV (avg lifetime ~5.8 mo at the M1/M3/M6 curve): **$70 contribution.**
Blended (40% annual mix): **$108 LTV per paid user.**

Sensitivity: if M3 = 45% (industry norm), blended LTV drops to ~$78. At $25 CAC that's still 3.1x. At 35%, LTV ≈ $58, 2.3x — uncomfortable.

#### CAC modeling by channel

| Channel | Y1 mix target | Estimated CAC | Notes |
|---|---|---|---|
| Creator partnerships (Floris, Extramilest, MAF/zone-2 podcasts) | 35% | $25–40 | Sponsorships + affiliate; aligned audience converts well |
| Organic content / SEO | 25% | $5–15 | Founder-led; slow build; compounds Y2+ |
| Community / forums (Reddit, Slowtwitch, FB groups) | 15% | $0–10 | Founder-led; relationship discipline required |
| Strava clubs | 10% | ~$0 | Operate clubs, share aggregate adherence stats with consenting members |
| Coach-led (MVP-1) | 10% | ~$43/paid | See §2c; founder-time-driven |
| Paid test budget (Meta, Google) | 5% | $60–150 | Test only; do not scale until LP exp-2 stabilizes |

**Blended CAC target Y1: ~$25.** This is achievable only if the top three channels (creator, organic, community) actually carry 75% of signups. They are the make-or-break for the model.

Channel kill: if creator+organic+community share drops below 65% by month 6, blended CAC blows past $40 and W1 K-rule applies. Tracked as K-W4-2 below.

#### Coach channel (MVP-1) economics

- Each engaged coach has roughly 15–30 athletes already.
- Estimated funnel: 50% try → 70% activate → 30% pay = roughly **2.5–4.5 paid athletes per engaged coach**.
- Coach acquisition: founder-led demo, ~3 hours per coach. At $50/hr opportunity cost: ~$150/coach.
- Implied CAC: $150 ÷ 3.5 paid = **~$43/paid athlete** (in line with creator partnerships).
- Coach view is free; coach margin contribution is 0; the channel pays back through athlete subscriptions, not coach payments.
- This works at MVP-1 scale (≤30 coaches) without tooling. Beyond 30, coach onboarding needs self-serve flow — not at MVP scope.

### §2c. Q3: What failure mode or unresolved unknown remains?

#### Provider cost stack (10k Pro users) — bottoms-up

| Item | Monthly cost | Per-user/mo |
|---|---|---|
| Strava API | $0 | $0 |
| Garmin Activity API (assumed avg $12k/yr partnership cost) | $1,000 | $0.10 |
| Push (FCM/APNS) | $30 | $0.003 |
| Queues (Cloudflare/SQS) | $50 | $0.005 |
| Email (Postmark, ~10k Weekly Reviews + creep digests/mo) | $50 | $0.005 |
| Storage (FIT + derived data, ~180 GB/yr accreted) | $200 | $0.02 |
| App hosting (Vercel/Fly/AWS) | $300 | $0.03 |
| Stripe (2.9% + $0.30) | $360 (3% blended) | $0.36 |
| Support (assume 5% contact rate, 10 min × $25/hr) | $2,000 | $0.20 |
| Customer success / onboarding ops | $500 | $0.05 |
| **All-in COGS** | **$4,490** | **$0.78** |

Gross margin at $12 ARPU: **93.5%.** At $10 ARPU (heavy annual mix amortized monthly): 92%. Healthy.

At 1k Pro users, Stripe + support + hosting are the dominant lines, COGS ≈ $1.40/user (lower fixed-cost amortization), gross margin still ~88%. Breakeven against a single founder taking $80k/yr happens around **600–700 paid Pro users** (excluding marketing, coach onboarding time, and partnership amortization).

#### Y1 financial walk

| Metric | Conservative | Target | Stretch |
|---|---|---|---|
| Paid users month 12 | 500 | 1,000 | 1,800 |
| ARR month 12 | $60k | $120k | $216k |
| Blended CAC | $35 | $25 | $20 |
| Acquisition cost Y1 | $14k | $19k | $30k |
| COGS Y1 | $5k | $11k | $20k |
| Founder draw | $80k | $80k | $80k |
| Net Y1 (cash basis) | -$39k | +$10k | +$86k |

The model **does not require external capital** at the target trajectory, assuming the founder accepts the $80k/yr draw and the channel mix holds. Conservative case requires bridging ~$40k of personal runway through Y1 — a known small-business reality, not a flag.

#### Top three failure modes

1. **M3 retention < 50%.** Single biggest economic risk. Reading: K-W2-5 (concierge vs auto retention) is the leading indicator. If exp-4 auto-cohort retention disappoints, the W2 loop didn't land and economics break before pricing matters.
2. **Channel mix tilts to paid too early.** If creator+organic+community don't fill the funnel, paid spend takes over at $60–150 CAC and unit economics invert. K-W4-2 below catches this.
3. **Annual mix below 30%.** Annual is the LTV anchor. If athletes default to monthly and churn at month 4, blended LTV falls to ~$70, marginal at any CAC ≥$25.

#### Refund / chargeback policy

- 14-day money-back on monthly (no questions).
- 30-day money-back on annual.
- Chargeback budget: 1.0% of revenue.
- Annual prorated refund after 30 days: not offered; pause-subscription option instead (preserves LTV).

---

## §3. Secondary memo — Demand & Positioning Strategist

Three notes.

1. **Annual discount move (25% → 17%) is correct *if* exp-1 confirms WTP.** If WTP median lands below $10/mo, drop annual back to $108 (25%) to preserve conversion. This is a 30-minute change post-evidence; don't overthink the lock.
2. **Free-tier framing.** "3 Weekly Reviews capped" reads as arbitrary in copy. Reframe in product surface as *"Try the first 3 Weekly Reviews on us"* with explicit progress indicator ("2 of 3 reviews used"). Friendly + transparent. The same language goes on the LP and in the Pro upsell. This is brand-level discipline, not pricing.
3. **Creator partnership terms matter.** Most creators in the durable-aerobic niche prefer affiliate over flat sponsorship (their audiences distrust ads). Recommendation: **30% lifetime affiliate** for the first 12 months on annual subscriptions; **20% on monthly**. At $120/yr × 30% = $36/conversion to creator. Implies creator-channel CAC ≈ $36 *only* on conversions (no fixed cost), which is more attractive than the $25–40 sponsorship range. Lock this affiliate structure now.

**My kill criterion (added):**
- **K-W4-1 (Demand):** If creator-channel conversion rate falls below 1.5% (visit → paid) on aligned podcasts/YouTube within 90 days of partnership, the Convert audience is not as concentrated in those creators as assumed; rework channel mix toward organic SEO and community.

---

## §4. Secondary memo — Behavior Change Product Strategist

Two notes, both on activation.

1. **The provisional-cap activation path is correct, but it must be reversible without data loss.** Athletes who activate with a provisional MAF cap must be able to upgrade to a validated cap (decoupling test, talk test, coach-supplied) without losing their Adherence % history. This is achievable only if Adherence % is computed lazily against the *currently set cap*, not snapshotted at write time. Architectural note for engineering: store HR streams; compute Adherence on read. Don't precompute and freeze.
2. **Coach channel surveillance risk.** If a coach can see an athlete's adherence in real time and uses it for coaching pressure, the W2 loop's "self-driven adherence" promise is broken. Mitigation: athlete-controlled visibility toggle; coach view requires explicit athlete consent during onboarding; athlete can revoke at any time. This is a Trust feature (W6) but the surface lives in W4's coach-channel model.

**My kill criterion (added):**
- **K-W4-3 (Behavior):** If activation rate via provisional-cap path is ≥10pp below activation rate via calibration-test path on a matched cohort, the provisional path is degrading the loop and must be reworked (likely by gating Aerobic Trend more tightly to encourage calibration without blocking activation).

---

## §5. Secondary memo — Integrations & Ecosystem Strategist

Two notes on cost.

1. **Garmin partnership engineering cost is missing from the model.** Even at $0 partnership fee, Garmin Activity API integration takes ~3–4 engineer-weeks (OAuth flow, FIT extension handling, edge cases, testing). At a $120/hr blended rate that's $14k–$19k of one-time engineering. This should be capitalized over the first 18 months of post-Garmin Pro users, not expensed in Y1. **Adjustment:** add a $1k/month amortization line to the COGS table starting at Garmin go-live. Negligible unit-cost impact.
2. **Coach view multi-tenancy is non-trivial.** The MVP-1 coach dashboard requires: (a) coach-athlete linking, (b) RBAC on athlete data, (c) coach-side aggregate views, (d) athlete consent surface. Estimated engineering: 4–6 weeks. This is a real cost the model treats as $0; it should be carried as a Y1 fixed engineering cost ~$25k. Doesn't break the model but should be visible.

**My change request:** Add an "engineering one-time costs" line to the Y1 financial walk, ~$45k total (Garmin integration + coach view + on-demand FIT parser maintenance). Conservative case Y1 net moves from −$39k to −$84k; target case from +$10k to −$35k. **The model still does not require external capital**, but founder runway requirement extends to ~$90k personal capital across 18 months in the target case.

**My kill criterion (added):**
- **K-W4-4 (Integrations):** If Y1 unplanned engineering costs exceed $60k (e.g., Garmin integration overruns, FIT parser rewrite, coach-view scope creep), pause feature work and revisit Y2 plan; do not raise capital to cover engineering rework that was avoidable with tighter scoping.

---

## §6. Founder synthesis

**Decisions locked**

1. Pricing: **$12/mo or $120/yr (17% off)**. Reverts to $108 (25% off) only if exp-1 WTP median below $10/mo (K-W1-7).
2. Free tier: 3 Weekly Reviews capped; framed as *"Try the first 3 Weekly Reviews on us"* (Demand §3 #2 adopted). 90-day fresh-window for returning lapsed users.
3. Activation window extended to **14 days** post-signup (Behavior §4 adopted via §2b).
4. **Provisional MAF cap path** for activation; cap upgradable later without data loss. Adherence computed lazily on read against the current cap (Behavior §4 #1 adopted).
5. Y1 milestone goal: 1,000 paid users / $120k ARR. Conservative case 500 paid / $60k ARR is acceptable.
6. Channel mix Y1: 35% creator, 25% organic, 15% community, 10% Strava clubs, 10% coach (MVP-1), 5% paid. Blended CAC target $25.
7. Creator partnerships use **30% lifetime affiliate on annual / 20% on monthly** for the first 12 months (Demand §3 #3 adopted).
8. Coach channel at MVP-1: free seat; ≤30 coaches before tooling needed; **athlete-controlled visibility toggle** (Behavior §4 #2 adopted).
9. Refund: 14-day monthly, 30-day annual; pause-subscription instead of post-30 prorated refund.
10. COGS model includes Garmin partnership avg $12k/yr (assumed), Stripe at ~3%, support at $0.20/user/mo. Gross margin 91–94%.
11. Engineering Y1 fixed costs: ~$45k (Garmin integration + coach view + ongoing FIT parser maintenance). Founder runway requirement: ~$90k personal capital across 18 months in target case (Integrations §5 #2 adopted).

**Reopened from earlier workshops**

- A-W1-8 ($12/mo defensibility) updated: now paired with a $120/yr (17% off) annual price. exp-1 WTP and exp-3 deposit conversion remain the resolvers.
- A-W1-9 (free-tier shape) closed at the W2 form (3-Review cap), but the alternative (14-day full-Pro trial) is queued as a post-1k-signups A/B.
- K-W2-4 (60% activation) reaffirmed; provisional-cap path is the leading mitigation.

**Items pushed to later workshops**

- **W5 (Science):** confirm provisional-cap framing language doesn't oversell the method; cap-validation prompt cadence; Goodhart counter-metric must be visible enough to prevent gaming.
- **W6 (Trust):** athlete-controlled coach visibility surface; affiliate program privacy implications (we don't share athlete data with creators, only conversion attribution); refund and pause-subscription policies in plain language; data retention on free tier.
- **W7 (Red Team):** model-breaks if M3 retention < 45%; channel mix collapse to paid-dominant; Garmin denial scenario; price war from a competitor (Intervals.icu adds a "Pro" tier; TrainingPeaks targets the Convert).

**New entries for the assumption register**

- **A-W4-1:** M3 retention ≥55% achievable with the W2 loop on the Convert. *Owner: exp-4 + first 200 paid cohort.*
- **A-W4-2:** Free → paid conversion ≥6% within 60 days under the 3-Weekly-Reviews-capped shape. *Owner: first 1k signups.*
- **A-W4-3:** Annual mix ≥40% of paid users at $120/yr (17% off). *Owner: first 200 paid cohort.*
- **A-W4-4:** Channels (creator + organic + community) carry ≥75% of signups by month 6 at blended CAC ≤$30. *Owner: monthly cohort attribution.*
- **A-W4-5:** Coach-channel at MVP-1 produces ≥2.5 paid athletes per engaged coach within 90 days. *Owner: post-MVP coach pilot.*
- **A-W4-6:** Provisional-cap activation path produces an activation rate within 10pp of calibration-test path (no significant funnel-quality loss). *Owner: matched-cohort instrumentation.*

**New entries for the kill-criteria ledger**

- K-W4-1 (Demand): Creator-channel visit→paid <1.5% on aligned creators within 90 days → rework channel mix.
- K-W4-2 (Economics): Creator+organic+community share <65% by month 6 → channel mix broken; reassess paid scaling.
- K-W4-3 (Behavior): Provisional-cap activation rate ≥10pp below calibration-test activation rate → rework provisional path.
- K-W4-4 (Integrations): Y1 unplanned engineering >$60k → pause features, revisit Y2 plan; no capital raise to cover scope creep.
- K-W4-5 (Economics, retention): M3 paid retention <45% in first 200 paid cohort → unit economics broken; revisit W2 loop and W5 metric set before scaling.

**Workshop 5 inbox (Science, Safety & Coaching Credibility)**

- Cap-setting confidence labels and language (provisional / data-validated / lab-validated).
- Recovery-delta wording library: green / amber / suppressed states with descriptive-only language and mandatory medication/cardiac onboarding question.
- Goodhart counter-metric design (e.g., "real easy minutes" excluding stops/walks above some duration; or coupling Adherence with Aerobic Trend in a single composite).
- Edge case catalog: heat, altitude, illness, sensor error, cardiac drift, beta-blockers, novice misunderstanding, talking-test fidelity.
- Aerobic Trend smoothing window and noise-label thresholds.
- Claims policy: what we claim vs. what we describe; the line between "endurance training app" and "health guidance".

**Confidence after Workshop 4:** ~60% the unit economics work at the W2 retention assumption and W3 channel feasibility. ~40% if M3 retention undershoots. The model is robust to reasonable pricing variation, partnership cost variation, and engineering one-time costs; it is **not** robust to a retention shortfall or a channel-mix collapse to paid. exp-4 and first-1k-signup instrumentation are the two pieces of evidence that decide the business.
