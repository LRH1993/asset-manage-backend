import api from './axios';

/**
 * 平衡操作
 */
export interface RebalanceAction {
  symbol: string;
  actionType: 'buy' | 'sell';
  shares: number;
  price: number;
  amount: number;
  reason: string;
}

/**
 * 平衡方案
 */
export interface RebalancePlan {
  id: number;
  totalValue: number;
  rebalanceDate: string;
  triggerReason: string;
  actions: RebalanceAction[];
  estimatedFee: number;
  status: 'pending' | 'executed' | 'cancelled';
}

/**
 * 平衡历史
 */
export interface RebalanceHistory {
  id: number;
  balanceDate: string;
  totalValueBefore: number;
  totalValueAfter?: number;
  triggerReason: string;
  actions: RebalanceAction[];
  status: 'pending' | 'executed' | 'cancelled';
  executedAt?: string;
  notes?: string;
  createTime: string;
}

/**
 * 平衡请求参数
 */
export interface RebalanceRequest {
  triggerReason: string;
  threshold?: number;
}

/**
 * 获取平衡状态
 */
export const getRebalanceStatus = (): Promise<{
  totalDeviation: number;
  needsRebalance: boolean;
  lastRebalanceDate: string;
}> => {
  return api.get('/rebalance/status');
};

/**
 * 生成平衡方案
 */
export const generateRebalancePlan = (data: RebalanceRequest): Promise<RebalancePlan> => {
  return api.post('/rebalance/plan', data);
};

/**
 * 执行平衡方案
 */
export const executeRebalance = (plan: RebalancePlan): Promise<void> => {
  return api.post('/rebalance/execute', plan);
};

/**
 * 获取平衡历史
 */
export const getRebalanceHistory = (params?: { page?: number; pageSize?: number }): Promise<{ list: RebalanceHistory[]; total: number }> => {
  return api.get('/rebalance/history', { params });
};
