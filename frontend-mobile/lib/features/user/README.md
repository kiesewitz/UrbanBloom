# Feature: User Context

**Bounded Context:** User (Generic Domain)  
**Zweck:** Benutzerprofil und Einstellungen

---

## Übersicht

Das **User Feature** verwaltet das Benutzerprofil und zeigt Statistiken.

### User Stories

- ✅ Als Nutzer kann ich mein Profil sehen (Name, E-Mail, Nutzergruppe)
- ✅ Als Nutzer sehe ich meine Ausleih-Statistiken
- ✅ Als Nutzer sehe ich mein Borrowing Limit
- ✅ Als Nutzer kann ich meine Einstellungen ändern

---

## Struktur

```
user/
├── data/
│   ├── models/
│   │   └── user_dto.dart           # User-Profil
│   └── repositories/
│       └── user_repository.dart
│
└── presentation/
    ├── atoms/
    │   ├── user_avatar.dart        # Avatar
    │   └── stat_tile.dart          # Statistik-Kachel
    ├── molecules/
    │   ├── profile_header.dart     # Avatar + Name + E-Mail
    │   └── stats_row.dart          # Statistik-Zeile
    ├── organisms/
    │   └── profile_card.dart       # Profil-Karte mit Stats
    └── pages/
        ├── profile_page.dart       # Hauptseite: Profil
        └── settings_page.dart      # Einstellungen
```

---

## Data Layer

### `user_dto.dart`

```dart
@freezed
class UserDTO with _$UserDTO {
  const factory UserDTO({
    required String id,
    required String email,
    required String firstName,
    required String lastName,
    required String userGroup, // STUDENT, TEACHER, LIBRARIAN
    required int borrowingLimit,
    int? activeLoansCount,
    int? overdueLoansCount,
  }) = _UserDTO;

  factory UserDTO.fromJson(Map<String, dynamic> json) => _$UserDTOFromJson(json);
}
```

---

## Presentation Layer

### Pages

**`profile_page.dart` (Smart Component):**
- Lädt User-Profil von `/users/me`
- Zeigt `ProfileCard` mit Stats
- Navigiert zu Einstellungen

---

## Referenzen

- 📖 [Design System README](../../design_system/README.md)
- 🎯 [Backend: User Context](../../../backend/module-user/README.md)
