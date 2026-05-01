# HR spike anomaly correction

## Idea

Build a data-quality layer that detects bogus heart-rate spikes and either flags or
repairs them before they contaminate downstream metrics.

The motivating case came from marathon training in hot weather: sudden HR spikes that did
not match breathing or perceived effort, yet polluted max HR, average HR, zone time, and
other derived summaries.

## Why it matters

An aerobic-first product depends on trust. If raw data is visibly unfair or noisy, the
product's interpretation layer will be blamed even when the sensor is at fault.

Fixing or flagging these anomalies protects:

- max and average HR
- zone-time calculations
- training-load estimates
- adherence scores
- goal-feasibility models

This is not the core wedge, but it is a trust-enabling layer that makes every other metric
more believable.

## Candidate detection signals

- impossible or near-impossible HR slope
- short isolated spikes with fast return to baseline
- mismatch versus pace, power, cadence, or session context
- mismatch versus breathing or perceived effort notes when available
- known strap or wrist-sensor artifact patterns (contact issues, cadence lock, sweat /
  heat artifacts)

## Product stance

Corrections should be:

- **flagged**
- **reversible**
- **auditable**

Keep the raw trace. Mark corrected regions. Do not silently rewrite data in a way that
could hide genuine physiological events.

## Positioning angle

This can support a trust message such as:

> We do not just analyze your data. We help clean the artifacts that would otherwise make
> the analysis unfair.

## Cautions

- Distinguish sensor artifacts from real physiological anomalies.
- Avoid aggressive correction when confidence is low.
- Expose enough context for athletes to understand why a correction happened.
