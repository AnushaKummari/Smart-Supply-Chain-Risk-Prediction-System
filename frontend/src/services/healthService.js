import apiClient from './apiClient.js';

export async function getHealth() {
  const res = await apiClient.get('/health');
  return res.data;
}

