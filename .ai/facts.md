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
