<!-- GENERATED from core@3b953ef - edit in core/, then: python core/tools/ai.py sync -->

# Portfolio - instructions for AI agents

# Constraints

These are the rules that apply to every session. Everything procedural lives in
skills and loads on demand — this file stays small on purpose.

## Git

- Never force-push, hard-reset, or delete branches without an explicit request.
- Never skip hooks (`--no-verify`). If a hook blocks you, fix the cause.
- Stage specific files; no blanket `git add -A` in repos with unrelated changes.
- Commit format: `type(scope): description` — feat, fix, refactor, docs, test, chore.
- Commit when a discrete task completes. Ask before pushing to main/master/dev.

## Secrets and data

- No secrets, credentials, or `.env` files in any commit, ever. The pre-commit
  secret scan is a backstop, not permission to be careless.
- PDPA: no real personal data in code, tests, or fixtures. CREAMS and
  RinggitSense are sensitive; use synthetic data.
- Financial advice output (RinggitSense, Sen2Nal) carries the mandatory
  disclaimers defined in each project's facts.

## Engineering

- Behavior changes need a test that fails first. Run the verification command
  and read its output before claiming anything works.
- Prefer built-in tools and CLIs over MCP when an equivalent exists.
- Keep diffs scoped to the task. No opportunistic refactors or drive-by fixes;
  flag them instead.
- Don't invent facts — especially Malaysian regulations, bank formats, or
  statistics. Say what you don't know.

## Escalation

- Ask before anything destructive, schema-changing, or production-affecting.
- When a task turns out bigger than classified, say so before continuing.

## Spring Boot / Java

- Constructor injection only; no field @Autowired. Service layer owns
  transactions (@Transactional at service, not controller or repository).
- DTOs at API boundaries — never expose entities. Bean Validation on input.
- Tests: JUnit 5 + Mockito for units, MockMvc for web slices, Testcontainers
  for repository tests against real PostgreSQL.
- Depth: the `springboot` skill (patterns, security, tdd, verification
  references).

## React / TypeScript

- React 18 + TypeScript, strict mode. Server data through a query library
  (SWR/React Query) — never raw `useEffect` fetching.
- Parallelize independent fetches; no request waterfalls.
- Accessibility before aesthetics: labels, keyboard nav, 4.5:1 contrast,
  44px touch targets. The `ui-accessibility` skill has the full checklist.
- Semantic color tokens, no hardcoded hex in components.
- Depth: `react-ui-patterns` and `frontend-design-aesthetics` skills.

## Project facts

Portfolio platform: a working full-stack product (Spring Boot + React) whose
job is proving real engineering capability. Credibility over hype; every
technical claim must be truthful and demonstrable. Roughly complete — changes
are maintenance and polish, not rebuilds.

### Audience

Non-technical recruiters (fast legitimacy signal), technical hiring managers
(evidence of depth), and Faisal as maintainer.

### Content priorities

Represent CREAMS, Auto-Recruit, and this portfolio itself well. Preserve:
clear storytelling, professional presentation, truthful claims, maintainable
implementation.

### Operational truth rule (unchanged from the previous AGENTS.md)

Context files answer no operational questions. Runtime, deployment, CI/CD,
and session-workflow truth live in `docs/SOURCE_OF_TRUTH.md` and
`docs/PORTFOLIO_SESSION_SOURCE_OF_TRUTH.md` — follow those, and the
executable files they reference. The pre-2026-02 CLAUDE.md is archived at
`docs/archive/reference/CLAUDE_2026-02-18.md`; don't trust it.

## Synced skills

On-demand skills in `.claude/skills/`: springboot, react-ui-patterns, ui-accessibility, frontend-design-aesthetics, tdd, code-commenting, verification-before-completion.
Trust their trigger descriptions; don't preload them.
