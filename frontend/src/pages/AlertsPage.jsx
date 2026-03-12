import React, { useEffect, useState } from 'react';
import { getAlerts } from '../services/alertService.js';

const formatDate = (d) => (d ? new Date(d).toLocaleString() : '–');

const severityBadgeClass = (sev) => {
  switch ((sev || '').toUpperCase()) {
    case 'CRITICAL':
      return 'badge-critical';
    case 'HIGH':
      return 'badge-high';
    case 'MEDIUM':
      return 'badge-medium';
    case 'LOW':
      return 'badge-low';
    default:
      return 'badge-neutral';
  }
};

const AlertsPage = () => {
  const [data, setData] = useState({ content: [], totalPages: 0, number: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = (page = 0) => {
    setLoading(true);
    setError(null);
    getAlerts(page, 20)
      .then((res) => setData({
        content: res.content || [],
        totalPages: res.totalPages ?? 0,
        number: res.number ?? 0,
      }))
      .catch(() => setError('Failed to load alerts.'))
      .finally(() => setLoading(false));
  };

  useEffect(() => load(), []);

  if (loading && data.content.length === 0) {
    return (
      <div className="container">
        <div className="page-header">
          <div>
            <h1 className="page-title">Alerts</h1>
            <p className="page-subtitle">Real-time and historical system alerts.</p>
          </div>
        </div>
        <div className="card">
          <div className="card-body">Loading alerts...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <div className="page-header">
        <div>
          <h1 className="page-title">Alerts</h1>
          <p className="page-subtitle">Real-time and historical system alerts.</p>
        </div>
      </div>

      {error && (
        <div className="card" style={{ marginBottom: '1rem' }}>
          <div className="card-body">
            <p style={{ color: '#b91c1c', margin: 0 }}>{error}</p>
          </div>
        </div>
      )}

      <div className="card">
        <div className="card-header">
          <div className="card-title">Alert log</div>
        </div>
        <div className="card-body" style={{ padding: 0 }}>
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Type</th>
                <th>Severity</th>
                <th>Message</th>
                <th>Shipment</th>
                <th>Created</th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((a) => (
                <tr key={a.id}>
                  <td>{a.id}</td>
                  <td>{a.alertType}</td>
                  <td><span className={`badge ${severityBadgeClass(a.severity)}`}>{a.severity}</span></td>
                  <td>{a.alertMessage}</td>
                  <td>{a.shipmentId ?? '–'}</td>
                  <td>{formatDate(a.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {data.content.length === 0 && !loading && (
            <div style={{ padding: '1rem', color: '#64748b' }}>
              No alerts yet. Alerts will appear when high-risk shipments are detected.
            </div>
          )}
        </div>
      </div>

      {data.totalPages > 1 && (
        <div style={{ marginTop: '1rem', display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
          <span style={{ color: '#64748b' }}>Page {data.number + 1} of {data.totalPages}</span>
          <button className="btn" type="button" onClick={() => load(data.number - 1)} disabled={data.number <= 0}>Previous</button>
          <button className="btn" type="button" onClick={() => load(data.number + 1)} disabled={data.number >= data.totalPages - 1}>Next</button>
        </div>
      )}
    </div>
  );
};

export default AlertsPage;


