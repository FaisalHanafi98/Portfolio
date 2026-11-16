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

        // Project 1: Auto-Recruit (Featured)
        Project autoRecruit = new Project();
        autoRecruit.setSlug("auto-recruit");
        autoRecruit.setTitle("Auto-Recruit — Enterprise Recruitment & Approval Automation");
        autoRecruit.setShortDescription("Replaced disconnected Excel trackers and email chains at Accenture Malaysia with a centralized recruitment workflow application — helping recruiters, supervisors, and hiring managers track 20,000+ candidate records without losing applicants between handoffs.");
        autoRecruit.setFullDescription("Auto-Recruit is an enterprise recruitment pipeline and Service Number (SN) management application built inside Microsoft SharePoint Online at Accenture Technology Malaysia. Instead of temporary recruiters, supervisors, and hiring managers passing spreadsheets back and forth over email, everyone works from a single role-controlled workspace with automated status tracking, interview scheduling, and weekly reporting.");
        autoRecruit.setProblemStatement("Recruiters and hiring managers were tracking hundreds of open roles across separate Excel files and email threads. Candidates fell through the cracks during supervisor handoffs, duplicate or restricted submissions weren't caught early, and preparing weekly pipeline reports took hours of manual spreadsheet cleanup.");
        autoRecruit.setSolution("Designed and refactored the SharePoint application into a fast, role-based workspace for 6 user roles (Recruiter, Supervisor, Hiring Manager, Resource Manager, Admin, Viewer). Built automated checks for duplicate candidates, client anti-poaching rules, and 6-month retry windows, alongside one-click calendar invites and automated Friday pipeline reports.");
        autoRecruit.setFeatured(true);
        autoRecruit.setDisplayOrder(1);
        autoRecruit.setPeriod("Mar 2025 – Feb 2026");
        autoRecruit.setTechnologies(Set.of(spfx, react, typescript, fluentUi, sharepoint));
        autoRecruit.setArchitectureDecisions(List.of(
                "Collaborated on refactoring a monolithic 2,520-line React component into 50+ modular components and 10 service modules (~77% main-component reduction)",
                "Split global state into dedicated Auth, Data, and UI contexts with memoized selectors, cutting dashboard calculation time from 500ms to 5ms",
                "Engineered server-side pagination ($top, $skiptoken), 7 indexed columns, and progressive 100-row loading to handle 20,000+ records within SharePoint's 5,000-item threshold",
                "Enforced 23 granular RBAC permissions and 20+ business validation rules (duplicate email detection, client anti-poaching, 6-month cooling period)"
        ));
        autoRecruit.setTechStack(Map.of(
                "Frontend", List.of("SPFx 1.21.1", "React 17", "TypeScript 5.3 (Strict Mode)", "Fluent UI"),
                "Architecture", List.of("3 Context Providers (Auth, Data, UI)", "10 Service Modules", "5 Custom Hooks"),
                "Enterprise Integration", List.of("SharePoint Lists", "Power Automate", "Outlook RFC 5545 .ics Calendar", "Excel/CSV Export")
        ));
        autoRecruit.setChallenges(List.of(
                "Keeping search and filtering responsive across 20,000+ candidate records despite SharePoint Online's strict 5,000-item view threshold",
                "Eliminating full-tree React re-renders that slowed down daily recruiter data entry — achieving 68% faster page loads",
                "Translating complex enterprise hiring rules (multi-stage supervisor-to-HM approvals and anti-poaching checks) into clear, foolproof form validation"
        ));
        autoRecruit.setCostJustification("Business Result: Cut page load times by 68%, eliminated manual weekly spreadsheet merging, prevented lost candidates across handoffs, and ran at zero extra infrastructure cost using existing Microsoft 365 licences.");

        ProjectImage arImg = new ProjectImage();
        arImg.setUrl("https://placeholder.com/auto-recruit-list.png");
        arImg.setAltText("Auto-Recruit candidate pipeline in SharePoint");
        arImg.setDisplayOrder(1);
        arImg.setProject(autoRecruit);
        autoRecruit.getImages().add(arImg);

        // Project 2: CREAMS (Featured)
        Project creams = new Project();
        creams.setSlug("creams");
        creams.setTitle("CREAMS — Community Rehabilitation Centre Operations & Funding Platform");
        creams.setShortDescription("Digitized daily attendance, trainee rehabilitation plans, and programme budget tracking for Malaysian community rehabilitation centres (PDK) — replacing paper files so teachers and supervisors can focus on supporting children with special needs.");
        creams.setFullDescription("CREAMS is a web-based operational platform designed for Malaysian Community-Based Rehabilitation Centres (PPDK). It replaces scattered paper files and manual spreadsheets with a unified system where staff record daily trainee attendance, track Individualised Education Plans (IEP), reconcile attendance-linked allowances, and manage grant/donation budgets with strict centre-level privacy.");
        creams.setProblemStatement("Teachers and supervisors at rehabilitation centres were spending hours managing paper attendance rosters, manual progress notes, and disconnected budget spreadsheets. Records were hard to audit, allowance eligibility had to be cross-checked by hand, and sensitive trainee medical/personal data needed strict protection between centres.");
        creams.setSolution("Built a multi-role Laravel platform tailored for Admin, Supervisor, Teacher, and Committee (AJK) workflows. Added central data isolation across 25 data models so each centre only sees its own records, private document storage for sensitive PDFs, an Attendance-to-Allowance Reconciler that flags discrepancies for human review, and end-to-end Programme Funding & Budget tracking.");
        creams.setFeatured(true);
        creams.setDisplayOrder(2);
        creams.setPeriod("2024 – 2026");
        creams.setGithubUrl("https://github.com/FaisalHanafi98/CREAMS");
        creams.setLiveUrl(null);
        creams.setTechnologies(Set.of(laravel, php, mysql, javascript, tailwind));
        creams.setArchitectureDecisions(List.of(
                "Centralized tenant isolation across 25 Eloquent models (23 direct CentreScope models + 2 relationship-scoped asset models) so one missing query filter never leaks another centre's records",
                "Role-specific workflows and custom session authentication (Admin, Supervisor, Teacher, AJK) with LiteSpeed cache-bypass and logout session hardening",
                "Programme Funding & Budget Management and Attendance-to-Allowance Reconciler modules connecting grants, expenditure approvals, and attendance eligibility",
                "Private storage wrappers for sensitive medical/financial PDFs and pre-commit NRIC/IC scrubbing hooks to protect trainee privacy"
        ));
        creams.setTechStack(Map.of(
                "Backend", List.of("Laravel 12", "PHP 8.2", "Eloquent ORM", "Custom Session Auth"),
                "Frontend", List.of("Blade Templates", "Bootstrap 5", "JavaScript", "Vite"),
                "Database", List.of("MySQL 8.4 (35+ migrations, 50+ models)", "SQLite CI Compatibility"),
                "Verification & Quality", List.of("PHPUnit Regression Suite", "Playwright Browser Tests", "DomPDF & Excel Reporting")
        ));
        creams.setChallenges(List.of(
                "Isolating centre-owned data across 25 models — including assets and maintenance records that inherit ownership through parent relationships",
                "Connecting daily trainee attendance records with allowance eligibility rules and programme grant budgets without losing audit traceability",
                "Managing framework upgrades (Laravel 10 -> 11 -> 12) and rolling back an incompatible Laravel 13 upgrade to match PHP 8.2 hosting constraints"
        ));
        creams.setCostJustification("Business Result: Gave rehabilitation staff a single structured place for attendance, progress tracking, and grant accountability while keeping sensitive trainee records isolated by centre.");

        ProjectImage creamsImg = new ProjectImage();
        creamsImg.setUrl("/images/creams.png");
        creamsImg.setAltText("CREAMS dashboard showing rehabilitation centre overview");
        creamsImg.setDisplayOrder(1);
        creamsImg.setProject(creams);
        creams.getImages().add(creamsImg);

        // Project 3: RinggitSense (Featured)
        Project ringgitSense = new Project();
        ringgitSense.setSlug("ringgitsense");
        ringgitSense.setTitle("RinggitSense — Malaysian Personal Finance, Statement Parser & Scenario Planner");
        ringgitSense.setShortDescription("Helps Malaysians turn raw bank and e-wallet statements into a clear view of their real spending, hidden BNPL/informal debt, and monthly savings scenarios — without manual expense logging or hallucinated math.");
        ringgitSense.setFullDescription("RinggitSense ('Sedar duit, sedar diri') is a personal finance platform built specifically for how Malaysians actually spend and borrow. Instead of forcing users to type every expense by hand, it parses local bank CSV/PDF statements, normalizes messy merchant names (DuitNow, Touch 'n Go, Grab, tolls), separates formal loans from BNPL and informal 'hutang', and lets users test realistic monthly budget and savings scenarios.");
        ringgitSense.setProblemStatement("Global budgeting apps don't understand Malaysian bank statement formats or local spending habits — where money is split across Maybank/CIMB accounts, e-wallets, SPayLater/Atome BNPL plans, and informal family transfers. Meanwhile, generic AI finance tools often invent numbers or give unsafe advice.");
        ringgitSense.setSolution("Built a FastAPI and React platform that combines deterministic statement parsing and duplicate detection with a 6-stage AI workflow and an interactive Financial Goals & Scenario Planner. Every calculation uses exact decimal math, ambiguous transfers ask for user confirmation instead of guessing, and all outputs carry explicit financial disclaimers.");
        ringgitSense.setFeatured(true);
        ringgitSense.setDisplayOrder(3);
        ringgitSense.setPeriod("2025 – 2026");
        ringgitSense.setGithubUrl("https://github.com/FaisalHanafi98/RinggitSense");
        ringgitSense.setTechnologies(Set.of(python, fastapi, react, typescript, postgresql, redis, claudeAi));
        ringgitSense.setArchitectureDecisions(List.of(
                "Three-tier Malaysian debt model distinguishing Formal Loans (banks/PTPTN), BNPL instalments (SPayLater/Atome), and Informal Hutang (person-to-person transfers)",
                "Extensible bank statement parser registry (Maybank, CIMB) with credit-safe SHA fingerprint deduplication so legitimate repeat purchases aren't dropped as duplicates",
                "Financial Goals & Scenario Planner, Merchant Normaliser, and Tax-Relief Receipt Vault modules using exact-money arithmetic and deterministic query routing",
                "Bounded 6-agent Claude architecture with Pydantic output contracts, confidence scores (0.0–1.0), and [NEEDS INPUT] abstention when statement evidence is ambiguous"
        ));
        ringgitSense.setTechStack(Map.of(
                "Backend", List.of("Python 3.11", "FastAPI (Async)", "SQLAlchemy 2.0", "Pydantic v2"),
                "AI & Domain Logic", List.of("Claude Sonnet 4 (6-Agent Design)", "Malaysian Statement Parsers", "Exact-Decimal Scenario Engine"),
                "Frontend", List.of("React", "TypeScript", "Tailwind CSS", "TanStack Query"),
                "Data & Verification", List.of("PostgreSQL 15 + pgvector", "Alembic", "Pytest Contract & Golden Dataset Suite")
        ));
        ringgitSense.setChallenges(List.of(
                "Normalizing cryptic Malaysian bank statement descriptions and inconsistent debit/credit sign conventions across different local banks",
                "Distinguishing a genuine duplicate upload from two identical RM transactions (or a same-day refund) on the same date",
                "Ensuring AI agents never hallucinate balances or skip required Malaysian financial disclaimers by keeping math deterministic"
        ));
        ringgitSense.setCostJustification("Business Result: Eliminates hours of manual expense entry, surfaces hidden BNPL and informal debt obligations clearly, and gives users trustworthy, verifiable monthly savings projections.");

        ProjectImage rsImg = new ProjectImage();
        rsImg.setUrl("/images/ringgitsense.png");
        rsImg.setAltText("RinggitSense Malaysian personal finance dashboard");
        rsImg.setDisplayOrder(1);
        rsImg.setProject(ringgitSense);
        ringgitSense.getImages().add(rsImg);

        // Project 4: BAiTI (Featured)
        Project baiti = new Project();
        baiti.setSlug("baiti");
        baiti.setTitle("BAiTI — Multi-Tenant Neighbourhood Operations, Procurement & Policy Platform");
        baiti.setShortDescription("Gives Malaysian Residents' Associations (RAs) and neighbourhood committees a secure, isolated workspace to manage housing units, maintenance quotations, vendor work orders, and community by-laws without WhatsApp chaos.");
        baiti.setFullDescription("BAiTI is a multi-tenant web platform built for Malaysian residential communities and landed Residents' Associations. Committee members and property managers use it to manage blocks and units, compare vendor quotations for maintenance repairs, track arrears and reserve funds, and look up community by-laws — while database-level security guarantees one neighbourhood can never access another's records.");
        baiti.setProblemStatement("Neighbourhood committees often run operations over WhatsApp groups and shared spreadsheets — making it hard to track which units are occupied, compare contractor quotes fairly, audit maintenance spending, or find exact clauses in community by-laws. In multi-community software, a single missing database filter can also leak resident data across neighbourhoods.");
        baiti.setSolution("Built a Spring Boot and React platform where every neighbourhood is isolated at the PostgreSQL database layer (FORCE Row-Level Security). Delivered role-scoped property management (Admin, Manager, Resident, Guard), a Maintenance-to-Vendor Procurement workflow with exact-decimal quotation comparison, Arrears & Reserve Planning, and a Tenant-Scoped Policy Assistant with source citations.");
        baiti.setFeatured(true);
        baiti.setDisplayOrder(4);
        baiti.setPeriod("2026 – Present");
        baiti.setGithubUrl("https://github.com/FaisalHanafi98/BAiTI");
        baiti.setTechnologies(Set.of(springBoot, java, postgresql, flyway, react, typescript, vite));
        baiti.setArchitectureDecisions(List.of(
                "Dual PostgreSQL database roles (baiti_migrator for Flyway schema migrations vs baiti_app bound by FORCE ROW LEVEL SECURITY) so even table-owner defaults cannot bypass tenant isolation",
                "Pool-safe TenantAwareDataSource that binds and resets community context per transaction and evicts connections on rollback failure",
                "Vendor & Procurement Management module linking maintenance requests to RFQs, quotation comparisons, auditable approvals, and exact-decimal work orders",
                "Role-scoped workspace (42 permissions across Admin, Manager, Resident, and Guard) with rotating HttpOnly refresh cookies and multi-tab Web Locks coordination"
        ));
        baiti.setTechStack(Map.of(
                "Backend", List.of("Spring Boot", "Java 21", "Spring Security", "Hibernate 6 @TenantId"),
                "Database & Isolation", List.of("PostgreSQL 16 (FORCE RLS)", "Flyway (33 SQL Migrations)", "Composite Tenant Foreign Keys"),
                "Frontend", List.of("React 18", "TypeScript", "Vite", "OpenAPI Generated Client"),
                "Verification", List.of("330 Automated Backend & RLS Isolation Tests", "168-Case Permission Matrix", "Vitest & Chrome E2E Journeys")
        ));
        baiti.setChallenges(List.of(
                "Guaranteeing that a bug in application code can never expose one residential community's records or vendor quotes to another tenant",
                "Preserving committee members' unsaved form drafts when network connections drop, while returning neutral HTTP 404 responses for unauthorized records",
                "Making vendor quotation comparisons, approvals, and reserve-fund scenarios transparent and auditable for volunteer committee members"
        ));
        baiti.setCostJustification("Business Result: Replaces scattered committee spreadsheets with auditable property and vendor workflows, while allowing dozens of neighbourhoods to share one database safely.");

        // Project 5: Sen2Nal (Featured)
        Project sen2nal = new Project();
        sen2nal.setSlug("sen2nal");
        sen2nal.setTitle("Sen2Nal — Market Sentiment Research Journal & Signal Replay Platform");
        sen2nal.setShortDescription("Cuts through noisy financial news and forum hype by decomposing market sentiment, calendar seasonality, and historical context into an inspectable daily research journal — so researchers can audit why a signal appeared.");
        sen2nal.setFullDescription("Sen2Nal ('Signal over certainty') is a financial NLP and market-research platform designed to make market information easier to interrogate. Instead of acting as a black-box stock predictor, it collects news and forum discussions, scores financial sentiment using FinBERT and VADER, checks exchange calendar seasonality, and locks each day's evidence into a timestamped Research Journal & Signal Replay snapshot.");
        sen2nal.setProblemStatement("Investors and analysts face a flood of conflicting financial news headlines, social media noise, and black-box AI tips. Worse, many backtests accidentally leak future news into past predictions, making noisy signals look artificially accurate until real money is on the line.");
        sen2nal.setSolution("Built a 6-stage Python and FastAPI pipeline paired with a React 'NightOps Terminal' interface. Each stage validates its data before passing it forward, rolls back incomplete runs automatically, and stores frozen pre-market (08:00 New York time) evidence snapshots across 50 US companies so users can replay and audit exact historical signals without hindsight bias.");
        sen2nal.setFeatured(true);
        sen2nal.setDisplayOrder(5);
        sen2nal.setPeriod("2025 – 2026");
        sen2nal.setGithubUrl("https://github.com/FaisalHanafi98/Sen2Nal");
        sen2nal.setTechnologies(Set.of(python, fastapi, react, typescript, postgresql, finbert, xgboost));
        sen2nal.setArchitectureDecisions(List.of(
                "Stage-gated 6-agent pipeline (Ingestion -> FinBERT/VADER Sentiment -> Exchange Calendar -> Features -> XGBoost -> Experiment) that halts and rolls back on any critical data-contract failure",
                "Research Journal & Signal Replay module with SHA-256 frozen input manifests at an 08:00 America/New_York cutoff so later news can never contaminate earlier research snapshots",
                "Rolling walk-forward validation (252 trading-day training window / 21-day test window per ADR-005) to respect chronological market order",
                "Calibration & Drift Monitor and Historical Evidence Search separating operational data/feed health warnings from statistical model claims"
        ));
        sen2nal.setTechStack(Map.of(
                "Backend & Pipeline", List.of("Python 3.11", "FastAPI", "Pydantic Data Contracts", "SQLAlchemy", "PostgreSQL"),
                "NLP & Modeling", List.of("FinBERT (Financial BERT)", "VADER Fallback", "XGBoost", "Walk-Forward Validation (ADR-005)"),
                "Frontend", List.of("React 18", "TypeScript", "Vite", "Tailwind CSS (NightOps Terminal)"),
                "Verification", List.of("Pytest Suite (336 Offline Tests)", "5 Architecture Decision Records", "Ruff & Mypy")
        ));
        sen2nal.setChallenges(List.of(
                "Preventing partial API failures or malformed news batches from silently corrupting downstream sentiment and feature tables",
                "Designing a replayable research journal that proves exactly what news and scores were available before market open on any given day",
                "Communicating uncertainty, component score conflicts (sentiment vs calendar), and prominent NOT FINANCIAL ADVICE boundaries clearly in the UI"
        ));
        sen2nal.setCostJustification("Business Result: Turns noisy, unstructured financial news into an auditable, time-stamped research workflow where every score can be traced back to its exact source article.");

        ProjectImage s2nImg = new ProjectImage();
        s2nImg.setUrl("/images/sen2nal.png");
        s2nImg.setAltText("Sen2Nal NightOps Terminal dashboard");
        s2nImg.setDisplayOrder(1);
        s2nImg.setProject(sen2nal);
        sen2nal.getImages().add(s2nImg);

        projectRepository.saveAll(List.of(autoRecruit, creams, ringgitSense, baiti, sen2nal));
    }

    private void seedSkills() {
        // 1. Data Engineering, ETL & BI
        SkillCategory dataEtl = category("Data Engineering, ETL & BI", 1);
        addSkill(dataEtl, "Python & R (Data Wrangling / ETL)", 85, 1);
        addSkill(dataEtl, "SQL (PostgreSQL / MySQL)", 85, 2);
        addSkill(dataEtl, "Power BI (Power Query M & DAX)", 85, 3);
        addSkill(dataEtl, "Regex & Multi-Template Profiling", 85, 4);
        addSkill(dataEtl, "Migration Reconciliation & QA/QC", 85, 5);
        addSkill(dataEtl, "Excel / VBA7 & Win32 Automation", 85, 6);

        // 2. Full-Stack & Enterprise Applications
        SkillCategory fullStack = category("Full-Stack & Enterprise Software", 2);
        addSkill(fullStack, "React & TypeScript", 85, 1);
        addSkill(fullStack, "SharePoint Framework (SPFx) & Fluent UI", 85, 2);
        addSkill(fullStack, "PHP & Laravel (Blade / Eloquent)", 85, 3);
        addSkill(fullStack, "Python FastAPI & Pydantic", 85, 4);
        addSkill(fullStack, "Java 21 & Spring Boot", 80, 5);
        addSkill(fullStack, "REST APIs & OpenAPI Contracts", 85, 6);

        // 3. Applied AI & Decision Pipelines
        SkillCategory appliedAi = category("Applied AI & Decision Systems", 3);
        addSkill(appliedAi, "Multi-Agent LLM Workflows (Claude API)", 80, 1);
        addSkill(appliedAi, "Financial NLP (FinBERT & VADER)", 80, 2);
        addSkill(appliedAi, "XGBoost & Walk-Forward Validation", 75, 3);
        addSkill(appliedAi, "Deterministic Guardrails & Schema Validation", 85, 4);

        // 4. Security, Architecture & Quality
        SkillCategory securityQuality = category("Architecture, Security & Verification", 4);
        addSkill(securityQuality, "Multi-Tenant Isolation & PostgreSQL RLS", 85, 1);
        addSkill(securityQuality, "Role-Based Access Control (RBAC)", 85, 2);
        addSkill(securityQuality, "Automated Testing (Pytest, PHPUnit, JUnit, Playwright)", 85, 3);
        addSkill(securityQuality, "Flyway & Alembic Schema Migrations", 80, 4);
        addSkill(securityQuality, "Git, CI/CD (GitHub Actions) & Docker", 80, 5);

        // 5. Industrial Systems, Hardware & Cloud
        SkillCategory infraHardware = category("Industrial Systems, Hardware & Cloud", 5);
        addSkill(infraHardware, "Equipment-Data Integration (SECS/GEM Context)", 75, 1);
        addSkill(infraHardware, "Custom PC & NAS Builds (10+ ATX / mATX / Mini-ITX)", 90, 2);
        addSkill(infraHardware, "Workstation Diagnostics, Repair & WSL/Linux", 90, 3);
        addSkill(infraHardware, "Microsoft 365, Power Automate & SharePoint Online", 85, 4);
        addSkill(infraHardware, "AWS & Azure Cloud Fundamentals", 80, 5);

        skillCategoryRepository.saveAll(List.of(dataEtl, fullStack, appliedAi, securityQuality, infraHardware));
    }

    private void seedExperiences() {
        Experience ase = new Experience();
        ase.setCompany("ASE Electronics (M) Sdn. Bhd.");
        ase.setRole("Automation Engineer Trainee (Industrial OJT — Test Operations)");
        ase.setLocation("Bayan Lepas, Penang");
        ase.setStartDate(LocalDate.of(2026, 7, 27));
        ase.setEndDate(LocalDate.of(2026, 10, 14));
        ase.setDescription("Built zero-additional-dependency reporting automations, reconciled physical-to-digital tester inventories, and supported equipment-data integration on restricted semiconductor cleanroom workstations.");
        ase.setHighlights(List.of(
                "Automated legacy query client (FwQuery.exe / Bei Bei) extraction using installed Excel, 64-bit VBA7, and native Win32 APIs — cutting T650* remnant buffer reporting time by an estimated 88.5% (~45 min manual work to ~28 sec in trial comparisons) and reducing navigation from 35+ keystrokes to 6",
                "Engineered multi-source Overview reporting (Daily WIP, Finished Goods, Test Front End) stamping ~120 binning rows in under 15 seconds, and redesigned 4-shift output tracking separating Wafer Sort and Final Test plans",
                "Reconciled physical and spreadsheet records across 400+ semiconductor electrical testers (Advantest, Chroma, Teradyne), resolving duplicate entries and shared-customer allocations",
                "Contributed to SECS/GEM-related integration connecting legacy software with newer testers and handlers to support sensor-derived production output, operating state, and downtime tracking"
        ));
        ase.setDisplayOrder(1);

        Experience accenture = new Experience();
        accenture.setCompany("Accenture Technology Malaysia");
        accenture.setRole("Software Engineer Associate / Packaged App Development Associate (CL12)");
        accenture.setLocation("Kuala Lumpur, Malaysia");
        accenture.setStartDate(LocalDate.of(2025, 3, 1));
        accenture.setEndDate(LocalDate.of(2026, 2, 28));
        accenture.setDescription("Delivered enterprise workflow software and internal Business Intelligence reporting, bridging full-stack React/TypeScript/SPFx application engineering with Power BI analytics.");
        accenture.setHighlights(List.of(
                "Collaborated on refactoring a monolithic enterprise recruitment application into 50+ modular React/TypeScript components and 10 service modules (~77% main-component size reduction), cutting page load time by 68% and dashboard calculation time from 500 ms to 5 ms",
                "Designed server-side pagination, column indexing, and progressive loading to handle 20,000+ candidate records within SharePoint Online's 5,000-item threshold",
                "Implemented a 6-role, 23-permission RBAC engine, 20+ validation rules (duplicate checks, anti-poaching, 6-month retry enforcement), Power Automate flows, and RFC 5545 .ics interview scheduling",
                "Built internal delivery-centre Business Intelligence dashboards using Power BI, Power Query (M), and DAX to consolidate multi-sheet Excel trackers into stakeholder KPI views"
        ));
        accenture.setDisplayOrder(2);

        Experience aem = new Experience();
        aem.setCompany("AEM Enersol Sdn. Bhd.");
        aem.setRole("Data Analyst Intern (Petroleum Engineering & Data Solutions)");
        aem.setLocation("Kuala Lumpur, Malaysia");
        aem.setStartDate(LocalDate.of(2023, 8, 1));
        aem.setEndDate(LocalDate.of(2024, 2, 2));
        aem.setDescription("Executed industrial data extraction, multi-template profiling, formatting-aware transformation, and before-and-after migration reconciliation for petroleum engineering engagements.");
        aem.setHighlights(List.of(
                "Contributed to a Halliburton risk-assessment ETL engagement: profiled a 5,687-sheet Excel corpus, selected 697 relevant reports across 43 distinct templates, and standardized output into a 37-column dataframe using R, Python, and regex — helping the team deliver two weeks ahead of schedule",
                "Implemented formatting-aware extraction to detect and exclude superseded strikethrough text, followed by field-level QA/QC and anomaly inspection",
                "Onboarded wellbore auxiliary schematics into WORA 7 (DROID 2.0 / WEQR) for pre-UAT validation, reconciled before-and-after database migration datasets, and supported EnQuest well-barrier and workover Master Production Mapping (MPM)"
        ));
        aem.setDisplayOrder(3);

        Experience iium = new Experience();
        iium.setCompany("International Islamic University Malaysia (IIUM)");
        iium.setRole("BSc (Hons) in Computer Science — Data Science & Computational Intelligence");
        iium.setLocation("Kuala Lumpur, Malaysia");
        iium.setStartDate(LocalDate.of(2020, 9, 1));
        iium.setEndDate(LocalDate.of(2024, 3, 13));
        iium.setDescription("Built a strong foundation in data science, statistical analysis, machine learning, and software engineering, culminating in the CREAMS rehabilitation management system.");
        iium.setHighlights(List.of(
                "Specialized in Data Science and Computational Intelligence with coursework across Machine Learning, Natural Language Processing, Big Data Analysis, Databases, and Software Engineering",
                "Originated CREAMS (Community Rehabilitation Management System) as a Final Year Project to digitize operations for Malaysian PDK rehabilitation centres",
                "Combined academic data-modeling rigor with practical full-stack application development"
        ));
        iium.setDisplayOrder(4);

        experienceRepository.saveAll(List.of(ase, accenture, aem, iium));
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
