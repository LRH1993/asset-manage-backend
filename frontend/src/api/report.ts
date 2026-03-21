import api from './axios';
import type { ApiResponse } from './types';

/**
 * 报表配置
 */
export interface ReportConfig {
  type: 'monthly' | 'quarterly' | 'yearly' | 'custom';
  year: number;
  month?: number;
  quarter?: number;
  startDate?: string;
  endDate?: string;
}

/**
 * 报表数据
 */
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

/**
 * 生成月度报告
 */
export const generateMonthlyReport = (year: number, month: number): Promise<ApiResponse<ReportData>> => {
  return api.get(`/report/monthly/${year}/${month}`);
};

/**
 * 生成季度报告
 */
export const generateQuarterlyReport = (year: number, quarter: 1 | 2 | 3 | 4): Promise<ApiResponse<ReportData>> => {
  return api.get(`/report/quarterly/${year}/${quarter}`);
};

/**
 * 生成年度报告
 */
export const generateYearlyReport = (year: number): Promise<ApiResponse<ReportData>> => {
  return api.get(`/report/yearly/${year}`);
};

/**
 * 生成自定义报表
 */
export const generateCustomReport = (config: ReportConfig): Promise<ApiResponse<ReportData>> => {
  return api.post('/report/custom', config);
};

/**
 * 导出报表为PDF
 */
export const exportReportToPdf = (reportId: string): Promise<Blob> => {
  return api.get(`/report/${reportId}/export/pdf`, { responseType: 'blob' });
};

/**
 * 导出报表为Excel
 */
export const exportReportToExcel = (reportId: string): Promise<Blob> => {
  return api.get(`/report/${reportId}/export/excel`, { responseType: 'blob' });
};
