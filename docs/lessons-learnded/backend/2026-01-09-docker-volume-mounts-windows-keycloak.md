# Lessons Learned: Docker Volume Mounts mit Keycloak Themes auf Windows

**Datum:** 2026-01-09  
**Kontext:** Feature US-USR-02: Self-Registration (Mobile) — Keycloak Email Template Customization  
**Beteiligte:** Development Team

## 1. Problemübersicht (Situation)

### Symptom
Nach dem Hinzufügen eines Custom-Email-Themes zu Keycloak in `docker-compose.yml` mit einem relativen Pfad (`./themes`) wurde das Theme-Verzeichnis **nicht im Keycloak-Container gemountet**. Trotz gültiger Verzeichnisstruktur auf dem Host-System zeigte Keycloak keine verfügbaren Custom-Themes in der Admin Console.

### Zeitpunkt
Trat bei der Implementierung von Keycloak Email-Template Customization auf.

### Auswirkung
- Custom Email-Templates konnten nicht aktiviert werden
- Benutzerfreundliche deutsche Verifizierungsmails nicht möglich
- Mehrere Neustarts und Debugging-Zyklen notwendig

## 2. Kernursachen (Root Cause Analysis)

### Diagnose
```bash
# Fehlgeschlagener Check:
docker exec schoollibrary-keycloak ls -la /opt/keycloak/themes
# Result: Directory /opt/keycloak/themes is empty (nur Standard Keycloak themes, nicht unser Custom theme)
```

### Root Causes

- [ x ] **Technisches Hindernis:** Docker-Desktop auf Windows mit relativen Pfaden
  - **Ursache 1:** Relativer Pfad `./themes` wird von Docker nicht korrekt aufgelöst auf Windows
  - **Ursache 2:** Docker-Desktop nutzt WSL2/Hyper-V, der relativer Pfad wird nicht vom Host-Working-Directory aus berechnet
  - **Ursache 3:** `${PWD}` Variable war nicht in der `docker-compose.yml` definiert

- [ x ] **Wissenslücke:** Volume Paths in Docker-Compose auf verschiedenen Betriebssystemen
  - Annahme war, dass `./themes` wie in Linux automatisch relativ zum Docker-Compose-File aufgelöst wird
  - Fehlende Kenntnis über Windows-spezifische Pfad-Handling-Unterschiede

## 3. Umgesetzte Lösung (Action Taken)

### Schritt 1: Problem-Erkennung
```bash
# Container-Debugging
docker exec schoollibrary-keycloak ls -la /opt/keycloak/themes/schoollibrary/
# Result: Directory does not exist
```

### Schritt 2: Pfad-Konfiguration korrigieren
**Vorher (Fehlerhaft):**
```yaml
keycloak:
  volumes:
    - ./themes:/opt/keycloak/themes:ro
```

**Nachher (Funktionierend):**
```yaml
keycloak:
  volumes:
    - ${PWD}/themes:/opt/keycloak/themes:ro
```

### Schritt 3: Docker-Compose neu starten
```bash
docker-compose down keycloak
docker-compose up -d keycloak
# Wait for Keycloak to start (~45 seconds)
```

### Schritt 4: Verifizierung
```bash
docker exec schoollibrary-keycloak ls -la /opt/keycloak/themes/schoollibrary/email/
# Result: Successfully mounted all theme files
```

## 4. Konkrete Referenzen (Code & Docs)

### Veränderte Dateien
- **docker-compose.yml:** [docker-compose.yml](docker-compose.yml#L27) — Volume-Mount korrigiert mit `${PWD}`
- **Keycloak Dokumentation:** [docs/configurations/keycloak-setup.md](docs/configurations/keycloak-setup.md#L103) — Theme-Setup erweitert
- **Theme-Struktur:**
  - [themes/schoollibrary/theme.properties](themes/schoollibrary/theme.properties)
  - [themes/schoollibrary/email/html/email-verification.ftl](themes/schoollibrary/email/html/email-verification.ftl)
  - [themes/schoollibrary/email/text/email-verification.ftl](themes/schoollibrary/email/text/email-verification.ftl)
  - [themes/schoollibrary/email/messages/messages_de.properties](themes/schoollibrary/email/messages/messages_de.properties)

## 5. Empfehlungen & Lessons Learned

### Stop Doing ❌
- **Relative Pfade in Docker-Compose auf Windows verwenden** — Sie werden nicht wie erwartet aufgelöst
- **Annahmen über Plattform-Kompatibilität treffen** — Docker-Verhalten unterscheidet sich zwischen Linux, Mac und Windows

### Start Doing ✅
- **Immer `${PWD}` oder absolute Pfade für Docker Volumes verwenden** — Funktioniert cross-platform konsistent
  ```yaml
  volumes:
    - ${PWD}/themes:/opt/keycloak/themes:ro  # ✅ Correct
    - ./themes:/opt/keycloak/themes:ro       # ❌ Avoid on Windows
  ```

- **Docker Volume Mount Debugging in Checkliste aufnehmen:**
  ```bash
  # Verification Command
  docker exec <container-name> ls -la <target-path>
  ```

- **Wartezeit nach Docker-Restart einplanen** — Keycloak braucht ~45 Sekunden um vollständig zu starten und Themes zu laden

- **FreeMarker-Template-Debugging mit `${..}` Syntax vertraut machen** — Keycloak nutzt FreeMarker für Email-Templates
  ```ftl
  ${user.firstName}  <!-- User-Attribute -->
  ${link}            <!-- Verification Link -->
  ${link.validityPeriodFormatted}  <!-- Token-Gültigkeitsdauer -->
  ```

### Keep Doing ✅
- **Umfassende Dokumentation für Infrastruktur-Setup** — Hilft künftig schneller zu debuggen
- **Theme-Struktur mit HTML und Text-Varianten** — Ermöglicht Fallback für Text-only Email-Clients

## 6. Alternative Ansätze

### Ansatz 1: Dockerfile für Keycloak (❌ Verworfen)
**Beschreibung:** Custom Dockerfile bauen, der Theme beim Image-Build kopiert
```dockerfile
FROM keycloak/keycloak:26.4.7
COPY themes/schoollibrary /opt/keycloak/themes/schoollibrary
```

**Grund für Ablehnung:**
- Adds complexity in build process
- Weniger flexibel für Entwicklung (Theme-Änderungen erfordern Image-Rebuild)
- Längere Feedback-Zyklen beim Template-Debugging

### Ansatz 2: Admin API für Theme-Upload (❌ Nicht praktikabel)
**Beschreibung:** Themes via Keycloak Admin API nach dem Start uploaden

**Grund für Ablehnung:**
- Keycloak unterstützt kein direktes Theme-Upload via REST API
- Würde Custom-Initalisierungs-Script erfordern
- Unnötig komplex für Development-Setup

### Ansatz 3: Volume Mount mit absoluten Pfaden (✅ Akzeptiert)
Aktuell verwendete Lösung mit `${PWD}` — Best Practice auf allen Plattformen

## 7. Fazit / TL;DR

**Docker-Compose mit relativen Pfaden funktioniert auf Windows nicht zuverlässig. Mit `${PWD}` statt `./` werden Umgebungsvariablen richtig evaluiert und der absolute Pfad an Docker weitergegeben — funktioniert konsistent auf Linux, Mac und Windows.**

### Checkliste für zukünftige Docker Volume Mounts:
- [ ] Pfad-Syntax: `${PWD}/your-dir:/container/path` verwenden
- [ ] Verifizierung: `docker exec <container> ls -la /container/path`
- [ ] Wartezeiten: ~45 Sekunden für Service-Ready nach Restart
- [ ] Dokumentation: Setup-Schritte in `docs/configurations/` festhalten

---

## 8. Relevante Links

- **Docker-Compose Doku:** https://docs.docker.com/compose/compose-file/compose-file-v3/#volumes
- **Windows + Docker:** https://docs.docker.com/desktop/install/windows-install/
- **Keycloak Themes:** https://www.keycloak.org/docs/latest/server_development/#_themes
- **FreeMarker Email Templates:** https://freemarker.apache.org/