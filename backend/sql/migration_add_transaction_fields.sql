-- ====================================================================
-- 数据库迁移脚本: 为交易记录表添加模块和市场类型字段
-- 版本: 1.0.1
-- 日期: 2026-03-23
-- ====================================================================

USE `asset_management`;

-- 添加 name 字段
ALTER TABLE `transactions`
ADD COLUMN `name` VARCHAR(200) DEFAULT NULL COMMENT '标的名称' AFTER `symbol`;

-- 添加 module 字段
ALTER TABLE `transactions`
ADD COLUMN `module` VARCHAR(20) DEFAULT NULL COMMENT '所属模块: dividend/fixed/growth/allweather' AFTER `name`;

-- 添加 market 字段
ALTER TABLE `transactions`
ADD COLUMN `market` VARCHAR(20) DEFAULT NULL COMMENT '市场类型: a_stock/etf/fund/hk_stock/us_stock' AFTER `module`;

-- 添加 module 索引
ALTER TABLE `transactions`
ADD INDEX `idx_module` (`module`);

-- 添加 market 索引
ALTER TABLE `transactions`
ADD INDEX `idx_market` (`market`);
