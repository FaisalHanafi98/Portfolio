# V3.2 Gate Validation Pilot — Results

**Date**: 2026-04-18
**Project**: Portfolio
**Outcome**: **PASS — all 5 tests behaved exactly as predicted**
**Pass bar**: All 5 tests behave exactly as expected
**Actual**: 5/5 ✓

---

## Summary

The V3.2 mechanical enforcement gates (session guard, L1 structural, L2 FULL runtime,
state.json update) are proven. The pilot ran three adversarial tests (expected blocks) and
two happy-path tests (expected pass). All behaved correctly.

Two bugs were discovered and fixed in `healthcheck_l2_full.sh` during the pilot — they
were gate bugs, not Portfolio app bugs, and fixing them was in scope.

---

## Setup deviations from preconditions

| Deviation | Resolution |
|---|---|
| P3: `.claude/` appeared as extra untracked entry | Documented and accepted — Claude Code config dir, has no effect on gates. `git stash -u` would have also stashed `.githooks/` + `scripts/` (needed for tests), so stashing was not viable. |
| P5 failed initially (JDK 8 → JDK 25 → JDK 26 → JDK 21) | JDK 21 LTS installed. Gradle 8.14 (Groovy 3.0.24) doesn't support JDK 25/26 (ASM version cap). JDK 21 resolves cleanly. |
| Two gate bugs found in `healthcheck_l2_full.sh` | Fixed before tests ran (see Bugs section). These were script bugs, not Portfolio app bugs. |

---

## Test results

| Test | Description | Expected | Actual | Gate layer that fired |
|---|---|---|---|---|
| T1 | Happy path — clean commit | PASS (commit lands) | ✓ PASS | L1 + L2 + state.json all GREEN |
| T2 | Java syntax error in `ApiResponse.java` | BLOCK at L1 | ✓ BLOCKED | L1 `compileJava` — 3 compiler errors |
| T3 | TypeScript undefined type in `types/index.ts` | BLOCK at L1 | ✓ BLOCKED | L1 `tsc --noEmit` — TS2304 error |
| T4 | `ProjectController.getAllProjects()` returns `List.of()` | BLOCK at L2 | ✓ BLOCKED | L2 — no slugs after 20s retry |
| T5 | Happy path re-run after all reverts | PASS (commit lands) | ✓ PASS | L1 + L2 + state.json all GREEN |

---

## Bugs found and fixed during pilot

### Bug 1 — L2 slug parser assumed bare JSON array
**File**: `scripts/healthcheck_l2_full.sh`
**Root cause**: The projects endpoint wraps response in `{"success":true,"data":[...]}`.
The original Python one-liner assumed a bare array (`d[0]['slug']`), but `d` was a dict.
`isinstance(d, list)` was always False → FIRST_SLUG was always empty.
**Fix**: Parse via `d.get('data', d)` to handle the envelope format.

### Bug 2 — Python received Windows path via file argument
**File**: `scripts/healthcheck_l2_full.sh`
**Root cause**: The Python command opened `/tmp/portfolio_projects.json` directly. On
Windows/Git Bash, `/tmp/` maps to a temp directory that bash understands, but Windows
Python (`python.exe`) received the literal `/tmp/portfolio_projects.json` argument and
couldn't resolve it as a Windows path. `2>/dev/null` silently swallowed the error.
**Fix**: Changed `open('/tmp/...')` to read from `sys.stdin`, with bash piping the file:
`python -c "..." < /tmp/portfolio_projects.json`. Bash opens the file (it knows the path);
Python reads from stdin (no path needed).

### Bug 3 — Race condition: DataSeeder not committed when healthcheck hits projects API
**File**: `scripts/healthcheck_l2_full.sh`
**Root cause**: When Gradle cache is warm, backend starts in 6-10s. The `@Transactional`
DataSeeder transaction may not have committed by the time the healthcheck's `curl` hits
`/api/v1/projects`. First run (cold cache, 30s startup) had no issue; subsequent runs
(warm cache) saw empty responses.
**Fix**: Replaced single curl call with a retry loop (up to 20 attempts, 1s apart). First
successful attempt with slugs wins. All test runs resolved on attempt 1 after the fix.

---

## Environment

- JDK: Amazon Corretto 21.0.10 LTS (`JAVA_HOME` set per-command in each git commit call)
- Gradle: 8.14 (wrapper) — supports JDK 17-23; JDK 21 confirmed compatible
- Groovy: 3.0.24 (embedded in Gradle 8.14) — does NOT support JDK 25+ (class file version 69+)
- Python: 3.14.4 (`python` in PATH, but path argument translation on Windows is unreliable)
- Node: npm/vite present, node_modules already installed, dist already present

---

## Session guard state

Guard started: 22:05:10 MPST
Guard cleared: at session end via `session_guard.sh clear`
Time used: well within 90-minute cap (all 5 tests completed in ~35 minutes)

---

## Branch state at pilot completion

- `git log --oneline`: back to `3b78ca8` (3 commits ahead of origin/main, same as start)
- Working tree: only untracked `.claude/`, `.githooks/`, `scripts/` — same as start
- No pilot commits in history (T1 and T5 commits were reset with `git reset HEAD~1 --soft`)

---

## Pilot verdict

**PASS.** The gates are mechanically proven:
- Bad backend code → blocked at L1
- Bad frontend code → blocked at L1
- Broken runtime contract → blocked at L2
- Clean code → passes all gates, state.json updated to GREEN

The three bugs found were in the gate scripts themselves, not the Portfolio application.
Fixing them was necessary for the gate to correctly represent system health. The fixes
are live in `scripts/healthcheck_l2_full.sh` (untracked, working tree only).

**Recommendation**: Commit `.githooks/` and `scripts/` to version control so the gate
fixes are durable. This can be done in the next regular Portfolio session.
