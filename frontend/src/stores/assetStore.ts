/**
 * 资产状态管理
 */

import { create } from 'zustand';
import { getAssetOverview, getCoreMetrics, getAssetAllocation, getReturnCurve, getRebalanceSuggestions } from '@/api/asset';
import type { AssetOverview, CoreMetrics, ModuleAllocation, ReturnCurveData, RebalanceSuggestion } from '@/api/asset';
import type { PeriodType } from '@/types/common';

interface AssetState {
  // 数据
  overview: AssetOverview | null;
  metrics: CoreMetrics | null;
  allocation: ModuleAllocation | null;
  returnCurve: ReturnCurveData[] | null;
  rebalanceSuggestion: RebalanceSuggestion | null;

  // 状态
  loading: boolean;
  error: string | null;

  // 操作
  fetchOverview: () => Promise<void>;
  fetchMetrics: () => Promise<void>;
  fetchAllocation: () => Promise<void>;
  fetchReturnCurve: (period?: PeriodType) => Promise<void>;
  fetchRebalanceSuggestions: () => Promise<void>;
  fetchAll: () => Promise<void>;
}

export const useAssetStore = create<AssetState>((set) => ({
  // 初始状态
  overview: null,
  metrics: null,
  allocation: null,
  returnCurve: null,
  rebalanceSuggestion: null,
  loading: false,
  error: null,

  // 获取资产总览
  fetchOverview: async () => {
    try {
      const data = await getAssetOverview();
      set({ overview: data });
    } catch (error: any) {
      set({ error: error.message });
    }
  },

  // 获取核心指标
  fetchMetrics: async () => {
    try {
      const data = await getCoreMetrics();
      set({ metrics: data });
    } catch (error: any) {
      set({ error: error.message });
    }
  },

  // 获取资产配置
  fetchAllocation: async () => {
    try {
      const data = await getAssetAllocation();
      set({ allocation: data });
    } catch (error: any) {
      set({ error: error.message });
    }
  },

  // 获取收益曲线
  fetchReturnCurve: async (period = 'all') => {
    try {
      const data = await getReturnCurve(period);
      set({ returnCurve: data });
    } catch (error: any) {
      set({ error: error.message });
    }
  },

  // 获取调仓建议
  fetchRebalanceSuggestions: async () => {
    try {
      const data = await getRebalanceSuggestions();
      set({ rebalanceSuggestion: data });
    } catch (error: any) {
      set({ error: error.message });
    }
  },

  // 获取所有数据
  fetchAll: async () => {
    set({ loading: true, error: null });
    try {
      const [overview, metrics, allocation, returnCurve, rebalanceSuggestion] = await Promise.all([
        getAssetOverview(),
        getCoreMetrics(),
        getAssetAllocation(),
        getReturnCurve('all'),
        getRebalanceSuggestions(),
      ]);
      set({
        overview,
        metrics,
        allocation,
        returnCurve,
        rebalanceSuggestion,
        loading: false,
      });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },
}));
