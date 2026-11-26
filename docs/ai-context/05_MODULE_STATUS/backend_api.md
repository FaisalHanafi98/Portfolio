# Backend API — Module Status

Source files verified: `ProjectController.java`, `SkillController.java`, `ExperienceController.java`,
`StatusController.java`, `StatusService.java`, `GitHubStatusService.java`, `StatusSnapshotDto.java`,
`ApiResponse.java`, `application.yml`, `application-dev.yml`, `application-prod.yml`
Last verified: 2026-11-27
Confidence: HIGH for all documented routes and `prod` Actuator lockdown (`EnterpriseBoundaryArchitectureTest` + `StatusControllerTest` + `StatusDegradedDbTest` verified)

---

## Purpose

Spring Boot REST API serving public portfolio content and read-only operational telemetry.
No authentication and no write endpoints. All endpoints return a consistent `ApiResponse<T>` envelope.

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
- `StatusController` returns `Cache-Control: no-store` so visitors receive current DB probe latency and dynamic GitHub cache age.
- `GitHubStatusService` caches public `/actions/runs` and `/commits` responses in memory for 15 minutes (`900s` default) using conditional requests (`ETag`/`If-None-Match`, `If-Modified-Since`) and bounded exponential backoff (`60s`–`3600s`).

---

## OpenAPI / Swagger (VERIFIED)

- Enabled in dev: swagger-ui at `/swagger-ui.html`, api-docs at `/api-docs`
- Disabled in production: both `springdoc.api-docs.enabled` and `springdoc.swagger-ui.enabled`
  are set to `false` in `application-prod.yml`

---

## Known Gaps / Future Roadmap

1. No database schema migration tool (Flyway) yet — required by `PORTFOLIO_ROADMAP.md` before adding first-party analytics write endpoints.
2. Live rate-limit/outage behaviour against public `api.github.com` and PostgreSQL Compose rehearsal remain verified only via mocks/CI smoke until Docker rehearsal resumes.
