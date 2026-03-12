import apiClient from './apiClient.js';

export async function getShipments(page = 0, size = 20) {
  const res = await apiClient.get('/shipments', { params: { page, size } });
  return res.data;
}

export async function getShipmentById(id) {
  const res = await apiClient.get(`/shipments/${id}`);
  return res.data;
}

export async function createShipment(data) {
  const res = await apiClient.post('/shipments', data);
  return res.data;
}

export async function updateShipmentStatus(id, shipmentStatus) {
  const res = await apiClient.patch(`/shipments/${id}/status`, { shipmentStatus });
  return res.data;
}

export async function predictDelay(id) {
  const res = await apiClient.post(`/shipments/${id}/predict-delay`);
  return res.data;
}

export async function optimizeRouteForShipment(id) {
  const res = await apiClient.get(`/routes/optimize/shipment/${id}`);
  return res.data;
}
