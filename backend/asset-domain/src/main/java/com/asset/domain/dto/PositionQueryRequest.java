package com.asset.domain.dto;

import lombok.Data;

/**
 * 持仓查询请求
 */
@Data
public class PositionQueryRequest {

    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String module;
    private String keyword;
    private String status;
    private String sortBy;
    private String sortOrder = "desc";
}
