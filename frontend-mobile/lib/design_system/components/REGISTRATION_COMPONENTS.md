# Registration Screen Components

Diese Dokumentation beschreibt die Flutter-Komponenten, die basierend auf dem HTML/CSS-Prototyp für den Registrierungsbildschirm erstellt wurden.

## Atomic Design Struktur

### Atome (Atoms)
Kleinste UI-Bausteine, die nicht weiter zerlegt werden können:

#### 1. `AppIconButton`
- **Pfad**: `lib/design_system/components/atoms/app_icon_button.dart`
- **Beschreibung**: Icon-Button für Aktionen wie Zurück, Hilfe, Sichtbarkeitsumschaltung
- **Props**:
  - `icon`: IconData - Das anzuzeigende Icon
  - `onPressed`: VoidCallback - Callback bei Klick
  - `semanticLabel`: String? - Label für Barrierefreiheit
  - `size`: double - Größe des Button-Containers (Standard: 40)
  - `iconSize`: double - Größe des Icons (Standard: 24)
  - `color`: Color? - Icon-Farbe

#### 2. `AppTextField`
- **Pfad**: `lib/design_system/components/atoms/app_text_field.dart`
- **Beschreibung**: Basis-Eingabefeld mit konfigurierbaren Eigenschaften
- **Props**:
  - `controller`: TextEditingController
  - `hintText`: String? - Platzhaltertext
  - `labelText`: String? - Label über dem Feld
  - `obscureText`: bool - Für Passwörter (Standard: false)
  - `keyboardType`: TextInputType? - Tastaturtyp
  - `prefixIcon`: Widget? - Icon am Anfang
  - `suffixIcon`: Widget? - Icon/Widget am Ende
  - `validator`: Function? - Validierungsfunktion
  - `onChanged`: Function? - Callback bei Änderung

#### 3. `AppPrimaryButton`
- **Pfad**: `lib/design_system/components/atoms/app_primary_button.dart`
- **Beschreibung**: Primärer Call-to-Action Button
- **Props**:
  - `onPressed`: VoidCallback
  - `label`: String - Button-Text
  - `icon`: IconData? - Optionales Icon am Ende
  - `isLoading`: bool - Loading-Status (Standard: false)
  - `fullWidth`: bool - Volle Breite (Standard: true)

#### 4. `AppInfoBadge`
- **Pfad**: `lib/design_system/components/atoms/app_info_badge.dart`
- **Beschreibung**: Info-Badge für Hinweise und Warnungen
- **Props**:
  - `text`: String - Anzuzeigender Text
  - `icon`: IconData - Icon (Standard: info_outline)
  - `backgroundColor`: Color? - Hintergrundfarbe
  - `foregroundColor`: Color? - Text- und Icon-Farbe

#### 5. `AppLogoContainer`
- **Pfad**: `lib/design_system/components/atoms/app_logo_container.dart`
- **Beschreibung**: Logo-Container mit Icon und Schatten
- **Props**:
  - `icon`: IconData - Anzuzeigendes Icon
  - `size`: double - Container-Größe (Standard: 64)
  - `iconSize`: double - Icon-Größe (Standard: 32)
  - `backgroundColor`: Color? - Hintergrundfarbe
  - `iconColor`: Color - Icon-Farbe (Standard: weiß)

---

### Moleküle (Molecules)
Einfache Kombinationen von Atomen:

#### 1. `LabeledInputField`
- **Pfad**: `lib/design_system/components/molecules/labeled_input_field.dart`
- **Beschreibung**: Eingabefeld mit Label
- **Kombiniert**: Label-Text + AppTextField
- **Props**:
  - `label`: String - Label-Text
  - `controller`: TextEditingController
  - `hintText`: String?
  - `obscureText`: bool
  - `keyboardType`: TextInputType?
  - `prefixIcon`: IconData?
  - `suffixIcon`: Widget?
  - `validator`: Function?
  - `onChanged`: Function?

#### 2. `PasswordInputField`
- **Pfad**: `lib/design_system/components/molecules/password_input_field.dart`
- **Beschreibung**: Passwort-Eingabefeld mit Sichtbarkeitsumschaltung
- **Kombiniert**: Label + AppTextField + Visibility Toggle Button
- **State**: Verwaltet Sichtbarkeitsstatus
- **Props**:
  - `label`: String
  - `controller`: TextEditingController
  - `hintText`: String?
  - `validator`: Function?
  - `onChanged`: Function?

#### 3. `AuthHeroSection`
- **Pfad**: `lib/design_system/components/molecules/auth_hero_section.dart`
- **Beschreibung**: Hero-Bereich mit Logo, Titel und Beschreibung
- **Kombiniert**: AppLogoContainer + Titel + Beschreibungstext
- **Props**:
  - `title`: String - Haupttitel
  - `description`: String - Beschreibungstext
  - `icon`: IconData - Icon im Logo (Standard: school)

---

### Organismen (Organisms)
Komplexe UI-Komponenten:

#### 1. `AuthAppBar`
- **Pfad**: `lib/design_system/components/organisms/auth_app_bar.dart`
- **Beschreibung**: App-Bar für Authentifizierungsseiten
- **Kombiniert**: Zurück-Button + Hilfe-Button
- **Implementiert**: PreferredSizeWidget
- **Props**:
  - `onBackPressed`: VoidCallback? - Zurück-Aktion
  - `onHelpPressed`: VoidCallback? - Hilfe-Aktion
  - `showBackButton`: bool (Standard: true)
  - `showHelpButton`: bool (Standard: true)

#### 2. `RegistrationForm`
- **Pfad**: `lib/design_system/components/organisms/registration_form.dart`
- **Beschreibung**: Komplettes Registrierungsformular
- **Kombiniert**: 
  - LabeledInputField (E-Mail)
  - 2× PasswordInputField (Passwort, Bestätigung)
  - AppPrimaryButton (Absenden)
- **State**: Verwaltet Form-State und Validierung
- **Validierung**:
  - E-Mail: Muss mit @schule.de enden
  - Passwort: Mindestens 8 Zeichen
  - Bestätigung: Muss mit Passwort übereinstimmen
- **Props**:
  - `onSubmit`: Function({email, password}) - Submit-Callback
  - `isLoading`: bool - Loading-Status (Standard: false)

---

### Template/Screen

#### `RegistrationScreen`
- **Pfad**: `lib/features/user/presentation/pages/registration_screen.dart`
- **Beschreibung**: Vollständiger Registrierungsbildschirm
- **Kombiniert**:
  - AuthAppBar
  - AuthHeroSection
  - AppInfoBadge
  - RegistrationForm
  - Login-Link
- **Features**:
  - Responsive Layout mit ScrollView
  - Loading-State-Management
  - Error-Handling mit SnackBars
  - Hilfe-Dialog
  - Navigation zum Login

---

## Verwendung

### Beispiel: Registration Screen verwenden

```dart
import 'package:flutter/material.dart';
import 'package:school_library_mobile/features/user/presentation/pages/registration_screen.dart';

// Navigation zur Registrierung
Navigator.push(
  context,
  MaterialPageRoute(
    builder: (context) => const RegistrationScreen(),
  ),
);
```

### Beispiel: Einzelne Komponenten verwenden

```dart
import 'package:school_library_mobile/design_system/components/components.dart';

// Atom
AppPrimaryButton(
  onPressed: () => print('Clicked'),
  label: 'Klick mich',
  icon: Icons.arrow_forward,
)

// Molekül
PasswordInputField(
  label: 'Passwort',
  controller: _passwordController,
  validator: (value) => value?.isEmpty ?? true ? 'Required' : null,
)

// Organismus
RegistrationForm(
  onSubmit: ({required email, required password}) {
    print('Registering: $email');
  },
)
```

---

## Design Tokens

Die Komponenten verwenden folgende Design Tokens aus dem HTML-Prototyp:

### Farben
- **Primary**: `#0F63A8`
- **Background Light**: `#f8f8f5`
- **Background Dark**: `#23220f`
- **Text Light**: `#181811`
- **Text Dark**: `#ffffff`
- **Muted Light**: `#8c8b5f`
- **Muted Dark**: `#a1a18f`
- **Border Light**: `#e6e6db`
- **Border Dark**: `#4a4a35`

### Abstände
- Kleine Abstände: 8px
- Mittlere Abstände: 16-24px
- Große Abstände: 32-48px

### Border Radius
- Input-Felder: 12px (entspricht 1rem im HTML)
- Buttons: 999px (vollständig rund)
- Logo-Container: 16px (entspricht 2rem im HTML)

---

## Theme-Unterstützung

Alle Komponenten unterstützen Light- und Dark-Mode:

```dart
// Die Theme-Einstellung wird automatisch erkannt
final isDark = Theme.of(context).brightness == Brightness.dark;

// Farben passen sich automatisch an
backgroundColor: isDark 
    ? const Color(0xFF23220f)  // Dark
    : const Color(0xFFf8f8f5), // Light
```

---

## Barrierefreiheit

- Alle interaktiven Elemente haben Semantics-Labels
- Formularfelder haben aussagekräftige Labels
- Fokus-Indikatoren sind sichtbar
- Kontrastverhältnisse erfüllen WCAG-Standards

---

## Best Practices

1. **Wiederverwendung**: Nutzen Sie die Atome und Moleküle für konsistente UI
2. **Validierung**: Nutzen Sie die eingebauten Validator-Funktionen
3. **Theme**: Verwenden Sie die Theme-Farben statt hartcodierte Werte wo möglich
4. **State-Management**: Form-State wird lokal verwaltet, Daten werden nach oben gereicht
5. **Error-Handling**: Fehler werden über SnackBars kommuniziert

---

## Weitere Entwicklung

Diese Komponenten können erweitert werden für:
- Login-Screen
- Passwort-Vergessen-Screen
- Profil-Bearbeitung
- Andere Formulare in der App
