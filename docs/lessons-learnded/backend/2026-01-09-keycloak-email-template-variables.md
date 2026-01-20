# Lessons Learned: Keycloak Email Template Variable Usage and Testing

**Datum:** 2026-01-09  
**Kontext:** Feature US-USR-02: Self-Registration (Mobile) — Keycloak Email Template Customization  
**Beteiligte:** Development Team

## 1. Problemübersicht (Situation)

### Symptom
Nach erfolgreicher Aktivierung eines custom Email-Themes in Keycloak funktionierte die E-Mail-Verifizierung nicht mehr. Die Logs zeigten Template-Fehler: `Failed to process template text/email-verification.ftl` mit der Fehlermeldung `Expected a hash, but this has evaluated to a string (wrapper: f.t.SimpleScalar): ==> link`.

### Zeitpunkt
Trat nach Aktivierung des "schoollibrary" Themes in der Keycloak Admin Console auf.

### Auswirkung
- E-Mail-Verifizierung komplett ausgefallen
- Keycloak-Service wurde "unhealthy" 
- Neue Benutzerregistrierungen konnten nicht abgeschlossen werden
- Mehrere Debugging-Zyklen und Service-Neustarts notwendig

## 2. Kernursachen (Root Cause Analysis)

### Diagnose
```bash
# Keycloak Logs zeigten:
Caused by: freemarker.core.NonHashException: For "." left-hand operand: Expected a hash, but this has evaluated to a string (wrapper: f.t.SimpleScalar): ==> link
FTL stack trace ("~" means nesting-related):
- Failed at: ${link.validityPeriodFormatted}  [in template "text/email-verification.ftl" at line 11, column 19]
```

### Root Causes

- [x] **Wissenslücke:** Unkenntnis über verfügbare FreeMarker-Variablen in Keycloak Email-Templates
  - **Ursache 1:** Annahme, dass `link` ein Objekt mit Eigenschaften wie `validityPeriodFormatted` ist
  - **Ursache 2:** Fehlende Dokumentation oder Beispiele für Keycloak 26.x Template-Variablen
  - **Ursache 3:** Kein Testing der Templates vor Aktivierung in Produktion

- [x] **Technisches Hindernis:** FreeMarker Template-Engine verhält sich anders als erwartet
  - In Keycloak Email-Templates ist `link` nur ein String (die URL), kein Objekt
  - `validityPeriodFormatted` ist keine verfügbare Variable in diesem Kontext

## 3. Umgesetzte Lösung (Action Taken)

### Schritt 1: Template-Fehler identifizieren
```bash
# Logs analysiert:
docker-compose logs keycloak --tail 20
# Fehler: NonHashException bei ${link.validityPeriodFormatted}
```

### Schritt 2: Template-Variablen korrigieren
**Falsche Verwendung:**
```ftl
Dieser Link ist ${link.validityPeriodFormatted} gültig.
```

**Korrigierte Version:**
```ftl
Dieser Link ist 24 Stunden gültig.
```

### Schritt 3: Templates in beiden Formaten korrigieren
- `themes/schoollibrary/email/text/email-verification.ftl`
- `themes/schoollibrary/email/html/email-verification.ftl`

### Schritt 4: Keycloak neu starten und testen
```bash
docker-compose restart keycloak
# Templates werden beim Start neu geladen
```

## 4. Konkrete Referenzen (Code & Docs)

### Template-Dateien:
- Text-Template: [themes/schoollibrary/email/text/email-verification.ftl](themes/schoollibrary/email/text/email-verification.ftl#L11)
- HTML-Template: [themes/schoollibrary/email/html/email-verification.ftl](themes/schoollibrary/email/html/email-verification.ftl#L85)
- Theme-Konfiguration: [themes/schoollibrary/theme.properties](themes/schoollibrary/theme.properties)

### Docker-Konfiguration:
- Volume-Mount: [docker-compose.yml](docker-compose.yml#L25)
- Keycloak-Setup: [docs/configurations/keycloak-setup.md](docs/configurations/keycloak-setup.md#7)

## 5. Empfehlungen & Lessons Learned

### Stop doing:
- **Nicht testen von Templates vor Aktivierung:** Templates ohne Validierung in Produktion aktivieren
- **Annahmen über Framework-Variablen:** Ohne Dokumentation von verfügbaren Variablen ausgehen

### Start doing:
- **Template-Testing etablieren:** Vor Aktivierung Templates lokal validieren
  ```bash
  # Template-Syntax prüfen
  docker exec keycloak freemarker-cli validate /opt/keycloak/themes/custom/email/text/email-verification.ftl
  ```
- **Variable-Dokumentation führen:** Für jedes Framework die verfügbaren Template-Variablen dokumentieren
- **Staged Rollout für Themes:** Zuerst in Test-Umgebung, dann Produktion

### Keep doing:
- **Modulare Theme-Struktur:** Trennung von HTML/Text/Message Templates beibehalten
- **Lokalisierung verwenden:** Deutsche Übersetzungen über `messages_de.properties` weiterführen

## 6. Alternative Ansätze

### Ansatz A: Dynamische Gültigkeitsdauer über Keycloak-Konfiguration
- **Idee:** Statt harcodierter "24 Stunden" die tatsächliche Gültigkeitsdauer aus Keycloak-Konfiguration lesen
- **Ablehnung:** Zu komplex für initiale Implementierung; Standard-Gültigkeit (24h) ausreichend

### Ansatz B: Vollständige Template-Debugging-Umgebung
- **Idee:** Lokale FreeMarker-Umgebung zum Testen von Templates vor Deployment
- **Ablehnung:** Overkill für aktuelles Projekt; würde Entwicklungszeit verlängern

## 7. Fazit / TL;DR
Bei der Anpassung von Keycloak Email-Templates immer die verfügbaren FreeMarker-Variablen validieren und Templates vor Aktivierung testen, um Service-Ausfälle zu vermeiden.</content>
<parameter name="filePath">e:\SW-Dev\Git\ukondert\_school-projects\pr_digital-school-library\docs\lessons-learnded\backend\2026-01-09-keycloak-email-template-variables.md