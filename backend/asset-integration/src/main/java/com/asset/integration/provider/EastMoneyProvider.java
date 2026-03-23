package com.asset.integration.provider;

import com.asset.integration.config.MarketDataConfig;
import com.asset.integration.model.MarketType;
import com.asset.integration.model.QuoteData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 东方财富数据提供者
 * 用于获取A股和场内ETF的行情数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EastMoneyProvider implements MarketDataProvider {

    private final WebClient.Builder webClientBuilder;
    private final MarketDataConfig config;
    private final ObjectMapper objectMapper;

    /**
     * 行情字段配置
     * f43: 最新价, f44: 最高价, f45: 最低价, f46: 今开价
     * f47: 成交量(手), f48: 成交额
     * f55: 股票代码, f57: 股票代码, f58: 股票名称, f60: 昨收价
     * f170: 涨跌幅
     */
    private static final String FIELDS = "f43,f44,f45,f46,f47,f48,f55,f57,f58,f60,f170";

    @Override
    public QuoteData getQuote(String symbol) {
        try {
            String secId = buildSecId(symbol);
            String url = UriComponentsBuilder.fromHttpUrl(config.getEastMoney().getStockUrl())
                    .queryParam("secid", secId)
                    .queryParam("fields", FIELDS)
                    .queryParam("ut", "fa5fd1943c7b386f172d6893dbfba10b")
                    .toUriString();

            String response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseQuoteResponse(symbol, response);
        } catch (Exception e) {
            log.error("获取A股行情失败: symbol={}", symbol, e);
            return QuoteData.builder()
                    .symbol(symbol)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    public List<QuoteData> getQuotes(List<String> symbols) {
        List<QuoteData> results = new ArrayList<>();

        // 东方财富支持批量查询，但为了稳定性，这里逐个查询
        for (String symbol : symbols) {
            results.add(getQuote(symbol));
            try {
                Thread.sleep(config.getBatchInterval());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return results;
    }

    @Override
    public boolean supports(MarketType marketType) {
        return marketType == MarketType.SH ||
               marketType == MarketType.SZ ||
               marketType == MarketType.ETF;
    }

    @Override
    public String getProviderName() {
        return "EastMoney";
    }

    /**
     * 构建东方财富的 secid
     * 格式：市场ID.股票代码
     */
    private String buildSecId(String symbol) {
        String pureCode = symbol.split("\\.")[0];

        // 判断市场
        if (pureCode.startsWith("6")) {
            // 沪市
            return "1." + pureCode;
        } else {
            // 深市（包括0、3开头的A股和ETF）
            return "0." + pureCode;
        }
    }

    /**
     * 解析行情响应
     */
    private QuoteData parseQuoteResponse(String symbol, String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.path("data");

            if (data.isMissingNode() || data.isNull()) {
                return QuoteData.builder()
                        .symbol(symbol)
                        .success(false)
                        .errorMessage("无行情数据")
                        .build();
            }

            // 解析各字段（东方财富返回的是数值，需要除以100转换为正常值）
            BigDecimal currentPrice = parsePrice(data.path("f43"));
            BigDecimal prevClosePrice = parsePrice(data.path("f60"));  // f60 是昨收价
            BigDecimal openPrice = parsePrice(data.path("f46"));
            BigDecimal highPrice = parsePrice(data.path("f44"));
            BigDecimal lowPrice = parsePrice(data.path("f45"));

            // 成交量（单位：手）
            Long volume = parseLong(data.path("f47"));
            // 成交额
            BigDecimal turnover = parsePrice(data.path("f48"));

            // 涨跌幅（已经乘以100了，如 -236 表示 -2.36%）
            BigDecimal changePercent = parsePrice(data.path("f170"));

            // 股票名称（f58 是名称）
            String stockName = data.path("f58").asText();

            // 计算昨日收盘价（如果接口没有返回）
            if (prevClosePrice == null && currentPrice != null && changePercent != null) {
                // 昨收 = 现价 / (1 + 涨跌幅/100)
                BigDecimal rate = changePercent.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
                prevClosePrice = currentPrice.divide(BigDecimal.ONE.add(rate), 4, RoundingMode.HALF_UP);
            }

            // 计算涨跌额
            BigDecimal changeAmount = null;
            if (currentPrice != null && prevClosePrice != null) {
                changeAmount = currentPrice.subtract(prevClosePrice);
            }

            return QuoteData.builder()
                    .symbol(symbol)
                    .name(stockName)
                    .currentPrice(currentPrice)
                    .prevClosePrice(prevClosePrice)
                    .openPrice(openPrice)
                    .highPrice(highPrice)
                    .lowPrice(lowPrice)
                    .volume(volume != null ? volume * 100 : null) // 转换为股
                    .turnover(turnover)
                    .changePercent(changePercent)
                    .changeAmount(changeAmount)
                    .marketType(MarketType.fromSymbol(symbol))
                    .updateTime(LocalDateTime.now())
                    .dataSource("EastMoney")
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("解析行情数据失败: symbol={}", symbol, e);
            return QuoteData.builder()
                    .symbol(symbol)
                    .success(false)
                    .errorMessage("解析数据失败: " + e.getMessage())
                    .build();
        }
    }

    private BigDecimal parsePrice(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }
        double value = node.asDouble();
        if (value == 0) {
            return null;
        }
        return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }

    private Long parseLong(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }
        return node.asLong();
    }
}
