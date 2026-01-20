# User Story US-ADM-01: Ausleih- und Reminder-Regeln konfigurieren

**ID:** US-ADM-01
**Titel:** Regeln für Ausleihe, Reservierung und Reminder pflegen
**Priorität:** Must-Have
**Epic:** Administration & Bestand

**Als** Bibliotheks-Admin
**möchte ich** Ausleihfristen, Reservierungsfrist, Verlängerungslimits und Reminder-Zeitpunkte in der Admin Web-App konfigurieren,
**damit** sich die Bibliothek flexibel an Schul- und Ferienrhythmen anpassen kann, ohne einen Release abzuwarten.

**Akzeptanzkriterien:**
- [ ] Es gibt eine Admin-Ansicht "Regelkonfiguration" mit Eingabefeldern für LoanPolicy (Student/Teacher/Librarian) in Tagen (Defaults: 21/56/90).
- [ ] Ich kann die Reservierungs-Abholfrist (ReservationPolicy.ttl) in Stunden/Tagen setzen (Default: 48h).
- [ ] Ich kann das Verlängerungslimit (RenewalPolicy.maxRenewals) und die Verlängerungsdauer in Tagen setzen (Default: 2 Verlängerungen; Dauer = LoanPolicy der UserGroup).
- [ ] Ich kann Reminder-Zeitpunkte (Upcoming, Overdue, Eskalation) sowie die Scheduler-Uhrzeit pflegen (Defaults: T-3, T+1, T+7; 08:00).
- [ ] Änderungen werden gespeichert und gelten für neue Vorgänge; bestehende Loans behalten ihre bisherigen DueDates.
- [ ] Validierungen verhindern ungültige Werte (z.B. negative Tage, ttl < 1 Stunde, maxRenewals < 0).
- [ ] Erfolgreiche Speicherung wird bestätigt; fehlschlagende Saves zeigen eine Fehlermeldung.
