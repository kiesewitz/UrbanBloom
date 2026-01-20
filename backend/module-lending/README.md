# Module: Lending Context (Core Domain)

**Domänen-Kategorie:** Core Domain  
**Architekturmuster:** Hexagonale Architektur (Ports & Adapters) + DDD Tactical Patterns  
**Kritikalität:** ⭐⭐⭐ (Highest)

## Übersicht

Der **Lending Context** ist die **Core Domain** des Digital School Library Systems. Hier findet die komplexeste Geschäftslogik statt: Ausleihe, Reservierung, Wartelisten und Klassensatz-Verwaltung.

### Verantwortlichkeiten

- ✅ Verwaltung aktiver Ausleihen (`Loan`)
- ✅ Reservierungen verfügbarer Medien (`Reservation`)
- ✅ Vormerkungen (Warteliste) verliehener Medien (`PreReservation`)
- ✅ Klassensatz-Verwaltung (`ClassSet`)
- ✅ Durchsetzung von Ausleih- und Reservierungsregeln (`LoanPolicy`, `ReservationPolicy`)
- ✅ Verwaltung von Verlängerungen und Überfälligkeiten
- ✅ Automatische Wartelisten-Verarbeitung bei Rückgabe

## Architektur-Struktur

Dieses Modul folgt strikt der **Hexagonalen Architektur**:

```
module-lending/
├── domain/                      # 🎯 DOMAIN LAYER (Framework-frei)
│   ├── model/                   # Aggregates, Entities, Value Objects
│   ├── service/                 # Domain Services
│   ├── event/                   # Domain Events
│   └── repository/              # Repository Ports (Interfaces)
│
├── application/                 # 🎯 APPLICATION LAYER (Use Cases)
│   ├── command/                 # Write Commands (DTOs)
│   ├── query/                   # Read Queries (DTOs)
│   └── service/                 # Application Services (Orchestration)
│
├── adapter/                     # 🎯 ADAPTER LAYER (Infrastructure)
│   ├── in/
│   │   └── rest/               # REST Controllers + DTOs
│   └── out/
│       └── persistence/        # JPA Entities + Repository Adapters
│           ├── entity/         # JPA Entities (Anemic)
│           └── mapper/         # MapStruct Mappers (Domain ↔ JPA)
│
└── config/                      # Spring Configuration
```

## Domain Layer (Framework-frei!)

### 📦 `domain/model/`

**Zweck:** Zentrale Geschäftslogik in Aggregates, Entities und Value Objects

**Wichtige Klassen:**
- `Loan` (Aggregate Root) - Aktive Ausleihe
- `Reservation` (Aggregate Root) - Reservierung verfügbarer Medien
- `PreReservation` (Aggregate Root) - Vormerkung (Warteliste)
- `ClassSet` (Aggregate Root) - Klassensatz-Ausleihe
- `LoanPolicy` (Value Object) - Ausleihregeln
- `ReservationPolicy` (Value Object) - Reservierungsregeln
- `DueDate` (Value Object) - Fälligkeitsdatum
- `Barcode` (Value Object) - Medien-/User-Kennung

**Best Practices:**
- ✅ **Rich Domain Model:** Aggregates enthalten Geschäftslogik (z.B. `loan.renew()`, `loan.isOverdue()`)
- ✅ **Invarianten schützen:** Nur über Methoden veränderbar, nicht über Setter
- ✅ **Keine Framework-Dependencies:** Keine Spring, JPA oder Lombok-Annotationen (außer `@Value` für VOs)

### 📦 `domain/service/`

**Zweck:** Domain Services für komplexe Geschäftslogik über mehrere Aggregates

**Beispiele:**
- `WaitlistProcessor` - Verarbeitet Warteliste bei Rückgabe
- `LoanEligibilityChecker` - Prüft Ausleihberechtigung
- `RenewalPolicyService` - Berechnet Verlängerungsregeln

**Best Practices:**
- ✅ Stateless
- ✅ Nur Domain-Objekte als Parameter/Return-Typen
- ✅ Keine direkte Kommunikation mit Adaptern

### 📦 `domain/event/`

**Zweck:** Domain Events für wichtige Geschäftsereignisse

**Wichtige Events:**
- `LoanCreatedEvent` - Ausleihe erstellt
- `LoanReturnedEvent` - Medium zurückgegeben
- `ReservationCreatedEvent` - Reservierung erstellt
- `ReservationExpiredEvent` - Reservierung verfallen
- `LoanOverdueEvent` - Ausleihe überfällig

**Best Practices:**
- ✅ Immutable (alle Felder `final`)
- ✅ Enthalten alle relevanten Daten (kein Nachladen nötig)
- ✅ Past tense (z.B. `LoanCreated`, nicht `CreateLoan`)

### 📦 `domain/repository/`

**Zweck:** Repository Ports (Interfaces) für Persistenz

**Beispiele:**
- `LoanRepository` (Interface)
- `ReservationRepository` (Interface)
- `ClassSetRepository` (Interface)

**Best Practices:**
- ✅ Nur Interfaces, keine Implementierungen
- ✅ Verwenden Domain-Objekte (nicht JPA Entities)
- ✅ Methodennamen in Ubiquitous Language

## Application Layer (Orchestration)

### 📦 `application/command/`

**Zweck:** Write Commands (DTOs für Schreib-Operationen)

**Beispiele:**
- `CheckoutCommand` - Ausleihe erstellen
- `ReturnCommand` - Medium zurückgeben
- `RenewLoanCommand` - Ausleihe verlängern
- `CreateReservationCommand` - Reservierung erstellen

### 📦 `application/query/`

**Zweck:** Read Queries (DTOs für Lese-Operationen)

**Beispiele:**
- `FindLoansByUserQuery`
- `FindOverdueLoansQuery`
- `FindActiveReservationsQuery`

### 📦 `application/service/`

**Zweck:** Application Services orchestrieren Domain-Objekte

**Wichtige Services:**
- `CheckoutApplicationService` - Ausleihe erstellen
- `ReturnApplicationService` - Rückgabe verarbeiten
- `ReservationApplicationService` - Reservierungen verwalten
- `RenewalApplicationService` - Verlängerungen verarbeiten

**Best Practices:**
- ✅ Eine Methode pro Use Case
- ✅ Annotiert mit `@Transactional`
- ✅ Delegiert an Domain-Objekte, enthält KEINE Business-Logik
- ✅ Publiziert Domain Events

## Adapter Layer (Infrastructure)

### 📦 `adapter/in/rest/`

**Zweck:** REST API Endpoints

**Beispiele:**
- `LoanController` - `/api/v1/loans`
- `ReservationController` - `/api/v1/reservations`

**Best Practices:**
- ✅ Thin Controllers (nur Delegation)
- ✅ Verwenden Request/Response DTOs (in `dto/` Unterordner)
- ✅ Mapping: REST-DTO → Command/Query → Application Service

### 📦 `adapter/out/persistence/`

**Zweck:** Persistenz-Adapter (JPA)

**Unterordner:**
- `entity/` - JPA Entities (Anemic Model, nur Getter/Setter)
- `mapper/` - MapStruct Mapper (Domain ↔ JPA)
- Repository-Implementierungen (Spring Data JPA)

**Best Practices:**
- ✅ JPA Entities sind **separat** von Domain Model
- ✅ Mapping erfolgt durch MapStruct
- ✅ Repository-Implementierungen nutzen Spring Data JPA intern

## Configuration

### 📦 `config/`

**Zweck:** Spring Configuration für das Modul

**Beispiele:**
- `LendingModuleConfiguration` - Bean-Definitionen
- `LendingDataSourceConfiguration` - Datenbank-Konfiguration (Schema: `lending_schema`)

## Geschäftsregeln (Invarianten)

### Ausleihe (Loan)
- ✅ User muss aktiv sein
- ✅ Medium muss verfügbar sein (`AVAILABLE`)
- ✅ Borrowing Limit darf nicht überschritten werden
- ✅ Due Date wird nach `LoanPolicy` berechnet (abhängig von `UserGroup`)
- ✅ Max. Verlängerungen pro `LoanPolicy`
- ✅ Verlängerung nur möglich, wenn keine Vormerkung existiert

### Reservierung (Reservation)
- ✅ Medium muss `AVAILABLE` sein
- ✅ TTL: 48 Stunden (konfigurierbar), danach automatisch verfallen
- ✅ User kann nur 1 aktive Reservierung pro Medium haben

### Vormerkung (PreReservation / Waitlist)
- ✅ Medium muss `CHECKED_OUT` sein
- ✅ FIFO-Prinzip: Erste Vormerkung wird bei Rückgabe zu Reservierung
- ✅ User kann nur 1 aktive Vormerkung pro Medium haben

### Klassensatz (ClassSet)
- ✅ Nur für Lehrer (`UserGroup.TEACHER`)
- ✅ Längere Ausleihdauer (8 Wochen)
- ✅ Alle Exemplare zusammen ausleihen/zurückgeben
- ✅ Bei Rückgabe muss vollständiger Satz zurück sein

## Domain Events

| Event | Trigger | Subscriber |
|-------|---------|-----------|
| `LoanCreatedEvent` | Ausleihe erstellt | Notification Context |
| `LoanReturnedEvent` | Medium zurückgegeben | Notification, Reminding |
| `LoanOverdueEvent` | Fälligkeit überschritten | Notification, Reminding |
| `ReservationCreatedEvent` | Reservierung erstellt | Notification |
| `ReservationExpiredEvent` | Reservierung verfallen | Catalog Context |

## Testing

- **Unit Tests:** Für Domain-Logik (keine Spring-Kontext)
- **Integration Tests:** Mit Testcontainers (PostgreSQL)
- **Test-Aggregates:** In `test/.../domain/model/`

## Entwicklungsrichtlinien

1. **Domain Layer zuerst:** Implementiere Aggregate/Entities/VOs vor den Adaptern
2. **Keine Framework-Abhängigkeiten in Domain:** Domain Layer muss framework-frei bleiben
3. **Rich Domain Model:** Business-Logik gehört in Aggregates, nicht in Services
4. **Repository Ports:** Nur Interfaces in Domain, Implementierungen in Adapter
5. **Transaktionsgrenzen:** `@Transactional` nur auf Application Services
6. **Event Publishing:** Application Services publizieren Domain Events nach erfolgreicher Transaktion

## Abhängigkeiten

- **Erlaubt:** `shared` Modul (Shared Kernel)
- **Verboten:** Keine Abhängigkeiten zu anderen Modulen (außer `shared`)

## Datenbankschema

Dieses Modul verwendet das Schema: **`lending_schema`**

Flyway-Migrationen liegen in: `host-application/src/main/resources/db/migration/V2__lending_*.sql`

## Referenzen

- 📖 [Strategic Architecture Summary](../../docs/architecture/strategic-architecture-summary.md)
- 🗺️ [Bounded Contexts Map](../../docs/architecture/bounded-contexts-map.md)
- 📚 [Ubiquitous Language Glossar](../../docs/architecture/ubiquitous-language-glossar.md)
- 📬 [Domain Events & Integrations](../../docs/architecture/domain-events-integrations.md)
