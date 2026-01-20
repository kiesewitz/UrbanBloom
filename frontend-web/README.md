# Frontend-Web (Admin Dashboard)

React + TypeScript web application for the Digital School Library Admin interface.

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ and npm
- Backend running on `http://localhost:8080`

### Installation

```bash
# Navigate to frontend-web directory
cd frontend-web

# Install dependencies
npm install

# Start development server
npm run dev
```

The application will be available at `http://localhost:5173`

## 📦 Available Scripts

```bash
# Development
npm run dev          # Start dev server with HMR

# Production
npm run build        # Build for production
npm run preview      # Preview production build

# Testing
npm run test         # Run tests in watch mode
npm run test:ui      # Run tests with UI
npm run test:coverage # Generate coverage report

# Code Quality
npm run lint         # Run ESLint
```

## 🏗️ Project Structure

```
frontend-web/
├── src/
│   ├── components/          # React components
│   │   └── Dashboard/       # Dashboard component
│   ├── services/            # API services
│   │   └── api.service.ts   # Backend API client
│   ├── types/               # TypeScript type definitions
│   │   └── api.types.ts     # API response types
│   ├── test/                # Test setup
│   │   └── setup.ts         # Vitest setup
│   ├── App.tsx              # Main App with routing
│   ├── main.tsx             # Application entry point
│   └── index.css            # Global styles
├── index.html               # HTML entry point
├── vite.config.ts           # Vite configuration
├── tsconfig.json            # TypeScript configuration
└── package.json             # Dependencies
```

## 🛠️ Technology Stack

- **React 18.3** - UI framework
- **TypeScript 5.6** - Type safety
- **Vite 5.4** - Build tool & dev server
- **React Router 6** - Client-side routing
- **Axios** - HTTP client for API communication
- **Vitest** - Unit testing framework
- **Testing Library** - React component testing

## 🎯 Features

### Current (Hello World)
- ✅ Health check dashboard
- ✅ Backend status monitoring
- ✅ Application info display
- ✅ Type-safe API client
- ✅ Basic routing setup
- ✅ Component tests

### Planned
- User authentication (Keycloak)
- Catalog management
- Lending operations
- User management
- Notification center

## 🔧 Configuration

### Environment Variables

Create a `.env` file based on `.env.example`:

```env
VITE_API_BASE_URL=http://localhost:8080
```

### API Endpoints

The application communicates with these backend endpoints:
- `GET /health` - Health check
- `GET /api/v1/app/info` - Application information

## 🧪 Testing

Tests are written using Vitest and React Testing Library:

```bash
# Run all tests
npm run test

# Run tests with UI
npm run test:ui

# Generate coverage report
npm run test:coverage
```

Example test location: [Dashboard.test.tsx](src/components/Dashboard/Dashboard.test.tsx)

## 📝 Code Style

- TypeScript strict mode enabled
- ESLint for code quality
- Functional components with hooks
- Type-safe API communication
- Component-driven development

## 🚢 Production Build

```bash
# Create optimized production build
npm run build

# Preview production build locally
npm run preview
```

Build output will be in the `dist/` directory.

## 🔗 Related Documentation

- [Backend README](../backend/README.md)
- [Project Architecture](../docs/architecture/README.md)
- [API Documentation](../docs/api/)

## 📌 Issue Reference

This frontend setup is part of issue [#3](https://github.com/ukondert/pr_digital-school-library/issues/3) - Mono-Repo Project Structure.

## ⚠️ Current Limitations

- Basic Hello World implementation only
- No authentication yet (Keycloak integration planned)
- No real business logic (placeholder for infrastructure testing)
- Limited error handling

## 🤝 Contributing

1. Follow TypeScript strict mode guidelines
2. Write tests for new components
3. Use functional components with hooks
4. Keep components small and focused
5. Document complex logic with comments

## 📄 License

Part of the Digital School Library project.
