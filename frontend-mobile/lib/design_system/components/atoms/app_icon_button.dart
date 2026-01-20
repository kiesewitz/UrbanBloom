import 'package:flutter/material.dart';

/// An icon button atom following the app's design system
/// Used for actions like back, help, visibility toggle, etc.
class AppIconButton extends StatelessWidget {
  /// Creates an icon button
  const AppIconButton({
    required this.icon,
    required this.onPressed,
    this.semanticLabel,
    this.size = 40.0,
    this.iconSize = 24.0,
    this.color,
    super.key,
  });

  /// The icon to display
  final IconData icon;

  /// Called when the button is pressed
  final VoidCallback? onPressed;

  /// Semantic label for accessibility
  final String? semanticLabel;

  /// Size of the button container
  final double size;

  /// Size of the icon
  final double iconSize;

  /// Icon color (defaults to theme color if not provided)
  final Color? color;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final effectiveColor = color ?? theme.colorScheme.onSurface;

    return Semantics(
      label: semanticLabel,
      button: true,
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: onPressed,
          borderRadius: BorderRadius.circular(size / 2),
          child: Container(
            width: size,
            height: size,
            alignment: Alignment.center,
            child: Icon(
              icon,
              size: iconSize,
              color: effectiveColor,
            ),
          ),
        ),
      ),
    );
  }
}
