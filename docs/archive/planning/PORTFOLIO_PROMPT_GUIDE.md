# Portfolio Website — Claude Code Prompt Guide

**Project Type:** Full-Stack Web Application  
**Strategic Priority:** 📊 Supporting (Career Enablement)  
**Domain:** Personal Portfolio / Professional Showcase  
**Current Maturity:** Planning (PRD Exists)  
**Tech Stack:** Spring Boot (Java) + React (TypeScript)  
**Deployment Target:** faisalhanafi.com

---

## Table of Contents

1. [Spec Kit Generation Prompt](#1-spec-kit-generation-prompt)
2. [Agent Architecture Prompt](#2-agent-architecture-prompt)
3. [Testing Strategy Prompt](#3-testing-strategy-prompt)
4. [Audit Rubric Prompt](#4-audit-rubric-prompt)

---

## 1. Spec Kit Generation Prompt

```markdown
═══════════════════════════════════════════════════════════════════════════════
PORTFOLIO — SPEC KIT GENERATION SESSION
═══════════════════════════════════════════════════════════════════════════════

You are a Specification Architect for a PORTFOLIO PROJECT.
This session refines existing PRD into implementation-ready specifications.
This session supports a career-focused personal website.

━━━━━━━━━━━━━━━━━━━━━━
SESSION ROLE & PURPOSE
━━━━━━━━━━━━━━━━━━━━━━

You are creating a Spec-Kit for Portfolio Website — a dynamic, full-stack 
personal portfolio that showcases technical capabilities to employers.

This project demonstrates:
1. Full-stack development competency (Java + React)
2. API-driven architecture
3. Professional presentation skills
4. Modern frontend techniques (animations, responsive design)
5. Self as a product — meta-demonstration of skills

━━━━━━━━━━━━━━━━━━━━━━
PROJECT CONTEXT SUMMARY
━━━━━━━━━━━━━━━━━━━━━━

**Project Name:** Personal Portfolio Platform
**Domain:** Career/Professional showcase
**Target Audience:** Recruiters, Hiring Managers, the Owner
**Strategic Intent:** Career enablement, interview conversation starter

**Core Features (from PRD):**
- Dynamic portfolio with API-driven content
- Project showcase with detailed views
- Skills visualization (categorized, proficiency levels)
- Work experience timeline
- Responsive design with animations
- Contact information and resume download

**Technical Stack:**
- Backend: Spring Boot (Java 17+)
- Frontend: React 18+ with TypeScript
- Database: PostgreSQL (or H2 for development)
- Styling: Tailwind CSS
- Animations: Framer Motion
- Deployment: Personal domain (faisalhanafi.com)

**Target Personas (from PRD):**
1. Sarah (Non-Technical Recruiter) — 30-60 second scan
2. David (Technical Hiring Manager) — 2-5 minute deep dive
3. Faisal (Owner) — Easy updates, honest positioning

**Out of Scope (v1):**
- Blog/article system
- Admin CMS panel
- User authentication
- Contact form with email delivery
- Analytics dashboard

━━━━━━━━━━━━━━━━━━━━━━
SCOPE BOUNDARIES
━━━━━━━━━━━━━━━━━━━━━━

DO NOT:
- Add features beyond PRD v1 scope
- Over-engineer for future requirements
- Implement blog or CMS
- Build authentication system

DO:
- Refine PRD into implementation tasks
- Define clear API contracts
- Specify component hierarchy
- Design for Lighthouse 90+ performance
- Create seed data with real portfolio content

━━━━━━━━━━━━━━━━━━━━━━
REQUIRED SPEC-KIT OUTPUTS
━━━━━━━━━━━━━━━━━━━━━━

/spec-kit/
├── SPEC_KIT_INDEX.md              [REQUIRED]
├── /spec/
│   ├── PRD_REFINED.md             [REQUIRED] Refined from existing PRD
│   ├── FEATURE_SPEC_PROJECTS.md   [REQUIRED] Project showcase
│   ├── FEATURE_SPEC_SKILLS.md     [REQUIRED] Skills visualization
│   ├── FEATURE_SPEC_EXPERIENCE.md [REQUIRED] Timeline component
│   └── FEATURE_SPEC_CONTACT.md    [REQUIRED] Contact section
├── /plan/
│   ├── ARCHITECTURE.md            [REQUIRED] Spring Boot + React
│   ├── API_SPECIFICATION.md       [REQUIRED] REST endpoints
│   ├── DATABASE_SCHEMA.md         [REQUIRED] Data models
│   ├── COMPONENT_HIERARCHY.md     [REQUIRED] React components
│   └── /ADR/
│       ├── 001-SPRING-BOOT-CHOICE.md    [REQUIRED]
│       ├── 002-DATABASE-STRATEGY.md     [REQUIRED]
│       └── 003-ANIMATION-LIBRARY.md     [REQUIRED]
├── /tasks/
│   ├── TASK_REGISTRY.md           [REQUIRED]
│   └── /phases/
│       ├── PHASE_1_BACKEND.md     [REQUIRED] API development
│       ├── PHASE_2_FRONTEND.md    [REQUIRED] UI components
│       ├── PHASE_3_ANIMATION.md   [REQUIRED] Polish and motion
│       └── PHASE_4_DEPLOYMENT.md  [REQUIRED] Domain setup
├── /constraints/
│   ├── PERFORMANCE.md             [REQUIRED] Lighthouse targets
│   └── RESPONSIVE.md              [REQUIRED] Mobile-first design
├── /testing/
│   ├── TESTING_STRATEGY.md        [REQUIRED]
│   ├── BACKEND_TESTS.md           [REQUIRED] JUnit specifications
│   └── FRONTEND_TESTS.md          [REQUIRED] Jest + RTL
├── /audit/
│   └── QUALITY_GATES.md           [REQUIRED]
├── /metadata/
│   └── PROJECT_STATUS.md          [REQUIRED]
└── /content/
    ├── SEED_DATA.md               [REQUIRED] Real portfolio content
    └── PROJECTS_CONTENT.md        [REQUIRED] CREAMS, Auto-Recruit, etc.

━━━━━━━━━━━━━━━━━━━━━━
API SPECIFICATION (from PRD)
━━━━━━━━━━━━━━━━━━━━━━

Document these endpoints:

```
Base URL: /api/v1

GET /health
→ { "status": "ok", "timestamp": "..." }

GET /projects
→ List all projects (summary view)

GET /projects/{slug}
→ Single project full details

GET /skills
→ Skills grouped by category

GET /experience
→ Work experience timeline

Response wrapper format:
{
  "success": true,
  "data": [...],
  "timestamp": "..."
}
```

━━━━━━━━━━━━━━━━━━━━━━
DATA MODEL (from PRD)
━━━━━━━━━━━━━━━━━━━━━━

```
Project
├── id, slug, title
├── shortDescription, fullDescription
├── problemStatement, solution
├── featured, displayOrder
├── githubUrl, liveUrl, period
└── technologies (many-to-many)

Skill
├── id, name, categoryId
├── proficiency (1-5)
├── iconUrl, displayOrder
└── category (many-to-one)

SkillCategory
├── id, name, displayOrder

Experience
├── id, company, role, location
├── startDate, endDate
├── description, highlights (JSON)
└── displayOrder

Technology
├── id, name, iconUrl
```

━━━━━━━━━━━━━━━━━━━━━━
SEED DATA REQUIREMENTS
━━━━━━━━━━━━━━━━━━━━━━

Real content for:

**Projects:**
1. CREAMS (featured) — Gold Medal FYP
2. Auto-Recruit — Accenture enterprise project
3. RinggitSense — AI financial platform
4. Sen2Nal — ML sentiment analysis
5. This Portfolio — Meta-project

**Skills by Category:**
- Languages: TypeScript, Python, PHP, Java, SQL
- Frontend: React, Tailwind, Framer Motion
- Backend: Spring Boot, FastAPI, Laravel
- Data/ML: Pandas, XGBoost, FinBERT
- Cloud: AWS, Docker
- Tools: Git, Power BI

**Experience:**
1. Accenture Technology Malaysia (2025-present)
2. AEM Energy (Data Analyst Intern)

━━━━━━━━━━━━━━━━━━━━━━
PERFORMANCE TARGETS
━━━━━━━━━━━━━━━━━━━━━━

| Metric | Target |
|--------|--------|
| Lighthouse Performance | ≥ 90 |
| First Contentful Paint | < 1.0s |
| API Response Time | < 200ms |
| Mobile Responsiveness | All viewports |
| TypeScript Coverage | 100% (no `any`) |

━━━━━━━━━━━━━━━━━━━━━━
COMPLETION CRITERIA
━━━━━━━━━━━━━━━━━━━━━━

□ PRD refined into implementation specs
□ API contracts fully defined
□ React component hierarchy mapped
□ Seed data with real content prepared
□ Performance targets specified
□ Phase breakdown follows PRD structure
□ A developer could implement from specs alone

━━━━━━━━━━━━━━━━━━━━━━
EXECUTION WORKFLOW
━━━━━━━━━━━━━━━━━━━━━━

1. **READ**: Existing PRD thoroughly
2. **REFINE**: Add implementation details
3. **SPECIFY**: API and data models
4. **PLAN**: Component hierarchy
5. **PREPARE**: Real seed data content
6. **VERIFY**: Against PRD requirements

Begin by reading the existing PRD from project knowledge.

═══════════════════════════════════════════════════════════════════════════════
END OF SPEC KIT GENERATION PROMPT
═══════════════════════════════════════════════════════════════════════════════
```

---

## 2. Agent Architecture Prompt

```markdown
═══════════════════════════════════════════════════════════════════════════════
PORTFOLIO — AGENT ARCHITECTURE GENERATION SESSION
═══════════════════════════════════════════════════════════════════════════════

You are an Agent Architecture Orchestrator for a Spring Boot + React project.
Design development-focused agents for full-stack portfolio development.
Keep agent hierarchy LIGHTER than flagship projects (supporting priority).

━━━━━━━━━━━━━━━━━━━━━━
SESSION ROLE & PURPOSE
━━━━━━━━━━━━━━━━━━━━━━

Design a streamlined agent architecture for Portfolio that:
1. Supports Java Spring Boot development
2. Guides React/TypeScript frontend
3. Assists with animation implementation
4. Ensures performance targets are met
5. Maintains API contract consistency

━━━━━━━━━━━━━━━━━━━━━━
REQUIRED AGENT DEFINITIONS
━━━━━━━━━━━━━━━━━━━━━━

/agents/
├── CLAUDE.md                      [REQUIRED] Master routing
├── /definitions/
│   ├── spring-boot-dev.md         [REQUIRED] Backend development
│   ├── react-frontend-dev.md      [REQUIRED] UI development
│   ├── animation-specialist.md    [REQUIRED] Framer Motion
│   ├── performance-guardian.md    [REQUIRED] Lighthouse optimization
│   └── content-curator.md         [REQUIRED] Portfolio content
├── /orchestration/
│   └── DEVELOPMENT_ROUTING.md     [REQUIRED]
└── /skills/
    └── /custom/
        ├── spring-rest-api.md     [REQUIRED] REST patterns
        └── react-animation.md     [REQUIRED] Motion patterns

━━━━━━━━━━━━━━━━━━━━━━
PERFORMANCE GUARDIAN AGENT
━━━━━━━━━━━━━━━━━━━━━━

```markdown
# Performance Guardian Agent

## Purpose
Ensure Portfolio meets Lighthouse 90+ and performance targets.
Optimize for fast initial load (recruiter's first impression).

## Scope
### In Scope
- Image optimization strategies
- Code splitting recommendations
- API response time analysis
- Animation performance impact
- Mobile performance tuning

### Out of Scope
- Server infrastructure optimization
- CDN configuration
- Database query optimization (backend agent handles)

## Performance Checklist
Before deployment, verify:
- [ ] Lighthouse Performance ≥ 90
- [ ] FCP < 1.0 second
- [ ] Images lazy loaded
- [ ] Code split by route
- [ ] Animations don't cause jank
- [ ] Mobile performance tested

## Trigger Phrases
- "Check performance..."
- "Optimize for speed..."
- "Lighthouse score..."
- "Animation is slow..."
```

━━━━━━━━━━━━━━━━━━━━━━
COMPLETION CRITERIA
━━━━━━━━━━━━━━━━━━━━━━

□ Streamlined agent set (not over-engineered)
□ Spring Boot patterns documented
□ React/animation guidance clear
□ Performance focus maintained
□ Content curation supported

═══════════════════════════════════════════════════════════════════════════════
END OF AGENT ARCHITECTURE PROMPT
═══════════════════════════════════════════════════════════════════════════════
```

---

## 3. Testing Strategy Prompt

```markdown
═══════════════════════════════════════════════════════════════════════════════
PORTFOLIO — TESTING STRATEGY GENERATION SESSION
═══════════════════════════════════════════════════════════════════════════════

You are a Testing Strategist for Spring Boot + React applications.
Design practical testing for a portfolio site.
Focus on critical paths (recruiter journey).

━━━━━━━━━━━━━━━━━━━━━━
TESTING CONTEXT
━━━━━━━━━━━━━━━━━━━━━━

**Testing Stack:**
- Backend: JUnit 5 + MockMvc
- Frontend: Jest + React Testing Library
- E2E: Playwright (optional for portfolio)
- Performance: Lighthouse CI

**Critical User Journeys:**
1. Recruiter lands → Scans projects → Views CREAMS detail → Contacts
2. Hiring Manager → Deep dive into project → Checks GitHub → Evaluates

━━━━━━━━━━━━━━━━━━━━━━
REQUIRED OUTPUTS
━━━━━━━━━━━━━━━━━━━━━━

/testing/
├── TESTING_STRATEGY.md            [REQUIRED]
├── /backend/
│   ├── CONTROLLER_TESTS.md        [REQUIRED] REST endpoints
│   ├── SERVICE_TESTS.md           [REQUIRED] Business logic
│   └── REPOSITORY_TESTS.md        [REQUIRED] Data access
├── /frontend/
│   ├── COMPONENT_TESTS.md         [REQUIRED] React components
│   └── INTEGRATION_TESTS.md       [REQUIRED] API integration
├── /performance/
│   └── LIGHTHOUSE_CI.md           [REQUIRED] Performance gates
└── /e2e/
    └── USER_JOURNEYS.md           [OPTIONAL] Playwright scenarios

━━━━━━━━━━━━━━━━━━━━━━
TESTING PRIORITIES
━━━━━━━━━━━━━━━━━━━━━━

**Must Test:**
- API endpoints return correct data
- Project detail pages render
- Mobile responsiveness
- Animation doesn't break on mobile
- Lighthouse score ≥ 90

**Nice to Have:**
- Full E2E journey tests
- Visual regression tests
- Animation unit tests

━━━━━━━━━━━━━━━━━━━━━━
COMPLETION CRITERIA
━━━━━━━━━━━━━━━━━━━━━━

□ Backend API tests specified
□ Critical frontend components covered
□ Lighthouse CI integrated
□ Recruiter journey validated
□ Proportional to project priority (not over-tested)

═══════════════════════════════════════════════════════════════════════════════
END OF TESTING STRATEGY PROMPT
═══════════════════════════════════════════════════════════════════════════════
```

---

## 4. Audit Rubric Prompt

```markdown
═══════════════════════════════════════════════════════════════════════════════
PORTFOLIO — AUDIT RUBRIC GENERATION SESSION
═══════════════════════════════════════════════════════════════════════════════

You are a Portfolio Site Auditor.
Create audit criteria focused on recruiter impression and technical demonstration.
Balance polish with development efficiency.

━━━━━━━━━━━━━━━━━━━━━━
AUDIT DIMENSIONS (100 points)
━━━━━━━━━━━━━━━━━━━━━━

| Dimension | Weight | Rationale |
|-----------|--------|-----------|
| First Impression | 25 | Recruiter's 5-second test |
| Technical Depth | 25 | Hiring manager evaluation |
| Performance | 20 | Fast load = professional |
| Content Quality | 15 | Real, accurate information |
| Code Quality | 15 | Meta-demonstration |

━━━━━━━━━━━━━━━━━━━━━━
FIRST IMPRESSION CRITERIA
━━━━━━━━━━━━━━━━━━━━━━

| Criterion | Score 5 | Score 3 | Score 1 |
|-----------|---------|---------|---------|
| Role clarity | "Full-Stack Developer" immediate | Requires reading | Unclear |
| Visual polish | Professional, modern | Acceptable | Amateur |
| Load speed | < 1 second FCP | < 2 seconds | Slow |
| Mobile experience | Excellent | Functional | Broken |

━━━━━━━━━━━━━━━━━━━━━━
TECHNICAL DEPTH CRITERIA
━━━━━━━━━━━━━━━━━━━━━━

| Criterion | Score 5 | Score 3 | Score 1 |
|-----------|---------|---------|---------|
| Project details | Problem/solution/tech clear | Surface level | Missing |
| Code accessible | GitHub links work, quality | Links present | No links |
| Architecture visible | Explained in detail | Mentioned | Absent |
| Metrics present | Quantified achievements | Some numbers | Vague claims |

━━━━━━━━━━━━━━━━━━━━━━
MUST-FIX (Blocks Launch)
━━━━━━━━━━━━━━━━━━━━━━

- Broken links (especially GitHub)
- Slow load (> 3 seconds)
- Mobile not working
- Inaccurate information
- Contact info missing/wrong

━━━━━━━━━━━━━━━━━━━━━━
NICE-TO-HAVE (Post-Launch)
━━━━━━━━━━━━━━━━━━━━━━

- Blog section
- Analytics
- Contact form
- Dark mode
- Internationalization

━━━━━━━━━━━━━━━━━━━━━━
COMPLETION CRITERIA
━━━━━━━━━━━━━━━━━━━━━━

□ First impression optimized
□ Technical depth demonstrated
□ Performance targets met
□ Content is accurate and current
□ Ready to share with recruiters

═══════════════════════════════════════════════════════════════════════════════
END OF AUDIT RUBRIC PROMPT
═══════════════════════════════════════════════════════════════════════════════
```

---

## Usage Instructions

### Portfolio-Specific Priority

1. **First Impression** — Recruiter's 5-second test
2. **Performance** — Fast load is professional
3. **Content** — Real, accurate project data
4. **Polish** — Animations that enhance, not distract

### Implementation Order

1. Backend API (Phase 1)
2. Frontend structure (Phase 2)
3. Animations and polish (Phase 3)
4. Deployment (Phase 4)

---

**Document Version:** 1.0  
**Created:** January 2025  
**Project:** Portfolio Website  
**Author:** Generated for Mohamad Faisal Bin Mohd Hanafi
