# Portfolio Dev Session — Commit, Fix Backend DB, Verify Deployment

> **Date**: 2026-04-01
> **Project Path**: ./Portfolio
> **Session Type**: Stabilization — commit pending work, fix backend, verify deployment
> **Governance**: Root CLAUDE.md (SOP v2.2.0) → Portfolio/CLAUDE.md

---

## Context You Need

This is Faisal's personal portfolio platform — a dynamic, API-driven Spring Boot (backend) + React/TypeScript (frontend) site. It showcases CREAMS, Auto-Recruit, and the portfolio itself.

**Stack**: Spring Boot 3.x / Java 17+ / Gradle (backend), React 18 / TypeScript / Vite / Tailwind / Framer Motion (frontend), PostgreSQL (production) / H2 (dev).

**Deployed at**: https://faisalhanafi.com (Lightsail, same server as CREAMS demo)
- Frontend: deployed to `/var/www/portfolio`, served via Nginx
- Backend: Spring Boot JAR on port 8080, proxied via Nginx at `/api/*`
- CI/CD: GitHub Actions → SSH deploy to Lightsail

**Current state**: ~85% complete (per project registry). Frontend has been substantially redesigned but **37 files are uncommitted** — all frontend component rewrites, config changes, and deployment scripts. Backend is crashing on H2 initialization (needs production database config).

**Last commit**: `4ff24f3` — "fix: correct API base URL and contact email for production" (Feb 2026)

---

## YOUR MISSION THIS SESSION

### Task 1 — Audit and Commit Pending Changes

37 files modified/added but never committed. This is risky — one bad `git checkout` and it's all gone.

1. Run `git diff --stat` to understand the scope
2. Review the changes — are they coherent? Do they represent one logical change or multiple?
3. Stage and commit logically:
   - If it's one big redesign, commit as one: `feat: frontend redesign with updated components and styles`
   - If separable, split into logical commits (deployment scripts, frontend redesign, config fixes)
4. **Do NOT commit**: `lightsail private key.txt` — this MUST be added to `.gitignore` immediately. It's a private SSH key sitting in the repo root.

### Task 2 — Fix Backend Database Configuration

The deployment log says: *"Backend requires production database configuration (currently crashing on H2 initialization)"*

1. Read `backend/src/main/resources/application.yml` and `application-production.yml`
2. Determine what's wrong — likely missing PostgreSQL config for production profile
3. Fix the configuration so:
   - `dev` profile uses H2 (in-memory, for local development)
   - `production` profile uses PostgreSQL (on Lightsail, same MySQL/PostgreSQL instance or a new one)
4. Verify the backend starts locally: `./gradlew bootRun`

### Task 3 — Verify Frontend Build

1. `cd frontend && npm run build`
2. Check for TypeScript errors: `npm run type-check` (if configured)
3. Check for lint errors: `npm run lint`
4. Fix any build failures

### Task 4 — Security Cleanup

1. **CRITICAL**: Add `lightsail private key.txt` and `LightsailDefaultKey-ap-southeast-1.pem` and `.ppk` to `.gitignore`
2. Check if these files were ever committed to git history: `git log --all --follow -- "lightsail private key.txt"`
3. If committed, flag it — the key may need to be rotated on Lightsail
4. Check `.env.production` in frontend — ensure no secrets are exposed (API keys, etc.)

### Task 5 — Test Deployment Pipeline

1. After committing, push to trigger GitHub Actions
2. Verify the CI/deploy workflow runs successfully
3. If the backend DB issue is fixed, verify `https://faisalhanafi.com/api/health` responds

---

## Key Files

| File | Purpose |
|------|---------|
| `CLAUDE.md` | Project instructions (comprehensive) |
| `docs/PORTFOLIO_PRD.md` | Full PRD with phases |
| `DEPLOYMENT_LOG.md` | Deployment history and setup |
| `backend/src/main/resources/application.yml` | Backend config |
| `backend/src/main/resources/application-production.yml` | Production config (likely broken) |
| `backend/build.gradle` | Dependencies |
| `frontend/package.json` | Frontend dependencies |
| `frontend/vite.config.ts` | Vite config |
| `.github/workflows/deploy-portfolio.yml` | CI/CD pipeline |
| `deployment/` | Server setup scripts (uncommitted) |

---

## DO NOT

- Add new features. Stabilize and commit what exists.
- Redesign the frontend. The redesign is done — commit it.
- Push private keys to GitHub. Add them to `.gitignore` FIRST.
- Spend time on animations or visual polish. That's a future session.
- Refactor the backend API. Just make it start with the correct DB config.

---

## EXIT CRITERIA

Before closing this session:
- [ ] Private keys added to `.gitignore` (and verified never committed to git history)
- [ ] All 37 pending changes reviewed and committed (logical commits)
- [ ] Backend starts locally with `./gradlew bootRun` (H2 dev profile)
- [ ] Production config points to correct database
- [ ] Frontend builds without errors (`npm run build`)
- [ ] Changes pushed, CI/CD pipeline verified
- [ ] `https://faisalhanafi.com` serves the updated frontend
- [ ] Backend health endpoint responds (or issue documented if DB not yet provisioned)

After this session: Content updates (project descriptions, skills, experience timeline) → Final polish → Portfolio complete.
