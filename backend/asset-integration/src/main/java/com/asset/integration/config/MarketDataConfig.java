package com.asset.integration.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 行情数据源配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "market-data")
public class MarketDataConfig {

    /**
     * 东方财富配置
     */
    private EastMoneyConfig eastMoney = new EastMoneyConfig();

    /**
     * Yahoo Finance配置
     */
    private YahooConfig yahoo = new YahooConfig();

    /**
     * 天天基金配置
     */
    private FundConfig fund = new FundConfig();

    /**
     * 请求超时时间（毫秒）
     */
    private int timeout = 10000;

    /**
     * 重试次数
     */
    private int retryCount = 3;

    /**
     * 批量请求间隔（毫秒）
     */
    private long batchInterval = 500;

    @Data
    public static class EastMoneyConfig {
        /**
         * A股行情接口
         */
        private String stockUrl = "https://push2.eastmoney.com/api/qt/stock/get";

        /**
         * 批量行情接口
         */
        private String batchUrl = "https://push2.eastmoney.com/api/qt/ulist.np";

        /**
         * 基金净值接口
         */
        private String fundUrl = "https://fundgz.1234567.com.cn/js";
    }

    @Data
    public static class YahooConfig {
        /**
         * Yahoo Finance API 基础URL
         */
        private String baseUrl = "https://query1.finance.yahoo.com/v8/finance/chart";

        /**
         * 请求间隔（毫秒），避免频率限制
         */
        private long requestInterval = 200;
    }

    @Data
    public static class FundConfig {
        /**
         * 天天基金接口
         */
        private String url = "https://fundgz.1234567.com.cn/js";

        /**
         * 基金详情接口
         */
        private String detailUrl = "https://fund.eastmoney.com/pingzhongdata";
    }
}
