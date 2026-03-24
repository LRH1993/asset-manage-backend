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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Alpha Vantage 数据提供者
 * 用于获取美股行情数据
 * 免费版限制：每天500次调用，每分钟5次调用
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlphaVantageProvider implements MarketDataProvider {

    private final WebClient.Builder webClientBuilder;
    private final MarketDataConfig config;
    private final ObjectMapper objectMapper;

    @Override
    public QuoteData getQuote(String symbol) {
        try {
            // 转换股票代码（去掉 .US 后缀）
            String pureSymbol = symbol.replace(".US", "");

            String url = UriComponentsBuilder.fromHttpUrl(config.getAlphaVantage().getBaseUrl())
                    .queryParam("function", "GLOBAL_QUOTE")
                    .queryParam("symbol", pureSymbol)
                    .queryParam("apikey", config.getAlphaVantage().getApiKey())
                    .toUriString();

            String response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseQuoteResponse(symbol, response);
        } catch (Exception e) {
            log.error("获取Alpha Vantage行情失败: symbol={}", symbol, e);
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

        for (String symbol : symbols) {
            results.add(getQuote(symbol));
            try {
                // Alpha Vantage 免费版每分钟5次调用限制
                Thread.sleep(config.getAlphaVantage().getRequestInterval());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return results;
    }

    @Override
    public boolean supports(MarketType marketType) {
        return marketType == MarketType.US_STOCK;
    }

    @Override
    public String getProviderName() {
        return "AlphaVantage";
    }

    /**
     * 解析 Alpha Vantage 响应
     */
    private QuoteData parseQuoteResponse(String originalSymbol, String response) {
        try {
            JsonNode root = objectMapper.readTree(response);

            // 检查错误信息
            if (root.has("Error Message")) {
                return QuoteData.builder()
                        .symbol(originalSymbol)
                        .success(false)
                        .errorMessage("API错误: " + root.path("Error Message").asText())
                        .build();
            }

            // 检查频率限制
            if (root.has("Note")) {
                return QuoteData.builder()
                        .symbol(originalSymbol)
                        .success(false)
                        .errorMessage("API调用频率限制: " + root.path("Note").asText())
                        .build();
            }

            JsonNode quote = root.path("Global Quote");

            if (quote.isMissingNode() || quote.isEmpty()) {
                return QuoteData.builder()
                        .symbol(originalSymbol)
                        .success(false)
                        .errorMessage("无行情数据")
                        .build();
            }

            // 解析各字段
            // "05. price": "404.0400"
            BigDecimal currentPrice = parsePrice(quote.path("05. price"));
            BigDecimal prevClosePrice = parsePrice(quote.path("08. previous close"));
            BigDecimal openPrice = parsePrice(quote.path("02. open"));
            BigDecimal highPrice = parsePrice(quote.path("03. high"));
            BigDecimal lowPrice = parsePrice(quote.path("04. low"));
            Long volume = parseVolume(quote.path("06. volume"));

            // 解析涨跌幅 "10. change percent": "-2.2594%"
            BigDecimal changePercent = parseChangePercent(quote.path("10. change percent"));
            BigDecimal changeAmount = parsePrice(quote.path("09. change"));

            // 解析交易日期 "07. latest trading day": "2026-03-23"
            LocalDateTime updateTime = parseTradingDay(quote.path("07. latest trading day"));

            return QuoteData.builder()
                    .symbol(originalSymbol)
                    .name(quote.path("01. symbol").asText())
                    .currentPrice(currentPrice != null ? currentPrice.setScale(4, RoundingMode.HALF_UP) : null)
                    .prevClosePrice(prevClosePrice != null ? prevClosePrice.setScale(4, RoundingMode.HALF_UP) : null)
                    .openPrice(openPrice != null ? openPrice.setScale(4, RoundingMode.HALF_UP) : null)
                    .highPrice(highPrice != null ? highPrice.setScale(4, RoundingMode.HALF_UP) : null)
                    .lowPrice(lowPrice != null ? lowPrice.setScale(4, RoundingMode.HALF_UP) : null)
                    .volume(volume)
                    .changePercent(changePercent != null ? changePercent.setScale(2, RoundingMode.HALF_UP) : null)
                    .changeAmount(changeAmount != null ? changeAmount.setScale(4, RoundingMode.HALF_UP) : null)
                    .marketType(MarketType.US_STOCK)
                    .updateTime(updateTime)
                    .dataSource("AlphaVantage")
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("解析Alpha Vantage行情数据失败: symbol={}", originalSymbol, e);
            return QuoteData.builder()
                    .symbol(originalSymbol)
                    .success(false)
                    .errorMessage("解析数据失败: " + e.getMessage())
                    .build();
        }
    }

    private BigDecimal parsePrice(JsonNode node) {
        if (node.isMissingNode() || node.isNull() || node.asText().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(node.asText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long parseVolume(JsonNode node) {
        if (node.isMissingNode() || node.isNull() || node.asText().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(node.asText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 解析涨跌幅，如 "-2.2594%" -> -2.2594
     */
    private BigDecimal parseChangePercent(JsonNode node) {
        if (node.isMissingNode() || node.isNull() || node.asText().isEmpty()) {
            return null;
        }
        try {
            String text = node.asText().replace("%", "").trim();
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 解析交易日期
     */
    private LocalDateTime parseTradingDay(JsonNode node) {
        if (node.isMissingNode() || node.isNull() || node.asText().isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            LocalDate date = LocalDate.parse(node.asText(), DateTimeFormatter.ISO_LOCAL_DATE);
            return date.atTime(16, 0); // 美股收盘时间
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
