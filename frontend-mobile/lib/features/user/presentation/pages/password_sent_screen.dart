import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../design_system/components/organisms/password_reset_success_card.dart';
import '../../../../design_system/design_tokens.dart';

/// Confirmation screen shown after password reset email is sent
/// Displays success message and back to login option
class PasswordSentScreen extends StatelessWidget {
  /// Creates a password sent confirmation screen
  const PasswordSentScreen({this.email, super.key});

  /// Optional email address that reset was sent to
  final String? email;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
      backgroundColor: isDark
          ? const Color(0xFF121212)
          : const Color(0xFFF9FAFB),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(AppSpacing.lg),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Navigation Header
              Row(
                children: [
                  IconButton(
                    onPressed: () => context.go('/login'),
                    icon: const Icon(Icons.arrow_back),
                    style: IconButton.styleFrom(
                      backgroundColor: isDark
                          ? const Color(0xFF1E1E1E)
                          : const Color(0xFFF5F5F5),
                    ),
                  ),
                  Expanded(
                    child: Center(
                      child: Text(
                        'E-MAIL GESENDET',
                        style: AppTypography.bodyMedium.copyWith(
                          letterSpacing: 1.2,
                          fontWeight: FontWeight.w600,
                          color: isDark ? Colors.grey[600] : Colors.grey[500],
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 40), // Balance spacing
                ],
              ),
              const SizedBox(height: AppSpacing.xxl),

              // Success Card
              PasswordResetSuccessCard(
                onBackToLogin: () => context.go('/login'),
                email: email,
              ),
              const SizedBox(height: AppSpacing.xl),

              // Help Text
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: AppSpacing.lg),
                child: Text(
                  'Keine E-Mail erhalten? Bitte überprüfe auch deinen Spam-Ordner '
                  'oder wende dich an die Bibliothek.',
                  style: AppTypography.bodySmall.copyWith(
                    color: isDark ? Colors.grey[500] : Colors.grey[600],
                    height: 1.5,
                  ),
                  textAlign: TextAlign.center,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
