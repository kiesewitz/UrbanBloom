import 'package:flutter/material.dart';
import '../molecules/labeled_input_field.dart';
import '../molecules/password_input_field.dart';
import '../atoms/app_primary_button.dart';

/// A registration form organism containing all input fields and submit button
/// Handles user registration with validation
class RegistrationForm extends StatefulWidget {
  /// Creates a registration form
  const RegistrationForm({
    required this.onSubmit,
    this.isLoading = false,
    super.key,
  });

  /// Called when the form is submitted with valid data
  final void Function({
    required String email,
    required String password,
    required String firstName,
    required String lastName,
  }) onSubmit;

  /// Whether the form is in loading state
  final bool isLoading;

  @override
  State<RegistrationForm> createState() => _RegistrationFormState();
}

class _RegistrationFormState extends State<RegistrationForm> {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();
  final _firstNameController = TextEditingController();
  final _lastNameController = TextEditingController();

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    _firstNameController.dispose();
    _lastNameController.dispose();
    super.dispose();
  }

  String? _validateEmail(String? value) {
    if (value == null || value.isEmpty) {
      return 'Bitte E-Mail-Adresse eingeben';
    }
    if (!value.endsWith('@schule.de') && !value.endsWith('@gmail.com')) {
      return 'Nur E-Mails mit @schule.de oder @gmail.com erlaubt';
    }
    
    return null;
  }

  String? _validatePassword(String? value) {
    if (value == null || value.isEmpty) {
      return 'Bitte Passwort eingeben';
    }
    if (value.length < 8) {
      return 'Passwort muss mindestens 8 Zeichen lang sein';
    }
    return null;
  }

  String? _validateConfirmPassword(String? value) {
    if (value == null || value.isEmpty) {
      return 'Bitte Passwort bestätigen';
    }
    if (value != _passwordController.text) {
      return 'Passwörter stimmen nicht überein';
    }
    return null;
  }

  String? _validateName(String? value) {
    if (value == null || value.isEmpty) {
      return 'Bitte Namen eingeben';
    }
    if (value.length < 2) {
      return 'Name muss mindestens 2 Zeichen lang sein';
    }
    return null;
  }

  void _handleSubmit() {
    if (_formKey.currentState?.validate() ?? false) {
      widget.onSubmit(
        email: _emailController.text,
        password: _passwordController.text,
        firstName: _firstNameController.text,
        lastName: _lastNameController.text,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        children: [
          LabeledInputField(
            label: 'Schul-E-Mail',
            controller: _emailController,
            hintText: 'vorname.nachname@schule.de',
            keyboardType: TextInputType.emailAddress,
            prefixIcon: Icons.mail_outline,
            validator: _validateEmail,
          ),
          const SizedBox(height: 20),
          LabeledInputField(
            label: 'Vorname',
            controller: _firstNameController,
            hintText: 'Ihr Vorname',
            keyboardType: TextInputType.name,
            prefixIcon: Icons.person_outline,
            validator: _validateName,
          ),
          const SizedBox(height: 20),
          LabeledInputField(
            label: 'Nachname',
            controller: _lastNameController,
            hintText: 'Ihr Nachname',
            keyboardType: TextInputType.name,
            prefixIcon: Icons.person_outline,
            validator: _validateName,
          ),
          const SizedBox(height: 20),
          PasswordInputField(
            label: 'Neues Passwort',
            controller: _passwordController,
            hintText: 'Mindestens 8 Zeichen',
            validator: _validatePassword,
          ),
          const SizedBox(height: 20),
          PasswordInputField(
            label: 'Passwort bestätigen',
            controller: _confirmPasswordController,
            hintText: 'Passwort wiederholen',
            validator: _validateConfirmPassword,
          ),
          const SizedBox(height: 32),
          AppPrimaryButton(
            onPressed: _handleSubmit,
            label: 'Konto erstellen',
            icon: Icons.arrow_forward,
            isLoading: widget.isLoading,
          ),
        ],
      ),
    );
  }
}
