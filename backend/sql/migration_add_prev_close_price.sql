-- ====================================================================
-- 添加昨日收盘价字段 - 用于计算今日盈亏
-- ====================================================================

USE `asset_management`;

-- 给 positions 表添加昨日收盘价字段
ALTER TABLE `positions`
ADD COLUMN `prev_close_price` DECIMAL(18, 4) DEFAULT NULL COMMENT '昨日收盘价（用于计算今日盈亏）'
AFTER `current_value`;

-- 初始化昨日收盘价数据（使用当前价格作为演示）
UPDATE `positions`
SET `prev_close_price` = `current_price`
WHERE `status` = 'active' AND `deleted` = 0;

-- 给 price_history 表添加更新时间字段
ALTER TABLE `price_history`
ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
AFTER `create_time`;
