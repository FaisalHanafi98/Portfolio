# Resolved Bugs

All items here are confirmed fixed and verified.

---

## BUG-001: Slug Parser Envelope Mismatch

**Date resolved:** 2026-04-18 (during gate pilot)
**File affected:** `scripts/healthcheck_l2_full.sh`
**Severity:** HIGH — blocked the L2-full commit gate

**Root cause:** The Python slug parser in healthcheck_l2_full.sh was written expecting the
projects API to return a bare JSON array `[...]`. The actual response is an envelope:
`{"success": true, "data": [...], "timestamp": "..."}`. The parser returned an empty result
because `data` was nested inside the envelope, causing the gate to report no slugs found and
exit RED even when the backend was healthy and DataSeeder had seeded data.

**Fix applied:** Changed the Python expression to handle both bare array and envelope formats:
```
d.get('data', d) if isinstance(d, dict) else d
```
The expression checks whether the parsed result is a dict (envelope) and extracts `data` if so,
otherwise treats it as the array directly.

**Verification:** Gate PASS 5/5 in pilot run after fix applied.

---

## BUG-002: Python Stdin Path — Windows Compatibility

**Date resolved:** 2026-04-18 (during gate pilot)
**File affected:** `scripts/healthcheck_l2_full.sh`
**Severity:** HIGH — gate was non-functional on Windows dev environment

**Root cause:** The script passed the JSON file path `/tmp/portfolio_projects.json` directly
as a Python argument. In the Windows bash environment (Git Bash / WSL), opening a raw `/tmp/`
path as a file inside Python raised an error because the path resolution differed from the
shell's behavior.

**Fix applied:** Changed to pass the file via stdin instead of as a path argument:
```bash
python -c "import json,sys;..." < /tmp/portfolio_projects.json
```
stdin always works regardless of OS path semantics.

**Verification:** Gate PASS 5/5 in pilot run after fix applied.

---

## BUG-003: DataSeeder Race Condition

**Date resolved:** 2026-04-18 (during gate pilot)
**File affected:** `scripts/healthcheck_l2_full.sh`
**Severity:** HIGH — gate intermittently failed on fresh backend boot

**Root cause:** When the healthcheck script started the backend, it waited for
`/actuator/health` to return `"status":"UP"`. Spring Boot reports UP as soon as the
application context is loaded, but DataSeeder (a CommandLineRunner) commits its transaction
slightly after that point. If the gate hit `/api/v1/projects` in that window, it received an
empty array (no slugs), which caused the gate to exit RED incorrectly.

**Fix applied:** Added a 20-second retry loop with 1-second intervals after the initial
projects request. The gate waits for the response to contain `"slug"` before proceeding, giving
DataSeeder time to complete its transaction.

**Verification:** Gate PASS 5/5 in pilot run after fix applied; no further race condition
observed in subsequent sessions.

---

## BUG-004: Backend Profile Mismatch (Production Config)

**Date resolved:** 2026-04-20 (commit eaa3359)
**Files affected:** `backend/src/main/resources/application.yml`, `application-prod.yml`
**Severity:** HIGH — silently ran H2 instead of PostgreSQL in production

**Root cause:** The application.yml had `spring.profiles.active` hardcoded, which prevented
the prod profile from being activated correctly via environment variables. Additionally, the
production config file was named incorrectly and did not map to Spring's `prod` profile name.

**Fix applied:** Set `spring.profiles.default: dev` instead of hardcoding active profile.
Renamed the production config file to `application-prod.yml`.

**Verification:** Commit message for eaa3359 lists verified items including bootRun defaulting
to dev profile, H2 fallback confirmed, /actuator/health returning UP, L1 and L2 gate passing.

---

## BUG-005: JDK Version Incompatibility (Pilot Setup)

**Date resolved:** 2026-04-18 (during gate pilot)
**Severity:** MEDIUM — blocked initial pilot execution

**Root cause:** JAVA_HOME was pointing to JDK 8 on the development machine. The build.gradle
requires JDK 21 via the toolchain declaration. Gradle attempted to use the system JDK and
failed to compile.

**Fix applied:** Set JAVA_HOME to JDK 21 (Temurin/Amazon Corretto). Also documented in
WINDOWS_SETUP_GUIDE.md.

**Verification:** Build succeeded after JAVA_HOME correction. All subsequent commits passed gate.
