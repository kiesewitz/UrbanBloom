import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:school_library_mobile/features/user/presentation/pages/registration_screen.dart';
import 'package:school_library_mobile/core/network/user_service.dart';
import 'package:school_library_mobile/core/network/models/registration_response.dart';
import 'package:school_library_mobile/core/di/providers.dart';
import 'package:school_library_mobile/design_system/components/atoms/app_primary_button.dart';

// Generate mocks
@GenerateMocks([UserService])
import 'registration_screen_api_test.mocks.dart';

void main() {
  late MockUserService mockUserService;

  setUp(() {
    mockUserService = MockUserService();
  });

  Widget createTestWidget() {
    return ProviderScope(
      overrides: [userServiceProvider.overrideWithValue(mockUserService)],
      child: const MaterialApp(home: RegistrationScreen()),
    );
  }

  group('RegistrationScreen API Integration Tests', () {
    testWidgets('successful registration shows success message', (
      WidgetTester tester,
    ) async {
      // Arrange
      const testEmail = 'test@schule.de';
      const testPassword = 'password123';
      const testFirstName = 'Test';
      const testLastName = 'User';
      const successMessage = 'Registrierung erfolgreich!';

      final mockResponse = RegistrationResponse(
        message: successMessage,
        userId: '123',
        email: testEmail,
      );

      when(mockUserService.register(any)).thenAnswer((_) async => mockResponse);

      await tester.pumpWidget(createTestWidget());
      await tester.binding.setSurfaceSize(const Size(800, 1200));
      await tester.pumpAndSettle();

      // Act - Fill form fields
      final emailField = find.byType(TextFormField).at(0);
      final firstNameField = find.byType(TextFormField).at(1);
      final lastNameField = find.byType(TextFormField).at(2);
      final passwordField = find.byType(TextFormField).at(3);
      final confirmPasswordField = find.byType(TextFormField).at(4);

      await tester.enterText(emailField, testEmail);
      await tester.enterText(firstNameField, testFirstName);
      await tester.enterText(lastNameField, testLastName);
      await tester.enterText(passwordField, testPassword);
      await tester.enterText(confirmPasswordField, testPassword);

      // Submit form
      final submitButton = find.byType(AppPrimaryButton);
      await tester.tap(submitButton);
      await tester.pumpAndSettle();

      // Assert
      verify(mockUserService.register(any)).called(1);
      expect(find.text(successMessage), findsOneWidget);
    });

    testWidgets('registration failure shows error message', (
      WidgetTester tester,
    ) async {
      // Arrange
      const testEmail = 'test@schule.de';
      const testPassword = 'password123';
      const testFirstName = 'Test';
      const testLastName = 'User';
      const errorMessage = 'E-Mail bereits vergeben';

      when(
        mockUserService.register(any),
      ).thenThrow(ApiException(errorMessage, 409));

      await tester.pumpWidget(createTestWidget());
      await tester.binding.setSurfaceSize(const Size(800, 1200));
      await tester.pumpAndSettle();

      // Act - Fill form and submit
      final emailField = find.byType(TextFormField).at(0);
      final firstNameField = find.byType(TextFormField).at(1);
      final lastNameField = find.byType(TextFormField).at(2);
      final passwordField = find.byType(TextFormField).at(3);
      final confirmPasswordField = find.byType(TextFormField).at(4);

      await tester.enterText(emailField, testEmail);
      await tester.enterText(firstNameField, testFirstName);
      await tester.enterText(lastNameField, testLastName);
      await tester.enterText(passwordField, testPassword);
      await tester.enterText(confirmPasswordField, testPassword);

      await tester.tap(find.byType(AppPrimaryButton));
      await tester.pumpAndSettle();

      // Assert
      verify(mockUserService.register(any)).called(1);
      expect(find.text(errorMessage), findsOneWidget);
    });

    testWidgets('network error shows appropriate message', (
      WidgetTester tester,
    ) async {
      // Arrange
      when(
        mockUserService.register(any),
      ).thenThrow(ApiException('Netzwerkfehler'));

      await tester.pumpWidget(createTestWidget());
      await tester.binding.setSurfaceSize(const Size(800, 1200));
      await tester.pumpAndSettle();

      // Act - Fill minimal form and submit
      final emailField = find.byType(TextFormField).at(0);
      final firstNameField = find.byType(TextFormField).at(1);
      final lastNameField = find.byType(TextFormField).at(2);
      final passwordField = find.byType(TextFormField).at(3);
      final confirmPasswordField = find.byType(TextFormField).at(4);

      await tester.enterText(emailField, 'test@schule.de');
      await tester.enterText(firstNameField, 'Test');
      await tester.enterText(lastNameField, 'User');
      await tester.enterText(passwordField, 'password123');
      await tester.enterText(confirmPasswordField, 'password123');

      await tester.tap(find.byType(AppPrimaryButton));
      await tester.pumpAndSettle();

      // Assert
      verify(mockUserService.register(any)).called(1);
      expect(find.text('Netzwerkfehler'), findsOneWidget);
    });

    testWidgets('loading state is shown during registration', (
      WidgetTester tester,
    ) async {
      // Arrange
      final mockResponse = RegistrationResponse(
        message: 'Erfolg',
        userId: '123',
        email: 'test@schule.de',
      );

      when(mockUserService.register(any)).thenAnswer((_) async {
        await Future.delayed(const Duration(milliseconds: 500));
        return mockResponse;
      });

      await tester.pumpWidget(createTestWidget());
      await tester.binding.setSurfaceSize(const Size(800, 1200));
      await tester.pumpAndSettle();

      // Act - Fill form and submit
      final emailField = find.byType(TextFormField).at(0);
      final firstNameField = find.byType(TextFormField).at(1);
      final lastNameField = find.byType(TextFormField).at(2);
      final passwordField = find.byType(TextFormField).at(3);
      final confirmPasswordField = find.byType(TextFormField).at(4);

      await tester.enterText(emailField, 'test@schule.de');
      await tester.enterText(firstNameField, 'Test');
      await tester.enterText(lastNameField, 'User');
      await tester.enterText(passwordField, 'password123');
      await tester.enterText(confirmPasswordField, 'password123');

      await tester.tap(find.byType(AppPrimaryButton));
      await tester.pump(); // Show loading state

      // Assert - Loading indicator should be visible
      expect(find.byType(CircularProgressIndicator), findsOneWidget);

      await tester.pumpAndSettle(); // Wait for completion

      // Assert - Loading indicator should be gone
      expect(find.byType(CircularProgressIndicator), findsNothing);
      expect(find.text('Erfolg'), findsOneWidget);
    });

    testWidgets('form validation prevents invalid submission', (
      WidgetTester tester,
    ) async {
      await tester.pumpWidget(createTestWidget());
      await tester.binding.setSurfaceSize(const Size(800, 1200));
      await tester.pumpAndSettle();

      // Act - Try to submit empty form
      await tester.tap(find.byType(AppPrimaryButton));
      await tester.pumpAndSettle();

      // Assert - API should not be called
      verifyNever(mockUserService.register(any));
    });

    testWidgets('email domain validation works', (WidgetTester tester) async {
      await tester.pumpWidget(createTestWidget());
      await tester.binding.setSurfaceSize(const Size(800, 1200));
      await tester.pumpAndSettle();

      // Act - Enter invalid email domain
      final emailField = find.byType(TextFormField).at(0);
      await tester.enterText(emailField, 'test@gmail.com');

      await tester.tap(find.byType(AppPrimaryButton));
      await tester.pumpAndSettle();

      // Assert - API should not be called
      verifyNever(mockUserService.register(any));
    });

    testWidgets('password confirmation validation works', (
      WidgetTester tester,
    ) async {
      await tester.pumpWidget(createTestWidget());
      await tester.binding.setSurfaceSize(const Size(800, 1200));
      await tester.pumpAndSettle();

      // Act - Enter mismatched passwords
      final passwordField = find.byType(TextFormField).at(3);
      final confirmPasswordField = find.byType(TextFormField).at(4);

      await tester.enterText(passwordField, 'password123');
      await tester.enterText(confirmPasswordField, 'password456');

      await tester.tap(find.byType(AppPrimaryButton));
      await tester.pumpAndSettle();

      // Assert - API should not be called
      verifyNever(mockUserService.register(any));
    });
  });
}
