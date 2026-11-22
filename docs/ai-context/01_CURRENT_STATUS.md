# Portfolio project summary

**As of 22 November 2026 · implementation through `6b263f8`**

## Implementation, 22 November 2026

Faisal authorised continued implementation in micro-commits and publication to GitHub.
This pass adds repeatable browser verification, keyboard navigation for the
story chapters, and recovery from unknown routes. Opening a page scrolls to
the top or its section anchor, including the return from a project detail to
the showcase. The baseline is `5fd85cb` with 13 passing frontend fixture tests.

Use Playwright Test 1.65.1 (Apache-2.0, Node 20+) as a development dependency
for reproducible browser tests. It exercises local servers and uses an installed
Chrome locally or a downloaded Chromium in CI. Browser binaries and test traces
are local test artifacts. Tests run against a local seeded development backend,
with fixture responses limited to failure cases. Acceptance requires working
project navigation, a real API 404, keyboard chapter changes with focus preserved,
and a useful unknown-route page.

The existing Gradle 9.7.1 wrapper update passed backend tests and a boot JAR
build under Java 21 with isolated output. Its wrapper JAR matched the checksum
published by Gradle, and the distribution checksum is now pinned. The first
build attempt failed when it tried to delete the existing locked output under
`backend/build/`. Verification used an init script in the local Git metadata
to direct output to ignored `backend/.gradle-home/verification-2026-11-22/`.

Seven Chrome browser journeys passed against the real local H2 API, and all
13 rendering fixtures passed alongside lint and the frontend production build.
The staged backend baseline passed 17 tests, including the default-profile test
that is deleted only in the working copy. The active secret-scan hook passed.
The legacy L1 and L2-full checks also passed with isolated build output. The
session-cap check was skipped because its documented shared guard is absent.
The new keyboard, unknown-route, and return-to-projects tests reproduced their
failures before the fixes. The local Compose configuration and CI YAML validate.
Docker Desktop failed to start, so PostgreSQL-backed browser tests in the
updated CI job await execution.

The micro-commit patches, SOP messages, and ordered file manifest are saved
locally in `.git/codex-staging/2026-11-22/`. The four implementation commits
are `d40b7c8`, `b5577b3`, `d1d6189`, and
`6b263f8`. This summary is the fifth micro-commit. Remote CI
and deployment results were unverified when these commits were prepared.

Portfolio is Faisal Hanafi’s public, read-only engineering portfolio. It gives
recruiters a quick view of his work and gives technical reviewers details on the
problems, technologies, and trade-offs behind it. CREAMS, Auto-Recruit, and
Portfolio itself are the priority stories. The application also demonstrates how
Faisal connects a frontend, an API, and a database into a working product.
[Product requirements](../PORTFOLIO_PRD.md)

## Current implementation

| Area | What the checkout contains |
|---|---|
| Website | A responsive React and TypeScript single-page app built with Vite and styled with Tailwind CSS. The home page presents a business-value introduction (`01 / DATA & BI`, `02 / WORKFLOW SOFTWARE`, `03 / AUTOMATION & AI`), an interactive 5-chapter story and 4-step engineering process carousel (`About`), a dual-view (`Business & Impact View` vs. `+ Engineering Depth`) showcase of 5 real business systems (`Auto-Recruit`, `CREAMS`, `RinggitSense`, `BAiTI`, `Sen2Nal` — omitting the self-referential `Portfolio` entry), skills with a Cloud & BI Certifications strip, all 4 chronological career/academic experiences (`ASE Electronics`, `Accenture Technology Malaysia`, `AEM Enersol`, `IIUM`), and contact links (`faisalhanafi.dev@gmail.com`). React Router serves project details at `/projects/:slug`. |
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
- Candidate Intelligence Profile (`CIP-2026-11-11.2`) and business-first UX
  overhaul completed on 17 November 2026:
  - Added the missing `ASE Electronics (M) Sdn. Bhd.` industrial OJT (`Jul 2026 – Oct 2026`)
    alongside `Accenture Technology Malaysia` (including internal Power BI / Power Query / DAX),
    `AEM Enersol Sdn. Bhd.` (5,687-sheet Halliburton ETL, strikethrough semantics, WORA 7/WEQR),
    and `IIUM`, plus the Cloud & BI Certifications strip (`PL-300`, `DP-900`, `AZ-900`, `AWS CCP`).
  - Redesigned `AboutSection.tsx` into an interactive 5-chapter story and 4-step
    engineering process carousel (`role="tablist"` with keyboard/button navigation),
    removing the 5-paragraph wall of text and the 3 static academic stat boxes.
  - Reframed `ProjectsSection.tsx`, `FeaturedProject.tsx`, and `ProjectDetail.tsx`
    around three plain-English business questions (`01 / What This Project Is About`,
    `02 / What I Did`, `03 / Why People Care`) with a Dual-View Toggle
    (`Business & Impact View` vs. `+ Engineering Depth`), and removed `Portfolio`
    from the featured projects showcase.
  [November 2026 session record](02_SESSION_HISTORY/2026-11-16_ui_motion_and_credibility_polish.md)
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
