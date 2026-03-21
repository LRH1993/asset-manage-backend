package com.asset.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 核心指标VO
 */
@Data
public class CoreMetricsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 最大回撤
     */
    private Double maxDrawdown;

    /**
     * 夏普比率
     */
    private Double sharpeRatio;

    /**
     * 30日波动率
     */
    private Double volatility30d;

    /**
     * 90日波动率
     */
    private Double volatility90d;

    /**
     * 集中度风险
     */
    private Double concentrationRisk;

    /**
     * VaR(95%)
     */
    private Double var95;

    /**
     * VaR(99%)
     */
    private Double var99;
}