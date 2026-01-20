import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:school_library_mobile/design_system/components/atoms/app_info_badge.dart';

void main() {
  group('AppInfoBadge', () {
    testWidgets('renders text correctly', (WidgetTester tester) async {
      const testText = 'Important information';

      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppInfoBadge(text: testText),
          ),
        ),
      );

      expect(find.text(testText), findsOneWidget);
    });

    testWidgets('displays default info icon', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppInfoBadge(text: 'Info'),
          ),
        ),
      );

      expect(find.byIcon(Icons.info_outline), findsOneWidget);
    });

    testWidgets('displays custom icon', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppInfoBadge(
              text: 'Warning',
              icon: Icons.warning,
            ),
          ),
        ),
      );

      expect(find.byIcon(Icons.warning), findsOneWidget);
    });

    testWidgets('applies custom colors correctly',
        (WidgetTester tester) async {
      const customBackground = Colors.yellow;
      const customForeground = Colors.black;

      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppInfoBadge(
              text: 'Custom',
              backgroundColor: customBackground,
              foregroundColor: customForeground,
            ),
          ),
        ),
      );

      final container = tester.widget<Container>(
        find.ancestor(
          of: find.byType(Row),
          matching: find.byType(Container),
        ),
      );

      final decoration = container.decoration as BoxDecoration;
      expect(decoration.color, customBackground);
    });

    testWidgets('has rounded border', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppInfoBadge(text: 'Rounded'),
          ),
        ),
      );

      final container = tester.widget<Container>(
        find.ancestor(
          of: find.byType(Row),
          matching: find.byType(Container),
        ),
      );

      final decoration = container.decoration as BoxDecoration;
      expect(decoration.borderRadius, BorderRadius.circular(999));
    });

    testWidgets('adapts to theme brightness', (WidgetTester tester) async {
      // Test light theme
      await tester.pumpWidget(
        MaterialApp(
          theme: ThemeData.light(),
          home: const Scaffold(
            body: AppInfoBadge(text: 'Light'),
          ),
        ),
      );

      expect(find.byType(AppInfoBadge), findsOneWidget);

      // Test dark theme
      await tester.pumpWidget(
        MaterialApp(
          theme: ThemeData.dark(),
          home: const Scaffold(
            body: AppInfoBadge(text: 'Dark'),
          ),
        ),
      );

      expect(find.byType(AppInfoBadge), findsOneWidget);
    });

    testWidgets('text wraps when too long', (WidgetTester tester) async {
      const longText =
          'This is a very long text that should wrap to multiple lines';

      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: SizedBox(
              width: 200,
              child: AppInfoBadge(text: longText),
            ),
          ),
        ),
      );

      expect(find.text(longText), findsOneWidget);
      expect(find.byType(Flexible), findsOneWidget);
    });
  });
}
