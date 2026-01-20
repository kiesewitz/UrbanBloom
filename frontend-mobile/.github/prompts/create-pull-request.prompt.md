---
name: Create Pull Request
description: Leitfaden zur Erstellung einer strukturierten Pull-Request-Beschreibung
agent: 'agent'
tools: ['execute/getTerminalOutput', 'execute/runTask', 'execute/getTaskOutput', 'execute/runInTerminal', 'execute/runTests', 'read/terminalSelection', 'read/terminalLastCommand', 'edit/createFile', 'edit/editFiles', 'search', 'web', 'github.vscode-pull-request-github/copilotCodingAgent', 'github.vscode-pull-request-github/issue_fetch', 'github.vscode-pull-request-github/suggest-fix', 'github.vscode-pull-request-github/searchSyntax', 'github.vscode-pull-request-github/doSearch', 'github.vscode-pull-request-github/renderIssues', 'github.vscode-pull-request-github/activePullRequest', 'github.vscode-pull-request-github/openPullRequest']
---

## Ziel

- Erzeuge eine vollständige, aussagekräftige Pull-Request-Beschreibung basierend auf dem aktuell behandelten GitHub-Issue.
- Dokumentiere: umgesetzte Anforderungen, teilweise oder nicht umgesetzte Anforderungen (mit Begründung), relevante Code-Referenzen, Tests, offene Punkte und Follow-up-Tasks.
- Wenn alle Akzeptanzkriterien erfüllt sind, füge "Closes #<issue_number>" ein, sodass das Issue beim Merge automatisch geschlossen wird. Andernfalls verwende "Related to #<issue_number>".
- Erstelle den Pull Request über die GitHub CLI mit dem generierten Titel und der Beschreibung.

Eingabe-Variablen

- `<owner>`: Repository-Besitzer (z. B. ukondert)
- `<repo>`: Repository-Name (z. B. template-bmad_method_ext)
- `<issue_number>`: Nummer des Issues
- `<branch>`: Quelle für den PR (Feature-Branch)
- `base`: Ziel-Branch (Standard: `main` oder `origin/main`)

Vorgehen (ausführbar)

1. Issue lesen

   - GitHub CLI:
     `gh issue view <issue_number> --repo <owner>/<repo> --json number,title,body,labels,comments`
   - Oder API:
     `curl -H "Authorization: token $GITHUB_TOKEN" https://api.github.com/repos/<owner>/<repo>/issues/<issue_number>`

2. Geänderte Dateien ermitteln

   - Basis aktualisieren: `git fetch origin`
   - Dateien zwischen `base` und Branch auflisten:
     `git diff --name-only origin/<base>...HEAD`

3. Diffs & Suche

   - Voller Diff: `git --no-pager diff origin/<base>...HEAD`
   - Suche in geänderten Dateien nach Schlüsselwörtern aus dem Issue:
     ```bash
     changed=$(git diff --name-only origin/<base>...HEAD)
     git grep -n "Schlüsselbegriff|andererBegriff" -- $changed
     ```
   - Alternative Tools: `rg` (ripgrep), `git grep`, PowerShell `Select-String`.

4. Anforderungen extrahieren und abgleichen

   - Extrahiere Akzeptanzkriterien / Anforderungen aus dem Issue-Text als nummerierte Liste.
   - Für jedes Kriterium erzeuge:
     - Status: `Implementiert` / `Teilweise` / `Nicht implementiert`
     - Kurze Begründung (falls nicht/teilweise)
     - Code-Referenz: `path/to/file` und optional `#Lstart-Lend`
     - Falls nötig: empfohlener Scope für Follow-up-PR

5. Tests & Verifikation

   - Führe relevante Tests/Checks aus (projektabhängig, Beispiele):
     - Python: `pytest -q`
     - Node: `npm test` oder `yarn test`
     - Java: `mvn test` oder `./gradlew check`
   - Sammle Test-Outputs und fasse kurze Ergebnisse zusammen.

6. Entscheidung zur Issue-Schließung
   - Wenn alle Kriterien `Implementiert` → `close_marker = "Closes #<issue_number>"`.
   - Sonst → `close_marker = "Related to #<issue_number>"`.
   - Platziere `close_marker` sichtbar in der PR-Beschreibung (automatisches Schließen beim Merge erfolgt nur bei `Closes`).

PR-Beschreibung (Template)

- Title: `<Typ>: Kurze Beschreibung — [optional] closes #<issue_number>` (oder `relates`)
- Body:
  - Kurz-Übersicht: 1–2 Sätze, was geändert wurde.
  - Bezug: `Closes #<issue_number>` oder `Related to #<issue_number>` (laut Entscheidung).
  - Umgesetzte Anforderungen:
    1. [Implementiert] Kriterium 1 — Code: `path/to/file` (`#Lstart-Lend`)
    2. [Teilweise] Kriterium 2 — Offener Anteil: ...
    3. [Nicht implementiert] Kriterium 3 — Grund + ToDos
  - Relevante Dateien mit Kurzbeschreibung (Liste)
  - Tests: Commands + Ergebnis (Kurzfassung)
  - Offene Punkte / Follow-up-PRs
  - Checkliste (Akzeptanzkriterien):
    - [x] Kriterium 1 — Implementiert (`path/to/file`)
    - [ ] Kriterium 2 — Nicht implementiert (Grund)
    - [~] Kriterium 3 — Teilweise

Beispiel: PR-Erstellung (GitHub CLI)

1. Erzeuge `pr_body.md` mit inhaltlicher PR-Beschreibung (inkl. `Closes #<issue_number>` falls zutreffend).
2. Zeige den erstellten PR-Beschreibungstext zur Überprüfung an und frage nach Bestätigung.
3. Erstelle PR:
   `gh pr create --repo <owner>/<repo> --base <base> --head <branch> --title "TITEL HIER" --body-file pr_body.md`

Hinweise zum Stil

- Sachlich, knapp, nachvollziehbar.
- Jede Nicht- oder Teilumsetzung kurz begründen und priorisieren.
- Verweise auf Code-Dateien mit Pfaden und Zeilenbereichen.

Optional: Automatisierungs-Pseudo

- Lese Issue → parse Anforderungen
- Bestimme geänderte Dateien → suche Stichwörter
- Mappe Anforderungen auf Implementierung mit Referenzen
- Entscheide `Closes` vs `Related to`
- Schreibe `pr_body.md` und führe `gh pr create` aus

Ende.
