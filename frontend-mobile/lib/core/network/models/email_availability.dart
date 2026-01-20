class EmailAvailability {
  final String? email;
  final bool available;

  EmailAvailability({this.email, required this.available});

  factory EmailAvailability.fromJson(Map<String, dynamic> json) => EmailAvailability(
        email: json['email'] as String?,
        available: json['available'] as bool? ?? false,
      );
}