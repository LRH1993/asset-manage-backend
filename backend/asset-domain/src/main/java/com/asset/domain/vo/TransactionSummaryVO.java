package com.asset.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 交易汇总统计VO
 */
@Data
public class TransactionSummaryVO {

    /**
     * 交易次数
     */
    private Integer totalCount;

    /**
     * 买入次数
     */
    private Integer buyCount;

    /**
     * 卖出次数
     */
    private Integer sellCount;

    /**
     * 买入金额
     */
    private BigDecimal buyAmount;

    /**
     * 卖出金额
     */
    private BigDecimal sellAmount;

    /**
     * 手续费总计
     */
    private BigDecimal totalFee;

    /**
     * 已实现收益
     */
    private BigDecimal realizedProfit;

    /**
     * 已实现收益率
     */
    private BigDecimal realizedProfitRate;

    /**
     * 按模块统计
     */
    private List<ModuleSummary> moduleSummaries;

    /**
     * 模块统计
     */
    @Data
    public static class ModuleSummary {
        private String module;
        private String moduleName;
        private Integer count;
        private BigDecimal amount;
    }
}
