import './StatusIndicator.css';
import { colors } from '@/design-system/tokens';

export type HealthStatus = 'UP' | 'DOWN' | 'UNKNOWN' | string;

export interface StatusIndicatorProps {
  status?: HealthStatus;
}

export const StatusIndicator = ({ status = 'UNKNOWN' }: StatusIndicatorProps) => {
  const color = status === 'UP' ? colors.success : status === 'DOWN' ? colors.danger : colors.warning;

  return (
    <div className="sl-status-indicator" style={{ backgroundColor: color }}>
      {String(status)}
    </div>
  );
};

export default StatusIndicator;
