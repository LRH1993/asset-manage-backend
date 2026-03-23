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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Yahoo Finance 数据提供者
 * 用于获取美股和港股的行情数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class YahooFinanceProvider implements MarketDataProvider {

    private final WebClient.Builder webClientBuilder;
    private final MarketDataConfig config;
    private final ObjectMapper objectMapper;

    @Override
    public QuoteData getQuote(String symbol) {
        try {
            // 转换股票代码格式
            String yahooSymbol = convertToYahooSymbol(symbol);

            String url = config.getYahoo().getBaseUrl() + "/" + yahooSymbol +
                    "?interval=1d&range=2d";

            String response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .header("User-Agent", "Mozilla/5.0")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseYahooResponse(symbol, response);
        } catch (Exception e) {
            log.error("获取Yahoo行情失败: symbol={}", symbol, e);
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
                Thread.sleep(config.getYahoo().getRequestInterval());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return results;
    }

    @Override
    public boolean supports(MarketType marketType) {
        return marketType == MarketType.HK || marketType == MarketType.US;
    }

    @Override
    public String getProviderName() {
        return "YahooFinance";
    }

    /**
     * 转换股票代码为Yahoo格式
     * 例如：
     * - 00700 -> 00700.HK
     * - AAPL.US -> AAPL
     */
    private String convertToYahooSymbol(String symbol) {
        if (symbol.contains(".HK")) {
            return symbol;
        }
        if (symbol.contains(".US")) {
            return symbol.replace(".US", "");
        }
        // 纯数字5位，认为是港股
        if (symbol.matches("\\d{5}")) {
            return symbol + ".HK";
        }
        // 纯字母，认为是美股
        if (symbol.matches("[A-Z]+")) {
            return symbol;
        }
        return symbol;
    }

    /**
     * 解析Yahoo Finance响应
     */
    private QuoteData parseYahooResponse(String originalSymbol, String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode chart = root.path("chart").path("result");

            if (chart.isMissingNode() || chart.isNull() || !chart.isArray() || chart.isEmpty()) {
                return QuoteData.builder()
                        .symbol(originalSymbol)
                        .success(false)
                        .errorMessage("无行情数据")
                        .build();
            }

            JsonNode result = chart.get(0);
            JsonNode meta = result.path("meta");
            JsonNode indicators = result.path("indicators").path("quote");

            if (meta.isMissingNode()) {
                return QuoteData.builder()
                        .symbol(originalSymbol)
                        .success(false)
                        .errorMessage("缺少元数据")
                        .build();
            }

            // 获取最新价格
            BigDecimal currentPrice = null;
            BigDecimal openPrice = null;
            BigDecimal highPrice = null;
            BigDecimal lowPrice = null;
            Long volume = null;
            BigDecimal prevClosePrice = null;

            // 从meta获取当前价格
            if (meta.has("regularMarketPrice")) {
                currentPrice = BigDecimal.valueOf(meta.path("regularMarketPrice").asDouble());
            }
            if (meta.has("previousClose")) {
                prevClosePrice = BigDecimal.valueOf(meta.path("previousClose").asDouble());
            }

            // 从indicators获取OHLCV数据
            if (indicators.isArray() && !indicators.isEmpty()) {
                JsonNode quoteData = indicators.get(0);
                JsonNode opens = quoteData.path("open");
                JsonNode closes = quoteData.path("close");
                JsonNode highs = quoteData.path("high");
                JsonNode lows = quoteData.path("low");
                JsonNode volumes = quoteData.path("volume");

                // 获取最后一条数据
                int lastIndex = -1;
                if (closes.isArray() && closes.size() > 0) {
                    for (int i = closes.size() - 1; i >= 0; i--) {
                        if (!closes.get(i).isNull()) {
                            lastIndex = i;
                            break;
                        }
                    }
                }

                if (lastIndex >= 0) {
                    if (opens.isArray() && lastIndex < opens.size() && !opens.get(lastIndex).isNull()) {
                        openPrice = BigDecimal.valueOf(opens.get(lastIndex).asDouble());
                    }
                    if (highs.isArray() && lastIndex < highs.size() && !highs.get(lastIndex).isNull()) {
                        highPrice = BigDecimal.valueOf(highs.get(lastIndex).asDouble());
                    }
                    if (lows.isArray() && lastIndex < lows.size() && !lows.get(lastIndex).isNull()) {
                        lowPrice = BigDecimal.valueOf(lows.get(lastIndex).asDouble());
                    }
                    if (volumes.isArray() && lastIndex < volumes.size() && !volumes.get(lastIndex).isNull()) {
                        volume = volumes.get(lastIndex).asLong();
                    }

                    // 如果倒数第二条数据存在，作为昨日收盘价
                    if (prevClosePrice == null && lastIndex > 0 && closes.isArray() &&
                        lastIndex - 1 < closes.size() && !closes.get(lastIndex - 1).isNull()) {
                        prevClosePrice = BigDecimal.valueOf(closes.get(lastIndex - 1).asDouble());
                    }
                }
            }

            // 计算涨跌幅
            BigDecimal changePercent = null;
            BigDecimal changeAmount = null;
            if (currentPrice != null && prevClosePrice != null &&
                prevClosePrice.compareTo(BigDecimal.ZERO) > 0) {
                changeAmount = currentPrice.subtract(prevClosePrice);
                changePercent = changeAmount.divide(prevClosePrice, 6, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            // 获取更新时间
            LocalDateTime updateTime = LocalDateTime.now();
            if (meta.has("regularMarketTime")) {
                long timestamp = meta.path("regularMarketTime").asLong();
                updateTime = ZonedDateTime.ofInstant(
                        java.time.Instant.ofEpochSecond(timestamp),
                        ZoneId.systemDefault()
                ).toLocalDateTime();
            }

            return QuoteData.builder()
                    .symbol(originalSymbol)
                    .name(meta.path("symbol").asText())
                    .currentPrice(currentPrice != null ? currentPrice.setScale(4, RoundingMode.HALF_UP) : null)
                    .prevClosePrice(prevClosePrice != null ? prevClosePrice.setScale(4, RoundingMode.HALF_UP) : null)
                    .openPrice(openPrice != null ? openPrice.setScale(4, RoundingMode.HALF_UP) : null)
                    .highPrice(highPrice != null ? highPrice.setScale(4, RoundingMode.HALF_UP) : null)
                    .lowPrice(lowPrice != null ? lowPrice.setScale(4, RoundingMode.HALF_UP) : null)
                    .volume(volume)
                    .changePercent(changePercent != null ? changePercent.setScale(2, RoundingMode.HALF_UP) : null)
                    .changeAmount(changeAmount != null ? changeAmount.setScale(4, RoundingMode.HALF_UP) : null)
                    .marketType(MarketType.fromSymbol(originalSymbol))
                    .updateTime(updateTime)
                    .dataSource("YahooFinance")
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("解析Yahoo行情数据失败: symbol={}", originalSymbol, e);
            return QuoteData.builder()
                    .symbol(originalSymbol)
                    .success(false)
                    .errorMessage("解析数据失败: " + e.getMessage())
                    .build();
        }
    }
}
