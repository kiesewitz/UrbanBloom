/// User profile model based on API User schema
class UserProfile {
  final String userId;
  final String schoolIdentity;
  final String firstName;
  final String lastName;
  final String email;
  final String userGroup;
  final int? borrowingLimit;
  final bool? isActive;
  final DateTime? registrationDate;
  final DateTime? lastLoginAt;

  const UserProfile({
    required this.userId,
    required this.schoolIdentity,
    required this.firstName,
    required this.lastName,
    required this.email,
    required this.userGroup,
    this.borrowingLimit,
    this.isActive,
    this.registrationDate,
    this.lastLoginAt,
  });

  /// Factory constructor to create UserProfile from JSON response
  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      userId: json['userId'] as String,
      schoolIdentity: json['schoolIdentity'] as String,
      firstName: json['firstName'] as String,
      lastName: json['lastName'] as String,
      email: json['email'] as String,
      userGroup: json['userGroup'] as String,
      borrowingLimit: json['borrowingLimit'] as int?,
      isActive: json['isActive'] as bool?,
      registrationDate: json['registrationDate'] != null
          ? DateTime.parse(json['registrationDate'] as String)
          : null,
      lastLoginAt: json['lastLoginAt'] != null
          ? DateTime.parse(json['lastLoginAt'] as String)
          : null,
    );
  }

  /// Convert UserProfile to JSON for storage/serialization
  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'schoolIdentity': schoolIdentity,
      'firstName': firstName,
      'lastName': lastName,
      'email': email,
      'userGroup': userGroup,
      'borrowingLimit': borrowingLimit,
      'isActive': isActive,
      'registrationDate': registrationDate?.toIso8601String(),
      'lastLoginAt': lastLoginAt?.toIso8601String(),
    };
  }

  /// Get full display name
  String get displayName => '$firstName $lastName';

  @override
  String toString() {
    return 'UserProfile(userId: $userId, displayName: $displayName, email: $email, userGroup: $userGroup)';
  }
}