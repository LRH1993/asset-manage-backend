/**
 * 数字工具函数
 */

import Decimal from 'decimal.js';

/**
 * 四舍五入
 */
export const round = (value: number, decimals: number = 2): number => {
  return new Decimal(value).toDecimalPlaces(decimals).toNumber();
};

/**
 * 向上取整
 */
export const ceil = (value: number): number => {
  return Math.ceil(value);
};

/**
 * 向下取整
 */
export const floor = (value: number): number => {
  return Math.floor(value);
};

/**
 * 取绝对值
 */
export const abs = (value: number): number => {
  return Math.abs(value);
};

/**
 * 比较两个数字
 */
export const compare = (a: number, b: number): number => {
  return new Decimal(a).comparedTo(b);
};

/**
 * 判断是否相等
 */
export const isEqual = (a: number, b: number, precision: number = 6): boolean => {
  return new Decimal(a).toDecimalPlaces(precision).equals(new Decimal(b).toDecimalPlaces(precision));
};

/**
 * 判断是否大于
 */
export const isGreaterThan = (a: number, b: number): boolean => {
  return new Decimal(a).greaterThan(b);
};

/**
 * 判断是否大于等于
 */
export const isGreaterThanOrEqual = (a: number, b: number): boolean => {
  return new Decimal(a).greaterThanOrEqualTo(b);
};

/**
 * 判断是否小于
 */
export const isLessThan = (a: number, b: number): boolean => {
  return new Decimal(a).lessThan(b);
};

/**
 * 判断是否小于等于
 */
export const isLessThanOrEqual = (a: number, b: number): boolean => {
  return new Decimal(a).lessThanOrEqualTo(b);
};

/**
 * 判断是否为正数
 */
export const isPositive = (value: number): boolean => {
  return value > 0;
};

/**
 * 判断是否为负数
 */
export const isNegative = (value: number): boolean => {
  return value < 0;
};

/**
 * 判断是否为零
 */
export const isZero = (value: number, precision: number = 6): boolean => {
  return new Decimal(value).abs().toDecimalPlaces(precision).equals(0);
};

/**
 * 获取两个数之间的随机数
 */
export const random = (min: number, max: number): number => {
  return Math.random() * (max - min) + min;
};

/**
 * 获取随机整数
 */
export const randomInt = (min: number, max: number): number => {
  return Math.floor(random(min, max + 1));
};

/**
 * 将数值限制在范围内
 */
export const clamp = (value: number, min: number, max: number): number => {
  return Math.min(Math.max(value, min), max);
};

/**
 * 线性插值
 */
export const lerp = (start: number, end: number, t: number): number => {
  return start + (end - start) * t;
};

/**
 * 映射范围
 */
export const mapRange = (
  value: number,
  inMin: number,
  inMax: number,
  outMin: number,
  outMax: number
): number => {
  return ((value - inMin) * (outMax - outMin)) / (inMax - inMin) + outMin;
};

/**
 * 计算百分比
 */
export const percent = (value: number, total: number): number => {
  if (total === 0) return 0;
  return (value / total) * 100;
};

/**
 * 计算数值的百分比
 */
export const percentOf = (percent: number, total: number): number => {
  return (percent / 100) * total;
};

/**
 * 计算增长率
 */
export const growthRate = (oldValue: number, newValue: number): number => {
  if (oldValue === 0) return 0;
  return ((newValue - oldValue) / oldValue) * 100;
};

/**
 * 计算总和
 */
export const sum = (values: number[]): number => {
  return values.reduce((acc, val) => acc + val, 0);
};

/**
 * 计算平均值
 */
export const average = (values: number[]): number => {
  if (values.length === 0) return 0;
  return sum(values) / values.length;
};

/**
 * 计算中位数
 */
export const median = (values: number[]): number => {
  if (values.length === 0) return 0;

  const sorted = [...values].sort((a, b) => a - b);
  const mid = Math.floor(sorted.length / 2);

  if (sorted.length % 2 === 0) {
    return (sorted[mid - 1] + sorted[mid]) / 2;
  }

  return sorted[mid];
};

/**
 * 计算最大值
 */
export const max = (values: number[]): number => {
  if (values.length === 0) return 0;
  return Math.max(...values);
};

/**
 * 计算最小值
 */
export const min = (values: number[]): number => {
  if (values.length === 0) return 0;
  return Math.min(...values);
};

/**
 * 获取最大值的索引
 */
export const maxIndex = (values: number[]): number => {
  if (values.length === 0) return -1;
  return values.indexOf(max(values));
};

/**
 * 获取最小值的索引
 */
export const minIndex = (values: number[]): number => {
  if (values.length === 0) return -1;
  return values.indexOf(min(values));
};
