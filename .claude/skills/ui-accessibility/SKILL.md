---
name: ui-accessibility
description: Apply to any UI work - components, forms, navigation, widgets: WCAG 2.1 contrast, keyboard navigation, ARIA, touch targets, focus states. Accessibility wins over aesthetics on conflict.
---

# UI Accessibility

Apply to all frontend code across RinggitSense, Auto-Recruit, and Portfolio. Accessibility is **priority 1** — implement before aesthetic decisions.

---

## Priority 1 — Accessibility (WCAG 2.1)

### Contrast

- Body text: minimum **4.5:1** contrast ratio (light and dark modes independently)
- Large text (18px+ regular or 14px+ bold): minimum **3:1**
- UI components and focus indicators: minimum **3:1**
- Test both light and dark themes — a color that passes in light mode often fails in dark

```css
/* Semantic color tokens enforce this — never hardcode hex for text */
color: var(--color-text-primary);      /* not #6b7280 */
background: var(--color-surface);
```

### Keyboard Navigation

Every interactive element must be reachable and operable by keyboard alone.

- Tab order must match visual reading order
- Focus must never be trapped (except intentional modal trapping with Escape to exit)
- Custom components (dropdowns, date pickers, sliders) must implement full ARIA keyboard patterns

```tsx
// Keyboard trap inside modal — intentional, with escape
useEffect(() => {
  const handleKeyDown = (e: KeyboardEvent) => {
    if (e.key === 'Escape') onClose()
    if (e.key === 'Tab') trapFocus(e, modalRef)
  }
  document.addEventListener('keydown', handleKeyDown)
  return () => document.removeEventListener('keydown', handleKeyDown)
}, [onClose])
```

### ARIA Labels

```tsx
// Every interactive element needs an accessible name
<button aria-label="Close dialog">
  <XIcon />  {/* icon-only button */}
</button>

// Use aria-describedby for helper text
<input
  id="email"
  aria-describedby="email-hint email-error"
/>
<p id="email-hint">We'll never share your email.</p>
<p id="email-error" role="alert">{error}</p>

// Live regions for dynamic content
<div aria-live="polite" aria-atomic="true">
  {statusMessage}
</div>
```

### Focus States

Never `outline: none` without a replacement. Every focused element must be visually distinct.

```css
/* Global focus style — applies everywhere */
:focus-visible {
  outline: 2px solid var(--color-focus);
  outline-offset: 2px;
}

/* Remove only for mouse users (not keyboard) */
:focus:not(:focus-visible) {
  outline: none;
}
```

### Semantic HTML

Use the right element. ARIA is a fallback when HTML semantics aren't enough — not a replacement.

```tsx
// BAD — div soup
<div class="btn" onClick={submit}>Submit</div>

// GOOD — semantic
<button type="submit">Submit</button>
```

| Use | Instead of |
|-----|-----------|
| `<button>` | `<div onClick>` |
| `<nav>` | `<div class="nav">` |
| `<main>`, `<header>`, `<footer>` | `<div id="main">` |
| `<label htmlFor>` | `<div class="label">` |
| `<ul>/<ol>` | `<div class="list">` |
| `<table>` with `scope` | `<div class="grid">` for tabular data |

---

## Priority 2 — Touch & Interaction

### Touch Targets

- Minimum **44×44px** for all interactive elements (buttons, links, checkboxes, toggles)
- Minimum **8px** spacing between adjacent targets
- Use `padding` to expand hit area without changing visual size

```css
/* Small icon button — expand hit area with padding */
.icon-btn {
  width: 20px;
  height: 20px;
  padding: 12px;  /* 20 + 24 = 44px total */
}
```

### Loading & Feedback

- Provide visual feedback within **80-150ms** of any user interaction
- Show skeleton screens for content loading, not just spinners
- Disable + show loading state on submit buttons during async operations — prevent double-submit

```tsx
<button
  type="submit"
  disabled={isSubmitting}
  aria-busy={isSubmitting}
>
  {isSubmitting ? <Spinner size="sm" /> : 'Save changes'}
</button>
```

---

## Forms

- Every input must have a visible `<label>` — not just placeholder text (placeholder disappears on focus)
- Place error messages immediately after the input they describe
- Use `required` and `aria-required` consistently
- Group related inputs with `<fieldset>` + `<legend>`

```tsx
<fieldset>
  <legend>Billing address</legend>
  <div>
    <label htmlFor="street">Street address</label>
    <input id="street" name="street" required aria-required="true" />
    {errors.street && (
      <p role="alert" id="street-error">{errors.street}</p>
    )}
  </div>
</fieldset>
```

---

## Screen Reader Considerations

- Screen reader focus order must match the visual order — CSS `order` and `flex-direction: row-reverse` break this
- Icons that convey meaning need `aria-label` or visually-hidden text; decorative icons get `aria-hidden="true"`
- Modals must move focus to the first focusable element on open; return focus to the trigger on close

```tsx
// Visually hidden but accessible
const VisuallyHidden = ({ children }) => (
  <span className="sr-only">{children}</span>
)

// Usage
<button>
  <TrashIcon aria-hidden="true" />
  <VisuallyHidden>Delete item</VisuallyHidden>
</button>
```

---

## Pre-Delivery Checklist

Run before every PR that touches UI:

**Accessibility**
- [ ] 4.5:1 contrast in both light and dark modes
- [ ] All interactive elements reachable by Tab
- [ ] No `outline: none` without `:focus-visible` replacement
- [ ] All icon-only buttons have `aria-label`
- [ ] Dynamic content uses `aria-live` regions
- [ ] Semantic HTML — no div/span where a proper element exists

**Touch & Interaction**
- [ ] All touch targets ≥ 44×44px
- [ ] 8px minimum spacing between adjacent targets
- [ ] Loading feedback within 80-150ms
- [ ] Submit buttons disabled + `aria-busy` during async ops

**Forms**
- [ ] Every input has a visible `<label>` (not just placeholder)
- [ ] Error messages placed immediately after their input
- [ ] `required` and `aria-required` used consistently

**Screen Readers**
- [ ] Visual order matches DOM order
- [ ] Decorative icons have `aria-hidden="true"`
- [ ] Modals trap and restore focus correctly
- [ ] Tested with keyboard only (no mouse)
