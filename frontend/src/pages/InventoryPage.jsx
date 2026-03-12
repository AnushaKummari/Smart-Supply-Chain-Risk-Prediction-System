import React from 'react';

const InventoryPage = () => (
  <div className="container">
    <div className="page-header">
      <div>
        <h1 className="page-title">Inventory</h1>
        <p className="page-subtitle">Inventory Shortage Prediction Module</p>
      </div>
    </div>
    <div className="card">
      <div className="card-header">
        <h2 className="card-title">Coming soon</h2>
      </div>
      <div className="card-body">
        <p style={{ marginBottom: '0.5rem' }}>
          Inventory shortage prediction, safety stock monitoring, and reorder point analytics will be added in an
          upcoming iteration.
        </p>
        <p style={{ margin: 0, color: '#64748b' }}>
          This page is intentionally left as a roadmap placeholder so stakeholders can see that inventory risk
          monitoring is planned in the product backlog.
        </p>
      </div>
    </div>
  </div>
);

export default InventoryPage;

