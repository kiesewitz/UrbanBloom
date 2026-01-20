# API Review: Nutzerprofil (User Context)

**Review Date:** 2025-12-18
**Reviewer:** GitHub Copilot
**Scope:** Registration & Login (US-001)
**Spec File:** `docs/api/nutzerprofil.yaml`

## 1. User Story Analysis (US-001)

**Requirements:**
- **Registration:** Only allowed with email from configured school domain.
- **Password:** Must be set during first login (registration).
- **Login:** Email + Password for subsequent logins.
- **Configuration:** Admin can configure allowed domains.

**Conflict Note:**
- The Ubiquitous Language Glossary states "No local passwords, only SSO".
- However, US-001 explicitly requires "Password initialization" and "Login with Email/Password".
- **Decision:** The API follows US-001 (Local Password Auth) as the current requirement.

## 2. API Specification Review

### ✅ Completeness
- `/auth/register` (POST): Handles registration with password.
- `/auth/login` (POST): Handles login with credentials.
- `/config/domains` (PUT): Handles domain configuration.

### ✅ Naming & REST
- Resource naming (`/auth`, `/config`) is clear, though `/auth` is RPC-style (common for auth).
- HTTP Methods (POST for actions, GET/PUT for config) are correct.

### ✅ Security
- `/config/domains` is secured with `bearerAuth`.
- `/auth` endpoints are public (as expected).

## 3. Test Plan / Verification Cases

The following test cases define the expected behavior of the API and should be used for implementation testing (Integration Tests).

### 3.1 Registration (POST /auth/register)

| ID | Scenario | Input Data | Expected Status | Expected Result |
|----|----------|------------|-----------------|-----------------|
| **TC-001** | **Successful Registration** | `email: "student@schule.de"`, `password: "Secure123!"` | **201 Created** | User created, Token returned (optional) or ID |
| **TC-002** | **Invalid Domain** | `email: "hacker@evil.com"`, `password: "123"` | **400 Bad Request** | Error: "Domain not allowed" |
| **TC-003** | **Weak Password** | `email: "student@schule.de"`, `password: "123"` | **400 Bad Request** | Error: "Password too weak" |
| **TC-004** | **Duplicate Email** | `email: "existing@schule.de"`, `password: "..."` | **409 Conflict** | Error: "User already exists" |

### 3.2 Login (POST /auth/login)

| ID | Scenario | Input Data | Expected Status | Expected Result |
|----|----------|------------|-----------------|-----------------|
| **TC-005** | **Successful Login** | `email: "student@schule.de"`, `password: "Secure123!"` | **200 OK** | JWT Token in response |
| **TC-006** | **Wrong Password** | `email: "student@schule.de"`, `password: "Wrong!"` | **401 Unauthorized** | Error: "Invalid credentials" |
| **TC-007** | **Unknown User** | `email: "unknown@schule.de"`, `password: "..."` | **401 Unauthorized** | Error: "Invalid credentials" |

### 3.3 Domain Configuration (PUT /config/domains)

| ID | Scenario | Input Data | Expected Status | Expected Result |
|----|----------|------------|-----------------|-----------------|
| **TC-008** | **Update Domains (Admin)** | Header: `AdminToken`, Body: `["schule.de", "lehrer.de"]` | **200 OK** | List updated |
| **TC-009** | **Update Domains (Student)** | Header: `StudentToken`, Body: `["evil.com"]` | **403 Forbidden** | Error: "Access denied" |
| **TC-010** | **Update Domains (Anon)** | Header: `None`, Body: `["evil.com"]` | **401 Unauthorized** | Error: "Missing token" |

## 4. Findings & Action Items

- [x] **Spec is valid** against US-001.
- [ ] **Implementation Task:** Ensure `DomainConfiguration` entity exists and is pre-seeded or configurable.
- [ ] **Implementation Task:** Implement `EmailValidator` service checking against `DomainConfiguration`.
