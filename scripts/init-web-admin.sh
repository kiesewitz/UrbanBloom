#!/bin/bash
# Web Admin Project Initialization Script - UrbanBloom
# This script initializes the Flutter Web admin project with all necessary dependencies and structure

set -e  # Exit on error

echo "=========================================="
echo "UrbanBloom Admin Web - Project Initialization"
echo "=========================================="

# Navigate to admin-web directory
cd "$(dirname "$0")/../admin-web" || exit 1

echo ""
echo "Step 1: Creating Flutter Web project..."
if [ -f "pubspec.yaml" ]; then
    echo "⚠️  Flutter project already exists. Skipping creation."
else
    flutter create \
        --org com.urbanbloom \
        --project-name urban_bloom_admin \
        --platforms web \
        --description "UrbanBloom admin web panel for city administration and analytics" \
        .
fi

echo ""
echo "Step 2: Adding dependencies..."
flutter pub add \
    riverpod \
    flutter_riverpod \
    riverpod_annotation \
    go_router \
    dio \
    json_annotation \
    intl \
    fl_chart \
    data_table_2 \
    csv \
    file_picker \
    url_launcher \
    shared_preferences

echo ""
echo "Step 3: Adding dev dependencies..."
flutter pub add dev:build_runner \
    dev:riverpod_generator \
    dev:json_serializable \
    dev:flutter_lints

echo ""
echo "Step 4: Creating directory structure..."
mkdir -p lib/ui/{atoms,molecules,organisms,pages}
mkdir -p lib/domain/{models,repositories}
mkdir -p lib/data/{api,repositories}
mkdir -p lib/data/api/interceptors
mkdir -p lib/providers
mkdir -p lib/core/{theme,utils,constants}
mkdir -p test/widget

echo ""
echo "Step 5: Creating initial files..."

# Create main.dart
cat > lib/main.dart << 'EOF'
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

void main() {
  runApp(
    const ProviderScope(
      child: UrbanBloomAdminApp(),
    ),
  );
}

class UrbanBloomAdminApp extends StatelessWidget {
  const UrbanBloomAdminApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'UrbanBloom Admin',
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF4CAF50)),
      ),
      home: const AdminDashboard(),
    );
  }
}

class AdminDashboard extends StatelessWidget {
  const AdminDashboard({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Row(
        children: [
          // Sidebar navigation
          NavigationRail(
            extended: true,
            destinations: const [
              NavigationRailDestination(
                icon: Icon(Icons.dashboard),
                label: Text('Dashboard'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.eco),
                label: Text('Actions'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.people),
                label: Text('Users'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.analytics),
                label: Text('Analytics'),
              ),
            ],
            selectedIndex: 0,
            onDestinationSelected: (index) {
              // TODO: Implement navigation
            },
          ),
          const VerticalDivider(thickness: 1, width: 1),
          // Main content
          Expanded(
            child: Padding(
              padding: const EdgeInsets.all(24),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'UrbanBloom Admin Dashboard',
                    style: Theme.of(context).textTheme.headlineLarge,
                  ),
                  const SizedBox(height: 24),
                  const Center(
                    child: Text(
                      'Welcome to the admin panel!',
                      style: TextStyle(fontSize: 18),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
EOF

# Create design tokens (colors) - same as mobile
cat > lib/core/theme/colors.dart << 'EOF'
import 'package:flutter/material.dart';

/// Design tokens for UrbanBloom (imported from shared-resources/design-tokens)
class AppColors {
  // Primary colors
  static const primary = Color(0xFF4CAF50);      // Green
  static const primaryDark = Color(0xFF388E3C);
  static const primaryLight = Color(0xFF81C784);

  // Secondary colors
  static const secondary = Color(0xFF8BC34A);    // Light Green
  static const accent = Color(0xFFFF9800);       // Orange

  // Neutral colors
  static const background = Color(0xFFF5F5F5);
  static const surface = Color(0xFFFFFFFF);
  static const error = Color(0xFFD32F2F);

  // Text colors
  static const textPrimary = Color(0xFF212121);
  static const textSecondary = Color(0xFF757575);
  static const textHint = Color(0xFF9E9E9E);
}
EOF

# Create spacing constants - web has larger spacing
cat > lib/core/theme/spacing.dart << 'EOF'
/// Spacing constants for consistent layout (web/desktop sizing)
class AppSpacing {
  static const double xs = 4.0;
  static const double sm = 8.0;
  static const double md = 16.0;
  static const double lg = 24.0;
  static const double xl = 32.0;
  static const double xxl = 48.0;
  static const double xxxl = 64.0;  // Extra spacing for web
}
EOF

# Create .gitignore additions
cat >> .gitignore << 'EOF'

# Generated files
*.g.dart

# Build artifacts
build/
.dart_tool/

# Generated code
*.freezed.dart
*.gr.dart
EOF

echo ""
echo "Step 6: Running pub get..."
flutter pub get

echo ""
echo "Step 7: Running build_runner (generating code)..."
flutter pub run build_runner build --delete-conflicting-outputs || echo "⚠️  No generated files yet (expected)"

echo ""
echo "✅ Admin web project initialized successfully!"
echo ""
echo "Next steps:"
echo "1. Review the generated structure in lib/"
echo "2. Generate API client from OpenAPI spec:"
echo "   cd .. && openapi-generator generate -i openapi/urbanbloom-api-v1.yaml -g dart-dio -o admin-web/lib/data/api/generated"
echo "3. Implement domain models in lib/domain/models/"
echo "4. Create admin UI components (data tables, charts, forms) in lib/ui/"
echo "5. Run the web app: flutter run -d chrome"
echo ""
echo "For more guidance, see:"
echo "- .github/instructions/frontend-web-instructions.md"
echo "- .github/agents/web-admin-cdd.agent.md"
echo ""
