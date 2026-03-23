-- ====================================================================
-- 数据库迁移脚本: 移除持仓表冗余字段
-- 版本: 1.0.2
-- 日期: 2026-03-23
-- ====================================================================

USE `asset_management`;

-- 移除 asset_type 字段（市场类型已涵盖）
ALTER TABLE `positions` DROP COLUMN IF EXISTS `asset_type`;

-- 移除 target_weight 字段（前端未使用）
ALTER TABLE `positions` DROP COLUMN IF EXISTS `target_weight`;

-- 移除 buy_price_threshold 字段（前端未使用）
ALTER TABLE `positions` DROP COLUMN IF EXISTS `buy_price_threshold`;

-- 移除 sell_price_threshold 字段（前端未使用）
ALTER TABLE `positions` DROP COLUMN IF EXISTS `sell_price_threshold`;

-- 移除 remarks 字段（前端未使用）
ALTER TABLE `positions` DROP COLUMN IF EXISTS `remarks`;

-- 修改 market 字段长度以适应新的市场类型
ALTER TABLE `positions` MODIFY COLUMN `market` VARCHAR(20) NOT NULL COMMENT '市场类型: a_stock/etf/fund/hk_stock/us_stock';
