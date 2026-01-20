# Feature: Catalog

**Context:** Supporting Domain - Mediensuche, -übersicht, -details

---

## Structure

```
catalog/
├── data/
│   ├── models/
│   │   └── MediaDTO.ts         # Medium-Daten
│   └── repositories/
│       └── CatalogRepository.ts
│
└── presentation/
    ├── atoms/
    │   └── AvailabilityBadge.tsx
    ├── molecules/
    │   └── MediaCard.tsx       # Medium-Karte
    ├── organisms/
    │   └── MediaGrid.tsx       # Grid aller Medien
    └── pages/
        ├── CatalogPage.tsx     # Medienübersicht
        └── MediaDetailPage.tsx # Detail-Seite
```

---

## Data Models

```typescript
// models/MediaDTO.ts
export interface MediaDTO {
  id: string;
  title: string;
  author: string;
  isbn?: string;
  category?: string;
  coverUrl?: string;
  totalCopies: number;
  availableCopies: number;
  description?: string;
}
```

---

## Repository

```typescript
// repositories/CatalogRepository.ts
export class CatalogRepository {
  async getMedia(): Promise<MediaDTO[]>;
  async getMediaById(id: string): Promise<MediaDTO>;
  async searchMedia(query: string): Promise<MediaDTO[]>;
}
```

---

## Components

**Atoms:** `AvailabilityBadge` - Zeigt Verfügbarkeit  
**Molecules:** `MediaCard` - Medium mit Cover, Titel, Autor, Status  
**Organisms:** `MediaGrid` - Grid/List aller Medien  
**Pages:** `CatalogPage`, `MediaDetailPage`

---

## References

- [Backend: Catalog Context](../../../../backend/module-catalog/README.md)
