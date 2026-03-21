/**
 * 路由配置
 */

import { createBrowserRouter, Navigate } from 'react-router-dom';
import MainLayout from '@/layouts/MainLayout';

// 页面组件
import Dashboard from '@/pages/Dashboard';
import Positions from '@/pages/Portfolio/Positions';
import Transactions from '@/pages/Portfolio/Transactions';
import Universe from '@/pages/Opportunity/Universe';
import Alerts from '@/pages/Opportunity/Alerts';
import Config from '@/pages/Strategy/Config';
import Backtest from '@/pages/Strategy/Backtest';
import RiskMonitor from '@/pages/Risk/Monitor';
import StressTest from '@/pages/Risk/StressTest';
import RebalanceMonitor from '@/pages/Rebalance/Monitor';
import RebalanceExecute from '@/pages/Rebalance/Execute';
import Report from '@/pages/Report';
import Settings from '@/pages/Settings';

const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    children: [
      {
        index: true,
        element: <Dashboard />,
      },
      {
        path: 'portfolio/positions',
        element: <Positions />,
      },
      {
        path: 'portfolio/transactions',
        element: <Transactions />,
      },
      {
        path: 'opportunity/universe',
        element: <Universe />,
      },
      {
        path: 'opportunity/alerts',
        element: <Alerts />,
      },
      {
        path: 'strategy/config',
        element: <Config />,
      },
      {
        path: 'strategy/backtest',
        element: <Backtest />,
      },
      {
        path: 'risk/monitor',
        element: <RiskMonitor />,
      },
      {
        path: 'risk/stress-test',
        element: <StressTest />,
      },
      {
        path: 'rebalance/monitor',
        element: <RebalanceMonitor />,
      },
      {
        path: 'rebalance/execute',
        element: <RebalanceExecute />,
      },
      {
        path: 'report',
        element: <Report />,
      },
      {
        path: 'settings',
        element: <Settings />,
      },
      {
        path: '*',
        element: <Navigate to="/" replace />,
      },
    ],
  },
]);

export default router;
