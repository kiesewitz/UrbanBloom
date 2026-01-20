/**
 * Supported build environments
 */
export enum Environment {
  /** Local mock server for testing without backend */
  Mock = 'mock',

  /** Development backend server */
  Development = 'development',

  /** Test backend server */
  Test = 'test',

  /** Production backend server */
  Production = 'production',
}

/**
 * Application configuration based on environment
 */
export const Config = {
  /**
   * Current environment from Vite meta.env
   * Defaults to mock for local development if not specified
   */
  get current(): Environment {
    const envString = import.meta.env.VITE_APP_ENV || 'mock';
    if (Object.values(Environment).includes(envString as Environment)) {
      return envString as Environment;
    }
    return Environment.Mock;
  },

  /**
   * API base URL based on environment
   * Returns specific ports for local environments
   */
  get apiBaseUrl(): string {
    // Priority: Explicit env variable override
    if (import.meta.env.VITE_API_BASE_URL) {
      return import.meta.env.VITE_API_BASE_URL;
    }

    // Default port mapping based on environment
    switch (this.current) {
      case Environment.Mock:
        return 'http://localhost:4010';
      case Environment.Development:
        return 'http://localhost:8080';
      case Environment.Test:
        return 'http://localhost:9080';
      case Environment.Production:
        return 'https://api.schulbibliothek.de';
      default:
        return 'http://localhost:8080';
    }
  },

  /** Whether the app is running in mock mode */
  get isMock(): boolean {
    return this.current === Environment.Mock;
  },

  /** Whether the app is running in development mode */
  get isDevelopment(): boolean {
    return this.current === Environment.Development;
  },

  /** Whether the app is running in test mode */
  get isTest(): boolean {
    return this.current === Environment.Test;
  },

  /** Whether the app is running in production mode */
  get isProduction(): boolean {
    return this.current === Environment.Production;
  },

  /** Environment name as string */
  get environmentName(): string {
    return this.current;
  },
};
