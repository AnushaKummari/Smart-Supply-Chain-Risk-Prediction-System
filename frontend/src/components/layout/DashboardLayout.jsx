import React from 'react';
import { Link } from 'react-router-dom';

const DashboardLayout = ({ children }) => (
  <div className="layout">
    <aside className="sidebar">
      <h2>SSRP</h2>
      <nav>
        <Link to="/dashboard">Dashboard</Link>
        <Link to="/shipments">Shipments</Link>
        <Link to="/suppliers">Suppliers</Link>
        <Link to="/inventory">Inventory</Link>
        <Link to="/alerts">Alerts</Link>
        <Link to="/analytics">Analytics</Link>
      </nav>
    </aside>
    <main className="content">
      {children}
    </main>
  </div>
);

export default DashboardLayout;

