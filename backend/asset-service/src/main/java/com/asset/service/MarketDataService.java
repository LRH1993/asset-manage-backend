package com.asset.service;

import com.asset.integration.model.QuoteData;

import java.util.List;
import java.util.Map;

/**
 * 行情数据服务接口
 */
public interface MarketDataService {

    /**
     * 获取单个标的的实时行情
     *
     * @param symbol 标的代码
     * @return 行情数据
     */
    QuoteData getQuote(String symbol);

    /**
     * 批量获取行情数据
     *
     * @param symbols 标的代码列表
     * @return 行情数据列表
     */
    List<QuoteData> getQuotes(List<String> symbols);

    /**
     * 刷新所有持仓的行情数据
     *
     * @return 刷新结果统计
     */
    Map<String, Object> refreshAllPositions();

    /**
     * 刷新指定持仓的行情数据
     *
     * @param symbol 标的代码
     * @return 行情数据
     */
    QuoteData refreshPosition(String symbol);

    /**
     * 判断是否为交易日
     *
     * @return 是否为交易日
     */
    boolean isTradingDay();
}
