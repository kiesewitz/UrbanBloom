import './HealthCard.css';
import { StatusIndicator } from '@/components/atoms/StatusIndicator/StatusIndicator';
import Button from '@/components/atoms/Button/Button';
import type { HealthResponse, AppInfoResponse } from '@/types/api.types';

interface HealthCardProps {
  health?: HealthResponse | null;
  appInfo?: AppInfoResponse | null;
  loading?: boolean;
  error?: string | null;
  onRefresh?: () => void;
}

export const HealthCard = ({ health, appInfo, loading, error, onRefresh }: HealthCardProps) => {
  return (
    <div className="sl-health-card">
      <h2>Backend Health Status</h2>

      {loading && <p className="loading">Checking backend status...</p>}
      {error && <p className="error">{error}</p>}

      {health && !loading && (
        <div className="status-info">
          <StatusIndicator status={health.status} />

          {health.timestamp && (
            <p className="timestamp">Last checked: {new Date(health.timestamp).toLocaleString()}</p>
          )}

          {appInfo && (
            <div className="app-info">
              <h3>Application Information</h3>
              <ul>
                <li><strong>Name:</strong> {appInfo.name}</li>
                <li><strong>Version:</strong> {appInfo.version}</li>
                {appInfo.description && <li><strong>Description:</strong> {appInfo.description}</li>}
                {appInfo.environment && <li><strong>Environment:</strong> {appInfo.environment}</li>}
              </ul>
            </div>
          )}
        </div>
      )}

      <Button
        onClick={onRefresh}
        disabled={loading}
        className="refresh-button"
        aria-label="Refresh Health Status"
      >
        {loading ? 'Checking...' : 'Refresh Health Status'}
      </Button>
    </div>
  );
};

export default HealthCard;
