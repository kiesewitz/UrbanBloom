# 📱 School Library Mobile App

Digital School Library - Mobile Application built with Flutter following Component-Driven Development (CDD) and Atomic Design principles.

## 🎯 Purpose

This is a "Hello World" infrastructure setup application to test:
- Flutter project structure with CDD/Atomic Design
- Backend connectivity via Health Check endpoint
- State management with Riverpod
- Navigation with GoRouter
- HTTP communication with Dio
- Widget testing setup

**⚠️ Note:** This version does NOT implement concrete functional requirements. It's a foundation for feature development.

## 🏗️ Architecture

### Component-Driven Development (CDD)
Follows **Atomic Design** methodology:
- **Atoms**: Basic UI building blocks (`PrimaryButton`, `StatusBadge`)
- **Molecules**: Simple component compositions
- **Organisms**: Complex UI components (`_HealthStatusCard`)
- **Templates**: Page layouts without data
- **Pages**: Complete screens with data (`HealthCheckPage`)

### Feature-First Structure
Organized by bounded contexts aligned with DDD backend:
```
lib/
├── core/                          # Cross-cutting concerns
│   ├── di/                        # Dependency Injection (Riverpod providers)
│   ├── network/                   # API Client configuration
│   └── routing/                   # App navigation (GoRouter)
├── design_system/                 # Centralized Design System
│   ├── components/
│   │   ├── atoms/                 # Basic components
│   │   ├── molecules/             # Composite components
│   │   └── organisms/             # Complex components
│   └── design_tokens.dart         # Colors, Spacing, Typography
└── features/                      # Feature modules (bounded contexts)
    └── health/                    # Health Check feature
        ├── data/
        │   ├── models/            # DTOs (Data Transfer Objects)
        │   └── repositories/      # Data access layer
        └── presentation/
            └── pages/             # Smart components (Stateful)
```

## 🛠️ Technology Stack

| Category | Technology | Purpose |
|----------|-----------|---------|
| **Framework** | Flutter 3.x | Mobile UI framework |
| **Language** | Dart | Programming language |
| **State Management** | Riverpod | Dependency injection & state |
| **Navigation** | GoRouter | Declarative routing |
| **HTTP Client** | Dio | Backend communication |
| **Code Generation** | Freezed | Immutable models |
| **Testing** | flutter_test | Widget & unit tests |
| **Documentation** | Widgetbook | Component catalog |

## 📋 Prerequisites

- Flutter SDK ≥ 3.10.0
- Dart SDK (included with Flutter)
- VS Code or Android Studio
- Git

## 🚀 Quick Start

### 1. Install Dependencies
```bash
cd frontend-mobile
flutter pub get
```

### 2. Generate Code (Freezed models)
```bash
dart run build_runner build --delete-conflicting-outputs
```

### 3. Run Tests
```bash
flutter test
```
**Expected:** All 9 widget tests pass ✅

### 4. Run Application
```bash
flutter run
```
**Starts on connected device/emulator**

### 5. Test Health Check
1. Ensure backend is running on `http://localhost:8080`
2. In app, press **"Check Backend Health"** button
3. Should display status: UP ✅ or DOWN ❌

## 🧪 Testing

### Run All Tests
```bash
flutter test
```

### Run Specific Test
```bash
flutter test test/features/health/health_check_page_test.dart
```

### Test Coverage
```bash
flutter test --coverage
```

## 📐 Design System

### Design Tokens (`design_tokens.dart`)
Centralized constants for consistency:

| Token Type | Examples |
|------------|----------|
| **Colors** | `AppColors.primary`, `AppColors.success` |
| **Spacing** | `AppSpacing.sm`, `AppSpacing.lg` |
| **Typography** | `AppTypography.headlineLarge` |
| **Radius** | `AppRadius.md`, `AppRadius.round` |
| **Elevation** | `AppElevation.sm` |

### Usage Example
```dart
Container(
  padding: const EdgeInsets.all(AppSpacing.lg),
  decoration: BoxDecoration(
    color: AppColors.primary,
    borderRadius: BorderRadius.circular(AppRadius.md),
  ),
  child: Text(
    'Hello',
    style: AppTypography.headlineLarge,
  ),
)
```

## 🔌 Backend Configuration

### API Base URL
Default: `http://localhost:8080/api/v1`

### Change Backend URL
Edit `lib/core/network/api_client.dart`:
```dart
static const String baseUrl = 'YOUR_BACKEND_URL';
```

## 🏗️ Adding Features

### 1. Create Feature Module
```bash
# Example: Catalog feature
lib/features/catalog/
├── data/
│   ├── models/
│   │   └── book_dto.dart
│   └── repositories/
│       └── catalog_repository.dart
└── presentation/
    ├── atoms/
    ├── molecules/
    ├── organisms/
    └── pages/
        └── catalog_page.dart
```

### 2. Create DTOs with Freezed
```dart
@freezed
class BookDTO with _$BookDTO {
  const factory BookDTO({
    required String id,
    required String title,
  }) = _BookDTO;

  factory BookDTO.fromJson(Map<String, dynamic> json) =>
      _$BookDTOFromJson(json);
}
```

### 3. Add Provider (DI)
In `lib/core/di/providers.dart`:
```dart
final catalogRepositoryProvider = Provider<CatalogRepository>((ref) {
  final apiClient = ref.watch(apiClientProvider);
  return CatalogRepository(apiClient.dio);
});
```

### 4. Add Route
In `lib/core/routing/app_router.dart`:
```dart
GoRoute(
  path: '/catalog',
  name: 'catalog',
  builder: (context, state) => const CatalogPage(),
)
```

## 🎨 Widgetbook (Component Documentation)

**Note:** Widgetbook setup is prepared but not yet implemented.

To add component stories:
```bash
# Create widgetbook main
lib/widgetbook/widgetbook.dart

# Run widgetbook
flutter run -t lib/widgetbook/widgetbook.dart
```

## 🐛 Troubleshooting

### "Failed to connect to backend"
- ✅ Backend running on `http://localhost:8080`?
- ✅ Android Emulator? Use `http://10.0.2.2:8080` instead
- ✅ iOS Simulator? Use actual IP address

### Code generation issues
```bash
flutter clean
flutter pub get
dart run build_runner clean
dart run build_runner build --delete-conflicting-outputs
```

### Test failures
```bash
flutter clean
flutter pub get
flutter test
```

## 📚 References

- **Ubiquitous Language**: `docs/architecture/ubiquitous-language-glossar-complete.md`
- **Bounded Contexts**: `docs/architecture/bounded-contexts-map.md`
- **Backend README**: `backend/README.md`

## 🎯 Acceptance Criteria

- ✅ `flutter pub get` succeeds
- ✅ `flutter run` starts without errors
- ✅ Hello World Screen displayed
- ✅ Health Check Button works
- ✅ 9 Widget Tests passing
- ✅ NO Dart analysis errors

## 📄 License

© 2025 School Library Project. Internal use only.

---

**Built with ❤️ using Flutter & Component-Driven Development**

