# Workshop 3 — Integrations & Ecosystem

*Primary agent: Integrations & Ecosystem Strategist*
*Secondary reviewers: Demand & Positioning, Trust & Platform Risk, SaaS Economics*
*Inputs: W1 locks (the Convert, $12/mo Pro), W2 locks (behavior loop, 2 h creep-alert SLA, Five metrics, exp-4 design), W2 inbox.*

---

## §1. Executive summary

**Locked decisions**

- **MVP integration scope:** Strava OAuth + webhooks (primary path) and FIT file manual upload (fallback). No other direct device integrations at MVP.
- **Phase-1.5 (within 6 months of MVP):** Direct Garmin Activity API integration. Partnership application opens **now**, in parallel with exp-4, so approval lead time doesn't gate launch.
- **Phase-2 (post-product-market evidence):** Wahoo Cloud API, Polar AccessLink, Coros Open API. Apple HealthKit gated on iOS app decision (deferred).
- **Activity types at MVP:** Run + Ride only. All other types ignored or hidden; manual tagging available but not relied on.
- **Coach role:** data model accepts a `coach_id` link from MVP-1; no coach UI at MVP. This protects the Phase-2 distribution channel without adding launch scope.
- **Strava is treated as a chokepoint, not a partner.** Any architecture decision that would deepen lock-in (e.g., proprietary use of Strava-only fields) is rejected. The Weekly Review can be delivered via email + web app without any provider's app surface.
- **Web-first remains correct.** No native mobile app at MVP; push notifications via web push (Notification API) for desktop and Android; iOS receives email-only fallback for the creep digest. Revisit at Phase-2 if iOS conversion lags.

**Open / reopened items**

- The 2-hour creep-alert SLA from W2 is **conditional on Strava's device-to-webhook chain** delivering ≤90 minutes for the P80 athlete. Strava webhook itself is fast (<1 min from activity creation), but the *device → Strava* leg is opaque and varies by manufacturer. Empirical measurement during exp-4 is required to confirm. (A-W2-2 stays open until exp-4 latency data lands.)
- iOS push delivery is the largest UX gap of the web-first decision. Mitigated by email + 8pm digest fallback from W2, but should be re-examined if iOS users underperform in retention.
- Garmin Activity API approval is uncertain. We assume "likely" but plan for "denied" via Strava-only Phase-1.

**Confidence after Workshop 3:** ~70% the integration scope can deliver the W2 loop at MVP. The largest residual risk is platform — specifically a Strava TOS change restricting derived insight delivery — and is owned by W6.

---

## §2. Primary memo — Integrations & Ecosystem Strategist

### §2a. Q1: What is the strongest version of the problem in your lane?

The Convert lives across at least four device ecosystems (Garmin dominant, then Wahoo for cyclists, then Coros, with a long tail of Apple/Polar/Suunto). They will not switch devices for our app, and they expect "it just works" from day one. At the same time, every direct integration costs developer time, ongoing maintenance, partnership effort, and increases the surface area of breakage.

The honest framing:

1. **Where does the data already aggregate?** For the Convert ICP, **Strava is the de-facto aggregation layer**. Empirically, 85%+ of Garmin owners auto-sync to Strava; Wahoo and Coros users do too. Building on Strava buys roughly the entire device universe at the cost of one OAuth integration.
2. **What does the Strava dependency cost?** Strava has tightened API terms in late 2024 and may continue to. Apps that displayed others' data, used AI/ML on activity content, or duplicated Strava features were cut. Our product (own-data only, derived insight delivery, no social) is on the safe side of every published rule, but the *policy itself* is the risk, not our specific use.
3. **What is the latency budget?** W2 requires creep alerts ≤2 h from sync. Strava's webhook fires within seconds of activity ingest; the unknown leg is **device → Strava**. Garmin Connect's "auto-sync to Strava" runs every ~15 min; Wahoo is faster; Coros varies. The realistic P80 budget is **device-finish → Strava: 5–30 min** + **Strava webhook → us: <1 min** + **our processing → push: <2 min**. Total: ~10–35 min P80, well inside the 2 h SLA. Long tail (athletes who manually push, sit on the device, ride without phone) blows past 2 h; the 8pm local digest from W2 catches them.
4. **What can't Strava do for us?** Three things: (a) raw HR streams at 1 Hz are sometimes truncated or smoothed when they pass through Strava; the FIT/TCX original is cleaner. For Adherence % accuracy this matters at the margin. (b) Strava's webhook payload doesn't include the stream; we have to fetch it (one extra API call per activity, fine within rate limits). (c) Strava cannot deliver pre-workout state; we don't need it for the W2 loop.
5. **What about athletes who don't use Strava?** Real but small fraction of the Convert. **FIT manual upload** is the answer at MVP; it preserves the wedge for a determined athlete and forms the natural fallback if Strava ever cuts us off.

### §2b. Q2: What proposal best addresses that problem?

#### Integration priority matrix

| Provider | Decision | Rationale | Risk |
|---|---|---|---|
| **Strava** | MVP — primary | Aggregates 85%+ of target devices; OAuth + webhook proven; rate limits comfortable for our workload | Policy / TOS change is the #1 platform risk |
| **FIT manual upload** | MVP — fallback | Open format, free parsers (fit-sdk, fitparse); covers any athlete with a device that exports FIT; required as Strava-cutoff fallback | None — owned by us |
| **Garmin Activity API** | Phase-1.5 (apply now) | Largest single device base; direct integration removes Strava chokepoint dependency; supports the "data sensitive about Strava" subset of the Convert | Approval is partner-discretionary; lead time 4–12 weeks; pricing unknown |
| **Wahoo Cloud API** | Phase-2 | Strong cyclist subset of the Convert; clean OAuth API; lower volume justifies later | Smaller user base — capital efficient to defer |
| **Coros Open API** | Phase-2 | Rising in Convert demographic, especially trail runners; partnership-gated, less mature | Maturity / docs risk — verify scope before committing |
| **Polar AccessLink** | Phase-2 | Mature API but small Convert footprint | Low — defer is safe |
| **Suunto** | Defer indefinitely | Negligible Convert footprint | None — Strava covers them at MVP |
| **Apple HealthKit** | Gated on iOS app decision | Requires native iOS app; HealthKit data quality for HR streams is mixed; significant scope | Major scope — should not be MVP |

**Net MVP scope:** one OAuth integration (Strava) + one upload pipeline (FIT). Two surfaces.

#### Strava-specific architecture

- **OAuth scopes requested:** `read`, `activity:read_all`. We do **not** request `activity:write` (we never publish back to Strava — preserves trust and avoids TOS minefield).
- **Webhooks:** subscribe at app level; receive `aspect_type=create` events; immediately enqueue a `streams` fetch (HR, time, distance, altitude, watts if present).
- **Storage:** persist the parsed stream and our derived metrics; never re-display Strava's raw activity feed or comments. Show only the athlete's own data and our derived views.
- **Rate-limit headroom:** Strava is 200 req/15min, 2000/day per app. At 10k users averaging 1 activity/day, peak burst is well under the 15-min limit; the daily limit is the binding one and is comfortable until ~50k DAU. Above that, request a higher tier.
- **Disconnect handling:** if a user revokes OAuth, last-known data is preserved for 30 days behind a "reconnect" prompt; on day 31, archived; on day 60, deleted. This is also the **Trust** position (W6).

#### Garmin Phase-1.5 plan

- File partnership application **this week** with the business case: derived training-discipline insights, own-data-only delivery, no marketing of Garmin product, no sale of Garmin-derived data, no AI/LLM use of activity content.
- Build the Garmin connector behind a feature flag; ship to a closed cohort once approved. Public availability follows the first 100 paid users.
- If Garmin denies, we are still viable on Strava-only at MVP. The cost of denial is a slower P80 latency for the ~15% of athletes who don't push to Strava — acceptable.

#### FIT upload UX

- Drag-drop in the web app. Bulk upload supported (athletes restoring history).
- Parser handles `.fit`, `.tcx`, `.gpx` (HR-bearing). Activities deduplicated against Strava ingestion by start-time + device-id + duration.
- Manual upload is **not** marketed as a primary flow — it's a fallback and a "load your history" option. Onboarding always offers Strava first.

#### Activity-type taxonomy

| Strava type | Our type | MVP behavior |
|---|---|---|
| Run, Trail Run, Virtual Run, Treadmill | Run | Full loop |
| Ride, Gravel Ride, Mountain Bike Ride, Virtual Ride | Ride | Full loop |
| Walk, Hike | (ignored) | Imported but excluded from Adherence % |
| Swim, Workout, WeightTraining, Yoga, Crossfit, etc. | (ignored) | Imported but suppressed in views |

Easy-tag is per-activity, defaulted from the activity's planned-vs-actual context (planned-easy = tagged-easy; everything else is untagged and excluded from Adherence %).

#### Coach role at MVP-1

- Data model from day 1 includes `coach_id` (nullable foreign key on athlete) and a `coach_view_token` table.
- No coach UI at MVP. No coach signup flow. No coach billing.
- MVP-1 (post-launch, if signal warrants): coach gets a read-only dashboard listing linked athletes' Adherence %, Drift, and Aerobic Trend. Coaches do not pay; cost-of-acquisition channel.
- Avoids the W2 economics trap of building a coaching product before athlete behavior change is proven.

#### Channel hypotheses (distribution, not integration, but related)

- **Endurance content creators in the durable-aerobic niche.** Floris Gierman (Extramilest), Stephen Seiler interviews, low-HR-running YouTube long tail, MAF-method podcasts. These creators have the Convert audience. Sponsored guest appearances + affiliate codes are the cheapest test.
- **Subreddit and forum presence:** r/AdvancedRunning, r/Triathlon, r/Running, MAF Method Facebook group, Slowtwitch forum. Founder-led, content-first; never spam.
- **Strava clubs** as a low-cost discovery surface (we operate a club, share weekly aggregate adherence stats with consenting members).
- **Coach-led athletes via the MVP-1 coach view.** Each coach we land brings 5–30 athletes.
- Cold paid (Meta, Google) deferred until LP exp-2 has a working CPL — paid is the W4 question, not W3.

### §2c. Q3: What failure mode or unresolved unknown remains?

1. **Strava policy risk.** A future TOS change could restrict derived weekly summaries even of an athlete's own data. Mitigations: (a) Garmin direct in flight; (b) FIT upload always available; (c) email + concierge delivery does not require platform surface area. We can survive a Strava cutoff — the question is at what cost to growth, not viability.
2. **Latency tail for athletes who manually push.** ~15% of Convert athletes don't auto-push from Garmin to Strava (they batch). For them, the 2 h SLA fails routinely; the 8pm digest catches them but the *teachable-moment* benefit erodes. Direct Garmin integration fixes this; until then, accept and message it ("Connect your Garmin directly when available — Pro feature").
3. **HR stream fidelity through Strava.** Strava sometimes downsamples or smooths HR streams on long activities. Adherence % computed from Strava streams may differ by ±2 pp from the same calculation on the original FIT. Tolerable; flagged for W5 as a science note.
4. **Partnership uncertainty.** Garmin can deny or quote impractical pricing. Plan A: ship Strava-only. Plan B: defer Garmin direct to year 2.
5. **iOS push gap.** Web-first means iOS users get email-only for creep digests. If retention data shows an iOS shortfall, the iOS app jumps the queue.
6. **FIT format quirks.** Vendor-specific FIT extensions occasionally break parsers. Solution: instrument parse failures, fall back to GPX/TCX, surface "we couldn't fully parse this file — please reupload from source" in the UI.

#### Provider latency measurement (must be in exp-4 instrumentation)

For every synced activity in exp-4, log:

- `device_finish_ts` (from FIT or Strava activity start + duration)
- `strava_activity_create_ts` (from Strava API)
- `strava_webhook_received_ts`
- `our_processing_complete_ts`
- `push_delivered_ts` (from FCM / APNS / web push provider)

P50 / P80 / P95 reported across the cohort; segmented by device manufacturer. This data resolves A-W2-2 and is the input for any change to the 2 h SLA.

---

## §3. Secondary memo — Demand & Positioning Strategist

The Strava-first call is correct for the Convert at MVP, but two things to flag.

1. **Brand-promise / integration-coverage discipline.** The W1 frame is "read-only over your existing platforms." If the LP says "works with Garmin, Wahoo, Coros, Polar, Apple, Strava" but the reality at MVP is "Strava is the bridge for everything else," athletes will be confused at signup ("why does it ask me for Strava when I have a Garmin?"). Recommendation: LP copy says **"Connects to your Garmin, Wahoo, Coros, and Polar — automatically, through Strava."** One sentence. Tested as an A/B variant in exp-2. Don't hide the chain; own it.
2. **Activation friction.** Strava OAuth is one click for athletes already on Strava and 4–6 minutes for athletes who aren't (signup, email verify, device sync, OAuth). If 15% of the Convert isn't on Strava, that's a real funnel hit. Add a **"don't have Strava? upload a FIT file"** path on the OAuth screen, not buried in settings. Eliminates the dead-end.

**My kill criterion (added):**
- **K-W3-1 (Demand):** If exp-2 LP variants that name "Garmin, Wahoo, Coros" in the headline outperform "works with Strava" by ≥40% on email capture *and* Strava OAuth completion rate is below 70% in exp-4, the Strava-first framing must be revisited even though the architecture is correct.

---

## §4. Secondary memo — Trust, Privacy & Platform Risk Architect

The Strava chokepoint is real and the mitigations are sound, but I want three things tightened.

1. **Strava TOS quarterly review.** Schedule a recurring (every 3 months) read of Strava's API Agreement and Brand Guidelines. Flag any change that touches: (a) re-display of athlete-own data, (b) email/derived insights, (c) AI/ML use, (d) charging for derivative features. Document the read in `docs/_platform_risk_log.md` (new file). This is a 30-minute task per quarter and worth it.
2. **Data minimization on Strava ingest.** We import HR, time, distance, altitude, watts (when present), GPS — but for the W2 loop, **GPS is not required** outside of activity-type detection. Recommendation: store GPS only at session-summary granularity (start/end coordinates, total distance, elevation gain), not the full polyline. This (a) reduces storage cost, (b) reduces breach blast radius, (c) is more privacy-defensible, (d) sidesteps any future Strava TOS change about polyline reuse. If a user wants polyline-aware features later, opt in.
3. **Disconnect / data-portability defaults.** From W2 we already have: 30-day reconnect window, 60-day deletion. Add: **one-click full data export (CSV + raw FIT bundle) at any time**. This is a Trust feature, but also a *Strava-cutoff insurance* feature — if Strava cuts us, athletes export and re-upload via FIT, and they keep their Adherence history. Build it in MVP, not later.

**Mandatory disclosures:** privacy policy, data-processing terms, Strava attribution badge (per their brand guidelines), GDPR cookie/consent banner. These are W6 detail but the surfaces must exist at MVP.

**My kill criteria (added):**
- **K-W3-2 (Trust):** If Strava issues a TOS change that restricts derived weekly summaries delivered to the athlete via email or web, business viability is materially threatened; activate Garmin-direct timeline acceleration and consider a feature-set pivot. Treat as a P0 incident if it occurs.
- **K-W3-3 (Trust):** If any provider's webhook or API exposes data of *another athlete* to our system through misconfiguration (e.g., a shared club leak), kill the integration immediately and disclose. Have an incident response plan ready before MVP launch.

---

## §5. Secondary memo — SaaS Economics Analyst

Three economic notes, all minor.

1. **Strava API is free at our volume.** Cost to MVP: $0 in API fees. Garmin partnership pricing is the unknown. Budget assumption: **$0 at Phase-1.5** (most consumer integrations at this stage are free with revenue-share triggers above thresholds we won't hit). If Garmin quotes >$25k/year minimum, we defer Garmin direct to year 2 — see kill criterion below. This does not break the wedge.
2. **FIT parsing has near-zero marginal cost.** Open-source parsers (fit-sdk, python-fitparse, fit-decode) handle the format. Maintenance cost is one engineer-day per quarter as vendor extensions evolve.
3. **Webhook + push infrastructure cost.** At 10k Pro users averaging 1 activity/day: ~10k webhook events/day, ~10k pushes/day. Cost: <$50/month on a managed queue (e.g., Cloudflare Queues, AWS SQS) and <$30/month on push providers. Trivial. The cost story doesn't change W4 economics.

**My change request:** Add provider-cost line items to the W4 unit economics model so Garmin direct (if it costs anything) gets visibility before the partnership conversation closes.

**My kill criterion (added):**
- **K-W3-4 (Economics):** If Garmin Activity API partnership requires >$25k/year minimum or revenue share >5%, defer Garmin direct to year 2 and reassess at year 1 retention data. Do not gate MVP on Garmin.

---

## §6. Founder synthesis

**Decisions locked**

1. MVP integration scope: Strava OAuth + webhooks (primary) and FIT manual upload (fallback). Two surfaces.
2. OAuth scope is `read` + `activity:read_all` only. No write scope, ever. We never post to Strava.
3. Garmin Activity API partnership application opens this week, in parallel with exp-4. Phase-1.5 ship target: 6 months post-MVP, conditional on approval and pricing.
4. Wahoo, Polar, Coros: Phase-2, gated on retention evidence.
5. Apple HealthKit: deferred; revisit when an iOS-app decision is made (not at MVP).
6. Activity types at MVP: Run + Ride. Everything else imported but excluded from views.
7. Coach role: `coach_id` data model from day 1, no UI at MVP, read-only dashboard at MVP-1.
8. Strava data minimization: store GPS at session-summary granularity, not full polyline (Trust §4 #2 adopted).
9. One-click full data export (CSV + raw FIT bundle) ships at MVP (Trust §4 #3 adopted).
10. LP copy names devices explicitly with the connection chain: "Connects to your Garmin, Wahoo, Coros, and Polar — automatically, through Strava." Tested as A/B in exp-2 (Demand §3 #1 adopted).
11. OAuth screen offers FIT-upload alternative inline (Demand §3 #2 adopted).
12. Quarterly Strava TOS review logged in `docs/_platform_risk_log.md` (Trust §4 #1 adopted).
13. exp-4 instrumentation captures full latency chain (`device_finish` → `push_delivered`) per activity, segmented by manufacturer, to resolve A-W2-2.
14. Web-first (no native mobile app) confirmed at MVP. iOS push gap accepted; revisit if iOS retention lags.

**Reopened from earlier workshops**

- A-W2-2 (2 h creep-alert SLA) — reframed: SLA is achievable for athletes who auto-sync to Strava (likely ≥80% of the Convert), with an 8pm digest fallback for the manual-push tail. Confirmed once exp-4 latency data lands.

**Items pushed to later workshops**

- **W4 (Economics):** add provider-cost line items (Garmin partnership, push, queues, FIT storage) to the unit economics model. Re-examine $12/mo Pro defensibility against the actual cost stack.
- **W5 (Science):** note the HR-stream-fidelity-through-Strava margin (±2 pp on Adherence %) in claims policy. Decide whether to surface "computed from Strava stream" vs. "computed from original FIT" provenance to athletes.
- **W6 (Trust):** Strava brand attribution requirements; GDPR/CCPA implementation; medication / cardiac data sensitivity from W2 must be reflected in the privacy policy; data-export and deletion SLAs.
- **W7 (Red Team):** Strava cutoff scenario as a top-of-list failure mode. What is the 30-day, 90-day, 180-day plan if it happens?

**New entries for the assumption register**

- **A-W3-1:** Strava auto-sync covers ≥80% of Convert-owned devices (Garmin, Wahoo, Coros, Polar) with end-to-end latency ≤90 min P80. *Owner: exp-4 latency data.*
- **A-W3-2:** Garmin Activity API partnership is approvable for our use case at $0–$25k/year. *Owner: partnership conversation, target outcome by month 3 of MVP.*
- **A-W3-3:** Strava's API Agreement remains permissive for athlete-own derived insight delivery via email/web for the next 12 months. *Owner: quarterly TOS review.*
- **A-W3-4:** FIT manual upload UX is sufficient as a fallback for Strava-less athletes (~15% of Convert) at acceptable activation rates. *Owner: exp-4 + first-100 instrumentation.*
- **A-W3-5:** Web push (web app) is sufficient for the creep-alert UX on Android/desktop; iOS email-only fallback does not materially hurt retention. *Owner: post-MVP retention split by platform.*

**New entries for the kill-criteria ledger**

- K-W3-1 (Demand): exp-2 LP — if naming devices outperforms Strava-first messaging by ≥40% on email capture *and* Strava OAuth completion is <70% in exp-4, revisit framing.
- K-W3-2 (Trust): Strava TOS change restricting derived weekly summaries → P0; accelerate Garmin-direct, consider feature-set pivot.
- K-W3-3 (Trust): Cross-athlete data leak via any provider integration → kill integration immediately, disclose, incident response.
- K-W3-4 (Economics): Garmin partnership requires >$25k/year minimum or >5% revenue share → defer to year 2; do not gate MVP.

**Workshop 4 inbox (Pricing, Retention & Distribution Economics)**

- Model the **3-Weekly-Reviews-capped** free tier (W2) against alternative shapes (4-week full Pro trial; reverse-trial Pro→free).
- Activation funnel: signup → Strava OAuth (or FIT upload) → cap-set → 3+ sessions → first Weekly Review. Estimate drop-off at each step from analogous SaaS benchmarks; plan first-100 instrumentation.
- Retention: M1 / M3 / M6 retention targets given W2's 5-metric restraint. What's the activation-to-paid conversion target?
- CAC: blended target ≤$40 from W1; channel mix assumption (creator partnerships, organic content, community, Strava clubs, paid spend). Model creator-partnership CPM and conversion.
- Pricing stress tests: $9 / $12 / $15 monthly; $99 / $108 / $129 annual. Sensitivity analysis on conversion and LTV.
- Provider-cost stack from W3 §5 — bottom-up unit cost at 1k, 10k, 50k Pro users.
- Refund / chargeback policy and support load assumptions.
- Coach-channel economics for MVP-1: zero-cost coach seat, athlete-paid model — does it pay back the build cost?

**Confidence after Workshop 3:** ~70% the integration plan can deliver the W2 loop at MVP and survive provider risk. The largest remaining uncertainty is *policy* (Strava TOS) rather than *technical* — and W6 is the right place to address it. The W4 economic stress test now has a clean cost stack to work with.
