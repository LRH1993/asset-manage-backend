package com.asset.api.controller;

import com.asset.common.result.Result;
import com.asset.domain.dto.TransactionQueryRequest;
import com.asset.domain.dto.TransactionRequest;
import com.asset.domain.vo.TransactionSummaryVO;
import com.asset.domain.vo.TransactionVO;
import com.asset.service.TransactionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 交易记录控制器
 */
@Tag(name = "交易记录", description = "交易记录相关接口")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "分页查询交易记录列表")
    @GetMapping
    public Result<Page<TransactionVO>> getPageList(TransactionQueryRequest request) {
        return Result.success(transactionService.getPageList(request));
    }

    @Operation(summary = "获取交易详情")
    @GetMapping("/{id}")
    public Result<TransactionVO> getById(
            @Parameter(description = "交易ID") @PathVariable Long id) {
        return Result.success(transactionService.getById(id));
    }

    @Operation(summary = "创建交易记录")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody TransactionRequest request) {
        return Result.success(transactionService.create(request));
    }

    @Operation(summary = "更新交易记录")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "交易ID") @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        transactionService.update(id, request);
        return Result.success();
    }

    @Operation(summary = "删除交易记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "交易ID") @PathVariable Long id) {
        transactionService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取交易汇总统计")
    @GetMapping("/summary")
    public Result<TransactionSummaryVO> getSummary() {
        return Result.success(transactionService.getSummary());
    }
}
