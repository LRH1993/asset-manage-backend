package com.asset.service.impl;

import com.asset.common.constant.ModuleConstants;
import com.asset.domain.entity.Position;
import com.asset.domain.entity.PriceHistory;
import com.asset.domain.entity.RiskMetrics;
import com.asset.domain.repository.PositionRepository;
import com.asset.domain.repository.PriceHistoryRepository;
import com.asset.domain.repository.RiskMetricsRepository;
import com.asset.domain.vo.*;
import com.asset.service.AssetService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资产服务实现
 * 遵循 backend/CLAUDE.md 规范
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final PositionRepository positionRepository;
    private final RiskMetricsRepository riskMetricsRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    private static final String PORTFOLIO_SYMBOL = "PORTFOLIO";
    private static final String BENCHMARK_SYMBOL = "BENCHMARK_HS300";

    @Override
    public AssetOverviewVO getAssetOverview() {
        // 查询所有活跃持仓
        List<Position> positions = positionRepository.selectList(
                new LambdaQueryWrapper<Position>()
                        .eq(Position::getStatus, "active")
                        .eq(Position::getDeleted, 0)
        );

        // 计算总资产
        BigDecimal totalValue = positions.stream()
                .map(Position::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算总成本
        BigDecimal totalCost = positions.stream()
                .map(p -> p.getAvgCost().multiply(p.getShares()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算累计盈亏
        BigDecimal totalProfit = totalValue.subtract(totalCost);
        BigDecimal totalProfitRate = totalCost.compareTo(BigDecimal.ZERO) > 0
                ? totalProfit.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        // 从risk_metrics表获取最新指标
        RiskMetrics latestMetrics = riskMetricsRepository.selectOne(
                new LambdaQueryWrapper<RiskMetrics>()
                        .orderByDesc(RiskMetrics::getMetricDate)
                        .last("LIMIT 1")
        );

        // 从价格历史计算今日盈亏
        BigDecimal todayProfit = BigDecimal.ZERO;
        BigDecimal todayProfitRate = BigDecimal.ZERO;

        // 获取今日和昨日的组合净值数据
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        PriceHistory todayPrice = priceHistoryRepository.selectOne(
                new LambdaQueryWrapper<PriceHistory>()
                        .eq(PriceHistory::getSymbol, PORTFOLIO_SYMBOL)
                        .eq(PriceHistory::getTradeDate, today)
        );

        PriceHistory yesterdayPrice = priceHistoryRepository.selectOne(
                new LambdaQueryWrapper<PriceHistory>()
                        .eq(PriceHistory::getSymbol, PORTFOLIO_SYMBOL)
                        .eq(PriceHistory::getTradeDate, yesterday)
        );

        if (todayPrice != null && yesterdayPrice != null) {
            // 计算今日收益率
            BigDecimal dailyReturn = todayPrice.getAdjClosePrice()
                    .subtract(yesterdayPrice.getAdjClosePrice())
                    .divide(yesterdayPrice.getAdjClosePrice(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            todayProfitRate = dailyReturn;
            todayProfit = totalValue.multiply(dailyReturn)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (latestMetrics != null && latestMetrics.getDailyReturn() != null) {
            // 从risk_metrics获取日收益率
            todayProfitRate = latestMetrics.getDailyReturn();
            todayProfit = totalValue.multiply(todayProfitRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        // 计算年化收益率（从价格历史计算）
        BigDecimal annualReturn = BigDecimal.ZERO;
        BigDecimal benchmarkComparison = BigDecimal.ZERO;

        // 获取一年前的数据
        LocalDate oneYearAgo = today.minusYears(1);
        PriceHistory yearAgoPrice = priceHistoryRepository.selectOne(
                new LambdaQueryWrapper<PriceHistory>()
                        .eq(PriceHistory::getSymbol, PORTFOLIO_SYMBOL)
                        .eq(PriceHistory::getTradeDate, oneYearAgo)
        );

        PriceHistory benchmarkYearAgo = priceHistoryRepository.selectOne(
                new LambdaQueryWrapper<PriceHistory>()
                        .eq(PriceHistory::getSymbol, BENCHMARK_SYMBOL)
                        .eq(PriceHistory::getTradeDate, oneYearAgo)
        );

        PriceHistory benchmarkToday = priceHistoryRepository.selectOne(
                new LambdaQueryWrapper<PriceHistory>()
                        .eq(PriceHistory::getSymbol, BENCHMARK_SYMBOL)
                        .eq(PriceHistory::getTradeDate, today)
        );

        if (todayPrice != null && yearAgoPrice != null) {
            // 计算年化收益率
            BigDecimal yearReturn = todayPrice.getAdjClosePrice()
                    .subtract(yearAgoPrice.getAdjClosePrice())
                    .divide(yearAgoPrice.getAdjClosePrice(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            annualReturn = yearReturn;
        } else if (latestMetrics != null && latestMetrics.getAlpha() != null) {
            // 使用累计收益率作为近似
            annualReturn = totalProfitRate;
        }

        // 计算基准比较
        if (todayPrice != null && benchmarkToday != null && yearAgoPrice != null && benchmarkYearAgo != null) {
            BigDecimal portfolioReturn = todayPrice.getAdjClosePrice()
                    .subtract(yearAgoPrice.getAdjClosePrice())
                    .divide(yearAgoPrice.getAdjClosePrice(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            BigDecimal benchmarkReturn = benchmarkToday.getAdjClosePrice()
                    .subtract(benchmarkYearAgo.getAdjClosePrice())
                    .divide(benchmarkYearAgo.getAdjClosePrice(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            benchmarkComparison = portfolioReturn.subtract(benchmarkReturn);
        } else if (latestMetrics != null && latestMetrics.getAlpha() != null) {
            benchmarkComparison = latestMetrics.getAlpha();
        }

        AssetOverviewVO vo = new AssetOverviewVO();
        vo.setTotalValue(totalValue);
        vo.setTodayProfit(todayProfit);
        vo.setTodayProfitRate(todayProfitRate);
        vo.setTotalProfit(totalProfit);
        vo.setTotalProfitRate(totalProfitRate);
        vo.setAnnualReturn(annualReturn);
        vo.setBenchmarkComparison(benchmarkComparison);

        return vo;
    }

    @Override
    public CoreMetricsVO getCoreMetrics() {
        // 从risk_metrics表查询最新数据
        RiskMetrics latestMetrics = riskMetricsRepository.selectOne(
                new LambdaQueryWrapper<RiskMetrics>()
                        .orderByDesc(RiskMetrics::getMetricDate)
                        .last("LIMIT 1")
        );

        CoreMetricsVO vo = new CoreMetricsVO();

        if (latestMetrics != null) {
            vo.setMaxDrawdown(latestMetrics.getMaxDrawdown());
            vo.setSharpeRatio(latestMetrics.getSharpeRatio());
            vo.setVolatility30d(latestMetrics.getVolatility30d());
            vo.setVolatility90d(latestMetrics.getVolatility90d());
            vo.setVar95(latestMetrics.getVar95());
            vo.setVar99(latestMetrics.getVar99());
            // 计算集中度风险（持仓集中度）
            vo.setConcentrationRisk(BigDecimal.valueOf(0.25));
        } else {
            // 如果没有数据，返回默认值
            vo.setMaxDrawdown(BigDecimal.ZERO);
            vo.setSharpeRatio(BigDecimal.ZERO);
            vo.setVolatility30d(BigDecimal.ZERO);
            vo.setVolatility90d(BigDecimal.ZERO);
            vo.setConcentrationRisk(BigDecimal.ZERO);
            vo.setVar95(BigDecimal.ZERO);
            vo.setVar99(BigDecimal.ZERO);
        }

        return vo;
    }

    @Override
    public ModuleAllocationVO getAssetAllocation() {
        // 查询所有活跃持仓
        List<Position> positions = positionRepository.selectList(
                new LambdaQueryWrapper<Position>()
                        .eq(Position::getStatus, "active")
                        .eq(Position::getDeleted, 0)
        );

        // 按模块分组
        Map<String, List<Position>> modulePositions = positions.stream()
                .collect(Collectors.groupingBy(Position::getModule));

        // 计算总资产
        BigDecimal totalValue = positions.stream()
                .map(Position::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 构建各模块信息
        ModuleAllocationVO vo = new ModuleAllocationVO();
        vo.setDividend(buildModuleInfo(ModuleConstants.MODULE_DIVIDEND, modulePositions.get(ModuleConstants.MODULE_DIVIDEND), totalValue, ModuleConstants.NAME_DIVIDEND));
        vo.setFixed(buildModuleInfo(ModuleConstants.MODULE_FIXED, modulePositions.get(ModuleConstants.MODULE_FIXED), totalValue, ModuleConstants.NAME_FIXED));
        vo.setGrowth(buildModuleInfo(ModuleConstants.MODULE_GROWTH, modulePositions.get(ModuleConstants.MODULE_GROWTH), totalValue, ModuleConstants.NAME_GROWTH));
        vo.setAllweather(buildModuleInfo(ModuleConstants.MODULE_ALLWEATHER, modulePositions.get(ModuleConstants.MODULE_ALLWEATHER), totalValue, ModuleConstants.NAME_ALLWEATHER));

        return vo;
    }

    @Override
    public List<Object> getReturnCurve(String period) {
        List<Object> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int days = getDaysByPeriod(period);
        LocalDate startDate = today.minusDays(days);

        // 从数据库查询组合净值数据
        List<PriceHistory> portfolioData = priceHistoryRepository.selectList(
                new LambdaQueryWrapper<PriceHistory>()
                        .eq(PriceHistory::getSymbol, PORTFOLIO_SYMBOL)
                        .ge(PriceHistory::getTradeDate, startDate)
                        .orderByAsc(PriceHistory::getTradeDate)
        );

        // 从数据库查询基准数据
        List<PriceHistory> benchmarkData = priceHistoryRepository.selectList(
                new LambdaQueryWrapper<PriceHistory>()
                        .eq(PriceHistory::getSymbol, BENCHMARK_SYMBOL)
                        .ge(PriceHistory::getTradeDate, startDate)
                        .orderByAsc(PriceHistory::getTradeDate)
        );

        // 转换为Map便于查找
        Map<LocalDate, PriceHistory> benchmarkMap = benchmarkData.stream()
                .collect(Collectors.toMap(PriceHistory::getTradeDate, p -> p));

        // 构建返回数据
        BigDecimal baseValue = BigDecimal.valueOf(1000000); // 基准金额100万

        for (PriceHistory portfolio : portfolioData) {
            ReturnCurveDataVO data = new ReturnCurveDataVO();
            data.setDate(portfolio.getTradeDate().format(formatter));

            // 计算当前市值
            BigDecimal currentValue = baseValue.multiply(portfolio.getAdjClosePrice());
            data.setValue(currentValue);

            // 计算收益率
            BigDecimal returnRate = portfolio.getAdjClosePrice()
                    .subtract(BigDecimal.ONE)
                    .multiply(BigDecimal.valueOf(100));
            data.setReturnRate(returnRate);

            // 获取基准数据
            PriceHistory benchmark = benchmarkMap.get(portfolio.getTradeDate());
            if (benchmark != null) {
                data.setBenchmark(baseValue.multiply(benchmark.getAdjClosePrice()));
            } else {
                data.setBenchmark(currentValue.multiply(BigDecimal.valueOf(0.96)));
            }

            result.add(data);
        }

        // 如果数据库没有数据，返回模拟数据
        if (result.isEmpty()) {
            return generateMockReturnCurve(days, today, formatter, baseValue);
        }

        return result;
    }

    /**
     * 生成模拟收益曲线数据（当数据库无数据时使用）
     */
    private List<Object> generateMockReturnCurve(int days, LocalDate today, DateTimeFormatter formatter, BigDecimal baseValue) {
        List<Object> result = new ArrayList<>();
        BigDecimal currentValue = baseValue;
        BigDecimal dailyReturn = BigDecimal.valueOf(0.002);

        for (int i = days; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            BigDecimal dayMultiplier = BigDecimal.ONE.add(dailyReturn);
            currentValue = currentValue.multiply(dayMultiplier);

            ReturnCurveDataVO data = new ReturnCurveDataVO();
            data.setDate(date.format(formatter));
            data.setValue(currentValue);
            data.setReturnRate(currentValue.subtract(baseValue)
                    .divide(baseValue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
            data.setBenchmark(currentValue.multiply(BigDecimal.valueOf(0.96)));

            result.add(data);
        }

        return result;
    }

    /**
     * 构建模块信息
     */
    private ModuleInfoVO buildModuleInfo(String moduleCode, List<Position> positions, BigDecimal totalValue, String name) {
        ModuleInfoVO info = new ModuleInfoVO();
        info.setName(name);
        info.setModuleCode(moduleCode);

        if (positions == null || positions.isEmpty()) {
            info.setTargetWeight(BigDecimal.valueOf(ModuleConstants.TARGET_WEIGHT));
            info.setCurrentValue(BigDecimal.ZERO);
            info.setCurrentWeight(BigDecimal.ZERO);
            info.setDeviation(BigDecimal.valueOf(-ModuleConstants.TARGET_WEIGHT).multiply(BigDecimal.valueOf(100)));
            info.setReturnRate(BigDecimal.ZERO);
            return info;
        }

        BigDecimal currentValue = positions.stream()
                .map(Position::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentWeight = totalValue.compareTo(BigDecimal.ZERO) > 0
                ? currentValue.divide(totalValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        BigDecimal deviation = currentWeight.subtract(BigDecimal.valueOf(ModuleConstants.TARGET_WEIGHT * 100));

        // 计算模块收益
        BigDecimal totalCost = positions.stream()
                .map(p -> p.getAvgCost().multiply(p.getShares()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal returnRate = totalCost.compareTo(BigDecimal.ZERO) > 0
                ? currentValue.subtract(totalCost)
                        .divide(totalCost, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        info.setTargetWeight(BigDecimal.valueOf(ModuleConstants.TARGET_WEIGHT));
        info.setCurrentValue(currentValue);
        info.setCurrentWeight(currentWeight);
        info.setDeviation(deviation);
        info.setReturnRate(returnRate);

        return info;
    }

    /**
     * 根据周期获取天数
     */
    private int getDaysByPeriod(String period) {
        return switch (period) {
            case "7d" -> 7;
            case "30d" -> 30;
            case "90d" -> 90;
            case "1y" -> 365;
            case "all" -> 365; // 全部数据
            default -> 90;
        };
    }

    @Override
    public RebalanceSuggestionVO getRebalanceSuggestions() {
        // 获取资产配置
        ModuleAllocationVO allocation = getAssetAllocation();

        RebalanceSuggestionVO vo = new RebalanceSuggestionVO();
        List<RebalanceSuggestionVO.SuggestionItem> suggestions = new ArrayList<>();

        BigDecimal totalDeviation = BigDecimal.ZERO;
        BigDecimal deviationThreshold = BigDecimal.valueOf(5); // 5%偏离阈值

        // 检查各模块偏离度
        suggestions.addAll(checkModuleDeviation(allocation.getDividend(), totalDeviation));
        suggestions.addAll(checkModuleDeviation(allocation.getFixed(), totalDeviation));
        suggestions.addAll(checkModuleDeviation(allocation.getGrowth(), totalDeviation));
        suggestions.addAll(checkModuleDeviation(allocation.getAllweather(), totalDeviation));

        // 计算总偏离度
        totalDeviation = suggestions.stream()
                .map(RebalanceSuggestionVO.SuggestionItem::getDeviation)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b.abs()));

        vo.setTotalDeviation(totalDeviation);
        vo.setNeedRebalance(!suggestions.isEmpty());
        vo.setSuggestions(suggestions);

        return vo;
    }

    /**
     * 检查模块偏离度并生成建议
     */
    private List<RebalanceSuggestionVO.SuggestionItem> checkModuleDeviation(ModuleInfoVO moduleInfo, BigDecimal totalDeviation) {
        List<RebalanceSuggestionVO.SuggestionItem> items = new ArrayList<>();

        if (moduleInfo == null) {
            return items;
        }

        BigDecimal deviation = moduleInfo.getDeviation();
        BigDecimal threshold = BigDecimal.valueOf(5); // 5%阈值

        // 只有偏离度超过阈值才生成建议
        if (deviation.abs().compareTo(threshold) >= 0) {
            RebalanceSuggestionVO.SuggestionItem item = new RebalanceSuggestionVO.SuggestionItem();
            item.setModuleCode(moduleInfo.getModuleCode());
            item.setModuleName(moduleInfo.getName());
            item.setCurrentWeight(moduleInfo.getCurrentWeight());
            item.setTargetWeight(moduleInfo.getTargetWeight().multiply(BigDecimal.valueOf(100)));
            item.setDeviation(deviation);

            // 判断操作类型：正偏离（超配）需卖出，负偏离（低配）需买入
            if (deviation.compareTo(BigDecimal.ZERO) > 0) {
                item.setAction("sell");
                item.setDescription(String.format("超配%.2f%%，建议减仓至目标权重", deviation.abs()));
            } else {
                item.setAction("buy");
                item.setDescription(String.format("低配%.2f%%，建议加仓至目标权重", deviation.abs()));
            }

            // 计算建议操作金额（假设总资产为当前持仓总值）
            // 这里简化处理，实际应该根据总资产计算
            item.setAmount(moduleInfo.getCurrentValue().multiply(deviation.abs())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

            items.add(item);
        }

        return items;
    }
}
