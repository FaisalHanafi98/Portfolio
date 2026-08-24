# Backend API — Module Status

Source files verified: ProjectController.java, SkillController.java, ExperienceController.java,
ApiResponse.java, application.yml, application-dev.yml, application-prod.yml
Last verified: 2026-05-10
Confidence: HIGH for documented routes; INFERRED for routes not found

---

## Purpose

Spring Boot REST API serving public portfolio content. No authentication. No write endpoints.
All endpoints return a consistent `ApiResponse<T>` envelope.

---

## Response Envelope Format (VERIFIED)

```json
{
  "success": true,
  "data": <T>,
  "timestamp": "2026-05-10T12:00:00Z"
}
```

On error:
```json
{
  "success": false,
  "error": "message",
  "timestamp": "2026-05-10T12:00:00Z"
}
```

ApiResponse is a Java record with `@JsonInclude(NON_NULL)` — null fields are omitted from output.
`data` is null on error; `error` is null on success.

---

## API Routes (VERIFIED from controller code)

### ProjectController — @RequestMapping("/api/v1")

| Method | Path | Returns | Cache | Source |
|---|---|---|---|---|
| GET | /api/v1/health | ApiResponse<Map<String,String>> {"status":"ok"} | 1h max-age, 1d stale-while-revalidate | VERIFIED |
| GET | /api/v1/projects | ApiResponse<List<ProjectDto>> | 1h max-age | VERIFIED |
| GET | /api/v1/projects/{slug} | ApiResponse<ProjectDetailDto> | 1h max-age | VERIFIED |

### SkillController — @RequestMapping("/api/v1/skills")

| Method | Path | Returns | Cache | Source |
|---|---|---|---|---|
| GET | /api/v1/skills | ApiResponse<List<SkillCategoryDto>> | 1h max-age | VERIFIED |

### ExperienceController — @RequestMapping("/api/v1/experience")

| Method | Path | Returns | Cache | Source |
|---|---|---|---|---|
| GET | /api/v1/experience | ApiResponse<List<ExperienceDto>> | 1h max-age | VERIFIED |

### Actuator (runtime ops only — not public production endpoints)

| Path | Purpose | Notes |
|---|---|---|
| /actuator/health | Health status | Used by healthcheck scripts and CI |
| /actuator/info | Build info | Enabled in application.yml |
| /actuator/metrics | Metrics | Enabled |
| /actuator/prometheus | Prometheus scrape | Enabled via micrometer-registry-prometheus |

Source of truth for actuator exposure: `backend/src/main/resources/application.yml`

---

## Routes NOT Found (Possible Defects)

| Route | Called from | Backend source | Status |
|---|---|---|---|
| GET /api/v1/about | frontend/src/api/client.ts (api.getAbout) | No AboutController in class list | POSSIBLE DEFECT |

Investigation needed: verify whether the frontend actually calls `getAbout()` in any component.
If no component calls it, the method in client.ts is dead code. If a component calls it,
requests to /api/v1/about return 404 (handled by GlobalExceptionHandler or default Spring error).

---

## Cache Strategy (VERIFIED)

All public endpoints set `Cache-Control: max-age=3600, stale-while-revalidate=86400`.
This allows CDN or proxy caches to serve stale content for up to 1 day while revalidating,
reducing backend load significantly for a low-update portfolio.

Cache is set at the controller layer via `CacheControl.maxAge(1, TimeUnit.HOURS)`.
No in-process cache (Redis, Caffeine) is in use — the data set is small and seeded.

---

## OpenAPI / Swagger (VERIFIED)

- Enabled in dev: swagger-ui at `/swagger-ui.html`, api-docs at `/api-docs`
- Disabled in production: both `springdoc.api-docs.enabled` and `springdoc.swagger-ui.enabled`
  are set to `false` in application-prod.yml

---

## Known Gaps

1. No /api/v1/about backend endpoint (POSSIBLE DEFECT — see above)
2. No rate limiting (listed in OPTIONAL_IMPROVEMENTS as 2.6, not implemented)
3. No response compression configured (listed in OPTIONAL_IMPROVEMENTS as 2.7, not implemented)
4. No API versioning strategy beyond current v1 path prefix (v2 path exists in OPTIONAL_IMPROVEMENTS)
5. Health check returns only `{"status":"ok"}` — does not include database connectivity or
   response times (listed in OPTIONAL_IMPROVEMENTS as 2.8, not implemented)
