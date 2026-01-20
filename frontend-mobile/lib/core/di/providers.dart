import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../api/generated/user_api.dart';
import '../network/api_client.dart';
import '../network/user_service.dart';
import '../services/token_storage.dart';
import '../../features/health/data/repositories/health_repository.dart';

/// Dependency Injection - Service Locator Pattern with Riverpod

// API Client Provider
final apiClientProvider = Provider<ApiClient>((ref) {
  final tokenStorage = ref.watch(tokenStorageProvider);
  return ApiClient(tokenStorage);
});

// Token Storage Provider
final tokenStorageProvider = Provider<TokenStorage>((ref) {
  return TokenStorage();
});

// User API Provider
final userApiProvider = Provider<UserApi>((ref) {
  final apiClient = ref.watch(apiClientProvider);
  return UserApi(apiClient.dio);
});

// User Service Provider
final userServiceProvider = Provider<UserService>((ref) {
  final apiClient = ref.watch(apiClientProvider);
  final tokenStorage = ref.watch(tokenStorageProvider);
  return UserService(apiClient.dio, tokenStorage);
});

// Health Repository Provider
final healthRepositoryProvider = Provider<HealthRepository>((ref) {
  final apiClient = ref.watch(apiClientProvider);
  return HealthRepository(apiClient.dio);
});
