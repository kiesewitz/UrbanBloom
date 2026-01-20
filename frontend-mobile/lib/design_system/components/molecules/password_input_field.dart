import 'package:flutter/material.dart';
import '../atoms/app_text_field.dart';

/// A password input field molecule with visibility toggle
/// Allows users to show/hide password text
class PasswordInputField extends StatefulWidget {
  /// Creates a password input field
  const PasswordInputField({
    required this.label,
    required this.controller,
    this.hintText,
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

  /// Validator function for form validation
  final String? Function(String?)? validator;

  /// Called when the text changes
  final void Function(String)? onChanged;

  @override
  State<PasswordInputField> createState() => _PasswordInputFieldState();
}

class _PasswordInputFieldState extends State<PasswordInputField> {
  bool _obscureText = true;

  void _toggleVisibility() {
    setState(() {
      _obscureText = !_obscureText;
    });
  }

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
            widget.label,
            style: theme.textTheme.labelLarge?.copyWith(
              fontWeight: FontWeight.w600,
              color: isDark ? Colors.white : const Color(0xFF181811),
            ),
          ),
        ),
        AppTextField(
          controller: widget.controller,
          hintText: widget.hintText,
          obscureText: _obscureText,
          keyboardType: TextInputType.visiblePassword,
          prefixIcon: Icon(
            Icons.lock_outline,
            color: isDark
                ? const Color(0xFFa1a18f)
                : const Color(0xFF8c8b5f),
          ),
          suffixIcon: IconButton(
            icon: Icon(
              _obscureText ? Icons.visibility_outlined : Icons.visibility_off_outlined,
              size: 20,
              color: isDark
                  ? const Color(0xFFa1a18f)
                  : const Color(0xFF8c8b5f),
            ),
            onPressed: _toggleVisibility,
          ),
          validator: widget.validator,
          onChanged: widget.onChanged,
        ),
      ],
    );
  }
}
