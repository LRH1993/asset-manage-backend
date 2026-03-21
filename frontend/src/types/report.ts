/**
 * 报表相关类型定义
 */

export interface ReportConfig {
  type: 'monthly' | 'quarterly' | 'yearly' | 'custom';
  year: number;
  month?: number;
  quarter?: number;
  startDate?: string;
  endDate?: string;
}

export interface ReportData {
  title: string;
  period: string;
  summary: {
    totalValue: number;
    periodReturn: number;
    periodReturnRate: number;
    benchmarkReturn: number;
    alpha: number;
  };
  performance: {
    dailyReturns: Array<{ date: string; return: number }>;
    monthlyReturns: Array<{ month: string; return: number }>;
    drawdowns: Array<{ date: string; drawdown: number }>;
  };
  risk: {
    volatility: number;
    maxDrawdown: number;
    sharpeRatio: number;
    var95: number;
  };
  allocation: {
    dividend: { target: number; actual: number; return: number };
    fixed: { target: number; actual: number; return: number };
    growth: { target: number; actual: number; return: number };
    allweather: { target: number; actual: number; return: number };
  };
  topPositions: Array<{
    symbol: string;
    name: string;
    value: number;
    weight: number;
    return: number;
  }>;
  trades: {
    buyCount: number;
    sellCount: number;
    totalVolume: number;
    totalFee: number;
  };
}
