package com.asset.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 交易记录实体
 */
@Data
@TableName("transactions")
public class Transaction {

    @TableId(type = IdType.AUTO)
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
     * 所属模块（dividend/fixed/growth/allweather）
     */
    private String module;

    /**
     * 市场类型（a_stock/etf/fund/hk_stock/us_stock）
     */
    private String market;

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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 逻辑删除标识
     */
    private Integer deleted;
}