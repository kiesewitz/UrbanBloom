package com.urbanbloom.user.integration;

import com.urbanbloom.shared.ddd.DomainEventPublisher;
import com.urbanbloom.user.adapter.infrastructure.keycloak.KeycloakIdentityProvider;
import com.urbanbloom.user.application.RegisterUserCommand;
import com.urbanbloom.user.application.RegistrationException;
import com.urbanbloom.user.application.RegistrationResult;
import com.urbanbloom.user.application.UserRegistrationService;
import com.urbanbloom.user.config.RegistrationConfigProperties;
import com.urbanbloom.user.domain.*;
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
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Integration test for user registration with real Keycloak instance.
 * Uses Testcontainers to spin up a Keycloak server.
 */
@Slf4j
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRegistrationIT {

    private static final String TEST_REALM = "urbanbloom-test";
    private static final String TEST_CLIENT_ID = "server-app";

    private static Keycloak keycloakAdminClient;
    private static KeycloakIdentityProvider identityProvider;
    private static UserRegistrationService registrationService;
    private static InMemoryUserProfileRepository userProfileRepository;
    private static DomainEventPublisher eventPublisher;

    private static final Network testNetwork = Network.newNetwork();

    @Container
    private static final GenericContainer<?> greenMailContainer = new GenericContainer<>("greenmail/standalone:1.6.3")
            .withExposedPorts(3025, 3110)
            .withEnv("GREENMAIL_USERS", "user@urbanbloom.local:password")
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
        
        // Mock event publisher
        eventPublisher = mock(DomainEventPublisher.class);

        // Create registration service with real components
        RegistrationService domainService = new DefaultRegistrationService();
        registrationService = new TestUserRegistrationService(
                identityProvider,
                userProfileRepository,
                domainService,
                eventPublisher,
                List.of("urbanbloom.local", "city.urbanbloom.local"));
    }

    @AfterAll
    static void tearDown() {
        if (keycloakAdminClient != null) {
            keycloakAdminClient.close();
        }
    }

    private static final java.util.Set<String> createdUserIds = new java.util.HashSet<>();

    @BeforeEach
    void cleanupBeforeTest() {
        userProfileRepository.clear();
    }

    @AfterEach
    void cleanupAfterTest() {
        for (String userId : createdUserIds) {
            try {
                keycloakAdminClient.realm(TEST_REALM).users().get(userId).remove();
            } catch (Exception e) {
                log.warn("Failed to clean up user {} from Keycloak: {}", userId, e.getMessage());
            }
        }
        createdUserIds.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Should successfully register a new user in Keycloak")
    void shouldRegisterUserInKeycloak() {
        // Given
        RegisterUserCommand command = new RegisterUserCommand();
        command.setEmail("user@urbanbloom.local");
        command.setPassword("Test123!Password");
        command.setFirstName("Max");
        command.setLastName("Mustermann");

        // When
        RegistrationResult result = registrationService.registerUser(command);
        createdUserIds.add(result.getUserId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isNotEmpty();
        assertThat(result.getEmail()).isEqualTo("user@urbanbloom.local");
        assertThat(result.isVerificationRequired()).isTrue();

        // Verify user exists in Keycloak
        var users = keycloakAdminClient.realm(TEST_REALM)
                .users()
                .search("user@urbanbloom.local", true);
        assertThat(users).hasSize(1);

        // Verify user profile in local repository
        Optional<UserProfile> profile = userProfileRepository.findByEmail(
                Email.of("user@urbanbloom.local"));
        assertThat(profile).isPresent();
        assertThat(profile.get().getRole()).isEqualTo(UserRole.CITIZEN);

        // Verify email received by GreenMail
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
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify email received by GreenMail", e);
        }
    }

    @Test
    @Order(2)
    @DisplayName("Should prevent duplicate registration")
    void shouldPreventDuplicateRegistration() {
        RegisterUserCommand command = new RegisterUserCommand();
        command.setEmail("duplicate@urbanbloom.local");
        command.setPassword("Test123!Password");
        command.setFirstName("Test");
        command.setLastName("User");

        RegistrationResult result = registrationService.registerUser(command);
        createdUserIds.add(result.getUserId());

        assertThatThrownBy(() -> registrationService.registerUser(command))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("bereits registriert");
    }

    @Test
    @Order(3)
    @DisplayName("Should assign CITIZEN role by default")
    void shouldAssignCitizenRoleByDefault() {
        RegisterUserCommand command = new RegisterUserCommand();
        command.setEmail("newuser@urbanbloom.local");
        command.setPassword("Test123!Password");
        command.setFirstName("New");
        command.setLastName("User");

        RegistrationResult result = registrationService.registerUser(command);
        createdUserIds.add(result.getUserId());

        var userResource = keycloakAdminClient.realm(TEST_REALM).users().get(result.getUserId());
        var realmRoles = userResource.roles().realmLevel().listAll();
        assertThat(realmRoles).extracting(RoleRepresentation::getName).contains("CITIZEN");
    }

    private static void setupTestRealm() {
        try {
            keycloakAdminClient.realm(TEST_REALM).toRepresentation();
            return;
        } catch (Exception e) {}

        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(TEST_REALM);
        realm.setEnabled(true);
        realm.setRegistrationAllowed(true);
        realm.setVerifyEmail(true);
        keycloakAdminClient.realms().create(realm);

        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(TEST_CLIENT_ID);
        client.setEnabled(true);
        client.setPublicClient(false);
        client.setDirectAccessGrantsEnabled(true);
        keycloakAdminClient.realm(TEST_REALM).clients().create(client);

        for (String roleName : Arrays.asList("CITIZEN", "VERIFIED_CITIZEN", "DISTRICT_MANAGER", "ADMIN")) {
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            keycloakAdminClient.realm(TEST_REALM).roles().create(role);
        }
    }

    private static class TestKeycloakIdentityProvider extends KeycloakIdentityProvider {
        public TestKeycloakIdentityProvider(Keycloak keycloak, String realm) {
            super(keycloak, new RegistrationConfigProperties());
            try {
                var field = KeycloakIdentityProvider.class.getDeclaredField("realm");
                field.setAccessible(true);
                field.set(this, realm);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class TestUserRegistrationService extends UserRegistrationService {
        public TestUserRegistrationService(
                IdentityProvider identityProvider,
                UserProfileRepository userProfileRepository,
                RegistrationService registrationService,
                DomainEventPublisher eventPublisher,
                List<String> allowedDomains) {
            super(identityProvider, userProfileRepository, registrationService, eventPublisher);
            try {
                var field = UserRegistrationService.class.getDeclaredField("allowedDomainStrings");
                field.setAccessible(true);
                field.set(this, allowedDomains);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class InMemoryUserProfileRepository implements UserProfileRepository {
        private final java.util.Map<String, UserProfile> profiles = new java.util.HashMap<>();
        @Override public UserProfile save(UserProfile userProfile) { profiles.put(userProfile.getId(), userProfile); return userProfile; }
        @Override public Optional<UserProfile> findById(String id) { return Optional.ofNullable(profiles.get(id)); }
        @Override public Optional<UserProfile> findByExternalUserId(ExternalUserId id) { return profiles.values().stream().filter(p -> p.getExternalUserId().equals(id)).findFirst(); }
        @Override public Optional<UserProfile> findByEmail(Email email) { return profiles.values().stream().filter(p -> p.getEmail().equals(email)).findFirst(); }
        @Override public boolean existsByEmail(Email email) { return findByEmail(email).isPresent(); }
        @Override public boolean existsByExternalUserId(ExternalUserId id) { return findByExternalUserId(id).isPresent(); }
        @Override public void delete(UserProfile p) {}
        @Override public void deleteById(String id) {}
        public void clear() { profiles.clear(); }
    }
}
