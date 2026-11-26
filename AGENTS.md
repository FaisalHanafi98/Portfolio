<!-- GENERATED from core@8907e10 - edit in core/, then: python core/tools/ai.py sync -->

# Portfolio-cleanup-20261124 - instructions for AI agents

Read the [workspace standards](../../standards/README.md) once before working.
Shared policies live there; project facts and exceptions follow below.
This shared mode requires the surrounding Development workspace.

Stack rules: [springboot](../../core/rules/stacks/springboot.md). Read when relevant.

Stack rules: [react](../../core/rules/stacks/react.md). Read when relevant.

## Project facts

Portfolio platform: a working full-stack product (Spring Boot + React) whose
job is proving real engineering capability. Credibility over hype; every
technical claim must be truthful and demonstrable. Roughly complete — changes
are maintenance and polish, not rebuilds.

### Audience

Non-technical recruiters (fast legitimacy signal), technical hiring managers
(evidence of depth), and Faisal as maintainer.

### Content priorities

Lead with CREAMS, Sen2Nal, and BAiTI. Preserve:
clear storytelling, professional presentation, truthful claims, maintainable
implementation.

### Engineering & UI Contracts

- **Design system lock:** [design.md](design.md) governs palette tokens, typography, motion, WCAG 2.2 AA / 44px touch targets, and `QueryContent` UX state-machine guarantees.
- **Ponytail (`lite` default):** invoke `/ponytail-review staged` before `code-reviewer` and `security-reviewer`. Non-strippable invariants: never cut input validation, Actuator/Prometheus privacy controls, bounded Micrometer route-template labels, degraded-DB timeout bounds, WCAG 2.2 AA accessibility attributes, or UX state-machine edge cases. Record any bounded deferral as `TODO(CR-XXX): <limit>, <upgrade trigger>` per `standards/DOCUMENTATION.md` rather than `shortcut:`.

### Operational truth rule (unchanged from the previous AGENTS.md)

Context files answer no operational questions. Runtime, deployment, CI/CD,
and session-workflow truth live in `docs/SOURCE_OF_TRUTH.md` and
`docs/PORTFOLIO_SESSION_SOURCE_OF_TRUTH.md` — follow those, and the
executable files they reference. The pre-2026-02 CLAUDE.md is archived at
`docs/archive/reference/CLAUDE_2026-02-18.md`; don't trust it.

## Synced skills

On-demand skills in `.claude/skills/`: springboot, react-ui-patterns, security-review, tdd, systematic-debugging, verification-before-completion, ponytail, ui-accessibility, frontend-design-aesthetics.
Trust their trigger descriptions; don't preload them.
