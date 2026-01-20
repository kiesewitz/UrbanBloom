# Lessons Learned: Keycloak Integration und Backend Konfiguration für die Selbstregistrierung

**Datum:** 2026-01-09  
**Kontext:** US-USR-02 Self-Registration (Mobile) - Fehlerbehebung bei der Keycloak-Integration  
**Beteiligte:** GitHub Copilot / Senior Software Architect, Benutzer

## 1. Problemübersicht (Situation)
Die Selbstregistrierung im Backend schlug fehl, was zu verschiedenen Fehlersymptomen in der mobilen App und im Backend-Log führte.
- **Symptome:** 
  1. Doppelte Pfad-Segmente im Endpunkt (`/api/v1/api/v1/registration`).
  2. "Registration failed due to server error" (HTTP 500) im Frontend.
  3. `jakarta.ws.rs.NotFoundException: HTTP 404 Not Found` in den Backend-Logs bei Interaktionen mit der Keycloak-API.
- **Auswirkungen:** Die Registrierung war funktionsunfähig; Entwicklungsstopp für den mobilen Registrierungs-Flow.

## 2. Kernursachen (Root Cause Analysis)
Es gab eine Kombination aus Code-Fehlern und fehlender Infrastruktur-Konfiguration:
- [x] **Technisches Hindernis:** Doppelt deklarierte Request-Mappings im Controller führten zu falschen URLs.
- [x] **Wissenslücke:** Unklarheit darüber, welche manuellen Schritte in Keycloak nach dem Aufsetzen des Docker-Containers notwendig sind.
- [x] **Prozessfehler:** Fehlende Dokumentation der notwendigen Keycloak-Initialkonfiguration (Realms, Clients, Roles).
- *Detailbeschreibung der Ursachen:*
  - Die `@RequestMapping("/api/v1/registration")` Annotation im [RegistrationController.java](backend/module-user/src/main/java/com/schoollibrary/user/api/RegistrationController.java) addierte sich zum globalen Präfix.
  - Der Keycloak-Dienst startete zwar, aber der erforderliche Realm `schoollibrary` war nicht vorhanden.
  - Der `admin-cli` Client war nicht für den neuen Realm konfiguriert (fehlendes Client-Secret).
  - Die fachlich erforderliche Rolle `STUDENT` fehlte im Keycloak-System.

## 3. Umgesetzte Lösung (Action Taken)
Die Lösung erfolgte iterativ durch Analyse der Stacktraces:
1. **Code-Fix:** Korrektur des Pfad-Mappings im [RegistrationController.java](backend/module-user/src/main/java/com/schoollibrary/user/api/RegistrationController.java#L23).
2. **Keycloak Realm:** Manuelle Erstellung des Realms `schoollibrary` in der Keycloak Admin Console.
3. **Keycloak Client:** 
   - Konfiguration des `admin-cli` Clients im `schoollibrary` Realm.
   - Aktivierung von `Client authentication`.
   - Generierung und Übertrag des Client-Secrets in die [application.properties](backend/schoollibrary-app/src/main/resources/application.properties#L50).
4. **Keycloak Roles:** Erstellung der Rolle `STUDENT` unter "Realm Roles", da diese vom [UserRegistrationService.java](backend/module-user/src/main/java/com/schoollibrary/user/application/UserRegistrationService.java) zur Zuweisung erwartet wird.

## 4. Konkrete Referenzen (Code & Docs)
- Controller Fix: [RegistrationController.java](backend/module-user/src/main/java/com/schoollibrary/user/api/RegistrationController.java)
- Konfiguration: [application.properties](backend/schoollibrary-app/src/main/resources/application.properties)
- Identity Provider Logik: [KeycloakIdentityProvider.java](backend/module-user/src/main/java/com/schoollibrary/user/adapter/infrastructure/keycloak/KeycloakIdentityProvider.java)

## 5. Empfehlungen & Lessons Learned
- **Stop doing:** Hartcodierte Pfad-Präfixe in Controllern verwenden, wenn diese bereits global (z.B. via Common Context) definiert sind.
- **Start doing:** Erstinbetriebnahme-Checkliste für Keycloak (Realm -> Client -> Roles) erstellen. Infrastructure-as-Code (z.B. Terraform oder Keycloak Export/Import) für die lokale Dev-Umgebung in Betracht ziehen.
- **Keep doing:** Detailliertes Logging der Keycloak-Interaktionen beibehalten – die Stacktraces waren entscheidend für die Lokalisierung der fehlenden Rollen/Clients.

## 6. Alternative Ansätze
- **Ansatz A:** Automatisierte Realm-Provisionierung beim Start des Backends (z.B. via Keycloak Admin Client Startup Bean).
  - *Grund für Ablehnung:* Erhöht die Komplexität des Backends; manuelle Konfiguration reicht für die aktuelle Phase aus.

## 7. Fazit / TL;DR
Eine erfolgreiche Keycloak-Integration erfordert nicht nur korrekte Zugangsdaten, sondern auch eine exakte Übereinstimmung der Infrastruktur-Objekte (Realm, Client-ID, Secret, Roles) mit der Backend-Konfiguration.
