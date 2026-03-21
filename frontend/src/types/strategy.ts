/**
 * 策略分析相关类型定义
 */

export interface Strategy {
  id: number;
  name: string;
  key: 'dividend' | 'fixed' | 'growth' | 'allweather';
  description: string;
  params: Record<string, any>;
  enabled: boolean;
  createTime: string;
  updateTime: string;
}

export interface BacktestConfig {
  strategyId: number;
  startDate: string;
  endDate: string;
  benchmark?: string;
  initialCapital: number;
  fee: number;
}

export interface BacktestResult {
  backtestId: string;
  config: BacktestConfig;
  metrics: {
    totalReturn: number;
    annualReturn: number;
    maxDrawdown: number;
    sharpeRatio: number;
    sortinoRatio: number;
    winRate: number;
    profitLossRatio: number;
  };
  equityCurve: Array<{
    date: string;
    value: number;
    benchmark?: number;
  }>;
  monthlyReturns: Array<{
    year: number;
    month: number;
    return: number;
  }>;
  trades: Array<{
    date: string;
    symbol: string;
    action: 'buy' | 'sell';
    shares: number;
    price: number;
    amount: number;
  }>;
}
