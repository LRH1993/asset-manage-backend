/**
 * 动态平衡状态管理
 */

import { create } from 'zustand';
import { getRebalanceStatus, generateRebalancePlan, executeRebalance, getRebalanceHistory, type RebalanceRequest } from '@/api/rebalance';
import type { RebalancePlan, RebalanceHistory } from '@/api/rebalance';

interface RebalanceState {
  status: any;
  currentPlan: RebalancePlan | null;
  history: RebalanceHistory[];
  loading: boolean;
  error: string | null;
  fetchStatus: () => Promise<void>;
  generatePlan: (request: RebalanceRequest) => Promise<void>;
  executePlan: (plan: RebalancePlan) => Promise<void>;
  fetchHistory: (params?: any) => Promise<void>;
}

export const useRebalanceStore = create<RebalanceState>((set) => ({
  status: null,
  currentPlan: null,
  history: [],
  loading: false,
  error: null,

  fetchStatus: async () => {
    set({ loading: true, error: null });
    try {
      const data = await getRebalanceStatus();
      set({ status: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  generatePlan: async (request) => {
    set({ loading: true, error: null });
    try {
      const data = await generateRebalancePlan(request);
      set({ currentPlan: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  executePlan: async (plan) => {
    set({ loading: true, error: null });
    try {
      await executeRebalance(plan);
      set({ loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  fetchHistory: async (params) => {
    set({ loading: true, error: null });
    try {
      const response = await getRebalanceHistory(params);
      set({ history: response.list, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },
}));
