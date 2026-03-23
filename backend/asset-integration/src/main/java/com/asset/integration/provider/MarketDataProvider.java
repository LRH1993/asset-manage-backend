package com.asset.integration.provider;

import com.asset.integration.model.MarketType;
import com.asset.integration.model.QuoteData;

import java.util.List;

/**
 * 行情数据提供者接口
 */
public interface MarketDataProvider {

    /**
     * 获取单个标的的行情数据
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
     * 判断是否支持该市场类型
     *
     * @param marketType 市场类型
     * @return 是否支持
     */
    boolean supports(MarketType marketType);

    /**
     * 获取提供者名称
     *
     * @return 提供者名称
     */
    String getProviderName();
}
