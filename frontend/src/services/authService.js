import apiClient from './apiClient.js';

export async function login(email, password) {
  const res = await apiClient.post('/auth/login', { email, password });
  const { accessToken, tokenType } = res.data;
  const token = `${tokenType} ${accessToken}`.trimStart().startsWith('Bearer ')
    ? accessToken
    : accessToken;
  // Store only raw JWT; header is added by interceptor
  localStorage.setItem('ssrp_jwt', token);
  return res.data;
}

export function logout() {
  localStorage.removeItem('ssrp_jwt');
}

export function isAuthenticated() {
  return !!localStorage.getItem('ssrp_jwt');
}

