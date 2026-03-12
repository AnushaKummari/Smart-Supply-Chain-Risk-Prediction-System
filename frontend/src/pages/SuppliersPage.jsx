import React, { useEffect, useState } from 'react';
import { getSuppliers, createSupplier } from '../services/supplierService.js';

const SuppliersPage = () => {
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [form, setForm] = useState({ supplierName: '', contactEmail: '', contactPhone: '' });
  const [submitting, setSubmitting] = useState(false);

  const load = () => {
    setLoading(true);
    getSuppliers()
      .then(setSuppliers)
      .catch(() => setError('Failed to load suppliers.'))
      .finally(() => setLoading(false));
  };

  useEffect(() => load(), []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.supplierName?.trim()) return;
    setSubmitting(true);
    setError(null);
    try {
      await createSupplier({
        supplierName: form.supplierName.trim(),
        contactEmail: form.contactEmail?.trim() || null,
        contactPhone: form.contactPhone?.trim() || null,
      });
      setForm({ supplierName: '', contactEmail: '', contactPhone: '' });
      load();
    } catch (err) {
      setError('Failed to create supplier.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container">
      <div className="page-header">
        <div>
          <h1 className="page-title">Suppliers</h1>
          <p className="page-subtitle">Supplier registry and performance overview.</p>
        </div>
      </div>

      <div className="grid-2" style={{ marginBottom: '1.5rem' }}>
        <div className="card">
          <div className="card-header">
            <h2 className="card-title">Add supplier</h2>
          </div>
          <div className="card-body">
            <form onSubmit={handleSubmit} className="login-form">
              <div className="form-field">
                <label className="form-label">Name *</label>
                <input
                  className="form-input"
                  name="supplierName"
                  value={form.supplierName}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="form-field">
                <label className="form-label">Email</label>
                <input
                  className="form-input"
                  name="contactEmail"
                  type="email"
                  value={form.contactEmail}
                  onChange={handleChange}
                />
              </div>
              <div className="form-field">
                <label className="form-label">Phone</label>
                <input
                  className="form-input"
                  name="contactPhone"
                  value={form.contactPhone}
                  onChange={handleChange}
                />
              </div>
              {error && <p className="form-error">{error}</p>}
              <button className="btn btn-primary" type="submit" disabled={submitting}>
                {submitting ? 'Adding...' : 'Add supplier'}
              </button>
            </form>
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <h2 className="card-title">Suppliers</h2>
          </div>
          <div className="card-body" style={{ padding: 0 }}>
            {loading ? (
              <div style={{ padding: '1rem', color: '#64748b' }}>Loading...</div>
            ) : (
              <table className="table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                  </tr>
                </thead>
                <tbody>
                  {suppliers.map((s) => (
                    <tr key={s.id}>
                      <td>{s.id}</td>
                      <td>{s.supplierName}</td>
                      <td>{s.contactEmail ?? '–'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
            {!loading && suppliers.length === 0 && (
              <div style={{ padding: '1rem', color: '#64748b' }}>
                No suppliers. Add one on the left to start creating shipments.
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default SuppliersPage;
