package com.asset.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 收益曲线数据VO
 * 遵循 backend/CLAUDE.md 规范
 */
@Data
public class ReturnCurveDataVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private String date;

    /**
     * 资产净值
     */
    private BigDecimal value;

    /**
     * 收益率（百分比数值）
     */
    private BigDecimal returnRate;

    /**
     * 基准对比
     */
    private BigDecimal benchmark;
}
