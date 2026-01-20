import axios, { AxiosInstance, AxiosError } from 'axios';
import type { HealthResponse, AppInfoResponse, ApiError } from '@/types/api.types';
import { Config } from '@/core/config/environment';

/**
 * Base API client for communicating with the Spring Boot backend
 */
class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: Config.apiBaseUrl as string,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Response interceptor for error handling
    this.client.interceptors.response.use(
      (response) => response,
      (error: AxiosError) => {
        const apiError: ApiError = {
          message: error.message,
          status: error.response?.status,
          timestamp: new Date().toISOString(),
        };
        return Promise.reject(apiError);
      }
    );
  }

  /**
   * Check backend health status
   * @returns Health status response
   */
  async checkHealth(): Promise<HealthResponse> {
    try {
      const response = await this.client.get<HealthResponse>('/health');
      return response.data;
    } catch (error) {
      // Return DOWN status if health check fails
      return {
        status: 'DOWN',
        timestamp: new Date().toISOString(),
      };
    }
  }

  /**
   * Get application information
   * @returns Application info response
   */
  async getAppInfo(): Promise<AppInfoResponse> {
    const response = await this.client.get<AppInfoResponse>('/api/v1/app/info');
    return response.data;
  }
}

// Export singleton instance
export const apiClient = new ApiClient();
