# Deployment and CI/CD — Module Status

Source: `.github/workflows/ci.yml`, `docker-compose.yml`, `docs/SOURCE_OF_TRUTH.md`
Last verified: 2026-11-28 (active CI pipeline; Lightsail retired at `31a63b0`, Docker VPS rollout KIV at `550caa5`)
Confidence: HIGH for CI/Compose definition; replacement VPS host PENDING

---

## Active CI Pipeline (VERIFIED — `.github/workflows/ci.yml`)

Triggers: push or pull request to `main` or `master`.

1. **Backend Build & Test (`ubuntu-latest`)**: JDK 21 (Temurin), `./gradlew clean build --no-daemon`.
2. **Frontend Build & Lint (`ubuntu-latest`)**: Node 20, `npm ci`, `npm run type-check`, `npm run lint`, `npm test` (`tests/*.test.mjs`, 22 Node SSR & analytics tracker tests), `npm run build`.
3. **Compose Smoke Test (`ubuntu-latest`)**: Installs Chromium, builds & starts PostgreSQL (with Flyway `V1` + `V2` migrations) + Spring (`prod` profile on `:8080`) + Nginx (`:3000`, `Host $http_host`), verifies `/api/v1/projects`, `/api/v1/health`, `/api/v1/status`, `/api/v1/analytics/summary`, `/api/v1/projects/creams`, direct `/projects/creams`, `/status`, and `/analytics` SPA routes, runs `npm run test:e2e`, and runs `docker compose down --volumes`.

---

## Retired Target & Historical Reference (`deploy/`)

The AWS Lightsail instance was retired in August 2026, and `.github/workflows/deploy-portfolio.yml` was removed in `31a63b0` (preserved in Git history).
Files remaining in `deploy/` (`nginx-portfolio.conf`, `nginx-portfolio-bootstrap.conf`, `nginx-subdomain-template.conf`, `portfolio.service`, `setup-lightsail.sh`) are historical Lightsail setup references preserved for migration context only; see `docs/SOURCE_OF_TRUTH.md`.
