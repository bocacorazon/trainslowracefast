# Live training device integration

Status: **Research - not scheduled**

Research captured: 2026-10-24

## Opportunity

Extend TrainSlowRaceFast from post-workout interpretation into live aerobic training
guidance: help athletes stay within their intended intensity while cycling or rowing,
then preserve the session for review.

The motivating devices are an original CycleOps Hammer trainer and a Concept2 rower.
They are discovery targets, not a commitment to support every trainer or rowing machine.
The desired experience is a web-based interface rather than an installed desktop UI,
with Kotlin retained where it is useful for Android and shared device logic.

This could complement the within-session adherence idea in
`02_within_session_adherence_score.md`. It is not yet evidence that device control
belongs in the SaaS MVP.

## Evidence and confidence

### CycleOps Hammer

- The supplied product-label photo identifies model **9810F**, UPC **012527014514**,
  with a printed date of **01/25/17**. The user confirmed it is the original Hammer.
  The label's date should not be treated as a verified manufacturing date.
- The label explicitly marks **ANT+ FE-C**, **ANT+ Power**, and **Bluetooth Smart**.
  FE-C is the evidence-backed candidate for standardized trainer control over ANT+.
- Bluetooth Smart branding alone does **not** establish FTMS support or identify the
  BLE resistance-control protocol.
- The installed firmware and actual GATT inventory have not been captured. FTMS versus
  an older proprietary control path remains unresolved.
- GoldenCheetah and QDomyos-Zwift contain potentially useful protocol references, but
  source-code support is not a successful compatibility test on this particular unit.
  QDomyos-Zwift includes Hammer-specific routing to different implementations; that
  does not prove which one this trainer needs.

FTMS terminology to preserve correctly: service `0x1826`, Control Point `0x2AD9`,
Indoor Bike Data `0x2AD2`. Set Target Power is a command sent to the Control Point,
not a separate target-power characteristic.

### Concept2 rower

- The user owns a Concept2 rower; **monitor model and firmware are not yet known**.
- Concept2 explicitly encourages third-party PM5 applications and publishes its PM
  communication specification, including the protocol needed to investigate telemetry
  and workout programming.
- Current ErgData supports **PM5 only** and connects over Bluetooth. PM5 monitors can
  be retrofitted to older Concept2 rowers.
- Concept2 documents USB connectivity for PM3, PM4, and PM5 through a USB host. That
  would be a separate transport investigation, not the same BLE integration.
- ErgData demonstrates live displays, workout setup, detailed results, and Online
  Logbook synchronization. Reproducing selected functions is plausible; full ErgData
  parity has not been investigated or promised.
- A RowErg is not an electronically controlled cycling brake: the app cannot enforce
  a 150 W ERG target by adjusting resistance. Guidance and supported workout programming
  must be distinguished from resistance control.

Candidate PM5 data includes power, pace, stroke rate, distance, elapsed time, and heart
rate when a suitable source is connected. Exact fields, rates, and command behavior must
be checked against the specification and physical monitor.

Concept2 Online Logbook upload is a separate cloud integration. A PM5 BLE connection
does not automatically provide logbook synchronization, verified/ranked results, or
ErgData's community features.

## Architecture options

| Option | Deployment | Main constraint |
|---|---|---|
| Hosted web UI with direct Web Bluetooth | No local app installation on supported browsers | Browser/OS compatibility and background execution |
| Web UI with local Kotlin BLE engine | Small local service; no native desktop UI | Installation remains, plus browser-to-local-service integration |
| Web UI with Kotlin Android companion | Android app owns BLE and session execution | Phone required; pairing and remote communication need design |

### Browser-owned BLE

Kotlin/JS and JavaScript use the same browser capabilities. Compiling Kotlin to JS or
Wasm does not bypass Web Bluetooth permissions or platform limitations.

Chrome's documentation requires a secure context and a user gesture for initial device
selection. It documents experimental enablement on Linux. Do not assume support in
Safari or Firefox, or that an installed PWA can run a dependable screen-off workout.
Recheck the actual browser/OS matrix when this work is scheduled.

Custom GATT services can be accessed when allowed and explicitly requested. With
name-based selection, required service UUIDs must be included in `optionalServices`.
Browser access is therefore not a substitute for an unrestricted initial GATT inventory
when the proprietary service UUIDs are unknown.

### Native BLE engine with web presentation

The engine, rather than the browser, would own device connections, command sequencing,
workout timing, reconnect policy, and durable recording. The web UI would send intentions
and render acknowledged state.

Closing a tab or losing internet must have explicit session semantics. Avoid making
cloud availability necessary for an ongoing local workout.

A local service is not a zero-cost browser bridge. A hosted HTTPS page accessing it
requires investigation of origin restrictions, local-network permissions, authentication,
and supported transport. A remote Android companion likewise needs authenticated pairing,
authorization, and potentially a relay. Do not expose unauthenticated trainer commands.

## Provisional stack recommendation

- **Web presentation:** TypeScript/React with standard HTML/DOM is the default
  recommendation for SaaS integration, accessibility, browser tooling, and charting.
- **Shared device/session logic:** Kotlin Multiplatform is a candidate for packet
  codecs, capabilities, command sequencing, telemetry, and workout state.
- **BLE execution:** Kotlin Android companion for reliable mobile execution; a
  Kotlin/JVM BlueZ-backed Linux service only if the Linux use case justifies it.
- **Backend:** choose independently. Kotlin/Ktor is an option, not a decision implied
  by the client language.

The user previously preferred an all-Kotlin implementation. Whether that is still a
firm constraint after the web-first shift is open. If it is, Kotlin/JS with an
HTML-based UI is a viable alternative. Kotlin/Wasm with Compose is another option when
sharing UI is more valuable than web-native integration. Neither choice solves BLE
availability or background lifecycle by itself.

Prefer a device-capability model over a universal cycling-trainer model:

- telemetry: available metrics, units, timestamps, and sources
- workout programming: supported workout structures and commands
- target-power control: supported only by devices that actually implement it
- lifecycle: device-specific start, stop, pause, and disconnect behavior

For example, a Hammer adapter might expose target-power control; a PM5 rower adapter
could expose workout programming without electronic resistance control. Missing sensor
metrics must remain unavailable rather than be fabricated.

## Unverified assumptions and cautions

- Hammer firmware, BLE services, control protocol, and response semantics
- Concept2 monitor model, firmware, and available BLE features
- Browser-only BLE reliability on the intended Linux machine and Android phone
- Whether users want live guidance enough to justify companion installation
- Whether a common Kotlin core is worthwhile once actual protocol needs are known
- Whether a session continues, pauses, or ends after UI loss, engine restart, or
  sensor failure; do not assume a disconnected device released resistance
- HR sensor connectivity, stale-data handling, and intensity-guidance requirements
- Recording durability, consent, retention, and the existing product trust policy
- License obligations if GPL code is copied or translated; using a protocol reference
  does not automatically settle the licensing question

No new schedule estimate is committed. Previous conversational estimates were not
validated against either physical device and should not drive delivery commitments.

## Revisit trigger and discovery gate

Revisit when live training guidance becomes a deliberate product priority, rather than
silently expanding the current post-workout SaaS scope.

Before implementation:

1. Confirm the Concept2 monitor and both devices' firmware versions.
2. Capture sanitized GATT inventories using a native diagnostic tool. Record UUIDs,
   characteristic properties, notification sources, and control responses.
3. Prove Hammer power telemetry and conservative, explicitly requested target changes.
   Do not equate a successful BLE write with a successful control command.
4. If PM5 is present, prove live rowing telemetry, then separately test one supported
   simple workout-programming command.
5. Compare direct Web Bluetooth against a native transport on the intended hardware:
   permission flow, reconnects, tab/background behavior, internet loss, and recording
   recovery. Define the supported browser matrix from observed results.
6. Decide UI language, execution ownership, licenses, and the minimum supported platform
   set from that evidence.

Create bounded implementation issues only after this direction is scheduled. Link them
here; do not interpret this note as replacing or approving existing increments.

## Non-goals

- Replacing all ErgData features or reproducing Concept2's community services
- Supporting every exercise machine, monitor generation, or browser
- Automatic HR-driven resistance adjustment in the first release
- Committing to Android, browser-only, or a local Linux service before discovery
- Expanding the current SaaS MVP or its integration promises without a product decision

## Sources and existing work

Official sources reviewed on 2026-10-24; compatibility and API details can change:

- [Concept2 software development resources and PM communication specification](https://www.concept2.com/support/software-development)
- [ErgData features and technical requirements](https://www.concept2.com/ergdata)
- [PM5 support and retrofit availability](https://www.concept2.com/support/monitors/pm5)
- [Chrome Web Bluetooth: platforms, permissions, services, and GATT operations](https://developer.chrome.com/docs/capabilities/bluetooth)
- [Kotlin web targets: Kotlin/JS versus Kotlin/Wasm](https://kotlinlang.org/docs/web-overview.html)
- [Bluetooth SIG Fitness Machine Service](https://www.bluetooth.com/specifications/specs/fitness-machine-service-1-0/)

Implementation references, not compatibility certification:

- [QDomyos-Zwift](https://github.com/cagnulein/qdomyos-zwift)
- [GoldenCheetah](https://github.com/GoldenCheetah/GoldenCheetah)

Repository context:

- `../06_integrations_workshop.md`: existing web-first, post-workout MVP scope
- `../increments/001-hammer-connection-and-erg-control.md`: original discovery plan
- `../increments/002-desktop-ble-erg-mvp.md`: desktop-oriented requirements that would
  need explicit revision if this direction is adopted

The existing `core` module has Android/JVM targets and a trainer contract; the Linux
launcher inspected during this research uses `FakeTrainerClient`. That skeleton is
useful starting work, not evidence of a completed physical BLE integration.
