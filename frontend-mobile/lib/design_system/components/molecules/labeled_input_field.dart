import 'package:flutter/material.dart';
import '../atoms/app_text_field.dart';

/// A labeled input field molecule combining a label and text field
/// Used for form inputs with consistent styling
class LabeledInputField extends StatelessWidget {
  /// Creates a labeled input field
  const LabeledInputField({
    required this.label,
    required this.controller,
    this.hintText,
    this.obscureText = false,
    this.keyboardType,
    this.prefixIcon,
    this.suffixIcon,
    this.validator,
    this.onChanged,
    super.key,
  });

  /// Label text displayed above the field
  final String label;

  /// Controller for the text field
  final TextEditingController controller;

  /// Hint text displayed when empty
  final String? hintText;

  /// Whether the text should be obscured (for passwords)
  final bool obscureText;

  /// Type of keyboard to show
  final TextInputType? keyboardType;

  /// Icon to display at the start
  final IconData? prefixIcon;

  /// Widget to display at the end
  final Widget? suffixIcon;

  /// Validator function for form validation
  final String? Function(String?)? validator;

  /// Called when the text changes
  final void Function(String)? onChanged;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.only(left: 4, bottom: 8),
          child: Text(
            label,
            style: theme.textTheme.labelLarge?.copyWith(
              fontWeight: FontWeight.w600,
              color: isDark ? Colors.white : const Color(0xFF181811),
            ),
          ),
        ),
        AppTextField(
          controller: controller,
          hintText: hintText,
          obscureText: obscureText,
          keyboardType: keyboardType,
          prefixIcon: prefixIcon != null
              ? Icon(
                  prefixIcon,
                  color: isDark
                      ? const Color(0xFFa1a18f)
                      : const Color(0xFF8c8b5f),
                )
              : null,
          suffixIcon: suffixIcon,
          validator: validator,
          onChanged: onChanged,
        ),
      ],
    );
  }
}
