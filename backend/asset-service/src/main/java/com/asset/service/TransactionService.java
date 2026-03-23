package com.asset.service;

import com.asset.domain.dto.TransactionQueryRequest;
import com.asset.domain.dto.TransactionRequest;
import com.asset.domain.vo.TransactionSummaryVO;
import com.asset.domain.vo.TransactionVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 交易记录服务接口
 */
public interface TransactionService {

    /**
     * 分页查询交易记录列表
     */
    Page<TransactionVO> getPageList(TransactionQueryRequest request);

    /**
     * 获取交易详情
     */
    TransactionVO getById(Long id);

    /**
     * 创建交易记录
     */
    Long create(TransactionRequest request);

    /**
     * 更新交易记录
     */
    void update(Long id, TransactionRequest request);

    /**
     * 删除交易记录
     */
    void delete(Long id);

    /**
     * 获取交易汇总统计
     */
    TransactionSummaryVO getSummary();
}
