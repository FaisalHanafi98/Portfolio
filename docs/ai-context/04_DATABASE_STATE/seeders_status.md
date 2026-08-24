# DataSeeder Status

Source: `backend/src/main/java/com/faisal/portfolio/seed/DataSeeder.java`
Last verified: 2026-05-10 (DataSeeder.java read directly)
Confidence: HIGH

---

## Overview

DataSeeder is a Spring `@Component` that implements `CommandLineRunner`. It runs automatically
on every application boot, after the application context is fully initialized.

It is `@Transactional` — all seed operations run in a single database transaction.

---

## Conditional Seed Logic

```
if (projectRepository.count() == 0) {
    seedTechnologiesAndProjects()
    seedSkills()
    seedExperiences()
}
refreshNarrativeContent()  // always runs
```

The initial seed runs only once (when the database is empty). This means:
- In dev (H2 create-drop): runs on every boot because the schema is recreated fresh
- In prod (PostgreSQL update): runs only on first-ever boot; subsequent boots skip the seed block

`refreshNarrativeContent()` always runs on every boot. It uses `findBySlug` and `findByCompany`
to locate existing records and update their text fields. This allows narrative content to be
updated in code without requiring a database wipe.

---

## Seeded Content (VERIFIED from DataSeeder.java)

### Projects (4 total)

| Slug | Title | Featured | Display Order |
|---|---|---|---|
| creams | CREAMS — Community Rehabilitation & Case Management System | true | 1 |
| auto-recruit | Auto-Recruit — Enterprise Recruitment Automation | true | 2 |
| sen2nal | Sen2Nal — Stock Sentiment Analysis Platform | false | 3 |
| portfolio | Portfolio — Personal Developer Portfolio | false | 4 |

Note: Initial titles set in `seedTechnologiesAndProjects()` are overwritten by
`refreshNarrativeContent()` on every boot. The final titles are:
- "CREAMS - Community Rehabilitation & Case Management System"
- "Auto-Recruit - Enterprise Recruitment Workflow System"
- "Sen2Nal - Financial Sentiment and Market Intelligence"
- "Portfolio - Data, AI, and Software Systems in One Place"

### Technologies (16 total, VERIFIED)

Laravel, PHP, MySQL, JavaScript, React, TypeScript, Spring Boot, Java, PostgreSQL,
Python, Streamlit, Tailwind CSS, SPFx, SharePoint, Vite, Docker

### Skill Categories (5 total)

| Category | Skills | Display Order |
|---|---|---|
| Backend | Java(80), Spring Boot(75), PHP(85), Laravel(85), Python(70), Node.js(60) | 1 |
| Frontend | React(80), TypeScript(75), JavaScript(85), Tailwind CSS(85), HTML/CSS(90) | 2 |
| Data & ML | Pandas(70), Scikit-learn(60), NLP(55), Streamlit(70) | 3 |
| Cloud & DevOps | AWS(65), Docker(60), GitHub Actions(70), Linux(70), Nginx(65) | 4 |
| Tools | Git(85), VS Code(90), IntelliJ IDEA(75), Postman(80), Figma(55) | 5 |

Proficiency values are integers (55–90).

### Experience Records (3 total after refreshNarrativeContent)

| Company | Role | Start | End | Display Order |
|---|---|---|---|---|
| Accenture Technology Malaysia | Packaged App Development Associate | 2024-07-01 | null (current) | 1 |
| AEM Energy Solutions | Data Analyst Intern | 2023-06-01 | 2023-09-30 | 2 |
| International Islamic University Malaysia (IIUM) | BSc in Data Science | 2020-09-01 | 2024-06-30 | 3 |

Note: seedExperiences() creates 2 records (Accenture and IIUM with different role/CGPA content).
refreshNarrativeContent() then updates both and creates AEM Energy Solutions. Final state = 3.

---

## Key Behavioral Note

The `refreshNarrativeContent()` method uses `findByCompany()` for experience lookup and
`findBySlug()` for project lookup. Both use `orElseGet(Experience::new)` / `ifPresent()`.
If no match is found, a new entity is created. If a match is found, it is updated in place.

This means narrative content updates can be deployed without wiping the production database —
they take effect on the next application restart.

---

## DataSeeder Dependency: ExperienceRepository.findByCompany

DataSeeder calls `experienceRepository.findByCompany("Accenture Technology Malaysia")`.
This method must exist in ExperienceRepository. Its existence is INFERRED from DataSeeder usage.
