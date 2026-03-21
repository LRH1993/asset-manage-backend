package com.asset.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 风险指标实体
 */
@Data
@TableName("risk_metrics")
public class RiskMetrics {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指标日期
     */
    private LocalDate metricDate;

    /**
     * 总资产
     */
    private BigDecimal totalValue;

    /**
     * 日收益率
     */
    private BigDecimal dailyReturn;

    /**
     * 30日波动率
     */
    private BigDecimal volatility30d;

    /**
     * 90日波动率
     */
    private BigDecimal volatility90d;

    /**
     * 最大回撤
     */
    private BigDecimal maxDrawdown;

    /**
     * 夏普比率
     */
    private BigDecimal sharpeRatio;

    /**
     * 索提诺比率
     */
    private BigDecimal sortinoRatio;

    /**
     * VaR(95%)
     */
    private BigDecimal var95;

    /**
     * VaR(99%)
     */
    private BigDecimal var99;

    /**
     * Beta
     */
    private BigDecimal beta;

    /**
     * Alpha
     */
    private BigDecimal alpha;

    /**
     * 相关性矩阵(JSON)
     */
    private String correlationMatrix;

    /**
     * 行业分布(JSON)
     */
    private String sectorDistribution;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}