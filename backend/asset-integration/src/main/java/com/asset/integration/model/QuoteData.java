package com.asset.integration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 行情数据模型
 * 用于封装各数据源返回的行情信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteData {

    /**
     * 标的代码
     */
    private String symbol;

    /**
     * 标的名称
     */
    private String name;

    /**
     * 当前价格
     */
    private BigDecimal currentPrice;

    /**
     * 昨日收盘价
     */
    private BigDecimal prevClosePrice;

    /**
     * 今日开盘价
     */
    private BigDecimal openPrice;

    /**
     * 今日最高价
     */
    private BigDecimal highPrice;

    /**
     * 今日最低价
     */
    private BigDecimal lowPrice;

    /**
     * 成交量
     */
    private Long volume;

    /**
     * 成交额
     */
    private BigDecimal turnover;

    /**
     * 涨跌幅（百分比，如 5.23 表示涨5.23%）
     */
    private BigDecimal changePercent;

    /**
     * 涨跌额
     */
    private BigDecimal changeAmount;

    /**
     * 市场类型
     */
    private MarketType marketType;

    /**
     * 数据更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 数据源
     */
    private String dataSource;

    /**
     * 是否获取成功
     */
    private boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;
}
