# Interview: Bibliothekarin (Key Stakeholder)
- Datum: 2025-12-16
- Kontext: Digitalisierung der Schulbibliothek (Suche, Reservierung, Ausleihe, Rueckgabe, Mahnwesen)
- Teilnehmer: Interviewer (Requirements Engineer), Bibliothekarin (Key Stakeholder)

## Transkript (semi-strukturiert)
1. **Interviewer:** Wie laeuft der aktuelle Ausleihprozess ab?
   **Bibliothekarin:** Schueler kommen an den Tresen, nennen Titel oder Autor, ich suche in Excel und schaue im Regal. Ich trage Ausleihe manuell in eine Excel-Liste ein, gebe Rueckgabedatum mit und erinnere per Zettel oder E-Mail.
2. **Interviewer:** Welche Schritte kosten am meisten Zeit?
   **Bibliothekarin:** Suchen nach Verfuegbarkeit in mehreren Excel-Tabellen, Rueckverlaengerungen und Mahnungen manuell versenden, Medien im Regal finden, doppelte Eingaben (Inventar und Ausleihe getrennt).
3. **Interviewer:** Wie wird Verfuegbarkeit aktuell gepflegt?
   **Bibliothekarin:** Ein Tab fuer Bestand, einer fuer Ausleihen. Rueckgaben tragen wir ein, aber bei Stress passiert das zeitverzoegert. Deshalb stimmt die Verfuegbarkeit oft nicht.
4. **Interviewer:** Gibt es Reservierungen oder Vormerkungen?
   **Bibliothekarin:** Selten, dann fuehre ich handschriftliche Zettel. Keine digitale Vormerkung, keine automatische Benachrichtigung.
5. **Interviewer:** Welche Medienarten verwalten Sie?
   **Bibliothekarin:** Vor allem Buecher, dazu einige DVDs, Fachzeitschriften-Jahrgaenge, vereinzelt Klassensaetze (mehrere Exemplare eines Titels fuer Klassenarbeiten).
6. **Interviewer:** Wie haeufig werden Ausleihen getaetigt und von wie vielen Nutzerinnen/Nutzern?
   **Bibliothekarin:** Ca. 80–120 aktive Schueler pro Monat, 15–20 Lehrkraefte. Insgesamt 1.200–1.500 Ausleihen im Schuljahr.
7. **Interviewer:** Welche Regeln gelten fuer Ausleihe und Verlaengerung?
   **Bibliothekarin:** Standard: 3 Wochen fuer Schueler, 6 Wochen fuer Lehrkraefte. Zwei Verlaengerungen, wenn keine Vormerkung existiert. DVDs nur 1 Woche, keine Verlaengerung.
8. **Interviewer:** Wie handhaben Sie Mahnungen und Gebuehren?
   **Bibliothekarin:** Nach Rueckgabedatum verschicke ich E-Mail-Erinnerungen, spaeter Briefe ueber das Sekretariat. Gebuehren selten, eher Rueckgabe-Pflicht. Automatisches Stufen-Mahnwesen waere hilfreich.
9. **Interviewer:** Welche Suchkriterien brauchen die Nutzer?
   **Bibliothekarin:** Titel, Autor, Stichwort/Sachgebiet, ISBN, Sprache, Medienart, Standort/Signatur, Verfuegbarkeit, Erscheinungsjahr. Fuer Lehrkraefte: Klassensatz-Filter und Empfohlene-Lektuere-Listen.
10. **Interviewer:** Welche Daten erfassen Sie bei Neuzugaengen?
    **Bibliothekarin:** Titel, Autor, Verlag, ISBN, Ausgabejahr, Kategorie/Sachgruppe, Sprache, Signatur/Standort, Anzahl Exemplare, Anschaffungsdatum, Lieferant, Preis optional.
11. **Interviewer:** Wie loeschen oder aussortieren Sie Medien?
    **Bibliothekarin:** Markiere in Excel als „ausgesondert“ und entferne spaeter physisch. Will entscheiden nach seltenen Ausleihen und Zustand. Statistik dazu fehlt.
12. **Interviewer:** Welche Rollen und Berechtigungen brauchen Sie?
    **Bibliothekarin:** Admin/Bibliothek: Bestand pflegen, Ausleihe/Rueckgabe/Mahnen, Nutzer sperren/entsperren. Lehrkraft: suchen, ausleihen/reservieren, fuer Klassen vormerken. Schueler: suchen, reservieren, ausleihen; evtl. Verlaengerung selbst vornehmen, falls keine Vormerkung besteht.
13. **Interviewer:** Wie sollen sich Nutzer anmelden?
    **Bibliothekarin:** Ideal via Schul-Accounts/SSO. Im Notfall E-Mail + Schul-ID. Wichtig: Einfach fuer Schueler.
14. **Interviewer:** Welche Benachrichtigungen wuenschen Sie?
    **Bibliothekarin:** Bestaetigung von Ausleihe/Verlaengerung/Reservierung, Rueckgabefrist-Erinnerungen, Vormerk-hinfaellig, Bereitstellung zur Abholung, Mahnstufen. Kanal: E-Mail, optional Push.
15. **Interviewer:** Was waere ein erfolgreicher Start (MVP)?
    **Bibliothekarin:** Zuverlaessige Verfuegbarkeit, einfache Suche, digitale Ausleihe/Rueckgabe mit Scanner, automatische Rueckgabe-Erinnerungen, Reservierung/Vormerkung, Rollen fuer Schueler/Lehrer/Admin.
16. **Interviewer:** Groesste Risiken?
    **Bibliothekarin:** Datenverlust, falsche Verfuegbarkeitsanzeige, komplizierte Bedienung fuer Schueler, fehlende Anbindung an Schul-Accounts.
17. **Interviewer:** Welche Hardware steht zur Verfuegung?
    **Bibliothekarin:** Desktop im Bibliotheksraum, gelegentlich Tablet. Barcode-Scanner vorhanden, waere gut nutzbar.
18. **Interviewer:** Welche Berichte brauchen Sie regelmaessig?
    **Bibliothekarin:** Top-Ausleihen pro Zeitraum, ueberfaellige Medien, Vormerklisten, Aussonderungsvorschlaege (selten/nie ausgeliehen), Bestand nach Sachgruppen, Ausleihen nach Klassenstufe.
19. **Interviewer:** Gibt es Datenschutz- oder Aufbewahrungsanforderungen?
    **Bibliothekarin:** Nur notwendige Personendaten speichern; Historie fuer Statistik ist gut, aber keine Dauer-Protokolle nach Schuljahresende fuer einzelne Schueler. Zugriffsschutz fuer personenbezogene Daten.
20. **Interviewer:** Sonstige Wuensche?
    **Bibliothekarin:** Einfache Oberflaeche, wenig Klicks; klare Ampel fuer Verfuegbarkeit; QR/Barcode-Scan in App; Offline-Reserve ist nicht noetig.

## Erste extrahierte Kernthemen
- Verfuegbarkeit und Ausleihstatus muessen jederzeit konsistent sein.
- Reservierung/Vormerkung mit automatischer Benachrichtigung und Abholfenster.
- Rollenmodell: Admin/Bibliothek, Lehrkraft, Schueler; bevorzugt Schul-SSO.
- Barcode-/QR-Scan fuer Ausleihe/Rueckgabe; Medienarten inkl. DVDs und Klassensaetze.
- Mahnwesen stufenweise mit Erinnerungen; klare Rueckgaberegeln und Verlaengerungslogik.
- Reporting: Ausleihstatistik, ueberfaellige Medien, Aussonderungsvorschlaege, Sachgruppen- und Klassenstufen-Sichten.
