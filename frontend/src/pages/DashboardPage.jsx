import React, { useEffect, useState } from 'react';
import { getHealth } from '../services/healthService.js';
import { getOverview } from '../services/analyticsService.js';
import { getAlerts } from '../services/alertService.js';
import NotificationCenter from '../components/notifications/NotificationCenter.jsx';

const DashboardPage = () => {
  const [health, setHealth] = useState(null);
  const [overview, setOverview] = useState(null);
  const [openAlerts, setOpenAlerts] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
    Promise.all([
      getHealth(),
      getOverview(),
      getAlerts(0, 1)
    ])
      .then(([h, o, a]) => {
        setHealth(h);
        setOverview(o);
        setOpenAlerts(a?.totalElements ?? (a?.content?.length ?? 0));
      })
      .catch(() => setError('Failed to load dashboard data.'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="container">
      <div className="page-header">
        <div>
          <h1 className="page-title">Dashboard</h1>
          <p className="page-subtitle">Operational summary and live system health.</p>
        </div>
      </div>

      <NotificationCenter />

      {error && (
        <div className="card">
          <div className="card-body">
            <p style={{ color: '#b91c1c', margin: 0 }}>{error}</p>
          </div>
        </div>
      )}

      {loading && <p>Loading dashboard...</p>}

      {!loading && overview && (
        <div className="cards-grid" style={{ marginTop: '1rem' }}>
          <div className="card">
            <div className="card-body metric">
              <div className="metric-label">Total shipments</div>
              <div className="metric-value">{overview.totalShipments}</div>
            </div>
          </div>
          <div className="card">
            <div className="card-body metric">
              <div className="metric-label">Delivered</div>
              <div className="metric-value">{overview.deliveredShipments}</div>
            </div>
          </div>
          <div className="card">
            <div className="card-body metric">
              <div className="metric-label">In transit</div>
              <div className="metric-value">{overview.inTransitShipments}</div>
            </div>
          </div>
          <div className="card">
            <div className="card-body metric">
              <div className="metric-label">High risk shipments</div>
              <div className="metric-value">{overview.highRiskShipments}</div>
            </div>
          </div>
          <div className="card">
            <div className="card-body metric">
              <div className="metric-label">Open alerts</div>
              <div className="metric-value">{openAlerts ?? '–'}</div>
            </div>
          </div>
          <div className="card">
            <div className="card-body metric">
              <div className="metric-label">Avg predicted delay (hrs)</div>
              <div className="metric-value">{overview.averagePredictedDelayHours}</div>
            </div>
          </div>
        </div>
      )}

      <div style={{ marginTop: '1rem' }} className="card">
        <div className="card-header">
          <div className="card-title">System health</div>
        </div>
        <div className="card-body">
          {health ? (
            <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
              <span className={`badge ${health.status === 'UP' ? 'badge-low' : 'badge-high'}`}>{health.status}</span>
              <span style={{ color: '#64748b' }}>{health.backendVersion}</span>
            </div>
          ) : (
            <p style={{ margin: 0, color: '#64748b' }}>Health not available yet.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;

