import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:school_library_mobile/design_system/components/molecules/auth_hero_section.dart';

void main() {
  group('AuthHeroSection', () {
    testWidgets('renders title and description',
        (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AuthHeroSection(
              title: 'Welcome',
              description: 'Please sign in to continue',
            ),
          ),
        ),
      );

      expect(find.text('Welcome'), findsOneWidget);
      expect(find.text('Please sign in to continue'), findsOneWidget);
    });

    testWidgets('displays logo container with icon',
        (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AuthHeroSection(
              title: 'Sign In',
              description: 'Enter your credentials',
              icon: Icons.lock,
            ),
          ),
        ),
      );

      expect(find.byIcon(Icons.lock), findsOneWidget);
    });

    testWidgets('uses default school icon', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AuthHeroSection(
              title: 'Register',
              description: 'Create your account',
            ),
          ),
        ),
      );

      expect(find.byIcon(Icons.school), findsOneWidget);
    });

    testWidgets('title has correct styling', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AuthHeroSection(
              title: 'Test Title',
              description: 'Test description',
            ),
          ),
        ),
      );

      final titleText = tester.widget<Text>(
        find.text('Test Title'),
      );

      expect(titleText.style?.fontWeight, FontWeight.bold);
      expect(titleText.textAlign, TextAlign.center);
    });

    testWidgets('description is centered and constrained',
        (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AuthHeroSection(
              title: 'Title',
              description: 'This is a long description that should be constrained',
            ),
          ),
        ),
      );

      final constrainedBox = tester.widget<ConstrainedBox>(
        find.ancestor(
          of: find.text('This is a long description that should be constrained'),
          matching: find.byType(ConstrainedBox),
        ),
      );

      expect(constrainedBox.constraints.maxWidth, 280);
    });

    testWidgets('components are vertically spaced correctly',
        (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AuthHeroSection(
              title: 'Title',
              description: 'Description',
            ),
          ),
        ),
      );

      final column = tester.widget<Column>(
        find.byType(Column),
      );

      final children = column.children;
      expect(children.length, greaterThan(3));

      // Check for SizedBox spacing
      final spacingBoxes = tester.widgetList<SizedBox>(
        find.byType(SizedBox),
      );
      expect(spacingBoxes, isNotEmpty);
    });

    testWidgets('adapts to theme brightness', (WidgetTester tester) async {
      // Light theme
      await tester.pumpWidget(
        MaterialApp(
          theme: ThemeData.light(),
          home: const Scaffold(
            body: AuthHeroSection(
              title: 'Light',
              description: 'Light description',
            ),
          ),
        ),
      );

      expect(find.text('Light'), findsOneWidget);

      // Dark theme
      await tester.pumpWidget(
        MaterialApp(
          theme: ThemeData.dark(),
          home: const Scaffold(
            body: AuthHeroSection(
              title: 'Dark',
              description: 'Dark description',
            ),
          ),
        ),
      );

      expect(find.text('Dark'), findsOneWidget);
    });

    testWidgets('renders all child components', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AuthHeroSection(
              title: 'Complete Test',
              description: 'Testing all components',
              icon: Icons.verified_user,
            ),
          ),
        ),
      );

      // Logo container
      expect(find.byIcon(Icons.verified_user), findsOneWidget);
      
      // Title
      expect(find.text('Complete Test'), findsOneWidget);
      
      // Description
      expect(find.text('Testing all components'), findsOneWidget);
      
      // Main column layout
      expect(find.byType(Column), findsOneWidget);
    });
  });
}
