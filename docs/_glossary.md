# Glossary

Acronyms, jargon, and shorthand used across the W1–W7 workshops, the assumption register, the kill-criteria ledger, and the platform-risk log. The business-plan section is first and most fully explained; training science, integrations, and privacy/infrastructure follow.

## Business plan, SaaS economics & validation framework

| Term | Expansion | What it means in our plan |
|---|---|---|
| **SaaS** | Software as a Service | Subscription-based hosted software. Our delivery model — $12/mo or $120/yr Pro tier, free tier capped at 3 Weekly Reviews. |
| **ICP** | Ideal Customer Profile | The sharply-defined target user. Ours is **"the Convert"**: an athlete moving away from intensity-maximizing tools (TrainingPeaks, Strava) toward an aerobic-first method, often after an injury or after reading the literature. |
| **MVP** | Minimum Viable Product | The smallest version of the product that delivers the wedge value and is testable in market. Our MVP scope: web-only, Strava OAuth + FIT upload, Run+Ride, Five metrics, behavior loop. |
| **MVP-1** | (informal) "the version after MVP" | Read-only coach dashboard and similar near-term post-launch features. |
| **Wedge** | (jargon, not acronym) | The narrow, sharp value proposition that opens the market door. Ours is "Easy days, done right." Wedges are deliberately narrow — they win one job before broadening. |
| **LP** | Landing Page | The marketing page used in **exp-1** to test demand at the cheapest possible price. Gate: ≥4% LP→email. |
| **WTP** | Willingness to Pay | What users say or demonstrate they will pay. Tested in exp-1 / exp-2; if median WTP <$10/mo, annual discount reverts from 17% to 25% (W4 §6 reopen rule). |
| **CAC** | Customer Acquisition Cost | Fully-loaded cost to acquire one paying customer (ad spend + creator commissions + tooling allocation, divided by paid signups attributed). Target blended ≤$25. **Kill threshold: >$40 sustained 90 days** (K-W4-3). |
| **LTV** | Lifetime Value | Total gross revenue from a customer over their lifetime, often net of COGS ("contribution LTV"). LTV / CAC ratio is the standard SaaS health number; ≥3 is healthy. |
| **ARR** | Annual Recurring Revenue | The annualized run-rate of subscription revenue. Y1 target: **$120k ARR (≈1k paid)**. Conservative: **$60k (≈500 paid)**. Wind-down trigger: <$40k at month 9 (K-W7-2). |
| **MRR** | Monthly Recurring Revenue | Same idea, monthly. ARR ≈ MRR × 12. |
| **COGS** | Cost of Goods Sold | Variable cost per user per month (hosting, payment processing, email, push). Modeled at **~$0.78/user/mo**, leaving ~93% gross margin at $12 list price. |
| **NPS** | Net Promoter Score | Loyalty survey: "How likely are you to recommend this to a friend?" 0–10 scale. NPS = % "9–10" minus % "0–6". Range −100 to +100. **exp-4 gate: NPS ≥30.** |
| **CSAT** | Customer Satisfaction (not used by us, but related) | Single-question satisfaction survey. We chose NPS because it correlates better with referral behavior. |
| **DAU / WAU / MAU** | Daily / Weekly / Monthly Active Users | Engagement denominators. We track WAU as the most diagnostic for our weekly-cadence product. |
| **M3 retention** | Month-3 retention | Fraction of athletes who started in a given month's cohort and are still actively using at month 3. **Load-bearing W4 assumption.** Threshold: ≥60% sustained 2 cohorts (K-W2-2). |
| **Cohort** | (jargon) | Group of users who started in the same period (typically same calendar month). Cohort retention curves are the SaaS engagement diagnostic. |
| **Churn** | (jargon) | Rate at which paid users cancel. Inverse of retention. Monthly churn ×12 ≈ annual churn for low values. |
| **Activation** | (defined term) | Our funnel step: signup → OAuth or FIT upload → cap-set → ≥3 sessions → first Weekly Review delivered, **within 14 days**. Net account→activation modeled at 54% (W4 §2b). |
| **Funnel** | (jargon) | The sequence of conversion steps from awareness to paid. Our top-of-funnel metric is LP→email; bottom is paid → M3 retained. |
| **Conversion rate** | (jargon) | Percentage moving from one funnel step to the next. E.g., "free → paid conversion rate" — we target ≥6% (kill at <6% sustained 2 cohorts, K-W4-2). |
| **Top-of-funnel** | (jargon) | Earliest awareness/acquisition steps (LP visits, email signups). Distinct from "bottom-of-funnel" (paid conversion, retention). |
| **Channel mix** | (jargon) | The distribution of customer acquisition across sources. Our W4 target: 35% creator partnerships, 25% organic, 15% community, 10% Strava clubs, 10% coach referral, 5% paid. |
| **Creator partnerships** | (jargon) | Paid affiliate relationships with podcasters / YouTubers / writers in the aerobic-training space. Largest single channel in our W4 model. |
| **Organic** | (jargon) | Non-paid acquisition: SEO, content marketing, word of mouth, social. |
| **Pre-pay** | (jargon) | Asking interview candidates to put money down ($50) for early access. The strongest demand signal — words are cheap, money is not. exp-2 gate: ≥6 of 15 willing. |
| **Concierge MVP** | (jargon) | A non-scalable pilot in which the founder manually delivers what the product will eventually automate. Our **exp-4** is a 13-week concierge — the founder writes each Weekly Review by hand. Validates the wedge before MVP build commits. |
| **Beachhead** | (jargon) | The narrow market segment where you land first and consolidate. Ours is the Convert ICP. |
| **B2B / B2C** | Business-to-Business / Business-to-Consumer | We are B2C at MVP. The coach/team product (B2B) is out of scope for W1–W7 validation. |
| **DTC** | Direct-to-Consumer | Often used interchangeably with B2C in subscription contexts. |
| **PLG** | Product-Led Growth | Acquisition model where the product itself drives signups (free trial, free tier, virality). Our free-tier-capped-at-3-Reviews shape is a PLG mechanic. |
| **TAM / SAM / SOM** | Total / Serviceable / Serviceable Obtainable Market | Standard market-sizing triple. Less prominent in our plan because we are a wedge product, not a horizontal play. |
| **Burn / Runway** | (jargon) | Burn = monthly cash spend. Runway = months of cash remaining at current burn. Ours: founder personal capital ~$90k over 18 months (≈$5k/mo blended burn). |
| **Bootstrap** | (jargon) | Self-funding a business from revenue and personal capital, no external investment. Our posture. |
| **Y1 / Y2 / MVP+90** | Year 1 / Year 2 post-launch / 90 days after MVP launch | Our MVP+90 hard checkpoint reviews 5 metrics; 4-of-5 = full plan, 2–3 = scope down, 0–1 = wind down (K-W7-3). |
| **exp-1** | Experiment 1 | Landing-page demand test (Stage 1 gate). |
| **exp-2** | Experiment 2 | Convert-candidate interview round (Stage 2 gate, 15 candidates). |
| **exp-3** | Experiment 3 | Pricing / WTP probe — folded into exp-1 / exp-2 in our adaptation. |
| **exp-4** | Experiment 4 | The 13-week concierge pilot (Stage 3 gate). |
| **A-W{n}-{seq}** | Assumption ID | E.g., A-W4-3 = third assumption added in Workshop 4. Tracked in `_assumption_register.md`. |
| **K-W{n}-{seq}** | Kill-criterion ID | E.g., K-W6-1 = first kill criterion from Workshop 6. Tracked in `_kill_criteria.md`. **Hard rules** (K-W5-3, K-W6-1, K-W6-4) must never trigger in production. |
| **Rev-2 / Rev-2.1** | Revision banner | Used when a later workshop reopens an earlier lock. |
| **Five MVP metrics** | (defined term) | Adherence % (Real Easy Minutes), Drift minutes, Aerobic Trend, Recovery delta (descriptive only), Weekly intensity distribution. Explicitly **not** TSS, CTL/ATL/TSB, FTP estimates, VO2max estimates, training-load colors, or readiness scores. |
| **The behavior loop** | (defined term) | Sync → ≤2h deviation digest → Sunday Weekly Review → Aerobic Trend reward (W2). The Sunday Weekly Review is the activating moment. |
| **The Convert** | (defined term, our ICP) | See ICP above. |
| **"Train slow, race fast"** | (defined term) | Locked brand line (W1). |
| **"Easy days, done right"** | (defined term) | Locked MVP headline (W1). |

## Training science & physiology

| Term | Expansion / meaning |
|---|---|
| **MAF** | Maximum Aerobic Function — Phil Maffetone's low-heart-rate aerobic-base method ("180 − age" cap). |
| **Zone 2** | The training zone at LT1 — fat-oxidation maximum, lactate near baseline. Cultural shorthand for "go slow, build aerobic base." |
| **Polarized** | Training distribution: ~80% low-intensity, ~20% high-intensity, little time in the middle. |
| **HR / HRmax** | Heart Rate / Maximum Heart Rate. |
| **HRV** | Heart Rate Variability — beat-to-beat variation. Proxy for autonomic state ("recovery"). Confound-prone. |
| **TSS** | Training Stress Score — TrainingPeaks' workload metric, normalized to 100 = 1-hour all-out. De-facto industry standard. |
| **CTL / ATL / TSB** | Chronic / Acute Training Load (42-day / 7-day exponentially weighted TSS) / Training Stress Balance (CTL−ATL). |
| **FTP** | Functional Threshold Power — ~1-hour sustained cycling power. Anchors power zones. |
| **VO2max** | Maximal oxygen uptake. We do **not** show watch-estimated VO2max (noise too high). |
| **LT1 / LT2** | Lactate Threshold 1 (aerobic threshold) / 2 (anaerobic threshold ≈ FTP). |
| **Aerobic drift** | Gradual HR rise at constant pace/power in long sessions. Durability indicator. |
| **Cardiac drift** | Aerobic drift driven by cardiovascular changes (heat/dehydration). |
| **Aerobic Trend** | Our metric: pace/power at sub-cap HR over a 14-day window. |
| **Real Easy Minutes** | Our Adherence definition (W5): minutes within cap, excluding stops >2 min and sub-walking segments. |

## Integrations & data

| Term | Expansion / meaning |
|---|---|
| **API** | Application Programming Interface — the published interface a vendor (Strava, Garmin) exposes. |
| **OAuth** | Open Authorization — delegated-access protocol for Strava/Garmin login. |
| **TOS** | Terms of Service — platform contractual rules. Quarterly review locked in W3. |
| **FIT** | Flexible and Interoperable Data Transfer — Garmin's binary workout file format. Our Tier 2 fallback. |
| **GPS** | Global Positioning System — route traces are retained only in a purpose-limited route-data layer with privacy controls; default product views use summary/derived fields. |
| **CSV** | Comma-Separated Values — export format. |
| **Webhook** | Vendor-pushed HTTP notification (e.g., new Strava activity uploaded). |
| **Phase-1.5** | Our shorthand for "between MVP and Y2" — when Garmin direct lands. |
| **Tier 1 / 2 / 3** | Strava-chokepoint resilience tiers (W6): Garmin direct / FIT manual / concierge email-only. |
| **CV** | Coefficient of Variation — std dev / mean. CV>12% triggers a noise label on Aerobic Trend. |
| **k-anonymity** | Privacy property where any record is indistinguishable from at least *k*−1 others. We require k≥10 for any aggregated research use of athlete data. |

## Privacy, security & compliance

| Term | Expansion / meaning |
|---|---|
| **GDPR** | General Data Protection Regulation — EU privacy law. |
| **Article 9** | GDPR clause covering health, biometric, genetic special-category data. Higher consent + security bar. |
| **CCPA / CPRA** | California Consumer Privacy Act / California Privacy Rights Act. |
| **SPI** | Sensitive Personal Information — CCPA/CPRA analog of Article 9. Includes health, biometrics, precise location. |
| **PII** | Personally Identifiable Information. |
| **DPA** | Data Processing Agreement — contract with each subprocessor. |
| **DPO** | Data Protection Officer — GDPR-required role under certain large-scale processing thresholds. Not required at MVP scale. |
| **DSAR** | Data Subject Access Request — user request to access/export/delete their data. >1% MAU sustained = trust signal (K-W6-5). |
| **SCC** | Standard Contractual Clauses — EU mechanism for cross-border data transfer. |
| **BAA / HIPAA** | Business Associate Agreement / US Health Insurance Portability and Accountability Act. We are **not** a HIPAA covered entity. |
| **RLS** | Row-Level Security — PostgreSQL database-layer isolation between athletes. |
| **KMS** | Key Management Service — managed key custody for envelope encryption. |
| **AES-256 / TLS / SSE** | Advanced Encryption Standard 256-bit / Transport Layer Security / Server-Side Encryption. |
| **SSO / 2FA** | Single Sign-On / Two-Factor Authentication. |
| **IR / SLA** | Incident Response / Service Level Agreement. GDPR breach disclosure SLA: 72h to authorities. |
| **P0 / P1** | Priority 0 / 1 — severity classifications. P0 = stop-everything. |

## Infrastructure

| Term | Expansion / meaning |
|---|---|
| **AWS** | Amazon Web Services. |
| **RDS / S3 / SQS** | AWS managed database / object storage / message queue. |
| **FCM / APNS** | Firebase Cloud Messaging / Apple Push Notification Service. |
| **ORM** | Object-Relational Mapping — code-to-database abstraction. RLS-bypass-reviewed. |
| **UX** | User Experience. |

## Integrations & data

| Term | Expansion / meaning |
|---|---|
| **API** | Application Programming Interface — the published software interface a vendor exposes for third-party apps. |
| **OAuth** | Open Authorization — the delegated-access protocol by which Strava users grant our app read access to their data without sharing their password. |
| **TOS** | Terms of Service — the contractual rules a platform (Strava, Garmin) imposes on third-party developers. |
| **FIT** | Flexible and Interoperable Data Transfer — Garmin's binary file format for workout data. Our Tier 2 fallback when OAuth is unavailable. |
| **GPS** | Global Positioning System — location tracking. Route traces are retained only in a purpose-limited route-data layer with privacy controls; default product views use summary/derived fields. |
| **CSV** | Comma-Separated Values — the export format for our one-click data export. |
| **Webhook** | A vendor-pushed HTTP notification (e.g., Strava notifies us when a new activity is uploaded). |
| **Phase-1.5** | Our shorthand for "between MVP and Y2" — when Garmin direct integration lands. |
| **Tier 1 / 2 / 3** | The Strava-chokepoint resilience tiers (W6): Tier 1 = Garmin direct, Tier 2 = FIT manual upload, Tier 3 = concierge email-only delivery. |

## Privacy, security, and compliance

| Term | Expansion / meaning |
|---|---|
| **GDPR** | General Data Protection Regulation — EU privacy law. Applies whenever we process personal data of EU residents. |
| **Article 9** | GDPR's special-categories-of-data clause covering health, biometric, genetic, and similar sensitive data. Higher consent and security bar. |
| **CCPA / CPRA** | California Consumer Privacy Act / California Privacy Rights Act — California's privacy laws. |
| **SPI** | Sensitive Personal Information — CCPA/CPRA's analog of GDPR Article 9. Includes health information, biometrics, precise geolocation. |
| **PII** | Personally Identifiable Information — data that identifies a specific individual. |
| **DPA** | Data Processing Agreement — a contract between us and a subprocessor governing how the subprocessor handles user data. |
| **DPO** | Data Protection Officer — a role GDPR requires under certain conditions (large-scale processing of special-category data). We monitor for the threshold; not required at MVP scale. |
| **DSAR** | Data Subject Access Request — a user request to access, export, or delete their data. Volume >1% MAU is a kill-criteria signal. |
| **SCC** | Standard Contractual Clauses — EU-approved contractual mechanism for transferring personal data outside the EU. |
| **BAA / HIPAA** | Business Associate Agreement / Health Insurance Portability and Accountability Act — US healthcare compliance. We are **not** a HIPAA covered entity; we do not need a BAA. |
| **PG / RLS** | PostgreSQL / Row-Level Security — the database isolation mechanism we use to prevent cross-athlete data leakage at the database layer (not the application layer alone). |
| **KMS** | Key Management Service — managed key custody for envelope encryption of sensitive fields. |
| **AES-256** | Advanced Encryption Standard, 256-bit key — symmetric encryption used for at-rest data. |
| **TLS** | Transport Layer Security — encryption for data in transit (HTTPS). Minimum TLS 1.2. |
| **SSE** | Server-Side Encryption — provider-managed at-rest encryption (e.g., AWS S3 SSE). |
| **SSO** | Single Sign-On — centralized authentication (we plan Google Workspace SSO for internal access). |
| **2FA** | Two-Factor Authentication — required on all infrastructure tooling. |
| **IR / SLA** | Incident Response / Service Level Agreement — our disclosure SLA is 72 hours to authorities under GDPR. |
| **P0 / P1** | Priority 0 / Priority 1 — severity classifications. P0 = "stop everything, fix now" (e.g., active data leak). |

## Infrastructure

| Term | Expansion / meaning |
|---|---|
| **AWS** | Amazon Web Services — our likely hosting provider. |
| **RDS / S3** | AWS managed database service / AWS object storage. |
| **SQS** | AWS Simple Queue Service — managed message queue. |
| **FCM / APNS** | Firebase Cloud Messaging / Apple Push Notification Service — push delivery providers. (Web-first MVP; push is a later feature.) |
| **ORM** | Object-Relational Mapping — the abstraction layer between application code and the database; reviewed for RLS-bypass risk. |
| **UX** | User Experience — the design and interaction surface. |

## Validation framework conventions

| Term | Expansion / meaning |
|---|---|
| **W1 … W7** | Workshop 1 through Workshop 7 — the seven validation cycles. |
| **exp-1** | Landing-page demand test (Stage 1 gate). |
| **exp-2** | Convert-candidate interview round (Stage 2 gate). |
| **exp-3** | Pricing / WTP probe (folded into exp-1/exp-2 in our adaptation). |
| **exp-4** | The 13-week concierge pilot (Stage 3 gate). |
| **A-W{n}-{seq}** | Assumption ID convention — e.g., A-W4-3 is the third assumption added in Workshop 4. Tracked in `_assumption_register.md`. |
| **K-W{n}-{seq}** | Kill-criteria ID convention — e.g., K-W6-1 is the first kill criterion from Workshop 6. Tracked in `_kill_criteria.md`. **Hard rules** are flagged explicitly (K-W5-3, K-W6-1, K-W6-4) and must never trigger in production. |
| **Rev-2 / Rev-2.1** | Revision banners on workshop documents when a later workshop reopens an earlier lock. Convention borrowed from the sibling `myownemail` project. |

## Brand / positioning shorthand

| Term | Expansion / meaning |
|---|---|
| **"Train slow, race fast"** | Our locked brand line (W1). |
| **"Easy days, done right"** | Our locked MVP headline (W1). |
| **"Convert mode"** | Hypothetical incumbent feature in W7 Scenario 3 — what TrainingPeaks or Intervals.icu might ship to compete. |
| **The Five MVP metrics** | Adherence % (Real Easy Minutes), Drift minutes, Aerobic Trend, Recovery delta (descriptive only), Weekly intensity distribution. Explicitly **not** TSS, CTL/ATL/TSB, FTP estimates, VO2max estimates, training-load colors, or readiness scores. |
| **The behavior loop** | Sync → ≤2h deviation digest → Sunday Weekly Review → Aerobic Trend reward (W2). The Sunday Weekly Review is the activating moment. |
