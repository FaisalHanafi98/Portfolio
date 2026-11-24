# Portfolio Source Of Truth

This is the only documentation entry point for this repository.

If any document conflicts with executable files, the executable files win.

For the project overview, implementation status, and roadmap, see the
[Portfolio project summary](ai-context/01_CURRENT_STATUS.md).

## Active Authorities

### Execution truth

- `docs/PORTFOLIO_SESSION_SOURCE_OF_TRUTH.md`
- `.githooks/pre-commit` delegates secret scanning to `.hooks/pre-commit` and runs L1 and L2-full.
- `scripts/healthcheck.sh`
- `scripts/healthcheck_l1.sh`
- `scripts/healthcheck_l2_fast.sh`
- `scripts/healthcheck_l2_full.sh`
- `../../standards/COMMIT_MESSAGES.md`

### Deployment truth

- [GitHub CI](../.github/workflows/ci.yml) verifies the stack.
- The replacement production deployment is pending. The files under `deploy/`
  describe the former Lightsail setup.

### Backend profile and runtime truth

- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/application-dev.yml`
- `backend/src/main/resources/application-prod.yml`
- `docker-compose.yml` for the local PostgreSQL rehearsal

### Product truth

- `docs/PORTFOLIO_PRD.md`

### Assistant context only

- `AGENTS.md`
- `AGENTS.md` is not a source of runtime, deployment, or workflow truth.

## Current Reality Summary

- Local backend defaults to profile `dev`.
- Local backend runs on port `8081`.
- Local runtime and ops healthcheck is `http://localhost:8081/actuator/health`.
- Local user-path verification includes `http://localhost:8081/api/v1/projects`.
- The Lightsail instance retired in August 2026. The automatic Lightsail
  workflow was removed on 24 November 2026. The replacement host is pending.
- The deployment configuration uses Nginx for the frontend and proxies the
  API to Spring on port `8080`.
- Public deployment verification uses `/api/v1/health`.
- `/actuator/` is not a public production endpoint.

## Local PostgreSQL rehearsal

From the project root, run `docker compose up --build -d` on a machine with a
running Docker engine. Compose uses the PostgreSQL-compatible `prod` profile
on port 8080. The bundled frontend serves the app on port 3000 and proxies
`/api/` to that backend. Standalone development uses `dev` and port 8081.

Check `http://localhost:3000/api/v1/health`, then confirm
`http://localhost:3000/api/v1/projects` returns a populated list and
`http://localhost:3000/api/v1/projects/creams` returns the CREAMS project.
Open `http://localhost:3000/projects/creams` to exercise direct navigation.
Use `docker compose down` to stop the rehearsal and retain its database volume.
The [CI smoke test](../.github/workflows/ci.yml) runs those checks against an
isolated Compose stack on the runner.

## VPS migration preparation

The [August 2026 session record](ai-context/02_SESSION_HISTORY/2026-08-23_lightsail_decommission_and_streamlining.md)
records the retirement of the former Lightsail instance. The failed
[22 November release attempt](https://github.com/FaisalHanafi98/Portfolio/actions/runs/44970309509)
ran the [historical workflow at `63feb9e`](https://github.com/FaisalHanafi98/Portfolio/blob/63feb9e41da7e21473a499a6ee14a90ff779d237/.github/workflows/deploy-portfolio.yml).

The [proposed roadmap](PORTFOLIO_ROADMAP.md) covers a Singapore VPS, GHCR
image delivery, Caddy HTTPS, Docker Compose and encrypted PostgreSQL backups.
The Docker-based rollout is on hold (KIV) following a Docker Desktop startup
failure. Resume on a working Docker environment and choose a provider before
cutover. The next planned implementation is the status snapshot using the
local Spring Boot development profile.

## Browser regression checks

Use Java 21 to start the local backend with `./gradlew bootRun` from
`backend/` (`.\gradlew.bat bootRun` in PowerShell). Then, from `frontend/`,
run `npm ci`, `npx --no-install playwright install chromium`, and
`npm run test:e2e`. The tests start Vite on `127.0.0.1:5173` and wait for
the backend's seeded project list before exercising the content journeys.

To use an installed Chrome, set `PLAYWRIGHT_CHANNEL=chrome` and run the same
test command. In PowerShell, use `$env:PLAYWRIGHT_CHANNEL='chrome'`.
To test a running local Compose stack, set
`PLAYWRIGHT_BASE_URL=http://127.0.0.1:3000`. With an explicit base URL the
test runner uses that server directly. It creates no portfolio records.

The browser suite covers project navigation and section scrolling, engineering
details, a real API 404, recovery from a simulated HTTP 503, mobile menu
scrolling and focus, story tab keyboard navigation, and unknown URLs.
Failure traces and screenshots go to the ignored `frontend/test-results/`
folder. Run the faster rendering fixtures with `npm test` from `frontend/`.

## Supporting Reference Only

- `README.md`
- `docs/WINDOWS_SETUP_GUIDE.md`
- `docs/OPTIONAL_IMPROVEMENTS_Portfolio.md`

## Historical / Archived

- `docs/archive/`
- Anything in `docs/archive/` is historical only and must not guide current implementation decisions.

## Practical Rule

Start here, then follow the specific active authority for the question you are answering.
