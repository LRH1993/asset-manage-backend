/**
 * 交易相关类型定义
 */

import type { TransactionType } from './common';

export type Module = 'dividend' | 'fixed' | 'growth' | 'allweather';

export interface Transaction {
  id: number;
  positionId: number | null;
  symbol: string;
  name: string | null;
  module: Module | null;
  transactionType: TransactionType;
  shares: number;
  price: number;
  totalAmount: number;
  fee: number;
  currency: string;
  transactionDate: string;
  notes: string | null;
  realizedProfit: number | null;
  createTime: string;
}

export interface TransactionRequest {
  positionId?: number;
  symbol: string;
  transactionType: TransactionType;
  shares: number;
  price: number;
  fee?: number;
  currency?: string;
  transactionDate: string;
  notes?: string;
}

export interface TransactionQuery {
  pageNum?: number;
  pageSize?: number;
  transactionType?: TransactionType | '';
  module?: Module | '';
  keyword?: string;
  startDate?: string;
  endDate?: string;
}

export interface TransactionSummary {
  totalCount: number;
  buyCount: number;
  sellCount: number;
  buyAmount: number;
  sellAmount: number;
  totalFee: number;
  realizedProfit: number;
  realizedProfitRate: number;
  moduleSummaries: Array<{
    module: Module;
    moduleName: string;
    count: number;
    amount: number;
  }>;
}
