/**
 * 风险管理相关类型定义
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

export interface StressTestScenario {
  id: string;
  name: string;
  description: string;
  marketDrop: number;
  rateChange: number;
  currencyChange?: number;
  inflationChange?: number;
}

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
