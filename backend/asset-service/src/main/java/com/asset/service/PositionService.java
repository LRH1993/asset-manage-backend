package com.asset.service;

import com.asset.domain.dto.PositionQueryRequest;
import com.asset.domain.dto.PositionRequest;
import com.asset.domain.vo.PositionSummaryVO;
import com.asset.domain.vo.PositionVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 持仓服务接口
 */
public interface PositionService {

    /**
     * 分页查询持仓列表
     */
    Page<PositionVO> getPageList(PositionQueryRequest request);

    /**
     * 获取持仓详情
     */
    PositionVO getById(Long id);

    /**
     * 创建持仓
     */
    Long create(PositionRequest request);

    /**
     * 更新持仓
     */
    void update(Long id, PositionRequest request);

    /**
     * 删除持仓
     */
    void delete(Long id);

    /**
     * 获取持仓汇总统计
     */
    PositionSummaryVO getSummary();

    /**
     * 更新持仓价格
     */
    void updatePrice(Long id, java.math.BigDecimal price);

    /**
     * 初始化价格历史数据
     * 用于首次部署时初始化昨日收盘价
     */
    void initPriceHistory();
}
