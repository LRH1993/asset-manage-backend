package com.asset.api.controller;

import com.asset.domain.vo.*;
import com.asset.service.AssetService;
import com.asset.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产管理控制器
 */
@Tag(name = "资产管理", description = "资产管理相关接口")
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AssetController {
    private final AssetService assetService;

    @Operation(summary = "获取资产总览")
    @GetMapping("/overview")
    public Result<AssetOverviewVO> getOverview() {
        return Result.success(assetService.getAssetOverview());
    }

    @Operation(summary = "获取核心指标")
    @GetMapping("/metrics")
    public Result<CoreMetricsVO> getMetrics() {
        return Result.success(assetService.getCoreMetrics());
    }

    @Operation(summary = "获取资产配置（四象限）")
    @GetMapping("/allocation")
    public Result<ModuleAllocationVO> getAllocation() {
        return Result.success(assetService.getAssetAllocation());
    }

    @Operation(summary = "获取收益曲线")
    @GetMapping("/return-curve")
    public Result<List<Object>> getReturnCurve(
            @RequestParam(defaultValue = "all") String period) {
        return Result.success(assetService.getReturnCurve(period));
    }
}