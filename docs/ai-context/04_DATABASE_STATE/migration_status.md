# Database Migration Status

---

## Development Profile

| Property | Value | Confidence |
|---|---|---|
| Database | H2 in-memory | HIGH — application-dev.yml verified |
| JDBC URL | jdbc:h2:mem:portfoliodb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE | HIGH — application-dev.yml verified |
| Schema management | ddl-auto: create-drop | HIGH — application-dev.yml verified |
| Seeding | DataSeeder (CommandLineRunner) runs on every boot | HIGH — DataSeeder.java verified |
| H2 console | Enabled at /h2-console | HIGH — application-dev.yml verified |

Implication of create-drop: the schema is dropped and recreated on every application restart.
No persistent data survives a restart in the dev profile. This is intentional — DataSeeder
repopulates everything fresh on each boot.

---

## Production Profile

| Property | Value | Confidence |
|---|---|---|
| Database | PostgreSQL | HIGH — application-prod.yml verified |
| Connection | Via env vars DB_URL, DB_USERNAME, DB_PASSWORD | HIGH — application-prod.yml verified |
| Schema management | ddl-auto: update | HIGH — application-prod.yml verified |
| Migration tool | None (no Flyway or Liquibase in build.gradle) | HIGH — build.gradle verified |
| Seeding | DataSeeder runs on boot; skips if projectRepository.count() > 0 | HIGH — DataSeeder.java verified |
| Connection pool | HikariCP: max 5, min-idle 2, idle-timeout 300s, connect-timeout 20s | HIGH — application-prod.yml verified |

---

## Production Migration Strategy — NEEDS REVIEW

No Flyway or Liquibase is present. Hibernate manages schema evolution via `ddl-auto: update`.

What this means in practice:
- Hibernate compares its entity model to the current database schema on startup
- It adds missing columns, creates missing tables, adds missing indexes
- It does NOT drop columns or tables that are no longer in the entity model (update mode is additive)
- It does NOT provide rollback capability

This is an ACCEPTED DEVIATION for a portfolio project — the data set is small, the schema is
stable, and there are no migration rollback requirements. However, it means:

1. Renaming an entity field requires a manual SQL migration (update does not rename columns)
2. Removing a field leaves the old column in the database indefinitely
3. There is no migration history or audit trail

Risk: ACCEPTED for current scope. NEEDS REVIEW if the project ever handles real user data
or requires coordinated schema changes across multiple environments.

---

## Prod Migration Verification Status

UNVERIFIED — no session record confirms the production PostgreSQL schema was last inspected.
The DataSeeder guard (`if (projectRepository.count() == 0)`) means seed data runs once on
first boot and never again. If the schema changes and existing seed data becomes stale, a
manual delete-and-reseed or a targeted DataSeeder update would be required.
