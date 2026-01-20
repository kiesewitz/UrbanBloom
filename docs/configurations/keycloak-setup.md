# Keycloak Setup Anleitung (Digital School Library)

Diese Anleitung beschreibt Schritt für Schritt die notwendige Konfiguration von Keycloak, um die Benutzerregistrierung und Authentifizierung für das Projekt "Digital School Library" zu ermöglichen. Sie ist so geschrieben, dass sie auch ohne tiefere Keycloak-Vorkenntnisse durchgeführt werden kann.

## 1. Zugriff auf die Keycloak Admin-Konsole
1. Stelle sicher, dass die Docker-Container laufen (`docker-compose up -d`).
2. Öffne einen Webbrowser und navigiere zu: `http://localhost:8081`
3. Klicke auf **"Administration Console"**.
4. Melde dich mit den Standard-Zugangsdaten an:
   - **Username:** `admin`
   - **Password:** `admin`

## 2. Den Realm "schoollibrary" erstellen
Ein "Realm" ist ein isolierter Bereich für Benutzer, Clients und Rollen.
1. Bewege die Maus oben links über den Schriftzug **"Master"** (oder das Dropdown-Feld daneben).
2. Klicke auf die Schaltfläche **"Create Realm"**.
3. Gib im Feld **"Realm name"** folgendes ein: `schoollibrary` (alles kleingeschrieben).
4. Klicke auf **"Create"**.

## 3. Den Admin-Client konfigurieren
Damit das Backend-System (Java) automatisch Benutzer in Keycloak anlegen darf, muss der technische Client `admin-cli` vorbereitet werden.

1. Stelle sicher, dass oben links der Realm **"schoollibrary"** ausgewählt ist.
2. Klicke im linken Menü auf **"Clients"**.
3. Klicke in der Liste auf den Client mit der ID **"admin-cli"**.
4. Im Tab **"Settings"** (Standardansicht):
   - Suche den Bereich **"Capability config"**.
   - Stelle den Schalter **"Client authentication"** auf **"On"**.
   - Stelle sicher, dass unter **"Authentication flow"** die Option **"Service accounts roles"** angehakt ist.
   - Klicke unten auf **"Save"**.
5. Wechsel zum Tab **"Credentials"** (oben in der Tableiste):
   - Hier findest du das **"Client secret"**. Klicke auf das "Auge"-Symbol oder das Kopieren-Symbol, um den Wert zu sichern. **Dieses Secret musst du später im Backend eintragen.**
6. Wechsel zum Tab **"Client List"** und wähle aus der Liste **realm-management** aus, anschließend wechsle in das Tab Roles und suche/wähle folgende Rollen aus.
     - `manage-users`
     - `view-users`
     - `query-users`
   - Klicke jewails auf **"Save"**.

## 4. Den App-Client konfigurieren
Damit das Backend-System (Java) sich bei Keycloak für Benutzer-Logins authentifizieren kann, muss der Client `schoollibrary-app` konfiguriert werden.

1. Stelle sicher, dass oben links der Realm **"schoollibrary"** ausgewählt ist.
2. Klicke im linken Menü auf **"Clients"**.
3. Klicke in der Liste auf den Client mit der ID **"schoollibrary-app"**.
4. Im Tab **"Settings"** (Standardansicht):
   - Suche den Bereich **"Capability config"**.
   - Stelle den Schalter **"Client authentication"** auf **"On"**.
   - Stelle den Schalter **"DPoP"** auf **"Off"** (um DPoP zu deaktivieren, falls aktiviert).
   - Klicke unten auf **"Save"**.
5. Wechsel zum Tab **"Credentials"** (oben in der Tableiste):
   - Hier findest du das **"Client secret"**. Klicke auf das "Auge"-Symbol oder das Kopieren-Symbol, um den Wert zu sichern. **Dieses Secret musst du später im Backend eintragen.**

## 5. Rollen für Benutzer anlegen
Das System weist neuen Benutzern standardmäßig die Rolle `STUDENT` zu. Diese muss existieren.

1. Klicke im linken Menü auf **"Realm roles"**.
2. Klicke auf die Schaltfläche **"Create role"**.
3. Gib unter **"Role name"** folgendes ein: `STUDENT` (Großbuchstaben).
4. Klicke auf **"Save"**.
## 6. E-Mail Verification (SMTP) konfigurieren
Damit Keycloak E-Mails zur Verifizierung versenden kann, muss ein SMTP-Server konfiguriert werden.

1. Gehe im linken Menü auf **"Realm settings"**.
2. Wechsel zum Tab **"Email"**.
3. Trage die folgenden Daten ein (orientiere dich an den Werten in deiner `application.properties`):
   - **From:** `noreply@schoollibrary.local` (oder deine Gmail-Adresse)
   - **Host:** `smtp.gmail.com`
   - **Port:** `587`
   - **Enable StartTLS:** `On`
   - **Enable SSL:** `Off`
   - **Enable Authentication:** `On`
   - **Username:** (Deine Gmail-Adresse, z.B. `uwe.testmail.server@gmail.com`)
   - **Password:** (Dein 16-stelliges Google App-Passwort)
4. Klicke auf **"Save"**.
5. Nutze den Button **"Test connection"**, um die Einstellungen zu prüfen.

> **Tipp:** Falls du Gmail nutzt, musst du in deinem Google-Konto ein "App-Passwort" generieren. Das normale Passwort funktioniert oft nicht.

## 7. Backend-Konfiguration aktualisieren
Zuletzt muss das Backend über die neuen Secrets informiert werden.

1. Öffne im Projekt die Datei: [backend/schoollibrary-app/src/main/resources/application.properties](../../backend/schoollibrary-app/src/main/resources/application.properties)
2. Suche die Zeilen:
   `keycloak.admin.client-secret=`
   `keycloak.token.client-secret=`
3. Füge nach dem Gleichheitszeichen die in Schritt 3 und 4 kopierten Secrets ein:
   ```properties
   keycloak.admin.client-secret=8gNbhElYUd3WvfxwIeBBEWAho0tGURPW (Beispiel für admin-cli)
   keycloak.token.client-secret=xoqnjFzXrFNz4Qx9lXCAH4JN8xZmTG1M (Beispiel für schoollibrary-app)
   ```
4. Speichere die Datei und starte das Backend neu.

---
**Hinweis:** Falls die Registrierung weiterhin fehlschlägt, prüfe im Backend-Log ([backend/schoollibrary-app/logs/schoollibrary.log](../../backend/schoollibrary-app/logs/schoollibrary.log)), ob dort ein "404 Not Found" (Realm/Rollen-Name falsch) oder "401 Unauthorized" (Secret falsch) auftaucht.

### Wichtig: Umgang mit Client Secrets beim Import/Export
Wenn du einen Realm exportierst (`realm-export.json`), werden sensible Daten wie Client Secrets standardmäßig aus Sicherheitsgründen maskiert (`**********`).

**Das Problem:** Beim Import dieser Datei generiert Keycloak **neue** Secrets, da es mit den Sternchen nichts anfangen kann. Dadurch passen die Secrets in deiner `application.properties` nicht mehr zum Server.

**Best Practice für Test/Dev-Environments:**
Damit das Deployment reproduzierbar bleibt, solltest du das Secret in der `realm-export.json` **fest eintragen** (statt der Sternchen).
1.  Kopiere das funktionierende Secret aus Keycloak oder deiner `application.properties`.
2.  Suche in der `realm-export.json` nach `"clientAuthenticatorType": "client-secret"`.
3.  Ersetze:
    ```json
    "secret": "**********"
    ```
    durch:
    ```json
    "secret": "DEIN_FIXES_SECRET"
    ```
So wird bei jedem Container-Neustart exakt dasselbe Secret importiert, und deine App konnektiert sich sofort erfolgreich.

## 8. E-Mail-Templates anpassen (Optional)
Standardmäßig verwendet Keycloak englische E-Mail-Texte. Für eine deutsche Schulbibliothek kannst du die E-Mail-Inhalte anpassen.

### Option 1: Theme über Admin Console ändern (Einfach)
1. Gehe zu **"Realm settings"** > **"Themes"** Tab
2. Unter **"Email Theme"** wähle **"keycloak"** (Standard) oder erstelle ein Custom Theme
3. Klicke **"Save"**

### Option 2: Deutsche Übersetzungen aktivieren
Keycloak unterstützt Internationalisierung. Deutsche Texte sind bereits verfügbar:

1. Gehe zu **"Realm settings"** > **"Localization"** Tab
2. Aktiviere **"Internationalization Enabled"**
3. Füge unter **"Supported Locales"** hinzu: `de`
4. Setze **"Default Locale"** auf `de`
5. Klicke **"Save"**

### Option 3: Vollständige Template-Anpassung (Erweitert)
Für vollständige Kontrolle über Design und Inhalt:

#### Schritt 1: Theme-Struktur erstellen
Das Repository enthält bereits ein Beispiel-Theme unter `themes/schoollibrary/` mit deutschen E-Mail-Templates.

Struktur:
```
themes/schoollibrary/
├── email/
│   ├── messages/
│   │   └── messages_de.properties
│   ├── html/
│   │   ├── email-verification.ftl
│   │   └── template.ftl
│   └── text/
│       └── email-verification.ftl
└── theme.properties
```

#### Schritt 2: Theme in Keycloak-Container einbinden

**Option A: Volume Mount (Empfohlen für Entwicklung)**

1. Öffne die Datei [docker-compose.yml](../../docker-compose.yml)
2. Füge unter `keycloak:` einen `volumes:` Eintrag hinzu:
   ```yaml
   keycloak:
     image: keycloak/keycloak:26.4.7
     container_name: schoollibrary-keycloak
     volumes:
       - ./themes:/opt/keycloak/themes:ro
     # ... rest der Konfiguration
   ```
3. Starte Keycloak neu: `docker-compose restart keycloak`

**Option B: Theme ins Container-Image kopieren (Produktion)**

1. Erstelle ein `Dockerfile.keycloak`:
   ```dockerfile
   FROM keycloak/keycloak:26.4.7
   COPY themes/schoollibrary /opt/keycloak/themes/schoollibrary
   ```
2. Baue das Custom Image und nutze es in `docker-compose.yml`

#### Schritt 3: Theme in Keycloak aktivieren

1. Warte bis Keycloak vollständig gestartet ist (~30 Sekunden nach Restart)
2. Öffne Keycloak Admin Console: `http://localhost:8081`
3. Gehe zu **"Realm settings"** > **"Themes"** Tab
4. Unter **"Email Theme"** wähle aus dem Dropdown: **"schoollibrary"**
5. Klicke **"Save"**

#### Schritt 4: Theme testen

1. Registriere einen neuen Test-Benutzer über die App
2. Prüfe dein E-Mail-Postfach auf die Verifizierungsmail
3. Die Mail sollte nun auf Deutsch sein mit deinem Custom-Design

**Troubleshooting:**
- Theme erscheint nicht im Dropdown? → `docker-compose restart keycloak` und 30 Sek. warten
- Mail immer noch auf Englisch? → Cache löschen in Realm Settings > **"Clear Cache"**
- Theme-Änderungen werden nicht übernommen? → Keycloak neu starten

#### Schritt 5: Templates anpassen

Die E-Mail-Templates liegen in `themes/schoollibrary/email/`:
- `html/email-verification.ftl` - HTML-Version der Verifizierungsmail
- `text/email-verification.ftl` - Plain-Text-Version  
- `messages/messages_de.properties` - Deutsche Texte/Übersetzungen

### Beispiel für deutsche Verifizierungsmail:
```ftl
<#-- email-verification.ftl -->
Hallo ${user.firstName} ${user.lastName},

Willkommen bei der Schulbibliothek! Um dein Konto zu aktivieren, klicke bitte auf den folgenden Link:

[AKTIVIERUNGS-LINK]

Der Link ist 24 Stunden gültig.

Falls du dich nicht registriert hast, ignoriere diese E-Mail.

Mit freundlichen Grüßen,
Dein Schulbibliothek-Team
```

**Tipp:** Die Templates verwenden FreeMarker-Syntax. Du kannst Variablen wie `${user.firstName}`, `${link}`, etc. verwenden.

## 9. Passwort-Reset Funktionalität konfigurieren

Um die Passwort-Reset-Funktionalität über das Spring Boot Backend zu nutzen, sind folgende Einstellungen in Keycloak zwingend erforderlich:

### 1. SMTP-Server konfigurieren
Ohne SMTP-Server kann Keycloak keine E-Mails versenden. Siehe [Abschnitt 6: E-Mail Verification (SMTP) konfigurieren](#6-e-mail-verification-smtp-konfigurieren) weiter oben.

### 2. Required Actions aktivieren
Die Aktion `UPDATE_PASSWORD` muss in Keycloak aktiv sein.
1. Navigiere zu **"Authentication"** > **"Required Actions"**.
2. Stelle sicher, dass **"Update Password"** auf **"Enabled"** steht (Standard).

### 3. Service Account Roles (Backend-Berechtigungen)
Da das Backend die `executeActionsEmail`-API nutzt, benötigt der Admin-Client (`admin-cli`) die entsprechenden Rechte.
1. Navigiere zu **"Clients"** > **"admin-cli"**.
2. Wechsel zum Tab **"Service accounts roles"**.
3. Klicke auf **"Assign role"**.
4. Filtere nach **"Service Roles"** und wähle den Client-Scope **"realm-management"**.
5. Weise die Rolle **`manage-users`** zu (falls nicht schon in Schritt 3 geschehen).

### 4. Deep Links / Redirect URIs für Mobile-App
Damit der Benutzer nach dem Passwort-Reset im Browser wieder zurück in die Flutter-App geführt wird:
1. Navigiere zu **"Clients"** > **"schoollibrary-app"**.
2. Füge bei **"Valid redirect URIs"** folgendes hinzu: `schoollibrary://*`
3. (Empfohlen) Setze die **"Base URL"** auf `schoollibrary://login`.

### 5. "Passwort vergessen" Link auf Login-Seite (Optional)
Falls der Link direkt auf der Keycloak-Loginseite im Browser erscheinen soll:
1. Navigiere zu **"Realm settings"** > **"Login"**.
2. Aktiviere den Schalter **"Forgot password"**.

### 6. Mobile App Konfiguration (Deep Linking)

Damit die Mobile-App auf den Redirect von Keycloak reagieren kann, muss das URL-Schema in den App-Projekten registriert werden.

#### Android (`android/app/src/main/AndroidManifest.xml`)
Füge den folgenden `intent-filter` zur Haupt-Activity (`.MainActivity`) hinzu:

```xml
<intent-filter android:label="schoollibrary_login">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <!-- Das Schema deiner App -->
    <data android:scheme="schoollibrary" android:host="login" />
</intent-filter>
```

#### iOS (`ios/Runner/Info.plist`)
Registriere den URL-Typ in der `Info.plist`:

```xml
<key>CFBundleURLTypes</key>
<array>
    <dict>
        <key>CFBundleURLSchemes</key>
        <array>
            <string>schoollibrary</string>
        </array>
    </dict>
</array>
```


### 7. Zeit- und Zeitzonenprobleme beheben
Falls in den Docker-Containern (Keycloak, Datenbank, Logs) eine falsche Uhrzeit angezeigt wird (z.B. 1 Stunde Verschiebung), sollten die Zeitzone explizit gesetzt und die Systemzeit des Hosts gemountet werden.

**Lösung in `docker-compose.yml`:**
```yaml
services:
  keycloak:
    environment:
      TZ: Europe/Berlin
    volumes:
      - /etc/localtime:/etc/localtime:ro
  # Dasselbe für postgres und backend wiederholen
```

---
**Hinweis:** Nach diesen Änderungen sollte ein Neustart der Infrastruktur durchgeführt werden, um sicherzustellen, dass alle Konfigurationen (insbesondere Themes) korrekt geladen werden.
