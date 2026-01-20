import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';
import 'package:school_library_mobile/core/network/models/registration_request.dart';
import 'package:school_library_mobile/core/network/user_service.dart';
import 'package:school_library_mobile/core/services/token_storage.dart';


import 'user_service_test.mocks.dart';

@GenerateMocks([Dio, TokenStorage])
void main() {
  late MockDio mockDio;
  late MockTokenStorage mockTokenStorage;
  late UserService userService;

  setUp(() {
    mockDio = MockDio();
    mockTokenStorage = MockTokenStorage();
    userService = UserService(mockDio, mockTokenStorage);
  });

  group('UserService', () {
    test('register should return RegistrationResponse on success', () async {
      final request = RegistrationRequest(
        email: 'test@example.com',
        password: 'password123',
        firstName: 'John',
        lastName: 'Doe',
      );

      final responsePayload = {
        'userId': '123',
        'email': 'test@example.com',
        'message': 'Registration successful',
        'verificationRequired': true,
      };

      when(mockDio.post(
        any,
        data: anyNamed('data'),
      )).thenAnswer((_) async => Response(
            data: responsePayload,
            statusCode: 201,
            requestOptions: RequestOptions(path: ''),
          ));

      final response = await userService.register(request);

      expect(response.userId, '123');
      expect(response.email, 'test@example.com');
      expect(response.message, 'Registration successful');
      expect(response.verificationRequired, true);
    });

    test('checkEmailAvailability should return EmailAvailability on success', () async {
      final email = 'test@example.com';

      final responsePayload = {
        'email': email,
        'available': true,
      };

      when(mockDio.get(
        any,
        queryParameters: anyNamed('queryParameters'),
      )).thenAnswer((_) async => Response(
            data: responsePayload,
            statusCode: 200,
            requestOptions: RequestOptions(path: ''),
          ));

      final response = await userService.checkEmailAvailability(email);

      expect(response.email, email);
      expect(response.available, true);
    });

    test('register should throw ApiException on failure', () async {
      final request = RegistrationRequest(
        email: 'test@example.com',
        password: 'password123',
        firstName: 'John',
        lastName: 'Doe',
      );

      when(mockDio.post(
        any,
        data: anyNamed('data'),
      )).thenThrow(DioException(
        requestOptions: RequestOptions(path: ''),
        response: Response(
          statusCode: 400,
          data: {'message': 'Bad Request'},
          requestOptions: RequestOptions(path: ''),
        ),
      ));

      expect(
        () async => await userService.register(request),
        throwsA(isA<ApiException>()),
      );
    });

    test('checkEmailAvailability should throw ApiException on failure', () async {
      final email = 'test@example.com';

      when(mockDio.get(
        any,
        queryParameters: anyNamed('queryParameters'),
      )).thenThrow(DioException(
        requestOptions: RequestOptions(path: ''),
        response: Response(
          statusCode: 404,
          data: {'message': 'Not Found'},
          requestOptions: RequestOptions(path: ''),
        ),
      ));

      expect(
        () async => await userService.checkEmailAvailability(email),
        throwsA(isA<ApiException>()),
      );
    });
  });
}