package com.schoollibrary.user.domain;

import com.schoollibrary.shared.ddd.ValueObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Value Object representing an allowed school email domain.
 * Only users with email addresses from allowed domains can register.
 *
 * <p>Business Rules:</p>
 * <ul>
 *   <li>Domain must not be empty</li>
 *   <li>Domain must follow standard domain format</li>
 *   <li>Domain comparison is case-insensitive</li>
 * </ul>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AllowedDomain extends ValueObject {

    private final String domain;

    public String getDomain() {
        return domain;
    }

    /**
     * Creates an AllowedDomain from a string.
     *
     * @param domain the domain string (e.g., "schule.de")
     * @return new AllowedDomain instance
     * @throws IllegalArgumentException if domain is invalid
     */
    public static AllowedDomain of(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            throw new IllegalArgumentException("Domain cannot be null or empty");
        }

        String normalized = domain.trim().toLowerCase();

        // Basic domain validation
        if (!normalized.matches("^[a-z0-9.-]+\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid domain format: " + domain);
        }

        return new AllowedDomain(normalized);
    }

    /**
     * Creates a list of allowed domains from strings.
     *
     * @param domains varargs of domain strings
     * @return list of AllowedDomain instances
     */
    public static List<AllowedDomain> ofList(String... domains) {
        return Arrays.stream(domains)
                .map(AllowedDomain::of)
                .toList();
    }

    /**
     * Checks if the given email belongs to this domain.
     *
     * @param email the email to check
     * @return true if email domain matches
     */
    public boolean matches(Email email) {
        if (email == null) {
            return false;
        }
        String emailDomain = email.getValue().substring(email.getValue().indexOf('@') + 1).toLowerCase();
        return emailDomain.equals(domain);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllowedDomain that = (AllowedDomain) o;
        return domain.equals(that.domain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain);
    }

    @Override
    public String toString() {
        return domain;
    }
}
