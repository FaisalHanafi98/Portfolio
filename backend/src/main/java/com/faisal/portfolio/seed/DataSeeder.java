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
        boolean emptyPortfolio = projectRepository.count() == 0;
        seedTechnologiesAndProjects(emptyPortfolio);

        if (emptyPortfolio) {
            seedSkills();
            seedExperiences();
        }
        log.info("Portfolio content seeding complete");
    }

    private void seedTechnologiesAndProjects(boolean emptyPortfolio) {
        // Technologies
        Technology laravel = tech("Laravel");
        Technology php = tech("PHP");
        Technology mysql = tech("MySQL");
        Technology javascript = tech("JavaScript");
        Technology bootstrap = tech("Bootstrap");
        Technology react = tech("React");
        Technology typescript = tech("TypeScript");
        Technology springBoot = tech("Spring Boot");
        Technology java = tech("Java");
        Technology postgresql = tech("PostgreSQL");
        Technology python = tech("Python");
        Technology fastapi = tech("FastAPI");
        Technology tailwind = tech("Tailwind CSS");
        Technology vite = tech("Vite");
        Technology docker = tech("Docker");
        Technology flyway = tech("Flyway");
        Technology vader = tech("VADER");
        Technology finbert = tech("FinBERT");

        if (emptyPortfolio) {
            seedAdditionalProjects(react, typescript, python, fastapi, postgresql);
        } else {
            demoteProject("auto-recruit", 4);
            demoteProject("ringgitsense", 5);
        }

        Project creams = flagship("creams", 1);
        creams.setTitle("CREAMS — Community Rehabilitation Centre Operations & Funding Platform");
        creams.setShortDescription("Centre-scoped software for attendance, rehabilitation operations and document handling at Malaysian community rehabilitation centres.");
        creams.setFullDescription("CREAMS started as a final-year project to consolidate rehabilitation-centre workflows. Its architecture separates staff responsibilities and constrains centre-owned data through Eloquent scopes and route-level authorisation.");
        creams.setProblemStatement("Centre staff need structured attendance, operations and reporting while handling sensitive records across locations.");
        creams.setSolution("Built a Laravel application with role-scoped workflows, central centre filtering, relationship scopes for asset records and private document handling.");
        creams.setPeriod("2024 – 2026");
        creams.setGithubUrl("https://github.com/FaisalHanafi98/CREAMS");
        updateTechnologies(creams, Set.of(laravel, php, mysql, javascript, bootstrap));
        creams.setArchitectureDecisions(List.of(
                "Eloquent CentreScope filters centre-owned records and relationship scopes apply centre ownership to child asset records.",
                "Role checks distinguish ordinary centre access from documented administrator and background execution paths.",
                "Private document routes restrict access to sensitive PDF records, with automated checks for centre and role boundaries."
        ));
        creams.setTechStack(Map.of(
                "Backend", List.of("Laravel 12", "PHP", "Eloquent ORM", "Session authentication"),
                "Frontend", List.of("Blade", "Bootstrap 5", "JavaScript", "Vite"),
                "Data & verification", List.of("MySQL", "PHPUnit", "Playwright")
        ));
        creams.setChallenges(List.of(
                "Define centre ownership for both direct records and child records that inherit access.",
                "Separate scoped staff workflows from the documented global administrator and system paths."
        ));
        creams.setCostJustification("The project demonstrates how centre-specific authorisation and structured workflows support usable, auditable rehabilitation operations.");

        if (creams.getImages().isEmpty()) {
            ProjectImage creamsImg = new ProjectImage();
            creamsImg.setUrl("/images/creams.png");
            creamsImg.setAltText("CREAMS dashboard showing rehabilitation centre overview");
            creamsImg.setDisplayOrder(1);
            creamsImg.setProject(creams);
            creams.getImages().add(creamsImg);
        }

        Project baiti = flagship("baiti", 3);
        baiti.setTitle("BAiTI — Multi-Tenant Neighbourhood Operations, Procurement & Policy Platform");
        baiti.setShortDescription("A multi-tenant property workspace for Malaysian neighbourhoods with database-enforced isolation.");
        baiti.setFullDescription("BAiTI is a modular Spring Boot and React platform for neighbourhood operations. The locally verified scope includes the authentication shell and property workflows for blocks and units. Procurement, reserve planning and policy assistance are future work.");
        baiti.setProblemStatement("Neighbourhood committees need a shared place for property records with a clear boundary between communities.");
        baiti.setSolution("Built tenant-scoped property workflows backed by PostgreSQL row-level security, with separately privileged migration and runtime roles.");
        baiti.setPeriod("2026 – Present");
        baiti.setGithubUrl(null);
        updateTechnologies(baiti, Set.of(springBoot, java, postgresql, flyway, react, typescript, vite));
        baiti.setArchitectureDecisions(List.of(
                "Shared-schema tenancy combines application tenant context with PostgreSQL FORCE ROW LEVEL SECURITY.",
                "Flyway owns schema changes through a separate migration role. The runtime connection pool uses a restricted role.",
                "Tenant-aware connection handling manages context on pooled connections. Native PostgreSQL tests exercise property APIs and isolation."
        ));
        baiti.setTechStack(Map.of(
                "Backend", List.of("Java 21", "Spring Boot", "Spring Security", "Hibernate"),
                "Data", List.of("PostgreSQL 16", "Row-level security", "Flyway"),
                "Frontend", List.of("React", "TypeScript", "Vite", "OpenAPI client")
        ));
        baiti.setChallenges(List.of(
                "Fail closed when tenant context is missing and prevent context reuse across pooled connections.",
                "Keep property screens and access rules aligned across admin and tenant workflows."
        ));
        baiti.setCostJustification("Local PostgreSQL and browser checks verify the property workspace. The shared-schema design keeps deployment and schema migration simpler for a small team.");

        Project sen2nal = flagship("sen2nal", 2);
        sen2nal.setTitle("Sen2Nal — Market Sentiment Research Journal & Signal Replay Platform");
        sen2nal.setShortDescription("A market-research pipeline that records stage outcomes and freezes time-bounded evidence for reproducible research.");
        sen2nal.setFullDescription("Sen2Nal uses a staged Python pipeline for market inputs, sentiment, calendar features and experiments. Its tested replay contract freezes eligible source versions at 08:00 New York time. Live replay publication and provider validation remain open. Research only. Not financial advice.");
        sen2nal.setProblemStatement("Financial news and calendar signals can conflict. Research needs to identify what evidence was available at the decision cutoff.");
        sen2nal.setSolution("Built a staged research pipeline with typed write contracts, run-correlated status and frozen SHA-256 input manifests. Verified VADER scoring with local fixtures.");
        sen2nal.setPeriod("2025 – 2026");
        sen2nal.setGithubUrl("https://github.com/FaisalHanafi98/Sen2Nal");
        updateTechnologies(sen2nal, Set.of(python, fastapi, react, typescript, postgresql, vader, finbert));
        sen2nal.setArchitectureDecisions(List.of(
                "Reject late or ambiguous revisions at the 08:00 America/New_York cutoff and hash the eligible manifest for replay.",
                "Correlate stage results with a saved run ID and roll back pending writes on failure. Earlier committed stages retain their records.",
                "Keep VADER verification separate from the FinBERT runtime and model-rights gate. Use a chronological evaluation design for later experiments."
        ));
        sen2nal.setTechStack(Map.of(
                "Pipeline & API", List.of("Python", "FastAPI", "Pydantic", "SQLAlchemy"),
                "Storage & research", List.of("PostgreSQL", "SHA-256 manifests", "VADER", "FinBERT integration"),
                "Frontend & verification", List.of("React", "TypeScript", "Vite", "Pytest")
        ));
        sen2nal.setChallenges(List.of(
                "Exclude information unavailable at the cutoff and preserve the version actually selected.",
                "Keep empty, partial, failed and withheld research states distinct."
        ));
        sen2nal.setCostJustification("The offline work makes the evidence cutoff and run status inspectable. Live replay and model quality require separate verification.");

        if (sen2nal.getImages().isEmpty()) {
            ProjectImage s2nImg = new ProjectImage();
            s2nImg.setUrl("/images/sen2nal.png");
            s2nImg.setAltText("Sen2Nal NightOps Terminal dashboard");
            s2nImg.setDisplayOrder(1);
            s2nImg.setProject(sen2nal);
            sen2nal.getImages().add(s2nImg);
        }

        List.of(creams, sen2nal, baiti).stream()
                .filter(project -> project.getId() == null)
                .forEach(entityManager::persist);
    }

    private void updateTechnologies(Project project, Set<Technology> technologies) {
        if (!project.getTechnologies().equals(technologies)) {
            project.getTechnologies().clear();
            project.getTechnologies().addAll(technologies);
        }
    }

    private Project flagship(String slug, int order) {
        Project project = projectRepository.findBySlug(slug).orElseGet(Project::new);
        project.setSlug(slug);
        project.setFeatured(true);
        project.setDisplayOrder(order);
        return project;
    }

    private void demoteProject(String slug, int order) {
        projectRepository.findBySlug(slug).ifPresent(project -> {
            project.setFeatured(false);
            project.setDisplayOrder(order);
        });
    }

    private void seedAdditionalProjects(Technology react, Technology typescript, Technology python,
                                        Technology fastapi, Technology postgresql) {
        Technology redis = tech("Redis");
        Technology spfx = tech("SPFx");
        Technology sharepoint = tech("SharePoint");
        Technology fluentUi = tech("Fluent UI");
        Technology claudeAi = tech("Claude AI");

        Project autoRecruit = new Project();
        autoRecruit.setSlug("auto-recruit");
        autoRecruit.setTitle("Auto-Recruit — Enterprise Recruitment & Approval Automation");
        autoRecruit.setShortDescription("Replaced disconnected Excel trackers and email chains at Accenture Malaysia with a centralized recruitment workflow application — helping recruiters, supervisors, and hiring managers track 20,000+ candidate records without losing applicants between handoffs.");
        autoRecruit.setFullDescription("Auto-Recruit is an enterprise recruitment pipeline and Service Number (SN) management application built inside Microsoft SharePoint Online at Accenture Technology Malaysia. Instead of temporary recruiters, supervisors, and hiring managers passing spreadsheets back and forth over email, everyone works from a single role-controlled workspace with automated status tracking, interview scheduling, and weekly reporting.");
        autoRecruit.setProblemStatement("Recruiters and hiring managers were tracking hundreds of open roles across separate Excel files and email threads. Candidates fell through the cracks during supervisor handoffs, duplicate or restricted submissions weren't caught early, and preparing weekly pipeline reports took hours of manual spreadsheet cleanup.");
        autoRecruit.setSolution("Designed and refactored the SharePoint application into a fast, role-based workspace for 6 user roles (Recruiter, Supervisor, Hiring Manager, Resource Manager, Admin, Viewer). Built automated checks for duplicate candidates, client anti-poaching rules, and 6-month retry windows, alongside one-click calendar invites and automated Friday pipeline reports.");
        autoRecruit.setFeatured(false);
        autoRecruit.setDisplayOrder(4);
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

        Project ringgitSense = new Project();
        ringgitSense.setSlug("ringgitsense");
        ringgitSense.setTitle("RinggitSense — Malaysian Personal Finance, Statement Parser & Scenario Planner");
        ringgitSense.setShortDescription("Helps Malaysians turn raw bank and e-wallet statements into a clear view of their real spending, hidden BNPL/informal debt, and monthly savings scenarios — without manual expense logging or hallucinated math.");
        ringgitSense.setFullDescription("RinggitSense ('Sedar duit, sedar diri') is a personal finance platform built specifically for how Malaysians actually spend and borrow. Instead of forcing users to type every expense by hand, it parses local bank CSV/PDF statements, normalizes messy merchant names (DuitNow, Touch 'n Go, Grab, tolls), separates formal loans from BNPL and informal 'hutang', and lets users test realistic monthly budget and savings scenarios.");
        ringgitSense.setProblemStatement("Global budgeting apps don't understand Malaysian bank statement formats or local spending habits — where money is split across Maybank/CIMB accounts, e-wallets, SPayLater/Atome BNPL plans, and informal family transfers. Meanwhile, generic AI finance tools often invent numbers or give unsafe advice.");
        ringgitSense.setSolution("Built a FastAPI and React platform that combines deterministic statement parsing and duplicate detection with a 6-stage AI workflow and an interactive Financial Goals & Scenario Planner. Every calculation uses exact decimal math, ambiguous transfers ask for user confirmation instead of guessing, and all outputs carry explicit financial disclaimers.");
        ringgitSense.setFeatured(false);
        ringgitSense.setDisplayOrder(5);
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

        projectRepository.saveAll(List.of(autoRecruit, ringgitSense));
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
        return entityManager.createQuery(
                        "SELECT t FROM Technology t WHERE t.name = :name ORDER BY t.id", Technology.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList().stream()
                .findFirst()
                .orElseGet(() -> {
                    Technology technology = new Technology();
                    technology.setName(name);
                    entityManager.persist(technology);
                    return technology;
                });
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
