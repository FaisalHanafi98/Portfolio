# Portfolio project summary

**As of 16 November 2026 · local working copy at `887a99e`**

Portfolio is Faisal Hanafi’s public, read-only engineering portfolio. It gives
recruiters a quick view of his work and gives technical reviewers details on the
problems, technologies, and trade-offs behind it. CREAMS, Auto-Recruit, and
Portfolio itself are the priority stories. The application also demonstrates how
Faisal connects a frontend, an API, and a database into a working product.
[Product requirements](../PORTFOLIO_PRD.md)

## Current implementation

| Area | What the checkout contains |
|---|---|
| Website | A responsive React and TypeScript single-page app built with Vite and styled with Tailwind CSS. The home page presents an introduction, projects, skills, experience, and contact links. React Router serves project details at `/projects/:slug`. The seed includes CREAMS, Auto-Recruit, Sen2Nal, and Portfolio. |
| API and data | Java 21 and Spring Boot 3.4 expose versioned, read-only JSON endpoints for health, project summaries and details, skill categories, and experience. Spring Data JPA reads the stored content. The application seeds the database on startup when no projects exist. |
| Runtime | Standalone development uses H2 and a Spring backend on port `8081`. Local Compose is configured to use PostgreSQL, a Spring backend on `8080`, and an Nginx frontend on `3000` that forwards `/api/` to Spring. |
| Verification | The GitHub Actions workflow defines backend build and tests, frontend type-check, lint, tests and build, and a Compose smoke test covering API health, project list and detail, and the direct CREAMS page URL through Nginx. |

[Frontend entry point](../../frontend/src/App.tsx) ·
[Project API](../../backend/src/main/java/com/faisal/portfolio/project/ProjectController.java) ·
[Seed data](../../backend/src/main/java/com/faisal/portfolio/seed/DataSeeder.java) ·
[Runtime guide](../SOURCE_OF_TRUTH.md) · [CI workflow](../../.github/workflows/ci.yml)

### Recent changes in the working copy

- Projects, project detail, skills, and experience share a rendering policy for
  loading, offline pauses, empty results, and failures. Visitors can retry and
  keep reading cached content after a failed refresh. A confirmed project
  HTTP 404 takes precedence over a cached detail. [Request-state contract](../PORTFOLIO_PRD.md#content-request-states)
- The mobile menu releases its scroll lock when closed or resized to desktop.
  Escape returns focus to the toggle. Experience selection follows a stable ID,
  and the first entry is shown if the selected one disappears. Project views
  use matching screenshots where available. [UI state owners](../PORTFOLIO_PRD.md#other-state-owners)
- UI motion, contrast, case-study hierarchy, and seed data were polished and
  hardened on 16 November 2026: `MotionConfig reducedMotion="user"` governs
  Framer Motion transitions, `transition-all` was replaced with explicit
  compositor/color properties, accent colors meet WCAG AA contrast (`>= 4.5:1`)
  on cream and white surfaces, `DataSeeder.java` and Hero proof-tape copy align
  with verified project/experience facts, and the navbar provides an explicit
  `"Request Resume"` email action until a PDF asset is configured via
  `VITE_RESUME_URL`. [November 2026 session record](02_SESSION_HISTORY/2026-11-16_ui_motion_and_credibility_polish.md)
- Compose now uses the PostgreSQL-compatible Spring profile and a single
  executable backend JAR. The new CI smoke job is defined locally.
  [Compose configuration](../../docker-compose.yml) ·
  [Backend image build](../../backend/Dockerfile) · [CI workflow](../../.github/workflows/ci.yml)

## Accepted direction

- Keep a public, read-only, API-backed portfolio with accurate project stories,
  responsive navigation, and accessible interaction states. The current product
  scope excludes authentication, an admin CMS, a blog, and an analytics dashboard.
  [Product requirements](../PORTFOLIO_PRD.md#8-current-product-gaps--backlog)
- Faisal’s August 2026 direction retired the old Lightsail instance and shifted
  work to a local full-stack rehearsal, automated verification, and preparation
  for a replacement VPS. The repository still contains the legacy Lightsail
  deployment workflow. Its push trigger records how the code is configured;
  the latest live deployment and the replacement provider are unconfirmed.
  [Owner-reviewed August session](02_SESSION_HISTORY/2026-08-23_lightsail_decommission_and_streamlining.md) ·
  [Legacy workflow](../../.github/workflows/deploy-portfolio.yml)

## Proposed changes and open decisions

| Item | Status and next evidence |
|---|---|
| Validate the full stack | Run the Compose smoke test with a working Docker engine and record the result for the actual revision. The existing frontend fixture tests exercise rendering states, and a real API-backed browser journey remains a useful next extension. |
| Restore local gate enforcement | The session guide describes a runtime gate under `.githooks/`. This checkout selects `.hooks/` for secret scanning, and the documented session guard is absent at its expected path. Reconcile the two before relying on the local commit gate. |
| Choose and deploy to a budget VPS | The proposed design builds the Vite assets and Spring Boot JAR in CI and runs Nginx, Spring via systemd, and PostgreSQL on one VPS. Nginx serves HTTPS and proxies the API on the same origin. Spring and PostgreSQL stay private. Keep a database backup and the previous release available for recovery. The hosting shortlist is research, and provider choice and cutover are open. |
| Review release security | The August status tracked a pre-deployment security review. Record its findings and verify the selected host's public exposure before cutover. |
| Polish release content | CREAMS, Auto-Recruit, Sen2Nal, and Portfolio seed narratives and hero proof-tape claims were aligned with verified facts on 16 November 2026. The navbar now uses a safe `"Request Resume"` email action when `VITE_RESUME_URL` / `frontend/public/resume.pdf` is absent; supply the PDF asset when ready for direct download. |
| Consider optional improvements | Social share metadata, a sitemap, browser automation, and other enhancements are candidates. The optional backlog’s owner-selection section is blank. |

[Session gate](../PORTFOLIO_SESSION_SOURCE_OF_TRUTH.md) ·
[VPS migration design](../SOURCE_OF_TRUTH.md#vps-migration-preparation) ·
[Optional backlog](../OPTIONAL_IMPROVEMENTS_Portfolio.md#your-prioritized-backlog)

## Verification as of 16 November 2026

- All 11 frontend content, case-study hierarchy, and navigation fixture tests
  passed (`node --test tests/content-states.test.mjs`), alongside `npm run type-check`,
  `npm run lint` (`--max-warnings 0`), `npm run build`, and backend `./gradlew test`
  under Java 21. [Frontend tests](../../frontend/tests/content-states.test.mjs)
- Live browser verification against the Vite dev server (`:5173`) and H2 backend
  (`:8081`) passed across `1280x800` desktop, `768x1024` tablet, `375x812` mobile,
  and `prefers-reduced-motion: reduce`, confirming WCAG AA text contrast
  (`4.98:1` to `12.68:1`), zero horizontal overflow, mobile menu `Escape` focus
  return and resize auto-close, experience tab selection, and confirmed HTTP `404`
  handling on `/projects/:slug`. [Session verification log](02_SESSION_HISTORY/2026-11-16_ui_motion_and_credibility_polish.md)
- Compose configuration validated. The Docker engine was unavailable (`//./pipe/dockerDesktopLinuxEngine` not running), so the
  full-stack Compose run and the remote CI job remain unverified. No live deployment
  was verified for this summary. [Compose verification steps](../SOURCE_OF_TRUTH.md#local-postgresql-rehearsal)

The [April 2026 gate pilot](02_SESSION_HISTORY/2026-04-18_gate_pilot.md)
recorded five passing scenarios for the earlier gate configuration. The May
frontend redesign and seed narrative refresh are recorded at `7a47312`.
