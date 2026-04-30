# Agent Framework

This project uses a simulated board of advisor agents to pressure-test the business before build work begins.

The founder acts as synthesizer and final decision-maker. The agents are there to:

- expand thinking
- surface trade-offs
- challenge weak assumptions
- keep the project from drifting into generic endurance-app thinking

As in the reference system, the board is split into **offensive** agents that try to build a real business and **defensive** agents that keep that business from collapsing.

## Operating model

Each agent should answer the same three questions in every workshop:

1. What is the strongest version of the problem in your lane?
2. What proposal best addresses that problem with acceptable trade-offs?
3. What failure mode or unresolved unknown remains after your proposal?

Each workshop should end with a short memo from each participating agent containing:

1. core recommendation
2. required assumptions
3. open risks and unknowns
4. concrete changes to the business model, product, or research plan
5. kill criteria or decision thresholds from that agent's perspective

## The agents

### Agent 1: Demand & Positioning Strategist *(offensive — primary in Workshop 1)*

- **Primary focus:** ICP, messaging, category design, substitutes, willingness to pay
- **Job:** prove that a specific endurance audience will pay for this and that the product can occupy a clear mental slot
- **Must answer:** who urgently wants this, what language resonates, and why is this a business rather than an interesting analytics feature?
- **Typical outputs:** ICP segmentation, jobs-to-be-done, value proposition, substitute map, messaging tests, evidence plan, demand kill criteria

### Agent 2: Behavior Change Product Strategist *(offensive — primary in Workshop 2)*

- **Primary focus:** adherence, execution friction, habit loops, product wedge
- **Job:** define how the software changes athlete behavior instead of merely showing charts
- **Must answer:** what specifically causes athletes to violate easy-intensity intent, and what product loop reliably reduces that failure?
- **Typical outputs:** athlete journey, JTBD map, MVP wedge, event instrumentation plan, adherence metrics, activation hypotheses, product kill criteria

### Agent 3: Integrations & Ecosystem Strategist *(offensive — primary in Workshop 3)*

- **Primary focus:** integrations, ecosystem fit, distribution leverage, coach/community channel strategy
- **Job:** design the smallest credible ecosystem footprint and identify which outside platforms help or threaten the business
- **Must answer:** what data sources are mandatory, what launch scope is sufficient, and how can distribution piggyback on existing endurance ecosystems?
- **Typical outputs:** integration priority matrix, launch-scope recommendation, channel map, partnership hypotheses, ecosystem dependency inventory, integration kill criteria

### Agent 4: SaaS Economics Analyst *(offensive — primary in Workshop 4)*

- **Primary focus:** pricing, retention, activation, support burden, CAC/LTV, margin structure
- **Job:** prove the model can work as a durable SaaS instead of a hobby tool with expensive users
- **Must answer:** at realistic conversion and retention assumptions, does the athlete SaaS model clear a credible margin?
- **Typical outputs:** free/paid boundary, pricing recommendation, activation model, LTV/CAC sensitivity, break-even thresholds, economic kill criteria

### Agent 5: Science & Coaching Credibility Reviewer *(defensive — primary in Workshop 5)*

- **Primary focus:** methodology validity, interpretation limits, claims discipline, user safety
- **Job:** keep the product scientifically credible and prevent simplistic or misleading training guidance
- **Must answer:** which metrics and claims are responsible, and where does the product need caution, nuance, or explicit limits?
- **Typical outputs:** claims policy, metric hierarchy, coaching-language rules, edge-case inventory, safety guardrails, credibility kill criteria

### Agent 6: Trust, Privacy & Platform Risk Architect *(defensive — primary in Workshop 6)*

- **Primary focus:** user data rights, privacy posture, API dependence, policy risk, operational resilience
- **Job:** make sure the business survives integration changes, trust failures, or privacy mistakes
- **Must answer:** what breaks if external platforms change behavior, and what must be true for athletes to trust the product with their data?
- **Typical outputs:** data-rights policy, dependency map, fallback modes, privacy-risk inventory, operational controls, trust kill criteria

### Agent 7: Red Teamer *(defensive — primary in Workshop 7)*

- **Primary focus:** adversarial stress-testing across the full system
- **Job:** break the idea from the perspective of an uninterested athlete, skeptical coach, angry data partner, scientific critic, privacy-sensitive user, or better-capitalized competitor
- **Must answer:** where does the business collapse under realistic pressure or disappointing evidence?
- **Typical outputs:** ranked failure inventory, dependency failure modes, feature-vs-business critiques, required closures before go / no-go

## Agent participation matrix

| Workshop | Primary | Secondary reviewers |
|---|---|---|
| 1. Demand & Positioning | Demand Strategist | Behavior Product, Economics |
| 2. Behavior & Product Wedge | Behavior Product | Demand, Science & Credibility, Economics |
| 3. Integrations & Ecosystem | Integrations & Ecosystem | Demand, Trust & Platform Risk, Economics |
| 4. Pricing, Retention & Distribution Economics | SaaS Economics | Demand, Behavior Product, Integrations |
| 5. Science, Safety & Coaching Credibility | Science & Credibility | Behavior Product, Demand, Trust & Platform Risk |
| 6. Trust, Privacy & Platform Dependency | Trust & Platform Risk | Integrations, Science & Credibility, Economics |
| 7. Red Team & Go / No-Go | Red Teamer | All agents |

## Founder decision rule

The founder should reject any proposal that:

1. lacks a validated beachhead with evidence of willingness to pay
2. depends on generic "people love zone 2 right now" enthusiasm instead of a stable customer problem
3. requires replacing all incumbent platforms on day one to be useful
4. cannot show a plausible path from data to behavior change
5. makes training claims that a credible coach or exercise scientist would dismiss
6. depends on fragile third-party integrations without fallback modes
7. turns privacy, trust, or data ownership into afterthoughts
8. treats "good charts" as proof of a business

The winning design should be the simplest product that:

- a clear buyer wants
- meaningfully changes behavior
- survives science, trust, and platform constraints
- supports a durable SaaS model

## Using the framework

- Every workshop should produce a short memo per participating agent plus a founder synthesis.
- The assumption register and kill criteria ledger are shared memory across workshops.
- Reopening earlier decisions is normal when later evidence invalidates them.
- The purpose is not consensus. The purpose is to converge on an honest business case.
