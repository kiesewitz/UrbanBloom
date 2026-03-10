# UrbanBloom — Complete Project Specification (AI-Optimized)

> **Setup Deadline (MS-00)**: January 27 — workspace, CI, folder structure, prototypes, and Keycloak config must be pushed to GitHub BEFORE the class session.
>
> **Implementation Milestone (MS-01)**: Authentication, Registration & Password Management — see Section 9 for full specification.
>
> **Important for the AI**: Read this entire document before writing any code or modifying any files.
> Start by exploring all three reference folders listed in Section 1, then proceed to implement changes
> in `UrbanBloom/` only.

---

## 1. Folder Structure in AI Scope

Three folders are available in the working directory. Their roles are strictly defined:

| Folder | Role | How to Use |
|---|---|---|
| `prj_school-library-ref/` | **Structural Reference** | Provides the foundational DDD Maven module layout, Keycloak theme structure, and workspace file patterns. Use as the base architectural template. |
| `EcoTrack/` | **Fully Implemented Reference** | A complete, working project. Use for concrete implementation examples: agent definitions, workflow YAML, tasks.json, workspace configs, Keycloak Freemarker templates, DDD module implementations. **Prefer this over the structural reference for any implementation detail.** |
| `UrbanBloom/` | **Target Project** (implement here) | The project to be built. Partial implementations already exist. Everything **except** the `docs/` folder may be overwritten. The `docs/` folder is documentation only and must not be touched. |

> **Workflow for the AI**:
> 1. Read and understand `EcoTrack/` and `prj_school-library-ref/` first.
> 2. Inspect what already exists in `UrbanBloom/` before writing anything.
> 3. Only then implement or overwrite files in `UrbanBloom/`.

---

## 2. UrbanBloom: Current State vs. Target State

### ⚠️ Critical: Folder Name Discrepancy

The `UrbanBloom/` project has an **inconsistency** between the workspace files and the actual folder names. This must be resolved as part of the implementation:

| Workspace References | Actual Folders Currently in `UrbanBloom/` |
|---|---|
| `server/` | `backend/` ← wrong name |
| `mobile/` | `frontend-mobile/` ← wrong name |
| `admin-web/` | `frontend-web/` ← wrong name |

The workspace files (`urbanbloom-*.code-workspace`) reference `server/`, `mobile/`, and `admin-web/`. The actual component folders must be **renamed** to match:
- `backend/` → `server/`
- `frontend-mobile/` → `mobile/`
- `frontend-web/` → `admin-web/`

Additionally, the `backend/` folder currently contains a copy of the School Library reference project code (wrong package names: `com.schoollibrary.*`). This must be replaced with properly named UrbanBloom code using the package `com.urbanbloom.*`.

### What Already Exists (Do Not Delete)
- `.github/` — agents, instructions, issue templates, PR templates, CI workflow (partially configured)
- `.vscode/tasks.json` — fully configured task definitions
- `urbanbloom-*.code-workspace` — four workspace files (correctly reference `server/`, `mobile/`, `admin-web/`)
- `.gitignore`, `.env.example`
- `openapi/urbanbloom-api-v1.yaml` — OpenAPI spec (Single Source of Truth)
- `scripts/init-*.sh` — initialization scripts
- `docs/` — all documentation (READ ONLY, do not modify)

### What Needs to Be Created / Fixed
See checklist in Section 6.

---

## 3. Project Summary: UrbanBloom

### What is UrbanBloom?
UrbanBloom is a **citizen-science platform** that motivates citizens to plant and maintain urban green spaces. The system uses gamification (points, badges, leaderboards) and enables city administrators to analyze district-level progress and organize challenges.

### Components

| Folder (Correct Name) | Technology | Purpose |
|---|---|---|
| `server/` | Spring Boot 3.2+, Java 17, Maven, PostgreSQL | REST API, DDD business logic, Keycloak integration |
| `mobile/` | Flutter 3.16+, Dart 3.2+, Riverpod, Drift (SQLite) | Citizen app: GPS/QR action logging, offline mode, gamification |
| `admin-web/` | Flutter Web (Dart), Riverpod | Admin dashboard: district comparisons, reports, challenges |
| `shared-resources/` | — | Shared docs, OpenAPI contract, design tokens |

> **Note**: Both `mobile/` and `admin-web/` use **Flutter/Dart**, not Next.js/React. The admin web is a Flutter Web application.

### Architecture Principles

- **Backend**: Domain-Driven Design (DDD) — 9 Bounded Contexts communicating via Domain Events. Hexagonal Architecture (Ports & Adapters).
- **Frontend**: Component-Driven Design (CDD) — Atomic Design: `atoms/` → `molecules/` → `organisms/` → `templates/` → `pages/`
- **API**: OpenAPI 3.0 (`openapi/urbanbloom-api-v1.yaml`) as Single Source of Truth — update the spec before implementing
- **Mobile**: Offline-First using Drift (SQLite) with background sync
- **Auth**: Keycloak (dedicated server, one realm per app)

### The 9 Bounded Contexts (Backend DDD)

| # | Context | Responsibility |
|---|---|---|
| 1 | **User / Identity** | Registration, login, GDPR deletion |
| 2 | **Action / Observation** | Logging and validation of green actions |
| 3 | **Plant Catalog** | Central plant database with care instructions |
| 4 | **Location / District** | Location management and district assignment |
| 5 | **Gamification** | Points, badges, leaderboards |
| 6 | **Challenge** | Time-limited city/district competitions |
| 7 | **Notification / Reminder** | Push notifications and care reminders |
| 8 | **Admin / Analytics** | Reports and district comparisons for administrators |
| 9 | **Sync / Offline** | Offline-first sync with conflict resolution |

### Key Business Rules
- Actions must pass geo-verification and plausibility checks before being validated
- Points are awarded **only for validated** actions
- Badges are automatically assigned when defined thresholds are reached
- Offline sync is idempotent (no duplicates)
- Reports must not contain raw personally identifiable data

### Technology Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2+, PostgreSQL 15+, Flyway, JUnit 5, TestContainers, ArchUnit |
| Mobile | Flutter 3.16+, Dart 3.2+, Riverpod, go_router, Drift (SQLite), Dio |
| Admin Web | Flutter Web (Dart), Riverpod, go_router |
| Auth | Keycloak (separate Docker container) |
| CI/CD | GitHub Actions |
| API Design | OpenAPI 3.0 + openapi-generator-cli |

---

## 4. Universal Requirements (ALL Teams)

### 4.1 Workspace Configuration

Four `.code-workspace` files exist and must be correctly configured:

| File | Opens |
|---|---|
| `urbanbloom-full.code-workspace` | Root + `server/` + `mobile/` + `admin-web/` + `shared-resources/` |
| `urbanbloom-mobile.code-workspace` | `mobile/` + `shared-resources/` |
| `urbanbloom-web.code-workspace` | `admin-web/` + `shared-resources/` |
| `urbanbloom-server.code-workspace` | `server/` + `shared-resources/` |

**Requirements for each workspace file:**
- `shared-resources/` is included as a folder entry and therefore **visible** in every workspace
- `extensions.recommendations` lists the appropriate extensions for that workspace's tech stack
- `extensions.unwantedRecommendations` suppresses irrelevant extensions

**Extension sets by role:**

| Workspace | Recommended Extensions (IDs) |
|---|---|
| Server | `vscjava.vscode-java-pack`, `vscjava.vscode-spring-initializr`, `vmware.vscode-spring-boot`, `vscjava.vscode-maven`, `redhat.java`, `gabrielbb.vscode-lombok`, `streetsidesoftware.code-spell-checker` |
| Mobile | `dart-code.dart-code`, `dart-code.flutter`, `nash.awesome-flutter-snippets`, `robert-brunhage.flutter-riverpod-snippets`, `usernamehw.errorlens`, `streetsidesoftware.code-spell-checker` |
| Web Admin | Same as Mobile (Flutter Web) |
| Full | All of the above |

> Reference: `EcoTrack/ecotrack-mobile.code-workspace`, `EcoTrack/ecotrack-web.code-workspace`, `UrbanBloom/urbanbloom-server.code-workspace`

---

### 4.2 Implementation Folder Structure

#### Backend (`server/`) — Domain-Driven Design

Maven multi-module project. Package root: `com.urbanbloom`.

```
server/
├── pom.xml                          # Parent POM (multi-module)
├── server-app/                      # Host application (entry point)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/urbanbloom/app/
│       │   ├── UrbanBloomApplication.java
│       │   └── config/
│       │       ├── SecurityConfig.java
│       │       └── CorsConfig.java
│       └── resources/
│           ├── application.yml
│           └── db/migration/        # Flyway migrations
├── module-user/                     # User/Identity bounded context
│   ├── pom.xml
│   └── src/main/java/com/urbanbloom/user/
│       ├── domain/                  # Pure business logic (no framework deps)
│       │   ├── User.java            # Aggregate Root
│       │   ├── UserId.java          # Value Object
│       │   ├── Email.java           # Value Object
│       │   ├── UserRepository.java  # Repository Interface
│       │   └── events/
│       │       └── UserRegistered.java  # Domain Event
│       ├── application/             # Use cases / Application Services
│       │   ├── RegisterUserUseCase.java
│       │   └── dto/
│       ├── adapter/                 # Infrastructure adapters
│       │   ├── in/rest/             # REST controllers
│       │   │   └── UserController.java
│       │   └── out/
│       │       ├── persistence/     # JPA entities + repositories
│       │       └── keycloak/        # Keycloak adapter
│       ├── config/
│       └── api/                     # Module facade (public API)
├── module-action/                   # Action/Observation bounded context
├── module-gamification/             # Gamification bounded context
├── module-challenge/                # Challenge bounded context
├── module-notification/             # Notification bounded context
├── module-analytics/                # Admin/Analytics bounded context
└── shared/                         # Shared Kernel (Value Objects, DDD base classes)
    └── src/main/java/com/urbanbloom/shared/ddd/
        ├── AggregateRoot.java
        ├── DomainEvent.java
        ├── Entity.java
        └── ValueObject.java
```

**DDD Rules (mandatory):**
- Domain layer (`domain/`) has **zero** Spring/JPA dependencies — pure Java only
- Aggregates reference other aggregates **by ID only** (never by object reference)
- Cross-context communication via **Domain Events only** (no direct service calls between modules)
- Repository interfaces live in `domain/`, implementations in `adapter/out/persistence/`

> Reference implementation: `EcoTrack/server/module-recycling/`, `EcoTrack/server/module-gamification/`
> Shared kernel: `EcoTrack/server/shared/` and `prj_school-library-ref/backend/shared/`

#### Frontend Mobile (`mobile/`) — Component-Driven Design (Flutter)

```
mobile/
├── pubspec.yaml
├── lib/
│   ├── main.dart
│   ├── core/
│   │   ├── config/environment.dart
│   │   ├── routing/app_router.dart      # go_router
│   │   ├── di/providers.dart            # Riverpod providers
│   │   ├── network/api_client.dart      # Dio HTTP client
│   │   └── services/auth_service.dart
│   ├── design_system/
│   │   ├── design_tokens.dart           # Colors, spacing, typography
│   │   └── components/
│   │       ├── atoms/                   # Buttons, text fields, icons
│   │       ├── molecules/               # Input groups, cards
│   │       ├── organisms/               # Forms, lists, navigation bars
│   │       └── components.dart          # Barrel export
│   ├── features/
│   │   ├── user/
│   │   │   └── presentation/pages/
│   │   │       ├── login_screen.dart
│   │   │       ├── registration_screen.dart
│   │   │       ├── profile_screen.dart
│   │   │       └── forgot_password_screen.dart
│   │   ├── action/
│   │   ├── gamification/
│   │   └── ...
│   └── data/
│       ├── api/generated/               # OpenAPI-generated Dart client
│       └── local/                       # Drift (SQLite) for offline storage
└── test/
    ├── design_system/components/        # Widget tests per component level
    └── features/
```

> Reference implementation: `prj_school-library-ref/frontend-mobile/lib/`

#### Frontend Admin Web (`admin-web/`) — Component-Driven Design (Flutter Web)

```
admin-web/
├── pubspec.yaml
├── lib/
│   ├── main.dart
│   ├── core/
│   │   ├── providers/
│   │   └── services/admin_api_client.dart
│   ├── design_system/
│   │   ├── design_tokens.dart
│   │   └── components/
│   │       ├── atoms/                   # admin_badge, admin_text, admin_text_field
│   │       ├── molecules/               # admin_stat_card, admin_search_bar, admin_pagination
│   │       └── organisms/              # admin_data_table, admin_header, admin_sidebar, admin_kpi_row
│   ├── features/
│   │   ├── access_control/              # Admin login
│   │   ├── analytics/                   # District stats and reports
│   │   ├── challenges/                  # Challenge management
│   │   └── ...
│   └── data/
│       └── api/generated/
└── test/
```

> Reference implementation: `EcoTrack/admin-web/lib/`

---

### 4.3 `.github/` Directory

The `.github/` directory already contains agents and instructions. Verify completeness and fill gaps:

#### Minimum Requirements

| Element | Min. Count | Location | File Format | Status in UrbanBloom |
|---|---|---|---|---|
| Development Agent | 1 | `.github/agents/` | `*.agent.md` | ✅ 7 agents exist |
| Custom Instruction | 1 | `.github/instructions/` | `*.instructions.md` | ✅ 3 instructions exist |
| Custom Prompts | **2** | `.github/prompts/` | `*.prompt.md` | ❌ **MISSING — folder doesn't exist** |

**Action required**: Create `.github/prompts/` with at least 2 prompt files.

#### Existing Agents (verify content, adapt to UrbanBloom if needed)
- `backend-ddd.agent.md` — DDD domain modeling
- `backend-business-logic.agent.md` — Use cases and REST controllers
- `mobile-cdd.agent.md` — Flutter mobile components
- `web-admin-cdd.agent.md` — Flutter Web admin components
- `admin-web.agent.md` — Admin web specific
- `documentation.agent.md` — Documentation tasks
- `project-manager.agent.md` — User stories and planning

#### Prompts to Create (`.github/prompts/`)

Create at minimum these two files, adapting from `EcoTrack/.github/prompts/`:

**`create-fullstack-feature.prompt.md`** — Template for implementing a full-stack feature spanning backend DDD module + Flutter screen + OpenAPI update.

**`debug-and-fix.prompt.md`** — Template for diagnosing and fixing bugs across the stack.

> Reference: `EcoTrack/.github/prompts/create-fullstack-feature.md`, `EcoTrack/.github/prompts/debug-and-fix.md`

---

### 4.4 `.vscode/tasks.json`

The file **already exists** at `UrbanBloom/.vscode/tasks.json` and is fully configured. Verify it contains at minimum these 3 task categories (it currently does):

| Task Label | Command | Working Dir |
|---|---|---|
| `server: mvn clean install` | `mvn clean install` | `${workspaceFolder}/server` |
| `server: run` | `mvn spring-boot:run` | `${workspaceFolder}/server` |
| `server: test` | `mvn test` | `${workspaceFolder}/server` |
| `mobile: pub get` | `flutter pub get` | `${workspaceFolder}/mobile` |
| `mobile: run` | `flutter run` | `${workspaceFolder}/mobile` |
| `mobile: test` | `flutter test` | `${workspaceFolder}/mobile` |
| `mobile: build apk` | `flutter build apk` | `${workspaceFolder}/mobile` |
| `web: pub get` | `flutter pub get` | `${workspaceFolder}/admin-web` |
| `web: run` | `flutter run -d chrome` | `${workspaceFolder}/admin-web` |
| `web: test` | `flutter test` | `${workspaceFolder}/admin-web` |
| `web: build` | `flutter build web` | `${workspaceFolder}/admin-web` |
| `openapi: validate` | `npx @openapitools/openapi-generator-cli validate -i openapi/urbanbloom-api-v1.yaml` | root |
| `ci: validate all` | depends on: validate + verify + test | composite |

> Note: The tasks reference `server/`, `mobile/`, `admin-web/` — these must match the actual renamed folder names (see Section 2).

---

### 4.5 GitHub Actions CI/CD Workflow

The file **already exists** at `UrbanBloom/.github/workflows/ci.yml` and is fully configured. It defines 7 jobs:

1. `openapi-validation` — Validates `openapi/urbanbloom-api-v1.yaml` and generates server stubs + Dart client
2. `backend-build` — Java 17, `mvn clean install` + `mvn test` (skips gracefully if `server/pom.xml` missing)
3. `mobile-build` — Flutter stable, `flutter pub get` + `flutter analyze` + `flutter test` + `flutter build apk`
4. `web-admin-build` — Flutter stable, same as mobile but `flutter build web --release`
5. `api-compliance` — Runs after backend build (stub, to be implemented)
6. `pr-comment` — Posts CI result summary as PR comment
7. `notify-on-failure` — Sends notification on main branch failures

**Triggers**: `push` and `pull_request` on `main` and `develop` branches.

**Important**: The workflow checks whether `pom.xml` / `pubspec.yaml` exist before running. If the project is not yet initialized, the job skips gracefully with a warning. This means the workflow will not fail on an empty project — it must be initialized first (see `scripts/init-*.sh`).

> The CI workflow references `server/`, `mobile/`, `admin-web/` — folder rename (Section 2) must be done first.

---

## 5. Frontend Teams — Additional Requirements

### 5.1 HTML/CSS UI Prototypes

**Location**: `shared-resources/docs/ui/` (already partially populated in `EcoTrack/shared-resources/docs/ui/`)

> ✅ **UrbanBloom already has prototypes** in `docs/ui/prototypes/mobile_app/` and `docs/ui/frontend/`. These must be **moved or copied** to `shared-resources/docs/ui/` to match the expected location.

Each prototype consists of:
- `code.html` — complete, self-contained HTML/CSS (no external framework required)
- `screen.png` — screenshot of the design

#### Admin Web App — Required Screens

| Screen | Target Path |
|---|---|
| Login Screen | `shared-resources/docs/ui/admin/login.html` |
| Dashboard Screen | `shared-resources/docs/ui/admin/dashboard.html` |
| User Management Screen | `shared-resources/docs/ui/admin/user-management.html` |

> Reference: `EcoTrack/shared-resources/docs/ui/admin/login.html`, `dashboard.html`, `user-management.html`

#### Mobile App — Required Screens

| Screen | Target Path |
|---|---|
| Registration Screen | `shared-resources/docs/ui/mobile/registration.html` |
| Login Screen | `shared-resources/docs/ui/mobile/login.html` |
| Profile Screen | `shared-resources/docs/ui/mobile/profile.html` |
| Password Reset Screen | `shared-resources/docs/ui/mobile/password-reset.html` |

> Reference: `EcoTrack/shared-resources/docs/ui/mobile/` (all 4 files exist)
> Also see: `UrbanBloom/docs/ui/prototypes/mobile_app/` (existing UrbanBloom prototypes to use as base)

---

### 5.2 User Stories — Prototype References

All existing user stories in `docs/requirements/user-stories/refined/` must be updated to explicitly reference their corresponding UI prototype.

**Required section to add to each affected user story:**

```markdown
## UI Prototype
- **Screen**: [Screen Name]
- **File**: `shared-resources/docs/ui/[admin|mobile]/[filename].html`
- **Screenshot**: `shared-resources/docs/ui/[admin|mobile]/[filename].png`
```

**Affected user stories (minimum):**

| User Story File | References Screen |
|---|---|
| `US-USR-01-REF_user-authentication.md` | Login Screen (mobile + admin) |
| `US-USR-02-REF_self-registration.md` | Registration Screen (mobile) |
| `US-USR-03-REF_password-reset.md` | Password Reset Screen (mobile) |

> Reference: `prj_school-library-ref/docs/requirements/user-stories/refined/`

---

### 5.3 Email Templates (HTML)

Two standalone HTML email templates must exist for the frontend. These are **not** Keycloak templates (those are in Section 6.3) — these are for review/reference purposes and potential use in a custom email service.

| Template | Path | Required Content |
|---|---|---|
| Registration confirmation | `shared-resources/email-templates/registration-confirmation.html` | Welcome message, activation link placeholder, UrbanBloom branding |
| Password reset | `shared-resources/email-templates/password-reset.html` | Reset link placeholder, expiry note, security warning |

> Reference for structure and styling: `prj_school-library-ref/themes/schoollibrary/email/html/`

---

## 6. Backend Teams — Additional Requirements

### 6.1 Keycloak Server: Two Realm Configuration

Two separate Keycloak realms must be configured and exported.

---

#### Realm 1: Mobile App — `urbanbloom-mobile`

| Setting | Value |
|---|---|
| Realm Name | `urbanbloom-mobile` |
| Purpose | Authentication for citizen mobile app |

**Client — `urbanbloom-mobile-app`:**

| Property | Value |
|---|---|
| Client ID | `urbanbloom-mobile-app` |
| Client Authentication | `On` |
| Authentication Flow | Authorization Code Flow |
| Service Accounts | Enabled |
| DPoP | `Off` |

**Realm Roles:**

| Role | Description |
|---|---|
| `CITIZEN` | Default role for all registered citizens |
| `VERIFIED_CITIZEN` | Extended role after identity verification |

**Admin CLI Client** (for backend service to manage users):
- Client: `admin-cli`
- Enable Client Authentication + Service Accounts
- Assign service account roles: `manage-users`, `view-users`, `query-users`

---

#### Realm 2: Admin Web App — `urbanbloom-admin`

| Setting | Value |
|---|---|
| Realm Name | `urbanbloom-admin` |
| Purpose | Authentication for city administration web app |

**Client — `urbanbloom-admin-web`:**

| Property | Value |
|---|---|
| Client ID | `urbanbloom-admin-web` |
| Client Authentication | `On` |
| Authentication Flow | Authorization Code Flow + Service Accounts |
| DPoP | `Off` |

**Realm Roles:**

| Role | Description |
|---|---|
| `ADMIN` | Full access to all administrative functions |
| `DISTRICT_MANAGER` | Restricted read/write access to district-level data |

**Admin CLI Client** (same configuration as above, for this realm).

---

### 6.2 Authentication Logic (Spring Boot)

- All authentication goes **exclusively through Keycloak** — no custom username/password login in the backend
- Admin users are managed **only within the Keycloak server** — no local admin user table
- Spring Boot validates JWT tokens via Keycloak's JWKS endpoint
- User management operations (create, deactivate users) use the `admin-cli` service account

**Key configuration** (`application.yml`):
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8081/realms/urbanbloom-mobile
```

> Reference implementation: `prj_school-library-ref/backend/schoollibrary-app/src/main/java/com/schoollibrary/app/config/SecurityConfig.java`
> Reference: `prj_school-library-ref/backend/module-user/src/main/java/com/schoollibrary/user/adapter/infrastructure/keycloak/KeycloakIdentityProvider.java`
> Keycloak setup guide: `prj_school-library-ref/docs/configurations/keycloak-setup.md`

---

### 6.3 Keycloak Email Templates (Freemarker)

Custom email templates must be created as a Keycloak **theme** and configured in each realm.

**Theme structure** (mount into Keycloak via Docker volume):

```
config/keycloak/themes/urbanbloom/
└── email/
    ├── theme.properties          # parent=base
    ├── messages/
    │   └── messages_en.properties
    ├── html/
    │   ├── email-verification.ftl          # Registration verification
    │   ├── email-verification-subject.ftl
    │   ├── password-reset.ftl              # Password reset
    │   ├── password-reset-subject.ftl
    │   ├── executeActions.ftl              # Generic action email
    │   └── executeActions-subject.ftl
    └── text/
        ├── email-verification.ftl
        ├── password-reset.ftl
        └── executeActions.ftl
```

**Configure in Keycloak Admin:**
- Realm Settings → Themes → Email Theme → `urbanbloom`
- Realm Settings → Email (SMTP) → configure SMTP server

**For local development**: Use GreenMail or Mailhog as dummy SMTP (see lessons learned).

> Reference: `prj_school-library-ref/themes/schoollibrary/` — copy and adapt all `.ftl` files
> Keycloak template variables: `prj_school-library-ref/docs/lessons-learnded/backend/2026-01-09-keycloak-email-template-variables.md`
> Lessons learned on registration setup: `prj_school-library-ref/docs/lessons-learnded/backend/2026-01-09-keycloak-registration-setup.md`
> Windows Docker volume issue: `prj_school-library-ref/docs/lessons-learnded/backend/2026-01-09-docker-volume-mounts-windows-keycloak.md`

---

### 6.4 Realm Export (`realm-export.json`)

Both Keycloak realms must be exported and committed to the repository.

**Location**: `config/keycloak/`

**File names**:
- `config/keycloak/urbanbloom-mobile-realm-export.json`
- `config/keycloak/urbanbloom-admin-realm-export.json`

**The export must include:**
- All realm settings (registration, login, token lifetimes)
- All client definitions with their configurations
- All realm roles
- Email theme assignment
- Client secrets set to **actual values** (not empty or placeholder)

**How to export** (Keycloak Admin UI):
Realm Settings → Action (top right) → Export → enable "Export clients" and "Export groups" → Download

> Security note: Dev secrets (e.g., `dev-secret-123`) are acceptable in the committed export. For production deployments, secrets must be injected via environment variables and removed from the export.

> Reference export: `prj_school-library-ref/config/keycloak/realm-export.json`

---

## 7. Complete Checklist

### All Teams

- [ ] Folders renamed: `backend/` → `server/`, `frontend-mobile/` → `mobile/`, `frontend-web/` → `admin-web/`
- [ ] Backend package names updated from `com.schoollibrary.*` to `com.urbanbloom.*`
- [ ] Each `.code-workspace` file includes `shared-resources/` as a visible folder
- [ ] Recommended extensions listed in each workspace file and installed locally
- [ ] Implementation structure follows CDD (Flutter) / DDD (Spring Boot)
- [ ] `.github/agents/` — at least 1 agent (`*.agent.md`) — ✅ already 7
- [ ] `.github/instructions/` — at least 1 instruction (`*.instructions.md`) — ✅ already 3
- [ ] `.github/prompts/` — at least **2 prompt files** (`*.prompt.md`) — ❌ **MISSING**
- [ ] `.vscode/tasks.json` — contains `compile`, `run`, `test` tasks — ✅ exists
- [ ] GitHub Actions workflow passes on push — ✅ exists, verify after folder rename

### Frontend Teams

- [ ] HTML/CSS prototypes in `shared-resources/docs/ui/admin/`: `login.html`, `dashboard.html`, `user-management.html`
- [ ] HTML/CSS prototypes in `shared-resources/docs/ui/mobile/`: `registration.html`, `login.html`, `profile.html`, `password-reset.html`
- [ ] Each prototype has `code.html` (or named `.html`) + `screen.png`
- [ ] User stories in `docs/requirements/user-stories/refined/` reference their prototypes
- [ ] `shared-resources/email-templates/registration-confirmation.html` created
- [ ] `shared-resources/email-templates/password-reset.html` created

### Backend Teams

- [ ] Keycloak realm `urbanbloom-mobile` configured (client, roles, admin-cli, auth flow)
- [ ] Keycloak realm `urbanbloom-admin` configured (client, roles, admin-cli, auth flow)
- [ ] Authentication handled exclusively via Keycloak (no local auth)
- [ ] Admin users managed only in Keycloak
- [ ] Keycloak email theme `urbanbloom` created with Freemarker templates
- [ ] Email theme assigned in both realms
- [ ] `config/keycloak/urbanbloom-mobile-realm-export.json` with client credentials set
- [ ] `config/keycloak/urbanbloom-admin-realm-export.json` with client credentials set

---

## 8. Quick Reference: Where to Find What

| What You Need | Where to Find It |
|---|---|
| Workspace file examples | `EcoTrack/ecotrack-mobile.code-workspace`, `UrbanBloom/urbanbloom-server.code-workspace` |
| Agent file examples | `EcoTrack/.github/agents/*.agent.md` |
| Prompt file examples | `EcoTrack/.github/prompts/*.md` |
| Instruction file examples | `EcoTrack/admin-web/.github/instructions/web-admin-development.md` |
| `tasks.json` (complete) | `UrbanBloom/.vscode/tasks.json` ✅ already done |
| GitHub Actions workflow | `UrbanBloom/.github/workflows/ci.yml` ✅ already done |
| DDD Maven module structure | `EcoTrack/server/module-recycling/` (simplest full module) |
| DDD Shared Kernel | `EcoTrack/server/shared/` and `prj_school-library-ref/backend/shared/` |
| Flutter CDD structure (mobile) | `prj_school-library-ref/frontend-mobile/lib/` |
| Flutter CDD structure (web) | `EcoTrack/admin-web/lib/` |
| Keycloak setup guide | `prj_school-library-ref/docs/configurations/keycloak-setup.md` |
| Keycloak Freemarker templates | `prj_school-library-ref/themes/schoollibrary/email/` |
| Keycloak lessons learned | `prj_school-library-ref/docs/lessons-learnded/backend/` |
| Keycloak realm export example | `prj_school-library-ref/config/keycloak/realm-export.json` |
| UI prototype examples | `EcoTrack/shared-resources/docs/ui/` |
| Existing UrbanBloom prototypes | `UrbanBloom/docs/ui/prototypes/mobile_app/` and `UrbanBloom/docs/ui/frontend/` |
| UrbanBloom OpenAPI spec | `UrbanBloom/openapi/urbanbloom-api-v1.yaml` |
| UrbanBloom user stories | `UrbanBloom/docs/requirements/user-stories/` |
| UrbanBloom DDD architecture docs | `UrbanBloom/docs/architecture/` |
| UrbanBloom coding standards | `UrbanBloom/.github/copilot-instructions.md` |
| Init scripts | `UrbanBloom/scripts/init-server.sh`, `init-mobile.sh`, `init-web-admin.sh` |

---

## 9. Milestone MS-01: Authentication, Registration & Password Management

> **Document reference**: `MS-01-AUTH-REG-RESET.pdf` (2026-02-03)
>
> **Scope**: This milestone covers the complete implementation of Identity and Access Management (IAM)
> for UrbanBloom. Keycloak acts as the central Identity Provider (IdP) for both mobile users (citizens)
> and administrators (web-admin).
>
> **Explicitly OUT of scope for MS-01**: Token refresh mechanism — do NOT implement this yet.

---

### 9.1 Functional Requirements

#### 9.1.1 Self-Registration (Mobile App — Citizens Only)

- Citizens can register themselves directly in the mobile app
- **Email verification**: After registration a confirmation email is sent; the account is only activated after the user clicks the verification link
- **Role assignment**: New users automatically receive the `CITIZEN` role as default

#### 9.1.2 Login

**Mobile (Citizens):**
- Login via email + password directly in the Flutter app
- Login only works after email has been verified

**Admin Web:**
- Administrators log in through a dedicated admin login screen
- Admins are created manually in Keycloak — there is no self-registration for admins
- **Forced password change**: On the very first login, if Keycloak returns `update_password` status, the app detects this and redirects the admin to the Keycloak UI to set a new password. After completing the password change, the admin is redirected back to the web-admin app and must log in again.

#### 9.1.3 Password Management (Forgot Password)

- Users can request a password reset link via "Forgot Password"
- The backend triggers a Keycloak reset action, which sends an email containing a Keycloak-hosted reset link
- The user sets the new password in the **Keycloak browser UI** (not in the app)
- After completing the reset, Keycloak automatically redirects back to the app:
  - Mobile: via **deep link**
  - Web Admin: via **redirect URL**
- The backend responds with neutral feedback ("email sent") regardless of whether the address exists (prevents user enumeration)

#### 9.1.4 Logout (Mobile & Web Admin)

- Users can log out securely from both apps
- **Server-side token invalidation**: The app sends the refresh token to the backend (`POST /api/v1/auth/logout`), which revokes it in Keycloak, ending the server-side session
- **Client-side cleanup**: The app deletes all locally stored tokens (SecureStorage on mobile, LocalStorage on web) and redirects to the login screen

#### 9.1.5 User Profile Data — Dual Storage & Sync

User data is stored redundantly across two systems:

| System | Stores | Authority |
|---|---|---|
| **Keycloak** | Email, password (hashed), roles, account status | Leading for authentication |
| **PostgreSQL** | Extended profile data: display name, registration date, status, app-specific fields | Leading for profile/app logic |

**Synchronization rule**: At registration and login, both systems must be in sync. The link between Keycloak and the local DB is the `externalId` field, which holds the **Keycloak User ID**.

**Registration flow**:
1. Backend creates user in Keycloak → receives Keycloak User ID
2. Backend creates user profile in PostgreSQL with `externalId = keycloakUserId`, `status = DISABLED`
3. User clicks verification link → Keycloak activates the account → PostgreSQL status updated to `ENABLED`

---

### 9.2 REST API Endpoints (Backend — `server/`)

All endpoints must be implemented in the `module-user` bounded context.
All endpoints must be registered in `openapi/urbanbloom-api-v1.yaml` **before** implementation.

| Endpoint | Method | Description |
|---|---|---|
| `/api/v1/auth/mobile/login` | `POST` | Authenticate citizen (email + password) via Keycloak Direct Grant |
| `/api/v1/auth/admin/login` | `POST` | Authenticate admin via Keycloak Direct Grant — validates `ADMIN` role |
| `/api/v1/registration` | `POST` | Create new citizen account (Keycloak + PostgreSQL) |
| `/api/v1/users/me` | `GET` | Fetch the enriched user profile from PostgreSQL (requires valid JWT) |
| `/api/v1/auth/password/reset-request` | `POST` | Trigger Keycloak password reset email |
| `/api/v1/auth/logout` | `POST` | Revoke refresh token in Keycloak (server-side session end) |

#### Request/Response Details

**`POST /api/v1/auth/mobile/login`**
```json
// Request
{ "email": "user@example.com", "password": "secret" }

// Response 200
{ "accessToken": "...", "refreshToken": "...", "expiresIn": 900 }

// Response 401 — wrong credentials or email not verified
{ "error": "UNAUTHORIZED", "message": "Invalid credentials or account not verified" }
```

**`POST /api/v1/auth/admin/login`**
```json
// Request
{ "email": "admin@urbanbloom.city", "password": "..." }

// Response 200
{ "accessToken": "...", "refreshToken": "...", "expiresIn": 900 }

// Response 401 — temporary password (first login)
{ "error": "UPDATE_PASSWORD_REQUIRED", "message": "Password change required", "keycloakLoginUrl": "http://keycloak:8081/realms/urbanbloom-admin/protocol/openid-connect/auth?..." }

// Response 403 — authenticated but missing ADMIN role
{ "error": "FORBIDDEN", "message": "Insufficient role" }
```

**`POST /api/v1/registration`**
```json
// Request
{ "email": "user@example.com", "password": "secret", "firstName": "Max", "lastName": "Mustermann" }

// Response 201
{ "userId": "local-db-uuid", "externalId": "keycloak-uuid", "message": "Registration successful. Please verify your email." }

// Response 409 — email already exists
{ "error": "CONFLICT", "message": "Email already registered" }
```

**`GET /api/v1/users/me`**
```json
// Response 200 (requires Authorization: Bearer <accessToken>)
{ "id": "local-db-uuid", "externalId": "keycloak-uuid", "email": "user@example.com", "firstName": "Max", "lastName": "Mustermann", "status": "ENABLED", "roles": ["CITIZEN"], "registeredAt": "2026-01-15T10:00:00Z" }
```

**`POST /api/v1/auth/password/reset-request`**
```json
// Request
{ "email": "user@example.com" }

// Response 200 — always returns OK (never reveals whether email exists)
{ "message": "If this email is registered, a reset link has been sent." }
```

**`POST /api/v1/auth/logout`**
```json
// Request (requires Authorization: Bearer <accessToken>)
{ "refreshToken": "..." }

// Response 200
{ "message": "Logged out successfully" }
```

---

### 9.3 Process Flows (Sequence Diagrams)

#### Flow A: Registration & Email Verification

```
Mobile App          Backend API         PostgreSQL          Keycloak            Mail Server
    |                    |                   |                   |                    |
    |-- POST /api/v1/registration ---------->|                   |                    |
    |                    |-- createUser() -------------------------------->|           |
    |                    |<-- keycloakUserId -----------------------------|           |
    |                    |-- INSERT user_profile (externalId, status=DISABLED) ->|   |
    |                    |<-- profile saved ------------------|           |           |
    |                    |                   |-- send verification email ------------>|
    |<-- 201 Created ----|                   |                   |                    |
    |                    |                   |                   |                    |
    |     [User clicks verification link in browser]            |                    |
    |                    |                   |-- activate user (status=ENABLED) ->|  |
    |                    |                   |<-- confirmed ------|                   |
```

#### Flow B: Admin Login — Forced Password Change

```
Admin Web App       Backend API         Keycloak
    |                    |                   |
    |-- POST /api/v1/auth/admin/login ------>|
    |                    |-- Direct Grant Auth ----------->|
    |                    |<-- error: update_password ------|
    |<-- 401 { UPDATE_PASSWORD_REQUIRED, keycloakLoginUrl }
    |
    |-- [Redirect to Keycloak UI via keycloakLoginUrl]
    |                                        |
    |         Admin sets new password in Keycloak browser UI
    |                                        |
    |<-- [Redirect back to Web Admin App] ---|
    |
    |-- POST /api/v1/auth/admin/login (new password) ----->|
    |<-- 200 { accessToken, refreshToken } ---|
```

#### Flow C: Forgot Password (Reset)

```
Mobile/Web App      Backend API         Keycloak            Mail Server
    |                    |                   |                   |
    |-- POST /api/v1/auth/password/reset-request (email) ------->|
    |                    |-- triggerResetAction() -------->|      |
    |                    |<-- OK -------------------------|      |
    |<-- 200 "email sent" (neutral)                        |      |
    |                    |                   |-- send reset email ->|
    |                    |                   |                   |  |
    |     [User clicks reset link → Keycloak browser UI]        |  |
    |                    |                   |                   |
    |         User sets new password on Keycloak page
    |                    |                   |
    |<-- [Redirect to app via deep-link / redirect URL] ---------|
```

#### Flow D: Logout

```
Mobile/Web App      Backend API         Keycloak
    |                    |                   |
    |-- POST /api/v1/auth/logout (refreshToken) ----------->|
    |                    |-- revokeToken(refreshToken) -->|
    |                    |<-- OK -------------------------|
    |<-- 200 "logged out" |                   |
    |                    |                   |
    |-- [Delete local tokens from SecureStorage/LocalStorage]
    |-- [Navigate to Login screen]
```

---

### 9.4 Backend Implementation Details

#### DDD Module: `module-user`

All MS-01 backend logic lives in `server/module-user/`. The module follows Hexagonal Architecture:

```
server/module-user/src/main/java/com/urbanbloom/user/
├── domain/
│   ├── UserProfile.java              # Aggregate Root (local DB profile)
│   ├── UserId.java                   # Value Object (local DB ID)
│   ├── ExternalUserId.java           # Value Object (Keycloak ID)
│   ├── Email.java                    # Value Object (validated)
│   ├── UserRole.java                 # Enum: CITIZEN, ADMIN, DISTRICT_MANAGER
│   ├── UserStatus.java               # Enum: ENABLED, DISABLED
│   ├── UserProfileRepository.java    # Repository Interface (domain layer)
│   ├── IdentityProvider.java         # Port Interface (Keycloak abstraction)
│   ├── RegistrationService.java      # Domain Service
│   └── events/
│       ├── UserRegisteredEvent.java
│       └── UserProfileCreatedEvent.java
├── application/
│   ├── RegisterUserUseCase.java       # Orchestrates Keycloak + DB creation
│   ├── AuthenticateMobileUserUseCase.java
│   ├── AuthenticateAdminUseCase.java
│   ├── RequestPasswordResetUseCase.java
│   ├── LogoutUseCase.java
│   ├── GetUserProfileUseCase.java
│   └── commands/
│       ├── RegisterUserCommand.java
│       ├── LoginCommand.java
│       └── PasswordResetRequestCommand.java
├── adapter/
│   ├── in/rest/
│   │   ├── AuthController.java        # /api/v1/auth/**
│   │   ├── RegistrationController.java # /api/v1/registration
│   │   ├── UserController.java        # /api/v1/users/me
│   │   └── dto/                       # Request/Response DTOs
│   └── out/
│       ├── persistence/
│       │   ├── UserProfileJpaEntity.java
│       │   ├── UserProfileJpaRepository.java
│       │   └── UserProfileRepositoryAdapter.java
│       └── keycloak/
│           ├── KeycloakIdentityProvider.java  # Implements IdentityProvider port
│           └── KeycloakTokenResponse.java
└── config/
    ├── KeycloakConfig.java            # Keycloak admin client bean
    └── SecurityConfig.java            # JWT resource server config
```

> Reference implementation (directly reusable): `prj_school-library-ref/backend/module-user/`
> This module already implements login, registration, password reset, and Keycloak integration.
> **Rename packages from `com.schoollibrary.user` to `com.urbanbloom.user` and adapt to UrbanBloom roles.**

#### Key Implementation Rules

- The `IdentityProvider` interface in the domain layer abstracts all Keycloak calls — the domain never imports Keycloak libraries directly
- `UserProfile` in PostgreSQL is created with `status = DISABLED` at registration; it is activated when Keycloak fires the email verification event or the backend polls for it
- Admin login must explicitly check that the returned JWT contains the `ADMIN` role; reject with `403` if not
- Password reset endpoint **always returns 200** regardless of whether the email is registered — this prevents user enumeration attacks
- Token storage on mobile: `flutter_secure_storage` only — never plain SharedPreferences

#### Keycloak Admin Client Configuration (`KeycloakConfig.java`)

The backend uses the `admin-cli` service account to manage users programmatically:

```java
// Required Keycloak properties in application.yml:
keycloak:
  auth-server-url: http://localhost:8081
  realm: urbanbloom-mobile          # or urbanbloom-admin
  resource: admin-cli
  credentials:
    secret: ${KEYCLOAK_ADMIN_CLI_SECRET}
```

---

### 9.5 Frontend Implementation Details

#### Mobile App (`mobile/`) — Flutter

**Screens required for MS-01** (map to existing prototypes in `shared-resources/docs/ui/mobile/`):

| Screen | File | Prototype |
|---|---|---|
| Login | `features/user/presentation/pages/login_screen.dart` | `shared-resources/docs/ui/mobile/login.html` |
| Registration | `features/user/presentation/pages/registration_screen.dart` | `shared-resources/docs/ui/mobile/registration.html` |
| Forgot Password | `features/user/presentation/pages/forgot_password_screen.dart` | `shared-resources/docs/ui/mobile/password-reset.html` |
| Profile | `features/user/presentation/pages/profile_screen.dart` | `shared-resources/docs/ui/mobile/profile.html` |

**Deep Link setup** (for password reset redirect back into the app):
- Configure a custom URL scheme (e.g., `urbanbloom://`) or App Links in `AndroidManifest.xml` / `Info.plist`
- `go_router` handles the incoming deep link and navigates to the appropriate screen

**Auth service** (`core/services/auth_service.dart`):
- Wraps all API calls to `auth/login`, `auth/logout`, `registration`, `users/me`
- Stores `accessToken` and `refreshToken` in `flutter_secure_storage`
- On app start: checks for stored token, validates, and navigates to home or login accordingly

> Reference: `prj_school-library-ref/frontend-mobile/lib/core/services/auth_service.dart`
> Reference: `prj_school-library-ref/frontend-mobile/lib/features/user/presentation/pages/`

#### Admin Web App (`admin-web/`) — Flutter Web

**Screens required for MS-01** (map to existing prototypes in `shared-resources/docs/ui/admin/`):

| Screen | File | Prototype |
|---|---|---|
| Admin Login | `features/access_control/presentation/pages/admin_login_page.dart` | `shared-resources/docs/ui/admin/login.html` |
| Dashboard (post-login) | `features/analytics/presentation/pages/dashboard_page.dart` | `shared-resources/docs/ui/admin/dashboard.html` |

**Forced password change handling**:
- On `401` response with `UPDATE_PASSWORD_REQUIRED`: extract `keycloakLoginUrl` from response body and open it in a new browser tab / redirect
- After the admin returns, trigger a fresh login

**Token storage**: Use `dart:html` `window.localStorage` (web-safe) or a session-scoped provider — never `flutter_secure_storage` on web (not supported)

> Reference: `EcoTrack/admin-web/lib/features/access_control/presentation/pages/`

---

### 9.6 Acceptance Criteria

| # | Criterion |
|---|---|
| AC-01 | A citizen can register with email + password; the account is inactive until the verification email is clicked |
| AC-02 | Login is rejected for unverified accounts with a clear error message |
| AC-03 | A verified citizen can log in with correct credentials and receives an access token |
| AC-04 | An admin logs in via the admin login screen; first login forces a password change via Keycloak UI redirect |
| AC-05 | After a password change, the admin can log in again with the new password |
| AC-06 | "Forgot Password" sends an email; the reset link opens the Keycloak password page; after reset the user is redirected back to the app |
| AC-07 | Logout invalidates the session server-side (Keycloak revokes the token) |
| AC-08 | After logout, locally stored tokens are deleted and the user sees the login screen |
| AC-09 | `GET /api/v1/users/me` returns the enriched profile from PostgreSQL for an authenticated user |
| AC-10 | Password reset always returns 200 regardless of whether the email exists |

---

### 9.7 Definition of Done (MS-01)

- [ ] All 6 REST endpoints implemented and documented in `openapi/urbanbloom-api-v1.yaml`
- [ ] Unit tests for all domain classes (`UserProfile`, `Email`, `ExternalUserId`, `RegistrationService`)
- [ ] Integration tests for all 6 endpoints (using TestContainers + Keycloak test realm)
- [ ] Keycloak realms (`urbanbloom-mobile`, `urbanbloom-admin`) configured per Section 6.1
- [ ] Email templates configured in Keycloak (registration verification + password reset)
- [ ] Mobile screens: Login, Registration, Forgot Password, Profile — connected to API
- [ ] Admin web screens: Login — connected to API, forced-password-change redirect working
- [ ] Deep link / redirect URL configured for password reset return flow
- [ ] All CI pipeline jobs pass (GitHub Actions)

---

### 9.8 Key References for MS-01

| What | Where |
|---|---|
| Complete reference backend implementation | `prj_school-library-ref/backend/module-user/` |
| Keycloak identity provider adapter | `prj_school-library-ref/backend/module-user/src/.../keycloak/KeycloakIdentityProvider.java` |
| Auth controllers (login, registration, reset) | `prj_school-library-ref/backend/module-user/src/.../api/AuthController.java` |
| Registration controller | `prj_school-library-ref/backend/module-user/src/.../api/RegistrationController.java` |
| Mobile auth service | `prj_school-library-ref/frontend-mobile/lib/core/services/auth_service.dart` |
| Mobile login/registration/forgot-password screens | `prj_school-library-ref/frontend-mobile/lib/features/user/presentation/pages/` |
| Admin login page (Flutter Web) | `EcoTrack/admin-web/lib/features/access_control/presentation/pages/admin_login_page.dart` |
| Password reset walkthrough | `prj_school-library-ref/docs/implementation-details/walkthroughs/Password Reset Frontend Implementation - Walkthrough.md` |
| Backend auth implementation details | `prj_school-library-ref/docs/implementation-details/impl-details_backend_US-USR-01-REF_user-authentication.md` |
| Backend registration implementation details | `prj_school-library-ref/docs/implementation-details/impl-details_backend_US-USR-02-REF_self-registration.md` |
| Backend-Frontend auth explanation | `prj_school-library-ref/docs/chats/backend-frontend/authentication-explanation.chat.md` |
| Password reset deep-link chat | `prj_school-library-ref/docs/chats/backend-frontend/Backend Password Reset and deep-link.md` |
| Keycloak DPoP lesson learned | `prj_school-library-ref/docs/lessons-learnded/backend/keycloak-dpop-login.md` |
| GreenMail integration lesson | `prj_school-library-ref/docs/lessons-learnded/backend/lessons-learned-greenmail-keycloak-integration.md` |
| JPA + Mail lesson | `prj_school-library-ref/docs/lessons-learnded/backend/lessons-learned-jpa-and-mail.md` |
