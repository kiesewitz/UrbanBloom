import 'package:flutter/material.dart';

/// Design Tokens for School Library Mobile App
/// Centralized colors, spacing, typography for consistency

/// Color Tokens
class AppColors {
  AppColors._();

  // Primary Colors
  static const Color primary = Color(0xFF2196F3);
  static const Color primaryDark = Color(0xFF1976D2);
  static const Color primaryLight = Color(0xFF64B5F6);

  // Secondary Colors
  static const Color secondary = Color(0xFF4CAF50);
  static const Color secondaryDark = Color(0xFF388E3C);
  static const Color secondaryLight = Color(0xFF81C784);

  // Semantic Colors
  static const Color success = Color(0xFF4CAF50);
  static const Color warning = Color(0xFFFFC107);
  static const Color error = Color(0xFFF44336);
  static const Color info = Color(0xFF2196F3);

  // Neutral Colors
  static const Color background = Color(0xFFFAFAFA);
  static const Color surface = Color(0xFFFFFFFF);
  static const Color surfaceVariant = Color(0xFFF5F5F5);

  // Text Colors
  static const Color textPrimary = Color(0xFF212121);
  static const Color textSecondary = Color(0xFF757575);
  static const Color textDisabled = Color(0xFFBDBDBD);
  static const Color textOnPrimary = Color(0xFFFFFFFF);

  // Border & Divider
  static const Color border = Color(0xFFE0E0E0);
  static const Color divider = Color(0xFFBDBDBD);
}

/// Spacing Tokens
class AppSpacing {
  AppSpacing._();

  static const double xs = 4.0;
  static const double sm = 8.0;
  static const double md = 16.0;
  static const double lg = 24.0;
  static const double xl = 32.0;
  static const double xxl = 48.0;
  static const double xxxl = 64.0;
}

/// Border Radius Tokens
class AppRadius {
  AppRadius._();

  static const double xs = 4.0;
  static const double sm = 8.0;
  static const double md = 12.0;
  static const double lg = 16.0;
  static const double xl = 24.0;
  static const double round = 999.0;
}

/// Typography Tokens
class AppTypography {
  AppTypography._();

  // Font Family
  static const String fontFamily = 'Roboto';

  // Font Sizes
  static const double fontSizeXs = 12.0;
  static const double fontSizeSm = 14.0;
  static const double fontSizeMd = 16.0;
  static const double fontSizeLg = 18.0;
  static const double fontSizeXl = 20.0;
  static const double fontSizeXxl = 24.0;
  static const double fontSizeXxxl = 32.0;

  // Font Weights
  static const FontWeight fontWeightLight = FontWeight.w300;
  static const FontWeight fontWeightRegular = FontWeight.w400;
  static const FontWeight fontWeightMedium = FontWeight.w500;
  static const FontWeight fontWeightBold = FontWeight.w700;

  // Text Styles
  static const TextStyle displayLarge = TextStyle(
    fontSize: fontSizeXxxl,
    fontWeight: fontWeightBold,
    color: AppColors.textPrimary,
    fontFamily: fontFamily,
  );

  static const TextStyle displayMedium = TextStyle(
    fontSize: fontSizeXxl,
    fontWeight: fontWeightBold,
    color: AppColors.textPrimary,
    fontFamily: fontFamily,
  );

  static const TextStyle headlineLarge = TextStyle(
    fontSize: fontSizeXl,
    fontWeight: fontWeightBold,
    color: AppColors.textPrimary,
    fontFamily: fontFamily,
  );

  static const TextStyle headlineMedium = TextStyle(
    fontSize: fontSizeLg,
    fontWeight: fontWeightBold,
    color: AppColors.textPrimary,
    fontFamily: fontFamily,
  );

  static const TextStyle titleLarge = TextStyle(
    fontSize: fontSizeMd,
    fontWeight: fontWeightMedium,
    color: AppColors.textPrimary,
    fontFamily: fontFamily,
  );

  static const TextStyle titleMedium = TextStyle(
    fontSize: fontSizeSm,
    fontWeight: fontWeightMedium,
    color: AppColors.textPrimary,
    fontFamily: fontFamily,
  );

  static const TextStyle bodyLarge = TextStyle(
    fontSize: fontSizeMd,
    fontWeight: fontWeightRegular,
    color: AppColors.textPrimary,
    fontFamily: fontFamily,
  );

  static const TextStyle bodyMedium = TextStyle(
    fontSize: fontSizeSm,
    fontWeight: fontWeightRegular,
    color: AppColors.textPrimary,
    fontFamily: fontFamily,
  );

  static const TextStyle bodySmall = TextStyle(
    fontSize: fontSizeXs,
    fontWeight: fontWeightRegular,
    color: AppColors.textSecondary,
    fontFamily: fontFamily,
  );

  static const TextStyle labelLarge = TextStyle(
    fontSize: fontSizeSm,
    fontWeight: fontWeightMedium,
    color: AppColors.textPrimary,
    fontFamily: fontFamily,
  );
}

/// Elevation/Shadow Tokens
class AppElevation {
  AppElevation._();

  static const double none = 0.0;
  static const double sm = 2.0;
  static const double md = 4.0;
  static const double lg = 8.0;
  static const double xl = 16.0;
}

/// Animation Duration Tokens
class AppDuration {
  AppDuration._();

  static const Duration fast = Duration(milliseconds: 150);
  static const Duration normal = Duration(milliseconds: 300);
  static const Duration slow = Duration(milliseconds: 500);
}
