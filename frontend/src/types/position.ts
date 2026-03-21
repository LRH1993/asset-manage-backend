/**
 * 持仓相关类型定义
 */

export interface Position {
  id: number;
  symbol: string;
  name: string;
  module: 'dividend' | 'fixed' | 'growth' | 'allweather';
  market: 'CN' | 'US' | 'HK';
  assetType: 'stock' | 'etf' | 'fund' | 'bond';
  shares: number;
  avgCost: number;
  currentPrice: number;
  currentValue: number;
  targetWeight: number;
  buyPriceThreshold?: number;
  sellPriceThreshold?: number;
  status: 'active' | 'inactive';
  remarks?: string;
  createTime: string;
  updateTime: string;

  // 计算字段
  profit?: number;
  profitRate?: number;
  weight?: number;
}

export interface PositionRequest {
  symbol: string;
  name?: string;
  module: 'dividend' | 'fixed' | 'growth' | 'allweather';
  market: 'CN' | 'US' | 'HK';
  assetType: 'stock' | 'etf' | 'fund' | 'bond';
  shares: number;
  avgCost: number;
  targetWeight: number;
  buyPriceThreshold?: number;
  sellPriceThreshold?: number;
  remarks?: string;
}
