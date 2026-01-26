# Keycloak Configuration für UrbanBloom

Dieses Verzeichnis enthält die Keycloak Realm-Konfigurationen für das UrbanBloom-Projekt.

## Übersicht

UrbanBloom verwendet **zwei separate Keycloak Realms**:

### 1. **urbanbloom-mobile** (Mobile App)
- **Zielgruppe**: Bürger, Schüler, Lehrer (öffentliche Nutzer)
- **Client ID**: `urbanbloom-mobile-app`
- **Registrierung**: Aktiviert (Self-Registration)
- **Rollen**:
  - `citizen` - Standard-Bürger
  - `student` - Schüler
  - `teacher` - Lehrer
- **Authentication**: OAuth2/OpenID Connect mit PKCE
- **Platform**: iOS, Android (Flutter)

### 2. **urbanbloom-webadmin** (Web Admin)
- **Zielgruppe**: Administratoren, Stadtverwalter, Bibliothekare
- **Client ID**: `urbanbloom-webadmin-app`
- **Registrierung**: Deaktiviert (nur Admin-verwaltete Accounts)
- **Rollen**:
  - `admin` - Super-Administrator
  - `city_admin` - Stadtverwaltung
  - `librarian` - Bibliothekar
  - `moderator` - Content-Moderator
- **Authentication**: OAuth2/OpenID Connect
- **Platform**: Web (Flutter Web)

## Dateien

```
config/keycloak/
├── realm-export-mobile.json         # Realm Config für Mobile App
├── realm-export-webadmin.json       # Realm Config für Web Admin
├── email-templates/                 # E-Mail Templates
│   ├── email-verification.ftl       # Registrierungsmail
│   ├── password-reset.ftl           # Passwort-Reset Mail
│   └── README.md                    # Template Dokumentation
└── README.md                        # Diese Datei
```

## Installation

### Voraussetzungen

- Docker und Docker Compose installiert
- Keycloak 22.0+ (empfohlen)
- SMTP-Server für E-Mail-Versand (z.B. Gmail, SendGrid)

### Schritt 1: Keycloak starten

```bash
# Im Projekt-Root-Verzeichnis
docker-compose up -d keycloak
```

### Schritt 2: Realms importieren

#### Option A: Via Admin Console (empfohlen)

1. Öffne Keycloak Admin Console: http://localhost:8080
2. Login mit Admin-Credentials (siehe docker-compose.yml)
3. Klicke auf das Realm-Dropdown (oben links)
4. Wähle **"Create Realm"**
5. Klicke auf **"Browse"** und wähle `realm-export-mobile.json`
6. Klicke **"Create"**
7. Wiederhole für `realm-export-webadmin.json`

#### Option B: Via Docker Volume Mount

```yaml
# docker-compose.yml
services:
  keycloak:
    volumes:
      - ./config/keycloak:/opt/keycloak/data/import
    command:
      - start-dev
      - --import-realm
```

### Schritt 3: Client Secrets konfigurieren

Die Realm-Exports enthalten Platzhalter (`**********`) für sensible Daten:

1. **Mobile App Client Secret**:
   ```bash
   # Generiere ein neues Secret
   openssl rand -base64 32
   ```
   - Gehe zu: Realm `urbanbloom-mobile` → Clients → `urbanbloom-mobile-app`
   - Tab: **Credentials**
   - Ersetze das Secret

2. **Web Admin Client Secret**:
   ```bash
   # Generiere ein neues Secret
   openssl rand -base64 32
   ```
   - Gehe zu: Realm `urbanbloom-webadmin` → Clients → `urbanbloom-webadmin-app`
   - Tab: **Credentials**
   - Ersetze das Secret

3. **Speichere Secrets sicher**:
   ```bash
   # .env Datei (NICHT in Git committen!)
   KEYCLOAK_MOBILE_CLIENT_SECRET=your-generated-secret-here
   KEYCLOAK_WEBADMIN_CLIENT_SECRET=your-generated-secret-here
   ```

### Schritt 4: SMTP konfigurieren

Beide Realms benötigen SMTP für E-Mail-Versand:

1. Gehe zu: Realm Settings → **Email**
2. Konfiguriere SMTP-Server:

   ```
   Host: smtp.gmail.com
   Port: 587
   From: noreply@urbanbloom.de
   From Display Name: UrbanBloom
   Enable StartTLS: Yes
   Enable Authentication: Yes
   Username: your-email@gmail.com
   Password: your-app-password
   ```

3. **Gmail App Password**:
   - Aktiviere 2FA in deinem Google Account
   - Gehe zu: https://myaccount.google.com/apppasswords
   - Erstelle ein App-Passwort für "Mail"
   - Verwende dieses Passwort (nicht dein normales Passwort)

4. **Test E-Mail senden**:
   - Klicke auf **"Test connection"**
   - Gib eine Test-E-Mail-Adresse ein
   - Überprüfe den Posteingang

### Schritt 5: E-Mail Templates einrichten

Siehe `email-templates/README.md` für detaillierte Anleitung.

Kurzversion:
```bash
# Kopiere Templates in Keycloak Theme
mkdir -p keycloak/themes/urbanbloom/email/html
cp config/keycloak/email-templates/*.ftl keycloak/themes/urbanbloom/email/html/
```

## Verwendung in der Anwendung

### Backend (Spring Boot)

```yaml
# application.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/urbanbloom-mobile
          jwk-set-uri: http://localhost:8080/realms/urbanbloom-mobile/protocol/openid-connect/certs

keycloak:
  mobile:
    realm: urbanbloom-mobile
    auth-server-url: http://localhost:8080
    client-id: urbanbloom-mobile-app
    client-secret: ${KEYCLOAK_MOBILE_CLIENT_SECRET}

  webadmin:
    realm: urbanbloom-webadmin
    auth-server-url: http://localhost:8080
    client-id: urbanbloom-webadmin-app
    client-secret: ${KEYCLOAK_WEBADMIN_CLIENT_SECRET}
```

### Frontend Mobile (Flutter)

```dart
// lib/core/auth/keycloak_config.dart
class KeycloakConfig {
  static const String issuer = 'http://localhost:8080/realms/urbanbloom-mobile';
  static const String clientId = 'urbanbloom-mobile-app';
  static const String redirectUri = 'urbanbloom://oauth2redirect';
  static const List<String> scopes = ['openid', 'profile', 'email', 'offline_access'];
}

// Use flutter_appauth package
final AuthorizationTokenResponse? result = await appAuth.authorizeAndExchangeCode(
  AuthorizationTokenRequest(
    clientId,
    redirectUri,
    issuer: issuer,
    scopes: scopes,
    promptValues: ['login'],
  ),
);
```

### Frontend Web Admin (Flutter Web)

```dart
// lib/core/auth/keycloak_config.dart
class KeycloakConfig {
  static const String issuer = 'http://localhost:8080/realms/urbanbloom-webadmin';
  static const String clientId = 'urbanbloom-webadmin-app';
  static const String clientSecret = String.fromEnvironment('KEYCLOAK_WEBADMIN_SECRET');
  static const String redirectUri = 'http://localhost:8081/callback';
  static const List<String> scopes = ['openid', 'profile', 'email', 'roles'];
}
```

## Rollen & Berechtigungen

### Mobile App Rollen

| Rolle     | Beschreibung                          | Standard |
|-----------|---------------------------------------|----------|
| `citizen` | Standard-Bürger                       | Ja       |
| `student` | Schüler mit erweiterten Rechten       | Nein     |
| `teacher` | Lehrer für Schulprojekte              | Nein     |

### Web Admin Rollen

| Rolle        | Beschreibung                              | Berechtigungen                                    |
|--------------|-------------------------------------------|---------------------------------------------------|
| `admin`      | Super-Administrator                       | Vollzugriff auf alle Funktionen                   |
| `city_admin` | Stadtverwaltung                           | Analytics, Reporting, Challenge-Management        |
| `librarian`  | Bibliothekar                              | Buch-Verwaltung, Ausleihen, Rückgaben             |
| `moderator`  | Content-Moderator                         | Aktionen verifizieren, User-Content moderieren    |

## Sicherheit

### Best Practices

✅ **Implementiert**:
- PKCE für Mobile Apps (Protection gegen Authorization Code Interception)
- Client Secrets für Web Admin (Confidential Client)
- Token Expiry: Access Token (15min), Refresh Token (7 Tage)
- Brute Force Protection (30 Fehlversuche → 15min Lock)
- E-Mail Verification bei Registrierung
- HTTPS-only in Production (via `sslRequired: external`)

⚠️ **Wichtig**:
- Verwende **niemals** Client Secrets im Mobile App Code
- Mobile App ist `publicClient: true` (keine Secrets)
- Web Admin ist `publicClient: false` (mit Secret)
- Client Secrets **nie** in Git committen
- Verwende Environment Variables für Secrets

### Token Lebensdauer

| Token Type        | Lebensdauer | Verwendung                          |
|-------------------|-------------|-------------------------------------|
| Access Token      | 15 Minuten  | API-Zugriff                         |
| Refresh Token     | 7 Tage      | Neue Access Tokens anfordern        |
| ID Token          | 15 Minuten  | User-Informationen                  |
| Auth Code         | 1 Minute    | OAuth2 Authorization Flow           |
| Password Reset    | 15 Minuten  | Passwort-Reset Link                 |
| Email Verify      | 24 Stunden  | E-Mail-Bestätigungslink             |

## Troubleshooting

### Problem: "Invalid redirect URI"

**Lösung**: Stelle sicher, dass alle Redirect URIs in den Client-Einstellungen konfiguriert sind:
- Mobile: `urbanbloom://oauth2redirect`, `urbanbloom://callback`
- Web: `http://localhost:8081/*`, `https://admin.urbanbloom.de/*`

### Problem: "Client authentication failed"

**Lösung**:
1. Prüfe, ob Client Secret korrekt konfiguriert ist
2. Bei Mobile App: Stelle sicher, dass `publicClient: true` ist
3. Bei Web Admin: Stelle sicher, dass `publicClient: false` ist

### Problem: E-Mails kommen nicht an

**Lösung**:
1. Prüfe SMTP-Konfiguration in Realm Settings → Email
2. Teste mit "Test connection"
3. Prüfe Firewall/Security Groups (Port 587)
4. Bei Gmail: Verwende App-Passwort, nicht normales Passwort
5. Prüfe Keycloak Logs: `docker logs keycloak`

### Problem: "Token expired"

**Lösung**:
1. Verwende Refresh Token zum Erneuern
2. Implementiere Auto-Refresh-Logik in der App
3. Bei Web Admin: Session-Management in Backend

### Problem: "User nicht gefunden nach Registrierung"

**Lösung**:
1. Prüfe, ob E-Mail-Verification aktiviert ist
2. User muss E-Mail bestätigen, bevor Login möglich ist
3. Check Keycloak Admin Console → Users → "View all users"

## Export aktualisieren

Wenn du Änderungen in Keycloak machst, exportiere die Realms erneut:

### Via Admin Console

1. Gehe zu: Realm Settings → **Action** → **Partial Export**
2. Wähle aus:
   - ✅ Export groups and roles
   - ✅ Export clients
   - ✅ Include client credentials (für Realm-Export)
3. Klicke **Export**
4. **Wichtig**: Entferne sensible Daten vor Git Commit:
   ```bash
   # Ersetze Client Secrets und Passwörter mit **********
   sed -i 's/"secret": ".*"/"secret": "**********"/g' realm-export-*.json
   sed -i 's/"password": ".*"/"password": "**********"/g' realm-export-*.json
   ```

### Via CLI (Docker)

```bash
# Export Mobile Realm
docker exec -it keycloak /opt/keycloak/bin/kc.sh export \
  --dir /tmp/export \
  --realm urbanbloom-mobile \
  --users realm_file

# Export Web Admin Realm
docker exec -it keycloak /opt/keycloak/bin/kc.sh export \
  --dir /tmp/export \
  --realm urbanbloom-webadmin \
  --users realm_file

# Copy from Container
docker cp keycloak:/tmp/export/urbanbloom-mobile-realm.json ./config/keycloak/realm-export-mobile.json
docker cp keycloak:/tmp/export/urbanbloom-webadmin-realm.json ./config/keycloak/realm-export-webadmin.json
```

## Weiterführende Links

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [OAuth 2.0 RFC](https://oauth.net/2/)
- [OpenID Connect Specification](https://openid.net/connect/)
- [PKCE RFC 7636](https://tools.ietf.org/html/rfc7636)

## Support

Bei Fragen oder Problemen:
- GitHub Issues: https://github.com/urbanbloom/issues
- E-Mail: support@urbanbloom.de
- Dokumentation: https://docs.urbanbloom.de

---

© 2025 UrbanBloom. Alle Rechte vorbehalten.
