package com.asset.integration.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 交易日历数据提供者
 * 从外部 API 获取交易日历数据，支持缓存
 */
@Slf4j
@Component
public class TradingCalendarProvider {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    /**
     * 交易日缓存（按交易所和年份缓存）
     * Key: exchange_year (如 SSE_2024)
     * Value: 该年的交易日集合
     */
    private final ConcurrentHashMap<String, Set<LocalDate>> tradingDaysCache = new ConcurrentHashMap<>();

    /**
     * 新浪交易日历接口
     * 返回格式：var hq_str_mc_tradedate="2025-01-02,2025-01-03,..."
     */
    private static final String SINA_TRADE_CALENDAR_API = "https://hq.sinajs.cn/cn/l2/tradedate.js";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TradingCalendarProvider(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }

    /**
     * 判断指定日期是否为交易日
     *
     * @param date     日期
     * @param exchange 交易所代码（SSE/SZSE）
     * @return 是否为交易日
     */
    public boolean isTradingDay(LocalDate date, String exchange) {
        Set<LocalDate> tradingDays = getTradingDays(date.getYear(), exchange);
        return tradingDays.contains(date);
    }

    /**
     * 获取指定年份的交易日集合
     *
     * @param year     年份
     * @param exchange 交易所代码
     * @return 交易日集合
     */
    public Set<LocalDate> getTradingDays(int year, String exchange) {
        String cacheKey = exchange + "_" + year;

        return tradingDaysCache.computeIfAbsent(cacheKey, key -> {
            try {
                return fetchTradingDaysFromApi(year, exchange);
            } catch (Exception e) {
                log.error("获取交易日历失败: year={}, exchange={}", year, exchange, e);
                // 返回空集合，使用本地默认逻辑
                return new HashSet<>();
            }
        });
    }

    /**
     * 从 API 获取交易日数据
     */
    private Set<LocalDate> fetchTradingDaysFromApi(int year, String exchange) {
        Set<LocalDate> tradingDays = new HashSet<>();

        try {
            String response = webClientBuilder.build()
                    .get()
                    .uri(SINA_TRADE_CALENDAR_API)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null || response.isEmpty()) {
                log.warn("交易日历 API 返回空数据");
                return tradingDays;
            }

            // 解析响应：var hq_str_mc_tradedate="2025-01-02,2025-01-03,..."
            int start = response.indexOf("\"");
            int end = response.lastIndexOf("\"");
            if (start < 0 || end < 0) {
                log.warn("交易日历响应格式错误: {}", response);
                return tradingDays;
            }

            String datesStr = response.substring(start + 1, end);
            String[] dates = datesStr.split(",");

            for (String dateStr : dates) {
                try {
                    LocalDate date = LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
                    // 只保留指定年份的数据
                    if (date.getYear() == year) {
                        tradingDays.add(date);
                    }
                } catch (Exception e) {
                    log.debug("解析日期失败: {}", dateStr);
                }
            }

            log.info("从 API 获取交易日历成功: year={}, exchange={}, count={}", year, exchange, tradingDays.size());

        } catch (Exception e) {
            log.error("获取交易日历 API 失败", e);
        }

        return tradingDays;
    }

    /**
     * 刷新缓存
     */
    public void refreshCache() {
        int currentYear = LocalDate.now().getYear();
        tradingDaysCache.remove("SSE_" + currentYear);
        tradingDaysCache.remove("SZSE_" + currentYear);
        log.info("交易日历缓存已刷新");
    }

    /**
     * 预加载指定年份的交易日历
     */
    public void preload(int year, String exchange) {
        getTradingDays(year, exchange);
    }
}
