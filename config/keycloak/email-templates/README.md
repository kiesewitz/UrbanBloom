# Keycloak E-Mail Templates

Dieses Verzeichnis enthält die E-Mail-Templates für Keycloak, die für Registrierung und Passwort-Reset verwendet werden.

## Verfügbare Templates

### 1. `email-verification.ftl`
- **Zweck**: Bestätigung der E-Mail-Adresse bei der Registrierung
- **Trigger**: Wird gesendet, wenn ein neuer Benutzer sich registriert
- **Variablen**:
  - `${user.firstName}` - Vorname des Benutzers
  - `${link}` - Bestätigungslink
  - `${.now?string("yyyy")}` - Aktuelles Jahr

### 2. `password-reset.ftl`
- **Zweck**: Zurücksetzen des Passworts
- **Trigger**: Wird gesendet, wenn ein Benutzer "Passwort vergessen" anklickt
- **Variablen**:
  - `${user.firstName}` - Vorname des Benutzers
  - `${code}` - 6-stelliger Verifizierungscode
  - `${link}` - Direkt-Link zum Passwort-Reset
  - `${.now?string("yyyy")}` - Aktuelles Jahr

## Installation in Keycloak

### Option 1: Via Keycloak Admin Console

1. Melde dich in der Keycloak Admin Console an
2. Wähle dein Realm aus (z.B. `urbanbloom-mobile` oder `urbanbloom-web`)
3. Gehe zu **Realm Settings** → **Themes** → **Email**
4. Wähle ein Custom Theme oder erstelle ein neues
5. Kopiere die `.ftl` Dateien in das Theme-Verzeichnis:
   ```
   keycloak/themes/<your-theme>/email/html/
   ```

### Option 2: Via Docker Volume Mount

Wenn Keycloak in Docker läuft:

```yaml
# docker-compose.yml
services:
  keycloak:
    volumes:
      - ./config/keycloak/email-templates:/opt/keycloak/themes/urbanbloom/email/html
```

### Option 3: Realm Import

1. Exportiere dein aktuelles Realm
2. Füge die Theme-Konfiguration hinzu:
   ```json
   {
     "emailTheme": "urbanbloom"
   }
   ```
3. Importiere das Realm erneut

## Freemarker Template Syntax

Die Templates verwenden Freemarker Template Engine (FTL):

### Variablen
```ftl
${user.firstName}       // Vorname
${user.lastName}        // Nachname
${user.email}           // E-Mail
${link}                 // Aktionslink
${linkExpiration}       // Ablaufzeit
${realmName}            // Realm-Name
${code}                 // Verifizierungscode (nur bei password-reset)
```

### Datum/Zeit
```ftl
${.now?string("yyyy")}              // Jahr: 2025
${.now?string("dd.MM.yyyy")}        // Datum: 26.01.2025
${.now?string("HH:mm")}             // Zeit: 14:30
```

### Bedingte Anzeige
```ftl
<#if user.firstName??>
    Hallo ${user.firstName}!
<#else>
    Hallo!
</#if>
```

## Anpassungen

### Branding
- **Logo**: Ersetze 🌱 und 🔒 durch `<img src="...">` Tags
- **Farben**: Passe die Hex-Werte in den `<style>` Tags an
- **Texte**: Alle deutschen Texte können angepasst werden

### Design
- Die Templates sind responsive und mobile-optimiert
- Inline-CSS für maximale E-Mail-Client-Kompatibilität
- Getestet in: Gmail, Outlook, Apple Mail, Thunderbird

### Links
- Social Media Links: Aktualisiere die `<a href="#">` Tags
- Footer Links: Passe Datenschutz, AGB, Support URLs an

## Testing

### 1. Lokales Testing mit Keycloak

```bash
# Starte Keycloak mit den Templates
docker-compose up keycloak

# Teste Registrierung
# 1. Öffne http://localhost:8080
# 2. Erstelle neuen User
# 3. Prüfe E-Mail-Postfach (oder Logs)
```

### 2. E-Mail Vorschau Tools

- **Litmus**: https://litmus.com/
- **Email on Acid**: https://www.emailonacid.com/
- **Mailtrap**: https://mailtrap.io/ (für Testing ohne echte E-Mails)

### 3. SMTP Configuration

Konfiguriere SMTP in Keycloak Admin Console:
- **Realm Settings** → **Email**
- Host: `smtp.gmail.com` (oder anderer Provider)
- Port: `587` (TLS) oder `465` (SSL)
- From: `noreply@urbanbloom.de`
- Authentication: Username/Password

## Sicherheit

### Wichtige Hinweise:
- ✅ Links enthalten sichere Tokens
- ✅ Zeitlich begrenzte Gültigkeit (15min für Reset, 24h für Verification)
- ✅ HTTPS-only Links
- ⚠️ Keine sensiblen Daten im Klartext
- ⚠️ SPF, DKIM, DMARC für E-Mail-Domain konfigurieren

## Troubleshooting

### E-Mails kommen nicht an
1. Prüfe SMTP-Konfiguration in Keycloak
2. Prüfe Firewall/Security Groups (Port 587/465)
3. Prüfe Spam-Ordner
4. Teste mit Mailtrap oder ähnlichem Service

### Template-Fehler
1. Prüfe FTL-Syntax (Freemarker Validator)
2. Prüfe Keycloak Logs: `docker logs keycloak`
3. Stelle sicher, dass Theme korrekt geladen wird

### Styling funktioniert nicht
- Verwende Inline-CSS (nicht `<style>` im `<head>`)
- Vermeide `float`, `position: absolute`
- Teste mit Email Client Preview Tools

## Weiterführende Links

- [Keycloak Email Theming Guide](https://www.keycloak.org/docs/latest/server_development/#_themes)
- [Freemarker Template Documentation](https://freemarker.apache.org/docs/dgui.html)
- [Email Client CSS Support](https://www.campaignmonitor.com/css/)

## Lizenz

© 2025 UrbanBloom. Alle Rechte vorbehalten.
