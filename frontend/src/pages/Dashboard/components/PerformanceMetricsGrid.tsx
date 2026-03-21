/**
 * 性能指标网格组件
 */

import React from 'react';
import { Card, Row, Col } from 'antd';

const PerformanceMetricsGrid: React.FC = () => {
  const metrics = [
    {
      icon: '📈',
      title: '累计收益率',
      value: '+23.5%',
      change: '+8.2%',
      changeType: 'positive' as 'positive' | 'warning' | 'negative',
      changeLabel: '跑赢基准',
    },
    {
      icon: '📅',
      title: '年化收益率',
      value: '+15.6%',
      change: '+13.2%',
      changeType: 'positive',
      changeLabel: '过去12个月',
    },
    {
      icon: '📊',
      title: '偏离度',
      value: '5.0%',
      change: '-5.0%',
      changeType: 'warning',
      changeLabel: '需要调仓',
    },
  ];

  return (
    <Row gutter={16} style={{ height: '100%' }}>
      {metrics.map((metric, index) => (
        <Col xs={8} sm={8} md={8} key={index}>
          <Card
            style={{
              borderRadius: 12,
              boxShadow: '0 2px 12px rgba(0, 0, 0, 0.06)',
              transition: 'transform 0.3s ease',
              height: '100%',
            }}
            bodyStyle={{ padding: '16px 12px' }}
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
                  color: metric.changeType === 'positive' ? '#48bb78' : metric.changeType === 'negative' ? '#f56565' : '#ecc94b',
                }}
              >
                {metric.changeType === 'positive' && '🟢 '}
                {metric.changeType === 'negative' && '🔴 '}
                {metric.changeLabel}
              </div>
            </div>
          </Card>
        </Col>
      ))}
    </Row>
  );
};

export default PerformanceMetricsGrid;
