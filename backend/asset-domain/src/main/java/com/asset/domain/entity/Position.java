package com.asset.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 持仓实体
 */
@Data
@TableName("positions")
public class Position {

    @TableId(type = IdType.AUTO)
    private Long id;

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
     * A股:a_stock, 场内ETF:etf, 场外基金:fund, 港股:hk_stock, 美股:us_stock
     */
    private String market;

    /**
     * 持仓数量
     */
    private BigDecimal shares;

    /**
     * 平均成本
     */
    private BigDecimal avgCost;

    /**
     * 当前价格（每日行情更新）
     */
    private BigDecimal currentPrice;

    /**
     * 当前市值（计算得出）
     */
    private BigDecimal currentValue;

    /**
     * 昨日收盘价（用于计算今日盈亏）
     */
    private BigDecimal prevClosePrice;

    /**
     * 状态（active/inactive/sold）
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 逻辑删除标识
     */
    private Integer deleted;
}