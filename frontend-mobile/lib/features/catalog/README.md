# Feature: Catalog Context

**Bounded Context:** Catalog (Supporting Domain)  
**Zweck:** Mediensuche, -übersicht und -details

---

## Übersicht

Das **Catalog Feature** ermöglicht Nutzern die Suche und Ansicht von Medien im Bibliotheksbestand.

### User Stories

- ✅ Als Nutzer kann ich nach Medien suchen (Titel, Autor, ISBN)
- ✅ Als Nutzer kann ich Medien nach Kategorie filtern
- ✅ Als Nutzer kann ich Details zu einem Medium sehen
- ✅ Als Nutzer sehe ich die Verfügbarkeit eines Mediums
- ✅ Als Nutzer kann ich ein verfügbares Medium direkt ausleihen
- ✅ Als Nutzer kann ich ein verfügbares Medium reservieren

---

## Struktur

```
catalog/
├── data/
│   ├── models/
│   │   ├── media_dto.dart          # Medium (Buch, DVD, etc.)
│   │   └── media_copy_dto.dart     # Exemplar
│   └── repositories/
│       └── catalog_repository.dart
│
└── presentation/
    ├── atoms/
    │   ├── availability_badge.dart # Verfügbarkeits-Status
    │   └── category_chip.dart      # Kategorie-Tag
    ├── molecules/
    │   ├── media_card.dart         # Medium-Karte (Grid/List)
    │   ├── search_bar.dart         # Suchfeld
    │   └── filter_panel.dart       # Filter-Sidebar
    ├── organisms/
    │   ├── media_grid.dart         # Grid aller Medien
    │   └── media_detail_header.dart # Detail-Header mit Cover
    └── pages/
        ├── catalog_page.dart       # Hauptseite: Medienübersicht
        ├── media_detail_page.dart  # Detail-Seite
        └── search_results_page.dart # Suchergebnisse
```

---

## Data Layer

### `media_dto.dart`

```dart
@freezed
class MediaDTO with _$MediaDTO {
  const factory MediaDTO({
    required String id,
    required String title,
    required String author,
    String? isbn,
    String? category,
    String? coverUrl,
    required int totalCopies,
    required int availableCopies,
    String? description,
  }) = _MediaDTO;

  factory MediaDTO.fromJson(Map<String, dynamic> json) => _$MediaDTOFromJson(json);
}
```

---

## Presentation Layer

### Pages

**`catalog_page.dart` (Smart Component):**
- Lädt Medien vom Repository
- Zeigt `MediaGrid` Organism
- Navigiert zu Detail-Seite bei Tap

**`media_detail_page.dart` (Smart Component):**
- Lädt Medium-Details by ID
- Zeigt Cover, Titel, Autor, Beschreibung, Verfügbarkeit
- Aktionen: Ausleihen, Reservieren

---

## Testing

Widget-Tests für:
- `MediaCard` - Rendert Titel, Autor, Status
- `AvailabilityBadge` - Farben korrekt
- `MediaGrid` - Zeigt Liste/Grid

---

## Referenzen

- 📖 [Design System README](../../design_system/README.md)
- 🎯 [Backend: Catalog Context](../../../backend/module-catalog/README.md)
