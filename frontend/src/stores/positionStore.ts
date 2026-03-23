/**
 * 持仓状态管理
 */

import { create } from 'zustand';
import {
  getPositions,
  getPositionDetail,
  createPosition as createPositionApi,
  updatePosition as updatePositionApi,
  deletePosition as deletePositionApi,
  getPositionSummary,
} from '@/api/position';
import type { Position, PositionRequest, PositionQuery, PositionSummary } from '@/api/position';

interface PositionState {
  // 数据
  positions: Position[];
  currentPosition: Position | null;
  summary: PositionSummary | null;
  total: number;

  // 状态
  loading: boolean;
  summaryLoading: boolean;
  error: string | null;

  // 查询参数
  queryParams: PositionQuery;

  // 操作
  fetchPositions: (params?: PositionQuery) => Promise<void>;
  fetchSummary: () => Promise<void>;
  fetchPositionDetail: (id: number) => Promise<void>;
  addPosition: (data: PositionRequest) => Promise<number>;
  updatePosition: (id: number, data: Partial<PositionRequest>) => Promise<void>;
  removePosition: (id: number) => Promise<void>;
  setQueryParams: (params: Partial<PositionQuery>) => void;
  reset: () => void;
}

const defaultQueryParams: PositionQuery = {
  pageNum: 1,
  pageSize: 10,
};

export const usePositionStore = create<PositionState>((set, get) => ({
  // 初始状态
  positions: [],
  currentPosition: null,
  summary: null,
  total: 0,
  loading: false,
  summaryLoading: false,
  error: null,
  queryParams: defaultQueryParams,

  // 获取持仓列表
  fetchPositions: async (params) => {
    const newParams = { ...get().queryParams, ...params };
    set({ loading: true, error: null, queryParams: newParams });
    try {
      const response = await getPositions(newParams);
      set({
        positions: response.list,
        total: response.total,
        loading: false,
      });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  // 获取汇总统计
  fetchSummary: async () => {
    set({ summaryLoading: true });
    try {
      const data = await getPositionSummary();
      set({ summary: data, summaryLoading: false });
    } catch (error: any) {
      set({ error: error.message, summaryLoading: false });
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
      const id = await createPositionApi(data);
      // 刷新列表和汇总
      await get().fetchPositions();
      await get().fetchSummary();
      set({ loading: false });
      return id;
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
      // 刷新列表和汇总
      await get().fetchPositions();
      await get().fetchSummary();
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
      // 刷新列表和汇总
      await get().fetchPositions();
      await get().fetchSummary();
      set({ loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
      throw error;
    }
  },

  // 设置查询参数
  setQueryParams: (params) => {
    set((state) => ({
      queryParams: { ...state.queryParams, ...params },
    }));
  },

  // 重置
  reset: () => {
    set({
      positions: [],
      currentPosition: null,
      summary: null,
      total: 0,
      loading: false,
      summaryLoading: false,
      error: null,
      queryParams: defaultQueryParams,
    });
  },
}));
