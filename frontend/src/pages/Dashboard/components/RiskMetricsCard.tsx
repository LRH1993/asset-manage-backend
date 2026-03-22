/**
 * 风险指标卡片组件
 * 遵循 ui/CLAUDE.md 设计规范
 */

import React from 'react';
import { Card } from 'antd';
import { FUNCTIONAL_COLORS } from '@/constants/colors';
import { useAssetStore } from '@/stores/assetStore';

const RiskMetricsCard: React.FC = () => {
  const { metrics } = useAssetStore();

  // 从store获取真实数据，如果没有则使用默认值
  const riskMetrics = [
    {
      label: '最大回撤',
      value: metrics?.maxDrawdown != null ? `${metrics.maxDrawdown.toFixed(2)}%` : '-',
      level: (metrics?.maxDrawdown != null && metrics.maxDrawdown < -10) ? 'danger' :
             (metrics?.maxDrawdown != null && metrics.maxDrawdown < -5) ? 'warning' : 'normal',
    },
    {
      label: '30日波动率',
      value: metrics?.volatility30d != null ? `${metrics.volatility30d.toFixed(2)}%` : '-',
      level: 'normal',
    },
    {
      label: '夏普比率',
      value: metrics?.sharpeRatio != null ? metrics.sharpeRatio.toFixed(2) : '-',
      level: 'normal',
    },
    {
      label: 'VaR (95%)',
      value: metrics?.var95 != null ? `${metrics.var95.toFixed(2)}%` : '-',
      level: (metrics?.var95 != null && metrics.var95 < -3) ? 'danger' : 'normal',
    },
    {
      label: 'VaR (99%)',
      value: metrics?.var99 != null ? `${metrics.var99.toFixed(2)}%` : '-',
      level: (metrics?.var99 != null && metrics.var99 < -5) ? 'danger' : 'normal',
    },
  ];

  const getLevelStyle = (level: string) => {
    switch (level) {
      case 'warning':
        return { color: FUNCTIONAL_COLORS.warning, background: 'rgba(250, 173, 20, 0.1)' };
      case 'danger':
        return { color: FUNCTIONAL_COLORS.error, background: 'rgba(255, 77, 79, 0.1)' };
      default:
        return { color: '#262626', background: 'transparent' };
    }
  };

  return (
    <Card
      title={
        <span style={{ fontSize: 16, fontWeight: 600, color: '#262626' }}>
          ⚠️ 风险指标
        </span>
      }
      style={{
        borderRadius: 12,
        border: '1px solid #F0F0F0',
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
              borderBottom: index === riskMetrics.length - 1 ? 'none' : '1px solid #F0F0F0',
            }}
          >
            <span style={{ fontSize: 14, color: '#595959' }}>{metric.label}</span>
            <span
              style={{
                fontSize: 15,
                fontWeight: 500,
                color: levelStyle.color,
                padding: metric.level !== 'normal' ? '4px 10px' : '0',
                background: levelStyle.background,
                borderRadius: 4,
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
