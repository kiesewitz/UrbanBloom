class RegistrationRequest {
  final String email;
  final String password;
  final String firstName;
  final String lastName;
  final String? studentId;
  final String? schoolClass;

  RegistrationRequest({
    required this.email,
    required this.password,
    required this.firstName,
    required this.lastName,
    this.studentId,
    this.schoolClass,
  });

  Map<String, dynamic> toJson() => {
        'email': email,
        'password': password,
        'firstName': firstName,
        'lastName': lastName,
        'studentId': studentId,
        'schoolClass': schoolClass,
      };
}