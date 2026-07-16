# Frontend Development Patterns

## Composition Over Inheritance

Build components through composition, not inheritance.

```tsx
// Compound component pattern — shared state via Context
const Tabs = ({ children, defaultTab }: TabsProps) => {
  const [active, setActive] = useState(defaultTab)
  return <TabsContext.Provider value={{ active, setActive }}>{children}</TabsContext.Provider>
}
Tabs.Tab = ({ id, children }: TabProps) => {
  const { active, setActive } = useTabsContext()
  return <button onClick={() => setActive(id)} aria-selected={active === id}>{children}</button>
}
Tabs.Panel = ({ id, children }: PanelProps) => {
  const { active } = useTabsContext()
  return active === id ? <div>{children}</div> : null
}

// Usage
<Tabs defaultTab="overview">
  <Tabs.Tab id="overview">Overview</Tabs.Tab>
  <Tabs.Panel id="overview"><OverviewContent /></Tabs.Panel>
</Tabs>
```

## Custom Hooks — Reusable Logic

```tsx
// Data fetching hook
function usePatients(filters?: PatientFilters) {
  const [data, setData] = useState<Patient[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<Error | null>(null)

  useEffect(() => {
    setLoading(true)
    fetchPatients(filters)
      .then(setData)
      .catch(setError)
      .finally(() => setLoading(false))
  }, [JSON.stringify(filters)])  // stable dep

  return { data, loading, error }
}

// Utility hooks
function useDebounce<T>(value: T, delay: number): T {
  const [debounced, setDebounced] = useState(value)
  useEffect(() => {
    const timer = setTimeout(() => setDebounced(value), delay)
    return () => clearTimeout(timer)
  }, [value, delay])
  return debounced
}
```

## Performance: Memoization

```tsx
// useMemo — expensive calculations
const filteredPatients = useMemo(
  () => patients.filter(p => p.status === activeFilter),
  [patients, activeFilter]
)

// useCallback — stable function references for child props
const handlePatientUpdate = useCallback(
  (id: string, data: Partial<Patient>) => updatePatient(id, data),
  [updatePatient]
)

// React.memo — prevent unnecessary re-renders
const PatientCard = React.memo(({ patient }: { patient: Patient }) => (
  <div>{patient.name}</div>
))
```

## Code Splitting

```tsx
// Lazy load heavy components
const AssessmentChart = lazy(() => import('./AssessmentChart'))
const ReportExport = lazy(() => import('./ReportExport'))

function Dashboard() {
  return (
    <Suspense fallback={<Skeleton />}>
      <AssessmentChart />
    </Suspense>
  )
}
```

## Error Boundaries

```tsx
class ErrorBoundary extends Component<{ fallback: ReactNode }, { hasError: boolean }> {
  state = { hasError: false }

  static getDerivedStateFromError(): { hasError: boolean } {
    return { hasError: true }
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error('Component error:', error, info)
  }

  render() {
    return this.state.hasError ? this.props.fallback : this.props.children
  }
}

// Usage
<ErrorBoundary fallback={<ErrorMessage />}>
  <PatientDashboard />
</ErrorBoundary>
```

## State Management (Context + useReducer)

```tsx
// For complex state that multiple components share
type Action = { type: 'SET_FILTER'; filter: string } | { type: 'RESET' }

function patientReducer(state: PatientState, action: Action): PatientState {
  switch (action.type) {
    case 'SET_FILTER': return { ...state, filter: action.filter }
    case 'RESET': return initialState
    default: return state
  }
}

const PatientContext = createContext<{ state: PatientState; dispatch: Dispatch<Action> } | null>(null)

export function PatientProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(patientReducer, initialState)
  return <PatientContext.Provider value={{ state, dispatch }}>{children}</PatientContext.Provider>
}
```

## Accessibility

```tsx
// Keyboard navigation
<button
  onKeyDown={(e) => e.key === 'Enter' && handleAction()}
  aria-label="View patient details"
  role="button"
  tabIndex={0}
>
  View Details
</button>

// Focus management after modal close
const triggerRef = useRef<HTMLButtonElement>(null)
function handleModalClose() {
  setIsOpen(false)
  triggerRef.current?.focus()  // Return focus to trigger
}
```

## Anti-Patterns to Avoid

```tsx
// BAD: Unstable useEffect dependency
useEffect(() => {
  fetchData(filters)
}, [filters])  // filters is an object — new ref every render

// GOOD: Stable dependency
useEffect(() => {
  fetchData(filters)
}, [filters.status, filters.page])  // primitives are stable

// BAD: Missing key prop
{patients.map(p => <PatientCard patient={p} />)}

// GOOD
{patients.map(p => <PatientCard key={p.id} patient={p} />)}

// BAD: Prop drilling 4+ levels
<A filter={filter}><B filter={filter}><C filter={filter}/></B></A>

// GOOD: Context for shared state
<FilterContext.Provider value={filter}>...</FilterContext.Provider>
```
