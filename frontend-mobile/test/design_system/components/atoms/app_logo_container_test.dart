import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:school_library_mobile/design_system/components/atoms/app_logo_container.dart';

void main() {
  group('AppLogoContainer', () {
    testWidgets('renders icon correctly', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppLogoContainer(icon: Icons.school),
          ),
        ),
      );

      expect(find.byIcon(Icons.school), findsOneWidget);
    });

    testWidgets('applies default size correctly', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppLogoContainer(icon: Icons.library_books),
          ),
        ),
      );

      final container = tester.widget<Container>(find.byType(Container));

      expect(container.constraints?.minWidth, 64);
      expect(container.constraints?.minHeight, 64);
    });

    testWidgets('applies custom size correctly', (WidgetTester tester) async {
      const customSize = 80.0;

      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppLogoContainer(
              icon: Icons.book,
              size: customSize,
            ),
          ),
        ),
      );

      final container = tester.widget<Container>(find.byType(Container));

      expect(container.constraints?.minWidth, customSize);
      expect(container.constraints?.minHeight, customSize);
    });

    testWidgets('applies custom icon size', (WidgetTester tester) async {
      const customIconSize = 40.0;

      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppLogoContainer(
              icon: Icons.menu_book,
              iconSize: customIconSize,
            ),
          ),
        ),
      );

      final icon = tester.widget<Icon>(find.byType(Icon));
      expect(icon.size, customIconSize);
    });

    testWidgets('applies default primary background color',
        (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppLogoContainer(icon: Icons.school),
          ),
        ),
      );

      final container = tester.widget<Container>(find.byType(Container));
      final decoration = container.decoration as BoxDecoration;

      expect(decoration.color, const Color(0xFF0F63A8));
    });

    testWidgets('applies custom background color',
        (WidgetTester tester) async {
      const customColor = Colors.green;

      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppLogoContainer(
              icon: Icons.check,
              backgroundColor: customColor,
            ),
          ),
        ),
      );

      final container = tester.widget<Container>(find.byType(Container));
      final decoration = container.decoration as BoxDecoration;

      expect(decoration.color, customColor);
    });

    testWidgets('applies custom icon color', (WidgetTester tester) async {
      const customIconColor = Colors.black;

      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppLogoContainer(
              icon: Icons.star,
              iconColor: customIconColor,
            ),
          ),
        ),
      );

      final icon = tester.widget<Icon>(find.byType(Icon));
      expect(icon.color, customIconColor);
    });

    testWidgets('has rounded corners', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppLogoContainer(icon: Icons.home),
          ),
        ),
      );

      final container = tester.widget<Container>(find.byType(Container));
      final decoration = container.decoration as BoxDecoration;

      expect(decoration.borderRadius, BorderRadius.circular(16));
    });

    testWidgets('has box shadow', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: AppLogoContainer(icon: Icons.school),
          ),
        ),
      );

      final container = tester.widget<Container>(find.byType(Container));
      final decoration = container.decoration as BoxDecoration;

      expect(decoration.boxShadow, isNotNull);
      expect(decoration.boxShadow?.length, 1);
    });
  });
}
