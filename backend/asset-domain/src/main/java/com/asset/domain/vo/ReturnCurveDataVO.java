package com.asset.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 收益曲线数据VO
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
    private Double value;

    /**
     * 收益率
     */
    private Double returnRate;

    /**
     * 基准对比
     */
    private Double benchmark;
}