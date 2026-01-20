import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Dashboard } from './Dashboard';

describe('Dashboard Component', () => {
  it('renders hello world dashboard title', () => {
    render(<Dashboard />);
    
    const title = screen.getByText(/School Library Admin/i);
    expect(title).toBeInTheDocument();
    
    const subtitle = screen.getByText(/Hello World Dashboard/i);
    expect(subtitle).toBeInTheDocument();
  });

  it('renders health status section', () => {
    render(<Dashboard />);
    
    const healthSection = screen.getByText(/Backend Health Status/i);
    expect(healthSection).toBeInTheDocument();
  });

  it('renders refresh button', () => {
    render(<Dashboard />);
    
    const button = screen.getByRole('button', { name: /Refresh Health Status/i });
    expect(button).toBeInTheDocument();
  });

  it('displays quick start information', () => {
    render(<Dashboard />);
    
    const quickStart = screen.getByText(/Quick Start/i);
    expect(quickStart).toBeInTheDocument();
    
    const backendUrl = screen.getByText(/http:\/\/localhost:8080/i);
    expect(backendUrl).toBeInTheDocument();
  });
});
