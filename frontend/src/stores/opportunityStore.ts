/**
 * 机会发现状态管理
 */

import { create } from 'zustand';
import { getInvestmentUniverse, getOpportunityAlerts, markAlertAsRead, processAlert as processAlertApi } from '@/api/opportunity';
import type { InvestmentUniverse, OpportunityAlert } from '@/api/opportunity';

interface OpportunityState {
  universe: InvestmentUniverse[];
  alerts: OpportunityAlert[];
  total: number;
  unreadCount: number;
  loading: boolean;
  error: string | null;
  fetchUniverse: (params?: any) => Promise<void>;
  fetchAlerts: (params?: any) => Promise<void>;
  markAsRead: (id: number) => Promise<void>;
  processAlert: (id: number, result: string) => Promise<void>;
}

export const useOpportunityStore = create<OpportunityState>((set) => ({
  universe: [],
  alerts: [],
  total: 0,
  unreadCount: 0,
  loading: false,
  error: null,

  fetchUniverse: async (params) => {
    set({ loading: true, error: null });
    try {
      const response = await getInvestmentUniverse(params);
      set({ universe: response.list, total: response.total, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  fetchAlerts: async (params) => {
    set({ loading: true, error: null });
    try {
      const response = await getOpportunityAlerts(params);
      const unreadCount = response.list.filter((a: any) => !a.isRead).length;
      set({ alerts: response.list, total: response.total, unreadCount, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  markAsRead: async (id) => {
    try {
      await markAlertAsRead(id);
      set(state => ({
        alerts: state.alerts.map(a => a.id === id ? { ...a, isRead: true } : a),
        unreadCount: Math.max(0, state.unreadCount - 1),
      }));
    } catch (error: any) {
      set({ error: error.message });
    }
  },

  processAlert: async (id, result) => {
    try {
      await processAlertApi(id, result);
      set(state => ({
        alerts: state.alerts.map(a => a.id === id ? { ...a, isProcessed: true, processResult: result } : a),
      }));
    } catch (error: any) {
      set({ error: error.message });
    }
  },
}));
