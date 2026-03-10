import 'dart:html' as html;
import 'package:dio/dio.dart';
import '../network/api_client.dart';

class AuthService {
  final ApiClient apiClient;

  static const String _accessTokenKey = 'admin_access_token';
  static const String _refreshTokenKey = 'admin_refresh_token';

  AuthService({required this.apiClient});

  Future<void> loginAdmin(String email, String password) async {
    try {
      final response = await apiClient.dio.post('/auth/admin/login', data: {
        'email': email,
        'password': password,
      });

      final accessToken = response.data['accessToken'];
      final refreshToken = response.data['refreshToken'];

      _saveTokens(accessToken, refreshToken);
      apiClient.setAuthToken(accessToken);
    } on DioException catch (e) {
      if (e.response?.statusCode == 401 && 
          e.response?.data['error'] == 'UPDATE_PASSWORD_REQUIRED') {
        throw UpdatePasswordRequiredException(
          e.response?.data['message'] ?? 'Password update required',
          e.response?.data['keycloakLoginUrl'],
        );
      }
      throw _handleError(e);
    }
  }

  Future<void> logout() async {
    final refreshToken = html.window.localStorage[_refreshTokenKey];
    if (refreshToken != null) {
      try {
        await apiClient.dio.post('/auth/logout', data: {
          'refreshToken': refreshToken,
        });
      } catch (e) {}
    }
    _clearTokens();
    apiClient.setAuthToken(null);
  }

  Future<bool> isAuthenticated() async {
    final token = html.window.localStorage[_accessTokenKey];
    if (token != null) {
      apiClient.setAuthToken(token);
      return true;
    }
    return false;
  }

  void _saveTokens(String access, String refresh) {
    html.window.localStorage[_accessTokenKey] = access;
    html.window.localStorage[_refreshTokenKey] = refresh;
  }

  void _clearTokens() {
    html.window.localStorage.remove(_accessTokenKey);
    html.window.localStorage.remove(_refreshTokenKey);
  }

  Exception _handleError(DioException e) {
    if (e.response != null && e.response?.data is Map) {
      final message = e.response?.data['message'] ?? 'An unknown error occurred';
      return Exception(message);
    }
    return Exception('Connection error');
  }
}

class UpdatePasswordRequiredException implements Exception {
  final String message;
  final String? loginUrl;
  UpdatePasswordRequiredException(this.message, this.loginUrl);
  @override
  String toString() => message;
}
