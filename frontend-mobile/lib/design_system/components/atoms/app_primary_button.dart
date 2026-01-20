import 'package:flutter/material.dart';

/// A primary button atom following the app's design system
/// Used for main call-to-action buttons
class AppPrimaryButton extends StatelessWidget {
  /// Creates a primary button
  const AppPrimaryButton({
    required this.onPressed,
    required this.label,
    this.icon,
    this.isLoading = false,
    this.fullWidth = true,
    super.key,
  });

  /// Called when the button is pressed
  final VoidCallback? onPressed;

  /// Button label text
  final String label;

  /// Optional trailing icon
  final IconData? icon;

  /// Whether the button is in loading state
  final bool isLoading;

  /// Whether the button should take full width
  final bool fullWidth;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    Widget child = isLoading
        ? const SizedBox(
            width: 20,
            height: 20,
            child: CircularProgressIndicator(
              strokeWidth: 2,
              valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
            ),
          )
        : Row(
            mainAxisSize: MainAxisSize.min,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                label,
                style: theme.textTheme.titleMedium?.copyWith(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                ),
              ),
              if (icon != null) ...[
                const SizedBox(width: 8),
                Icon(
                  icon,
                  size: 20,
                  color: Colors.white,
                ),
              ],
            ],
          );

    return SizedBox(
      width: fullWidth ? double.infinity : null,
      child: ElevatedButton(
        onPressed: isLoading ? null : onPressed,
        style: ElevatedButton.styleFrom(
          backgroundColor: const Color(0xFF0F63A8),
          foregroundColor: Colors.white,
          disabledBackgroundColor: const Color(0xFF0F63A8).withValues(alpha: 0.5),
          elevation: 2,
          shadowColor: const Color(0xFF0F63A8).withValues(alpha: 0.2),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(999),
          ),
          padding: const EdgeInsets.symmetric(
            horizontal: 24,
            vertical: 16,
          ),
        ),
        child: child,
      ),
    );
  }
}
