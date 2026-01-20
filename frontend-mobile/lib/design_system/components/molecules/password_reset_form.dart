import 'package:flutter/material.dart';
import '../atoms/app_primary_button.dart';
import '../molecules/labeled_input_field.dart';

/// A password reset request form molecule
/// Handles email input for password reset requests
class PasswordResetForm extends StatefulWidget {
  /// Creates a password reset form
  const PasswordResetForm({
    required this.onSubmit,
    this.isLoading = false,
    this.initialEmail,
    super.key,
  });

  /// Called when the form is submitted with valid email
  final void Function(String email) onSubmit;

  /// Whether the submission process is in progress
  final bool isLoading;

  /// Optional pre-filled email address
  final String? initialEmail;

  @override
  State<PasswordResetForm> createState() => _PasswordResetFormState();
}

class _PasswordResetFormState extends State<PasswordResetForm> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _emailController;

  @override
  void initState() {
    super.initState();
    _emailController = TextEditingController(text: widget.initialEmail);
  }

  @override
  void dispose() {
    _emailController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Email Field
          LabeledInputField(
            label: 'Schul-E-Mail-Adresse',
            controller: _emailController,
            hintText: 'name@schule.de',
            keyboardType: TextInputType.emailAddress,
            prefixIcon: Icons.email_outlined,
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Bitte E-Mail-Adresse eingeben';
              }
              if (!value.contains('@') || !value.contains('.')) {
                return 'Bitte gültige E-Mail-Adresse eingeben';
              }
              return null;
            },
          ),
          const SizedBox(height: 32),

          // Submit Button
          AppPrimaryButton(
            onPressed: widget.isLoading
                ? null
                : () {
                    if (_formKey.currentState?.validate() ?? false) {
                      widget.onSubmit(_emailController.text.trim());
                    }
                  },
            label: widget.isLoading ? 'Wird gesendet...' : 'Link anfordern',
            icon: widget.isLoading ? null : Icons.send,
          ),
        ],
      ),
    );
  }
}
