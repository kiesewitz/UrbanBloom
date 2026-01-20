# Workshop-Protokoll: Digitale Schulbibliothek
- **Datum:** 2025-12-16
- **Thema:** Anforderungserhebung und Ideensammlung für die neue Bibliotheks-Software
- **Teilnehmer:**
    - Moderator (KI)
    - Frau Müller (Bibliothekarin)
    - Lisa & Tom (Schülervertreter)
    - Herr Weber & Frau Klein (Lehrkräfte)
    - Frau Direktor (Schulleitung/Schuladmin)
    - Herr Tech (IT-Admin)

## Ablauf und Diskussion

### 1. Begrüßung und Zielsetzung
Der Moderator stellt das Ziel vor: Eine moderne, digitale Lösung für die Schulbibliothek, die den Verwaltungsaufwand minimiert und die Nutzung für Schüler und Lehrer attraktiver macht.

### 2. Themenblock: Mobile App & Schüler-Experience
**Lisa (Schüler):** "Für uns ist wichtig, dass wir nicht extra in die Bib rennen müssen, um zu sehen, ob ein Buch da ist. Eine App, wo ich sofort sehe 'Verfügbar: Ja/Nein' und es direkt reservieren kann, wäre super."
**Tom (Schüler):** "Genau. Und eine Erinnerung am Handy, bevor die Frist abläuft. Niemand schaut in seine E-Mails. Push-Nachrichten wären viel besser."
**Frau Müller (Bibliothekarin):** "Push-Nachrichten fände ich auch gut, wenn das technisch geht. Die E-Mails werden oft ignoriert."
**Herr Tech (IT-Admin):** "Push-Notifications sind machbar, aber wir müssen Datenschutz beachten. Die App darf nicht zu viele Daten sammeln. Anmeldung über unser bestehendes Schul-Portal (SSO) ist Pflicht."

**Ergebnisse:**
- Echtzeit-Verfügbarkeitsanzeige in der App.
- Reservierungsfunktion ("Click & Collect").
- Push-Benachrichtigungen für Fristen und Abholbereit-Status.
- Login via Schul-SSO.

### 3. Themenblock: Unterricht & Lehrkräfte
**Herr Weber (Lehrer):** "Ich brauche oft Klassensätze für den Deutschunterricht. Ich muss Monate im Voraus wissen, ob 'Faust' für die 10b im März verfügbar ist. Eine Kalenderansicht oder langfristige Vormerkung wäre hilfreich."
**Frau Klein (Lehrer):** "Und Empfehlungslisten. Wenn ich sage 'Lest was zum Thema Klimawandel', wäre es toll, wenn ich eine Liste in der App für meine Klasse freigeben könnte."
**Frau Müller:** "Das mit den Klassensätzen ist aktuell ein Chaos. Ein Buchungssystem für Lehrer wäre eine riesige Erleichterung."

**Ergebnisse:**
- Verwaltung und langfristige Buchung von Klassensätzen.
- Erstellung von kuratierten Listen (Empfehlungen) durch Lehrer.
- Kalenderansicht für Verfügbarkeit (besonders für Lehrer).

### 4. Themenblock: Administration & Prozesse
**Frau Direktor:** "Mir ist wichtig, dass wir den Überblick behalten. Wer hat was wie lange? Und das Mahnwesen muss effizienter werden, aber fair bleiben. Keine versteckten Kosten für Eltern, aber klare Konsequenzen."
**Frau Müller:** "Ich brauche Unterstützung beim Inventarisieren. Barcode-Scanner ist ein Muss. Und das Aussortieren alter Bücher – das System soll mir vorschlagen, was seit 5 Jahren niemand mehr ausgeliehen hat."
**Herr Tech:** "Wir brauchen eine saubere Trennung der Daten. Schüler verlassen die Schule. Die Accounts müssen automatisch deaktiviert/anonymisiert werden, wenn sie aus dem zentralen Schulverzeichnis fliegen."

**Ergebnisse:**
- Effizientes Mahnwesen (automatisiert, stufig).
- Barcode-Scanner-Integration für schnelles Ausleihen/Rückgeben/Inventarisieren.
- Automatisierte Vorschläge für Aussonderung (Ladenhüter).
- Automatische Nutzerverwaltung (Sync mit Schulverzeichnis/LDAP).

### 5. Themenblock: Technik & Infrastruktur
**Herr Tech:** "Wir setzen auf Flutter für Mobile und React/TS für Web. Backend Spring Boot. Das passt gut in unsere Infrastruktur. Datenbank Postgres ist auch Standard hier. Wichtig ist mir die Wartbarkeit. Wer pflegt das, wenn die Schüler, die es bauen, weg sind?"
**Frau Direktor:** "Guter Punkt. Es muss eine saubere Dokumentation geben."

**Ergebnisse:**
- Technologie-Stack bestätigt (Flutter, React, Spring Boot, Postgres).
- Hohe Anforderung an Dokumentation und Wartbarkeit.

### 6. Priorisierung (Stimmungsbild)
Jeder Teilnehmer durfte 3 Punkte für seine wichtigsten Features vergeben.

1.  **Echtzeit-Suche & Reservierung (App):** 6 Stimmen (Schüler, Lehrer, Bib)
2.  **Barcode-Scan & Schnelle Ausleihe (Admin):** 5 Stimmen (Bib, Lehrer, IT)
3.  **Mahnwesen & Erinnerungen (Push/Mail):** 4 Stimmen (Schüler, Bib, Direktor)
4.  **Klassensatz-Verwaltung:** 3 Stimmen (Lehrer, Bib)
5.  **SSO-Integration:** 3 Stimmen (IT, Direktor, Schüler)

## Zusammenfassung der Anforderungen aus dem Workshop
- **Must-Have:** App für Suche/Reservierung, Admin-Web für Ausleihe/Bestand (Scan), SSO, Push-Erinnerungen.
- **Should-Have:** Klassensatz-Management, Empfehlungslisten für Lehrer, Aussonderungs-Vorschläge.
- **Could-Have:** Kalenderansicht für Verfügbarkeit, detaillierte Statistiken für Schulleitung.
