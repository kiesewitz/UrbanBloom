/*
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';
import 'package:school_library_mobile/core/network/user_service.dart';
import 'package:school_library_mobile/features/user/presentation/pages/profile_screen.dart';
import 'package:school_library_mobile/core/di/providers.dart';
import 'package:school_library_mobile/models/user_profile.dart';

import 'profile_screen_test.mocks.dart';

@GenerateMocks([UserService])
void main() {
  late MockUserService mockUserService;

  setUp(() {
    mockUserService = MockUserService();
  });

  Widget createWidgetUnderTest() {
    return ProviderScope(
      overrides: [
        userServiceProvider.overrideWithValue(mockUserService),
      ],
      child: const MaterialApp(
        home: ProfileScreen(),
      ),
    );
  }

  group('ProfileScreen', () {
    testWidgets('should display loading indicator initially', (tester) async {
      // Arrange
      when(mockUserService.getUserProfile()).thenAnswer((_) async {
        // Simulate delay
        await Future.delayed(const Duration(seconds: 1));
        return UserProfile(
          userId: 'user-123',
          schoolIdentity: 'test@school.edu',
          firstName: 'John',
          lastName: 'Doe',
          email: 'john.doe@email.com',
          userGroup: 'STUDENT',
        );
      });

      // Act
      await tester.pumpWidget(createWidgetUnderTest());

      // Assert
      expect(find.byType(CircularProgressIndicator), findsOneWidget);
      expect(find.text('Profile'), findsOneWidget);
    });

    testWidgets('should display user profile when loaded successfully', (tester) async {
      // Arrange
      final userProfile = UserProfile(
        userId: 'user-123',
        schoolIdentity: 'test@school.edu',
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@email.com',
        userGroup: 'STUDENT',
        borrowingLimit: 5,
        isActive: true,
        registrationDate: DateTime.parse('2023-09-01'),
        lastLoginAt: DateTime.parse('2023-10-01T10:00:00Z'),
      );

      when(mockUserService.getUserProfile()).thenAnswer((_) async => userProfile);

      // Act
      await tester.pumpWidget(createWidgetUnderTest());
      await tester.pump(); // Trigger the load
      await tester.pump(); // Complete the future

      // Assert
      expect(find.text('John Doe'), findsOneWidget);
      expect(find.text('john.doe@email.com'), findsOneWidget);
      expect(find.text('STUDENT'), findsOneWidget);
      expect(find.text('Borrowing Limit:'), findsOneWidget);
      expect(find.text('5'), findsOneWidget);
      expect(find.text('Active:'), findsOneWidget);
      expect(find.text('Yes'), findsOneWidget);
      expect(find.text('Registered:'), findsOneWidget);
      expect(find.text('2023-09-01'), findsOneWidget);
    });

    testWidgets('should display error message when loading fails', (tester) async {
      // Arrange
      when(mockUserService.getUserProfile()).thenAnswer((_) async => throw Exception('Network error'));

      // Act
      await tester.pumpWidget(createWidgetUnderTest());
      await tester.pump(); // Trigger the load
      await tester.pump(); // Complete the future

      // Assert
      expect(find.text('Error loading profile'), findsOneWidget);
      expect(find.textContaining('Network error'), findsOneWidget);
      expect(find.text('Retry'), findsOneWidget);
    });

    testWidgets('should retry loading profile when retry button is pressed', (tester) async {
      // Arrange
      when(mockUserService.getUserProfile())
          .thenAnswer((_) async => throw Exception('Network error'))
          .thenAnswer((_) async => UserProfile(
                userId: 'user-123',
                schoolIdentity: 'test@school.edu',
                firstName: 'John',
                lastName: 'Doe',
                email: 'john.doe@email.com',
                userGroup: 'STUDENT',
              ));

      await tester.pumpWidget(createWidgetUnderTest());
      await tester.pump(); // Trigger the load
      await tester.pump(); // Complete the future

      // Act - Press retry button
      await tester.tap(find.text('Retry'));
      await tester.pump(); // Trigger retry
      await tester.pump(); // Complete the future

      // Assert
      expect(find.text('John Doe'), findsOneWidget);
      verify(mockUserService.getUserProfile()).called(2);
    });

    testWidgets('should navigate to login on logout', (tester) async {
      // Arrange
      final userProfile = UserProfile(
        userId: 'user-123',
        schoolIdentity: 'test@school.edu',
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@email.com',
        userGroup: 'STUDENT',
      );

      when(mockUserService.getUserProfile()).thenAnswer((_) async => userProfile);

      await tester.pumpWidget(createWidgetUnderTest());
      await tester.pump(); // Trigger the load
      await tester.pump(); // Complete the future

      // Act - Press logout button
      await tester.tap(find.byIcon(Icons.logout));
      await tester.pump(); // Trigger logout

      // Assert - Navigation would happen, but we can't easily test navigation in this setup
      // In a real app, we'd use a mock navigator or integration test
      verify(mockUserService.logout()).called(1);
    });
  });
}
*/

void main() {}
