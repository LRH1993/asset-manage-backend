/**
 * 头部组件
 */

import React from 'react';
import { Layout, Space, Button, Badge, Dropdown } from 'antd';
import { MenuFoldOutlined, MenuUnfoldOutlined, BellOutlined, UserOutlined } from '@ant-design/icons';
import { useUIStore } from '@/stores/uiStore';

const { Header: AntHeader } = Layout;

const HeaderComponent: React.FC = () => {
  const { sidebarCollapsed, toggleSidebar, notifications } = useUIStore();

  const menuItems = [
    {
      key: 'profile',
      label: '个人中心',
      icon: <UserOutlined />,
    },
    {
      key: 'logout',
      label: '退出登录',
      danger: true,
    },
  ];

  return (
    <AntHeader
      style={{
        padding: '0 24px 0 24px',
        background: '#fff',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        boxShadow: '0 1px 4px rgba(0, 21, 41, 0.08)',
        height: 64,
      }}
    >
      <Button
        type="text"
        icon={sidebarCollapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
        onClick={toggleSidebar}
        style={{ fontSize: 16, marginLeft: -8 }}
      />

      <Space>
        <Badge count={notifications.length} dot>
          <Button type="text" icon={<BellOutlined />} />
        </Badge>

        <Dropdown menu={{ items: menuItems }} placement="bottomRight">
          <Button type="text" icon={<UserOutlined />}>
            管理员
          </Button>
        </Dropdown>
      </Space>
    </AntHeader>
  );
};

export default HeaderComponent;
