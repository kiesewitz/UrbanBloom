# Feature: Lending

**Context:** Core Domain - Ausleihe, Rückgabe, Verlängerung, Reservierung

---

## Structure

```
lending/
├── data/
│   ├── models/
│   │   ├── LoanDTO.ts          # Ausleihe-Daten
│   │   └── ReservationDTO.ts   # Reservierungs-Daten
│   └── repositories/
│       └── LoanRepository.ts   # API calls
│
└── presentation/
    ├── atoms/
    │   └── DueDateBadge.tsx    # Fälligkeitsdatum Badge
    ├── molecules/
    │   └── LoanListItem.tsx    # Einzelner Ausleihen-Eintrag
    ├── organisms/
    │   └── LoanList.tsx        # Liste aller Ausleihen
    └── pages/
        └── MyLoansPage.tsx     # Hauptseite: Meine Ausleihen
```

---

## Data Models

```typescript
// models/LoanDTO.ts
export interface LoanDTO {
  id: string;
  userId: string;
  mediaBarcode: string;
  mediaTitle: string;
  mediaAuthor: string;
  dueDate: string; // ISO 8601
  status: 'CHECKED_OUT' | 'RETURNED' | 'OVERDUE';
  renewalCount: number;
  maxRenewals: number;
  hasPreReservation?: boolean;
}
```

---

## Repository

```typescript
// repositories/LoanRepository.ts
export class LoanRepository {
  async getMyLoans(): Promise<LoanDTO[]>;
  async renewLoan(loanId: string): Promise<LoanDTO>;
  async returnLoan(loanId: string): Promise<void>;
}
```

---

## Components

**Atoms:** `DueDateBadge` - Shows due date with color coding  
**Molecules:** `LoanListItem` - Single loan entry with actions  
**Organisms:** `LoanList` - Full list with loading/empty states  
**Pages:** `MyLoansPage` - Smart component with data fetching

---

## References

- [Backend: Lending Context](../../../../backend/module-lending/README.md)
