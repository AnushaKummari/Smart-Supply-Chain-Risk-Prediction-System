import React, { useEffect, useMemo, useState } from 'react';
import { connectAlerts } from '../../services/realtimeService.js';

const formatDate = (d) => (d ? new Date(d).toLocaleString() : '–');

const severityColor = (sev) => {
  switch ((sev || '').toUpperCase()) {
    case 'CRITICAL':
      return '#b91c1c';
    case 'HIGH':
      return '#dc2626';
    case 'MEDIUM':
      return '#d97706';
    case 'LOW':
      return '#2563eb';
    default:
      return '#374151';
  }
};

const NotificationCenter = () => {
  const [connected, setConnected] = useState(false);
  const [alerts, setAlerts] = useState([]);

  useEffect(() => {
    const disconnect = connectAlerts((alert) => {
      setAlerts((prev) => [alert, ...prev].slice(0, 20));
    });
    setConnected(true);
    return () => {
      setConnected(false);
      disconnect();
    };
  }, []);

  const unreadCount = useMemo(() => alerts.length, [alerts]);

  return (
    <div style={{ marginBottom: '1rem', padding: '0.75rem', border: '1px solid #e5e7eb', borderRadius: '8px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', gap: '1rem', alignItems: 'center' }}>
        <strong>Live Alerts</strong>
        <span style={{ color: connected ? '#16a34a' : '#6b7280' }}>
          {connected ? 'Connected' : 'Disconnected'}
        </span>
      </div>
      <div style={{ marginTop: '0.5rem', color: '#6b7280' }}>
        Latest notifications: {unreadCount}
      </div>
      <div style={{ marginTop: '0.75rem', display: 'grid', gap: '0.5rem' }}>
        {alerts.length === 0 && <div style={{ color: '#6b7280' }}>No live alerts yet.</div>}
        {alerts.map((a) => (
          <div key={a.id ?? `${a.alertType}-${a.createdAt}`} style={{ border: '1px solid #f3f4f6', borderRadius: '8px', padding: '0.5rem 0.75rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', gap: '1rem' }}>
              <span style={{ fontWeight: 600 }}>{a.alertType}</span>
              <span style={{ color: severityColor(a.severity), fontWeight: 700 }}>{a.severity}</span>
            </div>
            <div style={{ color: '#374151', marginTop: '0.25rem' }}>{a.alertMessage}</div>
            <div style={{ color: '#6b7280', marginTop: '0.25rem', fontSize: '0.9rem' }}>
              Shipment: {a.shipmentId ?? '–'} · {formatDate(a.createdAt)}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default NotificationCenter;

