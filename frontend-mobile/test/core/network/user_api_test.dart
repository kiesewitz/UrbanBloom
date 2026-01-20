import 'package:flutter_test/flutter_test.dart';
import 'package:dio/dio.dart';
import 'package:school_library_mobile/core/network/user_service.dart';
import 'package:school_library_mobile/core/network/api_client.dart';
import 'package:school_library_mobile/core/services/token_storage.dart';
import 'package:school_library_mobile/core/network/models/registration_request.dart';
import 'package:mockito/annotations.dart';
// models used via UserService responses

@GenerateMocks([TokenStorage])
import 'user_api_test.mocks.dart';

ApiClient _clientWithResponder(
  TokenStorage tokenStorage,
  Future<Response> Function(RequestOptions options) responder,
) {
  final client = ApiClient(tokenStorage);
  client.dio.interceptors.clear();
  client.dio.interceptors.add(
    InterceptorsWrapper(
      onRequest: (options, handler) async {
        try {
          final resp = await responder(options);
          handler.resolve(resp);
        } on DioException catch (e) {
          handler.reject(e);
        } catch (e) {
          handler.reject(DioException(requestOptions: options, error: e));
        }
      },
    ),
  );
  return client;
}

void main() {
  late MockTokenStorage mockTokenStorage;

  setUp(() {
    mockTokenStorage = MockTokenStorage();
  });

  // Gruppe: Registrierungstestfälle
  group('UserApi - register', () {
    // Test: Bei HTTP 201 wird ein erfolgreicher RegistrationResponse zurückgegeben
    test('returns RegistrationResponse on 201', () async {
      final client = _clientWithResponder(mockTokenStorage, (options) async {
        return Response(
          requestOptions: options,
          data: {
            'userId': 'u1',
            'email': 'a@b.com',
            'message': null,
            'verificationRequired': false,
          },
          statusCode: 201,
        );
      });

      final api = UserService(client.dio, mockTokenStorage);

      final resp = await api.register(
        RegistrationRequest(
          email: 'a@b.com',
          password: 'p',
          firstName: 'F',
          lastName: 'L',
        ),
      );

      expect(resp.userId, 'u1');
      expect(resp.email, 'a@b.com');
    });

    // Test: Bei HTTP 400 (Validierungsfehler) wird der Fehler-Body als RegistrationResponse zurückgegeben
    test('throws ApiException on 400 validation error', () async {
      final client = _clientWithResponder(mockTokenStorage, (options) async {
        throw DioException(
          requestOptions: options,
          response: Response(
            requestOptions: options,
            data: {'message': 'E-Mail-Adresse ist bereits registriert'},
            statusCode: 400,
          ),
          message: 'Bad Request',
        );
      });

      final api = UserService(client.dio, mockTokenStorage);

      expect(
        api.register(
          RegistrationRequest(
            email: 'a@b.com',
            password: 'p',
            firstName: 'F',
            lastName: 'L',
          ),
        ),
        throwsA(isA<ApiException>()),
      );
    });

    // Test: Bei einem Netzwerkfehler ohne Server-Antwort wird ein ApiException geworfen
    test(
      'throws ApiException on network DioException without response',
      () async {
        final client = _clientWithResponder(mockTokenStorage, (options) async {
          throw DioException(requestOptions: options, message: 'network');
        });

        final api = UserService(client.dio, mockTokenStorage);

        expect(
          api.register(
            RegistrationRequest(
              email: 'a@b.com',
              password: 'p',
              firstName: 'F',
              lastName: 'L',
            ),
          ),
          throwsA(isA<ApiException>()),
        );
      },
    );

    // Test: Wenn ein DioException mit einer Response (z.B. 500) geworfen wird,
    // wird ein ApiException mit dem Statuscode weitergereicht
    test(
      'throws ApiException with status from DioException response',
      () async {
        final client = _clientWithResponder(mockTokenStorage, (options) async {
          final resp = Response(
            requestOptions: options,
            data: {'message': 'server'},
            statusCode: 500,
          );
          throw DioException(
            requestOptions: options,
            response: resp,
            message: 'server error',
          );
        });

        final api = UserService(client.dio, mockTokenStorage);

        final future = api.register(
          RegistrationRequest(
            email: 'a@b.com',
            password: 'p',
            firstName: 'F',
            lastName: 'L',
          ),
        );

        await expectLater(future, throwsA(isA<ApiException>()));
        try {
          await future;
        } catch (e) {
          final ae = e as ApiException;
          expect(ae.statusCode, 500);
        }
      },
    );
  });

  // Gruppe: Email-Verfügbarkeitsprüfungen
  group('UserApi - checkEmailAvailability', () {
    // Test: Bei Erfolg (200) wird EmailAvailability korrekt geparst
    test('returns EmailAvailability on success', () async {
      final client = _clientWithResponder(mockTokenStorage, (options) async {
        return Response(
          requestOptions: options,
          data: {'email': 'a@b.com', 'available': true},
          statusCode: 200,
        );
      });

      final api = UserService(client.dio, mockTokenStorage);
      final resp = await api.checkEmailAvailability('a@b.com');
      expect(resp.available, isTrue);
      expect(resp.email, 'a@b.com');
    });

    // Test: Bei Netzwerkfehler ohne Server-Antwort wirft die Methode einen ApiException
    test('throws ApiException on DioException without response', () async {
      final client = _clientWithResponder(mockTokenStorage, (options) async {
        throw DioException(requestOptions: options, message: 'network');
      });

      final api = UserService(client.dio, mockTokenStorage);

      expect(
        api.checkEmailAvailability('a@b.com'),
        throwsA(isA<ApiException>()),
      );
    });
  });
}
