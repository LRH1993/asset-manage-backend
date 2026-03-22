/**
 * 总资产卡片组件
 * 遵循 ui/CLAUDE.md 设计规范
 */

import React from 'react';
import { Card } from 'antd';
import { ArrowUpOutlined, ArrowDownOutlined } from '@ant-design/icons';
import { formatMoney, getProfitColor, formatPercent } from '@/utils/format';

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

  // 使用统一的涨跌色（A股习惯：红涨绿跌）
  const todayColor = getProfitColor(todayProfit);
  const totalColor = getProfitColor(totalProfit);

  return (
    <Card
      style={{
        textAlign: 'center',
        padding: '20px 24px',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        borderRadius: 12,
        border: 'none',
        height: '100%',
      }}
      styles={{ body: { padding: '16px 20px' } }}
    >
      <div style={{ color: 'white' }}>
        <div style={{ fontSize: 14, opacity: 0.9, marginBottom: 8, letterSpacing: 1 }}>
          总资产
        </div>
        <div style={{ fontSize: 42, fontWeight: 600, marginBottom: 12 }}>
          {formatMoney(totalValue)}
        </div>
        <div style={{ display: 'flex', justifyContent: 'center', gap: 24, fontSize: 14 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <span style={{ opacity: 0.85 }}>今日:</span>
            <span style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              {isTodayProfitPositive ? <ArrowUpOutlined /> : <ArrowDownOutlined />}
              <span style={{ color: todayColor, fontSize: 15 }}>
                {isTodayProfitPositive ? '+' : ''}{formatMoney(Math.abs(todayProfit))}
              </span>
            </span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <span style={{ opacity: 0.85 }}>累计:</span>
            <span style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              {isTotalProfitPositive ? <ArrowUpOutlined /> : <ArrowDownOutlined />}
              <span style={{ color: totalColor, fontSize: 15 }}>
                {isTotalProfitPositive ? '+' : ''}{formatMoney(Math.abs(totalProfit))}
              </span>
            </span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <span style={{ opacity: 0.85 }}>年化:</span>
            <span style={{ color: getProfitColor(annualReturn), fontSize: 15 }}>
              {formatPercent(annualReturn)}
            </span>
          </div>
        </div>
      </div>
    </Card>
  );
};

export default TotalAssetCard;
