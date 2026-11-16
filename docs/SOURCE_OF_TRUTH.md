# Portfolio Source Of Truth

This is the only documentation entry point for this repository.

If any document conflicts with executable files, the executable files win.

For the project overview, implementation status, and roadmap, see the
[Portfolio project summary](ai-context/01_CURRENT_STATUS.md).

## Active Authorities

### Execution truth

- `docs/PORTFOLIO_SESSION_SOURCE_OF_TRUTH.md`
- `.githooks/pre-commit`
- `scripts/healthcheck.sh`
- `scripts/healthcheck_l1.sh`
- `scripts/healthcheck_l2_fast.sh`
- `scripts/healthcheck_l2_full.sh`
- `../.orchestrator/session_guard.sh`
- `../../standards/COMMIT_MESSAGES.md`

### Deployment truth

- `.github/workflows/deploy-portfolio.yml`
- `deploy/portfolio.service`
- `deploy/nginx-portfolio.conf`
- `deploy/setup-lightsail.sh`

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
- The legacy deployment workflow targets AWS Lightsail. The former instance
  was retired in August 2026, and the replacement host is pending.
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
records the retirement of the former Lightsail instance. The checked-in
Lightsail workflow still triggers on pushes to `main` or `master`.

For a replacement VPS, build the frontend and boot JAR in CI and deploy those
artifacts to the host. The existing Nginx and systemd files under `deploy/`
document the frontend root, private backend port, and same-origin API proxy.
Supply the production profile and database credentials through the service
environment. Keep PostgreSQL and the Spring port private, and retain a database
backup and the preceding deploy artifacts before changing the live release.
Verify public HTTPS, API health, the project list, and a direct project link
before treating the cutover as complete.

## Supporting Reference Only

- `README.md`
- `docs/WINDOWS_SETUP_GUIDE.md`
- `docs/OPTIONAL_IMPROVEMENTS_Portfolio.md`

## Historical / Archived

- `docs/archive/`
- Anything in `docs/archive/` is historical only and must not guide current implementation decisions.

## Practical Rule

Start here, then follow the specific active authority for the question you are answering.
