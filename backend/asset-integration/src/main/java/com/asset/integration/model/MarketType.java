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
     * 支持新代码(a_stock, etf, fund, hk_stock, us_stock)和旧代码(sh, sz, us, hk)
     */
    public static MarketType fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return A_STOCK;
        }
        String lowerCode = code.toLowerCase();

        // 新代码匹配
        for (MarketType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }

        // 旧代码映射
        return switch (lowerCode) {
            case "sh", "sz" -> A_STOCK;  // 沪市/深市 -> A股
            case "hk" -> HK_STOCK;        // 港股
            case "us" -> US_STOCK;        // 美股
            default -> A_STOCK;
        };
    }

    /**
     * 根据股票代码推断市场类型
     * @param symbol 股票代码（如 SH600519, SZ000001, 00700.HK, AAPL.US）
     * @return 市场类型
     */
    public static MarketType fromSymbol(String symbol) {
        if (symbol == null || symbol.isEmpty()) {
            return A_STOCK;
        }

        String upperSymbol = symbol.toUpperCase();

        // 港股：5位数字或包含 .HK
        if (upperSymbol.matches("\\d{5}") || upperSymbol.contains(".HK")) {
            return HK_STOCK;
        }

        // 美股：纯字母或包含 .US
        if (upperSymbol.matches("[A-Z]+(\\.US)?")) {
            return US_STOCK;
        }

        // 场外基金：6位数字，以 00、11、15 等开头
        if (symbol.matches("\\d{6}")) {
            return FUND;
        }

        // 默认返回 A股/ETF
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
