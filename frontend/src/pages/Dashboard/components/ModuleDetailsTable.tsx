/**
 * 模块详情表格组件
 * 遵循 ui/CLAUDE.md 设计规范
 */

import React from 'react';
import { Card, Table } from 'antd';
import { MODULES } from '@/constants/modules';
import { PROFIT_COLORS, FUNCTIONAL_COLORS } from '@/constants/colors';
import { formatCurrency, formatPercent, getProfitColor } from '@/utils/format';
import { useAssetStore } from '@/stores/assetStore';

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
  const { allocation } = useAssetStore();

  // 从store获取真实数据
  const modules: ModuleDetail[] = allocation ? [
    {
      key: 'dividend',
      name: MODULES.dividend.name,
      icon: MODULES.dividend.icon,
      currentWeight: allocation.dividend?.currentWeight || 0,
      targetWeight: allocation.dividend?.targetWeight ? allocation.dividend.targetWeight * 100 : 25,
      deviation: allocation.dividend?.deviation || 0,
      returnRate: allocation.dividend?.return || 0,
      value: allocation.dividend?.currentValue || 0,
    },
    {
      key: 'fixed',
      name: MODULES.fixed.name,
      icon: MODULES.fixed.icon,
      currentWeight: allocation.fixed?.currentWeight || 0,
      targetWeight: allocation.fixed?.targetWeight ? allocation.fixed.targetWeight * 100 : 25,
      deviation: allocation.fixed?.deviation || 0,
      returnRate: allocation.fixed?.return || 0,
      value: allocation.fixed?.currentValue || 0,
    },
    {
      key: 'growth',
      name: MODULES.growth.name,
      icon: MODULES.growth.icon,
      currentWeight: allocation.growth?.currentWeight || 0,
      targetWeight: allocation.growth?.targetWeight ? allocation.growth.targetWeight * 100 : 25,
      deviation: allocation.growth?.deviation || 0,
      returnRate: allocation.growth?.return || 0,
      value: allocation.growth?.currentValue || 0,
    },
    {
      key: 'allweather',
      name: MODULES.allweather.name,
      icon: MODULES.allweather.icon,
      currentWeight: allocation.allweather?.currentWeight || 0,
      targetWeight: allocation.allweather?.targetWeight ? allocation.allweather.targetWeight * 100 : 25,
      deviation: allocation.allweather?.deviation || 0,
      returnRate: allocation.allweather?.return || 0,
      value: allocation.allweather?.currentValue || 0,
    },
  ] : [];

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
            color: MODULES[record.key]?.color || FUNCTIONAL_COLORS.primary,
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
        // 偏离度：正数表示超配（需减仓），负数表示低配（需加仓）
        // 超配用警告色，低配用主色调
        const isOverweight = value > 0;
        const color = value === 0 ? PROFIT_COLORS.neutral :
                      isOverweight ? FUNCTIONAL_COLORS.warning : FUNCTIONAL_COLORS.primary;
        return (
          <span
            style={{
              color,
              fontSize: 14,
              fontWeight: 600,
              padding: '6px 12px',
              background: value === 0 ? 'rgba(140, 140, 140, 0.1)' :
                         isOverweight ? 'rgba(250, 173, 20, 0.1)' : 'rgba(24, 144, 255, 0.1)',
              borderRadius: 6,
            }}
          >
            {value >= 0 ? '+' : ''}{value}%
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
      render: (value: number) => {
        // 收益率使用涨跌色（A股习惯：红涨绿跌）
        const color = getProfitColor(value);
        return (
          <span style={{ color, fontSize: 14, fontWeight: 600 }}>
            {value >= 0 ? '+' : ''}{formatPercent(value)}
          </span>
        );
      },
    },
    {
      title: '市值',
      dataIndex: 'value',
      key: 'value',
      width: 140,
      align: 'right' as const,
      render: (value: number) => (
        <span style={{ color: '#262626', fontSize: 14, fontWeight: 500 }}>
          {formatCurrency(value)}
        </span>
      ),
    },
  ];

  return (
    <Card
      title={
        <span style={{ fontSize: 16, fontWeight: 600, color: '#262626' }}>
          📊 模块详情
        </span>
      }
      style={{
        borderRadius: 12,
        border: '1px solid #F0F0F0',
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
