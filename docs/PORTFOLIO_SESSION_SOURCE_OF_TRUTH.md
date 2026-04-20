# Portfolio Session Source Of Truth

This is the only file that defines session execution behavior for this repository.

## Session Rules

- One deliverable only per session.
- Do not bypass `.githooks/pre-commit`.
- Do not treat `AGENTS.md` as workflow truth.
- Do not use archived documents to guide execution.

## Session Start

- Start the session guard:
  - `../.orchestrator/session_guard.sh start`
- Run the entry runtime check:
  - `scripts/healthcheck.sh --l2-fast`

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
  - session guard check
  - L1
  - L2-full

## Definition Of Done For A Session

A session is done only when:

- the single deliverable is complete
- the required checks for that deliverable have been run
- the pre-commit gate passes
- the commit message follows `docs/COMMIT_MESSAGE_SOP.md`

## Reality Boundaries

- Local runtime truth is defined by the healthcheck scripts.
- Deployment truth is defined by the Lightsail workflow and files under `deploy/`.
- Product intent is defined by `docs/PORTFOLIO_PRD.md`.
