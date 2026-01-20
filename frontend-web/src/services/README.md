# Services Layer

**Zweck:** API client and service configuration

---

## Structure

```
services/
└── api.service.ts      # Axios API client
```

---

## API Client

**File:** `api.service.ts`

```typescript
import axios, { AxiosInstance } from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

export const apiClient: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

// Request Interceptor (Auth Token)
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor (Error Handling)
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Redirect to login
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

---

## Environment Variables

**File:** `.env`

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

---

## Usage in Repositories

```typescript
import { apiClient } from '@/services/api.service';

export class LoanRepository {
  async getMyLoans(): Promise<LoanDTO[]> {
    const response = await apiClient.get<LoanDTO[]>('/loans');
    return response.data;
  }
}
```

---

## Guidelines

- ✅ Single API client instance
- ✅ Interceptors for auth + error handling
- ✅ Base URL from environment variables
- ✅ TypeScript types for responses

---

## References

- [Axios Documentation](https://axios-http.com/)
- [Vite Environment Variables](https://vitejs.dev/guide/env-and-mode.html)
