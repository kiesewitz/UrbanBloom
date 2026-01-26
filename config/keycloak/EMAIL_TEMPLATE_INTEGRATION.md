# E-Mail Template Integration in Keycloak

## Übersicht

Die E-Mail Templates für Registrierung und Passwort-Reset müssen als Custom Theme in Keycloak integriert werden. Die Templates befinden sich in `config/keycloak/email-templates/`.

## Verfügbare Templates

```
config/keycloak/email-templates/
├── email-verification.ftl       # Registrierungs-Bestätigungsmail
├── password-reset.ftl            # Passwort-Zurücksetzen Mail
└── README.md                     # Template Dokumentation
```

## Integration Methoden

### Methode 1: Docker Volume Mount (Empfohlen für Development)

Diese Methode mountet die Templates direkt in den Keycloak Container.

#### 1. Docker Compose konfigurieren

```yaml
# docker-compose.yml
services:
  keycloak:
    image: quay.io/keycloak/keycloak:26.0.7
    volumes:
      # Realm Imports
      - ./config/keycloak/realm-export-mobile.json:/opt/keycloak/data/import/realm-export-mobile.json
      - ./config/keycloak/realm-export-webadmin.json:/opt/keycloak/data/import/realm-export-webadmin.json

      # E-Mail Templates (Custom Theme)
      - ./config/keycloak/email-templates:/opt/keycloak/themes/urbanbloom/email/html

      # Theme Properties (optional)
      - ./config/keycloak/theme.properties:/opt/keycloak/themes/urbanbloom/email/theme.properties
    command:
      - start-dev
      - --import-realm
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
      KC_DB: postgres
      KC_DB_URL: jdbc:postgresql://postgres:5432/keycloak
      KC_DB_USERNAME: keycloak
      KC_DB_PASSWORD: keycloak
```

#### 2. Theme Properties erstellen

Erstelle `config/keycloak/theme.properties`:

```properties
# Theme Properties für UrbanBloom Email Theme
parent=base
import=common/keycloak

# Locales
locales=de,en

# Theme Name
name=urbanbloom
```

#### 3. Keycloak neu starten

```bash
docker-compose down
docker-compose up -d keycloak
```

#### 4. Theme in Realm aktivieren

1. Login in Keycloak Admin Console: http://localhost:8080
2. Wähle Realm: **urbanbloom-mobile** (oder urbanbloom-webadmin)
3. Navigiere zu: **Realm Settings** → **Themes** Tab
4. Setze **Email Theme** auf: `urbanbloom`
5. Klicke **Save**

### Methode 2: Theme JAR Build (Empfohlen für Production)

Diese Methode erstellt ein JAR-Archiv mit dem Theme.

#### 1. Theme Struktur erstellen

```bash
mkdir -p keycloak-theme/src/main/resources/theme/urbanbloom/email/html
mkdir -p keycloak-theme/src/main/resources/theme/urbanbloom/email/messages

# Kopiere Templates
cp config/keycloak/email-templates/*.ftl \
   keycloak-theme/src/main/resources/theme/urbanbloom/email/html/

# Erstelle theme.properties
cat > keycloak-theme/src/main/resources/theme/urbanbloom/email/theme.properties << 'EOF'
parent=base
import=common/keycloak
locales=de,en
EOF
```

#### 2. Messages für Internationalisierung

**messages_de.properties**:
```properties
emailVerificationSubject=E-Mail-Adresse bestätigen - UrbanBloom
emailVerificationBodyHtml=<h1>Willkommen bei UrbanBloom!</h1>
passwordResetSubject=Passwort zurücksetzen - UrbanBloom
passwordResetBodyHtml=<h1>Passwort zurücksetzen</h1>
```

**messages_en.properties**:
```properties
emailVerificationSubject=Verify your email - UrbanBloom
emailVerificationBodyHtml=<h1>Welcome to UrbanBloom!</h1>
passwordResetSubject=Reset your password - UrbanBloom
passwordResetBodyHtml=<h1>Reset Password</h1>
```

#### 3. Maven POM erstellen

`keycloak-theme/pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>de.urbanbloom</groupId>
    <artifactId>keycloak-email-theme</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>UrbanBloom Keycloak Email Theme</name>
    <description>Custom email templates for UrbanBloom</description>

    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
            </resource>
        </resources>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

#### 4. JAR bauen und deployen

```bash
# Build
cd keycloak-theme
mvn clean package

# Deploy zum Keycloak Container
docker cp target/keycloak-email-theme-1.0.0.jar \
  keycloak:/opt/keycloak/providers/

# Keycloak neu starten (damit Provider geladen wird)
docker restart keycloak
```

### Methode 3: Direkt in Container kopieren (Quick Test)

Für schnelle Tests ohne Rebuild:

```bash
# Kopiere Templates direkt in laufenden Container
docker exec keycloak mkdir -p /opt/keycloak/themes/urbanbloom/email/html

docker cp config/keycloak/email-templates/email-verification.ftl \
  keycloak:/opt/keycloak/themes/urbanbloom/email/html/

docker cp config/keycloak/email-templates/password-reset.ftl \
  keycloak:/opt/keycloak/themes/urbanbloom/email/html/

# Theme Properties
docker exec keycloak bash -c 'cat > /opt/keycloak/themes/urbanbloom/email/theme.properties << EOF
parent=base
import=common/keycloak
locales=de,en
EOF'

# Keycloak muss eventuell neu gestartet werden
docker restart keycloak
```

## Template Variablen

Die FreeMarker Templates (`.ftl`) haben Zugriff auf folgende Variablen:

### Email Verification Template

```freemarker
${link}                          # Verifikationslink
${linkExpiration}                # Link-Ablaufzeit in Minuten
${realmName}                     # Name des Realms
${user.email}                    # E-Mail des Users
${user.firstName}                # Vorname
${user.lastName}                 # Nachname
${user.username}                 # Benutzername
```

### Password Reset Template

```freemarker
${link}                          # Passwort-Reset-Link
${linkExpiration}                # Link-Ablaufzeit in Minuten
${realmName}                     # Name des Realms
${user.email}                    # E-Mail des Users
${user.firstName}                # Vorname
${user.lastName}                 # Nachname
```

### Beispiel-Verwendung in Template

```html
<!DOCTYPE html>
<html>
<head>
    <title>Email Verification</title>
</head>
<body>
    <h1>Hallo ${user.firstName!""}!</h1>

    <p>Bitte klicke auf den folgenden Link, um deine E-Mail-Adresse zu bestätigen:</p>

    <a href="${link}" style="background: #4F46E5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">
        E-Mail bestätigen
    </a>

    <p>Der Link ist ${linkExpiration} Minuten gültig.</p>

    <p>Falls du dich nicht bei ${realmName} registriert hast, ignoriere diese E-Mail.</p>
</body>
</html>
```

## SMTP Konfiguration

Die E-Mails werden über SMTP versendet. Konfiguriere SMTP in jedem Realm:

### Via Keycloak Admin Console

1. Login: http://localhost:8080
2. Wähle Realm: **urbanbloom-mobile** (oder urbanbloom-webadmin)
3. Navigiere zu: **Realm Settings** → **Email** Tab
4. Konfiguriere:

   ```
   Host: smtp.gmail.com
   Port: 587
   From: noreply@urbanbloom.de
   From Display Name: UrbanBloom
   Enable StartTLS: ON
   Enable Authentication: ON
   Username: your-email@gmail.com
   Password: your-app-password
   ```

5. Klicke **Save**
6. Teste mit **Test connection**

### Via Realm Export JSON

Die SMTP-Konfiguration kann auch im Realm Export gesetzt werden:

```json
{
  "realm": "urbanbloom-mobile",
  "smtpServer": {
    "host": "smtp.gmail.com",
    "port": "587",
    "from": "noreply@urbanbloom.de",
    "fromDisplayName": "UrbanBloom",
    "starttls": "true",
    "auth": "true",
    "user": "your-email@gmail.com",
    "password": "**********"
  },
  "emailTheme": "urbanbloom"
}
```

**⚠️ WICHTIG**: Passwörter nie im Git committen! Verwende `**********` als Platzhalter.

## Gmail App Password erstellen

Für Gmail SMTP:

1. Gehe zu: https://myaccount.google.com/security
2. Aktiviere **2-Step Verification**
3. Gehe zu: https://myaccount.google.com/apppasswords
4. Wähle: **Mail** → **Other (Custom name)**
5. Name: `Keycloak UrbanBloom`
6. Klicke **Generate**
7. Kopiere das 16-stellige Passwort
8. Verwende dieses Passwort in Keycloak SMTP Config

## Testing

### 1. E-Mail Verification testen

```bash
# Registriere einen neuen User über API
curl -X POST http://localhost:8080/realms/urbanbloom-mobile/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=urbanbloom-mobile-app" \
  -d "username=test@example.com" \
  -d "password=Test1234!" \
  -d "scope=openid email profile"

# Überprüfe E-Mail-Posteingang auf Verifikationsmail
```

### 2. Password Reset testen

```bash
# Trigger Password Reset
curl -X POST http://localhost:8080/realms/urbanbloom-mobile/login-actions/reset-credentials \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=test@example.com"

# Überprüfe E-Mail-Posteingang auf Reset-Mail
```

### 3. Template Debugging

Keycloak Logs anzeigen:

```bash
# Docker Logs
docker logs keycloak -f

# Suche nach E-Mail-bezogenen Logs
docker logs keycloak 2>&1 | grep -i "email\|smtp\|mail"
```

## Troubleshooting

### Problem: Theme wird nicht gefunden

**Symptome**: Theme "urbanbloom" erscheint nicht in Dropdown

**Lösung**:
1. Prüfe Theme-Struktur:
   ```bash
   docker exec keycloak ls -la /opt/keycloak/themes/urbanbloom/email/
   ```
2. Stelle sicher, dass `theme.properties` existiert
3. Restart Keycloak: `docker restart keycloak`
4. Cache leeren: Admin Console → Realm Settings → Clear cache

### Problem: E-Mails werden nicht versendet

**Symptome**: Keine E-Mails im Posteingang

**Lösung**:
1. Prüfe SMTP-Konfiguration in Realm Settings → Email
2. Teste Verbindung mit "Test connection"
3. Prüfe Logs: `docker logs keycloak 2>&1 | grep -i smtp`
4. Bei Gmail: Verwende App-Passwort, nicht normales Passwort
5. Prüfe Firewall (Port 587 muss offen sein)

### Problem: Template-Variablen werden nicht ersetzt

**Symptome**: `${link}` erscheint als Text statt als Link

**Lösung**:
1. Prüfe FreeMarker-Syntax in `.ftl` Datei
2. Verwende `${variable!"default"}` für Optional Values
3. Prüfe Keycloak Logs auf Template-Fehler
4. Teste mit einfachem Template ohne Styling

### Problem: Styling funktioniert nicht

**Symptome**: E-Mail sieht nicht formatiert aus

**Lösung**:
1. Verwende Inline CSS (nicht externe Stylesheets)
2. Teste mit verschiedenen E-Mail-Clients
3. Verwende E-Mail-sichere HTML (Tables statt Flexbox/Grid)
4. Prüfe Spam-Ordner (manchmal landen styled Mails dort)

## Best Practices

### ✅ DO

- Verwende Inline CSS für Styling
- Teste E-Mails in mehreren Clients (Gmail, Outlook, Apple Mail)
- Verwende responsive Design (Media Queries)
- Füge Plain-Text Alternative hinzu
- Verwende HTTPS für alle Links
- Setze `alt` Text für Bilder
- Verwende absolute URLs für Bilder
- Teste Link-Ablauf (default: 5 Minuten)

### ❌ DON'T

- Keine externen Stylesheets verwenden
- Keine JavaScript in E-Mails
- Keine Flash/Video-Embeds
- Keine Forms in E-Mails
- Keine sensiblen Daten in E-Mails anzeigen
- Keine Tracking-Pixel ohne User-Consent

## Weitere Templates

Keycloak unterstützt weitere E-Mail-Templates:

- `emailVerificationSubject.ftl` / `emailVerificationBody.ftl`
- `passwordResetSubject.ftl` / `passwordResetBody.ftl`
- `executeActionsSubject.ftl` / `executeActionsBody.ftl`
- `eventLoginErrorSubject.ftl` / `eventLoginErrorBody.ftl`
- `eventRemoveTotpSubject.ftl` / `eventRemoveTotpBody.ftl`
- `eventUpdatePasswordSubject.ftl` / `eventUpdatePasswordBody.ftl`
- `eventUpdateTotpSubject.ftl` / `eventUpdateTotpBody.ftl`
- `identityProviderLinkSubject.ftl` / `identityProviderLinkBody.ftl`
- `orgInviteSubject.ftl` / `orgInviteBody.ftl`

Template-Dateien müssen in `html/` Ordner als `.ftl` (FreeMarker) liegen.

## Weiterführende Links

- [Keycloak Server Developer Guide - Themes](https://www.keycloak.org/docs/latest/server_development/#_themes)
- [FreeMarker Documentation](https://freemarker.apache.org/docs/)
- [Email Template Design Best Practices](https://www.campaignmonitor.com/css/)

---

**Letzte Aktualisierung**: 2025-01-26
**Verantwortlich**: DevOps Team
