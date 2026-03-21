/**
 * 风险管理状态管理
 */

import { create } from 'zustand';
import { getRiskMetrics, getRiskDashboard, executeStressTest, getCorrelationMatrix, getRiskAlerts } from '@/api/risk';
import type { RiskMetrics, StressTestResult, RiskAlert } from '@/api/risk';

interface RiskState {
  metrics: RiskMetrics | null;
  dashboard: any;
  stressTestResult: StressTestResult | null;
  alerts: RiskAlert[];
  loading: boolean;
  error: string | null;
  fetchMetrics: (date?: string) => Promise<void>;
  fetchDashboard: () => Promise<void>;
  runStressTest: (scenario: any) => Promise<void>;
  fetchCorrelation: () => Promise<void>;
  fetchAlerts: () => Promise<void>;
}

export const useRiskStore = create<RiskState>((set) => ({
  metrics: null,
  dashboard: null,
  stressTestResult: null,
  alerts: [],
  loading: false,
  error: null,

  fetchMetrics: async (date) => {
    set({ loading: true, error: null });
    try {
      const data = await getRiskMetrics(date);
      set({ metrics: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  fetchDashboard: async () => {
    set({ loading: true, error: null });
    try {
      const data = await getRiskDashboard();
      set({ dashboard: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  runStressTest: async (scenario) => {
    set({ loading: true, error: null });
    try {
      const data = await executeStressTest(scenario);
      set({ stressTestResult: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  fetchCorrelation: async () => {
    set({ loading: true, error: null });
    try {
      await getCorrelationMatrix();
      set({ loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },

  fetchAlerts: async () => {
    set({ loading: true, error: null });
    try {
      const data = await getRiskAlerts();
      set({ alerts: data, loading: false });
    } catch (error: any) {
      set({ error: error.message, loading: false });
    }
  },
}));
