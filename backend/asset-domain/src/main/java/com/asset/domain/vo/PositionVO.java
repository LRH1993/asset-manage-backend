package com.asset.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 持仓VO
 */
@Data
public class PositionVO {

    private Long id;
    private String symbol;
    private String name;
    private String module;
    private String market;
    private BigDecimal shares;
    private BigDecimal avgCost;
    private BigDecimal currentPrice;
    private BigDecimal currentValue;
    private BigDecimal costValue;
    private BigDecimal profitAmount;
    private BigDecimal profitRate;
    private BigDecimal actualWeight;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 今日盈亏率（百分比）
     * 需要行情服务支持
     */
    private BigDecimal todayProfitRate;

    /**
     * 今日盈亏金额
     * = 市值 * 今日涨跌幅 / 100
     */
    private BigDecimal todayProfitAmount;
}
