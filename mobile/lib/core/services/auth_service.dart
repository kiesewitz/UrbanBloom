import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../network/api_client.dart';

class AuthService {
  final ApiClient apiClient;
  final FlutterSecureStorage storage;

  static const String _accessTokenKey = 'access_token';
  static const String _refreshTokenKey = 'refresh_token';

  AuthService({required this.apiClient, required this.storage});

  Future<void> register(
      String email, String password, String firstName, String lastName) async {
    try {
      await apiClient.dio.post('/registration', data: {
        'email': email,
        'password': password,
        'firstName': firstName,
        'lastName': lastName,
      });
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  Future<void> login(String email, String password) async {
    try {
      final response = await apiClient.dio.post('/auth/mobile/login', data: {
        'email': email,
        'password': password,
      });

      final accessToken = response.data['accessToken'];
      final refreshToken = response.data['refreshToken'];

      await _saveTokens(accessToken, refreshToken);
      apiClient.setAuthToken(accessToken);
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  Future<void> logout() async {
    final refreshToken = await storage.read(key: _refreshTokenKey);
    if (refreshToken != null) {
      try {
        await apiClient.dio.post('/auth/logout', data: {
          'refreshToken': refreshToken,
        });
      } catch (e) {
        // Log error but proceed with client-side cleanup
      }
    }
    await _clearTokens();
    apiClient.setAuthToken(null);
  }

  Future<void> resetPassword(String email) async {
    try {
      await apiClient.dio.post('/auth/password/reset-request', data: {
        'email': email,
      });
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  Future<Map<String, dynamic>> getCurrentUser() async {
    try {
      final response = await apiClient.dio.get('/users/me');
      return response.data;
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  Future<bool> isAuthenticated() async {
    final token = await storage.read(key: _accessTokenKey);
    if (token != null) {
      apiClient.setAuthToken(token);
      return true;
    }
    return false;
  }

  Future<void> _saveTokens(String access, String refresh) async {
    await storage.write(key: _accessTokenKey, value: access);
    await storage.write(key: _refreshTokenKey, value: refresh);
  }

  Future<void> _clearTokens() async {
    await storage.delete(key: _accessTokenKey);
    await storage.delete(key: _refreshTokenKey);
  }

  Exception _handleError(DioException e) {
    if (e.response != null && e.response?.data != null) {
      final data = e.response?.data;
      if (data is Map) {
        final message = data['message'] ?? data['error'] ?? 'An unknown error occurred';
        return Exception(message);
      } else if (data is String) {
        return Exception(data);
      }
    }
    return Exception('Connection error');
  }
}
