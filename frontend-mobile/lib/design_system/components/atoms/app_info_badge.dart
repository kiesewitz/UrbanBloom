import 'package:flutter/material.dart';

/// An info badge atom displaying informational text with an icon
/// Used for notices, warnings, or helpful hints
class AppInfoBadge extends StatelessWidget {
  /// Creates an info badge
  const AppInfoBadge({
    required this.text,
    this.icon = Icons.info_outline,
    this.backgroundColor,
    this.foregroundColor,
    super.key,
  });

  /// Text to display in the badge
  final String text;

  /// Icon to display
  final IconData icon;

  /// Background color (defaults to primary color with opacity)
  final Color? backgroundColor;

  /// Text and icon color (defaults to primary color)
  final Color? foregroundColor;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    
    const primaryColor = Color(0xFF0F63A8);
    final effectiveBackgroundColor = backgroundColor ?? 
        (isDark 
            ? primaryColor.withValues(alpha: 0.2)
            : primaryColor.withValues(alpha: 0.1));
    final effectiveForegroundColor = foregroundColor ?? 
        (isDark ? const Color(0xFF64B5F6) : primaryColor);

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        color: effectiveBackgroundColor,
        borderRadius: BorderRadius.circular(999),
        border: Border.all(
          color: isDark 
              ? primaryColor.withValues(alpha: 0.3)
              : primaryColor.withValues(alpha: 0.2),
        ),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(
            icon,
            size: 18,
            color: effectiveForegroundColor,
          ),
          const SizedBox(width: 8),
          Flexible(
            child: Text(
              text,
              style: theme.textTheme.labelSmall?.copyWith(
                color: effectiveForegroundColor,
                fontWeight: FontWeight.w600,
                letterSpacing: 0.5,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
