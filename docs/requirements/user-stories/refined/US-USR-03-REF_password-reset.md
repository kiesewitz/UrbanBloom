# User Story US-USR-03-REF: Password Reset (Refined)

## Story
**As a** registered user (student, teacher or library staff member)
**I want to** reset my password securely via email
**So that** I can regain access to my account if I forget my password

## Priority
**Must-Have** | MVP Phase 1

## UI-Prototypen

### Mobile App Prototypes

**Password Reset Screen** (3-Step Flow):
- HTML: [Mobile Password Reset](../../../ui/prototypes/mobile_app/password_reset_screen/code.html)
- Features:
  - Step 1: Email eingeben
  - Step 2: 6-stelligen Code eingeben
  - Step 3: Neues Passwort setzen
  - Success Screen

**Link from Login**:
- HTML: [Mobile Login - Forgot Password Link](../../../ui/prototypes/mobile_app/login_screen/code.html)

### Admin Web Prototypes

**Password Reset Screen**:
- HTML: [Web Password Reset](../../../ui/frontend/password_reset_screen/code.html)

### E-Mail Templates

**Password Reset Email Template**:
- Template: [Password Reset Template](../../../../config/keycloak/email-templates/password-reset.ftl)
- Features:
  - 6-stelliger Verifizierungscode
  - Direkt-Link zum Reset
  - Ablaufzeit-Anzeige (15 Minuten)
  - Sicherheitshinweise
- Dokumentation: [Email Templates README](../../../../config/keycloak/email-templates/README.md)

## Refinement Discussion

### Klärungspunkte & Entscheidungen

| Punkt | Entscheidung | Begründung |
|-------|--------------|------------|
| Reset-Methode | E-Mail mit temporärem Link + 6-stelliger Code | Standard Security Practice, benutzerfreundlich |
| Link-Gültigkeit | 15 Minuten | Balance zwischen Security und UX |
| Code-Format | 6-stellige Zahl | Einfach einzugeben, sicher genug für kurze Gültigkeit |
| Rate Limiting | Max 3 Reset-Anfragen pro Stunde | Schutz vor Missbrauch |
| Passwort-Anforderungen | Min. 8 Zeichen, 1 Groß-, 1 Zahl, 1 Sonderzeichen | Standard Security Requirements |

## Tasks

### Backend
- [ ] Implementiere Password-Reset-Request-Endpoint (`POST /api/v1/auth/password-reset/request`)
- [ ] Generiere sichere temporäre Reset-Tokens (signiert, zeitlich begrenzt)
- [ ] Generiere 6-stellige Verifizierungscodes
- [ ] Implementiere Token-Validierungs-Endpoint (`POST /api/v1/auth/password-reset/verify-code`)
- [ ] Implementiere Password-Update-Endpoint (`POST /api/v1/auth/password-reset/complete`)
- [ ] Integration mit Keycloak über `IdentityProvider`-Interface
- [ ] E-Mail-Versand über Queue (asynchron)
- [ ] Rate Limiting für Reset-Anfragen (max 3/Stunde)
- [ ] Audit-Logging für Reset-Ereignisse
- [ ] Token-Cleanup-Job für abgelaufene Tokens

### Frontend Mobile
- [ ] Password Reset Screen mit 3-Step Flow implementieren
- [ ] Step 1: Email-Eingabe mit Validierung
- [ ] Step 2: 6-stellige Code-Eingabe mit Auto-Focus
- [ ] Step 3: Neues Passwort mit Bestätigung
- [ ] Success Screen nach erfolgreichem Reset
- [ ] "Forgot Password" Link auf Login Screen
- [ ] Error Handling für ungültige Codes / abgelaufene Links
- [ ] "Resend Code" Funktionalität mit Countdown-Timer
- [ ] Deep Link Handling für Reset-Link aus E-Mail

### Frontend Web Admin
- [ ] Password Reset Flow analog zu Mobile
- [ ] Integration mit Backend-API
- [ ] Token-Handling im Browser

### Testing
- [ ] Unit-Tests für Token-Generierung und Validierung
- [ ] Integration-Tests für kompletten Reset-Flow
- [ ] E2E-Tests: Anfrage → E-Mail → Code-Eingabe → Passwort-Update → Login
- [ ] Security-Tests: Rate Limiting, Token-Expiry, ungültige Tokens
- [ ] E-Mail-Template-Tests

## Akzeptanzkriterien

### Functional
- [ ] "Passwort vergessen"-Link ist auf Login-Screen vorhanden
- [ ] User kann E-Mail-Adresse eingeben und Reset anfordern
- [ ] Reset-E-Mail wird an registrierte Adresse versendet
- [ ] E-Mail enthält 6-stelligen Code und Reset-Link
- [ ] Code-Eingabe-Screen akzeptiert 6-stellige Codes
- [ ] Bei korrektem Code wird Passwort-Eingabe-Screen angezeigt
- [ ] Neues Passwort muss Sicherheitsanforderungen erfüllen
- [ ] Passwort-Bestätigung muss übereinstimmen
- [ ] Success-Screen wird nach erfolgreichem Reset angezeigt
- [ ] User kann sich mit neuem Passwort anmelden
- [ ] "Code erneut senden" funktioniert mit Countdown (60s)
- [ ] Abgelaufene Codes/Links zeigen klare Fehlermeldung
- [ ] Ungültige E-Mail-Adresse zeigt Fehlermeldung
- [ ] Zu viele Reset-Anfragen werden geblockt (Rate Limiting)

### Non-Functional
- [ ] Reset-Anfrage dauert < 2 Sekunden (API Response)
- [ ] E-Mail wird innerhalb von 1 Minute zugestellt
- [ ] Reset-Link ist 15 Minuten gültig
- [ ] Code ist 15 Minuten gültig
- [ ] Tokens sind kryptographisch sicher (nicht ratebar)
- [ ] Rate Limiting: Max 3 Anfragen/Stunde pro E-Mail
- [ ] Passwörter werden niemals im Klartext übertragen oder gespeichert

### Technical
- [ ] Tokens sind JWT-signiert und validiert
- [ ] Codes werden in sicherer DB gespeichert (gehashed)
- [ ] HTTPS für alle Reset-Requests
- [ ] E-Mail-Templates sind Keycloak-kompatibel (FTL)
- [ ] Audit-Logs für alle Reset-Ereignisse
- [ ] Integration über `IdentityProvider`-Interface (vendor-neutral)

## Technische Notizen

### Backend API Endpoints

```java
// Request Password Reset
POST /api/v1/auth/password-reset/request
Body: { "email": "user@example.com" }
Response: 200 OK { "message": "Reset email sent" }

// Verify Reset Code
POST /api/v1/auth/password-reset/verify-code
Body: { "email": "user@example.com", "code": "123456" }
Response: 200 OK { "reset_token": "temp-jwt-token" }

// Complete Password Reset
POST /api/v1/auth/password-reset/complete
Body: {
  "reset_token": "temp-jwt-token",
  "new_password": "NewSecure123!",
  "confirm_password": "NewSecure123!"
}
Response: 200 OK { "message": "Password updated successfully" }
```

### Frontend Mobile (Flutter)

```dart
// Password Reset Service
class PasswordResetService {
  final Dio _dio;

  Future<void> requestReset(String email) async {
    await _dio.post('/auth/password-reset/request', data: {'email': email});
  }

  Future<String> verifyCode(String email, String code) async {
    final response = await _dio.post('/auth/password-reset/verify-code',
      data: {'email': email, 'code': code});
    return response.data['reset_token'];
  }

  Future<void> completeReset(String resetToken, String newPassword) async {
    await _dio.post('/auth/password-reset/complete', data: {
      'reset_token': resetToken,
      'new_password': newPassword,
      'confirm_password': newPassword,
    });
  }
}
```

### Keycloak Integration

```java
// IdentityProvider Interface
public interface IdentityProvider {
    void sendPasswordResetEmail(String email);
    boolean validateResetToken(String token);
    void updatePassword(String userId, String newPassword);
}

// Keycloak Implementation
@Service
public class KeycloakIdentityProvider implements IdentityProvider {
    @Override
    public void sendPasswordResetEmail(String email) {
        // Use Keycloak Admin API to trigger password reset
        var user = keycloakAdmin.realm(realmName).users()
            .search(email).get(0);
        user.executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }
}
```

## Definition of Done
- [ ] Code reviewed und genehmigt
- [ ] Alle Tests bestanden (Unit, Integration, E2E)
- [ ] E-Mail-Templates in Keycloak konfiguriert
- [ ] Security-Audit durchgeführt (Token-Sicherheit, Rate Limiting)
- [ ] API-Dokumentation aktualisiert
- [ ] User-Dokumentation erstellt (FAQ: "Passwort vergessen")
- [ ] Deployment in Test-Umgebung erfolgreich
- [ ] Product Owner hat Feature abgenommen

## Abhängigkeiten
- Keycloak Server mit konfigurierten E-Mail-Templates
- SMTP-Server für E-Mail-Versand
- Redis für Rate Limiting (optional, kann auch DB sein)
- US-USR-01-REF (User Authentication) muss implementiert sein

## Risiken & Offene Punkte
- E-Mail-Zustellung kann verzögert sein → Monitoring erforderlich
- Spam-Filter könnten Reset-Mails blocken → SPF/DKIM konfigurieren
- Internationalisierung: E-Mail-Templates in mehreren Sprachen?
- Mobile Deep Links: Fallback wenn App nicht installiert?
- Rate Limiting: Sollte pro IP oder pro E-Mail sein? → Entscheidung: Pro E-Mail (besserer Schutz)

## Security Considerations

### Token Security
- Tokens sind JWT-signiert mit 15min Expiry
- Codes sind einmalig verwendbar (werden nach Nutzung invalidiert)
- Codes werden gehashed in DB gespeichert
- Brute-Force-Schutz: Max 5 Versuche pro Token

### Privacy
- Reset-Anfragen für nicht-existierende E-Mails geben keine Hinweise (gleiche Response)
- Keine Leakage von User-Existenz durch Timing-Attacken
- Audit-Logs enthalten keine Passwörter (nur Hashes)

### Rate Limiting
- Max 3 Reset-Anfragen pro Stunde pro E-Mail-Adresse
- Exponentielles Backoff bei wiederholten Anfragen
- Admin-Benachrichtigung bei verdächtigen Mustern

## Related Stories
- US-USR-01-REF (User Authentication)
- US-USR-02-REF (Self-Registration)
