# Feasibility Validation Plan

## Philosophy: demand first, behavior second, systems third

Before building software, this project should prove three things in order:

1. there is a buyer
2. the product wedge changes athlete behavior in a meaningful way
3. the business can survive science, platform, privacy, and economic constraints

This is not a generic endurance app. The validation sequence must reflect that.

The business does not win by logging workouts. It wins only if it helps athletes stay inside intended intensities and see progress through the lens of durable aerobic development.

## Validation outputs

The validation phase should produce:

1. a primary ICP with evidence of willingness to pay
2. a durable positioning frame that is broader than a temporary "zone 2 trend"
3. an MVP definition focused on behavior change rather than analytics sprawl
4. a minimum viable integrations strategy
5. a pricing model and activation/retention thesis
6. a science and safety posture for claims, indicators, and coaching language
7. a privacy, trust, and platform-dependency operating model
8. a red-team failure inventory
9. a go / no-go recommendation with explicit kill criteria

Each workshop should feed a shared assumption register and kill-criteria ledger.

## The 7-step workshop plan

### Step 1: Demand & Positioning Workshop

*Focus: prove there is a buyer and a story.*

- Primary question: who urgently wants this, in what form, and why would they switch attention from their existing training tools?
- Define candidate ICPs: self-coached runners, cyclists, triathletes, masters athletes, overtrained strivers, coach-led athletes, health-oriented amateurs.
- Map substitutes: Garmin Connect, Strava, Intervals.icu, TrainingPeaks, coaches, spreadsheets, notebooks.
- Decide whether the wedge is best framed as zone 2, durable endurance, low-intensity adherence, anti-intensity-creep, or another narrative.
- Produce a willingness-to-pay hypothesis and the first evidence plan: interviews, landing pages, waitlists, paid tests, concierge reviews.
- Output: ICP memo, positioning memo, substitute map, pricing hypothesis, evidence plan, demand kill criteria.

### Step 2: Behavior & Product Wedge Workshop

*Focus: prove the product solves the real adherence problem.*

- Primary question: what exactly makes "go easy" hard, and how can software reduce that failure?
- Define the core jobs-to-be-done around execution: staying under cap, recovering well, avoiding medium-hard drift, seeing aerobic progress.
- Map the athlete journey before, during, and after workouts.
- Define the smallest feature set that creates a behavior loop: planned intensity, actual intensity, deviation analysis, weekly review, benchmark tests, rewards.
- Distinguish "interesting metrics" from "metrics that actually change behavior."
- Output: JTBD map, MVP feature wedge, instrumentation plan, behavior-change hypotheses, product kill criteria.

### Step 3: Integrations & Ecosystem Workshop

*Focus: prove the product can access the right data and fit into the athlete's existing stack.*

- Primary question: what integrations are mandatory at launch, and what dependencies are dangerous?
- Define the minimum import surface: Garmin, Strava, Wahoo, Coros, Apple, Polar, FIT file upload, manual entry.
- Decide whether the product is an analysis layer, planning layer, coaching layer, or some deliberate combination.
- Define coach and creator roles in distribution without making coaches the initial product dependency.
- Rank ecosystem dependencies by importance and fragility.
- Output: integration priority matrix, launch-scope recommendation, ecosystem risk inventory, channel hypotheses, integration kill criteria.

### Step 4: Pricing, Retention & Distribution Economics Workshop

*Focus: prove the model makes money without becoming a broad commodity training log.*

- Primary question: can athlete SaaS economics work at realistic activation, retention, support, and acquisition assumptions?
- Model free vs. paid boundaries.
- Estimate activation milestones and what events correlate with retention.
- Model price sensitivity against substitutes and against "I can already see my workouts elsewhere."
- Stress-test whether coach or community channels can reduce CAC enough to matter.
- Output: pricing recommendation, activation model, retention hypothesis, CAC/LTV sensitivity, economic kill criteria.

### Step 5: Science, Safety & Coaching Credibility Workshop

*Focus: protect credibility and avoid oversimplified training advice.*

- Primary question: what can the product claim responsibly, and where must it remain descriptive rather than prescriptive?
- Define support for multiple low-intensity frameworks without pretending they are identical.
- Decide which indicators are valid enough to display prominently.
- Define how the product handles edge cases: heat, terrain, fatigue, illness, sensor error, cardiac drift, medication, aging, and novice misunderstanding.
- Define when the app is giving analysis vs. coaching vs. health-adjacent guidance.
- Output: claims policy, metric hierarchy, coaching language guidelines, user-safety guardrails, science kill criteria.

### Step 6: Trust, Privacy & Platform Dependency Workshop

*Focus: make the business durable if data partners, users, or regulators push back.*

- Primary question: what breaks if integrations weaken, permissions change, or trust is lost?
- Define data ownership, export, deletion, consent, and privacy posture.
- Identify third-party API, app-store, and policy dependencies.
- Define fallback modes: file import, partial sync, degraded analysis.
- Define AI-assisted insights boundaries if any are added later.
- Output: privacy and data-rights policy, dependency map, fallback plan, operational risk inventory, trust kill criteria.

### Step 7: Red Team & Go / No-Go

*Focus: attack the idea before execution.*

- Primary question: after every friction, limitation, and cost is included, is this still a compelling business?
- Attack the category framing, retention logic, integration dependence, science posture, growth assumptions, and pricing.
- Identify what would make this a feature instead of a business.
- Decide whether to proceed, narrow further, or stop.
- Output: ranked failure inventory, founder synthesis, residual risks, staged go / no-go decision.

## Workshop dependencies

```text
1. Demand & Positioning
        ↓
2. Behavior & Product Wedge
        ↓
3. Integrations & Ecosystem
        ↓
4. Pricing, Retention & Distribution Economics
        ↓
5. Science, Safety & Coaching Credibility
        ↓
6. Trust, Privacy & Platform Dependency
        ↓
7. Red Team & Go / No-Go
```

Later workshops may reopen earlier decisions if evidence invalidates them.

## Cross-cutting artifacts

- **Assumption Register:** every important belief, status, and evidence pointer
- **Kill Criteria Ledger:** numeric thresholds that stop drift and force honest decisions
- **Evidence Log:** interviews, landing pages, prototype tests, and experiments

The point of the process is not neat documentation. It is to avoid building a full product before the business case deserves it.
