package com.asset.api.controller;

import com.asset.common.result.Result;
import com.asset.integration.model.QuoteData;
import com.asset.service.MarketDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 行情数据控制器
 */
@Slf4j
@Tag(name = "行情数据", description = "行情数据管理接口")
@RestController
@RequestMapping("/api/market-data")
@RequiredArgsConstructor
public class MarketDataController {

    private final MarketDataService marketDataService;

    @Operation(summary = "获取单个标的实时行情")
    @GetMapping("/quote/{symbol}")
    public Result<QuoteData> getQuote(
            @Parameter(description = "标的代码") @PathVariable String symbol) {
        QuoteData quote = marketDataService.getQuote(symbol);
        return Result.success(quote);
    }

    @Operation(summary = "批量获取实时行情")
    @PostMapping("/quotes")
    public Result<List<QuoteData>> getQuotes(
            @Parameter(description = "标的代码列表") @RequestBody List<String> symbols) {
        List<QuoteData> quotes = marketDataService.getQuotes(symbols);
        return Result.success(quotes);
    }

    @Operation(summary = "刷新所有持仓行情")
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refreshAllPositions() {
        if (!marketDataService.isTradingDay()) {
            return Result.success("非交易日，跳过刷新", Map.of(
                    "total", 0,
                    "success", 0,
                    "failed", 0,
                    "message", "非交易日"
            ));
        }

        Map<String, Object> result = marketDataService.refreshAllPositions();
        return Result.success(result);
    }

    @Operation(summary = "刷新单个标的行情")
    @PostMapping("/refresh/{symbol}")
    public Result<QuoteData> refreshPosition(
            @Parameter(description = "标的代码") @PathVariable String symbol) {
        QuoteData quote = marketDataService.refreshPosition(symbol);
        return Result.success(quote);
    }

    @Operation(summary = "检查是否为交易日")
    @GetMapping("/trading-day")
    public Result<Map<String, Object>> checkTradingDay() {
        boolean isTradingDay = marketDataService.isTradingDay();
        return Result.success(Map.of(
                "isTradingDay", isTradingDay,
                "message", isTradingDay ? "交易日" : "非交易日"
        ));
    }
}
