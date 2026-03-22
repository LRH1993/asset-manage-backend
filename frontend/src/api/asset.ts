import api from './axios';

/**
 * 资产总览
 */
export interface AssetOverview {
  totalValue: number;
  todayProfit: number;
  todayProfitRate: number;
  totalProfit: number;
  totalProfitRate: number;
  annualReturn: number;
  benchmarkComparison: number;
}

/**
 * 核心指标
 */
export interface CoreMetrics {
  maxDrawdown: number;
  sharpeRatio: number;
  volatility30d: number;
  volatility90d: number;
  concentrationRisk: number;
  var95: number;
  var99: number;
}

/**
 * 模块配置
 */
export interface ModuleAllocation {
  dividend: ModuleInfo;
  fixed: ModuleInfo;
  growth: ModuleInfo;
  allweather: ModuleInfo;
}

export interface ModuleInfo {
  name: string;
  moduleCode?: string;
  targetWeight: number;
  currentValue: number;
  currentWeight: number;
  deviation: number;
  return?: number;
  returnRate?: number;
}

/**
 * 收益曲线数据
 */
export interface ReturnCurveData {
  date: string;
  value: number;
  returnRate: number;
  benchmark?: number;
}

/**
 * 调仓建议
 */
export interface RebalanceSuggestion {
  needRebalance: boolean;
  totalDeviation: number;
  suggestions: SuggestionItem[];
}

export interface SuggestionItem {
  moduleCode: string;
  moduleName: string;
  currentWeight: number;
  targetWeight: number;
  deviation: number;
  action: 'buy' | 'sell';
  amount: number;
  description: string;
}

/**
 * 获取资产总览
 */
export const getAssetOverview = (): Promise<AssetOverview> => {
  return api.get('/assets/overview');
};

/**
 * 获取核心指标
 */
export const getCoreMetrics = (): Promise<CoreMetrics> => {
  return api.get('/assets/metrics');
};

/**
 * 获取资产配置
 */
export const getAssetAllocation = (): Promise<ModuleAllocation> => {
  return api.get('/assets/allocation');
};

/**
 * 获取收益曲线
 */
export const getReturnCurve = (period: '7d' | '30d' | '90d' | '1y' | 'all' = 'all'): Promise<ReturnCurveData[]> => {
  return api.get('/assets/return-curve', { params: { period } });
};

/**
 * 获取调仓建议
 */
export const getRebalanceSuggestions = (): Promise<RebalanceSuggestion> => {
  return api.get('/assets/rebalance-suggestions');
};
