package com.asset.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 模块信息VO
 */
@Data
public class ModuleInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模块名称
     */
    private String name;

    /**
     * 目标权重
     */
    private Double targetWeight;

    /**
     * 当前市值
     */
    private Double currentValue;

    /**
     * 当前权重
     */
    private Double currentWeight;

    /**
     * 偏离度
     */
    private Double deviation;

    /**
     * 模块收益
     */
    private Double returnRate;
}