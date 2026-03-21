/**
 * 资产类型配置
 */

export const ASSET_TYPES = {
  stock: {
    key: 'stock',
    name: '股票',
    icon: '📈',
  },
  etf: {
    key: 'etf',
    name: 'ETF',
    icon: '💹',
  },
  fund: {
    key: 'fund',
    name: '基金',
    icon: '💰',
  },
  bond: {
    key: 'bond',
    name: '债券',
    icon: '📊',
  },
};

export const ASSET_TYPE_LIST = Object.values(ASSET_TYPES);
