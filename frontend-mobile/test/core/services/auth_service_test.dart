import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';
import 'package:dio/dio.dart';
import 'package:school_library_mobile/core/services/auth_service.dart';
import 'package:school_library_mobile/models/auth_tokens.dart';

// Generate mocks
@GenerateMocks([Dio])
import 'auth_service_test.mocks.dart';

void main() {
  late AuthService authService;
  late MockDio mockDio;

  setUp(() {
    mockDio = MockDio();
    authService = AuthService(mockDio);
  });

  group('AuthService', () {
    group('login', () {
      test('should return AuthTokens on successful login', () async {
        // Arrange
        const email = 'test@example.com';
        const password = 'password123';
        final responseData = {
          'access_token': 'access_token_123',
          'refresh_token': 'refresh_token_456',
          'token_type': 'Bearer',
          'expires_in': 3600,
        };
        final response = Response(
          data: responseData,
          statusCode: 200,
          requestOptions: RequestOptions(path: '/api/v1/auth/login'),
        );

        when(mockDio.post(
          '/api/v1/auth/login',
          data: {
            'email': email,
            'password': password,
          },
        )).thenAnswer((_) async => response);

        // Act
        final result = await authService.login(email, password);

        // Assert
        expect(result, isA<AuthTokens>());
        expect(result.accessToken, 'access_token_123');
        expect(result.refreshToken, 'refresh_token_456');
        expect(result.tokenType, 'Bearer');
        expect(result.expiresIn, 3600);
        expect(result.authorizationHeader, 'Bearer access_token_123');
      });

      test('should throw exception on invalid credentials', () async {
        // Arrange
        const email = 'test@example.com';
        const password = 'wrong_password';
        final response = Response(
          data: {'message': 'Invalid credentials'},
          statusCode: 401,
          requestOptions: RequestOptions(path: '/api/v1/auth/login'),
        );

        when(mockDio.post(
          '/api/v1/auth/login',
          data: {
            'email': email,
            'password': password,
          },
        )).thenThrow(DioException(
          requestOptions: RequestOptions(path: '/api/v1/auth/login'),
          response: response,
        ));

        // Act & Assert
        expect(
          () => authService.login(email, password),
          throwsA(isA<Exception>().having(
            (e) => e.toString(),
            'message',
            contains('Invalid email or password'),
          )),
        );
      });

      test('should throw exception on network error', () async {
        // Arrange
        const email = 'test@example.com';
        const password = 'password123';

        when(mockDio.post(
          '/api/v1/auth/login',
          data: {
            'email': email,
            'password': password,
          },
        )).thenThrow(DioException(
          requestOptions: RequestOptions(path: '/api/v1/auth/login'),
          message: 'Network error',
        ));

        // Act & Assert
        expect(
          () => authService.login(email, password),
          throwsA(isA<Exception>().having(
            (e) => e.toString(),
            'message',
            contains('Login failed'),
          )),
        );
      });
    });

    group('refreshToken', () {
      test('should return new AuthTokens on successful refresh', () async {
        // Arrange
        const refreshToken = 'refresh_token_456';
        final responseData = {
          'access_token': 'new_access_token_789',
          'refresh_token': 'new_refresh_token_101',
          'token_type': 'Bearer',
          'expires_in': 3600,
        };
        final response = Response(
          data: responseData,
          statusCode: 200,
          requestOptions: RequestOptions(path: '/api/v1/auth/refresh'),
        );

        when(mockDio.post(
          '/api/v1/auth/refresh',
          data: {
            'refresh_token': refreshToken,
          },
        )).thenAnswer((_) async => response);

        // Act
        final result = await authService.refreshToken(refreshToken);

        // Assert
        expect(result, isA<AuthTokens>());
        expect(result.accessToken, 'new_access_token_789');
        expect(result.refreshToken, 'new_refresh_token_101');
      });

      test('should throw exception on invalid refresh token', () async {
        // Arrange
        const refreshToken = 'invalid_refresh_token';
        final response = Response(
          data: {'message': 'Invalid refresh token'},
          statusCode: 401,
          requestOptions: RequestOptions(path: '/api/v1/auth/refresh'),
        );

        when(mockDio.post(
          '/api/v1/auth/refresh',
          data: {
            'refresh_token': refreshToken,
          },
        )).thenThrow(DioException(
          requestOptions: RequestOptions(path: '/api/v1/auth/refresh'),
          response: response,
        ));

        // Act & Assert
        expect(
          () => authService.refreshToken(refreshToken),
          throwsA(isA<Exception>().having(
            (e) => e.toString(),
            'message',
            contains('Invalid refresh token'),
          )),
        );
      });
    });

    group('logout', () {
      test('should complete successfully', () async {
        // Act
        await authService.logout();

        // Assert - logout is a no-op for now, just ensure it doesn't throw
      });
    });

    group('getUserProfile', () {
      test('should return UserProfile on successful request', () async {
        // Arrange
        final responseData = {
          'userId': 'user-123',
          'schoolIdentity': 'test@school.edu',
          'firstName': 'John',
          'lastName': 'Doe',
          'email': 'john.doe@email.com',
          'userGroup': 'STUDENT',
          'borrowingLimit': 5,
          'isActive': true,
          'registrationDate': '2023-09-01',
          'lastLoginAt': '2023-10-01T10:00:00Z',
        };
        final response = Response(
          data: responseData,
          statusCode: 200,
          requestOptions: RequestOptions(path: '/api/v1/users/me'),
        );

        when(mockDio.get('/api/v1/users/me')).thenAnswer((_) async => response);

        // Act
        final result = await authService.getUserProfile();

        // Assert
        expect(result.userId, 'user-123');
        expect(result.firstName, 'John');
        expect(result.lastName, 'Doe');
        expect(result.displayName, 'John Doe');
        expect(result.userGroup, 'STUDENT');
        expect(result.borrowingLimit, 5);
        expect(result.isActive, true);
      });

      test('should throw exception on authentication error', () async {
        // Arrange
        final response = Response(
          data: {'message': 'Unauthorized'},
          statusCode: 401,
          requestOptions: RequestOptions(path: '/api/v1/users/me'),
        );

        when(mockDio.get('/api/v1/users/me')).thenThrow(DioException(
          requestOptions: RequestOptions(path: '/api/v1/users/me'),
          response: response,
        ));

        // Act & Assert
        expect(
          () => authService.getUserProfile(),
          throwsA(isA<Exception>().having(
            (e) => e.toString(),
            'message',
            contains('Authentication required'),
          )),
        );
      });
    });
  });
}