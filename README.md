# Portfolio Platform

[![CI](https://github.com/FaisalHanafi98/Portfolio/actions/workflows/ci.yml/badge.svg)](https://github.com/FaisalHanafi98/Portfolio/actions/workflows/ci.yml)

A dynamic, full-stack portfolio platform built with Spring Boot and React, demonstrating modern web development practices.

## Tech Stack

### Backend
- **Framework:** Spring Boot 3.4.2
- **Language:** Java 21
- **Database:** PostgreSQL (production) / H2 (development)
- **Monitoring:** Spring Boot Actuator + Prometheus
- **Documentation:** OpenAPI/Swagger
- **Build Tool:** Gradle 8.14

### Frontend
- **Framework:** React 18
- **Language:** TypeScript (strict mode)
- **Build Tool:** Vite
- **Styling:** Tailwind CSS
- **Animation:** Framer Motion
- **State Management:** React Query

## Project Structure

```
portfolio/
├── backend/           # Spring Boot API
│   └── src/main/java/com/faisal/portfolio/
│       ├── project/       # Projects feature
│       ├── skill/         # Skills feature
│       ├── experience/    # Experience feature
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

# Run in development mode (uses H2 in-memory database)
./gradlew bootRun

# The API will be available at http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
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
| GET | `/api/v1/projects` | List all projects |
| GET | `/api/v1/projects/{slug}` | Get project details |
| GET | `/api/v1/skills` | Get skills by category |
| GET | `/api/v1/experience` | Get work experience |
| GET | `/api/health` | Actuator health (DB status) |
| GET | `/api/prometheus` | Prometheus metrics |

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
```

## Deployment

- **Backend:** AWS Lightsail (systemd + Nginx reverse proxy)
- **Frontend:** Vercel

## Author

**Mohamad Faisal Bin Mohd Hanafi**

- LinkedIn: [linkedin.com/in/faisal-hanafi](https://linkedin.com/in/faisal-hanafi)
- GitHub: [github.com/FaisalHanafi](https://github.com/FaisalHanafi)

## License

This project is open source and available under the MIT License.
