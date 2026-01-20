package com.schoollibrary.app.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Security Configuration for the application.
 * Configures OAuth2 Resource Server with Keycloak.
 *
 * @author Backend Team
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(@Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Value("${KEYCLOAK_CLIENT_ID:schoollibrary-app}")
    private String keycloakClientId;

    /**
     * Configure the security filter chain.
     * For Hello World testing, we allow public access to /health and
     * /api/v1/app/info.
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/health", "/health/**", "/api/v1/health").permitAll()
                        .requestMatchers("/api/v1/app/info").permitAll()
                        .requestMatchers("/api/v1/registration", "/api/v1/registration/**").permitAll()
                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/allowed-domains",
                                "/api/v1/auth/password/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractKeycloakAuthorities);
        return converter;
    }

    private Collection<GrantedAuthority> extractKeycloakAuthorities(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        Object realmAccessObj = jwt.getClaim("realm_access");
        if (realmAccessObj instanceof Map<?, ?> realmAccess) {
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj instanceof List<?> roles) {
                for (Object role : roles) {
                    if (role instanceof String r && !r.isBlank()) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
                    }
                }
            }
        }

        Object resourceAccessObj = jwt.getClaim("resource_access");
        if (resourceAccessObj instanceof Map<?, ?> resourceAccess) {
            Object clientAccess = resourceAccess.get(keycloakClientId);
            if (clientAccess instanceof Map<?, ?> clientMap) {
                Object clientRolesObj = clientMap.get("roles");
                if (clientRolesObj instanceof List<?> clientRoles) {
                    for (Object role : clientRoles) {
                        if (role instanceof String r && !r.isBlank()) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
                        }
                    }
                }
            }
        }

        return authorities;
    }
}
