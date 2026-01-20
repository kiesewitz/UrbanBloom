import 'package:dio/dio.dart';
import '../api/generated/user_api.dart';
import '../services/auth_service.dart';
import '../services/token_storage.dart';
import 'models/registration_request.dart';
import 'models/registration_response.dart';
import 'models/email_availability.dart';
import '../../models/auth_tokens.dart';
import '../../models/user_profile.dart';

/// Generic API exception for higher-level handling
class ApiException implements Exception {
  final String message;
  final int? statusCode;

  ApiException(this.message, [this.statusCode]);

  @override
  String toString() => 'ApiException: $message (status: $statusCode)';
}

/// Service methods
class UserService {
  final UserApi _generatedApi;
  final AuthService _authService;
  final TokenStorage _tokenStorage;

  UserService(Dio dio, TokenStorage tokenStorage)
      : _generatedApi = UserApi(dio),
        _authService = AuthService(dio),
        _tokenStorage = tokenStorage;

  Future<RegistrationResponse> register(RegistrationRequest request) async {
    try {
      final response = await _generatedApi.registerUser(request.toJson());
      return RegistrationResponse.fromJson(response.data);
    } on DioException catch (e) {
      if (e.response != null) {
        throw ApiException(e.message ?? 'Request failed', e.response?.statusCode);
      }
      throw ApiException(e.message ?? 'Network error');
    }
  }

  Future<EmailAvailability> checkEmailAvailability(String email) async {
    try {
      final response = await _generatedApi.checkEmailAvailability(email);
      return EmailAvailability.fromJson(response.data);
    } on DioException catch (e) {
      if (e.response != null) {
        throw ApiException(e.message ?? 'Request failed', e.response?.statusCode);
      }
      throw ApiException(e.message ?? 'Network error');
    }
  }

  Future<AuthTokens> login(String email, String password) async {
    final tokens = await _authService.login(email, password);
    await _tokenStorage.storeTokens(tokens);
    return tokens;
  }

  Future<UserProfile> getUserProfile() async {
    return _authService.getUserProfile();
  }

  Future<void> logout() async {
    await _authService.logout();
    await _tokenStorage.clearTokens();
  }
}