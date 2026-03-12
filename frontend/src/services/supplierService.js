import apiClient from './apiClient.js';

export async function getSuppliers() {
  const res = await apiClient.get('/suppliers');
  return res.data;
}

export async function createSupplier(data) {
  const res = await apiClient.post('/suppliers', data);
  return res.data;
}
