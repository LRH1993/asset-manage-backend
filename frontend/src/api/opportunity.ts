import api from './axios';

/**
 * 投资标的
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

/**
 * 机会提醒
 */
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

/**
 * 标的库查询参数
 */
export interface UniverseQuery {
  page?: number;
  pageSize?: number;
  module?: 'dividend' | 'fixed' | 'growth' | 'allweather';
  market?: 'CN' | 'US' | 'HK';
  assetType?: 'stock' | 'etf' | 'fund' | 'bond';
  status?: 'watching' | 'invested' | 'excluded';
  riskLevel?: 'low' | 'medium' | 'high';
  keyword?: string;
}

/**
 * 机会提醒查询参数
 */
export interface AlertQuery {
  page?: number;
  pageSize?: number;
  symbol?: string;
  alertType?: 'value_buy' | 'value_sell' | 'dividend' | 'technical';
  alertLevel?: 'info' | 'warning' | 'critical';
  isRead?: boolean;
}

/**
 * 获取投资标的库
 */
export const getInvestmentUniverse = (params?: UniverseQuery): Promise<{ list: InvestmentUniverse[]; total: number }> => {
  return api.get('/opportunity/universe', { params });
};

/**
 * 获取标的信息
 */
export const getUniverseDetail = (symbol: string): Promise<InvestmentUniverse> => {
  return api.get(`/opportunity/universe/${symbol}`);
};

/**
 * 获取机会提醒
 */
export const getOpportunityAlerts = (params?: AlertQuery): Promise<{ list: OpportunityAlert[]; total: number }> => {
  return api.get('/opportunity/alerts', { params });
};

/**
 * 标记提醒已读
 */
export const markAlertAsRead = (id: number): Promise<void> => {
  return api.put(`/opportunity/alerts/${id}/read`);
};

/**
 * 处理提醒
 */
export const processAlert = (id: number, result: string): Promise<void> => {
  return api.put(`/opportunity/alerts/${id}/process`, { result });
};
