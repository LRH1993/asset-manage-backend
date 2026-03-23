import api from './axios';
import type { Transaction, TransactionQuery, TransactionRequest, TransactionSummary } from '@/types/transaction';

interface PageResponse<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
}

/**
 * 获取交易列表
 */
export const getTransactions = async (params?: TransactionQuery): Promise<{ list: Transaction[]; total: number }> => {
  const response = await api.get('/transactions', { params }) as PageResponse<Transaction>;
  return {
    list: response.records,
    total: response.total,
  };
};

/**
 * 获取交易详情
 */
export const getTransactionById = async (id: number): Promise<Transaction> => {
  return await api.get(`/transactions/${id}`) as unknown as Transaction;
};

/**
 * 创建交易
 */
export const createTransaction = async (data: TransactionRequest): Promise<number> => {
  return await api.post('/transactions', data) as unknown as number;
};

/**
 * 更新交易
 */
export const updateTransaction = async (id: number, data: TransactionRequest): Promise<void> => {
  await api.put(`/transactions/${id}`, data);
};

/**
 * 删除交易
 */
export const deleteTransaction = async (id: number): Promise<void> => {
  await api.delete(`/transactions/${id}`);
};

/**
 * 获取交易汇总统计
 */
export const getTransactionSummary = async (): Promise<TransactionSummary> => {
  return await api.get('/transactions/summary') as unknown as TransactionSummary;
};
