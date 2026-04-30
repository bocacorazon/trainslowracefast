# Demand & Positioning Workshop

**Workshop:** 1 of 7
**Primary agent:** Demand & Positioning Strategist (offensive)
**Secondary reviewers:** Behavior Change Product Strategist, SaaS Economics Analyst
**Status:** DECISIVE — commits to a primary ICP, a primary positioning frame, and a pricing hypothesis. All locked decisions are pending falsification by the evidence plan in §2g.

---

## 1. Executive summary

**Problem statement (demand view):** A real and growing population of endurance athletes already believe — through Maffetone, Seiler, Attia, San Millán, the polarized literature, or coach influence — that most of their training should be easy. They do not have a tool that helps them *execute* that belief. Their watches reward consistency and intensity. Strava rewards segments and comparisons. TrainingPeaks and Intervals.icu present excellent analytics that are intensity-neutral or implicitly performance-maximizing. The result is an athlete who *intends* to go easy, *fails* to go easy, and has no software ally that makes that failure visible, costly, or correctable.

**Primary ICP (locked):** **Self-coached endurance athletes, age 35–55, with 2+ years of structured training history, who have explicitly adopted a low-intensity-first methodology (zone 2, MAF, 80/20, polarized) within the past 24 months and own a HR-capable watch or cycling computer.** Working name: the **Convert** segment. Proxy archetype: a 42-year-old age-group triathlete or endurance runner who used to chase TSS, hit a wall (overtraining, plateau, injury, or a doctor visit), read or watched something that reframed easy training as the missing piece, and now wants to do it correctly without paying for a coach.

**Primary positioning frame (locked, MVP):** **"Easy days, done right."** Concrete and behavioral. Names the failure mode the customer already feels. Survives shifts in training jargon because it is about a behavior, not a brand vocabulary.

**Brand-level frame (locked, durable):** **"Train slow, race fast."** This is the long-term identity of the product and the company. The MVP headline narrows it to the wedge ("easy days, done right"), but the brand survives even if the zone-2 vocabulary fades, because the underlying truth — that aerobic-base discipline produces race-day performance — is not a fad.

**Pricing hypothesis:**
- Free tier: import + last-7-days adherence view + one weekly review per week.
- **Pro tier: $12/month or $108/year (25% annual discount).** Adherence trends, intensity-creep alerts, planned-vs-actual analysis, benchmark tests (MAF / aerobic-decoupling), unlimited history, structured weekly review.
- No coach tier at MVP. Coach features are explicitly deferred to post-beachhead.

**Risk level:** MEDIUM.
**Confidence:** MEDIUM-HIGH (~65%) that a viable beachhead exists at this ICP and price; LOW-MEDIUM (~40%) that the product can change behavior enough to retain at SaaS-defensible rates. Demand thesis is the easier half. Behavior and retention are the harder half and are handed forward to Workshop 2.

---

## 2. Demand & Positioning Strategist memo

### Three framework questions

**1. Strongest version of the problem in my lane.**
Every endurance athlete with a watch already has a place to *see* their workouts. The defensible demand is not "give me another log." It is: "I have decided easy training matters, I keep failing to do it, and no product I use treats that failure as the main event." For this athlete the existing platforms are not wrong — they are *aimed at a different question*. Strava asks "how did you compare?" TrainingPeaks asks "how much load did you accumulate?" Intervals.icu asks "what does your data show?" None of them ask "did you train the way you said you would?" That gap is the only place a small company can win against incumbents whose feature breadth we will never match.

**2. Proposal that best addresses it.**
Ship a beachhead product that targets the Convert segment exclusively, frames itself as an *adherence and execution* tool (not an analysis tool, not a planning tool, not a coaching tool), prices in the middle of the market at $12/mo, and refuses to add features that pull it back toward generic logging. The product layers on top of existing platforms; it does not replace them. The MVP looks like a focused weekly review experience plus a small set of adherence-specific charts and alerts — and *nothing else of importance*. Saying no to features is the strategy.

**3. Unresolved failure mode / unknown.**
The Convert segment is real, but it may be too small to support a SaaS business *on its own*, and it may bleed into adjacent segments (Performance-curious, Health-first, Coach-led) where substitutes dominate us. If the product expands too aggressively to chase TAM, it dilutes the wedge and ends up competing head-on with Intervals.icu, where we lose. The first-class risk handed to Workshop 2 (Behavior & Product Wedge) and Workshop 4 (Economics) is: can the Convert beachhead scale to a real SaaS business *without* losing positioning, or is it a niche that needs a deliberate Phase-2 expansion?

---

### 2a. Candidate ICP segments — comparative analysis

Size estimates are first-principles ballparks, not sourced figures.

#### Segment 1: The Convert (low-intensity adopter)
- **Who:** Endurance athletes who have explicitly adopted a low-intensity-first method within the past ~24 months. Self-coached. Mostly 35–55. Mostly running or cycling, often triathlon. Owns a HR-capable device. Has read or watched at least one of: *Endure*, *The Big Book of Endurance*, Stephen Seiler interviews, Peter Attia podcast, San Millán content, Iñigo San Millán + Pogačar coverage, Floris Gierman, MAF-method content.
- **Size:** Hard to bound. US/EU/AU/UK English-speaking endurance athletes who self-coach and own a HR device: ~3–8M. The Convert sub-segment within that is plausibly 5–15%, i.e., **150k–1.2M addressable**. SAM in year 1 is a thin slice — maybe **20k–80k** reachable via content and community channels.
- **Urgency:** MEDIUM-HIGH. The pain is recurring and emotional ("I went too hard *again*"). It is not as sharp as a deadline pain, but it repeats every week.
- **WTP:** $8–15/mo defensible. Anchors against TrainingPeaks ($11.25/mo), Intervals.icu ($4/mo Supporter), and a coach ($150–400/mo). Substantially cheaper than a coach, modestly more expensive than Intervals supporter, in line with TrainingPeaks.
- **Substitute quality:** MEDIUM. Intervals.icu is the strongest substitute and is excellent — but is intensity-neutral and not designed around adherence. TrainingPeaks is performance-framed. A real coach beats us, but at 10–30× the price.
- **Acquisition:** Targetable. Endurance-specific YouTube, podcasts, Reddit (r/Velo, r/AdvancedRunning, r/triathlon, r/Maffetone), Strava clubs, niche Substacks, age-group Facebook groups. Clean channels exist and are not winner-take-all.
- **Verdict:** **STRONG. This is the beachhead.**

#### Segment 2: The Performance-curious (TSS-fluent, low-intensity-curious)
- **Who:** TrainingPeaks/Intervals.icu users who have heard about polarized training and are intellectually interested but have not committed. They still primarily care about FTP, threshold, race results.
- **Size:** Larger than the Convert segment in absolute terms. Maybe **500k–2M** addressable.
- **Urgency:** LOW–MEDIUM. They are not in pain; they are curious.
- **WTP:** $5–12/mo. Capped by what they already pay TrainingPeaks or Intervals.icu — they will *add* us only if we are clearly cheaper or clearly different.
- **Substitute quality:** VERY HIGH. Intervals.icu already gives them everything they ask for, plus more, for $0–$4/mo. We cannot win on features.
- **Architecture sensitivity:** They want depth and customization. Our deliberately narrow product will feel limiting.
- **Verdict:** **Weak as a beachhead.** Reachable later as a Phase-2 expansion *if* the brand earns credibility through the Convert segment first.

#### Segment 3: Health-first (Attia listeners, longevity-curious, non-racing)
- **Who:** People who care about VO2max and metabolic health, not race results. Often older (45–65). Often guided by a podcast, not a coach.
- **Size:** Large and growing. Plausibly **2–10M** in English-speaking markets.
- **Urgency:** MEDIUM. Health framing makes the urgency real but slow.
- **WTP:** $5–10/mo, but inconsistent. Many are happy with their Apple Watch / Garmin and don't seek a third-party tool.
- **Substitute quality:** HIGH. They are not data-rich users. They tend to satisfice with the watch app and their podcast of choice.
- **Acquisition:** Possible via longevity podcasts and content but channels are crowded and not endurance-specific.
- **Verdict:** **Adjacent.** Tempting because it is large and trendy, but: (a) they are not in the workflow of analyzing training, (b) winning here means competing on a different axis (medical/longevity authority) we cannot credibly hold at MVP, and (c) chasing them dilutes the Convert frame.

#### Segment 4: Coach-led athletes (and their coaches)
- **Who:** Athletes who already have a coach using TrainingPeaks or Final Surge.
- **Size:** ~500k–1.5M coached endurance athletes globally.
- **Urgency:** Their coach owns the workflow. The athlete is not the buyer.
- **WTP:** Low directly; the relevant buyer is the coach, who has switching costs.
- **Substitute quality:** VERY HIGH. TrainingPeaks and Final Surge own the coach workflow. Replacing them requires features we will not have for years.
- **Verdict:** **Wrong shape for MVP.** Coach-side product is a different business with two-sided distribution problems. Defer to Phase-2 or Phase-3.

#### Segment 5: Beginner endurance athletes (couch-to-5k, first-time triathletes)
- **Who:** New to endurance, often using a generic plan or app (Runna, Strava plans, Garmin Coach).
- **Size:** Large.
- **Urgency:** Their problem is "complete the plan," not "stay easy."
- **WTP:** Low. Already paying for plans or watches.
- **Substitute quality:** VERY HIGH. Runna, Garmin Coach, generic free plans, Couch-to-5k.
- **Verdict:** **Weak.** Wrong job-to-be-done. Adherence as we frame it is not the problem they have.

#### Segment 6: Overtrained / returning-from-injury athletes
- **Who:** Athletes coming back from injury, illness, or burnout. Often instructed by a doctor or PT to keep HR low for weeks or months.
- **Size:** Small at any moment but high-turnover; **50k–250k** active in this state at any time.
- **Urgency:** HIGH but transient.
- **WTP:** $10–20/mo for a defined period.
- **Substitute quality:** MEDIUM. Many use the HR alarm on their watch, which is crude but free.
- **Verdict:** **Strong sub-segment of the Convert beachhead.** Don't make them the primary ICP — too transient and too narrow — but include them in messaging variants. They convert fast and validate the wedge cheaply.

**Ranking:** Convert (strong) >> Overtrained/Returning (strong sub-segment) > Performance-curious (medium, dominated by Intervals.icu) > Health-first (adjacent, brand-diluting) > Coach-led (wrong shape) > Beginner (wrong job).

---

### 2b. Substitute analysis

| Substitute | Price | What it does well | Where it fails for the Convert | Why we win |
|---|---|---|---|---|
| **Intervals.icu** | $0 / $4 supporter | Best-in-class free analytics, custom zones, fitness chart, multi-sport | Intensity-neutral; no adherence-first review; no nudges to stay easy | Deliberately narrow product around an adherence loop the Convert is already trying to enforce by hand |
| **TrainingPeaks** | $11.25/mo | Coach workflow, planning, established TSS/PMC vocabulary | Vocabulary and metrics implicitly reward load and intensity | We name the *opposite* problem and refuse to display TSS as a primary metric |
| **Strava Premium** | $6.67/mo | Social motivation, routes, segments, fitness score | Comparison and segments actively encourage going harder | We reward restraint, not comparison |
| **Garmin Connect** | bundled with hardware | Default data sink, on-device alerts, decent HR-zone view | Hardware-locked; uneven across models; no platform-wide adherence story | Cross-device, focused on a behavior the watch only crudely surfaces |
| **A real coach** | $150–400/mo | Accountability, individualized plan, judgment | Expensive; not always available; quality varies wildly | 10–30× cheaper; consistent; software-delivered accountability |
| **Spreadsheet / notebook** | free | Total flexibility, athlete owns it | Manual, no integrations, no nudges | Auto-imports data; review is structured; cheaper than the time it costs |
| **Final Surge** | free / paid coach | Clean coach-athlete workflow, plans library | Generalist; not aerobic-first | Different positioning, different buyer |
| **Wahoo SYSTM / Zwift / TrainerRoad** | $15–20/mo | Structured indoor workouts | Workout-execution layer, not an adherence/analysis layer | Different product type; complementary rather than competing |
| **HR alarm on the watch** | free | Real-time cap | No history, no trend, no review, no learning | The post-workout and weekly layer the alarm cannot provide |

**Conclusion:** The strongest substitute is **Intervals.icu**, which is excellent and free. We do not beat it on features and should not try. We beat it on **focus**: a product whose entire surface area is built around the adherence loop and the weekly review, with opinionated defaults, fewer knobs, and a brand identity that says "this is the place where staying easy is the point." Against every other substitute we either compete on price (vs. coach), on focus (vs. TrainingPeaks/Strava), or on completeness (vs. Garmin alone).

---

### 2c. Positioning frame analysis

| Frame | Specific | Survives jargon shift | Resonates with Convert | Risk |
|---|---|---|---|---|
| "The zone 2 app" | very | poor | high today | category risk if zone-2 vocabulary fades or fragments |
| "MAF method app" | very | poor | high in Maffetone sub-cohort only | tied to one author; fragments the audience |
| "Polarized training app" | medium | medium | high in cyclists | weaker in runners; jargon-heavy |
| **"Easy days, done right."** | high | strong | high | none material |
| **"Train slow, race fast."** | high | very strong | high | needs proof points to avoid sounding like a slogan |
| "The training app for durable endurance" | medium | strong | medium | sounds like marketing copy, not a product promise |
| "The anti-intensity-creep app" | high | medium | medium | negative framing; harder to lead a brand with |

**Decision:**
- **Brand-level frame:** *Train slow, race fast.* This is the company's identity and the repository name. It is the durable promise.
- **MVP-level headline:** *Easy days, done right.* This is what the landing page leads with. It names the failure mode and survives jargon shifts.
- **Supporting language:** zone 2, MAF, 80/20, polarized appear as *supported methods*, not as the brand. The product is method-pluralistic on purpose.

---

### 2d. Primary ICP decision

**Locked: The Convert — self-coached endurance athletes 35–55 who have adopted a low-intensity-first method within the past ~24 months and own a HR-capable device.**

**Why this segment first (not the others):**
1. **Only segment where the wedge is the value.** Every other segment can be served by an existing tool that does more for less.
2. **Behavior pain is recurring and emotional.** Every easy run that ran too hot is a small repeated failure the product can address.
3. **Targetable acquisition.** The Convert reads specific content, listens to specific podcasts, and lives in specific communities. We can find them.
4. **Compatible with a small initial product.** A focused adherence loop is enough; we do not need to replace TrainingPeaks.
5. **Methodology-pluralistic.** The Convert is not loyal to one author; they care about the underlying behavior.
6. **Other ICPs are downstream.** Performance-curious and Health-first are larger but require winning the beachhead first to have any positioning leverage.

**What "winning the beachhead" looks like (12-month targets, for go/no-go at end of year one):**

| Metric | Year-1 target |
|---|---|
| Paying subscribers | 1,500 |
| MRR | $15,000 |
| ARR | ~$180k |
| Free→paid conversion | ≥ 8% |
| Cohort 12-month retention | ≥ 65% |
| Blended CAC | ≤ $40 |
| Paid acquisition share | ≤ 35% (rest from content, community, referral) |

These are first-pass targets; Workshop 4 owns the economics model and may revise them.

**Architecture that best serves the Convert:**
- Layered on top of existing platforms (Garmin, Strava, Wahoo, Coros, FIT upload).
- Read-only at MVP. We do not write workouts to devices in v1.
- Web-first; mobile companion later.
- Opinionated defaults, fewer knobs than Intervals.icu by design.

---

### 2e. Value proposition & messaging

**One-sentence positioning:**
> For self-coached endurance athletes who already believe easy training matters, *Train Slow Race Fast* is the only training app whose entire job is helping you stay easy on easy days — so the hard ones, and race day, actually count.

**Landing-page headline (MVP candidate):**

> # Easy days, done right.
> Your watch tells you how far. Your plan tells you how easy. We tell you whether you actually did it — and what it cost you when you didn't.

**Top 3 proof points:**
1. **One weekly review you'll actually read.** Imported from your watch in seconds. Built around the question your other apps don't ask: did you train the way you said you would?
2. **Adherence, not comparison.** No segments, no leaderboards, no kudos. The number that matters is how much of your easy work was actually easy.
3. **Aerobic progress made visible.** Pace-at-HR, decoupling, and benchmark tests — without TSS as the lead metric.

**Top 3 objections and responses:**

| Objection | Response |
|---|---|
| "I already use Intervals.icu / Strava / TrainingPeaks." | Keep them. We import from them. We are the adherence layer, not a replacement. Most of our users sync from at least one of those platforms. |
| "I can see my HR zones on my watch." | The watch tells you in the moment. We tell you across the week, the month, and the year — and we hold you to your own plan. |
| "Isn't this just zone 2?" | Zone 2 is one method we support. The product also fits MAF, 80/20, and polarized training. The wedge is execution, not a single doctrine. |

---

### 2f. Pricing hypotheses

**Proposed structure:**

| Tier | Price | Includes |
|---|---|---|
| Free | $0 | Auto-import, last 7 days adherence view, one weekly review/week, basic HR-zone analysis |
| **Pro** | **$12/mo or $108/yr (25% off annual)** | Unlimited history, intensity-creep alerts, planned-vs-actual deviation analysis, benchmark tests (MAF / decoupling), advanced adherence trends, weekly digest email, cross-sport view |
| Coach | (deferred) | Multi-athlete dashboard, shared adherence reports — explicitly post-beachhead |

**Pricing rationale grounded in substitutes:**
- **Above Intervals.icu Supporter ($4):** we ask a 3× premium because we are doing something Intervals.icu is not — narrow, opinionated, adherence-first. If we cannot defend that premium with focus, we cannot defend it at all.
- **Just above TrainingPeaks Premium ($11.25/mo):** the Convert who is paying TrainingPeaks today is not getting what they want; we ask the same money for a product aimed at their actual problem.
- **Far below a coach ($150–400/mo):** the comparison the Convert most often makes is "should I just hire a coach?" Our answer: not yet, or not for this. The price gap supports that.
- **Annual discount (25%) is generous on purpose:** annual buyers are a critical retention signal at MVP. Cheaper to acquire commitment than to chase month-to-month churn.

**Free tier rationale:** the free tier exists to demonstrate the adherence frame, not to be a permanent home. The "one weekly review per week" cap is the most important free→paid conversion lever — the weekly review *is* the product, and rationing it creates a credible reason to upgrade without crippling discoverability.

**Open pricing questions handed to Workshop 4:**
- Is $12/mo too low? A 60% premium over TrainingPeaks ($18/mo) is defensible if the wedge is real and Workshop 2 confirms behavior-change value.
- Is the 7-day free history the right cap? It may be too restrictive for activation. A 30-day cap may convert better at the cost of a weaker upgrade prompt.
- Is annual prepay (25% off) too generous? A 15–20% discount is more standard.

---

### 2g. Evidence plan

The locked decisions above are committed-on-paper, not validated. The following experiments resolve them.

**Exp-1: Founder interviews (n = 20–25)**
- Recruit from Reddit (r/AdvancedRunning, r/Velo, r/triathlon, r/Maffetone), MAF Facebook groups, age-group triathlon Slacks, and content-creator audiences.
- Filter to: self-coached, 2+ years training, HR device, has adopted a low-intensity-first method within ~24 months.
- Script probes: (a) urgency and recurrence of the "went too hard on easy day" failure, (b) what they currently use, (c) what would make them switch attention from their existing tool, (d) WTP at $8 / $12 / $18 / $24 monthly, (e) what they would expect a "weekly review" to look like.
- **Resolves:** A1, A2, A3, A4, A5, partial WTP signal for A-W4-1.
- **Owner:** Founder. **Timing:** before any landing page goes up.

**Exp-2: Landing-page test**
- Single landing page at the brand domain. Two headline variants:
  - A: *Easy days, done right.*
  - B: *Train slow, race fast.*
- Identical body copy; differ only in headline. CTA: "Get the weekly review when we open it up" → email capture.
- Traffic: small paid Reddit + Meta campaigns (~$500 total) targeted to endurance subreddits and lookalikes; one organic post in 2–3 communities; one outreach to a relevant podcast or newsletter for a mention.
- **Measures:** signup conversion per variant; cost per email; share of signups choosing each device platform (Garmin / Strava / Wahoo / Coros / Apple).
- **Resolves:** A2 (frame durability), partial A1, K3.
- **Owner:** Founder. **Timing:** after exp-1 informs copy.

**Exp-3: $5 deposit / pre-order test**
- Email captures from exp-2 receive a follow-up offering an early-access seat for a $5 refundable deposit, locked-in $9/mo or $84/yr lifetime founder pricing.
- **Measures:** capture→deposit conversion; open and click rates; deposit refund-request rate.
- **Resolves:** K4, partial K2 (WTP signal).
- **Owner:** Founder. **Timing:** 2–4 weeks after exp-2 launches.

**Exp-4: Concierge weekly review pilot (n = 10–15)**
- Recruit from deposit-takers in exp-3.
- Concierge: athlete connects Strava/Garmin (read-only); founder manually produces a weekly review document (Notion or PDF) every Monday for 6 weeks.
- The document follows the proposed product loop: adherence summary, intensity-creep flags, aerobic-progress chart, one prioritized recommendation.
- **Measures:** weekly engagement (did they read it; did it change their next week); 6-week retention; verbatim quotes; willingness to convert to a paid auto-version at week 6.
- **Resolves:** A3 (behavior pain), partial A-W2-1, A-W2-2, K-W2-1, K-W2-3, K5.
- **Owner:** Founder. **Timing:** runs in parallel with exp-2/3.

**Exp-5: Substitute survey**
- Sent to all email captures from exp-2.
- Asks: which platforms they currently use, what they pay, what they would replace vs. add, would they cancel TrainingPeaks/Intervals.icu Supporter for us, and the inverse.
- **Resolves:** A5, substitute-quality refinement, partial A-W3-2.
- **Owner:** Founder. **Timing:** anytime after exp-2 has 100+ captures.

---

## 3. Behavior Change Product Strategist memo (secondary review)

### Three framework questions

**1. Strongest version of the problem in my lane.**
The hardest part of the proposed wedge is not finding the customer or naming the frame; it is proving that *seeing* their adherence failure changes how they train next week. There is a non-trivial chance that the Convert reads the weekly review, agrees, and then drifts again. Software that only *describes* behavior is a chart. Software that *changes* behavior is a habit loop with a trigger, an action, a reward, and a salient cost when the behavior fails. The Demand Strategist's lock has the trigger (weekly review) and a recognizable failure (intensity creep), but does not yet specify the reward or the salient cost.

**2. Proposal that best addresses it.**
Concur with the locked ICP and frame. Add the following requirements as inputs to Workshop 2:
- The MVP must include a *visible weekly streak or score* that rewards adherence. Anti-intensity-creep is hard to celebrate; we will need to celebrate the *staying easy*, not the absence of going hard.
- The MVP must include a *next-week recommendation* that is shorter than three sentences and is generated, not chosen from a menu. The Convert does not need a coach; they need a small, opinionated nudge.
- The intensity-creep alert must arrive *fast enough* to matter — within 1–2 hours of a workout, not at the next weekly review — or the behavior change will not stick.
- The free tier's "one weekly review" cap may be the wrong lever. The activating moment is the *first* adherence chart, not the weekly review. Reconsider in Workshop 2.

**3. Unresolved failure mode / unknown.**
Whether software alone (without human accountability) can produce adherence change in this segment. The honest answer is unknown. The concierge pilot (exp-4) is the only way to find out cheaply. If the concierge produces behavior change but the auto-version does not, the business is structurally a coach-augmentation product, not a SaaS — which would force a re-open of W1 ICP and W4 economics.

### Deltas requested
- Workshop 2 must define the behavior-change instrumentation explicitly before MVP build.
- Add to assumption register: *A-W1-6 — Adherence visibility alone (no human accountability) can produce sustained behavior change in the Convert at SaaS-defensible retention rates.*
- Add to kill ledger: *K-W1-5 — If concierge users (exp-4) report meaningful behavior change but cohort retention drops sharply when handed the auto-version, the product is not a SaaS.*

---

## 4. SaaS Economics Analyst memo (secondary review)

### Three framework questions

**1. Strongest version of the problem in my lane.**
The Demand Strategist's beachhead numbers (1,500 paying subs, $180k ARR, 12-month retention ≥ 65%, CAC ≤ $40) are plausible but depend on three optimistic inputs: (a) free→paid conversion at 8%, which is high for a top-of-funnel content-driven SaaS, (b) blended CAC at $40, which assumes content and community do most of the work, and (c) retention at 65%, which is a guess for a software-only behavior-change product with no precedent in this exact niche. The risk is not that any single input is wrong; it is that all three need to be simultaneously good for the model to work, and the failure modes are correlated: weak retention means we need cheaper acquisition, and weak acquisition means we need stronger retention.

**2. Proposal that best addresses it.**
Concur with $12/mo as the headline price. Two pushbacks for Workshop 4:
- **Annual discount at 25% is too generous.** Industry-standard 15–20% is fine. The 25% discount sets a precedent that compresses ARPU permanently. Recommend $108/yr → $115/yr (~20% off).
- **Free tier may be too generous.** "One weekly review per week" sounds like a cap, but the weekly review is also the activating moment. If the free user gets the activating moment indefinitely, the upgrade pressure is weak. Workshop 4 should test a 4-week free trial of full Pro instead of an indefinite limited free tier.

**3. Unresolved failure mode / unknown.**
The Convert TAM may be too small for a *standalone* SaaS even with good unit economics. 20k–80k reachable in year 1, even at 5% conversion, is 1k–4k subscribers — a real beachhead but not a real business at $12/mo unless Phase-2 expansion to Performance-curious or Health-first is achievable without losing positioning. Workshop 4 must explicitly model this and Workshop 7 must hold the answer as a go/no-go input.

### Deltas requested
- Add to assumption register: *A-W1-7 — The Convert beachhead is large enough to support a $1M+ ARR business on its own, OR the brand can credibly expand to Performance-curious in Phase 2 without dilution.*
- Add to kill ledger: *K-W1-6 — If exp-2 cost-per-email exceeds $4 on cold paid traffic, blended CAC ≤ $40 is not credible and the model needs to be reworked at Workshop 4.*
- Add to kill ledger: *K-W1-7 — If exp-1 median WTP is below $8/mo, the $12/mo price is unsupported and the entire economics model must be revisited.*

---

## 5. Founder synthesis

**Locked at end of Workshop 1:**
- **Primary ICP:** the Convert (self-coached, 35–55, low-intensity-first adopter, HR-capable device).
- **Brand frame:** *Train slow, race fast.*
- **MVP headline frame:** *Easy days, done right.*
- **Pricing hypothesis:** Free tier + Pro at $12/mo or $108/yr (annual discount value to be revisited in Workshop 4 per Economics pushback).
- **Architecture posture:** read-only layer on top of existing platforms; no device write-back at MVP; web-first.

**Reopened or escalated:**
- Annual discount magnitude (W4).
- Free-tier shape — capped weekly review vs. time-limited full trial (W2 / W4).
- Whether software-only behavior change is sufficient for SaaS retention (W2).
- Whether the Convert beachhead can scale or requires a Phase-2 expansion plan (W4 / W7).

**Evidence plan in flight (priority order):**
1. exp-1 founder interviews (n=20–25).
2. exp-4 concierge pilot (n=10–15) starts in parallel as soon as exp-1 surfaces qualified candidates.
3. exp-2 landing-page test (after exp-1 informs copy).
4. exp-3 $5 deposit test (after exp-2 has captures).
5. exp-5 substitute survey (continuous to capture cohort).

**Workshop 2 inbox (to address before product spec):**
- Define the behavior-change loop concretely: trigger, action, reward, cost.
- Specify the intensity-creep alert SLA (target: within 1–2 hours of workout sync).
- Decide whether the activating moment is the first adherence chart or the first weekly review, and align the free-tier cap with that decision.
- Specify the smallest set of metrics that drive behavior, not interest.

**Confidence after Workshop 1:** ~65% there is a viable beachhead at this ICP and frame; ~40% the product can change behavior enough to retain at SaaS-defensible rates; the second number is the gating uncertainty for the entire business and is owned by Workshop 2.
