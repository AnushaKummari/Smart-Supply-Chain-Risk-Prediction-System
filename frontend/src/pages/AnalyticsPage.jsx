import React, { useEffect, useMemo, useRef, useState } from 'react';
import Chart from 'chart.js/auto';
import {
  getDelayTrends,
  getOverview,
  getRiskDistribution,
  getSupplierPerformance
} from '../services/analyticsService.js';

const AnalyticsPage = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [overview, setOverview] = useState(null);
  const [risk, setRisk] = useState(null);
  const [supplierPerf, setSupplierPerf] = useState(null);
  const [delayTrends, setDelayTrends] = useState(null);

  const statusCanvasRef = useRef(null);
  const riskCanvasRef = useRef(null);
  const supplierCanvasRef = useRef(null);
  const delayCanvasRef = useRef(null);

  const statusChartRef = useRef(null);
  const riskChartRef = useRef(null);
  const supplierChartRef = useRef(null);
  const delayChartRef = useRef(null);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      const [o, r, s, d] = await Promise.all([
        getOverview(),
        getRiskDistribution(),
        getSupplierPerformance(10),
        getDelayTrends(30)
      ]);
      setOverview(o);
      setRisk(r);
      setSupplierPerf(s);
      setDelayTrends(d);
    } catch (e) {
      setError('Failed to load analytics.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const statusData = useMemo(() => {
    const dist = overview?.statusDistribution || [];
    return {
      labels: dist.map((x) => x.status),
      values: dist.map((x) => x.count)
    };
  }, [overview]);

  const riskData = useMemo(() => {
    const dist = risk?.distribution || [];
    return {
      labels: dist.map((x) => x.riskLevel),
      values: dist.map((x) => x.count)
    };
  }, [risk]);

  const supplierData = useMemo(() => {
    const list = supplierPerf?.suppliers || [];
    return {
      labels: list.map((x) => x.supplierName),
      reliability: list.map((x) => x.reliabilityScore ?? 0),
      avgRisk: list.map((x) => x.averageRiskScore ?? 0)
    };
  }, [supplierPerf]);

  const delayData = useMemo(() => {
    const pts = delayTrends?.points || [];
    return {
      labels: pts.map((p) => p.date),
      values: pts.map((p) => p.averagePredictedDelayHours ?? 0)
    };
  }, [delayTrends]);

  // Status distribution chart
  useEffect(() => {
    const canvas = statusCanvasRef.current;
    if (!canvas) return;
    if (statusChartRef.current) {
      statusChartRef.current.destroy();
      statusChartRef.current = null;
    }
    if (!statusData.labels.length) return;

    statusChartRef.current = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: statusData.labels,
        datasets: [
          {
            label: 'Shipments',
            data: statusData.values,
            backgroundColor: ['#60a5fa', '#34d399', '#fbbf24', '#f87171', '#a78bfa', '#9ca3af']
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'bottom' }
        }
      }
    });
  }, [statusData]);

  // Risk distribution chart
  useEffect(() => {
    const canvas = riskCanvasRef.current;
    if (!canvas) return;
    if (riskChartRef.current) {
      riskChartRef.current.destroy();
      riskChartRef.current = null;
    }
    if (!riskData.labels.length) return;

    riskChartRef.current = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: riskData.labels,
        datasets: [
          {
            label: 'Shipments',
            data: riskData.values,
            backgroundColor: ['#34d399', '#fbbf24', '#f87171', '#9ca3af']
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'bottom' }
        }
      }
    });
  }, [riskData]);

  // Supplier performance chart
  useEffect(() => {
    const canvas = supplierCanvasRef.current;
    if (!canvas) return;
    if (supplierChartRef.current) {
      supplierChartRef.current.destroy();
      supplierChartRef.current = null;
    }
    if (!supplierData.labels.length) return;

    supplierChartRef.current = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: supplierData.labels,
        datasets: [
          {
            label: 'Reliability score',
            data: supplierData.reliability,
            backgroundColor: '#60a5fa'
          },
          {
            label: 'Avg risk score',
            data: supplierData.avgRisk,
            backgroundColor: '#f87171'
          }
        ]
      },
      options: {
        responsive: true,
        scales: {
          y: { beginAtZero: true }
        }
      }
    });
  }, [supplierData]);

  // Delay trends chart
  useEffect(() => {
    const canvas = delayCanvasRef.current;
    if (!canvas) return;
    if (delayChartRef.current) {
      delayChartRef.current.destroy();
      delayChartRef.current = null;
    }
    if (!delayData.labels.length) return;

    delayChartRef.current = new Chart(canvas, {
      type: 'line',
      data: {
        labels: delayData.labels,
        datasets: [
          {
            label: 'Avg predicted delay (hours)',
            data: delayData.values,
            borderColor: '#34d399',
            backgroundColor: 'rgba(52, 211, 153, 0.2)',
            tension: 0.25,
            fill: true
          }
        ]
      },
      options: {
        responsive: true,
        scales: {
          y: { beginAtZero: true }
        }
      }
    });
  }, [delayData]);

  if (loading) return <p>Loading analytics...</p>;
  if (error) return (
    <div>
      <h1>Analytics</h1>
      <p style={{ color: 'red' }}>{error}</p>
      <button className="btn" type="button" onClick={load}>Retry</button>
    </div>
  );

  return (
    <div className="container">
      <div className="page-header">
        <div>
          <h1 className="page-title">Analytics</h1>
          <p className="page-subtitle">Trends and performance indicators for shipments and suppliers.</p>
        </div>
      </div>

      {overview && (
        <div className="cards-grid" style={{ marginTop: '1rem' }}>
          <div className="card"><div className="card-body metric"><div className="metric-label">Total shipments</div><div className="metric-value">{overview.totalShipments}</div></div></div>
          <div className="card"><div className="card-body metric"><div className="metric-label">Delivered</div><div className="metric-value">{overview.deliveredShipments}</div></div></div>
          <div className="card"><div className="card-body metric"><div className="metric-label">In transit</div><div className="metric-value">{overview.inTransitShipments}</div></div></div>
          <div className="card"><div className="card-body metric"><div className="metric-label">High risk shipments</div><div className="metric-value">{overview.highRiskShipments}</div></div></div>
          <div className="card"><div className="card-body metric"><div className="metric-label">Avg predicted delay (hrs)</div><div className="metric-value">{overview.averagePredictedDelayHours}</div></div></div>
        </div>
      )}

      <div className="grid-2" style={{ marginTop: '1.5rem' }}>
        <div className="card">
          <div className="card-header"><div className="card-title">Shipment status distribution</div></div>
          <div className="card-body"><canvas ref={statusCanvasRef} /></div>
        </div>
        <div className="card">
          <div className="card-header"><div className="card-title">Risk distribution</div></div>
          <div className="card-body"><canvas ref={riskCanvasRef} /></div>
        </div>
        <div className="card">
          <div className="card-header"><div className="card-title">Supplier performance</div></div>
          <div className="card-body"><canvas ref={supplierCanvasRef} /></div>
        </div>
        <div className="card">
          <div className="card-header"><div className="card-title">Delay trends (30 days)</div></div>
          <div className="card-body"><canvas ref={delayCanvasRef} /></div>
        </div>
      </div>
    </div>
  );
};

export default AnalyticsPage;

