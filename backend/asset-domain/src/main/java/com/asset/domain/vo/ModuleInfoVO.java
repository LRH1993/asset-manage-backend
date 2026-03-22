package com.asset.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 模块信息VO
 * 遵循 backend/CLAUDE.md 规范
 */
@Data
public class ModuleInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模块名称
     */
    private String name;

    /**
     * 模块代码（dividend, fixed, growth, allweather）
     */
    private String moduleCode;

    /**
     * 目标权重（百分比数值，如 25 表示 25%）
     */
    private BigDecimal targetWeight;

    /**
     * 当前市值
     */
    private BigDecimal currentValue;

    /**
     * 当前权重（百分比数值）
     */
    private BigDecimal currentWeight;

    /**
     * 偏离度（百分比数值）
     */
    private BigDecimal deviation;

    /**
     * 模块收益率（百分比数值）
     */
    private BigDecimal returnRate;
}
