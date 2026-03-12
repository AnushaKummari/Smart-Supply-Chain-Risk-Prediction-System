import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from '../pages/LoginPage.jsx';
import DashboardPage from '../pages/DashboardPage.jsx';
import ShipmentsPage from '../pages/ShipmentsPage.jsx';
import CreateShipmentPage from '../pages/CreateShipmentPage.jsx';
import ShipmentDetailPage from '../pages/ShipmentDetailPage.jsx';
import SuppliersPage from '../pages/SuppliersPage.jsx';
import InventoryPage from '../pages/InventoryPage.jsx';
import AlertsPage from '../pages/AlertsPage.jsx';
import AnalyticsPage from '../pages/AnalyticsPage.jsx';
import { isAuthenticated } from '../services/authService.js';

const PrivateRoute = ({ element }) => {
  if (!isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }
  return element;
};

const AppRouter = () => (
  <Routes>
    <Route path="/login" element={<LoginPage />} />
    <Route path="/dashboard" element={<PrivateRoute element={<DashboardPage />} />} />
    <Route path="/shipments" element={<PrivateRoute element={<ShipmentsPage />} />} />
    <Route path="/shipments/new" element={<PrivateRoute element={<CreateShipmentPage />} />} />
    <Route path="/shipments/:id" element={<PrivateRoute element={<ShipmentDetailPage />} />} />
    <Route path="/suppliers" element={<PrivateRoute element={<SuppliersPage />} />} />
    <Route path="/inventory" element={<PrivateRoute element={<InventoryPage />} />} />
    <Route path="/alerts" element={<PrivateRoute element={<AlertsPage />} />} />
    <Route path="/analytics" element={<PrivateRoute element={<AnalyticsPage />} />} />
    <Route path="*" element={<Navigate to="/dashboard" replace />} />
  </Routes>
);

export default AppRouter;


