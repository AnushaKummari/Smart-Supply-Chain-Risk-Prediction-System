import apiClient from './apiClient.js';

export async function getAlerts(page = 0, size = 20) {
  const res = await apiClient.get('/alerts', { params: { page, size } });
  return res.data;
}

