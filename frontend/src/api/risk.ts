import api from './axios';

/**
 * 风险指标
 */
export interface RiskMetrics {
  metricDate: string;
  totalValue: number;
  dailyReturn: number;
  volatility30d: number;
  volatility90d: number;
  maxDrawdown: number;
  sharpeRatio: number;
  sortinoRatio: number;
  var95: number;
  var99: number;
  beta: number;
  alpha: number;
  correlationMatrix?: Record<string, Record<string, number>>;
  sectorDistribution?: Record<string, number>;
}

/**
 * 压力测试场景
 */
export interface StressTestScenario {
  id: string;
  name: string;
  description: string;
  marketDrop: number;
  rateChange: number;
  currencyChange?: number;
  inflationChange?: number;
}

/**
 * 压力测试结果
 */
export interface StressTestResult {
  scenarioId: string;
  scenarioName: string;
  estimatedLoss: number;
  estimatedLossRate: number;
  moduleImpact: Record<string, number>;
  positionImpact: Record<string, number>;
  sensitivityAnalysis: {
    marketSensitivity: number;
    rateSensitivity: number;
    currencySensitivity: number;
  };
}

/**
 * 风险预警
 */
export interface RiskAlert {
  id: number;
  alertType: 'volatility' | 'drawdown' | 'concentration' | 'correlation';
  severity: 'info' | 'warning' | 'critical';
  message: string;
  currentValue: number;
  threshold: number;
  isRead: boolean;
  createTime: string;
}

/**
 * 获取风险指标
 */
export const getRiskMetrics = (date?: string): Promise<RiskMetrics> => {
  return api.get('/risk/metrics', { params: { date } });
};

/**
 * 获取风险仪表盘数据
 */
export const getRiskDashboard = (): Promise<{
  metrics: RiskMetrics;
  alerts: RiskAlert[];
  riskLevel: 'low' | 'medium' | 'high';
}> => {
  return api.get('/risk/dashboard');
};

/**
 * 执行压力测试
 */
export const executeStressTest = (scenario: Partial<StressTestScenario>): Promise<StressTestResult> => {
  return api.post('/risk/stress-test', scenario);
};

/**
 * 获取相关性分析
 */
export const getCorrelationMatrix = (): Promise<Record<string, Record<string, number>>> => {
  return api.get('/risk/correlation');
};

/**
 * 获取风险预警列表
 */
export const getRiskAlerts = (): Promise<RiskAlert[]> => {
  return api.get('/risk/alerts');
};
