# Core Layer

**Zweck:** Cross-cutting infrastructure (hooks, utils)

---

## Structure

```
core/
├── hooks/              # Custom React Hooks
│   ├── useApi.ts      # API call hook with loading/error
│   └── useAuth.ts     # Authentication hook
│
└── utils/              # Helper functions
    ├── dateFormatter.ts
    └── validators.ts
```

---

## Custom Hooks

### `useApi.ts`

```typescript
export function useApi<T>(
  apiCall: () => Promise<T>
) {
  const [data, setData] = useState<T | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const execute = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const result = await apiCall();
      setData(result);
    } catch (err) {
      setError(err as Error);
    } finally {
      setIsLoading(false);
    }
  };

  return { data, isLoading, error, execute };
}
```

### `useAuth.ts`

```typescript
export function useAuth() {
  const [user, setUser] = useState<UserDTO | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Auth logic...

  return { user, isAuthenticated, login, logout };
}
```

---

## Utils

### `dateFormatter.ts`

```typescript
export const DateFormatter = {
  formatDueDate(date: string): string {
    const dueDate = new Date(date);
    const today = new Date();
    const diff = Math.floor((dueDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
    
    if (diff < 0) return `Überfällig seit ${-diff} Tagen`;
    if (diff === 0) return 'Heute fällig';
    if (diff === 1) return 'Morgen fällig';
    return `Fällig in ${diff} Tagen`;
  },

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('de-DE');
  },
};
```

### `validators.ts`

```typescript
export const Validators = {
  isValidEmail(email: string): boolean {
    return /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email);
  },

  isValidBarcode(barcode: string): boolean {
    return /^\d{6}$/.test(barcode);
  },
};
```

---

## Guidelines

- ✅ Hooks for reusable logic
- ✅ Utils for pure functions
- ✅ No feature-specific code
- ✅ Fully typed (TypeScript)

---

## References

- [React Hooks](https://react.dev/reference/react)
