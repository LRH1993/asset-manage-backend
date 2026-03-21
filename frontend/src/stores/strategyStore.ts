/**
 * 策略分析状态管理
 */

import { create } from 'zustand';
import { getStrategies, getStrategyDetail, updateStrategyParams, executeBacktest, compareStrategies } from '@/api/strategy';
import type { Strategy, BacktestConfig, BacktestResult } from '@/api/strategy';

interface StrategyState {
  strategies: Strategy[];
  currentStrategy: Strategy | null;
  backtestResult: BacktestResult | null;
  loading: boolean;
  error: string | null;
  fetchStrategies: () => Promise<void>;
  fetchStrategyDetail: (id: number) => Promise<void>;
  updateParams: (id: number, params: Record<string, any>) => Promise<void>;
  runBacktest: (config: BacktestConfig) => Promise<void>;
  compare: (strategyIds: number[]) => Promise<void>;
}

export const useStrategyStore = create<StrategyState>((set) => ({
  strategies: [],
  currentStrategy: null,
  backtestResult: null,
  loading: false,
  error: null,

  fetchStrategies: async () => {
    set({ loading: true, error: null });
    try {
      const data = await getStrategies();
      set({ strategies: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  fetchStrategyDetail: async (id) => {
    set({ loading: true, error: null });
    try {
      const data = await getStrategyDetail(id);
      set({ currentStrategy: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  updateParams: async (id, params) => {
    set({ loading: true, error: null });
    try {
      await updateStrategyParams(id, params);
      set({ loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  runBacktest: async (config) => {
    set({ loading: true, error: null });
    try {
      const data = await executeBacktest(config);
      set({ backtestResult: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  compare: async (strategyIds) => {
    set({ loading: true, error: null });
    try {
      await compareStrategies(strategyIds);
      set({ loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },
}));
