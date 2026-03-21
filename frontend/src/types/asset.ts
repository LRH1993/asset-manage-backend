/**
 * 资产相关类型定义
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

export interface CoreMetrics {
  maxDrawdown: number;
  sharpeRatio: number;
  volatility30d: number;
  volatility90d: number;
  concentrationRisk: number;
  var95: number;
  var99: number;
}

export interface ModuleAllocation {
  dividend: ModuleInfo;
  fixed: ModuleInfo;
  growth: ModuleInfo;
  allweather: ModuleInfo;
}

export interface ModuleInfo {
  name: string;
  targetWeight: number;
  currentValue: number;
  currentWeight: number;
  deviation: number;
  return: number;
}

export interface ReturnCurveData {
  date: string;
  value: number;
  returnRate: number;
  benchmark?: number;
}
