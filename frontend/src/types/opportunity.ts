/**
 * 机会发现相关类型定义
 */

export interface InvestmentUniverse {
  id: number;
  symbol: string;
  name: string;
  module: 'dividend' | 'fixed' | 'growth' | 'allweather';
  market: 'CN' | 'US' | 'HK';
  assetType: 'stock' | 'etf' | 'fund' | 'bond';
  riskLevel: 'low' | 'medium' | 'high';
  dividendYield?: number;
  peRatio?: number;
  pbRatio?: number;
  psRatio?: number;
  marketCap?: number;
  expenseRatio?: number;
  valuationPercentile?: number;
  dividendGrowthRate?: number;
  revenueGrowthRate?: number;
  profitGrowthRate?: number;
  roe?: number;
  fcfYield?: number;
  status: 'watching' | 'invested' | 'excluded';
  priority: number;
  tags?: string[];
  lastReviewedAt?: string;
  createTime: string;
  updateTime: string;
}

export interface OpportunityAlert {
  id: number;
  symbol: string;
  alertType: 'value_buy' | 'value_sell' | 'dividend' | 'technical';
  alertContent: string;
  alertLevel: 'info' | 'warning' | 'critical';
  currentValue?: number;
  targetValue?: number;
  isRead: boolean;
  isProcessed: boolean;
  processResult?: string;
  createTime: string;
}
