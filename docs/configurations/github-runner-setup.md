# GitHub Self-Hosted Runner Installation & CI/CD Setup

Dieses Dokument beschreibt die Einrichtung eines lokalen GitHub Runners auf einem Windows-System, um ein automatisiertes Deployment des Digital School Library Projekts (Backend, Frontend, Keycloak) auf eine lokale Testinstanz zu ermöglichen.

## 1. Voraussetzungen

- **Betriebssystem**: Windows (mit PowerShell 5.1 oder höher)
- **Docker Desktop**: Muss installiert sein und laufen.
- **GitHub Repository**: Schreibzugriff auf das Repository (Settings-Rechte).
- **Hardware**: Ausreichend RAM (empfohlen 8GB+), da Keycloak und Spring Boot parallel laufen.

## 2. GitHub Konfiguration (Token generieren)

Um den Runner mit deinem Repository zu verbinden, benötigst du einen Konfigurations-Token:

1. Navigiere in deinem Browser zu deinem GitHub Repository.
2. Klicke auf **Settings** (oben rechts).
3. Wähle in der linken Seitenleiste **Actions** > **Runners**.
4. Klicke auf den Button **New self-hosted runner**.
5. Wähle **Windows** als Betriebssystem (Runner Image).

## 3. Installation auf dem Windows-PC

Öffne eine **PowerShell als Administrator** und führe die folgenden Schritte aus (ersetze dabei die Platzhalter durch die Werte von der GitHub-Seite):

### A. Verzeichnis erstellen und Download
```powershell
# Erstelle einen dedizierten Ordner für den Runner
mkdir C:\github-actions-runner; cd C:\github-actions-runner

# Lade das Runner-Paket herunter (Version kann variieren, siehe GitHub-Seite)
Invoke-WebRequest -Uri https://github.com/actions/runner/releases/download/v2.321.0/actions-runner-win-x64-2.321.0.zip -OutFile actions-runner-win-x64.zip

# Entpacke das Paket
Add-Type -AssemblyName System.IO.Compression.FileSystem ; [System.IO.Compression.ZipFile]::ExtractToDirectory("$PWD\actions-runner-win-x64.zip", "$PWD")
```

### B. Konfiguration
Kopiere den exakten `config.cmd`-Befehl von der GitHub-Seite. Er sieht ungefähr so aus:

```powershell
./config.cmd --url https://github.com/DEIN_USER/DEIN_REPO --token DEIN_GEHEIMER_TOKEN
```

**Einstellungen während der Konfiguration:**
- **Runner Group**: Einfach [Enter] (Standard).
- **Name of Runner**: Gib einen Namen an (z. B. `local-test-server`).
- **Runner Labels**: Einfach [Enter] (Label `self-hosted` wird automatisch vergeben).
- **Work Folder**: Einfach [Enter] (`_work`).

### C. Windows Long Path Support aktivieren (KRITISCH!)

Windows hat standardmäßig ein Limit von 260 Zeichen für Dateipfade. Da der Runner in einem verschachtelten Verzeichnis arbeitet und Java-Projekte lange Paketnamen haben, **muss** Long Path Support aktiviert werden, sonst schlägt der Checkout fehl.

**Öffne eine PowerShell als Administrator** und führe aus:

```powershell
# Git Long Paths aktivieren
git config --system core.longpaths true
```

**Alternative: Windows-Registry (falls Git-Befehl nicht ausreicht):**

```powershell
# Long Paths in Windows aktivieren
New-ItemProperty -Path "HKLM:\SYSTEM\CurrentControlSet\Control\FileSystem" `
                 -Name "LongPathsEnabled" `
                 -Value 1 `
                 -PropertyType DWORD `
                 -Force
```

**Wichtig:** Nach der Aktivierung muss der Runner neu gestartet werden (falls bereits laufend).

### D. Als Windows-Dienst einrichten (Empfohlen)
Damit der Runner auch nach einem Neustart des PCs automatisch im Hintergrund startet:

```powershell
./svc.sh install
./svc.sh start
```

**Alternativ: Manueller Start für Tests**
Für erste Tests oder Debugging kannst du den Runner auch manuell starten:

```powershell
./run.cmd
```

Dies zeigt dir Live-Logs in der Konsole. Beende mit `Strg+C`.

## 4. Funktionsweise der Testinstanz

Die Testinstanz läuft **parallel** zu deiner normalen Entwicklungsumgebung. Die Ports sind nun **symmetrisch** konfiguriert (interner Container-Port = externer Host-Port), was Fehlkonfigurationen vermeidet:

| Komponente | Test-Port (Intern/Extern) | Entwicklungs-Port (Intern/Extern) |
| :--- | :--- | :--- |
| **Web Frontend** | `4000` | `3000` |
| **Spring Backend**| `9080` | `8080` |
| **Keycloak** | `9081` | `8081` |
| **Mailpit UI** | `9025` | `8025` |
| **Mailpit SMTP** | `9026` | `1025` |
| **PostgreSQL** | `6432` | `5432` |

### Konfigurations-Mechanismus (Infrastructure as Code)

Die Testinstanz nutzt die reguläre `docker-compose.yml` des Projekts, wird aber durch eine dynamisch generierte `.env` Datei parametrisiert.

1.  **Variablen-Substitution**: In der `docker-compose.yml` werden Platzhalter wie `${BACKEND_PORT:-8080}` sowohl für den Host-Port als auch für den internen Container-Port verwendet.
2.  **Dynamische `.env` Erzeugung**: Im `deploy`-Job der `deploy-test.yml` wird eine `.env` Datei auf dem Runner erstellt, die projektspezifische Werte für die Testumgebung setzt.
3.  **Frontend & Backend Modi**: 
    *   Das **Backend** lädt via `SPRING_PROFILES_ACTIVE=test` die `application-test.properties` (lauscht dort auf 9080).
    *   Das **Web-Frontend** wird via GitHub Actions mit `--mode test` gebaut (lauscht via Umgebungsvariable `PORT` auf 4000).
    *   Das **Mobile-Frontend** (lokal) nutzt `Environment.test`, um sich mit `localhost:9080` zu verbinden.
4.  **Deployment-Befehle**:
    ```yaml
    docker compose pull
    docker compose down -v --remove-orphans
    # Profile aktivieren (wichtig für startende Services) und Umgebungsvariablen nutzen
    docker compose up -d
    ```

### Isolierung
Durch das Setzen von `COMPOSE_PROJECT_NAME=school-library-test` in der CI/CD-Pipeline (`deploy-test.yml`) werden separate Docker-Container und Volumes erstellt. Deine lokalen Entwicklungsdaten in der Standard-Datenbank bleiben somit unangetastet.

## 5. Keycloak Konfiguration übertragen

Damit der Testserver (Keycloak) dieselben Realms, Clients und User hat wie deine lokale Entwicklungsumgebung, kannst du die Konfiguration exportieren und automatisch importieren lassen.

1.  **Exportiere deinen lokalen Realm:**
    *   Gehe in deine lokale Keycloak Admin Console (http://localhost:8081).
    *   Wähle den Realm `schoollibrary` aus.
    *   Gehe auf **Realm Settings** > **Action** (oben rechts) > **Partial Export**.
    *   Aktiviere **Includes groups and roles**.
    *   Speichere die Datei als `realm-export.json`.

2.  **Speichere die Datei im Projekt:**
    *   Lege die Datei im Ordner `config/keycloak/` ab (erstelle den Ordner, falls er nicht existiert).
    *   Pfad: `PROJEKT_ROOT/config/keycloak/realm-export.json`.

3.  **Wichtig:**
    *   Die Datei darf **nicht** mit committed werden, wenn sie echte User-Daten enthält (in Partial Export sind User meist nicht enthalten, prüfe das!).
    *   Für die CI/CD-Pipeline sollte eine "saubere" Test-Konfiguration (evtl. mit Test-Usern) verwendet werden.

Der Testserver liest diese Datei beim Start automatisch ein (konfiguriert in `docker-compose.yml`). 


**Hinweis zu Keycloak 26+:** Falls deine `realm-export.json` Scripte (z.B. Custom Mapper) enthält, muss das Feature `scripts` explizit aktiviert werden (siehe Fehlerbehebung).

### Wichtig: Client Secrets in CI/CD
In der CI/CD-Pipeline startet Keycloak bei jedem Lauf frisch. Wenn in der `realm-export.json` das Client Secret maskiert ist (`**********`), generiert Keycloak ein zufälliges neues Secret.
**Folge:** Das Backend (mit fix konfigurierem Secret) kann sich nicht verbinden.

**Lösung:**
Trage für die Testumgebung das Secret im Klartext in die `realm-export.json` ein (siehe Keycloak Setup Guide). Da es sich um Testdaten handelt, ist dies ein akzeptabler Kompromiss für die Stabilität der Tests.


## 6. Workflow manuell starten

Der Workflow kann auf zwei Arten gestartet werden:

### Via GitHub Web-UI
1. Gehe zu: `https://github.com/DEIN_USER/DEIN_REPO/actions`
2. Klicke links auf **"Build and Deploy to Local Machine"**
3. Klicke rechts auf **"Run workflow"**
4. Wähle den Branch aus (z.B. `main`)
5. Klicke auf **"Run workflow"**

### Via GitHub CLI
```bash
# Workflow vom main-Branch starten
gh workflow run deploy-test.yml

# Workflow von einem Feature-Branch starten
gh workflow run deploy-test.yml --ref feature/mein-branch
```

**Wichtig:** Der Workflow muss auf dem `main`-Branch existieren, damit er über die CLI gestartet werden kann. Workflows auf Feature-Branches können nur über die Web-UI gestartet werden.

## 6. Fehlerbehebung

### Docker Permission Denied
**Problem:** Der Runner kann nicht auf Docker zugreifen.

**Lösung:** Stelle sicher, dass Docker Desktop läuft und der Benutzer, unter dem der Runner-Dienst läuft, zur Gruppe `docker-users` gehört.

### Ports bereits belegt
**Problem:** `Error: bind: address already in use`

**Lösung:** Die Testinstanz nutzt andere Ports als die Entwicklungsumgebung. Falls ein Port (z.B. 9080) dennoch belegt ist:
1. Prüfe mit `netstat -ano | findstr :9080` (oder dem betroffenen Port), welcher Prozess ihn nutzt.
2. Der SMTP-Port für Mailpit in der Testumgebung wurde auf `9026` gelegt, um Konflikte mit dem Host (1025) zu vermeiden.
3. Passe die Ports in `.github/workflows/deploy-test.yml` an, falls nötig.

### Runner Offline / Nicht erreichbar
**Problem:** GitHub zeigt "Waiting for a runner to pick up this job"

**Lösungen:**
1. Prüfe in GitHub Settings > Actions > Runners, ob der Runner als "Idle" (grün) markiert ist
2. Falls "Offline": Starte den Runner-Dienst neu:
   ```powershell
   ./svc.sh stop
   ./svc.sh start
   ```
3. Falls manuell gestartet: Prüfe, ob `./run.cmd` noch läuft und "Listening for Jobs" anzeigt

### Filename too long (Git Checkout Fehler)
**Problem:** `unable to create file ... Filename too long`

**Ursache:** Windows Long Path Support ist nicht aktiviert oder der Runner verwendet einen veralteten Arbeitsbereich.

**Lösung 1:** Siehe Abschnitt 3.C - Git Long Paths aktivieren und Runner neu starten.

**Lösung 2 (Falls Fehler weiterhin besteht):**
Manchmal behält der Runner Cache-Dateien im alten Modus. Lösche den Arbeitsordner (`_work`) manuell:
1. Stoppe den Runner.
2. Lösche den Ordner `C:\github-actions-runner\_work` (oder wo immer er installiert ist).
3. Starte den Runner neu.

### Docker Cache Export Error
**Problem:** `ERROR: Cache export is not supported for the docker driver`

**Ursache:** Der Workflow nutzt GitHub Actions Cache, aber Docker Buildx ist nicht konfiguriert.

**Lösung:** Stelle sicher, dass in `.github/workflows/deploy-test.yml` der Schritt `docker/setup-buildx-action@v3` vorhanden ist (sollte bereits enthalten sein).

### Workflow startet nicht vom Feature-Branch
**Problem:** `HTTP 404: workflow not found on the default branch`

**Ursache:** GitHub CLI kann Workflows nur vom `main`-Branch starten.

**Lösungen:**
1. Nutze die GitHub Web-UI zum Starten (siehe Abschnitt 5)
2. Merge den Feature-Branch in `main`
3. Erstelle einen Pull Request (triggert den Workflow automatisch)

### Shell-Fehler / Bash nicht gefunden
**Problem:** `shell: C:\Windows\system32\bash.EXE ... No such file or directory` oder Syntax-Fehler bei `$(command)`.

**Ursache:** Der Runner versucht Bash-Befehle auf einem Windows-System auszuführen.

**Lösung:**
Stelle sicher, dass im Workflow (`.github/workflows/deploy-test.yml`) für den Windows-Job die Shell explizit auf PowerShell gesetzt ist:

```yaml
jobs:
  deploy:
    runs-on: self-hosted
    steps:
      - name: Deploy
        shell: pwsh  # <--- Wichtig für Windows!
        run: |
           # PowerShell Syntax verwenden
           Add-Content -Path .env -Value "..."
```

### Container starten nicht / Health Check Failed
**Problem:** Backend oder Keycloak bleiben im Status "starting" oder "unhealthy"

**Diagnose:**
```powershell
# Logs des Backend-Containers ansehen
docker logs school-library-test-backend-1

# Logs von Keycloak ansehen
docker logs school-library-test-keycloak-1
```

**Häufige Ursachen:**
- Datenbank nicht erreichbar (Keycloak wartet auf PostgreSQL)
- Zu wenig RAM (Keycloak benötigt mindestens 1GB)
- Falsche Umgebungsvariablen in der `.env`-Datei

### Backend Logfile Permission Error
**Problem:** `java.io.FileNotFoundException: logs/schoollibrary.log (No such file or directory)`

**Ursache:** Das Docker-Image hat im Arbeitsverzeichnis keinen Ordner `logs/` oder der User hat keine Schreibrechte.

**Lösung:**
Passe das `backend/Dockerfile` an, um den Ordner zu erstellen und die Rechte zu setzen:
```dockerfile
# Create logs directory and set ownership
RUN mkdir -p logs && chown spring:spring logs
```

### Mount-Verzeichnisse sind leer (z.B. realm-export.json fehlt)
**Problem:** Der Mount im Docker-Container wird als aktiv angezeigt, aber das Verzeichnis ist leer (z.B. `/opt/keycloak/data/import` ist leer).

**Ursache:** Der GitHub-Runner arbeitet auf einem spezifischen Commit-Stand. Wenn Dateien (wie die `realm-export.json`) lokal erstellt, aber noch nicht gepusht wurden, "sieht" der Runner diese Dateien nicht.

**Lösung:**
1. Prüfe, ob die Datei im Repository gepusht wurde.
2. Prüfe den Trigger in der `deploy-test.yml`. Falls kein `push`-Trigger existiert, musst du den Workflow nach dem Pushen **manuell neu starten** (`workflow_dispatch`), damit der Runner den neuesten Stand auscheckt.
3. Kontrolliere den Commit-SHA im Runner-Arbeitsverzeichnis (`git rev-parse HEAD`).

### Keycloak: Script upload is disabled
**Problem:** Keycloak bricht beim Import der `realm-export.json` mit der Fehlermeldung `ERROR: Script upload is disabled` ab.

**Ursache:** Seit Keycloak 18+ ist der Import von JavaScript-basierten Komponenten (Mappers, Policies) aus Sicherheitsgründen standardmäßig deaktiviert.

**Lösung:**
Aktiviere das Feature in der `docker-compose.yml` (Entweder per Umgebungsvariable oder Command):
```yaml
environment:
  KC_FEATURES: scripts
```

### Keycloak: Veraltete Umgebungsvariablen
**Problem:** Warnung im Log: `Environment variable 'KEYCLOAK_ADMIN' is deprecated`.

**Lösung:** Ersetze die veralteten Variablen durch die neuen `KC_`-Präfixe:
- `KEYCLOAK_ADMIN` -> `KC_BOOTSTRAP_ADMIN_USERNAME`
- `KEYCLOAK_ADMIN_PASSWORD` -> `KC_BOOTSTRAP_ADMIN_PASSWORD`

### Keycloak: Unhealthy Status beim Start
**Problem:** Keycloak wird als `unhealthy` markiert, obwohl der Container läuft. Dies führt dazu, dass abhängige Services (Backend) nicht starten.

**Ursache 1:** Die Health-Endpoints sind in Keycloak standardmäßig deaktiviert.
**Lösung 1:** Setze `KC_HEALTH_ENABLED: "true"` in der `docker-compose.yml`.

**Ursache 2:** Der Healthcheck-Befehl nutzt `curl`, welches in neueren Keycloak-Images (basierend auf UBI-Minimal) oft nicht vorinstalliert ist.
**Lösung 2:** Verwende einen robusteren Check via Bash (TCP-Probe), der ohne externe Tools auskommt:
```yaml
healthcheck:
  test: ["CMD-SHELL", "timeout 10s bash -c ':> /dev/tcp/127.0.0.1/${KEYCLOAK_PORT:-8081}' || exit 1"]
```

### Docker Compose Pfade unter Windows
**Problem:** Mounts werden nicht korrekt aufgelöst oder führen zu Fehlern.

**Ursache:** Die Variable `${PWD}` wird je nach Shell (CMD, PowerShell, Git Bash) auf Windows unterschiedlich oder gar nicht aufgelöst.

**Lösung:** Verwende in der `docker-compose.yml` immer relative Pfade mit `./` (z.B. `./config/keycloak`), da Docker Compose diese zuverlässig relativ zum Speicherort der YAML-Datei auflöst.

### Testinstanz prüfen
Um zu verifizieren, dass das Backend läuft:

```bash
# Health Check aufrufen
curl http://localhost:9080/actuator/health

# Erwartete Antwort:
# {"status":"UP"}
```

Für das Frontend: Öffne `http://localhost:4000` im Browser.

