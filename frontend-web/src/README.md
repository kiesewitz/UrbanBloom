# Frontend Web - src/ Structure

**Tech Stack:** React 18, TypeScript 5.x, Vite, Vitest, React Router  
**Architecture:** Component-Driven Development (CDD) + Atomic Design + Feature-First

---

## 📁 Structure

```
src/
├── core/                           # Cross-cutting Infrastructure
│   ├── config/                     # Environment Configuration
│   │   └── environment.ts          # Mock/Dev/Test Port Logic
│   ├── hooks/                      # Custom React Hooks
│   └── utils/                      # Helper Functions
│
├── design-system/                  # Design System
│   ├── tokens.ts                   # Colors, Spacing, Typography
│   └── components/                 # Shared Components
│       ├── atoms/                  # Basic components
│       ├── molecules/              # Composed components
│       ├── organisms/              # Complex UI blocks
│       └── templates/              # Page layouts
│
├── features/                       # Features (Bounded Contexts)
│   ├── lending/                    # Ausleihe & Reservierung
│   ├── catalog/                    # Mediensuche & Details
│   └── user/                       # Benutzerprofil
│
├── services/                       # API Services
│   └── api.service.ts
│
└── types/                          # Global Types
    └── api.types.ts
```

---

## 🎨 Architecture

### Component-Driven Development (CDD)

Build UI bottom-up:
```
Atoms → Molecules → Organisms → Templates → Pages
```

### Feature-First + DDD Alignment

Features map to Backend Bounded Contexts:
- `lending/` → Lending Context (Core Domain)
- `catalog/` → Catalog Context (Supporting)
- `user/` → User Context (Generic)

### Smart vs Presentation

**Presentation Components:** Stateless, props-driven, emit events via `onX` callbacks  
**Smart Components (Pages):** Handle data fetching, state, side effects

---

## 📦 Layers

### [design-system/](design-system/README.md)
Reusable UI components + Design Tokens

### [features/](features/README.md)
Feature modules organized by Bounded Context

### [services/](services/README.md)
API client and services

### [core/](core/README.md)
Hooks, utils, constants

---

## 🚀 Development Workflow

1. **Define Design Tokens** (`design-system/tokens.ts`)
2. **Build Atoms → Molecules → Organisms → Templates**
3. **Implement Feature Data Layer** (models, repositories)
4. **Build Feature Presentation Layer** (atoms → pages)
5. **Write Tests** (Vitest + React Testing Library)

---

## 🌍 Environment Configuration

Das Web-Frontend unterstützt verschiedene Umgebungen, die über `Vite Modes` gesteuert werden. Die Port-Logik befindet sich zentral in `src/core/config/environment.ts`.

| Umgebung | Command | Backend Port | Beschreibung |
| :--- | :--- | :--- | :--- |
| **Mock** | `npm run dev:mock` | `4010` | Nutzt lokale Mock-Daten (In-Memory/Service) |
| **Development** | `npm run dev:dev` | `8080` | Verbindet sich mit dem lokalen Dev-Backend |
| **Test** | `npm run dev:test` | `9080` | Verbindet sich mit der CI/CD Test-Instanz |

Die Konfiguration wird über `.env.[mode]` Dateien gesteuert, wobei der Port standardmäßig im Code gesetzt ist, aber über `VITE_API_BASE_URL` überschrieben werden kann.

---

## 🧪 Scripts & Testing

### Development
```bash
npm run dev:mock      # Start with Mock environment
npm run dev:dev       # Start with Development environment
npm run dev:test      # Start with Test environment
```

### Testing
```bash
npm test              # Run vitest unit tests
npm run test:ui       # Vitest UI (interactive)
npm run test:coverage # Generate coverage report
```

---

## 📚 Guidelines

- ✅ Use Design Tokens (no hardcoded colors/spacing)
- ✅ Stateless Presentation Components
- ✅ Props via destructuring, events via `onX`
- ✅ One component per file
- ✅ Test all components
- ✅ TypeScript strict mode

---

## 📖 References

- [Design System README](design-system/README.md)
- [Features README](features/README.md)
- [Backend Architecture](../docs/architecture/README.md)
