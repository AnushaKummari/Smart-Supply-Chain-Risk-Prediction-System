import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getShipments } from '../services/shipmentService.js';

const formatDate = (d) => (d ? new Date(d).toLocaleString() : '–');

const riskBadge = (riskLevel) => {
  switch ((riskLevel || '').toUpperCase()) {
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

const statusBadge = (status) => {
  const s = (status || '').toUpperCase();
  if (s === 'DELIVERED') return 'badge-low';
  if (s === 'IN_TRANSIT') return 'badge-medium';
  if (s === 'DISPATCHED') return 'badge-neutral';
  return 'badge-neutral';
};

const ShipmentsPage = () => {
  const [data, setData] = useState({ content: [], totalPages: 0, number: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = (page = 0) => {
    setLoading(true);
    setError(null);
    getShipments(page, 20)
      .then((res) => setData({
        content: res.content || [],
        totalPages: res.totalPages ?? 0,
        number: res.number ?? 0,
      }))
      .catch(() => setError('Failed to load shipments.'))
      .finally(() => setLoading(false));
  };

  useEffect(() => load(), []);

  if (loading && data.content.length === 0) {
    return (
      <div className="container">
        <div className="page-header">
          <div>
            <h1 className="page-title">Shipments</h1>
            <p className="page-subtitle">Monitor shipments and risk indicators.</p>
          </div>
        </div>
        <div className="card">
          <div className="card-body">Loading shipments...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <div className="page-header">
        <div>
          <h1 className="page-title">Shipments</h1>
          <p className="page-subtitle">Monitor shipments and risk indicators.</p>
        </div>
        <div>
          <Link className="btn btn-primary" to="/shipments/new">Create shipment</Link>
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
          <div className="card-title">Shipment list</div>
        </div>
        <div className="card-body" style={{ padding: 0 }}>
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Supplier</th>
                <th>Source</th>
                <th>Destination</th>
                <th>Vehicle</th>
                <th>Status</th>
                <th>Risk</th>
                <th>Dispatch</th>
                <th>Expected</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((s) => (
                <tr key={s.id}>
                  <td>{s.id}</td>
                  <td>{s.supplierName ?? '–'}</td>
                  <td>{s.sourceLocation ?? '–'}</td>
                  <td>{s.destinationLocation ?? '–'}</td>
                  <td>{s.vehicleType ?? '–'}</td>
                  <td><span className={`badge ${statusBadge(s.shipmentStatus)}`}>{s.shipmentStatus ?? 'UNKNOWN'}</span></td>
                  <td>
                    {s.riskLevel ? (
                      <span className={`badge ${riskBadge(s.riskLevel)}`}>
                        {s.riskLevel} {s.riskScore != null ? `· ${Number(s.riskScore).toFixed(1)}` : ''}
                      </span>
                    ) : (
                      <span className="badge badge-neutral">N/A</span>
                    )}
                  </td>
                  <td>{formatDate(s.dispatchTime)}</td>
                  <td>{formatDate(s.expectedDeliveryTime)}</td>
                  <td>
                    <Link className="btn-link" to={`/shipments/${s.id}`}>View</Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {data.content.length === 0 && !loading && (
            <div style={{ padding: '1rem', color: '#64748b' }}>
              No shipments yet. Create a shipment to start monitoring risk.
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

export default ShipmentsPage;
