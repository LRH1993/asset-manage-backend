/**
 * 交易相关类型定义
 */

export interface Transaction {
  id: number;
  positionId?: number;
  symbol: string;
  positionName?: string;
  transactionType: 'buy' | 'sell';
  shares: number;
  price: number;
  totalAmount: number;
  fee: number;
  currency: string;
  transactionDate: string;
  notes?: string;
  createTime: string;
}

export interface TransactionRequest {
  positionId?: number;
  symbol: string;
  transactionType: 'buy' | 'sell';
  shares: number;
  price: number;
  totalAmount: number;
  fee: number;
  currency?: string;
  transactionDate: string;
  notes?: string;
}
