# Deployment and CI/CD — Module Status

Source: .github/workflows/deploy-portfolio.yml, deploy/ directory listing, SOURCE_OF_TRUTH.md
Last verified: 2026-05-10
Confidence: HIGH for pipeline structure; UNVERIFIED for current production state

---

## CI/CD Pipeline Overview (VERIFIED)

File: `.github/workflows/deploy-portfolio.yml`
Trigger: push to `main` or `master`, or manual workflow_dispatch
Runner: ubuntu-latest

### Pipeline Stages

1. Checkout repository
2. Set up JDK 21 (Temurin distribution) via actions/setup-java@v4
3. Set up Node.js 20 with npm cache
4. Build React frontend (`npm ci && npm run build` in frontend/)
   - VITE_API_URL intentionally unset — defaults to relative `/api/v1`
5. Build Spring Boot JAR (`./gradlew clean build -x test --no-daemon` in backend/)
   - Tests are skipped in CI build (`-x test`)
6. Create deployment package:
   - `frontend/dist/` → `deploy-pkg/frontend/`
   - Built JAR (non-plain) → `deploy-pkg/portfolio-backend.jar`
   - `deploy/nginx-portfolio.conf` → `deploy-pkg/`
   - `deploy/nginx-portfolio-bootstrap.conf` → `deploy-pkg/`
   - `deploy/portfolio.service` → `deploy-pkg/`
7. Deploy to Lightsail via SSH:
   - Frontend: SCP to /tmp, then sudo copy to /var/www/portfolio
   - Backend JAR: SCP to /home/ec2-user/portfolio-backend.jar
   - Nginx config: conditional HTTPS or bootstrap HTTP based on certbot cert presence
   - systemd service: copy and daemon-reload
   - Restart portfolio service
8. Verify deployment:
   - Service active check (6 attempts × 5s = 30s max)
   - Nginx frontend check via localhost with Host header
   - API health check via localhost with Host header
   - Certificate existence check
   - Public HTTPS frontend check (curl https://faisalhanafi.com/)
   - Public HTTPS API check (curl https://faisalhanafi.com/api/v1/health)

---

## Deploy Target: AWS Lightsail

| Property | Value | Confidence |
|---|---|---|
| Host | AWS Lightsail instance | HIGH — workflow verified |
| SSH user | ec2-user | HIGH — workflow verified |
| SSH key | GitHub secret LIGHTSAIL_SSH_KEY | HIGH — workflow verified |
| IP | GitHub secret LIGHTSAIL_IP | HIGH — workflow verified |
| Frontend location | /var/www/portfolio/ | HIGH — workflow verified |
| Backend JAR location | /home/ec2-user/portfolio-backend.jar | HIGH — workflow verified |
| Service name | portfolio | HIGH — workflow verified |
| Domain | faisalhanafi.com | HIGH — workflow verified |
| TLS | Let's Encrypt via certbot | HIGH — workflow verified |

---

## Production Nginx Config (VERIFIED — file exists)

Files in deploy/:
- `nginx-portfolio.conf` — HTTPS config (used when certbot cert exists)
- `nginx-portfolio-bootstrap.conf` — HTTP-only bootstrap (used when cert is missing)
- `nginx-subdomain-template.conf` — template for subdomain routing
- `portfolio.service` — systemd unit file
- `setup-lightsail.sh` — initial server setup script

The workflow applies nginx-portfolio.conf when certbot cert files exist at:
`/etc/letsencrypt/live/faisalhanafi.com/fullchain.pem`
`/etc/letsencrypt/live/faisalhanafi.com/privkey.pem`

Otherwise it falls back to nginx-portfolio-bootstrap.conf (HTTP only).

---

## Backend Systemd Service (INFERRED from deploy/portfolio.service)

The service is named `portfolio` and is managed by systemd. The workflow restarts it with
`sudo systemctl restart portfolio` after deploying the new JAR.

Spring profile in production: the prod profile must be activated via environment variable
or systemd service configuration. The application.yml defaults to `dev` if no profile is
explicitly set. The portfolio.service file likely sets `SPRING_PROFILES_ACTIVE=prod` and
the database environment variables (DB_URL, DB_USERNAME, DB_PASSWORD).
Confidence: INFERRED — portfolio.service not read directly.

---

## Last Known Production State

| Item | Status | Date | Evidence |
|---|---|---|---|
| Last deployment commit | 7a47312 (2026-05-01) | 2026-05-01 | git log |
| Deployment success confirmed | UNVERIFIED | — | No session record |
| Current live content | UNKNOWN | — | Not verified post 2026-05-01 |

---

## Known Gaps

1. Tests skipped in CI (`-x test`) — no unit or integration tests run in the pipeline
2. No rollback mechanism documented for failed deployments
3. Production deployment verification after 2026-05-01 not confirmed
