/**
 * 格式化工具函数
 * 遵循 ui/CLAUDE.md 设计规范
 */

import dayjs from 'dayjs';
import Decimal from 'decimal.js';
import { PROFIT_COLORS } from '@/constants/colors';

/**
 * 格式化货币
 * @description 大额数字自动转换单位（万/亿）
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
 * 格式化金额（智能单位转换）
 * @description 大于1万显示"万"，大于1亿显示"亿"
 */
export const formatMoney = (value: number | string): string => {
  const num = new Decimal(value);
  const abs = num.abs();

  if (abs.gte(100000000)) {
    return `${num.div(100000000).toFixed(2)}亿`;
  }
  if (abs.gte(10000)) {
    return `${num.div(10000).toFixed(2)}万`;
  }
  return num.toFixed(2);
};

/**
 * 格式化金额（千分位）
 * @description 用于表格精确显示
 */
export const formatMoneyWithComma = (value: number | string): string => {
  const num = new Decimal(value);
  return num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

/**
 * 格式化百分比
 * @description 自动添加正负号
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
 * 格式化股数/份额（千分位）
 */
export const formatShares = (value: number | string, decimal = 0): string => {
  const num = new Decimal(value);
  return num.toFixed(decimal).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
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
 * 获取盈亏颜色（A股习惯：红涨绿跌）
 * @param value 数值
 * @returns 颜色值
 */
export const getProfitColor = (value: number): string => {
  if (value > 0) return PROFIT_COLORS.up;      // #CF1322 红色
  if (value < 0) return PROFIT_COLORS.down;    // #3F8600 绿色
  return PROFIT_COLORS.neutral;                 // #8C8C8C 灰色
};

/**
 * 格式化盈亏（带颜色）
 * @description A股习惯：红涨绿跌
 */
export const formatProfitLoss = (
  value: number,
  decimals: number = 2
): { text: string; color: string } => {
  return {
    text: formatNumber(value, decimals),
    color: getProfitColor(value),
  };
};

/**
 * 格式化盈亏率（带颜色）
 * @description A股习惯：红涨绿跌
 */
export const formatProfitRate = (
  value: number,
  decimals: number = 2
): { text: string; color: string } => {
  return {
    text: formatPercent(value, decimals),
    color: getProfitColor(value),
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
