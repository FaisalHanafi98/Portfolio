# Portfolio Dev Session — V3.2 Gate Validation Pilot

> **Date**: 2026-04-18
> **Project Path**: `./Portfolio`
> **Session Type**: **V3.2 Gate Validation Pilot — NOT feature work**
> **Governance**: V3.2 mechanical enforcement (`.orchestrator/session_guard.sh` + `Portfolio/.githooks/pre-commit`)
> **Supersedes**: `PORTFOLIO_SESSION_2026-04-16.md` (deferred until pilot passes)
> **Hard cap**: 90 minutes. 75-minute halt rule enforced.

---

## Why this session is different

The V3.2 mechanical enforcement system (session guard, L1/L2 healthchecks, pre-commit hook, state.json) was installed on 2026-04-17. **It is not yet proven.** This pilot exists to prove — through adversarial testing — that the gates actually stop bad commits.

**Rule: no feature work in this session.** The only code edits are intentional breakages that will be reverted immediately. If the gates can't stop a deliberately broken commit, we don't trust them on real code. Real Portfolio work (cybersec audit, cost analysis, deployment) resumes after the pilot passes.

**Test failures are expected and desired for Tests 2, 3, and 4.** Those tests validate the gate BLOCKS bad code. "Pass" means the gate behaved exactly as predicted — blocked when it should, let through when it should.

Pass bar: **all 5 tests behave exactly as expected**. Partial pass is a fail.

---

## Time budget (enforced)

| Minutes | Phase |
|---|---|
| 0–10 | Entry preconditions + setup |
| 10–60 | Run Tests 1–5 sequentially |
| 60–75 | Fix any gate misbehaviour (NOT Portfolio app bugs) |
| 75–85 | Re-run full suite clean if fixes happened |
| 85–90 | Write results doc + exit |

**75-minute halt rule:** if at minute 75 the suite is not passing, STOP. Document current state. Do not extend. Fix Phase 1 in a separate session before re-running the pilot.

Track time with `../.orchestrator/session_guard.sh status` periodically.

---

## Entry preconditions

Run these in order from the `Portfolio/` directory. Every line must succeed before Test 1 starts. If any fails → halt, document why, exit.

```bash
# P1. Confirm you are in Portfolio
pwd
# Expected: .../Development/Portfolio

# P2. Confirm core.hooksPath is activated
git config --get core.hooksPath
# Expected: .githooks

# P3. Confirm working tree has only our Phase 1 infra as untracked
git status --short
# Expected: exactly these two lines:
#   ?? .githooks/
#   ?? scripts/
# If anything else appears, stash it first:  git stash -u

# P4. Confirm 3 commits ahead of origin/main
git log --oneline origin/main..HEAD
# Expected: 3 lines (3b78ca8, 7ac6171, 3b05b2e)

# P5. Confirm backend compiles
cd backend && ./gradlew compileJava --quiet && cd ..
# Expected: silent success (exit 0)

# P6. Start the session guard
../.orchestrator/session_guard.sh start
# Expected: "session_guard: started at ..."

# P7. Confirm state file exists and is well-formed
cat ../.orchestrator/state.json
# Expected: valid JSON with schema_version: 1 and a Portfolio entry

# P8. Confirm all 8 Phase 1 files are in place and executable
ls -la .githooks/pre-commit scripts/healthcheck*.sh ../.orchestrator/session_guard.sh ../.orchestrator/update_state.py
# Expected: all files exist, all marked -rwxr-xr-x
```

**If any precondition fails → do not run any test. Document in the results file why setup failed, clear the session guard, exit.**

---

## Test 1 — Happy path (commit should land)

**Purpose:** Prove the gate lets good commits through. Baseline sanity check.

### Pre-state
- Working tree clean except `.githooks/` + `scripts/` untracked.
- Session guard started (P6 above).

### Commands

```bash
# 1.1 Record start time
echo "Test 1 start: $(date)" >> /tmp/pilot_log.txt

# 1.2 Make a trivial, safe change
echo "" >> README.md

# 1.3 Stage and commit — this will trigger the full gate
git add README.md
git commit -m "chore(pilot): test 1 happy path"

# 1.4 Record end time
echo "Test 1 end: $(date)" >> /tmp/pilot_log.txt
```

### Expected outcome

- Pre-commit hook runs: session_guard check (OK), L1 compile (OK), L2 FULL (boot backend, GET /api/v1/projects returns slugs, GET by slug OK, npm build OK, dist/index.html present).
- Commit lands. `git log -1` shows the new commit.
- `cat ../.orchestrator/state.json` shows `last_healthcheck: "GREEN"` and the new commit SHA.

### If the test FAILS (commit blocked)

- Read the error — is it a real gate issue, or an environmental issue (e.g. backend port in use, node_modules missing)?
- Environmental issues → fix environment, re-run Test 1.
- Gate bug (hook rejects valid code) → STOP. Document. Do not proceed.

### Result to record

```
Test 1 — Happy path: ✓ | ✗
Evidence:
  - Commit SHA: <sha>
  - L1 wall clock: <s>
  - L2 FULL wall clock: <s>
  - Total commit time: <s>
  - state.json status after: <GREEN|other>
```

---

## Test 2 — L1 structural block (commit should be rejected at compile)

**Purpose:** Prove the gate catches compile errors before they land.

### Pre-state
- Test 1 passed, tree clean.

### Commands

```bash
# 2.1 Introduce a Java syntax error in a real controller
cp backend/src/main/java/com/faisal/portfolio/controller/ProjectController.java /tmp/ProjectController.java.bak
echo "THIS IS NOT VALID JAVA" >> backend/src/main/java/com/faisal/portfolio/controller/ProjectController.java

# 2.2 Attempt commit
git add backend/src/main/java/com/faisal/portfolio/controller/ProjectController.java
git commit -m "chore(pilot): test 2 should be blocked at L1"
```

### Expected outcome

- `./gradlew compileJava` fails with a Java compilation error.
- Commit is rejected. `git log -1` still shows the Test 1 commit (not Test 2).
- The hook never even reaches L2 FULL — that's the point of L1 running first.

### Revert (MANDATORY before Test 3)

```bash
# 2.3 Restore the original file
mv /tmp/ProjectController.java.bak backend/src/main/java/com/faisal/portfolio/controller/ProjectController.java

# 2.4 Unstage
git reset HEAD backend/src/main/java/com/faisal/portfolio/controller/ProjectController.java 2>/dev/null || true

# 2.5 Verify tree is clean again
git status --short
# Expected: only .githooks/ and scripts/ untracked (Test 1 committed README.md)
```

### Result to record

```
Test 2 — L1 structural block: ✓ | ✗
Evidence:
  - Hook rejected at stage: <L1|other>
  - Error message snippet: <first error line>
  - git log -1 after reject: <still Test 1 SHA>
```

---

## Test 3 — L2 FULL runtime block (THE TRUTH TEST)

**Purpose:** This is the single most important test. Prove the gate catches a commit that compiles cleanly but breaks at runtime. If this test passes, the "1 implementation = 1 regression" problem is solved at the mechanical level.

### Pre-state
- Test 2 reverted, tree clean.

### Step-by-step (precise — deviation invalidates the test)

```bash
# 3.1 Back up the controller
cp backend/src/main/java/com/faisal/portfolio/controller/ProjectController.java /tmp/ProjectController.java.bak
```

**3.2** Open `backend/src/main/java/com/faisal/portfolio/controller/ProjectController.java` in the editor. Find the method that handles `GET /api/v1/projects` (likely `getProjects()` or similar returning a list). Inside the method body, at the very top, insert this line:

```java
if (true) throw new RuntimeException("PILOT TEST 3 — intentional runtime break");
```

**Why this shape:** `if (true) throw` is valid Java (compiles) and bypasses any "unreachable code" check. The subsequent method body is preserved so the file is otherwise intact.

```bash
# 3.3 Confirm it still compiles (this is supposed to pass)
cd backend && ./gradlew compileJava --quiet && cd ..
# Expected: exit 0

# 3.4 Attempt commit
git add backend/src/main/java/com/faisal/portfolio/controller/ProjectController.java
git commit -m "chore(pilot): test 3 should be blocked at L2 FULL"
```

### Expected outcome

- L1 passes (code compiles).
- Pre-commit hook proceeds to L2 FULL.
- Backend boots successfully (method isn't called yet).
- Hook runs `curl GET /api/v1/projects`.
- Backend throws RuntimeException → Spring returns 500 → curl fails or returns non-200.
- `healthcheck_l2_full.sh` prints RED and exits 1.
- Commit is rejected.
- Backend is torn down by the hook's teardown trap.

### Revert (MANDATORY before Test 4)

```bash
# 3.5 Restore original file
mv /tmp/ProjectController.java.bak backend/src/main/java/com/faisal/portfolio/controller/ProjectController.java

# 3.6 Unstage
git reset HEAD backend/src/main/java/com/faisal/portfolio/controller/ProjectController.java 2>/dev/null || true

# 3.7 Verify
git status --short
git diff --stat
# Expected: no changes to ProjectController.java
```

### Result to record

```
Test 3 — L2 FULL runtime block: ✓ | ✗
Evidence:
  - Hook rejected at stage: <L2 FULL|other>
  - L1 passed? <yes|no>
  - Backend booted? <yes|no>
  - Error from curl /api/v1/projects: <snippet>
  - L2 FULL wall clock: <s>
  - Backend torn down cleanly? <yes|no>
```

**If this test fails — the gate lets a broken commit through — STOP the pilot. This is a Phase 1 bug.**

---

## Test 4 — Session cap block (90-min guard enforces)

**Purpose:** Prove the session guard blocks commits once the 90-minute cap elapses.

### Pre-state
- Test 3 reverted, tree clean. Current session still within 90 min.

### Commands

```bash
# 4.1 Fake the session start timestamp to ~100 minutes ago
echo "$(( $(date +%s) - 6000 ))" > ../.orchestrator/.session_start

# 4.2 Confirm guard status
../.orchestrator/session_guard.sh status
# Expected: elapsed > 5400, remaining negative

# 4.3 Attempt a trivial commit
echo "" >> README.md
git add README.md
git commit -m "chore(pilot): test 4 should be blocked by session guard"
```

### Expected outcome

- Pre-commit hook runs session_guard check first.
- Output includes: `SESSION CAP EXCEEDED (~6000s > 5400s)`.
- Commit is rejected.
- The hook never reaches L1 or L2 — guard blocks earliest.

### Revert

```bash
# 4.4 Clear guard and restart
../.orchestrator/session_guard.sh clear
../.orchestrator/session_guard.sh start

# 4.5 Undo the README change (it was staged but not committed)
git checkout -- README.md
git reset HEAD README.md 2>/dev/null || true
git status --short
```

### Result to record

```
Test 4 — Session cap block: ✓ | ✗
Evidence:
  - Guard message snippet: <first line of block>
  - Rejected before L1? <yes|no>
```

---

## Test 5 — Emergency bypass (commit lands, log records bypass)

**Purpose:** Prove the escape hatch works AND is accountable.

### Pre-state
- Test 4 reverted, session guard restarted, tree clean.

### Commands

```bash
# 5.1 Note current size of bypass log (if exists)
BYPASS_LOG=../.orchestrator/.commit_bypasses.log
wc -l "$BYPASS_LOG" 2>/dev/null || echo "log does not exist yet (that is fine)"

# 5.2 Attempt bypass commit
echo "" >> README.md
git add README.md
SOP_EMERGENCY_BYPASS=1 SOP_BYPASS_REASON='pilot test 5 — verify bypass path' git commit -m "chore(pilot): test 5 emergency bypass"

# 5.3 Verify commit landed
git log -1 --oneline
# Expected: the new Test 5 commit

# 5.4 Verify log was written
tail -1 "$BYPASS_LOG"
# Expected: a line containing today's date AND the reason string "pilot test 5"
```

### Expected outcome

- Hook prints: `pre-commit: EMERGENCY BYPASS (pilot test 5 — verify bypass path) — logged to ...`.
- Commit lands.
- `.orchestrator/.commit_bypasses.log` gains one new line with timestamp + reason.
- No L1 or L2 ran — bypass short-circuited everything.

### Result to record

```
Test 5 — Emergency bypass: ✓ | ✗
Evidence:
  - Commit SHA: <sha>
  - Bypass log tail: <last line>
  - Log contained reason string? <yes|no>
```

---

## Writing the results

After Test 5 (or after the 75-min halt), create the results file:

```bash
mkdir -p ../.orchestrator/pilot
```

Write `../.orchestrator/pilot/portfolio_gate_test_2026-04-18.md` using this template:

```markdown
# Portfolio V3.2 Pilot — Gate Validation Results

Date: 2026-04-18
Duration: <actual minutes from session_guard.sh status>
Overall: PASS | FAIL

## Summary table

| Test | Expected | Actual | ✓/✗ | Evidence snippet |
|---|---|---|---|---|
| 1 Happy path | commit lands, state GREEN | | | |
| 2 L1 block | rejected at compileJava | | | |
| 3 L2 FULL block | rejected at L2 runtime | | | |
| 4 Session cap | rejected at guard | | | |
| 5 Emergency bypass | lands + log entry | | | |

## Timing observations

- L1 wall clock: <s>
- L2 FULL wall clock (cold): <s>
- L2 FULL wall clock (warm): <s>
- First commit total: <s>
- Test 5 (bypass, no gates) total: <s>

## Surprises

<anything unexpected — unexpected failure, unexpected pass, weird timing, output format off>

## Verdict

<PASS — all 5 behaved as expected. Phase 1 is proven. Pilot runs for 5–7 days before expanding.>
<FAIL — these specific gaps must be fixed in Phase 1 before re-running:>
  - <gap 1>
  - <gap 2>
```

---

## Exit checklist

- [ ] All 5 test results recorded in `portfolio_gate_test_2026-04-18.md`
- [ ] Working tree shows: Test 1 and Test 5 commits on top of the 3 pre-existing commits (= 5 total ahead of origin/main); `.githooks/` + `scripts/` still untracked; no other uncommitted files
- [ ] Backend is not still running from Test 3 — check `curl -fsS http://localhost:8081/actuator/health` should fail (server down)
- [ ] Session guard cleared: `../.orchestrator/session_guard.sh clear`
- [ ] If any test failed: the results doc's Verdict section enumerates specific gaps

---

## DO NOT

- **Do NOT** edit any Portfolio source code except the intentional Test 2 / Test 3 breakages, and revert them immediately.
- **Do NOT** commit `.githooks/` or `scripts/` as tracked files during the pilot — they stay untracked until the pilot is green.
- **Do NOT** push any commits to origin/main during the pilot.
- **Do NOT** modify any file under `.orchestrator/` during the pilot (hook, guard, state, scripts).
- **Do NOT** run `npm install`, `npm ci`, or `./gradlew build` outside the healthcheck scripts. The healthchecks handle their own dependencies.
- **Do NOT** extend the session past 90 minutes, even to finish a "nearly working" test.
- **Do NOT** skip any of Tests 1–5, even if one "obviously works."
- **Do NOT** treat a gate failure as a reason to soften the gate. A gate that wrongly blocks is a gate bug, not a governance problem.
- **Do NOT** use `--no-verify` at any point. If a test requires bypassing, use `SOP_EMERGENCY_BYPASS=1` only (Test 5).
- **Do NOT** attempt any cybersec audit, cost analysis, DB config work, or deployment. Those belong to the post-pilot session.

---

## Successor session signal

- **Pilot PASSES (all 5 green):** `PORTFOLIO_SESSION_2026-04-16.md` (cybersec + Lightsail cost audit) un-blocks. Schedule it as the next Portfolio session. Real work now flows through the proven gates.
- **Pilot FAILS (any test unexpected):** Phase 1 is not done. List gaps in the results doc. The next session is a Phase 1 fix session, not feature work. Do NOT schedule any real work — including the 2026-04-16 cybersec session — until the pilot re-runs clean.
