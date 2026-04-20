# Portfolio Dev Session — Cybersec Hardening + Lightsail Cost Audit

> **Date**: 2026-04-16
> **Project Path**: ./Portfolio
> **Session Type**: Pre-deploy security + infrastructure audit — **NOT deployment**
> **Governance**: Root CLAUDE.md (SOP v2.2.0) → Portfolio/CLAUDE.md
> **Supersedes**: PORTFOLIO_SESSION_2026-04-01.md (partially executed; this session replaces the deploy push with a cybersec gate)

---

## Context You Need

Portfolio is Faisal's personal portfolio platform — Spring Boot (backend) + React/TypeScript (frontend). Deployed at https://faisalhanafi.com on AWS Lightsail (shared with CREAMS demo).

### Why This Session Is Different

The 2026-04-01 session planned to commit, push, and verify deployment. That plan is now **paused**:

1. **Cybersec gate**: No deployment push until a full security audit passes. Production traffic on a shared instance means Portfolio vulnerabilities become CREAMS vulnerabilities.
2. **Lightsail cost overrun**: The $5/month tier is trending toward $6/month. Need to find the leak and either stay on $5 or justify the upgrade.

This session delivers the **pre-deploy security baseline** and the **cost/traffic root-cause analysis**. No push, no deploy.

### Current Local State (Verified April 16)

| Item | Status |
|------|--------|
| Uncommitted local commits ahead of origin/main | **3 commits** (redesign, deployment scripts, gitignore) |
| Working tree | Clean (no uncommitted changes) |
| `.gitignore` entries for `*.pem`, `*.ppk`, `lightsail private key.txt` | Present |
| Private key files physically on disk | Yes — untracked |
| Backend config files | `application.yml`, `application-dev.yml`, `application-production.yml` present |

---

## YOUR MISSION THIS SESSION

### Task 1 — Git History Secret Audit (CRITICAL — do this FIRST)

The private key files exist untracked. But they may have been committed BEFORE `.gitignore` was updated. If so, the key is already in the repo history and must be rotated on Lightsail.

```bash
cd ./Portfolio
git log --all --full-history -- "lightsail private key.txt" "*.pem" "*.ppk"
git log --all --full-history -- "LightsailDefaultKey-ap-southeast-1.pem"
```

1. If ANY commit touches these paths: the key is compromised.
   - Rotate the Lightsail key immediately (AWS console → Lightsail → SSH keys → regenerate).
   - Decide: rewrite history (`git filter-repo`) OR accept the leak + rotation. Record the decision in `docs/SECURITY_INCIDENT.md`.
2. If NO commit touches them: document confirmation in `docs/SECURITY_AUDIT.md` with the commands you ran + the null output. Move on.

### Task 2 — Pre-Deploy Cybersec Checklist

Produce `docs/PRE_DEPLOY_SECURITY_CHECKLIST.md`. Each item must be verified with evidence (file path + line number or command output), not asserted.

**Backend (Spring Boot)**

- [ ] `application-production.yml` has no plaintext DB passwords — uses `${DB_PASSWORD}` env var
- [ ] `application.yml` has no secrets, no prod credentials
- [ ] CORS allowed origins is an explicit list — NOT `*` or regex wildcard
- [ ] HTTPS enforcement configured (reject plain HTTP at the Spring/Nginx layer)
- [ ] Rate limiting on `/api/*` endpoints (at least the public ones)
- [ ] No debug endpoints exposed (`/actuator/*` gated to internal IPs, or disabled in production profile)
- [ ] Exception handlers do not leak stack traces in production responses
- [ ] Dependency audit: `./gradlew dependencyCheckAnalyze` (or equivalent) — no HIGH/CRITICAL CVEs unresolved
- [ ] Log configuration does NOT include request bodies or auth headers

**Frontend (React / Vite)**

- [ ] `frontend/.env.production` has no API keys exposed (anything with `VITE_` prefix is shipped to browser)
- [ ] No `console.log` leaking user data or tokens
- [ ] Content-Security-Policy header set (Nginx-level or meta tag)
- [ ] Subresource integrity on any CDN-loaded scripts
- [ ] Source maps disabled in production build (or hosted privately)
- [ ] `npm audit` clean or documented exceptions

**Infrastructure (Lightsail / Nginx)**

- [ ] SSH: password auth disabled, only key auth allowed
- [ ] UFW/firewall: only 22, 80, 443 exposed externally
- [ ] Nginx: HTTP → HTTPS redirect configured
- [ ] TLS: modern config (TLS 1.2+ only, strong ciphers) — verify with SSL Labs scan (https://www.ssllabs.com/ssltest/)
- [ ] Fail2ban or equivalent to block brute-force SSH
- [ ] Automatic security updates enabled (`unattended-upgrades`)

**CI/CD (GitHub Actions)**

- [ ] Secrets stored in GitHub Secrets, not checked into workflow YAML
- [ ] Workflow does not echo secrets in logs
- [ ] Deployment SSH key rotated if it was ever committed (see Task 1)

### Task 3 — Lightsail Cost + Traffic Root-Cause

The bill climbed from $5 → ~$6. The $5 plan includes 1 TB data transfer out; overage is $0.09/GB. Likely culprit: bandwidth overrun, possibly from uncompressed assets or a hot-linked image.

1. **Get current usage** — from AWS console (or CLI if configured):
   - Instance tier + CPU burst credits
   - Data transfer out (current-month GB used)
   - Any attached block storage or static IPs
2. **Likely causes to check**, in order of suspicion:
   - Frontend assets not gzipped/brotli-compressed at Nginx layer
   - Images served at full resolution (no WebP, no responsive sizes)
   - Analytics/bot traffic hammering endpoints
   - Uploaded files or backup tarballs accidentally served publicly
   - Recursive crawler trap (poorly-defined routes)
3. **Produce `docs/COST_OPTIMIZATION.md`** with:
   - Current usage numbers
   - Root cause (with evidence — Nginx access log sample, asset size audit output)
   - Recommended remediation per cause:
     - Enable Nginx gzip + brotli for text types
     - Convert hero/large images to WebP, add `srcset`
     - Put Cloudflare free tier in front (primary fix — offloads all static bandwidth, adds WAF/DDoS for free)
     - Add `robots.txt` + rate limiting to deter crawlers
     - Move static frontend to S3 + CloudFront if Cloudflare is rejected
4. **Do NOT upgrade the instance tier yet.** Diagnose first.

### Task 4 — Backend DB Config Verification

The 2026-04-01 session planned this but may not have finished it.

1. Confirm `application-production.yml` uses PostgreSQL (not H2).
2. Confirm `application-dev.yml` uses H2 for local dev.
3. Run `./gradlew bootRun --args='--spring.profiles.active=dev'` — verify backend starts cleanly on H2.
4. Do NOT run the production profile locally unless you have a local PostgreSQL ready.
5. Document the profile matrix in `backend/README.md` or `docs/BACKEND_PROFILES.md`.

### Task 5 — Frontend Build Smoke Test

1. `cd frontend && npm ci && npm run build`
2. Fix any TypeScript or build errors. Do NOT widen `tsconfig` strictness to paper over them.
3. If the build fails and the cause isn't obvious in under 30 minutes — STOP, document the failure, don't push.

### Task 6 — Push Decision Gate

**Only IF all of the following are true, push the 3 local commits to origin/main:**

- Task 1 (git history audit) complete and documented
- Task 2 (cybersec checklist) 100% green
- Task 3 (cost analysis) documented — remediation plan exists even if not yet applied
- Task 4 (backend starts on dev profile) verified
- Task 5 (frontend builds clean) verified

**If ANY check fails, DO NOT push.** Commit any audit docs locally, surface the failing check to Faisal, and end the session there.

---

## Key Files

| File | Purpose |
|------|---------|
| `docs/SECURITY_AUDIT.md` | **NEW** — Task 1 evidence |
| `docs/SECURITY_INCIDENT.md` | **NEW IF** key leak found |
| `docs/PRE_DEPLOY_SECURITY_CHECKLIST.md` | **NEW** — Task 2 full checklist |
| `docs/COST_OPTIMIZATION.md` | **NEW** — Task 3 analysis + plan |
| `docs/BACKEND_PROFILES.md` or `backend/README.md` | Task 4 documentation |
| `backend/src/main/resources/application.yml` | Backend base config — audit for secrets |
| `backend/src/main/resources/application-production.yml` | Production profile |
| `frontend/.env.production` | Frontend env — audit for exposed keys |
| `.github/workflows/deploy-portfolio.yml` | CI/CD pipeline — audit for secret leaks |

---

## DO NOT

- **Do NOT push to origin/main until Task 6 gate passes.** This is the whole point of the session.
- **Do NOT deploy to Lightsail** under any circumstance. Deployment is paused.
- **Do NOT upgrade the Lightsail instance tier** to fix the cost overrun. Root-cause first.
- **Do NOT rotate the Lightsail key** unless Task 1 shows it was committed. Premature rotation breaks existing deployments.
- **Do NOT add new features or redesign the UI.** Stabilize and audit.
- **Do NOT commit** private keys, `.env` files, or any file matching `.gitignore` patterns (run `git status` before every commit).
- **Do NOT `git add -A`.** Stage files by name.
- **Do NOT loosen TypeScript strictness or suppress linter rules** to make the build pass.

---

## EXIT CRITERIA

- [ ] `docs/SECURITY_AUDIT.md` documents git history audit result
- [ ] `docs/PRE_DEPLOY_SECURITY_CHECKLIST.md` fully green OR failed items documented with blockers
- [ ] `docs/COST_OPTIMIZATION.md` identifies root cause of the $5 → $6 overrun and proposes remediation (Cloudflare front is the expected winner)
- [ ] Backend starts cleanly on dev profile (`./gradlew bootRun --args='--spring.profiles.active=dev'`)
- [ ] Frontend builds cleanly (`npm run build` in `frontend/`)
- [ ] Decision made on push (pushed only if Task 6 gate passes; otherwise clearly blocked)
- [ ] Session summary notes which items are still open for the next session

**After this session**: IF cybersec + cost audit pass, a deployment session can push and deploy. IF either fails, next session is focused remediation (Cloudflare setup, or the specific failing checklist item).
