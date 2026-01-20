# Core Layer

**Zweck:** Cross-cutting concerns und gemeinsame Infrastruktur

---

## Übersicht

Die **Core Layer** enthält Framework-Code, der von allen Features genutzt wird:
- Dependency Injection (DI)
- Networking (API Client)
- Routing
- Error Handling
- Utilities
- Konstanten

**Wichtig:** Keine Feature-spezifische Logik hier!

---

## Struktur

```
core/
├── di/                         # Dependency Injection
│   └── providers.dart          # Riverpod Providers
├── network/                    # HTTP Client
│   └── api_client.dart         # Dio Configuration
├── routing/                    # Navigation
│   └── app_router.dart         # GoRouter Configuration
├── error/                      # Error Handling
│   ├── exceptions.dart         # Custom Exceptions
│   └── failure.dart            # Failure Types
├── utils/                      # Helper Functions
│   ├── date_formatter.dart     # Date Utilities
│   └── validators.dart         # Input Validators
└── constants/                  # App Constants
    ├── api_endpoints.dart      # API URLs
    └── app_constants.dart      # General Constants
```

---

## 📦 `di/providers.dart`

**Zweck:** Zentrale Dependency Injection mit Riverpod

**Was gehört hier rein:**
- API Client Provider
- Repository Providers
- Service Providers
- Global State Providers

**Beispiel:**
```dart
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:dio/dio.dart';
import '../network/api_client.dart';

// API Client Provider
final apiClientProvider = Provider<Dio>((ref) {
  return ApiClient.createDioClient();
});

// Repository Providers (Feature-spezifisch)
final loanRepositoryProvider = Provider<LoanRepository>((ref) {
  return LoanRepository(apiClient: ref.watch(apiClientProvider));
});

final catalogRepositoryProvider = Provider<CatalogRepository>((ref) {
  return CatalogRepository(apiClient: ref.watch(apiClientProvider));
});

// Global State Providers
final currentUserProvider = StateProvider<UserDTO?>((ref) => null);

final isAuthenticatedProvider = Provider<bool>((ref) {
  final user = ref.watch(currentUserProvider);
  return user != null;
});
```

**Best Practices:**
- ✅ Provider für alle externen Abhängigkeiten
- ✅ Nutze `Provider` für stateless Objekte (Repositories, Services)
- ✅ Nutze `StateProvider` / `StateNotifierProvider` für State
- ✅ Lazy Initialization (Provider werden erst bei Bedarf erstellt)

---

## 📦 `network/api_client.dart`

**Zweck:** HTTP Client Konfiguration (Dio)

**Was gehört hier rein:**
- Dio-Instanz mit Base URL
- Interceptors (Logging, Auth, Error Handling)
- Timeout-Konfiguration

**Beispiel:**
```dart
import 'package:dio/dio.dart';

class ApiClient {
  static const String baseUrl = 'http://localhost:8080/api/v1';
  
  static Dio createDioClient() {
    final dio = Dio(
      BaseOptions(
        baseUrl: baseUrl,
        connectTimeout: const Duration(seconds: 10),
        receiveTimeout: const Duration(seconds: 10),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ),
    );

    // Logging Interceptor (nur Debug)
    dio.interceptors.add(LogInterceptor(
      request: true,
      requestHeader: true,
      requestBody: true,
      responseHeader: true,
      responseBody: true,
      error: true,
    ));

    // Auth Interceptor (JWT Token)
    dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        // TODO: Add JWT Token from Secure Storage
        // final token = await secureStorage.read(key: 'jwt_token');
        // if (token != null) {
        //   options.headers['Authorization'] = 'Bearer $token';
        // }
        return handler.next(options);
      },
      onError: (error, handler) async {
        // Handle 401 Unauthorized → Redirect to Login
        if (error.response?.statusCode == 401) {
          // TODO: Navigate to Login
        }
        return handler.next(error);
      },
    ));

    return dio;
  }
}
```

**Best Practices:**
- ✅ Zentrale Konfiguration
- ✅ Interceptors für Auth, Logging, Error Handling
- ✅ Base URL konfigurierbar (Environment Variables)

---

## 📦 `routing/app_router.dart`

**Zweck:** App-weites Routing mit GoRouter

**Was gehört hier rein:**
- Route Definitionen
- Navigation Guards (Auth-Check)
- Deep Links

**Beispiel:**
```dart
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../features/catalog/presentation/pages/catalog_page.dart';
import '../../features/lending/presentation/pages/my_loans_page.dart';
import '../../features/user/presentation/pages/profile_page.dart';

final appRouter = GoRouter(
  initialLocation: '/catalog',
  routes: [
    // Catalog Routes
    GoRoute(
      path: '/catalog',
      name: 'catalog',
      builder: (context, state) => const CatalogPage(),
    ),
    GoRoute(
      path: '/catalog/:id',
      name: 'media-detail',
      builder: (context, state) {
        final id = state.pathParameters['id']!;
        return MediaDetailPage(mediaId: id);
      },
    ),
    
    // Lending Routes
    GoRoute(
      path: '/loans',
      name: 'my-loans',
      builder: (context, state) => const MyLoansPage(),
    ),
    
    // User Routes
    GoRoute(
      path: '/profile',
      name: 'profile',
      builder: (context, state) => const ProfilePage(),
    ),
  ],
  
  // Error Page
  errorBuilder: (context, state) => Scaffold(
    body: Center(
      child: Text('Route not found: ${state.uri}'),
    ),
  ),
);
```

**Best Practices:**
- ✅ Named Routes für typsicheres Navigieren
- ✅ Path Parameters für IDs
- ✅ Redirect Guards für Auth-geschützte Routes

---

## 📦 `error/exceptions.dart`

**Zweck:** Custom Exceptions

**Beispiel:**
```dart
class ApiException implements Exception {
  final String message;
  final int? statusCode;
  
  ApiException(this.message, {this.statusCode});
  
  @override
  String toString() => 'ApiException: $message (Status: $statusCode)';
}

class NetworkException implements Exception {
  final String message;
  
  NetworkException(this.message);
  
  @override
  String toString() => 'NetworkException: $message';
}

class CacheException implements Exception {
  final String message;
  
  CacheException(this.message);
  
  @override
  String toString() => 'CacheException: $message';
}
```

---

## 📦 `error/failure.dart`

**Zweck:** Failure Types (für Repository Return Values)

**Beispiel:**
```dart
abstract class Failure {
  final String message;
  
  const Failure(this.message);
}

class ServerFailure extends Failure {
  const ServerFailure(super.message);
}

class NetworkFailure extends Failure {
  const NetworkFailure(super.message);
}

class CacheFailure extends Failure {
  const CacheFailure(super.message);
}

class ValidationFailure extends Failure {
  const ValidationFailure(super.message);
}
```

**Nutzung:**
```dart
// Repository
Future<Either<Failure, List<MediaDTO>>> getMedia() async {
  try {
    final response = await apiClient.get('/media');
    final media = (response.data as List)
        .map((json) => MediaDTO.fromJson(json))
        .toList();
    return Right(media);
  } on DioException catch (e) {
    if (e.type == DioExceptionType.connectionTimeout) {
      return Left(NetworkFailure('Connection timeout'));
    }
    return Left(ServerFailure('Server error: ${e.message}'));
  }
}
```

---

## 📦 `utils/date_formatter.dart`

**Zweck:** Date Utilities

**Beispiel:**
```dart
class DateFormatter {
  static String formatDueDate(DateTime date) {
    final now = DateTime.now();
    final difference = date.difference(now).inDays;
    
    if (difference < 0) {
      return 'Überfällig seit ${-difference} Tagen';
    } else if (difference == 0) {
      return 'Heute fällig';
    } else if (difference == 1) {
      return 'Morgen fällig';
    } else if (difference <= 3) {
      return 'Fällig in $difference Tagen';
    } else {
      return 'Fällig am ${_formatDate(date)}';
    }
  }
  
  static String _formatDate(DateTime date) {
    return '${date.day}.${date.month}.${date.year}';
  }
}
```

---

## 📦 `utils/validators.dart`

**Zweck:** Input Validators

**Beispiel:**
```dart
class Validators {
  static String? validateEmail(String? value) {
    if (value == null || value.isEmpty) {
      return 'E-Mail ist erforderlich';
    }
    
    final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
    if (!emailRegex.hasMatch(value)) {
      return 'Ungültige E-Mail-Adresse';
    }
    
    return null;
  }
  
  static String? validateBarcode(String? value) {
    if (value == null || value.isEmpty) {
      return 'Barcode ist erforderlich';
    }
    
    if (value.length != 6) {
      return 'Barcode muss 6 Zeichen lang sein';
    }
    
    return null;
  }
}
```

---

## 📦 `constants/api_endpoints.dart`

**Zweck:** API Endpoint Konstanten

**Beispiel:**
```dart
class ApiEndpoints {
  // Base
  static const String baseUrl = 'http://localhost:8080/api/v1';
  
  // Catalog
  static const String media = '/media';
  static String mediaById(String id) => '/media/$id';
  static const String searchMedia = '/media/search';
  
  // Lending
  static const String loans = '/loans';
  static String loanById(String id) => '/loans/$id';
  static String returnLoan(String id) => '/loans/$id/return';
  static String renewLoan(String id) => '/loans/$id/renew';
  
  // Reservations
  static const String reservations = '/reservations';
  
  // User
  static const String profile = '/users/me';
}
```

---

## Entwicklungsrichtlinien

1. **Keine Feature-Logik:** Core ist nur Infrastruktur
2. **Wiederverwendbar:** Alles hier muss von allen Features nutzbar sein
3. **Typsicher:** Provider mit expliziten Typen
4. **Testbar:** Dependency Injection ermöglicht Mocking

---

## Referenzen

- 📖 [PROJECT_STRUCTURE.md](../../PROJECT_STRUCTURE.md)
- 📚 [Riverpod Documentation](https://riverpod.dev/)
- 🌐 [Dio Documentation](https://pub.dev/packages/dio)
- 🧭 [GoRouter Documentation](https://pub.dev/packages/go_router)
