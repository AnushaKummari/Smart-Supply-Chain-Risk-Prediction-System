import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getShipmentById, optimizeRouteForShipment, predictDelay, updateShipmentStatus } from '../services/shipmentService.js';
import { getShipmentRecommendations } from '../services/recommendationService.js';

const formatDate = (d) => (d ? new Date(d).toLocaleString() : '–');

const ShipmentDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [shipment, setShipment] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [statusValue, setStatusValue] = useState('');
  const [updating, setUpdating] = useState(false);
  const [predicting, setPredicting] = useState(false);
  const [recoLoading, setRecoLoading] = useState(false);
  const [recommendations, setRecommendations] = useState(null);
  const [routeLoading, setRouteLoading] = useState(false);
  const [routeOpt, setRouteOpt] = useState(null);

  useEffect(() => {
    getShipmentById(id)
      .then((s) => {
        setShipment(s);
        setStatusValue(s.shipmentStatus || '');
      })
      .catch(() => setError('Shipment not found.'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleStatusSubmit = async (e) => {
    e.preventDefault();
    if (!statusValue.trim()) return;
    setUpdating(true);
    setError(null);
    try {
      const updated = await updateShipmentStatus(id, statusValue.trim());
      setShipment(updated);
    } catch (err) {
      setError('Failed to update status.');
    } finally {
      setUpdating(false);
    }
  };

  const handlePredict = async () => {
    setPredicting(true);
    setError(null);
    try {
      const updated = await predictDelay(id);
      setShipment(updated);
    } catch (err) {
      setError('Failed to run prediction.');
    } finally {
      setPredicting(false);
    }
  };

  const handleRecommendations = async () => {
    setRecoLoading(true);
    setError(null);
    try {
      const res = await getShipmentRecommendations(id);
      setRecommendations(res);
    } catch (err) {
      setError('Failed to load recommendations.');
    } finally {
      setRecoLoading(false);
    }
  };

  const handleRouteOptimization = async () => {
    setRouteLoading(true);
    setError(null);
    try {
      const res = await optimizeRouteForShipment(id);
      setRouteOpt(res);
    } catch (err) {
      setError('Failed to load route optimization suggestions.');
    } finally {
      setRouteLoading(false);
    }
  };

  if (loading) return <p>Loading...</p>;
  if (error || !shipment) return <p style={{ color: 'red' }}>{error || 'Not found'}</p>;

  return (
    <div>
      <h1>Shipment #{shipment.id}</h1>
      <p><button type="button" onClick={() => navigate('/shipments')}>Back to list</button></p>
      <p>
        <button type="button" onClick={handlePredict} disabled={predicting}>
          {predicting ? 'Predicting...' : 'Run delay prediction'}
        </button>
      </p>
      <p>
        <button type="button" onClick={handleRecommendations} disabled={recoLoading}>
          {recoLoading ? 'Loading...' : 'Get Recommendations'}
        </button>
      </p>
      <p>
        <button type="button" onClick={handleRouteOptimization} disabled={routeLoading}>
          {routeLoading ? 'Loading...' : 'Optimize Route'}
        </button>
      </p>
      <dl>
        <dt>Supplier</dt>
        <dd>{shipment.supplierName ?? '–'}</dd>
        <dt>Source</dt>
        <dd>{shipment.sourceLocation ?? '–'}</dd>
        <dt>Destination</dt>
        <dd>{shipment.destinationLocation ?? '–'}</dd>
        <dt>Distance (km)</dt>
        <dd>{shipment.distanceKm ?? '–'}</dd>
        <dt>Vehicle type</dt>
        <dd>{shipment.vehicleType ?? '–'}</dd>
        <dt>Dispatch time</dt>
        <dd>{formatDate(shipment.dispatchTime)}</dd>
        <dt>Expected delivery</dt>
        <dd>{formatDate(shipment.expectedDeliveryTime)}</dd>
        <dt>Actual delivery</dt>
        <dd>{formatDate(shipment.actualDeliveryTime)}</dd>
        <dt>Weather</dt>
        <dd>{shipment.weatherCondition ?? '–'}</dd>
        <dt>Traffic</dt>
        <dd>{shipment.trafficLevel ?? '–'}</dd>
        <dt>Status</dt>
        <dd>{shipment.shipmentStatus ?? '–'}</dd>
        <dt>Risk score</dt>
        <dd>{shipment.riskScore ?? '–'}</dd>
        <dt>Risk level</dt>
        <dd>{shipment.riskLevel ?? '–'}</dd>
        <dt>Delay probability</dt>
        <dd>{shipment.delayProbability ?? '–'}</dd>
        <dt>Predicted delay (hours)</dt>
        <dd>{shipment.predictedDelayHours ?? '–'}</dd>
        <dt>Created</dt>
        <dd>{formatDate(shipment.createdAt)}</dd>
      </dl>
      <h2>Update status</h2>
      <form onSubmit={handleStatusSubmit}>
        <input
          value={statusValue}
          onChange={(e) => setStatusValue(e.target.value)}
          placeholder="e.g. IN_TRANSIT, DELIVERED"
        />
        <button type="submit" disabled={updating}>{updating ? 'Updating...' : 'Update status'}</button>
      </form>

      <h2>Recommendations</h2>
      {!recommendations && <p>Click “Get Recommendations” to generate suggestions.</p>}
      {recommendations && (
        <div style={{ display: 'grid', gap: '0.75rem' }}>
          {(recommendations.recommendations || []).length === 0 && <p>No recommendations for this shipment.</p>}
          {(recommendations.recommendations || []).map((r, idx) => (
            <div key={`${r.type}-${idx}`} style={{ border: '1px solid #e5e7eb', padding: '0.75rem', borderRadius: '8px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', gap: '1rem' }}>
                <strong>{r.title}</strong>
                <span>{r.severity}</span>
              </div>
              <div style={{ color: '#374151', marginTop: '0.25rem' }}>{r.description}</div>
            </div>
          ))}
        </div>
      )}

      <h2>Route optimization</h2>
      {!routeOpt && <p>Click “Optimize Route” to get route improvement suggestions.</p>}
      {routeOpt && (
        <div style={{ display: 'grid', gap: '0.75rem' }}>
          {(routeOpt.suggestions || []).map((s, idx) => (
            <div key={`${s.type}-${idx}`} style={{ border: '1px solid #e5e7eb', padding: '0.75rem', borderRadius: '8px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', gap: '1rem' }}>
                <strong>{s.title}</strong>
                <span>{s.priority}</span>
              </div>
              <div style={{ color: '#374151', marginTop: '0.25rem' }}>{s.description}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ShipmentDetailPage;
