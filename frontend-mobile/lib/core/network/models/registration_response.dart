class RegistrationResponse {
  final String? userId;
  final String? email;
  final String? message;
  final bool? verificationRequired;

  RegistrationResponse({this.userId, this.email, this.message, this.verificationRequired});

  factory RegistrationResponse.fromJson(Map<String, dynamic> json) => RegistrationResponse(
        userId: json['userId'] as String?,
        email: json['email'] as String?,
        message: json['message'] as String?,
        verificationRequired: json['verificationRequired'] as bool?,
      );
}