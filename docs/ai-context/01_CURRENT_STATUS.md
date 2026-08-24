# Current Status — Portfolio

> **Last updated**: 2026-08-23 (Lightsail decommissioned; transitioning to local dev and new VPS planning)
> **Updated By**: Antigravity AI (Reviewed manually by Faisal)

---

## Latest Verified State

| Item | Status | Date verified | Evidence |
|---|---|---|---|
| V3.2 gate system | GREEN | 2026-04-18 | Pilot PASS 5/5 |
| origin/main | Up to date | 2026-05-01 | Last commit 7a47312 |
| Pre-commit hook | Active | 2026-04-18 | Enforced on all commits |
| Infrastructure | OFFLINE | 2026-08-23 | Lightsail decommissioned; awaiting new VPS |

---

## Current Goal

Focus on local full-stack development, automated end-to-end testing, and preparing containerized deployment playbooks for the upcoming VPS migration.

---

## Current Blockers

| Blocker | Severity | Notes |
|---|---|---|
| AWS Lightsail decommissioned | High (Infra) | Site currently offline; pending selection/provisioning of new VPS |
| Cybersec audit not done | Medium | Blocks `docs/PRE_DEPLOY_SECURITY_CHECKLIST.md` creation |
| Private key rotation status unknown | Low | `lightsail private key.txt` exists on disk; rotation not documented |
| DataEncodingTest.java never committed | Low | Untracked file; test passes gate (INFERRED) but is not in version control |

No blockers are preventing daily development work.

---

## Completed Verified Work

| Work item | Commit | Date | Verification |
|---|---|---|---|
| V3.2 gate system installation | ddedcb7 | 2026-04-18 | Pilot PASS 5/5 |
| Backend profile fix (dev/prod separation) | eaa3359 | 2026-04-20 | Gate passed |
| Commit message SOP introduced | bb2f819 | 2026-04-20 | Format observed in all subsequent commits |
| Frontend redesign (HeroSection, AboutSection rewrite) | 7a47312 | 2026-05-01 | Gate passed |
| DataSeeder expanded with narrative refresh | 7a47312 | 2026-05-01 | Gate passed |
| GitHub Actions deploy workflow operational | 7ac6171 | Earlier | Workflow file verified |

---

## Unverified Work

| Item | Status | Risk |
|---|---|---|
| DataEncodingTest.java | Untracked, never committed | Tests UTF-8 encoding of em-dash chars in API responses. Valid test, important for Windows dev. Currently invisible to CI/CD. |
| Production deployment of 7a47312 changes | Unknown | 2026-05-01 changes may or may not be live at faisalhanafi.com |
| /api/v1/about endpoint | POSSIBLE DEFECT | client.ts calls GET /about but no AboutController found in class list |

---

## Current Errors

None known.

---

## Next 5 Recommended Actions

1. Commit DataEncodingTest.java — run through gate first (`scripts/healthcheck.sh --l2-full`),
   then commit with the Project/Date/Task format. File path:
   `backend/src/test/java/com/faisal/portfolio/DataEncodingTest.java`

2. Run cybersec audit — review the planned tasks from
   `docs/archive/sessions/PORTFOLIO_SESSION_2026-04-16.md`. Create
   `docs/PRE_DEPLOY_SECURITY_CHECKLIST.md` as a session deliverable.

3. Verify production deployment — check whether faisalhanafi.com reflects the 2026-05-01
   changes from commit 7a47312 (DataSeeder expansion, HeroSection + AboutSection rewrites).
   The GitHub Actions deploy workflow runs on push to main; verify the last successful workflow run.

4. Investigate /api/v1/about — client.ts calls `GET /api/v1/about` but no AboutController
   was found in the backend class list. This may be a frontend call to a non-existent endpoint.
   Verify whether the endpoint exists or the frontend call fails silently.

5. Expand V3.2 gates to other projects — the session guard and healthcheck pattern from
   Portfolio could be applied to CREAMS, RinggitSense, and Sen2Nal.

---

## Do-Not-Repeat List

- Do not use `--no-verify` to bypass the pre-commit gate
- Do not run the `prod` Spring profile locally without a running PostgreSQL instance
- Do not commit `lightsail private key.txt` (it is gitignored but still present on disk)
- Do not use `git add -A` without reviewing `git status` first
- Do not claim work is complete without running verification commands and seeing the output
- Do not use JDK 8, 25, or 26 — only JDK 21 is confirmed working
