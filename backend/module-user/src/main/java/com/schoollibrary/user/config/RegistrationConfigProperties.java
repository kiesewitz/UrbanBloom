package com.schoollibrary.user.config;

import com.schoollibrary.user.domain.AllowedDomain;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for user registration.
 */
@Configuration
@ConfigurationProperties(prefix = "schoollibrary.registration")
@Getter
@Setter
public class RegistrationConfigProperties {

    /**
     * List of allowed email domains for registration.
     * Example: ["schule.de", "student.schule.de"]
     */
    private List<String> allowedDomains = new ArrayList<>();

    /**
     * Whether email verification is required after registration.
     */
    private boolean emailVerificationRequired = true;

    /**
     * Token expiration time in hours for verification emails.
     */
    private int verificationTokenExpirationHours = 24;

    public List<AllowedDomain> getAllowedDomainsAsObjects() {
        return allowedDomains.stream()
                .map(AllowedDomain::of)
                .toList();
    }
}
