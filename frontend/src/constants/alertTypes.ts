/**
 * 预警类型配置
 */

export const ALERT_TYPES = {
  value_buy: {
    key: 'value_buy',
    name: '价值买入',
    icon: '💎',
  },
  value_sell: {
    key: 'value_sell',
    name: '价值卖出',
    icon: '💰',
  },
  dividend: {
    key: 'dividend',
    name: '分红提醒',
    icon: '📅',
  },
  technical: {
    key: 'technical',
    name: '技术面',
    icon: '📊',
  },
  volatility: {
    key: 'volatility',
    name: '波动率',
    icon: '📉',
  },
  drawdown: {
    key: 'drawdown',
    name: '回撤',
    icon: '⬇️',
  },
  concentration: {
    key: 'concentration',
    name: '集中度',
    icon: '🎯',
  },
  correlation: {
    key: 'correlation',
    name: '相关性',
    icon: '🔗',
  },
};

export const ALERT_TYPE_LIST = Object.values(ALERT_TYPES);
