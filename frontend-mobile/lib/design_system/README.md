# Design System

**Zweck:** Zentrale UI-Komponenten und Design Tokens für konsistentes Design

---

## Übersicht

Das **Design System** definiert die visuellen Bausteine der App und folgt dem **Atomic Design Pattern**. Alle Komponenten sind wiederverwendbar, getestet und im Widgetbook dokumentiert.

### Prinzipien

- ✅ **Design Tokens first:** Alle Farben, Spacing, Typography zentral definiert
- ✅ **Atomic Design:** Hierarchische Komposition (Atoms → Molecules → Organisms → Templates)
- ✅ **Presentation Components:** Stateless, Props-driven, Event-emitting
- ✅ **Widgetbook-dokumentiert:** Jede Komponente hat einen Widgetbook-Eintrag
- ✅ **Getestet:** Widget-Tests für alle Komponenten

---

## Struktur

```
design_system/
├── design_tokens.dart          # Zentrale Design-Konstanten
└── components/
    ├── atoms/                  # Basis-Komponenten
    ├── molecules/              # Zusammengesetzte Komponenten
    ├── organisms/              # Komplexe UI-Blöcke
    └── templates/              # Layout-Templates
```

---

## 📦 `design_tokens.dart`

**Zweck:** Zentrale Definition aller Design-Konstanten

**Inhalt:**
- **Colors:** Primary, Secondary, Error, Success, Background, Surface, Text
- **Spacing:** xs, sm, md, lg, xl (4dp Grid)
- **Typography:** Headline, Body, Label (TextStyle)
- **Borders:** Radius, Width
- **Shadows:** Elevation Levels

**Beispiel:**
```dart
class DesignTokens {
  // Colors
  static const Color primaryColor = Color(0xFF1976D2);
  static const Color secondaryColor = Color(0xFFFFA726);
  static const Color errorColor = Color(0xFFD32F2F);
  static const Color successColor = Color(0xFF388E3C);
  
  static const Color backgroundColor = Color(0xFFF5F5F5);
  static const Color surfaceColor = Color(0xFFFFFFFF);
  
  static const Color textPrimary = Color(0xFF212121);
  static const Color textSecondary = Color(0xFF757575);
  
  // Spacing (4dp grid)
  static const double spacingXs = 4.0;
  static const double spacingSm = 8.0;
  static const double spacingMd = 16.0;
  static const double spacingLg = 24.0;
  static const double spacingXl = 32.0;
  
  // Typography
  static const TextStyle headlineStyle = TextStyle(
    fontSize: 24,
    fontWeight: FontWeight.bold,
    color: textPrimary,
  );
  
  static const TextStyle bodyStyle = TextStyle(
    fontSize: 16,
    fontWeight: FontWeight.normal,
    color: textPrimary,
  );
  
  // Borders
  static const double borderRadiusSm = 4.0;
  static const double borderRadiusMd = 8.0;
  static const double borderRadiusLg = 16.0;
  
  // Shadows
  static const BoxShadow shadowSm = BoxShadow(
    color: Color(0x1F000000),
    blurRadius: 4,
    offset: Offset(0, 2),
  );
}
```

**Best Practices:**
- ✅ Alle Komponenten konsumieren diese Tokens
- ✅ Keine hardcoded Farben/Werte in Komponenten
- ✅ Bei Änderungen zentral anpassen

---

## 📦 `components/atoms/`

**Zweck:** Basis-Komponenten (kleinstmögliche UI-Elemente)

**Was sind Atoms?**
- Kleinste UI-Bausteine
- Nicht weiter teilbar
- Wiederverwendbar
- Stateless

**Beispiele:**
- `PrimaryButton` - Standard-Button
- `SecondaryButton` - Sekundärer Button
- `StatusBadge` - Status-Indikator (Available, Checked Out, etc.)
- `InputField` - Textfeld
- `IconButton` - Icon-Button
- `Avatar` - Benutzer-Avatar
- `Chip` - Tag/Chip

**Beispiel-Implementierung:**
```dart
// atoms/primary_button.dart
class PrimaryButton extends StatelessWidget {
  const PrimaryButton({
    super.key,
    required this.text,
    required this.onPressed,
    this.isLoading = false,
    this.isDisabled = false,
  });

  final String text;
  final VoidCallback? onPressed;
  final bool isLoading;
  final bool isDisabled;

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: isDisabled || isLoading ? null : onPressed,
      style: ElevatedButton.styleFrom(
        backgroundColor: DesignTokens.primaryColor,
        foregroundColor: Colors.white,
        padding: const EdgeInsets.symmetric(
          horizontal: DesignTokens.spacingLg,
          vertical: DesignTokens.spacingMd,
        ),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(DesignTokens.borderRadiusMd),
        ),
      ),
      child: isLoading
          ? const SizedBox(
              width: 20,
              height: 20,
              child: CircularProgressIndicator(
                strokeWidth: 2,
                valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
              ),
            )
          : Text(text, style: const TextStyle(fontSize: 16)),
    );
  }
}
```

**Best Practices:**
- ✅ Props über Constructor (required vs optional)
- ✅ Events über Callbacks (`onPressed`, `onChanged`)
- ✅ Verwende Design Tokens
- ✅ Dokumentiere Props mit Kommentaren
- ✅ Widget-Tests schreiben
- ✅ Widgetbook-Eintrag erstellen

---

## 📦 `components/molecules/`

**Zweck:** Zusammengesetzte Komponenten aus Atoms

**Was sind Molecules?**
- Kombinieren mehrere Atoms
- Haben eigenen Zweck/Bedeutung
- Wiederverwendbar
- Stateless

**Beispiele:**
- `SearchBar` - Suchfeld + Icon + Clear-Button
- `MediaCard` - Avatar + Title + Subtitle + Status Badge
- `FormField` - Label + InputField + Error Message
- `LoanListItem` - Title + Subtitle + Due Date + Return Button
- `FilterChipGroup` - Liste von Chips mit Auswahl

**Beispiel-Implementierung:**
```dart
// molecules/media_card.dart
class MediaCard extends StatelessWidget {
  const MediaCard({
    super.key,
    required this.title,
    required this.author,
    required this.status,
    this.coverUrl,
    this.onTap,
  });

  final String title;
  final String author;
  final String status;
  final String? coverUrl;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(DesignTokens.borderRadiusMd),
      ),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(DesignTokens.borderRadiusMd),
        child: Padding(
          padding: const EdgeInsets.all(DesignTokens.spacingMd),
          child: Row(
            children: [
              // Cover Image (Atom)
              ClipRRect(
                borderRadius: BorderRadius.circular(DesignTokens.borderRadiusSm),
                child: coverUrl != null
                    ? Image.network(coverUrl!, width: 60, height: 80, fit: BoxFit.cover)
                    : Container(
                        width: 60,
                        height: 80,
                        color: DesignTokens.backgroundColor,
                        child: const Icon(Icons.book),
                      ),
              ),
              const SizedBox(width: DesignTokens.spacingMd),
              // Content
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(title, style: DesignTokens.bodyStyle),
                    const SizedBox(height: DesignTokens.spacingXs),
                    Text(author, style: DesignTokens.bodyStyle.copyWith(color: DesignTokens.textSecondary)),
                    const SizedBox(height: DesignTokens.spacingSm),
                    StatusBadge(status: status), // Atom
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
```

---

## 📦 `components/organisms/`

**Zweck:** Komplexe UI-Blöcke aus Molecules und Atoms

**Was sind Organisms?**
- Kombinieren Molecules und Atoms
- Bilden eigenständige Abschnitte
- Wiederverwendbar über Features hinweg
- Meist Stateless (außer lokale UI-State)

**Beispiele:**
- `LoanList` - Liste aller Ausleihen (LoanListItem + Empty State + Loading)
- `MediaGrid` - Grid von MediaCards mit Pagination
- `FilterPanel` - Sidebar mit FilterChipGroups + Apply/Reset Buttons
- `UserProfileCard` - Avatar + User Info + Borrowing Stats
- `NavigationBar` - Bottom Navigation mit Icons + Labels

**Beispiel-Implementierung:**
```dart
// organisms/loan_list.dart
class LoanList extends StatelessWidget {
  const LoanList({
    super.key,
    required this.loans,
    this.isLoading = false,
    this.onReturnTap,
    this.onRenewTap,
  });

  final List<LoanDTO> loans;
  final bool isLoading;
  final void Function(String loanId)? onReturnTap;
  final void Function(String loanId)? onRenewTap;

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (loans.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.book_outlined, size: 64, color: DesignTokens.textSecondary),
            const SizedBox(height: DesignTokens.spacingMd),
            Text('Keine Ausleihen', style: DesignTokens.bodyStyle),
          ],
        ),
      );
    }

    return ListView.separated(
      itemCount: loans.length,
      separatorBuilder: (_, __) => const SizedBox(height: DesignTokens.spacingSm),
      itemBuilder: (context, index) {
        final loan = loans[index];
        return LoanListItem( // Molecule
          title: loan.mediaTitle,
          dueDate: loan.dueDate,
          status: loan.status,
          onReturnTap: () => onReturnTap?.call(loan.id),
          onRenewTap: () => onRenewTap?.call(loan.id),
        );
      },
    );
  }
}
```

---

## 📦 `components/templates/`

**Zweck:** Layout-Templates für Seiten

**Was sind Templates?**
- Definieren Layout-Struktur
- Slots für Content
- Wiederverwendbar
- Stateless

**Beispiele:**
- `PageTemplate` - AppBar + Body + BottomNav
- `ListPageTemplate` - AppBar + SearchBar + List + FAB
- `DetailPageTemplate` - AppBar + Hero Image + Tabs + Content
- `FormPageTemplate` - AppBar + ScrollView + Form + Submit Button

**Beispiel-Implementierung:**
```dart
// templates/page_template.dart
class PageTemplate extends StatelessWidget {
  const PageTemplate({
    super.key,
    required this.title,
    required this.body,
    this.actions,
    this.floatingActionButton,
    this.showBottomNav = true,
  });

  final String title;
  final Widget body;
  final List<Widget>? actions;
  final Widget? floatingActionButton;
  final bool showBottomNav;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(title),
        actions: actions,
        backgroundColor: DesignTokens.primaryColor,
        foregroundColor: Colors.white,
      ),
      body: body,
      floatingActionButton: floatingActionButton,
      bottomNavigationBar: showBottomNav ? const AppBottomNav() : null,
    );
  }
}
```

---

## Testing

**Widget Tests für jede Komponente:**
```dart
// test/design_system/components/atoms/primary_button_test.dart
void main() {
  testWidgets('PrimaryButton renders text', (tester) async {
    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          body: PrimaryButton(
            text: 'Click Me',
            onPressed: () {},
          ),
        ),
      ),
    );

    expect(find.text('Click Me'), findsOneWidget);
  });

  testWidgets('PrimaryButton triggers onPressed', (tester) async {
    bool pressed = false;

    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          body: PrimaryButton(
            text: 'Click Me',
            onPressed: () => pressed = true,
          ),
        ),
      ),
    );

    await tester.tap(find.byType(PrimaryButton));
    expect(pressed, isTrue);
  });
}
```

---

## Widgetbook Integration

Jede Komponente sollte im Widgetbook dokumentiert sein:

```dart
// widgetbook/atoms.dart
@UseCase(name: 'Primary Button', type: PrimaryButton)
Widget primaryButtonUseCase(BuildContext context) {
  return PrimaryButton(
    text: context.knobs.string(label: 'Text', initialValue: 'Click Me'),
    onPressed: () {},
    isLoading: context.knobs.boolean(label: 'Is Loading', initialValue: false),
    isDisabled: context.knobs.boolean(label: 'Is Disabled', initialValue: false),
  );
}
```

---

## Entwicklungsrichtlinien

1. **Design Tokens first:** Konsumiere immer `DesignTokens`, niemals hardcoded Werte
2. **Stateless Komponenten:** Presentation Components haben keinen State
3. **Props-driven:** Alle Werte über Constructor
4. **Events über Callbacks:** `onPressed`, `onChanged`, etc.
5. **Composition over Configuration:** Lieber mehrere kleine Komponenten als eine riesige mit vielen Flags
6. **Test first:** Widget-Test vor Implementierung
7. **Widgetbook:** Dokumentiere jede Komponente

---

## Referenzen

- 📖 [PROJECT_STRUCTURE.md](../../PROJECT_STRUCTURE.md)
- 🎨 [Atomic Design Methodology](https://bradfrost.com/blog/post/atomic-web-design/)
- 📚 [Flutter Widget Catalog](https://docs.flutter.dev/ui/widgets)
