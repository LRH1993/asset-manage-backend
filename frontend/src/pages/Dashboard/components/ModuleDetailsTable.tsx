/**
 * 模块详情表格组件
 */

import React from 'react';
import { Card, Table } from 'antd';
import { MODULES } from '@/constants/modules';
import { formatCurrency, formatPercent } from '@/utils/format';

interface ModuleDetail {
  key: string;
  name: string;
  icon: string;
  currentWeight: number;
  targetWeight: number;
  deviation: number;
  returnRate: number;
  value: number;
}

const ModuleDetailsTable: React.FC = () => {
  const modules: ModuleDetail[] = [
    {
      key: MODULES.growth.key,
      name: MODULES.growth.name,
      icon: MODULES.growth.icon,
      currentWeight: 30,
      targetWeight: 25,
      deviation: 5,
      returnRate: 12.5,
      value: 370370,
    },
    {
      key: MODULES.dividend.key,
      name: MODULES.dividend.name,
      icon: MODULES.dividend.icon,
      currentWeight: 25,
      targetWeight: 25,
      deviation: 0,
      returnRate: 8.2,
      value: 308642,
    },
    {
      key: MODULES.fixed.key,
      name: MODULES.fixed.name,
      icon: MODULES.fixed.icon,
      currentWeight: 25,
      targetWeight: 25,
      deviation: 0,
      returnRate: 3.1,
      value: 308642,
    },
    {
      key: MODULES.allweather.key,
      name: MODULES.allweather.name,
      icon: MODULES.allweather.icon,
      currentWeight: 20,
      targetWeight: 25,
      deviation: -5,
      returnRate: 5.4,
      value: 246914,
    },
  ];

  const columns = [
    {
      title: '模块',
      dataIndex: 'name',
      key: 'name',
      width: 120,
      render: (text: string, record: ModuleDetail) => (
        <span
          style={{
            fontSize: 13,
            fontWeight: 600,
            color: MODULES[record.key].color,
          }}
        >
          {record.icon} {text}
        </span>
      ),
    },
    {
      title: '当前占比',
      dataIndex: 'currentWeight',
      key: 'currentWeight',
      width: 120,
      align: 'center' as const,
      render: (value: number) => (
        <span style={{ color: '#718096', fontSize: 14, fontWeight: 600 }}>
          {value}%
        </span>
      ),
    },
    {
      title: '目标占比',
      dataIndex: 'targetWeight',
      key: 'targetWeight',
      width: 120,
      align: 'center' as const,
      render: (value: number) => (
        <span style={{ color: '#718096', fontSize: 14, fontWeight: 600 }}>
          {value}%
        </span>
      ),
    },
    {
      title: '偏离度',
      dataIndex: 'deviation',
      key: 'deviation',
      width: 120,
      align: 'center' as const,
      render: (value: number) => {
        const isPositive = value >= 0;
        return (
          <span
            style={{
              color: isPositive ? '#48bb78' : '#f56565',
              fontSize: 14,
              fontWeight: 600,
              padding: '6px 12px',
              background: isPositive ? 'rgba(72, 187, 120, 0.1)' : 'rgba(245, 101, 101, 0.1)',
              borderRadius: 6,
            }}
          >
            {isPositive ? '+' : ''}{value}%
          </span>
        );
      },
    },
    {
      title: '收益率',
      dataIndex: 'returnRate',
      key: 'returnRate',
      width: 100,
      align: 'center' as const,
      render: (value: number) => (
        <span style={{ color: '#48bb78', fontSize: 14, fontWeight: 600 }}>
          +{formatPercent(value)}
        </span>
      ),
    },
    {
      title: '市值',
      dataIndex: 'value',
      key: 'value',
      width: 140,
      align: 'right' as const,
      render: (value: number) => (
        <span style={{ color: '#48bb78', fontSize: 14, fontWeight: 600 }}>
          {formatCurrency(value)}
        </span>
      ),
    },
  ];

  return (
    <Card
      title={
        <span style={{ fontSize: 16, fontWeight:  600, color: '#2d3748' }}>
          📊 模块详情
        </span>
      }
      style={{
        borderRadius: 16,
        boxShadow: '0 2px 12px rgba(0, 0, 0, 0.06)',
        height: '360px',
      }}
      styles={{ body: { padding: '16px', height: 'calc(100% - 56px)', overflow: 'auto' } }}
    >
      <Table
        columns={columns}
        dataSource={modules}
        pagination={false}
        size="middle"
        rowKey="key"
        scroll={{ y: 280 }}
      />
    </Card>
  );
};

export default ModuleDetailsTable;
