/**
 * 动态平衡建议组件
 */

import React from 'react';
import { Card } from 'antd';

interface RebalanceItem {
  module: string;
  action: string;
  detail: string;
  amount: string;
  actionType: 'sell' | 'buy' | 'hold';
}

const RebalanceSuggestions: React.FC = () => {
  const suggestions: RebalanceItem[] = [
    {
      module: '🟦 成长模块',
      action: '卖出',
      detail: '超配5%，建议卖出部分成长股',
      amount: '¥61,728',
      actionType: 'sell',
    },
    {
      module: '🔵 全天候模块',
      action: '买入',
      detail: '低配5%，建议增持全天候资产',
      amount: '¥61,728',
      actionType: 'buy',
    },
    {
      module: '🟩 红利模块',
      action: '保持',
      detail: '配置正常，无需调整',
      amount: '-',
      actionType: 'hold',
    },
    {
      module: '🟦 固收模块',
      action: '保持',
      detail: '配置正常，无需调整',
      amount: '-',
      actionType: 'hold',
    },
  ];

  const getActionStyle = (actionType: string) => {
    switch (actionType) {
      case 'sell':
        return {
          background: 'rgba(245, 101, 101, 0.1)',
          color: '#f56565',
          label: '卖出',
        };
      case 'buy':
        return {
          background: 'rgba(72, 187, 120, 0.1)',
          color: '#48bb78',
          label: '买入',
        };
      case 'hold':
        return {
          background: 'rgba(113, 128, 150, 0.1)',
          color: '#718096',
          label: '保持',
        };
      default:
        return {
          background: 'transparent',
          color: '#718096',
          label: '',
        };
    }
  };

  return (
    <Card
      title={
        <span style={{ fontSize: 16, fontWeight: 600, color: '#2d3748' }}>
          ⚖️ 动态平衡建议
        </span>
      }
      style={{
        borderRadius: 16,
        boxShadow: '0 2px 12px rgba(0, 0, 0, 0.06)',
      }}
      styles={{ body: { padding: '16px' } }}
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: 0 }}>
        {suggestions.map((suggestion, index) => {
          const actionStyle = getActionStyle(suggestion.actionType);

          return (
            <div
              key={index}
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '12px 0',
                borderBottom: index === suggestions.length - 1 ? 'none' : '1px solid #e2e8f0',
              }}
            >
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 13, fontWeight: 600, color: '#2d3748', marginBottom: 4 }}>
                  {suggestion.module}
                </div>
                <div style={{ fontSize: 14, color: '#718096', marginBottom: 8 }}>
                  {suggestion.detail}
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <span
                  style={{
                    padding: '8px 16px',
                    background: actionStyle.background,
                    color: actionStyle.color,
                    borderRadius: 8,
                    fontSize: 13,
                    fontWeight: 600,
                    border: '1px solid',
                  }}
                >
                  {actionStyle.label}
                </span>
                {suggestion.amount !== '-' && (
                  <span style={{ fontSize: 16, fontWeight: 600, color: '#2d3748' }}>
                    {suggestion.amount}
                  </span>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </Card>
  );
};

export default RebalanceSuggestions;
