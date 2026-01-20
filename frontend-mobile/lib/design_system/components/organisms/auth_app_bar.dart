import 'package:flutter/material.dart';
import '../atoms/app_icon_button.dart';

/// An app bar organism with navigation and action buttons
/// Provides consistent header styling across the app
class AuthAppBar extends StatelessWidget implements PreferredSizeWidget {
  /// Creates an auth app bar
  const AuthAppBar({
    this.onBackPressed,
    this.onHelpPressed,
    this.showBackButton = true,
    this.showHelpButton = true,
    super.key,
  });

  /// Called when the back button is pressed
  final VoidCallback? onBackPressed;

  /// Called when the help button is pressed
  final VoidCallback? onHelpPressed;

  /// Whether to show the back button
  final bool showBackButton;

  /// Whether to show the help button
  final bool showHelpButton;

  @override
  Size get preferredSize => const Size.fromHeight(56);

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Container(
      decoration: BoxDecoration(
        color: (isDark
                ? const Color(0xFF23220f)
                : const Color(0xFFf8f8f5))
            .withValues(alpha: 0.9),
        border: Border(
          bottom: BorderSide(
            color: isDark
                ? const Color(0xFF4a4a35).withValues(alpha: 0.3)
                : const Color(0xFFe6e6db).withValues(alpha: 0.3),
            width: 0.5,
          ),
        ),
      ),
      child: SafeArea(
        bottom: false,
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 4),
          child: Row(
            children: [
              if (showBackButton)
                AppIconButton(
                  icon: Icons.arrow_back,
                  onPressed: onBackPressed ?? () => Navigator.of(context).pop(),
                  semanticLabel: 'Zurück',
                )
              else
                const SizedBox(width: 40),
              const Spacer(),
              if (showHelpButton)
                AppIconButton(
                  icon: Icons.help_outline,
                  onPressed: onHelpPressed,
                  semanticLabel: 'Hilfe',
                )
              else
                const SizedBox(width: 40),
            ],
          ),
        ),
      ),
    );
  }
}
