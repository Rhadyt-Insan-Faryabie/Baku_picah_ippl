import axios from 'axios';
import { getToken } from './authStorage';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api',
  headers: { 'Content-Type': 'application/json' },
});

// Attach the fighter bearer token to every request, when present.
api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;

/** Extract a readable message from a FastAPI error response. */
export function apiErrorMessage(error, fallback = 'Terjadi kesalahan. Coba lagi.') {
  const detail = error?.response?.data?.detail;
  if (Array.isArray(detail)) {
    return detail[0]?.msg || fallback;
  }
  return detail || fallback;
}
