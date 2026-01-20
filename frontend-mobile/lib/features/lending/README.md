# Feature: Lending Context

**Bounded Context:** Lending (Core Domain)  
**Zweck:** Ausleihe, Rückgabe, Verlängerung, Reservierung

---

## Übersicht

Das **Lending Feature** implementiert die Core Domain der App. Nutzer können hier ihre aktiven Ausleihen verwalten, Medien verlängern, zurückgeben und reservieren.

### User Stories

- ✅ Als Nutzer kann ich meine aktiven Ausleihen sehen
- ✅ Als Nutzer kann ich ein Medium verlängern
- ✅ Als Nutzer kann ich ein Medium zurückgeben
- ✅ Als Nutzer kann ich ein verfügbares Medium reservieren
- ✅ Als Nutzer kann ich ein verliehenes Medium vormerken (Warteliste)
- ✅ Als Nutzer sehe ich Überfälligkeits-Warnungen

---

## Struktur

```
lending/
├── data/                           # Data Layer
│   ├── models/                     # DTOs (API-Modelle)
│   │   ├── loan_dto.dart
│   │   ├── reservation_dto.dart
│   │   └── prereservation_dto.dart
│   └── repositories/               # API Repositories
│       ├── loan_repository.dart
│       └── reservation_repository.dart
│
└── presentation/                   # Presentation Layer
    ├── atoms/                      # Feature-spezifische Atoms
    │   ├── due_date_badge.dart     # Fälligkeitsdatum mit Farbe
    │   └── renewal_count_chip.dart # Verlängerungs-Zähler
    ├── molecules/                  # Feature-spezifische Molecules
    │   ├── loan_list_item.dart     # Einzelner Ausleihen-Eintrag
    │   └── reservation_card.dart   # Reservierungs-Karte
    ├── organisms/                  # Feature-spezifische Organisms
    │   ├── loan_list.dart          # Liste aller Ausleihen
    │   └── reservation_list.dart   # Liste aller Reservierungen
    └── pages/                      # Smart Components (Pages)
        ├── my_loans_page.dart      # Hauptseite: Meine Ausleihen
        └── loan_detail_page.dart   # Detail-Seite einer Ausleihe
```

---

## Data Layer

### 📦 `data/models/`

**Zweck:** DTOs für API-Kommunikation

**Beispiel: `loan_dto.dart`**
```dart
import 'package:freezed_annotation/freezed_annotation.dart';

part 'loan_dto.freezed.dart';
part 'loan_dto.g.dart';

@freezed
class LoanDTO with _$LoanDTO {
  const factory LoanDTO({
    required String id,
    required String userId,
    required String mediaBarcode,
    required String mediaTitle,
    required String mediaAuthor,
    required DateTime dueDate,
    required String status, // CHECKED_OUT, RETURNED, OVERDUE
    required int renewalCount,
    required int maxRenewals,
    bool? hasPreReservation,
  }) = _LoanDTO;

  factory LoanDTO.fromJson(Map<String, dynamic> json) => _$LoanDTOFromJson(json);
}
```

**Best Practices:**
- ✅ Nutze `freezed` für Immutability
- ✅ JSON-Serialisierung mit `json_serializable`
- ✅ Alle Felder required, außer optionale
- ✅ Naming Convention: `*DTO` suffix

---

### 📦 `data/repositories/`

**Zweck:** API-Aufrufe für Lending-Domain

**Beispiel: `loan_repository.dart`**
```dart
import 'package:dio/dio.dart';
import 'package:dartz/dartz.dart';
import '../../core/error/failure.dart';
import '../../core/constants/api_endpoints.dart';
import '../models/loan_dto.dart';

class LoanRepository {
  final Dio apiClient;

  LoanRepository({required this.apiClient});

  Future<Either<Failure, List<LoanDTO>>> getMyLoans() async {
    try {
      final response = await apiClient.get(ApiEndpoints.loans);
      final loans = (response.data as List)
          .map((json) => LoanDTO.fromJson(json))
          .toList();
      return Right(loans);
    } on DioException catch (e) {
      return Left(_handleError(e));
    }
  }

  Future<Either<Failure, LoanDTO>> renewLoan(String loanId) async {
    try {
      final response = await apiClient.post(ApiEndpoints.renewLoan(loanId));
      final loan = LoanDTO.fromJson(response.data);
      return Right(loan);
    } on DioException catch (e) {
      return Left(_handleError(e));
    }
  }

  Future<Either<Failure, void>> returnLoan(String loanId) async {
    try {
      await apiClient.post(ApiEndpoints.returnLoan(loanId));
      return const Right(null);
    } on DioException catch (e) {
      return Left(_handleError(e));
    }
  }

  Failure _handleError(DioException e) {
    if (e.type == DioExceptionType.connectionTimeout) {
      return NetworkFailure('Connection timeout');
    }
    return ServerFailure(e.response?.data['message'] ?? 'Unknown error');
  }
}
```

**Best Practices:**
- ✅ Return `Either<Failure, Data>` für Error Handling
- ✅ Nutze `ApiEndpoints` für URLs
- ✅ Error Handling zentral in `_handleError()`
- ✅ Dependency Injection über Constructor

---

## Presentation Layer

### 📦 `presentation/atoms/`

**Beispiel: `due_date_badge.dart`**
```dart
class DueDateBadge extends StatelessWidget {
  const DueDateBadge({super.key, required this.dueDate});

  final DateTime dueDate;

  @override
  Widget build(BuildContext context) {
    final daysUntilDue = dueDate.difference(DateTime.now()).inDays;
    
    Color badgeColor;
    String text;
    
    if (daysUntilDue < 0) {
      badgeColor = DesignTokens.errorColor;
      text = 'Überfällig seit ${-daysUntilDue}d';
    } else if (daysUntilDue <= 3) {
      badgeColor = DesignTokens.warningColor;
      text = 'Fällig in ${daysUntilDue}d';
    } else {
      badgeColor = DesignTokens.successColor;
      text = 'Fällig ${DateFormatter.formatDate(dueDate)}';
    }

    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: DesignTokens.spacingSm,
        vertical: DesignTokens.spacingXs,
      ),
      decoration: BoxDecoration(
        color: badgeColor.withOpacity(0.2),
        borderRadius: BorderRadius.circular(DesignTokens.borderRadiusSm),
      ),
      child: Text(
        text,
        style: TextStyle(color: badgeColor, fontSize: 12, fontWeight: FontWeight.bold),
      ),
    );
  }
}
```

---

### 📦 `presentation/molecules/`

**Beispiel: `loan_list_item.dart`**
```dart
class LoanListItem extends StatelessWidget {
  const LoanListItem({
    super.key,
    required this.loan,
    this.onRenewTap,
    this.onReturnTap,
  });

  final LoanDTO loan;
  final VoidCallback? onRenewTap;
  final VoidCallback? onReturnTap;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(DesignTokens.spacingMd),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(loan.mediaTitle, style: DesignTokens.headlineStyle),
            const SizedBox(height: DesignTokens.spacingXs),
            Text(loan.mediaAuthor, style: DesignTokens.bodyStyle),
            const SizedBox(height: DesignTokens.spacingSm),
            DueDateBadge(dueDate: loan.dueDate), // Atom
            const SizedBox(height: DesignTokens.spacingMd),
            Row(
              children: [
                if (loan.renewalCount < loan.maxRenewals && loan.hasPreReservation != true)
                  SecondaryButton(
                    text: 'Verlängern (${loan.renewalCount}/${loan.maxRenewals})',
                    onPressed: onRenewTap,
                  ),
                const SizedBox(width: DesignTokens.spacingSm),
                PrimaryButton(
                  text: 'Zurückgeben',
                  onPressed: onReturnTap,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
```

---

### 📦 `presentation/organisms/`

**Beispiel: `loan_list.dart`**
```dart
class LoanList extends StatelessWidget {
  const LoanList({
    super.key,
    required this.loans,
    this.isLoading = false,
    this.onRenewTap,
    this.onReturnTap,
  });

  final List<LoanDTO> loans;
  final bool isLoading;
  final void Function(String loanId)? onRenewTap;
  final void Function(String loanId)? onReturnTap;

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (loans.isEmpty) {
      return const EmptyState(
        icon: Icons.book_outlined,
        message: 'Keine aktiven Ausleihen',
      );
    }

    return ListView.separated(
      itemCount: loans.length,
      separatorBuilder: (_, __) => const SizedBox(height: DesignTokens.spacingSm),
      itemBuilder: (context, index) {
        final loan = loans[index];
        return LoanListItem(
          loan: loan,
          onRenewTap: () => onRenewTap?.call(loan.id),
          onReturnTap: () => onReturnTap?.call(loan.id),
        );
      },
    );
  }
}
```

---

### 📦 `presentation/pages/`

**Beispiel: `my_loans_page.dart` (Smart Component)**
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
    _loadLoans();
  }

  Future<void> _loadLoans() async {
    setState(() => _isLoading = true);
    
    final repository = ref.read(loanRepositoryProvider);
    final result = await repository.getMyLoans();
    
    result.fold(
      (failure) {
        // Show error
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(failure.message)),
        );
      },
      (loans) {
        setState(() {
          _loans = loans;
          _isLoading = false;
        });
      },
    );
  }

  Future<void> _renewLoan(String loanId) async {
    final repository = ref.read(loanRepositoryProvider);
    final result = await repository.renewLoan(loanId);
    
    result.fold(
      (failure) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(failure.message)),
        );
      },
      (_) {
        _loadLoans(); // Refresh
      },
    );
  }

  Future<void> _returnLoan(String loanId) async {
    final repository = ref.read(loanRepositoryProvider);
    final result = await repository.returnLoan(loanId);
    
    result.fold(
      (failure) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(failure.message)),
        );
      },
      (_) {
        _loadLoans(); // Refresh
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return PageTemplate(
      title: 'Meine Ausleihen',
      body: RefreshIndicator(
        onRefresh: _loadLoans,
        child: LoanList(
          loans: _loans,
          isLoading: _isLoading,
          onRenewTap: _renewLoan,
          onReturnTap: _returnLoan,
        ),
      ),
    );
  }
}
```

**Best Practices:**
- ✅ Pages sind **Smart Components** (StatefulWidget)
- ✅ Data Fetching in `initState()`
- ✅ Repository via `ref.read()`
- ✅ Error Handling mit SnackBar
- ✅ Delegation an Presentation Components (LoanList)

---

## Testing

**Widget Tests für Presentation Components:**
```dart
// test/features/lending/presentation/molecules/loan_list_item_test.dart
void main() {
  testWidgets('LoanListItem displays loan info', (tester) async {
    final loan = LoanDTO(
      id: '1',
      userId: 'user1',
      mediaBarcode: '123456',
      mediaTitle: 'Test Book',
      mediaAuthor: 'Test Author',
      dueDate: DateTime.now().add(const Duration(days: 14)),
      status: 'CHECKED_OUT',
      renewalCount: 0,
      maxRenewals: 2,
    );

    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          body: LoanListItem(loan: loan),
        ),
      ),
    );

    expect(find.text('Test Book'), findsOneWidget);
    expect(find.text('Test Author'), findsOneWidget);
  });
}
```

---

## Referenzen

- 📖 [Design System README](../../design_system/README.md)
- 📚 [Core Layer README](../../core/README.md)
- 🎯 [Backend: Lending Context](../../../backend/module-lending/README.md)
