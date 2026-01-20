# Feedback & Validierung - Strategisches Design

**Datum:** 2024-12-16  
**Phase:** Abschluss Strategisches Design  
**Nächster Schritt:** Taktisches Design oder Feedback-Loop

---

## ✅ Validierungsfragen für Sie

Bitte gehen Sie folgende Fragen durch und geben Sie Feedback. Dies hilft, die Architektur vor der Implementierung zu korrigieren.

### 1. **Core Domain - Lending Context**

**Frage:** Ist es korrekt, dass Ausleihe & Reservierung die **Kerndomäne** (Core Domain) Ihres Systems darstellt?

- [X] ✅ Ja, korrekt. Das ist unser Wettbewerbsvorteil
- [ ] ⚠️ Teils-teils. Wichtig, aber nicht einzigartig
- [ ] ❌ Nein. Kerndomäne liegt woanders (bitte erklären)

**Falls "Nein":** Welche Funktion ist stattdessen die Kerndomäne?  
↳ `____________________________`

---

### 2. **Klassensatz-Handling**

**Frage:** Sollte Klassensatz-Management **Teil des Lending Context** sein oder einen **separaten Context** haben?

- [X] ✅ Teil von Lending Context (mit speziellen Regeln)
- [ ] ⚠️ Hybrid: Teilweise separate, teilweise integriert
- [ ] ❌ Separater Context für Klassensätze

**Falls "Separater Context":** Warum sollte es unabhängig sein?  
↳ `____________________________`

---

### 3. **Reporting & Statistik**

**Frage:** Ist es akzeptabel, Reporting & Statistik **NICHT im MVP** zu implementieren?

- [X] ✅ Ja, richtige Entscheidung für MVP
- [ ] ⚠️ Mindestens Basic-Reporting sollte rein
- [ ] ❌ Nein, Reporting ist MVP-kritisch

**Falls "MVP-kritisch":** Welche Reports sind essentiell?  
↳ `____________________________`

---

### 4. **User Context - SSO Integration**

**Frage:** Ist es sinnvoll, User-Management als **generische Subdomain** zu behandeln mit SSO-Integration via Adapter?

- [X] ✅ Ja, SSO-basiertes Design ist ideal
- [ ] ⚠️ SSO ja, aber mehr Custom-Logik nötig
- [ ] ❌ Nein, komplett Custom-Authentication

**Falls "Custom-Authentication":** Welche spezialisierten Anforderungen?  
↳ `____________________________`

---

### 5. **Bestandsverwaltung**

**Frage:** Sollte Bestandsverwaltung **Teil des Catalog Context** sein?

- [X] ✅ Ja, gehört zusammen
- [ ] ⚠️ Großteils ja, aber einige Aspekte anders
- [ ] ❌ Nein, eigener Context nötig

**Falls "Eigener Context":** Warum?  
↳ `____________________________`

---

## 📋 Geschäftsregeln Validierung

### Lending Invarianten

Bitte markieren Sie, welche Regeln für Ihren Schulbetrieb gelten:

**Ausleihe (Checkout):**
- [X] ✅ Benutzer muss aktiv sein
- [X] ✅ Keine überfälligen Medien
- [X] ✅ Ausleihgrenze pro Benutzergruppe
- [X] ✅ Media muss verfügbar sein

**Rückgabefrist (DueDate):**
- [X] ✅ Admin konfigurierbar in der Web-App (Default: Schüler 21 Tage, Lehrer 56 Tage)
- [ ] ⚠️ Feste Zeiträume bevorzugt
- [ ] ❓ Andere Zeiträume?

**Falls andere:** Bitte spezifizieren:  
↳ Student: ____ Tage, Lehrer: ____ Tage, Andere: ____ (bitte begründen)

**Verlängerung (Renewal):**
- [X] ✅ Max. Anzahl Verlängerungen ist in der Admin Web-App konfigurierbar (Default: 2)
- [X] ✅ Nur wenn keine Vormerkung existiert
- [X] ✅ Vor Rückgabefrist (nicht danach)
- [ ] ❓ Andere Regeln?

**Klassensatz:**
- [X] ✅ Nur Lehrer dürfen buchen
- [X] ✅ Längere Ausleihdauer (z.B. 8 Wochen)
- [X] ✅ Kompletter Satz muss zurück
- [ ] ❓ Andere Regeln?

### Reservierung Invarianten

**Verfügbar-Reservierung (Click & Collect):**
- [X] ✅ Abholfrist ist in der Admin Web-App konfigurierbar (Default: 48h)
- [X] ✅ Verfällt automatisch wenn nicht abgeholt
- [ ] ❓ Andere Frist?

**Falls andere Frist:** ____

**Vormerkung (Waitlist):**
- [X] ✅ FIFO-Queue (Reihenfolge nach Datum)
- [X] ✅ Auto-Reservation bei Media Return
- [x] ✅ User wird sofort notifiziert
- [ ] ❓ Andere Logik?

### Reminder Invarianten

**Reminder Zeitpunkte:**
- [X] ✅ Reminder-Zeitpunkte sind in der Admin Web-App konfigurierbar (Defaults: T-3, T+1, T+7)
- [ ] ⚠️ Feste Zeitpunkte bevorzugt
- [ ] ❓ Andere Zeitpunkte?

**Falls andere:** ____

---

## 🔄 Integration Validierung

### Synchrone vs. Asynchrone

**Frage:** Ist es akzeptabel, dass Notifications **asynchron** sind (können Sekunden verzögert sein)?

- [X] ✅ Ja, Verzögerung ist OK
- [ ] ⚠️ Für manche Notifications ja, für andere nein
- [ ] ❌ Nein, alle müssen synchron sein

**Falls "teilweise":** Welche müssen synchron sein?  
↳ `____________________________`

---

### Event-Driven Architecture

**Frage:** Ist eine **Event-Driven Architektur** (Pub-Sub) das richtige Pattern für Ihre Anforderungen?

- [ ] ✅ Ja, perfekt
- [X] ⚠️ Gut, aber mit Einschränkungen
- [ ] ❌ Nein, brauchen eher Request-Response

**Falls "Einschränkungen":** Welche?  
↳ nicht alle Contexte gleiche Architektur. Core->Hexagonal, Gemeric -> Adapter-Pattern, Supporting -> CRUD. Event-Driven dort zusätzlich wo Events benötigt werden

---

### SSO Integration

**Frage:** Ist die **Anti-Corruption Layer** zum SSO die richtige Lösung?

- [X] ✅ Ja, Adapter-Pattern ist ideal
- [ ] ⚠️ Gut, aber mehr Mapping nötig
- [ ] ❌ Nein, andere Integration bevorzugt

**Falls "andere":** Welche?  
↳ `____________________________`

---

## 📊 Kontext-Mapping Validierung

### Sind diese Contexts sinnvoll?

| Context | Sinnvoll? | Anmerkungen |
|---------|-----------|-------------|
| **Lending** (Core) | [X] ✅ [ ] ⚠️ [ ] ❌ | _____________ |
| **Catalog** (Support) | [X] ✅ [ ] ⚠️ [ ] ❌ | _____________ |
| **User** (Generic) | [X] ✅ [ ] ⚠️ [ ] ❌ | _____________ |
| **Notification** (Support) | [X] ✅ [ ] ⚠️ [ ] ❌ | _____________ |
| **Reminding** (Support) | [X] ✅ [ ] ⚠️ [ ] ❌ | __________ |

---

### Fehlende Contexts?

**Frage:** Gibt es Geschäftsfunktionen, die **keinem Context** zugeordnet sind?

- [X] Nein, alle Funktionen sind abgedeckt
- [ ] Ja, folgende fehlten:

```
1. ____________________________
2. ____________________________
3. ____________________________
```

---

## 🎯 Domain Events Validierung

### Event-Liste

**Frage:** Sind diese **8 Domain Events** vollständig und korrekt?

```
5. PreReservationResolved (Waitlist Exit) [X] OK [ ] Änderung
6. LoanRenewed          (Renewal)         [X] OK [ ] Änderung
7. ClassSetCheckedOut   (ClassSet)        [X] OK [ ] Änderung
8. ReminderTriggered    (Reminder)        [X] OK [ ] Änderung
```

**Fehlende Events:**
↳ ClassSetReturned (ClassSet)

---

### Event-Payload

**Frage:** Haben die Events genug Information (Payload)?

- [X] ✅ Ja, optimale Menge
- [ ] ⚠️ Einige Events haben zu viel/wenig
- [ ] ❌ Generell zu viel Daten

**Falls Anmerkungen:** Welche Felder sollten geändert werden?  
↳ `____________________________`

---

## 💬 Allgemeines Feedback

### Was funktioniert gut?

```
Punkt 1:
Punkt 2:
Punkt 3:
```

---

### Was könnte besser sein?

```
Punkt 1:
Punkt 2:
Punkt 3:
```

---

### Offene Fragen / Klärungsbedarf?

```
Frage 1:
Frage 2:
Frage 3:
```

---

## 🚀 Bereitschaft für nächste Phase?

### Sind Sie bereit für **Taktisches Design**?

```
✅ Strategisches Design ist validiert
✅ Alle Contexts sind akzeptiert
✅ Geschäftsregeln sind finalisiert
✅ Events sind definiert
✅ Integration Pattern sind klar
```

**Kann die nächste Phase beginnen?**

- [ ] ✅ JA - Los geht's!
- [ ] ⚠️ TEILS - Mit Feedback unten
- [X] ❌ NEIN - Brauchen mehr Zeit / weitere Anpassungen

---

## 📋 Feedback-Zusammenfassung

### Kategorien (Bitte ankreuzen was zutrifft):

- [ ] 🎯 **Strategische Änderungen** (Contexts, Core Domain, etc.)
- [ ] 📝 **Begriffliche Klärungen** (Glossar, Ubiquitous Language)
- [ ] 🔄 **Integrations-Anpassungen** (Events, Sync/Async, Patterns)
- [X] ⚙️ **Geschäftsregel-Korrektionen** (Invarianten, Policies)
- [ ] 📚 **Dokumentations-Verbesserungen** (Klarheit, Format, etc.)
- [ ] ✅ **Alles OK** (Keine Änderungen nötig)

---

## 🎯 Nächste Schritte

### Falls Feedback vorhanden:

1. **Feedback sammeln** aus diesem Formular
2. **Diskussions-Termin** vereinbaren (falls nötig)
3. **Dokumentation aktualisieren** basierend auf Input
4. **Validierungs-Runde 2** durchführen (falls viele Änderungen)
5. **Sign-Off** abholen von Key-Stakeholdern

### Falls kein Feedback:

1. **Direkt zu Taktischem Design übergehen**
2. **Glossar als Referenz verwenden**
3. **Domain Events in Implementierung abbilden**
4. **Invarianten in Geschäftslogik kodieren**

---

## 📞 Kontakt & Fragen

**Für Klärungsfragen während Taktisches Design:**
- Referenzieren Sie die **Ubiquitous Language Glossar** (docs/architecture/ubiquitous-language-glossar.md)
- Lesen Sie die **Bounded Contexts Map** (docs/architecture/bounded-contexts-map.md)
- Studieren Sie die **Domain Events** (docs/architecture/domain-events-integrations.md)

**Für größere Änderungen:**
- Starten Sie einen neuen Validierungs-Zyklus
- Aktualisieren Sie alle 5 Dokumente konsistent

---

## ✅ Abschluss

**Status:** ✅ Strategisches Design Phase 1 abgeschlossen

**Deliverables:**
- ✅ strategic-architecture-summary.md
- ✅ bounded-contexts-map.md
- ✅ ubiquitous-language-glossar.md
- ✅ domain-events-integrations.md
- ✅ context-map-visualizations.md
- ✅ README.md (Index)
- ✅ feedback-validation.md (dieses Dokument)

**Nächste Phase:** Taktisches Design  
**Chat-Mode:** `ddd-architect-taktik-design`

---

**Vielen Dank für Ihre Aufmerksamkeit!**

Bitte füllen Sie dieses Formular aus und geben Sie Feedback. Damit stellen wir sicher, dass die Architektur zu 100% zu Ihren Anforderungen passt.

