# Project Source of Truth — Portfolio

Stable facts about this project. Items marked PLANNED are not yet implemented.
Items marked INFERRED are derived from code patterns, not verified through direct testing.
When in doubt, read the executable files — they always win over this document.

Authority chain: `docs/SOURCE_OF_TRUTH.md` > `docs/PORTFOLIO_SESSION_SOURCE_OF_TRUTH.md`

---

## Project Purpose

A full-stack portfolio platform that demonstrates Faisal Hanafi's engineering capability through
a real backend, a polished frontend, and truthful project storytelling. The portfolio itself is
part of the proof-of-capability — it is not a static site.

Primary audiences:
- Non-technical recruiters (fast credibility signal)
- Technical hiring managers (engineering depth)
- Faisal (maintainable platform he can update honestly)

---

## Tech Stack

### Backend

| Component | Value | Confidence |
|---|---|---|
| Framework | Spring Boot 3.4.2 | HIGH — build.gradle verified |
| Language | Java 21 | HIGH — build.gradle toolchain confirmed |
| JDK requirement | Amazon Corretto or Temurin 21 ONLY | HIGH — pilot confirmed; JDK 8/25/26 all fail |
| Build tool | Gradle 9.7.1 (wrapper with distribution checksum) | HIGH — gradle-wrapper.properties verified |
| ORM | Spring Data JPA (Hibernate) | HIGH — build.gradle verified |
| Validation | spring-boot-starter-validation | HIGH — build.gradle verified |
| Observability | Spring Boot Actuator + Micrometer Prometheus | HIGH — build.gradle verified |
| API docs | springdoc-openapi-starter-webmvc-ui 2.8.4 | HIGH — build.gradle verified |
| Boilerplate reduction | Lombok | HIGH — build.gradle verified |
| Dev database | H2 in-memory | HIGH — application-dev.yml verified |
| Prod database | PostgreSQL (via env vars DB_URL, DB_USERNAME, DB_PASSWORD) | HIGH — application-prod.yml verified |

### Frontend

| Component | Value | Confidence |
|---|---|---|
| Framework | React 18 | HIGH — README verified |
| Language | TypeScript (strict mode) | HIGH — README + tsc check confirmed |
| Build tool | Vite | HIGH — README verified |
| Styling | Tailwind CSS | HIGH — README verified |
| Animation | Framer Motion | HIGH — README verified |
| State/data fetching | React Query | HIGH — README verified |
| HTTP client | Axios (via api/client.ts) | HIGH — client.ts verified |

### Infrastructure

| Component | Value | Confidence |
|---|---|---|
| Hosting | Lightsail retired (Aug 2026); replacement VPS pending (Docker rollout KIV) | HIGH — `docs/SOURCE_OF_TRUTH.md` verified |
| Web server | Nginx in Compose rehearsal (serves static Vite build + proxies `/api/` to `:8080`) | HIGH — `docker-compose.yml` verified |
| CI/CD | GitHub Actions (`.github/workflows/ci.yml` — Backend, Frontend, Compose smoke) | HIGH — `ci.yml` verified (`deploy-portfolio.yml` retired at `31a63b0`) |
| Historical reference | Legacy Lightsail scripts in `deploy/` (non-active; see `docs/SOURCE_OF_TRUTH.md`) | HIGH — directory verified |

---

## Architecture Overview

```
Local dev:
  React (Vite dev server :5173) --> Spring Boot (:8081, dev profile, H2)

Compose rehearsal / intended production:
  Browser --> Nginx (:3000 local / :80/:443 VPS)
                ├── /          --> static Vite build
                └── /api/      --> Spring Boot (:8080, prod profile, PostgreSQL)
```

Backend port distinction: local dev uses 8081; production backend uses 8080 (behind Nginx).

---

## Main Modules

### Backend — package: com.faisal.portfolio

| Package | Contents | Confidence |
|---|---|---|
| project | Project, ProjectController, ProjectDetailDto, ProjectDto, ProjectImage, ProjectRepository, ProjectService, Technology | HIGH — file list verified |
| skill | Skill, SkillCategory, SkillCategoryDto, SkillCategoryRepository, SkillController, SkillRepository, SkillService | HIGH — file list verified |
| experience | Experience, ExperienceController, ExperienceDto, ExperienceRepository, ExperienceService | HIGH — file list verified |
| status | ApiLatencyTracker, ApiMetricsFilter, GitHubStatusService, StatusController, StatusMetricsConfig, StatusProperties, StatusService, StatusSnapshotDto | HIGH — file list verified |
| common | ApiResponse (record), GlobalExceptionHandler, ResourceNotFoundException | HIGH — file list verified |
| config | CorsConfig, OpenApiConfig | HIGH — file list verified |
| seed | DataSeeder (CommandLineRunner) | HIGH — file verified |

### Frontend — src/

| Directory | Contents | Confidence |
|---|---|---|
| api/ | client.ts (Axios instance), hooks.ts | HIGH — file list verified |
| components/layout/ | EmailSidebar, Footer, Layout, Navbar, SocialSidebar | HIGH — file list verified |
| components/projects/ | FeaturedProject, ProjectCard, ProjectDetail | HIGH — file list verified |
| components/sections/ | AboutSection, ContactSection, ExperienceSection, HeroSection, ProjectsSection, SkillsSection | HIGH — file list verified |
| components/ui/ | AnimatedSection, Button, Card, QueryContent, Skeleton, SketchDivider, SketchDoodles, SketchUnderline, Tag | HIGH — file list verified |
| hooks/ | useScrollPosition.ts | HIGH — file list verified |
| lib/ | animations.ts, projectImages.ts, projectNarratives.ts | HIGH — file list verified |
| pages/ | HomePage.tsx, NotFoundPage.tsx, ProjectPage.tsx, StatusPage.tsx | HIGH — file list verified |
| types/ | index.ts | HIGH — file list verified |

---

## User Roles

None. The portfolio is a public read-only product. There is no authentication, no admin panel,
and no write access of any kind through the public API.

The PRD explicitly scopes out admin CMS, authentication, and blog platform as out-of-scope
unless explicitly reintroduced.

---

## Naming Conventions

| Convention | Rule | Confidence |
|---|---|---|
| Backend root package | com.faisal.portfolio | HIGH — all files verified |
| Backend architecture | Package-by-feature (project/, skill/, experience/) | HIGH — directory structure verified |
| Backend DTOs | Java records (e.g., ProjectDto, ProjectDetailDto, ExperienceDto) | HIGH — DataSeeder imports + ApiResponse verified |
| Frontend components | PascalCase (HeroSection.tsx, ProjectCard.tsx) | HIGH — file list verified |
| Frontend hooks | camelCase (useScrollPosition.ts) | HIGH — file list verified |

---

## Database Expectations

| Environment | Database | Schema management | Seed | Confidence |
|---|---|---|---|---|
| dev | H2 in-memory (portfoliodb) | ddl-auto: create-drop (recreated each boot) | DataSeeder runs on every boot | HIGH — application-dev.yml verified |
| prod | PostgreSQL (credentials via env vars) | ddl-auto: update | DataSeeder runs on every boot | HIGH — application-prod.yml verified; ddl-auto: update is INFERRED migration strategy |

Production migration note: No Flyway or Liquibase is present in build.gradle. ddl-auto: update
means Hibernate manages schema evolution automatically. This is sufficient for a portfolio but
means there is no migration rollback capability. Status: ACCEPTED for current scale.

---

## Non-Negotiable Rules

1. Pre-commit gate (`.githooks/pre-commit`) must pass before any commit. Never use `--no-verify`.
2. One deliverable per session (`docs/PORTFOLIO_SESSION_SOURCE_OF_TRUTH.md`; former shared session guard retired at `447ac37`).
3. JDK 21 only. JDK 8, 25, and 26 were all tested during the 2026-04-18 pilot and all failed.
4. Do not run the prod Spring profile locally unless PostgreSQL is available and configured.
5. Keep secrets and `lightsail private key.txt` (gitignored) out of Git commits.
6. Do not use `git add -A` without reviewing `git status` first.
7. Commit messages must follow the [shared workspace SOP](../../../../standards/COMMIT_MESSAGES.md).
8. Container/production backend port is 8080; local dev backend port is 8081.

---

## Links to Active Authority Documents

- `docs/SOURCE_OF_TRUTH.md` — documentation entry point; lists all active authorities
- `docs/PORTFOLIO_SESSION_SOURCE_OF_TRUTH.md` — session execution rules and gate semantics
- `../../standards/COMMIT_MESSAGES.md` — required commit message format
- `.githooks/pre-commit` — enforced commit gate
- `scripts/healthcheck.sh` — healthcheck dispatcher
- `.github/workflows/ci.yml` — CI pipeline (build, test, Compose + Playwright smoke)

---

## Stable Facts vs Planned Items

### Stable (implemented and verified)
- Spring Boot REST API serving projects, skills, experience, health, and bounded operational `/api/v1/status` (with DB probe, rolling 5-minute latency p95, and scheduled conditional GitHub poller)
- H2 dev database with DataSeeder
- Gate system (secret scan + staged Unicode/bidi verification + L1 + L2-fast + L2-full + pre-commit hook)
- Boundary architecture & production Actuator/Prometheus lockdown checks (`EnterpriseBoundaryArchitectureTest`)
- GitHub Actions CI pipeline (Backend, Frontend, Compose PostgreSQL + Playwright E2E)
- React frontend with Tailwind, Framer Motion, React Query, public `/status` route, 14 SSR fixture checks, and 12 Playwright E2E checks

### Planned (documented in `docs/PORTFOLIO_ROADMAP.md`)
- Docker VPS delivery rehearsal resume (currently KIV)
- Flyway schema baseline, first-party analytics, Ask About My Work (pgvector RAG), and JD matcher (Months 3–12)
