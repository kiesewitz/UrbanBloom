import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:school_library_mobile/design_system/components/atoms/app_icon_button.dart';

void main() {
  group('AppIconButton', () {
    testWidgets('renders icon correctly', (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AppIconButton(
              icon: Icons.arrow_back,
              onPressed: () {},
            ),
          ),
        ),
      );

      expect(find.byIcon(Icons.arrow_back), findsOneWidget);
    });

    testWidgets('calls onPressed when tapped', (WidgetTester tester) async {
      var pressed = false;

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AppIconButton(
              icon: Icons.help_outline,
              onPressed: () => pressed = true,
            ),
          ),
        ),
      );

      await tester.tap(find.byType(AppIconButton));
      await tester.pump();

      expect(pressed, isTrue);
    });

    testWidgets('does not call onPressed when disabled',
        (WidgetTester tester) async {
      var pressed = false;

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AppIconButton(
              icon: Icons.help_outline,
              onPressed: null,
            ),
          ),
        ),
      );

      await tester.tap(find.byType(AppIconButton));
      await tester.pump();

      expect(pressed, isFalse);
    });

    testWidgets('applies custom size correctly', (WidgetTester tester) async {
      const customSize = 48.0;

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AppIconButton(
              icon: Icons.close,
              onPressed: () {},
              size: customSize,
            ),
          ),
        ),
      );

      final container = tester.widget<Container>(
        find.descendant(
          of: find.byType(AppIconButton),
          matching: find.byType(Container),
        ),
      );

      expect(container.constraints?.minWidth, customSize);
      expect(container.constraints?.minHeight, customSize);
    });

    testWidgets('applies custom icon size correctly',
        (WidgetTester tester) async {
      const customIconSize = 32.0;

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AppIconButton(
              icon: Icons.search,
              onPressed: () {},
              iconSize: customIconSize,
            ),
          ),
        ),
      );

      final icon = tester.widget<Icon>(find.byIcon(Icons.search));
      expect(icon.size, customIconSize);
    });

    testWidgets('applies custom color correctly', (WidgetTester tester) async {
      const customColor = Colors.red;

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AppIconButton(
              icon: Icons.delete,
              onPressed: () {},
              color: customColor,
            ),
          ),
        ),
      );

      final icon = tester.widget<Icon>(find.byIcon(Icons.delete));
      expect(icon.color, customColor);
    });

    testWidgets('has semantic label for accessibility',
        (WidgetTester tester) async {
      const semanticLabel = 'Zurück';

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AppIconButton(
              icon: Icons.arrow_back,
              onPressed: () {},
              semanticLabel: semanticLabel,
            ),
          ),
        ),
      );

      expect(
        find.bySemanticsLabel(semanticLabel),
        findsOneWidget,
      );
    });
  });
}
