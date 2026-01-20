import 'package:flutter_test/flutter_test.dart';
import 'package:school_library_mobile/core/network/api_client.dart';
import 'package:school_library_mobile/core/services/token_storage.dart';
import 'package:dio/dio.dart';
import 'package:mockito/annotations.dart';

@GenerateMocks([TokenStorage])
import 'api_client_test.mocks.dart';

void main() {
  group('ApiClient', () {
    late MockTokenStorage mockTokenStorage;

    setUp(() {
      mockTokenStorage = MockTokenStorage();
    });

    test('should be initialized with correct base URL and timeout', () {
      final client = ApiClient(mockTokenStorage);

      expect(client.dio.options.baseUrl, 'http://localhost:4010');
      expect(client.dio.options.connectTimeout, const Duration(seconds: 30));
      expect(client.dio.options.receiveTimeout, const Duration(seconds: 30));
    });

    test('should have essential headers', () {
      final client = ApiClient(mockTokenStorage);

      expect(client.dio.options.headers['Content-Type'], 'application/json');
      expect(client.dio.options.headers['Accept'], 'application/json');
    });

    test('should have LogInterceptor', () {
      final client = ApiClient(mockTokenStorage);

      final hasLogInterceptor = client.dio.interceptors.any(
        (i) => i is LogInterceptor,
      );
      expect(hasLogInterceptor, isTrue);
    });
  });
}
