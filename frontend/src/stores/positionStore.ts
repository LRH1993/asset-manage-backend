/**
 * 持仓状态管理
 */

import { create } from 'zustand';
import { getPositions, getPositionDetail, createPosition as createPositionApi, updatePosition as updatePositionApi, deletePosition as deletePositionApi, updatePositionPrices } from '@/api/position';
import type { Position, PositionRequest } from '@/api/position';

interface PositionState {
  // 数据
  positions: Position[];
  currentPosition: Position | null;
  total: number;

  // 状态
  loading: boolean;
  error: string | null;

  // 操作
  fetchPositions: (params?: any) => Promise<void>;
  fetchPositionDetail: (id: number) => Promise<void>;
  addPosition: (data: PositionRequest) => Promise<void>;
  updatePosition: (id: number, data: Partial<PositionRequest>) => Promise<void>;
  removePosition: (id: number) => Promise<void>;
  refreshPrices: () => Promise<void>;
}

export const usePositionStore = create<PositionState>((set) => ({
  // 初始状态
  positions: [],
  currentPosition: null,
  total: 0,
  loading: false,
  error: null,

  // 获取持仓列表
  fetchPositions: async (params) => {
    set({ loading: true, error: null });
    try {
      const response = await getPositions(params);
      set({
        positions: response.list,
        total: response.total,
        loading: false,
      });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  // 获取持仓详情
  fetchPositionDetail: async (id) => {
    set({ loading: true, error: null });
    try {
      const data = await getPositionDetail(id);
      set({ currentPosition: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  // 添加持仓
  addPosition: async (data) => {
    set({ loading: true, error: null });
    try {
      await createPositionApi(data);
      set({ loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
      throw error;
    }
  },

  // 更新持仓
  updatePosition: async (id, data) => {
    set({ loading: true, error: null });
    try {
      await updatePositionApi(id, data);
      set({ loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
      throw error;
    }
  },

  // 删除持仓
  removePosition: async (id) => {
    set({ loading: true, error: null });
    try {
      await deletePositionApi(id);
      set({ loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
      throw error;
    }
  },

  // 更新持仓价格
  refreshPrices: async () => {
    set({ loading: true, error: null });
    try {
      await updatePositionPrices();
      set({ loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },
}));
