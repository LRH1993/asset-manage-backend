package com.asset.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 资产总览VO
 * 遵循 backend/CLAUDE.md 规范
 */
@Data
public class AssetOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总资产
     */
    private BigDecimal totalValue;

    /**
     * 今日盈亏
     */
    private BigDecimal todayProfit;

    /**
     * 今日盈亏率（百分比数值，如 2.35 表示 2.35%）
     */
    private BigDecimal todayProfitRate;

    /**
     * 累计盈亏
     */
    private BigDecimal totalProfit;

    /**
     * 累计盈亏率（百分比数值）
     */
    private BigDecimal totalProfitRate;

    /**
     * 年化收益率（百分比数值）
     */
    private BigDecimal annualReturn;

    /**
     * 基准对比（百分比数值）
     */
    private BigDecimal benchmarkComparison;
}
