package com.asset.service.impl;

import com.asset.common.constant.ModuleConstants;
import com.asset.domain.entity.Position;
import com.asset.domain.repository.PositionRepository;
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
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final PositionRepository positionRepository;

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
        Double totalProfitRate = totalCost.compareTo(BigDecimal.ZERO) > 0
                ? totalProfit.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;

        // 模拟今日盈亏（实际应从价格历史表计算）
        Double todayProfit = totalValue.multiply(BigDecimal.valueOf(0.0235)).doubleValue();
        Double todayProfitRate = 2.35;

        // 模拟年化收益率（实际应从历史数据计算）
        Double annualReturn = 15.6;
        Double benchmarkComparison = 5.2;

        AssetOverviewVO vo = new AssetOverviewVO();
        vo.setTotalValue(totalValue.doubleValue());
        vo.setTodayProfit(todayProfit);
        vo.setTodayProfitRate(todayProfitRate);
        vo.setTotalProfit(totalProfit.doubleValue());
        vo.setTotalProfitRate(totalProfitRate);
        vo.setAnnualReturn(annualReturn);
        vo.setBenchmarkComparison(benchmarkComparison);

        return vo;
    }

    @Override
    public CoreMetricsVO getCoreMetrics() {
        // 查询最新的风险指标
        // 实际应从risk_metrics表查询
        // 这里先返回模拟数据
        CoreMetricsVO vo = new CoreMetricsVO();
        vo.setMaxDrawdown(-8.5);
        vo.setSharpeRatio(1.85);
        vo.setVolatility30d(12.3);
        vo.setVolatility90d(14.2);
        vo.setConcentrationRisk(0.25);
        vo.setVar95(-3.0);
        vo.setVar99(-5.0);

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
        vo.setDividend(buildModuleInfo(modulePositions.get(ModuleConstants.MODULE_DIVIDEND), totalValue, ModuleConstants.NAME_DIVIDEND));
        vo.setFixed(buildModuleInfo(modulePositions.get(ModuleConstants.MODULE_FIXED), totalValue, ModuleConstants.NAME_FIXED));
        vo.setGrowth(buildModuleInfo(modulePositions.get(ModuleConstants.MODULE_GROWTH), totalValue, ModuleConstants.NAME_GROWTH));
        vo.setAllweather(buildModuleInfo(modulePositions.get(ModuleConstants.MODULE_ALLWEATHER), totalValue, ModuleConstants.NAME_ALLWEATHER));

        return vo;
    }

    @Override
    public List<Object> getReturnCurve(String period) {
        // 模拟生成收益曲线数据
        // 实际应从price_history和transactions表查询历史数据计算
        List<Object> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int days = getDaysByPeriod(period);
        BigDecimal baseValue = BigDecimal.valueOf(1000000);
        BigDecimal dailyReturn = BigDecimal.valueOf(0.002);

        for (int i = days; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            BigDecimal dayMultiplier = BigDecimal.ONE.add(dailyReturn);
            baseValue = baseValue.multiply(dayMultiplier);

            ReturnCurveDataVO data = new ReturnCurveDataVO();
            data.setDate(date.format(formatter));
            data.setValue(baseValue.doubleValue());
            data.setReturnRate(baseValue.subtract(BigDecimal.valueOf(1000000))
                    .divide(BigDecimal.valueOf(1000000), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue());
            data.setBenchmark(baseValue.multiply(BigDecimal.valueOf(0.96)).doubleValue());

            result.add(data);
        }

        return result;
    }

    /**
     * 构建模块信息
     */
    private ModuleInfoVO buildModuleInfo(List<Position> positions, BigDecimal totalValue, String name) {
        ModuleInfoVO info = new ModuleInfoVO();

        if (positions == null || positions.isEmpty()) {
            info.setName(name);
            info.setTargetWeight(ModuleConstants.TARGET_WEIGHT);
            info.setCurrentValue(0.0);
            info.setCurrentWeight(0.0);
            info.setDeviation(-ModuleConstants.TARGET_WEIGHT * 100);
            info.setReturnRate(0.0);
            return info;
        }

        BigDecimal currentValue = positions.stream()
                .map(Position::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentWeight = totalValue.compareTo(BigDecimal.ZERO) > 0
                ? currentValue.divide(totalValue, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal deviation = currentWeight.subtract(BigDecimal.valueOf(ModuleConstants.TARGET_WEIGHT))
                .multiply(BigDecimal.valueOf(100));

        // 计算模块收益（模拟）
        BigDecimal totalCost = positions.stream()
                .map(p -> p.getAvgCost().multiply(p.getShares()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Double returnRate = totalCost.compareTo(BigDecimal.ZERO) > 0
                ? currentValue.subtract(totalCost)
                        .divide(totalCost, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;

        info.setName(name);
        info.setTargetWeight(ModuleConstants.TARGET_WEIGHT);
        info.setCurrentValue(currentValue.doubleValue());
        info.setCurrentWeight(currentWeight.doubleValue());
        info.setDeviation(deviation.doubleValue());
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
            default -> 60;  // all默认60天
        };
    }
}