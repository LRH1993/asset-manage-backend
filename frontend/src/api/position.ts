import api from './axios';

/**
 * 持仓实体
 */
export interface Position {
  id: number;
  symbol: string;
  name: string;
  module: 'dividend' | 'fixed' | 'growth' | 'allweather';
  market: 'CN' | 'US' | 'HK';
  assetType: 'stock' | 'etf' | 'fund' | 'bond';
  shares: number;
  avgCost: number;
  currentPrice: number;
  currentValue: number;
  targetWeight: number;
  buyPriceThreshold?: number;
  sellPriceThreshold?: number;
  status: 'active' | 'inactive';
  remarks?: string;
  createTime: string;
  updateTime: string;

  // 计算字段
  profit?: number;
  profitRate?: number;
  weight?: number;
}

/**
 * 持仓请求参数
 */
export interface PositionRequest {
  symbol: string;
  name?: string;
  module: 'dividend' | 'fixed' | 'growth' | 'allweather';
  market: 'CN' | 'US' | 'HK';
  assetType: 'stock' | 'etf' | 'fund' | 'bond';
  shares: number;
  avgCost: number;
  targetWeight: number;
  buyPriceThreshold?: number;
  sellPriceThreshold?: number;
  remarks?: string;
}

/**
 * 持仓查询参数
 */
export interface PositionQuery {
  page?: number;
  pageSize?: number;
  module?: 'dividend' | 'fixed' | 'growth' | 'allweather';
  market?: 'CN' | 'US' | 'HK';
  assetType?: 'stock' | 'etf' | 'fund' | 'bond';
  status?: 'active' | 'inactive';
  keyword?: string;
}

/**
 * 获取持仓列表
 */
export const getPositions = (params?: PositionQuery): Promise<{ list: Position[]; total: number }> => {
  return api.get('/positions', { params });
};

/**
 * 获取持仓详情
 */
export const getPositionDetail = (id: number): Promise<Position> => {
  return api.get(`/positions/${id}`);
};

/**
 * 创建持仓
 */
export const createPosition = (data: PositionRequest): Promise<void> => {
  return api.post('/positions', data);
};

/**
 * 更新持仓
 */
export const updatePosition = (id: number, data: Partial<PositionRequest>): Promise<void> => {
  return api.put(`/positions/${id}`, data);
};

/**
 * 删除持仓
 */
export const deletePosition = (id: number): Promise<void> => {
  return api.delete(`/positions/${id}`);
};

/**
 * 更新持仓价格
 */
export const updatePositionPrices = (): Promise<void> => {
  return api.post('/positions/update-prices');
};
