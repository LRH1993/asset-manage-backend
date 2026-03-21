import api from './axios';

/**
 * 策略信息
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

/**
 * 回测配置
 */
export interface BacktestConfig {
  strategyId: number;
  startDate: string;
  endDate: string;
  benchmark?: string;
  initialCapital: number;
  fee: number;
}

/**
 * 回测结果
 */
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

/**
 * 获取策略列表
 */
export const getStrategies = (): Promise<Strategy[]> => {
  return api.get('/strategy/list');
};

/**
 * 获取策略详情
 */
export const getStrategyDetail = (id: number): Promise<Strategy> => {
  return api.get(`/strategy/${id}`);
};

/**
 * 更新策略参数
 */
export const updateStrategyParams = (id: number, params: Record<string, any>): Promise<void> => {
  return api.put(`/strategy/${id}/params`, { params });
};

/**
 * 执行回测
 */
export const executeBacktest = (config: BacktestConfig): Promise<BacktestResult> => {
  return api.post('/strategy/backtest', config);
};

/**
 * 策略对比
 */
export const compareStrategies = (strategyIds: number[]): Promise<BacktestResult[]> => {
  return api.post('/strategy/compare', { strategyIds });
};
