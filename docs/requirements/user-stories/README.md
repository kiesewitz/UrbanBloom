# User Stories

This directory contains refined user stories for the Digital School Library project.

## Refined Must-Have User Stories (MVP)

The following refined user stories have been created with detailed acceptance criteria, tasks, and technical notes:

1. **[US-USR-01-REF - User Authentication](./refined/US-USR-01-REF_user-authentication.md)** - Secure login for students and staff
2. **[US-USR-02-REF - Self-Registration](./refined/US-USR-02-REF_self-registration.md)** - Account creation and email validation for new users
3. **[US-CAT-01-REF - Book Catalog Browsing](./refined/US-CAT-01-REF_book-catalog-browsing.md)** - Browse and discover books in the library catalog
4. **[US-CAT-02-REF - Book Search](./refined/US-CAT-02-REF_book-search.md)** - Search for books by title, author, or ISBN
5. **[US-CAT-03-REF - Book Details View](./refined/US-CAT-03-REF_book-details-view.md)** - View detailed information about a specific book
6. **[US-LEN-01-REF - Book Borrowing Request](./refined/US-LEN-01-REF_book-borrowing-request.md)** - Request to borrow an available book
7. **[US-LEN-02-REF - My Borrowed Books](./refined/US-LEN-02-REF_my-borrowed-books.md)** - View current borrowed books and due dates

## Refined Folder (Kopie, neue Benennung)

Zusätzlich werden die *refined* User Stories auch unter `./refined/` abgelegt (Kopie), um die neue kontextbasierte Benennung konsistent zu halten:

- Schema: `US-<CTX>-NN-REF_<slug>.md`
- Beispiele:
	- `US-USR-01-REF_user-authentication.md`
	- `US-CAT-01-REF_book-catalog-browsing.md`
	- `US-LEN-01-REF_book-borrowing-request.md`

Siehe Issue #13: https://github.com/ukondert/pr_digital-school-library/issues/13

## Refinement Process

Each refined user story includes:

### Story Description
- **As a** [role]
- **I want to** [feature]
- **So that** [benefit]

### Priority Classification
- Must-Have | MVP Phase X

### Refinement Discussion
- Clarification points and decisions table
- Key technical and business decisions made

### Derived Tasks
Organized by area:
- Backend (Spring Boot/Java)
- Frontend Web (React/TypeScript)
- Frontend Mobile (Flutter/Dart)
- Testing

### Detailed Acceptance Criteria
- **Functional**: User-facing features and behaviors
- **Non-Functional**: Performance, security, accessibility
- **Technical**: API contracts, data models, protocols

### Technical Notes
- Backend architecture with code examples
- Frontend architecture with code examples
- Database schema
- API examples with request/response
- Integration points

### Definition of Done
- Checklist of completion criteria

### Dependencies and Risks
- Required prerequisites
- Known risks and mitigation strategies
- Open questions

## Development Guidelines

- Follow the technical implementations outlined in each user story
- Backend uses Spring Boot, Spring Security, JPA/Hibernate, PostgreSQL
- Frontend Web uses React, TypeScript, Material-UI
- Frontend Mobile uses Flutter/Dart with Provider state management
- All APIs follow REST principles with OpenAPI 3.0 documentation
- Authentication via Keycloak OAuth2/OpenID Connect
- Test coverage required: Unit, Integration, E2E

## References
- Architecture Documentation: `docs/architecture/`
- API Documentation: Generated via OpenAPI/Swagger
- Development Setup: See root `README.md`

## Benennungs-Konvention für User Stories

Ab jetzt verwenden User-Story-Dateien ein kontextbasiertes Präfix im Dateinamen: `US-<CTX>-NN-beschreibung.md`,
z.B. `US-CAT-01-katalog-suche.md` für Catalog, `US-LEN-01-reservierung-vormerkung.md` für Lending, `US-USR-01-benutzerkonto-sso.md` für User.

Zweck: bessere Auffindbarkeit, Traceability zu Bounded Contexts und Verlinkung zu UI-Prototypen.

Details und Diskussion: https://github.com/ukondert/pr_digital-school-library/issues/13
