# 家庭资产监控管理平台建设方案
## — 基于多元化配置的专业资产管理解决方案 (Spring Boot + MySQL 版本)

---

## 📋 方案概述

### 核心目标
- **短期目标**: 家庭资产的配置、管理、监控一体化
- **长期目标**: 建设完善的投资体系，实现长期年化10%收益率，最大回撤控制在8%以内

### 四象限配置策略
| 模块 | 目标占比 | 年化收益目标 | 风险特征 |
|------|----------|-------------|----------|
| 🟩 红利 | 25% | 10% | 低波动，稳定分红 |
| 🟦 固收 | 25% | 3% | 回撤<1.5%，稳健 |
| 🟪 成长 | 25% | 15%+ | 高波动，高收益 |
| 🟧 全天候 | 25% | 8% | 回撤~10%，平衡 |

---

## 🏗️ 系统架构设计

### 整体架构图
```
┌─────────────────────────────────────────────────────────┐
│                    前端展示层                        │
│  ┌──────────┬──────────┬──────────┬──────────┐    │
│  │ 仪表盘   │ 资产管理 │ 策略分析 │ 系统设置 │    │
│  └──────────┴──────────┴──────────┴──────────┘    │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   业务逻辑层 (Spring Boot)              │
│  ┌──────────┬──────────┬──────────┬──────────┐    │
│  │资产管理  │ 风险控制  │ 机会发现 │ 动态平衡 │    │
│  └──────────┴──────────┴──────────┴──────────┘    │
│  ┌──────────┬──────────┬──────────┬──────────┐    │
│  │交易记录  │ 收益计算  │ 报表生成 │ 预警通知 │    │
│  └──────────┴──────────┴──────────┴──────────┘    │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                    数据访问层 (JPA/MyBatis)          │
│  ┌──────────┬──────────┬──────────┬──────────┐    │
│  │ 持仓数据 │ 交易记录 │ 历史价格 │ 策略参数 │    │
│  └──────────┴──────────┴──────────┴──────────┘    │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                    数据存储层                         │
│  ┌──────────┬──────────┬──────────┬──────────┐    │
│  │ MySQL    │ Redis    │ MinIO    │ ELK/Logstash │    │
│  └──────────┴──────────┴──────────┴──────────┘    │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   外部数据源                         │
│  ┌──────────┬──────────┬──────────┬──────────┐    │
│  │ A股行情  │ 美股行情 │ 港股行情 │ 基金净值 │    │
│  └──────────┴──────────┴──────────┴──────────┘    │
└─────────────────────────────────────────────────────────┘
```

### 技术栈推荐

#### 前端
- **框架**: React + TypeScript
- **状态管理**: Zustand / Redux Toolkit
- **UI组件库**: Ant Design Pro (金融场景成熟)
- **图表库**: ECharts / Recharts
- **样式**: Tailwind CSS + CSS Modules
- **HTTP客户端**: Axios
- **构建工具**: Vite

#### 后端
- **框架**: Spring Boot 3.x (Java 17+)
- **ORM框架**: Spring Data JPA + Hibernate
- **MyBatis Plus**: 复杂查询优化
- **API文档**: SpringDoc OpenAPI (Swagger)
- **任务调度**: Spring Scheduler / XXL-Job
- **缓存**: Spring Cache + Redis
- **消息队列**: RabbitMQ / RocketMQ
- **安全认证**: Spring Security + JWT
- **参数校验**: Hibernate Validator
- **日志**: SLF4J + Logback

#### 数据库
- **主库**: MySQL 8.0+ (InnoDB引擎，支持事务)
- **缓存**: Redis 7.x (实时行情、会话、热点数据)
- **文件存储**: MinIO (本地对象存储，支持报表导出)
- **日志存储**: ELK Stack (Elasticsearch + Logstash + Kibana)

#### 中间件
- **API网关**: Spring Cloud Gateway (微服务场景)
- **服务注册**: Nacos / Eureka
- **配置中心**: Nacos / Spring Cloud Config
- **熔断限流**: Sentinel / Resilience4j
- **链路追踪**: SkyWalking / Zipkin

#### DevOps
- **容器化**: Docker + Docker Compose
- **CI/CD**: Jenkins / GitLab CI
- **监控**: Prometheus + Grafana
- **告警**: AlertManager

---

## 📊 数据模型设计

### 1. 持仓表 (positions)
```sql
CREATE TABLE `positions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `symbol` VARCHAR(50) NOT NULL COMMENT '标的代码',
    `name` VARCHAR(200) DEFAULT NULL COMMENT '标的名称',
    `module` VARCHAR(20) NOT NULL COMMENT '所属模块: dividend/fixed/growth/allweather',
    `market` VARCHAR(10) NOT NULL COMMENT '市场: CN/US/HK',
    `asset_type` VARCHAR(20) DEFAULT NULL COMMENT '资产类型: stock/etf/fund/bond',
    `shares` DECIMAL(18, 4) DEFAULT NULL COMMENT '持仓数量',
    `avg_cost` DECIMAL(18, 4) DEFAULT NULL COMMENT '平均成本',
    `current_price` DECIMAL(18, 4) DEFAULT NULL COMMENT '当前价格',
    `current_value` DECIMAL(18, 4) DEFAULT NULL COMMENT '当前市值',
    `target_weight` DECIMAL(5, 2) DEFAULT NULL COMMENT '目标权重',
    `buy_price_threshold` DECIMAL(18, 4) DEFAULT NULL COMMENT '买入价格阈值',
    `sell_price_threshold` DECIMAL(18, 4) DEFAULT NULL COMMENT '卖出价格阈值',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态: active/inactive',
    `remarks` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_symbol` (`symbol`),
    KEY `idx_module` (`module`),
    KEY `idx_market` (`market`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='持仓表';
```

### 2. 交易记录表 (transactions)
```sql
CREATE TABLE `transactions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `position_id` BIGINT DEFAULT NULL COMMENT '持仓ID',
    `symbol` VARCHAR(50) NOT NULL COMMENT '标的代码',
    `transaction_type` VARCHAR(10) NOT NULL COMMENT '交易类型: buy/sell',
    `shares` DECIMAL(18, 4) NOT NULL COMMENT '交易数量',
    `price` DECIMAL(18, 4) NOT NULL COMMENT '成交价格',
    `total_amount` DECIMAL(18, 4) NOT NULL COMMENT '成交金额',
    `fee` DECIMAL(18, 4) DEFAULT 0.0000 COMMENT '手续费',
    `currency` VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
    `transaction_date` DATE NOT NULL COMMENT '交易日期',
    `notes` VARCHAR(1000) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`),
    KEY `idx_symbol` (`symbol`),
    KEY `idx_transaction_date` (`transaction_date`),
    KEY `idx_position_id` (`position_id`),
    KEY `idx_transaction_type` (`transaction_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易记录表';
```

### 3. 历史行情表 (price_history)
```sql
CREATE TABLE `price_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `symbol` VARCHAR(50) NOT NULL COMMENT '标的代码',
    `trade_date` DATE NOT NULL COMMENT '交易日期',
    `open_price` DECIMAL(18, 4) DEFAULT NULL COMMENT '开盘价',
    `close_price` DECIMAL(18, 4) DEFAULT NULL COMMENT '收盘价',
    `high_price` DECIMAL(18, 4) DEFAULT NULL COMMENT '最高价',
    `low_price` DECIMAL(18, 4) DEFAULT NULL COMMENT '最低价',
    `volume` BIGINT DEFAULT NULL COMMENT '成交量',
    `turnover` DECIMAL(18, 2) DEFAULT NULL COMMENT '成交额',
    `adj_close_price` DECIMAL(18, 4) DEFAULT NULL COMMENT '复权收盘价',
    `dividend` DECIMAL(18, 4) DEFAULT 0.0000 COMMENT '分红',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_symbol_date` (`symbol`, `trade_date`),
    KEY `idx_symbol` (`symbol`),
    KEY `idx_trade_date` (`trade_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='历史行情表';
```

### 4. 投资标的库表 (investment_universe)
```sql
CREATE TABLE `investment_universe` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `symbol` VARCHAR(50) NOT NULL COMMENT '标的代码',
    `name` VARCHAR(200) DEFAULT NULL COMMENT '标的名称',
    `module` VARCHAR(20) NOT NULL COMMENT '所属模块',
    `market` VARCHAR(10) NOT NULL COMMENT '市场: CN/US/HK',
    `asset_type` VARCHAR(20) DEFAULT NULL COMMENT '资产类型: stock/etf/fund/bond',
    `isin_code` VARCHAR(20) DEFAULT NULL COMMENT 'ISIN编码',
    `description` TEXT COMMENT '描述',
    `risk_level` VARCHAR(10) DEFAULT 'medium' COMMENT '风险等级: low/medium/high',
    `dividend_yield` DECIMAL(5, 2) DEFAULT NULL COMMENT '股息率(%)',
    `pe_ratio` DECIMAL(10, 2) DEFAULT NULL COMMENT '市盈率',
    `pb_ratio` DECIMAL(10, 2) DEFAULT NULL COMMENT '市净率',
    `ps_ratio` DECIMAL(10, 2) DEFAULT NULL COMMENT '市销率',
    `market_cap` BIGINT DEFAULT NULL COMMENT '市值',
    `expense_ratio` DECIMAL(5, 4) DEFAULT NULL COMMENT '费率(%)',
    `tracking_error` DECIMAL(5, 2) DEFAULT NULL COMMENT '跟踪误差(%)',
    `strategy` VARCHAR(50) DEFAULT NULL COMMENT '投资策略',
    `valuation_percentile` DECIMAL(5, 2) DEFAULT NULL COMMENT '估值百分位',
    `dividend_growth_rate` DECIMAL(5, 2) DEFAULT NULL COMMENT '分红增长率(%)',
    `revenue_growth_rate` DECIMAL(5, 2) DEFAULT NULL COMMENT '收入增长率(%)',
    `profit_growth_rate` DECIMAL(5, 2) DEFAULT NULL COMMENT '利润增长率(%)',
    `roe` DECIMAL(5, 2) DEFAULT NULL COMMENT 'ROE(%)',
    `fcf_yield` DECIMAL(5, 2) DEFAULT NULL COMMENT 'FCF收益率(%)',
    `status` VARCHAR(20) DEFAULT 'watching' COMMENT '状态: watching/invested/excluded',
    `priority` INT DEFAULT 0 COMMENT '优先级',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签(JSON数组)',
    `last_reviewed_at` DATETIME DEFAULT NULL COMMENT '最后审核时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_symbol` (`symbol`),
    KEY `idx_module` (`module`),
    KEY `idx_market` (`market`),
    KEY `idx_status` (`status`),
    KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投资标的库';
```

### 5. 策略参数表 (strategy_params)
```sql
CREATE TABLE `strategy_params` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `module` VARCHAR(20) NOT NULL COMMENT '所属模块',
    `param_key` VARCHAR(50) NOT NULL COMMENT '参数键',
    `param_value` JSON NOT NULL COMMENT '参数值(JSON)',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `version` VARCHAR(20) DEFAULT '1.0' COMMENT '版本',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_module_key` (`module`, `param_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='策略参数表';
```

### 6. 风险指标表 (risk_metrics)
```sql
CREATE TABLE `risk_metrics` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `metric_date` DATE NOT NULL COMMENT '指标日期',
    `total_value` DECIMAL(18, 4) DEFAULT NULL COMMENT '总资产',
    `daily_return` DECIMAL(10, 4) DEFAULT NULL COMMENT '日收益率',
    `volatility_30d` DECIMAL(10, 4) DEFAULT NULL COMMENT '30日波动率',
    `volatility_90d` DECIMAL(10, 4) DEFAULT NULL COMMENT '90日波动率',
    `max_drawdown` DECIMAL(10, 4) DEFAULT NULL COMMENT '最大回撤',
    `sharpe_ratio` DECIMAL(10, 4) DEFAULT NULL COMMENT '夏普比率',
    `sortino_ratio` DECIMAL(10, 4) DEFAULT NULL COMMENT '索提诺比率',
    `var_95` DECIMAL(18, 4) DEFAULT NULL COMMENT 'VaR(95%)',
    `var_99` DECIMAL(18, 4) DEFAULT NULL COMMENT 'VaR(99%)',
    `beta` DECIMAL(10, 4) DEFAULT NULL COMMENT 'Beta',
    `alpha` DECIMAL(10, 4) DEFAULT NULL COMMENT 'Alpha',
    `correlation_matrix` JSON DEFAULT NULL COMMENT '相关性矩阵',
    `sector_distribution` JSON DEFAULT NULL COMMENT '行业分布',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_metric_date` (`metric_date`),
    KEY `idx_metric_date` (`metric_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='风险指标表';
```

### 7. 动态平衡记录表 (rebalance_history)
```sql
CREATE TABLE `rebalance_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `balance_date` DATE NOT NULL COMMENT '平衡日期',
    `total_value_before` DECIMAL(18, 4) DEFAULT NULL COMMENT '平衡前总资产',
    `total_value_after` DECIMAL(18, 4) DEFAULT NULL COMMENT '平衡后总资产',
    `trigger_reason` VARCHAR(100) DEFAULT NULL COMMENT '触发原因',
    `actions` JSON NOT NULL COMMENT '调仓操作(JSON)',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending/executed/cancelled',
    `executed_at` DATETIME DEFAULT NULL COMMENT '执行时间',
    `notes` VARCHAR(1000) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`),
    KEY `idx_balance_date` (`balance_date`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态平衡记录表';
```

### 8. 机会提醒表 (opportunity_alerts)
```sql
CREATE TABLE `opportunity_alerts` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `symbol` VARCHAR(50) NOT NULL COMMENT '标的代码',
    `alert_type` VARCHAR(20) NOT NULL COMMENT '提醒类型: value_buy/value_sell/dividend/technical',
    `alert_content` VARCHAR(500) DEFAULT NULL COMMENT '提醒内容',
    `alert_level` VARCHAR(20) DEFAULT 'info' COMMENT '提醒级别: info/warning/critical',
    `current_value` DECIMAL(18, 4) DEFAULT NULL COMMENT '当前值',
    `target_value` DECIMAL(18, 4) DEFAULT NULL COMMENT '目标值',
    `is_read` TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    `is_processed` TINYINT(1) DEFAULT 0 COMMENT '是否已处理',
    `process_result` VARCHAR(200) DEFAULT NULL COMMENT '处理结果',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_symbol` (`symbol`),
    KEY `idx_alert_type` (`alert_type`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='机会提醒表';
```

---

## 🎯 核心功能模块

### 模块1: 资产仪表盘 (Dashboard)

#### 1.1 总览看板
```
功能点:
├── 总资产卡片
│   ├── 当前净值
│   ├── 今日盈亏
│   ├── 累计收益
│   ├── 年化收益
│   └── vs基准对比
├── 四象限配置图
│   ├── 饼图展示
│   ├── 目标vs实际对比
│   ├── 偏离度预警
│   └── 点击下钻详情
├── 核心指标卡片区
│   ├── 最大回撤
│   ├── 夏普比率
│   ├── 波动率 (30/90日)
│   ├── 集中度风险
│   └── VaR (95%/99%)
└── 收益曲线图
    ├── 资产净值曲线
    ├── 基准对比曲线
    ├── 回撤曲线
    └── 多周期切换 (7日/30日/90日/1年/全部)
```

#### 1.2 模块详情页
```
功能点:
├── 模块概览
│   ├── 当前市值
│   ├── 目标市值
│   ├── 偏离度
│   ├── 模块收益
│   └── 模块风险指标
├── 持仓列表
│   ├── 标的基本信息
│   ├── 持仓数量/成本
│   ├── 当前市值/盈亏
│   ├── 收益率
│   ├── 占比
│   └── 操作按钮 (买入/卖出/详情)
└── 模块分析
    ├── 行业分布
    ├── 地域分布
    ├── 相关性分析
    └── 历史表现对比
```

---

### 模块2: 资产管理 (Portfolio Management)

#### 2.1 持仓管理
```
功能点:
├── 持仓列表
│   ├── 支持筛选 (模块/市场/资产类型)
│   ├── 支持排序 (市值/收益/占比等)
│   ├── 持仓详情弹窗
│   │   ├── 基本信息
│   │   ├── 持仓历史
│   │   ├── 收益分析
│   │   └── 操作记录
│   └── 快速操作 (买入/卖出/调整目标权重)
├── 持仓调整
│   ├── 调整目标权重
│   ├── 设置买入/卖出价格阈值
│   ├── 批量调整
│   └── 调整预览 (预估影响)
└── 持仓对比
    ├── vs同类持仓
    ├── vs指数表现
    └── vs历史高点/低点
```

#### 2.2 交易管理
```
功能点:
├── 交易记录
│   ├── 交易流水列表
│   ├── 支持筛选 (类型/日期/标的)
│   ├── 交易统计
│   │   ├── 买入次数/金额
│   │   ├── 卖出次数/金额
│   │   ├── 手续费统计
│   │   └── 盈亏统计
│   └── 导出功能
├── 新建交易
│   ├── 选择持仓 (或新建)
│   ├── 选择交易类型 (买入/卖出)
│   ├── 输入数量/价格
│   ├── 计算预估费用
│   ├── 输入备注
│   └── 确认提交
└── 待执行交易
    ├── 限价单列表
    ├── 触发条件列表
    ├── 手动取消
    └── 自动提醒
```

---

### 模块3: 机会发现 (Opportunity Discovery)

#### 3.1 投资标的库
```
功能点:
├── 标的库列表
│   ├── 支持筛选
│   │   ├── 模块筛选
│   │   ├── 市场筛选
│   │   ├── 资产类型筛选
│   │   ├── 状态筛选 (观察/已持有/已排除)
│   │   └── 自定义条件 (股息率/估值等)
│   ├── 支持排序
│   │   ├── 优先级
│   │   ├── 股息率
│   │   ├── 估值百分位
│   │   ├── 近期表现
│   │   └── 综合评分
│   ├── 标的详情页
│   │   ├── 基本信息
│   │   ├── 估值分析
│   │   ├── 历史表现
│   │   ├── 分红记录
│   │   ├── 相关新闻
│   │   ├── 研报分析
│   │   └── 操作按钮 (加入持有/调整优先级/添加备注)
│   └── 批量操作
│       ├── 批量加入关注
│       ├── 批量排除
│       └── 批量导出
├── 智能推荐
│   ├── 基于策略推荐
│   ├── 基于市场情况推荐
│   ├── 基于历史表现推荐
│   └── 推荐原因说明
└── 市场数据
    ├── 市场热力图
    ├── 板块轮动图
    ├── 资金流向图
    └── 情绪指标
```

#### 3.2 投资机会提醒
```
功能点:
├── 机会列表
│   ├── 按模块分组
│   ├── 按优先级排序
│   ├── 机会详情
│   │   ├── 标的信息
│   │   ├── 触发条件
│   │   ├── 建议操作
│   │   └── 预期收益
│   └── 快速操作 (标记已读/忽略/执行)
├── 提醒设置
│   ├── 价格提醒
│   ├── 估值提醒
│   ├── 分红提醒
│   ├── 新闻提醒
│   └── 自定义提醒
└── 机会评估
    ├── 风险评估
    ├── 收益预期
    ├── 适合度评分
    └── 历史回测
```

---

### 模块4: 策略分析 (Strategy Analysis)

#### 4.1 投资策略配置
```
功能点:
├── 策略列表
│   ├── 按模块展示
│   ├── 策略详情
│   │   ├── 策略描述
│   │   ├── 参数配置
│   │   ├── 历史回测
│   │   ├── 实战表现
│   │   └── 适用条件
│   └── 策略启用/禁用
├── 参数配置
│   ├── 红利策略
│   │   ├── 最低股息率
│   │   ├── PE百分位上限
│   │   ├── FCF收益率下限
│   │   ├── 分红增长率要求
│   │   └── 波动率上限
│   ├── 固收策略
│   │   ├── 信用评级下限
│   │   ├── 剩余期限范围
│   │   ├── 久期范围
│   │   ├── 利率敏感性
│   │   └── 流动性要求
│   ├── 成长策略
│   │   ├── PE百分位上限
│   │   ├── 收入增长率下限
│   │   ├── ROE下限
│   │   ├── 行业集中度限制
│   │   └── 估值上限
│   └── 全天候策略
│       ├── 股债比例
│       ├── 黄金比例
│       ├── 大宗商品比例
│       ├── 再平衡频率
│       └── 偏离度阈值
└── 策略评估
    ├── 回测分析
    ├── 风险收益比
    ├── 稳定性测试
    └── 优化建议
```

#### 4.2 回测分析
```
功能点:
├── 回测配置
│   ├── 选择策略
│   ├── 选择时间区间
│   ├── 选择基准
│   ├── 选择标的池
│   ├── 初始资金
│   ├── 手续费设置
│   └── 开始回测
├── 回测结果
│   ├── 收益曲线
│   ├── 回撤曲线
│   ├── 关键指标
│   │   ├── 年化收益
│   │   ├── 累计收益
│   │   ├── 最大回撤
│   │   ├── 夏普比率
│   │   ├── 胜率
│   │   └── 盈亏比
│   ├── 月度/年度收益
│   ├── 交易明细
│   └── 导出报告
└── 策略对比
    ├── 多策略对比
    ├── 滚动回测
    ├── 蒙特卡洛模拟
    └── 参数优化
```

---

### 模块5: 风险控制 (Risk Management)

#### 5.1 风险监控
```
功能点:
├── 风险仪表盘
│   ├── 组合风险概览
│   ├── 风险热力图
│   ├── 风险预警列表
│   └── 风险趋势图
├── 风险指标
│   ├── 波动率分析
│   │   ├── 30日波动率
│   │   ├── 90日波动率
│   │   ├── 波动率趋势
│   │   └── vs基准对比
│   ├── 回撤分析
│   │   ├── 当前回撤
│   │   ├── 最大回撤
│   │   ├── 回撤分布
│   │   └── 回撤持续时间
│   ├── VaR分析
│   │   ├── VaR (95%)
│   │   ├── VaR (99%)
│   │   ├── CVaR
│   │   └── VaR历史趋势
│   ├── 相关性分析
│   │   ├── 持仓相关矩阵
│   │   ├── 模块相关矩阵
│   │   ├── vs基准相关性
│   │   └── 相关性趋势
│   └── 集中度分析
│       ├── 持仓集中度
│       ├── 行业集中度
│       ├── 地域集中度
│       └── 集中度预警
└── 风险预警
    ├── 预警规则配置
    │   ├── 波动率阈值
    │   ├── 回撤阈值
    │   ├── 集中度阈值
    │   ├── 单日亏损阈值
    │   └── 自定义规则
    ├── 预警通知
    │   ├── 邮件通知
    │   ├── 短信通知
    │   ├── App推送
    │   └── 微信通知
    └── 预警历史
```

#### 5.2 压力测试
```
功能点:
├── 场景设置
│   ├── 预设场景
│   │   ├── 2008年金融危机
│   │   ├── 2020年疫情冲击
│   │   ├── 美股崩盘
│   │   ├── A股熔断
│   │   └── 利率上升
│   └── 自定义场景
│       ├── 市场跌幅
│       ├── 利率变化
│       ├── 汇率变化
│       ├── 通胀变化
│       └── 行业冲击
├── 测试结果
│   ├── 预估损失
│   ├── 模块影响
│   ├── 持仓影响
│   └── 敏感性分析
└── 压力测试报告
    ├── 测试场景
    ├── 测试结果
    ├── 风险暴露
    ├── 应对建议
    └── 导出PDF
```

---

### 模块6: 动态平衡 (Dynamic Rebalancing)

#### 6.1 平衡监控
```
功能点:
├── 平衡状态
│   ├── 整体偏离度
│   ├── 模块偏离度
│   ├── 平衡建议
│   └── 平衡操作预览
├── 平衡历史
│   ├── 历史平衡记录
│   ├── 平衡效果分析
│   │   ├── 平衡前后对比
│   │   ├── 收益影响
│   │   └── 风险影响
│   └── 平衡成本统计
└── 平衡设置
    ├── 平衡周期 (月度/季度/半年/年度)
    ├── 偏离度阈值 (5%/10%/15%)
    ├── 平衡方式 (手动/自动/半自动)
    │   ├── 手动: 仅提醒
    │   ├── 自动: 自动执行
    │   └── 半自动: 生成方案，人工确认
    └── 交易规则
        ├── 最小交易金额
        ├── 优先调出模块
        ├── 优先调入模块
        └── 税务优化
```

#### 6.2 平衡执行
```
功能点:
├── 平衡方案生成
│   ├── 当前配置分析
│   ├── 偏离度计算
│   ├── 调仓建议
│   │   ├── 卖出标的
│   │   │   ├── 选择依据 (超配/表现差/高估)
│   │   │   ├── 卖出数量
│   │   │   ├── 预估收益
│   │   │   └── 交易成本
│   │   └── 买入标的
│   │       ├── 选择依据 (低配/低估/高优先级)
│   │       ├── 买入数量
│   │       ├── 预估成本
│   │       └── 交易成本
│   └── 总体评估
│       ├── 预估手续费
│       ├── 预估滑点
│       ├── 净收益影响
│       └── 风险改善
├── 方案调整
│   ├── 手动调整操作
│   ├── 添加/删除操作
│   ├── 调整数量
│   └── 方案对比
├── 方案确认
│   ├── 方案预览
│   ├── 风险提示
│   ├── 成本估算
│   └── 税务影响
└── 执行监控
    ├── 操作进度
    ├── 成交反馈
    ├── 执行结果
    └── 执行报告
```

---

## 💻 Spring Boot 项目结构

### 标准项目结构
```
asset-management/
├── asset-api/                    # API模块
│   ├── src/main/java/com/asset/
│   │   ├── api/                   # API接口
│   │   ├── dto/                   # 数据传输对象
│   │   │   ├── request/           # 请求DTO
│   │   │   └── response/          # 响应DTO
│   │   └── config/               # 配置类
│   └── pom.xml
├── asset-service/                 # 业务服务模块
│   ├── src/main/java/com/asset/
│   │   ├── service/               # 服务接口
│   │   └── impl/                 # 服务实现
│   └── pom.xml
├── asset-domain/                 # 领域模型模块
│   ├── src/main/java/com/asset/
│   │   ├── entity/               # 实体类
│   │   ├── repository/           # 仓储接口
│   │   ├── vo/                  # 视图对象
│   │   └── enums/                # 枚举类
│   └── pom.xml
├── asset-common/                 # 公共模块
│   ├── src/main/java/com/asset/common/
│   │   ├── constant/             # 常量
│   │   ├── exception/            # 异常
│   │   ├── util/                 # 工具类
│   │   └── annotation/           # 自定义注解
│   └── pom.xml
├── asset-integration/            # 集成模块
│   ├── src/main/java/com/asset/integration/
│   │   ├── data/                 # 数据源集成
│   │   │   ├── akshare/         # A股数据
│   │   │   ├── yfinance/        # 美股港股数据
│   │   │   └── fund/            # 基金数据
│   │   ├── notification/         # 通知集成
│   │   └── mq/                  # 消息队列
│   └── pom.xml
├── asset-job/                    # 定时任务模块
│   ├── src/main/java/com/asset/job/
│   │   ├── task/                 # 定时任务
│   │   └── config/               # 任务配置
│   └── pom.xml
├── asset-admin/                  # 管理后台
└── docker/                      # Docker配置
```

### 核心实体类示例

#### Position实体
```java
package com.asset.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "positions")
public class Position {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "symbol", nullable = false, length = 50)
    private String symbol;
    
    @Column(name = "name", length = 200)
    private String name;
    
    @Column(name = "module", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Module module;
    
    @Column(name = "market", nullable = false, length = 10)
    private String market;
    
    @Column(name = "asset_type", length = 20)
    private String assetType;
    
    @Column(name = "shares", precision = 18, scale = 4)
    private BigDecimal shares;
    
    @Column(name = "avg_cost", precision = 18, scale = 4)
    private BigDecimal avgCost;
    
    @Column(name = "current_price", precision = 18, scale = 4)
    private BigDecimal currentPrice;
    
    @Column(name = "current_value", precision = 18, scale = 4)
    private BigDecimal currentValue;
    
    @Column(name = "target_weight", precision = 5, scale = 2)
    private BigDecimal targetWeight;
    
    @Column(name = "status", length = 20)
    private String status;
    
    @Column(name = "remarks", length = 500)
    private String remarks;
    
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @Column(name = "create_by", length = 50)
    private String createBy;
    
    @Column(name = "update_by", length = 50)
    private String updateBy;
}
```

### 核心服务示例

#### 持仓服务
```java
package com.asset.service.impl;

import com.asset.domain.entity.Position;
import com.asset.domain.repository.PositionRepository;
import com.asset.service.PositionService;
import com.asset.dto.response.PositionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {
    
    private final PositionRepository positionRepository;
    private final MarketDataService marketDataService;
    
    @Override
    public List<PositionVO> getAllPositions() {
        List<Position> positions = positionRepository.findByStatus("active");
        return positions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public PositionVO getPositionDetail(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("持仓不存在"));
        return convertToVO(position);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPosition(Position position) {
        position.setStatus("active");
        position.setCreateTime(LocalDateTime.now());
        position.setUpdateTime(LocalDateTime.now());
        positionRepository.save(position);
        log.info("创建持仓: {}", position.getSymbol());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePosition(Long id, Position position) {
        Position existing = positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("持仓不存在"));
        
        existing.setShares(position.getShares());
        existing.setAvgCost(position.getAvgCost());
        existing.setTargetWeight(position.getTargetWeight());
        existing.setRemarks(position.getRemarks());
        existing.setUpdateTime(LocalDateTime.now());
        
        positionRepository.save(existing);
        log.info("更新持仓: {}", existing.getSymbol());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePosition(Long id) {
        positionRepository.findById(id).ifPresent(position -> {
            position.setStatus("inactive");
            position.setUpdateTime(LocalDateTime.now());
            positionRepository.save(position);
            log.info("删除持仓: {}", position.getSymbol());
        });
    }
    
    @Override
    public void updateCurrentPrices() {
        List<Position> positions = positionRepository.findByStatus("active");
        
        positions.forEach(position -> {
            BigDecimal currentPrice = marketDataService.getCurrentPrice(
                    position.getSymbol(), position.getMarket());
            
            position.setCurrentPrice(currentPrice);
            BigDecimal currentValue = position.getShares().multiply(currentPrice);
            position.setCurrentValue(currentValue);
        });
        
        positionRepository.saveAll(positions);
        log.info("更新{}个持仓的当前价格", positions.size());
    }
    
    private PositionVO convertToVO(Position position) {
        PositionVO vo = new PositionVO();
        vo.setId(position.getId());
        vo.setSymbol(position.getSymbol());
        vo.setName(position.getName());
        vo.setModule(position.getModule().name());
        vo.setMarket(position.getMarket());
        vo.setShares(position.getShares());
        vo.setAvgCost(position.getAvgCost());
        vo.setCurrentPrice(position.getCurrentPrice());
        vo.setCurrentValue(position.getCurrentValue());
        vo.setTargetWeight(position.getTargetWeight());
        
        // 计算盈亏
        if (position.getAvgCost() != null && position.getCurrentPrice() != null) {
            BigDecimal costValue = position.getShares().multiply(position.getAvgCost());
            BigDecimal currentValue = position.getCurrentValue();
            BigDecimal profit = currentValue.subtract(costValue);
            BigDecimal profitRate = profit.divide(costValue, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            
            vo.setProfit(profit);
            vo.setProfitRate(profitRate);
        }
        
        return vo;
    }
}
```

#### 动态平衡服务
```java
package com.asset.service.impl;

import com.asset.domain.entity.Position;
import com.asset.domain.repository.PositionRepository;
import com.asset.domain.entity.RebalanceHistory;
import com.asset.domain.repository.RebalanceHistoryRepository;
import com.asset.dto.request.RebalanceRequest;
import com.asset.dto.response.RebalancePlanVO;
import com.asset.service.RebalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RebalanceServiceImpl implements RebalanceService {
    
    private final PositionRepository positionRepository;
    private final RebalanceHistoryRepository rebalanceHistoryRepository;
    
    @Override
    public RebalancePlanVO generateRebalancePlan(RebalanceRequest request) {
        // 获取所有活跃持仓
        List<Position> positions = positionRepository.findByStatus("active");
        
        // 计算当前总资产
        BigDecimal totalValue = positions.stream()
                .map(Position::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 计算各模块当前配置
        Map<String, BigDecimal> moduleCurrentValues = new HashMap<>();
        Map<String, List<Position>> modulePositions = new HashMap<>();
        
        positions.forEach(position -> {
            String module = position.getModule().name();
            moduleCurrentValues.put(module, 
                    moduleCurrentValues.getOrDefault(module, BigDecimal.ZERO)
                            .add(position.getCurrentValue()));
            modulePositions.computeIfAbsent(module, k -> new ArrayList<>()).add(position);
        });
        
        // 目标配置
        Map<String, BigDecimal> targetAllocations = Map.of(
                "dividend", BigDecimal.valueOf(0.25),
                "fixed", BigDecimal.valueOf(0.25),
                "growth", BigDecimal.valueOf(0.25),
                "allweather", BigDecimal.valueOf(0.25)
        );
        
        // 生成调仓计划
        RebalancePlanVO plan = new RebalancePlanVO();
        plan.setTotalValue(totalValue);
        plan.setRebalanceDate(LocalDate.now());
        plan.setTriggerReason(request.getTriggerReason());
        
        List<RebalancePlanVO.RebalanceAction> actions = new ArrayList<>();
        
        targetAllocations.forEach((module, targetRatio) -> {
            BigDecimal currentValue = moduleCurrentValues.getOrDefault(module, BigDecimal.ZERO);
            BigDecimal targetValue = totalValue.multiply(targetRatio);
            BigDecimal deviation = currentValue.subtract(targetValue);
            BigDecimal deviationRate = deviation.divide(totalValue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            
            // 偏离度小于5%，不调整
            if (deviationRate.abs().compareTo(BigDecimal.valueOf(5)) < 0) {
                return;
            }
            
            // 需要卖出
            if (deviation.compareTo(BigDecimal.ZERO) > 0) {
                List<Position> modulePos = modulePositions.get(module);
                modulePos.sort((a, b) -> 
                        b.getCurrentValue().compareTo(a.getCurrentValue()));
                
                BigDecimal remainingSell = deviation;
                for (Position pos : modulePos) {
                    if (remainingSell.compareTo(BigDecimal.ZERO) <= 0) {
                        break;
                    }
                    
                    BigDecimal sellAmount = remainingSell.min(
                            pos.getCurrentValue().multiply(BigDecimal.valueOf(0.5)));
                    
                    BigDecimal sharesToSell = sellAmount.divide(pos.getCurrentPrice(), 0, 
                            RoundingMode.DOWN);
                    
                    RebalancePlanVO.RebalanceAction action = new RebalancePlanVO.RebalanceAction();
                    action.setSymbol(pos.getSymbol());
                    action.setActionType("sell");
                    action.setShares(sharesToSell);
                    action.setPrice(pos.getCurrentPrice());
                    action.setAmount(sellAmount);
                    action.setReason("超配");
                    
                    actions.add(action);
                    remainingSell = remainingSell.subtract(sellAmount);
                }
            }
            // 需要买入
            else {
                // 从机会库选择
                List<String> candidates = getInvestmentCandidates(module);
                BigDecimal remainingBuy = deviation.abs();
                
                for (String symbol : candidates) {
                    if (remainingBuy.compareTo(BigDecimal.ZERO) <= 0) {
                        break;
                    }
                    
                    BigDecimal price = getCurrentPrice(symbol);
                    BigDecimal buyAmount = remainingBuy.min(
                            totalValue.multiply(BigDecimal.valueOf(0.3)));
                    
                    BigDecimal sharesToBuy = buyAmount.divide(price, 0, 
                            RoundingMode.DOWN);
                    
                    RebalancePlanVO.RebalanceAction action = new RebalancePlanVO.RebalanceAction();
                    action.setSymbol(symbol);
                    action.setActionType("buy");
                    action.setShares(sharesToBuy);
                    action.setPrice(price);
                    action.setAmount(buyAmount);
                    action.setReason("低配");
                    
                    actions.add(action);
                    remainingBuy = remainingBuy.subtract(buyAmount);
                }
            }
        });
        
        plan.setActions(actions);
        plan.setEstimatedFee(calculateEstimatedFee(actions));
        
        return plan;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeRebalance(RebalancePlanVO plan) {
        // 记录平衡历史
        RebalanceHistory history = new RebalanceHistory();
        history.setBalanceDate(plan.getRebalanceDate());
        history.setTotalValueBefore(plan.getTotalValue());
        history.setTriggerReason(plan.getTriggerReason());
        history.setActions(convertActionsToJson(plan.getActions()));
        history.setStatus("executing");
        history.setCreateTime(LocalDateTime.now());
        
        rebalanceHistoryRepository.save(history);
        
        log.info("开始执行动态平衡，共{}项操作", plan.getActions().size());
        
        // 执行调仓操作 (这里需要接入实际券商API)
        int successCount = 0;
        for (RebalancePlanVO.RebalanceAction action : plan.getActions()) {
            try {
                executeTrade(action);
                successCount++;
            } catch (Exception e) {
                log.error("执行调仓操作失败: {}", action.getSymbol(), e);
            }
        }
        
        // 更新历史记录
        BigDecimal totalValueAfter = calculateTotalValue();
        history.setTotalValueAfter(totalValueAfter);
        history.setStatus("executed");
        history.setExecutedAt(LocalDateTime.now());
        history.setNotes(String.format("成功执行%d/%d项操作", successCount, plan.getActions().size()));
        
        rebalanceHistoryRepository.save(history);
        
        log.info("动态平衡执行完成，成功{}/{}", successCount, plan.getActions().size());
    }
    
    private void executeTrade(RebalancePlanVO.RebalanceAction action) {
        // 接入券商API执行交易
        // 这里需要根据实际券商API实现
        log.info("执行交易: {} {} @ {}", action.getActionType(), 
                action.getSymbol(), action.getPrice());
    }
    
    private BigDecimal calculateEstimatedFee(List<RebalancePlanVO.RebalanceAction> actions) {
        // 估算手续费
        return actions.stream()
                .map(action -> action.getAmount().multiply(BigDecimal.valueOf(0.0003)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private String convertActionsToJson(List<RebalancePlanVO.RebalanceAction> actions) {
        // 转换为JSON字符串
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(actions);
        } catch (Exception e) {
            log.error("转换操作为JSON失败", e);
            return "[]";
        }
    }
    
    private BigDecimal getCurrentPrice(String symbol) {
        // 从行情服务获取价格
        return BigDecimal.ZERO;
    }
    
    private List<String> getInvestmentCandidates(String module) {
        // 从投资标的库获取候选
        return new ArrayList<>();
    }
    
    private BigDecimal calculateTotalValue() {
        List<Position> positions = positionRepository.findByStatus("active");
        return positions.stream()
                .map(Position::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

### 控制器示例

#### 持仓控制器
```java
package com.asset.api;

import com.asset.service.PositionService;
import com.asset.dto.response.PositionVO;
import com.asset.dto.request.PositionRequest;
import com.asset.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "持仓管理", description = "持仓管理接口")
@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {
    
    private final PositionService positionService;
    
    @Operation(summary = "获取所有持仓")
    @GetMapping
    public Result<List<PositionVO>> getAllPositions() {
        List<PositionVO> positions = positionService.getAllPositions();
        return Result.success(positions);
    }
    
    @Operation(summary = "获取持仓详情")
    @GetMapping("/{id}")
    public Result<PositionVO> getPositionDetail(@PathVariable Long id) {
        PositionVO position = positionService.getPositionDetail(id);
        return Result.success(position);
    }
    
    @Operation(summary = "创建持仓")
    @PostMapping
    public Result<Void> createPosition(@Valid @RequestBody PositionRequest request) {
        positionService.createPosition(request);
        return Result.success();
    }
    
    @Operation(summary = "更新持仓")
    @PutMapping("/{id}")
    public Result<Void> updatePosition(@PathVariable Long id, 
                                     @Valid @RequestBody PositionRequest request) {
        positionService.updatePosition(id, request);
        return Result.success();
    }
    
    @Operation(summary = "删除持仓")
    @DeleteMapping("/{id}")
    public Result<Void> deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
        return Result.success();
    }
    
    @Operation(summary = "更新当前价格")
    @PostMapping("/update-prices")
    public Result<Void> updateCurrentPrices() {
        positionService.updateCurrentPrices();
        return Result.success();
    }
}
```

#### 动态平衡控制器
```java
package com.asset.api;

import com.asset.service.RebalanceService;
import com.asset.dto.request.RebalanceRequest;
import com.asset.dto.response.RebalancePlanVO;
import com.asset.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "动态平衡", description = "动态平衡接口")
@RestController
@RequestMapping("/api/rebalance")
@RequiredArgsConstructor
public class RebalanceController {
    
    private final RebalanceService rebalanceService;
    
    @Operation(summary = "生成平衡方案")
    @PostMapping("/plan")
    public Result<RebalancePlanVO> generateRebalancePlan(
            @Valid @RequestBody RebalanceRequest request) {
        RebalancePlanVO plan = rebalanceService.generateRebalancePlan(request);
        return Result.success(plan);
    }
    
    @Operation(summary = "执行平衡方案")
    @PostMapping("/execute")
    public Result<Void> executeRebalance(@Valid @RequestBody RebalancePlanVO plan) {
        rebalanceService.executeRebalance(plan);
        return Result.success();
    }
    
    @Operation(summary = "获取平衡历史")
    @GetMapping("/history")
    public Result<List<RebalanceHistoryVO>> getRebalanceHistory() {
        List<RebalanceHistoryVO> history = rebalanceService.getRebalanceHistory();
        return Result.success(history);
    }
}
```

### 定时任务示例

```java
package com.asset.job.task;

import com.asset.service.PositionService;
import com.asset.service.RebalanceService;
import com.asset.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    
    private final PositionService positionService;
    private final RebalanceService rebalanceService;
    private final MarketDataService marketDataService;
    
    /**
     * 每日9:30更新行情数据
     */
    @Scheduled(cron = "0 30 9 * * MON-FRI")
    public void updateMarketData() {
        log.info("开始更新市场行情数据");
        marketDataService.updateAllMarketData();
        log.info("市场行情数据更新完成");
    }
    
    /**
     * 每日15:00更新持仓价格
     */
    @Scheduled(cron = "0 0 15 * * MON-FRI")
    public void updatePositionPrices() {
        log.info("开始更新持仓价格");
        positionService.updateCurrentPrices();
        log.info("持仓价格更新完成");
    }
    
    /**
     * 每日16:00检查动态平衡
     */
    @Scheduled(cron = "0 0 16 * * MON-FRI")
    public void checkRebalance() {
        log.info("开始检查动态平衡");
        rebalanceService.checkAndAlertRebalance();
        log.info("动态平衡检查完成");
    }
    
    /**
     * 每月1日检查投资机会
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void scanInvestmentOpportunities() {
        log.info("开始扫描投资机会");
        rebalanceService.scanOpportunities();
        log.info("投资机会扫描完成");
    }
    
    /**
     * 每周日凌晨计算风险指标
     */
    @Scheduled(cron = "0 0 2 ? * MON")
    public void calculateRiskMetrics() {
        log.info("开始计算风险指标");
        rebalanceService.calculateRiskMetrics();
        log.info("风险指标计算完成");
    }
}
```

### 配置文件示例

#### application.yml
```yaml
spring:
  application:
    name: asset-management
  
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/asset_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: ${DB_PASSWORD:root}
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      auto-commit: true
      idle-timeout: 30000
      max-lifetime: 1800000
      connection-timeout: 30000
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 50
          order_inserts: true
          order_updates: true
  
  data:
    redis:
      host: localhost
      port: 6379
      password: ${REDIS_PASSWORD:}
      database: 0
      timeout: 5000
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
  
  cache:
    type: redis
    redis:
      time-to-live: 3600
      cache-null-values: false

server:
  port: 8080
  servlet:
    context-path: /api
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain

# MyBatis Plus配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# Swagger配置
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true

# 任务调度
schedule:
  enable: true

# 外部API配置
external:
  akshare:
    enabled: true
  yfinance:
    enabled: true
    rate-limit: 10
  fund:
    enabled: true

# 通知配置
notification:
  email:
    enabled: true
    host: smtp.example.com
    port: 587
    username: ${EMAIL_USERNAME}
    password: ${EMAIL_PASSWORD}
  wechat:
    enabled: false
    corp-id: ${WECHAT_CORP_ID}
    secret: ${WECHAT_SECRET}
  sms:
    enabled: false
    access-key: ${SMS_ACCESS_KEY}
    secret-key: ${SMS_SECRET_KEY}

# 日志配置
logging:
  level:
    com.asset: INFO
    org.springframework: WARN
  file:
    name: logs/asset-management.log
    max-size: 100MB
    max-history: 30
```

---

## 📊 投资策略体系

（注：投资策略内容与原方案保持一致，以下为简要说明）

### 1. 红利策略 (Dividend Strategy)

#### 核心理念
- **目标**: 低波动率，10%年化收益
- **特点**: 稳定分红，防御性强
- **适合人群**: 追求稳定现金流，风险厌恶型投资者

#### 投资标的池

**国内A股:**
| 标的代码 | 标的名称 | 类型 | 特点 | 优先级 |
|----------|----------|------|------|--------|
| SH000922 | 中证红利 | 指数ETF | 高股息率，低估值 | ⭐⭐⭐⭐⭐ |
| CSIH30269 | 红利低波 | 指数ETF | 低波动，防御性强 | ⭐⭐⭐⭐⭐ |
| SH512040 | 价值100ETF | 指数ETF | 价值投资，分红稳定 | ⭐⭐⭐⭐ |
| SZ159201 | 自由现金流ETF | 指数ETF | FCF收益率高，现金流好 | ⭐⭐⭐⭐⭐ |

（后续内容与原方案相同，包括成长策略、固收策略、全天候策略的详细说明...）

---

## 🚀 分阶段实施路线图

### Phase 1: MVP (2-3个月)

**目标**: 基础资产管理功能上线

**功能清单:**
```
✅ 数据模型搭建
   ├── MySQL数据库设计
   ├── Spring Boot项目搭建
   ├── 基础表结构创建
   └── 数据迁移工具

✅ 后端基础服务
   ├── Spring Boot框架搭建
   ├── 持仓管理Service
   ├── 交易记录Service
   ├── 行情数据接入
   └── 基础计算Service

✅ 前端基础页面
   ├── 仪表盘
   ├── 持仓列表
   ├── 交易记录
   └── 基础图表

✅ 数据接入
   ├── A股行情 (AKShare)
   ├── 基金净值 (天天基金API)
   └── 手动数据录入
```

**里程碑:**
- 能够记录持仓和交易
- 能够查看资产总览
- 能够计算基础收益
- 简单的Web界面

---

### Phase 2: 核心功能 (3-4个月)

**目标**: 四象限配置 + 动态平衡 + 机会发现

**功能清单:**
```
✅ 四象限配置
   ├── 模块划分
   ├── 目标权重设置
   ├── 当前权重计算
   └── 偏离度监控

✅ 动态平衡
   ├── 平衡方案生成
   ├── 平衡建议展示
   ├── 手动执行记录
   └── 平衡效果跟踪

✅ 投资标的库
   ├── 标的信息管理
   ├── 基本面数据
   ├── 估值数据
   └── 评分系统

✅ 机会发现
   ├── 估值机会识别
   ├── 红利机会识别
   ├── 策略信号生成
   └── 机会提醒

✅ 数据扩展
   ├── 美股行情 (yfinance)
   ├── 港股行情 (yfinance)
   ├── 汇率数据
   └── 分红数据
```

**里程碑:**
- 完整的四象限配置系统
- 自动生成平衡建议
- 基本的投资标的库
- 简单的机会提醒

---

### Phase 3: 高级功能 (3-4个月)

**目标**: 风险管理 + 策略分析 + 回测

**功能清单:**
```
✅ 风险管理
   ├── 风险指标计算
   ├── 回撤分析
   ├── 波动率分析
   ├── 相关性分析
   ├── 集中度分析
   └── 风险预警

✅ 策略分析
   ├── 策略参数配置
   ├── 策略回测
   ├── 回测报告
   └── 策略对比

✅ 报表系统
   ├── 月度报告
   ├── 季度报告
   ├── 年度报告
   └── 自定义报表

✅ 通知系统
   ├── 邮件通知
   ├── 短信通知
   ├── App推送
   └── 微信通知
```

**里程碑:**
- 完善的风险监控
- 策略回测功能
- 自动化报表
- 多渠道通知

---

### Phase 4: 智能化 (4-6个月)

**目标**: 机器学习 + 自动化 + AI推荐

**功能清单:**
```
✅ 机器学习模型
   ├── 估值预测模型
   ├── 价格趋势预测
   ├── 风险预测模型
   └── 机会优先级排序

✅ 自动化系统
   ├── 自动数据采集
   ├── 自动机会发现
   ├── 自动平衡执行（可选）
   └── 自动止损/止盈

✅ AI推荐
   ├── 智能标的推荐
   ├── 个性化建议
   ├── 市场解读
   └── 研报摘要

✅ 高级分析
   ├── 因子分析
   ├── 归因分析
   ├── 压力测试
   └── 蒙特卡洛模拟
```

**里程碑:**
- ML模型上线
- 自动化程度提升
- AI辅助决策
- 高级风险分析

---

### Phase 5: 完善优化 (持续)

**目标**: 用户体验 + 性能优化 + 功能扩展

**功能清单:**
```
✅ 用户体验优化
   ├── 移动端适配
   ├── 暗黑模式
   ├── 自定义仪表盘
   ├── 快捷操作
   └── 批量操作

✅ 性能优化
   ├── 数据库优化
   ├── 缓存优化
   ├── API性能优化
   ├── 前端性能优化
   └── CDN加速

✅ 功能扩展
   ├── 更多市场支持
   ├── 更多策略
   ├── 社区分享
   ├── 数据导出
   └── 第三方集成
```

---

## 📋 开发计划与时间表

### 详细甘特图

```
月份    P1(MVP)    P2(核心)    P3(高级)    P4(智能)    P5(优化)
────────────────────────────────────────────────────────────
M1     ████
M2     ████
M3     ████
M4                 ████
M5                 ████
M6                 ████
M7                             ████
M8                             ████
M9                             ████
M10                                        ████
M11                                        ████
M12                                        ████
M13-18                                              ████
持续                                                     ████
```

---

## 📖 Maven依赖配置

### pom.xml (根pom)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.asset</groupId>
    <artifactId>asset-management</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
        <module>asset-common</module>
        <module>asset-domain</module>
        <module>asset-service</module>
        <module>asset-api</module>
        <module>asset-integration</module>
        <module>asset-job</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <hutool.version>5.8.23</hutool.version>
        <knife4j.version>4.4.0</knife4j.version>
        <easyexcel.version>3.3.2</easyexcel.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- MyBatis Plus -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>
            
            <!-- Hutool工具类 -->
            <dependency>
                <groupId>cn.hutool</groupId>
                <artifactId>hutool-all</artifactId>
                <version>${hutool.version}</version>
            </dependency>
            
            <!-- Knife4j接口文档 -->
            <dependency>
                <groupId>com.github.xiaoymin</groupId>
                <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
