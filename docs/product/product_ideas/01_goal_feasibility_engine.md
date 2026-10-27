# Goal feasibility engine

## Idea

Create a positive, forward-looking product layer that estimates what a target race time
or performance goal usually requires, then compares that requirement to the athlete's
current training reality.

The promise is not just "stay disciplined." It is:

> Here is what your goal usually requires. Here is what your current training supports.
> Here is the gap.

## Why it matters

The aerobic-first wedge is credible, but "discipline, discipline, discipline" is weak as
the only emotional reward. A goal-feasibility layer gives the athlete something positive
to look toward:

- whether the target is currently supported, a stretch, or unrealistic right now
- what kind of sustained training dose is usually required
- where the current training is short (volume, long runs, consistency, sport-specific
  capacity)

This reframes restraint as progress toward a desired outcome instead of denial.

## Model families to start from

Do not try to infer everything from scratch from public datasets. Start from published,
sport-specific priors and calibrate later with product data.

- **Running:** Daniels / VDOT, Riegel, critical-speed style models
- **Cycling:** FTP, power-duration curve, Critical Power / W'
- **Swimming:** CSS
- **Rowing:** split / watts / distance relations
- **Triathlon:** combine single-sport engines later, not first

## Product shape

Each sport-specific engine should answer three questions:

1. **Current capacity** - What does the athlete's recent performance suggest today?
2. **Target requirement** - What does the desired outcome usually require?
3. **Gap and safest path** - What is missing, and can the athlete close the gap without
   abandoning the aerobic-first philosophy?

## Recommended sequencing

1. **Running first** - clearest models, lowest input burden, easiest first win.
2. **Cycling second** - strong physiology model, excellent fit with adherence work.
3. **Swimming or rowing third** - useful but smaller or less central to the initial wedge.
4. **Triathlon later** - high value, but only after the single-sport engines are solid.

## Positioning angle

This can become the positive promise layer on top of the adherence wedge:

- **Goal Feasibility:** what your target likely requires
- **Plan Adherence:** how well you are actually following the path to it

That is stronger than a generic zone-2 dashboard.

## Cautions

- Treat outputs as **probabilistic ranges**, not guarantees.
- Do not pretend there is one universal formula across sports.
- Public datasets are mostly weak for deriving true requirement models; they are more
  useful for signal work than for longitudinal training-dose insight.
- A Garmin or other platform partnership could help calibrate models later via
  user-consented histories, but should not be assumed as the starting point.
