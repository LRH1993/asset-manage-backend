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

        // 同步更新持仓
        Position position = syncPositionOnCreate(request);

        // 关联持仓ID
        if (position != null) {
            transaction.setPositionId(position.getId());
        }

        transactionRepository.insert(transaction);
        log.info("创建交易记录成功: id={}, symbol={}, type={}, positionId={}",
                transaction.getId(), transaction.getSymbol(), transaction.getTransactionType(),
                transaction.getPositionId());
        return transaction.getId();
    }

    /**
     * 创建交易时同步更新持仓
     */
    private Position syncPositionOnCreate(TransactionRequest request) {
        String transactionType = request.getTransactionType();
        BigDecimal shares = request.getShares();
        BigDecimal price = request.getPrice();

        if ("buy".equals(transactionType)) {
            // 买入：增加或创建持仓
            Position position;
            if (request.getPositionId() != null) {
                // 已有持仓，更新
                position = positionRepository.selectById(request.getPositionId());
                if (position == null) {
                    throw new BusinessException(400, "持仓不存在: " + request.getPositionId());
                }
                // 计算新的平均成本 = (原持仓数量 * 原平均成本 + 买入数量 * 买入价格) / 新持仓数量
                BigDecimal oldTotalCost = position.getShares().multiply(position.getAvgCost());
                BigDecimal buyTotalCost = shares.multiply(price);
                BigDecimal newShares = position.getShares().add(shares);
                BigDecimal newAvgCost = oldTotalCost.add(buyTotalCost).divide(newShares, 4, RoundingMode.HALF_UP);

                position.setShares(newShares);
                position.setAvgCost(newAvgCost);
                position.setUpdateTime(LocalDateTime.now());
                positionRepository.updateById(position);
                log.info("买入更新持仓: positionId={}, 原数量={}, 买入数量={}, 新数量={}, 新成本={}",
                        position.getId(), position.getShares().subtract(shares), shares, newShares, newAvgCost);
            } else {
                // 没有持仓ID，根据symbol查找或创建
                Position existingPosition = positionRepository.selectOne(
                        new LambdaQueryWrapper<Position>()
                                .eq(Position::getSymbol, request.getSymbol())
                                .eq(Position::getStatus, "active")
                                .eq(Position::getDeleted, 0)
                );

                if (existingPosition != null) {
                    // 已存在该标的的持仓，更新
                    BigDecimal oldTotalCost = existingPosition.getShares().multiply(existingPosition.getAvgCost());
                    BigDecimal buyTotalCost = shares.multiply(price);
                    BigDecimal newShares = existingPosition.getShares().add(shares);
                    BigDecimal newAvgCost = oldTotalCost.add(buyTotalCost).divide(newShares, 4, RoundingMode.HALF_UP);

                    existingPosition.setShares(newShares);
                    existingPosition.setAvgCost(newAvgCost);
                    existingPosition.setUpdateTime(LocalDateTime.now());
                    positionRepository.updateById(existingPosition);
                    position = existingPosition;
                    log.info("买入更新已有持仓: positionId={}, symbol={}", position.getId(), request.getSymbol());
                } else {
                    // 检查是否有已删除的持仓记录，可以恢复
                    Position deletedPosition = positionRepository.selectOne(
                            new LambdaQueryWrapper<Position>()
                                    .eq(Position::getSymbol, request.getSymbol())
                                    .eq(Position::getDeleted, 1)
                    );

                    if (deletedPosition != null) {
                        // 恢复已删除的持仓
                        deletedPosition.setShares(shares);
                        deletedPosition.setAvgCost(price);
                        deletedPosition.setName(request.getName() != null ? request.getName() : deletedPosition.getName());
                        deletedPosition.setModule(request.getModule() != null ? request.getModule() : deletedPosition.getModule());
                        deletedPosition.setMarket(request.getMarket() != null ? request.getMarket() : deletedPosition.getMarket());
                        deletedPosition.setCurrentPrice(price);
                        deletedPosition.setCurrentValue(shares.multiply(price));
                        deletedPosition.setStatus("active");
                        deletedPosition.setDeleted(0);
                        deletedPosition.setUpdateTime(LocalDateTime.now());
                        positionRepository.updateById(deletedPosition);
                        position = deletedPosition;
                        log.info("买入恢复已删除持仓: positionId={}, symbol={}, shares={}", position.getId(), request.getSymbol(), shares);
                    } else {
                        // 创建新持仓
                        position = new Position();
                        position.setSymbol(request.getSymbol());
                        position.setName(request.getName());
                        position.setModule(request.getModule() != null ? request.getModule() : "dividend");
                        position.setMarket(request.getMarket() != null ? request.getMarket() : "a_stock");
                        position.setShares(shares);
                        position.setAvgCost(price);
                        position.setCurrentPrice(price);
                        position.setCurrentValue(shares.multiply(price));
                        position.setStatus("active");
                        position.setCreateTime(LocalDateTime.now());
                        position.setUpdateTime(LocalDateTime.now());
                        position.setDeleted(0);
                        positionRepository.insert(position);
                        log.info("买入创建新持仓: positionId={}, symbol={}, shares={}, avgCost={}",
                                position.getId(), request.getSymbol(), shares, price);
                    }
                }
            }
            return position;

        } else if ("sell".equals(transactionType)) {
            // 卖出：减少持仓
            Position position;
            if (request.getPositionId() != null) {
                position = positionRepository.selectById(request.getPositionId());
            } else {
                // 根据symbol查找持仓
                position = positionRepository.selectOne(
                        new LambdaQueryWrapper<Position>()
                                .eq(Position::getSymbol, request.getSymbol())
                                .eq(Position::getStatus, "active")
                                .eq(Position::getDeleted, 0)
                );
            }

            if (position == null) {
                throw new BusinessException(400, "卖出失败：未找到对应持仓");
            }

            BigDecimal currentShares = position.getShares();
            if (currentShares.compareTo(shares) < 0) {
                throw new BusinessException(400, "卖出失败：卖出数量超过持仓数量");
            }

            BigDecimal newShares = currentShares.subtract(shares);
            position.setShares(newShares);
            position.setUpdateTime(LocalDateTime.now());

            if (newShares.compareTo(BigDecimal.ZERO) == 0) {
                // 清仓，标记为已卖出
                position.setStatus("sold");
                log.info("卖出清仓: positionId={}, symbol={}", position.getId(), request.getSymbol());
            } else {
                log.info("卖出减仓: positionId={}, 原数量={}, 卖出数量={}, 剩余={}",
                        position.getId(), currentShares, shares, newShares);
            }

            positionRepository.updateById(position);
            return position;
        }

        return null;
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

        // 同步更新持仓（反向操作）
        syncPositionOnDelete(transaction);

        transactionRepository.deleteById(id);
        log.info("删除交易记录成功: id={}", id);
    }

    /**
     * 删除交易时同步更新持仓（反向操作）
     */
    private void syncPositionOnDelete(Transaction transaction) {
        String transactionType = transaction.getTransactionType();
        BigDecimal shares = transaction.getShares();
        BigDecimal price = transaction.getPrice();

        Position position = null;
        if (transaction.getPositionId() != null) {
            position = positionRepository.selectById(transaction.getPositionId());
        }

        if (position == null) {
            // 尝试根据symbol查找持仓
            position = positionRepository.selectOne(
                    new LambdaQueryWrapper<Position>()
                            .eq(Position::getSymbol, transaction.getSymbol())
                            .eq(Position::getDeleted, 0)
            );
        }

        if (position == null) {
            log.warn("删除交易时未找到对应持仓: symbol={}", transaction.getSymbol());
            return;
        }

        if ("buy".equals(transactionType)) {
            // 删除买入记录：减少持仓数量
            BigDecimal currentShares = position.getShares();
            if (currentShares.compareTo(shares) < 0) {
                log.warn("删除买入记录时持仓数量不足: positionId={}, 持仓数量={}, 交易数量={}",
                        position.getId(), currentShares, shares);
                // 设置为0而不是负数
                position.setShares(BigDecimal.ZERO);
                position.setStatus("sold");
            } else {
                BigDecimal newShares = currentShares.subtract(shares);
                position.setShares(newShares);

                if (newShares.compareTo(BigDecimal.ZERO) == 0) {
                    position.setStatus("sold");
                }

                // 重新计算平均成本（需要查询该持仓的所有买入记录）
                recalculatePositionAvgCost(position);
            }
            position.setUpdateTime(LocalDateTime.now());
            positionRepository.updateById(position);
            log.info("删除买入记录更新持仓: positionId={}, 新数量={}", position.getId(), position.getShares());

        } else if ("sell".equals(transactionType)) {
            // 删除卖出记录：恢复持仓数量
            BigDecimal currentShares = position.getShares();
            BigDecimal newShares = currentShares.add(shares);
            position.setShares(newShares);

            // 如果原来是sold状态，恢复为active
            if ("sold".equals(position.getStatus())) {
                position.setStatus("active");
            }

            position.setUpdateTime(LocalDateTime.now());
            positionRepository.updateById(position);
            log.info("删除卖出记录恢复持仓: positionId={}, 恢复数量={}, 新数量={}",
                    position.getId(), shares, newShares);
        }
    }

    /**
     * 重新计算持仓的平均成本
     */
    private void recalculatePositionAvgCost(Position position) {
        // 查询该持仓关联的所有买入交易
        List<Transaction> buyTransactions = transactionRepository.selectList(
                new LambdaQueryWrapper<Transaction>()
                        .eq(Transaction::getPositionId, position.getId())
                        .eq(Transaction::getTransactionType, "buy")
                        .eq(Transaction::getDeleted, 0)
        );

        if (buyTransactions.isEmpty()) {
            // 没有买入记录，保持原成本
            return;
        }

        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalShares = BigDecimal.ZERO;

        for (Transaction t : buyTransactions) {
            totalCost = totalCost.add(t.getShares().multiply(t.getPrice()));
            totalShares = totalShares.add(t.getShares());
        }

        if (totalShares.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal newAvgCost = totalCost.divide(totalShares, 4, RoundingMode.HALF_UP);
            position.setAvgCost(newAvgCost);
            log.info("重新计算持仓平均成本: positionId={}, avgCost={}", position.getId(), newAvgCost);
        }
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

        // 按模块统计（从持仓获取模块信息）
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
        vo.setTransactionType(transaction.getTransactionType());
        vo.setShares(transaction.getShares());
        vo.setPrice(transaction.getPrice());
        vo.setTotalAmount(transaction.getTotalAmount());
        vo.setFee(transaction.getFee());
        vo.setCurrency(transaction.getCurrency());
        vo.setTransactionDate(transaction.getTransactionDate());
        vo.setNotes(transaction.getNotes());
        vo.setCreateTime(transaction.getCreateTime());

        // 从持仓获取name, module, market信息
        if (transaction.getPositionId() != null) {
            Position position = positionRepository.selectById(transaction.getPositionId());
            if (position != null) {
                vo.setName(position.getName());
                vo.setModule(position.getModule());
                vo.setMarket(position.getMarket());

                // 计算已实现收益（仅卖出交易）
                if ("sell".equals(transaction.getTransactionType()) && position.getAvgCost() != null) {
                    // 已实现收益 = (卖出价格 - 平均成本) * 卖出数量
                    BigDecimal profit = transaction.getPrice().subtract(position.getAvgCost())
                            .multiply(transaction.getShares());
                    vo.setRealizedProfit(profit);
                }
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
