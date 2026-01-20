---
description: Führt Sie durch die taktischen Design-Entscheidungen und Implementierungsschritte basierend auf Domain-Driven Design (DDD)
tools: ['edit', 'search', 'Ref/*', 'sequentialthinking/*', , 'fetch', 'todos']
handoffs:
  - label: ➡️ OpenAP Design starten
    agent: dev-openapi-design
    prompt: 'Das Domain-Modell ist vollständig dokumentiert und visualisiert. Beginne nun mit der Implementierung. Analysiere die vorhandenen Dokumente: - docs/architecture/domain-model.md - docs/architecture/ubiquitous-language-glossar.md - docs/architecture/aggregates-entities-valueobjects.md - docs/architecture/traceability-matrix.md- docs/architecture/domain-models/*.puml Imlementiere die Aggregate, Entitäten, Reposistorie, DomainServices und Value Objects gemäß API-First DDD Workflow.'
    send: false
---

### Phase 1: Story Selection & Analysis

**Step 1.1: User Story auswählen**

```prompt
Wähle die nächste umzusetzende User Story aus docs/requirements/user-stories/refined/:

- Priorisierung: MVP > Must-Have > Should-Have > Nice-to-Have
- Abhängigkeiten: Stories mit wenigen Dependencies zuerst
- Bounded Context: Gruppiere Stories nach Bounded Context

Zeige:
- User Story ID & Titel
- Epic & Bounded Context
- Priorität & Story Points
- Abhängigkeiten zu anderen Stories
```

**Step 1.2: Story analysieren**

Analysiere die ausgewählte User Story:

1. **Akzeptanzkriterien** durchgehen
2. **Gherkin-Szenarien** identifizieren
3. **Betroffene Bounded Contexts** ermitteln
4. **Aggregates & Entities** aus Domain Model referenzieren
5. **Business Rules** aus Ubiquitous Language extrahieren

**Output:** Story Analysis Document
```markdown
# Story Analysis: [US-ID] [Titel]

## Bounded Context
- Primary: [Context Name]
- Secondary: [Context Name] (optional)

## Betroffene Aggregates
- [Aggregate Root 1]: [Beschreibung]
- [Aggregate Root 2]: [Beschreibung]

## Business Rules
1. [Rule aus Glossar]
2. [Rule aus Glossar]

## Domain Events
- [Event Name]: Wann ausgelöst?
- [Event Name]: Wann ausgelöst?

## Externe Dependencies
- [Service/System]: Wofür?
```

---

### Phase 2: API-First Design

**Step 2.1: OpenAPI Specification erstellen**

Erstelle/erweitere die OpenAPI Spec für die Story:

```prompt
Erstelle eine OpenAPI 3.0 Spezifikation für [User Story]:

Datei: api/openapi/[bounded-context].yaml

Berücksichtige:
1. RESTful Design Principles
2. Resource Naming (aus Ubiquitous Language)
3. HTTP Methods (GET, POST, PUT, PATCH, DELETE)
4. Request/Response Schemas
5. Error Responses (4xx, 5xx)
6. Authentication & Authorization (Bearer Token)
7. Pagination für Listen
8. Filtering & Sorting
9. HATEOAS Links (optional)

Verwende Schemas aus:
- docs/architecture/aggregates-entities-valueobjects.md
- docs/architecture/ubiquitous-language-glossar.md
```

Verwende das template: `api/openapi/bounded-context.template.yaml`

Speichere das OpenAPI Spec unter `docs/api/[bounded-context].yaml`


**Step 2.2: API Review**

```prompt
Überprüfe die OpenAPI Spec jedes bounded-contexts gegen:

1. ✅ Naming Conventions (aus Ubiquitous Language)
2. ✅ RESTful Best Practices
3. ✅ Vollständigkeit aller Akzeptanzkriterien
4. ✅ Error Handling
5. ✅ Security (Authentication/Authorization)
6. ✅ Pagination & Filtering
7. ✅ Documentation Quality
8. ✅ Schema Validations

Erstelle Review-Checklist und dokumentiere Findings.

---

### Step 2.3: CLI-gestützte Code- und Mock-Generierung (Praktische Befehle)

Nachdem die OpenAPI-Spezifikation erstellt und validiert ist, lassen sich Server-Stubs, Client-SDKs und Mock-Server automatisiert erzeugen. Die folgenden, verifizierten Beispiele verwenden `openapi-generator-cli` (Server + Clients) und `Prism` (Mock server). Passen Sie die Pfade/Optionen für Ihr Repo an.

- Validate spec (quick check):

```
openapi-generator-cli validate -i backend/schoollibrary-app/src/main/resources/api/ausleihe-api.yaml
```

- Generate Spring Boot server stub (example):

```
openapi-generator-cli generate \
  -i backend/schoollibrary-app/src/main/resources/api/ausleihe-api.yaml \
  -g spring \
  -o backend/ausleih-service/generated \
  --additional-properties=library=spring-boot,useBeanValidation=true
```

- Generate TypeScript Axios client for the web admin:

```
openapi-generator-cli generate \
  -i backend/schoollibrary-app/src/main/resources/api/ausleihe-api.yaml \
  -g typescript-axios \
  -o frontend-web/src/api/generated/ausleihe \
  --additional-properties=supportsES6=true,withSeparateModelsAndApi=true
```

- Generate Dart Dio client for the Flutter app:

```
openapi-generator-cli generate \
  -i backend/schoollibrary-app/src/main/resources/api/ausleihe-api.yaml \
  -g dart-dio \
  -o frontend-mobile/lib/core/api/generated/ausleihe \
  --additional-properties=pubName=schulbib_api,useEnumExtension=true
```

- Alternative: generate a WireMock-based mock server with OpenAPI Generator (java-wiremock):

```
openapi-generator-cli generate \
  -i backend/schoollibrary-app/src/main/resources/api/ausleihe-api.yaml \
  -g java-wiremock \
  -o build/mocks/ausleihe-wiremock
```

- Recommended lightweight mock server: Prism (no codegen required) — run a dynamic mock from the OpenAPI spec:

```
# install (once)
npm install -g @stoplight/prism-cli

# run mock server (bind to 0.0.0.0 for container/Docker usage)
prism mock backend/schoollibrary-app/src/main/resources/api/ausleihe-api.yaml -p 4010 -h 0.0.0.0

# or using npx (no global install)
npx @stoplight/prism-cli mock backend/schoollibrary-app/src/main/resources/api/ausleihe-api.yaml -p 4010
```

Notes:
- `openapi-generator-cli` accepts generator-specific options via `--additional-properties` or a `-c` config file. Use `openapi-generator-cli config-help -g <generator>` to list options for a generator.
- For reproducible CI runs, save generator options in a small YAML config file and call `openapi-generator-cli generate -c tools/openapi/<name>.yaml` or use the `batch` command.
- Place generated code under `generated/` folders (or the paths above) and add a short README explaining regeneration commands so developers know how to update stubs/clients.

---

### Step 2.4: Integrationshinweise

- Add a script/task in your repo (Makefile / npm script / npm-run) that runs the validated generation steps; include `validate` before `generate` and a post-generation formatting step when applicable.
- Document the generated outputs and any manual glue-code required (e.g., mapping generated DTOs to domain types in `backend/module-*` oder Flutter data mappers in `frontend-mobile/features/*/data`).

---

Update the OpenAPI-First workflow to include these CLI commands in CI (optional):

- CI job: `validate-spec` — runs `openapi-generator-cli validate`
- CI job: `generate-clients` — runs generation steps and commits generated clients to a dedicated branch or artifact store
- CI job: `run-mocks` — spin up `Prism` for contract testing against frontend E2E or component tests

```