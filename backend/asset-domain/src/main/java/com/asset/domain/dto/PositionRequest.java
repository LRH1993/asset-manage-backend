package com.asset.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 持仓请求（创建/更新）
 */
@Data
public class PositionRequest {

    private Long id;

    @NotBlank(message = "标的代码不能为空")
    private String symbol;

    @NotBlank(message = "标的名称不能为空")
    private String name;

    @NotBlank(message = "所属模块不能为空")
    private String module;

    private String market;
    private String assetType;

    @NotNull(message = "持仓数量不能为空")
    private BigDecimal shares;

    @NotNull(message = "平均成本不能为空")
    private BigDecimal avgCost;

    private BigDecimal currentPrice;
    private BigDecimal targetWeight;
    private BigDecimal buyPriceThreshold;
    private BigDecimal sellPriceThreshold;
    private String remarks;
}
