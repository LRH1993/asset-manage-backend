/**
 * 总资产卡片组件
 */

import React from 'react';
import { Card } from 'antd';
import { ArrowUpOutlined, ArrowDownOutlined } from '@ant-design/icons';
import { formatCurrency, formatPercent } from '@/utils/format';

interface TotalAssetCardProps {
  totalValue: number;
  todayProfit: number;
  totalProfit: number;
  annualReturn: number;
}

const TotalAssetCard: React.FC<TotalAssetCardProps> = ({
  totalValue,
  todayProfit,
  totalProfit,
  annualReturn,
}) => {
  const isTodayProfitPositive = todayProfit >= 0;
  const isTotalProfitPositive = totalProfit >= 0;

  return (
    <Card
      style={{
        textAlign: 'center',
        padding: '20px 24px',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        borderRadius: 16,
        boxShadow: '0 4px 20px rgba(102, 126, 234, 0.3)',
        height: '100%',
      }}
      bodyStyle={{ padding: '16px 20px' }}
    >
      <div style={{ color: 'white' }}>
        <div style={{ fontSize: 14, opacity: 0.9, marginBottom: 8, letterSpacing: 1 }}>
          总资产
        </div>
        <div style={{ fontSize: 42, fontWeight: 'bold', marginBottom: 12, textShadow: '0 2px 8px rgba(0, 0, 0, 0.2)' }}>
          {formatCurrency(totalValue)}
        </div>
        <div style={{ display: 'flex', justifyContent: 'center', gap: 24, fontSize: 14 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <span style={{ opacity: 0.85 }}>今日:</span>
            <span style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              {isTodayProfitPositive ? <ArrowUpOutlined /> : <ArrowDownOutlined />}
              <span style={{ color: isTodayProfitPositive ? '#68d391' : '#fc8181', fontSize: 15 }}>
                {isTodayProfitPositive ? '+' : ''}{formatCurrency(Math.abs(todayProfit))}
              </span>
            </span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <span style={{ opacity: 0.85 }}>累计:</span>
            <span style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              {isTotalProfitPositive ? <ArrowUpOutlined /> : <ArrowDownOutlined />}
              <span style={{ color: isTotalProfitPositive ? '#68d391' : '#fc8181', fontSize: 15 }}>
                {isTotalProfitPositive ? '+' : ''}{formatCurrency(Math.abs(totalProfit))}
              </span>
            </span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <span style={{ opacity: 0.85 }}>年化:</span>
            <span style={{ color: '#68d391', fontSize: 15 }}>
              {formatPercent(annualReturn)}
            </span>
          </div>
        </div>
      </div>
    </Card>
  );
};

export default TotalAssetCard;
