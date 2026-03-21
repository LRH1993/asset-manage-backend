/**
 * 市场配置
 */

import type { MarketConfig } from '@/types/common';

export const MARKETS: Record<string, MarketConfig> = {
  CN: {
    key: 'CN',
    name: 'A股',
    icon: '🇨🇳',
    currency: 'CNY',
  },
  US: {
    key: 'US',
    name: '美股',
    icon: '🇺🇸',
    currency: 'USD',
  },
  HK: {
    key: 'HK',
    name: '港股',
    icon: '🇭🇰',
    currency: 'HKD',
  },
};

export const MARKET_LIST = Object.values(MARKETS);
