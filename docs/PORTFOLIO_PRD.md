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
- a Lightsail deployment workflow exists

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

- surface flagship work clearly, especially CREAMS, Auto-Recruit, and this portfolio
- provide a project listing and project detail experience
- present skills in a grouped and readable format
- present work experience as a clear timeline or equivalent structure
- support responsive usage across mobile and desktop layouts
- maintain a visual standard appropriate for recruiters and engineering managers

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
- `.github/workflows/deploy-portfolio.yml`
- `deploy/*`
- backend runtime configuration files

## 13. Risks And Constraints

The main product risks are:

- documentation drift causing incorrect implementation decisions
- overstating product claims relative to what actually exists
- UX polish degrading clarity instead of improving it
- stale content reducing trustworthiness

The product should optimize for credibility, maintainability, and truthful presentation over feature growth.
