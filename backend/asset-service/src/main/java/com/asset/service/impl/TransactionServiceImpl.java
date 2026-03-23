package com.asset.service.impl;

import com.asset.common.exception.BusinessException;
import com.asset.domain.dto.TransactionQueryRequest;
import com.asset.domain.dto.TransactionRequest;
import com.asset.domain.entity.Position;
import com.asset.domain.entity.Transaction;
import com.asset.domain.repository.PositionRepository;
import com.asset.domain.repository.TransactionRepository;
import com.asset.domain.vo.TransactionSummaryVO;
import com.asset.domain.vo.TransactionVO;
import com.asset.service.TransactionService;
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
 * 交易记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final PositionRepository positionRepository;

    @Override
    public Page<TransactionVO> getPageList(TransactionQueryRequest request) {
        Page<Transaction> page = new Page<>(request.getPageNum(), request.getPageSize());

        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();

        // 交易类型筛选
        if (StringUtils.hasText(request.getTransactionType())) {
            wrapper.eq(Transaction::getTransactionType, request.getTransactionType());
        }

        // 模块筛选
        if (StringUtils.hasText(request.getModule())) {
            wrapper.eq(Transaction::getModule, request.getModule());
        }

        // 市场类型筛选
        if (StringUtils.hasText(request.getMarket())) {
            wrapper.eq(Transaction::getMarket, request.getMarket());
        }

        // 关键词搜索
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.like(Transaction::getSymbol, request.getKeyword());
        }

        // 日期范围筛选
        if (StringUtils.hasText(request.getStartDate())) {
            wrapper.ge(Transaction::getTransactionDate, LocalDate.parse(request.getStartDate()));
        }
        if (StringUtils.hasText(request.getEndDate())) {
            wrapper.le(Transaction::getTransactionDate, LocalDate.parse(request.getEndDate()));
        }

        // 按交易日期降序排序
        wrapper.orderByDesc(Transaction::getTransactionDate);

        Page<Transaction> transactionPage = transactionRepository.selectPage(page, wrapper);

        // 转换为VO
        Page<TransactionVO> voPage = new Page<>(transactionPage.getCurrent(), transactionPage.getSize(), transactionPage.getTotal());
        voPage.setRecords(transactionPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public TransactionVO getById(Long id) {
        Transaction transaction = transactionRepository.selectById(id);
        if (transaction == null) {
            throw new BusinessException(404, "交易记录不存在: " + id);
        }
        return convertToVO(transaction);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setPositionId(request.getPositionId());
        transaction.setSymbol(request.getSymbol());
        transaction.setName(request.getName());
        transaction.setModule(request.getModule());
        transaction.setMarket(request.getMarket());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setShares(request.getShares());
        transaction.setPrice(request.getPrice());
        transaction.setFee(request.getFee() != null ? request.getFee() : BigDecimal.ZERO);
        transaction.setCurrency(request.getCurrency() != null ? request.getCurrency() : "CNY");
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setNotes(request.getNotes());
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setDeleted(0);

        // 计算成交金额
        BigDecimal totalAmount = request.getPrice().multiply(request.getShares());
        transaction.setTotalAmount(totalAmount);

        transactionRepository.insert(transaction);
        log.info("创建交易记录成功: id={}, symbol={}, type={}, module={}, market={}",
                transaction.getId(), transaction.getSymbol(), transaction.getTransactionType(),
                transaction.getModule(), transaction.getMarket());
        return transaction.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, TransactionRequest request) {
        Transaction transaction = transactionRepository.selectById(id);
        if (transaction == null) {
            throw new BusinessException(404, "交易记录不存在: " + id);
        }

        transaction.setPositionId(request.getPositionId());
        transaction.setSymbol(request.getSymbol());
        transaction.setName(request.getName());
        transaction.setModule(request.getModule());
        transaction.setMarket(request.getMarket());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setShares(request.getShares());
        transaction.setPrice(request.getPrice());
        transaction.setFee(request.getFee());
        transaction.setCurrency(request.getCurrency());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setNotes(request.getNotes());

        // 重新计算成交金额
        BigDecimal totalAmount = request.getPrice().multiply(request.getShares());
        transaction.setTotalAmount(totalAmount);

        transactionRepository.updateById(transaction);
        log.info("更新交易记录成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Transaction transaction = transactionRepository.selectById(id);
        if (transaction == null) {
            throw new BusinessException(404, "交易记录不存在: " + id);
        }
        transactionRepository.deleteById(id);
        log.info("删除交易记录成功: id={}", id);
    }

    @Override
    public TransactionSummaryVO getSummary() {
        List<Transaction> transactions = transactionRepository.selectList(
                new LambdaQueryWrapper<Transaction>()
                        .eq(Transaction::getDeleted, 0)
        );

        TransactionSummaryVO summary = new TransactionSummaryVO();

        int totalCount = transactions.size();
        int buyCount = 0;
        int sellCount = 0;
        BigDecimal buyAmount = BigDecimal.ZERO;
        BigDecimal sellAmount = BigDecimal.ZERO;
        BigDecimal totalFee = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if ("buy".equals(t.getTransactionType())) {
                buyCount++;
                if (t.getTotalAmount() != null) {
                    buyAmount = buyAmount.add(t.getTotalAmount());
                }
            } else if ("sell".equals(t.getTransactionType())) {
                sellCount++;
                if (t.getTotalAmount() != null) {
                    sellAmount = sellAmount.add(t.getTotalAmount());
                }
            }
            if (t.getFee() != null) {
                totalFee = totalFee.add(t.getFee());
            }
        }

        summary.setTotalCount(totalCount);
        summary.setBuyCount(buyCount);
        summary.setSellCount(sellCount);
        summary.setBuyAmount(buyAmount);
        summary.setSellAmount(sellAmount);
        summary.setTotalFee(totalFee);

        // 计算已实现收益（简化计算：卖出金额 - 买入成本估算）
        BigDecimal realizedProfit = sellAmount.subtract(sellAmount.divide(BigDecimal.valueOf(1.05), 4, RoundingMode.HALF_UP));
        summary.setRealizedProfit(realizedProfit);

        // 计算已实现收益率
        if (sellAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = realizedProfit.divide(sellAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            summary.setRealizedProfitRate(rate);
        } else {
            summary.setRealizedProfitRate(BigDecimal.ZERO);
        }

        // 按模块统计
        Map<String, List<Transaction>> moduleMap = transactions.stream()
                .filter(t -> t.getPositionId() != null)
                .collect(Collectors.groupingBy(t -> {
                    Position position = positionRepository.selectById(t.getPositionId());
                    return position != null ? position.getModule() : "unknown";
                }));

        List<TransactionSummaryVO.ModuleSummary> moduleSummaries = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : moduleMap.entrySet()) {
            TransactionSummaryVO.ModuleSummary ms = new TransactionSummaryVO.ModuleSummary();
            ms.setModule(entry.getKey());
            ms.setModuleName(getModuleName(entry.getKey()));
            ms.setCount(entry.getValue().size());

            BigDecimal moduleAmount = entry.getValue().stream()
                    .map(Transaction::getTotalAmount)
                    .filter(a -> a != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            ms.setAmount(moduleAmount);

            moduleSummaries.add(ms);
        }

        summary.setModuleSummaries(moduleSummaries);
        return summary;
    }

    private TransactionVO convertToVO(Transaction transaction) {
        TransactionVO vo = new TransactionVO();
        vo.setId(transaction.getId());
        vo.setPositionId(transaction.getPositionId());
        vo.setSymbol(transaction.getSymbol());
        vo.setName(transaction.getName());
        vo.setModule(transaction.getModule());
        vo.setMarket(transaction.getMarket());
        vo.setTransactionType(transaction.getTransactionType());
        vo.setShares(transaction.getShares());
        vo.setPrice(transaction.getPrice());
        vo.setTotalAmount(transaction.getTotalAmount());
        vo.setFee(transaction.getFee());
        vo.setCurrency(transaction.getCurrency());
        vo.setTransactionDate(transaction.getTransactionDate());
        vo.setNotes(transaction.getNotes());
        vo.setCreateTime(transaction.getCreateTime());

        // 如果交易记录没有name，尝试从持仓获取
        if (vo.getName() == null && transaction.getPositionId() != null) {
            Position position = positionRepository.selectById(transaction.getPositionId());
            if (position != null) {
                vo.setName(position.getName());
            }
        }

        // 计算已实现收益（仅卖出交易）
        if ("sell".equals(transaction.getTransactionType()) && transaction.getPositionId() != null) {
            Position position = positionRepository.selectById(transaction.getPositionId());
            if (position != null && position.getAvgCost() != null) {
                // 已实现收益 = (卖出价格 - 平均成本) * 卖出数量
                BigDecimal profit = transaction.getPrice().subtract(position.getAvgCost())
                        .multiply(transaction.getShares());
                vo.setRealizedProfit(profit);
            }
        }

        return vo;
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
}
