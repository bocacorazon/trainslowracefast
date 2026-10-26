# UX Design Guidance — Active Workout Dashboard ("Cockpit")

Saved 2026-10-26 from external design guidance (AI-generated, with source links).
Reference for the active-workout screen design when development of the telemetry/dashboard UI begins.

---

A power (watts) and heart rate (BPM) training app requires an interface that balances high-density data with split-second legibility. When athletes train (cycling, rowing, or running), they need to process metrics without breaking focus.

## Core Active Workout Screen (The "Cockpit")

- **Focal Number Hierarchy:** Place current Power (in bold, massive type like 48–64pt) and current Heart Rate side-by-side or stacked cleanly in the upper half of the screen. (https://www.designrush.com/best-designs/apps/trends/fitness-app-design-examples)
- **Zone Color-Coding:** Use a consistent color spectrum across both metrics to indicate physiological strain (e.g., Zone 1/2 in calm blue/green, Zone 4 in vibrant orange, Zone 5/Anaerobic in explosive red/magenta).
- **Target vs. Actual Bars:** Display a slim horizontal or radial target band right behind the numbers so athletes can instantly see if they are drifting above or below their interval goal.
- **Frictionless Controls:** Keep play, pause, lap, and end-workout buttons oversized and thumb-accessible at the bottom, or support full-screen tap gestures. (https://excited.agency/services/fitness-management-app-design, https://mobbin.com/explore/mobile/app-categories/health-fitness)

## Data Visualization & Live Charts

- **Rolling Time-Series Graph:** A live, single-line or dual-line scrolling chart across the middle of the screen showing Watts (solid bright line) and Heart Rate (dashed or softer-toned line) over the last 5 to 10 minutes.
- **Power-to-HR Coupling (Efficiency Index):** A subtle indicator showing the ratio of your power output to your heart rate. If power stays high while heart rate drifts up slowly, your aerobic coupling is good; a sharp mismatch flags fatigue.
- **Interval Countdown Ring:** A clean circular SVG progress timer wrapping around the main metric or floating cleanly to show time remaining in the current work or rest interval.

## Post-Workout Summary & Analytics

- **Time-in-Zones Breakdown:** Stacked horizontal bar charts or side-by-side distribution rings showing total seconds/minutes spent in Heart Rate Zones (1–5) and Power/Coggan Zones (Z1–Z7).
- **Peak and Average Cards:** Clean grid cards displaying Normalized Power (NP), Maximum Heart Rate, Average Watts, and Estimated TSS (Training Stress Score).
- **Interactive Timeline Scrubbing:** Allow users to drag a finger across the completed workout graph to inspect exact Watts, BPM, and cadence at any specific second of the session.

## Design System & Ergonomics

- **High-Contrast Dark Mode:** Default to deep charcoal or pure black backgrounds to save OLED battery life and cut down glare in bright sunlight or dimly lit pain caves.
- **Haptic and Audio Cues:** Design visual micro-animations (like a soft screen pulse or border flash) paired with optional beep/voice prompts when crossing into a new interval or drifting out of a power zone.

---

## Project context (added 2026-10-26, ours — not from the source)

- **Cycling only** (indoor trainer); the source's sport/mount questions are answered by the project: Linux desktop is the primary target (trainer desk setup), Android is secondary (phone on a mount).
- **Cadence is unavailable** on our hardware (CycleOps Hammer H1 measures power + speed only; COOSPO strap gives HR) — treat cadence items (timeline scrubbing cadence column, etc.) as optional/gracefully-absent.
- Desktop-first ergonomics: the "thumb-accessible / full-screen tap gestures" guidance maps to large mouse/touch targets and keyboard shortcuts on desktop, thumb targets on Android.
- Dark mode is a hard default: pain-cave / dim-room use is the primary scenario (see also the "pain cave" note in the source).
