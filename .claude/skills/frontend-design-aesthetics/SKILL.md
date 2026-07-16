---
name: frontend-design-aesthetics
description: Use when designing new pages or choosing visual direction so the UI does not look AI-generated - typography, color, motion, layout. Apply after ui-accessibility constraints, never instead of them.
---

# Frontend Design Aesthetics

Apply when designing new pages, building UI components, or choosing visual direction. Works after ui-accessibility — accessibility constraints come first, then aesthetics within those constraints.

---

## The Root Problem

Claude defaults to the statistical median of web training data. That median is:
- **Inter** font
- **#3B82F6** blue or purple gradients on white
- `rounded-lg shadow-sm` white cards
- Hero → feature grid → CTA → footer
- Sidebar → content area → card grid

This is what "AI slop" looks like. Every instruction in this skill exists to override these defaults.

---

## Design Thinking — Before Writing Any UI Code

Answer these four questions. Be specific. Vague answers produce generic UI.

1. **Purpose** — What problem does this page/component solve? Who uses it and in what context?
2. **Tone** — Pick a direction and commit to it. Options:
   - Brutally minimal / Refined restraint
   - Maximalist / Rich and layered
   - Retro-futuristic / Sci-fi terminal
   - Organic / Natural and warm
   - Luxury / High-end editorial
   - Playful / Toy-like and tactile
   - Editorial / Magazine and typographic
   - Brutalist / Raw and confrontational
   - Art deco / Geometric and structured
   - Soft / Pastel and gentle
   - Industrial / Utilitarian and dense
   - Dark / Moody and atmospheric
   - Lo-fi / Zine and handcrafted
3. **Constraints** — Brand colors? Existing design system? Dark mode required? Accessibility requirements?
4. **Differentiation** — What makes this unforgettable? What is the one thing a user will remember?

**CRITICAL**: Choose a clear conceptual direction and execute it with precision. Bold maximalism and refined minimalism both work — the key is intentionality, not intensity.

**Never settle on the first common choice that comes to mind. If a font, color, or layout feels obvious, deliberately explore alternatives.**

---

## Typography

Typography is the fastest signal of design quality or lack of it.

### Fonts to avoid (Claude defaults to these)

- Inter
- Roboto
- Arial
- Open Sans
- Lato
- Default system font stacks
- Space Grotesk (has become a new convergence point — avoid as a default)

### Font alternatives by aesthetic

| Aesthetic | Display / Heading | Body |
|-----------|------------------|------|
| Editorial | Playfair Display, Fraunces | Crimson Pro, Newsreader |
| Startup / Modern | Clash Display, Cabinet Grotesk | Satoshi, Bricolage Grotesque |
| Technical / Developer | IBM Plex Mono, JetBrains Mono | IBM Plex Sans, Source Sans 3 |
| Code / Terminal | Fira Code, VT323 | IBM Plex Mono |
| Retro / Arcade | Press Start 2P | VT323 |
| Refined / Luxury | Obviously | DM Serif Display |

**Pairing principle**: High contrast = interesting. Display + monospace, serif + geometric sans, variable weight extremes.

**Use weight extremes**: 100/200 vs 800/900 — not 400 vs 600. Size jumps of 3× or more, not 1.5×.

Load from Google Fonts. State the font choice before writing CSS.

---

## Color & Theme

Commit to a cohesive aesthetic. Never use timid, evenly-distributed palettes with no dominant color.

### Color anti-patterns

- **Generic SaaS blue**: `#3B82F6` (Tailwind blue-500) with no brand rationale
- **Purple gradients on white** — the single most common AI slop signature
- **Glass morphism** — `backdrop-filter: blur` + semi-transparent panels (Bencium explicitly bans this)
- Flat solid color backgrounds with no depth
- Three equally weighted brand colors competing for attention

### What to do instead

- **Dominant color + sharp accent** — 90% neutral, 10% brand, and the brand hits hard
- Draw palette inspiration from IDE themes, film color grades, cultural aesthetics, or a specific era
- CSS variables for every color — never hardcode hex in components

```css
/* Token structure — always this, never raw hex in components */
:root {
  --color-brand: #e63946;         /* dominant accent — used sparingly */
  --color-surface: #0a0a0f;       /* background */
  --color-surface-raised: #14141e;
  --color-text: #f1f5f9;
  --color-text-muted: #64748b;
  --color-border: #1e293b;
  --color-focus: var(--color-brand);
}
```

Palette directions — pick one, commit fully:
- **Bold/saturated**: high chroma, one dominant color, everything else near-neutral
- **Moody/restrained**: dark surfaces, muted accents, dramatic shadows
- **High-contrast/minimal**: black and white with a single accent that earns its place

---

## Spatial Composition

Generic AI layouts are predictable: centered content, equal margins, symmetric columns.

**Techniques that break the pattern:**

- **Asymmetry** — weighted layouts where one side is heavier by intent
- **Overlap** — elements that break their container boundaries
- **Diagonal flow** — content that doesn't follow a strict horizontal axis
- **Grid-breaking** — pull-quotes, images, or accents that escape the column
- **Generous negative space OR controlled density** — pick one, commit, never the middle ground
- **Z-depth** — elements at different perceived depths through shadow and scale
- **Full-bleed** — images or color blocks that reach the viewport edge
- **Dramatic scale jumps** — a 200px headline next to 13px metadata, not two similar sizes

---

## Backgrounds & Visual Details

Never default to a solid color background. Create atmosphere.

**Layering techniques:**

```css
/* Gradient mesh — context-specific, not decorative noise */
background:
  radial-gradient(ellipse at 20% 50%, rgba(120, 40, 200, 0.15) 0%, transparent 60%),
  radial-gradient(ellipse at 80% 20%, rgba(40, 100, 200, 0.1) 0%, transparent 50%),
  #0a0a0f;

/* Noise texture overlay — adds grain and depth */
.surface::after {
  content: '';
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,..."); /* SVG noise */
  opacity: 0.03;
  pointer-events: none;
}
```

**Other background approaches:**
- Geometric patterns (CSS-only: repeating-linear-gradient, conic-gradient)
- Layered transparencies with mix-blend-mode
- Dramatic shadows that imply depth
- Decorative borders and dividers that carry the aesthetic
- Custom cursors when the aesthetic warrants it
- CRT scanlines, grain overlays, print textures (match to the aesthetic — not decorative noise)

---

## Motion & Animation

### Timing

- Micro-interactions (button press, toggle): **100-150ms**
- UI transitions (panel open, modal): **200-300ms**
- Page-level orchestration (staggered reveals): **300-600ms total**, staggered with `animation-delay`
- Easing: `ease-out` for entering, `ease-in` for exiting, `ease-in-out` for continuous

### What to animate

Only `transform` and `opacity` — GPU-accelerated, no layout thrash. Never animate `width`, `height`, `top`, `left`, `margin`, `padding`.

### Orchestration over scatter

One well-orchestrated page load creates more impact than 20 scattered micro-interactions.

```css
/* Staggered reveal — page loads with a sequence, not all at once */
.hero-title    { animation: fadeUp 400ms ease-out 0ms   both; }
.hero-subtitle { animation: fadeUp 400ms ease-out 80ms  both; }
.hero-cta      { animation: fadeUp 400ms ease-out 160ms both; }

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(16px); }
  to   { opacity: 1; transform: translateY(0); }
}
```

### Always respect reduced motion

```css
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

---

## UI/UX Pro Max Priorities (3-10)

### Priority 3 — Performance

- WebP or AVIF for all images — never PNG/JPG for web without reason
- Lazy-load below-the-fold: `loading="lazy"` on `<img>`
- Target CLS < 0.1 — always set `width` and `height` on images
- Preload LCP image: `<link rel="preload" as="image">`

### Priority 4 — Style Selection

Match visual style to the product and industry — not to what's trending.

| Product | Appropriate | Avoid |
|---------|-------------|-------|
| Finance (RinggitSense) | Clean minimalism, muted palette, information density | Glassmorphism, heavy gradients |
| Healthcare admin (CREAMS) | Flat, high contrast, clear hierarchy | Decorative flourishes |
| Enterprise (Auto-Recruit) | Professional, structured, consistent | Experimental, playful |
| Portfolio | Bold, distinctive, personality-forward | Generic Tailwind defaults |

Icons: Lucide React only. Never emoji as structural UI icons. Consistent family throughout.

### Priority 5 — Layout & Responsive

- Mobile-first: base styles for mobile, `min-width` queries to scale up
- No horizontal scroll on any viewport
- Spacing rhythm: 4px or 8px base unit (4, 8, 12, 16, 24, 32, 48, 64px)
- Max content width: 1280px centered, 16-24px horizontal padding on mobile

### Priority 6 — Typography & Color

- Body text minimum: **16px**
- Line height body: **1.5** | Headings: **1.2**
- Body text line length: **60-80 characters** — constrain with `max-width: 68ch`
- Semantic color tokens — never hardcode hex in components

### Priority 7 — Animation

Already covered above. Short version: 150-300ms, transform/opacity only, prefers-reduced-motion always respected.

### Priority 8 — Forms & Feedback

- Progressive disclosure for long forms — step-by-step, not all at once
- Validate on blur, not on change. Clear errors immediately on correction.
- Design all four states: empty, loading, error, success — not just the happy path.

### Priority 9 — Navigation

- Bottom nav (mobile): 3-5 items maximum
- Active state must be visually unambiguous — color change alone is not enough (add weight, indicator, or underline)
- Deep links must work — users share URLs

### Priority 10 — Charts & Data

- Always include a legend — never rely on color alone
- Label axes — never assume the user knows what Y means
- Accessible chart colors: use ColorBrewer palettes, avoid red/green pairs alone
- Tooltip on hover for precise values

For RinggitSense: show currency symbol and denomination; consistent decimal precision.

```tsx
const CHART_COLORS = ['#2563eb', '#d97706', '#059669', '#7c3aed', '#dc2626']
```

---

## Designed States — All Four, Always

AI slop reveals itself in the states that aren't the happy path. Design all four before a component is done.

| State | What to avoid | What to do |
|-------|--------------|-----------|
| **Empty** | "No data found." | Explain why it's empty + clear action to fill it |
| **Loading** | Generic spinner | Skeleton that matches the loaded layout exactly |
| **Error** | "Something went wrong." | Specific message + recovery action |
| **Success** | Silence after submit | Explicit confirmation with next action |

```tsx
// Empty state — not an afterthought
function EmptyTransactions() {
  return (
    <div className="text-center py-16">
      <ReceiptIcon className="mx-auto mb-4 text-neutral-400" size={40} />
      <h3 className="font-semibold text-neutral-900">No transactions yet</h3>
      <p className="text-neutral-500 mt-1">Add your first transaction to start tracking.</p>
      <Button variant="primary" className="mt-4">Add transaction</Button>
    </div>
  )
}
```

---

## AI Slop Anti-Pattern Checklist

Before shipping any UI, verify none of these made it in without deliberate reason:

**Fonts**
- [ ] Not using Inter, Roboto, Arial, Open Sans, Lato, or system fonts as primary
- [ ] Not defaulting to Space Grotesk just because it's "modern"

**Color**
- [ ] No purple-on-white gradient without a brand reason
- [ ] Not using Tailwind blue-500 (#3B82F6) as primary without rationale
- [ ] No glass morphism (backdrop-filter: blur on semi-transparent panels)
- [ ] Not three equally-weighted colors competing for attention

**Layout**
- [ ] Not symmetric equal-margin columns with no visual tension
- [ ] Not hero → feature-3-up → CTA → footer without questioning the structure
- [ ] Not sidebar → content → card-grid as default dashboard without thought

**States**
- [ ] Not "No data found." as empty state text
- [ ] Not a generic spinner as loading state
- [ ] Not "Something went wrong." as error text

**Icons & Assets**
- [ ] No emoji used as structural UI icons
- [ ] No mixed icon families in one project
- [ ] Images in WebP/AVIF, all with width + height + alt

**Motion**
- [ ] No scattered micro-interactions on every element
- [ ] No width/height/margin animations
- [ ] prefers-reduced-motion respected

---

## Visual Quality Checklist

Run before every PR that delivers a new page or significant UI change:

- [ ] Four design questions answered: purpose, tone, constraints, differentiation
- [ ] Tone committed to, not hovering between two directions
- [ ] Typography: distinctive choice, weight extremes used, 60-80 char line length
- [ ] Color: semantic tokens, dominant + accent structure, not timid palette
- [ ] Background: atmosphere created, not solid color default
- [ ] All four states designed: empty, loading, error, success
- [ ] Motion: orchestrated sequence, not scattered, reduced-motion respected
- [ ] Anti-slop checklist cleared
