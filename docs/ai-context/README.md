# AI Context Archive — Portfolio Project

This directory is the AI-readable project memory and session-resume system for the Portfolio project.

It is not a source of runtime truth. Runtime truth lives in `docs/SOURCE_OF_TRUTH.md` and the
executable files it references. This archive gives an AI agent enough orientation to resume work
safely without having to reconstruct history from scratch.

---

## Purpose

- Preserve verified facts across sessions so AI agents do not repeat already-answered questions
- Record what was done, what failed, and what remains unresolved
- Track alignment between documentation and actual code
- Provide a self-contained transfer package for any AI agent starting fresh

---

## How to Use This Archive

Resume order when starting a new session:

1. `00_PROJECT_SOURCE_OF_TRUTH.md` — stable facts about the project (stack, architecture, rules)
2. `01_CURRENT_STATUS.md` — latest verified state, current goal, next actions
3. `08_DOC_ALIGNMENT/deviation_register.md` — known deviations from expected behavior
4. `03_BUG_HISTORY/unresolved_bugs.md` — open issues and risks
5. Latest session file in `02_SESSION_HISTORY/` — most recent session outcome

Then follow the actual authority chain from `docs/SOURCE_OF_TRUTH.md` before doing any work.

---

## Status Labels

| Label | Meaning |
|---|---|
| ALIGNED | Doc and code agree |
| DOC AHEAD OF CODE | Doc describes something not yet implemented |
| CODE AHEAD OF DOC | Code does something the docs do not mention |
| STALE DOC | Doc predates a significant change and has not been updated |
| DEVIATION | Something differs from what is expected or documented |
| ACCEPTED DEVIATION | Deviation is known, reviewed, and intentionally kept |
| POSSIBLE DEFECT | Something that may be a problem but is not confirmed |
| UNKNOWN | Not enough information to determine status |

---

## Confidence Values

| Value | Meaning |
|---|---|
| HIGH | Verified directly in code or executable output |
| MEDIUM | Strongly inferred from code patterns or consistent documentation |
| LOW | Incomplete evidence; partial or indirect |
| UNKNOWN | No usable evidence found |

---

## Update Rules

Update this archive at the end of every session:

- Update `01_CURRENT_STATUS.md` with the new state and next actions
- Add a new file in `02_SESSION_HISTORY/` named `YYYY-MM-DD_short_description.md`
- Update `03_BUG_HISTORY/resolved_bugs.md` or `unresolved_bugs.md` as appropriate
- Update `08_DOC_ALIGNMENT/deviation_register.md` if new deviations are found

Do not update files in `09_ARCHIVE/`.

---

## Critical Rule

Past summaries are context, not proof. Current code is reality, not always correctness.

Always verify claims in the code before acting on them. This archive reflects what was true at the
time of writing. Treat every entry as a starting hypothesis, not a guaranteed fact.
