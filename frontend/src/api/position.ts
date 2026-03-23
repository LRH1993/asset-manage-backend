import api from './axios';

/**
 * 持仓实体
 */
export interface Position {
  id: number;
  symbol: string;
  name: string;
  module: 'dividend' | 'fixed' | 'growth' | 'allweather';
  market: string;
  assetType: string;
  shares: number;
  avgCost: number;
  currentPrice: number;
  currentValue: number;
  costValue: number;
  profitAmount: number;
  profitRate: number;
  todayProfitRate?: number;  // 今日盈亏率（可选）
  targetWeight: number;
  actualWeight?: number;
  buyPriceThreshold?: number;
  sellPriceThreshold?: number;
  status: string;
  remarks?: string;
  createTime: string;
  updateTime: string;
}

/**
 * 持仓请求参数
 */
export interface PositionRequest {
  id?: number;
  symbol: string;
  name: string;
  module: string;
  market?: string;
  assetType?: string;
  shares: number;
  avgCost: number;
  currentPrice?: number;
  targetWeight?: number;
  buyPriceThreshold?: number;
  sellPriceThreshold?: number;
  remarks?: string;
}

/**
 * 持仓查询参数
 */
export interface PositionQuery {
  pageNum?: number;
  pageSize?: number;
  module?: string;
  status?: string;
  keyword?: string;
  sortBy?: string;
  sortOrder?: string;
}

/**
 * 持仓汇总统计
 */
export interface PositionSummary {
  totalValue: number;
  totalCost: number;
  totalProfit: number;
  totalProfitRate: number;
  positionCount: number;
  profitCount: number;
  lossCount: number;
  todayProfit?: number;
  todayProfitRate?: number;
  moduleSummaries: ModuleSummary[];
}

export interface ModuleSummary {
  module: string;
  moduleName: string;
  value: number;
  weight: number;
  profitRate: number;
  count: number;
}

/**
 * 分页响应
 */
interface PageResponse<T> {
  records: T[];
  total: number;
  current: number;
  size: number;
}

/**
 * 获取持仓列表
 */
export const getPositions = async (params?: PositionQuery): Promise<{ list: Position[]; total: number }> => {
  const response = await api.get('/positions', { params }) as PageResponse<Position>;
  return {
    list: response.records,
    total: response.total,
  };
};

/**
 * 获取持仓详情
 */
export const getPositionDetail = async (id: number): Promise<Position> => {
  return await api.get(`/positions/${id}`) as Position;
};

/**
 * 创建持仓
 */
export const createPosition = async (data: PositionRequest): Promise<number> => {
  return await api.post('/positions', data) as number;
};

/**
 * 更新持仓
 */
export const updatePosition = async (id: number, data: Partial<PositionRequest>): Promise<void> => {
  await api.put(`/positions/${id}`, data);
};

/**
 * 删除持仓
 */
export const deletePosition = async (id: number): Promise<void> => {
  await api.delete(`/positions/${id}`);
};

/**
 * 获取持仓汇总统计
 */
export const getPositionSummary = async (): Promise<PositionSummary> => {
  return await api.get('/positions/summary') as PositionSummary;
};

/**
 * 更新持仓价格
 */
export const updatePositionPrice = async (id: number, price: number): Promise<void> => {
  await api.put(`/positions/${id}/price`, null, { params: { price } });
};
