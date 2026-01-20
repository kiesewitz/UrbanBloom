import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:school_library_mobile/features/health/presentation/pages/health_check_page.dart';
import 'package:school_library_mobile/design_system/components/atoms/primary_button.dart';

void main() {
  group('HealthCheckPage Widget Tests', () {
    testWidgets('should render Hello World text', (WidgetTester tester) async {
      // Arrange & Act
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: HealthCheckPage(),
          ),
        ),
      );

      // Assert
      expect(find.text('Hello World 👋'), findsOneWidget);
      expect(
        find.text('Digital School Library - Mobile App'),
        findsOneWidget,
      );
    });

    testWidgets('should render health check button',
        (WidgetTester tester) async {
      // Arrange & Act
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: HealthCheckPage(),
          ),
        ),
      );

      // Assert
      expect(find.byType(PrimaryButton), findsOneWidget);
      expect(find.text('Check Backend Health'), findsOneWidget);
    });

    testWidgets('should display app bar with title',
        (WidgetTester tester) async {
      // Arrange & Act
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: HealthCheckPage(),
          ),
        ),
      );

      // Assert
      expect(find.byType(AppBar), findsOneWidget);
      expect(find.text('School Library Mobile'), findsOneWidget);
    });

    testWidgets('should display info text', (WidgetTester tester) async {
      // Arrange & Act
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: HealthCheckPage(),
          ),
        ),
      );

      // Assert
      expect(
        find.text(
          'Press the button to check if the backend service is running.',
        ),
        findsOneWidget,
      );
    });
  });
}
