import apiClient from './apiClient.js';

export async function getOverview() {
  const res = await apiClient.get('/analytics/overview');
  return res.data;
}

export async function getRiskDistribution() {
  const res = await apiClient.get('/analytics/risk-distribution');
  return res.data;
}

export async function getSupplierPerformance(limit = 10) {
  const res = await apiClient.get('/analytics/supplier-performance', { params: { limit } });
  return res.data;
}

export async function getDelayTrends(days = 30) {
  const res = await apiClient.get('/analytics/delay-trends', { params: { days } });
  return res.data;
}

