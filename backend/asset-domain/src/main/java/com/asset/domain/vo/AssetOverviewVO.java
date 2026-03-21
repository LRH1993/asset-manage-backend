package com.asset.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 资产总览VO
 */
@Data
public class AssetOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总资产
     */
    private Double totalValue;

    /**
     * 今日盈亏
     */
    private Double todayProfit;

    /**
     * 今日盈亏率
     */
    private Double todayProfitRate;

    /**
     * 累计盈亏
     */
    private Double totalProfit;

    /**
     * 累计盈亏率
     */
    private Double totalProfitRate;

    /**
     * 年化收益率
     */
    private Double annualReturn;

    /**
     * 基准对比
     */
    private Double benchmarkComparison;
}