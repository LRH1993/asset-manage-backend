/**
 * 动态平衡相关类型定义
 */

export interface RebalanceAction {
  symbol: string;
  actionType: 'buy' | 'sell';
  shares: number;
  price: number;
  amount: number;
  reason: string;
}

export interface RebalancePlan {
  id: number;
  totalValue: number;
  rebalanceDate: string;
  triggerReason: string;
  actions: RebalanceAction[];
  estimatedFee: number;
  status: 'pending' | 'executed' | 'cancelled';
}

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
