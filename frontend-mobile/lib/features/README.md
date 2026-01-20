# Features Layer

**Zweck:** Feature-Module nach Bounded Contexts organisiert

---

## Übersicht

Die **Features Layer** enthält alle Feature-Module der App, organisiert nach den **Bounded Contexts** aus der Backend-Architektur:

```
features/
├── lending/        # Core Domain - Ausleihe & Reservierung
├── catalog/        # Supporting - Mediensuche & Details
├── user/           # Generic - Benutzerprofil
└── health/         # Testing/Monitoring
```

---

## Feature-Struktur (Convention)

Jedes Feature folgt dieser Struktur:

```
<feature>/
├── data/                           # Data Layer
│   ├── models/                     # DTOs (freezed + json_serializable)
│   └── repositories/               # API Repositories
│
└── presentation/                   # Presentation Layer (CDD)
    ├── atoms/                      # Feature-spezifische Basis-Komponenten
    ├── molecules/                  # Zusammengesetzte Komponenten
    ├── organisms/                  # Komplexe UI-Blöcke
    └── pages/                      # Smart Components (StatefulWidget)
```

---

## Data Layer

### 📦 `data/models/`

**DTOs (Data Transfer Objects):**
- Repräsentieren API-Responses/Requests
- Nutzen `freezed` für Immutability
- JSON-Serialisierung mit `json_serializable`
- Naming Convention: `*DTO` suffix

**Beispiel:**
```dart
import 'package:freezed_annotation/freezed_annotation.dart';

part 'loan_dto.freezed.dart';
part 'loan_dto.g.dart';

@freezed
class LoanDTO with _$LoanDTO {
  const factory LoanDTO({
    required String id,
    required String mediaTitle,
    required DateTime dueDate,
  }) = _LoanDTO;

  factory LoanDTO.fromJson(Map<String, dynamic> json) => _$LoanDTOFromJson(json);
}
```

### 📦 `data/repositories/`

**Repositories:**
- API-Aufrufe via Dio
- Error Handling mit `Either<Failure, Data>`
- Dependency Injection über Constructor
- Nutzen `ApiEndpoints` Konstanten

**Beispiel:**
```dart
import 'package:dartz/dartz.dart';

class LoanRepository {
  final Dio apiClient;

  LoanRepository({required this.apiClient});

  Future<Either<Failure, List<LoanDTO>>> getMyLoans() async {
    try {
      final response = await apiClient.get('/loans');
      final loans = (response.data as List)
          .map((json) => LoanDTO.fromJson(json))
          .toList();
      return Right(loans);
    } catch (e) {
      return Left(ServerFailure('Error: $e'));
    }
  }
}
```

---

## Presentation Layer (Component-Driven Development)

### 📦 `presentation/atoms/`

**Feature-spezifische Basis-Komponenten:**
- Stateless Widgets
- Props über Constructor
- Events über Callbacks
- Verwenden Design Tokens

**Beispiele:**
- `DueDateBadge` (Lending)
- `AvailabilityBadge` (Catalog)
- `UserAvatar` (User)

### 📦 `presentation/molecules/`

**Zusammengesetzte Komponenten:**
- Kombinieren Atoms
- Stateless
- Feature-spezifisch

**Beispiele:**
- `LoanListItem` (Lending)
- `MediaCard` (Catalog)
- `ProfileHeader` (User)

### 📦 `presentation/organisms/`

**Komplexe UI-Blöcke:**
- Kombinieren Molecules + Atoms
- Können lokalen UI-State haben
- Wiederverwendbar

**Beispiele:**
- `LoanList` (Lending)
- `MediaGrid` (Catalog)
- `ProfileCard` (User)

### 📦 `presentation/pages/`

**Smart Components (Pages):**
- `StatefulWidget` oder `ConsumerStatefulWidget` (Riverpod)
- Data Fetching
- State Management
- Error Handling
- Delegieren an Presentation Components

**Best Practices:**
```dart
class MyLoansPage extends ConsumerStatefulWidget {
  const MyLoansPage({super.key});

  @override
  ConsumerState<MyLoansPage> createState() => _MyLoansPageState();
}

class _MyLoansPageState extends ConsumerState<MyLoansPage> {
  List<LoanDTO> _loans = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    // Fetch data from repository
    final repository = ref.read(loanRepositoryProvider);
    final result = await repository.getMyLoans();
    
    result.fold(
      (failure) => _showError(failure.message),
      (loans) => setState(() {
        _loans = loans;
        _isLoading = false;
      }),
    );
  }

  @override
  Widget build(BuildContext context) {
    return PageTemplate(
      title: 'Meine Ausleihen',
      body: LoanList( // Organism
        loans: _loans,
        isLoading: _isLoading,
        onReturnTap: _handleReturn,
      ),
    );
  }
}
```

---

## Bounded Contexts Alignment

Features entsprechen den Backend Bounded Contexts:

| Feature | Backend Context | Domain Type |
|---------|----------------|-------------|
| `lending/` | Lending Context | Core Domain |
| `catalog/` | Catalog Context | Supporting Domain |
| `user/` | User Context | Generic Domain |

**Vorteile:**
- ✅ Frontend-Teams kennen Backend-Domänen
- ✅ Klare Ownership
- ✅ Einfachere API-Integration
- ✅ Konsistente Ubiquitous Language

---

## Testing

Jedes Feature sollte Tests haben:

### Widget Tests (Presentation Components)

```
test/features/<feature>/presentation/
├── atoms/
├── molecules/
├── organisms/
└── pages/
```

**Beispiel:**
```dart
// test/features/lending/presentation/molecules/loan_list_item_test.dart
void main() {
  testWidgets('LoanListItem displays loan info', (tester) async {
    final loan = LoanDTO(
      id: '1',
      mediaTitle: 'Test Book',
      dueDate: DateTime.now(),
    );

    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          body: LoanListItem(loan: loan),
        ),
      ),
    );

    expect(find.text('Test Book'), findsOneWidget);
  });
}
```

### Unit Tests (Repositories)

```
test/features/<feature>/data/repositories/
```

---

## Entwicklungsrichtlinien

1. **Feature-first:** Organisiere Code nach Feature, nicht nach Layer
2. **CDD:** Baue von Atoms → Molecules → Organisms → Pages
3. **Presentation Components:** Stateless, Props-driven, Event-emitting
4. **Smart Components:** Nur Pages dürfen Data Fetching + State Management
5. **Backend Alignment:** Nutze gleiche Terminologie wie Backend
6. **Test Coverage:** Widget-Tests für alle Presentation Components

---

## Neue Features hinzufügen

### 1. Erstelle Feature-Ordner

```
features/<new-feature>/
├── data/
│   ├── models/
│   └── repositories/
└── presentation/
    ├── atoms/
    ├── molecules/
    ├── organisms/
    └── pages/
```

### 2. Erstelle README.md

Beschreibe:
- Bounded Context
- User Stories
- Data Models (DTOs)
- Presentation Components

### 3. Implementiere Data Layer

- DTOs mit `freezed`
- Repository mit `Either<Failure, Data>`

### 4. Implementiere Presentation Layer

- Atoms → Molecules → Organisms → Pages
- Widget-Tests parallel entwickeln

### 5. Provider in `core/di/providers.dart`

```dart
final newFeatureRepositoryProvider = Provider<NewFeatureRepository>((ref) {
  return NewFeatureRepository(apiClient: ref.watch(apiClientProvider));
});
```

---

## Referenzen

- 📖 [Design System README](../design_system/README.md)
- 📚 [Core Layer README](../core/README.md)
- 🎯 [Backend Architecture](../../docs/architecture/README.md)
- 🗺️ [Bounded Contexts Map](../../docs/architecture/bounded-contexts-map.md)
