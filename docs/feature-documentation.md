# UrbanBloom Project Documentation - Feature Overview

This document provides a comprehensive overview of the features and architectural components of the UrbanBloom ecosystem, including the Backend engine, the Citizen Mobile Application, and the Administrative Web Portal.

## 1. Backend Infrastructure (Spring Boot)

The UrbanBloom backend is built as a Modular Monolith using Domain-Driven Design (DDD) principles. It provides a robust, scalable foundation for managing urban greening data and citizen engagement.

### Core Architecture
- **Domain-Driven Design**: The system is partitioned into nine distinct Bounded Contexts, ensuring high cohesion and loose coupling between different business areas.
- **Event-Driven Communication**: Modules communicate asynchronously via Domain Events (e.g., ActionValidated triggers PointsAwarded), allowing for a reactive and decoupled system.
- **Anti-Corruption Layer**: A specialized Identity Provider interface abstracts Keycloak integration, protecting the domain logic from external dependency changes.

### Bounded Contexts
1. **User / Identity**: Manages registration, secure authentication, and role-based access control (Citizen, Admin, District Manager).
2. **Action / Observation**: Handles the lifecycle of green actions, including creation, photo evidence management, and validation.
3. **Plant Catalog**: A central repository of plant species with specific growth requirements and maintenance guidelines.
4. **Location / District**: Manages geographic data and maps individual actions to specific city districts for localized impact analysis.
5. **Gamification**: Calculates user points and manages the badge achievement system based on validated actions.
6. **Challenge**: Orchestrates time-limited city-wide or district-specific competitions to drive community engagement.
7. **Notification**: Dispatches automated email and push notifications triggered by system events.
8. **Admin / Analytics**: Aggregates data across districts to provide high-level insights for city planning.
9. **Sync / Offline**: Provides infrastructure for the mobile app's offline-first synchronization strategy.

### Technical Components
- **API Layer**: Fully documented REST API via SpringDoc OpenAPI (Swagger UI).
- **Security**: OAuth2 and JWT-based security integrated with a dedicated Keycloak server.
- **Persistence**: High-performance data storage using PostgreSQL with Flyway for versioned schema migrations.

## 2. Citizen Mobile Application (Flutter Mobile)

The mobile application is designed for citizens to document and manage their environmental contributions directly from the field.

### User Experience and Features
- **Onboarding and Auth**: Multi-step registration and secure login. Users can join using verified school or organization email domains.
- **Action Management**: A centralized list view where users can track their submitted actions and monitor their verification status (Draft, Validated, Rejected).
- **Green Action Logging**: A guided process for recording new plantings, including species selection and geographic tagging.
- **Gamification Profile**: A dedicated profile screen showing user statistics, current rank (Seedling, Urban Forester, etc.), and earned badges.
- **Password Recovery**: Integrated password reset flow utilizing secure email-based verification.

### UI Architecture
- **Component-Driven Design**: Built using Atomic Design principles (Atoms, Molecules, Organisms) for maximum UI consistency.
- **State Management**: Utilizes Riverpod for reactive and testable data binding.
- **Design System**: A custom-themed interface with a professional green aesthetic tailored for urban nature initiatives.

## 3. Administrative Web Portal (Flutter Web)

The administrative portal serves as the command center for city officials and district managers to oversee the UrbanBloom initiative.

### Management Features
- **Executive Dashboard**: A high-level overview featuring KPI cards for total city-wide actions, active user counts, and total ecological points awarded.
- **District Analytics**: Visual comparison tools, including bar charts, to analyze the progress and greening growth trends across different city sectors.
- **User Management**: A searchable, role-aware database for managing the citizen directory, adjusting permissions, and monitoring user activity.
- **Challenge Orchestration**: Tools for creating and launching community challenges, setting goals, and tracking participation rates.
- **Professional Layout**: A responsive, sidebar-driven navigation system designed for efficient administrative workflows.

## 4. Integrated Communication

- **Email System**: Branded HTML templates for all system-generated communications, including registration confirmations and password resets.
- **Development Sandbox**: All outgoing mail is routed through a local Mailpit instance during development to ensure safe testing without external dependencies.

## 5. Technical Requirements for Operation
- **Runtime**: Java 21 (LTS)
- **Infrastructure**: Docker Desktop (PostgreSQL, Keycloak, Mailpit)
- **Development Tools**: VS Code with Workspace integration and Maven 3.x.
