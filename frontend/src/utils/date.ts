/**
 * 日期工具函数
 */

import dayjs from 'dayjs';
import quarterOfYear from 'dayjs/plugin/quarterOfYear';

dayjs.extend(quarterOfYear);

/**
 * 获取今日日期字符串
 */
export const getToday = (): string => {
  return dayjs().format('YYYY-MM-DD');
};

/**
 * 获取当前日期时间字符串
 */
export const getNow = (): string => {
  return dayjs().format('YYYY-MM-DD HH:mm:ss');
};

/**
 * 获取昨天的日期
 */
export const getYesterday = (): string => {
  return dayjs().subtract(1, 'day').format('YYYY-MM-DD');
};

/**
 * 获取指定天数前的日期
 */
export const getDateBefore = (days: number): string => {
  return dayjs().subtract(days, 'day').format('YYYY-MM-DD');
};

/**
 * 获取月初日期
 */
export const getMonthStart = (): string => {
  return dayjs().startOf('month').format('YYYY-MM-DD');
};

/**
 * 获取月末日期
 */
export const getMonthEnd = (): string => {
  return dayjs().endOf('month').format('YYYY-MM-DD');
};

/**
 * 获取年初日期
 */
export const getYearStart = (): string => {
  return dayjs().startOf('year').format('YYYY-MM-DD');
};

/**
 * 获取年末日期
 */
export const getYearEnd = (): string => {
  return dayjs().endOf('year').format('YYYY-MM-DD');
};

/**
 * 获取季度起始日期
 */
export const getQuarterStart = (quarter?: number): string => {
  return dayjs().quarter(quarter || dayjs().quarter()).startOf('quarter').format('YYYY-MM-DD');
};

/**
 * 获取季度结束日期
 */
export const getQuarterEnd = (quarter?: number): string => {
  return dayjs().quarter(quarter || dayjs().quarter()).endOf('quarter').format('YYYY-MM-DD');
};

/**
 * 判断是否是工作日
 */
export const isWeekday = (date: string | Date): boolean => {
  const day = dayjs(date).day();
  return day >= 1 && day <= 5; // 1-5 表示周一到周五
};

/**
 * 获取下一个工作日
 */
export const getNextWeekday = (date: string | Date): string => {
  let nextDay = dayjs(date).add(1, 'day');
  while (!isWeekday(nextDay.toDate())) {
    nextDay = nextDay.add(1, 'day');
  }
  return nextDay.format('YYYY-MM-DD');
};

/**
 * 计算两个日期之间的天数
 */
export const getDaysBetween = (
  start: string | Date,
  end: string | Date
): number => {
  return dayjs(end).diff(dayjs(start), 'day');
};

/**
 * 计算两个日期之间的工作日天数
 */
export const getWeekdaysBetween = (
  start: string | Date,
  end: string | Date
): number => {
  let days = 0;
  let current = dayjs(start);
  const endDay = dayjs(end);

  while (current.isBefore(endDay) || current.isSame(endDay)) {
    if (isWeekday(current.toDate())) {
      days++;
    }
    current = current.add(1, 'day');
  }

  return days;
};

/**
 * 获取月份天数
 */
export const getDaysInMonth = (date: string | Date): number => {
  return dayjs(date).daysInMonth();
};

/**
 * 获取季度天数
 */
export const getDaysInQuarter = (year?: number, quarter?: number): number => {
  const start = dayjs().year(year || dayjs().year()).quarter(quarter || dayjs().quarter()).startOf('quarter');
  const end = dayjs().year(year || dayjs().year()).quarter(quarter || dayjs().quarter()).endOf('quarter');
  return end.diff(start, 'day') + 1;
};

/**
 * 获取年份天数
 */
export const getDaysInYear = (year?: number): number => {
  const targetYear = year || dayjs().year();
  // 闰年判断：能被4整除但不能被100整除，或者能被400整除
  const isLeapYear = (targetYear % 4 === 0 && targetYear % 100 !== 0) || (targetYear % 400 === 0);
  return isLeapYear ? 366 : 365;
};
