package com.schoollibrary.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DefaultRegistrationServiceTest {

    private DefaultRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new DefaultRegistrationService();
    }

    @Test
    void shouldAllowRegistrationForAllowedDomain() {
        Email email = Email.of("student@schule.de");
        List<AllowedDomain> allowedDomains = AllowedDomain.ofList("schule.de", "student.schule.de");

        boolean allowed = service.isRegistrationAllowed(email, allowedDomains);

        assertThat(allowed).isTrue();
    }

    @Test
    void shouldNotAllowRegistrationForDisallowedDomain() {
        Email email = Email.of("user@other.de");
        List<AllowedDomain> allowedDomains = AllowedDomain.ofList("schule.de");

        boolean allowed = service.isRegistrationAllowed(email, allowedDomains);

        assertThat(allowed).isFalse();
    }

    @Test
    void shouldNotAllowRegistrationWhenEmailIsNull() {
        List<AllowedDomain> allowedDomains = AllowedDomain.ofList("schule.de");

        boolean allowed = service.isRegistrationAllowed(null, allowedDomains);

        assertThat(allowed).isFalse();
    }

    @Test
    void shouldNotAllowRegistrationWhenAllowedDomainsIsNull() {
        Email email = Email.of("student@schule.de");

        boolean allowed = service.isRegistrationAllowed(email, null);

        assertThat(allowed).isFalse();
    }

    @Test
    void shouldNotAllowRegistrationWhenAllowedDomainsIsEmpty() {
        Email email = Email.of("student@schule.de");

        boolean allowed = service.isRegistrationAllowed(email, List.of());

        assertThat(allowed).isFalse();
    }

    @Test
    void shouldAssignStudentRoleAsInitialRole() {
        Email email = Email.of("student@schule.de");

        UserRole role = service.determineInitialRole(email);

        assertThat(role).isEqualTo(UserRole.STUDENT);
    }
}
