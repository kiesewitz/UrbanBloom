#!/bin/bash
# Mobile Project Initialization Script - UrbanBloom
# This script initializes the Flutter mobile project with all necessary dependencies and structure

set -e  # Exit on error

echo "=========================================="
echo "UrbanBloom Mobile - Project Initialization"
echo "=========================================="

# Navigate to mobile directory
cd "$(dirname "$0")/../mobile" || exit 1

echo ""
echo "Step 1: Creating Flutter project..."
if [ -f "pubspec.yaml" ]; then
    echo "⚠️  Flutter project already exists. Skipping creation."
else
    flutter create \
        --org com.urbanbloom \
        --project-name urban_bloom_mobile \
        --platforms ios,android \
        --description "UrbanBloom mobile app for documenting and gamifying environmental actions" \
        .
fi

echo ""
echo "Step 2: Adding dependencies..."
flutter pub add \
    riverpod \
    flutter_riverpod \
    riverpod_annotation \
    go_router \
    drift \
    drift_flutter \
    sqlite3_flutter_libs \
    path_provider \
    path \
    dio \
    json_annotation \
    flutter_secure_storage \
    intl \
    image_picker \
    geolocator \
    permission_handler \
    connectivity_plus \
    shared_preferences

echo ""
echo "Step 3: Adding dev dependencies..."
flutter pub add dev:build_runner \
    dev:riverpod_generator \
    dev:drift_dev \
    dev:json_serializable \
    dev:flutter_lints \
    dev:patrol

echo ""
echo "Step 4: Creating directory structure..."
mkdir -p lib/ui/{atoms,molecules,organisms,templates,pages}
mkdir -p lib/domain/{models,repositories}
mkdir -p lib/data/{api,local,sync,repositories}
mkdir -p lib/data/local/{dao,tables}
mkdir -p lib/data/api/interceptors
mkdir -p lib/providers
mkdir -p lib/core/{theme,utils,constants}
mkdir -p test/{widget,integration,golden}
mkdir -p test/golden/goldens

echo ""
echo "Step 5: Creating initial files..."

# Create main.dart
cat > lib/main.dart << 'EOF'
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

void main() {
  runApp(
    const ProviderScope(
      child: UrbanBloomApp(),
    ),
  );
}

class UrbanBloomApp extends StatelessWidget {
  const UrbanBloomApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'UrbanBloom',
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF4CAF50)),
      ),
      home: const HomePage(),
    );
  }
}

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('UrbanBloom'),
      ),
      body: const Center(
        child: Text(
          'Welcome to UrbanBloom!',
          style: TextStyle(fontSize: 24),
        ),
      ),
    );
  }
}
EOF

# Create design tokens (colors)
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

# Create spacing constants
cat > lib/core/theme/spacing.dart << 'EOF'
/// Spacing constants for consistent layout
class AppSpacing {
  static const double xs = 4.0;
  static const double sm = 8.0;
  static const double md = 16.0;
  static const double lg = 24.0;
  static const double xl = 32.0;
  static const double xxl = 48.0;
}
EOF

# Create .gitignore additions
cat >> .gitignore << 'EOF'

# Drift generated files
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
echo "✅ Mobile project initialized successfully!"
echo ""
echo "Next steps:"
echo "1. Review the generated structure in lib/"
echo "2. Generate API client from OpenAPI spec:"
echo "   cd .. && openapi-generator generate -i openapi/urbanbloom-api-v1.yaml -g dart-dio -o mobile/lib/data/api/generated"
echo "3. Implement domain models in lib/domain/models/"
echo "4. Create UI components using Atomic Design in lib/ui/"
echo "5. Run the app: flutter run"
echo ""
echo "For more guidance, see:"
echo "- .github/instructions/frontend-mobile-instructions.md"
echo "- .github/agents/mobile-cdd.agent.md"
echo ""
