import { useEffect, useState } from 'react';
import HealthCard from '@/components/molecules/HealthCard/HealthCard';
import { apiClient } from '@/services/api.service';
import type { HealthResponse, AppInfoResponse } from '@/types/api.types';
import './DashboardOrganism.css';

export const DashboardOrganism = () => {
  const [health, setHealth] = useState<HealthResponse | null>(null);
  const [appInfo, setAppInfo] = useState<AppInfoResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const checkHealthStatus = async () => {
    setLoading(true);
    setError(null);

    try {
      const healthData = await apiClient.checkHealth();
      setHealth(healthData);

      if (healthData.status === 'UP') {
        try {
          const infoData = await apiClient.getAppInfo();
          setAppInfo(infoData);
        } catch (infoError) {
          console.warn('Could not fetch app info:', infoError);
        }
      }
    } catch (err) {
      setError('Failed to connect to backend');
      console.error('Health check error:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { checkHealthStatus(); }, []);

  return (
    <div className="dashboard-organism">
      <div className="dashboard-container">
        <h1>🏫 School Library Admin</h1>
        <p className="subtitle">Hello World Dashboard</p>

        <HealthCard
          health={health}
          appInfo={appInfo}
          loading={loading}
          error={error}
          onRefresh={checkHealthStatus}
        />

        <div className="info-card">
          <h3>🚀 Quick Start</h3>
          <ul>
            <li>Backend API: <code>http://localhost:8080</code></li>
            <li>Health Endpoint: <code>GET /health</code></li>
            <li>App Info Endpoint: <code>GET /api/v1/app/info</code></li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default DashboardOrganism;
