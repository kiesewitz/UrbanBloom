# Lessons Learned: Flutter Web + Spring Boot Backend Integration (CORS & Security)

**Datum:** 28. Dezember 2025  
**Kontext:** Integration der Flutter Mobile App (Web) mit Spring Boot Backend

---

## Problem 1: CORS-Fehler bei Flutter Web zu Backend-Kommunikation

### Symptom
```
DioException [connection error]: The connection errored: The XMLHttpRequest onError callback was called. 
This typically indicates an error on the network layer.
```

### Ursache
- Flutter Web läuft im Browser und unterliegt Browser-CORS-Policies
- Spring Boot hatte keine CORS-Konfiguration
- Browser blockiert HTTP-Anfragen zu anderen Origins standardmäßig

### Lösung
1. **Neue Klasse erstellt:** `CorsConfig.java`
   ```java
   @Configuration
   public class CorsConfig {
       @Bean
       public CorsConfigurationSource corsConfigurationSource() {
           // Konfiguration mit allowedOriginPatterns für localhost:*
       }
   }
   ```

2. **Wichtige Konfigurationen:**
   - `allowedOriginPatterns`: `http://localhost:*`, `http://127.0.0.1:*` (Flutter Web nutzt zufällige Ports)
   - `allowedMethods`: GET, POST, PUT, PATCH, DELETE, OPTIONS
   - `allowedHeaders`: Authorization, Content-Type, Accept, etc.
   - `allowCredentials`: true (für Authentifizierung)
   - `maxAge`: 3600L (Cache für Preflight-Requests)

### Learnings
- ✅ Flutter Web Debug-Mode nutzt zufällige Ports → `allowedOriginPatterns` statt feste Ports
- ✅ CORS muss sowohl in Spring Security als auch separat konfiguriert werden
- ⚠️ In Produktion: Patterns durch konkrete URLs ersetzen

---

## Problem 2: Bean-Konflikt beim Spring Boot Start

### Symptom
```
Parameter 0 of constructor in SecurityConfig required a single bean, but 2 were found:
- corsConfigurationSource
- mvcHandlerMappingIntrospector
```

### Ursache
- Spring Boot erstellt automatisch einen `mvcHandlerMappingIntrospector` Bean
- Unser `CorsConfig` erstellt ebenfalls einen `CorsConfigurationSource` Bean
- Constructor-Injection ohne `@Qualifier` war ambigue

### Lösung
```java
public SecurityConfig(@Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource) {
    this.corsConfigurationSource = corsConfigurationSource;
}
```

### Learnings
- ✅ Immer `@Qualifier` verwenden bei mehreren Beans des gleichen Typs
- ✅ Bean-Namen explizit im `@Bean`-Annotation setzen verhindert Konflikte

---

## Problem 3: Veraltete Spring Security Syntax

### Symptom
```
WARNING: cors() in HttpSecurity ist veraltet und wurde zum Entfernen markiert
WARNING: and() in SecurityConfigurerAdapter ist veraltet
WARNING: csrf() in HttpSecurity ist veraltet
```

### Ursache
- Spring Security 6.x verwendet neue Lambda-DSL
- Alte `.and()` Chaining-Syntax ist deprecated

### Lösung
**Vorher (deprecated):**
```java
http
    .cors().configurationSource(corsConfigurationSource)
    .and()
    .csrf().disable()
    .and()
    .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
```

**Nachher (modern):**
```java
http
    .cors(cors -> cors.configurationSource(corsConfigurationSource))
    .csrf(csrf -> csrf.disable())
    .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    )
```

### Learnings
- ✅ Spring Security 6.x Lambda-DSL ist prägnanter und typsicherer
- ✅ Keine `.and()` Aufrufe mehr notwendig
- ✅ Bessere IDE-Unterstützung durch Lambda-Scoping

---

## Problem 4: 401 Unauthorized - Endpoint nicht freigegeben

### Symptom
```
DEBUG: Securing GET /api/v1/health
Status: 401 Unauthorized
```

### Ursache
- Frontend ruft `/api/v1/health` auf
- SecurityConfig erlaubte nur `/health` und `/health/**`
- Endpoint war nicht in der `permitAll()`-Liste

### Lösung
```java
.authorizeHttpRequests(authz -> authz
    .requestMatchers("/health", "/health/**", "/api/v1/health").permitAll()
    .requestMatchers("/api/v1/app/info").permitAll()
    .anyRequest().authenticated()
)
```

### Learnings
- ✅ Security-Konfiguration muss exakt mit den tatsächlichen Endpoint-Pfaden übereinstimmen
- ✅ Bei öffentlichen Health-Checks: Alle Varianten freigeben (`/health`, `/api/v1/health`)
- ⚠️ Pattern-Matching beachten: `/health/**` matched nicht `/api/v1/health`

---

## Problem 5: 404 Not Found - Controller-Mapping fehlt

### Symptom
```
DEBUG: Resource not found
Status: 404 NOT_FOUND
NoResourceFoundException: No static resource api/v1/health
```

### Ursache
- `HealthController` hatte `@RequestMapping` ohne Pfad
- Endpoint war unter `/health` erreichbar, nicht unter `/api/v1/health`
- Frontend erwartete `/api/v1/health`

### Lösung
```java
@RestController
@RequestMapping("/api/v1")  // Präfix hinzugefügt
public class HealthController {
    @GetMapping("/health")  // → /api/v1/health
    public ResponseEntity<HealthStatusDto> health() { ... }
}
```

### Learnings
- ✅ API-Versionierung konsequent über `@RequestMapping` Präfix
- ✅ Controller-Pfade müssen mit Frontend-Erwartungen übereinstimmen
- ✅ Security-Freigabe allein reicht nicht - Controller muss Endpoint auch bereitstellen

---

## Best Practices - Checkliste für Flutter Web + Spring Boot

### Backend (Spring Boot)
- [ ] CORS-Konfiguration mit `allowedOriginPatterns` für Entwicklung
- [ ] `@Qualifier` bei mehreren Beans des gleichen Typs
- [ ] Moderne Spring Security 6.x Lambda-DSL verwenden
- [ ] Security `permitAll()` für öffentliche Endpoints (Health, Info)
- [ ] API-Versionierung über `@RequestMapping("/api/v1")` konsequent durchziehen
- [ ] Endpoint-Pfade müssen in Security und Controller übereinstimmen

### Frontend (Flutter Web)
- [ ] Basis-URL konfigurierbar halten (localhost vs. Production)
- [ ] CORS-Fehler vs. andere Netzwerkfehler unterscheiden
- [ ] Timeout-Werte für Entwicklung erhöhen

### Testing
- [ ] Health-Check als ersten Integrationstest
- [ ] Browser DevTools Network-Tab für CORS-Debugging
- [ ] Spring Boot Debug-Logs aktivieren: `logging.level.org.springframework.security=DEBUG`

---

## Debugging-Workflow

1. **CORS-Fehler?**
   - Browser DevTools → Network Tab → Preflight (OPTIONS) Request prüfen
   - Backend CORS-Header prüfen: `Access-Control-Allow-Origin`

2. **401 Unauthorized?**
   - Spring Security Debug-Logs prüfen: `o.s.security.web.FilterChainProxy`
   - Security `permitAll()` Patterns validieren

3. **404 Not Found?**
   - Controller `@RequestMapping` + `@GetMapping` Pfade prüfen
   - Spring Boot Startup-Logs: `s.w.s.m.m.a.RequestMappingHandlerMapping` zeigt alle Mappings

4. **Bean-Konflikt?**
   - `@Qualifier` oder `@Primary` verwenden
   - Bean-Namen in `@Bean(name = "...")` explizit setzen

---

## Weiterführende Ressourcen

- [Spring Security CORS Documentation](https://docs.spring.io/spring-security/reference/reactive/integrations/cors.html)
- [Flutter Web Cross-Origin Issues](https://docs.flutter.dev/platform-integration/web/faq#how-do-i-solve-cors-problems)
- [Spring Security Migration Guide 5.x → 6.x](https://docs.spring.io/spring-security/reference/migration/servlet/configuration.html)
