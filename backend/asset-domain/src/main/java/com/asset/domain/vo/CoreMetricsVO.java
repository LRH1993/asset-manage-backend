package com.asset.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 核心指标VO
 * 遵循 backend/CLAUDE.md 规范
 */
@Data
public class CoreMetricsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 最大回撤（百分比数值，如 -8.5 表示 -8.5%）
     */
    private BigDecimal maxDrawdown;

    /**
     * 夏普比率
     */
    private BigDecimal sharpeRatio;

    /**
     * 30日波动率（百分比数值）
     */
    private BigDecimal volatility30d;

    /**
     * 90日波动率（百分比数值）
     */
    private BigDecimal volatility90d;

    /**
     * 集中度风险（0-1之间的小数）
     */
    private BigDecimal concentrationRisk;

    /**
     * VaR(95%)（百分比数值）
     */
    private BigDecimal var95;

    /**
     * VaR(99%)（百分比数值）
     */
    private BigDecimal var99;
}
