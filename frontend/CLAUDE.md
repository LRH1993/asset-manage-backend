# 家庭资产监控管理平台 - 前端开发实践

> 🎨 **UI 规范**: 开发前请务必阅读 [UI 设计规范](../ui/CLAUDE.md)，确保实现符合设计标准。

## 🛠️ 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| React | 18+ | UI框架 |
| TypeScript | 5+ | 类型安全 |
| Vite | 5+ | 构建工具 |
| Ant Design Pro | - | UI组件库 |
| Tailwind CSS | - | 原子化CSS |
| Zustand | - | 状态管理 |
| ECharts | - | 图表库 |
| Axios | - | HTTP客户端 |
| dayjs | - | 日期处理 |
| decimal.js | - | 精确计算 |

---

## 🎨 色彩规范（与 UI 设计同步）

### 涨跌色（金融核心）

```typescript
// constants/colors.ts
export const PROFIT_COLORS = {
  up: '#CF1322',      // 上涨/盈利（红色，A股习惯）
  down: '#3F8600',    // 下跌/亏损（绿色）
  neutral: '#8C8C8C', // 平盘/无变化
} as const;
```

### 四象限模块色

```typescript
// constants/modules.ts
export const MODULE_COLORS = {
  dividend: {
    primary: '#52C41A',  // 红利 - 稳健收益
    light: '#F6FFED',
  },
  fixed: {
    primary: '#1890FF',  // 固收 - 安全信任
    light: '#E6F7FF',
  },
  growth: {
    primary: '#722ED1',  // 成长 - 成长潜力
    light: '#F9F0FF',
  },
  allweather: {
    primary: '#FA8C16',  // 全天候 - 平衡多元
    light: '#FFF7E6',
  },
} as const;
```

### 功能色

```typescript
export const FUNCTIONAL_COLORS = {
  primary: '#1890FF',
  success: '#52C41A',
  warning: '#FAAD14',
  error: '#FF4D4F',
  info: '#1890FF',
} as const;
```

> 完整色彩规范请参考 [UI 设计规范 - 色彩系统](../ui/CLAUDE.md#-色彩系统)

## 🏗️ 项目结构

```
frontend/
├── src/
│   ├── api/                    # API接口层
│   │   ├── axios.ts           # Axios实例
│   │   ├── interceptors.ts    # 拦截器
│   │   ├── position.ts        # 持仓API
│   │   ├── transaction.ts     # 交易API
│   │   └── types.ts           # API类型
│   │
│   ├── components/            # 公共组件
│   │   ├── charts/            # 图表组件
│   │   ├── cards/             # 卡片组件
│   │   ├── tables/            # 表格组件
│   │   ├── forms/             # 表单组件
│   │   └── common/            # 通用组件
│   │
│   ├── pages/                 # 页面组件
│   │   ├── Dashboard/         # 仪表盘
│   │   ├── Portfolio/         # 资产管理
│   │   ├── Opportunity/       # 机会发现
│   │   ├── Strategy/          # 策略分析
│   │   ├── Risk/              # 风险管理
│   │   └── Rebalance/         # 动态平衡
│   │
│   ├── stores/                # Zustand状态
│   │   ├── index.ts
│   │   ├── positionStore.ts
│   │   └── uiStore.ts
│   │
│   ├── types/                 # TypeScript类型
│   │   ├── position.ts
│   │   ├── transaction.ts
│   │   └── common.ts
│   │
│   ├── utils/                 # 工具函数
│   │   ├── format.ts          # 格式化
│   │   ├── calculate.ts       # 计算
│   │   └── storage.ts         # 存储
│   │
│   ├── constants/             # 常量
│   │   ├── modules.ts         # 四象限
│   │   └── config.ts          # 配置
│   │
│   ├── hooks/                 # 自定义Hooks
│   │   ├── useRequest.ts
│   │   └── useChart.ts
│   │
│   └── layouts/               # 布局组件
│       └── MainLayout.tsx
│
├── .env                       # 环境变量
├── vite.config.ts             # Vite配置
└── tsconfig.json              # TS配置
```

---

## 📐 组件设计规范

### 组件结构
```typescript
// PositionCard.tsx
import { Card, Statistic } from 'antd';
import { useMemo } from 'react';
import type { Position } from '@/types/position';
import { formatMoney, formatPercent } from '@/utils/format';
import styles from './PositionCard.module.css';

interface PositionCardProps {
  position: Position;
  onClick?: () => void;
}

export const PositionCard: React.FC<PositionCardProps> = ({
  position,
  onClick
}) => {
  // 计算盈亏
  const profit = useMemo(() => {
    return position.currentValue.minus(position.avgCost.times(position.shares));
  }, [position]);

  return (
    <Card
      className={styles.card}
      hoverable
      onClick={onClick}
    >
      <div className={styles.header}>
        <span className={styles.symbol}>{position.symbol}</span>
        <span className={styles.name}>{position.name}</span>
      </div>

      <Statistic
        title="当前市值"
        value={position.currentValue}
        precision={2}
        formatter={formatMoney}
      />

      <Statistic
        title="收益率"
        value={position.profitRate}
        precision={2}
        suffix="%"
        valueStyle={{
          color: profit.gte(0) ? '#cf1322' : '#3f8600'
        }}
      />
    </Card>
  );
};
```

### 组件命名规范
- **文件**: PascalCase (`PositionCard.tsx`)
- **组件**: PascalCase (`const PositionCard`)
- **Props接口**: 组件名 + Props (`PositionCardProps`)
- **样式文件**: 同名 + `.module.css`

---

## 🔌 API层设计

### Axios实例
```typescript
// api/axios.ts
import axios from 'axios';

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
instance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截器
instance.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default instance;
```

### API封装
```typescript
// api/position.ts
import request from './axios';
import type { Position, PositionQuery } from '@/types/position';
import type { PageResult, Result } from '@/types/common';

export const positionApi = {
  getList: (params?: PositionQuery) =>
    request.get<Result<Position[]>>('/positions', { params }),

  getDetail: (id: number) =>
    request.get<Result<Position>>(`/positions/${id}`),

  create: (data: PositionCreateDTO) =>
    request.post<Result<number>>('/positions', data),

  update: (id: number, data: PositionUpdateDTO) =>
    request.put<Result<void>>(`/positions/${id}`, data),

  delete: (id: number) =>
    request.delete<Result<void>>(`/positions/${id}`),
};
```

---

## 🗃️ 状态管理

### Store定义
```typescript
// stores/positionStore.ts
import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';
import type { Position } from '@/types/position';
import { positionApi } from '@/api/position';

interface PositionState {
  positions: Position[];
  loading: boolean;
  selectedId: number | null;

  // Actions
  fetchPositions: () => Promise<void>;
  addPosition: (position: Position) => void;
  updatePosition: (id: number, data: Partial<Position>) => void;
  removePosition: (id: number) => void;
  setSelectedId: (id: number | null) => void;
}

export const usePositionStore = create<PositionState>()(
  devtools(
    persist(
      (set, get) => ({
        positions: [],
        loading: false,
        selectedId: null,

        fetchPositions: async () => {
          set({ loading: true });
          try {
            const { data } = await positionApi.getList();
            set({ positions: data, loading: false });
          } catch (error) {
            set({ loading: false });
            throw error;
          }
        },

        updatePosition: (id, data) => {
          set((state) => ({
            positions: state.positions.map((p) =>
              p.id === id ? { ...p, ...data } : p
            ),
          }));
        },

        setSelectedId: (id) => set({ selectedId: id }),
      }),
      { name: 'position-store' }
    )
  )
);
```

---

## 📊 TypeScript类型定义

### 基础类型
```typescript
// types/position.ts
import type { Decimal } from 'decimal.js';

export type Module = 'dividend' | 'fixed' | 'growth' | 'allweather';
export type Market = 'CN' | 'US' | 'HK';
export type AssetType = 'stock' | 'etf' | 'fund' | 'bond';
export type PositionStatus = 'active' | 'inactive';

export interface Position {
  id: number;
  symbol: string;
  name: string;
  module: Module;
  market: Market;
  assetType: AssetType;
  shares: Decimal;
  avgCost: Decimal;
  currentPrice: Decimal;
  currentValue: Decimal;
  targetWeight: number;
  profitRate: number;
  status: PositionStatus;
  createTime: string;
  updateTime: string;
}

export interface PositionCreateDTO {
  symbol: string;
  name: string;
  module: Module;
  market: Market;
  assetType: AssetType;
  shares: number;
  avgCost: number;
  targetWeight?: number;
}
```

### API响应类型
```typescript
// types/common.ts
export interface Result<T> {
  code: string;
  message: string;
  data: T;
}

export interface PageResult<T> {
  list: T[];
  total: number;
  page: number;
  pageSize: number;
}

export interface PageQuery {
  page?: number;
  pageSize?: number;
  sortField?: string;
  sortOrder?: 'asc' | 'desc';
}
```

---

## 🎨 图表组件

> 详细图表规范请参考 [UI 设计规范 - 数据可视化](../ui/CLAUDE.md#-数据可视化规范)

### 图表配置常量

```typescript
// constants/charts.ts
import { MODULE_COLORS, PROFIT_COLORS } from './colors';

// 通用图表配置
export const CHART_BASE_CONFIG = {
  // 字体
  fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',

  // 颜色
  colors: {
    primary: '#1890FF',
    profit: PROFIT_COLORS.up,
    loss: PROFIT_COLORS.down,
    neutral: '#8C8C8C',
  },

  // 模块色
  moduleColors: MODULE_COLORS,

  // 图表尺寸
  minHeight: {
    line: 200,
    pie: 200,
    bar: 200,
    combo: 300,
  },
  recommendedHeight: {
    line: 300,
    pie: 280,
    bar: 300,
    combo: 400,
  },
} as const;
```

### ECharts封装

```typescript
// components/charts/LineChart.tsx
import ReactECharts from 'echarts-for-react';
import { useMemo } from 'react';
import type { EChartsOption } from 'echarts';
import { CHART_BASE_CONFIG } from '@/constants/charts';

interface LineChartProps {
  data: number[];
  dates: string[];
  title?: string;
  loading?: boolean;
  height?: number;  // 遵循 UI 规范，默认 400px
  color?: string;   // 可自定义颜色
}

export const LineChart: React.FC<LineChartProps> = ({
  data,
  dates,
  title,
  loading,
  height = 400,  // UI 规范推荐高度
  color = CHART_BASE_CONFIG.colors.primary,
}) => {
  const option: EChartsOption = useMemo(() => ({
    title: title ? {
      text: title,
      left: 'center',
      textStyle: { fontSize: 16, fontWeight: 500 }
    } : undefined,

    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>收益: {c}%',  // 百分比格式
    },

    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: '#D9D9D9' } },
      axisLabel: { color: '#595959' },
    },

    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%',
        color: '#8C8C8C',
      },
      splitLine: { lineStyle: { color: '#F0F0F0' } },
    },

    series: [{
      type: 'line',
      data,
      smooth: true,
      areaStyle: { opacity: 0.1 },  // 遵循 UI 规范
      itemStyle: { color },
      lineStyle: { width: 2 },
    }],

    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
    },
  }), [data, dates, title, color]);

  return (
    <ReactECharts
      option={option}
      style={{ height }}
      showLoading={loading}
    />
  );
};
```

### 收益曲线图（涨跌色）

```typescript
// components/charts/ReturnCurve.tsx
import { PROFIT_COLORS } from '@/constants/colors';

// 根据正负值自动着色
export const ReturnCurve: React.FC<ReturnCurveProps> = ({ data, dates }) => {
  const option: EChartsOption = {
    // ... 其他配置
    series: [{
      type: 'line',
      data,
      itemStyle: {
        color: (params: any) => {
          const value = params.value as number;
          if (value > 0) return PROFIT_COLORS.up;   // 红色
          if (value < 0) return PROFIT_COLORS.down; // 绿色
          return PROFIT_COLORS.neutral;             // 灰色
        },
      },
    }],
  };
};
```

### 饼图（四象限配置）

```typescript
// components/charts/AllocationPie.tsx
import { MODULE_COLORS } from '@/constants/colors';

export const AllocationPie: React.FC = ({ data }) => {
  const option: EChartsOption = {
    series: [{
      type: 'pie',
      data: [
        { value: data.dividend, name: '红利', itemStyle: { color: MODULE_COLORS.dividend.primary } },
        { value: data.fixed, name: '固收', itemStyle: { color: MODULE_COLORS.fixed.primary } },
        { value: data.growth, name: '成长', itemStyle: { color: MODULE_COLORS.growth.primary } },
        { value: data.allweather, name: '全天候', itemStyle: { color: MODULE_COLORS.allweather.primary } },
      ],
      radius: ['40%', '70%'],  // 环形图
      label: { show: false },   // 标签放图例
    }],
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
    },
  };
};
```

---

## 🛠️ 工具函数

> 数字格式化规则遵循 [UI 设计规范 - 数字格式化](../ui/CLAUDE.md#-数据可视化规范)

### 数字格式化

```typescript
// utils/format.ts
import Decimal from 'decimal.js';
import { PROFIT_COLORS } from '@/constants/colors';

/**
 * 格式化金额
 * - 大于1万显示"万"
 * - 大于1亿显示"亿"
 * - 保留2位小数
 */
export const formatMoney = (value: Decimal | number): string => {
  const num = new Decimal(value);
  const abs = num.abs();

  if (abs.gte(100000000)) {
    return `${num.div(100000000).toFixed(2)}亿`;
  }
  if (abs.gte(10000)) {
    return `${num.div(10000).toFixed(2)}万`;
  }
  return num.toFixed(2);
};

/**
 * 格式化金额（千分位）
 * 用于表格精确显示
 */
export const formatMoneyWithComma = (value: Decimal | number): string => {
  const num = new Decimal(value);
  return num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

/**
 * 格式化百分比
 * - 自动添加正负号
 * - 保留2位小数
 */
export const formatPercent = (value: Decimal | number): string => {
  const num = new Decimal(value);
  if (num.isZero()) return '0.00%';
  const sign = num.gt(0) ? '+' : '';
  return `${sign}${num.toFixed(2)}%`;
};

/**
 * 获取盈亏颜色
 * 红涨绿跌（A股习惯）
 */
export const getProfitColor = (value: Decimal | number): string => {
  const num = new Decimal(value);
  if (num.gt(0)) return PROFIT_COLORS.up;      // #CF1322 红色
  if (num.lt(0)) return PROFIT_COLORS.down;    // #3F8600 绿色
  return PROFIT_COLORS.neutral;                 // #8C8C8C 灰色
};

/**
 * 格式化股数/份额（千分位，无小数或2位小数）
 */
export const formatShares = (value: Decimal | number, decimal = 0): string => {
  const num = new Decimal(value);
  return num.toFixed(decimal).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};
```

### 金融计算
```typescript
// utils/calculate.ts
import Decimal from 'decimal.js';

/**
 * 计算收益率
 */
export const calculateReturnRate = (
  currentValue: Decimal,
  costValue: Decimal
): Decimal => {
  if (costValue.isZero()) return new Decimal(0);
  return currentValue.minus(costValue).div(costValue).mul(100);
};

/**
 * 计算最大回撤
 */
export const calculateMaxDrawdown = (values: Decimal[]): Decimal => {
  let maxDrawdown = new Decimal(0);
  let peak = values[0];

  for (const value of values) {
    if (value.gt(peak)) {
      peak = value;
    }
    const drawdown = peak.minus(value).div(peak);
    if (drawdown.gt(maxDrawdown)) {
      maxDrawdown = drawdown;
    }
  }

  return maxDrawdown.mul(100);
};
```

---

## 🎣 自定义Hooks

### 请求Hook
```typescript
// hooks/useRequest.ts
import { useState, useCallback, useEffect } from 'react';

interface UseRequestResult<T> {
  data: T | null;
  loading: boolean;
  error: Error | null;
  execute: () => Promise<void>;
}

export const useRequest = <T>(
  apiFn: () => Promise<T>,
  immediate = true
): UseRequestResult<T> => {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const execute = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await apiFn();
      setData(result);
    } catch (err) {
      setError(err as Error);
    } finally {
      setLoading(false);
    }
  }, [apiFn]);

  useEffect(() => {
    if (immediate) {
      execute();
    }
  }, []);

  return { data, loading, error, execute };
};
```

---

## 📱 页面开发示例

### 仪表盘页面
```typescript
// pages/Dashboard/Overview/index.tsx
import { Row, Col, Spin } from 'antd';
import { usePositionStore } from '@/stores/positionStore';
import { AssetSummary } from './AssetSummary';
import { ModuleAllocation } from './ModuleAllocation';
import { ReturnCurve } from './ReturnCurve';
import { MetricCards } from './MetricCards';

export const DashboardOverview: React.FC = () => {
  const { positions, loading } = usePositionStore();

  if (loading) {
    return <Spin size="large" />;
  }

  return (
    <div className="p-6">
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <AssetSummary positions={positions} />
        </Col>

        <Col span={12}>
          <ModuleAllocation positions={positions} />
        </Col>

        <Col span={12}>
          <MetricCards positions={positions} />
        </Col>

        <Col span={24}>
          <ReturnCurve />
        </Col>
      </Row>
    </div>
  );
};
```

---

## 🎨 四象限模块常量

```typescript
// constants/modules.ts
import { MODULE_COLORS } from './colors';

export interface ModuleConfig {
  key: 'dividend' | 'fixed' | 'growth' | 'allweather';
  name: string;
  color: string;          // 主色
  lightColor: string;     // 浅色背景
  targetWeight: number;   // 目标权重
  targetReturn: number;   // 目标收益
  description: string;    // 描述
}

export const MODULES: Record<string, ModuleConfig> = {
  dividend: {
    key: 'dividend',
    name: '红利',
    color: MODULE_COLORS.dividend.primary,      // #52C41A
    lightColor: MODULE_COLORS.dividend.light,   // #F6FFED
    targetWeight: 25,
    targetReturn: 10,
    description: '低波动，稳定分红',
  },
  fixed: {
    key: 'fixed',
    name: '固收',
    color: MODULE_COLORS.fixed.primary,         // #1890FF
    lightColor: MODULE_COLORS.fixed.light,      // #E6F7FF
    targetWeight: 25,
    targetReturn: 3,
    description: '回撤<1.5%，稳健',
  },
  growth: {
    key: 'growth',
    name: '成长',
    color: MODULE_COLORS.growth.primary,        // #722ED1
    lightColor: MODULE_COLORS.growth.light,     // #F9F0FF
    targetWeight: 25,
    targetReturn: 15,
    description: '高波动，高收益',
  },
  allweather: {
    key: 'allweather',
    name: '全天候',
    color: MODULE_COLORS.allweather.primary,    // #FA8C16
    lightColor: MODULE_COLORS.allweather.light, // #FFF7E6
    targetWeight: 25,
    targetReturn: 8,
    description: '回撤~10%，平衡',
  },
} as const;

// 获取模块配置
export const getModuleConfig = (key: string): ModuleConfig | undefined => {
  return MODULES[key];
};
```

---

## ⚠️ 注意事项

1. **精度问题**: 金融数据使用 `decimal.js`，避免 `number`
2. **时间格式**: 使用 `dayjs`，注意时区
3. **颜色规范**: 严格遵循 UI 规范，红涨绿跌（A股习惯）
4. **错误处理**: 统一使用 `message.error` 提示
5. **性能优化**: 大列表用虚拟滚动，图表懒加载
6. **数字格式化**: 遵循 UI 规范的格式化规则
7. **图表高度**: 遵循 UI 规范的最小/推荐高度
8. **⭐ UI 验证**: 每次 UI 修改后必须进行截图对比验证

## 📸 UI 验证流程（强制）

**每次修改 UI 后，必须执行以下验证：**

### 1. 截图对比命令
```
请使用 playwright 截图对比 http://localhost:5173/<page> 页面与 ui/prototypes/<page>.html 设计稿
```

### 2. 验证清单
| 检查项 | 说明 | 通过标准 |
|--------|------|----------|
| 布局结构 | 是否与设计稿一致 | 偏差 < 5% |
| 色彩 | 涨跌色、模块色是否正确 | 符合规范 |
| 字体大小 | 标题、正文、标签 | 符合规范 |
| 间距 | padding/margin | 偏差 < 3px |
| 交互状态 | hover/active/disabled | 完整实现 |

### 3. 偏差处理
- **偏差 < 5%**: 通过，可提交
- **偏差 5%-10%**: 需调整后重新验证
- **偏差 > 10%**: 需重做，不得提交

> 详细规范请查看 [UI 验证规范](../ui/UI_VERIFICATION.md)

## 🎯 UI 规范速查

| 规范项 | 说明 |
|--------|------|
| 涨跌色 | 红涨 `#CF1322`，绿跌 `#3F8600` |
| 数字格式 | 金额用万/亿，百分比带符号 |
| 图表高度 | 单折线 300-400px，组合图 400-500px |
| 卡片圆角 | 8px |
| 按钮高度 | 大 40px / 中 32px / 小 24px |

> 完整规范请查看 [UI 设计规范](../ui/CLAUDE.md)

---

## 🔗 相关文档

- [项目级规范](../CLAUDE.md)
- [后端开发实践](../backend/CLAUDE.md)
- [UI 设计规范](../ui/CLAUDE.md) ⭐
- [UI 验证规范](../ui/UI_VERIFICATION.md) ⭐⭐

---

**最后更新**: 2026-03-22
