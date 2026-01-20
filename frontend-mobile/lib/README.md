# Frontend Mobile - lib/ Structure

**Tech Stack:** Flutter 3.x, Dart, Riverpod, GoRouter, Freezed  
**Architecture:** Component-Driven Development (CDD) + Atomic Design + Feature-First + DDD Alignment

---

## 📁 Gesamtstruktur

```
lib/
├── main.dart                       # App Entry Point
├── core/                           # Cross-cutting Infrastructure
│   ├── di/                         # Dependency Injection (Riverpod)
│   ├── network/                    # API Client (Dio)
│   ├── routing/                    # Navigation (GoRouter)
│   ├── error/                      # Error Handling
│   ├── utils/                      # Helper Functions
│   └── constants/                  # App Constants
│
├── design_system/                  # Design System (Atomic Design)
│   ├── design_tokens.dart          # Colors, Spacing, Typography
│   └── components/
│       ├── atoms/                  # Basis-Komponenten
│       ├── molecules/              # Zusammengesetzte Komponenten
│       ├── organisms/              # Komplexe UI-Blöcke
│       └── templates/              # Layout-Templates
│
└── features/                       # Feature-Module (Bounded Contexts)
    ├── lending/                    # Core Domain - Ausleihe & Reservierung
    ├── catalog/                    # Supporting - Mediensuche & Details
    ├── user/                       # Generic - Benutzerprofil
    └── health/                     # Testing/Monitoring
```

---

## 🎨 Architektur-Prinzipien

### 1. **Component-Driven Development (CDD)**

UI wird von unten nach oben gebaut:
```
Atoms (Buttons, Badges)
  → Molecules (Cards, List Items)
    → Organisms (Lists, Grids)
      → Templates (Page Layouts)
        → Pages (Smart Components)
```

**Vorteile:**
- ✅ Wiederverwendbare Komponenten
- ✅ Konsistentes Design
- ✅ Isoliertes Testen
- ✅ Widgetbook-Dokumentation

### 2. **Atomic Design**

| Level | Beschreibung | Beispiel |
|-------|-------------|----------|
| **Atom** | Kleinstmögliche UI-Elemente | `PrimaryButton`, `StatusBadge` |
| **Molecule** | Kombinieren Atoms | `MediaCard`, `LoanListItem` |
| **Organism** | Komplexe UI-Blöcke | `LoanList`, `MediaGrid` |
| **Template** | Layout-Struktur | `PageTemplate`, `ListPageTemplate` |
| **Page** | Smart Components mit State | `MyLoansPage`, `CatalogPage` |

### 3. **Feature-First + DDD Alignment**

Features entsprechen Backend Bounded Contexts:

| Frontend Feature | Backend Context | Domain Type |
|-----------------|----------------|-------------|
| `features/lending/` | Lending Context | Core Domain |
| `features/catalog/` | Catalog Context | Supporting Domain |
| `features/user/` | User Context | Generic Domain |

**Vorteile:**
- ✅ Klare Ownership
- ✅ Konsistente Terminologie (Ubiquitous Language)
- ✅ Einfachere API-Integration
- ✅ Skalierbar (neue Features = neue Ordner)

### 4. **Smart vs Presentation Components**

**Presentation Components (Atoms, Molecules, Organisms):**
- ✅ `StatelessWidget`
- ✅ Props über Constructor
- ✅ Events über Callbacks (`onPressed`, `onChanged`)
- ✅ Keine Business-Logik
- ✅ Design Tokens verwenden

**Smart Components (Pages):**
- ✅ `StatefulWidget` oder `ConsumerStatefulWidget`
- ✅ Data Fetching (Repository)
- ✅ State Management (Riverpod)
- ✅ Error Handling
- ✅ Delegieren an Presentation Components

---

## 📦 Layer-Beschreibungen

### [core/](core/README.md)

**Zweck:** Cross-cutting Infrastructure

- **`di/`** - Dependency Injection (Riverpod Providers)
- **`network/`** - API Client (Dio Configuration)
- **`routing/`** - Navigation (GoRouter)
- **`error/`** - Custom Exceptions & Failures
- **`utils/`** - Helper Functions (DateFormatter, Validators)
- **`constants/`** - API Endpoints, App Constants

**Wichtig:** Keine Feature-spezifische Logik!

---

### [design_system/](design_system/README.md)

**Zweck:** Wiederverwendbare UI-Komponenten

- **`design_tokens.dart`** - Colors, Spacing, Typography (zentral!)
- **`components/atoms/`** - `PrimaryButton`, `StatusBadge`, `InputField`
- **`components/molecules/`** - `MediaCard`, `SearchBar`, `FormField`
- **`components/organisms/`** - `LoanList`, `MediaGrid`, `NavigationBar`
- **`components/templates/`** - `PageTemplate`, `ListPageTemplate`

**Best Practices:**
- ✅ Alle Komponenten konsumieren Design Tokens
- ✅ Widget-Tests für alle Komponenten
- ✅ Widgetbook-Dokumentation

---

### [features/](features/README.md)

**Zweck:** Feature-Module nach Bounded Contexts

Jedes Feature hat:
```
<feature>/
├── data/
│   ├── models/                     # DTOs (freezed)
│   └── repositories/               # API Repositories
└── presentation/
    ├── atoms/                      # Feature-spezifische Atoms
    ├── molecules/                  # Feature-spezifische Molecules
    ├── organisms/                  # Feature-spezifische Organisms
    └── pages/                      # Smart Components
```

**Features:**
- [**lending/**](features/lending/README.md) - Ausleihe, Rückgabe, Verlängerung
- [**catalog/**](features/catalog/README.md) - Mediensuche, -details
- [**user/**](features/user/README.md) - Benutzerprofil, Einstellungen
- **health/** - Health Check (Testing)

---

## 🚀 Entwicklungs-Workflow

### 1. **Design Tokens definieren**

Bevor du Komponenten baust, definiere Design Tokens:
```dart
// design_system/design_tokens.dart
class DesignTokens {
  static const Color primaryColor = Color(0xFF1976D2);
  static const double spacingMd = 16.0;
  static const TextStyle bodyStyle = TextStyle(fontSize: 16);
}
```

### 2. **Design System Komponenten bauen (CDD)**

**Reihenfolge: Atoms → Molecules → Organisms → Templates**

```dart
// 1. Atom
class PrimaryButton extends StatelessWidget { ... }

// 2. Molecule (nutzt Atoms)
class MediaCard extends StatelessWidget {
  // Verwendet: StatusBadge (Atom)
}

// 3. Organism (nutzt Molecules + Atoms)
class MediaGrid extends StatelessWidget {
  // Verwendet: MediaCard (Molecule)
}

// 4. Template
class PageTemplate extends StatelessWidget { ... }
```

### 3. **Feature Data Layer implementieren**

```dart
// 1. DTO
@freezed
class LoanDTO with _$LoanDTO {
  const factory LoanDTO({
    required String id,
    required String mediaTitle,
    required DateTime dueDate,
  }) = _LoanDTO;

  factory LoanDTO.fromJson(Map<String, dynamic> json) => _$LoanDTOFromJson(json);
}

// 2. Repository
class LoanRepository {
  Future<Either<Failure, List<LoanDTO>>> getMyLoans() async { ... }
}

// 3. Provider
final loanRepositoryProvider = Provider<LoanRepository>((ref) {
  return LoanRepository(apiClient: ref.watch(apiClientProvider));
});
```

### 4. **Feature Presentation Layer implementieren**

```dart
// 1. Feature-spezifische Atoms/Molecules/Organisms
class DueDateBadge extends StatelessWidget { ... }
class LoanListItem extends StatelessWidget { ... }
class LoanList extends StatelessWidget { ... }

// 2. Smart Component (Page)
class MyLoansPage extends ConsumerStatefulWidget {
  @override
  Widget build(BuildContext context) {
    return PageTemplate( // Template
      body: LoanList( // Organism
        loans: _loans,
        onReturnTap: _handleReturn,
      ),
    );
  }
}
```

### 5. **Tests schreiben**

```dart
// Widget Test für Presentation Component
testWidgets('LoanListItem displays title', (tester) async {
  await tester.pumpWidget(
    MaterialApp(
      home: Scaffold(
        body: LoanListItem(loan: testLoan),
      ),
    ),
  );
  
  expect(find.text('Test Book'), findsOneWidget);
});
```

### 6. **Widgetbook dokumentieren**

```dart
@UseCase(name: 'Primary Button', type: PrimaryButton)
Widget primaryButtonUseCase(BuildContext context) {
  return PrimaryButton(
    text: context.knobs.string(label: 'Text', initialValue: 'Click Me'),
    onPressed: () {},
  );
}
```

---

## 🧪 Testing-Strategie

### 1. **Widget Tests (Presentation Components)**

```
test/design_system/components/atoms/
test/design_system/components/molecules/
test/features/<feature>/presentation/
```

**Was testen:**
- ✅ Komponenten rendern korrekt
- ✅ Props werden angezeigt
- ✅ Events werden gefeuert
- ✅ Conditional Rendering

### 2. **Unit Tests (Repositories, Utils)**

```
test/core/utils/
test/features/<feature>/data/repositories/
```

**Was testen:**
- ✅ Repository Error Handling
- ✅ DTO Serialization
- ✅ Helper Functions

### 3. **Integration Tests (E2E)**

```
integration_test/
```

**Was testen:**
- ✅ User Flows (Login → Browse → Borrow → Return)
- ✅ Navigation
- ✅ API Integration

---

## 📚 Entwicklungsrichtlinien

### Design System

1. **Design Tokens first:** Keine hardcoded Farben/Spacing
2. **Stateless Components:** Presentation Components haben keinen State
3. **Props-driven:** Alle Werte über Constructor
4. **Events über Callbacks:** `onPressed`, `onChanged`, etc.
5. **Test Coverage:** Widget-Test für jede Komponente
6. **Widgetbook:** Dokumentiere alle Komponenten

### Features

1. **Feature-first:** Organisiere nach Feature, nicht nach Layer
2. **DDD Alignment:** Nutze Backend-Terminologie (Ubiquitous Language)
3. **DTOs mit Freezed:** Immutable Data Models
4. **Either für Error Handling:** `Either<Failure, Data>`
5. **Smart Components = Pages:** Nur Pages dürfen Data Fetching + State Management
6. **Dependency Injection:** Repositories über Riverpod Providers

### Core

1. **Wiederverwendbar:** Alles hier muss von allen Features nutzbar sein
2. **Keine Feature-Logik:** Core ist nur Infrastruktur
3. **Typsicher:** Explizite Typen in Providern
4. **Testbar:** DI ermöglicht Mocking

---

## 🔧 Nützliche Commands

```bash
# Code generieren (freezed, json_serializable)
flutter pub run build_runner build --delete-conflicting-outputs

# Tests ausführen
flutter test

# Test Coverage
flutter test --coverage
genhtml coverage/lcov.info -o coverage/html

# Widgetbook starten
flutter run -t widgetbook/main.dart

# Format Code
dart format .

# Analyze
flutter analyze
```

---

## 📖 Referenzen

- 📚 [Flutter Documentation](https://docs.flutter.dev/)
- 🎨 [Atomic Design](https://bradfrost.com/blog/post/atomic-web-design/)
- 🧩 [Riverpod Documentation](https://riverpod.dev/)
- 🌐 [Dio Documentation](https://pub.dev/packages/dio)
- 🧭 [GoRouter Documentation](https://pub.dev/packages/go_router)
- ❄️ [Freezed Documentation](https://pub.dev/packages/freezed)
- 🎯 [Backend Architecture](../docs/architecture/README.md)
- 🗺️ [Bounded Contexts Map](../docs/architecture/bounded-contexts-map.md)

---

## 📝 Hinweise

**Diese Struktur ist:**
- ✅ Skalierbar (neue Features = neue Ordner)
- ✅ Wartbar (klare Separation of Concerns)
- ✅ Testbar (Dependency Injection + Isolierte Komponenten)
- ✅ DDD-aligned (Frontend kennt Backend-Domänen)
- ✅ CDD-konform (Bottom-Up Component Development)

**Bei Fragen:**
- Lies die README.md-Dateien in den jeweiligen Ordnern
- Schaue in bestehende Features (z.B. `health/`)
- Nutze Widgetbook für Component-Übersicht
