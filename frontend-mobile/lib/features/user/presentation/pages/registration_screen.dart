import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../design_system/components/organisms/auth_app_bar.dart';
import '../../../../design_system/components/molecules/auth_hero_section.dart';
import '../../../../design_system/components/atoms/app_info_badge.dart';
import '../../../../design_system/components/organisms/registration_form.dart';
import '../../../../core/di/providers.dart';
import '../../../../core/network/user_service.dart';
import '../../../../core/network/models/registration_request.dart';

/// Registration screen for creating a new user account
/// Follows the school library app design with email validation
class RegistrationScreen extends ConsumerStatefulWidget {
  /// Creates a registration screen
  const RegistrationScreen({super.key});

  @override
  ConsumerState<RegistrationScreen> createState() => _RegistrationScreenState();
}

class _RegistrationScreenState extends ConsumerState<RegistrationScreen> {
  bool _isLoading = false;

  Future<void> _handleRegistration({
    required String email,
    required String password,
    required String firstName,
    required String lastName,
  }) async {
    setState(() {
      _isLoading = true;
    });

    try {
      final userService = ref.read(userServiceProvider);
      final request = RegistrationRequest(
        email: email,
        password: password,
        firstName: firstName,
        lastName: lastName,
      );
      final response = await userService.register(request);
      
      if (!mounted) return;
      
      // Show success message
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(response.message ?? 'Registrierung erfolgreich!'),
          backgroundColor: Colors.green,
        ),
      );
      
      // Navigate to next screen (e.g., verification or login)
      // Navigator.of(context).pushReplacement(...);
    } catch (error) {
      if (!mounted) return;
      
      final errorMessage = error is ApiException ? error.message : 'Unbekannter Fehler';
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(errorMessage),
          backgroundColor: Colors.red,
        ),
      );
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  void _handleHelp() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Hilfe'),
        content: const Text(
          'Verwenden Sie Ihre Schul-E-Mail-Adresse (endet mit @schule.de), '
          'um ein Konto zu erstellen. Das Passwort muss mindestens 8 Zeichen lang sein.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
      backgroundColor: isDark 
          ? const Color(0xFF23220f)
          : const Color(0xFFf8f8f5),
      appBar: AuthAppBar(
        onBackPressed: () => context.go('/login'),
        onHelpPressed: _handleHelp,
      ),
      body: SafeArea(
        child: LayoutBuilder(
          builder: (context, constraints) {
            return SingleChildScrollView(
              padding: const EdgeInsets.symmetric(horizontal: 24),
              child: ConstrainedBox(
                constraints: const BoxConstraints(
                  maxWidth: 480,
                ),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const SizedBox(height: 16),
                    const AuthHeroSection(
                      title: 'Konto erstellen',
                      description: 
                          'Registriere dich für die Schulbibliothek, '
                          'um Bücher auszuleihen.',
                      icon: Icons.school,
                    ),
                    const SizedBox(height: 32),
                    const AppInfoBadge(
                      text: 'Nur E-Mails mit @schule.de erlaubt',
                      icon: Icons.info_outline,
                    ),
                    const SizedBox(height: 32),
                    RegistrationForm(
                      onSubmit: _handleRegistration,
                      isLoading: _isLoading,
                    ),
                    const SizedBox(height: 32),
                    Padding(
                      padding: const EdgeInsets.symmetric(vertical: 24),
                      child: Text.rich(
                        TextSpan(
                          text: 'Bereits registriert? ',
                          style: theme.textTheme.bodyMedium?.copyWith(
                            color: isDark
                                ? const Color(0xFFa1a18f)
                                : const Color(0xFF8c8b5f),
                          ),
                          children: [
                            WidgetSpan(
                              child: GestureDetector(
                                onTap: () {
                                  // Navigate to login screen
                                  context.go('/login');
                                },
                                child: Text(
                                  'Anmelden',
                                  style: theme.textTheme.bodyMedium?.copyWith(
                                    fontWeight: FontWeight.bold,
                                    color: isDark
                                        ? Colors.white
                                        : const Color(0xFF181811),
                                    decoration: TextDecoration.underline,
                                    decorationColor: const Color(0xFF0F63A8),
                                    decorationThickness: 2,
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                        textAlign: TextAlign.center,
                      ),
                    ),
                  ],
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}
