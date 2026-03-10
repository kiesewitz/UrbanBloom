# UrbanBloom — Complete Project Specification (AI-Optimized)

> **Deadline**: January 27 — all changes must be pushed to GitHub **BEFORE** the class session.
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
