# 🚀 Quick Start - Mock API Server

## Schnellstart (3 Schritte)

### 1. Prism CLI installieren

```bash
npm install -g @stoplight/prism-cli
```

### 2. Mock Server starten

**Windows:**
```cmd
start-mock-server.bat
```

**Linux/Mac:**
```bash
chmod +x start-mock-server.sh
./start-mock-server.sh
```

**Oder manuell:**
```bash
prism mock docs/api/user.yaml --port 4010 --cors --dynamic
```

### 3. Flutter App starten

```bash
cd frontend-mobile
flutter run
```

**Die App verbindet sich automatisch mit dem Mock Server auf `http://localhost:4010`**

---

## Test-Szenarien

### Password Reset testen

1. **Mock Server starten** → `http://localhost:4010`
2. **Flutter App starten** →  `flutter run`
3. **Im Emulator/Device:**
   - Öffne App → Login Screen
   - Klicke "Passwort vergessen?"
   - Gib Email ein: `test@schule.de`
   - Klicke "Link anfordern"
   - ✅ Erfolg → navigiert zu "Prüfe dein Postfach"

### API direkt testen (Optional)

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

---

## Umgebungen wechseln

### Mock Server (Default)

```bash
flutter run
```

## Development Backend

```bash
flutter run --dart-define=ENV=development
```

### Production

```bash
flutter run --dart-define=ENV=production
```

---

## Troubleshooting

### Problem: "prism: command not found"

**Lösung:**
```bash
npm install -g @stoplight/prism-cli
```

Prüfen:
```bash
prism --version
```

### Problem: Port 4010 bereits belegt

**Lösung:** Anderen Port verwenden
```bash
prism mock docs/api/user.yaml --port 4011 --cors
```

Dann in `frontend-mobile/lib/core/config/environment.dart` anpassen:
```dart
case Environment.mock:
  return 'http://localhost:4011';  // <-- geändert
```

### Problem: CORS Fehler (Flutter Web)

**Lösung:** Mock Server mit `--cors` starten
```bash
prism mock docs/api/user.yaml --port 4010 --cors --dynamic
```

---

## Verfügbare Mock Endpoints

Der Mock Server stellt **alle** Endpoints aus `docs/api/user.yaml` bereit:

- ✅ `POST /api/v1/auth/login`
- ✅ `POST /api/v1/auth/register`
- ✅ `POST /api/v1/auth/password/reset-request` ← **Neu!**
- ✅ `POST /api/v1/auth/password/reset`
- ✅ `POST /api/v1/auth/refresh`
- ✅ Weitere User-/Profile-Endpoints...

---

## Weitere Informationen

📖 **Vollständige Dokumentation:** [docs/development/mock-api-server-setup.md](file:///e:/SW-Dev/Git/ukondert/_school-projects/pr_digital-school-library/docs/development/mock-api-server-setup.md)

🎯 **Password Reset Implementation:** [Walkthrough](file:///C:/Users/uweko/.gemini/antigravity/brain/d9dfcbd4-e838-48dd-9d88-a289e55868c7/walkthrough.md)
