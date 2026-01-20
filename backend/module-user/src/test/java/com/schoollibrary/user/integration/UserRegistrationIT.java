package com.schoollibrary.user.integration;

import com.schoollibrary.user.adapter.infrastructure.keycloak.KeycloakIdentityProvider;
import com.schoollibrary.user.application.RegisterUserCommand;
import com.schoollibrary.user.application.RegistrationException;
import com.schoollibrary.user.application.RegistrationResult;
import com.schoollibrary.user.application.UserRegistrationService;
import com.schoollibrary.user.config.RegistrationConfigProperties;
import com.schoollibrary.user.domain.*;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration test for user registration with real Keycloak instance.
 * Uses Testcontainers to spin up a Keycloak server.
 */
@Slf4j
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRegistrationIT {

    private static final String TEST_REALM = "schoollibrary-test";
    private static final String TEST_CLIENT_ID = "schoollibrary-app";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    private static Keycloak keycloakAdminClient;
    private static KeycloakIdentityProvider identityProvider;
    private static UserRegistrationService registrationService;
    private static InMemoryUserProfileRepository userProfileRepository;

    private static final Network testNetwork = Network.newNetwork();

    @Container
    private static final GenericContainer<?> greenMailContainer = new GenericContainer<>("greenmail/standalone:1.6.3")
            .withExposedPorts(3025, 3110)
            .withEnv("GREENMAIL_USERS", "max.mustermann@schule.de:password")
            .withNetwork(testNetwork)
            .withNetworkAliases("greenmail");

    @Container
    private static final KeycloakContainer keycloakContainer = new KeycloakContainer()
            .withRealmImportFile("keycloak-realm-test.json")
            .withNetwork(testNetwork)
            .withNetworkAliases("keycloak");

    @BeforeAll
    static void setUp() {
        // Create Keycloak admin client
        keycloakAdminClient = KeycloakBuilder.builder()
                .serverUrl(keycloakContainer.getAuthServerUrl())
                .realm("master")
                .clientId("admin-cli")
                .username(keycloakContainer.getAdminUsername())
                .password(keycloakContainer.getAdminPassword())
                .build();

        // Setup test realm if not imported
        setupTestRealm();

        // Create identity provider with test realm
        identityProvider = new TestKeycloakIdentityProvider(
                keycloakAdminClient,
                TEST_REALM);

        // Create in-memory repository
        userProfileRepository = new InMemoryUserProfileRepository();

        // GreenMail is provided as a Testcontainers container (greenmailContainer)

        // Create registration service with real components
        RegistrationService domainService = new DefaultRegistrationService();
        registrationService = new TestUserRegistrationService(
                identityProvider,
                userProfileRepository,
                domainService,
                List.of("schule.de", "student.schule.de"));
    }

    @AfterAll
    static void tearDown() {
        if (keycloakAdminClient != null) {
            keycloakAdminClient.close();
        }
        // Testcontainers will stop containers automatically
    }

    private static final java.util.Set<String> createdUserIds = new java.util.HashSet<>();

    @BeforeEach
    void cleanupBeforeTest() {
        // Clean up users created in previous tests
        userProfileRepository.clear();
    }

    @AfterEach
    void cleanupAfterTest() {
        // Delete each user created in Keycloak during the test
        for (String userId : createdUserIds) {
            try {
                keycloakAdminClient.realm(TEST_REALM).users().get(userId).remove();
                log.info("Cleaned up user {} from Keycloak after test", userId);
            } catch (Exception e) {
                log.warn("Failed to clean up user {} from Keycloak: {}", userId, e.getMessage());
            }
        }
        createdUserIds.clear();
    }

    private String registerUserAndTrack(RegisterUserCommand command) {
        RegistrationResult result = registrationService.registerUser(command);
        createdUserIds.add(result.getUserId());
        return result.getUserId();
    }

    @Test
    @Order(1)
    @DisplayName("Should successfully register a new user in Keycloak")
    void shouldRegisterUserInKeycloak() {
        // Given
        RegisterUserCommand command = new RegisterUserCommand();
        command.setEmail("max.mustermann@schule.de");
        command.setPassword("Test123!Password");
        command.setFirstName("Max");
        command.setLastName("Mustermann");
        command.setStudentId("S12345");
        command.setSchoolClass("10A");

        // When
        RegistrationResult result = registrationService.registerUser(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isNotEmpty();
        assertThat(result.getEmail()).isEqualTo("max.mustermann@schule.de");
        assertThat(result.getMessage()).contains("Registrierung erfolgreich");
        assertThat(result.isVerificationRequired()).isTrue();

        // Verify user exists in Keycloak
        var users = keycloakAdminClient.realm(TEST_REALM)
                .users()
                .search("max.mustermann@schule.de", true);
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("max.mustermann@schule.de");
        assertThat(users.get(0).getFirstName()).isEqualTo("Max");
        assertThat(users.get(0).getLastName()).isEqualTo("Mustermann");

        // Verify custom attributes
        var userAttributes = users.get(0).getAttributes();
        assertThat(userAttributes).containsKey("studentId");
        assertThat(userAttributes.get("studentId")).contains("S12345");
        assertThat(userAttributes).containsKey("schoolClass");
        assertThat(userAttributes.get("schoolClass")).contains("10A");

        // Verify user profile in local repository
        Optional<UserProfile> profile = userProfileRepository.findByEmail(
                Email.of("max.mustermann@schule.de"));
        assertThat(profile).isPresent();
        assertThat(profile.get().getEmail().getValue()).isEqualTo("max.mustermann@schule.de");
        assertThat(profile.get().getRole()).isEqualTo(UserRole.STUDENT);
        assertThat(profile.get().isActive()).isTrue();

        // Verify that a verification email was sent and received by GreenMail container
        // via POP3
        try {
            Properties props = new Properties();
            String host = greenMailContainer.getHost();
            int pop3Port = greenMailContainer.getMappedPort(3110);
            props.put("mail.store.protocol", "pop3");
            props.put("mail.pop3.host", host);
            props.put("mail.pop3.port", String.valueOf(pop3Port));
            Session session = Session.getInstance(props);
            Store store = session.getStore("pop3");
            store.connect(host, pop3Port, result.getEmail(), "password");
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            Message[] messages = inbox.getMessages();
            assertThat(messages).isNotEmpty();
            assertThat(((InternetAddress) messages[0].getAllRecipients()[0]).getAddress())
                    .isEqualTo("max.mustermann@schule.de");
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify email received by GreenMail container", e);
        }
    }

    @Test
    @Order(2)
    @DisplayName("Should prevent duplicate registration")
    void shouldPreventDuplicateRegistration() {
        // Given - register first user
        RegisterUserCommand firstCommand = new RegisterUserCommand();
        firstCommand.setEmail("duplicate@schule.de");
        firstCommand.setPassword("Test123!Password");
        firstCommand.setFirstName("Test");
        firstCommand.setLastName("User");

        RegistrationResult firstResult = registrationService.registerUser(firstCommand);
        createdUserIds.add(firstResult.getUserId());
        // String userId = firstResult.getUserId();

        try {
            // When - try to register same email again
            RegisterUserCommand secondCommand = new RegisterUserCommand();
            secondCommand.setEmail("duplicate@schule.de");
            secondCommand.setPassword("AnotherPassword123!");
            secondCommand.setFirstName("Another");
            secondCommand.setLastName("User");

            // Then
            assertThatThrownBy(() -> registrationService.registerUser(secondCommand))
                    .isInstanceOf(RegistrationException.class)
                    .hasMessageContaining("E-Mail-Adresse ist bereits registriert");

        } catch (Exception e) {
            // Test failed, AfterEach will clean up
            throw e;
        }
    }

    @Test
    @Order(3)
    @DisplayName("Should reject registration with invalid domain")
    void shouldRejectInvalidDomain() {
        // Given
        RegisterUserCommand command = new RegisterUserCommand();
        command.setEmail("user@invalid-domain.com");
        command.setPassword("Test123!Password");
        command.setFirstName("Test");
        command.setLastName("User");

        // When & Then
        assertThatThrownBy(() -> registrationService.registerUser(command))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("E-Mail-Domäne ist nicht zulässig");
    }

    @Test
    @Order(4)
    @DisplayName("Should assign STUDENT role by default")
    void shouldAssignStudentRoleByDefault() {
        // Given
        RegisterUserCommand command = new RegisterUserCommand();
        command.setEmail("student@schule.de");
        command.setPassword("Test123!Password");
        command.setFirstName("Student");
        command.setLastName("Test");

        // When
        RegistrationResult result = registrationService.registerUser(command);
        createdUserIds.add(result.getUserId());

        try {
            // Then - verify role in Keycloak
            var userResource = keycloakAdminClient.realm(TEST_REALM)
                    .users()
                    .get(result.getUserId());

            var realmRoles = userResource.roles().realmLevel().listAll();
            assertThat(realmRoles)
                    .extracting(RoleRepresentation::getName)
                    .contains("STUDENT");

            // Verify role in local profile
            Optional<UserProfile> profile = userProfileRepository.findByEmail(
                    Email.of("student@schule.de"));
            assertThat(profile).isPresent();
            assertThat(profile.get().getRole()).isEqualTo(UserRole.STUDENT);

        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    @Order(5)
    @DisplayName("Should handle Keycloak connection failures gracefully")
    void shouldHandleKeycloakConnectionFailures() {
        // Given - create a provider with invalid URL
        Keycloak invalidClient = KeycloakBuilder.builder()
                .serverUrl("http://invalid-host:8080")
                .realm("master")
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .build();

        KeycloakIdentityProvider invalidProvider = new TestKeycloakIdentityProvider(
                invalidClient,
                TEST_REALM);

        UserRegistrationService serviceWithInvalidProvider = new TestUserRegistrationService(
                invalidProvider,
                userProfileRepository,
                new DefaultRegistrationService(),
                List.of("schule.de"));

        RegisterUserCommand command = new RegisterUserCommand();
        command.setEmail("test@schule.de");
        command.setPassword("Test123!Password");
        command.setFirstName("Test");
        command.setLastName("User");

        // When & Then
        assertThatThrownBy(() -> serviceWithInvalidProvider.registerUser(command))
                .isInstanceOf(RuntimeException.class);

        invalidClient.close();
    }

    // Helper method to setup test realm
    private static void setupTestRealm() {
        // Check if realm already exists
        try {
            keycloakAdminClient.realm(TEST_REALM).toRepresentation();
            return; // Realm already exists
        } catch (Exception e) {
            // Realm doesn't exist, create it
        }

        // Create realm
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(TEST_REALM);
        realm.setEnabled(true);
        realm.setRegistrationAllowed(true);
        realm.setVerifyEmail(true);

        keycloakAdminClient.realms().create(realm);

        // Create client
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(TEST_CLIENT_ID);
        client.setEnabled(true);
        client.setPublicClient(false);
        client.setDirectAccessGrantsEnabled(true);

        keycloakAdminClient.realm(TEST_REALM).clients().create(client);

        // Create roles
        for (String roleName : Arrays.asList("STUDENT", "TEACHER", "LIBRARIAN", "ADMIN")) {
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            keycloakAdminClient.realm(TEST_REALM).roles().create(role);
        }
    }

    // Test implementation of KeycloakIdentityProvider
    private static class TestKeycloakIdentityProvider extends KeycloakIdentityProvider {
        private final String realm;

        public TestKeycloakIdentityProvider(Keycloak keycloak, String realm) {
            super(keycloak, new RegistrationConfigProperties());
            this.realm = realm;
            // Use reflection to set the realm field
            try {
                var field = KeycloakIdentityProvider.class.getDeclaredField("realm");
                field.setAccessible(true);
                field.set(this, realm);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set realm", e);
            }
        }
    }

    // Test implementation of UserRegistrationService with configurable domains
    private static class TestUserRegistrationService extends UserRegistrationService {
        private final List<String> allowedDomains;

        public TestUserRegistrationService(
                IdentityProvider identityProvider,
                UserProfileRepository userProfileRepository,
                RegistrationService registrationService,
                List<String> allowedDomains) {
            super(identityProvider, userProfileRepository, registrationService);
            this.allowedDomains = allowedDomains;
            // Use reflection to set the allowedDomainStrings field
            try {
                var field = UserRegistrationService.class.getDeclaredField("allowedDomainStrings");
                field.setAccessible(true);
                field.set(this, allowedDomains);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set allowed domains", e);
            }
        }
    }

    // Simple in-memory repository for testing
    private static class InMemoryUserProfileRepository implements UserProfileRepository {
        private final java.util.Map<String, UserProfile> profiles = new java.util.HashMap<>();

        @Override
        public UserProfile save(UserProfile userProfile) {
            profiles.put(userProfile.getId(), userProfile);
            return userProfile;
        }

        @Override
        public Optional<UserProfile> findById(String id) {
            return Optional.ofNullable(profiles.get(id));
        }

        @Override
        public Optional<UserProfile> findByExternalUserId(ExternalUserId externalUserId) {
            return profiles.values().stream()
                    .filter(p -> p.getExternalUserId().equals(externalUserId))
                    .findFirst();
        }

        @Override
        public Optional<UserProfile> findByEmail(Email email) {
            return profiles.values().stream()
                    .filter(p -> p.getEmail().equals(email))
                    .findFirst();
        }

        @Override
        public boolean existsByEmail(Email email) {
            return findByEmail(email).isPresent();
        }

        @Override
        public boolean existsByExternalUserId(ExternalUserId externalUserId) {
            return findByExternalUserId(externalUserId).isPresent();
        }

        @Override
        public void delete(UserProfile userProfile) {
            profiles.remove(userProfile.getId());
        }

        @Override
        public void deleteById(String id) {
            profiles.remove(id);
        }

        public void clear() {
            profiles.clear();
        }
    }
}
