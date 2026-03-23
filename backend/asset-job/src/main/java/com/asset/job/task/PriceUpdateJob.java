package com.asset.job.task;

import com.asset.domain.entity.Position;
import com.asset.domain.entity.PriceHistory;
import com.asset.domain.repository.PositionRepository;
import com.asset.domain.repository.PriceHistoryRepository;
import com.asset.integration.model.QuoteData;
import com.asset.service.MarketDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 价格更新定时任务
 * 每日收盘后更新持仓价格和记录价格历史
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PriceUpdateJob {

    private final PositionRepository positionRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final MarketDataService marketDataService;

    /**
     * 每日15:05执行：获取实时行情并更新持仓价格
     * A股收盘后执行
     */
    @Scheduled(cron = "0 5 15 ? * MON-FRI")
    public void fetchAndUpdateMarketData() {
        log.info("开始获取实时行情...");

        // 检查是否为交易日
        if (!marketDataService.isTradingDay()) {
            log.info("非交易日，跳过行情获取");
            return;
        }

        try {
            Map<String, Object> result = marketDataService.refreshAllPositions();
            log.info("行情获取完成: {}", result);
        } catch (Exception e) {
            log.error("行情获取失败", e);
        }
    }

    /**
     * 每日9:00执行：更新昨日收盘价到持仓表
     * 用于计算今日盈亏
     */
    @Scheduled(cron = "0 0 9 ? * MON-FRI")
    public void updatePrevClosePrice() {
        log.info("开始更新昨日收盘价...");

        // 获取所有活跃持仓
        List<Position> positions = positionRepository.selectList(
                new LambdaQueryWrapper<Position>()
                        .eq(Position::getStatus, "active")
                        .eq(Position::getDeleted, 0)
        );

        LocalDate yesterday = LocalDate.now().minusDays(1);
        int updatedCount = 0;

        for (Position position : positions) {
            try {
                // 从价格历史表获取昨日收盘价
                PriceHistory yesterdayPrice = priceHistoryRepository.selectOne(
                        new LambdaQueryWrapper<PriceHistory>()
                                .eq(PriceHistory::getSymbol, position.getSymbol())
                                .eq(PriceHistory::getTradeDate, yesterday)
                );

                if (yesterdayPrice != null && yesterdayPrice.getClosePrice() != null) {
                    position.setPrevClosePrice(yesterdayPrice.getClosePrice());
                    position.setUpdateTime(LocalDateTime.now());
                    positionRepository.updateById(position);
                    updatedCount++;
                }
            } catch (Exception e) {
                log.error("更新昨日收盘价失败: symbol={}", position.getSymbol(), e);
            }
        }

        log.info("昨日收盘价更新完成: 共{}条记录", updatedCount);
    }

    /**
     * 每日15:30执行：保存当日收盘价到价格历史表
     * 周一至周五执行
     */
    @Scheduled(cron = "0 30 15 ? * MON-FRI")
    public void saveDailyClosePrice() {
        log.info("开始保存每日收盘价...");

        // 获取所有活跃持仓
        List<Position> positions = positionRepository.selectList(
                new LambdaQueryWrapper<Position>()
                        .eq(Position::getStatus, "active")
                        .eq(Position::getDeleted, 0)
        );

        LocalDate today = LocalDate.now();
        int savedCount = 0;

        for (Position position : positions) {
            try {
                // 检查是否已存在今日记录
                PriceHistory existing = priceHistoryRepository.selectOne(
                        new LambdaQueryWrapper<PriceHistory>()
                                .eq(PriceHistory::getSymbol, position.getSymbol())
                                .eq(PriceHistory::getTradeDate, today)
                );

                if (existing != null) {
                    // 更新现有记录
                    existing.setClosePrice(position.getCurrentPrice());
                    existing.setUpdateTime(LocalDateTime.now());
                    priceHistoryRepository.updateById(existing);
                } else {
                    // 创建新记录
                    PriceHistory history = new PriceHistory();
                    history.setSymbol(position.getSymbol());
                    history.setTradeDate(today);
                    history.setClosePrice(position.getCurrentPrice());
                    history.setAdjClosePrice(position.getCurrentPrice());
                    history.setCreateTime(LocalDateTime.now());
                    priceHistoryRepository.insert(history);
                }
                savedCount++;
            } catch (Exception e) {
                log.error("保存价格历史失败: symbol={}", position.getSymbol(), e);
            }
        }

        log.info("每日收盘价保存完成: 共{}条记录", savedCount);
    }

    /**
     * 手动触发：初始化历史价格数据
     * 用于首次部署时初始化数据
     */
    public void initPriceHistory() {
        log.info("开始初始化价格历史数据...");

        List<Position> positions = positionRepository.selectList(
                new LambdaQueryWrapper<Position>()
                        .eq(Position::getStatus, "active")
                        .eq(Position::getDeleted, 0)
        );

        LocalDate today = LocalDate.now();

        for (Position position : positions) {
            // 将当前价格作为昨天的收盘价保存（用于演示）
            if (position.getCurrentPrice() != null) {
                // 保存昨日价格历史
                PriceHistory history = new PriceHistory();
                history.setSymbol(position.getSymbol());
                history.setTradeDate(today.minusDays(1));
                history.setClosePrice(position.getCurrentPrice());
                history.setAdjClosePrice(position.getCurrentPrice());
                history.setCreateTime(LocalDateTime.now());
                priceHistoryRepository.insert(history);

                // 更新持仓的昨日收盘价
                position.setPrevClosePrice(position.getCurrentPrice());
                positionRepository.updateById(position);
            }
        }

        log.info("价格历史数据初始化完成");
    }
}
