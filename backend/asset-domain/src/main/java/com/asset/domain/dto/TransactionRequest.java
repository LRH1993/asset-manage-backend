package com.asset.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 交易记录请求DTO
 */
@Data
public class TransactionRequest {

    /**
     * 持仓ID
     */
    private Long positionId;

    /**
     * 标的代码
     */
    @NotBlank(message = "标的代码不能为空")
    private String symbol;

    /**
     * 交易类型
     */
    @NotBlank(message = "交易类型不能为空")
    private String transactionType;

    /**
     * 交易数量
     */
    @NotNull(message = "交易数量不能为空")
    @Positive(message = "交易数量必须大于0")
    private BigDecimal shares;

    /**
     * 成交价格
     */
    @NotNull(message = "成交价格不能为空")
    @Positive(message = "成交价格必须大于0")
    private BigDecimal price;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 币种
     */
    private String currency;

    /**
     * 交易日期
     */
    @NotNull(message = "交易日期不能为空")
    private LocalDate transactionDate;

    /**
     * 备注
     */
    private String notes;
}
