# Prompt: Create Full-Stack Feature

## Purpose
This prompt guides the implementation of a complete full-stack feature across backend (Spring Boot DDD) and frontend (Flutter CDD) for UrbanBloom.

## When to Use
Use this when adding a new end-to-end feature that requires:
- A new API endpoint in the backend
- A new screen or significant UI component in mobile and/or admin-web
- Database schema changes and/or business logic

## Prompt Template

```
Implement the full-stack feature: [FEATURE_NAME]

Bounded context: [identity | recycling | gamification | insights]

Description:
[Short description of what the feature does from a business perspective]

Backend requirements:
- Module: module-[context]
- New aggregate/entity: [Name]
- Repository method(s): [listMethods]
- Use case(s): [Action][Entity]UseCase
- API endpoint: [METHOD] /api/v1/[path]
- Request DTO: [Entity]RequestDTO with fields [fields]
- Response DTO: [Entity]ResponseDTO with fields [fields]

Mobile requirements:
- Feature path: lib/features/[context]/
- New page: [PageName]Page
- Organism(s): [OrganismName]
- API call: [repositoryMethod]

Admin-web requirements:
- Feature path: lib/features/[context]/
- New page: [PageName]Page (if applicable)
- Widget(s): [WidgetName]
```

## Expected Output

1. **OpenAPI spec** update in `shared-resources/api-contracts/UrbanBloom-api.yaml`
2. **Backend classes**:
   - Domain entity / value objects
   - Repository interface + JPA implementation
   - Application use case
   - REST controller + DTOs
   - Unit tests (JUnit 5)
3. **Mobile Flutter code**:
   - Data layer: repository, API client method
   - Presentation: page + organism/molecule atoms
   - Widget test
4. **Admin-web Flutter code** (if applicable):
   - Data layer: repository, API client method
   - Presentation: page + components

## Constraints

- Follow DDD: no business logic in controllers, pure domain layer
- Follow CDD: no styling in pages, use design tokens
- Use Riverpod for state management in Flutter
- Validate all inputs (Bean Validation on backend, form validation on frontend)
- Add proper error handling with user-friendly messages
- Update CODEOWNERS if new bounded context files are added
