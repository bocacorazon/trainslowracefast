# Aerobic-First Endurance SaaS Feasibility Discussion

This project explores a software business for runners, cyclists, and triathletes who want to train with a long-term, low-intensity-first philosophy: zone 2, MAF, polarized, and similar approaches.

The core thesis is not "help athletes train harder." It is:

> Help athletes **stay easy enough, often enough, long enough** to realize the benefits of aerobic training.

## Why this category may exist

Most incumbent products reward some combination of:

- volume
- intensity
- load accumulation
- social comparison
- hard-workout execution
- progress framed as "more" or "faster"

That is rational for broad endurance software, but it leaves an underserved behavior problem:

> Many athletes already believe easy training matters, but they still drift too hard on easy days and lack tools that make restraint measurable and rewarding.

The opportunity is to build around that behavior problem rather than around generic activity logging.

## Early market view

Current leaders cluster into a few positions:

| Player | Core value | Likely weakness for this wedge |
|---|---|---|
| Strava | social motivation, routes, progress, goals | comparison and motivation loops can encourage more intensity rather than restraint |
| TrainingPeaks | planning, coach workflow, performance management | core language and metrics lean toward optimization, load, and TSS-style framing |
| Intervals.icu | rich analytics, planning, flexibility, strong visuals | excellent analysis, but not explicitly centered on low-intensity adherence |
| Garmin Connect | convenient device companion and default data hub | data source, not a focused methodology product |
| Final Surge | athlete/coach workflow and training plans | broad coaching product, not an aerobic-first specialist |

The working hypothesis is that no mainstream product clearly owns:

- low-intensity adherence
- anti-intensity-creep feedback
- aerobic progress indicators over "go harder" indicators
- a product identity built around durable endurance rather than maximal performance

## Beachhead customer

The strongest initial customer appears to be:

> Self-coached endurance athletes who already believe in zone 2 / MAF / polarized training, own a watch or cycling computer, and want help executing the method consistently.

Why this segment first:

1. They already have the data source.
2. They already consume training content and methodology discussions.
3. They feel the pain directly: "I know what I should do, but I still go too hard."
4. They are easier to reach through content, communities, and coaches than the mass-fitness market.

## Core product hypothesis

The MVP should not try to replace every training platform. It should become the best layer for aerobic-first execution.

Early product candidates:

1. **Easy-day compliance**
   - Did the athlete stay under the planned cap?
   - How much time drifted above the intended easy zone?

2. **Intensity creep detection**
   - HR drift above target
   - pace or power surges inconsistent with intent
   - route and terrain normalization where possible

3. **Aerobic progress indicators**
   - pace-at-HR trends
   - power-at-HR trends
   - decoupling / aerobic efficiency
   - recurring benchmark sessions such as MAF-style tests

4. **Training distribution discipline**
   - easy vs. moderate vs. hard distribution
   - planned vs. actual intensity mix
   - unplanned medium-hard accumulation

5. **Behavior loop**
   - rewards for restraint
   - post-workout analysis in the language of discipline and durability
   - weekly review that makes "stayed easy" feel like a real achievement

## Product positioning hypothesis

The likely winning frame is:

> **The training app for durable endurance.**

This keeps the product from sounding anti-performance or fad-driven. It allows support for zone 2, MAF, and polarized training without forcing the brand to depend on one doctrine or one influencer vocabulary.

Supporting positioning alternatives to test:

- "Stop ruining your easy days."
- "Train easy with confidence."
- "Make aerobic training visible."
- "Build endurance without intensity creep."

## Business model hypothesis

The likely first model is athlete SaaS, not coach software first.

- **Free tier:** sync/import, basic analysis, recent workout review
- **Pro tier:** advanced adherence analytics, trend charts, benchmark tests, weekly durability review, alerts, planning support
- **Later:** coach accounts, shared athlete dashboards, team plans, educational content, API/exports

Indicative pricing to test:

- free
- $10-15/month
- $99-129/year

## What must be true for this to work

Several beliefs need validation before any major build:

1. The problem is behavior and adherence, not just interest in zone 2 content.
2. Enough athletes will pay for accountability and insight layered on top of existing data sources.
3. A narrow initial integration scope can still feel useful.
4. The product can provide credible, science-aligned guidance without over-claiming or sounding dogmatic.
5. Retention is driven by habit formation and trend visibility, not one-off curiosity.
6. The category can be framed in a way that survives shifts in training jargon.

## Main risks

### 1. Category risk

"Zone 2" may be popular but too narrow, too trendy, or too inconsistently defined to anchor a durable business.

### 2. Feature-overlap risk

Athletes may say they want this, then continue using Garmin, Strava, or Intervals.icu as their only dashboard.

### 3. Integration dependence

The product is only credible if data import and normalization are reliable. Third-party platform rules can constrain this.

### 4. Science and safety risk

If guidance is too simplistic, prescriptive, or medically flavored, credibility and trust collapse.

### 5. Retention risk

If the value is only "interesting charts," churn will be high once curiosity fades.

## Working conclusion

This looks promising enough to validate systematically.

The wedge is real only if the business proves all three of the following:

1. **A buyer exists** for an aerobic-first training layer.
2. **The product can change behavior**, not merely display data.
3. **The economics work** without building a full TrainingPeaks replacement.

The rest of this repository should answer those questions in sequence before execution begins.
