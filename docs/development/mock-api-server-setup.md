# Mock API Server - Setup Anleitung

## Übersicht

Diese Anleitung zeigt, wie du einen **Mock REST API Server** mit [Prism CLI](https://stoplight.io/open-source/prism) einrichtest, um das Password Reset Frontend ohne laufendes Backend zu testen.

## Voraussetzungen

- Node.js und npm installiert
- OpenAPI Spezifikation (`docs/api/user.yaml`)

## Installation

### 1. Prism CLI global installieren

```bash
npm install -g @stoplight/prism-cli
```

Oder lokal im Projekt:

```bash
npm install --save-dev @stoplight/prism-cli
```

### 2. Prism Version prüfen

```bash
prism --version
```

## Mock Server starten

### Einfacher Start

```bash
prism mock docs/api/user.yaml
```

**Standard-Einstellungen:**
- Port: `4010`
- Host: `localhost`
- URL: `http://localhost:4010`

### Mit erweiterten Optionen

```bash
prism mock docs/api/user.yaml \
  --port 8080 \
  --host 0.0.0.0 \
  --dynamic
```

**Optionen:**
- `--port <PORT>`: Benutzerdefinierten Port
- `--host <HOST>`: Host-Addresse (0.0.0.0 für externe Zugriffe)
- `--dynamic`: Dynamische Beispiele generieren statt statischer
- `--cors`: CORS-Header aktivieren (wichtig für Flutter Web!)

### Empfohlener Befehl für Flutter Testing

```bash
prism mock docs/api/user.yaml --port 4010 --cors --dynamic
```

## Startup Script erstellen

Erstelle `start-mock-server.sh` (Linux/Mac) oder `start-mock-server.bat` (Windows):

### Windows (`start-mock-server.bat`)

```batch
@echo off
echo Starting Mock API Server...
prism mock docs/api/user.yaml --port 4010 --cors --dynamic
```

### Linux/Mac (`start-mock-server.sh`)

```bash
#!/bin/bash
echo "Starting Mock API Server..."
prism mock docs/api/user.yaml --port 4010 --cors --dynamic
```

Ausführbar machen:
```bash
chmod +x start-mock-server.sh
```

## Flutter App konfigurieren

### Schritt 1: API Base URL anpassen

Bearbeite `frontend-mobile/lib/core/network/api_client.dart`:

```dart
class ApiClient {
  // Original:
  // static const String baseUrl = 'http://localhost:8080';
  
  // Für Mock Server:
  static const String baseUrl = 'http://localhost:4010';
  
  // ...
}
```

**Oder** nutze Umgebungsvariablen:

```dart
static const String baseUrl = String.fromEnvironment(
  'API_BASE_URL',
  defaultValue: 'http://localhost:4010', // Mock Server default
);
```

Dann Flutter mit:
```bash
flutter run --dart-define=API_BASE_URL=http://localhost:4010
```

### Schritt 2: CORS für Flutter Web

Wenn du Flutter Web testest, stelle sicher dass Prism mit `--cors` läuft!

## Mock Server testen

### 1. Server läuft prüfen

```bash
curl http://localhost:4010
```

### 2. Password Reset Endpoint testen

**Request:**
```bash
curl -X POST http://localhost:4010/api/v1/auth/password/reset-request \
  -H "Content-Type: application/json" \
  -d '{"email": "test@schule.de"}'
```

**Erwartete Response:**
```json
{
  "message": "Falls ein Konto mit dieser E-Mail existiert, wurde eine E-Mail zum Zurücksetzen des Passworts gesendet."
}
```

### 3. Mit Flutter App testen

1. Mock Server starten: `./start-mock-server.sh`
2. Flutter App starten: `flutter run`
3. Navigiere zu "Passwort vergessen"
4. Gib Email ein und teste

## Prism Features

### Dynamische Responses

Prism generiert automatisch Beispieldaten basierend auf dem Schema:

```yaml
# user.yaml
PasswordResetResponse:
  properties:
    message:
      type: string
      example: "Falls ein Konto..."  # Prism verwendet dieses Beispiel!
```

### Request Validation

Prism validiert automatisch:
- ✅ Content-Type Header
- ✅ Request Body gegen Schema
- ✅ Required Fields
- ✅ Datentypen

**Ungültige Requests** → `400 Bad Request`

### Mehrere Endpoints

Prism mockt **alle** Endpoints aus `user.yaml`:
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/password/reset-request`
- `POST /api/v1/auth/password/reset`
- Etc.

## Troubleshooting

### Problem: Port bereits belegt

```bash
Error: listen EADDRINUSE: address already in use :::4010
```

**Lösung:** Anderen Port verwenden
```bash
prism mock docs/api/user.yaml --port 4011
```

### Problem: CORS Fehler in Flutter Web

```
Access to XMLHttpRequest at 'http://localhost:4010' from origin 
'http://localhost:xxxx' has been blocked by CORS policy
```

**Lösung:** Prism mit `--cors` starten
```bash
prism mock docs/api/user.yaml --cors
```

### Problem: "Cannot find module @stoplight/prism-cli"

**Lösung:** Prism neu installieren
```bash
npm install -g @stoplight/prism-cli
```

## Alternativen zu Prism

Falls Prism nicht funktioniert:

### 1. **Mockoon** (GUI)
- Download: https://mockoon.com/
- Import OpenAPI Spec
- Visuelles Interface

### 2. **json-server** (Einfacher)
- Für simple JSON Mock-Daten
- Weniger OpenAPI-Integration

### 3. **Postman Mock Server**
- CloudBasiert
- Import OpenAPI

## Production vs Mock

### Umgebungs-Switch

Erstelle `lib/core/config/environment.dart`:

```dart
enum Environment { mock, development, production }

class Config {
  static const Environment current = Environment.values.byName(
    String.fromEnvironment('ENV', defaultValue: 'mock'),
  );
  
  static String get apiBaseUrl {
    switch (current) {
      case Environment.mock:
        return 'http://localhost:4010';
      case Environment.development:
        return 'http://localhost:8080';
      case Environment.production:
        return 'https://api.schulbibliothek.de';
    }
  }
}
```

**Verwendung:**
```dart
class ApiClient {
  static final String baseUrl = Config.apiBaseUrl;
}
```

**Flutter Run:**
```bash
# Mock Server
flutter run --dart-define=ENV=mock

# Dev Backend
flutter run --dart-define=ENV=development

# Production
flutter run --dart-define=ENV=production
```

## Zusammenfassung

### Quick Start

```bash
# 1. Prism installieren
npm install -g @stoplight/prism-cli

# 2. Server starten
prism mock docs/api/user.yaml --port 4010 --cors --dynamic

# 3. In anderem Terminal: Flutter starten
flutter run --dart-define=API_BASE_URL=http://localhost:4010
```

### Wichtige URLs

- **Mock Server**: http://localhost:4010
- **API Docs** (generiert): http://localhost:4010/__docs
- **OpenAPI Spec**: `docs/api/user.yaml`

---

**Viel Erfolg beim Testen!** 🚀
