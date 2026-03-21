# 家庭资产监控管理平台 - 前端开发指南

## 📋 项目概述

**项目名称**: 家庭资产监控管理平台 (前端)
**技术栈**: React + TypeScript + Vite + Ant Design Pro + Zustand
**目标**: 实现家庭资产的配置、管理、监控一体化前端界面

**核心目标**:
- 短期目标: 家庭资产的配置、管理、监控一体化
- 长期目标: 建设完善的投资体系，实现长期年化10%收益率，最大回撤控制在8%以内

---

## 🛠️ 技术栈

### 核心框架
- **React**: 18.x+
- **TypeScript**: 5.x+
- **Vite**: 5.x+ (构建工具)

### UI框架
- **Ant Design Pro**: 企业级UI组件库
- **Ant Design**: 基础组件库
- **Tailwind CSS**: 原子化CSS框架
- **CSS Modules**: 组件级样式隔离

### 状态管理
- **Zustand**: 轻量级状态管理库

### 数据可视化
- **ECharts**: 专业金融图表库
- **Recharts**: React图表库（备选）

### 网络请求
- **Axios**: HTTP客户端

### 路由
- **React Router v6**: 前端路由管理

### 工具库
- **Day.js**: 日期处理
- **Numeral.js**: 数字格式化
- **Lodash**: 工具函数库
- **React Query**: 服务端状态管理（可选）

### 代码质量
- **ESLint**: 代码检查
- **Prettier**: 代码格式化
- **Husky**: Git钩子
- **Commitlint**: 提交信息规范

---

## 📁 项目结构

```
asset-frontend/
├── src/
│   ├── api/                    # API接口层
│   │   ├── axios.ts           # Axios实例配置
│   │   ├── interceptors.ts    # 请求/响应拦截器
│   │   ├── types.ts           # API类型定义
│   │   ├── asset.ts           # 资产相关API
│   │   ├── position.ts        # 持仓相关API
│   │   ├── transaction.ts     # 交易相关API
│   │   ├── rebalance.ts       # 动态平衡API
│   │   ├── opportunity.ts     # 机会发现API
│   │   ├── strategy.ts        # 策略分析API
│   │   ├── risk.ts            # 风险管理API
│   │   └── report.ts          # 报表API
│   │
│   ├── assets/                 # 静态资源
│   │   ├── images/
│   │   ├── icons/
│   │   └── fonts/
│   │
│   ├── components/            # 公共组件
│   │   ├── charts/            # 图表组件
│   │   │   ├── LineChart.tsx           # 折线图
│   │   │   ├── PieChart.tsx            # 饼图
│   │   │   ├── BarChart.tsx            # 柱状图
│   │   │   ├── HeatmapChart.tsx        # 热力图
│   │   │   ├── CombinedChart.tsx       # 组合图
│   │   │   └── index.ts
│   │   ├── cards/             # 卡片组件
│   │   │   ├── AssetCard.tsx           # 资产卡片
│   │   │   ├── MetricCard.tsx          # 指标卡片
│   │   │   ├── PositionCard.tsx        # 持仓卡片
│   │   │   └── index.ts
│   │   ├── tables/           # 表格组件
│   │   │   ├── PositionTable.tsx       # 持仓表格
│   │   │   ├── TransactionTable.tsx    # 交易表格
│   │   │   ├── DataTable.tsx           # 数据表格
│   │   │   └── index.ts
│   │   ├── forms/            # 表单组件
│   │   │   ├── PositionForm.tsx        # 持仓表单
│   │   │   ├── TransactionForm.tsx     # 交易表单
│   │   │   └── index.ts
│   │   ├── layout/           # 布局组件
│   │   │   ├── PageLayout.tsx          # 页面布局
│   │   │   ├── PageHeader.tsx          # 页面头部
│   │   │   ├── PageContent.tsx         # 页面内容
│   │   │   └── index.ts
│   │   ├── common/           # 通用组件
│   │   │   ├── Loading.tsx             # 加载组件
│   │   │   ├── Empty.tsx               # 空状态
│   │   │   ├── ErrorBoundary.tsx      # 错误边界
│   │   │   ├── ConfirmDialog.tsx      # 确认对话框
│   │   │   └── index.ts
│   │   └── index.ts
│   │
│   ├── pages/                # 页面组件
│   │   ├── Dashboard/        # 仪表盘
│   │   │   ├── Overview/              # 总览看板
│   │   │   │   ├── index.tsx
│   │   │   │   ├── AssetSummary.tsx   # 总资产卡片
│   │   │   │   ├── ModuleAllocation.tsx # 四象限配置图
│   │   │   │   ├── MetricCards.tsx    # 核心指标卡片区
│   │   │   │   └── ReturnCurve.tsx    # 收益曲线图
│   │   │   ├── ModuleDetail/          # 模块详情页
│   │   │   │   ├── index.tsx
│   │   │   │   ├── ModuleOverview.tsx # 模块概览
│   │   │   │   ├── ModulePositions.tsx # 模块持仓列表
│   │   │   │   └── ModuleAnalysis.tsx  # 模块分析
│   │   │   └── index.ts
│   │   │
│   │   ├── Portfolio/        # 资产管理
│   │   │   ├── Positions/     # 持仓管理
│   │   │   │   ├── index.tsx
│   │   │   │   ├── PositionList.tsx   # 持仓列表
│   │   │   │   ├── PositionDetail.tsx # 持仓详情弹窗
│   │   │   │   ├── PositionAdjust.tsx # 持仓调整
│   │   │   │   └── PositionCompare.tsx # 持仓对比
│   │   │   ├── Transactions/  # 交易管理
│   │   │   │   ├── index.tsx
│   │   │   │   ├── TransactionList.tsx # 交易记录列表
│   │   │   │   ├── TransactionForm.tsx # 新建交易表单
│   │   │   │   ├── TransactionStats.tsx # 交易统计
│   │   │   │   └── PendingOrders.tsx  # 待执行交易
│   │   │   └── index.ts
│   │   │
│   │   ├── Opportunity/      # 机会发现
│   │   │   ├── Universe/      # 投资标的库
│   │   │   │   ├── index.tsx
│   │   │   │   ├── UniverseList.tsx   # 标的库列表
│   │   │   │   ├── UniverseDetail.tsx # 标的详情页
│   │   │   │   ├── SmartRecommendation.tsx # 智能推荐
│   │   │   │   └── MarketData.tsx     # 市场数据
│   │   │   ├── Alerts/        # 机会提醒
│   │   │   │   ├── index.tsx
│   │   │   │   ├── AlertList.tsx      # 机会列表
│   │   │   │   ├── AlertSettings.tsx  # 提醒设置
│   │   │   │   └── AlertEvaluation.tsx # 机会评估
│   │   │   └── index.ts
│   │   │
│   │   ├── Strategy/         # 策略分析
│   │   │   ├── Config/        # 投资策略配置
│   │   │   │   ├── index.tsx
│   │   │   │   ├── StrategyList.tsx    # 策略列表
│   │   │   │   ├── StrategyDetail.tsx # 策略详情
│   │   │   │   ├── StrategyParams.tsx # 参数配置
│   │   │   │   └── StrategyEvaluation.tsx # 策略评估
│   │   │   ├── Backtest/      # 回测分析
│   │   │   │   ├── index.tsx
│   │   │   │   ├── BacktestConfig.tsx # 回测配置
│   │   │   │   ├── BacktestResult.tsx # 回测结果
│   │   │   │   └── StrategyCompare.tsx # 策略对比
│   │   │   └── index.ts
│   │   │
│   │   ├── Risk/            # 风险管理
│   │   │   ├── Monitor/       # 风险监控
│   │   │   │   ├── index.tsx
│   │   │   │   ├── RiskDashboard.tsx   # 风险仪表盘
│   │   │   │   ├── RiskMetrics.tsx     # 风险指标
│   │   │   │   ├── CorrelationMatrix.tsx # 相关性矩阵
│   │   │   │   └── Concentration.tsx   # 集中度分析
│   │   │   ├── StressTest/   # 压力测试
│   │   │   │   ├── index.tsx
│   │   │   │   ├── ScenarioSettings.tsx # 场景设置
│   │   │   │   ├── TestResults.tsx     # 测试结果
│   │   │   │   └── StressReport.tsx    # 压力测试报告
│   │   │   └── index.ts
│   │   │
│   │   ├── Rebalance/       # 动态平衡
│   │   │   ├── Monitor/      # 平衡监控
│   │   │   │   ├── index.tsx
│   │   │   │   ├── BalanceStatus.tsx    # 平衡状态
│   │   │   │   ├── BalanceHistory.tsx   # 平衡历史
│   │   │   │   └── BalanceSettings.tsx  # 平衡设置
│   │   │   ├── Execute/      # 平衡执行
│   │   │   │   ├── index.tsx
│   │   │   │   ├── PlanGenerator.tsx    # 平衡方案生成
│   │   │   │   ├── PlanAdjust.tsx       # 方案调整
│   │   │   │   ├── PlanConfirm.tsx      # 方案确认
│   │   │   │   └── ExecutionMonitor.tsx # 执行监控
│   │   │   └── index.ts
│   │   │
│   │   ├── Report/          # 报表系统
│   │   │   ├── index.tsx
│   │   │   ├── MonthlyReport.tsx
│   │   │   ├── QuarterlyReport.tsx
│   │   │   ├── YearlyReport.tsx
│   │   │   └── CustomReport.tsx
│   │   │
│   │   └── Settings/        # 系统设置
│   │       ├── index.tsx
│   │       ├── AccountSettings.tsx
│   │       ├── NotificationSettings.tsx
│   │       ├── AutomationSettings.tsx
│   │       └── SystemSettings.tsx
│   │
│   ├── stores/              # 状态管理 (Zustand)
│   │   ├── index.ts
│   │   ├── assetStore.ts           # 资产状态
│   │   ├── positionStore.ts        # 持仓状态
│   │   ├── transactionStore.ts     # 交易状态
│   │   ├── opportunityStore.ts      # 机会状态
│   │   ├── strategyStore.ts         # 策略状态
│   │   ├── riskStore.ts             # 风险状态
│   │   ├── rebalanceStore.ts        # 平衡状态
│   │   └── uiStore.ts              # UI状态
│   │
│   ├── types/               # TypeScript类型定义
│   │   ├── index.ts
│   │   ├── asset.ts
│   │   ├── position.ts
│   │   ├── transaction.ts
│   │   ├── opportunity.ts
│   │   ├── strategy.ts
│   │   ├── risk.ts
│   │   ├── rebalance.ts
│   │   ├── report.ts
│   │   ├── common.ts
│   │   └── api.ts
│   │
│   ├── utils/               # 工具函数
│   │   ├── index.ts
│   │   ├── format.ts              # 格式化工具
│   │   ├── calculate.ts           # 计算工具
│   │   ├── validation.ts          # 验证工具
│   │   ├── date.ts                # 日期工具
│   │   ├── number.ts              # 数字工具
│   │   ├── storage.ts             # 本地存储
│   │   ├── chart.ts               # 图表工具
│   │   └── export.ts              # 导出工具
│   │
│   ├── constants/           # 常量定义
│   │   ├── index.ts
│   │   ├── modules.ts             # 四象限模块
│   │   ├── markets.ts             # 市场类型
│   │   ├── assetTypes.ts          # 资产类型
│   │   ├── transactionTypes.ts    # 交易类型
│   │   ├── riskLevels.ts          # 风险等级
│   │   ├── alertTypes.ts          # 提醒类型
│   │   ├── config.ts              # 配置常量
│   │   └── charts.ts              # 图表配置
│   │
│   ├── hooks/               # 自定义Hooks
│   │   ├── index.ts
│   │   ├── useRequest.ts          # 请求Hook
│   │   ├── useWebSocket.ts        # WebSocket Hook
│   │   ├── useChart.ts            # 图表Hook
│   │   ├── useDebounce.ts         # 防抖Hook
│   │   ├── useLocalStorage.ts     # 本地存储Hook
│   │   └── usePagination.ts       # 分页Hook
│   │
│   ├── layouts/             # 布局组件
│   │   ├── MainLayout.tsx
│   │   ├── AuthLayout.tsx
│   │   └── components/
│   │       ├── Header.tsx
│   │       ├── Sidebar.tsx
│   │       └── Footer.tsx
│   │
│   ├── router.tsx          # 路由配置
│   ├── App.tsx
│   └── main.tsx
│
├── public/                 # 公共资源
├── tests/                  # 测试文件
│   ├── unit/              # 单元测试
│   └── e2e/               # 端到端测试
│
├── .env                    # 环境变量
├── .env.development        # 开发环境变量
├── .env.production         # 生产环境变量
├── .eslintrc.cjs          # ESLint配置
├── .prettierrc            # Prettier配置
├── commitlint.config.js    # Commitlint配置
├── vite.config.ts         # Vite配置
├── tailwind.config.js      # Tailwind配置
├── tsconfig.json          # TypeScript配置
├── package.json           # 项目依赖
└── README.md
```

---

## 📐 四象限配置策略

| 模块 | 英文标识 | 目标占比 | 年化收益目标 | 风险特征 | 颜色 |
|------|---------|----------|-------------|----------|------|
| 🟩 红利 | dividend | 25% | 10% | 低波动，稳定分红 | green |
| 🟦 固收 | fixed | 25% | 3% | 回撤<1.5%，稳健 | blue |
| 🟪 成长 | growth | 25% | 15%+ | 高波动，高收益 | purple |
| 🟧 全天候 | allweather | 25% | 8% | 回撤~10%，平衡 | orange |

---

## 🚀 分阶段实施计划

### Phase 1: MVP (2-3个月)

**目标**: 基础资产管理功能上线

**任务清单**:
- [ ] 项目初始化
  - [ ] 创建Vite + React + TypeScript项目
  - [ ] 配置Tailwind CSS
  - [ ] 集成Ant Design Pro
  - [ ] 配置React Router v6
  - [ ] 配置Axios及拦截器
  - [ ] 配置ESLint + Prettier
  - [ ] 配置Husky + Commitlint

- [ ] 基础页面开发
  - [ ] 仪表盘页面
    - [ ] 总资产卡片组件
    - [ ] 今日盈亏展示
    - [ ] 累计收益展示
    - [ ] 收益曲线图 (ECharts)
  - [ ] 持仓列表页面
    - [ ] 持仓表格组件
    - [ ] 筛选/排序功能
    - [ ] 持仓详情弹窗
  - [ ] 交易记录页面
    - [ ] 交易流水表格
    - [ ] 新建交易表单
    - [ ] 交易统计卡片

- [ ] 状态管理
  - [ ] 创建Zustand stores
  - [ ] assetStore
  - [ ] positionStore
  - [ ] transactionStore

- [ ] API对接
  - [ ] 配置Axios基础设置
  - [ ] 实现请求/响应拦截器
  - [ ] 封装各模块API接口
  - [ ] 错误处理机制

**里程碑**:
- 能够记录持仓和交易
- 能够查看资产总览
- 能够计算基础收益
- 简单的Web界面

---

### Phase 2: 核心功能 (3-4个月)

**目标**: 四象限配置 + 动态平衡 + 机会发现

**任务清单**:
- [ ] 四象限配置模块
  - [ ] 四象限配置图 (饼图)
  - [ ] 目标vs实际对比
  - [ ] 偏离度预警
  - [ ] 点击下钻详情
  - [ ] 模块详情页
    - [ ] 模块概览卡片
    - [ ] 模块持仓列表
    - [ ] 模块分析图表

- [ ] 动态平衡模块
  - [ ] 平衡监控页面
    - [ ] 整体偏离度展示
    - [ ] 模块偏离度图表
    - [ ] 平衡建议列表
    - [ ] 平衡方案预览
  - [ ] 平衡执行页面
    - [ ] 平衡方案生成
    - [ ] 操作列表 (买入/卖出)
    - [ ] 方案确认弹窗
    - [ ] 执行进度追踪
  - [ ] 平衡历史记录

- [ ] 投资标的库
  - [ ] 标的库列表页
    - [ ] 多维度筛选器
    - [ ] 表格/卡片视图切换
  - [ ] 标的详情页
    - [ ] 基本信息卡片
    - [ ] 估值分析图表
    - [ ] 历史表现曲线
    - [ ] 分红记录表格
  - [ ] 智能推荐列表
  - [ ] 市场数据展示

- [ ] 机会提醒模块
  - [ ] 机会提醒页面
    - [ ] 按模块分组的机会列表
    - [ ] 优先级排序
    - [ ] 机会详情弹窗
  - [ ] 提醒设置表单
  - [ ] 机会评估展示

**里程碑**:
- 完整的四象限配置系统
- 自动生成平衡建议
- 基本的投资标的库
- 简单的机会提醒

---

### Phase 3: 高级功能 (3-4个月)

**目标**: 风险管理 + 策略分析 + 回测

**任务清单**:
- [ ] 风险管理模块
  - [ ] 风险仪表盘
    - [ ] 风险指标卡片
    - [ ] 风险热力图
    - [ ] 相关性矩阵
    - [ ] 风险预警列表
  - [ ] 波动率分析
  - [ ] 回撤分析
  - [ ] VaR分析
  - [ ] 集中度分析
  - [ ] 压力测试页面

- [ ] 策略分析模块
  - [ ] 策略配置页面
    - [ ] 策略列表
    - [ ] 参数配置表单 (四大策略)
    - [ ] 策略评估结果
  - [ ] 回测分析页面
    - [ ] 回测配置表单
    - [ ] 回测结果展示
    - [ ] 收益曲线对比
    - [ ] 关键指标表格
    - [ ] 策略对比图表

- [ ] 报表系统
  - [ ] 月度报告页面
  - [ ] 季度报告页面
  - [ ] 年度报告页面
  - [ ] 自定义报表生成器

- [ ] 通知系统前端
  - [ ] 通知设置页面
  - [ ] 通知历史列表
  - [ ] 实时通知提示

**里程碑**:
- 完善的风险监控
- 策略回测功能
- 自动化报表
- 多渠道通知设置

---

### Phase 4: 智能化 (4-6个月)

**目标**: 机器学习 + 自动化 + AI推荐

**任务清单**:
- [ ] 机器学习相关UI
  - [ ] 模型预测页面
    - [ ] 估值预测图表
    - [ ] 价格趋势预测
    - [ ] 风险预测展示
    - [ ] 机会优先级排序

- [ ] 自动化设置
  - [ ] 自动化配置页面
    - [ ] 数据采集设置
    - [ ] 自动平衡开关
    - [ ] 止损/止盈设置
    - [ ] 规则配置器

- [ ] AI推荐
  - [ ] AI推荐页面
    - [ ] 智能标的推荐卡片
    - [ ] 个性化建议列表
    - [ ] 市场解读文章
    - [ ] 研报摘要展示

- [ ] 高级分析
  - [ ] 因子分析页面
  - [ ] 归因分析页面
  - [ ] 蒙特卡洛模拟页面

**里程碑**:
- ML模型上线
- 自动化程度提升
- AI辅助决策
- 高级风险分析

---

### Phase 5: 优化完善 (持续)

**目标**: 用户体验 + 性能优化 + 功能扩展

**任务清单**:
- [ ] 用户体验优化
  - [ ] 移动端响应式适配
  - [ ] 暗黑模式切换
  - [ ] 自定义仪表盘布局
  - [ ] 快捷操作面板
  - [ ] 批量操作功能

- [ ] 性能优化
  - [ ] 虚拟滚动 (大数据列表)
  - [ ] 图表懒加载
  - [ ] API请求防抖/节流
  - [ ] 组件按需加载
  - [ ] PWA支持
  - [ ] CDN加速

- [ ] 功能扩展
  - [ ] 更多市场支持
  - [ ] 更多策略
  - [ ] 社区分享功能
  - [ ] 数据导出功能
  - [ ] 第三方集成

---

## 🔌 API对接规范

### 基础URL
- 开发环境: `http://localhost:8080/api`
- 生产环境: `${VITE_API_BASE_URL}/api`

### 请求拦截器
```typescript
// 自动添加认证Token
// 自动处理请求格式
```

### 响应拦截器
```typescript
// 统一处理错误码
// 统一处理业务异常
// Loading状态管理
```

### API接口分类

#### 资产相关 (asset)
```typescript
GET    /api/assets/overview          // 获取资产总览
GET    /api/assets/return-curve      // 获取收益曲线
GET    /api/assets/metrics          // 获取核心指标
GET    /api/assets/allocation       // 获取资产配置
```

#### 持仓相关 (position)
```typescript
GET    /api/positions               // 获取持仓列表
GET    /api/positions/:id          // 获取持仓详情
POST   /api/positions               // 创建持仓
PUT    /api/positions/:id          // 更新持仓
DELETE /api/positions/:id          // 删除持仓
POST   /api/positions/update-prices // 更新持仓价格
```

#### 交易相关 (transaction)
```typescript
GET    /api/transactions            // 获取交易列表
GET    /api/transactions/:id       // 获取交易详情
POST   /api/transactions           // 创建交易
GET    /api/transactions/stats     // 获取交易统计
```

#### 动态平衡 (rebalance)
```typescript
GET    /api/rebalance/status        // 获取平衡状态
POST   /api/rebalance/plan         // 生成平衡方案
POST   /api/rebalance/execute      // 执行平衡方案
GET    /api/rebalance/history      // 获取平衡历史
```

#### 机会发现 (opportunity)
```typescript
GET    /api/opportunity/universe    // 获取投资标的库
GET    /api/opportunity/universe/:symbol // 获取标的信息
GET    /api/opportunity/alerts      // 获取机会提醒
POST   /api/opportunity/alerts      // 创建提醒
PUT    /api/opportunity/alerts/:id  // 更新提醒
```

#### 策略分析 (strategy)
```typescript
GET    /api/strategy/list           // 获取策略列表
GET    /api/strategy/:id            // 获取策略详情
POST   /api/strategy/backtest       // 执行回测
GET    /api/strategy/compare        // 策略对比
```

#### 风险管理 (risk)
```typescript
GET    /api/risk/metrics            // 获取风险指标
GET    /api/risk/dashboard          // 风险仪表盘数据
POST   /api/risk/stress-test        // 执行压力测试
GET    /api/risk/correlation       // 相关性分析
```

#### 报表 (report)
```typescript
GET    /api/report/monthly/:year/:month     // 月度报告
GET    /api/report/quarterly/:year/:quarter // 季度报告
GET    /api/report/yearly/:year              // 年度报告
POST  /api/report/custom                   // 自定义报表
```

---

## 📊 数据模型 (TypeScript类型定义)

### Position (持仓)
```typescript
interface Position {
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
}
```

### Transaction (交易)
```typescript
interface Transaction {
  id: number;
  positionId?: number;
  symbol: string;
  transactionType: 'buy' | 'sell';
  shares: number;
  price: number;
  totalAmount: number;
  fee: number;
  currency: string;
  transactionDate: string;
  notes?: string;
  createTime: string;
}
```

### Module (四象限模块)
```typescript
interface Module {
  key: 'dividend' | 'fixed' | 'growth' | 'allweather';
  name: string;
  targetWeight: number; // 目标权重 25%
  currentValue: number; // 当前市值
  currentWeight: number; // 当前权重
  deviation: number; // 偏离度
  return: number; // 模块收益
}
```

### RebalancePlan (平衡方案)
```typescript
interface RebalancePlan {
  id: number;
  totalValue: number;
  rebalanceDate: string;
  triggerReason: string;
  actions: RebalanceAction[];
  estimatedFee: number;
  status: 'pending' | 'executed' | 'cancelled';
}

interface RebalanceAction {
  symbol: string;
  actionType: 'buy' | 'sell';
  shares: number;
  price: number;
  amount: number;
  reason: string;
}
```

### RiskMetrics (风险指标)
```typescript
interface RiskMetrics {
  metricDate: string;
  totalValue: number;
  dailyReturn: number;
  volatility30d: number;
  volatility90d: number;
  maxDrawdown: number;
  sharpeRatio: number;
  sortinoRatio: number;
  var95: number;
  var99: number;
  beta: number;
  alpha: number;
}
```

---

## 🎨 开发规范

### 组件命名规范
- **文件名**: PascalCase (如 `PositionTable.tsx`)
- **组件名**: PascalCase (如 `const PositionTable: React.FC = ...`)
- **变量/函数**: camelCase (如 `handleSort`)

### 文件组织规范
- 每个页面文件夹包含 `index.tsx` 作为入口
- 组件按功能分类存放
- 类型定义统一放在 `types/` 目录
- 工具函数统一放在 `utils/` 目录

### 代码风格规范
- 使用ESLint + Prettier
- 使用2空格缩进
- 使用单引号
- 语句末尾加分号
- 组件使用函数式组件 + Hooks

### Git提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
perf: 性能优化
test: 测试相关
chore: 构建/工具相关
```

### 注释规范
```typescript
/**
 * 组件功能描述
 * @param props - 组件属性
 */
const Component: React.FC<Props> = (props) => {
  // ...
};
```

---

## 📦 常用命令

### 开发
```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 类型检查
npm run type-check

# 代码检查
npm run lint

# 代码格式化
npm run format
```

### 构建
```bash
# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

### 测试
```bash
# 运行单元测试
npm run test

# 运行测试并生成覆盖率
npm run test:coverage

# 运行端到端测试
npm run test:e2e
```

---

## 🔑 关键技术要点

### 1. 状态管理 (Zustand)
```typescript
import { create } from 'zustand';

interface PositionStore {
  positions: Position[];
  loading: boolean;
  fetchPositions: () => Promise<void>;
  updatePosition: (id: number, data: Partial<Position>) => void;
}

const usePositionStore = create<PositionStore>((set) => ({
  positions: [],
  loading: false,
  fetchPositions: async () => {
    set({ loading: true });
    try {
      const data = await api.get('/positions');
      set({ positions: data, loading: false });
    } catch (error) {
      set({ loading: false });
    }
  },
  updatePosition: (id, data) => {
    set((state) => ({
      positions: state.positions.map((p) =>
        p.id === id ? { ...p, ...data } : p
      ),
    }));
  },
}));
```

### 2. API请求封装
```typescript
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
});

// 请求拦截器
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截器
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      // 处理未授权
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

### 3. ECharts图表配置
```typescript
import ReactECharts from 'echarts-for-react';

const LineChart: React.FC<{ data: number[] }> = ({ data }) => {
  const option = {
    tooltip: {
      trigger: 'axis',
    },
    xAxis: {
      type: 'category',
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'],
    },
    yAxis: {
      type: 'value',
    },
    series: [
      {
        data: data,
        type: 'line',
        smooth: true,
      },
    ],
  };

  return <ReactECharts option={option} style={{ height: '400px' }} />;
};
```

### 4. 自定义Hook
```typescript
import { useState, useEffect, useCallback } from 'react';

const useRequest = <T,>(
  apiFunc: () => Promise<T>,
  immediate = true
) => {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const execute = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const result = await apiFunc();
      setData(result);
    } catch (err) {
      setError(err as Error);
    } finally {
      setLoading(false);
    }
  }, [apiFunc]);

  useEffect(() => {
    if (immediate) {
      execute();
    }
  }, [immediate, execute]);

  return { data, loading, error, execute };
};
```

---

## ⚠️ 注意事项

1. **数据精度**: 金融数据使用 `number` 类型，注意浮点数精度问题，建议使用 `decimal.js` 库处理
2. **时间处理**: 统一使用 `Day.js` 处理日期时间，注意时区问题
3. **安全性**: 敏感信息不要在前端存储，使用Token机制进行认证
4. **性能优化**:
   - 大数据列表使用虚拟滚动
   - 图表使用懒加载
   - API请求使用防抖/节流
5. **错误处理**: 所有API请求都要有错误处理机制
6. **类型安全**: 充分利用TypeScript类型检查，避免使用 `any`
7. **响应式**: 确保移动端适配良好
8. **可访问性**: 遵循WCAG标准，提高可访问性

---

## 📞 联系方式

**后端API文档**: http://localhost:8080/swagger-ui.html
**技术支持**: 查看项目Wiki或提交Issue

---

## 📚 参考资料

- [React官方文档](https://react.dev/)
- [TypeScript官方文档](https://www.typescriptlang.org/)
- [Vite官方文档](https://vitejs.dev/)
- [Ant Design Pro官方文档](https://pro.ant.design/)
- [Zustand官方文档](https://zustand-demo.pmnd.rs/)
- [ECharts官方文档](https://echarts.apache.org/)
- [Tailwind CSS官方文档](https://tailwindcss.com/)

---

**最后更新**: 2026-03-21
**文档版本**: v1.0.0
