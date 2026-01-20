/**
 * Health status response from backend
 */
export interface HealthResponse {
  status: 'UP' | 'DOWN' | 'UNKNOWN';
  timestamp?: string;
}

/**
 * Application info response from backend
 */
export interface AppInfoResponse {
  name: string;
  version: string;
  description?: string;
  environment?: string;
}

/**
 * API Error response
 */
export interface ApiError {
  message: string;
  status?: number;
  timestamp?: string;
}
