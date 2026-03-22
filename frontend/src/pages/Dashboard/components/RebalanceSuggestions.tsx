/**
 * 动态平衡建议组件
 * 遵循 ui/CLAUDE.md 设计规范
 */

import React from 'react';
import { Card } from 'antd';
import { PROFIT_COLORS, NEUTRAL_COLORS } from '@/constants/colors';
import { useAssetStore } from '@/stores/assetStore';
import { formatMoney } from '@/utils/format';

interface RebalanceItem {
  module: string;
  action: string;
  detail: string;
  amount: string;
  actionType: 'sell' | 'buy' | 'hold';
}

const RebalanceSuggestions: React.FC = () => {
  const { rebalanceSuggestion, allocation } = useAssetStore();

  // 从store获取真实数据
  const suggestions: RebalanceItem[] = [];

  if (rebalanceSuggestion?.suggestions && rebalanceSuggestion.suggestions.length > 0) {
    rebalanceSuggestion.suggestions.forEach(item => {
      suggestions.push({
        module: item.moduleName,
        action: item.action === 'buy' ? '买入' : '卖出',
        detail: item.description,
        amount: `¥${formatMoney(item.amount)}`,
        actionType: item.action,
      });
    });
  }

  // 添加保持状态的模块
  if (allocation) {
    const moduleNames: Record<string, string> = {
      dividend: '红利模块',
      fixed: '固收模块',
      growth: '成长模块',
      allweather: '全天候模块',
    };

    const existingModules = new Set(rebalanceSuggestion?.suggestions?.map(s => s.moduleCode) || []);

    ['dividend', 'fixed', 'growth', 'allweather'].forEach(key => {
      if (!existingModules.has(key) && allocation[key as keyof typeof allocation]) {
        suggestions.push({
          module: moduleNames[key],
          action: '保持',
          detail: '配置正常，无需调整',
          amount: '-',
          actionType: 'hold',
        });
      }
    });
  }

  // 如果没有建议，显示空状态
  if (suggestions.length === 0) {
    suggestions.push({
      module: '资产配置',
      action: '保持',
      detail: '当前配置符合目标，无需调仓',
      amount: '-',
      actionType: 'hold',
    });
  }

  // A股习惯：红涨绿跌
  // 买入 = 增加持仓 = 红色
  // 卖出 = 减少持仓 = 绿色
  const getActionStyle = (actionType: string) => {
    switch (actionType) {
      case 'sell':
        return {
          background: 'rgba(63, 134, 0, 0.1)',
          color: PROFIT_COLORS.down,    // 绿色
          label: '卖出',
        };
      case 'buy':
        return {
          background: 'rgba(207, 19, 34, 0.1)',
          color: PROFIT_COLORS.up,      // 红色
          label: '买入',
        };
      case 'hold':
        return {
          background: 'rgba(140, 140, 140, 0.1)',
          color: NEUTRAL_COLORS.textTertiary,
          label: '保持',
        };
      default:
        return {
          background: 'transparent',
          color: NEUTRAL_COLORS.textTertiary,
          label: '',
        };
    }
  };

  return (
    <Card
      title={
        <span style={{ fontSize: 16, fontWeight: 600, color: NEUTRAL_COLORS.textPrimary }}>
          ⚖️ 动态平衡建议
        </span>
      }
      style={{
        borderRadius: 12,
        border: '1px solid #F0F0F0',
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
                borderBottom: index === suggestions.length - 1 ? 'none' : '1px solid #F0F0F0',
              }}
            >
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 14, fontWeight: 500, color: NEUTRAL_COLORS.textPrimary, marginBottom: 4 }}>
                  {suggestion.module}
                </div>
                <div style={{ fontSize: 13, color: NEUTRAL_COLORS.textSecondary }}>
                  {suggestion.detail}
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <span
                  style={{
                    padding: '6px 14px',
                    background: actionStyle.background,
                    color: actionStyle.color,
                    borderRadius: 4,
                    fontSize: 13,
                    fontWeight: 500,
                  }}
                >
                  {actionStyle.label}
                </span>
                {suggestion.amount !== '-' && (
                  <span style={{ fontSize: 15, fontWeight: 500, color: NEUTRAL_COLORS.textPrimary }}>
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
