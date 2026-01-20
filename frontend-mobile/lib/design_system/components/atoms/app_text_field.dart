import 'package:flutter/material.dart';

/// A text field atom following the app's design system
/// Base input field with configurable properties
class AppTextField extends StatelessWidget {
  /// Creates a text field
  const AppTextField({
    required this.controller,
    this.hintText,
    this.labelText,
    this.obscureText = false,
    this.keyboardType,
    this.prefixIcon,
    this.suffixIcon,
    this.validator,
    this.onChanged,
    this.enabled = true,
    this.maxLines = 1,
    super.key,
  });

  /// Controller for the text field
  final TextEditingController controller;

  /// Hint text displayed when empty
  final String? hintText;

  /// Label text displayed above the field
  final String? labelText;

  /// Whether the text should be obscured (for passwords)
  final bool obscureText;

  /// Type of keyboard to show
  final TextInputType? keyboardType;

  /// Icon to display at the start
  final Widget? prefixIcon;

  /// Icon or widget to display at the end
  final Widget? suffixIcon;

  /// Validator function for form validation
  final String? Function(String?)? validator;

  /// Called when the text changes
  final void Function(String)? onChanged;

  /// Whether the field is enabled
  final bool enabled;

  /// Maximum number of lines
  final int maxLines;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return TextFormField(
      controller: controller,
      obscureText: obscureText,
      keyboardType: keyboardType,
      validator: validator,
      onChanged: onChanged,
      enabled: enabled,
      maxLines: maxLines,
      style: theme.textTheme.bodyLarge?.copyWith(
        color: isDark ? Colors.white : const Color(0xFF181811),
      ),
      decoration: InputDecoration(
        hintText: hintText,
        labelText: labelText,
        prefixIcon: prefixIcon,
        suffixIcon: suffixIcon,
        filled: true,
        fillColor: isDark 
            ? Colors.white.withValues(alpha: 0.05)
            : Colors.white,
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(
            color: isDark 
                ? const Color(0xFF4a4a35)
                : const Color(0xFFe6e6db),
          ),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(
            color: isDark 
                ? const Color(0xFF4a4a35)
                : const Color(0xFFe6e6db),
          ),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(
            color: Color(0xFF0F63A8),
            width: 2,
          ),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(
            color: theme.colorScheme.error,
          ),
        ),
        focusedErrorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(
            color: theme.colorScheme.error,
            width: 2,
          ),
        ),
        contentPadding: const EdgeInsets.symmetric(
          horizontal: 16,
          vertical: 14,
        ),
      ),
    );
  }
}
