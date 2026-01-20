import 'package:flutter/material.dart';
import '../atoms/app_primary_button.dart';
import '../molecules/labeled_input_field.dart';
import '../molecules/password_input_field.dart';

/// A login form organism combining input fields and login button
/// Handles user authentication input
class LoginForm extends StatefulWidget {
  /// Creates a login form
  const LoginForm({
    required this.onLoginPressed,
    required this.onForgotPasswordPressed,
    this.isLoading = false,
    super.key,
  });

  /// Called when the login button is pressed with email and password
  final void Function(String email, String password) onLoginPressed;

  /// Called when the forgot password link is pressed
  final VoidCallback onForgotPasswordPressed;

  /// Whether the login process is in progress
  final bool isLoading;

  @override
  State<LoginForm> createState() => _LoginFormState();
}

class _LoginFormState extends State<LoginForm> {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Form(
      key: _formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Email Field
          LabeledInputField(
            label: 'School Email',
            controller: _emailController,
            hintText: 'student@school.edu',
            keyboardType: TextInputType.emailAddress,
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please enter your email';
              }
              if (!value.contains('@')) {
                return 'Please enter a valid email';
              }
              return null;
            },
          ),
          const SizedBox(height: 16),

          // Password Field
          PasswordInputField(
            label: 'Password',
            controller: _passwordController,
            hintText: '********',
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please enter your password';
              }
              if (value.length < 8) {
                return 'Password must be at least 8 characters long';
              }
              return null;
            },
          ),

          // Forgot Password Link
          Align(
            alignment: Alignment.centerRight,
            child: TextButton(
              onPressed: widget.onForgotPasswordPressed,
              child: Text(
                'Forgot Password?',
                style: theme.textTheme.bodyMedium?.copyWith(
                  color: theme.primaryColor,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
          ),

          const SizedBox(height: 24),

          // Login Button
          AppPrimaryButton(
            onPressed: widget.isLoading
                ? null
                : () {
                    if (_formKey.currentState?.validate() ?? false) {
                      widget.onLoginPressed(
                        _emailController.text.trim(),
                        _passwordController.text,
                      );
                    }
                  },
            label: widget.isLoading
                ? 'Logging in...'
                : 'Log In',
          ),
        ],
      ),
    );
  }
}