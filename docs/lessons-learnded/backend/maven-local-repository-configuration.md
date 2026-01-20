# Lessons Learned: Maven Local Repository Configuration

**Datum:** 29. Dezember 2025  
**Kontext:** Refactoring der Backend-Projektstruktur (Issue #5)

## Problem

Nach der Einrichtung des modularen Maven-Projekts mit lokalem Repository gab es zwei separate `.m2/repository` Verzeichnisse:
1. `backend/.m2/repository` (konfiguriert in `backend/.mvn/maven.config`)
2. `backend/schoollibrary-app/.m2/` (nicht benötigt)

Zusätzlich schlug die VS Code Task "Backend: Run Spring Boot App" mit folgendem Fehler fehl:
```
Could not resolve dependencies for project com.schoollibrary:schoollibrary-app:jar:0.0.1-SNAPSHOT: 
The following artifacts could not be resolved: 
com.schoollibrary:schoollibrary-shared:jar:0.0.1-SNAPSHOT (absent), ...
```

## Root Cause

1. **Doppeltes Repository:** Das Verzeichnis `backend/schoollibrary-app/.m2/` wurde versehentlich erstellt und war redundant.

2. **Task-Konfiguration:** Die ursprüngliche Task führte Maven von `backend/schoollibrary-app` aus:
   ```json
   "options": {
     "cwd": "${workspaceFolder}/backend/schoollibrary-app"
   }
   ```
   Von diesem Verzeichnis aus konnte Maven die `.mvn/maven.config` Datei im `backend/` Verzeichnis nicht finden, wodurch das lokale Repository nicht verwendet wurde und die Module-Dependencies nicht auflösbar waren.

## Lösung

1. **Repository-Bereinigung:** Das redundante Repository wurde entfernt:
   ```powershell
   Remove-Item -Path "backend\schoollibrary-app\.m2" -Recurse -Force
   ```

2. **Task-Anpassung:** Die Task wurde so umkonfiguriert, dass Maven vom `backend/` Verzeichnis aus läuft, aber explizit das `schoollibrary-app` Modul anspricht:
   ```json
   {
     "label": "Backend: Run Spring Boot App",
     "command": "mvn",
     "args": [
       "-pl",
       "schoollibrary-app",
       "spring-boot:run"
     ],
     "options": {
       "cwd": "${workspaceFolder}/backend"
     }
   }
   ```

## Key Learnings

1. **Maven-Konfiguration ist vererbbar:** Die `.mvn/maven.config` Datei wird nur gefunden, wenn Maven vom entsprechenden Verzeichnis oder einem Unterverzeichnis aus gestartet wird, das die Datei enthält.

2. **Multi-Modul Maven Tasks:** Für modulare Maven-Projekte sollten Tasks immer vom Parent-POM-Verzeichnis aus ausgeführt werden, mit der Option `-pl <module-name>`, um spezifische Module anzusprechen.

3. **Lokale Repository-Konsistenz:** In einem modularen Projekt sollte nur ein lokales Repository konfiguriert sein, um Inkonsistenzen zu vermeiden.

## Best Practices

- **VS Code Tasks für Maven Multi-Modul-Projekte:**
  - `cwd` immer auf das Verzeichnis mit dem Parent-POM setzen
  - Mit `-pl <module>` das gewünschte Modul auswählen
  - Mit `-am` (also make) auch abhängige Module bauen, falls notwendig

- **Lokales Maven-Repository:**
  - Die `.mvn/maven.config` Datei sollte im Root des Maven-Projekts liegen
  - Nur ein lokales Repository pro Projekt verwenden
  - Pfad relativ zum Parent-POM-Verzeichnis angeben

## Betroffene Dateien

- [backend/.mvn/maven.config](../../../backend/.mvn/maven.config)
- [.vscode/tasks.json](../../../.vscode/tasks.json) - Task "Backend: Run Spring Boot App"
- `backend/schoollibrary-app/.m2/` (entfernt)

## Referenzen

- [Maven CLI Options](https://maven.apache.org/ref/current/maven-embedder/cli.html)
- [Maven Configuration File](https://maven.apache.org/docs/3.3.1/release-notes.html#JVM_and_Command_Line_Options)
