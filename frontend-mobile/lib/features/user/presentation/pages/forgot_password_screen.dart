import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/providers.dart';
import '../../../../design_system/components/molecules/password_reset_form.dart';
import '../../../../design_system/design_tokens.dart';

/// Screen for requesting a password reset
/// Allows users to enter their email to receive a password reset email
class ForgotPasswordScreen extends ConsumerStatefulWidget {
  /// Creates a forgot password screen
  const ForgotPasswordScreen({super.key});

  @override
  ConsumerState<ForgotPasswordScreen> createState() =>
      _ForgotPasswordScreenState();
}

class _ForgotPasswordScreenState extends ConsumerState<ForgotPasswordScreen> {
  bool _isLoading = false;

  Future<void> _handlePasswordResetRequest(String email) async {
    setState(() {
      _isLoading = true;
    });

    try {
      final userApi = ref.read(userApiProvider);
      await userApi.requestPasswordReset(email);

      if (mounted) {
        // Navigate to success screen
        context.pushReplacement(
          '/password-sent?email=${Uri.encodeComponent(email)}',
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Fehler: ${e.toString()}'),
            backgroundColor: AppColors.error,
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

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
                    onPressed: () => context.pop(),
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
                        'KONTO HILFE',
                        style: AppTypography.bodySmall.copyWith(
                          letterSpacing: 1.5,
                          fontWeight: FontWeight.w600,
                          color: isDark ? Colors.grey[600] : Colors.grey[400],
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 40), // Balance spacing
                ],
              ),
              const SizedBox(height: AppSpacing.xl),

              // Icon
              Container(
                width: 64,
                height: 64,
                decoration: BoxDecoration(
                  color: AppColors.primary.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: const Icon(
                  Icons.lock_reset,
                  color: AppColors.primary,
                  size: 32,
                ),
              ),
              const SizedBox(height: AppSpacing.lg),

              // Title
              Text(
                'Passwort\nzurücksetzen',
                style: AppTypography.displayLarge.copyWith(
                  color: isDark ? Colors.white : AppColors.textPrimary,
                  fontWeight: FontWeight.w700,
                ),
              ),
              const SizedBox(height: AppSpacing.md),

              // Description
              Text(
                'Gib deine Schul-E-Mail-Adresse ein. Wir senden dir einen Link, '
                'um dein Passwort sicher zu aktualisieren.',
                style: AppTypography.bodyLarge.copyWith(
                  color: isDark ? Colors.grey[400] : AppColors.textSecondary,
                  height: 1.5,
                ),
                maxLines: 3,
              ),
              const SizedBox(height: AppSpacing.xl),

              // Password Reset Form
              PasswordResetForm(
                onSubmit: _handlePasswordResetRequest,
                isLoading: _isLoading,
              ),
              const SizedBox(height: AppSpacing.xl),

              // Support Link
              Column(
                children: [
                  Text(
                    'Probleme beim Empfang der E-Mail?',
                    style: AppTypography.bodySmall.copyWith(
                      color: isDark ? Colors.grey[500] : Colors.grey[600],
                    ),
                  ),
                  const SizedBox(height: AppSpacing.sm),
                  TextButton.icon(
                    onPressed: () {
                      // TODO: Add support contact functionality
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(
                          content: Text('Support kontaktieren...'),
                        ),
                      );
                    },
                    icon: const Icon(Icons.support_agent, size: 18),
                    label: Text(
                      'Support kontaktieren',
                      style: AppTypography.bodySmall.copyWith(
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
