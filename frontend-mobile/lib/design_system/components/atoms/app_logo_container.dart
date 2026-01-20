import 'package:flutter/material.dart';

/// A logo container atom displaying an icon within a styled box
/// Used for branding and visual hierarchy
class AppLogoContainer extends StatelessWidget {
  /// Creates a logo container
  const AppLogoContainer({
    required this.icon,
    this.size = 64,
    this.iconSize = 32,
    this.backgroundColor,
    this.iconColor = Colors.white,
    super.key,
  });

  /// Icon to display
  final IconData icon;

  /// Size of the container
  final double size;

  /// Size of the icon
  final double iconSize;

  /// Background color (defaults to primary color)
  final Color? backgroundColor;

  /// Icon color
  final Color iconColor;

  @override
  Widget build(BuildContext context) {
    const primaryColor = Color(0xFF0F63A8);
    final effectiveBackgroundColor = backgroundColor ?? primaryColor;

    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        color: effectiveBackgroundColor,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: primaryColor.withValues(alpha: 0.3),
            blurRadius: 12,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Icon(
        icon,
        size: iconSize,
        color: iconColor,
      ),
    );
  }
}
