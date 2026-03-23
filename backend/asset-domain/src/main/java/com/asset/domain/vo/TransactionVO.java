package com.asset.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 交易记录VO
 */
@Data
public class TransactionVO {

    private Long id;

    /**
     * 持仓ID
     */
    private Long positionId;

    /**
     * 标的代码
     */
    private String symbol;

    /**
     * 标的名称
     */
    private String name;

    /**
     * 所属模块
     */
    private String module;

    /**
     * 交易类型
     */
    private String transactionType;

    /**
     * 交易数量
     */
    private BigDecimal shares;

    /**
     * 成交价格
     */
    private BigDecimal price;

    /**
     * 成交金额
     */
    private BigDecimal totalAmount;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 币种
     */
    private String currency;

    /**
     * 交易日期
     */
    private LocalDate transactionDate;

    /**
     * 备注
     */
    private String notes;

    /**
     * 已实现收益（卖出时计算）
     */
    private BigDecimal realizedProfit;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
