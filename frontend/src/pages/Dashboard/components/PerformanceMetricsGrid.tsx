/**
 * 性能指标网格组件
 * 遵循 ui/CLAUDE.md 设计规范
 */

import React from 'react';
import { Card, Row, Col } from 'antd';
import { PROFIT_COLORS } from '@/constants/colors';
import { useAssetStore } from '@/stores/assetStore';
import { formatPercent } from '@/utils/format';

const PerformanceMetricsGrid: React.FC = () => {
  const { overview, allocation } = useAssetStore();

  // 计算总偏离度
  const calculateTotalDeviation = () => {
    if (!allocation) return 0;
    const modules = [allocation.dividend, allocation.fixed, allocation.growth, allocation.allweather];
    return modules.reduce((sum, m) => sum + Math.abs(m?.deviation || 0), 0);
  };

  const metrics = [
    {
      icon: '📈',
      title: '累计收益率',
      value: overview?.totalProfitRate != null ? formatPercent(overview.totalProfitRate) : '-',
      change: overview?.benchmarkComparison != null ? `跑赢基准 ${formatPercent(overview.benchmarkComparison)}` : '-',
      changeType: (overview?.benchmarkComparison ?? 0) > 0 ? 'positive' as const : 'negative' as const,
    },
    {
      icon: '📅',
      title: '年化收益率',
      value: overview?.annualReturn != null ? formatPercent(overview.annualReturn) : '-',
      change: '过去12个月',
      changeType: (overview?.annualReturn ?? 0) > 0 ? 'positive' as const : 'negative' as const,
    },
    {
      icon: '📊',
      title: '偏离度',
      value: `${calculateTotalDeviation().toFixed(1)}%`,
      change: calculateTotalDeviation() > 5 ? '需要调仓' : '配置正常',
      changeType: calculateTotalDeviation() > 5 ? 'warning' as const : 'positive' as const,
    },
  ];

  // 获取状态颜色（A股习惯：红涨绿跌）
  const getChangeColor = (type: 'positive' | 'warning' | 'negative') => {
    switch (type) {
      case 'positive':
        return PROFIT_COLORS.up;      // #CF1322 红色
      case 'negative':
        return PROFIT_COLORS.down;    // #3F8600 绿色
      case 'warning':
        return '#FAAD14';             // 警告黄色
      default:
        return PROFIT_COLORS.neutral;
    }
  };

  return (
    <Row gutter={16} style={{ height: '100%' }}>
      {metrics.map((metric, index) => (
        <Col xs={8} sm={8} md={8} key={index}>
          <Card
            style={{
              borderRadius: 12,
              border: '1px solid #F0F0F0',
              height: '100%',
            }}
            styles={{ body: { padding: '16px 12px' } }}
            hoverable
          >
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: 24, marginBottom: 8 }}>{metric.icon}</div>
              <div style={{ fontSize: 12, color: '#718096', marginBottom: 6 }}>
                {metric.title}
              </div>
              <div style={{ fontSize: 24, fontWeight: 600, color: '#2d3748', marginBottom: 4 }}>
                {metric.value}
              </div>
              <div
                style={{
                  fontSize: 12,
                  color: getChangeColor(metric.changeType),
                }}
              >
                {metric.changeType === 'positive' && '🔴 '}
                {metric.changeType === 'negative' && '🟢 '}
                {metric.change}
              </div>
            </div>
          </Card>
        </Col>
      ))}
    </Row>
  );
};

export default PerformanceMetricsGrid;
