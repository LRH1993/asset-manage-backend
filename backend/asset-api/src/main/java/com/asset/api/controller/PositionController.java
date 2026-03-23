package com.asset.api.controller;

import com.asset.domain.dto.PositionQueryRequest;
import com.asset.domain.dto.PositionRequest;
import com.asset.common.result.Result;
import com.asset.domain.vo.PositionSummaryVO;
import com.asset.domain.vo.PositionVO;
import com.asset.service.PositionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 持仓管理控制器
 */
@Tag(name = "持仓管理", description = "持仓管理相关接口")
@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PositionController {

    private final PositionService positionService;

    @Operation(summary = "分页查询持仓列表")
    @GetMapping
    public Result<Page<PositionVO>> getPageList(PositionQueryRequest request) {
        return Result.success(positionService.getPageList(request));
    }

    @Operation(summary = "获取持仓详情")
    @GetMapping("/{id}")
    public Result<PositionVO> getById(
            @Parameter(description = "持仓ID") @PathVariable Long id) {
        return Result.success(positionService.getById(id));
    }

    @Operation(summary = "创建持仓")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody PositionRequest request) {
        return Result.success(positionService.create(request));
    }

    @Operation(summary = "更新持仓")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "持仓ID") @PathVariable Long id,
            @Valid @RequestBody PositionRequest request) {
        positionService.update(id, request);
        return Result.success();
    }

    @Operation(summary = "删除持仓")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "持仓ID") @PathVariable Long id) {
        positionService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取持仓汇总统计")
    @GetMapping("/summary")
    public Result<PositionSummaryVO> getSummary() {
        return Result.success(positionService.getSummary());
    }

    @Operation(summary = "更新持仓价格")
    @PutMapping("/{id}/price")
    public Result<Void> updatePrice(
            @Parameter(description = "持仓ID") @PathVariable Long id,
            @Parameter(description = "最新价格") @RequestParam BigDecimal price) {
        positionService.updatePrice(id, price);
        return Result.success();
    }

    @Operation(summary = "初始化价格历史数据")
    @PostMapping("/init-price-history")
    public Result<Void> initPriceHistory() {
        positionService.initPriceHistory();
        return Result.success();
    }
}
