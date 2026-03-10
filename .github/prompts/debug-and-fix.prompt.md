# Prompt: Fix Bug or Error

## Purpose
This prompt guides systematic debugging and bug-fixing across the UrbanBloom stack, ensuring root-cause analysis and proper resolution in the correct architectural layer.

## When to Use
Use this prompt when:
- A runtime error occurs (Flutter exception, Spring exception, Docker issue)
- A test is failing
- Keycloak authentication is not working
- API request/response is incorrect

## Prompt Template

```
Fix the following bug in UrbanBloom:

Error message / symptom:
[Paste the full error message, stack trace, or describe the symptom]

Where it occurs:
[e.g., "Mobile app when tapping Submit on RecyclingSubmissionPage"]
[e.g., "Server returns 500 on POST /api/v1/recycling/submissions"]
[e.g., "Keycloak login redirect loop in admin-web"]

Affected component(s):
- Service/class: [ClassName or file path]
- Bounded context: [identity | recycling | gamification | insights]
- Layer: [domain | application | infrastructure | presentation | UI]

Steps to reproduce:
1. [Step 1]
2. [Step 2]

Expected behavior:
[What should happen]

Actual behavior:
[What currently happens]
```

## Debugging Checklist

### Backend (Spring Boot)
- [ ] Check domain invariants — does the exception come from a domain rule violation?
- [ ] Verify transaction boundaries — is `@Transactional` applied at the correct level?
- [ ] Review DTO validation — is `@Valid` missing on the controller method?
- [ ] Check security config — is the endpoint protected correctly in `SecurityConfig`?
- [ ] Review logs at `DEBUG` level: look for hibernate SQL, token parsing errors

### Frontend (Flutter)
- [ ] Check `mounted` guard before `setState` / Riverpod provider calls
- [ ] Verify API call error handling — is `on DioException` catching the right status code?
- [ ] Check Riverpod provider — is `autoDispose` causing state to reset unexpectedly?
- [ ] Ensure `flutter pub get` ran after `pubspec.yaml` changes
- [ ] Run `flutter analyze` to catch static issues

### Keycloak / Docker
- [ ] Verify realm is imported: `docker compose exec keycloak /opt/keycloak/bin/kcadm.sh get realms --server http://localhost:8081 -r master --user admin --password admin`
- [ ] Check client secret matches in application.properties / .env
- [ ] Confirm redirect URIs include the running app URL
- [ ] Inspect Keycloak logs: `docker compose logs keycloak --tail=100`
- [ ] Mailpit available at http://localhost:8025 for email verification

## Expected Output

1. Root cause analysis explaining **which layer** caused the bug
2. Minimal code change to fix the issue
3. Explanation of why this fix prevents recurrence
4. A regression test (unit or widget) to catch this in the future
