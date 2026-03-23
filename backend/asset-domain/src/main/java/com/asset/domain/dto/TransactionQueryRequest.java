package com.asset.domain.dto;

import lombok.Data;

/**
 * 交易记录查询请求
 */
@Data
public class TransactionQueryRequest {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 交易类型筛选
     */
    private String transactionType;

    /**
     * 模块筛选
     */
    private String module;

    /**
     * 标的代码/名称关键词
     */
    private String keyword;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}
