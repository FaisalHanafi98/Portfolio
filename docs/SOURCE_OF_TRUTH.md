# Portfolio Source Of Truth

This is the only documentation entry point for this repository.

If any document conflicts with executable files, the executable files win.

## Active Authorities

### Execution truth

- `docs/PORTFOLIO_SESSION_SOURCE_OF_TRUTH.md`
- `.githooks/pre-commit`
- `scripts/healthcheck.sh`
- `scripts/healthcheck_l1.sh`
- `scripts/healthcheck_l2_fast.sh`
- `scripts/healthcheck_l2_full.sh`
- `../.orchestrator/session_guard.sh`
- `docs/COMMIT_MESSAGE_SOP.md`

### Deployment truth

- `.github/workflows/deploy-portfolio.yml`
- `deploy/portfolio.service`
- `deploy/nginx-portfolio.conf`
- `deploy/setup-lightsail.sh`

### Backend profile and runtime truth

- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/application-dev.yml`
- `backend/src/main/resources/application-prod.yml`

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
- Production deployment target is AWS Lightsail.
- Production frontend is served by Nginx.
- Production backend runs behind Nginx on port `8080`.
- Public deployment verification uses `/api/v1/health`.
- `/actuator/` is not a public production endpoint.

## Supporting Reference Only

- `README.md`
- `docs/WINDOWS_SETUP_GUIDE.md`
- `docs/OPTIONAL_IMPROVEMENTS_Portfolio.md`

## Historical / Archived

- `docs/archive/`
- Anything in `docs/archive/` is historical only and must not guide current implementation decisions.

## Practical Rule

Start here, then follow the specific active authority for the question you are answering.
