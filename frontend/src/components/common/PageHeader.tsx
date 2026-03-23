/**
 * 页面标题栏组件
 * 统一的页面标题栏，包含标题、操作按钮、最后更新时间
 */

import React from 'react';
import { Space, Button, Tooltip } from 'antd';
import dayjs from 'dayjs';

interface PageHeaderProps {
  /** 页面图标 */
  icon: string;
  /** 页面标题 */
  title: string;
  /** 最后更新时间 */
  lastUpdate?: string | Date;
  /** 操作按钮 */
  actions?: React.ReactNode;
  /** 是否正在加载 */
  loading?: boolean;
  /** 刷新回调 */
  onRefresh?: () => void;
}

const PageHeader: React.FC<PageHeaderProps> = ({
  icon,
  title,
  lastUpdate,
  actions,
  loading,
  onRefresh,
}) => {
  return (
    <div
      style={{
        background: '#fff',
        padding: '16px 0',
        marginBottom: 16,
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        borderBottom: '1px solid #F0F0F0',
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
        <h1
          style={{
            fontSize: 18,
            fontWeight: 600,
            margin: 0,
            color: '#262626',
            display: 'flex',
            alignItems: 'center',
            gap: 8,
          }}
        >
          <span>{icon}</span>
          <span>{title}</span>
        </h1>
        {lastUpdate && (
          <span
            style={{
              fontSize: 12,
              color: '#8C8C8C',
              background: '#FAFAFA',
              padding: '4px 8px',
              borderRadius: 4,
            }}
          >
            最后更新: {dayjs(lastUpdate).format('YYYY-MM-DD HH:mm:ss')}
          </span>
        )}
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
        {onRefresh && (
          <Tooltip title="刷新数据">
            <Button
              type="text"
              onClick={onRefresh}
              loading={loading}
              style={{ color: '#8C8C8C' }}
            >
              🔄 刷新
            </Button>
          </Tooltip>
        )}
        {actions && <Space>{actions}</Space>}
      </div>
    </div>
  );
};

export default PageHeader;
