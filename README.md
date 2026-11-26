# Portfolio Platform

[![CI](https://github.com/FaisalHanafi98/Portfolio/actions/workflows/ci.yml/badge.svg)](https://github.com/FaisalHanafi98/Portfolio/actions/workflows/ci.yml)

A dynamic, full-stack portfolio platform built with Spring Boot and React, demonstrating modern web development practices.

## Start Here

`docs/SOURCE_OF_TRUTH.md` is the only documentation entry point for this repository.

Use this README as supporting reference only.

## Tech Stack

### Backend
- **Framework:** Spring Boot 3.4.2
- **Language:** Java 21
- **Database:** PostgreSQL (production) / H2 (development)
- **Monitoring:** Spring Boot Actuator + Prometheus
- **Documentation:** OpenAPI/Swagger
- **Build Tool:** Gradle 9.7.1 (wrapper with distribution checksum)

### Frontend
- **Framework:** React 18
- **Language:** TypeScript (strict mode)
- **Build Tool:** Vite
- **Styling:** Tailwind CSS
- **Animation:** Framer Motion
- **State Management:** React Query
- **E2E Verification:** Playwright Test (`1.65.1`)

## Project Structure

```
portfolio/
├── backend/           # Spring Boot API
│   └── src/main/java/com/faisal/portfolio/
│       ├── project/       # Projects feature
│       ├── skill/         # Skills feature
│       ├── experience/    # Experience feature
│       ├── status/        # Status snapshot, latency metrics & GitHub poller
│       ├── common/        # Shared (ApiResponse, exceptions)
│       ├── config/        # CORS, OpenAPI config
│       └── seed/          # Data seeder
│
├── frontend/          # React application
│   └── src/
│       ├── api/           # API client & hooks
│       ├── components/    # React components
│       ├── pages/         # Page components
│       ├── types/         # TypeScript types
│       └── lib/           # Utilities
│
└── .github/workflows/ # CI/CD pipelines
```

## Getting Started

### Prerequisites

- Java 21+
- Node.js 20+
- npm

### Backend Setup

```bash
cd backend

# Run with the default dev profile (uses H2 in-memory database on port 8081)
./gradlew bootRun

# Health: http://localhost:8081/actuator/health
# API: http://localhost:8081/api/v1/projects
# Swagger UI: http://localhost:8081/swagger-ui.html
```

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# The app will be available at http://localhost:5173
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/health` | Health check |
| GET | `/api/v1/status` | Bounded operational status snapshot (`uptimeSeconds`, coarse DB health, revision, 5-min request count/p95, and cached GitHub workflow/commit sync) |
| GET | `/api/v1/projects` | List all projects |
| GET | `/api/v1/projects/{slug}` | Get project details |
| GET | `/api/v1/skills` | Get skills by category |
| GET | `/api/v1/experience` | Get work experience |
| GET | `/actuator/health` | Local ops/runtime healthcheck (`show-details: never` in `prod`) |
| GET | `/actuator/prometheus` | Local dev Prometheus metrics (`access: none` / blocked in `prod`) |

## Development Commands

### Backend

```bash
./gradlew bootRun          # Start server
./gradlew test             # Run tests
./gradlew build            # Build JAR
```

### Frontend

```bash
npm run dev                # Start dev server
npm run build              # Production build
npm run lint               # Run ESLint
npm run type-check         # TypeScript check
npm test                   # Run SSR content-state assertions
npm run test:e2e           # Run Playwright E2E checks (requires running backend)
```

## Deployment

- **Deployment:** The Lightsail workflow is retired; Compose PostgreSQL/Nginx rehearsal is verified in CI and VPS cutover is KIV. See the [runtime guide](docs/SOURCE_OF_TRUTH.md) and [roadmap](docs/PORTFOLIO_ROADMAP.md).
- **Frontend:** Static Vite build served by Nginx with `/api/` reverse-proxied to Spring Boot on port `8080`.
- **Operational rule:** Treat `docs/SOURCE_OF_TRUTH.md` as authoritative if this README and executable files ever diverge.

## Author

**Mohamad Faisal Bin Mohd Hanafi**

- LinkedIn: [linkedin.com/in/faisalhanafidsa0105](https://www.linkedin.com/in/faisalhanafidsa0105/)
- GitHub: [github.com/FaisalHanafi98](https://github.com/FaisalHanafi98)

## License

This project is open source and available under the MIT License.
