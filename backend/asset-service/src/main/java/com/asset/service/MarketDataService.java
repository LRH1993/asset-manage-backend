package com.asset.service;

import com.asset.integration.model.QuoteData;

import java.time.LocalDate;
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
     * 判断指定日期是否为交易日（根据标的所属市场）
     *
     * @param date   日期
     * @param symbol 标的代码（用于判断市场）
     * @return 是否为交易日
     */
    boolean isTradingDay(LocalDate date, String symbol);

    /**
     * 判断今天是否为交易日（默认A股）
     *
     * @return 是否为交易日
     */
    boolean isTradingDay();

    /**
     * 获取指定市场的下一个交易日
     *
     * @param date   日期
     * @param symbol 标的代码
     * @return 下一个交易日
     */
    LocalDate getNextTradingDay(LocalDate date, String symbol);
}
