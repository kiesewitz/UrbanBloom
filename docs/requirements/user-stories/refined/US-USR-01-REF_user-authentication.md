# User Story US-USR-01-REF: User Authentication (Refined)

## Story
**As a** student, teacher or library staff member  
**I want to** log in to the digital school library system securely  
**So that** I can access my personal library account and protected features

## Priority
**Must-Have** | MVP Phase 1

## UI-Prototypen

### Mobile App Prototypes

**Login Screen**:
- HTML: [Mobile Login](../../../ui/prototypes/mobile_app/login_screen/code.html)
- Referenz (alt): [login_für_schul-app](../../../ui/prototypes/stitch_schulbibliotheks_app/login_für_schul-app/code.html)

**Registration Screen**:
- HTML: [Mobile Registration](../../../ui/prototypes/mobile_app/registration_screen/code.html)
- Referenz (alt): [registrierung_für_schul-app](../../../ui/prototypes/stitch_schulbibliotheks_app/registrierung_für_schul-app/code.html)

**Password Reset Screen**:
- HTML: [Mobile Password Reset](../../../ui/prototypes/mobile_app/password_reset_screen/code.html)
- Referenz (alt): [password-reset](../../../ui/prototypes/stitch_schulbibliotheks_app/password-reset/code.html)

**Profile Screen**:
- HTML: [Mobile Profile](../../../ui/prototypes/mobile_app/profile_screen/code.html)

### Admin Web Prototypes

**Login Screen**:
- HTML: [Web Login](../../../ui/frontend/login_screen/code.html)

**Registration Screen**:
- HTML: [Web Registration](../../../ui/frontend/signup_screen/code.html)

**Password Reset Screen**:
- HTML: [Web Password Reset](../../../ui/frontend/password_reset_screen/code.html)

**Profile Screen**:
- HTML: [Web Profile](../../../ui/frontend/profile_screen/code.html)

## Refinement Discussion

### Klärungspunkte & Entscheidungen

| Punkt | Entscheidung | Begründung |
|-------|--------------|------------|
| Welches Authentifizierungssystem verwenden? | Keycloak OAuth2/OpenID Connect | Bereits in Infrastruktur vorhanden, Enterprise-Standard |
| Welche Benutzerrollen existieren? | Student, Teacher, Librarian, Admin | Abdeckung aller Hauptakteure im System |
| Self-Registration erlauben? | Ja, aber nur mit registrierter Schuldomäne | Schuldomäne, sowie Lehrer-Accounts werden von Admin importiert, Andere als Lehrer e-mails werden automatisch der Schüler-Rolle zugewiesen |
| Password-Reset-Funktion? | Ja, via E-Mail-Link | Standard-Security-Practice |
| Multi-Factor Authentication (MFA)? | Nein im MVP | Kann später als Should-Have ergänzt werden |
| Session-Timeout? | 30 Minuten Inaktivität | Balance zwischen Security und UX |
| Remember-Me Funktion? | Ja, 7 Tage | Verbessert UX für häufige Nutzung |

### Conversation Points — Decisions

1. Allowed school domains for self-registration

- Decision: The allowed domain list is defined in the MVP via Spring properties (already implemented).
- Enforcement: The domain will be validated both frontend and backend during registration/login.
- Integration: Backend will expose a simple REST endpoint (suggested `GET /api/v1/auth/allowed-domains`) that returns the configured allowed domains; the registration page (mobile/web) should call this endpoint and show guidance to users about allowed school domains.

2. Admin provisioning vs self-registration adapter

- Decision: Admin provisioning is handled through the Admin Web application and will not be merged into the self-registration flow for the MVP.
- TEACHER role: Special provisioning/handling for the `TEACHER` role will be implemented in a later iteration, not in MVP self-registration.

3. Authentication flow architecture

- Decision: Login and registration are handled via Spring Backend API endpoints. The backend communicates with Keycloak Admin CLI for user verification and token issuance. User data is stored in both Keycloak and the application's Postgres DB for dual persistence.
- Enforcement: Login endpoint (`POST /api/v1/auth/login`) accepts email/password, validates via Keycloak, and returns JWT tokens. Registration assigns STUDENT role automatically for non-teacher emails.
- Integration: Clients (mobile/web) call the backend API for auth; backend uses `IdentityProvider` interface to interact with Keycloak.

4. Token refresh architecture

- Decision: Token refresh is also handled via Spring Backend API to maintain consistency and avoid direct client-Keycloak coupling. Clients send refresh tokens to `POST /api/v1/auth/refresh` endpoint.
- Enforcement: Backend validates refresh token with Keycloak and issues new access/refresh tokens. This ensures all auth operations go through the `IdentityProvider` interface.
- Integration: Reduces vendor lock-in; clients don't need to know Keycloak endpoints directly.

### Tasks

#### Backend
- Keycloak-Integration in Spring Security konfigurieren
- OAuth2 Resource Server Setup für JWT-Validation
- SecurityConfig mit Keycloak-Settings erstellen
- User-Entität mit Keycloak-User-ID verknüpfen
- UserService für Benutzerdetails implementieren
- Role-Mapping zwischen Keycloak und Anwendung
- Session-Management und Token-Refresh implementieren
- CORS-Konfiguration für Frontend-Origins
- Implementiere Login-Endpoint (`POST /api/v1/auth/login`), der Credentials validiert und JWT-Tokens von Keycloak abruft
- Implementiere Refresh-Endpoint (`POST /api/v1/auth/refresh`), der Refresh-Token validiert und neue JWT-Tokens von Keycloak abruft
- Implementiere ein `IdentityProvider`-Interface und eine `KeycloakIdentityProvider`-Implementierung, die provider-spezifische Admin/Provisioning-APIs kapselt (User‑Provisioning, Attribute, Verifikation).
- Trenne Auth von Domain: pflege eine minimale `UserProfile`-Entität (z. B. `externalId` → IdP user id) in der App‑DB und verknüpfe sie mit Rollen/Perms.
- Stelle Token‑Validation über OIDC Discovery (`issuer-uri` / JWK‑Set) sicher; nutze `JwtAuthenticationConverter` für Claim→Authority‑Mapping.
- Alle Provisioning-/Registration‑Aufrufe im Code sollten das `IdentityProvider`-Adapter verwenden, nicht direkte Keycloak‑API‑Aufrufe, um Vendor‑Lock‑in zu vermeiden.

#### Frontend Web-Admin
- Login-Seite implementieren, die POST /api/v1/auth/login aufruft (Anmeldung mit Schüler-Accounts NICHT möglich)
- Token-Storage im LocalStorage/SessionStorage
- Axios-Interceptor für JWT-Token
- Token-Refresh-Logik via POST /api/v1/auth/refresh bei Expiry
- Logout-Funktion mit API-Call
- Protected-Route-Guards implementieren
- Session-Timeout-Warnung anzeigen

#### Frontend Mobile
- Register-Seite mit Anlegen von User-Account durch API
- Login-Screen, der POST /api/v1/auth/login aufruft
- Secure-Storage für Tokens (flutter_secure_storage)
- HTTP-Interceptor für JWT-Token (Dio)
- Token-Refresh-Mechanismus via POST /api/v1/auth/refresh
- Logout mit API-Call
- Biometrische Authentifizierung (Nice-to-have)
- Route-Guards für protected Screens

#### Testing
- Unit-Tests für SecurityConfig
- Integration-Tests für Login-Flow
- Token-Validation-Tests
- Role-Based-Access-Tests
- E2E-Tests für kompletten Login/Logout-Zyklus
- Security-Tests für unauthorized access

## Akzeptanzkriterien

### Functional
- [ ] Login-Screen zeigt den Titel "Login"
- [ ] Login-Screen bietet die Felder "School Email" und "Password" (Passwort-Input maskiert)
- [ ] Login-Button ist vorhanden und ermöglicht Anmeldung mit E-Mail + Passwort
- [ ] Link "Forgot Password?" ist vorhanden
- [ ] Hinweis/Link "Sign up" ist vorhanden (führt zur Registrierung)
- [ ] Nach erfolgreicher Anmeldung wird Benutzer zum Dashboard weitergeleitet
- [ ] Ungültige Anmeldedaten zeigen Fehlermeldung
- [ ] "Passwort vergessen"-Link funktioniert und sendet Reset-E-Mail
- [ ] Benutzer können sich abmelden
- [ ] Nach Abmeldung werden Tokens gelöscht und Session beendet
- [ ] "Remember Me" speichert Session für 7 Tage
- [ ] Session-Timeout nach 30 Minuten Inaktivität
- [ ] Warnung 5 Minuten vor Session-Timeout
- [ ] Automatische Weiterleitung zu Login bei abgelaufener Session

### Non-Functional
- [ ] Registrierungsprozess dauert < 3 Sekunden
- [ ] Login-Prozess dauert < 3 Sekunden
- [ ] Passwörter werden niemals im Klartext gespeichert
- [ ] JWT-Tokens sind signiert und validiert
- [ ] HTTPS für alle Authentifizierungs-Requests
- [ ] Tokens sind HTTP-only und Secure (wo möglich)
- [ ] Brute-Force-Schutz (Rate Limiting)
- [ ] Login-Seite ist WCAG 2.1 AA konform

### Technical
- [ ] Registrierung (hinzufügen von Benutzer) erfolgt mittels Keycloak Server
- [ ] OAuth2/OpenID Connect Standard wird befolgt
- [ ] JWT-Tokens enthalten User-ID und Rollen
- [ ] Token-Expiry: Access Token 15 Min, Refresh Token 7 Tage
- [ ] Keycloak-Realm "schoollibrary" konfiguriert
- [ ] Client-IDs für Web und Mobile registriert
- [ ] CORS-Origins für Dev und Prod konfiguriert
- [ ] Fehlerbehandlung mit Standard-HTTP-Status (401, 403)

## Technische Notizen

### Backend-Architektur
**Technologie**: Spring Boot 3.x, Spring Security 6.x, OAuth2 Resource Server

**SecurityConfig Beispiel**:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/health", "/api/v1/app/info").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/librarian/**").hasAnyRole("LIBRARIAN", "ADMIN")
                .anyRequest().authenticated());
        return http.build();
    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }
}
```

**application.properties**:
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=${KEYCLOAK_ISSUER_URI}
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${KEYCLOAK_JWK_SET_URI}
```

### Frontend-Architektur Web Admin (nur Admin-Stories)
_Hinweis: Frontend Web ist die Web Admin App und betrifft nur Admin-Rollen._
**Technologie**: React, TypeScript, Axios

**API Client für Login**:
```typescript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL
});

export const login = async (email: string, password: string) => {
  const response = await apiClient.post('/api/v1/auth/login', { email, password });
  return response.data; // { access_token, refresh_token, ... }
};
```
    return false;
  }
};
```

**API Client mit Interceptor**:
```typescript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL
});

apiClient.interceptors.request.use(async (config) => {
  if (keycloak.token) {
    await keycloak.updateToken(30); // Refresh if expires in 30s
    config.headers.Authorization = `Bearer ${keycloak.token}`;
  }
  return config;
});
```

### Frontend-Architektur Mobile
**Technologie**: Flutter/Dart, Dio

**API Client für Login**:
```dart
import 'package:dio/dio.dart';

final Dio dio = Dio(BaseOptions(baseUrl: 'http://localhost:8080/api/v1'));

Future<Map<String, dynamic>> login(String email, String password) async {
  final response = await dio.post('/auth/login', data: {
    'email': email,
    'password': password,
  });
  return response.data; // { 'access_token': '...', 'refresh_token': '...' }
}
```

### Keycloak-Konfiguration
**Realm**: schoollibrary  
**Clients**:
- `schoollibrary-web` (Public, PKCE)
- `schoollibrary-mobile` (Public, PKCE)

**Roles**:
- STUDENT
- LIBRARIAN  
- ADMIN

**User Attributes**:
- studentId / staffId
- schoolClass (für Students)
- department (für Staff)

## Definition of Done
- [ ] Code reviewed und genehmigt
- [ ] Alle Tests bestanden (Unit, Integration, E2E)
- [ ] Keycloak-Realm konfiguriert und dokumentiert
- [ ] Security-Audit durchgeführt (OWASP Top 10)
- [ ] API-Dokumentation aktualisiert
- [ ] User-Dokumentation erstellt
- [ ] Deployment in Test-Umgebung erfolgreich
- [ ] Penetration-Tests durchgeführt
- [ ] Product Owner hat Feature abgenommen

## Abhängigkeiten
- Keycloak-Server muss laufen (Port 8081 in dev)
- PostgreSQL für Keycloak-Daten
- E-Mail-Server für Password-Reset
- HTTPS-Zertifikate für Production

## Risiken & Offene Punkte
- Keycloak Single Point of Failure → HA-Setup für Production
- E-Mail-Zustellung kann verzögert sein → SLA mit IT-Abteilung klären
- Schulinterne Identity Provider Integration → LDAP/AD-Anbindung später
- Datenschutz: Token-Storage muss DSGVO-konform sein
- Mobile: Biometrische Auth erfordert zusätzliche Plattform-Permissions
