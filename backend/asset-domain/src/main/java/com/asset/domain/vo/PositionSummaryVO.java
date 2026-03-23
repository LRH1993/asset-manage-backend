package com.asset.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 持仓汇总统计VO
 */
@Data
public class PositionSummaryVO {

    private BigDecimal totalValue;
    private BigDecimal totalCost;
    private BigDecimal totalProfit;
    private BigDecimal totalProfitRate;
    private Integer positionCount;
    private Integer profitCount;
    private Integer lossCount;
    private BigDecimal todayProfit;
    private BigDecimal todayProfitRate;
    private List<ModuleSummary> moduleSummaries;

    /**
     * 模块统计
     */
    @Data
    public static class ModuleSummary {
        private String module;
        private String moduleName;
        private BigDecimal value;
        private BigDecimal weight;
        private BigDecimal profitRate;
        private Integer count;
    }
}
