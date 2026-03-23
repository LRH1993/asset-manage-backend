-- ====================================================================
-- 家庭资产监控管理平台 - 数据库初始化脚本
-- ====================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `asset_management`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `asset_management`;

-- ====================================================================
-- 1. 持仓表 (positions)
-- ====================================================================
DROP TABLE IF EXISTS `positions`;
CREATE TABLE `positions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `symbol` VARCHAR(50) NOT NULL COMMENT '标的代码',
    `name` VARCHAR(200) DEFAULT NULL COMMENT '标的名称',
    `module` VARCHAR(20) NOT NULL COMMENT '所属模块: dividend/fixed/growth/allweather',
    `market` VARCHAR(20) NOT NULL COMMENT '市场类型: a_stock/etf/fund/hk_stock/us_stock',
    `shares` DECIMAL(18, 4) DEFAULT NULL COMMENT '持仓数量',
    `avg_cost` DECIMAL(18, 4) DEFAULT NULL COMMENT '平均成本',
    `current_price` DECIMAL(18, 4) DEFAULT NULL COMMENT '当前价格（每日行情更新）',
    `current_value` DECIMAL(18, 4) DEFAULT NULL COMMENT '当前市值',
    `prev_close_price` DECIMAL(18, 4) DEFAULT NULL COMMENT '昨日收盘价（用于计算今日盈亏）',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态: active/inactive/sold',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标识: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_symbol` (`symbol`),
    KEY `idx_module` (`module`),
    KEY `idx_market` (`market`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='持仓表';

-- ====================================================================
-- 2. 交易记录表 (transactions)
-- ====================================================================
DROP TABLE IF EXISTS `transactions`;
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
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标识: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_symbol` (`symbol`),
    KEY `idx_transaction_date` (`transaction_date`),
    KEY `idx_position_id` (`position_id`),
    KEY `idx_transaction_type` (`transaction_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易记录表';

-- ====================================================================
-- 3. 历史行情表 (price_history)
-- ====================================================================
DROP TABLE IF EXISTS `price_history`;
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

-- ====================================================================
-- 4. 投资标的库表 (investment_universe)
-- ====================================================================
DROP TABLE IF EXISTS `investment_universe`;
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
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标识: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_symbol` (`symbol`),
    KEY `idx_module` (`module`),
    KEY `idx_market` (`market`),
    KEY `idx_status` (`status`),
    KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投资标的库';

-- ====================================================================
-- 5. 策略参数表 (strategy_params)
-- ====================================================================
DROP TABLE IF EXISTS `strategy_params`;
CREATE TABLE `strategy_params` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `module` VARCHAR(20) NOT NULL COMMENT '所属模块',
    `param_key` VARCHAR(50) NOT NULL COMMENT '参数键',
    `param_value` JSON NOT NULL COMMENT '参数值(JSON)',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `version` VARCHAR(20) DEFAULT '1.0' COMMENT '版本',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_module_key` (`module`, `param_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='策略参数表';

-- ====================================================================
-- 6. 风险指标表 (risk_metrics)
-- ====================================================================
DROP TABLE IF EXISTS `risk_metrics`;
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

-- ====================================================================
-- 7. 动态平衡记录表 (rebalance_history)
-- ====================================================================
DROP TABLE IF EXISTS `rebalance_history`;
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
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标识: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_balance_date` (`balance_date`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态平衡记录表';

-- ====================================================================
-- 8. 机会提醒表 (opportunity_alerts)
-- ====================================================================
DROP TABLE IF EXISTS `opportunity_alerts`;
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

-- ====================================================================
-- 插入默认策略参数
-- ====================================================================
INSERT INTO `strategy_params` (`module`, `param_key`, `param_value`, `description`) VALUES
-- 红利策略参数
('dividend', 'target_weight', '{"value": 0.25}', '目标权重'),
('dividend', 'min_dividend_yield', '{"value": 3.0}', '最低股息率(%)'),
('dividend', 'max_pe_percentile', '{"value": 50}', 'PE百分位上限(%)'),
('dividend', 'min_fcf_yield', '{"value": 4.0}', 'FCF收益率下限(%)'),
('dividend', 'min_dividend_growth_rate', '{"value": 2.0}', '分红增长率要求(%)'),
('dividend', 'max_volatility', '{"value": 25.0}', '波动率上限(%)'),
-- 固收策略参数
('fixed', 'target_weight', '{"value": 0.25}', '目标权重'),
('fixed', 'min_credit_rating', '{"value": "AAA"}', '信用评级下限'),
('fixed', 'min_remaining_maturity', '{"value": 365}', '剩余期限下限(天)'),
('fixed', 'max_remaining_maturity', '{"value": 3650}', '剩余期限上限(天)'),
('fixed', 'min_duration', '{"value": 0.5}', '久期下限(年)'),
('fixed', 'max_duration', '{"value": 10.0}', '久期上限(年)'),
-- 成长策略参数
('growth', 'target_weight', '{"value": 0.25}', '目标权重'),
('growth', 'max_pe_percentile', '{"value": 70}', 'PE百分位上限(%)'),
('growth', 'min_revenue_growth_rate', '{"value": 10.0}', '收入增长率下限(%)'),
('growth', 'min_roe', '{"value": 15.0}', 'ROE下限(%)'),
('growth', 'max_sector_concentration', '{"value": 0.3}', '行业集中度限制'),
('growth', 'max_valuation_ratio', '{"value": 2.0}', '估值上限(倍)'),
-- 全天候策略参数
('allweather', 'target_weight', '{"value": 0.25}', '目标权重'),
('allweather', 'stock_ratio', '{"value": 0.3}', '股票比例'),
('allweather', 'bond_ratio', '{"value": 0.4}', '债券比例'),
('allweather', 'gold_ratio', '{"value": 0.075}', '黄金比例'),
('allweather', 'commodity_ratio', '{"value": 0.075}', '大宗商品比例'),
('allweather', 'rebalance_frequency', '{"value": "quarterly"}', '再平衡频率'),
('allweather', 'deviation_threshold', '{"value": 0.05}', '偏离度阈值');

-- ====================================================================
-- 插入示例投资标的
-- ====================================================================
INSERT INTO `investment_universe`
    (`symbol`, `name`, `module`, `market`, `asset_type`, `description`, `risk_level`, `dividend_yield`, `pe_ratio`, `strategy`, `valuation_percentile`, `status`, `priority`)
VALUES
-- 红利模块
('SH000922', '中证红利', 'dividend', 'CN', 'etf', '中证红利指数ETF，高股息率，低估值', 'low', 6.5, 8.5, '价值投资', 20, 'watching', 5),
('CSIH30269', '红利低波', 'dividend', 'CN', 'etf', '红利低波指数ETF，低波动，防御性强', 'low', 5.8, 9.2, '低波动策略', 25, 'watching', 5),
('SH512040', '价值100ETF', 'dividend', 'CN', 'etf', '价值100指数ETF，价值投资，分红稳定', 'medium', 4.5, 7.8, '价值投资', 30, 'watching', 4),
('SZ159201', '自由现金流ETF', 'dividend', 'CN', 'etf', '自由现金流指数ETF，FCF收益率高', 'medium', 3.8, 10.5, '价值投资', 35, 'watching', 4),
-- 固收模块
('SH511010', '国债ETF', 'fixed', 'CN', 'bond', '国债指数ETF，低风险，稳健收益', 'low', 2.8, NULL, '国债投资', NULL, 'watching', 5),
('SH511260', '可转债ETF', 'fixed', 'CN', 'etf', '可转债指数ETF，下有保底，上不封顶', 'medium', 3.2, 12.5, '可转债投资', 40, 'watching', 4),
('SZ161726', '信用债ETF', 'fixed', 'CN', 'bond', '信用债指数ETF，中高收益', 'medium', 4.5, NULL, '信用债投资', NULL, 'watching', 4),
-- 成长模块
('SZ159915', '创业板ETF', 'growth', 'CN', 'etf', '创业板指数ETF，高成长，高波动', 'high', 1.2, 35.0, '成长投资', 50, 'watching', 5),
('SH512480', '半导体ETF', 'growth', 'CN', 'etf', '半导体指数ETF，科技成长', 'high', 0.8, 42.0, '科技成长', 60, 'watching', 4),
('SH512690', '新能源ETF', 'growth', 'CN', 'etf', '新能源指数ETF，绿色成长', 'high', 1.0, 38.0, '绿色成长', 55, 'watching', 4),
-- 全天候模块
('SH518880', '沪深300ETF', 'allweather', 'CN', 'etf', '沪深300指数ETF，宽基指数', 'medium', 2.5, 12.0, '宽基配置', 45, 'watching', 5),
('SZ159919', '沪深300ETF', 'allweather', 'CN', 'etf', '沪深300指数ETF，宽基指数', 'medium', 2.4, 11.8, '宽基配置', 44, 'watching', 5),
('SZ159995', '芯片ETF', 'allweather', 'CN', 'etf', '芯片指数ETF，科技配置', 'high', 1.5, 32.0, '科技配置', 58, 'watching', 3);

-- ====================================================================
-- 初始化完成
-- ====================================================================