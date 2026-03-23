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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 东方财富基金数据提供者
 * 用于获取场外基金的净值数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EastMoneyFundProvider implements MarketDataProvider {

    private final WebClient.Builder webClientBuilder;
    private final MarketDataConfig config;
    private final ObjectMapper objectMapper;

    /**
     * JSONP响应解析正则
     */
    private static final Pattern JSONP_PATTERN = Pattern.compile("jsonpgz\\((.*)\\)");

    @Override
    public QuoteData getQuote(String fundCode) {
        try {
            // 确保是6位基金代码
            String code = fundCode.split("\\.")[0];

            String url = config.getFund().getUrl() + "/" + code + ".js";

            String response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseFundResponse(fundCode, response);
        } catch (Exception e) {
            log.error("获取基金净值失败: fundCode={}", fundCode, e);
            return QuoteData.builder()
                    .symbol(fundCode)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    public List<QuoteData> getQuotes(List<String> fundCodes) {
        List<QuoteData> results = new ArrayList<>();

        for (String code : fundCodes) {
            results.add(getQuote(code));
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
        return marketType == MarketType.FUND;
    }

    @Override
    public String getProviderName() {
        return "EastMoneyFund";
    }

    /**
     * 解析天天基金响应
     * 响应格式：jsonpgz({"fundcode":"000001","name":"华夏成长","jzrq":"2024-01-15","dwjz":"1.234","gsz":"1.235",...});
     */
    private QuoteData parseFundResponse(String originalCode, String response) {
        try {
            if (response == null || response.isEmpty()) {
                return QuoteData.builder()
                        .symbol(originalCode)
                        .success(false)
                        .errorMessage("响应为空")
                        .build();
            }

            // 解析JSONP响应
            Matcher matcher = JSONP_PATTERN.matcher(response);
            String jsonStr;
            if (matcher.find()) {
                jsonStr = matcher.group(1);
            } else {
                // 尝试直接解析JSON
                jsonStr = response;
            }

            JsonNode data = objectMapper.readTree(jsonStr);

            // 基金代码
            String fundCode = data.path("fundcode").asText(originalCode);
            // 基金名称
            String fundName = data.path("name").asText();
            // 单位净值（昨日）
            BigDecimal dwjz = parseBigDecimal(data.path("dwjz"));
            // 估算净值（今日实时）
            BigDecimal gsz = parseBigDecimal(data.path("gsz"));
            // 净值日期
            String jzrq = data.path("jzrq").asText();
            // 估算涨跌幅
            BigDecimal gszzl = parseBigDecimal(data.path("gszzl"));

            // 当前价格使用估算净值（如果有）或昨日净值
            BigDecimal currentPrice = gsz != null ? gsz : dwjz;

            // 涨跌幅
            BigDecimal changePercent = gszzl;

            // 计算涨跌额和昨日收盘价
            BigDecimal changeAmount = null;
            BigDecimal prevClosePrice = dwjz;
            if (currentPrice != null && prevClosePrice != null) {
                changeAmount = currentPrice.subtract(prevClosePrice);
            }

            // 如果涨跌幅为空，手动计算
            if (changePercent == null && currentPrice != null && prevClosePrice != null &&
                prevClosePrice.compareTo(BigDecimal.ZERO) > 0) {
                changePercent = currentPrice.subtract(prevClosePrice)
                        .divide(prevClosePrice, 6, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            return QuoteData.builder()
                    .symbol(originalCode)
                    .name(fundName)
                    .currentPrice(currentPrice != null ? currentPrice.setScale(4, RoundingMode.HALF_UP) : null)
                    .prevClosePrice(prevClosePrice != null ? prevClosePrice.setScale(4, RoundingMode.HALF_UP) : null)
                    .changePercent(changePercent)
                    .changeAmount(changeAmount != null ? changeAmount.setScale(4, RoundingMode.HALF_UP) : null)
                    .marketType(MarketType.FUND)
                    .updateTime(LocalDateTime.now())
                    .dataSource("EastMoneyFund")
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("解析基金数据失败: fundCode={}", originalCode, e);
            return QuoteData.builder()
                    .symbol(originalCode)
                    .success(false)
                    .errorMessage("解析数据失败: " + e.getMessage())
                    .build();
        }
    }

    private BigDecimal parseBigDecimal(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }
        try {
            return BigDecimal.valueOf(node.asDouble());
        } catch (Exception e) {
            return null;
        }
    }
}
