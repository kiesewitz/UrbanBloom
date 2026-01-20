import 'package:flutter/material.dart';

/// A hero section molecule displaying an image, title, and description
/// Used for branding and context setting in authentication flows
class LoginHeroSection extends StatelessWidget {
  /// Creates a login hero section
  const LoginHeroSection({
    required this.title,
    required this.description,
    this.imageUrl,
    super.key,
  });

  /// Main title text
  final String title;

  /// Description text below the title
  final String description;

  /// URL of the background image
  final String? imageUrl;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Container(
      width: double.infinity,
      height: 240,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(16),
        image: imageUrl != null
            ? DecorationImage(
                image: NetworkImage(imageUrl!),
                fit: BoxFit.cover,
              )
            : null,
        color: imageUrl == null ? theme.primaryColor : null,
      ),
      child: Container(
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16),
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [
              Colors.transparent,
              Colors.black.withValues(alpha: 0.4),
            ],
          ),
        ),
      ),
    );
  }
}