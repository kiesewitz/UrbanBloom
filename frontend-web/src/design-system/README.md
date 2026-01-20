# Design System

**Zweck:** Reusable UI components + Design Tokens

---

## Structure

```
design-system/
├── tokens.ts                   # Design Tokens (Colors, Spacing, Typography)
└── components/
    ├── atoms/                  # Button, Badge, Input
    ├── molecules/              # Card, SearchBar, FormField
    ├── organisms/              # List, Grid, Navigation
    └── templates/              # PageTemplate, ListTemplate
```

---

## Design Tokens

**File:** `tokens.ts`

```typescript
export const DesignTokens = {
  // Colors
  colors: {
    primary: '#1976D2',
    secondary: '#FFA726',
    error: '#D32F2F',
    success: '#388E3C',
    background: '#F5F5F5',
    surface: '#FFFFFF',
    textPrimary: '#212121',
    textSecondary: '#757575',
  },
  
  // Spacing (4px grid)
  spacing: {
    xs: '4px',
    sm: '8px',
    md: '16px',
    lg: '24px',
    xl: '32px',
  },
  
  // Typography
  typography: {
    headline: { fontSize: '24px', fontWeight: 700 },
    body: { fontSize: '16px', fontWeight: 400 },
    label: { fontSize: '14px', fontWeight: 500 },
  },
  
  // Borders
  borderRadius: {
    sm: '4px',
    md: '8px',
    lg: '16px',
  },
} as const;
```

---

## Atomic Design

### Atoms
Basic components: `Button`, `StatusBadge`, `Input`

```tsx
// atoms/Button/Button.tsx
interface ButtonProps {
  text: string;
  onClick?: () => void;
  variant?: 'primary' | 'secondary';
  disabled?: boolean;
}

export const Button: React.FC<ButtonProps> = ({ 
  text, onClick, variant = 'primary', disabled 
}) => (
  <button 
    className={`btn btn-${variant}`} 
    onClick={onClick}
    disabled={disabled}
  >
    {text}
  </button>
);
```

### Molecules
Composed components: `MediaCard`, `LoanListItem`

### Organisms
Complex UI blocks: `LoanList`, `MediaGrid`

### Templates
Page layouts: `PageTemplate`, `ListPageTemplate`

---

## Guidelines

- ✅ Consume Design Tokens (no hardcoded values)
- ✅ Stateless components
- ✅ Props via destructuring
- ✅ Events via `onX` callbacks
- ✅ One component per file with `.tsx` + `.css`
- ✅ Test with Vitest + React Testing Library

---

## Testing

```tsx
// Button.test.tsx
import { render, screen, fireEvent } from '@testing-library/react';
import { Button } from './Button';

test('renders button text', () => {
  render(<Button text="Click me" />);
  expect(screen.getByText('Click me')).toBeInTheDocument();
});

test('calls onClick when clicked', () => {
  const handleClick = vi.fn();
  render(<Button text="Click me" onClick={handleClick} />);
  fireEvent.click(screen.getByText('Click me'));
  expect(handleClick).toHaveBeenCalledTimes(1);
});
```

---

## References

- [Atomic Design](https://bradfrost.com/blog/post/atomic-web-design/)
- [React Documentation](https://react.dev/)
