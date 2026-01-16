# 📋 Portfolio Platform — Product Requirements Document (PRD)

**Document Version:** 1.0  
**Author:** Mohamad Faisal Bin Mohd Hanafi  
**Created:** December 2025  
**Status:** Approved for Development

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Problem Statement](#2-problem-statement)
3. [Solution Overview](#3-solution-overview)
4. [Target Audience](#4-target-audience)
5. [Success Metrics](#5-success-metrics)
6. [Functional Requirements by Phase](#6-functional-requirements-by-phase)
7. [Non-Functional Requirements](#7-non-functional-requirements)
8. [Data Models](#8-data-models)
9. [API Specification](#9-api-specification)
10. [UI/UX Requirements](#10-uiux-requirements)
11. [Security Considerations](#11-security-considerations)
12. [Deployment Strategy](#12-deployment-strategy)
13. [Risk Assessment](#13-risk-assessment)
14. [Glossary](#14-glossary)

---

## 1. Executive Summary

### 1.1 Purpose

This document defines the requirements for a **Personal Portfolio Platform** — a dynamic, full-stack web application designed to showcase technical capabilities to potential employers. Unlike static portfolio sites, this platform demonstrates engineering depth through its own implementation.

### 1.2 Strategic Value

| For Recruiters | For Hiring Managers | For the Owner |
|----------------|---------------------|---------------|
| Quick legitimacy assessment | Code quality signal | Central proof-of-capability |
| Clear skill communication | Architecture evaluation | Updateable career asset |
| Professional presentation | Modern pattern usage | Interview talking point |

### 1.3 Scope

**In Scope (v1):**
- Dynamic portfolio with API-driven content
- Project showcase with detailed views
- Skills visualization
- Work experience timeline
- Responsive design with animations
- Contact information and resume download

**Out of Scope (v1):**
- Blog/article system
- Admin CMS panel
- User authentication
- Contact form with email delivery
- Analytics dashboard

---

## 2. Problem Statement

### 2.1 Current State

Faisal has completed substantial projects (CREAMS, Auto-Recruit) that demonstrate enterprise-grade development capability. However:

1. **Projects exist in isolation** — No unified presentation layer
2. **Static resumes are limiting** — Cannot demonstrate live code quality
3. **Documentation alone is insufficient** — Recruiters want to see working software
4. **Existing projects have constraints** — Auto-Recruit is a prototype; CREAMS is academic

### 2.2 Desired State

A live, polished portfolio that:
- Proves technical capability through its own implementation
- Contextualizes existing work for different audiences
- Creates a memorable first impression
- Serves as a conversation starter in interviews

### 2.3 Gap Analysis

| Gap | Impact | Solution |
|-----|--------|----------|
| No live demonstration of skills | Recruiters rely solely on resume claims | Build a working full-stack app |
| Projects not presented cohesively | Scattered GitHub repos, no narrative | Unified showcase with context |
| No frontend excellence proof | Hired for backend/data despite React work | Animation-rich, polished UI |
| Static content can't evolve | Outdated as career progresses | Database-driven, easily updated |

---

## 3. Solution Overview

### 3.1 Architecture Summary

```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (React/TypeScript)              │
│  • Vite build system                                        │
│  • Tailwind CSS styling                                     │
│  • Framer Motion animations                                 │
│  • React Query for data fetching                            │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ REST API (JSON)
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    BACKEND (Spring Boot)                    │
│  • RESTful controllers                                      │
│  • Service layer business logic                             │
│  • JPA repositories                                         │
│  • OpenAPI documentation                                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ JPA/Hibernate
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    DATABASE (PostgreSQL)                    │
│  • Projects, Skills, Experience tables                      │
│  • Seed data for initial content                            │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 Technology Choices & Rationale

| Technology | Choice | Rationale |
|------------|--------|-----------|
| Backend Framework | Spring Boot | Enterprise standard, demonstrates Java proficiency |
| Frontend Framework | React + TypeScript | Industry demand, strict typing prevents bugs |
| Styling | Tailwind CSS | Rapid development, consistent design system |
| Animation | Framer Motion | Declarative, performant, React-native |
| Database | PostgreSQL | Production-grade, free tier availability |
| Build Tool | Gradle (BE), Vite (FE) | Modern, fast, widely adopted |

---

## 4. Target Audience

### 4.1 Primary Personas

#### Persona 1: Non-Technical Recruiter (Sarah)
- **Role:** Talent Acquisition Specialist
- **Goal:** Quickly assess if candidate is worth forwarding to hiring manager
- **Behavior:** Scans for 30-60 seconds, looks at headlines, visuals, job titles
- **Needs:** Clear role identification, professional appearance, easy contact
- **Pain Points:** Confusing layouts, too much technical jargon, slow-loading sites

#### Persona 2: Technical Hiring Manager (David)
- **Role:** Engineering Manager / Tech Lead
- **Goal:** Evaluate technical depth and code quality
- **Behavior:** Spends 2-5 minutes, clicks into projects, may check GitHub
- **Needs:** Architecture visibility, code samples, technology breadth
- **Pain Points:** All-talk-no-code portfolios, outdated tech stacks, poor UX despite "frontend" claims

#### Persona 3: Content Owner (Faisal)
- **Role:** Portfolio owner
- **Goal:** Present authentic, updateable representation of skills
- **Behavior:** Updates content periodically, shares link in applications
- **Needs:** Easy content updates, honest positioning, professional appearance
- **Pain Points:** Static sites that become outdated, exaggerated claims that backfire

### 4.2 User Journey Maps

**Sarah (Recruiter) Journey:**
```
Google/LinkedIn → Land on portfolio → Quick scan (5s) → 
"Looks professional" → Check projects section → See role fit → 
Contact/LinkedIn → Forward to David
```

**David (Hiring Manager) Journey:**
```
Email from Sarah → Click portfolio link → Land on portfolio →
Impressed by animations → Deep dive into CREAMS project →
Check GitHub link → Review code quality → 
"This person can build things" → Schedule interview
```

---

## 5. Success Metrics

### 5.1 Technical Metrics

| Metric | Target | Measurement Method |
|--------|--------|-------------------|
| Lighthouse Performance Score | ≥ 90 | Chrome DevTools |
| First Contentful Paint | < 1.0s | Chrome DevTools |
| TypeScript Coverage | 100% (no `any`) | Build-time check |
| Mobile Responsiveness | Pass all viewports | Manual testing |
| API Response Time | < 200ms | Postman/monitoring |

### 5.2 User Experience Metrics

| Metric | Target | Measurement Method |
|--------|--------|-------------------|
| Time to Understand Role | < 5 seconds | User testing |
| Projects Discoverable | < 1 scroll | Layout design |
| Contact Info Accessible | Always visible | Fixed nav/footer |
| Animation Load Impact | No jank | Performance profiling |

### 5.3 Career Metrics

| Metric | Target | Evidence |
|--------|--------|----------|
| Interview Conversation Starter | 100% of interviews | Self-reported |
| Positive Recruiter Feedback | Qualitative | Direct feedback |
| GitHub Profile Views | Increase post-launch | GitHub analytics |

---

## 6. Functional Requirements by Phase

---

### Phase 0: Foundation & Scaffolding

**Duration:** 2-3 days  
**Goal:** Both projects running locally with basic connectivity

#### Requirements

| ID | Requirement | Priority | Acceptance Criteria |
|----|-------------|----------|---------------------|
| P0-01 | Initialize Spring Boot project with Gradle | P0 | `./gradlew bootRun` starts server on port 8080 |
| P0-02 | Create health check endpoint | P0 | `GET /api/v1/health` returns `{"status": "ok"}` |
| P0-03 | Configure CORS for local development | P0 | Frontend can call backend without CORS errors |
| P0-04 | Initialize React project with Vite | P0 | `npm run dev` starts server on port 5173 |
| P0-05 | Configure TypeScript strict mode | P0 | `tsconfig.json` has `strict: true`, build passes |
| P0-06 | Set up Tailwind CSS | P0 | Tailwind classes render correctly |
| P0-07 | Install Framer Motion | P1 | Package in `package.json`, importable |
| P0-08 | Create basic folder structure | P0 | Matches defined project structure |
| P0-09 | Frontend calls backend health endpoint | P0 | Page displays "Backend: Connected" |
| P0-10 | Set up H2 in-memory database for dev | P1 | Spring Boot connects to H2, console accessible |

#### Deliverables
- [ ] `/backend` — Running Spring Boot app with health endpoint
- [ ] `/frontend` — Running React app displaying backend status
- [ ] `README.md` — Setup instructions

#### Exit Criteria
- Both apps start without errors
- Frontend successfully fetches from backend
- TypeScript compiles in strict mode
- Tailwind classes apply correctly

---

### Phase 1: Backend API Complete

**Duration:** 3-4 days  
**Goal:** All API endpoints functional with seeded data

#### Requirements

| ID | Requirement | Priority | Acceptance Criteria |
|----|-------------|----------|---------------------|
| P1-01 | Create Project entity and repository | P0 | Entity maps to database, CRUD operations work |
| P1-02 | Create Skill and SkillCategory entities | P0 | Skills belong to categories, queryable |
| P1-03 | Create Experience entity | P0 | Entity stores work history with date ranges |
| P1-04 | Create Technology entity | P1 | Many-to-many relationship with Projects |
| P1-05 | Implement ProjectService | P0 | Business logic isolated from controller |
| P1-06 | Implement SkillService | P0 | Returns skills grouped by category |
| P1-07 | Implement ExperienceService | P0 | Returns experiences sorted by date |
| P1-08 | Create ProjectController with endpoints | P0 | `GET /projects` and `GET /projects/{slug}` work |
| P1-09 | Create SkillController | P0 | `GET /skills` returns categorized skills |
| P1-10 | Create ExperienceController | P0 | `GET /experience` returns timeline data |
| P1-11 | Implement standardized API response wrapper | P0 | All responses follow `{success, data, timestamp}` format |
| P1-12 | Add global exception handling | P1 | Errors return proper JSON, not stack traces |
| P1-13 | Create DTOs for response shaping | P1 | Entities don't leak directly to API |
| P1-14 | Seed database with real portfolio content | P0 | CREAMS, Auto-Recruit, skills, experience populated |
| P1-15 | Add OpenAPI/Swagger documentation | P2 | `/swagger-ui.html` shows all endpoints |

#### Deliverables
- [ ] Complete data model implementation
- [ ] All REST endpoints functional
- [ ] Seed data with real content
- [ ] API documentation accessible

#### Exit Criteria
- All endpoints return expected JSON structure
- Seed data represents actual projects/skills/experience
- No N+1 query issues (verified via logs)
- Swagger UI accessible and accurate

---

### Phase 2: Frontend Structure (No Animation)

**Duration:** 4-5 days  
**Goal:** Complete UI rendering all content, fully responsive, no animations

#### Requirements

| ID | Requirement | Priority | Acceptance Criteria |
|----|-------------|----------|---------------------|
| P2-01 | Set up React Router with routes | P0 | `/` and `/projects/:slug` routes work |
| P2-02 | Create Layout component with Navbar | P0 | Persistent nav across all pages |
| P2-03 | Create Footer component | P1 | Social links, copyright, resume download |
| P2-04 | Build HeroSection | P0 | Name, tagline, role visible above fold |
| P2-05 | Build AboutSection | P0 | Brief bio, photo placeholder, stats |
| P2-06 | Build ProjectsSection | P0 | Grid/list of project cards |
| P2-07 | Create ProjectCard component | P0 | Image, title, short description, tech tags |
| P2-08 | Create FeaturedProject component | P1 | Larger card for CREAMS highlight |
| P2-09 | Build SkillsSection | P0 | Skills displayed by category |
| P2-10 | Build ExperienceSection | P0 | Timeline layout with company, role, dates |
| P2-11 | Build ContactSection | P0 | Email, LinkedIn, GitHub links |
| P2-12 | Create ProjectPage (detail view) | P0 | Full project info from `/projects/:slug` |
| P2-13 | Implement API client with React Query | P0 | Hooks for fetching projects, skills, experience |
| P2-14 | Add loading states | P1 | Skeleton or spinner during data fetch |
| P2-15 | Add error states | P1 | Graceful handling when API fails |
| P2-16 | Mobile responsiveness pass | P0 | All sections work at 375px, 768px, 1440px |
| P2-17 | Dark mode implementation | P1 | Toggle in navbar, persists to localStorage |
| P2-18 | Semantic HTML structure | P1 | Proper heading hierarchy, landmarks |

#### Deliverables
- [ ] Complete single-page portfolio (desktop + mobile)
- [ ] Project detail page
- [ ] API integration with loading/error states
- [ ] Dark/light mode toggle

#### Exit Criteria
- All content from backend renders correctly
- Site is fully navigable on mobile and desktop
- No layout breaks at any viewport
- TypeScript compiles without errors

---

### Phase 3: Animation & Polish

**Duration:** 3-4 days  
**Goal:** Polished, animated experience that feels intentional

#### Requirements

| ID | Requirement | Priority | Acceptance Criteria |
|----|-------------|----------|---------------------|
| P3-01 | Hero text staggered reveal animation | P0 | Name animates in letter-by-letter or word-by-word |
| P3-02 | Scroll-triggered section reveals | P0 | Sections fade/slide in when entering viewport |
| P3-03 | Create AnimatedSection wrapper | P0 | Reusable component for scroll animations |
| P3-04 | Page transition animations | P1 | Smooth transition between home and project pages |
| P3-05 | Project card hover effects | P0 | Subtle lift, shadow change on hover |
| P3-06 | Navbar scroll behavior | P1 | Transparent on hero, solid after scroll |
| P3-07 | Skill badges animation | P2 | Subtle entrance animation for skill tags |
| P3-08 | Timeline entrance animation | P1 | Items animate in sequence on scroll |
| P3-09 | Button micro-interactions | P2 | Hover/active states with subtle motion |
| P3-10 | Loading skeleton animations | P2 | Pulse animation for loading states |
| P3-11 | Theme toggle animation | P2 | Smooth color transition on toggle |
| P3-12 | Performance audit | P0 | Lighthouse ≥ 90, no jank during animations |
| P3-13 | Reduced motion support | P1 | `prefers-reduced-motion` disables animations |
| P3-14 | Animation timing refinement | P1 | Consistent easing, appropriate durations |

#### Deliverables
- [ ] All animations implemented
- [ ] Performance verified (Lighthouse ≥ 90)
- [ ] Reduced motion accessibility support

#### Exit Criteria
- Animations enhance, not distract
- No performance degradation from animations
- Site feels polished and intentional
- Accessibility considerations met

---

### Phase 4: Deployment

**Duration:** 1-2 days  
**Goal:** Live on the internet, accessible via URL

#### Requirements

| ID | Requirement | Priority | Acceptance Criteria |
|----|-------------|----------|---------------------|
| P4-01 | Set up production database | P0 | PostgreSQL instance on Supabase/PlanetScale |
| P4-02 | Configure production environment variables | P0 | Secrets not in code, env-specific configs |
| P4-03 | Deploy backend to Render/Railway | P0 | API accessible at production URL |
| P4-04 | Deploy frontend to Vercel/Netlify | P0 | Site accessible at production URL |
| P4-05 | Configure CORS for production | P0 | Frontend can call production backend |
| P4-06 | HTTPS verification | P0 | Both frontend and backend serve over HTTPS |
| P4-07 | Environment separation | P1 | Dev and prod configs properly isolated |
| P4-08 | Custom domain setup (optional) | P2 | `portfolio.faisal.dev` or similar |
| P4-09 | Health monitoring setup | P2 | Basic uptime monitoring (UptimeRobot free tier) |
| P4-10 | Cross-browser testing | P1 | Works on Chrome, Firefox, Safari, Edge |

#### Deliverables
- [ ] Live production URL
- [ ] Backend API accessible
- [ ] HTTPS enforced
- [ ] Documentation updated with live URLs

#### Exit Criteria
- Site loads from production URL
- API responds correctly
- No mixed content warnings
- Works across major browsers

---

### Phase 5: Content & Ongoing Polish

**Duration:** Continuous  
**Goal:** Resume-ready, continuously improved

#### Requirements

| ID | Requirement | Priority | Acceptance Criteria |
|----|-------------|----------|---------------------|
| P5-01 | Refine project descriptions | P0 | Clear, impactful, recruiter-friendly |
| P5-02 | Add project screenshots | P0 | High-quality images for each project |
| P5-03 | Update resume PDF | P0 | Portfolio URL prominently featured |
| P5-04 | Update LinkedIn profile | P0 | Link to portfolio in featured section |
| P5-05 | Create GitHub README for repos | P1 | Both backend and frontend repos documented |
| P5-06 | SEO meta tags | P2 | Title, description, OG tags for sharing |
| P5-07 | Favicon and branding | P2 | Custom favicon, consistent branding |
| P5-08 | Record demo video (optional) | P2 | 2-3 minute walkthrough for applications |
| P5-09 | Performance optimization pass | P1 | Image optimization, code splitting if needed |
| P5-10 | Accessibility audit | P1 | WCAG 2.1 AA compliance check |

#### Deliverables
- [ ] Polished content
- [ ] Resume updated
- [ ] LinkedIn updated
- [ ] GitHub repos documented

#### Exit Criteria
- Portfolio is interview-ready
- All content is accurate and compelling
- Owner confident sharing URL

---

## 7. Non-Functional Requirements

### 7.1 Performance

| Requirement | Target | Rationale |
|-------------|--------|-----------|
| Page Load Time | < 3 seconds on 3G | Mobile recruiters on slow connections |
| API Response Time | < 200ms | Snappy data loading |
| Bundle Size | < 200KB gzipped | Fast initial load |
| Lighthouse Score | ≥ 90 | Industry standard benchmark |

### 7.2 Scalability

| Requirement | Target | Rationale |
|-------------|--------|-----------|
| Concurrent Users | 100+ | Handle viral LinkedIn post scenario |
| Database Size | < 100MB | Fits in free tier limits |
| CDN Caching | Static assets cached | Reduce server load |

### 7.3 Reliability

| Requirement | Target | Rationale |
|-------------|--------|-----------|
| Uptime | 99% | Cannot be down during recruiter visits |
| Error Handling | Graceful degradation | Show something even if API fails |
| Data Backup | Weekly | Protect content investment |

### 7.4 Maintainability

| Requirement | Target | Rationale |
|-------------|--------|-----------|
| Code Documentation | Self-documenting + comments | Future self can understand |
| Dependency Updates | Monthly review | Security and compatibility |
| TypeScript Coverage | 100% strict mode | Prevent runtime errors |

---

## 8. Data Models

### 8.1 Entity Relationship Diagram

```
┌─────────────────┐       ┌──────────────────────┐       ┌─────────────┐
│    Project      │───────│  ProjectTechnology   │───────│ Technology  │
├─────────────────┤       ├──────────────────────┤       ├─────────────┤
│ id              │       │ project_id           │       │ id          │
│ slug            │       │ technology_id        │       │ name        │
│ title           │       └──────────────────────┘       │ icon_url    │
│ short_desc      │                                      └─────────────┘
│ full_desc       │
│ problem_stmt    │       ┌─────────────────┐
│ solution        │───────│  ProjectImage   │
│ featured        │       ├─────────────────┤
│ display_order   │       │ id              │
│ github_url      │       │ project_id      │
│ live_url        │       │ url             │
│ period          │       │ alt_text        │
│ created_at      │       │ display_order   │
│ updated_at      │       └─────────────────┘
└─────────────────┘

┌─────────────────┐       ┌─────────────────┐
│     Skill       │───────│  SkillCategory  │
├─────────────────┤       ├─────────────────┤
│ id              │       │ id              │
│ name            │       │ name            │
│ category_id     │       │ display_order   │
│ proficiency     │       └─────────────────┘
│ icon_url        │
│ display_order   │
└─────────────────┘

┌─────────────────┐
│   Experience    │
├─────────────────┤
│ id              │
│ company         │
│ role            │
│ location        │
│ start_date      │
│ end_date        │
│ description     │
│ highlights      │ (JSON array)
│ display_order   │
└─────────────────┘
```

### 8.2 Seed Data Summary

| Entity | Records | Notes |
|--------|---------|-------|
| Projects | 3-4 | CREAMS (featured), Auto-Recruit, Portfolio, TeZer (optional) |
| Technologies | ~20 | TypeScript, React, Laravel, Spring Boot, etc. |
| Skills | ~25 | Grouped by category |
| SkillCategories | 5-6 | Languages, Frontend, Backend, Data, Cloud, Tools |
| Experience | 2-3 | Accenture, AEM Energy, UiTM (optional) |

---

## 9. API Specification

### 9.1 Base Configuration

```
Base URL:       /api/v1
Content-Type:   application/json
```

### 9.2 Endpoints

#### GET /health
```
Response: { "status": "ok", "timestamp": "..." }
Purpose:  Health check for monitoring
```

#### GET /projects
```
Response: {
  "success": true,
  "data": [
    {
      "id": 1,
      "slug": "creams",
      "title": "CREAMS...",
      "shortDescription": "...",
      "technologies": ["Laravel", "PHP"],
      "thumbnailUrl": "...",
      "featured": true
    }
  ]
}
Purpose: List all projects (summary view)
```

#### GET /projects/{slug}
```
Response: {
  "success": true,
  "data": {
    "id": 1,
    "slug": "creams",
    "title": "...",
    "fullDescription": "...",
    "problemStatement": "...",
    "solution": "...",
    "keyFeatures": [...],
    "technologies": [...],
    "metrics": {...},
    "images": [...],
    "links": {...},
    "period": "2023-2024"
  }
}
Purpose: Single project full details
```

#### GET /skills
```
Response: {
  "success": true,
  "data": [
    {
      "category": "Languages",
      "skills": [
        { "name": "TypeScript", "proficiency": 4, "iconUrl": "..." }
      ]
    }
  ]
}
Purpose: Skills grouped by category
```

#### GET /experience
```
Response: {
  "success": true,
  "data": [
    {
      "id": 1,
      "company": "Accenture Technology Malaysia",
      "role": "Packaged App Development Associate",
      "location": "Kuala Lumpur",
      "startDate": "2025-03",
      "endDate": null,
      "description": "...",
      "highlights": [...]
    }
  ]
}
Purpose: Work experience timeline
```

---

## 10. UI/UX Requirements

### 10.1 Layout Structure

```
┌────────────────────────────────────────────────────────────┐
│  Navbar (sticky)                           [Dark/Light] 🌙 │
├────────────────────────────────────────────────────────────┤
│                                                            │
│                    HERO SECTION                            │
│              Full viewport height                          │
│         Name, Role, Tagline, CTA Button                   │
│                                                            │
├────────────────────────────────────────────────────────────┤
│                    ABOUT SECTION                           │
│         Brief narrative, photo, quick stats               │
├────────────────────────────────────────────────────────────┤
│                   PROJECTS SECTION                         │
│      Featured project (large) + Other projects (grid)     │
├────────────────────────────────────────────────────────────┤
│                    SKILLS SECTION                          │
│           Skills grouped by category with icons           │
├────────────────────────────────────────────────────────────┤
│                  EXPERIENCE SECTION                        │
│                  Vertical timeline                         │
├────────────────────────────────────────────────────────────┤
│                   CONTACT / FOOTER                         │
│            Social links, email, resume download           │
└────────────────────────────────────────────────────────────┘
```

### 10.2 Responsive Breakpoints

| Breakpoint | Width | Layout Adjustments |
|------------|-------|-------------------|
| Mobile | < 640px | Single column, stacked navigation |
| Tablet | 640px - 1024px | Two-column grids, expanded nav |
| Desktop | > 1024px | Full layout, max-width container |

### 10.3 Color Palette

**Dark Mode (Primary):**
- Background: `#0a192f` (deep navy)
- Surface: `#112240` (lighter navy)
- Text Primary: `#ccd6f6` (light slate)
- Text Secondary: `#8892b0` (slate)
- Accent: `#64ffda` (teal)
- Accent Hover: `#4fd1c5` (darker teal)

**Light Mode:**
- Background: `#ffffff` (white)
- Surface: `#f8fafc` (slate-50)
- Text Primary: `#1e293b` (slate-800)
- Text Secondary: `#64748b` (slate-500)
- Accent: `#0d9488` (teal-600)

### 10.4 Typography

| Element | Font | Size | Weight |
|---------|------|------|--------|
| Hero Title | Inter/Manrope | 48-72px | 700 |
| Section Headings | Inter/Manrope | 32-40px | 600 |
| Body Text | Inter | 16-18px | 400 |
| Code/Technical | JetBrains Mono | 14-16px | 400 |

---

## 11. Security Considerations

### 11.1 v1 Security (Read-Only Public Site)

| Concern | Mitigation |
|---------|------------|
| XSS | React's default escaping, no `dangerouslySetInnerHTML` |
| CORS | Restrict to frontend domain in production |
| Data Exposure | Only public portfolio data exposed |
| Dependency Vulnerabilities | Regular `npm audit`, `gradle dependencyCheckAnalyze` |

### 11.2 Future Security (If Admin Added)

| Concern | Mitigation |
|---------|------------|
| Authentication | JWT with httpOnly cookies |
| Authorization | Role-based access control |
| Password Storage | bcrypt hashing |
| CSRF | CSRF tokens for state-changing operations |

---

## 12. Deployment Strategy

### 12.1 Environment Configuration

| Environment | Frontend | Backend | Database |
|-------------|----------|---------|----------|
| Development | localhost:5173 | localhost:8080 | H2 in-memory |
| Production | Vercel/Netlify | Render/Railway | Supabase PostgreSQL |

### 12.2 CI/CD Pipeline (Future Enhancement)

```
Push to main → Run tests → Build → Deploy
```

### 12.3 Deployment Checklist

- [ ] Environment variables configured (not in code)
- [ ] Production database connection string secure
- [ ] CORS configured for production domain
- [ ] HTTPS enforced
- [ ] Seed data loaded
- [ ] Health check endpoint responding

---

## 13. Risk Assessment

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| Scope creep | High | Medium | Strict phase adherence, defer non-essentials |
| Animation performance issues | Medium | High | Test on low-end devices, performance budgets |
| Backend complexity | Low | Medium | Keep it simple, no over-engineering |
| Free tier limitations | Medium | Low | Monitor usage, have backup plans |
| Content bottleneck | Medium | Medium | Reuse existing documentation |
| Time pressure | Medium | High | Prioritize P0 requirements per phase |

---

## 14. Glossary

| Term | Definition |
|------|------------|
| SPA | Single Page Application |
| CORS | Cross-Origin Resource Sharing |
| JWT | JSON Web Token |
| DTO | Data Transfer Object |
| JPA | Java Persistence API |
| RBAC | Role-Based Access Control |
| OG Tags | Open Graph meta tags for social sharing |
| Lighthouse | Google's web performance auditing tool |

---

## Document History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | Dec 2025 | Faisal | Initial PRD creation |

---

**Approval:**

- [ ] Technical approach reviewed
- [ ] Phase scope agreed
- [ ] Ready to begin Phase 0
