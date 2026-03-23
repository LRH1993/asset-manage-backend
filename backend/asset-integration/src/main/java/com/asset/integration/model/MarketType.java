package com.asset.integration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 市场类型枚举
 */
@Getter
@AllArgsConstructor
public enum MarketType {

    /**
     * A股 - 沪市
     */
    SH("沪市", "sh", "1"),

    /**
     * A股 - 深市
     */
    SZ("深市", "sz", "0"),

    /**
     * 港股
     */
    HK("港股", "hk", "116"),

    /**
     * 美股
     */
    US("美股", "us", "105"),

    /**
     * 场内ETF
     */
    ETF("场内ETF", "etf", null),

    /**
     * 场外基金
     */
    FUND("场外基金", "fund", null);

    /**
     * 市场名称
     */
    private final String name;

    /**
     * 市场代码
     */
    private final String code;

    /**
     * 东方财富市场ID
     */
    private final String eastMoneyMarketId;

    /**
     * 根据标的代码判断市场类型
     */
    public static MarketType fromSymbol(String symbol) {
        if (symbol == null || symbol.isEmpty()) {
            return null;
        }

        // 去掉后缀
        String pureCode = symbol.split("\\.")[0];

        // A股沪市：6开头
        if (pureCode.startsWith("6")) {
            return SH;
        }

        // A股深市：0、3开头
        if (pureCode.startsWith("0") || pureCode.startsWith("3")) {
            return SZ;
        }

        // 港股
        if (symbol.contains(".HK") || pureCode.length() == 5 && pureCode.matches("\\d{5}")) {
            return HK;
        }

        // 美股
        if (symbol.contains(".US") || symbol.matches("[A-Z]+")) {
            return US;
        }

        // 场外基金：纯数字且长度为6
        if (pureCode.matches("\\d{6}")) {
            return FUND;
        }

        // 默认返回深市
        return SZ;
    }

    /**
     * 判断是否为A股或场内ETF
     */
    public boolean isChineseMarket() {
        return this == SH || this == SZ || this == ETF;
    }

    /**
     * 判断是否为场外基金
     */
    public boolean isFund() {
        return this == FUND;
    }

    /**
     * 判断是否为港股或美股
     */
    public boolean isOverseasMarket() {
        return this == HK || this == US;
    }
}
