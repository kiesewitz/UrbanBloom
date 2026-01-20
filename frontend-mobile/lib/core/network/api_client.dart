import 'package:dio/dio.dart';
import '../config/environment.dart';
import '../services/token_storage.dart';

/// API Client Configuration
class ApiClient {
  static String get baseUrl => Config.apiBaseUrl;
  static const Duration timeout = Duration(seconds: 30);

  late final Dio _dio;
  final TokenStorage _tokenStorage;

  ApiClient(this._tokenStorage) {
    _dio = Dio(
      BaseOptions(
        baseUrl: baseUrl,
        connectTimeout: timeout,
        receiveTimeout: timeout,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ),
    );

    // Add interceptors for logging and authentication
    _dio.interceptors.add(
      LogInterceptor(requestBody: true, responseBody: true, error: true),
    );

    // Add auth interceptor
    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          // Add authorization header if tokens are available
          final tokens = await _tokenStorage.getTokens();
          if (tokens != null) {
            options.headers['Authorization'] = tokens.authorizationHeader;
          }
          handler.next(options);
        },
      ),
    );
  }

  Dio get dio => _dio;
}
