---
name: springboot
description: Use for any Spring Boot/Java work (Portfolio) - architecture patterns, security, JUnit 5/Mockito/Testcontainers TDD, and verification checklists. Not for plain Java without Spring.
---

# Spring Boot

One skill, four depths. Load the reference that matches the work:

| Doing | Read |
|---|---|
| Services, repositories, entities, config | [references/patterns.md](references/patterns.md) |
| Spring Security, validation, secrets | [references/security.md](references/security.md) |
| Writing tests or working test-first | [references/tdd.md](references/tdd.md) |
| Pre-PR / pre-deploy checks | [references/verification.md](references/verification.md) |

Iron rules regardless of reference:

- Constructor injection only. Transactions live at the service layer.
- DTOs at API boundaries; entities never leave the service layer.
- Repository tests run against real PostgreSQL via Testcontainers, not H2.
