# 家庭资产监控管理平台 - 后端开发实践

> 🎨 **UI 协作**: 返回数据需配合前端 UI 展示，详见 [UI 设计规范](../ui/CLAUDE.md)

## 🏗️ 项目结构

```
backend/
├── asset-api/                    # API层
│   └── src/main/java/com/asset/
│       ├── controller/           # 控制器
│       ├── dto/                  # 数据传输对象
│       │   ├── request/          # 请求DTO
│       │   └── response/         # 响应DTO
│       └── config/               # 配置类
│
├── asset-service/                # 服务层
│   └── src/main/java/com/asset/
│       ├── service/              # 服务接口
│       └── impl/                 # 服务实现
│
├── asset-domain/                 # 领域层
│   └── src/main/java/com/asset/
│       ├── entity/               # 实体类
│       ├── repository/           # 仓储接口
│       ├── vo/                   # 值对象
│       └── enums/                # 枚举
│
├── asset-common/                 # 公共模块
│   └── src/main/java/com/asset/common/
│       ├── constant/             # 常量
│       ├── exception/            # 异常
│       ├── result/               # 统一响应
│       └── util/                 # 工具类
│
├── asset-integration/            # 集成模块
│   └── src/main/java/com/asset/integration/
│       ├── data/                 # 数据源
│       └── notification/         # 通知
│
└── asset-job/                    # 定时任务
    └── src/main/java/com/asset/job/
        └── task/                 # 任务类
```

---

## 📐 分层架构规范

### Controller层
```java
@Tag(name = "持仓管理", description = "持仓管理接口")
@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @Operation(summary = "获取持仓列表")
    @GetMapping
    public Result<PageResult<PositionVO>> getPositions(PositionQueryRequest request) {
        return Result.success(positionService.getPageList(request));
    }

    @Operation(summary = "创建持仓")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody PositionRequest request) {
        return Result.success(positionService.create(request));
    }
}
```

**规范要点:**
- 只做参数校验和结果封装
- 不包含业务逻辑
- 使用 `@Valid` 校验参数
- 返回统一 `Result` 包装

### Service层
```java
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PositionRequest request) {
        // 1. 参数校验
        validateRequest(request);

        // 2. 业务处理
        Position position = convertToEntity(request);
        position.setStatus(PositionStatus.ACTIVE);

        // 3. 持久化
        positionRepository.save(position);

        log.info("创建持仓成功: symbol={}", position.getSymbol());
        return position.getId();
    }
}
```

**规范要点:**
- 包含所有业务逻辑
- 事务注解必须加 `rollbackFor`
- 关键操作记录日志
- 复杂逻辑抽取私有方法

### Repository层
```java
public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByStatus(PositionStatus status);

    @Query("SELECT p FROM Position p WHERE p.module = :module AND p.status = :status")
    List<Position> findByModuleAndStatus(@Param("module") Module module,
                                          @Param("status") PositionStatus status);
}
```

---

## 💾 实体设计规范

### 基础实体
```java
@Data
@Entity
@Table(name = "positions")
@EntityListeners(AuditingEntityListener.class)
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false, length = 50)
    private String symbol;

    @Column(name = "module", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Module module;

    // 金融数据必须用 BigDecimal
    @Column(name = "shares", precision = 18, scale = 4)
    private BigDecimal shares;

    @Column(name = "current_price", precision = 18, scale = 4)
    private BigDecimal currentPrice;

    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
```

### 枚举定义
```java
public enum Module {
    DIVIDEND("红利", "dividend"),
    FIXED("固收", "fixed"),
    GROWTH("成长", "growth"),
    ALLWEATHER("全天候", "allweather");

    private final String name;
    private final String code;
}
```

---

## 🔢 金融计算规范

### BigDecimal 使用
```java
// ✅ 正确
BigDecimal profit = currentValue.subtract(costValue);
BigDecimal rate = profit.divide(costValue, 4, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100));

// ❌ 错误
double profit = currentValue - costValue;  // 精度丢失
```

### 计算工具类
```java
public class FinancialCalculator {

    private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);

    /**
     * 计算收益率
     */
    public static BigDecimal calculateReturnRate(BigDecimal currentValue,
                                                   BigDecimal costValue) {
        if (costValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentValue.subtract(costValue)
                .divide(costValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 计算最大回撤
     */
    public static BigDecimal calculateMaxDrawdown(List<BigDecimal> values) {
        BigDecimal maxDrawdown = BigDecimal.ZERO;
        BigDecimal peak = values.get(0);

        for (BigDecimal value : values) {
            if (value.compareTo(peak) > 0) {
                peak = value;
            }
            BigDecimal drawdown = peak.subtract(value).divide(peak, 4, RoundingMode.HALF_UP);
            if (drawdown.compareTo(maxDrawdown) > 0) {
                maxDrawdown = drawdown;
            }
        }
        return maxDrawdown.multiply(BigDecimal.valueOf(100));
    }
}
```

---

## ⚠️ 异常处理

### 统一异常类
```java
public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}

public class PositionNotFoundException extends BusinessException {
    public PositionNotFoundException(Long id) {
        super("POSITION_NOT_FOUND", "持仓不存在: " + id);
    }
}
```

### 全局异常处理
```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.fail("VALIDATION_ERROR", message);
    }
}
```

---

## 📤 返回数据规范

### 统一响应格式

```java
public class Result<T> {
    private String code;
    private String message;
    private T data;
}
```

### 数值类型返回规范

**原则**: 后端返回原始数值，由前端进行格式化展示

| 字段类型 | 后端返回示例 | 说明 |
|----------|--------------|------|
| 金额 | `123456.7890` | 不做单位转换，前端处理 |
| 收益率 | `12.34` | 返回百分比数值，前端添加% |
| 涨跌幅 | `-5.67` | 负数表示下跌，前端判断颜色 |

**DTO 示例**:

```java
@Data
public class PositionVO {
    private Long id;
    private String symbol;
    private String name;

    // 金额：返回原始值，前端格式化
    private BigDecimal currentValue;

    // 收益率：百分比数值（如 12.34 表示 12.34%）
    private BigDecimal profitRate;

    // 不需要额外字段，前端自行计算颜色和格式
}
```

### 枚举返回规范

枚举返回 code 字符串，前端根据 code 匹配 UI 色彩：

```java
// 后端返回
{
  "module": "dividend"  // 返回 code
}

// 前端根据 code 查找颜色
// dividend -> #52C41A (红利绿)
// fixed -> #1890FF (固收蓝)
// growth -> #722ED1 (成长紫)
// allweather -> #FA8C16 (全天候橙)
```

---

## 📊 数据库规范

### 表命名
- 使用小写下划线: `position_transactions`
- 关联表: `position_tags`
- 历史表: `price_history`

### 字段规范
- 主键: `id` BIGINT AUTO_INCREMENT
- 时间: `create_time`, `update_time`
- 状态: `status` VARCHAR(20)
- 金额: DECIMAL(18, 4)

### 数据精度与 UI 配合

| 后端类型 | 数据库精度 | 前端显示 | 说明 |
|----------|------------|----------|------|
| 金额 | DECIMAL(18,4) | 2位小数 + 单位 | 前端格式化为万/亿 |
| 收益率 | DECIMAL(10,4) | 2位小数 + % | 前端自动添加符号 |
| 股数 | DECIMAL(18,4) | 千分位 | 前端添加分隔符 |
| 权重 | DECIMAL(5,2) | 2位小数 + % | 百分比存储 |

**示例**:
```
后端返回: 123456.7890 (DECIMAL 18,4)
前端显示: 12.35万

后端返回: 0.1234 (DECIMAL 10,4)
前端显示: +12.34%
```

### 索引规范
```sql
-- 唯一索引
UNIQUE KEY `uk_symbol` (`symbol`)

-- 普通索引
KEY `idx_module` (`module`)
KEY `idx_create_time` (`create_time`)

-- 组合索引
KEY `idx_module_status` (`module`, `status`)
```

---

## 🔄 定时任务

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class MarketDataJob {

    private final MarketDataService marketDataService;

    /**
     * 每日9:30更新行情
     */
    @Scheduled(cron = "0 30 9 * * MON-FRI")
    public void updateMarketData() {
        log.info("开始更新市场行情");
        try {
            marketDataService.updateAllPrices();
            log.info("行情更新完成");
        } catch (Exception e) {
            log.error("行情更新失败", e);
        }
    }
}
```

---

## 📝 日志规范

```java
// ✅ 正确
log.info("创建持仓: symbol={}, shares={}", position.getSymbol(), position.getShares());
log.warn("偏离度超过阈值: module={}, deviation={}", module, deviation);
log.error("执行交易失败: action={}", action, e);

// ❌ 错误
log.info("创建持仓: " + position);  // 字符串拼接
log.debug("处理中...");  // 无上下文
```

---

## 🧪 测试规范

```java
@SpringBootTest
class PositionServiceTest {

    @Autowired
    private PositionService positionService;

    @Test
    @Transactional
    void should_create_position_successfully() {
        // Given
        PositionRequest request = PositionRequest.builder()
                .symbol("600519.SH")
                .module(Module.DIVIDEND)
                .shares(new BigDecimal("100"))
                .build();

        // When
        Long id = positionService.create(request);

        // Then
        assertThat(id).isNotNull();
    }
}
```

---

## 📋 核心数据表

### positions (持仓表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| symbol | VARCHAR(50) | 标的代码 |
| module | VARCHAR(20) | 模块(dividend/fixed/growth/allweather) |
| shares | DECIMAL(18,4) | 持仓数量 |
| avg_cost | DECIMAL(18,4) | 平均成本 |
| current_price | DECIMAL(18,4) | 当前价格 |
| current_value | DECIMAL(18,4) | 当前市值 |

### transactions (交易记录表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| position_id | BIGINT | 持仓ID |
| transaction_type | VARCHAR(10) | 交易类型(buy/sell) |
| shares | DECIMAL(18,4) | 交易数量 |
| price | DECIMAL(18,4) | 成交价格 |
| total_amount | DECIMAL(18,4) | 成交金额 |

---

## 🔗 相关文档

- [项目级规范](../CLAUDE.md)
- [前端开发实践](../frontend/CLAUDE.md)
- [UI 设计规范](../ui/CLAUDE.md) - 数据精度与 UI 配合

---

**最后更新**: 2026-03-22
