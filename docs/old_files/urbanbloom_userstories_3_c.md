## UrbanBloom – User Stories (nach 3‑C‑Modell)

### Epic 1 – Gamified Motivation (11 Punkte)

**User Story 1.1 – Punkte & Auszeichnungen**  
**Card:** Als Bürger möchte ich für meine Begrünungsaktionen Punkte und Auszeichnungen erhalten, um mein Engagement sichtbar zu machen und mich mit anderen vergleichen zu können.  
**Conversation:** Hr Schaar erklärte, dass Auszeichnungen automatisch bei bestimmten Punkteschwellen vergeben werden sollen. Hr Leitner schlug zusätzlich Sonderpreise für herausragende Projekte vor.  
**Confirmation (Akzeptanzkriterien):** Gegeben sei ein Benutzer mit erfasster Aktion. Wenn die Aktion registriert wird, dann werden Punkte gutgeschrieben und ggf. eine Auszeichnung angezeigt.  
**Story Points:** 5

**User Story 1.2 – Ranglisten**  
**Card:** Als Bürger möchte ich Ranglisten (individuell, Bezirk, Stadt) sehen, um meinen Fortschritt mit anderen vergleichen zu können.  
**Conversation:** Hr Maksic schlug vor, Ranglisten wöchentlich zu aktualisieren. Hr Gierer wünschte eine Filterfunktion nach Stadtteilen.  
**Confirmation:** Gegeben sei ein Benutzer. Wenn die Rangliste geöffnet wird, dann erscheinen aktuelle Platzierungen mit Filteroptionen.  
**Story Points:** 3

**User Story 1.3 – Persönlicher Fortschritt**  
**Card:** Als Benutzer möchte ich meinen persönlichen Fortschritt grafisch sehen, um meine Entwicklung zu verfolgen.  
**Conversation:** Hr Pirkmann schlug ein Balkendiagramm für wöchentliche Begrünungsaktionen vor. Hr Schaar empfahl, Badges grafisch hervorzuheben.  
**Confirmation:** Gegeben sei ein Benutzer. Wenn das Profil geöffnet wird, dann zeigt ein Diagramm die Entwicklung der Punkte und Auszeichnungen.  
**Story Points:** 3

---

### Epic 2 – Smart Green Tracking (15 Punkte)

**User Story 2.1 – GPS-Tracking & QR-Code-Scan**  
**Card:** Als Benutzer möchte ich Begrünungsaktionen über GPS oder QR-Code erfassen, um meine Aktion schnell und genau zu registrieren.  
**Conversation:** Hr Maksic erklärte, dass jede Pflanzfläche eine eindeutige ID oder GPS-Position besitzen soll. Hr Gierer ergänzte, dass Scans auch offline möglich sein sollten.  
**Confirmation:** Gegeben sei eine App mit aktivierter Standortfunktion. Wenn eine Fläche markiert oder gescannt wird, dann wird sie gespeichert und synchronisiert.  
**Story Points:** 5

**User Story 2.2 – Manuelle Eingabe**  
**Card:** Als Benutzer möchte ich Begrünungsaktionen manuell eintragen, um nicht automatisch erfasste Aktionen hinzuzufügen.  
**Conversation:** Hr Schaar betonte, dass Art der Pflanze, Datum und Ort angegeben werden müssen. Hr Leitner forderte eine automatische Plausibilitätsprüfung.  
**Confirmation:** Gegeben sei das Eingabeformular. Wenn alle Pflichtfelder korrekt ausgefüllt sind, dann wird die Aktion gespeichert und Punkte vergeben.  
**Story Points:** 3

**User Story 2.3 – Push-Benachrichtigungen**  
**Card:** Als Benutzer möchte ich Push-Benachrichtigungen erhalten, um an regelmäßige Pflegeaktionen erinnert zu werden.  
**Conversation:** Hr Pirkmann schlug Erinnerungsintervalle vor. Hr Gierer empfahl, Benachrichtigungen deaktivierbar zu machen.  
**Confirmation:** Gegeben sei ein Benutzer mit aktivierten Push-Benachrichtigungen. Wenn ein Intervall eingestellt ist, dann wird die Erinnerung zum gewählten Zeitpunkt gesendet.  
**Story Points:** 2

**User Story 2.4 – Offline-Modus**  
**Card:** Als Benutzer möchte ich Begrünungsaktionen auch offline erfassen, um sie später automatisch hochladen zu lassen.  
**Conversation:** Hr Maksic betonte die Wichtigkeit für ländliche Gebiete ohne stabile Verbindung. Hr Schaar erklärte, dass Aktionen lokal gespeichert werden sollen.  
**Confirmation:** Gegeben sei kein Internet. Wenn eine Aktion gespeichert wird, bleibt sie lokal erhalten und wird später synchronisiert.  
**Story Points:** 5

---

### Epic 3 – Admin Insights & Analytics (14 Punkte)

**User Story 3.1 – Fortschritt anzeigen**  
**Card:** Als Stadtverwaltung möchte ich den Fortschritt einzelner Bezirke und Gruppen sehen, um Maßnahmen gezielt fördern zu können.  
**Conversation:** Hr Gierer erklärte, dass Diagramme pro Bezirk hilfreich wären. Hr Leitner empfahl Filterfunktionen nach Zeitraum.  
**Confirmation:** Gegeben sei ein Admin mit Dashboard-Zugriff. Wenn ein Bezirk ausgewählt wird, dann zeigt das System Fortschrittsdiagramme.  
**Story Points:** 4

**User Story 3.2 – Bezirke vergleichen**  
**Card:** Als Admin möchte ich Begrünungsquoten verschiedener Bezirke vergleichen, um Wettbewerbe und Kampagnen zu steuern.  
**Conversation:** Hr Schaar empfahl eine Rangliste der aktivsten Bezirke. Hr Pirkmann wünschte einen CSV-Export.  
**Confirmation:** Gegeben sei ein Admin mit Dashboard-Zugriff. Wenn die Übersicht geöffnet wird, erscheinen Bezirksvergleiche mit Exportoption.  
**Story Points:** 5

**User Story 3.3 – Challenges erstellen**  
**Card:** Als Administrator möchte ich thematische Challenges erstellen (z. B. „100 neue Pflanzen im Bezirk Mitte“), um Aktionen zu fördern.  
**Conversation:** Hr Maksic schlug vor, dass Challenges Titel, Zeitraum und Zielwert enthalten. Hr Gierer ergänzte, dass der Fortschritt automatisch berechnet werden soll.  
**Confirmation:** Gegeben sei ein Admin. Wenn eine neue Challenge angelegt wird, erscheint sie in der Übersicht aktiver Wettbewerbe.  
**Story Points:** 5

---

### Cross‑Epic Stories (11 Punkte)

**User Story T1 – Sichere App‑Schnittstelle**  
**Card:** Als Entwickler möchte ich eine sichere REST‑API mit Authentifizierung implementieren, um App‑ und Web‑Zugriffe zu schützen.  
**Conversation:** Hr Schaar erklärte, dass jede Anfrage mit Token und Zeitstempel abgesichert werden soll. Hr Leitner ergänzte, dass Logfiles für sicherheitsrelevante Aktionen nötig sind.  
**Confirmation:** Gegeben sei ein API‑Schlüssel. Wenn eine Anfrage gesendet wird, dann wird sie authentifiziert und geprüft.  
**Story Points:** 8

**User Story T2 – Datensicherheit**  
**Card:** Als Benutzer möchte ich wissen, dass meine persönlichen Daten geschützt sind, um der App zu vertrauen.  
**Conversation:** Hr Gierer forderte Ende‑zu‑Ende‑Verschlüsselung. Hr Pirkmann ergänzte, dass Daten anonymisiert ausgewertet werden müssen.  
**Confirmation:** Gegeben sei ein Benutzerprofil. Wenn Daten gespeichert oder übertragen werden, dann erfolgt dies verschlüsselt und anonymisiert.  
**Story Points:** 3

