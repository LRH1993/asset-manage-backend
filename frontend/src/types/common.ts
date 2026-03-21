/**
 * 通用类型定义
 */

/**
 * 四象限模块类型
 */
export type ModuleType = 'dividend' | 'fixed' | 'growth' | 'allweather';

/**
 * 市场类型
 */
export type MarketType = 'CN' | 'US' | 'HK';

/**
 * 资产类型
 */
export type AssetType = 'stock' | 'etf' | 'fund' | 'bond';

/**
 * 风险等级
 */
export type RiskLevel = 'low' | 'medium' | 'high';

/**
 * 状态类型
 */
export type StatusType = 'active' | 'inactive' | 'pending' | 'executed' | 'cancelled';

/**
 * 交易类型
 */
export type TransactionType = 'buy' | 'sell';

/**
 * 预警级别
 */
export type AlertLevel = 'info' | 'warning' | 'critical';

/**
 * 预警类型
 */
export type AlertType = 'value_buy' | 'value_sell' | 'dividend' | 'technical' | 'volatility' | 'drawdown' | 'concentration' | 'correlation';

/**
 * 周期类型
 */
export type PeriodType = '7d' | '30d' | '90d' | '1y' | 'all';

/**
 * 排序方向
 */
export type SortOrder = 'asc' | 'desc';

/**
 * 模块配置信息
 */
export interface ModuleConfig {
  key: ModuleType;
  name: string;
  nameEn: string;
  targetWeight: number;
  color: string;
  icon: string;
}

/**
 * 市场配置信息
 */
export interface MarketConfig {
  key: MarketType;
  name: string;
  icon: string;
  currency: string;
}
