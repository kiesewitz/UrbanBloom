# CI/CD Workflows Overview

Status: Draft
Last Updated: 2026-01-04
Scope: GitHub Actions for backend, frontend-web, frontend-mobile

## Triggers
- push: main, develop, feature/**
- pull_request: main
- workflow_dispatch: manual
- workflow_call: reusable from other workflows

## Jobs (per workflow)
- CI Backend: Java 21, Maven `clean verify`, cache `~backend/.m2/repository`
- CI Frontend Web: Node 20, `npm ci`, `npm run lint`, `npm test`, `npm run build`, caches npm + `frontend-web/node_modules`
- CI Frontend Mobile: Flutter stable, `flutter pub get`, `flutter config --enable-web`, `flutter analyze`, `flutter test`, `flutter build web --release`, pub cache enabled

## Caching
- Maven: `~/backend/.m2/repository` keyed by all backend `pom.xml`
- npm: setup-node cache + `frontend-web/node_modules`
- Flutter: pub cache via `subosito/flutter-action@v2` (`cache: true`)

## Secrets / Credentials
- Aktuell keine Secrets benötigt.
- Wenn zukünftig nötig: hinterlege als Repository Secret; Zugriff nur via `secrets.<NAME>` in Workflows. Keine Hardcodierung in YAML.

## Local parity commands
```bash
# Backend
cd backend && mvn -B -Dmaven.repo.local=$HOME/backend/.m2/repository clean verify

# Frontend-Web
cd frontend-web && npm ci && npm run lint && npm test && npm run build

# Frontend-Mobile (web target)
cd frontend-mobile && flutter pub get && flutter config --enable-web && flutter analyze && flutter test && flutter build web --release
```

## Notes
- Jobs are reusable via `workflow_call` for future orchestration workflows.
- Keep toolchain versions aligned (Java 21, Node 20, Flutter stable) to avoid drift.
