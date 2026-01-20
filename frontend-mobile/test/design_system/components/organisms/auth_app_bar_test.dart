import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:school_library_mobile/design_system/components/organisms/auth_app_bar.dart';

void main() {
  group('AuthAppBar', () {
    testWidgets('renders back and help buttons by default',
        (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            appBar: AuthAppBar(),
          ),
        ),
      );

      expect(find.byIcon(Icons.arrow_back), findsOneWidget);
      expect(find.byIcon(Icons.help_outline), findsOneWidget);
    });

    testWidgets('calls onBackPressed when back button is tapped',
        (WidgetTester tester) async {
      var backPressed = false;

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            appBar: AuthAppBar(
              onBackPressed: () => backPressed = true,
            ),
          ),
        ),
      );

      await tester.tap(find.byIcon(Icons.arrow_back));
      await tester.pump();

      expect(backPressed, isTrue);
    });

    testWidgets('calls onHelpPressed when help button is tapped',
        (WidgetTester tester) async {
      var helpPressed = false;

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            appBar: AuthAppBar(
              onHelpPressed: () => helpPressed = true,
            ),
          ),
        ),
      );

      await tester.tap(find.byIcon(Icons.help_outline));
      await tester.pump();

      expect(helpPressed, isTrue);
    });

    testWidgets('pops navigation when back is pressed without callback',
        (WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: Builder(
            builder: (context) => Scaffold(
              appBar: const AuthAppBar(),
              body: Center(
                child: ElevatedButton(
                  onPressed: () {
                    Navigator.of(context).push(
                      MaterialPageRoute(
                        builder: (context) => Scaffold(
                          appBar: const AuthAppBar(),
                          body: const Center(child: Text('Second Page')),
                        ),
                      ),
                    );
                  },
                  child: const Text('Next'),
                ),
              ),
            ),
          ),
        ),
      );

      // Navigate to second page
      await tester.tap(find.text('Next'));
      await tester.pumpAndSettle();

      expect(find.text('Second Page'), findsOneWidget);

      // Tap back button
      await tester.tap(find.byIcon(Icons.arrow_back));
      await tester.pumpAndSettle();

      expect(find.text('Second Page'), findsNothing);
    });

    testWidgets('hides back button when showBackButton is false',
        (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            appBar: AuthAppBar(showBackButton: false),
          ),
        ),
      );

      expect(find.byIcon(Icons.arrow_back), findsNothing);
      expect(find.byIcon(Icons.help_outline), findsOneWidget);
    });

    testWidgets('hides help button when showHelpButton is false',
        (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            appBar: AuthAppBar(showHelpButton: false),
          ),
        ),
      );

      expect(find.byIcon(Icons.arrow_back), findsOneWidget);
      expect(find.byIcon(Icons.help_outline), findsNothing);
    });

    testWidgets('has correct preferred size', (WidgetTester tester) async {
      const appBar = AuthAppBar();
      expect(appBar.preferredSize, const Size.fromHeight(56));
    });

    testWidgets('has transparent blur effect', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            appBar: AuthAppBar(),
          ),
        ),
      );

      final container = tester.widget<Container>(
        find.descendant(
          of: find.byType(AuthAppBar),
          matching: find.byType(Container),
        ).first,
      );

      final decoration = container.decoration as BoxDecoration?;
      expect(decoration?.color?.a, lessThan(1.0));
    });

    testWidgets('adapts to theme brightness', (WidgetTester tester) async {
      // Light theme
      await tester.pumpWidget(
        MaterialApp(
          theme: ThemeData.light(),
          home: const Scaffold(
            appBar: AuthAppBar(),
          ),
        ),
      );

      expect(find.byType(AuthAppBar), findsOneWidget);

      // Dark theme
      await tester.pumpWidget(
        MaterialApp(
          theme: ThemeData.dark(),
          home: const Scaffold(
            appBar: AuthAppBar(),
          ),
        ),
      );

      expect(find.byType(AuthAppBar), findsOneWidget);
    });

    testWidgets('buttons have correct semantic labels',
        (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            appBar: AuthAppBar(),
          ),
        ),
      );

      expect(find.bySemanticsLabel('Zurück'), findsOneWidget);
      expect(find.bySemanticsLabel('Hilfe'), findsOneWidget);
    });

    testWidgets('buttons are properly spaced', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            appBar: AuthAppBar(),
          ),
        ),
      );

      final row = tester.widget<Row>(
        find.descendant(
          of: find.byType(AuthAppBar),
          matching: find.byType(Row),
        ),
      );

      expect(row.children.length, 3); // back button, spacer, help button
      expect(row.children[1], isA<Spacer>());
    });
  });
}
