package com.schoollibrary.user.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AllowedDomainTest {

    @Test
    void shouldCreateValidDomain() {
        AllowedDomain domain = AllowedDomain.of("schule.de");
        assertThat(domain.getDomain()).isEqualTo("schule.de");
    }

    @Test
    void shouldNormalizeDomainToLowercase() {
        AllowedDomain domain = AllowedDomain.of("Schule.DE");
        assertThat(domain.getDomain()).isEqualTo("schule.de");
    }

    @Test
    void shouldThrowExceptionForNullDomain() {
        assertThatThrownBy(() -> AllowedDomain.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Domain cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionForEmptyDomain() {
        assertThatThrownBy(() -> AllowedDomain.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Domain cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionForInvalidDomainFormat() {
        assertThatThrownBy(() -> AllowedDomain.of("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid domain format");
    }

    @Test
    void shouldMatchEmailFromSameDomain() {
        AllowedDomain domain = AllowedDomain.of("schule.de");
        Email email = Email.of("student@schule.de");
        
        assertThat(domain.matches(email)).isTrue();
    }

    @Test
    void shouldNotMatchEmailFromDifferentDomain() {
        AllowedDomain domain = AllowedDomain.of("schule.de");
        Email email = Email.of("student@other.de");
        
        assertThat(domain.matches(email)).isFalse();
    }

    @Test
    void shouldMatchEmailCaseInsensitively() {
        AllowedDomain domain = AllowedDomain.of("schule.de");
        Email email = Email.of("student@SCHULE.DE");
        
        assertThat(domain.matches(email)).isTrue();
    }

    @Test
    void shouldHandleNullEmailInMatches() {
        AllowedDomain domain = AllowedDomain.of("schule.de");
        
        assertThat(domain.matches(null)).isFalse();
    }
}
