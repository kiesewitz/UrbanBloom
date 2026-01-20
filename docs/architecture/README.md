# Architecture Documentation Index

**Version:** 1.0  
**Datum:** 2024-12-16  
**Phase:** Strategisches Design - ABGESCHLOSSEN  
**Status:** ✅ Ready for Tactical Design

---

## 📚 Dokumentations-Struktur

Alle Architektur-Dokumente befinden sich im Verzeichnis: `docs/architecture/`

### 🎯 Phase 1: Strategisches Design (AKTUELL)

**START HIER ↓**

#### 1. **strategic-architecture-summary.md** 
   - 🎯 Executive Summary
   - ✅ 5 Bounded Contexts Übersicht
   - ✅ Domänen-Kategorisierung
   - ✅ Strategische Entscheidungen (5 Stück)
   - ✅ Kernbegriffe & Geschäftsregeln
   - ✅ Integration Architecture
   - ✅ Roadmap
   - **Zielgruppe:** Alle (Start-Punkt)
   - **Lesedauer:** 5-10 Minuten
   - **Format:** Markdown mit Tabellen & Checklisten

---

#### 2. **bounded-contexts-map.md**
   - 🗺️ Detaillierte Context Map
   - 📊 ASCII-Visualisierung aller 5 Contexts
   - 📍 Context-Details (Kern-Begriffe, Geschäftsregeln)
   - 🔄 Integration zwischen Contexts
   - 🎬 Query & Command Flowchart
   - 📋 Domain Events Mapping
   - 🛡️ Anti-Corruption Layer
   - 📚 Context-zu-User-Story-Mapping
   - **Zielgruppe:** Architekten, Lead-Entwickler
   - **Lesedauer:** 15-20 Minuten
   - **Format:** Markdown mit Flowcharts & YAML-Payloads

---

#### 3. **ubiquitous-language-glossar.md**
   - 📖 Umfassendes Domain-Glossar
   - 🎯 Allgemeine Begriffe (domänenweit)
   - 🔑 Pro Context:
     - Nomen (Substantive) Tabelle
     - Verben (Aktionen) Tabelle
     - Geschäftsregeln
     - Integrationen
   - **Zielgruppe:** Entwickler, Business Analyst
   - **Lesedauer:** 20-30 Minuten
   - **Format:** Markdown mit detaillierten Tabellen

---

#### 4. **domain-events-integrations.md**
   - 📬 Domain Events Katalog (8 Events)
   - 🔄 Integration Patterns (3 Patterns)
   - 🎬 Sequenz-Diagramme
   - 📊 Integration Matrix
   - 🛡️ Error Handling & Resilience
   - 📡 Event Broker Recommendations
   - **Zielgruppe:** Entwickler, Integration Engineer
   - **Lesedauer:** 25-35 Minuten
   - **Format:** Markdown mit YAML-Payload-Definitionen & Code-Beispiele

---

#### 5. **context-map-visualizations.md**
   - 📊 8 Mermaid Diagramme:
     1. Bounded Contexts Übersicht
     2. Integration Flows (Sequenzdiagramm)
     3. Domain Events Chain
     4. Waitlist / PreReservation Resolution
     5. Klassensatz Special Handling
     6. State Machines (3x)
     7. Context Dependency Matrix
     8. MVP vs. Future
   - 🎨 Legende & Farbcodierung
   - **Zielgruppe:** Alle (visuell)
   - **Lesedauer:** 10-15 Minuten
   - **Format:** Mermaid (kopierbar in Live-Editor)

---

## 🎓 Empfohlene Lese-Reihenfolge

### **Option 1: Quick Overview (20 Min)**
1. strategic-architecture-summary.md
2. context-map-visualizations.md (Diagramm 1, 3, 6)
3. Fertig! Basis verstanden

### **Option 2: Implementation Prep (90 Min)**
1. strategic-architecture-summary.md
2. bounded-contexts-map.md
3. ubiquitous-language-glossar.md
4. domain-events-integrations.md (Events 1-7)
5. context-map-visualizations.md (alle)

### **Option 3: Deep Dive (3+ Stunden)**
1. Alle Dokumente in Reihenfolge
2. Diagramme mermaid.live exportieren & annotieren
3. Glossar als Referenz für Coding verwenden
4. Events-Dokument als Template für Implementierung

---

## 📖 Pro Context - Schnelleinstieg

### **LENDING CONTEXT** (Core Domain)

| Dokument | Section | Fokus |
|----------|---------|-------|
| **bounded-contexts-map.md** | Abschnitt 3 | Geschäftsregeln, Invarianten |
| **ubiquitous-language-glossar.md** | Abschnitt 5 (LENDING) | Aggregate, Verben, Rules |
| **domain-events-integrations.md** | Events 1-7 | MediaCheckedOut, Waitlist, etc |
| **context-map-visualizations.md** | Diagramme 2, 3, 4, 5, 6 | Flows, State Machines |

**Quick Start:** 30 min in bounded-contexts-map + ubiquitous-language-glossar Abschnitt 5

---

### **CATALOG CONTEXT** (Supporting Subdomain)

| Dokument | Section | Fokus |
|----------|---------|-------|
| **bounded-contexts-map.md** | Abschnitt 2 | Kernel-Begriffe |
| **ubiquitous-language-glossar.md** | Abschnitt 4 (CATALOG) | Media, MediaCopy, Inventory |
| **domain-events-integrations.md** | "Handlers" in Events 1-7 | Update AvailabilityStatus |
| **context-map-visualizations.md** | Diagramme 1, 2, 3 | Dependencies |

**Quick Start:** 15 min in bounded-contexts-map + 10 min Glossar

---

### **USER CONTEXT** (Generic Subdomain)

| Dokument | Section | Fokus |
|----------|---------|-------|
| **bounded-contexts-map.md** | Abschnitt 1 + ACL | SSO Integration, Adapter |
| **ubiquitous-language-glossar.md** | Abschnitt 3 (USER) | SchoolIdentity, UserGroup |
| **domain-events-integrations.md** | ACL Pattern | Anti-Corruption Layer |

**Quick Start:** 10 min in bounded-contexts-map Abschnitt 1

---

### **NOTIFICATION CONTEXT** (Supporting Subdomain)

| Dokument | Section | Fokus |
|----------|---------|-------|
| **bounded-contexts-map.md** | Abschnitt 4 | Event-Driven Responsibility |
| **ubiquitous-language-glossar.md** | Abschnitt 7 (NOTIFICATION) | Notification, Channel, Handler |
| **domain-events-integrations.md** | "Handlers" in allen Events | Send email, async processing |

**Quick Start:** 10 min in bounded-contexts-map + Events Handlers

---

### **REMINDING CONTEXT** (Supporting Subdomain)

| Dokument | Section | Fokus |
|----------|---------|-------|
| **bounded-contexts-map.md** | Abschnitt 5 | Policy-basierte Trigger |
| **ubiquitous-language-glossar.md** | Abschnitt 8 (REMINDING) | ReminderPolicy, Campaign |
| **domain-events-integrations.md** | Event 8: ReminderTriggered | Scheduler, Reminder Types |

**Quick Start:** 10 min in bounded-contexts-map + Event 8

---

## 🔍 Nach Use Case suchen?

### "Ich möchte verstehen, wie der Checkout funktioniert"
→ **Lesbar:** domain-events-integrations.md → Event 1: MediaCheckedOut → Integration Pattern 2

### "Wie wird die Warteliste verarbeitet?"
→ **Lesbar:** bounded-contexts-map.md → Invariante 5 + context-map-visualizations.md → Diagramm 4

### "Was sind die Geschäftsregeln für Klassensätze?"
→ **Lesbar:** bounded-contexts-map.md → Invariante 7 + context-map-visualizations.md → Diagramm 5

### "Wie integriere ich mit dem SSO?"
→ **Lesbar:** bounded-contexts-map.md → ACL-Abschnitt + domain-events-integrations.md → ACL Pattern

### "Welche Events gibt es?"
→ **Lesbar:** domain-events-integrations.md → Events 1-8 (Katalog)

### "Was sind die Value Objects?"
→ **Lesbar:** ubiquitous-language-glossar.md → Jeder Context-Abschnitt

---

## 🚀 Nächste Phase: Taktisches Design

**Startdatum:** Nach Validierung dieser strategischen Design  
**Chat-Mode:** `ddd-architect-taktik-design`

### Was wird dann dokumentiert?

1. **Taktisches Design - Aggregate Roots**
   - Aggregate Root pro Context
   - Root-Verantwortlichkeiten
   - Transaktions-Grenzen

2. **Taktisches Design - Value Objects**
   - Alle Value Objects mit Invarianten
   - Validierungsregeln
   - Immutability-Constraints

3. **Taktisches Design - Entities & Identity**
   - Entity-Definitionen
   - Identity-Strategien (UUID, Sequential, etc)
   - Lifecycle Management

4. **Taktisches Design - Domain Services**
   - Pure Business Logic Services
   - Service-Dependencies
   - Exception Handling

5. **Taktisches Design - Repository & Factories**
   - Persistence Abstraction
   - Factory Patterns
   - Query Objects

6. **Taktisches Design - Application Services**
   - Use Case Implementation
   - Command Handlers
   - Query Handlers (Read Model)

---

## 📝 Changelog

### Version 1.0 (2024-12-16)
- ✅ Strategic architecture complete
- ✅ 5 Bounded Contexts defined & validated
- ✅ Domain events catalog
- ✅ Integration patterns documented
- ✅ Ubiquitous language glossar created
- ✅ Visualizations (Mermaid) provided
- ✅ This index created

---

## 📞 Verwendung in Ihrem Projekt

### Als Team-Referenz
```markdown
# Projektarchitektur

Die Architektur basiert auf Domain-Driven Design.
Siehe: docs/architecture/strategic-architecture-summary.md
```

### Für neue Team-Mitglieder
```markdown
## Onboarding-Anleitung

1. Lesen Sie: docs/architecture/strategic-architecture-summary.md
2. Studieren Sie: docs/architecture/context-map-visualizations.md
3. Nutzen Sie als Referenz: docs/architecture/ubiquitous-language-glossar.md
```

### Für Code-Reviews
```java
// Verify against:
// - docs/architecture/ubiquitous-language-glossar.md (LENDING CONTEXT)
// - docs/architecture/domain-events-integrations.md (Event definitions)
// - docs/architecture/bounded-contexts-map.md (Invarianten)
```

---

## ✅ Quality Checklist

Diese Dokumentation erfüllt folgende Kriterien:

- ✅ **Vollständigkeit:** Alle 5 Contexts abgedeckt
- ✅ **Klarheit:** Für Anfänger & Experten verständlich
- ✅ **Referenz:** Durchgehend intern verlinkt
- ✅ **Visuell:** Diagramme & ASCII-Visualisierungen
- ✅ **Praktisch:** Code-Patterns & Implementierungs-Hinweise
- ✅ **Wartbar:** Versioniert, Changelog vorhanden
- ✅ **Skalierbar:** Design für Phase 2+ vorbereitet

---

## 📌 Wichtige Hinweise

### 🔴 DO: 
- ✅ Glossar als Single Source of Truth verwenden
- ✅ Domain Events als Pub-Sub-Basis implementieren
- ✅ Invarianten aus Dokumentation in Tests prüfen
- ✅ Contexts als eigenständige Java-Packages strukturieren

### ❌ DON'T:
- ❌ Geschäftsregeln in UI oder API-Layer implementieren
- ❌ Domain Events synchron machen (nur async!)
- ❌ User Context mit SSO vermischen
- ❌ Lending Context in andere Contexts einmischen

---

**Fragen? Starten Sie Chat-Mode: `ddd-architect-taktik-design`**

