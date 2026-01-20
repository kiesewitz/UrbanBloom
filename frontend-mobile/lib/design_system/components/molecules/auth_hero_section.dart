import 'package:flutter/material.dart';
import '../atoms/app_logo_container.dart';

/// A hero section molecule displaying logo, title, and description
/// Used for branding and context setting in authentication flows
class AuthHeroSection extends StatelessWidget {
  /// Creates an auth hero section
  const AuthHeroSection({
    required this.title,
    required this.description,
    this.icon = Icons.school,
    super.key,
  });

  /// Main title text
  final String title;

  /// Description text below the title
  final String description;

  /// Icon to display in the logo container
  final IconData icon;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Column(
      children: [
        AppLogoContainer(
          icon: icon,
          size: 64,
          iconSize: 32,
        ),
        const SizedBox(height: 24),
        Text(
          title,
          style: theme.textTheme.headlineMedium?.copyWith(
            fontWeight: FontWeight.bold,
            letterSpacing: -0.5,
            color: isDark ? Colors.white : const Color(0xFF181811),
          ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 8),
        ConstrainedBox(
          constraints: const BoxConstraints(maxWidth: 280),
          child: Text(
            description,
            style: theme.textTheme.bodyMedium?.copyWith(
              fontWeight: FontWeight.w500,
              color: isDark
                  ? const Color(0xFFa1a18f)
                  : const Color(0xFF8c8b5f),
            ),
            textAlign: TextAlign.center,
          ),
        ),
      ],
    );
  }
}
