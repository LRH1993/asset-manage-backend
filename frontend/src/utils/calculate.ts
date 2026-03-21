/**
 * 计算工具函数
 */

import Decimal from 'decimal.js';

/**
 * 计算收益率
 */
export const calculateReturnRate = (
  current: number,
  cost: number
): number => {
  if (cost === 0) return 0;
  return new Decimal(current)
    .minus(cost)
    .div(cost)
    .mul(100)
    .toNumber();
};

/**
 * 计算盈亏
 */
export const calculateProfit = (
  current: number,
  cost: number,
  shares: number
): number => {
  return new Decimal(current)
    .minus(cost)
    .mul(shares)
    .toNumber();
};

/**
 * 计算权重
 */
export const calculateWeight = (
  value: number,
  total: number
): number => {
  if (total === 0) return 0;
  return new Decimal(value).div(total).mul(100).toNumber();
};

/**
 * 计算偏离度
 */
export const calculateDeviation = (
  actual: number,
  target: number
): number => {
  if (target === 0) return 0;
  return new Decimal(actual).minus(target).div(target).mul(100).toNumber();
};

/**
 * 计算累计收益率
 */
export const calculateCumulativeReturn = (
  values: number[]
): number => {
  if (values.length < 2) return 0;
  const start = values[0];
  const end = values[values.length - 1];
  return calculateReturnRate(end, start);
};

/**
 * 计算年化收益率
 */
export const calculateAnnualReturn = (
  cumulativeReturn: number,
  days: number
): number => {
  if (days === 0) return 0;
  const dailyRate = new Decimal(1).plus(cumulativeReturn).pow(1 / days).minus(1);
  return dailyRate.mul(365).mul(100).toNumber();
};

/**
 * 计算最大回撤
 */
export const calculateMaxDrawdown = (
  values: number[]
): number => {
  if (values.length === 0) return 0;

  let maxDrawdown = 0;
  let peak = values[0];

  for (let i = 1; i < values.length; i++) {
    if (values[i] > peak) {
      peak = values[i];
    } else {
      const drawdown = new Decimal(peak).minus(values[i]).div(peak).mul(100);
      if (drawdown.toNumber() > maxDrawdown) {
        maxDrawdown = drawdown.toNumber();
      }
    }
  }

  return maxDrawdown;
};

/**
 * 计算波动率（标准差）
 */
export const calculateVolatility = (
  returns: number[],
  annualize: boolean = true
): number => {
  if (returns.length < 2) return 0;

  const mean = returns.reduce((sum, r) => sum + r, 0) / returns.length;
  const variance =
    returns.reduce((sum, r) => sum + Math.pow(r - mean, 2), 0) /
    (returns.length - 1);
  const stdDev = Math.sqrt(variance);

  return annualize ? stdDev * Math.sqrt(252) : stdDev;
};

/**
 * 计算夏普比率
 */
export const calculateSharpeRatio = (
  returns: number[],
  riskFreeRate: number = 0.03
): number => {
  if (returns.length < 2) return 0;

  const annualizedReturn = returns.reduce((sum, r) => sum + r, 0) * 252;
  const volatility = calculateVolatility(returns, true);

  if (volatility === 0) return 0;

  return (annualizedReturn - riskFreeRate) / volatility;
};

/**
 * 计算相关性
 */
export const calculateCorrelation = (
  x: number[],
  y: number[]
): number => {
  if (x.length !== y.length || x.length < 2) return 0;

  const n = x.length;
  const meanX = x.reduce((sum, val) => sum + val, 0) / n;
  const meanY = y.reduce((sum, val) => sum + val, 0) / n;

  let numerator = 0;
  let denominatorX = 0;
  let denominatorY = 0;

  for (let i = 0; i < n; i++) {
    const diffX = x[i] - meanX;
    const diffY = y[i] - meanY;
    numerator += diffX * diffY;
    denominatorX += diffX * diffX;
    denominatorY += diffY * diffY;
  }

  const denominator = Math.sqrt(denominatorX * denominatorY);
  return denominator === 0 ? 0 : numerator / denominator;
};
