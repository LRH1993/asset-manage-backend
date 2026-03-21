package com.asset.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 模块配置VO
 */
@Data
public class ModuleAllocationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 红利模块
     */
    private ModuleInfoVO dividend;

    /**
     * 固收模块
     */
    private ModuleInfoVO fixed;

    /**
     * 成长模块
     */
    private ModuleInfoVO growth;

    /**
     * 全天候模块
     */
    private ModuleInfoVO allweather;
}