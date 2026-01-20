# 🗂️ Frontend Mobile - Project Structure

This document provides an overview of the folder structure and key files.

## 📁 Directory Structure

```
frontend-mobile/
├── lib/
│   ├── main.dart                           # App entry point
│   ├── core/                               # Cross-cutting concerns
│   │   ├── di/
│   │   │   └── providers.dart              # Riverpod DI providers
│   │   ├── network/
│   │   │   └── api_client.dart             # Dio HTTP client config
│   │   └── routing/
│   │       └── app_router.dart             # GoRouter configuration
│   ├── design_system/                      # Design System
│   │   ├── design_tokens.dart              # Colors, Spacing, Typography
│   │   └── components/
│   │       ├── atoms/                      # Basic components
│   │       │   ├── primary_button.dart
│   │       │   └── status_badge.dart
│   │       ├── molecules/                  # (empty - for future use)
│   │       └── organisms/                  # (empty - for future use)
│   └── features/                           # Feature modules (Bounded Contexts)
│       └── health/                         # Health Check feature
│           ├── data/
│           │   ├── models/
│           │   │   ├── health_dto.dart
│           │   │   ├── health_dto.freezed.dart   # Generated
│           │   │   └── health_dto.g.dart         # Generated
│           │   └── repositories/
│           │       └── health_repository.dart
│           └── presentation/
│               └── pages/
│                   └── health_check_page.dart
├── test/                                   # Widget & Unit Tests
│   ├── design_system/
│   │   └── primary_button_test.dart
│   └── features/
│       └── health/
│           └── health_check_page_test.dart
├── widgetbook/                             # Component catalog (prepared)
├── pubspec.yaml                            # Dependencies
├── analysis_options.yaml                   # Linting rules
└── README.md                               # Documentation

```

## 📄 Key Files

### Entry Point
- **`lib/main.dart`**: App initialization with ProviderScope and MaterialApp.router

### Core Layer
- **`core/di/providers.dart`**: Dependency Injection container (Riverpod)
- **`core/network/api_client.dart`**: Dio configuration for HTTP requests
- **`core/routing/app_router.dart`**: GoRouter route definitions

### Design System
- **`design_system/design_tokens.dart`**: All design constants (colors, spacing, typography)
- **`design_system/components/atoms/primary_button.dart`**: Reusable button component
- **`design_system/components/atoms/status_badge.dart`**: Status indicator component

### Features
- **`features/health/data/models/health_dto.dart`**: Health status data model
- **`features/health/data/repositories/health_repository.dart`**: API calls for health check
- **`features/health/presentation/pages/health_check_page.dart`**: Main Hello World screen

### Tests
- **`test/design_system/primary_button_test.dart`**: Atom component tests
- **`test/features/health/health_check_page_test.dart`**: Page widget tests

## 🎨 Component Hierarchy

### Atomic Design Levels

```
Page (Smart Component - Stateful)
  └── HealthCheckPage
        ├── AppBar
        ├── Organism: _HealthStatusCard
        │     ├── Atom: StatusBadge
        │     └── Icon
        ├── Molecule: _ErrorCard
        └── Atom: PrimaryButton
```

### Smart vs Presentation Components

**Smart Components** (`StatefulWidget`):
- Handle data fetching
- Manage local state
- Use Riverpod providers
- Example: `HealthCheckPage`

**Presentation Components** (`StatelessWidget`):
- Only UI rendering
- Props via constructor
- Emit events via callbacks
- Example: `PrimaryButton`, `StatusBadge`

## 🔄 Data Flow

```
User Action (Button Tap)
  ↓
Smart Component (HealthCheckPage)
  ↓
Riverpod Provider (healthRepositoryProvider)
  ↓
Repository (HealthRepository)
  ↓
API Client (Dio)
  ↓
Backend (http://localhost:8080/health)
  ↓
DTO (HealthDTO)
  ↓
State Update (setState)
  ↓
UI Re-render
```

## 📦 Dependencies Overview

| Package | Purpose | Layer |
|---------|---------|-------|
| `flutter_riverpod` | State management, DI | Core |
| `go_router` | Navigation | Core |
| `dio` | HTTP client | Data |
| `freezed` | Immutable models | Data |
| `flutter_test` | Testing | Test |
| `mockito` | Mocking | Test |
| `widgetbook` | Component docs | Docs |

## 🚀 Next Steps

### Add New Feature (e.g., Catalog)

1. **Create feature structure:**
   ```
   lib/features/catalog/
   ├── data/
   │   ├── models/book_dto.dart
   │   └── repositories/catalog_repository.dart
   └── presentation/
       ├── atoms/
       ├── molecules/
       ├── organisms/
       └── pages/catalog_page.dart
   ```

2. **Add provider** in `core/di/providers.dart`

3. **Add route** in `core/routing/app_router.dart`

4. **Write tests** in `test/features/catalog/`

### Add New Atom Component

1. Create in `design_system/components/atoms/`
2. Use design tokens
3. Make it `StatelessWidget`
4. Add to Widgetbook (future)
5. Write widget tests

## 📚 References

- Flutter Docs: https://docs.flutter.dev/
- Riverpod Docs: https://riverpod.dev/
- Atomic Design: https://atomicdesign.bradfrost.com/

---

**Maintained by:** Frontend Team  
**Last Updated:** December 28, 2025
