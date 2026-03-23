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

    private String name;

    @NotBlank(message = "所属模块不能为空")
    private String module;

    @NotBlank(message = "市场类型不能为空")
    private String market;

    @NotNull(message = "持仓数量不能为空")
    private BigDecimal shares;

    @NotNull(message = "平均成本不能为空")
    private BigDecimal avgCost;
}
