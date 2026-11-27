# Backend API — Module Status

Source files verified: `ProjectController.java`, `SkillController.java`, `ExperienceController.java`,
`StatusController.java`, `StatusService.java`, `GitHubStatusService.java`, `StatusSnapshotDto.java`,
`AnalyticsController.java`, `AnalyticsIngestionService.java`, `AnalyticsSummaryService.java`,
`AnalyticsRetentionService.java`, `AnalyticsIngestionRateLimiter.java`, `AnalyticsSummaryDto.java`,
`CorsConfig.java`, `ApiResponse.java`, `application.yml`, `application-dev.yml`, `application-prod.yml`,
`V1__baseline_portfolio_schema.sql`, `V2__first_party_analytics_schema.sql`
Last verified: 2026-11-28
Confidence: HIGH for all documented routes, Flyway `V1`/`V2` validation (`ddl-auto: validate`), and `prod` Actuator lockdown (`EnterpriseBoundaryArchitectureTest`, `FlywayBaselineSchemaTest`, `Status*Test`, `Analytics*Test` verified)

---

## Purpose

Spring Boot REST API serving public portfolio content, read-only operational telemetry, and a
bounded same-origin first-party analytics ingestion and aggregate summary slice. No user accounts or
admin CMS endpoints. All endpoints return a consistent `ApiResponse<T>` envelope.

---

## Response Envelope Format (VERIFIED)

```json
{
  "success": true,
  "data": <T>,
  "timestamp": "2026-11-27T00:00:00Z"
}
```

On error:

```json
{
  "success": false,
  "error": "message",
  "timestamp": "2026-11-27T00:00:00Z"
}
```

`ApiResponse` is a Java record with `@JsonInclude(NON_NULL)` — null fields are omitted from output.
`data` is null on error; `error` is null on success.

---

## API Routes (VERIFIED from controller code)

### ProjectController — `@RequestMapping("/api/v1")`

| Method | Path | Returns | Cache | Source |
|---|---|---|---|---|
| GET | `/api/v1/health` | `ApiResponse<Map<String,String>>` `{"status":"ok"}` | 1h max-age, 1d stale-while-revalidate | VERIFIED |
| GET | `/api/v1/projects` | `ApiResponse<List<ProjectDto>>` | 1h max-age, 1d stale-while-revalidate | VERIFIED |
| GET | `/api/v1/projects/{slug}` | `ApiResponse<ProjectDetailDto>` | 1h max-age, 1d stale-while-revalidate | VERIFIED |

### SkillController — `@RequestMapping("/api/v1/skills")`

| Method | Path | Returns | Cache | Source |
|---|---|---|---|---|
| GET | `/api/v1/skills` | `ApiResponse<List<SkillCategoryDto>>` | 1h max-age, 1d stale-while-revalidate | VERIFIED |

### ExperienceController — `@RequestMapping("/api/v1/experience")`

| Method | Path | Returns | Cache | Source |
|---|---|---|---|---|
| GET | `/api/v1/experience` | `ApiResponse<List<ExperienceDto>>` | 1h max-age, 1d stale-while-revalidate | VERIFIED |

### StatusController — `@RequestMapping("/api/v1/status")`

| Method | Path | Returns | Cache | Source |
|---|---|---|---|---|
| GET | `/api/v1/status` | `ApiResponse<StatusSnapshotDto>` (JVM uptime, timeout-bounded DB probe, revision, rolling 5-minute API count/p95, cached GitHub workflow runs & commits) | `Cache-Control: no-store` | VERIFIED |

### AnalyticsController — `@RequestMapping("/api/v1/analytics")`

| Method | Path | Returns / Behaviour | Cache | Source |
|---|---|---|---|---|
| POST | `/api/v1/analytics/events` | `ApiResponse<AnalyticsIngestResultDto>` (`202` accepted / `200` duplicate `eventId`; strict field allowlist, `maxBodyBytes`, Jakarta validation, daily-salted IP hash rate limiter, server UTC timestamps) | No store (write endpoint) | VERIFIED |
| GET | `/api/v1/analytics/summary` | `ApiResponse<AnalyticsSummaryDto>` (project funnel denominators, read-depth buckets, freshness, `30d`/`12m` retention metadata, sparse topic suppression `< 5` events) | `Cache-Control: no-store` | VERIFIED |

### Actuator (internal/local runtime ops only — locked in `application-prod.yml`)

| Path | Dev (`application.yml`) | Prod (`application-prod.yml`) | Notes |
|---|---|---|---|
| `/actuator/health` | Exposed (`show-details: when-authorized`) | Exposed (`show-details: never`) | Used by `L2` healthcheck scripts |
| `/actuator/info` | Exposed | Not exposed | Dev/local diagnostics only |
| `/actuator/metrics` | Exposed | Not exposed | Dev/local diagnostics only |
| `/actuator/prometheus` | Exposed via Micrometer | Blocked (`prometheus.access: none`) | Enforced by `EnterpriseBoundaryArchitectureTest` |

---

## Cache Strategy (VERIFIED)

- Public portfolio content controllers set `Cache-Control: max-age=3600, stale-while-revalidate=86400`.
- `StatusController` and `AnalyticsController` summary return `Cache-Control: no-store` so visitors receive current DB probe latency, dynamic GitHub cache age, and fresh analytics funnel counts.
- `AnalyticsRetentionService` runs daily at `03:15 UTC` to purge raw `analytics_events` older than `30` days and `analytics_daily_aggregates` older than `12` months.
- `GitHubStatusService` caches public `/actions/runs` and `/commits` responses in memory for 15 minutes (`900s` default) using conditional requests (`ETag`/`If-None-Match`, `If-Modified-Since`) and bounded exponential backoff (`60s`–`3600s`).

---

## OpenAPI / Swagger (VERIFIED)

- Enabled in dev: swagger-ui at `/swagger-ui.html`, api-docs at `/api-docs`
- Disabled in production: both `springdoc.api-docs.enabled` and `springdoc.swagger-ui.enabled`
  are set to `false` in `application-prod.yml`

---

## Known Gaps / Future Roadmap

1. Flyway (`V1` baseline, `V2` analytics) and H2 schema validation pass locally (`ddl-auto: validate`); live PostgreSQL migration rehearsal runs in CI Compose smoke (`ci.yml`) and awaits local Docker recovery for workstation rehearsal.
2. Live rate-limit/outage behaviour against public `api.github.com` and concurrent analytics p95 ingestion load on the production host remain verified via mocks/local H2 until Docker/VPS rehearsal resumes.
