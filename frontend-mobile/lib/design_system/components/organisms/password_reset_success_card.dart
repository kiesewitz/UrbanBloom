import 'package:flutter/material.dart';
import '../../../design_system/design_tokens.dart';
import '../atoms/app_primary_button.dart';

/// Success confirmation card organism for password reset
/// Displays confirmation message after password reset email is sent
class PasswordResetSuccessCard extends StatelessWidget {
  /// Creates a password reset success card
  const PasswordResetSuccessCard({
    required this.onBackToLogin,
    this.email,
    super.key,
  });

  /// Called when back to login button is pressed
  final VoidCallback onBackToLogin;

  /// Optional email address to display
  final String? email;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Container(
      decoration: BoxDecoration(
        color: isDark ? const Color(0xFF1E1E1E) : Colors.white,
        borderRadius: BorderRadius.circular(AppRadius.xl),
        border: Border.all(
          color: isDark ? const Color(0xFF424242) : const Color(0xFFE0E0E0),
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      padding: const EdgeInsets.all(AppSpacing.xl),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          // Icon
          Container(
            width: 96,
            height: 96,
            decoration: BoxDecoration(
              color: AppColors.primary.withOpacity(0.1),
              shape: BoxShape.circle,
            ),
            child: Center(
              child: Container(
                width: 64,
                height: 64,
                decoration: BoxDecoration(
                  color: isDark ? const Color(0xFF424242) : Colors.white,
                  shape: BoxShape.circle,
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.05),
                      blurRadius: 8,
                      offset: const Offset(0, 2),
                    ),
                  ],
                ),
                child: const Icon(
                  Icons.email,
                  color: AppColors.primary,
                  size: 36,
                ),
              ),
            ),
          ),
          const SizedBox(height: AppSpacing.lg),

          // Title
          Text(
            'Prüfe dein Postfach',
            style: AppTypography.displayMedium.copyWith(
              color: isDark ? Colors.white : AppColors.textPrimary,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: AppSpacing.md),

          // Description
          Text(
            'Wir haben einen Link zum Zurücksetzen deines Passworts an deine '
            'Schul-E-Mail-Adresse gesendet.',
            style: AppTypography.bodyLarge.copyWith(
              color: isDark ? Colors.grey[400] : AppColors.textSecondary,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: AppSpacing.lg),

          // Security Badge
          Container(
            padding: const EdgeInsets.symmetric(
              horizontal: AppSpacing.lg,
              vertical: AppSpacing.md,
            ),
            decoration: BoxDecoration(
              color: isDark ? const Color(0xFF2A2A2A) : const Color(0xFFF5F5F5),
              borderRadius: BorderRadius.circular(AppRadius.md),
              border: Border.all(
                color: isDark
                    ? const Color(0xFF424242)
                    : const Color(0xFFE0E0E0),
              ),
            ),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Icon(
                  Icons.verified_user,
                  color: AppColors.primary,
                  size: 16,
                ),
                const SizedBox(width: AppSpacing.sm),
                Text(
                  'SICHERER VERSAND',
                  style: AppTypography.labelLarge.copyWith(
                    color: AppColors.primary,
                    fontWeight: FontWeight.w600,
                    letterSpacing: 0.8,
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: AppSpacing.xl),

          // Back to Login Button
          AppPrimaryButton(onPressed: onBackToLogin, label: 'Zurück zum Login'),
        ],
      ),
    );
  }
}
