/**
 * 主布局组件
 */

import React from 'react';
import { Outlet } from 'react-router-dom';
import { Layout } from 'antd';
import { useUIStore } from '@/stores/uiStore';
import Header from './components/Header';
import Sidebar from './components/Sidebar';

const { Content } = Layout;

const MainLayout: React.FC = () => {
  const { sidebarCollapsed } = useUIStore();

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sidebar />
      <Layout style={{ marginLeft: sidebarCollapsed ? 80 : 256, transition: 'all 0.2s' }}>
        <Header />
        <Content style={{ margin: '24px 16px', padding: 24, background: '#fff', minHeight: 280 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainLayout;
