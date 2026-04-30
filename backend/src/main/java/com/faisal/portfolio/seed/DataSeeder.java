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
        if (projectRepository.count() == 0) {
            log.info("Seeding portfolio data...");
            seedTechnologiesAndProjects();
            seedSkills();
            seedExperiences();
            log.info("Seeding complete");
        }
        refreshNarrativeContent();
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
        Technology streamlit = tech("Streamlit");
        Technology tailwind = tech("Tailwind CSS");
        Technology spfx = tech("SPFx");
        Technology sharepoint = tech("SharePoint");
        Technology vite = tech("Vite");
        Technology docker = tech("Docker");

        // Project 1: CREAMS (Featured)
        Project creams = new Project();
        creams.setSlug("creams");
        creams.setTitle("CREAMS — Community Rehabilitation & Case Management System");
        creams.setShortDescription("Gold medal FYP — full-stack rehabilitation case management system handling 50+ database tables across 6 user roles with complete CRUD operations, reporting, and role-based access control.");
        creams.setFullDescription("CREAMS is a comprehensive web-based case management system built for the Department of Social Welfare Malaysia. It digitizes the rehabilitation workflow for community-based programs, replacing manual paper-based processes with a structured digital system that tracks clients from intake through discharge.");
        creams.setProblemStatement("Rehabilitation officers were managing cases using paper forms and spreadsheets, leading to lost records, inconsistent reporting, and no way to track client progress over time. Each officer handled 30-50 active cases with no centralized system.");
        creams.setSolution("Built a Laravel-based multi-role platform with 6 distinct user types (Admin, Officer, Supervisor, Counselor, Client, Guest), each with tailored dashboards and permissions. Implemented full case lifecycle management with automated reporting and audit trails.");
        creams.setFeatured(true);
        creams.setDisplayOrder(1);
        creams.setPeriod("Jan – Jun 2024");
        creams.setGithubUrl(null);
        creams.setLiveUrl("https://faisalhanafi.com/creams/demo/");
        creams.setTechnologies(Set.of(laravel, php, mysql, javascript, tailwind));
        creams.setArchitectureDecisions(List.of(
                "Laravel MVC with service layer for complex business logic separation",
                "6-role RBAC using Laravel middleware and policy-based authorization",
                "Eloquent ORM with eager loading to prevent N+1 queries on dashboard views",
                "Blade templating with reusable component library for consistent UI across roles",
                "MySQL with normalized schema design across 50+ tables for data integrity"
        ));
        creams.setTechStack(Map.of(
                "Backend", List.of("Laravel 10", "PHP 8.2"),
                "Frontend", List.of("Blade Templates", "JavaScript", "Tailwind CSS"),
                "Database", List.of("MySQL 8"),
                "Infrastructure", List.of("XAMPP (dev)", "Apache")
        ));
        creams.setChallenges(List.of(
                "Designing a permission matrix that correctly isolates data across 6 user roles without over-complicating the middleware stack",
                "Building dynamic PDF report generation with different layouts for case summaries, progress reports, and discharge forms",
                "Handling concurrent case updates when multiple officers collaborate on the same client record"
        ));
        creams.setCostJustification("Academic FYP project — zero infrastructure cost. Deployed on university-provided hosting during evaluation. Won Gold Medal at IIUM FYP showcase.");

        ProjectImage creamsImg = new ProjectImage();
        creamsImg.setUrl("https://placeholder.com/creams-dashboard.png");
        creamsImg.setAltText("CREAMS dashboard showing rehabilitation case overview");
        creamsImg.setDisplayOrder(1);
        creamsImg.setProject(creams);
        creams.getImages().add(creamsImg);

        // Project 2: Auto-Recruit
        Project autoRecruit = new Project();
        autoRecruit.setSlug("auto-recruit");
        autoRecruit.setTitle("Auto-Recruit — Enterprise Recruitment Automation");
        autoRecruit.setShortDescription("Enterprise SPFx recruitment solution built on SharePoint Online that automates candidate tracking, interview scheduling, and hiring workflows for Accenture Malaysia.");
        autoRecruit.setFullDescription("Auto-Recruit is a SharePoint Framework (SPFx) web part that replaces manual recruitment tracking spreadsheets with a structured, permission-controlled application embedded directly in the SharePoint environment that hiring managers already use daily.");
        autoRecruit.setProblemStatement("Recruitment teams were tracking candidates across disconnected Excel files shared via email. No single source of truth existed — interview feedback was lost, candidate status was outdated, and hiring managers had no visibility into pipeline progress.");
        autoRecruit.setSolution("Built a React-based SPFx web part that integrates directly with SharePoint lists as its data layer. Hiring managers interact with a modern UI while SharePoint handles permissions, versioning, and data storage — no separate database needed.");
        autoRecruit.setFeatured(true);
        autoRecruit.setDisplayOrder(2);
        autoRecruit.setPeriod("Jul – Dec 2024");
        autoRecruit.setTechnologies(Set.of(spfx, react, typescript, sharepoint));
        autoRecruit.setArchitectureDecisions(List.of(
                "SPFx web part over standalone app — zero additional infrastructure, lives inside existing SharePoint",
                "SharePoint Lists as data layer — inherits enterprise permissions, versioning, and backup for free",
                "React with TypeScript for type-safe component development within SPFx constraints",
                "PnPjs library for simplified SharePoint API calls instead of raw REST endpoints"
        ));
        autoRecruit.setTechStack(Map.of(
                "Frontend", List.of("React 17", "TypeScript 4.x", "SPFx 1.18"),
                "Data Layer", List.of("SharePoint Lists", "PnPjs"),
                "Infrastructure", List.of("SharePoint Online", "Microsoft 365")
        ));
        autoRecruit.setChallenges(List.of(
                "Working within SPFx bundle size limits while maintaining rich UI interactions",
                "Handling SharePoint list throttling (5000 item threshold) with indexed columns and CAML queries",
                "Building offline-capable features when SharePoint API latency spikes during peak hours"
        ));
        autoRecruit.setCostJustification("Zero infrastructure cost — runs entirely on existing Microsoft 365 E5 licenses. No servers, no database, no hosting fees. SharePoint handles storage, auth, and backups.");

        ProjectImage arImg = new ProjectImage();
        arImg.setUrl("https://placeholder.com/auto-recruit-list.png");
        arImg.setAltText("Auto-Recruit candidate listing in SharePoint");
        arImg.setDisplayOrder(1);
        arImg.setProject(autoRecruit);
        autoRecruit.getImages().add(arImg);

        // Project 3: Sen2Nal
        Project sen2nal = new Project();
        sen2nal.setSlug("sen2nal");
        sen2nal.setTitle("Sen2Nal — Stock Sentiment Analysis Platform");
        sen2nal.setShortDescription("ML/NLP-driven sentiment analysis platform for Bursa Malaysia stocks, combining news sentiment scoring with technical indicators to surface market signals.");
        sen2nal.setFullDescription("Sen2Nal scrapes and analyzes financial news articles related to Bursa Malaysia-listed stocks, applies NLP sentiment analysis, and presents the results alongside price data through an interactive Streamlit dashboard. It is designed as a research tool, not trading advice.");
        sen2nal.setProblemStatement("Retail investors in Malaysia lack access to sentiment analysis tools that cover Bursa Malaysia stocks specifically. Most existing tools focus on US markets and do not process Malay-language financial news sources.");
        sen2nal.setSolution("Built a Python pipeline that scrapes Malaysian financial news, runs NLP sentiment analysis using pre-trained transformer models, and correlates sentiment scores with stock price movements. Results are presented through a Streamlit dashboard with filtering and visualization.");
        sen2nal.setFeatured(false);
        sen2nal.setDisplayOrder(3);
        sen2nal.setPeriod("2025 – Present");
        sen2nal.setGithubUrl("https://github.com/FaisalHanafi/Sen2Nal");
        sen2nal.setTechnologies(Set.of(python, streamlit));
        sen2nal.setArchitectureDecisions(List.of(
                "Streamlit for rapid prototyping — skip frontend development entirely for MVP",
                "Pandas + Scikit-learn pipeline for data processing and feature engineering",
                "Pre-trained transformer models (FinBERT) for financial sentiment classification",
                "Modular pipeline design — scraper, analyzer, and visualizer as separate components"
        ));
        sen2nal.setTechStack(Map.of(
                "Application", List.of("Python 3.11", "Streamlit"),
                "Data Processing", List.of("Pandas", "NumPy"),
                "ML/NLP", List.of("Scikit-learn", "FinBERT", "Transformers"),
                "Data Sources", List.of("Web scraping", "Bursa Malaysia API")
        ));
        sen2nal.setChallenges(List.of(
                "Handling mixed Malay/English financial news articles with a single NLP pipeline",
                "Managing API rate limits from financial data providers without missing market hours data",
                "Validating sentiment scores against actual price movements to measure prediction accuracy"
        ));
        sen2nal.setCostJustification("Free tier infrastructure — Streamlit Community Cloud for hosting, GitHub for code. Only cost is API calls to financial data providers during development.");

        // Project 4: Portfolio (meta)
        Project portfolio = new Project();
        portfolio.setSlug("portfolio");
        portfolio.setTitle("Portfolio — Personal Developer Portfolio");
        portfolio.setShortDescription("This very site — Spring Boot 3.4 API with package-by-feature architecture, React 18 frontend with Framer Motion animations, deployed on AWS Lightsail with CI/CD via GitHub Actions.");
        portfolio.setFullDescription("A full-stack portfolio website built to showcase engineering projects with proper backend architecture, not just a static site. The backend serves as a real API with structured data, caching, and proper error handling. The frontend features smooth animations and responsive design.");
        portfolio.setProblemStatement("Most developer portfolios are static sites with hardcoded content. Updating projects means editing HTML. There is no structured data, no API, and the site itself does not demonstrate backend engineering skills.");
        portfolio.setSolution("Built a proper full-stack application where project data lives in PostgreSQL, served through a REST API with DTOs and response wrappers. The frontend consumes the API with React Query for caching. The site itself is a portfolio piece that demonstrates the same engineering practices used in the projects it showcases.");
        portfolio.setFeatured(false);
        portfolio.setDisplayOrder(4);
        portfolio.setPeriod("2025 – Present");
        portfolio.setGithubUrl("https://github.com/FaisalHanafi/Portfolio");
        portfolio.setLiveUrl("https://faisalhanafi.com");
        portfolio.setTechnologies(Set.of(springBoot, java, postgresql, react, typescript, vite, tailwind));
        portfolio.setArchitectureDecisions(List.of(
                "Package-by-feature (project/, skill/, experience/) over package-by-layer for cohesion",
                "Java records for DTOs — immutable, concise, no Lombok boilerplate needed",
                "ApiResponse<T> wrapper for consistent API contract across all endpoints",
                "CommandLineRunner seeder instead of Flyway — appropriate for a portfolio with 7 seeded tables",
                "HTTP Cache-Control headers for CDN-friendly responses without Redis overhead"
        ));
        portfolio.setTechStack(Map.of(
                "Backend", List.of("Spring Boot 3.4.2", "Java 21", "PostgreSQL 16"),
                "Frontend", List.of("React 18", "TypeScript 5", "Vite", "Tailwind CSS", "Framer Motion"),
                "Infrastructure", List.of("AWS Lightsail", "Nginx", "systemd", "GitHub Actions"),
                "Testing", List.of("JUnit 5", "MockMvc", "Spring Boot Test")
        ));
        portfolio.setChallenges(List.of(
                "Deploying Spring Boot on a t3.micro (1GB RAM) — required careful HikariCP tuning and JVM memory management",
                "Fixing a silent production bug where H2 was running instead of PostgreSQL due to Spring profile name mismatch",
                "Building a CI/CD pipeline that handles both backend JAR and frontend static files in a single workflow"
        ));
        portfolio.setCostJustification("AWS Lightsail keeps hosting simple and low-cost while Nginx serves the frontend and proxies the backend on a single deployment target.");

        projectRepository.saveAll(List.of(creams, autoRecruit, sen2nal, portfolio));
    }

    private void seedSkills() {
        // Backend
        SkillCategory backend = category("Backend", 1);
        addSkill(backend, "Java", 80, 1);
        addSkill(backend, "Spring Boot", 75, 2);
        addSkill(backend, "PHP", 85, 3);
        addSkill(backend, "Laravel", 85, 4);
        addSkill(backend, "Python", 70, 5);
        addSkill(backend, "Node.js", 60, 6);

        // Frontend
        SkillCategory frontend = category("Frontend", 2);
        addSkill(frontend, "React", 80, 1);
        addSkill(frontend, "TypeScript", 75, 2);
        addSkill(frontend, "JavaScript", 85, 3);
        addSkill(frontend, "Tailwind CSS", 85, 4);
        addSkill(frontend, "HTML/CSS", 90, 5);

        // Data & ML
        SkillCategory dataMl = category("Data & ML", 3);
        addSkill(dataMl, "Pandas", 70, 1);
        addSkill(dataMl, "Scikit-learn", 60, 2);
        addSkill(dataMl, "NLP", 55, 3);
        addSkill(dataMl, "Streamlit", 70, 4);

        // Cloud & DevOps
        SkillCategory cloudDevops = category("Cloud & DevOps", 4);
        addSkill(cloudDevops, "AWS", 65, 1);
        addSkill(cloudDevops, "Docker", 60, 2);
        addSkill(cloudDevops, "GitHub Actions", 70, 3);
        addSkill(cloudDevops, "Linux", 70, 4);
        addSkill(cloudDevops, "Nginx", 65, 5);

        // Tools
        SkillCategory tools = category("Tools", 5);
        addSkill(tools, "Git", 85, 1);
        addSkill(tools, "VS Code", 90, 2);
        addSkill(tools, "IntelliJ IDEA", 75, 3);
        addSkill(tools, "Postman", 80, 4);
        addSkill(tools, "Figma", 55, 5);

        skillCategoryRepository.saveAll(List.of(backend, frontend, dataMl, cloudDevops, tools));
    }

    private void seedExperiences() {
        Experience accenture = new Experience();
        accenture.setCompany("Accenture Technology Malaysia");
        accenture.setRole("Application Development Associate");
        accenture.setLocation("Kuala Lumpur, Malaysia");
        accenture.setStartDate(LocalDate.of(2024, 7, 1));
        accenture.setEndDate(null);
        accenture.setDescription("Working on enterprise application development projects, contributing to full-stack solutions for clients across Southeast Asia. Primarily focused on SharePoint and .NET ecosystem development.");
        accenture.setHighlights(List.of(
                "Built Auto-Recruit SPFx solution that automated recruitment workflows for internal hiring teams",
                "Developed and maintained SharePoint-based enterprise applications serving 500+ users",
                "Collaborated with cross-functional teams across Malaysia and Philippines offices"
        ));
        accenture.setDisplayOrder(1);

        Experience iium = new Experience();
        iium.setCompany("International Islamic University Malaysia (IIUM)");
        iium.setRole("Bachelor of Computer Science");
        iium.setLocation("Kuala Lumpur, Malaysia");
        iium.setStartDate(LocalDate.of(2020, 9, 1));
        iium.setEndDate(LocalDate.of(2024, 6, 30));
        iium.setDescription("Graduated with a Bachelor of Computer Science, specializing in software engineering. Final year project (CREAMS) received Gold Medal recognition at the university FYP showcase.");
        iium.setHighlights(List.of(
                "Gold Medal — Final Year Project (CREAMS: Community Rehabilitation Case Management System)",
                "Dean's List recognition for academic excellence",
                "Coursework in Data Structures, Algorithms, Database Systems, Software Engineering, and AI"
        ));
        iium.setDisplayOrder(2);

        experienceRepository.saveAll(List.of(accenture, iium));
    }

    private void refreshNarrativeContent() {
        log.info("Refreshing portfolio narrative content...");

        projectRepository.findBySlug("creams").ifPresent(project -> {
            project.setTitle("CREAMS - Community Rehabilitation & Case Management System");
            project.setShortDescription("Gold Medal final year project that grew from a university brief into a case management system I came to see as genuinely important to the development and support of unique children.");
            project.setFullDescription("CREAMS began as a project I was told to create, but the deeper I got into the workflow, the more I understood that it was not just another academic deliverable. It was tied to the development of unique children and the people responsible for supporting that progress. That changed the way I approached the system. I treated it less like an assignment and more like a structured tool for care, coordination, and accountability.");
            project.setProblemStatement("The rehabilitation workflow depended heavily on scattered records, manual updates, and inconsistent reporting. That created friction for the staff doing the work, but it also weakened visibility into the progress of children whose development needed careful and consistent support.");
            project.setSolution("I built a full-stack case management platform that organized the workflow around role-based access, structured records, progress tracking, scheduling, and reporting. More importantly, I built it with the understanding that usability and accountability mattered because the system was supporting work with human consequences.");
            project.setChallenges(List.of(
                    "Designing a permission model that stayed practical for multiple roles without making the system harder to use",
                    "Turning a university brief into a product that felt operationally credible instead of merely presentable",
                    "Balancing data structure, reporting needs, and workflow clarity in a domain where consistency really matters"
            ));
            project.setCostJustification("Built as a final year project, but designed with the seriousness of a real operational system because the underlying work it supports is meaningful.");
            project.getImages().forEach(image -> image.setAltText("CREAMS dashboard and rehabilitation workflow overview"));
        });

        projectRepository.findBySlug("auto-recruit").ifPresent(project -> {
            project.setTitle("Auto-Recruit - Enterprise Recruitment Workflow System");
            project.setShortDescription("Enterprise recruitment workflow solution built in the SharePoint ecosystem, focused on clearer operations, stronger validation, and a smoother experience for business users.");
            project.setFullDescription("Auto-Recruit reflects the kind of work I do well in enterprise environments: taking a process that people depend on every day and making it more structured, more visible, and easier to trust. It was not just about building screens. It was about reducing friction for hiring workflows, improving validation, and helping business users work with live information more confidently.");
            project.setProblemStatement("Recruitment tracking often becomes fragmented across spreadsheets, manual updates, and disconnected process steps. That slows down decision-making and makes it harder for teams to trust the current state of the workflow.");
            project.setSolution("I contributed to a SharePoint-based workflow solution that combined React, TypeScript, SPFx, validation logic, reporting needs, and business-facing improvements. The goal was to make the workflow more reliable and easier to navigate while staying grounded in the realities of enterprise delivery.");
            project.setChallenges(List.of(
                    "Working within enterprise platform constraints while still improving usability and responsiveness",
                    "Supporting changing business processes without turning the workflow into something brittle",
                    "Balancing validation, performance, and stakeholder expectations inside a live operational environment"
            ));
            project.setCostJustification("Built within an existing enterprise platform, which kept infrastructure overhead low while emphasizing operational value and maintainability.");
        });

        projectRepository.findBySlug("sen2nal").ifPresent(project -> {
            project.setTitle("Sen2Nal - Financial Sentiment and Market Intelligence");
            project.setShortDescription("A financial NLP and market analysis project shaped by my interest in trading, signal quality, and how better interpretation can support better decisions.");
            project.setFullDescription("Sen2Nal comes from a very personal interest in markets and decision-making. As an active day trader, I am constantly aware of how noisy financial information can be, and how difficult it is to separate signal from distraction. This project let me explore that problem more seriously through sentiment analysis, market context, and predictive modelling.");
            project.setProblemStatement("Financial data is abundant, but interpretation is uneven. News, sentiment, and price movement do not naturally line up in ways that are easy to read, especially for people trying to make disciplined decisions in fast-moving environments.");
            project.setSolution("I designed Sen2Nal as a market intelligence project that combines financial NLP, structured storage, time-series thinking, and explainable modelling. The aim was not just to produce output, but to build a system that helps make market information easier to interrogate and reason about.");
            project.setChallenges(List.of(
                    "Connecting sentiment signals with market behavior in a way that stayed analytically honest",
                    "Balancing modelling ambition with explainability through tools like SHAP and walk-forward validation",
                    "Designing a finance-focused project that felt useful rather than just technically decorative"
            ));
            project.setCostJustification("Built as a self-directed finance and NLP project, focused more on analytical depth and system thinking than on polished hosting infrastructure.");
        });

        projectRepository.findBySlug("portfolio").ifPresent(project -> {
            project.setTitle("Portfolio - Data, AI, and Software Systems in One Place");
            project.setShortDescription("A portfolio built as a real product, not a static page, because I wanted the site itself to demonstrate how I think about APIs, data, deployment, and storytelling.");
            project.setFullDescription("I did not want my portfolio to be a page full of claims with no system behind it. I wanted it to reflect the way I actually think: data should be structured, content should be queryable, and the product should hold together from backend to frontend to deployment. That is why this site exists as a full-stack application instead of a static template.");
            project.setProblemStatement("Many portfolios look polished on the surface but reveal very little about how the person behind them approaches systems, data, or delivery. I wanted mine to do more than present information. I wanted it to prove a way of thinking.");
            project.setSolution("I built the portfolio as a Spring Boot and React application with database-backed content, API-driven sections, and a deployment setup that reflects real operational concerns. The site itself acts as both a showcase and a small proof of how I connect engineering choices to practical outcomes.");
            project.setChallenges(List.of(
                    "Keeping the portfolio honest to my real profile while still making it engaging to read",
                    "Running a proper backend and deployment workflow for a project many people would have kept static",
                    "Treating content quality, API structure, and production behavior as part of the same product"
            ));
            project.setCostJustification("Designed as a low-cost full-stack product on Lightsail so the portfolio itself could demonstrate delivery decisions, not just design choices.");
        });

        Experience accenture = experienceRepository.findByCompany("Accenture Technology Malaysia")
                .orElseGet(Experience::new);
        accenture.setCompany("Accenture Technology Malaysia");
        accenture.setRole("Packaged App Development Associate");
        accenture.setLocation("Kuala Lumpur, Malaysia");
        accenture.setStartDate(LocalDate.of(2024, 7, 1));
        accenture.setEndDate(null);
        accenture.setDescription("I work on enterprise workflow and analytics delivery, helping translate business processes into systems that are faster, clearer, and easier for teams to trust.");
        accenture.setHighlights(List.of(
                "Worked across React, TypeScript, SPFx, validation logic, Power BI reporting, stakeholder-facing updates, and operational workflow improvements",
                "Helped improve page load performance by around 68 percent, reducing friction for people using the system day to day",
                "Built 20 plus validation rules and supported 28 plus workflow updates aligned with live business processes"
        ));
        accenture.setDisplayOrder(1);
        experienceRepository.save(accenture);

        Experience aem = experienceRepository.findByCompany("AEM Energy Solutions")
                .orElseGet(Experience::new);
        aem.setCompany("AEM Energy Solutions");
        aem.setRole("Data Analyst Intern");
        aem.setLocation("Kuala Lumpur, Malaysia");
        aem.setStartDate(LocalDate.of(2023, 6, 1));
        aem.setEndDate(LocalDate.of(2023, 9, 30));
        aem.setDescription("I learned early that data work becomes valuable only when it is cleaned, validated, and structured carefully enough for people to trust what comes out of it.");
        aem.setHighlights(List.of(
                "Built ETL routines with R and regex to extract, clean, validate, and standardize data across 43 templates and 37 fields",
                "Supported reporting preparation and secure data handling tied to operational work and client-facing needs",
                "Developed a practical respect for messy data, consistency, and the quiet discipline behind reliable reporting"
        ));
        aem.setDisplayOrder(2);
        experienceRepository.save(aem);

        Experience iium = experienceRepository.findByCompany("International Islamic University Malaysia (IIUM)")
                .orElseGet(Experience::new);
        iium.setCompany("International Islamic University Malaysia (IIUM)");
        iium.setRole("BSc in Data Science");
        iium.setLocation("Kuala Lumpur, Malaysia");
        iium.setStartDate(LocalDate.of(2020, 9, 1));
        iium.setEndDate(LocalDate.of(2024, 6, 30));
        iium.setDescription("My academic years gave me the technical foundation, but they also taught me that I care most about systems that connect structured thinking to real human outcomes.");
        iium.setHighlights(List.of(
                "Graduated with a CGPA of 3.72 out of 4.00 and Dean's List recognition for 9 semesters",
                "Won the Gold Medal for Best Final Year Project through CREAMS",
                "Built early depth across data preparation, software engineering, databases, and applied decision-oriented systems"
        ));
        iium.setDisplayOrder(3);
        experienceRepository.save(iium);
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
