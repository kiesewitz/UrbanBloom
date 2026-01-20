/// Environment configuration for different build environments
/// Supports: mock, development, production
enum Environment {
  /// Local mock server for testing without backend
  mock,

  /// Development backend server
  development,

  /// Test backend server
  test,

  /// Production backend server
  production,
}

/// Application configuration based on environment
class Config {
  /// Current environment, defaults to mock for local development
  static final Environment current = _parseEnvironment();

  static Environment _parseEnvironment() {
    const envString = String.fromEnvironment('ENV', defaultValue: 'mock');
    try {
      return Environment.values.byName(envString);
    } catch (_) {
      return Environment.mock;
    }
  }

  /// API base URL based on current environment
  static String get apiBaseUrl {
    switch (current) {
      case Environment.mock:
        return 'http://localhost:4010';
      case Environment.development:
        return 'http://localhost:8080';
      case Environment.test:
        return 'http://localhost:9080';
      case Environment.production:
        return 'https://api.schulbibliothek.de';
    }
  }

  /// Whether the app is running in mock mode
  static bool get isMock => current == Environment.mock;

  /// Whether the app is running in development mode
  static bool get isDevelopment => current == Environment.development;

  /// Whether the app is running in production mode
  static bool get isProduction => current == Environment.production;

  /// Environment name as string
  static String get environmentName => current.name;
}
