**GreenMail + Keycloak (Testcontainers) — Lessons Learned**

**Problemübersicht:**
- Keycloak in einem Testcontainers-Container hat während der Integrationstests versucht, Verifizierungs-E-Mails zu senden, konnte aber keinen SMTP-Server erreichen. Die ursprüngliche Test-Ansatz nutzte eine auf dem Host gestartete GreenMail-Instanz bzw. `host.testcontainers.internal` in der Realm-Konfiguration, was in unserer Umgebung (Windows + Docker Desktop / Testcontainers) zu Namensauflösungs- und Erreichbarkeitsproblemen führte.

**Kernursachen:**
- **host.testcontainers.internal nicht zuverlässig**: Innerhalb des Keycloak-Containers war `host.testcontainers.internal` nicht auflösbar/erreichbar in unserer Umgebung → UnknownHostException / MailConnectException.
- **Unterschied Host vs Container-Netzwerk**: Keycloak läuft in einem Container; ein SMTP-Server, der auf dem Host oder außerhalb des gemeinsamen Testcontainers-Netzwerks läuft, ist nicht automatisch erreichbar.
- **POP/SMTP Auth und Benutzer**: GreenMail als Container benötigt ggf. konfigurierte Benutzer/Passwörter, wenn die Tests per POP3/SMTP authentifizieren wollen.

**Umgesetzte Lösung:**
- Starte GreenMail selbst als Testcontainers-Container (z. B. `greenmail/standalone`) und hänge ihn an dasselbe Testcontainers-`Network` wie Keycloak.
- Gebe dem GreenMail-Container eine Network-alias (z. B. `greenmail`) und setze in `keycloak-realm-test.json` unter `smtpServer.host` genau diesen Alias (`greenmail`).
- Exportiere die benötigten Ports (SMTP 3025, POP3 3110) und konfiguriere bei Bedarf Test-User via `GREENMAIL_USERS=address:password` damit POP3-Authentifizierung im Test funktioniert.
- Passe den Integrationstest an: verwende Testcontainers `GenericContainer` für GreenMail, warte bis beide Container laufen, und hole E‑Mails via POP3 (mapped port) aus dem GreenMail-Container zur Verifikation.

**Konkrete Änderungen (Referenzen):**
- **Realm:** See file: [backend/module-user/src/test/resources/keycloak-realm-test.json](backend/module-user/src/test/resources/keycloak-realm-test.json#L1-L200) — SMTP-Host auf `greenmail` gesetzt.
- **Test:** `UserRegistrationIntegrationTest` wurde so angepasst, dass GreenMail als Testcontainer gestartet wird, ein gemeinsames `Network` verwendet wird und die Prüfung der empfangenen Mail per POP3 gegen den GreenMail-Container erfolgt: [backend/module-user/src/test/java/com/schoollibrary/user/integration/UserRegistrationIntegrationTest.java](backend/module-user/src/test/java/com/schoollibrary/user/integration/UserRegistrationIntegrationTest.java#L1-L220).

**Pragmatische Anpassungen / Robustheit:**
- Die Keycloak-Adapter-Logik wurde so verändert, dass Ausfälle beim Mailversand (z. B. SMTP nicht erreichbar) nicht die gesamte Registrierung fehlschlagen lassen — das macht Tests robuster gegen Übergangsfehler.

**Lessons / Empfehlungen:**
- **Prefer containerized test services:** Starte testabhängige Infrastruktur (SMTP, DB, etc.) als Testcontainers-Container und verbinde sie per Netzwerk, statt „host-only“ Dienste zu erwarten.
- **Network aliases statt host.testcontainers.internal**: Verwende `withNetworkAliases("alias")` und referenziere diesen Alias in Import-/Konfigurationsdateien; das ist portabler zwischen OS/CI-Umgebungen.
- **Expose & use mapped ports for test assertions:** Zum Abholen von E‑Mails aus dem Testprozess (auf dem Host) verwende `getMappedPort(...)` und die vom Container gelieferten Host/Port-Kombinationen.
- **Set up mail users when authenticating:** Lege Testkonten in GreenMail an (z. B. über `GREENMAIL_USERS`) wenn POP3/IMAP/SMTP-Auth in Tests verwendet wird.
- **Fail-safe production behaviour:** Produktion und Tests haben unterschiedliche Anforderungen — in Tests ist es sinnvoll, Mail-Fehler zu beobachten und gleichzeitig den Hauptfluss (z. B. Nutzeranlage) nicht zu blockieren.

**Alternative Ansätze:**
- Wenn Testcontainers nicht möglich ist, kann man GreenMail lokal starten und Keycloak so konfigurieren, dass es die Host-IP verwendet — das ist jedoch weniger stabil und CI-abhängig (erfordert z. B. Docker-in-Docker oder spezielle DNS/host-routing).

**Wie lokal/CI ausführen:**
```
cd backend
mvn -pl module-user test -Dtest=UserRegistrationIntegrationTest
```

**Kurzfazit:**
- Containerisiertes GreenMail im selben Testcontainers-Netzwerk ist der zuverlässigste und reproduzierbare Weg, um E‑Mail-Flows zwischen Keycloak und der App in Integrationstests zu prüfen. Gleichzeitig macht eine defensive Fehlerbehandlung im Mail-Adapter die Tests robuster.
