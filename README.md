# Portfolio Platform

A dynamic, full-stack portfolio platform built with Spring Boot and React, demonstrating modern web development practices.

## Tech Stack

### Backend
- **Framework:** Spring Boot 3.2
- **Language:** Java 17
- **Database:** PostgreSQL (production) / H2 (development)
- **Documentation:** OpenAPI/Swagger
- **Build Tool:** Gradle

### Frontend
- **Framework:** React 18
- **Language:** TypeScript (strict mode)
- **Build Tool:** Vite
- **Styling:** Tailwind CSS
- **Animation:** Framer Motion
- **State Management:** React Query

## Project Structure

```
portfolio-platform/
├── backend/           # Spring Boot API
│   ├── src/main/java/dev/faisal/portfolio/
│   │   ├── controller/    # REST endpoints
│   │   ├── service/       # Business logic
│   │   ├── repository/    # Data access
│   │   ├── model/         # JPA entities
│   │   ├── dto/           # Data transfer objects
│   │   ├── config/        # Configuration
│   │   └── exception/     # Error handling
│   └── src/main/resources/
│       ├── application.yml
│       └── data.sql       # Seed data
│
├── frontend/          # React application
│   ├── src/
│   │   ├── api/           # API client & hooks
│   │   ├── components/    # React components
│   │   ├── pages/         # Page components
│   │   ├── hooks/         # Custom hooks
│   │   ├── types/         # TypeScript types
│   │   ├── lib/           # Utilities
│   │   └── styles/        # Global styles
│   └── public/
│
└── docs/              # Documentation
```

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- npm or yarn

### Backend Setup

```bash
cd backend

# Run in development mode (uses H2 in-memory database)
./gradlew bootRun

# The API will be available at http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
# H2 Console: http://localhost:8080/h2-console
```

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Create environment file
cp .env.example .env

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
| GET | `/api/v1/about` | Get bio information |

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
npm run preview            # Preview production build
```

## Deployment

### Backend (Render/Railway)

1. Set environment variables:
   - `DATABASE_URL`: PostgreSQL connection string
   - `SPRING_PROFILES_ACTIVE`: `prod`

2. Build command: `./gradlew build`
3. Start command: `java -jar build/libs/portfolio-0.0.1-SNAPSHOT.jar`

### Frontend (Vercel/Netlify)

1. Set environment variables:
   - `VITE_API_URL`: Production API URL

2. Build command: `npm run build`
3. Output directory: `dist`

## Features

- Responsive design (mobile-first)
- Dark/light theme toggle
- Smooth animations with Framer Motion
- Accessible (WCAG 2.1 AA)
- SEO optimized
- TypeScript strict mode (zero `any` types)

## Author

**Mohamad Faisal Bin Mohd Hanafi**

- LinkedIn: [linkedin.com/in/faisal-hanafi](https://linkedin.com/in/faisal-hanafi)
- GitHub: [github.com/FaisalHanafi](https://github.com/FaisalHanafi)

## License

This project is open source and available under the MIT License.
