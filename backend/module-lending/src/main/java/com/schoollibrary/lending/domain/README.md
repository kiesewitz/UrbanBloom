# Domain Layer - Lending Context

**Zweck:** Framework-freie Geschäftslogik (DDD Tactical Patterns)

---

## Übersicht

Die Domain Layer ist das **Herz des Lending Context**. Hier befindet sich die gesamte Geschäftslogik in Form von:
- **Aggregates** (Konsistenzgrenzen)
- **Entities** (Objekte mit Identität)
- **Value Objects** (unveränderliche Werte)
- **Domain Services** (komplexe domänenübergreifende Logik)
- **Domain Events** (Geschäftsereignisse)
- **Repository Ports** (Persistenz-Schnittstellen)

## ⚠️ WICHTIG: Framework-Freiheit

Diese Layer **DARF KEINE** Abhängigkeiten zu Frameworks haben:
- ❌ **Keine Spring-Annotationen** (außer vielleicht `@Service` für Domain Services)
- ❌ **Keine JPA-Annotationen** (`@Entity`, `@Table`, etc.)
- ❌ **Keine Jackson-Annotationen**
- ✅ **Lombok ist erlaubt:** `@Value`, `@Builder`, `@Getter` (für Value Objects)

**Grund:** Domain-Logik soll unabhängig von Infrastruktur testbar sein.

---

## Unterordner

### 📦 `model/`

**Zweck:** Aggregates, Entities und Value Objects

**Was gehört hier rein:**
- **Aggregate Roots:** `Loan`, `Reservation`, `PreReservation`, `ClassSet`
- **Entities:** Objekte mit eigener Identität
- **Value Objects:** Immutable Objekte wie `DueDate`, `Barcode`, `LoanPolicy`

**Beispiele:**
```java
// Aggregate Root
public class Loan {
    private final LoanId id;
    private final UserId userId;
    private final Barcode mediaBarcode;
    private DueDate dueDate;
    private LoanStatus status;
    private int renewalCount;

    // ✅ Business-Logik in Methoden
    public void renew(LoanPolicy policy) {
        if (!canBeRenewed(policy)) {
            throw new RenewalNotAllowedException("...");
        }
        this.dueDate = policy.calculateNewDueDate(this.dueDate);
        this.renewalCount++;
    }

    public boolean isOverdue(LocalDate today) {
        return status == LoanStatus.CHECKED_OUT && dueDate.isBefore(today);
    }
}

// Value Object
@Value
@Builder
public class DueDate {
    LocalDate date;

    public boolean isBefore(LocalDate other) {
        return date.isBefore(other);
    }
}
```

**Best Practices:**
- ✅ **Rich Domain Model:** Aggregates haben Business-Methoden
- ✅ **Invarianten schützen:** Keine öffentlichen Setter
- ✅ **Ubiquitous Language:** Methodennamen aus der Domäne
- ✅ **Value Objects sind immutable:** `@Value` (Lombok)

---

### 📦 `service/`

**Zweck:** Domain Services für Logik über mehrere Aggregates hinweg

**Wann braucht man Domain Services?**
- Wenn Business-Logik **nicht zu einem einzelnen Aggregate** passt
- Wenn Operationen **mehrere Aggregates koordinieren**
- Wenn Berechnungen **zustandslos** sind

**Beispiele:**
```java
// Domain Service
public class WaitlistProcessor {
    
    public Optional<Reservation> processNextInQueue(
        List<PreReservation> waitlist,
        MediaCopy returnedCopy
    ) {
        if (waitlist.isEmpty()) {
            return Optional.empty();
        }
        
        PreReservation first = waitlist.get(0);
        Reservation reservation = Reservation.fromPreReservation(
            first, 
            returnedCopy,
            Duration.ofHours(48)
        );
        
        return Optional.of(reservation);
    }
}
```

**Best Practices:**
- ✅ Stateless
- ✅ Nur Domain-Objekte als Parameter/Rückgabewerte
- ✅ Keine Repository-Zugriffe (das macht Application Service)

---

### 📦 `event/`

**Zweck:** Domain Events für wichtige Geschäftsereignisse

**Was sind Domain Events?**
Events, die signifikante Geschäftsereignisse repräsentieren und von anderen Contexts konsumiert werden.

**Wichtige Events:**
```java
@Value
@Builder
public class LoanCreatedEvent {
    UUID loanId;
    String userId;
    String mediaBarcode;
    LocalDate dueDate;
    Instant occurredAt;
}

@Value
@Builder
public class LoanReturnedEvent {
    UUID loanId;
    String userId;
    String mediaBarcode;
    LocalDate returnDate;
    boolean wasOverdue;
    Instant occurredAt;
}
```

**Best Practices:**
- ✅ **Immutable** (alle Felder `final`)
- ✅ **Past tense:** `LoanCreated`, nicht `CreateLoan`
- ✅ **Enthält alle Daten:** Consumer brauchen keine weitere Queries
- ✅ **Timestamp:** `occurredAt` für Event-Reihenfolge

**Event Publishing:**
Events werden **nicht hier** publiziert, sondern im **Application Service** nach erfolgreicher Transaktion.

---

### 📦 `repository/`

**Zweck:** Repository Ports (Interfaces) für Persistenz

**Was gehört hier rein:**
- Nur **Interfaces**, keine Implementierungen
- Methoden verwenden **Domain-Objekte** (nicht JPA-Entities)

**Beispiele:**
```java
public interface LoanRepository {
    Loan save(Loan loan);
    Optional<Loan> findById(LoanId id);
    List<Loan> findByUserId(UserId userId);
    List<Loan> findOverdueLoans(LocalDate today);
    void delete(LoanId id);
}

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(ReservationId id);
    List<Reservation> findExpiredReservations(Instant now);
}
```

**Best Practices:**
- ✅ **Nur Interfaces:** Implementierung in `adapter/out/persistence/`
- ✅ **Domain-Objekte:** Parameter/Rückgabewerte sind Domain-Objekte
- ✅ **Ubiquitous Language:** `findOverdueLoans()`, nicht `getLoansWhereStatusIsOverdue()`

**Dependency Rule:**
```
Domain Repository (Interface)
        ↑
        | implements
        |
Adapter Repository (Implementation)
```

---

## Entwicklungs-Workflow

### 1. **Aggregate zuerst modellieren**
   - Identifiziere Aggregate Roots
   - Definiere Invarianten (Geschäftsregeln)
   - Implementiere Business-Methoden

### 2. **Value Objects definieren**
   - Identifiziere unveränderliche Konzepte
   - Nutze `@Value` (Lombok)
   - Validierung im Constructor

### 3. **Domain Services für komplexe Logik**
   - Wenn Logik nicht in ein Aggregate passt
   - Stateless Services

### 4. **Repository Ports definieren**
   - Nur Interfaces
   - Methodensignaturen in Ubiquitous Language

### 5. **Domain Events modellieren**
   - Welche Ereignisse sind für andere Contexts relevant?
   - Immutable DTOs

### 6. **Unit Tests schreiben**
   - Domain-Logik ohne Spring-Kontext testen
   - Aggregates isoliert testen
   - Mocking nur für Repository-Ports

---

## Testing

**Unit Tests für Domain Layer:**
```java
@Test
void shouldRenewLoanWhenPolicyAllows() {
    // Given
    Loan loan = Loan.builder()
        .id(new LoanId(UUID.randomUUID()))
        .userId(new UserId("user123"))
        .mediaBarcode(new Barcode("123456"))
        .dueDate(new DueDate(LocalDate.now().plusDays(21)))
        .status(LoanStatus.CHECKED_OUT)
        .renewalCount(0)
        .build();
    
    LoanPolicy policy = LoanPolicy.builder()
        .maxRenewals(2)
        .renewalPeriodDays(21)
        .build();
    
    // When
    loan.renew(policy);
    
    // Then
    assertThat(loan.getRenewalCount()).isEqualTo(1);
    assertThat(loan.getDueDate().getDate()).isAfter(LocalDate.now().plusDays(21));
}
```

**Vorteile:**
- ✅ Schnell (kein Spring-Kontext)
- ✅ Isoliert (keine Datenbank)
- ✅ Fokussiert auf Business-Logik

---

## Anti-Patterns (vermeiden!)

❌ **Anemic Domain Model:**
```java
// FALSCH: Nur Getter/Setter, keine Logik
public class Loan {
    private UUID id;
    private String userId;
    private LocalDate dueDate;
    
    // Nur Getter/Setter...
}
```

❌ **Framework-Abhängigkeiten:**
```java
// FALSCH: JPA-Annotationen in Domain Layer
@Entity
@Table(name = "loans")
public class Loan { ... }
```

❌ **Business-Logik in Adapter:**
```java
// FALSCH: Geschäftslogik im Controller
@PostMapping("/loans/{id}/renew")
public ResponseEntity<LoanDto> renewLoan(@PathVariable UUID id) {
    Loan loan = loanRepository.findById(id);
    if (loan.getRenewalCount() >= 2) { // ❌ Geschäftsregel im Controller
        throw new BadRequestException("Max renewals reached");
    }
    loan.setDueDate(loan.getDueDate().plusDays(21)); // ❌ Berechnung im Controller
    loanRepository.save(loan);
    return ResponseEntity.ok(mapper.toDto(loan));
}
```

✅ **Richtig: Business-Logik im Aggregate:**
```java
// Domain Model
public class Loan {
    public void renew(LoanPolicy policy) {
        if (!canBeRenewed(policy)) {
            throw new RenewalNotAllowedException();
        }
        this.dueDate = policy.calculateNewDueDate(this.dueDate);
        this.renewalCount++;
    }
}

// Controller (nur Delegation)
@PostMapping("/loans/{id}/renew")
public ResponseEntity<LoanDto> renewLoan(@PathVariable UUID id) {
    RenewLoanCommand command = new RenewLoanCommand(id);
    Loan loan = renewalApplicationService.renewLoan(command);
    return ResponseEntity.ok(mapper.toDto(loan));
}
```

---

## Referenzen

- 📖 [Lending Context README](../README.md)
- 📚 [DDD Aggregates](../../../../../docs/architecture/aggregates-entities-valueobjects.md)
- 🎯 [Ubiquitous Language](../../../../../docs/architecture/ubiquitous-language-glossar.md)
