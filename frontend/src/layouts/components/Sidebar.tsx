/**
 * 侧边栏组件
 */

import React from 'react';
import { Layout, Menu } from 'antd';
import { Link, useLocation } from 'react-router-dom';
import {
  DashboardOutlined,
  WalletOutlined,
  SearchOutlined,
  LineChartOutlined,
  SafetyOutlined,
  SwapOutlined,
  FileTextOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import { useUIStore } from '@/stores/uiStore';

const { Sider } = Layout;

const Sidebar: React.FC = () => {
  const location = useLocation();
  const { sidebarCollapsed } = useUIStore();

  const menuItems = [
    {
      key: '/',
      icon: <DashboardOutlined />,
      label: <Link to="/">仪表盘</Link>,
    },
    {
      key: 'portfolio',
      icon: <WalletOutlined />,
      label: '资产管理',
      children: [
        {
          key: '/portfolio/positions',
          label: <Link to="/portfolio/positions">持仓管理</Link>,
        },
        {
          key: '/portfolio/transactions',
          label: <Link to="/portfolio/transactions">交易记录</Link>,
        },
      ],
    },
    {
      key: 'opportunity',
      icon: <SearchOutlined />,
      label: '机会发现',
      children: [
        {
          key: '/opportunity/universe',
          label: <Link to="/opportunity/universe">投资标的库</Link>,
        },
        {
          key: '/opportunity/alerts',
          label: <Link to="/opportunity/alerts">机会提醒</Link>,
        },
      ],
    },
    {
      key: 'strategy',
      icon: <LineChartOutlined />,
      label: '策略分析',
      children: [
        {
          key: '/strategy/config',
          label: <Link to="/strategy/config">策略配置</Link>,
        },
        {
          key: '/strategy/backtest',
          label: <Link to="/strategy/backtest">回测分析</Link>,
        },
      ],
    },
    {
      key: 'risk',
      icon: <SafetyOutlined />,
      label: '风险管理',
      children: [
        {
          key: '/risk/monitor',
          label: <Link to="/risk/monitor">风险监控</Link>,
        },
        {
          key: '/risk/stress-test',
          label: <Link to="/risk/stress-test">压力测试</Link>,
        },
      ],
    },
    {
      key: 'rebalance',
      icon: <SwapOutlined />,
      label: '动态平衡',
      children: [
        {
          key: '/rebalance/monitor',
          label: <Link to="/rebalance/monitor">平衡监控</Link>,
        },
        {
          key: '/rebalance/execute',
          label: <Link to="/rebalance/execute">平衡执行</Link>,
        },
      ],
    },
    {
      key: '/report',
      icon: <FileTextOutlined />,
      label: <Link to="/report">报表系统</Link>,
    },
    {
      key: '/settings',
      icon: <SettingOutlined />,
      label: <Link to="/settings">系统设置</Link>,
    },
  ];

  return (
    <Sider
      trigger={null}
      collapsible
      collapsed={sidebarCollapsed}
      style={{
        overflow: 'auto',
        height: '100vh',
        position: 'fixed',
        left: 0,
        top: 0,
        bottom: 0,
      }}
    >
      <div
        style={{
          height: '64px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          background: '#001529',
          color: '#fff',
          fontSize: '18px',
          fontWeight: 'bold',
          borderBottom: '1px solid #002140',
        }}
      >
        {sidebarCollapsed ? '资产' : '家庭资产监控'}
      </div>
      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={[location.pathname]}
        defaultOpenKeys={location.pathname.split('/').slice(1, 2).filter(Boolean)}
        items={menuItems}
      />
    </Sider>
  );
};

export default Sidebar;
