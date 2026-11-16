package com.faisal.portfolio.seed;

import com.faisal.portfolio.experience.Experience;
import com.faisal.portfolio.experience.ExperienceRepository;
import com.faisal.portfolio.project.Project;
import com.faisal.portfolio.project.ProjectImage;
import com.faisal.portfolio.project.ProjectRepository;
import com.faisal.portfolio.project.Technology;
import com.faisal.portfolio.skill.Skill;
import com.faisal.portfolio.skill.SkillCategory;
import com.faisal.portfolio.skill.SkillCategoryRepository;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ProjectRepository projectRepository;
    private final SkillCategoryRepository skillCategoryRepository;
    private final ExperienceRepository experienceRepository;
    private final EntityManager entityManager;

    public DataSeeder(ProjectRepository projectRepository,
                      SkillCategoryRepository skillCategoryRepository,
                      ExperienceRepository experienceRepository,
                      EntityManager entityManager) {
        this.projectRepository = projectRepository;
        this.skillCategoryRepository = skillCategoryRepository;
        this.experienceRepository = experienceRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (projectRepository.count() > 0) {
            log.info("Data already seeded, skipping");
            return;
        }

        log.info("Seeding portfolio data...");
        seedTechnologiesAndProjects();
        seedSkills();
        seedExperiences();
        log.info("Seeding complete");
    }

    private void seedTechnologiesAndProjects() {
        // Technologies
        Technology laravel = tech("Laravel");
        Technology php = tech("PHP");
        Technology mysql = tech("MySQL");
        Technology javascript = tech("JavaScript");
        Technology react = tech("React");
        Technology typescript = tech("TypeScript");
        Technology springBoot = tech("Spring Boot");
        Technology java = tech("Java");
        Technology postgresql = tech("PostgreSQL");
        Technology python = tech("Python");
        Technology fastapi = tech("FastAPI");
        Technology redis = tech("Redis");
        Technology tailwind = tech("Tailwind CSS");
        Technology spfx = tech("SPFx");
        Technology sharepoint = tech("SharePoint");
        Technology fluentUi = tech("Fluent UI");
        Technology vite = tech("Vite");
        Technology docker = tech("Docker");
        Technology flyway = tech("Flyway");
        Technology xgboost = tech("XGBoost");
        Technology finbert = tech("FinBERT");
        Technology claudeAi = tech("Claude AI");

        // Project 1: CREAMS (Featured)
        Project creams = new Project();
        creams.setSlug("creams");
        creams.setTitle("CREAMS — Community Rehabilitation & Case Management System");
        creams.setShortDescription("Gold Medal FYP evolved into a production-grade Laravel case management platform for Malaysian PDK rehabilitation centres (Admin, Supervisor, Teacher, and AJK workflows) backed by 400+ automated PHPUnit and Playwright checks.");
        creams.setFullDescription("CREAMS is a full-stack rehabilitation centre management platform built for Malaysian community-based rehabilitation programmes (PDK). Originating as an IIUM Gold Medal Final Year Project, it was engineered into a multi-role operational system covering trainee enrolment, attendance, IEP/activity tracking, asset movements, and centre operations under strict Malaysian PDPA data-protection discipline.");
        creams.setProblemStatement("Rehabilitation centres relied on scattered paper forms and disconnected spreadsheets across staff roles, making it difficult to track trainee attendance, Individualised Education Programme (IEP) progress, centre assets, and operational reporting consistently.");
        creams.setSolution("Built a Laravel application with custom session authentication and role-scoped workflows for Admin, Supervisor, Teacher, and AJK staff. Reinforced the system with source-linked workflow contracts, schema-aligned Eloquent services, and over 400 automated PHPUnit and Playwright browser assertions using synthetic PDPA-safe fixtures.");
        creams.setFeatured(true);
        creams.setDisplayOrder(1);
        creams.setPeriod("2024 – 2026");
        creams.setGithubUrl("https://github.com/FaisalHanafi98/CREAMS");
        creams.setLiveUrl(null);
        creams.setTechnologies(Set.of(laravel, php, mysql, javascript, tailwind));
        creams.setArchitectureDecisions(List.of(
                "Role-scoped dashboards and middleware for Admin, Supervisor, Teacher, and AJK roles (ADR-002)",
                "Custom session authentication flow (POST /auth/check) with clean public route boundaries",
                "Service-layer separation for asset movements, attendance tracking, and trainee activity workflows",
                "PDPA-safe synthetic factories and seeders ensuring zero real trainee PII enters test or repository state",
                "End-to-end verification combining 400+ PHPUnit assertions against isolated MySQL 8.4 and 215 Playwright browser checks"
        ));
        creams.setTechStack(Map.of(
                "Backend", List.of("Laravel", "PHP 8.x", "Eloquent ORM"),
                "Frontend", List.of("Blade Templates", "JavaScript", "Tailwind CSS"),
                "Database", List.of("MySQL 8.4"),
                "Verification", List.of("PHPUnit (409 tests)", "Playwright E2E (215 tests)")
        ));
        creams.setChallenges(List.of(
                "Aligning legacy schema relations (such as asset movements and IEP goals) without breaking live role dashboards",
                "Eliminating N+1 query bottlenecks and unguarded relation loads across multi-centre views",
                "Enforcing strict Malaysian PDPA compliance across all seeders, logs, and regression fixtures"
        ));
        creams.setCostJustification("Originated as a Gold Medal Final Year Project at IIUM and matured for low-cost shared/VPS Linux hosting with MySQL and automated pre-commit regression gates.");

        ProjectImage creamsImg = new ProjectImage();
        creamsImg.setUrl("/images/creams.png");
        creamsImg.setAltText("CREAMS dashboard showing rehabilitation centre overview");
        creamsImg.setDisplayOrder(1);
        creamsImg.setProject(creams);
        creams.getImages().add(creamsImg);

        // Project 2: Auto-Recruit (Featured)
        Project autoRecruit = new Project();
        autoRecruit.setSlug("auto-recruit");
        autoRecruit.setTitle("Auto-Recruit — Enterprise SPFx Recruitment Pipeline");
        autoRecruit.setShortDescription("Enterprise SharePoint Framework (SPFx 1.21) recruitment automation platform built at Accenture Malaysia — refactored from a 2,520-line monolith into 50+ React/TypeScript components and 10 service modules with 68% faster page loads.");
        autoRecruit.setFullDescription("Auto-Recruit is an enterprise recruitment pipeline and Service Number (SN) management application built with SharePoint Framework (SPFx), React, TypeScript, and Fluent UI. Designed for Accenture Technology Malaysia workflows, it replaces fragmented Excel trackers with multi-level approvals (Supervisor to Hiring Manager), a 6-role / 23-permission RBAC engine, and real-time analytics.");
        autoRecruit.setProblemStatement("Recruitment teams tracked candidates and Service Numbers across disconnected Excel files shared over email. Feedback was delayed, role permissions were manual, and a 2,520-line monolithic prototype suffered from slow rendering and SharePoint 5,000-item list throttling.");
        autoRecruit.setSolution("Refactored the codebase (~15,300 LOC) into 50+ modular React/TypeScript components and 10 dedicated service modules (RBAC, Validation, SharePoint CRUD, Power Automate notifications, Outlook .ics scheduling, and retry resilience), cutting main component complexity by 95% and page load times by 68%.");
        autoRecruit.setFeatured(true);
        autoRecruit.setDisplayOrder(2);
        autoRecruit.setPeriod("Jul 2024 – 2025");
        autoRecruit.setTechnologies(Set.of(spfx, react, typescript, fluentUi, sharepoint));
        autoRecruit.setArchitectureDecisions(List.of(
                "Decomposed a 2,520-line monolithic component into 50+ focused React components and 10 service modules",
                "Dedicated RBACService enforcing 6 roles (Admin, Hiring Manager, Supervisor, Recruiter, Viewer, System) across 23 granular permissions",
                "ValidationService (500+ lines) centralizing form and business-rule validation across multi-stage approvals",
                "Server-side pagination, memoization, and debounced filtering to bypass SharePoint's 5,000-item threshold (tested up to 20,000+ records)"
        ));
        autoRecruit.setTechStack(Map.of(
                "Frontend", List.of("SPFx 1.21.1", "React 17", "TypeScript 5.3", "Fluent UI"),
                "Services & State", List.of("React Context", "10 Service Modules", "RBAC Engine (23 permissions)"),
                "Data & Automation", List.of("SharePoint Lists", "Power Automate", "Outlook .ics Calendar", "Excel/CSV Export")
        ));
        autoRecruit.setChallenges(List.of(
                "Overcoming SharePoint Online's 5,000-item list view threshold with indexed queries and server-side pagination",
                "Reducing dashboard calculation latency by 99% and initial page load by 68% through memoization and state decomposition",
                "Bridging LocalStorage prototype resilience with SharePoint REST API retry handling and Power Automate triggers"
        ));
        autoRecruit.setCostJustification("Zero incremental infrastructure cost — runs natively inside existing enterprise Microsoft 365 / SharePoint Online licences.");

        ProjectImage arImg = new ProjectImage();
        arImg.setUrl("https://placeholder.com/auto-recruit-list.png");
        arImg.setAltText("Auto-Recruit candidate pipeline in SharePoint");
        arImg.setDisplayOrder(1);
        arImg.setProject(autoRecruit);
        autoRecruit.getImages().add(arImg);

        // Project 3: BAiTI (Featured)
        Project baiti = new Project();
        baiti.setSlug("baiti");
        baiti.setTitle("BAiTI — Multi-Tenant Neighbourhood & RA Management Platform");
        baiti.setShortDescription("Multi-tenant Spring Boot + React platform for Malaysian residential associations featuring dual-role PostgreSQL Row-Level Security (FORCE RLS), 33 Flyway migrations, rotating JWT/cookie auth, and 330+ verified backend tests.");
        baiti.setFullDescription("BAiTI is a multi-tenant web platform designed for Malaysian neighbourhoods and landed Residents' Associations (RA). Each community operates in strict isolation with its own accounts, 42-permission RBAC catalogue, property blocks/units, and audit trail, backed by PostgreSQL 16 Row-Level Security and a React 18 + TypeScript workspace.");
        baiti.setProblemStatement("Residential committees manage unit occupancy, resident directories, billing, and maintenance across informal chat groups and spreadsheets, while naive multi-tenant schemas risk cross-community data leaks if a single query omits a tenant filter.");
        baiti.setSolution("Engineered a modular Spring Boot monolith with dual database roles (baiti_migrator for Flyway V1–V33 schema evolution and baiti_app bound by PostgreSQL FORCE RLS), pool-safe TenantAwareDataSource context binding, HS256 access tokens with SHA-256 rotating refresh cookies, and an OpenAPI-typed React 18 property and authentication workspace.");
        baiti.setFeatured(true);
        baiti.setDisplayOrder(3);
        baiti.setPeriod("2026 – Present");
        baiti.setGithubUrl("https://github.com/FaisalHanafi98/BAiTI");
        baiti.setTechnologies(Set.of(springBoot, java, postgresql, flyway, react, typescript, vite));
        baiti.setArchitectureDecisions(List.of(
                "Dual PostgreSQL roles (baiti_migrator vs baiti_app) with FORCE ROW LEVEL SECURITY across tenant tables and composite tenant foreign keys (V1–V33)",
                "Pool-safe TenantAwareDataSource that binds and resets tenant context per transaction and evicts tainted connections on rollback failure",
                "Separate platform onboarding plane vs tenant auth plane with first-use password replacement, rotating SHA-256 refresh tokens, and Web Locks tab coordination",
                "42-permission RBAC catalogue across 4 default roles (Admin, Manager, Resident, Guard) synchronized idempotently via advisory locks"
        ));
        baiti.setTechStack(Map.of(
                "Backend", List.of("Spring Boot", "Java 21", "Spring Security", "Hibernate 6 @TenantId"),
                "Database & Security", List.of("PostgreSQL 16 (FORCE RLS)", "Flyway (33 migrations)", "HS256 JWT + HttpOnly Refresh Cookies"),
                "Frontend", List.of("React 18", "TypeScript", "Vite", "OpenAPI Generated Client"),
                "Verification", List.of("330 JUnit/Integration Tests (I1–I9 RLS Gate)", "97.4% Changed-Line JaCoCo Coverage", "Vitest + Chrome Journeys")
        ));
        baiti.setChallenges(List.of(
                "Preventing PostgreSQL table-owner RLS bypass by separating migration ownership from runtime application queries",
                "Guaranteeing zero cross-tenant connection leakage under concurrent refresh-token replay and aborted transactions",
                "Preserving user form drafts on uncertain network write outcomes while returning neutral HTTP 404 responses for foreign tenant records"
        ));
        baiti.setCostJustification("Architected as a modular monolith over native PostgreSQL 16 RLS so dozens of neighbourhood communities can share a single database instance safely without per-tenant infrastructure sprawl.");

        // Project 4: RinggitSense (Featured)
        Project ringgitSense = new Project();
        ringgitSense.setSlug("ringgitsense");
        ringgitSense.setTitle("RinggitSense — Malaysian AI Personal Finance Platform");
        ringgitSense.setShortDescription("\"Sedar duit, sedar diri\" — Malaysian-first personal finance platform (FastAPI + React 18) with bank/e-wallet statement parsers and a 6-agent Claude AI pipeline for expense categorization, BNPL/debt detection, and cashflow forecasting.");
        ringgitSense.setFullDescription("RinggitSense is an AI-powered personal finance application tailored for Malaysian young professionals. It parses local bank statements (Maybank, CIMB, RHB, Public Bank, Hong Leong, Aeon) and e-wallet/BNPL records (Touch 'n Go, GrabPay, ShopeePay, SPayLater, Atome, highway tolls) through a sequential 6-agent Claude pipeline while enforcing strict financial accuracy and PDPA disclaimers.");
        ringgitSense.setProblemStatement("Generic budgeting apps fail on Malaysian statement formats, ignore local e-wallet and BNPL fragmentation (SPayLater, GrabPayLater, Touch 'n Go, PLUS/LDP tolls), and often hallucinate financial advice without traceable numbers or regulatory disclaimers.");
        ringgitSense.setSolution("Built a FastAPI + React 18 platform combining deterministic statement parsers (with credit-safe fingerprint deduplication) and a boundary-isolated 6-agent AI pipeline (AG-01 Categorizer -> AG-02 3-Tier Debt Detector -> AG-03 Pattern Analyzer -> AG-04 Predictor -> AG-05 Query -> AG-06 Advisor) where every output carries confidence scores and verbatim financial disclaimers.");
        ringgitSense.setFeatured(true);
        ringgitSense.setDisplayOrder(4);
        ringgitSense.setPeriod("2025 – 2026");
        ringgitSense.setGithubUrl("https://github.com/FaisalHanafi98/RinggitSense");
        ringgitSense.setTechnologies(Set.of(python, fastapi, react, typescript, postgresql, redis, claudeAi));
        ringgitSense.setArchitectureDecisions(List.of(
                "Strict 6-agent boundary architecture (AG-01 through AG-06) — each agent has one contract and never crosses domain boundaries",
                "Abstract statement parser factory with is_income-aware SHA fingerprint deduplication for Malaysian bank CSV/PDF uploads",
                "Financial accuracy protocol requiring traceable input citations, 0.0–1.0 confidence scores, and three mandatory verbatim disclaimers",
                "Structured JSON logging with request correlation IDs (RequestIDMiddleware) and centralized configuration validation"
        ));
        ringgitSense.setTechStack(Map.of(
                "Backend", List.of("Python 3.11", "FastAPI", "Pydantic", "SQLAlchemy"),
                "AI & Data Pipeline", List.of("Claude AI (6-Agent Pipeline)", "Custom Malaysian Bank Parsers"),
                "Frontend", List.of("React 18", "TypeScript", "Tailwind CSS"),
                "Data & Infra", List.of("PostgreSQL 15", "Redis 7", "Pytest Golden Dataset Suite")
        ));
        ringgitSense.setChallenges(List.of(
                "Normalizing inconsistent debit/credit sign conventions and Malaysian date formats across Maybank, CIMB, and e-wallet exports",
                "Preventing false duplicate drops when identical ringgit amounts occur as both a debit and a refund on the same day",
                "Enforcing deterministic compliance guardrails so AI advisor outputs never omit required Malaysian financial disclaimers"
        ));
        ringgitSense.setCostJustification("Designed with deterministic local statement parsing first so LLM tokens are spent only on structured summaries and bounded agent stages.");

        ProjectImage rsImg = new ProjectImage();
        rsImg.setUrl("/images/ringgitsense.png");
        rsImg.setAltText("RinggitSense Malaysian personal finance dashboard");
        rsImg.setDisplayOrder(1);
        rsImg.setProject(ringgitSense);
        ringgitSense.getImages().add(rsImg);

        // Project 5: Sen2Nal
        Project sen2nal = new Project();
        sen2nal.setSlug("sen2nal");
        sen2nal.setTitle("Sen2Nal — Stage-Gated Financial Sentiment & Signal Pipeline");
        sen2nal.setShortDescription("Six-agent sequential ML/NLP pipeline (FinBERT sentiment + VADER fallback, exchange calendar seasonality, and XGBoost walk-forward signals) paired with a FastAPI backend and React 'NightOps Terminal' dashboard (336 offline tests, ~80% coverage).");
        sen2nal.setFullDescription("Sen2Nal ('Signal over certainty') is a stage-gated financial NLP and machine learning research platform. It orchestrates six sequential agents (Ingestion -> FinBERT/VADER Sentiment -> Exchange Calendar -> Feature Engineering -> XGBoost Prediction -> Weekly LLM Experiment) with strict Pydantic write contracts, deterministic 08:00 America/New_York publication cutoffs, and a React + Vite NightOps Terminal UI.");
        sen2nal.setProblemStatement("Financial news and forum streams are noisy, and naive sentiment pipelines often leak future data across market close boundaries, silently pass partial stage failures as successful runs, or present black-box outputs without uncertainty decomposition.");
        sen2nal.setSolution("Replaced an early Streamlit prototype with a FastAPI + PostgreSQL backend and a React NightOps Terminal frontend. Implemented a stage-gated runner with schema-scoped PostgreSQL advisory locks, frozen SHA-256 as-of input manifests, walk-forward validation (252-day train / 21-day test, ADR-005), and 336 offline regression tests.");
        sen2nal.setFeatured(false);
        sen2nal.setDisplayOrder(5);
        sen2nal.setPeriod("2025 – 2026");
        sen2nal.setGithubUrl("https://github.com/FaisalHanafi98/Sen2Nal");
        sen2nal.setTechnologies(Set.of(python, fastapi, react, typescript, postgresql, finbert, xgboost));
        sen2nal.setArchitectureDecisions(List.of(
                "Stage-gated 6-agent pipeline (AG-01 Ingestion to AG-06 Experiment) that halts and rolls back pending writes on any critical stage contract failure",
                "Frozen, SHA-256 hashable publication and replay manifests enforcing an 08:00 America/New_York cutoff with zero look-ahead leakage",
                "Walk-forward validation (252 trading-day train, 21-day test per ADR-005) instead of random k-fold splits on time-series data",
                "Migrated from Streamlit to a dedicated React + Tailwind 'NightOps Terminal' dashboard backed by FastAPI and PostgreSQL advisory locks"
        ));
        sen2nal.setTechStack(Map.of(
                "Backend & Pipeline", List.of("Python 3.11", "FastAPI", "Pydantic", "SQLAlchemy", "PostgreSQL"),
                "ML & NLP", List.of("FinBERT", "VADER Sentiment", "XGBoost", "Scikit-learn", "Walk-Forward Validation"),
                "Frontend", List.of("React 18", "TypeScript", "Vite", "Tailwind CSS (NightOps Terminal)"),
                "Verification", List.of("Pytest (336 offline tests, 79.99% coverage)", "Ruff", "Mypy")
        ));
        sen2nal.setChallenges(List.of(
                "Ensuring partial ingestion or malformed sentiment batches trigger immediate transaction rollback rather than silent corrupt runs",
                "Preventing concurrent duplicate pipeline runs across API, CLI, and scheduler triggers using PostgreSQL transaction locks and queued worker claims",
                "Binding replay candidates to exact scorer references and pre-cutoff history while keeping prominent NOT FINANCIAL ADVICE risk disclosures"
        ));
        sen2nal.setCostJustification("Runs offline-capable VADER/FinBERT and XGBoost pipelines with local joblib artifact persistence and strict provider quota guards — zero paid API overages.");

        ProjectImage s2nImg = new ProjectImage();
        s2nImg.setUrl("/images/sen2nal.png");
        s2nImg.setAltText("Sen2Nal NightOps Terminal dashboard");
        s2nImg.setDisplayOrder(1);
        s2nImg.setProject(sen2nal);
        sen2nal.getImages().add(s2nImg);

        // Project 6: Portfolio (meta)
        Project portfolio = new Project();
        portfolio.setSlug("portfolio");
        portfolio.setTitle("Portfolio — Full-Stack Engineering Portfolio Platform");
        portfolio.setShortDescription("This site — a Spring Boot 3.4 + React 18 full-stack product with package-by-feature Java 21 records, resilient React Query request-state contracts, WCAG AA editorial notebook styling, and containerized Nginx + PostgreSQL delivery.");
        portfolio.setFullDescription("A full-stack portfolio application built to prove engineering capability through working software rather than static claims. The Spring Boot 3.4 API serves structured project case studies, skills, and experience from PostgreSQL/H2, while the React 18 frontend enforces accessible loading, offline-pause, cached-refresh fallback, and HTTP 404 states.");
        portfolio.setProblemStatement("Most developer portfolios are static templates with hardcoded copy and unverified claims. They neither demonstrate backend API design nor handle real-world frontend request states like offline pauses, failed background refreshes, or reduced-motion accessibility.");
        portfolio.setSolution("Built a Spring Boot 3.4 REST API (package-by-feature, immutable Java 21 DTO records, CommandLineRunner seeding, and HTTP Cache-Control headers) paired with a React 18 + TypeScript + Framer Motion frontend tested against 11 SSR state-contract assertions and a Docker Compose PostgreSQL smoke suite.");
        portfolio.setFeatured(false);
        portfolio.setDisplayOrder(6);
        portfolio.setPeriod("2025 – Present");
        portfolio.setGithubUrl("https://github.com/FaisalHanafi98/Portfolio");
        portfolio.setLiveUrl(null);
        portfolio.setTechnologies(Set.of(springBoot, java, postgresql, react, typescript, vite, tailwind, docker));
        portfolio.setArchitectureDecisions(List.of(
                "Package-by-feature (project/, skill/, experience/) with Java 21 record DTOs and a unified ApiResponse<T> envelope",
                "Shared QueryContent<T> React Query state boundary handling loading, offline pause, empty, error + retry + cached data, and confirmed HTTP 404 precedence",
                "MotionConfig reducedMotion='user' and compositor-safe transform/opacity transitions with WCAG AA (>= 4.5:1) colour tokens",
                "Multi-stage Docker Compose rehearsal pairing Nginx on :3000 with Spring Boot on :8080 and PostgreSQL 15"
        ));
        portfolio.setTechStack(Map.of(
                "Backend", List.of("Spring Boot 3.4.2", "Java 21", "Spring Data JPA", "PostgreSQL 15 / H2"),
                "Frontend", List.of("React 18", "TypeScript 5", "Vite 6", "Tailwind CSS", "TanStack Query", "Framer Motion"),
                "Infrastructure", List.of("Docker Compose", "Nginx", "systemd", "GitHub Actions CI"),
                "Testing", List.of("JUnit 5 + MockMvc", "Node SSR State Contract Tests (11 cases)", "Playwright Viewport & A11y Checks")
        ));
        portfolio.setChallenges(List.of(
                "Tuning Spring Boot and HikariCP for small single-node VPS memory budgets (1GB RAM)",
                "Preserving cached content during transient API refresh failures while letting a confirmed HTTP 404 override stale project cache",
                "Building a CI/CD pipeline that verifies backend tests, frontend lint/type-check/state tests, and an isolated Compose smoke stack"
        ));
        portfolio.setCostJustification("Designed for single-node VPS deployment (Nginx serving the Vite bundle and proxying /api/ to a private Spring Boot service backed by PostgreSQL) after retiring the initial AWS Lightsail instance.");

        projectRepository.saveAll(List.of(creams, autoRecruit, baiti, ringgitSense, sen2nal, portfolio));
    }

    private void seedSkills() {
        // Backend & Architecture
        SkillCategory backend = category("Backend & Architecture", 1);
        addSkill(backend, "Java 21 / Spring Boot", 85, 1);
        addSkill(backend, "Python 3.11 / FastAPI", 85, 2);
        addSkill(backend, "PHP 8 / Laravel", 85, 3);
        addSkill(backend, "PostgreSQL (RLS) & MySQL", 85, 4);
        addSkill(backend, "Flyway & REST / OpenAPI", 80, 5);
        addSkill(backend, "Redis", 70, 6);

        // Frontend & Enterprise UI
        SkillCategory frontend = category("Frontend & Enterprise UI", 2);
        addSkill(frontend, "React 18", 85, 1);
        addSkill(frontend, "TypeScript 5", 85, 2);
        addSkill(frontend, "SharePoint Framework (SPFx)", 85, 3);
        addSkill(frontend, "Tailwind CSS & Fluent UI", 85, 4);
        addSkill(frontend, "TanStack Query & Framer Motion", 80, 5);

        // AI, Data & Analytics
        SkillCategory dataMl = category("AI, Data & Analytics", 3);
        addSkill(dataMl, "Multi-Agent LLM Pipelines", 80, 1);
        addSkill(dataMl, "FinBERT / VADER NLP", 75, 2);
        addSkill(dataMl, "XGBoost & Walk-Forward ML", 75, 3);
        addSkill(dataMl, "Power BI (Power Query / DAX)", 85, 4);
        addSkill(dataMl, "Pandas / R / Regex ETL", 80, 5);

        // Cloud, DevOps & Quality
        SkillCategory cloudDevops = category("Cloud, DevOps & Quality", 4);
        addSkill(cloudDevops, "Docker & Compose", 75, 1);
        addSkill(cloudDevops, "GitHub Actions CI/CD", 80, 2);
        addSkill(cloudDevops, "Playwright / Vitest / JUnit 5", 85, 3);
        addSkill(cloudDevops, "Linux / Nginx / systemd", 75, 4);

        // Ecosystem & Tooling
        SkillCategory tools = category("Ecosystem & Tooling", 5);
        addSkill(tools, "Git & Multi-Worktree Flows", 90, 1);
        addSkill(tools, "SharePoint Online & Power Automate", 85, 2);
        addSkill(tools, "IntelliJ IDEA & VS Code", 90, 3);
        addSkill(tools, "Postman & OpenAPI", 85, 4);

        skillCategoryRepository.saveAll(List.of(backend, frontend, dataMl, cloudDevops, tools));
    }

    private void seedExperiences() {
        Experience accenture = new Experience();
        accenture.setCompany("Accenture Technology Malaysia");
        accenture.setRole("Packaged App Development Associate");
        accenture.setLocation("Kuala Lumpur, Malaysia");
        accenture.setStartDate(LocalDate.of(2024, 7, 1));
        accenture.setEndDate(null);
        accenture.setDescription("Working on enterprise workflow and analytics delivery, helping translate business processes into systems that are faster, clearer, and easier for teams to trust across the SharePoint, React, TypeScript, and Power BI ecosystem.");
        accenture.setHighlights(List.of(
                "Built and maintained enterprise SharePoint Framework (SPFx) and React/TypeScript workflow solutions, including Auto-Recruit",
                "Improved page load performance by roughly 68% and implemented 20+ validation rules across 28+ workflow updates",
                "Supported Power BI reporting and cross-functional delivery across Malaysia and regional teams"
        ));
        accenture.setDisplayOrder(1);

        Experience aem = new Experience();
        aem.setCompany("AEM Energy Solutions");
        aem.setRole("Data Analyst Intern");
        aem.setLocation("Kuala Lumpur, Malaysia");
        aem.setStartDate(LocalDate.of(2023, 6, 1));
        aem.setEndDate(LocalDate.of(2023, 9, 30));
        aem.setDescription("Focused on data extraction, validation, and standardization so operational and client-facing reporting could be trusted.");
        aem.setHighlights(List.of(
                "Built ETL routines with R and regex to extract, clean, validate, and standardize data across 43 templates and 37 fields",
                "Supported reporting preparation and structured data quality checks for operational workflows"
        ));
        aem.setDisplayOrder(2);

        Experience iium = new Experience();
        iium.setCompany("International Islamic University Malaysia (IIUM)");
        iium.setRole("Bachelor of Computer Science (Data Science)");
        iium.setLocation("Kuala Lumpur, Malaysia");
        iium.setStartDate(LocalDate.of(2020, 9, 1));
        iium.setEndDate(LocalDate.of(2024, 6, 30));
        iium.setDescription("Graduated with a CGPA of 3.72, combining data science foundations with full-stack software engineering. Final year project (CREAMS) received the Gold Medal at the university FYP showcase.");
        iium.setHighlights(List.of(
                "Gold Medal — Final Year Project (CREAMS: Community Rehabilitation Case Management System)",
                "Dean's List recognition for 9 semesters (CGPA 3.72 / 4.00)",
                "Coursework across Data Science, Software Engineering, Database Systems, Algorithms, and AI"
        ));
        iium.setDisplayOrder(3);

        experienceRepository.saveAll(List.of(accenture, aem, iium));
    }

    private Technology tech(String name) {
        Technology t = new Technology();
        t.setName(name);
        entityManager.persist(t);
        return t;
    }

    private SkillCategory category(String name, int order) {
        SkillCategory c = new SkillCategory();
        c.setName(name);
        c.setDisplayOrder(order);
        return c;
    }

    private void addSkill(SkillCategory category, String name, int proficiency, int order) {
        Skill s = new Skill();
        s.setName(name);
        s.setProficiency(proficiency);
        s.setDisplayOrder(order);
        s.setCategory(category);
        category.getSkills().add(s);
    }
}
