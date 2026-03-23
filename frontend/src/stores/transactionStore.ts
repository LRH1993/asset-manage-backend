/**
 * 交易状态管理
 */

import { create } from 'zustand';
import { getTransactions, getTransactionById, createTransaction } from '@/api/transaction';
import type { Transaction, TransactionRequest } from '@/types/transaction';

interface TransactionState {
  transactions: Transaction[];
  currentTransaction: Transaction | null;
  total: number;
  loading: boolean;
  error: string | null;
  fetchTransactions: (params?: any) => Promise<void>;
  fetchTransactionDetail: (id: number) => Promise<void>;
  addTransaction: (data: TransactionRequest) => Promise<void>;
}

export const useTransactionStore = create<TransactionState>((set) => ({
  transactions: [],
  currentTransaction: null,
  total: 0,
  loading: false,
  error: null,

  fetchTransactions: async (params) => {
    set({ loading: true, error: null });
    try {
      const response = await getTransactions(params);
      set({ transactions: response.list, total: response.total, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  fetchTransactionDetail: async (id) => {
    set({ loading: true, error: null });
    try {
      const data = await getTransactionById(id);
      set({ currentTransaction: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  addTransaction: async (data) => {
    set({ loading: true, error: null });
    try {
      await createTransaction(data);
      set({ loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
      throw error;
    }
  },
}));
