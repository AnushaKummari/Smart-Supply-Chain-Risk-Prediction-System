import apiClient from './apiClient.js';

export async function getShipmentRecommendations(id) {
  const res = await apiClient.get(`/recommendations/shipment/${id}`);
  return res.data;
}

