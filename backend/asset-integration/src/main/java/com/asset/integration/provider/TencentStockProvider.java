package com.asset.integration.provider;

import com.asset.integration.config.MarketDataConfig;
import com.asset.integration.model.MarketType;
import com.asset.integration.model.QuoteData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 腾讯股票数据提供者
 * 用于获取A股和ETF的行情数据（作为东方财富的备选）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TencentStockProvider implements MarketDataProvider {

    private final WebClient.Builder webClientBuilder;
    private final MarketDataConfig config;

    private static final String TENCENT_API = "https://qt.gtimg.cn/q=";

    @Override
    public QuoteData getQuote(String symbol) {
        try {
            String tencentSymbol = buildTencentSymbol(symbol);
            String url = TENCENT_API + tencentSymbol;

            byte[] responseBytes = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            // 腾讯API返回GBK编码
            String response = new String(responseBytes, Charset.forName("GBK"));
            return parseTencentResponse(symbol, response);
        } catch (Exception e) {
            log.error("获取腾讯行情失败: symbol={}", symbol, e);
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

        // 腾讯支持批量查询，用逗号分隔
        StringBuilder symbolsParam = new StringBuilder();
        for (int i = 0; i < symbols.size(); i++) {
            if (i > 0) symbolsParam.append(",");
            symbolsParam.append(buildTencentSymbol(symbols.get(i)));
        }

        try {
            String url = TENCENT_API + symbolsParam;
            byte[] responseBytes = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            String response = new String(responseBytes, Charset.forName("GBK"));
            return parseBatchResponse(symbols, response);
        } catch (Exception e) {
            log.error("批量获取腾讯行情失败", e);
            // 返回所有失败结果
            for (String symbol : symbols) {
                results.add(QuoteData.builder()
                        .symbol(symbol)
                        .success(false)
                        .errorMessage(e.getMessage())
                        .build());
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
        return "TencentStock";
    }

    /**
     * 构建腾讯股票代码
     * sh + 6位代码（沪市）
     * sz + 6位代码（深市）
     */
    private String buildTencentSymbol(String symbol) {
        String pureCode = symbol.split("\\.")[0];
        if (pureCode.startsWith("6")) {
            return "sh" + pureCode;
        } else {
            return "sz" + pureCode;
        }
    }

    /**
     * 解析腾讯API响应
     * 格式: v_sh600519="1~贵州茅台~600519~1409.95~1445.00~..."
     */
    private QuoteData parseTencentResponse(String originalSymbol, String response) {
        try {
            // 查找等号后面的内容
            int start = response.indexOf("\"");
            int end = response.lastIndexOf("\"");
            if (start < 0 || end < 0) {
                return QuoteData.builder()
                        .symbol(originalSymbol)
                        .success(false)
                        .errorMessage("响应格式错误")
                        .build();
            }

            String content = response.substring(start + 1, end);
            String[] fields = content.split("~");

            if (fields.length < 35) {
                return QuoteData.builder()
                        .symbol(originalSymbol)
                        .success(false)
                        .errorMessage("响应字段不足")
                        .build();
            }

            // 解析字段（索引从0开始）
            // 字段1 (索引1): 股票名称
            String name = fields[1];
            // 字段3 (索引3): 当前价格
            BigDecimal currentPrice = parseBigDecimal(fields[3]);
            // 字段4 (索引4): 昨日收盘价
            BigDecimal prevClosePrice = parseBigDecimal(fields[4]);
            // 字段5 (索引5): 今开价
            BigDecimal openPrice = parseBigDecimal(fields[5]);
            // 字段33 (索引33): 最高价
            BigDecimal highPrice = parseBigDecimal(fields[33]);
            // 字段34 (索引34): 最低价
            BigDecimal lowPrice = parseBigDecimal(fields[34]);
            // 字段6 (索引6): 成交量（手）
            Long volume = parseLong(fields[6]);
            // 字段37 (索引37): 成交额（万）
            BigDecimal turnover = parseBigDecimal(fields[37]);
            if (turnover != null) {
                turnover = turnover.multiply(BigDecimal.valueOf(10000)); // 转换为元
            }
            // 字段32 (索引32): 涨跌幅
            BigDecimal changePercent = parseBigDecimal(fields[32]);
            // 计算涨跌额
            BigDecimal changeAmount = null;
            if (currentPrice != null && prevClosePrice != null) {
                changeAmount = currentPrice.subtract(prevClosePrice);
            }

            return QuoteData.builder()
                    .symbol(originalSymbol)
                    .name(name)
                    .currentPrice(currentPrice)
                    .prevClosePrice(prevClosePrice)
                    .openPrice(openPrice)
                    .highPrice(highPrice)
                    .lowPrice(lowPrice)
                    .volume(volume != null ? volume * 100 : null) // 手转换为股
                    .turnover(turnover)
                    .changePercent(changePercent)
                    .changeAmount(changeAmount)
                    .marketType(MarketType.fromSymbol(originalSymbol))
                    .updateTime(LocalDateTime.now())
                    .dataSource("TencentStock")
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("解析腾讯行情数据失败: symbol={}", originalSymbol, e);
            return QuoteData.builder()
                    .symbol(originalSymbol)
                    .success(false)
                    .errorMessage("解析数据失败: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 解析批量响应
     */
    private List<QuoteData> parseBatchResponse(List<String> symbols, String response) {
        List<QuoteData> results = new ArrayList<>();
        String[] lines = response.split(";");

        for (int i = 0; i < symbols.size(); i++) {
            if (i < lines.length) {
                results.add(parseTencentResponse(symbols.get(i), lines[i]));
            } else {
                results.add(QuoteData.builder()
                        .symbol(symbols.get(i))
                        .success(false)
                        .errorMessage("未获取到数据")
                        .build());
            }
        }

        return results;
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value).setScale(4, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return null;
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return null;
        }
    }
}
