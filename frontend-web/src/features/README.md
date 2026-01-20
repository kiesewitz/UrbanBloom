# Features Layer

**Zweck:** Feature modules organized by Bounded Contexts

---

## Structure

```
features/
├── lending/        # Core Domain - Ausleihe & Reservierung
├── catalog/        # Supporting - Mediensuche & Details
└── user/           # Generic - Benutzerprofil
```

---

## Feature Convention

Each feature follows this structure:

```
<feature>/
├── data/
│   ├── models/                 # TypeScript interfaces/types
│   └── repositories/           # API calls
│
└── presentation/
    ├── atoms/                  # Feature-specific atoms
    ├── molecules/              # Feature-specific molecules
    ├── organisms/              # Feature-specific organisms
    └── pages/                  # Smart components
```

---

## Data Layer

### Models (`data/models/`)

```typescript
// models/LoanDTO.ts
export interface LoanDTO {
  id: string;
  userId: string;
  mediaTitle: string;
  dueDate: string;
  status: 'CHECKED_OUT' | 'RETURNED' | 'OVERDUE';
  renewalCount: number;
  maxRenewals: number;
}
```

### Repositories (`data/repositories/`)

```typescript
// repositories/LoanRepository.ts
import { apiClient } from '@/services/api.service';
import type { LoanDTO } from '../models/LoanDTO';

export class LoanRepository {
  async getMyLoans(): Promise<LoanDTO[]> {
    const response = await apiClient.get<LoanDTO[]>('/loans');
    return response.data;
  }

  async renewLoan(loanId: string): Promise<LoanDTO> {
    const response = await apiClient.post<LoanDTO>(`/loans/${loanId}/renew`);
    return response.data;
  }

  async returnLoan(loanId: string): Promise<void> {
    await apiClient.post(`/loans/${loanId}/return`);
  }
}

export const loanRepository = new LoanRepository();
```

---

## Presentation Layer

### Atoms
Feature-specific: `DueDateBadge`, `RenewalCountChip`

### Molecules
Feature-specific: `LoanListItem`, `MediaCard`

### Organisms
Feature-specific: `LoanList`, `MediaGrid`

### Pages (Smart Components)

```typescript
// pages/MyLoansPage.tsx
import { useState, useEffect } from 'react';
import { loanRepository } from '../data/repositories/LoanRepository';
import { LoanList } from '../presentation/organisms/LoanList';
import type { LoanDTO } from '../data/models/LoanDTO';

export const MyLoansPage: React.FC = () => {
  const [loans, setLoans] = useState<LoanDTO[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadLoans();
  }, []);

  const loadLoans = async () => {
    setIsLoading(true);
    try {
      const data = await loanRepository.getMyLoans();
      setLoans(data);
    } catch (error) {
      console.error('Failed to load loans:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRenew = async (loanId: string) => {
    await loanRepository.renewLoan(loanId);
    loadLoans(); // Refresh
  };

  return (
    <LoanList 
      loans={loans} 
      isLoading={isLoading}
      onRenew={handleRenew}
    />
  );
};
```

---

## Bounded Contexts Alignment

| Feature | Backend Context | Domain Type |
|---------|----------------|-------------|
| `lending/` | Lending Context | Core Domain |
| `catalog/` | Catalog Context | Supporting |
| `user/` | User Context | Generic |

---

## Guidelines

- ✅ Feature-first organization
- ✅ Data layer separate from presentation
- ✅ Smart components = Pages only
- ✅ Repository pattern for API calls
- ✅ TypeScript interfaces for all DTOs
- ✅ Test presentation components

---

## Adding New Features

1. Create feature folder: `features/<feature>/`
2. Add data layer: models + repositories
3. Build presentation: atoms → molecules → organisms → pages
4. Add routes to router
5. Write tests

---

## References

- [Backend Architecture](../../../docs/architecture/README.md)
- [Bounded Contexts Map](../../../docs/architecture/bounded-contexts-map.md)
