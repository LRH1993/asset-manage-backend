/**
 * 交易类型配置
 */

export const TRANSACTION_TYPES = {
  buy: {
    key: 'buy',
    name: '买入',
    icon: '🟢',
  },
  sell: {
    key: 'sell',
    name: '卖出',
    icon: '🔴',
  },
};

export const TRANSACTION_TYPE_LIST = Object.values(TRANSACTION_TYPES);
