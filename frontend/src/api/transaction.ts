import api from './axios';

/**
 * 交易实体
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

/**
 * 交易请求参数
 */
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

/**
 * 交易查询参数
 */
export interface TransactionQuery {
  page?: number;
  pageSize?: number;
  symbol?: string;
  transactionType?: 'buy' | 'sell';
  startDate?: string;
  endDate?: string;
}

/**
 * 获取交易列表
 */
export const getTransactions = (params?: TransactionQuery): Promise<{ list: Transaction[]; total: number }> => {
  return api.get('/transactions', { params });
};

/**
 * 获取交易详情
 */
export const getTransactionDetail = (id: number): Promise<Transaction> => {
  return api.get(`/transactions/${id}`);
};

/**
 * 创建交易
 */
export const createTransaction = (data: TransactionRequest): Promise<void> => {
  return api.post('/transactions', data);
};
