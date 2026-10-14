/**
 * Auth persistence helpers backed by localStorage.
 */
export const TOKEN_KEY = 'bp_fighter_token';
export const USER_KEY = 'bp_fighter_user';

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function getStoredUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null');
  } catch {
    return null;
  }
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function setStoredUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

export function isAuthenticated() {
  return Boolean(getToken());
}
