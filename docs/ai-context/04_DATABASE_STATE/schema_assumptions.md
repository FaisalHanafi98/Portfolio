# Schema Assumptions

Tables and entities inferred from code. No migration files exist — schema is created by
Hibernate from entity classes. All entries are INFERRED FROM CODE unless noted otherwise.

---

## Entity: Project

**Table name:** INFERRED — `project` (Spring default from class name)
**Source:** `com.faisal.portfolio.project.Project`

| Field | Type (inferred) | Notes |
|---|---|---|
| id | Long (PK, auto-generated) | INFERRED — standard Spring Data |
| slug | String | INFERRED — `findBySlug` method exists in ProjectRepository |
| title | String | INFERRED — set in DataSeeder |
| shortDescription | String / TEXT | INFERRED — set in DataSeeder |
| fullDescription | String / TEXT | INFERRED — set in DataSeeder |
| problemStatement | String / TEXT | INFERRED — set in DataSeeder |
| solution | String / TEXT | INFERRED — set in DataSeeder |
| featured | boolean | INFERRED — set in DataSeeder |
| displayOrder | int | INFERRED — set in DataSeeder |
| period | String | INFERRED — set in DataSeeder |
| githubUrl | String (nullable) | INFERRED — some projects have null |
| liveUrl | String (nullable) | INFERRED — some projects have null |
| architectureDecisions | List<String> | INFERRED — likely @ElementCollection or JSON column |
| techStack | Map<String, List<String>> | INFERRED — likely @ElementCollection or JSON; complex type |
| challenges | List<String> | INFERRED — @ElementCollection or JSON |
| costJustification | String | INFERRED — set in DataSeeder |
| technologies | Set<Technology> | INFERRED — @ManyToMany join table |
| images | List<ProjectImage> | INFERRED — @OneToMany |

---

## Entity: Technology

**Table name:** INFERRED — `technology`
**Source:** `com.faisal.portfolio.project.Technology`

| Field | Type (inferred) | Notes |
|---|---|---|
| id | Long (PK) | INFERRED |
| name | String | INFERRED — set via `entityManager.persist()` in DataSeeder |

Note: Technology entities are persisted directly via EntityManager in DataSeeder, not via a
TechnologyRepository. This means they are persisted within the same transaction as the Project.

---

## Entity: ProjectImage

**Table name:** INFERRED — `project_image`
**Source:** `com.faisal.portfolio.project.ProjectImage`

| Field | Type (inferred) | Notes |
|---|---|---|
| id | Long (PK) | INFERRED |
| url | String | INFERRED — set in DataSeeder |
| altText | String | INFERRED — set in DataSeeder |
| displayOrder | int | INFERRED — set in DataSeeder |
| project | Project (FK) | INFERRED — `setProject(creams)` in DataSeeder |

---

## Entity: SkillCategory

**Table name:** INFERRED — `skill_category`
**Source:** `com.faisal.portfolio.skill.SkillCategory`

| Field | Type (inferred) | Notes |
|---|---|---|
| id | Long (PK) | INFERRED |
| name | String | INFERRED — set in DataSeeder |
| displayOrder | int | INFERRED — set in DataSeeder |
| skills | List<Skill> | INFERRED — @OneToMany based on `addSkill` method |

---

## Entity: Skill

**Table name:** INFERRED — `skill`
**Source:** `com.faisal.portfolio.skill.Skill`

| Field | Type (inferred) | Notes |
|---|---|---|
| id | Long (PK) | INFERRED |
| name | String | INFERRED — set in DataSeeder |
| proficiency | int | INFERRED — set in DataSeeder (values 55-90) |
| displayOrder | int | INFERRED — set in DataSeeder |
| category | SkillCategory (FK) | INFERRED — `setCategory(category)` in DataSeeder |

---

## Entity: Experience

**Table name:** INFERRED — `experience`
**Source:** `com.faisal.portfolio.experience.Experience`

| Field | Type (inferred) | Notes |
|---|---|---|
| id | Long (PK) | INFERRED |
| company | String | INFERRED — used as lookup key in `findByCompany` |
| role | String | INFERRED — set in DataSeeder |
| location | String | INFERRED — set in DataSeeder |
| startDate | LocalDate | INFERRED — set in DataSeeder |
| endDate | LocalDate (nullable) | INFERRED — null for current position |
| description | String / TEXT | INFERRED — set in DataSeeder |
| highlights | List<String> | INFERRED — @ElementCollection or JSON |
| displayOrder | int | INFERRED — set in DataSeeder |

---

## Join Tables (Inferred)

| Join table | Entities | Notes |
|---|---|---|
| project_technology (or similar) | Project <-> Technology | INFERRED — @ManyToMany |

---

## Seeded Data Summary (VERIFIED from DataSeeder.java)

4 Projects: creams, auto-recruit, sen2nal, portfolio
5 SkillCategories: Backend, Frontend, Data & ML, Cloud & DevOps, Tools
Total skills: 26 skills across 5 categories
3 Experience records: Accenture Technology Malaysia, AEM Energy Solutions, IIUM (via refreshNarrativeContent)

Note: The initial seed (seedExperiences) creates 2 experiences. The refreshNarrativeContent
method updates those 2 and creates/updates a third (AEM Energy Solutions). The final state
after a fresh boot should be 3 experience records.
