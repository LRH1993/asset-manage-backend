package com.asset.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 调仓建议VO
 */
@Data
public class RebalanceSuggestionVO {

    /**
     * 是否需要调仓
     */
    private Boolean needRebalance;

    /**
     * 总偏离度
     */
    private BigDecimal totalDeviation;

    /**
     * 调仓建议列表
     */
    private List<SuggestionItem> suggestions;

    /**
     * 调仓建议项
     */
    @Data
    public static class SuggestionItem {
        /**
         * 模块代码
         */
        private String moduleCode;

        /**
         * 模块名称
         */
        private String moduleName;

        /**
         * 当前占比(%)
         */
        private BigDecimal currentWeight;

        /**
         * 目标占比(%)
         */
        private BigDecimal targetWeight;

        /**
         * 偏离度(%)
         */
        private BigDecimal deviation;

        /**
         * 操作类型: buy/sell
         */
        private String action;

        /**
         * 建议操作金额
         */
        private BigDecimal amount;

        /**
         * 建议说明
         */
        private String description;
    }
}
