# Failed Attempts

A record of approaches that were tried and failed. Prevents repeating the same mistakes.

---

## FAIL-001: JDK 8 — Gradle Build Failure

**Date:** 2026-04-18 (during gate pilot)
**Context:** Initial attempt to run the gate during the pilot session

**Attempt:** JAVA_HOME was pointing to JDK 8. Ran `./gradlew bootRun`.

**Failure:** Gradle attempted to use JDK 8 but the build.gradle declares a Java 21 toolchain:
```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```
Gradle could not satisfy the toolchain requirement with JDK 8 and the build failed immediately.

**Resolution:** Changed JAVA_HOME to JDK 21 (Temurin). Build succeeded.
**Do not retry:** Never attempt to run the portfolio backend with JDK 8.

---

## FAIL-002: JDK 25 — Gradle Build Failure

**Date:** 2026-04-18 (during gate pilot)
**Context:** Secondary attempt after initial JDK resolution

**Attempt:** A JDK 25 early-access build was available on the machine and was briefly tested.

**Failure:** Spring Boot 3.4.2 / Spring Data JPA had incompatibilities with JDK 25 (EA).
The build produced errors related to internal API access and class loading.

**Resolution:** Reverted to JDK 21.
**Do not retry:** JDK 25 is not supported. Use JDK 21 only.

---

## FAIL-003: JDK 26 — Gradle Build Failure

**Date:** 2026-04-18 (during gate pilot)
**Context:** Third JDK variant tested during environment troubleshooting

**Attempt:** JDK 26 (EA) was tested.

**Failure:** Same class of issues as JDK 25. Spring Boot 3.4.2 is not compatible with
pre-release JDK versions beyond 21.

**Resolution:** Confirmed JDK 21 as the only working version.
**Do not retry:** JDK 26 is not supported. Use JDK 21 only.

---

## FAIL-004: Original Slug Parser — Assumed Bare Array

**Date:** 2026-04-18 (during gate pilot)
**Context:** First run of healthcheck_l2_full.sh after backend was UP

**Attempt:** The original Python expression in healthcheck_l2_full.sh was:
```bash
python -c "import json,sys;d=json.loads(open('/tmp/portfolio_projects.json').read());print(d[0]['slug'] if d else '')"
```
This assumed the API response was a bare JSON array `[...]`.

**Failure:** The API response is an envelope `{"success":true,"data":[...],"timestamp":"..."}`.
The parser tried `d[0]` on a dict and got a KeyError. The gate returned an empty slug and exited
RED even though the backend was healthy and projects were seeded.

**Resolution:** Updated to handle the envelope format via `d.get('data', d)`.
**Do not retry:** Always account for the `{"success": true, "data": [...]}` envelope when
parsing API responses in shell scripts.

---

## FAIL-005: Python Direct File Path — Windows Incompatibility

**Date:** 2026-04-18 (during gate pilot)
**Context:** Initial version of the fixed slug parser

**Attempt:** Used `open('/tmp/portfolio_projects.json')` inside the Python one-liner to read
the saved response file.

**Failure:** In the Windows bash environment (Git Bash), Python's file path resolution for
`/tmp/portfolio_projects.json` differed from the shell's resolution. The file existed at the
path bash expected but Python could not open it, raising FileNotFoundError.

**Resolution:** Changed to pass the file via stdin: `python -c "..." < /tmp/portfolio_projects.json`
**Do not retry:** On Windows dev environments, never rely on Python resolving Unix-style /tmp/
paths as file arguments. Use stdin redirection instead.
