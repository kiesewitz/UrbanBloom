# School Library Backend - Development Setup

## Overview

This is a **Modular Monolith** backend implementation for the Digital School Library, following **Domain-Driven Design (DDD)** and **Hexagonal Architecture** patterns.

### Project Structure

```
backend/
├── shared/                      # DDD Base Classes & Shared Utilities
├── module-lending/              # Core Domain - Checkout, Return, Reservation
├── module-catalog/              # Supporting - Media Catalog
├── module-notification/         # Supporting - Event-Driven Notifications
├── module-reminding/            # Supporting - Scheduled Reminders
├── module-user/                 # Generic - User & Keycloak Integration
└── schoollibrary-app/           # Main Spring Boot Application
```

### Technology Stack

- **Java 21 (LTS)** with Spring Boot 3.2.0
- **PostgreSQL 15** (Docker)
- **Keycloak 26.4.7** (SSO/Identity Management)
- **Flyway** (Database Migration)
- **Lombok** & **MapStruct** (Code Generation)
- **JUnit 5** & **Mockito** (Testing)

---

## Quick Start

### Prerequisites

- Java 21 JDK installed (LTS)
- Maven 3.8+ installed
- Docker & Docker Compose installed
- Git installed

### 1. Clone & Setup Repository

```bash
# Clone the repository
git clone https://github.com/ukondert/pr_digital-school-library.git
cd pr_digital-school-library

# Copy environment file
cp .env.example .env

# Start Docker containers (PostgreSQL & Keycloak)
docker-compose up -d

# Wait for services to be ready (~30 seconds)
docker-compose ps
```

### 2. Build Backend

```bash
cd backend

# Clean install (runs tests)
mvn clean install

# Or skip tests for faster build
mvn clean install -DskipTests
```

Note: This repository uses `backend/.mvn/maven.config` to isolate the Maven local repository at `backend/.m2/repository`.
This avoids intermittent file-lock issues on Windows when installing snapshots into the global `~/.m2`.

### 3. Run Application

```bash
cd backend/schoollibrary-app

# Option A: Run via Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Option B: Run built JAR
java -jar target/schoollibrary-app-0.0.1-SNAPSHOT.jar

# Application will start on http://localhost:8080
```

### 4. Test Endpoints

```bash
# Health Check
curl http://localhost:8080/health
# Expected response: { "status": "UP", "timestamp": "...", "checks": { ... } }

# App Info
curl http://localhost:8080/api/v1/app/info
# Expected response: { "name": "Digital School Library", "version": "0.0.1-SNAPSHOT", "bounded_contexts": [...] }

# Detailed Health
curl http://localhost:8080/health/detailed
```

### 5. Stop Services

```bash
# Stop Docker containers
docker-compose down

# Or stop but keep data
docker-compose down -v  # Remove volumes
```

---

## Development Workflow

### Running Tests

```bash
# Run all tests
mvn test

# Run tests for specific module
mvn test -pl schoollibrary-app

# Run with coverage
mvn test jacoco:report
```

### Code Generation

Lombok and MapStruct annotations are automatically processed during compilation:

```bash
# Force recompilation
mvn clean compile
```

### Database Migrations

Flyway migrations are executed automatically on application startup:

```bash
# Location: backend/schoollibrary-app/src/main/resources/db/migration/
# Format: V{version}__{description}.sql
```

---

## Architecture

### DDD Bounded Contexts

| Context | Type | Package | Purpose |
|---------|------|---------|---------|
| **Lending** | Core Domain | `com.schoollibrary.lending` | Checkout, Return, Renewal, Reservation |
| **Catalog** | Supporting | `com.schoollibrary.catalog` | Media Management, Inventory |
| **Notification** | Supporting | `com.schoollibrary.notification` | Event-Driven Notifications |
| **Reminding** | Supporting | `com.schoollibrary.reminding` | Scheduled Reminders |
| **User** | Generic | `com.schoollibrary.user` | User Management, Keycloak Integration |

### Layer Structure (Per Module)

```
module-{context}/
├── domain/              # Aggregates, Entities, Value Objects, Domain Services
├── application/         # Use Cases, Commands, Queries, Application Services
├── infrastructure/      # Repositories, Data Sources, External Services
└── interfaces/          # REST Controllers, DTOs
```

---

## Configuration

### Environment Variables

Create `.env.local` with:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=schoollibrary_db
DB_USER=dev_user
DB_PASSWORD=dev_password

KEYCLOAK_URL=http://localhost:8081
KEYCLOAK_REALM=schoollibrary
KEYCLOAK_CLIENT_ID=schoollibrary-app
KEYCLOAK_CLIENT_SECRET=your-secret

SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
```

### Authentication & Authorization

The backend uses Spring OAuth2 Resource Server to validate JWTs issued by Keycloak.

- Configure `spring.security.oauth2.resourceserver.jwt.issuer-uri` in application properties using `KEYCLOAK_URL` and `KEYCLOAK_REALM`.
- JWKS certificates are fetched automatically from the issuer; optionally set `spring.security.oauth2.resourceserver.jwt.jwk-set-uri` explicitly.
- Role mapping (ACL): realm roles (`realm_access.roles`) and client roles (`resource_access.<KEYCLOAK_CLIENT_ID>.roles`) are mapped to Spring authorities like `ROLE_<role>`.
- Note: `KEYCLOAK_CLIENT_SECRET` is not required for JWT validation in the backend.

### Profiles

- **dev**: Development with logging, H2 in-memory for tests
- **test**: Test profile with H2 in-memory database
- **prod**: Production profile (not configured yet)

---

## Troubleshooting

### Docker Services Won't Start

```bash
# Check container logs
docker-compose logs postgres
docker-compose logs keycloak

# Rebuild containers
docker-compose down -v
docker-compose up -d

# Wait for health checks
docker-compose ps
```

### Maven Build Failures

```bash
# Clear local cache
mvn clean install -U

# Check Java version
java -version  # Should be 21.x.x

# Check Maven version
mvn -version   # Should be 3.8+
```

### Application Won't Connect to Database

```bash
# Verify PostgreSQL is running
docker exec schoollibrary-postgres psql -U dev_user -d schoollibrary_db -c "\dt"

# Check application logs
tail -f backend/schoollibrary-app/logs/application.log
```

### Keycloak Admin Access

- URL: `http://localhost:8081`
- Username: `admin`
- Password: `admin`

---

## CI/CD

GitHub Actions workflows are configured in `.github/workflows/` (TBD in separate issue)

---

## Documentation

- [Strategic Architecture](../../docs/architecture/strategic-architecture-summary.md)
- [Domain Model](../../docs/architecture/domain-model.md)
- [Ubiquitous Language](../../docs/architecture/ubiquitous-language-glossar-complete.md)
- [Bounded Contexts Map](../../docs/architecture/bounded-contexts-map.md)

---

## Support & Questions

For questions or issues, please refer to the GitHub Issues or contact the Backend Team.

**Status:** ✅ Hello World Proof-of-Concept (Infrastructure Testing)
