import 'dart:convert';
import 'package:dio/dio.dart';

class UserApi {
  final Dio _dio;

  UserApi(this._dio);

  Future<Response> registerUser(
    Map<String, dynamic> registrationRequest,
  ) async {
    return await _dio.post(
      '/api/v1/registration',
      data: jsonEncode(registrationRequest),
    );
  }

  Future<Response> checkEmailAvailability(String email) async {
    return await _dio.get(
      '/api/v1/registration/check-email',
      queryParameters: {'email': email},
    );
  }

  /// Requests a password reset email for the given email address
  Future<Response> requestPasswordReset(String email) async {
    return await _dio.post(
      '/api/v1/auth/password/reset-request',
      data: jsonEncode({'email': email}),
    );
  }
}
