# Strategic Architecture Summary - Digital School Library

**Datum:** 2024-12-16  
**Phase:** Domain-Driven Design - Strategisches Design (ABGESCHLOSSEN)  
**Nächste Phase:** Taktisches Design  
**Status:** ✅ Ready for Tactical Design Phase

---

## 🎯 Executive Summary

Das Digital School Library Projekt wurde einer umfassenden **Domain-Driven Design Analyse** unterzogen. Die Ergebnisse zeigen:

### ✅ **5 Bounded Contexts identifiziert & validiert**

```
┌────────────────────────────────────────────────────────────┐
│  STRATEGISCHE ARCHITEKTUR - MVP DESIGN                    │
├────────────────────────────────────────────────────────────┤
│                                                             │
│  CORE DOMAIN (Wettbewerbsvorteil)                         │
│  ├─ Lending Context (Ausleihe & Reservierung)             │
│  │  └─ Klassensatz-Handling als spezialisierte Regel      │
│  │                                                          │
│  SUPPORTING SUBDOMAINS (Geschäftsunterstützung)           │
│  ├─ Catalog Context (Katalog & Bestand)                   │
│  ├─ Notification Context (Benachrichtigungen)             │
│  └─ Reminding Context (automatische Erinnerungen)         │
│                                                             │
│  GENERIC SUBDOMAINS (Standard-Lösungen)                   │
│  └─ User Context (SSO-Integration)                        │
│                                                             │
└────────────────────────────────────────────────────────────┘
```

### 📊 **Domänen-Kategorisierung**

| Context | Kategorie | MVP | Kritikalität | Investition |
|---------|-----------|-----|---|---|
| **Lending** | Core Domain | ✅ | ⭐⭐⭐ | High |
| **Catalog** | Supporting | ✅ | ⭐⭐ | Medium |
| **Notification** | Supporting | ✅ | ⭐⭐ | Medium |
| **Reminding** | Supporting | ✅ | ⭐⭐ | Medium |
| **User** | Generic | ✅ | ⭐ | Low |

---

## 🔑 Strategische Entscheidungen

### 1. **Lending + Reservation als integrierte Core Domain**

**Warum?**
- ✅ Größte fachliche Komplexität (Policies, Waitlists, ClassSets)
- ✅ Kerngeschäft der Schulbibliothek
- ✅ Wettbewerbsvorteil durch spezialisierte Rules
- ✅ Änderungen hier beeinflussen viele andere Systeme

**Konsequenz:** Beste Entwickler-Ressourcen, detailliertes Testen, mögliche Event-Sourcing Integration später.

---

### 2. **Klassensatz-Handling als Teil von Lending Context**

**Warum nicht separater Context?**
- ❌ Zu spezialisiert (nur 1 User-Gruppe: Lehrer)
- ✅ Gleiche Geschäftsfunktion wie normale Ausleihe
- ✅ Einfach durch Policies unterscheidbar
- ✅ Weniger Kontext-Übergreifende Integration nötig

**Implementierung:** `ClassSet Aggregate` mit speziellen Rules:
- Längere Ausleihdauer (8 Wochen statt 3)
- Multi-Media-Handling (alle Exemplare zusammen)
- Return-Validierung (vollständiger Satz erforderlich)

---

### 3. **Reporting & Statistik später planen (nicht im MVP)**

**Warum nicht jetzt?**
- ❌ Nicht im MVP-Scope
- ✅ Keine Geschäftsfunktion, nur Analytics
- ✅ Kann über Event-Store später leicht aufgebaut werden
- ✅ Reduziert Initial-Komplexität

**Future Plan:** Separate Analytics Context in Phase 2+, der auf Events abonniert.

---

### 4. **Bestandsverwaltung als Teil von Catalog Context**

**Warum?**
- ✅ Gleiche Daten (Media + MediaCopy)
- ✅ Admin-Interface in Catalog Context
- ✅ Kein separater Business-Flow
- ❌ Keine speziellen Rules oder Workflow

---

### 5. **User Context als Generic Subdomain mit ACL**

**Warum?**
- ✅ Standard-IAM Problem
- ✅ SSO ist bewährtes Pattern
- ❌ Keine schulspezifischen Besonderheiten
- ✅ Klare Anti-Corruption Layer zu externem SSO

**Integration:** Adapter-Pattern konvertiert SSO-Attribute zu internen Enums

---

## 📚 Ubiquitous Language - Kernbegriffe

### Context-übergreifend (allgegenwärtig)

| Begriff | Definition |
|---------|-----------|
| **User** | Eine authentifizierte Person im System |
| **Media** | Ein Medienwerk (Buch, DVD, etc.) |
| **MediaCopy** | Ein physisches Exemplar |
| **Barcode** | Eindeutige Kennung |
| **UserGroup** | Rolle (Student, Teacher, Librarian) |

### Lending Context Kernbegriffe (CORE DOMAIN)

| Aggregate | Verantwortung |
|-----------|---------------|
| **Loan** | Aktiver Ausleihvorgang |
| **Reservation** | Reservierung verfügbares Medium (48h TTL) |
| **PreReservation** | Vormerkung verliehenes Medium (Waitlist) |
| **ClassSet** | Sammlung für Klassenausleihe |

### Geschäftsregeln (Invarianten)

```
CHECKOUT Guard:    User active? Media available? Limit ok?
DUE DATE:         LoanPolicy pro UserGroup über Admin Web-App konfigurierbar (Defaults: Student 21 Tage, Teacher 56 Tage)
RENEWAL:          Max. Anzahl Verlängerungen in Admin Web-App konfigurierbar (Default: 2), nur ohne PreReservation
RETURN:           Auto-process Waitlist, flag overdue
RESERVATION:      TTL in Admin Web-App konfigurierbar (Default: 48h), verfällt automatisch
WAITLIST (FIFO):  Auto-Reservation bei Media Return
CLASSSET:         Nur Teachers, 56 Tage, vollständig zurück
```

---

## 🔄 Integration Architecture

### Synchronous (Request-Reply)

```
Checkout Flow:
  Lending Context  ─Q─>  User Context (checkEligibility)
                   ─Q─>  Catalog Context (checkAvailability)
```

### Asynchronous (Event-Driven)

```
Publishing Chain:
  Lending Context publishes MediaCheckedOut Event
              ├─>  Catalog Context   (update status)
              ├─>  Notification Ctx  (send email)
              └─>  Reminding Ctx     (schedule reminders)
```

### Key Domain Events

| Event | Source | Consumers | Type |
|-------|--------|-----------|------|
| **MediaCheckedOut** | Lending | Catalog, Notification | Async |
| **MediaReturned** | Lending | Catalog, Notification, Reminding | Async |
| **MediaReserved** | Lending | Catalog, Notification | Async |
| **PreReservationCreated** | Lending | Notification | Async |
| **PreReservationResolved** | Lending | Notification | Async |
| **LoanRenewed** | Lending | Notification, Reminding | Async |
| **ReminderTriggered** | Reminding | Notification | Async |

---

## 🛡️ Anti-Corruption Layer

### SSO Integration (User Context ↔ External School SSO)

```
External SSO Format          Internal Format
├─ eduPersonPrimaryAffiliation=student  →  UserGroup.STUDENT
├─ eduPersonPrimaryAffiliation=faculty  →  UserGroup.TEACHER
├─ admin flag                           →  UserGroup.LIBRARIAN
└─ email                                →  SchoolIdentity
```

**Implementierung:** Adapter/Translator im User Context

---

## 📈 Implementation Roadmap

### Phase 1: Taktisches Design (nächster Chat-Mode)
- Aggregate Roots, Entities, Value Objects pro Context
- Domain Services identifizieren
- Repositories und Factories
- Application Services (Command/Query Handlers)

### Phase 2: Code-Architektur
- Package-Struktur pro Context
- DDD Code-Patterns (Spring Boot)
- Event Publishing Infrastructure
- Data Schema Design

### Phase 3: Implementation
- Repository Implementations
- Service Logic
- REST APIs
- Testing Strategy

### Phase 4: Integration & Deployment
- Event-Broker Setup
- SSO Integration
- Load Testing
- Production Deployment

---

## 📋 Deliverables dieser Phase

✅ **bounded-contexts-map.md** - Context Map mit Visualisierungen  
✅ **ubiquitous-language-glossar.md** - Umfassendes Glossar pro Context  
✅ **domain-events-integrations.md** - Domain Events mit Payload & Handler  
✅ **strategic-architecture-summary.md** - Dieses Dokument  

**Alle Dateien befinden sich in:** `docs/architecture/`

---

## 🎓 Nächste Schritte

**➡️ Starten Sie Chat-Mode: `ddd-architect-taktik-design`**

Dieser wird Sie durch folgende Schritte leiten:

1. **Aggregate Roots definieren** pro Context
2. **Value Objects** mit Invarianten
3. **Entities** und ihre Identität
4. **Domain Services** (Pure Geschäftslogik)
5. **Repositories** (Persistierung-Abstraktionen)
6. **Application Services** (Use Cases)

Damit wird aus der **Strategie** die **konkrete Implementierungsbasis**.

---

## 🔍 Validierungschecklist

Haben Sie folgende Punkte validiert?

- [ ] Core Domain (Lending) ist richtig identifiziert
- [ ] Klassensatz-Handling ist sinnvollerweise Part von Lending
- [ ] Reporting später planen verstanden
- [ ] Bestandsverwaltung in Catalog Context akzeptiert
- [ ] SSO-Integration via ACL nachvollziehbar
- [ ] Domain Events machen Sinn
- [ ] Synchrone vs. Asynchrone Integration verständlich
- [ ] Ubiquitous Language spricht Geschäftslogik ab

**Falls alle ✅:** Sie sind bereit für **Taktisches Design**!

