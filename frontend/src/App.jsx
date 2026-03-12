import React from 'react';
import { useLocation } from 'react-router-dom';
import AppRouter from './routes/AppRouter.jsx';
import DashboardLayout from './components/layout/DashboardLayout.jsx';

function App() {
  const location = useLocation();
  const isAuthRoute = location.pathname === '/login';
  const content = <AppRouter />;

  if (isAuthRoute) {
    return content;
  }

  return (
    <DashboardLayout>
      {content}
    </DashboardLayout>
  );
}

export default App;

