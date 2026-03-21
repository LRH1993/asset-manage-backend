/**
 * 风险指标卡片组件
 */

import React from 'react';
import { Card } from 'antd';

const RiskMetricsCard: React.FC = () => {
  const riskMetrics = [
    {
      label: '最大回撤',
      value: '-12.3%',
      level: 'warning' as 'normal' | 'warning' | 'danger',
    },
    {
      label: '30日波动率',
      value: '8.5%',
      level: 'normal',
    },
    {
      label: '夏普比率',
      value: '1.42',
      level: 'normal',
    },
    {
      label: '集中度风险',
      value: '低',
      level: 'normal',
    },
    {
      label: 'VaR (95%)',
      value: '-¥45,000',
      level: 'danger',
    },
  ];

  const getLevelStyle = (level: string) => {
    switch (level) {
      case 'warning':
        return { color: '#ecc94b', background: 'rgba(236, 148, 75, 0.1)' };
      case 'danger':
        return { color: '#f56565', background: 'rgba(245, 101, 101, 0.1)' };
      default:
        return { color: '#2d3748', background: 'transparent' };
    }
  };

  return (
    <Card
      title={
        <span style={{ fontSize: 16, fontWeight: 600, color: '#2d3748' }}>
          ⚠️ 风险指标
        </span>
      }
      style={{
        borderRadius: 16,
        boxShadow: '0 2px 12px rgba(0, 0, 0, 0.06)',
        height: '360px',
      }}
      styles={{ body: { padding: '16px', height: 'calc(100% - 56px)' } }}
    >
      {riskMetrics.map((metric, index) => {
        const levelStyle = getLevelStyle(metric.level);
        return (
          <div
            key={index}
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              padding: '12px 0',
              borderBottom: index === riskMetrics.length - 1 ? 'none' : '1px solid #e2e8f0',
            }}
          >
            <span style={{ fontSize: 14, color: '#718096' }}>{metric.label}</span>
            <span
              style={{
                fontSize: 16,
                fontWeight: 600,
                color: levelStyle.color,
                padding: metric.level !== 'normal' ? '6px 12px' : '0',
                background: levelStyle.background,
                borderRadius: 6,
              }}
            >
              {metric.value}
            </span>
          </div>
        );
      })}
    </Card>
  );
};

export default RiskMetricsCard;
