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
    private String assetType;
    private BigDecimal shares;
    private BigDecimal avgCost;
    private BigDecimal currentPrice;
    private BigDecimal currentValue;
    private BigDecimal costValue;
    private BigDecimal profitAmount;
    private BigDecimal profitRate;
    private BigDecimal targetWeight;
    private BigDecimal actualWeight;
    private BigDecimal buyPriceThreshold;
    private BigDecimal sellPriceThreshold;
    private String status;
    private String remarks;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 今日盈亏率（百分比）
     * 需要行情服务支持
     */
    private BigDecimal todayProfitRate;
}
