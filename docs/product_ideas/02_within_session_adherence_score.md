# Within-session adherence score

## Idea

Create a product-defining metric that scores how well an athlete executed the intended
session intensity plan.

Start at **100** and deduct points nonlinearly as the athlete spends more time above the
planned cap and/or deviates further from it.

Conceptually:

> Adherence Score = 100 - sum of excursion penalties

Where each excursion penalty increases with both:

- **duration above plan**
- **magnitude of deviation**

The penalty should be nonlinear so that brief small overshoots are tolerated while long
or large overshoots materially damage the score.

## Why it matters

Most mainstream metrics reward:

- total stress
- acute stimulus
- session hardness

This idea rewards something else:

- executing the session you meant to do
- preserving the integrity of easy days
- staying aligned with an aerobic-first plan

That is much closer to the core wedge than TSS or Garmin Training Effect.

## Desired behavior of the metric

- 10 seconds at +2 bpm should barely matter
- 5 minutes at +8 bpm should matter
- 25 minutes at +10 bpm should clearly show that the easy session became something else

## Design choices

### 1. Above-plan should hurt more than below-plan

On easy days, going a bit easier than planned is usually less damaging than drifting above
the cap. The score should be asymmetric.

### 2. Smooth sensor noise

Short noise spikes, HR lag, GPS oddities, and terrain-related blips should not destroy the
score.

### 3. Score against the actual plan

The reference should be the intended session prescription, not a generic zone model.

### 4. Keep it separate from load metrics

A session can be long and tiring while still being highly adherent. This score measures
execution, not total training stress.

## Product sentence

> Most platforms score how hard the workout was. We score how well you executed the
> workout you meant to do.

## Implementation cautions

- allow brief terrain-related overshoots
- keep formulas transparent enough to explain
- avoid overfitting to one sport or one sensor type
- make sure downstream metrics can coexist rather than compete with this score
