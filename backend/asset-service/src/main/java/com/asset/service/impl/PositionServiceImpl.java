package com.asset.service.impl;

import com.asset.domain.dto.PositionQueryRequest;
import com.asset.domain.dto.PositionRequest;
import com.asset.common.exception.BusinessException;
import com.asset.domain.entity.Position;
import com.asset.domain.entity.PriceHistory;
import com.asset.domain.repository.PositionRepository;
import com.asset.domain.repository.PriceHistoryRepository;
import com.asset.domain.vo.PositionSummaryVO;
import com.asset.domain.vo.PositionVO;
import com.asset.service.PositionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 持仓服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    @Override
    public Page<PositionVO> getPageList(PositionQueryRequest request) {
        Page<Position> page = new Page<>(request.getPageNum(), request.getPageSize());

        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();

        // 模块筛选
        if (StringUtils.hasText(request.getModule())) {
            wrapper.eq(Position::getModule, request.getModule());
        }

        // 状态筛选
        if (StringUtils.hasText(request.getStatus())) {
            wrapper.eq(Position::getStatus, request.getStatus());
        }

        // 关键词搜索
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w
                    .like(Position::getSymbol, request.getKeyword())
                    .or()
                    .like(Position::getName, request.getKeyword())
            );
        }

        // 排序
        if (StringUtils.hasText(request.getSortBy())) {
            boolean isAsc = "asc".equalsIgnoreCase(request.getSortOrder());
            switch (request.getSortBy()) {
                case "currentValue":
                    wrapper.orderBy(true, isAsc, Position::getCurrentValue);
                    break;
                case "profitRate":
                    // 收益率需要计算，这里用市值和成本的差值排序
                    wrapper.orderBy(true, isAsc, Position::getCurrentValue);
                    break;
                case "updateTime":
                    wrapper.orderBy(true, isAsc, Position::getUpdateTime);
                    break;
                default:
                    wrapper.orderByDesc(Position::getUpdateTime);
            }
        } else {
            wrapper.orderByDesc(Position::getUpdateTime);
        }

        Page<Position> positionPage = positionRepository.selectPage(page, wrapper);

        // 转换为VO
        Page<PositionVO> voPage = new Page<>(positionPage.getCurrent(), positionPage.getSize(), positionPage.getTotal());
        voPage.setRecords(positionPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public PositionVO getById(Long id) {
        Position position = positionRepository.selectById(id);
        if (position == null) {
            throw new BusinessException(404, "持仓不存在: " + id);
        }
        return convertToVO(position);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PositionRequest request) {
        Position position = new Position();
        position.setSymbol(request.getSymbol());
        position.setName(request.getName());
        position.setModule(request.getModule());
        position.setMarket(request.getMarket());
        position.setAssetType(request.getAssetType());
        position.setShares(request.getShares());
        position.setAvgCost(request.getAvgCost());
        position.setCurrentPrice(request.getCurrentPrice());
        position.setTargetWeight(request.getTargetWeight());
        position.setBuyPriceThreshold(request.getBuyPriceThreshold());
        position.setSellPriceThreshold(request.getSellPriceThreshold());
        position.setRemarks(request.getRemarks());
        position.setStatus("active");
        position.setCreateTime(LocalDateTime.now());
        position.setUpdateTime(LocalDateTime.now());
        position.setDeleted(0);

        // 计算当前市值
        if (request.getCurrentPrice() != null && request.getShares() != null) {
            position.setCurrentValue(request.getCurrentPrice().multiply(request.getShares()));
        }

        positionRepository.insert(position);
        log.info("创建持仓成功: id={}, symbol={}", position.getId(), position.getSymbol());
        return position.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, PositionRequest request) {
        Position position = positionRepository.selectById(id);
        if (position == null) {
            throw new BusinessException(404, "持仓不存在: " + id);
        }

        position.setSymbol(request.getSymbol());
        position.setName(request.getName());
        position.setModule(request.getModule());
        position.setMarket(request.getMarket());
        position.setAssetType(request.getAssetType());
        position.setShares(request.getShares());
        position.setAvgCost(request.getAvgCost());
        position.setCurrentPrice(request.getCurrentPrice());
        position.setTargetWeight(request.getTargetWeight());
        position.setBuyPriceThreshold(request.getBuyPriceThreshold());
        position.setSellPriceThreshold(request.getSellPriceThreshold());
        position.setRemarks(request.getRemarks());
        position.setUpdateTime(LocalDateTime.now());

        // 重新计算市值
        if (request.getCurrentPrice() != null && request.getShares() != null) {
            position.setCurrentValue(request.getCurrentPrice().multiply(request.getShares()));
        }

        positionRepository.updateById(position);
        log.info("更新持仓成功: id={}, symbol={}", id, position.getSymbol());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Position position = positionRepository.selectById(id);
        if (position == null) {
            throw new BusinessException(404, "持仓不存在: " + id);
        }
        positionRepository.deleteById(id);
        log.info("删除持仓成功: id={}", id);
    }

    @Override
    public PositionSummaryVO getSummary() {
        List<Position> positions = positionRepository.selectList(
                new LambdaQueryWrapper<Position>()
                        .eq(Position::getStatus, "active")
                        .eq(Position::getDeleted, 0)
        );

        PositionSummaryVO summary = new PositionSummaryVO();

        // 计算总市值和总成本
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        int profitCount = 0;
        int lossCount = 0;

        for (Position p : positions) {
            if (p.getCurrentValue() != null) {
                totalValue = totalValue.add(p.getCurrentValue());
            }
            if (p.getAvgCost() != null && p.getShares() != null) {
                totalCost = totalCost.add(p.getAvgCost().multiply(p.getShares()));
            }

            // 计算盈亏
            if (p.getCurrentValue() != null && p.getAvgCost() != null && p.getShares() != null) {
                BigDecimal cost = p.getAvgCost().multiply(p.getShares());
                if (p.getCurrentValue().compareTo(cost) >= 0) {
                    profitCount++;
                } else {
                    lossCount++;
                }
            }
        }

        summary.setTotalValue(totalValue);
        summary.setTotalCost(totalCost);
        summary.setTotalProfit(totalValue.subtract(totalCost));
        summary.setPositionCount(positions.size());
        summary.setProfitCount(profitCount);
        summary.setLossCount(lossCount);

        // 计算总收益率
        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal profitRate = totalValue.subtract(totalCost)
                    .divide(totalCost, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            summary.setTotalProfitRate(profitRate);
        } else {
            summary.setTotalProfitRate(BigDecimal.ZERO);
        }

        // 计算今日盈亏
        BigDecimal todayProfit = BigDecimal.ZERO;
        BigDecimal yesterdayTotalValue = BigDecimal.ZERO;

        for (Position p : positions) {
            // 计算今日盈亏金额
            BigDecimal positionTodayProfit = calculateTodayProfitAmount(p);
            if (positionTodayProfit != null) {
                todayProfit = todayProfit.add(positionTodayProfit);
            }

            // 计算昨日总市值
            BigDecimal yesterdayValue = calculateYesterdayValue(p);
            if (yesterdayValue != null) {
                yesterdayTotalValue = yesterdayTotalValue.add(yesterdayValue);
            }
        }

        summary.setTodayProfit(todayProfit);

        // 计算今日盈亏率
        if (yesterdayTotalValue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal todayRate = todayProfit
                    .divide(yesterdayTotalValue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            summary.setTodayProfitRate(todayRate);
        } else {
            summary.setTodayProfitRate(BigDecimal.ZERO);
        }

        // 按模块统计
        Map<String, List<Position>> moduleMap = positions.stream()
                .collect(Collectors.groupingBy(Position::getModule));

        List<PositionSummaryVO.ModuleSummary> moduleSummaries = new ArrayList<>();
        for (Map.Entry<String, List<Position>> entry : moduleMap.entrySet()) {
            PositionSummaryVO.ModuleSummary ms = new PositionSummaryVO.ModuleSummary();
            ms.setModule(entry.getKey());
            ms.setModuleName(getModuleName(entry.getKey()));

            BigDecimal moduleValue = BigDecimal.ZERO;
            BigDecimal moduleCost = BigDecimal.ZERO;
            for (Position p : entry.getValue()) {
                if (p.getCurrentValue() != null) {
                    moduleValue = moduleValue.add(p.getCurrentValue());
                }
                if (p.getAvgCost() != null && p.getShares() != null) {
                    moduleCost = moduleCost.add(p.getAvgCost().multiply(p.getShares()));
                }
            }

            ms.setValue(moduleValue);
            ms.setCount(entry.getValue().size());

            // 计算权重
            if (totalValue.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal weight = moduleValue
                        .divide(totalValue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                ms.setWeight(weight);
            } else {
                ms.setWeight(BigDecimal.ZERO);
            }

            // 计算模块收益率
            if (moduleCost.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = moduleValue.subtract(moduleCost)
                        .divide(moduleCost, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                ms.setProfitRate(rate);
            } else {
                ms.setProfitRate(BigDecimal.ZERO);
            }

            moduleSummaries.add(ms);
        }

        summary.setModuleSummaries(moduleSummaries);
        return summary;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePrice(Long id, BigDecimal price) {
        Position position = positionRepository.selectById(id);
        if (position == null) {
            throw new BusinessException(404, "持仓不存在: " + id);
        }

        position.setCurrentPrice(price);
        if (position.getShares() != null) {
            position.setCurrentValue(price.multiply(position.getShares()));
        }
        position.setUpdateTime(LocalDateTime.now());

        positionRepository.updateById(position);
        log.info("更新持仓价格: id={}, price={}", id, price);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initPriceHistory() {
        log.info("开始初始化价格历史数据...");

        List<Position> positions = positionRepository.selectList(
                new LambdaQueryWrapper<Position>()
                        .eq(Position::getStatus, "active")
                        .eq(Position::getDeleted, 0)
        );

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        int count = 0;

        for (Position position : positions) {
            if (position.getCurrentPrice() != null) {
                // 检查是否已存在昨日价格记录
                PriceHistory existing = priceHistoryRepository.selectOne(
                        new LambdaQueryWrapper<PriceHistory>()
                                .eq(PriceHistory::getSymbol, position.getSymbol())
                                .eq(PriceHistory::getTradeDate, yesterday)
                );

                if (existing == null) {
                    // 保存昨日价格历史（模拟：当前价格 * 0.98 作为昨日价格）
                    BigDecimal simulatedYesterdayPrice = position.getCurrentPrice().multiply(
                            new BigDecimal("0.98"));

                    PriceHistory history = new PriceHistory();
                    history.setSymbol(position.getSymbol());
                    history.setTradeDate(yesterday);
                    history.setClosePrice(simulatedYesterdayPrice);
                    history.setAdjClosePrice(simulatedYesterdayPrice);
                    history.setCreateTime(LocalDateTime.now());
                    priceHistoryRepository.insert(history);

                    // 更新持仓的昨日收盘价
                    position.setPrevClosePrice(simulatedYesterdayPrice);
                    position.setUpdateTime(LocalDateTime.now());
                    positionRepository.updateById(position);

                    count++;
                }
            }
        }

        log.info("价格历史数据初始化完成: 共{}条记录", count);
    }

    private PositionVO convertToVO(Position position) {
        PositionVO vo = new PositionVO();
        vo.setId(position.getId());
        vo.setSymbol(position.getSymbol());
        vo.setName(position.getName());
        vo.setModule(position.getModule());
        vo.setMarket(position.getMarket());
        vo.setAssetType(position.getAssetType());
        vo.setShares(position.getShares());
        vo.setAvgCost(position.getAvgCost());
        vo.setCurrentPrice(position.getCurrentPrice());
        vo.setCurrentValue(position.getCurrentValue());
        vo.setTargetWeight(position.getTargetWeight());
        vo.setBuyPriceThreshold(position.getBuyPriceThreshold());
        vo.setSellPriceThreshold(position.getSellPriceThreshold());
        vo.setStatus(position.getStatus());
        vo.setRemarks(position.getRemarks());
        vo.setCreateTime(position.getCreateTime());
        vo.setUpdateTime(position.getUpdateTime());

        // 计算成本金额
        if (position.getAvgCost() != null && position.getShares() != null) {
            BigDecimal costValue = position.getAvgCost().multiply(position.getShares());
            vo.setCostValue(costValue);

            // 计算收益金额
            if (position.getCurrentValue() != null) {
                vo.setProfitAmount(position.getCurrentValue().subtract(costValue));

                // 计算收益率
                if (costValue.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal profitRate = position.getCurrentValue().subtract(costValue)
                            .divide(costValue, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    vo.setProfitRate(profitRate);
                }
            }
        }

        // 计算今日盈亏率
        BigDecimal todayProfitRate = calculateTodayProfitRate(position);
        vo.setTodayProfitRate(todayProfitRate);

        return vo;
    }

    /**
     * 计算今日盈亏率
     */
    private BigDecimal calculateTodayProfitRate(Position position) {
        // 优先使用实体中的昨日收盘价
        if (position.getPrevClosePrice() != null && position.getPrevClosePrice().compareTo(BigDecimal.ZERO) > 0
                && position.getCurrentPrice() != null) {
            return position.getCurrentPrice().subtract(position.getPrevClosePrice())
                    .divide(position.getPrevClosePrice(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        // 从价格历史表获取昨日收盘价
        LocalDate yesterday = LocalDate.now().minusDays(1);
        PriceHistory yesterdayPrice = priceHistoryRepository.selectOne(
                new LambdaQueryWrapper<PriceHistory>()
                        .eq(PriceHistory::getSymbol, position.getSymbol())
                        .eq(PriceHistory::getTradeDate, yesterday)
        );

        if (yesterdayPrice != null && yesterdayPrice.getClosePrice() != null
                && yesterdayPrice.getClosePrice().compareTo(BigDecimal.ZERO) > 0
                && position.getCurrentPrice() != null) {
            return position.getCurrentPrice().subtract(yesterdayPrice.getClosePrice())
                    .divide(yesterdayPrice.getClosePrice(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return null;
    }

    private String getModuleName(String module) {
        return switch (module) {
            case "dividend" -> "红利";
            case "fixed" -> "固收";
            case "growth" -> "成长";
            case "allweather" -> "全天候";
            default -> module;
        };
    }

    /**
     * 计算单个持仓的今日盈亏金额
     */
    private BigDecimal calculateTodayProfitAmount(Position position) {
        BigDecimal yesterdayPrice = getYesterdayClosePrice(position);
        if (yesterdayPrice != null && yesterdayPrice.compareTo(BigDecimal.ZERO) > 0
                && position.getCurrentPrice() != null && position.getShares() != null) {
            // 今日盈亏 = (当前价格 - 昨日收盘价) * 持仓数量
            return position.getCurrentPrice().subtract(yesterdayPrice)
                    .multiply(position.getShares());
        }
        return null;
    }

    /**
     * 计算单个持仓的昨日市值
     */
    private BigDecimal calculateYesterdayValue(Position position) {
        BigDecimal yesterdayPrice = getYesterdayClosePrice(position);
        if (yesterdayPrice != null && yesterdayPrice.compareTo(BigDecimal.ZERO) > 0
                && position.getShares() != null) {
            // 昨日市值 = 昨日收盘价 * 持仓数量
            return yesterdayPrice.multiply(position.getShares());
        }
        return null;
    }

    /**
     * 获取昨日收盘价
     */
    private BigDecimal getYesterdayClosePrice(Position position) {
        // 优先使用实体中的昨日收盘价
        if (position.getPrevClosePrice() != null && position.getPrevClosePrice().compareTo(BigDecimal.ZERO) > 0) {
            return position.getPrevClosePrice();
        }

        // 从价格历史表获取昨日收盘价
        LocalDate yesterday = LocalDate.now().minusDays(1);
        PriceHistory yesterdayPrice = priceHistoryRepository.selectOne(
                new LambdaQueryWrapper<PriceHistory>()
                        .eq(PriceHistory::getSymbol, position.getSymbol())
                        .eq(PriceHistory::getTradeDate, yesterday)
        );

        if (yesterdayPrice != null && yesterdayPrice.getClosePrice() != null
                && yesterdayPrice.getClosePrice().compareTo(BigDecimal.ZERO) > 0) {
            return yesterdayPrice.getClosePrice();
        }

        return null;
    }
}
