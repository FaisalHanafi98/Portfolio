# 🚀 Portfolio Platform — Claude Code Session Prompt

**Project:** Personal Portfolio Platform  
**Owner:** Mohamad Faisal Bin Mohd Hanafi  
**Stack:** Spring Boot (Backend) + React/TypeScript (Frontend)  
**Document Version:** 1.0  
**Last Updated:** January 2026

---

## Token Optimization (@import from Root)

This project inherits token optimization rules from Root CLAUDE.md v1.2.0.

### Key Rules
1. **CLI-First**: Use `git` via Bash (NOT mcp__git__), native tools before MCP
2. **Response Calibration**: Match response length to task complexity
3. **Context Management**: Use `/clear` after discrete tasks, commit as checkpoints

### Project-Specific
- Backend verification: Use `curl` for API testing (port 8081 in dev)
- Frontend verification: Use Playwright MCP for browser testing
- Git operations: Use git CLI directly
- Build commands: `.\gradlew.bat bootRun` (backend), `npm run dev` (frontend)

@see ../CLAUDE.md for complete token optimization framework

---

## 🎯 Mission Statement

You are helping build a **dynamic, animated, data-driven portfolio platform** that serves as a central proof of engineering capability. This is NOT a static site—it demonstrates full-stack proficiency through a real Spring Boot API serving a polished React frontend.

The portfolio must impress both **non-technical recruiters** (visual polish, clear communication) and **technical hiring managers** (code quality, architecture decisions, modern patterns).

---

## 👤 About the Project Owner

**Faisal** is a Data Science graduate (IIUM, CGPA 3.72, Gold Medal FYP) currently working as Application Development Associate at Accenture Technology Malaysia. His unique positioning:

- **Data Science foundation** with hands-on ML, NLP, and analytics experience
- **Software engineering proficiency** learned on-the-job (React, TypeScript, Laravel)
- **Enterprise experience** building production-grade systems with RBAC, performance optimization
- **Certifications:** AWS Cloud Architecting, Azure AZ-900 & DP-900, Cloud Solutions Architect

**Key Projects to Showcase:**
1. **CREAMS** — Laravel rehabilitation center management system (50+ tables, 6-role RBAC, Gold Medal FYP)
2. **Auto-Recruit** — SPFx/React recruitment automation (15,000+ lines TypeScript, 95% complexity reduction)
3. **This Portfolio** — Spring Boot + React demonstration of full-stack capability

---

## 🎭 Actors & Their Goals

### Human Actors

| Actor | Goals | Attention Budget | Success Criteria |
|-------|-------|------------------|------------------|
| **Non-Technical Recruiter** | Quick legitimacy assessment | 30-60 seconds | "This person clearly knows what they're doing" |
| **Technical Hiring Manager** | Evaluate engineering depth | 2-5 minutes | Finds code worth reviewing, architecture impressive |
| **Faisal (Content Owner)** | Easy updates, honest representation | Ongoing | Maintainable, truthful, evolves with career |

### System Actors

| Actor | Responsibility |
|-------|---------------|
| **Frontend App** | Render UI, handle animations, call API, manage state |
| **Backend API** | Serve content, enforce business logic, return structured JSON |
| **Database** | Persist portfolio content (projects, skills, experience) |
| **Hosting Platform** | Serve both apps, manage environments |

---

## 🏗️ Technical Stack (Non-Negotiable)

### Backend
```
Framework:     Spring Boot 3.x (latest stable)
Language:      Java 17+
API Style:     RESTful JSON
Database:      PostgreSQL (production) / H2 (development)
Auth:          JWT-ready structure (not implemented in v1)
Build Tool:    Gradle
Documentation: OpenAPI/Swagger
```

### Frontend
```
Framework:     React 18+
Language:      TypeScript (strict mode, zero `any` types)
Build Tool:    Vite
Styling:       Tailwind CSS
Animation:     Framer Motion
State:         React Query or SWR for server state
Routing:       React Router v6
```

### Deployment Targets
```
Frontend:      Vercel or Netlify (free tier)
Backend:       Render or Railway (free tier)
Database:      Supabase or PlanetScale (free tier)
```

---

## 📁 Project Structure

```
portfolio-platform/
├── backend/                          # Spring Boot application
│   ├── src/main/java/dev/faisal/portfolio/
│   │   ├── PortfolioApplication.java
│   │   ├── config/
│   │   │   ├── CorsConfig.java
│   │   │   └── OpenApiConfig.java
│   │   ├── controller/
│   │   │   ├── ProjectController.java
│   │   │   ├── SkillController.java
│   │   │   ├── ExperienceController.java
│   │   │   └── HealthController.java
│   │   ├── service/
│   │   │   ├── ProjectService.java
│   │   │   ├── SkillService.java
│   │   │   └── ExperienceService.java
│   │   ├── repository/
│   │   │   ├── ProjectRepository.java
│   │   │   ├── SkillRepository.java
│   │   │   └── ExperienceRepository.java
│   │   ├── model/
│   │   │   ├── Project.java
│   │   │   ├── Skill.java
│   │   │   ├── SkillCategory.java
│   │   │   ├── Experience.java
│   │   │   └── Technology.java
│   │   ├── dto/
│   │   │   ├── ProjectSummaryDTO.java
│   │   │   ├── ProjectDetailDTO.java
│   │   │   └── ApiResponse.java
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       └── ResourceNotFoundException.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   ├── application-prod.yml
│   │   └── data.sql                  # Seed data
│   └── build.gradle
│
├── frontend/                         # React application
│   ├── src/
│   │   ├── main.tsx
│   │   ├── App.tsx
│   │   ├── api/
│   │   │   ├── client.ts             # Axios/fetch setup
│   │   │   └── hooks.ts              # React Query hooks
│   │   ├── components/
│   │   │   ├── layout/
│   │   │   │   ├── Navbar.tsx
│   │   │   │   ├── Footer.tsx
│   │   │   │   └── Layout.tsx
│   │   │   ├── sections/
│   │   │   │   ├── HeroSection.tsx
│   │   │   │   ├── AboutSection.tsx
│   │   │   │   ├── ProjectsSection.tsx
│   │   │   │   ├── SkillsSection.tsx
│   │   │   │   ├── ExperienceSection.tsx
│   │   │   │   └── ContactSection.tsx
│   │   │   ├── ui/
│   │   │   │   ├── Button.tsx
│   │   │   │   ├── Card.tsx
│   │   │   │   ├── Tag.tsx
│   │   │   │   └── AnimatedSection.tsx
│   │   │   └── projects/
│   │   │       ├── ProjectCard.tsx
│   │   │       ├── FeaturedProject.tsx
│   │   │       └── ProjectDetail.tsx
│   │   ├── pages/
│   │   │   ├── HomePage.tsx
│   │   │   └── ProjectPage.tsx
│   │   ├── hooks/
│   │   │   ├── useScrollPosition.ts
│   │   │   └── useTheme.ts
│   │   ├── types/
│   │   │   └── index.ts              # TypeScript interfaces
│   │   ├── styles/
│   │   │   └── globals.css
│   │   └── lib/
│   │       └── animations.ts         # Framer Motion variants
│   ├── public/
│   │   └── images/
│   ├── index.html
│   ├── vite.config.ts
│   ├── tailwind.config.js
│   └── tsconfig.json
│
├── docs/
│   ├── PORTFOLIO_PRD.md
│   ├── OPTIONAL_IMPROVEMENTS.md
│   └── API.md
│
└── README.md
```

---

## 🎨 Design Direction

### Visual Tone
- **"Confident minimalism with purposeful motion"**
- Dark mode primary (deep navy #0a192f or charcoal)
- Vibrant accent color (teal #64ffda or blue #60a5fa)
- Modern sans-serif typography (Inter or Manrope)
- Generous whitespace, content breathes

### Animation Philosophy
| Type | Purpose | Budget |
|------|---------|--------|
| Hero text reveal | First impression, personality | < 500ms |
| Page transitions | Smooth navigation feel | < 300ms |
| Scroll reveals | Guide attention progressively | Per-element |
| Card hovers | Interactive feedback | Instant (CSS) |
| Loading states | Perceived performance | During fetch |

**Rule:** If the user would notice the animation's *absence*, it's valuable. If they'd only notice its *presence*, it's noise.

### Mobile-First Requirement
- All layouts must be designed mobile-first
- Touch targets minimum 44x44px
- No horizontal scroll on any viewport
- Test at 375px (iPhone SE), 768px (iPad), 1440px (Desktop)

---

## 🔌 API Contract

### Base URL
```
Development: http://localhost:8081/api/v1
Production:  https://api.portfolio.faisal.dev/api/v1
```

### Endpoints

```
GET  /health                → Health check
GET  /projects              → List all projects (summary)
GET  /projects/{slug}       → Single project (full details)
GET  /skills                → Skills grouped by category
GET  /experience            → Work experience timeline
GET  /about                 → Bio and personal information
```

### Response Structure

```json
// Success response
{
  "success": true,
  "data": { ... },
  "timestamp": "2025-12-28T10:30:00Z"
}

// Error response
{
  "success": false,
  "error": {
    "code": "NOT_FOUND",
    "message": "Project not found"
  },
  "timestamp": "2025-12-28T10:30:00Z"
}
```

### Project Response Example

```json
// GET /projects
{
  "success": true,
  "data": [
    {
      "id": 1,
      "slug": "creams",
      "title": "CREAMS Rehabilitation Management System",
      "shortDescription": "Enterprise-grade Laravel system for rehabilitation centers",
      "technologies": ["Laravel", "PHP", "MySQL", "JavaScript"],
      "thumbnailUrl": "/images/creams-thumb.png",
      "featured": true
    }
  ]
}

// GET /projects/creams
{
  "success": true,
  "data": {
    "id": 1,
    "slug": "creams",
    "title": "CREAMS Rehabilitation Management System",
    "fullDescription": "A comprehensive management system...",
    "problemStatement": "Rehabilitation centers lacked...",
    "solution": "Built an enterprise-grade system...",
    "keyFeatures": [
      "6-role RBAC with granular permissions",
      "Session scheduling with competency tracking",
      "PDF/Excel reporting system"
    ],
    "technologies": [
      { "name": "Laravel", "category": "Backend" },
      { "name": "MySQL", "category": "Database" }
    ],
    "metrics": {
      "databaseTables": 50,
      "userRoles": 6,
      "recognition": "Gold Medal - Best FYP"
    },
    "images": [
      { "url": "/images/creams-dashboard.png", "alt": "Dashboard view" }
    ],
    "links": {
      "github": "https://github.com/FaisalHanafi/creams",
      "live": null
    },
    "period": "2023-2024"
  }
}
```

---

## ⚡ Performance Targets

| Metric | Target | Measurement |
|--------|--------|-------------|
| Lighthouse Performance | ≥ 90 | CI check |
| First Contentful Paint | < 1.0s | Chrome DevTools |
| Largest Contentful Paint | < 2.0s | Chrome DevTools |
| Cumulative Layout Shift | < 0.1 | Lighthouse |
| Time to Interactive | < 3.0s | Lighthouse |
| Bundle size (gzipped) | < 200KB | Build output |

---

## 🚫 Constraints & Anti-Patterns

### DO NOT:
- ❌ Use `any` type in TypeScript — ever
- ❌ Add Redux (Context + React Query is sufficient)
- ❌ Use heavy UI libraries that obscure fundamentals
- ❌ Over-engineer with microservices
- ❌ Add authentication in v1 (structure for it, don't implement)
- ❌ Create separate CSS/JS files for single components
- ❌ Use inline styles except for dynamic values
- ❌ Ignore mobile responsiveness
- ❌ Add features not in the current phase

### DO:
- ✅ Use TypeScript strict mode
- ✅ Write self-documenting code
- ✅ Keep components small and focused
- ✅ Use semantic HTML
- ✅ Add proper alt text to images
- ✅ Test on mobile viewport during development
- ✅ Commit frequently with meaningful messages
- ✅ Document non-obvious decisions

---

## 📋 Phase-Aware Development

This project follows a phased approach. **Always ask which phase we're working on** at the start of a session, then constrain work to that phase's scope.

| Phase | Focus | Duration |
|-------|-------|----------|
| 0 | Foundation & Scaffolding | 2-3 days |
| 1 | Backend API Complete | 3-4 days |
| 2 | Frontend Structure (No Animation) | 4-5 days |
| 3 | Animation & Polish | 3-4 days |
| 4 | Deployment | 1-2 days |
| 5 | Content & Ongoing Polish | Continuous |

See `PORTFOLIO_PRD.md` for detailed phase requirements.

---

## 🔄 Session Protocol

### At Session Start:
1. **Confirm current phase** — "Which phase are we working on?"
2. **Review phase requirements** — Check PRD for acceptance criteria
3. **Identify specific task** — What exactly are we building this session?
4. **Check dependencies** — Does this task depend on something incomplete?

### During Development:
1. **Stay in scope** — If tempted to add something from a future phase, note it but don't implement
2. **Test incrementally** — Verify each component works before moving on
3. **Ask clarifying questions** — Don't assume, especially about design decisions
4. **Document as you go** — Update comments, README when needed

### At Session End:
1. **Summarize completed work** — What was built?
2. **Note blockers** — What's preventing progress?
3. **Identify next steps** — What should the next session tackle?
4. **Commit with clear message** — `feat(phase-1): Add project endpoints with seed data`

---

## 📝 Content Reference

### Projects to Include

**1. CREAMS (Featured)**
- Full name: Community-based REhabilitAtions Management System
- Tech: Laravel 10, PHP 8, MySQL, JavaScript, HTML/CSS
- Highlights: 50+ database tables, 6-role RBAC, session scheduling, competency tracking, PDF/Excel reports
- Recognition: Gold Medal - Best FYP (Development Category)
- Period: 2023-2024

**2. Auto-Recruit**
- Full name: Recruitment Pipeline Automation System
- Tech: SPFx, React 17, TypeScript 5.3, Fluent UI
- Highlights: 15,000+ lines TypeScript, 95% complexity reduction, 6-role RBAC with 23 permissions, 68% page load improvement
- Note: Prototype demonstrating enterprise patterns (not production-deployed)
- Period: 2025

**3. This Portfolio**
- Tech: Spring Boot, React, TypeScript, Tailwind, Framer Motion
- Highlights: Dynamic API-driven content, animated UI, full-stack demonstration
- Period: 2025

**4. TeZer (Optional)**
- Full name: Text Summarizer
- Tech: Python, SpaCy, NLP
- Highlights: Extractive summarization, configurable summary length

### Experience Timeline

**Accenture Technology Malaysia** (March 2025 - Present)
- Role: Packaged App Development Associate (CL12)
- Focus: Enterprise application development, React/TypeScript, SharePoint

**AEM Energy Solutions** (August 2023 - March 2024)
- Role: Data Analyst Intern
- Focus: R programming, data analysis, oil & gas risk assessment

### Skills Categories

**Languages:** TypeScript, JavaScript, PHP, Python, Java, R, SQL
**Frontend:** React, HTML/CSS, Tailwind, Framer Motion
**Backend:** Spring Boot, Laravel, Node.js
**Data:** MySQL, PostgreSQL, Power BI
**Cloud:** AWS, Azure (AZ-900, DP-900)
**Tools:** Git, Docker, Vite

---

## 🎬 Quick Start Commands

```bash
# Backend (from /backend directory)
./gradlew bootRun                    # Start Spring Boot
./gradlew test                       # Run tests
./gradlew build                      # Build JAR

# Frontend (from /frontend directory)
npm install                          # Install dependencies
npm run dev                          # Start dev server
npm run build                        # Production build
npm run lint                         # Lint check
npm run type-check                   # TypeScript check
```

---

## ✅ Definition of Done

A feature is complete when:
1. Code compiles without errors
2. TypeScript has no type errors (frontend)
3. Component is responsive (tested at 375px, 768px, 1440px)
4. No console errors or warnings
5. Accessibility basics met (semantic HTML, alt text, keyboard nav)
6. Code is self-documenting or has necessary comments
7. Changes are committed with a meaningful message

---

**Remember:** This portfolio represents Faisal's engineering capability. Every line of code, every animation, every API response is a demonstration of skill. Build it like you're being interviewed through the codebase.
