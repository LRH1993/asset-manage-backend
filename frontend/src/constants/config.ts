/**
 * 配置常量
 */

export const CONFIG = {
  // API配置
  API_TIMEOUT: 30000,
  API_RETRY_TIMES: 3,

  // 分页配置
  DEFAULT_PAGE_SIZE: 20,
  PAGE_SIZE_OPTIONS: [10, 20, 50, 100],

  // 表格配置
  TABLE_SCROLL_X: 1200,

  // 图表配置
  CHART_HEIGHT: 400,
  CHART_COLORS: {
    dividend: '#22c55e',
    fixed: '#3b82f6',
    growth: '#8b5cf6',
    allweather: '#f97316',
    profit: '#22c55e',
    loss: '#ef4444',
  },

  // 格式化配置
  DECIMAL_PLACES: 2,
  PERCENT_DECIMAL_PLACES: 2,
  CURRENCY_DECIMAL_PLACES: 2,

  // 刷新配置
  REFRESH_INTERVAL: 60000, // 1分钟
  AUTO_REFRESH_ENABLED: false,

  // 存储配置
  STORAGE_PREFIX: 'asset_',
};
