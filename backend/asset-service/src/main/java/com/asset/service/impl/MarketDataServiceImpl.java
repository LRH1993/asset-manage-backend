package com.asset.service.impl;

import com.asset.domain.entity.Position;
import com.asset.domain.entity.PriceHistory;
import com.asset.domain.repository.PositionRepository;
import com.asset.domain.repository.PriceHistoryRepository;
import com.asset.integration.model.MarketType;
import com.asset.integration.model.QuoteData;
import com.asset.integration.model.TradingCalendar;
import com.asset.integration.provider.MarketDataProvider;
import com.asset.service.MarketDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 行情数据服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataServiceImpl implements MarketDataService {

    private final List<MarketDataProvider> providers;
    private final PositionRepository positionRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    @Override
    public QuoteData getQuote(String symbol) {
        // 先尝试从持仓获取市场类型
        Position position = positionRepository.selectOne(
                new LambdaQueryWrapper<Position>()
                        .eq(Position::getSymbol, symbol)
                        .eq(Position::getDeleted, 0)
                        .last("LIMIT 1")
        );

        MarketType marketType;
        if (position != null && position.getMarket() != null) {
            // 从持仓的market字段获取市场类型
            marketType = MarketType.fromCode(position.getMarket());
        } else {
            // 持仓不存在时，从symbol推断
            marketType = MarketType.fromSymbol(symbol);
        }

        return getQuoteByMarketType(symbol, marketType);
    }

    /**
     * 根据指定的市场类型获取行情
     */
    private QuoteData getQuoteByMarketType(String symbol, MarketType marketType) {
        List<MarketDataProvider> supportedProviders = findProviders(marketType);

        if (supportedProviders.isEmpty()) {
            log.warn("未找到支持的市场数据提供者: symbol={}, marketType={}", symbol, marketType);
            return QuoteData.builder()
                    .symbol(symbol)
                    .success(false)
                    .errorMessage("不支持的市场类型")
                    .build();
        }

        // 按顺序尝试各个提供者，直到成功
        QuoteData quote = null;
        List<String> errors = new ArrayList<>();

        for (MarketDataProvider provider : supportedProviders) {
            try {
                log.debug("尝试使用 {} 获取行情: symbol={}", provider.getProviderName(), symbol);
                quote = provider.getQuote(symbol);
                if (quote.isSuccess()) {
                    log.debug("成功获取行情: symbol={}, provider={}", symbol, provider.getProviderName());
                    return quote;
                }
                errors.add(provider.getProviderName() + ": " + quote.getErrorMessage());
            } catch (Exception e) {
                log.warn("提供者 {} 获取行情异常: symbol={}", provider.getProviderName(), symbol, e);
                errors.add(provider.getProviderName() + ": " + e.getMessage());
            }
        }

        // 所有提供者都失败
        return QuoteData.builder()
                .symbol(symbol)
                .success(false)
                .errorMessage("所有数据源都失败: " + String.join("; ", errors))
                .build();
    }

    @Override
    public List<QuoteData> getQuotes(List<String> symbols) {
        // 按市场类型分组，优先从持仓获取market字段
        Map<MarketType, List<String>> groupedSymbols = new HashMap<>();
        for (String symbol : symbols) {
            MarketType marketType;
            // 尝试从持仓获取市场类型
            Position position = positionRepository.selectOne(
                    new LambdaQueryWrapper<Position>()
                            .eq(Position::getSymbol, symbol)
                            .eq(Position::getDeleted, 0)
                            .last("LIMIT 1")
            );
            if (position != null && position.getMarket() != null) {
                marketType = MarketType.fromCode(position.getMarket());
            } else {
                marketType = MarketType.fromSymbol(symbol);
            }
            groupedSymbols.computeIfAbsent(marketType, k -> new ArrayList<>()).add(symbol);
        }

        // 分别获取行情
        List<QuoteData> results = new ArrayList<>();
        for (Map.Entry<MarketType, List<String>> entry : groupedSymbols.entrySet()) {
            List<QuoteData> quotes = getQuotesByMarketType(entry.getValue(), entry.getKey());
            results.addAll(quotes);
        }

        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> refreshAllPositions() {
        log.info("开始刷新所有持仓行情...");

        // 获取所有活跃持仓
        List<Position> positions = positionRepository.selectList(
                new LambdaQueryWrapper<Position>()
                        .eq(Position::getStatus, "active")
                        .eq(Position::getDeleted, 0)
        );

        if (positions.isEmpty()) {
            log.info("没有活跃持仓需要刷新");
            return Map.of(
                    "total", 0,
                    "success", 0,
                    "failed", 0,
                    "message", "没有活跃持仓"
            );
        }

        // 按持仓的市场类型分组（使用持仓的market字段，而不是从symbol推断）
        Map<MarketType, List<Position>> groupedPositions = new HashMap<>();
        for (Position position : positions) {
            MarketType marketType;
            if (position.getMarket() != null) {
                marketType = MarketType.fromCode(position.getMarket());
            } else {
                // 如果market字段为空，从symbol推断（兼容旧数据）
                marketType = MarketType.fromSymbol(position.getSymbol());
            }
            groupedPositions.computeIfAbsent(marketType, k -> new ArrayList<>()).add(position);
        }

        // 更新持仓和保存历史
        int successCount = 0;
        int failedCount = 0;
        List<String> failedSymbols = new ArrayList<>();

        for (Map.Entry<MarketType, List<Position>> entry : groupedPositions.entrySet()) {
            MarketType marketType = entry.getKey();
            List<Position> positionList = entry.getValue();

            // 获取该市场类型的行情
            List<String> symbols = positionList.stream()
                    .map(Position::getSymbol)
                    .distinct()
                    .toList();

            List<QuoteData> quotes = getQuotesByMarketType(symbols, marketType);
            Map<String, QuoteData> quoteMap = new HashMap<>();
            for (QuoteData quote : quotes) {
                quoteMap.put(quote.getSymbol(), quote);
            }

            // 更新每个持仓
            for (Position position : positionList) {
                QuoteData quote = quoteMap.get(position.getSymbol());

                if (quote != null && quote.isSuccess()) {
                    try {
                        updatePositionWithQuote(position, quote);
                        savePriceHistory(quote);
                        successCount++;
                    } catch (Exception e) {
                        log.error("更新持仓失败: symbol={}", position.getSymbol(), e);
                        failedCount++;
                        failedSymbols.add(position.getSymbol());
                    }
                } else {
                    failedCount++;
                    String errorMsg = quote != null ? quote.getErrorMessage() : "未获取到行情数据";
                    failedSymbols.add(position.getSymbol() + "(" + errorMsg + ")");
                    log.warn("获取行情失败: symbol={}, error={}", position.getSymbol(), errorMsg);
                }
            }
        }

        log.info("持仓行情刷新完成: 成功={}, 失败={}", successCount, failedCount);

        return Map.of(
                "total", positions.size(),
                "success", successCount,
                "failed", failedCount,
                "failedSymbols", failedSymbols,
                "message", String.format("刷新完成，成功%d条，失败%d条", successCount, failedCount)
        );
    }

    /**
     * 根据指定的市场类型批量获取行情
     */
    private List<QuoteData> getQuotesByMarketType(List<String> symbols, MarketType marketType) {
        List<MarketDataProvider> supportedProviders = findProviders(marketType);
        List<QuoteData> results = new ArrayList<>();

        if (supportedProviders.isEmpty()) {
            for (String symbol : symbols) {
                results.add(QuoteData.builder()
                        .symbol(symbol)
                        .success(false)
                        .errorMessage("不支持的市场类型")
                        .build());
            }
            return results;
        }

        // 尝试第一个提供者批量获取
        MarketDataProvider provider = supportedProviders.get(0);
        List<QuoteData> quotes = provider.getQuotes(symbols);

        // 检查失败的，用第二个提供者重试
        if (supportedProviders.size() > 1) {
            List<String> failedSymbols = new ArrayList<>();
            for (QuoteData q : quotes) {
                if (!q.isSuccess()) {
                    failedSymbols.add(q.getSymbol());
                }
            }
            if (!failedSymbols.isEmpty()) {
                MarketDataProvider fallbackProvider = supportedProviders.get(1);
                log.info("使用备选提供者 {} 重试 {} 个失败标的", fallbackProvider.getProviderName(), failedSymbols.size());
                List<QuoteData> retryQuotes = fallbackProvider.getQuotes(failedSymbols);
                // 合并结果
                for (int i = 0; i < quotes.size(); i++) {
                    if (!quotes.get(i).isSuccess()) {
                        for (QuoteData retry : retryQuotes) {
                            if (retry.getSymbol().equals(quotes.get(i).getSymbol()) && retry.isSuccess()) {
                                quotes.set(i, retry);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return quotes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuoteData refreshPosition(String symbol) {
        log.info("刷新单个持仓行情: symbol={}", symbol);

        QuoteData quote = getQuote(symbol);

        if (quote.isSuccess()) {
            // 获取持仓
            Position position = positionRepository.selectOne(
                    new LambdaQueryWrapper<Position>()
                            .eq(Position::getSymbol, symbol)
                            .eq(Position::getStatus, "active")
                            .eq(Position::getDeleted, 0)
            );

            if (position != null) {
                updatePositionWithQuote(position, quote);
            }

            // 保存价格历史
            savePriceHistory(quote);
        }

        return quote;
    }

    @Override
    public boolean isTradingDay(LocalDate date, String symbol) {
        TradingCalendar.Market market = getMarketFromSymbol(symbol);
        return TradingCalendar.isTradingDay(date, market);
    }

    @Override
    public boolean isTradingDay() {
        // 默认使用A股交易日历
        return TradingCalendar.isTradingDay(LocalDate.now(), TradingCalendar.Market.A_SHARE);
    }

    @Override
    public LocalDate getNextTradingDay(LocalDate date, String symbol) {
        TradingCalendar.Market market = getMarketFromSymbol(symbol);
        return TradingCalendar.getNextTradingDay(date, market);
    }

    /**
     * 根据标的代码获取市场类型（优先从持仓获取）
     */
    private TradingCalendar.Market getMarketFromSymbol(String symbol) {
        // 尝试从持仓获取market字段
        Position position = positionRepository.selectOne(
                new LambdaQueryWrapper<Position>()
                        .eq(Position::getSymbol, symbol)
                        .eq(Position::getDeleted, 0)
                        .last("LIMIT 1")
        );

        MarketType marketType;
        if (position != null && position.getMarket() != null) {
            marketType = MarketType.fromCode(position.getMarket());
        } else {
            marketType = MarketType.fromSymbol(symbol);
        }

        return convertToTradingCalendarMarket(marketType);
    }

    /**
     * 将MarketType转换为TradingCalendar.Market
     */
    private TradingCalendar.Market convertToTradingCalendarMarket(MarketType marketType) {
        return switch (marketType) {
            case A_STOCK, ETF -> TradingCalendar.Market.A_SHARE;
            case HK_STOCK -> TradingCalendar.Market.HK;
            case US_STOCK -> TradingCalendar.Market.US;
            case FUND -> TradingCalendar.Market.FUND;
        };
    }

    /**
     * 查找支持指定市场类型的所有数据提供者（按优先级排序）
     * 优先级：TencentStock > EastMoney > YahooFinance > EastMoneyFund
     */
    private List<MarketDataProvider> findProviders(MarketType marketType) {
        // 定义优先级顺序
        List<String> priorityOrder = List.of(
                "TencentStock",    // A股/ETF 首选腾讯
                "EastMoney",       // A股/ETF/港股 备选东方财富
                "AlphaVantage",    // 美股
                "YahooFinance",    // 港股备选（已被东方财富替代）
                "EastMoneyFund"    // 场外基金
        );

        return providers.stream()
                .filter(p -> p.supports(marketType))
                .sorted((a, b) -> {
                    int indexA = priorityOrder.indexOf(a.getProviderName());
                    int indexB = priorityOrder.indexOf(b.getProviderName());
                    if (indexA == -1) indexA = Integer.MAX_VALUE;
                    if (indexB == -1) indexB = Integer.MAX_VALUE;
                    return Integer.compare(indexA, indexB);
                })
                .toList();
    }

    /**
     * 使用行情数据更新持仓
     */
    private void updatePositionWithQuote(Position position, QuoteData quote) {
        // 更新当前价格
        if (quote.getCurrentPrice() != null) {
            position.setCurrentPrice(quote.getCurrentPrice());
        }

        // 更新昨日收盘价
        if (quote.getPrevClosePrice() != null) {
            position.setPrevClosePrice(quote.getPrevClosePrice());
        }

        // 更新名称（如果为空）
        if (position.getName() == null || position.getName().isEmpty()) {
            position.setName(quote.getName());
        }

        // 更新市值
        if (position.getCurrentPrice() != null && position.getShares() != null) {
            BigDecimal currentValue = position.getCurrentPrice()
                    .multiply(position.getShares())
                    .setScale(4, RoundingMode.HALF_UP);
            position.setCurrentValue(currentValue);
        }

        position.setUpdateTime(LocalDateTime.now());
        positionRepository.updateById(position);

        log.debug("更新持仓成功: symbol={}, price={}", position.getSymbol(), position.getCurrentPrice());
    }

    /**
     * 保存价格历史记录
     */
    private void savePriceHistory(QuoteData quote) {
        if (quote.getCurrentPrice() == null) {
            return;
        }

        LocalDate today = LocalDate.now();

        // 检查是否已存在今日记录
        PriceHistory existing = priceHistoryRepository.selectOne(
                new LambdaQueryWrapper<PriceHistory>()
                        .eq(PriceHistory::getSymbol, quote.getSymbol())
                        .eq(PriceHistory::getTradeDate, today)
        );

        if (existing != null) {
            // 更新现有记录
            existing.setClosePrice(quote.getCurrentPrice());
            existing.setOpenPrice(quote.getOpenPrice());
            existing.setHighPrice(quote.getHighPrice());
            existing.setLowPrice(quote.getLowPrice());
            existing.setVolume(quote.getVolume());
            existing.setTurnover(quote.getTurnover());
            existing.setAdjClosePrice(quote.getCurrentPrice());
            existing.setUpdateTime(LocalDateTime.now());
            priceHistoryRepository.updateById(existing);
        } else {
            // 创建新记录
            PriceHistory history = new PriceHistory();
            history.setSymbol(quote.getSymbol());
            history.setTradeDate(today);
            history.setOpenPrice(quote.getOpenPrice());
            history.setClosePrice(quote.getCurrentPrice());
            history.setHighPrice(quote.getHighPrice());
            history.setLowPrice(quote.getLowPrice());
            history.setVolume(quote.getVolume());
            history.setTurnover(quote.getTurnover());
            history.setAdjClosePrice(quote.getCurrentPrice());
            history.setCreateTime(LocalDateTime.now());
            priceHistoryRepository.insert(history);
        }
    }
}
