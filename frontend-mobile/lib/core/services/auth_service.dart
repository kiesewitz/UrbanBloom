import 'package:dio/dio.dart';
import '../../models/auth_tokens.dart';
import '../../models/user_profile.dart';

/// Authentication service for handling login, refresh, and logout operations
class AuthService {
  final Dio _dio;

  AuthService(this._dio);

  /// Login with email and password
  /// Returns AuthTokens on success
  Future<AuthTokens> login(String email, String password) async {
    try {
      final response = await _dio.post(
        '/api/v1/auth/login',
        data: {
          'email': email,
          'password': password,
        },
      );

      return AuthTokens.fromJson(response.data);
    } on DioException catch (e) {
      if (e.response?.statusCode == 401) {
        throw Exception('Invalid email or password');
      }
      throw Exception('Login failed: ${e.message}');
    }
  }

  /// Refresh access token using refresh token
  /// Returns new AuthTokens on success
  Future<AuthTokens> refreshToken(String refreshToken) async {
    try {
      final response = await _dio.post(
        '/api/v1/auth/refresh',
        data: {
          'refresh_token': refreshToken,
        },
      );

      return AuthTokens.fromJson(response.data);
    } on DioException catch (e) {
      if (e.response?.statusCode == 401) {
        throw Exception('Invalid refresh token');
      }
      throw Exception('Token refresh failed: ${e.message}');
    }
  }

  /// Logout by clearing tokens (client-side only, no server call needed)
  Future<void> logout() async {
    // In a real implementation, you might want to call a logout endpoint
    // to invalidate the refresh token on the server
    // For now, just return successfully
    return Future.value();
  }

  /// Get current user profile
  /// Requires valid access token in Dio interceptors
  Future<UserProfile> getUserProfile() async {
    try {
      final response = await _dio.get('/api/v1/users/me');
      return UserProfile.fromJson(response.data);
    } on DioException catch (e) {
      if (e.response?.statusCode == 401) {
        throw Exception('Authentication required');
      }
      throw Exception('Failed to get user profile: ${e.message}');
    }
  }
}