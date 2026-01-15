---
name: "Admin_Web_Frontend"
description: "Next.js/React Component Driven Design für UrbanBloom Admin Web"
applyTo: "admin-web/src/**/*.ts,admin-web/src/**/*.tsx"
---

## Scope

Diese Instructions gelten für **alles TypeScript/TSX im Ordner `admin-web/src`**.
Ziel ist ein sauberes, wartbares **Admin-Web** auf Basis von:

- Next.js App Router + React (Functional Components)
- Component Driven Design (Atoms → Molecules → Organisms → Templates → Pages)
- Backend-/API-First & DDD-Domainstruktur von UrbanBloom

---

## Architektur & Projektstruktur

- Schneide Features **fachlich**, nicht technisch:
  - `adminAnalytics`, `actions`, `challenges`, `locations`, `plants`, `gamification`, `notifications`, `users` etc.
- Verwende folgende Grundstruktur unter `admin-web/src`:

  - `app/` – Next.js Routen (Pages, Layouts)
  - `design-system/` – globale UI-Bausteine
    - `atoms/`
    - `molecules/`
    - `organisms/`
    - `templates/`
  - `features/<feature>/`
    - `data/` (API-Access, Repositories, DTO-Mapping)
    - `presentation/`
      - `pages/` (Smart Components)
      - `organisms/`
      - `molecules/`
      - `atoms/`

- Keine neuen Top-Level-Ordner wie `components/`, `services/`, `models/` auf Root-Ebene einführen – nutze `design-system/` und `features/`.

---

## Atomic Design & Component Driven Design

### Atoms

- Kleinste, generische UI-Elemente:
  - `Button`, `TextField`, `Select`, `Icon`, `Badge`, `Card`, `Spinner`, `Tag`
- Speicherort: `src/design-system/atoms/`
- Eigenschaften:
  - Keine API-Calls
  - Kein globaler State
  - Wiederverwendbar in allen Features

### Molecules

- Kombination weniger Atoms mit kleiner Logik:
  - `SearchField` (Input + Button), `StatCard`, `LabeledValue`, `UserAvatarWithName`
- Speicherort:
  - Global: `src/design-system/molecules/`
  - Feature-spezifisch: `src/features/<feature>/presentation/molecules/`
- Eigenschaften:
  - Kein API-Call
  - Minimaler UI-State (z. B. „ist Dialog offen“)

### Organisms

- Größere UI-Sektionen:
  - `AppSidebar`, `AppHeader`, `DataTable`, `FilterBar`, `KpiSection`, `ActionsTable`
- Speicherort:
  - Global: `src/design-system/organisms/`
  - Feature-spezifisch: `src/features/<feature>/presentation/organisms/`
- Eigenschaften:
  - Komponieren Atoms + Molecules
  - Kein direkter API-Call
  - Orchestrieren UI-Layout für einen Bereich

### Templates

- Layout-Skelette für Seiten:
  - `AdminPageTemplate` mit Slots für `header`, `toolbar`, `content`
- Speicherort: `src/design-system/templates/`
- Eigenschaften:
  - Kein Daten-Fetching
  - Platzhalter für Inhalte („Widgets“)

### Pages

- Finale Seiten mit echten Daten:
  - Next.js-Routen unter `src/app/(admin)/**/page.tsx`
  - „Smart Components“ unter `src/features/<feature>/presentation/pages/`
- Verantwortung:
  - Daten laden (API-Calls)
  - State verwalten
  - Fehler-Handling
  - Zusammensetzen von Templates + Organisms + Molecules + Atoms

---

## Smart vs. Presentation Components

### Smart Components (Container)

- Typisch in `src/features/<feature>/presentation/pages/` oder als Route-Komponente in `src/app/`.
- Aufgaben:
  - API-Calls über Daten-/Repository-Layer
  - lokalen oder globalen State verwalten
  - Error- und Loading-States steuern
  - Child-Komponenten mit Props versorgen
- Styling:
  - Minimal, Layout vor allem delegieren (Template/Organisms)

### Presentation Components

- Typisch unter `design-system/*` oder `features/*/presentation/{organisms,molecules,atoms}`.
- Aufgaben:
  - Daten via Props anzeigen
  - Events per Callback emittieren (`onClick`, `onSelect`, `onFilterChange`, …)
- Strikte Regeln:
  - **Keine API-Calls**
  - **Kein Domain-/Business-Logic-Duplikat** (nur UI-Logik)
  - Styling und Layout vollständig in der Komponente
  - Props als klare, typsichere Schnittstelle gestalten

---

## API-First & Domain-Anbindung

- Verwende konsequent den **API-First Ansatz**:
  - Die OpenAPI-Spec im Monorepo ist die Wahrheit.
  - Erfinde keine neuen Felder oder Endpoints im Frontend.
- Setze nach Möglichkeit einen **generierten API-Client** ein:
  - Daten-Zugriff in `src/features/<feature>/data/` kapseln.
  - Pages/Smart-Components rufen nur Repository-/Service-Funktionen auf.
- Dupliziere **keine Geschäftslogik** im Frontend:
  - Validierungen, Regeln, Berechnungen bleiben im Backend.
  - Frontend führt nur UI-Logik aus (z. B. Sortierung/Filterung, Paging).

---

## State Management & Props

- Bevorzuge lokalen State (`useState`, `useReducer`) nahe an der Komponente.
- Wenn Props mehr als ~2 Ebenen tief gereicht werden:
  - State nach oben ziehen oder
  - geeigneten globalen State/Context einsetzen (z. B. Auth-User, Theme).
- Anti-Pattern: „Prop Drilling“ über viele Ebenen – stattdessen Feature- oder App-weit geteilten State nutzen.
- Props-Design:
  - Sinnvolle Namen (`actions`, `filters`, `selectedDistrictId`) statt `data` oder `item`.
  - Callbacks mit `on...`-Präfix (`onActionClick`, `onFilterChange`).
  - Required/Optional Props klar trennen, Default-Werte nutzen wo sinnvoll.

---

## Code Style & TypeScript

- Nur **funktionale Komponenten** und Hooks verwenden.
- Für Props und DTOs:
  - `interface` statt `type`, wenn möglich.
- Typisierung:
  - Keine `any`-Types verwenden.
  - Public-Funktionen mit explizitem Rückgabewert typisieren.
- Naming:
  - Komponentennamen in PascalCase (`AdminDashboardPage`, `ActionFilterBar`).
  - Hooks mit `use`-Präfix (`useActions`, `useChallengeFilters`).
  - Domain-Begriffe aus UrbanBloom (Action, Plant, Challenge, Location, Gamification, AdminAnalytics).

---

## Testing (Kurzrichtlinie)

- Ziel: Präsentations-Komponenten sollen einfach zu testen sein.
- Test-Typen:
  - Unit-Tests für Atoms, Molecules, Organisms mit Mock-Props.
  - Integrationstests für Smart Components/Pages, die mehrere Komponenten kombinieren.
- Tests prüfen:
  - Verhalten aus Nutzersicht (Rendern, Interaktionen, sichtbare Änderungen).
  - Kein Testen von Implementation Details, soweit möglich.

---

## Nutzung von KI / Tools

Wenn du KI (z. B. Copilot Chat) für Admin-Web nutzt:

- Halte dich an diese Instructions für alle Dateien, die unter `admin-web/src` fallen.
- Bei Code-Analyse:
  - Nutze `#tool:search` um relevante Dateien zu finden.
  - Bevorzuge lokale Projektquellen (DDD-Dokumente, API-Spec, vorhandene Features).
- Bei neuen Komponenten:
  - Erst entscheiden, auf welcher Ebene (Atom/Molecule/Organism/Template/Page) sie hingehört.
  - Dann Code generieren lassen, der in diese Struktur passt.
