# Portfolio Session Source Of Truth

This is the only file that defines session execution behavior for this repository.

## Session Rules

- One deliverable only per session.
- Run the configured `.githooks/pre-commit` on each micro-commit.
- Do not treat `AGENTS.md` as workflow truth.
- Do not use archived documents to guide execution.

## Session Start

Set `git config --local core.hooksPath .githooks` once per clone and verify
with `git config --get core.hooksPath`. Git shares this setting across linked
worktrees. The project hook invokes the generated `.hooks/pre-commit`
scanner. The former shared session guard has been retired from this workflow.
Use the shared tooling's `--hooks-only` mode when refreshing hook sources so
the project hook path stays configured.

Run `bash scripts/healthcheck.sh --l2-fast` at session start.

## Gate Semantics

- `L1` = structural check
  - backend compile
  - frontend typecheck when dependencies are present

- `L2-fast` = entry runtime check
  - checks whether the backend is already responding on local dev runtime
  - uses `http://localhost:8081/actuator/health`
  - may return YELLOW if the backend is not yet running

- `L2-full` = commit and runtime truth gate
  - boots the backend in local dev profile if needed
  - waits for `http://localhost:8081/actuator/health`
  - verifies `GET /api/v1/projects`
  - verifies `GET /api/v1/projects/{slug}`
  - builds the frontend
  - tears down the backend if the script started it

- `pre-commit` = enforced commit gate
  - shared secret scan through `.hooks/pre-commit`
  - L1
  - L2-full

## Definition Of Done For A Session

A session is done only when:

- the single deliverable is complete
- the required checks for that deliverable have been run
- the pre-commit gate passes
- the commit message follows `../../standards/COMMIT_MESSAGES.md`

## Reality Boundaries

- Local runtime truth is defined by the healthcheck scripts.
- Deployment status and future work are tracked in [the runtime guide](SOURCE_OF_TRUTH.md).
- Product intent is defined by `docs/PORTFOLIO_PRD.md`.
