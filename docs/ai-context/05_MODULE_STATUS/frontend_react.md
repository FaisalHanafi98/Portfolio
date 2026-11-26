# Frontend React — Module Status

Source: `frontend/src/` file list, `client.ts`, `App.tsx`, `StatusPage.tsx`, `README.md`
Last verified: 2026-11-27
Confidence: HIGH (`type-check`, ESLint `--max-warnings 0`, `14/14` SSR fixture assertions, and `12/12` Chrome Playwright E2E tests verified)

---

## Build Configuration

| Property | Value | Confidence |
|---|---|---|
| Build tool | Vite | HIGH — build verified |
| Framework | React 18 | HIGH — package verified |
| Language | TypeScript (strict mode) | HIGH — `tsc --noEmit` verified |
| Styling | Tailwind CSS | HIGH — `design.md` tokens verified |
| Animation | Framer Motion (`<MotionConfig reducedMotion="user">`) | HIGH — `App.tsx` verified |
| Data fetching | React Query + `QueryContent` state machine | HIGH — `client.ts` & SSR verified |
| HTTP client | Axios (custom instance in `api/client.ts`) | HIGH — `client.ts` verified |
| Tests | Node SSR (`tests/content-states.test.mjs`, 14 tests) + Playwright E2E (`tests/e2e/*.spec.ts`, 12 tests) | HIGH — locally executed |

Build command: `npm run build` (in `frontend/`)
Output directory: `frontend/dist/` (`index.html` confirmed by `L2-full` gate check)
Dev server: `npm run dev` at `http://localhost:5173`
Type check command: `npx tsc --noEmit` (used in `L1` gate)

---

## API Client Configuration (VERIFIED — `client.ts`)

Base URL selection:
- Production build (`import.meta.env.PROD = true`): `/api/v1` (relative — Nginx proxies `/api/`)
- Development build with `VITE_API_URL` set: uses that value
- Development build without `VITE_API_URL`: falls back to `/api/v1`

Defined API methods in `client.ts`:
- `api.healthCheck()` -> `GET /api/v1/health`
- `api.getStatus()` -> `GET /api/v1/status`
- `api.getProjects()` -> `GET /api/v1/projects`
- `api.getProject(slug)` -> `GET /api/v1/projects/{slug}`
- `api.getSkills()` -> `GET /api/v1/skills`
- `api.getExperience()` -> `GET /api/v1/experience`

---

## Frontend Component Structure (VERIFIED — file list)

### Pages

| File | Purpose |
|---|---|
| `pages/HomePage.tsx` | Main single-page portfolio layout |
| `pages/ProjectPage.tsx` | Lazy-loaded individual project case-study page |
| `pages/StatusPage.tsx` | Lazy-loaded public `/status` telemetry & GitHub sync view |
| `pages/NotFoundPage.tsx` | Unmatched-route recovery page |

### Sections

| File | Purpose |
|---|---|
| `components/sections/HeroSection.tsx` | Hero/landing section |
| `components/sections/AboutSection.tsx` | Story chapter carousel (`tablist`/keyboard nav) |
| `components/sections/ProjectsSection.tsx` | Trilogy (`CREAMS`, `Sen2Nal`, `BAiTI`) + additional projects |
| `components/sections/SkillsSection.tsx` | Skills grouped by category |
| `components/sections/ExperienceSection.tsx` | Interactive career timeline |
| `components/sections/ContactSection.tsx` | Contact links & safe copy |

### UI Primitives & Layout

- `components/layout/`: `EmailSidebar.tsx`, `Footer.tsx`, `Layout.tsx`, `Navbar.tsx`, `SocialSidebar.tsx`
- `components/projects/`: `FeaturedProject.tsx`, `ProjectCard.tsx`, `ProjectDetail.tsx`
- `components/ui/`: `AnimatedSection.tsx`, `Button.tsx`, `Card.tsx`, `QueryContent.tsx`, `Skeleton.tsx`, `SketchDivider.tsx`, `SketchDoodles.tsx`, `SketchUnderline.tsx`, `Tag.tsx`

---

## Known Gaps (see `design.md` and `PORTFOLIO_ROADMAP.md`)

1. No Storybook, OG meta tags, or `sitemap.xml`/`robots.txt` yet (`OPTIONAL_IMPROVEMENTS`).
2. Compose PostgreSQL/Nginx E2E smoke test runs in CI (`ci.yml`), while local verification runs Chrome against Vite + H2 `dev` runtime while Docker Desktop remains KIV.
