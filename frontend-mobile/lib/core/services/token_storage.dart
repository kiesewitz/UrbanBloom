import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../../models/auth_tokens.dart';

/// Secure storage keys
class StorageKeys {
  static const String accessToken = 'access_token';
  static const String refreshToken = 'refresh_token';
  static const String tokenType = 'token_type';
  static const String expiresIn = 'expires_in';
}

/// Service for securely storing and retrieving authentication tokens
class TokenStorage {
  final FlutterSecureStorage _storage;

  TokenStorage() : _storage = const FlutterSecureStorage();

  /// Store authentication tokens securely
  Future<void> storeTokens(AuthTokens tokens) async {
    await _storage.write(key: StorageKeys.accessToken, value: tokens.accessToken);
    await _storage.write(key: StorageKeys.refreshToken, value: tokens.refreshToken);
    await _storage.write(key: StorageKeys.tokenType, value: tokens.tokenType);
    await _storage.write(key: StorageKeys.expiresIn, value: tokens.expiresIn.toString());
  }

  /// Retrieve stored authentication tokens
  Future<AuthTokens?> getTokens() async {
    final accessToken = await _storage.read(key: StorageKeys.accessToken);
    final refreshToken = await _storage.read(key: StorageKeys.refreshToken);
    final tokenType = await _storage.read(key: StorageKeys.tokenType);
    final expiresInStr = await _storage.read(key: StorageKeys.expiresIn);

    if (accessToken == null || refreshToken == null || tokenType == null || expiresInStr == null) {
      return null;
    }

    final expiresIn = int.tryParse(expiresInStr);
    if (expiresIn == null) {
      return null;
    }

    return AuthTokens(
      accessToken: accessToken,
      refreshToken: refreshToken,
      tokenType: tokenType,
      expiresIn: expiresIn,
    );
  }

  /// Clear all stored tokens
  Future<void> clearTokens() async {
    await _storage.delete(key: StorageKeys.accessToken);
    await _storage.delete(key: StorageKeys.refreshToken);
    await _storage.delete(key: StorageKeys.tokenType);
    await _storage.delete(key: StorageKeys.expiresIn);
  }

  /// Check if tokens are stored
  Future<bool> hasTokens() async {
    final tokens = await getTokens();
    return tokens != null;
  }
}