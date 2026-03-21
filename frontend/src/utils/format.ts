/**
 * 格式化工具函数
 */

import dayjs from 'dayjs';
import Decimal from 'decimal.js';

/**
 * 格式化货币
 */
export const formatCurrency = (
  value: number | string,
  currency: string = 'CNY',
  decimals: number = 2
): string => {
  const num = new Decimal(value);
  return `${num.toFixed(decimals)} ${currency}`;
};

/**
 * 格式化百分比
 */
export const formatPercent = (
  value: number | string,
  decimals: number = 2,
  showSign: boolean = true
): string => {
  const num = new Decimal(value);
  const formatted = num.toFixed(decimals);
  const sign = showSign && num.greaterThan(0) ? '+' : '';
  return `${sign}${formatted}%`;
};

/**
 * 格式化数字（添加千分位）
 */
export const formatNumber = (
  value: number | string,
  decimals: number = 2
): string => {
  const num = new Decimal(value);
  return num.toNumber().toLocaleString('zh-CN', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  });
};

/**
 * 格式化日期
 */
export const formatDate = (
  date: string | Date,
  format: string = 'YYYY-MM-DD'
): string => {
  return dayjs(date).format(format);
};

/**
 * 格式化日期时间
 */
export const formatDateTime = (
  date: string | Date,
  format: string = 'YYYY-MM-DD HH:mm:ss'
): string => {
  return dayjs(date).format(format);
};

/**
 * 格式化时间差
 */
export const formatRelativeTime = (date: string | Date): string => {
  const now = dayjs();
  const target = dayjs(date);
  const diff = now.diff(target, 'second');

  if (diff < 60) {
    return '刚刚';
  } else if (diff < 3600) {
    return `${Math.floor(diff / 60)}分钟前`;
  } else if (diff < 86400) {
    return `${Math.floor(diff / 3600)}小时前`;
  } else if (diff < 2592000) {
    return `${Math.floor(diff / 86400)}天前`;
  } else if (diff < 31536000) {
    return `${Math.floor(diff / 2592000)}个月前`;
  } else {
    return `${Math.floor(diff / 31536000)}年前`;
  }
};

/**
 * 格式化股票代码
 */
export const formatStockCode = (code: string, market: string): string => {
  if (market === 'CN') {
    if (code.startsWith('6')) {
      return `SH${code}`;
    } else if (code.startsWith('0') || code.startsWith('3')) {
      return `SZ${code}`;
    }
  } else if (market === 'US') {
    return code.toUpperCase();
  } else if (market === 'HK') {
    return code.padStart(5, '0');
  }
  return code;
};

/**
 * 格式化盈亏（带颜色）
 */
export const formatProfitLoss = (
  value: number,
  decimals: number = 2
): { text: string; color: string } => {
  const isPositive = value > 0;
  const isNegative = value < 0;

  let color = 'text-gray-500';
  if (isPositive) color = 'text-green-500';
  if (isNegative) color = 'text-red-500';

  return {
    text: formatNumber(value, decimals),
    color,
  };
};

/**
 * 格式化盈亏率（带颜色）
 */
export const formatProfitRate = (
  value: number,
  decimals: number = 2
): { text: string; color: string } => {
  const isPositive = value > 0;
  const isNegative = value < 0;

  let color = 'text-gray-500';
  if (isPositive) color = 'text-green-500';
  if (isNegative) color = 'text-red-500';

  return {
    text: formatPercent(value, decimals),
    color,
  };
};

/**
 * 格式化大数字
 */
export const formatLargeNumber = (value: number): string => {
  if (value >= 100000000) {
    return `${(value / 100000000).toFixed(2)}亿`;
  } else if (value >= 10000) {
    return `${(value / 10000).toFixed(2)}万`;
  }
  return formatNumber(value);
};
