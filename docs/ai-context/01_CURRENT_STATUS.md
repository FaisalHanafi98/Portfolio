# Portfolio project summary

**As of 27 November 2026**

## Branch review & documentation reconciliation (`550caa5..HEAD`) · 27 November 2026

Completed pre-review audit of `codex/portfolio-cleanup-20261124` in
`projects/Portfolio-cleanup-20261124` ahead of `origin/main` (`963df7d`), keeping
all commits local, `projects/Portfolio` (`63feb9e`) untouched, and `stash@{0}`
intact:

- Reconciled shared `.claude/skills/{react-ui-patterns,ui-accessibility}/SKILL.md`
  pointers via `ai.py sync ... --docs-only` (`ai.py doctor` now passes `[ok]`,
  `~681` always-on tokens).
- Removed trailing EOF blank line warning in `frontend/tests/content-states.test.mjs`
  (`git diff --check` clean).
- Added `/api/v1/status` JSON and `/status` SPA checks to the Compose smoke job
  in `.github/workflows/ci.yml`.
- Updated `docs/SOURCE_OF_TRUTH.md`, `docs/PORTFOLIO_ROADMAP.md`,
  `docs/PORTFOLIO_PRD.md`, `docs/ai-context/00_PROJECT_SOURCE_OF_TRUTH.md`,
  `docs/ai-context/05_MODULE_STATUS/{backend_api,deployment_ci_cd,frontend_react}.md`,
  and `README.md` so all active and module docs match the delivered code and checks.

## Server-side GitHub workflow & activity poller · 27 November 2026

Implemented the scheduled GitHub workflow and commit activity poller in
`projects/Portfolio-cleanup-20261124` on `codex/portfolio-cleanup-20261124`
(from `21497cd`), extending `GET /api/v1/status` and `StatusPage` (`/status`):

- **Backend poller (`GitHubStatusService`, `StatusProperties.GitHubProperties`, `StatusMetricsConfig`, `StatusSnapshotDto`):**
  - Polls public `/repos/{owner}/{repo}/actions/runs` and `/commits` on a
    scheduled background cycle using conditional requests (`ETag`/`If-None-Match`,
    `If-Modified-Since`), a 15-minute server cache (`cache-ttl-seconds: 900`),
    strict `4000ms` connect/read timeouts with `Redirect.NEVER`, and bounded
    exponential backoff (`60s` initial, `3600s` max, honoring bounded `Retry-After`
    / `X-RateLimit-Reset`).
  - Keeps GitHub credentials (`GITHUB_TOKEN`) and quota strictly server-side,
    omits committer emails, validates `https://github.com/` URLs, caps parsed
    payload size (`512 KB`) and items (`max-items: 5`), and isolates DB outages
    from GitHub outages cleanly.
  - Exposes dynamic `snapshotAgeSeconds` and distinguishes `healthy`,
    `degraded`, `stale`, `unavailable`, and `empty` states cleanly.
- **Frontend UI (`StatusPage`, `types/index.ts`, `Skeleton.tsx`):**
  - Renders `Repository CI & Activity Sync` with colour+text+icon badges across
    all 5 GitHub states, snapshot age, cache cadence, workflow runs, commits,
    `44px` verified external links, and an updated `Operational Signals Table`
    row.
- **Checks run after final edit:** Backend `./gradlew.bat test` (`36/36`),
  frontend `type-check`, `lint`, SSR `content-states.test.mjs` (`14/14`),
  `vite build`, and Chrome Playwright E2E (`12/12`, `PLAYWRIGHT_CHANNEL=chrome`
  against booted H2 `:8081`).
- **Not verified here:** Docker Compose PostgreSQL/Nginx rehearsal, live
  rate-limited traffic against public `api.github.com`, and remote CI workflows.

## Public `/status` frontend view · 26 November 2026

Implemented the public `/status` route (`StatusPage`) in
`projects/Portfolio-cleanup-20261124` on `codex/portfolio-cleanup-20261124`
(from `53f0c21`), consuming `GET /api/v1/status` through `useStatus` and
`QueryContent` under the `design.md` contract (`#faf8f5` cream tokens, WCAG AA
status labels, `44px` targets, non-focus-stealing manual refresh, explicit
JVM-process-uptime caveat, and the `Operational Signals Table` fallback).
Navigation links are exposed from `Navbar` (`Status`) and `Footer`
(`System Status`). Scheduled GitHub workflow polling remains deferred.

- **Verified locally after edits:** Frontend `type-check`, `lint`
  (`--max-warnings 0`), `build`, SSR fixture suite (`14/14` in
  `frontend/tests/content-states.test.mjs`), and full Chrome Playwright E2E
  suite (`11/11`, `PLAYWRIGHT_CHANNEL=chrome` against booted H2 `:8081`).
- **Not verified here:** Docker Compose Postgres/Nginx rehearsal and remote CI
  workflows.

## Bounded `GET /api/v1/status` backend snapshot · 26 November 2026

Implemented the next roadmap slice in `projects/Portfolio-cleanup-20261124`
on `codex/portfolio-cleanup-20261124` (from `a82e2aa`): a narrow, read-only,
non-cached (`Cache-Control: no-store`) public operational snapshot at
`GET /api/v1/status`. The frontend `/status` route and scheduled GitHub
workflow polling remain separate future slices.

- **Status contract (`com.faisal.portfolio.status`):**
  - Exposes JVM process uptime (`uptimeSeconds`, `startedAt`), resolved revision
    (`portfolio.status.revision` / `APP_REVISION` -> `GitProperties` ->
    `BuildProperties`, defaulting to `local-dev` in `dev` and `unconfigured` when
    unset), coarse DB probe state (`up` / `degraded`, redacted `latencyMs`), and
    rolling 5-minute API traffic metrics (`windowSeconds`, `requestCount`,
    `minSamples`, `p95Status`, `p95Ms`).
  - `StatusService` bounds JDBC `Connection.isValid` probes via a 2-thread
    daemon executor, 2-permit concurrency guard, and `portfolio.status.db-timeout-ms`
    (`1000ms` default, clamped `50–5000ms`), returning `degraded` cleanly without
    leaking JDBC URLs, hostnames, or `SQLException` details.
  - `ApiMetricsFilter`, `ApiLatencyTracker`, and `StatusMetricsConfig` cap
    Micrometer scrape cardinality (`maximumAllowableTags` 16, allowlisted
    `/api/v1/*` templates + `OTHER`, bounded SLO buckets, 5-minute rolling expiry)
    and return `p95Status: "insufficient_data"` (`p95Ms: null`) below
    `portfolio.status.min-samples` (`20` default).
- **Verification recorded:**
  - `./gradlew.bat test --no-daemon --rerun-tasks` (Java 21): `29/29` tests
    passed (`StatusControllerTest` `2/2`, `ApiLatencyTrackerTest` `2/2`,
    `StatusDegradedDbTest` `2/2`, `EnterpriseBoundaryArchitectureTest` `4/4`,
    `DefaultDevProfileIntegrationTest` `3/3`, `ProjectControllerTest` `5/5`, and
    all remaining suites).
  - `.githooks/pre-commit` (`bash .hooks/pre-commit`, Unicode/bidi scan, `L1`, and
    `L2-full` verifying live `GET /api/v1/status` against H2 `:8081`) passed.
  - Docker Compose PostgreSQL verification and remote CI remain unexecuted here.

## Governance, skill admission & gate hardening · 26 November 2026

Completed in isolated linked worktree `projects/Portfolio-cleanup-20261124`
on `codex/portfolio-cleanup-20261124` (from `550caa5`), preserving
`projects/Portfolio` (`main` at `63feb9e`) and `stash@{0}`
(`de1104ad832201d3abe12eac57706134680899cd`). Prepares the repository before
implementing the bounded read-only `GET /api/v1/status` backend snapshot.

- **SOP & artifact reconciliation:** Retired superseded
  `docs/COMMIT_MESSAGE_SOP.md` and obsolete root `backend.tar.gz`; reconciled
  stale Lightsail, Gradle-version, and session-guard references across
  `README.md`, `.github/workflows/ci.yml`, `.cursor/rules/core.mdc`,
  `docs/ai-context/00_PROJECT_SOURCE_OF_TRUTH.md`, and
  `docs/ai-context/05_MODULE_STATUS/{deployment_ci_cd,frontend_react}.md`.
  Kept `"mcp": []` with Playwright Test pinned at `1.65.1` in `frontend/package.json`.
- **Skill budget, `design.md` lock & pinned capabilities:**
  - Switched `.ai/manifest.json` to `context_mode: shared`, added `security-review`
    (`+ security-reviewer`), `systematic-debugging`, and `ponytail` (pinned in
    `Development/core/skills/ponytail/` @ `ebd8654c2c627f721eb8f9c0d1bd4090a29f9e10`, MIT),
    narrowed `ui-accessibility` and `frontend-design-aesthetics` to frontend UI
    turns (`core` @ `ef3cbe6` / `8907e10`), removed `code-commenting` and 12 stale copied
    `.claude/skills/*/references/` files, and excluded `impeccable`,
    `design-for-ai`, and `sloplint.mjs`.
  - Verified `diagram-design` (`b228c5d77794a84150fca41b22940bb316b1398d`, MIT
    with offline/privacy override) remains workspace-only outside Portfolio.
  - Added locked [design.md](../../design.md) and Portfolio `ponytail` (`lite`
    default, `/ponytail-review staged` before `code-reviewer`/`security-reviewer`,
    `TODO(CR-XXX):`, and Non-Strippable Invariants) in `.ai/facts.md`.
- **Pre-commit & deterministic gates:**
  - Extended shared [.githooks/pre-commit](../../.githooks/pre-commit) with staged
    index-blob scanning blocking Trojan-Source bidi controls (`U+202A–U+202E`,
    `U+2066–U+2069`, `U+200E`, `U+200F`, `U+061C`) across text blobs and
    invisible/exotic whitespace (`U+200B–U+200D`, `U+2060`, `U+FEFF`, `U+00A0`,
    `U+202F`, `U+205F`, `U+3000`) in code/config files before running `L1` and `L2-full`.
  - Locked `management.endpoints.web.exposure.include: health`, `show-details: never`,
    and `prometheus.access: none` in [application-prod.yml](../../backend/src/main/resources/application-prod.yml).
  - Added [EnterpriseBoundaryArchitectureTest.java](../../backend/src/test/java/com/faisal/portfolio/EnterpriseBoundaryArchitectureTest.java)
    (no `System.out`/`printStackTrace`/`@Autowired` in `src/main/`, DTO/Map boundary
    check on `@RestController`, `prod` lockdown assertion, and zero JDBC/stacktrace
    leak on simulated DB exceptions).
  - Enabled `'no-console': ['error', { allow: ['warn', 'error'] }]` in `frontend/.eslintrc.cjs`,
    wired `npm run lint` into `scripts/healthcheck_l1.sh`, and wired `npm test` into
    `scripts/healthcheck_l2_full.sh`.
- **Verification recorded:** Synthetic `.githooks/pre-commit` rejections (`U+202E` and
  `U+200B`), `ai.py doctor` (`[ok]`, `~681` tokens vs `2000` budget), Java 21 backend
  `./gradlew.bat test --no-daemon --rerun-tasks` (`23/23` tests passing), frontend
  `type-check` / `lint` (`--max-warnings 0`) / `test` (`13/13`) / `build`, and
  `bash scripts/healthcheck.sh --all` (`L1: PASS`, `L2 FULL: GREEN`) all passed.
  Docker Compose PostgreSQL rehearsal, remote CI execution, and Playwright E2E
  re-runs were not executed in this governance pass.

## Cleanup work order · 24 November 2026

1. Record the exact CI and Lightsail outcomes at `63feb9e`. Retire the
   automatic Lightsail workflow and preserve its historical revision.
2. Reconcile Git's configured hook with secret scanning and L1/L2 verification.
   Audit local edits and work from a clean linked checkout.
3. Verify the three case studies in their source projects. Update the seed,
   showcase and PRD. Confirm stable slugs and row identities, three ordered
   flagships and the existing loading, error and navigation behaviour.
4. Write the proposed 6–12 month roadmap. Start with a repeatable VPS release,
   then add measured operations, first-party analytics and grounded AI.

The cleanup runs in an isolated checkout at `63feb9e`. Tests use local fixture
data and an in-memory development database. Use individual SOP commits so each
cleanup can be reviewed and reverted independently.

### Cleanup evidence

- The exact [CI run at 63feb9e](https://github.com/FaisalHanafi98/Portfolio/actions/runs/44970309452)
  passed Backend, Frontend and Compose. The separate [Lightsail run](https://github.com/FaisalHanafi98/Portfolio/actions/runs/44970309509)
  failed at its deploy step. The workflow removal landed at `31a63b0` and the
  push scheduled only CI.
- The checkout uses the project hook for secret scanning and L1/L2-full.
  The historical orchestrator session cap is retired. See the
  [session guide](../PORTFOLIO_SESSION_SOURCE_OF_TRUTH.md).
- The original checkout audit found 28 tracked edits or deletions and 69
  untracked files. The untracked files comprise 47 detached-worktree files,
  five archived files, six deployment drafts, three local settings or
  environment files, three candidate images and five tooling or note files.
  The implementation was isolated in a clean linked checkout, preserving the
  original files and the working-copy deletion of the backend test.
- The flagship evidence was checked against CREAMS at `5592ab5`, Sen2Nal at
  `790bd17`, and BAiTI at `56eb320`. The case studies distinguish verified
  local behaviour from features still in development.
  [Flagship scope](../PORTFOLIO_PRD.md#flagship-case-studies)

[Proposed 6–12 month roadmap](../PORTFOLIO_ROADMAP.md)

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
problems, technologies, and trade-offs behind it. CREAMS, Sen2Nal, and BAiTI are the priority stories. The application also demonstrates how
Faisal connects a frontend, an API, and a database into a working product.
[Product requirements](../PORTFOLIO_PRD.md)

## Current implementation

| Area | What the checkout contains |
|---|---|
| Website | A responsive React and TypeScript single-page app built with Vite and styled with Tailwind CSS. The home page presents a business-value introduction (`01 / DATA & BI`, `02 / WORKFLOW SOFTWARE`, `03 / AUTOMATION & AI`), an interactive 5-chapter story and 4-step engineering process carousel (`About`), a dual-view showcase of CREAMS, Sen2Nal, and BAiTI, plus an Additional projects section for Auto-Recruit and RinggitSense, skills with a Cloud & BI Certifications strip, all 4 chronological career/academic experiences (`ASE Electronics`, `Accenture Technology Malaysia`, `AEM Enersol`, `IIUM`), and contact links (`faisalhanafi.dev@gmail.com`). React Router serves project details at `/projects/:slug`. |
| API and data | Java 21 and Spring Boot 3.4 expose versioned, read-only JSON endpoints for health, project summaries and details, skill categories, and experience. Spring Data JPA reads the stored content. The application seeds an empty database and refreshes the three flagship projects in place. |
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
  for a replacement VPS. The retired deployment workflow is preserved in Git history. The replacement
  provider remains open.
  [Owner-reviewed August session](02_SESSION_HISTORY/2026-08-23_lightsail_decommission_and_streamlining.md) ·
  [Failed Lightsail run](https://github.com/FaisalHanafi98/Portfolio/actions/runs/44970309509)

## Proposed changes and open decisions

| Item | Status and next evidence |
|---|---|
| Release verification | CI validated the PostgreSQL Compose and browser journeys at `63feb9e`. Carry the same checks into the first VPS release. |
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
