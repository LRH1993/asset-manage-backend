package com.asset.service;

import com.asset.domain.vo.AssetOverviewVO;
import com.asset.domain.vo.CoreMetricsVO;
import com.asset.domain.vo.ModuleAllocationVO;

import java.util.List;

/**
 * 资产服务接口
 */
public interface AssetService {

    /**
     * 获取资产总览
     */
    AssetOverviewVO getAssetOverview();

    /**
     * 获取核心指标
     */
    CoreMetricsVO getCoreMetrics();

    /**
     * 获取资产配置（四象限）
     */
    ModuleAllocationVO getAssetAllocation();

    /**
     * 获取收益曲线
     * @param period 周期类型：7d/30d/90d/1y/all
     */
    List<Object> getReturnCurve(String period);
}