# Portfolio Platform - Product Requirements Document

> Runtime, deployment, CI/CD, ports, profiles, and workflow enforcement do not live in this document.
> For operational truth, start with `docs/SOURCE_OF_TRUTH.md`.

**Document Version:** 2.0  
**Author:** Mohamad Faisal Bin Mohd Hanafi  
**Status:** Active product document

## 1. Executive Summary

The Portfolio Platform is a full-stack product used to present Faisal's engineering capability through a real backend, a polished frontend, and truthful project storytelling. Its job is not only to display content, but to act as proof that the product owner can design, build, and maintain software with production-minded discipline.

## 2. Problem Statement

Faisal has multiple strong projects and relevant professional experience, but without a unified product those signals are fragmented across resumes, GitHub repositories, and ad hoc explanations. A static portfolio is not enough. The product must present work clearly for recruiters, offer technical depth for engineering reviewers, and remain maintainable as content evolves.

## 3. Solution Overview

The product is a portfolio application with:

- a backend that exposes structured portfolio content through public JSON endpoints
- a frontend that renders the content with a professional and responsive presentation
- seeded content that demonstrates real projects, skills, and experience
- a UX direction that balances clarity for non-technical readers with depth for technical readers

The portfolio itself is part of the proof-of-capability. It should demonstrate engineering credibility without overstating the implementation.

## 4. Target Audience

### Non-Technical Recruiters

Need a fast signal that Faisal is credible, professional, and relevant for software roles.

### Technical Hiring Managers

Need enough substance to believe the code, architecture choices, and project narratives are worth deeper review.

### Faisal

Needs a maintainable portfolio that can be updated honestly as experience, projects, and positioning evolve.

## 5. Success Metrics

The product is successful when it achieves the following outcomes:

- recruiters can understand Faisal's role and strengths quickly
- technical reviewers can identify meaningful engineering depth
- project narratives are clear, accurate, and specific
- the interface is responsive, polished, and professional
- the content remains easy to maintain without rewriting large parts of the product

## 6. Current Product Stage

The product is already beyond initial scaffolding.

Current reality:

- backend exists and serves portfolio content through public endpoints
- frontend exists and consumes the backend
- a gated local workflow exists for structural and runtime checks
- GitHub CI verifies the stack. The former Lightsail deployment is retired

The current stage is focused on:

- hardening correctness
- preserving truthful documentation
- improving UX polish
- maintaining alignment between product intent and executable reality

## 7. Current Capabilities

### Content Domains

The product currently represents:

- projects
- skills
- experience
- a lightweight public health capability

### Product Requirements

The active product must:

- surface flagship work clearly, in this order: CREAMS, Sen2Nal, and BAiTI
- provide a project listing and project detail experience
- present skills in a grouped and readable format
- present work experience as a clear timeline or equivalent structure
- support responsive usage across mobile and desktop layouts
- maintain a visual standard appropriate for recruiters and engineering managers

### Flagship case studies

| Case study | What the portfolio can demonstrate | Evidence boundary |
|---|---|---|
| CREAMS | Laravel 12, Blade, Bootstrap and MySQL support rehabilitation operations. CentreScope and relationship scopes apply centre ownership. Private document routes and role checks handle sensitive materials. | Centre filtering is enforced through Laravel. Documented administrator and execution paths have different scope rules. |
| Sen2Nal | Python, FastAPI, Pydantic, PostgreSQL and React support staged market research. Run status, cutoff eligibility and a hashed replay manifest make evidence traceable. | VADER and the replay contract have local/offline evidence. FinBERT runtime, provider rights and live replay publication remain open. Research only. Not financial advice. |
| BAiTI | Java 21, Spring Boot, PostgreSQL 16, Flyway and React support a shared-schema property workspace. Separate roles and forced row-level security constrain tenant access. | Authentication and property flows have local PostgreSQL and browser evidence. Procurement, reserve planning, policy assistance and hosting are later work. |

Auto-Recruit and RinggitSense retain their existing detail routes under
Additional projects. The seed refreshes the three named case studies in place
and retains their row identities. Existing secondary descriptions and media
survive a refresh.

### Public API Capabilities

The product requires public API support for:

- `GET /api/v1/health`
- `GET /api/v1/projects`
- `GET /api/v1/projects/{slug}`
- `GET /api/v1/skills`
- `GET /api/v1/experience`

These endpoints exist to support the portfolio product. This PRD defines their product purpose, not their runtime host, port, or deployment behavior.

## 8. Current Product Gaps / Backlog

The active backlog is product-focused rather than phase-focused.

Priority areas:

- improve clarity and quality of project storytelling
- keep backend and frontend documentation aligned with what actually runs
- continue polishing UI presentation without introducing misleading product claims
- strengthen confidence that seed data and rendered content stay consistent
- maintain strong responsiveness, accessibility basics, and overall professionalism

Out of scope unless explicitly reintroduced:

- admin CMS
- authentication
- blog platform
- analytics dashboard
- speculative platform expansion beyond portfolio use cases

## 9. Data Model Requirements

The product requires structured representations for:

- Projects
  - summary information for list views
  - detailed information for project pages
  - technologies, images, links, and narrative fields
- Skills
  - grouped by category
  - ordered for presentation
- Experience
  - company, role, date range, description, and highlights

The data model should prioritize truthful representation, maintainability, and clear rendering over unnecessary complexity.

## 10. UI/UX Requirements

The interface must:

- communicate credibility quickly
- remain readable on small screens
- highlight key projects without overwhelming the user
- preserve clear information hierarchy
- use motion and visual polish intentionally rather than decoratively

Design expectations:

- professional visual presentation
- strong readability
- responsive layout behavior
- accessible semantics and clear navigation
- consistent storytelling across sections

### Content request states

Projects, project detail, skills, and experience use
[`QueryContent`](../frontend/src/components/ui/QueryContent.tsx) to display
the state of their existing React Query requests.

| Request state | What visitors see |
|---|---|
| First load in progress | A skeleton with an accessible loading message |
| First load paused offline | A message that the request is waiting for a connection |
| Successful result | The content, or an explicit empty message for a list |
| First load failed | An error with a retry button |
| Refresh failed with cached data | The last loaded content and a retry button |
| Project detail returned HTTP 404 | Project Not Found and a way back, even if the project was cached |

React Query owns request, cache, and retry transitions. Retry acts on the
same query. Keep these states consistent when changing any of the four views.
Run `node --test tests/content-states.test.mjs` from `frontend/` to verify
the state rendering with fixture data.

### Other state owners

| Owner | Transitions |
|---|---|
| [Router and lazy route](../frontend/src/App.tsx) | Home, project detail, or unmatched URL. The detail code loads through Suspense before the project request starts. |
| [Navbar](../frontend/src/components/layout/Navbar.tsx) and [scroll hook](../frontend/src/hooks/useScrollPosition.ts) | Opening the mobile menu locks page scrolling on mobile. Selecting a link, pressing Escape, or crossing the desktop breakpoint closes it. The toggle exposes its expanded state. Header styling changes at the scroll threshold. |
| [Experience selection](../frontend/src/components/sections/ExperienceSection.tsx) | A company button selects its experience ID. Selection survives list reordering and falls back to the first entry if the selected ID disappears. The outgoing panel exits before the incoming panel enters. |
| [Project lists](../frontend/src/components/sections/ProjectsSection.tsx) and [detail](../frontend/src/components/projects/ProjectDetail.tsx) | Featured and other groups, optional detail blocks and links, and project image selection derive from the returned content. Image loading is browser-owned. |
| [Motion](../frontend/src/lib/animations.ts) and [styles](../frontend/src/styles/globals.css) | Viewport reveals, page transitions, pointer and focus feedback, and reduced-motion styles. |
| [API](../backend/src/main/java/com/faisal/portfolio/common/GlobalExceptionHandler.java) and [startup seed](../backend/src/main/java/com/faisal/portfolio/seed/DataSeeder.java) | Reads resolve to data or an HTTP error. Startup seeds a fresh database in a transaction. It refreshes the named flagship projects and their ordering in an existing database. |
| [Local runtime check](../scripts/healthcheck_l2_full.sh) | Reuse or start the dev backend, wait for health, wait separately for seeded projects, then check project detail and the frontend build. Clean up the backend if the check started it. |

### Navigation and story chapters

An unknown URL displays a Page Not Found heading with a working home link.
Opening a page without a section anchor starts at the top. Returning from a
project detail to `/#projects` brings the showcase into view.

The story chapter list has one tab stop. Left and right arrows select and focus
the adjacent chapter, wrapping at either end. Home and End select and focus the
first and last chapters. The selected tab identifies its panel, which is also
keyboard-focusable. Previous, next, and direct chapter buttons remain available.

Run the [browser regression checks](SOURCE_OF_TRUTH.md#browser-regression-checks)
to verify these interactions against a running application.

## 11. Security Considerations

This is a read-heavy public portfolio product. Security expectations should stay proportional to that reality.

The product should:

- expose only intentionally public content
- avoid unnecessary attack surface
- keep secrets and environment-specific credentials out of documentation and source-controlled product copy
- preserve safe defaults if future administrative features are ever introduced

## 12. Deployment And Runtime Ownership

This PRD does not define deployment targets, runtime ports, Spring profiles, healthcheck commands, or CI/CD behavior.

Those live in:

- `docs/SOURCE_OF_TRUTH.md`
- `.github/workflows/ci.yml` for build and rehearsal checks
- backend runtime configuration files

## 13. Risks And Constraints

The main product risks are:

- documentation drift causing incorrect implementation decisions
- overstating product claims relative to what actually exists
- UX polish degrading clarity instead of improving it
- stale content reducing trustworthiness

The product should optimize for credibility, maintainability, and truthful presentation over feature growth.
