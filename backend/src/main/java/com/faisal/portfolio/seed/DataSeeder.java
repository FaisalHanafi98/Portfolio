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
        creams.setShortDescription("PLACEHOLDER: Gold medal FYP — full-stack rehabilitation case management system");
        creams.setFullDescription("PLACEHOLDER: Detailed description of CREAMS project");
        creams.setProblemStatement("PLACEHOLDER: What problem CREAMS solves");
        creams.setSolution("PLACEHOLDER: How CREAMS solves it");
        creams.setFeatured(true);
        creams.setDisplayOrder(1);
        creams.setPeriod("Jan – Jun 2024");
        creams.setGithubUrl(null);
        creams.setLiveUrl("https://faisalhanafi.com/creams/demo/");
        creams.setTechnologies(Set.of(laravel, php, mysql, javascript, tailwind));

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
        autoRecruit.setShortDescription("PLACEHOLDER: Enterprise SPFx recruitment solution for SharePoint Online");
        autoRecruit.setFullDescription("PLACEHOLDER: Detailed description of Auto-Recruit");
        autoRecruit.setProblemStatement("PLACEHOLDER: What problem Auto-Recruit solves");
        autoRecruit.setSolution("PLACEHOLDER: How Auto-Recruit solves it");
        autoRecruit.setFeatured(true);
        autoRecruit.setDisplayOrder(2);
        autoRecruit.setPeriod("Jul – Dec 2024");
        autoRecruit.setTechnologies(Set.of(spfx, react, typescript, sharepoint));

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
        sen2nal.setShortDescription("PLACEHOLDER: ML/NLP-driven sentiment analysis for Bursa Malaysia stocks");
        sen2nal.setFullDescription("PLACEHOLDER: Detailed description of Sen2Nal");
        sen2nal.setProblemStatement("PLACEHOLDER: What problem Sen2Nal solves");
        sen2nal.setSolution("PLACEHOLDER: How Sen2Nal solves it");
        sen2nal.setFeatured(false);
        sen2nal.setDisplayOrder(3);
        sen2nal.setPeriod("2025 – Present");
        sen2nal.setGithubUrl("https://github.com/FaisalHanafi/Sen2Nal");
        sen2nal.setTechnologies(Set.of(python, streamlit));

        // Project 4: Portfolio (meta)
        Project portfolio = new Project();
        portfolio.setSlug("portfolio");
        portfolio.setTitle("Portfolio — Personal Developer Portfolio");
        portfolio.setShortDescription("PLACEHOLDER: This very site — Spring Boot API + React/TypeScript frontend");
        portfolio.setFullDescription("PLACEHOLDER: Detailed description of Portfolio project");
        portfolio.setProblemStatement("PLACEHOLDER: Why build a custom portfolio");
        portfolio.setSolution("PLACEHOLDER: How the portfolio was architected");
        portfolio.setFeatured(false);
        portfolio.setDisplayOrder(4);
        portfolio.setPeriod("2025 – Present");
        portfolio.setGithubUrl("https://github.com/FaisalHanafi/Portfolio");
        portfolio.setLiveUrl("https://faisalhanafi.com");
        portfolio.setTechnologies(Set.of(springBoot, java, postgresql, react, typescript, vite, tailwind));

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
        accenture.setDescription("PLACEHOLDER: Description of role at Accenture");
        accenture.setHighlights(List.of(
                "PLACEHOLDER: Key achievement 1",
                "PLACEHOLDER: Key achievement 2",
                "PLACEHOLDER: Key achievement 3"
        ));
        accenture.setDisplayOrder(1);

        Experience iium = new Experience();
        iium.setCompany("International Islamic University Malaysia (IIUM)");
        iium.setRole("Bachelor of Computer Science");
        iium.setLocation("Kuala Lumpur, Malaysia");
        iium.setStartDate(LocalDate.of(2020, 9, 1));
        iium.setEndDate(LocalDate.of(2024, 6, 30));
        iium.setDescription("PLACEHOLDER: Description of education at IIUM");
        iium.setHighlights(List.of(
                "PLACEHOLDER: Gold Medal FYP — CREAMS",
                "PLACEHOLDER: Dean's List",
                "PLACEHOLDER: Relevant coursework"
        ));
        iium.setDisplayOrder(2);

        experienceRepository.saveAll(List.of(accenture, iium));
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
