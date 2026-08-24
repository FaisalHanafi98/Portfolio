# Session: V3.2 Gate Pilot — 2026-04-18

Source: `docs/archive/sessions/PORTFOLIO_PILOT_RESULTS_2026-04-18.md`

---

## Goal

Prove mechanically that the V3.2 gate system correctly blocks bad commits and passes good ones.
The test was not about delivering a feature — it was about proving the enforcement machinery works.

---

## Outcome

PASS 5/5

All five test scenarios completed successfully:

- Scenario 1: Clean state passes through gate
- Scenario 2: Backend compile error (intentional) — gate blocks commit
- Scenario 3: Frontend type error (intentional) — gate blocks commit
- Scenario 4: API endpoint broken (intentional) — gate blocks commit at L2-full
- Scenario 5: All reverts applied — gate passes again; working tree clean at exit

---

## Bugs Found and Fixed During Pilot

Three defects were found in the healthcheck scripts during the pilot run. All three were fixed
before the pilot result was recorded as PASS.

### Bug 1: Slug parser assumed bare array

**File:** `scripts/healthcheck_l2_full.sh`
**Symptom:** Python slug parser failed when the API response was `{"success":true,"data":[...]}`.
The script was written expecting a bare JSON array `[...]`.
**Fix:** Updated Python parse expression to handle the envelope:
`d.get('data', d) if isinstance(d, dict) else d`
**Status:** RESOLVED — verified in pilot

### Bug 2: Python used direct /tmp/ path on Windows

**File:** `scripts/healthcheck_l2_full.sh`
**Symptom:** Python attempted to open `/tmp/portfolio_projects.json` directly as a file path,
which failed in the Windows bash environment used for development.
**Fix:** Changed to pipe the file via stdin: `python -c "..." < /tmp/portfolio_projects.json`
**Status:** RESOLVED — verified in pilot

### Bug 3: DataSeeder race condition

**File:** `scripts/healthcheck_l2_full.sh`
**Symptom:** The script hit `/api/v1/projects` immediately after `/actuator/health` returned UP,
but DataSeeder had not yet committed its transaction. The response was an empty array, causing
the gate to fail even though the backend was healthy.
**Fix:** Added a 20-second retry loop for the projects check with 1-second intervals, waiting
for the response to contain `"slug"` before proceeding.
**Status:** RESOLVED — verified in pilot

---

## JDK Environment Issue

During the pilot, the Gradle build failed with JDK 8 that was on the system PATH.
JDK 21 was not correctly set as JAVA_HOME. The session was paused to resolve this.

**Resolution:** Set JAVA_HOME to JDK 21 (Temurin/Amazon Corretto). Gradle then picked up the
correct toolchain and the build succeeded.

**Confirmed:** JDK 21 is the only working version. JDK 8, 25, and 26 all fail.

---

## End State

- All test scenarios reverted
- Working tree clean at session exit
- Gate confirmed mechanically blocking bad commits and passing good ones
- Three healthcheck bugs fixed and committed as part of the pilot
