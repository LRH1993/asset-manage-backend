/**
 * 资产状态管理
 */

import { create } from 'zustand';
import { getAssetOverview, getCoreMetrics, getAssetAllocation, getReturnCurve } from '@/api/asset';
import type { AssetOverview, CoreMetrics, ModuleAllocation, ReturnCurveData } from '@/api/asset';
import type { PeriodType } from '@/types/common';

interface AssetState {
  // 数据
  overview: AssetOverview | null;
  metrics: CoreMetrics | null;
  allocation: ModuleAllocation | null;
  returnCurve: ReturnCurveData[] | null;

  // 状态
  loading: boolean;
  error: string | null;

  // 操作
  fetchOverview: () => Promise<void>;
  fetchMetrics: () => Promise<void>;
  fetchAllocation: () => Promise<void>;
  fetchReturnCurve: (period?: PeriodType) => Promise<void>;
  fetchAll: () => Promise<void>;
}

export const useAssetStore = create<AssetState>((set, get) => ({
  // 初始状态
  overview: null,
  metrics: null,
  allocation: null,
  returnCurve: null,
  loading: false,
  error: null,

  // 获取资产总览
  fetchOverview: async () => {
    set({ loading: true, error: null });
    try {
      const data = await getAssetOverview();
      set({ overview: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  // 获取核心指标
  fetchMetrics: async () => {
    set({ loading: true, error: null });
    try {
      const data = await getCoreMetrics();
      set({ metrics: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  // 获取资产配置
  fetchAllocation: async () => {
    set({ loading: true, error: null });
    try {
      const data = await getAssetAllocation();
      set({ allocation: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  // 获取收益曲线
  fetchReturnCurve: async (period = 'all') => {
    set({ loading: true, error: null });
    try {
      const data = await getReturnCurve(period);
      set({ returnCurve: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  // 获取所有数据
  fetchAll: async () => {
    await Promise.all([
      get().fetchOverview(),
      get().fetchMetrics(),
      get().fetchAllocation(),
      get().fetchReturnCurve(),
    ]);
  },
}));
