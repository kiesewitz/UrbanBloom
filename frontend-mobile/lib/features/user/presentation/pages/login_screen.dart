import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../../design_system/components/molecules/molecules.dart';
import '../../../../design_system/components/organisms/organisms.dart';
import '../../../../core/di/providers.dart';

/// Login screen for user authentication
/// Displays hero section, login form, and sign up footer
class LoginScreen extends ConsumerStatefulWidget {
  /// Creates a login screen
  const LoginScreen({super.key});

  @override
  ConsumerState<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends ConsumerState<LoginScreen> {
  bool _isLoading = false;

  Future<void> _handleLogin(String email, String password) async {
    setState(() {
      _isLoading = true;
    });

    try {
      final userService = ref.read(userServiceProvider);
      await userService.login(email, password);

      if (mounted) {
        context.go('/profile');
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('Login failed: $e')));
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
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: ConstrainedBox(
            constraints: BoxConstraints(
              minHeight: MediaQuery.of(context).size.height - 32,
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // Hero Section with Image
                const LoginHeroSection(
                  title: 'Login',
                  description:
                      'Access the school library catalog to reserve and borrow books.',
                  imageUrl:
                      'https://lh3.googleusercontent.com/aida-public/AB6AXuBagD463ALDaBeTpBjZWvPSJVJK-4YlhLDQKiNMIBlSq2Rik3pNiy_dutHDHfJCDYOIzpVsX_jk4LWMllXKNz89uwdcPlytvi54F59aOXTtLgm0vvxg_E5EhJAAGTy6_moRmuEbxtYuGXU1PnXfDgiPz7__sC34DX3MewFFlfw353TtDXnYvrcbpqOBTK8Qn3VNUz3NOgqKhmNOoT2caGlpNevuNhcu4OIjWIA2qzMYnapcK7hqAV1NHpbT3iP6AtFoZvlAt1Aglg',
                ),

                const SizedBox(height: 24),

                // Header Text
                Text(
                  'Login',
                  style: theme.textTheme.headlineMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: isDark ? Colors.white : const Color(0xFF111518),
                  ),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 8),
                Text(
                  'Access the school library catalog to reserve and borrow books.',
                  style: theme.textTheme.bodyLarge?.copyWith(
                    color: isDark ? Colors.grey[400] : const Color(0xFF617789),
                  ),
                  textAlign: TextAlign.center,
                ),

                const SizedBox(height: 32),

                // Login Form
                LoginForm(
                  onLoginPressed: _handleLogin,
                  onForgotPasswordPressed: () {
                    context.push('/forgot-password');
                  },
                  isLoading: _isLoading,
                ),

                const SizedBox(height: 32),

                // Footer - Sign Up
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      "Don't have an account? ",
                      style: theme.textTheme.bodyMedium?.copyWith(
                        color: isDark
                            ? Colors.grey[400]
                            : const Color(0xFF617789),
                      ),
                    ),
                    TextButton(
                      onPressed: () {
                        // Navigate to registration screen
                        context.go('/register');
                      },
                      child: Text(
                        'Sign up',
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: theme.primaryColor,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
