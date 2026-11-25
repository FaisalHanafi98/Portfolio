# Frontend React — Module Status

Source: frontend/src/ file list, client.ts, README.md
Last verified: 2026-05-10 (file list and client.ts read directly)
Confidence: HIGH for file structure; MEDIUM for component-level behavior (components not read individually)

---

## Build Configuration

| Property | Value | Confidence |
|---|---|---|
| Build tool | Vite | HIGH — README |
| Framework | React 18 | HIGH — README |
| Language | TypeScript (strict mode) | HIGH — README |
| Styling | Tailwind CSS | HIGH — README |
| Animation | Framer Motion | HIGH — README |
| Data fetching | React Query | HIGH — README |
| HTTP client | Axios (custom instance in api/client.ts) | HIGH — client.ts verified |

Build command: `npm run build` (in frontend/)
Output directory: `frontend/dist/` (index.html confirmed by L2-full gate check)
Dev server: `npm run dev` at http://localhost:5173
Type check command: `npx tsc --noEmit` (used in L1 gate)

---

## API Client Configuration (VERIFIED — client.ts)

Base URL selection:
- Production build (`import.meta.env.PROD = true`): `/api/v1` (relative — Nginx proxies /api/)
- Development build with `VITE_API_URL` set: uses that value
- Development build without `VITE_API_URL`: falls back to `/api/v1`

VITE_API_URL is intentionally unset in the GitHub Actions build step. The relative `/api/v1`
path works because Nginx proxies `/api/` to the Spring Boot backend (in Compose/VPS; Vite proxies during local dev).

Timeout: 10 seconds
Error handling: Axios response interceptor logs errors to console; rejects the promise.

Defined API methods in client.ts:
- `api.healthCheck()` → GET /api/v1/health
- `api.getProjects()` → GET /api/v1/projects
- `api.getProject(slug)` → GET /api/v1/projects/{slug}
- `api.getSkills()` → GET /api/v1/skills
- `api.getExperience()` → GET /api/v1/experience
- `api.getAbout()` → GET /api/v1/about (POSSIBLE DEFECT — no backend endpoint found)

---

## Frontend Component Structure (VERIFIED — file list)

### Pages

| File | Purpose |
|---|---|
| pages/HomePage.tsx | Main single-page layout |
| pages/ProjectPage.tsx | Individual project detail page |

### Sections

| File | Purpose |
|---|---|
| components/sections/HeroSection.tsx | Hero/landing section — rewritten 2026-05-01 |
| components/sections/AboutSection.tsx | About section — rewritten 2026-05-01 |
| components/sections/ProjectsSection.tsx | Projects list |
| components/sections/SkillsSection.tsx | Skills by category |
| components/sections/ExperienceSection.tsx | Work experience timeline |
| components/sections/ContactSection.tsx | Contact information |

### Project Components

| File | Purpose |
|---|---|
| components/projects/FeaturedProject.tsx | Featured project card (CREAMS, Auto-Recruit) |
| components/projects/ProjectCard.tsx | Standard project card |
| components/projects/ProjectDetail.tsx | Full project detail view |

### Layout

| File | Purpose |
|---|---|
| components/layout/Layout.tsx | Root layout wrapper |
| components/layout/Navbar.tsx | Navigation bar |
| components/layout/Footer.tsx | Footer |
| components/layout/SocialSidebar.tsx | Social links sidebar |
| components/layout/EmailSidebar.tsx | Email sidebar element |

### UI Primitives

| File | Purpose |
|---|---|
| components/ui/AnimatedSection.tsx | Scroll-triggered animation wrapper |
| components/ui/Button.tsx | Button component |
| components/ui/Card.tsx | Card component |
| components/ui/Skeleton.tsx | Loading skeleton |
| components/ui/SketchDivider.tsx | Decorative sketch-style divider |
| components/ui/SketchDoodles.tsx | Decorative sketch elements |
| components/ui/SketchUnderline.tsx | Sketch-style underline effect |
| components/ui/Tag.tsx | Technology/skill tag |

### Utilities

| File | Purpose |
|---|---|
| hooks/useScrollPosition.ts | Custom hook for scroll position |
| lib/animations.ts | Framer Motion animation variants |
| types/index.ts | TypeScript type definitions |
| api/hooks.ts | React Query hooks wrapping api/client.ts |

---

## Known Gaps

1. No Playwright or Cypress E2E tests (PLANNED — OPTIONAL_IMPROVEMENTS 4.2)
2. No Storybook (PLANNED — OPTIONAL_IMPROVEMENTS 4.1, lower priority)
3. No OG meta tags (PLANNED — OPTIONAL_IMPROVEMENTS 3.2)
4. No sitemap.xml or robots.txt (PLANNED — OPTIONAL_IMPROVEMENTS 3.4)
5. No contact form implementation (PLANNED — OPTIONAL_IMPROVEMENTS 5.2)
6. No browser verification evidence post 2026-05-01 changes
