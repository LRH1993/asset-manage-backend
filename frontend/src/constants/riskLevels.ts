/**
 * 风险等级配置
 */

export const RISK_LEVELS = {
  low: {
    key: 'low',
    name: '低风险',
    color: '#22c55e',
    icon: '🛡️',
  },
  medium: {
    key: 'medium',
    name: '中风险',
    color: '#eab308',
    icon: '⚠️',
  },
  high: {
    key: 'high',
    name: '高风险',
    color: '#ef4444',
    icon: '🚨',
  },
};

export const RISK_LEVEL_LIST = Object.values(RISK_LEVELS);
