/// Auth tokens model for JWT authentication
class AuthTokens {
  final String accessToken;
  final String refreshToken;
  final String tokenType;
  final int expiresIn;

  const AuthTokens({
    required this.accessToken,
    required this.refreshToken,
    required this.tokenType,
    required this.expiresIn,
  });

  /// Factory constructor to create AuthTokens from JSON response
  factory AuthTokens.fromJson(Map<String, dynamic> json) {
    return AuthTokens(
      accessToken: json['access_token'] as String,
      refreshToken: json['refresh_token'] as String,
      tokenType: json['token_type'] as String,
      expiresIn: json['expires_in'] as int,
    );
  }

  /// Convert AuthTokens to JSON for storage
  Map<String, dynamic> toJson() {
    return {
      'access_token': accessToken,
      'refresh_token': refreshToken,
      'token_type': tokenType,
      'expires_in': expiresIn,
    };
  }

  /// Get the full authorization header value
  String get authorizationHeader => '$tokenType $accessToken';

  @override
  String toString() {
    return 'AuthTokens(accessToken: ${accessToken.substring(0, 10)}..., tokenType: $tokenType, expiresIn: $expiresIn)';
  }
}