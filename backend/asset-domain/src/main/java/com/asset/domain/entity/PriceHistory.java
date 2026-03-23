package com.asset.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 历史行情实体
 */
@Data
@TableName("price_history")
public class PriceHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标的代码
     */
    private String symbol;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 开盘价
     */
    private BigDecimal openPrice;

    /**
     * 收盘价
     */
    private BigDecimal closePrice;

    /**
     * 最高价
     */
    private BigDecimal highPrice;

    /**
     * 最低价
     */
    private BigDecimal lowPrice;

    /**
     * 成交量
     */
    private Long volume;

    /**
     * 成交额
     */
    private BigDecimal turnover;

    /**
     * 复权收盘价
     */
    private BigDecimal adjClosePrice;

    /**
     * 分红
     */
    private BigDecimal dividend;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}