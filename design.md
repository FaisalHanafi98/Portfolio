# Portfolio Design System & UX Contract

> Locked visual, accessibility, and UX state-machine contract for `Portfolio-cleanup-20261124` (and `Portfolio`). Any future route or UI slice (`/status`, GitHub activity card, analytics views, or AI retrieval experiences) MUST preserve these tokens, typography, motion discipline, accessibility contracts, and request states alongside [docs/PORTFOLIO_PRD.md](docs/PORTFOLIO_PRD.md).

---

## 1. Visual Identity & Surface Palette ("Warm Cream Notebook")

- **Primary Canvas & Alternating Bands (`frontend/src/styles/globals.css`, `tailwind.config.js`):**
  - `#intro` (Hero): `bg-cream` (`#FFF8F0`)
  - `#about` (Story & Engineering Process): `bg-white/90` (`#FFFFFF`) bordered by `border-y-2 border-cream-darker/80`
  - `#projects` (Business Impact & Case Studies): `bg-cream` (`#FFF8F0`) bordered by `border-b-2 border-cream-darker/80`
  - `#skills` (Technical Stack & Certifications): `bg-cream-dark/70` (`#F5EDE3`) with `.notebook-lines` and `border-b-2 border-cream-darker/80`
  - `#experience` (Career & Education): `bg-white/90` (`#FFFFFF`) bordered by `border-b-2 border-cream-darker/80`
  - `#contact`: `bg-cream-dark/50`
- **Borders, Hairlines & Elevation:** `cream-darker` (`#EBE1D5`) with `border` or `border-2` around `bg-white` (`rounded-xl` / `rounded-2xl`) and subtle `shadow-sm`. Avoid heavy dark-mode elevation, neon glow, or glassmorphism.
- **Text Ink Contrast:**
  - Primary body & headings: `charcoal` (`#2D3436`)
  - Secondary text: `charcoal-light` (`#636E72`, verified >= 4.5:1 AA on `#FFF8F0` and `#FFFFFF`)
  - Decorative hairlines/scrollbars only: `charcoal-lighter` (`#B2BEC3` — NEVER use for readable text)
- **WCAG AA Accent Tokens (>= 4.5:1 on `#FFF8F0` & `#FFFFFF`):**
  - `blue`: `#0767B2` (`dark`: `#054F8A`)
  - `pink`: `#C0277A` (`dark`: `#9B1D62`)
  - `green`: `#007A62` (`dark`: `#005F4C`)
  - `purple`: `#5541D9` (`dark`: `#412FB5`)
  - `coral`: `#B84A32`
  - `yellow-dark`: `#9E6E08`
- **Token Hygiene & Colour Independence:**
  - Always use Tailwind semantic tokens from `frontend/tailwind.config.js`. Never hardcode hex values in `.tsx` components or reintroduce dark-theme tokens from legacy unused `src/design-tokens/tokens.json`.
  - Never communicate health, status, or error states via colour alone; pair colour badges with text labels, borders, and icons (`aria-hidden="true"` when paired with text).

---

## 2. Typography & Information Hierarchy

- **Font Families (`frontend/index.html`, `frontend/tailwind.config.js`):**
  - UI & prose: `font-sans` (`Inter`, system fallbacks)
  - Metadata, badges, metrics, timestamps & code: `font-mono` (`JetBrains Mono`, `Fira Code`, monospace)
  - Wordmark & skill category headings only: `font-hand` (`Caveat`) — NEVER use handwriting fonts for body copy, status telemetry, or numeric tables.
- **Type Scale & Rhythm:**
  - Hero Display (`h1`): `text-4xl sm:text-5xl md:text-6xl font-bold tracking-tight`
  - Section Headings (`h2`): `text-3xl md:text-4xl font-bold`
  - Body Prose: `1rem` (`leading-relaxed` / `1.5–1.75`)
  - Section Eyebrows / Status Pills: `font-mono text-xs uppercase tracking-widest`
- **Data & Operational Telemetry Rules (for `/status` and future views):**
  - Show explicit units, rolling observation windows (e.g., 5-minute window and insufficient-data thresholds), sample sizes, and snapshot staleness/age.
  - Provide tabular text/table fallbacks for charts; never hide essential metric values behind hover-only tooltips.

---

## 3. Motion & Reduced-Motion Contract (`frontend/src/lib/animations.ts`, `App.tsx`)

- **Global Reduced-Motion Enforcement:** Root `<App />` wraps routes in `<MotionConfig reducedMotion="user">`, backed by `@media (prefers-reduced-motion: reduce)` in `frontend/src/styles/globals.css` (`0.01ms` duration, `scroll-behavior: auto`).
- **Compositor-Only Transitions:** Animate only `transform`, `opacity`, and explicit `color`/`background-color`/`border-color`/`box-shadow` transitions with crisp `ease-out` timing (`[0.22, 1, 0.36, 1]`, `150–400ms`).
- **Explicit Bans:** Never introduce `transition-all` (note legacy unused `src/components/ui/Card.tsx` must not be imported without replacing `transition-all` and `bg-navy-light`), sluggish > 500ms entrances, infinite decorative loops, or unguarded `scale(0)` pops.

---

## 4. Accessibility Standards (WCAG 2.2 AA & 44px Interactive Targets)

- **Target Size:** Enforce minimum `44×44px` hit areas (`min-h-[44px]`, and `min-w-[44px]` on icon controls) for header navigation, mobile menu links, toggle groups, story/company selectors, cards CTA buttons, and retry controls.
- **Focus Indicators:** Visible `focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue` (`outline-offset-[-2px]` inside overflow-scrollable lists).
- **Reflow & Text Wrapping:** Zero horizontal overflow across `375px` mobile, `768px` tablet, `1280px` desktop, and `200%` zoom (`min-w-0` on flex/grid containers and `break-words` on dynamic strings, slugs, and lists).
- **Widget Interaction Contracts:**
  - `Navbar.tsx`: Mobile drawer locks page scrolling (`max-md:overflow-hidden`), marks closed container `inert`, closes on desktop breakpoint (`768px`) or link selection, and returns focus to toggle button on `Escape`.
  - `AboutSection.tsx`: Story chapter `role="tablist"` exposes a single tab stop (`tabIndex={0}` active, `-1` inactive), wraps selection + focus on `ArrowLeft`/`ArrowRight`, jumps on `Home`/`End`, and links `aria-controls`/`aria-labelledby` to a focusable `tabpanel` (`tabIndex={0}`).
  - `ExperienceSection.tsx`: Selects entries by stable numeric ID with fallback to the first entry if removed.

---

## 5. Mandatory UX State-Machine Guarantees (`QueryContent.tsx` & Router)

Every API-backed view (`/api/v1/projects`, `/projects/:slug`, `/skills`, `/experience`, and future `/status`) MUST distinguish and preserve all seven operational states:

1. **Initial Load In Progress:** `role="status"` screen-reader announcement (`Loading <resource>…`) + decorative `aria-hidden="true"` skeleton.
2. **Paused Offline:** Explicit status message (`Waiting for a connection to load/refresh <resource>.`), distinct from error or 404.
3. **Empty Success:** Explicit empty message (`No <resource> available.`) with zero misleading retry error copy.
4. **Initial Load Failure:** Clear failure state with an enabled `"Try again"` button (`aria-disabled` while fetching).
5. **Refresh Failure With Cached Data:** Retain last loaded content cleanly alongside a non-destructive retry banner (`Couldn't refresh <resource>. Showing the last loaded result.`).
6. **Confirmed HTTP 404 on `/projects/:slug`:** `"Project Not Found"` + `"Go Home"` link takes precedence over cached project data.
7. **Unknown Route & Anchor Navigation:** Unmatched routes render `NotFoundPage` (`"Page Not Found"` + `"Go Home"`); `/#projects` anchors scroll `#projects` into view on return; route changes without hash reset scroll to top cleanly.
