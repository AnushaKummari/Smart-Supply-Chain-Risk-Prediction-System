import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createShipment } from '../services/shipmentService.js';
import { getSuppliers } from '../services/supplierService.js';

const CreateShipmentPage = () => {
  const navigate = useNavigate();
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [form, setForm] = useState({
    supplierId: '',
    sourceLocation: '',
    destinationLocation: '',
    distanceKm: '',
    vehicleType: '',
    dispatchTime: '',
    expectedDeliveryTime: '',
    weatherCondition: '',
    trafficLevel: '',
  });

  const [suppliersLoaded, setSuppliersLoaded] = useState(false);

  useEffect(() => {
    setError('');
    getSuppliers()
      .then((list) => {
        setSuppliers(list || []);
        setSuppliersLoaded(true);
      })
      .catch(() => {
        setError('Failed to load suppliers.');
        setSuppliersLoaded(true);
      });
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (!form.supplierId) {
      setError('Select a supplier.');
      return;
    }
    setLoading(true);
    try {
      const payload = {
        supplierId: Number(form.supplierId),
        sourceLocation: form.sourceLocation || null,
        destinationLocation: form.destinationLocation || null,
        distanceKm: form.distanceKm ? Number(form.distanceKm) : null,
        vehicleType: form.vehicleType || null,
        dispatchTime: form.dispatchTime ? new Date(form.dispatchTime).toISOString() : null,
        expectedDeliveryTime: form.expectedDeliveryTime ? new Date(form.expectedDeliveryTime).toISOString() : null,
        weatherCondition: form.weatherCondition || null,
        trafficLevel: form.trafficLevel || null,
      };
      const created = await createShipment(payload);
      navigate(`/shipments/${created.id}`);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create shipment.');
    } finally {
      setLoading(false);
    }
  };

  if (!suppliersLoaded && suppliers.length === 0) {
    return <div><h1>Create shipment</h1><p>Loading suppliers...</p></div>;
  }

  return (
    <div>
      <h1>Create shipment</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      {suppliersLoaded && suppliers.length === 0 && <p>No suppliers available. Add suppliers first.</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label>Supplier *</label>
          <select name="supplierId" value={form.supplierId} onChange={handleChange} required disabled={suppliers.length === 0}>
            <option value="">{suppliers.length === 0 ? 'No suppliers' : 'Select supplier'}</option>
            {suppliers.map((s) => (
              <option key={s.id} value={s.id}>{s.supplierName}</option>
            ))}
          </select>
        </div>
          <div>
            <label>Source location</label>
            <input name="sourceLocation" value={form.sourceLocation} onChange={handleChange} />
          </div>
          <div>
            <label>Destination location</label>
            <input name="destinationLocation" value={form.destinationLocation} onChange={handleChange} />
          </div>
          <div>
            <label>Distance (km)</label>
            <input name="distanceKm" type="number" step="0.01" value={form.distanceKm} onChange={handleChange} />
          </div>
          <div>
            <label>Vehicle type</label>
            <input name="vehicleType" value={form.vehicleType} onChange={handleChange} placeholder="e.g. Truck" />
          </div>
          <div>
            <label>Dispatch time</label>
            <input name="dispatchTime" type="datetime-local" value={form.dispatchTime} onChange={handleChange} />
          </div>
          <div>
            <label>Expected delivery time</label>
            <input name="expectedDeliveryTime" type="datetime-local" value={form.expectedDeliveryTime} onChange={handleChange} />
          </div>
          <div>
            <label>Weather condition</label>
            <input name="weatherCondition" value={form.weatherCondition} onChange={handleChange} />
          </div>
          <div>
            <label>Traffic level</label>
            <input name="trafficLevel" value={form.trafficLevel} onChange={handleChange} placeholder="e.g. Low, Medium, High" />
          </div>
          {error && <p style={{ color: 'red' }}>{error}</p>}
          <button type="submit" disabled={loading || suppliers.length === 0}>{loading ? 'Creating...' : 'Create shipment'}</button>
        </form>
    </div>
  );
};

export default CreateShipmentPage;
