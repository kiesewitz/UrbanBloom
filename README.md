# UrbanBloom

Bürger-Engagement-Plattform zur Förderung von urbanen Begrünungsaktionen

## Überblick

UrbanBloom ist eine Fullstack-Anwendung, die Bürger motiviert, aktiv an der Begrünung ihrer Stadt teilzunehmen. Das Projekt kombiniert eine mobile App für Bürger, eine Web-Admin-Oberfläche für die Stadtverwaltung und ein Backend-System basierend auf Domain-Driven Design.

## Komponenten

### Mobile App
Flutter-basierte mobile Anwendung für iOS und Android
- GPS/QR-basierte Erfassung von Begrünungsaktionen
- Offline-Funktionalität mit Synchronisation
- Gamification (Punkte, Badges, Ranglisten)
- Push-Benachrichtigungen für Erinnerungen

### Admin Web
Flutter Web-Anwendung für die Stadtverwaltung
- Übersicht über Bezirksfortschritte
- Vergleich zwischen Bezirken
- Challenge-Verwaltung
- Report-Generierung und CSV-Export

### Backend Server
Spring Boot REST API mit DDD-Architektur
- RESTful API für Mobile & Web Clients
- Event-basierte Domain-Kommunikation
- 9 Bounded Contexts (User, Action, Gamification, etc.)
- Sichere Authentifizierung und Autorisierung

## Repository-Struktur

```
UrbanBloom/
├── .github/                    # GitHub Issue & PR Templates
├── .vscode/                    # Globale VS Code-Konfiguration
├── mobile/                     # Flutter Mobile App
├── admin-web/                  # Flutter Web Admin
├── server/                     # Spring Boot Backend
├── shared-resources/
│   ├── documentation/         # DDD-Docs, User Stories
│   └── design-tokens/         # Gemeinsame Design-Tokens
├── docs/                       # Projekt-Dokumentation
└── *.code-workspace           # Multi-Root Workspaces
```

## Quick Start

### Workspace öffnen

Je nach Entwicklungsfokus einen der folgenden Workspaces öffnen:

```bash
# Alle Komponenten (für API-Änderungen, Überblick)
code urbanbloom-full.code-workspace

# Nur Mobile App (fokussierte Flutter-Entwicklung)
code urbanbloom-mobile.code-workspace

# Nur Admin Web (fokussierte Web-Entwicklung)
code urbanbloom-web.code-workspace

# Nur Backend (fokussierte Server-Entwicklung)
code urbanbloom-server.code-workspace
```

### VS Code Tasks

Nach dem Öffnen eines Workspaces stehen vorkonfigurierte Tasks zur Verfügung:

**Ausführen**: `Ctrl+Shift+P` → "Tasks: Run Task"

#### Mobile Tasks
- `mobile: pub get` - Dependencies installieren
- `mobile: run` - App im Emulator/Simulator starten
- `mobile: test` - Tests ausführen

#### Web Tasks
- `web: pub get` - Dependencies installieren
- `web: run` - Dev-Server starten (Chrome)
- `web: build` - Production Build erstellen

#### Server Tasks
- `server: mvn clean install` - Build mit Tests
- `server: mvn verify` - Alle Tests und Checks
- `server: run` - Server starten

## Dokumentation

### Projekt-Dokumentation
- **[docs/project-overview.md](docs/project-overview.md)**: Ausführliche Projekt-Übersicht, Workspace-Guide, Entwicklungsworkflow

### DDD-Dokumentation
- **[Domain Model](shared-resources/documentation/domain-model-description-urbanbloom.md)**: Detaillierte Beschreibung aller Bounded Contexts
- **[DDD Glossar](shared-resources/documentation/urban_bloom_ddd_glossar.md)**: Begriffsdefinitionen (Entities, VOs, Services)
- **[Domains](shared-resources/documentation/urban_bloom_domains.md)**: Domain-Übersicht mit Use Cases
- **[UML Models](shared-resources/documentation/urban_bloom_uml_model_f-2.md)**: PlantUML-Diagramme

### User Stories
- **[User Stories mit Domains](shared-resources/documentation/urban_bloom_user_stories_with_domains.md)**: Priorisierte Stories mit MoSCoW

## Domain-Driven Design

UrbanBloom folgt DDD-Prinzipien mit 9 Bounded Contexts:

1. **User / Identity** - Registrierung, Authentifizierung
2. **Action / Observation** - Begrünungsaktionen erfassen und validieren
3. **Plant Catalog** - Pflanzenkatalog verwalten
4. **Location / District** - Standorte und Bezirke
5. **Gamification** - Punkte, Badges, Leaderboards
6. **Challenge** - Challenges und Kampagnen
7. **Notification / Reminder** - Benachrichtigungen
8. **Admin / Analytics** - Reports und Auswertungen
9. **Sync / Offline** - Offline-Synchronisation

Kommunikation zwischen Domains erfolgt über **Domain Events**, nicht über direkte Referenzen.

## GitHub Templates

### Issues erstellen

Das Projekt verwendet GitHub Issue Forms (YAML):

- **User Story**: Neue Features aus Nutzersicht
- **Bug Report**: Fehlerberichte mit Reproduktionsschritten
- **Enhancement**: Verbesserungsvorschläge
- **Refactoring**: Technische Verbesserungen

### Pull Requests

Drei komponentenspezifische PR-Templates:
- `mobile_pr_template.md` - Mobile App PRs
- `web_pr_template.md` - Admin Web PRs
- `server_pr_template.md` - Backend PRs

Jedes Template enthält relevante Checklisten (Tests, Breaking Changes, Performance, etc.)

## Technologie-Stack

| Komponente | Technologie |
|------------|-------------|
| Mobile App | Flutter (Dart) |
| Admin Web | Flutter Web (Dart) |
| Backend | Spring Boot (Java) |
| Build Tool (Server) | Maven |
| API | RESTful |
| Versionskontrolle | Git |
| Mono-Repo | Multi-Root Workspaces |

## Entwickler-Tipps

### Warum Multi-Root Workspaces?

**Vorteile**:
- **Performance**: Nur relevante Dateien im Index
- **Fokus**: Keine Ablenkung durch andere Komponenten
- **Extensions**: Nur passende Extensions aktiv (z.B. Flutter vs. Java)
- **Flexibilität**: Einfacher Wechsel zwischen Focused und Full Workspace

### Extension-Empfehlungen

Die `.vscode/extensions.json` enthält empfohlene Extensions:
- Dart & Flutter (Mobile/Web)
- Java & Spring Boot (Server)
- GitHub Actions
- GitLens
- YAML & JSON Support

### Tasks anpassen

Tasks können in `.vscode/tasks.json` angepasst werden. Jeder Task hat das korrekte `cwd` (Working Directory) gesetzt.

## Lizenz

(Lizenz hier einfügen)

## Kontakt

Bei Fragen oder Problemen:
- GitHub Issues für Bug Reports und Feature Requests
- Siehe [docs/project-overview.md](docs/project-overview.md) für Details
