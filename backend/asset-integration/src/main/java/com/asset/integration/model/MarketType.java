package com.asset.integration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 市场类型枚举
 * 用于区分不同市场的投资标的
 */
@Getter
@AllArgsConstructor
public enum MarketType {

    /**
     * A股（沪深主板、创业板等）
     */
    A_STOCK("A股", "a_stock", "CN"),

    /**
     * 场内ETF（交易所交易基金）
     */
    ETF("场内ETF", "etf", "CN"),

    /**
     * 场外基金（公募基金）
     */
    FUND("场外基金", "fund", "CN"),

    /**
     * 港股
     */
    HK_STOCK("港股", "hk_stock", "HK"),

    /**
     * 美股
     */
    US_STOCK("美股", "us_stock", "US");

    /**
     * 市场名称（中文显示）
     */
    private final String name;

    /**
     * 市场代码（用于存储和API）
     */
    private final String code;

    /**
     * 交易日历区域
     */
    private final String calendarRegion;

    /**
     * 根据代码获取市场类型
     */
    public static MarketType fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return A_STOCK;
        }
        for (MarketType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return A_STOCK;
    }

    /**
     * 判断是否为中国市场（使用中国交易日历）
     */
    public boolean isChineseMarket() {
        return "CN".equals(this.calendarRegion);
    }

    /**
     * 判断是否需要实时行情
     */
    public boolean needsRealTimeQuote() {
        return this == A_STOCK || this == ETF || this == HK_STOCK || this == US_STOCK;
    }

    /**
     * 判断是否为场外基金
     */
    public boolean isFund() {
        return this == FUND;
    }
}
