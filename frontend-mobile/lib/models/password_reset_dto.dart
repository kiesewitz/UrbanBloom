/// Data transfer objects for password reset API communication
/// Based on API spec: docs/api/user.yaml

/// Request model for password reset
class PasswordResetRequestDTO {
  /// User email address to send password reset email to
  final String email;

  /// Creates a password reset request
  const PasswordResetRequestDTO({required this.email});

  /// Converts to JSON for API request
  Map<String, dynamic> toJson() => {'email': email};

  /// Creates instance from JSON
  factory PasswordResetRequestDTO.fromJson(Map<String, dynamic> json) {
    return PasswordResetRequestDTO(email: json['email'] as String);
  }
}

/// Response model for password reset request
class PasswordResetResponseDTO {
  /// Success or informational message
  final String message;

  /// Creates a password reset response
  const PasswordResetResponseDTO({required this.message});

  /// Creates instance from JSON response
  factory PasswordResetResponseDTO.fromJson(Map<String, dynamic> json) {
    return PasswordResetResponseDTO(message: json['message'] as String);
  }

  /// Converts to JSON
  Map<String, dynamic> toJson() => {'message': message};
}
