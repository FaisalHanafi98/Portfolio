---
name: react-ui-patterns
description: Use when writing React or Next.js components, hooks, or pages - waterfall elimination, bundle discipline, compound components, state placement; Next.js specifics in references/nextjs-patterns.md. Not for plain HTML/CSS pages.
---

# React UI Patterns

Apply when writing or reviewing React/Next.js code across RinggitSense, Auto-Recruit, and Portfolio frontends.

---

## CRITICAL: Waterfall Elimination

Sequential API calls are the #1 performance killer. Always parallelize.

```tsx
// BAD — sequential waterfall
const user = await fetchUser(id)
const posts = await fetchPosts(user.id)  // waits for user

// GOOD — parallel
const [user, posts] = await Promise.all([fetchUser(id), fetchPosts(id)])
```

For Next.js: fetch in Server Components and pass down as props — never fetch the same data in a child that the parent already fetched.

---

## Bundle Size

- Default to `dynamic()` for components >30KB or rarely visible (modals, rich editors, charts)
- Never import an entire library for one utility — use named imports
- Audit with `next build` bundle analyzer before shipping new heavy dependencies

```tsx
// BAD
import _ from 'lodash'
const result = _.groupBy(items, 'category')

// GOOD
import groupBy from 'lodash/groupBy'
// or just: items.reduce(...)
```

---

## Server vs Client Rendering

Default to Server Components. Move to Client only when you need:
- `useState` / `useReducer`
- `useEffect`
- Browser APIs (`window`, `document`)
- Event listeners

```tsx
// Mark the smallest possible boundary as client
'use client'
// Only the interactive leaf node, not the whole page
```

---

## Client-Side Data Fetching

Use SWR or React Query — never raw `useEffect` + `useState` for remote data.

```tsx
// BAD
useEffect(() => {
  fetch('/api/data').then(r => r.json()).then(setData)
}, [])

// GOOD (SWR)
const { data, error, isLoading } = useSWR('/api/data', fetcher)
```

Prefer server-side fetching in Next.js pages over client fetching when data is needed on first render.

---

## Component Architecture

### Kill Boolean Prop Sprawl

Boolean props that modify rendering are a design smell — they turn one component into many hidden components.

```tsx
// BAD — boolean proliferation
<Button primary large rounded icon={<Icon />} loading disabled />

// GOOD — explicit variants
<Button variant="primary" size="lg" shape="rounded" state="loading" />

// BEST for complex cases — compound components
<Select>
  <Select.Trigger>Choose an option</Select.Trigger>
  <Select.Content>
    <Select.Item value="a">Option A</Select.Item>
  </Select.Content>
</Select>
```

### Compound Components

Use when a group of components share implicit state and always appear together.

```tsx
// Shared state via context, not prop drilling
const TabContext = createContext<TabContextValue>(null)

function Tabs({ children, defaultTab }: TabsProps) {
  const [active, setActive] = useState(defaultTab)
  return (
    <TabContext.Provider value={{ active, setActive }}>
      {children}
    </TabContext.Provider>
  )
}

Tabs.List = TabList
Tabs.Tab = Tab
Tabs.Panel = TabPanel
```

### Context Providers

Keep context values stable — wrap object/array values in `useMemo`, callbacks in `useCallback`, or every render creates a new reference and triggers all consumers.

```tsx
// BAD — new object every render
<UserContext.Provider value={{ user, logout }}>

// GOOD
const value = useMemo(() => ({ user, logout }), [user, logout])
<UserContext.Provider value={value}>
```

---

## Memoization Rules

Only memoize when there's a measured performance problem. Premature memoization adds complexity for no gain.

**Use `useMemo` when:** expensive computation (>1ms) called on every render
**Use `useCallback` when:** function is a dependency of `useEffect` or passed to memoized child
**Use `React.memo` when:** component renders frequently with identical props (confirmed via profiler)

```tsx
// Memoize expensive derived data
const sortedItems = useMemo(
  () => items.slice().sort(compareByDate),
  [items]
)

// Stabilize callbacks passed to memo'd children
const handleSelect = useCallback((id: string) => {
  setSelected(id)
}, []) // no deps — setSelected is stable
```

---

## State Management

| State type | Use |
|-----------|-----|
| UI state (open/closed, active tab) | `useState` local to the component |
| Shared UI state (theme, sidebar) | Context + `useReducer` |
| Server data | SWR / React Query |
| Complex client state | Zustand (not Redux unless already in codebase) |
| URL-driven state (filters, pagination) | URL search params via router |

Don't lift state higher than necessary. Don't put server data in Redux/Zustand — that's what React Query is for.

---

## Error Boundaries

Wrap every major UI section. Never let one broken widget crash the whole page.

```tsx
// app/dashboard/layout.tsx
import { ErrorBoundary } from 'react-error-boundary'

export default function DashboardLayout({ children }) {
  return (
    <ErrorBoundary fallback={<DashboardError />}>
      {children}
    </ErrorBoundary>
  )
}
```

---

## Performance Checklist (before PR)

- [ ] No sequential awaits that could be `Promise.all`
- [ ] No raw `useEffect` fetching — using SWR/React Query
- [ ] Heavy components lazy-loaded with `dynamic()`
- [ ] No whole-library imports — named imports only
- [ ] Context values wrapped in `useMemo`
- [ ] `useMemo`/`useCallback` only where profiler shows a problem
- [ ] Error boundaries on major sections
- [ ] Server Components used by default, Client only when needed
