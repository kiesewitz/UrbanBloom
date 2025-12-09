# UrbanBloom - Projekt-Übersicht

## Einführung

UrbanBloom ist eine Fullstack-Anwendung zur Förderung von Begrünungsaktionen in Städten. Das Projekt besteht aus drei Hauptkomponenten, die in einem Mono-Repository verwaltet werden.

## Projektstruktur

Das UrbanBloom Mono-Repository ist wie folgt strukturiert:

```
UrbanBloom/
├── .github/                    # GitHub-Konfiguration
│   ├── ISSUE_TEMPLATE/        # Issue Templates (YAML)
│   └── PULL_REQUEST_TEMPLATE/ # PR Templates
├── .vscode/                    # Globale VS Code-Konfiguration
│   ├── settings.json
│   ├── extensions.json
│   └── tasks.json
├── mobile/                     # Flutter Mobile App (iOS/Android)
├── admin-web/                  # Flutter Web Admin-Oberfläche
├── server/                     # Spring Boot Backend
├── shared-resources/           # Gemeinsame Ressourcen
│   ├── documentation/         # DDD-Dokumentation, User Stories
│   └── design-tokens/         # Design System
├── docs/                       # Projekt-Dokumentation
└── *.code-workspace           # Multi-Root Workspace-Dateien
```

## Komponenten-Übersicht

### Mobile App (`mobile/`)
- **Technologie**: Flutter
- **Zielplattformen**: iOS, Android
- **Zweck**: Bürger-App zum Erfassen von Begrünungsaktionen
- **Hauptfunktionen**:
  - GPS/QR-basierte Erfassung von Aktionen
  - Offline-Modus mit Synchronisation
  - Gamification (Punkte, Badges, Ranglisten)
  - Push-Benachrichtigungen

### Admin Web (`admin-web/`)
- **Technologie**: Flutter Web
- **Zielplattform**: Webbrowser (Desktop)
- **Zweck**: Admin-Oberfläche für Stadtverwaltung
- **Hauptfunktionen**:
  - Bezirksfortschritt einsehen
  - Bezirke vergleichen
  - Challenges erstellen und verwalten
  - Reports generieren und exportieren

### Backend Server (`server/`)
- **Technologie**: Spring Boot (Java)
- **Zweck**: REST API und Business Logic
- **Hauptfunktionen**:
  - RESTful API für Mobile & Web
  - Domain-Driven Design Architektur
  - Event-basierte Kommunikation zwischen Domains
  - Datenpersistenz

## Workspace-Guide

Das Projekt bietet vier verschiedene VS Code Workspace-Dateien für unterschiedliche Entwicklungsszenarien:

### 1. Full Workspace (`urbanbloom-full.code-workspace`)

**Verwendung**: Cross-Platform-Entwicklung, API-Änderungen

Öffnet alle Komponenten gleichzeitig:
- Root-Verzeichnis
- Mobile App
- Admin Web
- Backend Server

**Öffnen**:
```bash
code urbanbloom-full.code-workspace
```

**Wann verwenden**:
- Bei API-Änderungen, die Frontend und Backend betreffen
- Wenn an mehreren Komponenten gleichzeitig gearbeitet wird
- Für Überblick über das Gesamtprojekt

### 2. Mobile Workspace (`urbanbloom-mobile.code-workspace`)

**Verwendung**: Fokussierte Mobile App Entwicklung

Öffnet nur:
- Mobile App Ordner
- Shared Resources

**Öffnen**:
```bash
code urbanbloom-mobile.code-workspace
```

**Vorteile**:
- Bessere Performance durch weniger geöffnete Dateien
- Nur Flutter/Dart Extensions aktiv
- Fokussierte Entwicklungsumgebung

### 3. Web Workspace (`urbanbloom-web.code-workspace`)

**Verwendung**: Fokussierte Admin Web Entwicklung

Öffnet nur:
- Admin Web Ordner
- Shared Resources

**Öffnen**:
```bash
code urbanbloom-web.code-workspace
```

**Vorteile**:
- Optimiert für Flutter Web Entwicklung
- Browser-spezifische Debug-Konfigurationen

### 4. Server Workspace (`urbanbloom-server.code-workspace`)

**Verwendung**: Fokussierte Backend Entwicklung

Öffnet nur:
- Server Ordner
- Shared Resources

**Öffnen**:
```bash
code urbanbloom-server.code-workspace
```

**Vorteile**:
- Nur Java/Spring Boot Extensions aktiv
- Maven-spezifische Tasks verfügbar
- Optimiert für Backend-Entwicklung

## GitHub Templates

### Issue Templates

Das Projekt verwendet GitHub Issue Forms (YAML) für standardisierte Issue-Erstellung:

1. **User Story** (`01_user_story.yml`)
   - Für neue User Stories
   - Enthält: Domain-Zuordnung, Akzeptanzkriterien, Priorität (MoSCoW), Story Points

2. **Bug Report** (`02_bug_report.yml`)
   - Für Fehlerberichte
   - Enthält: Betroffene Komponente/Domain, Reproduktionsschritte, Schweregrad

3. **Enhancement** (`03_enhancement.yml`)
   - Für Verbesserungsvorschläge
   - Enthält: Problem/Motivation, Lösungsvorschlag, Alternativen

4. **Refactoring** (`04_refactor.yml`)
   - Für Code-Refactoring
   - Enthält: Aktuelle Situation, vorgeschlagene Änderungen, Risiken

### Pull Request Templates

Drei komponentenspezifische PR-Templates:

1. **Mobile PR** (`mobile_pr_template.md`)
   - iOS/Android Testing-Checkliste
   - Offline-Funktionalität
   - Screenshots/Videos

2. **Web PR** (`web_pr_template.md`)
   - Browser-Kompatibilität
   - Responsive Design
   - Accessibility

3. **Server PR** (`server_pr_template.md`)
   - API-Änderungen
   - Database Migrations
   - Security Considerations
   - Domain Events

## Entwicklungsworkflow

### Tasks ausführen

VS Code Tasks sind für alle Komponenten vorkonfiguriert:

1. **Öffnen**: `Ctrl+Shift+P` (Windows/Linux) oder `Cmd+Shift+P` (Mac)
2. **Eingeben**: "Tasks: Run Task"
3. **Auswählen**: Gewünschten Task auswählen

#### Mobile Tasks
- `mobile: pub get` - Dependencies installieren
- `mobile: run` - App starten
- `mobile: test` - Tests ausführen
- `mobile: build apk` - APK erstellen

#### Admin Web Tasks
- `web: pub get` - Dependencies installieren
- `web: run` - Dev-Server starten (Chrome)
- `web: build` - Production Build
- `web: test` - Tests ausführen

#### Server Tasks
- `server: mvn clean install` - Build mit Tests
- `server: mvn verify` - Alle Tests und Checks
- `server: run` - Server starten
- `server: test` - Nur Tests

### Vorteil von Focused Workspaces

**Performance**:
- Weniger Dateien im Index
- Schnelleres Suchen und Navigieren
- Geringerer Speicherverbrauch

**Extension-Verwaltung**:
- Nur relevante Extensions aktiv
- Keine Konflikte zwischen Extension-Typen
- Schnellere Ladezeiten

**Fokus**:
- Keine Ablenkung durch andere Komponenten
- Klarere Ordnerstruktur
- Bessere Übersicht

## Domain-Driven Design

UrbanBloom folgt Domain-Driven Design Prinzipien. Die ausführliche DDD-Dokumentation findet sich unter:

- [shared-resources/documentation/domain-model-description-urbanbloom.md](../shared-resources/documentation/domain-model-description-urbanbloom.md)
- [shared-resources/documentation/urban_bloom_domains.md](../shared-resources/documentation/urban_bloom_domains.md)
- [shared-resources/documentation/urban_bloom_ddd_glossar.md](../shared-resources/documentation/urban_bloom_ddd_glossar.md)

### Bounded Contexts

1. User / Identity
2. Action / Observation
3. Plant Catalog
4. Location / District
5. Gamification
6. Challenge
7. Notification / Reminder
8. Admin / Analytics
9. Sync / Offline

### Kommunikation zwischen Domains

Die Kommunikation zwischen Domains erfolgt über **Domain Events**, nicht über direkte Referenzen. Dies gewährleistet lose Kopplung und ermöglicht unabhängige Entwicklung.

## Technologie-Stack

### Frontend (Mobile & Web)
- **Framework**: Flutter
- **Sprache**: Dart
- **State Management**: (zu definieren, z.B. Riverpod, Bloc)
- **Networking**: (zu definieren, z.B. Dio)
- **Local Storage**: (zu definieren, z.B. Hive, SQLite)

### Backend
- **Framework**: Spring Boot
- **Sprache**: Java
- **Build Tool**: Maven
- **Datenbank**: (zu definieren)
- **API**: RESTful
- **Authentication**: (zu definieren, z.B. JWT)

## Nächste Schritte

1. Flutter-Projekte initialisieren (`mobile/` und `admin-web/`)
2. Spring Boot Projekt initialisieren (`server/`)
3. Gemeinsame Design Tokens definieren
4. API-Spezifikation erstellen (OpenAPI/Swagger)
5. CI/CD Pipeline einrichten
6. Development Environment Setup Guide erstellen

## Kontakt & Support

Bei Fragen zum Projekt-Setup oder zur Verwendung der Workspaces, siehe:
- [README.md](../README.md) im Root-Verzeichnis
- DDD-Dokumentation unter `shared-resources/documentation/`
- GitHub Issues für spezifische Fragen
