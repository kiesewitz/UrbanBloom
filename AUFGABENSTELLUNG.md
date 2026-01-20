# Projektaufgabe: Diskrete Adaption und Erweiterung der "School Library" Referenzarchitektur

---

## 1. Einleitung & Szenario
Ihr Team übernimmt eine bestehende, professionelle Referenzarchitektur für ein Schulbibliothekssystem. Diese Architektur enthält bereits einen funktionierenden "Vertical Slice" für **Registrierung und Authentifizierung** (basierend auf Keycloak, Spring Boot und Flutter).

**Eure Mission:**
Ihr müsst diese Basis-Architektur auf euren spezifischen Anwendungsfall im Projekt adaptieren, verstehen, konfigurieren und die Qualitätssicherung automatisieren. Ziel ist es nicht, alles neu zu schreiben, sondern bestehende Industriestandards zu nutzen und zu erweitern.

---

## 2. Team-Rollen & Zusammensetzung
*   **Backend & DevOps (1-2 Personen):** Fokus auf Spring Boot, Keycloak, Docker, CI/CD Pipeline.
*   **Web-Frontend (2 Personen):** Fokus auf Admin-Oberfläche (Vue/React/Angular), Anbindung an Keycloak.
*   **Mobile-Frontend (2 Personen):** Fokus auf Schüler-App (Flutter), Adaption der bestehenden Auth-Logik.
*   **Scrum Master / PM (1-2 Personen):** Koordination, Sprints, Dokumentation, Sicherstellung der Akzeptanzkriterien.

---

## 3. Konkrete Aufgabenpakete

### A. Infrastruktur & Identity Management (Alle + Backend)
*   **Initial Setup:** Starten Sie den bereitgestellten Stack mittels `docker-compose`. Analysieren Sie die Services (Postgres, Keycloak).
*   **Keycloak Adaption:**
    *   Der Referenz-Realm (`realm-export.json`) ist auf die "School Library" zugeschnitten.
    *   **Aufgabe:** Erstellen oder konfigurieren Sie einen Realm passend zu *eurer* spezifischen Domäne.
    *   Definieren Sie eigene Rollen (z.B. `LIBRARIAN`, `STUDENT`, `TEACHER`) und Mappers.
    *   Passen Sie die Clients für Backend, Web und Mobile an.
    *   Exportieren Sie die neue Konfiguration sauber als JSON für das Repository.

### B. Backend Adaption (Backend Team)
*   **Start-Konfigurationen verstehen:** Analysieren Sie die vorhandenen Profile (`dev`, `test`).
*   **Konfigurations-Management:**
    *   Im Projekt werden aktuell **`.properties`**-Dateien verwendet (z.B. `application.properties`, `application-dev.properties`).
    *   **Aufgabe:** Passen Sie diese Dateien an eure Keycloak-URLs und Secrets an.
    *   *Optional:* Ihr könnt diese auf das modernere **YAML**-Format (`application.yml`) migrieren, wenn ihr die hierarchische Struktur bevorzugt.
    *   Stellen Sie sicher, dass die Authentifizierung mit eurem *neuen* Keycloak-Realm funktioniert.

### C. Mobile Frontend (Flutter Team)
*   **Konfiguration & Environments:**
    *   Untersuchen Sie das Handling der Environments (Dev, Test, Mock).
    *   Stellen Sie sicher, dass die App gegen den **Mock-Server** (lokal) laufen kann, ohne dass das echte Backend läuft.
*   **Design & Identity:**
    *   Ändern Sie das Branding (Farben, Name, Logo) der App passend zu eurem Projekt.
    *   Verbinden Sie die App mit dem neu konfigurierten Keycloak.
*   **Mocking:**
    *   Erweitern Sie den Mock-Server (oder die lokalen Mock-Daten im Code) um ein Szenario, das für euren Use-Case spezifisch ist.

### D. Web Frontend (Admin Team)
*   **Integration:**
    *   Setzen Sie das Admin-Dashboard auf (Technologie gem. Vorgabe/Wahl).
    *   Implementieren Sie den Login-Flow (OIDC/OAuth2) gegen den Keycloak.
*   **Docker:**
    *   Sorgen Sie dafür, dass das Web-Frontend lokal im Docker-Verbund (via `docker-compose`) gestartet werden kann (analog zum bereitgestellten Setup).

### E. CI/CD & Qualitätssicherung (DevOps / Alle)
*   **Branch Protection:**
    Richten Sie im Repository (GitHub/GitLab) Regeln ein, die verhindern, dass direkt auf `main` gepusht wird. Es muss immer via Pull Request (PR) gearbeitet werden.
*   **Backend Workflow:**
    Erstellen Sie eine GitHub Action (oder GitLab CI), die bei jedem PR:
    1.  Das Projekt baut (Build).
    2.  Die Unit- und Integrationstests ausführt.
    3.  Den PR blockiert, wenn Tests fehlschlagen.
*   **Frontend Workflow (Mobile & Web):**
    Erstellen Sie Workflows, die bei jedem PR:
    1.  Linting durchführen (Code-Qualität).
    2.  Tests ausführen (falls vorhanden).
    3.  Prüfen, ob das Projekt kompiliert.

---

## 4. Abgabekriterien (Definition of Done)

1.  **Repository:** Code ist sauber, keine Secrets im Code committed (nutzen Sie `.env` Dateien oder Secrets im Repo-Setup).
2.  **Lauffähigkeit:** Der Befehl `docker-compose up` startet den *angepassten* Stack (DB, Keycloak, Backend) fehlerfrei.
3.  **Login Flow:**
    *   Ein User kann sich in der Mobile App registrieren, einloggen und bekommt eine Verifzierungsmail, sowie kann sein Passwort zurücksetzen
    *   Ein Admin kann sich im Web-Frontend einloggen.
    *   Die Token werden vom Backend korrekt validiert (Signaturprüfung).
4.  **Mock Mode:** Die Mobile App startet im Mock-Modus und zeigt Daten an, ohne dass das Backend läuft.
5.  **CI/CD:** Ein fehlerhafter PR (z.B. Syntaxfehler oder fehlgeschlagener Test) wird von der Pipeline rot markiert und darf nicht gemerged werden.

---

## 5. Hilfestellung & Tipps für die Projektleiter
*   Nutzen Sie die vorhandene Dokumentation im Ordner `docs/` intensiv.
*   Verteilen Sie die Aufgaben so, dass die Integration (Keycloak) früh getestet wird. Wenn der Login nicht geht, steht das gesamte Team still.
*   Planen Sie Sprints so, dass zuerst die *Umgebung* steht, bevor neue Features gebaut werden.
