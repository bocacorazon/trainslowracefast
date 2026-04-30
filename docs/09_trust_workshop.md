# Workshop 6 — Trust, Privacy & Platform Dependency

*Primary agent: Trust, Privacy & Platform Risk Architect*
*Secondary reviewers: Integrations & Ecosystem, Science & Credibility, SaaS Economics*
*Inputs: W3 (Strava chokepoint, FIT fallback, OAuth disconnect window), W4 (engineering budget), W5 (HRV/medical-disclosure data, claims policy, conditions-consent surface).*

---

## §1. Executive summary

**Locked decisions**

- **Sensitive data classification.** HRV streams, declared cardiac conditions, declared HR-affecting medications, and self-tagged recovery context (sleep, alcohol, illness, menstrual cycle) are treated as **GDPR Article 9 special-category data** and **CCPA Sensitive Personal Information**. They require explicit opt-in, app-layer encryption, no cross-purpose use, and granular delete.
- **Encryption.** TLS 1.2+ in transit; AES-256 at rest via managed-database / object-store native encryption; **app-layer envelope encryption** with separate keys for the sensitive-data subset listed above. Keys rotated annually.
- **Retention schedule** locked per tier (§2b). The most consequential tightening: **Strava OAuth revocation triggers 24-hour soft-delete and 30-day hard-delete** (was 30/60 in W3 §2b), aligning with a strict reading of Strava's data-deletion obligations.
- **Strava chokepoint resilience plan** locked with three mitigation tiers and a 30 / 90 / 180-day playbook (§2d). Tier-3 (concierge-mode email delivery, no platform dependency) is the survival floor; the business does not die if Strava cuts off, only slows.
- **Multi-tenant isolation.** PostgreSQL row-level security (RLS) policies enforced at the database layer, *not* at the application layer alone. All sensitive-field reads are audit-logged. Penetration test annually, budget $5–10k Y1.
- **Coach-view consent and audit.** Coach access requires explicit per-athlete grant, instantly revocable; every read of an athlete's data by a coach is audit-logged with timestamp and surface (Adherence view, Aerobic Trend view, etc.).
- **Affiliate data isolation.** Creators / affiliate partners receive only aggregated conversion attribution (signups, paid conversions, gross revenue attributed). No athlete identifier, no email, no training data, no location ever flows to creators. Implementation enforced at query layer (separate aggregated table; no joins to athlete PII).
- **Pause-subscription policy.** Up to 6 months pause/year; data preserved as-is; no new analyses computed during pause; Adherence/Aerobic Trend are recomputed on unpause from preserved raw data (no analytics backfill).
- **Conditions-consent surface (W5 hand-off):** before completing onboarding, athletes who declare medication or cardiac conditions see a plain-language summary of what is shown vs. suppressed; explicit acknowledgment required; updatable any time.
- **Aggregated research use of athlete data** is allowed only with **explicit double opt-in**, k-anonymity ≥10, fully de-identified, and is never shared with third parties. Default off; not bundled with the standard ToS.
- **Mandatory operational controls:** 2FA on all infrastructure tools, founder + first hire only with production DB access, quarterly access review, incident-response plan with 72-hour authority disclosure SLA (GDPR), cyber liability insurance bound at MVP launch (~$2k/yr).

**Open / reopened items**

- W3 OAuth disconnect window tightened (30/60 → 24h/30 day) — assumption register updated.
- Subprocessor list and DPA collection is a one-time setup task; tracked as a build-time todo.
- Pen-test scope at MVP launch is end-to-end including auth, RLS, and webhook validation; vendor selection deferred to MVP build phase.

**Confidence after Workshop 6:** ~80% the trust posture is defensible at MVP and the business survives a Strava cutoff with a realistic plan. Remaining risk is execution discipline (audit logging gaps, subprocessor drift, RLS misconfiguration). Pen-testing closes most of it.

---

## §2. Primary memo — Trust, Privacy & Platform Risk Architect

### §2a. Q1: What is the strongest version of the problem in your lane?

A consumer SaaS handling heart-rate, HRV, and self-declared medication/condition data sits at a higher trust bar than a generic training log. Five concrete pressures:

1. **Regulatory classification.** HRV plus declared conditions plus tagged context is plausibly health data under GDPR Article 9 and CCPA SPI. The cost of getting this wrong is regulatory exposure (€20M / 4% revenue under GDPR; per-violation under CCPA), but more practically, **brand collapse**: "training app leaks athlete medical data" is a category-ending headline.
2. **Cross-athlete leakage.** A single misconfigured RLS policy or coach-view bug can expose one athlete's HRV state to another's account. This is not just a privacy violation — it's a *clinical* leak (athlete B can infer medical state about athlete A). Treated as P0.
3. **Platform chokepoint.** W3 already flagged Strava as the primary integration risk; this workshop owns the **operational** response: how do we keep delivering value if Strava cuts us off, and at what cost?
4. **Subprocessor drift.** Stripe, Postmark, AWS, push providers — each is a potential data-flow exit. Drift over time (a vendor changes practices, a new tool is added without DPA) erodes the privacy posture quietly.
5. **Concierge / founder-led operations.** During exp-4 and the first 100 users, the founder is reading individual athlete data to write Loom Weekly Reviews. This is operationally fine but must be disclosed and consented; it is not "automated processing" that hides behind a system boundary.

The honest framing: **trust is a product, not a checklist.** The Convert ICP is exactly the kind of athlete who reads privacy policies. A weak posture loses them faster than a missing feature.

### §2b. Q2: What proposal best addresses that problem?

#### Sensitive data classification and handling

| Data type | Classification | Encryption | Opt-in required | Retention default |
|---|---|---|---|---|
| HRV stream values | Article 9 health / CCPA SPI | App-layer envelope encryption + AES-256 at rest | Yes — explicit at first HRV-bearing sync | Active account: indefinite; on delete: 30-day hard-delete |
| Declared cardiac condition flag | Article 9 health / CCPA SPI | App-layer envelope encryption | Yes — at onboarding declaration | Same as account; deletable independently |
| Declared HR-affecting medication flag | Article 9 health / CCPA SPI | App-layer envelope encryption | Yes — at onboarding declaration | Same as account; deletable independently |
| Self-tagged recovery context (sleep, alcohol, illness, stress, menstrual cycle) | Article 9 health / CCPA SPI | App-layer envelope encryption | Yes — at first tag (default-off feature per W5) | Same as account; granular delete per tag |
| Workout HR streams, pace, GPS summary | Personal data (standard) | Provider-native at-rest (AES-256) | Implicit at OAuth; explicit at FIT upload | Active account: indefinite; on delete: 30-day hard |
| Email, account credentials | Personal data (standard) | Provider-native at-rest; bcrypt passwords | At signup | Account lifecycle |
| Stripe payment data | Stored at Stripe, not at us | N/A (Stripe-managed) | At purchase | Stripe's policy + 7yr billing records |
| Affiliate attribution | Aggregated only, no PII | N/A | N/A | Indefinite; not athlete-linked |

**Hard rules:**

- Sensitive-data fields are **never** included in error logs, support exports, debug dumps, or analytics events.
- Sensitive-data fields are **never** sent to email body (Weekly Review emails contain rendered metrics, not raw HRV values).
- Sensitive-data fields are **never** part of any aggregate computation outside the athlete's own account, except under the explicit double-opt-in research path with k-anonymity ≥10.

#### Retention schedule (full)

| Tier | Active | Inactive grace | Hard delete |
|---|---|---|---|
| Active Pro | Indefinite | N/A | On request: 30 days |
| Active Free | Indefinite | 12 months no-login → email; 18 months → soft-delete | 24 months → hard-delete |
| Lapsed Pro (downgraded) | Free-tier limits apply; 90-day grace where Pro analytics remain visible | Same as Free thereafter | Same as Free |
| Paused subscription (Pro) | Up to 6 months/yr; data preserved; no new analyses | At 6 months → auto-cancel + free-tier policy | Same as Free |
| Strava OAuth revoked (account active) | Soft-delete provider-derived data within **24 hours**; account remains | Hard-delete provider data at **30 days** unless reconnected | At 30 days |
| Account deletion request | Soft-delete immediately; export available for 7 days | Hard-delete at **30 days** | Day 30 |
| Backups | Encrypted; rolling 30-day retention | N/A | Backups containing deleted data are fully purged within **90 days** of original delete |
| Stripe billing records | Per Stripe + 7-year regulatory retention | N/A | N/A — legal exception |

The 24h/30d Strava-revocation window supersedes the W3 §2b 30/60 specification.

#### Multi-tenant isolation

- **PostgreSQL row-level security (RLS)** policies on every table containing athlete data. Policies enforce `athlete_id = current_setting('app.current_athlete_id')::int` (or equivalent) for all SELECTs.
- Coach views: a separate RLS policy permits SELECT on linked-athlete rows when `current_setting('app.coach_view') = athlete.coach_id` AND a valid `coach_view_token` exists.
- All raw SQL is rejected in app code; ORM queries are reviewed for RLS bypass risk.
- **Audit log** on every read of sensitive-data fields, with: actor (athlete-self / coach / system / founder-during-concierge), surface, timestamp, athlete_id, fields touched. Stored in a separate audit-only database, retention 24 months.
- Webhook validation: every Strava/Garmin webhook payload's `athlete_id` is verified against our subscription mapping before processing. Mismatches are logged and rejected.
- Pen-testing: third-party annual; budget $5–10k Y1, $10–15k Y2 with Garmin direct in scope.

#### Coach view consent and audit

- Athlete grants per-coach access from a settings panel; can revoke instantly.
- Grant is **scope-limited**: coach sees Adherence %, Drift, Aerobic Trend, Weekly Reviews summary. Coach **never** sees Recovery delta, declared medical conditions, declared medications, or tagged recovery context — even if the coach is a credentialed medical professional. The Trust posture is uniform.
- Every coach read is audit-logged.
- Athlete sees a read-history view ("Your coach viewed your Adherence on April 3").
- Coach onboarding includes a code-of-conduct acknowledgment: no using athlete data for non-coaching purposes; no aggregating across athletes for any commercial purpose without explicit per-athlete consent.

#### Affiliate program data isolation

- Creators receive a unique referral code at partnership signup.
- Conversion attribution is computed in a separate aggregated table (`affiliate_attribution_summary`) with: referral_code, signup_count, paid_count, gross_revenue_attributed, period.
- Creator-facing dashboard reads from this aggregated table only; no joins to athlete PII.
- A **W6-build-time audit** confirms no path exists from the creator dashboard to athlete data. Re-audited at any major schema change.
- Privacy policy explicitly names creators / affiliate partners as a data-sharing relationship and specifies the aggregated-only nature.

#### Pause-subscription mechanics

- Athlete can pause from settings; up to 6 months total/year.
- During pause: no charges, no new analyses computed, no Weekly Reviews sent, no creep alerts. Data preserved as-is including any sensitive fields.
- Historical views remain accessible (the athlete's own data, free-tier-limited unless they reactivate).
- On unpause: Adherence and Aerobic Trend recomputed from preserved raw data going forward; **no analytics backfill** of the paused period (Science §4 #2 adopted).
- After 6 months paused: auto-cancel with full free-tier-policy retention.

#### Conditions-consent surface (W5 §5 #2 implementation)

- Triggered when athlete declares cardiac condition or HR-affecting medication during onboarding.
- Before onboarding completes, athlete sees:
  - "Based on what you told us, here's what you'll see: ✓ Adherence % ✓ Drift minutes ✓ Weekly distribution"
  - "Here's what's suppressed and why: ✗ Recovery delta — HRV interpretation can be misleading on your medication/condition. ✗ Aerobic Trend at full confidence — HR-based metrics may be unreliable; we'll show with caveats."
  - "You can update these declarations any time in settings."
  - Explicit "I understand" button to proceed.
- Reviewable in settings → Conditions / Medications panel; changes trigger a re-consent.

### §2c. Q3: What failure mode or unresolved unknown remains?

#### Subprocessor inventory and DPA matrix

| Subprocessor | Purpose | Data accessed | DPA status |
|---|---|---|---|
| AWS / hosting (region: EU + US) | App + DB hosting | All app data | Standard DPA + SCCs |
| Stripe | Payments | Email, billing details (no training data) | Standard DPA |
| Postmark (or SendGrid) | Transactional email | Email content (Weekly Review rendered HTML, no raw HRV) | Standard DPA |
| Cloudflare (or AWS SQS) | Queues + edge | Webhook payloads in transit | Standard DPA |
| FCM / APNS | Push delivery | Push token + alert text (no sensitive data) | Standard ToS |
| Sentry (or equivalent error tracker) | Error monitoring | **No PII**, scrubbed automatically | DPA + scrubbing config |
| Plausible / no analytics | Product analytics | Pseudonymous events only; **no sensitive-data events ever** | DPA |
| Strava / Garmin | Data source (not subprocessor in classic sense) | Raw activity and HR data | Their ToS + our extra protections |
| Founder + first hire (during concierge) | Manual review for exp-4 | Workout-summary view; no raw HRV | NDA + access logged |

The list is **published on a public Trust page**. Material additions trigger a 30-day notice; replacements within a category (e.g., Postmark → SendGrid) trigger a notice but not a 30-day waiting period (Integrations §3 #2 adopted).

#### Strava chokepoint resilience plan

The W3 K-W3-2 risk (Strava TOS change restricting derived weekly summaries) is the largest single operational threat. The plan:

**Mitigation tiers (always in place):**

- **Tier 1 — Garmin direct.** Phase-1.5 partnership; covers ~50% of Convert at native fidelity.
- **Tier 2 — FIT manual upload.** Always available; 100% coverage for any athlete willing to upload; reduces but does not eliminate friction.
- **Tier 3 — Concierge / email-only delivery.** The Weekly Review can be delivered via email from FIT-uploaded or coach-shared data with no Strava platform surface. This is the floor — the business does not die at Tier 3, it slows.

**Playbook if Strava revokes or restricts:**

| Day | Action |
|---|---|
| 0–24h | Detect (TOS announcement or API failure); incident-response activation; status page update |
| 24–72h | All-user email + in-app notice; explain what's affected; offer FIT upload UX walkthrough; pause new Strava-only signups |
| Day 3–14 | Accelerate Garmin direct (if approved); push FIT-upload-first onboarding; offer 60-day free Pro extension to affected users |
| Day 14–30 | Assess: how many Convert athletes are using FIT upload? What's retention vs. pre-cutoff baseline? |
| Day 30–90 | If Garmin direct online: announce; migrate users; resume normal CAC. If Garmin denied: accelerate Wahoo + Coros |
| Day 90–180 | Reassess viability. If direct integrations cover ≥70% of Convert volume, business is intact. If <50%, evaluate beachhead pivot or sustain at smaller scale |

**Required pre-condition:** the playbook only works if FIT upload is *production-ready and tested* at MVP launch, not a future feature. Confirmed in W3 §6 #1 / §6 #2.

#### Operational controls

- **Access control.** Production DB and infrastructure tooling: founder + first hire only. 2FA mandatory. SSO (Google Workspace) + role-based.
- **Quarterly access review.** Document who has what access, terminate stale access.
- **Incident response plan.** Phases: detect → contain → assess → disclose → remediate → review. Roles: founder = incident commander; first hire = communications. Disclosure SLAs: 72h to authorities (GDPR), without undue delay to affected users (and within 30 days for high-risk incidents). Public post-mortem within 30 days for any incident with user impact.
- **Cyber liability insurance.** Bound at MVP launch, ~$2k/yr. Covers breach response costs, not regulatory fines.
- **Concierge-period disclosure.** During exp-4 and first 100 users: athletes consent at signup that "the founder may review your training data to write your Weekly Review during this pilot phase." Logged and time-bounded.

#### Open / unresolved items

1. **Pen-test vendor selection.** Deferred to MVP build phase. Budget $5–10k.
2. **DPO requirement.** GDPR Article 37 may require a Data Protection Officer if "core activities consist of large-scale processing of special categories of data." With ≤10k EU users, we are below most thresholds, but conservative reading suggests appointing a DPO at scale (~50k EU users). Tracked as A-W6-3 below.
3. **Backup encryption key custody.** Hosting provider manages backup encryption by default; for envelope-encrypted sensitive fields, the application key custody is a single point of failure. Recommendation: KMS-managed keys with audited access; key rotation quarterly during exp-4 (then settle to annual at MVP).
4. **Founder bus factor.** During concierge phase, only the founder + 1 hire have access. If the founder is incapacitated, athletes cannot get support or data export. Mitigation: legal handling instructions documented and stored with a trusted third party; at MVP-1, expand access controls.
5. **Strava TOS interpretation drift.** Quarterly review (W3 §4 #1) is locked but interpretation is judgmental. If a future TOS change is ambiguous, default to *more* restrictive reading; consult counsel before resuming any borderline use.

---

## §3. Secondary memo — Integrations & Ecosystem Strategist

Two operational notes.

1. **Strava revocation tightening (24h/30d) is correct.** It aligns with strict TOS reading and protects us from a future Strava enforcement action that targeted apps holding revoked-user data. Implementation cost: ~3 days engineering; absorbed in MVP build.
2. **Subprocessor change-notice policy.** The "30-day notice for category additions, no waiting period for within-category replacements" rule is industry-standard and right for our scale. Material risk to flag: the **payment processor** is hard to swap (Stripe vs. alternative), so a Stripe outage or terms change should trigger a more deliberate review than the standard rotation.

**My kill criterion (added):**
- **K-W6-3 (Integrations):** If any subprocessor announces a material data-handling change (e.g., new use of customer data for AI model training, changed data-residency posture), suspend new sensitive-data writes through that subprocessor within 7 days and evaluate replacement. If a viable replacement does not exist within 30 days, force the issue (re-evaluate the subprocessor relationship, not the feature).

---

## §4. Secondary memo — Science & Coaching Credibility Reviewer

Two notes that close W5 hand-offs cleanly.

1. **Cross-athlete leakage is clinical risk, not just privacy risk.** The audit log must capture sensitive-field reads at field-name granularity, not just row-level access. If an internal tool or coach view inadvertently exposes Recovery-delta state of athlete A to athlete B's session, the audit log must show *which field was read*, not just "row accessed." This is a build-time requirement.
2. **Pause-subscription analytics rule (no backfill) is correct.** When an athlete unpauses, recomputing from raw data going forward is honest; backfilling would create a misleading impression that the trend continued through the pause. Lock this UX explicitly: the Aerobic Trend chart shows a visible *gap* during the paused period, labeled "Paused — no data."

**My kill criterion (added):**
- **K-W6-4 (Science):** If any audit shows sensitive-field reads occurring outside the documented surfaces (athlete-self view, coach-grant view, founder-concierge during exp-4), treat as a P0 incident; remediate and disclose within the breach-disclosure SLA. (Same treatment as K-W5-3; this criterion is the *detection* mechanism.)

---

## §5. Secondary memo — SaaS Economics Analyst

Two cost notes.

1. **Privacy engineering is already absorbed in W4.** App-layer encryption (~2 weeks), granular delete (~1 week), export endpoint (~1 week), audit logging (~1 week) total ~5 engineer-weeks at $14–20k. This is part of the W4 ~$45k Y1 fixed engineering budget; no incremental ask.
2. **Compliance overhead is sub-linear with users.** ~1 hour/founder-time/week for privacy ops at MVP scale. Rises with volume of Data Subject Access Requests (DSARs) and subprocessor changes. At 50k+ users and an EU-heavy mix, expect a half-time privacy-ops person (~$40k/yr) — but that's a year-2+ problem, not a Y1 problem. Add as a planning note in W4 for Y2 budgeting.

**My kill criterion (added):**
- **K-W6-5 (Economics):** If DSAR (data export / deletion) request volume exceeds 1% of monthly active users sustained for 3+ months, the brand is signaling distrust and unit economics suffer (operations time + signal-to-acquisition decline). Investigate root cause; do not normalize as routine.

---

## §6. Founder synthesis

**Decisions locked**

1. Sensitive-data classification table and handling rules (§2b table). HRV, declared conditions, declared medications, tagged context = Article 9 / CCPA SPI; app-layer envelope encryption; explicit opt-in; no cross-purpose use.
2. Retention schedule by tier (§2b table), with the 24h/30d Strava-revocation tightening replacing W3's 30/60.
3. Multi-tenant isolation via PostgreSQL RLS at the database layer; sensitive-field reads audit-logged at field-name granularity (Science §4 #1 adopted).
4. Coach view: per-athlete grant, instantly revocable, scope-limited (Adherence/Drift/Aerobic Trend/Weekly Review only — never Recovery delta or declarations), audit-logged, athlete-visible read history.
5. Affiliate data isolation: aggregated-only attribution, no athlete PII to creators, build-time audit, public Trust page disclosure.
6. Pause-subscription: 6 months/yr max, data preserved, no in-pause analyses, no backfill on unpause; chart shows a visible "Paused" gap (Science §4 #2 adopted).
7. Conditions-consent surface at onboarding for declared medication / cardiac condition; updatable in settings; re-consent on change.
8. Aggregated research use of athlete data: explicit double opt-in, k-anonymity ≥10, de-identified, never shared third-party, default off.
9. Subprocessor list public; 30-day notice for category additions; within-category replacement notice but no waiting period; payment-processor changes get extra review (Integrations §3 #2 adopted).
10. Strava chokepoint resilience plan: Tier 1 Garmin direct, Tier 2 FIT upload, Tier 3 concierge email; 30/90/180-day playbook locked. FIT upload must be production-ready at MVP launch (W3 hand-off).
11. Operational controls: 2FA, founder + first-hire access only, quarterly access review, KMS-managed keys with quarterly rotation in pilot then annual at MVP, incident-response plan with 72h authority disclosure SLA, cyber liability insurance bound at MVP launch (~$2k/yr).
12. Concierge-period disclosure during exp-4 and first 100 users: explicit consent that the founder may read training data to write Weekly Reviews; logged and time-bounded.
13. Pen-test annually; budget $5–10k Y1, $10–15k Y2.
14. Founder bus-factor mitigation: legal handling instructions documented and stored with a trusted third party; access expansion at MVP-1.

**Reopened from earlier workshops**

- W3 §2b OAuth-disconnect window tightened from 30/60 to 24h/30d.
- A-W3-3 (Strava TOS permissive for next 12 months) — this workshop adds the operational playbook if it changes.

**Items pushed to later workshops**

- **W7 (Red Team & Go / No-Go):** scenarios to attack — Strava cutoff Day 0, medical incident with a declared-medication user, well-funded competitor (TrainingPeaks ships a "Convert mode"), creator-partner viral overclaim, M3 retention shortfall, founder bus-factor / runway exhaustion, data breach, zone-2 trend cooling vs. durable Convert demand. **Each of these must be walked through end-to-end in W7.**

**New entries for the assumption register**

- **A-W6-1:** RLS-based multi-tenant isolation prevents cross-athlete data leakage at production scale (≥10k Pro users) without significant query-performance regression. *Owner: pen-test + load-test at MVP launch.*
- **A-W6-2:** Strava-revocation 24h/30d window is enforceable operationally without missed deletions. *Owner: post-MVP audit at month 3.*
- **A-W6-3:** EU user share remains below the threshold that triggers a mandatory DPO appointment (conservative reading: ~50k EU users). *Owner: monitored at user-share milestones.*
- **A-W6-4:** Pen-test at MVP launch finds no critical vulnerabilities (zero P0/P1 findings); medium findings remediated within 30 days. *Owner: pen-test vendor selection + remediation plan.*
- **A-W6-5:** Subprocessor list remains stable through Y1 (no more than 1 category-addition per quarter, no payment-processor change). *Owner: ongoing.*

**New entries for the kill-criteria ledger**

- K-W6-1 (Trust): Any cross-athlete data leak detected (athlete A's data visible to athlete B account) → P0 incident, public disclosure, immediate root-cause and remediation within 72 hours. (Hard rule alongside K-W5-3.)
- K-W6-2 (Trust): Pen-test finds any critical (P0/P1) vulnerability that requires more than 14 days to remediate → halt new feature work until closed; consider delaying MVP launch if pre-launch.
- K-W6-3 (Integrations): Subprocessor announces material data-handling change → suspend sensitive-data writes within 7 days; replace within 30 days or escalate.
- K-W6-4 (Science): Audit shows sensitive-field reads outside documented surfaces → P0 (same treatment as K-W5-3).
- K-W6-5 (Economics): DSAR request volume >1% MAU sustained 3+ months → investigate brand-trust root cause; do not normalize as routine.

**Workshop 7 inbox (Red Team & Go / No-Go)**

The Red Team workshop should walk end-to-end through these scenarios:

1. **Strava cutoff at MVP+90 days:** does the Tier 1/2/3 plan actually hold? What's the user retention impact?
2. **Well-funded competitor moves into the Convert wedge:** TrainingPeaks ships a "Convert mode" or Intervals.icu adds a polarized-training-first toggle. What's our defensibility?
3. **M3 retention undershoots (lands at 40%):** the W2 loop didn't deliver. Pivot or kill?
4. **Channel mix collapses to paid-dominant:** creator partnerships underperform; CAC blows past $40. Rebuild or scale down?
5. **Medical incident:** an athlete on declared beta-blockers ignores the suppression notice, trains hard, and has a cardiac event. Is our claims policy and conditions-consent surface a defensible legal posture? What's the press-narrative risk?
6. **Creator-partner viral overclaim:** an aligned podcaster says "this app cures overtraining" on a 100k-listener show. We end the partnership — but the overclaim spreads. Damage assessment and recovery plan?
7. **Data breach at the application layer:** a misconfigured RLS policy exposes some HRV data. What's the disclosure timeline and the brand-recovery plan?
8. **Zone-2 trend cooling vs. durable Convert demand:** the cultural moment passes; popular interest in the method declines. Is the Convert ICP durable as a *segment* even if the *trend* fades?
9. **Founder bus-factor / runway exhaustion:** founder personal capital depletes at month 14, or founder is incapacitated. What survives?
10. **Garmin partnership denied:** Phase-1.5 falls through. Y2 plan?

Each scenario needs: a concrete failure path; the existing mitigations; what we accept as residual risk; what would force a kill or pivot.

**Confidence after Workshop 6:** ~80% the trust posture is defensible at MVP and the business survives a Strava cutoff with a realistic plan. Remaining risk is execution discipline (audit logging completeness, RLS misconfiguration, subprocessor drift). Pen-testing and a quarterly trust review close most of it. The Red Team workshop now has the full surface to attack.
