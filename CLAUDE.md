# 家庭资产监控管理平台 - 项目开发规范

## AI Agent 行为准则（重要）
作为 AI 辅助编程助手，请遵循以下原则：

### 先计划，后执行
- 对于任何超过 5 行代码的改动，**必须先输出清晰的计划**，等待用户确认后再实施。
- 计划应包括：影响范围、具体步骤、潜在风险。
- 使用 `/plan` 命令触发计划模式（如果已安装 everything-claude-code）。

### 验证闭环
- 代码修改后，必须运行必要的验证命令（如 `pnpm type-check`、`./mvnw test`）确保没有引入错误。
- 如果验证失败，自动修正后再提交。

### 最小权限原则
- 只修改用户明确要求的文件，不要擅自改动无关文件。
- 如果不确定，先询问。
- 绝对不执行破坏性操作（如 `rm -rf`、删除数据库等）。

### 清晰沟通
- 回答时先给出结论，再用列表展开细节。
- 遇到不确定的问题，明确说“我不确定，需要更多信息”。

## 📋 项目概述

**项目名称**: 家庭资产监控管理平台
**目标**: 家庭资产的配置、管理、监控一体化，实现长期年化10%收益率，最大回撤控制在8%以内

### 四象限配置策略
| 模块 | 标识 | 目标占比 | 年化目标 | 风险特征 |
|------|------|----------|----------|----------|
| 🟩 红利 | `dividend` | 25% | 10% | 低波动，稳定分红 |
| 🟦 固收 | `fixed` | 25% | 3% | 回撤<1.5%，稳健 |
| 🟪 成长 | `growth` | 25% | 15%+ | 高波动，高收益 |
| 🟧 全天候 | `allweather` | 25% | 8% | 回撤~10%，平衡 |

---

## 🛠️ 技术栈

### 前端 (frontend/)
- **框架**: React 18+ / TypeScript 5+
- **构建**: Vite 5+
- **UI**: Ant Design Pro + Tailwind CSS
- **状态管理**: Zustand
- **图表**: ECharts
- **HTTP**: Axios

### 后端 (backend/)
- **框架**: Spring Boot 3.x (Java 17+)
- **ORM**: Spring Data JPA + MyBatis Plus
- **数据库**: MySQL 8.0+ / Redis 7.x
- **API文档**: SpringDoc OpenAPI
- **安全**: Spring Security + JWT

---

## 📁 项目结构

```
asset_manage/
├── frontend/                    # 前端项目
│   ├── src/
│   │   ├── api/                # API接口
│   │   ├── components/         # 公共组件
│   │   ├── pages/              # 页面
│   │   ├── stores/             # Zustand状态
│   │   ├── types/              # TypeScript类型
│   │   └── utils/              # 工具函数
│   └── package.json
│
├── backend/                     # 后端项目
│   ├── asset-api/              # API模块
│   ├── asset-service/          # 业务服务
│   ├── asset-domain/           # 领域模型
│   ├── asset-common/           # 公共模块
│   ├── asset-integration/      # 外部集成
│   └── asset-job/              # 定时任务
│
├── ui/                          # UI设计规范
│   ├── CLAUDE.md               # 设计规范文档
│   ├── components/             # 组件设计
│   ├── pages/                  # 页面设计
│   └── assets/                 # 设计资源
│
└── CLAUDE.md                   # 本文件
```

---

## 🎯 开发阶段

### Phase 1: MVP (2-3月)
- 基础数据模型
- 持仓/交易CRUD
- 仪表盘总览
- 行情数据接入

### Phase 2: 核心功能 (3-4月)
- 四象限配置
- 动态平衡
- 投资标的库
- 机会提醒

### Phase 3: 高级功能 (3-4月)
- 风险管理
- 策略回测
- 报表系统
- 通知系统

### Phase 4: 智能化 (4-6月)
- ML预测模型
- 自动化执行
- AI推荐

---

## 📐 开发规范

### Git提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式
refactor: 重构
perf: 性能优化
test: 测试
chore: 构建/工具
```

### 分支策略
- `main`: 生产分支
- `develop`: 开发分支
- `feature/*`: 功能分支
- `hotfix/*`: 紧急修复

### API路径规范
```
GET    /api/positions           # 获取列表
GET    /api/positions/:id       # 获取详情
POST   /api/positions           # 创建
PUT    /api/positions/:id       # 更新
DELETE /api/positions/:id       # 删除
```

---

## 🔌 外部数据源

| 数据类型 | 来源 | 用途 |
|----------|------|------|
| A股行情 | AKShare | 实时/历史行情 |
| 美股行情 | yfinance | 美股数据 |
| 港股行情 | yfinance | 港股数据 |
| 基金净值 | 天天基金API | 基金数据 |

---

## ⚠️ 重要注意事项

1. **金融数据精度**: 使用 `BigDecimal`，避免浮点误差
2. **时间处理**: 后端用 `LocalDateTime`，前端用 `dayjs`
3. **敏感数据**: 禁止提交到代码库，使用环境变量
4. **错误处理**: 统一异常处理，友好错误提示
5. **日志记录**: 关键操作必须记录日志

## 跨端协作约定

- **API 契约**：后端使用 Swagger 生成 OpenAPI 文档，前端根据文档生成 TypeScript 类型。
- **版本管理**：提交时遵循 [Conventional Commits](https://www.conventionalcommits.org/)。

## 代码规范（通用）

### 命名

- 前端组件：`PascalCase.tsx`
- 后端类：`PascalCase.java`
- 文件名、目录名：`kebab-case`

### 提交信息

- 格式：`<type>(<scope>): <subject>`
- 示例：`feat(frontend): 添加用户登录页面`
- type: feat / fix / docs / style / refactor / test / chore

### 禁止事项

- ❌ 不要提交包含敏感信息的文件（`.env`、`application-prod.yml` 等）
- ❌ 不要直接在后端实体类中返回给前端，使用 DTO 转换
- ❌ 不要在前端组件中直接调用后端 API 路径

## 安全注意事项

- **前端**：不要将任何 API 密钥硬编码，使用环境变量 `VITE_*`
- **后端**：所有 API 必须进行身份认证（除公开端点），使用 JWT + Redis 管理会话

## 常见场景的处理流程

### 1. 新增一个功能页面（前后端联调）

1. 后端：创建实体、Repository、Service、Controller，编写 DTO
2. 后端：在 Swagger 测试 API 正确性
3. 前端：根据 Swagger 文档生成 TypeScript 类型
4. 前端：创建页面组件和路由
5. 联调：启动前后端，验证功能

### 2. 修复一个 bug

1. 复现问题，定位到错误日志
2. 分析根本原因（前端/后端/联调）
3. 修改代码
4. 运行相关测试
5. 手动验证修复
6. 提交时在 commit message 中描述原因和修复方式

---

## 📚 子项目规范文档

- [后端开发实践](./backend/CLAUDE.md)
- [前端开发实践](./frontend/CLAUDE.md)
- [UI 设计规范](./ui/CLAUDE.md)

---

## 🎨 UI 设计协作

### 设计先行原则
- 新功能开发前，优先参考 `ui/CLAUDE.md` 中的设计规范
- 前端实现必须遵循 UI 规范中的色彩、字体、组件标准
- 数据可视化需遵循图表配色和尺寸规范

### ⚠️ UI 验证规范（重要）

**每次修改 UI 后，必须执行截图对比验证：**

1. **启动前端**: `cd frontend && npm run dev`
2. **请求 Claude Code 进行截图对比**:
   ```
   请使用 playwright 截图对比 http://localhost:5173/positions 页面与 ui/prototypes/positions.html 设计稿
   ```
3. **验证清单**:
   - 布局结构是否一致
   - 色彩是否符合规范（涨跌色、模块色）
   - 字体大小、间距是否正确
   - 交互状态是否完整

4. **偏差标准**:
   - 偏差 < 5%: 通过
   - 偏差 5%-10%: 需调整
   - 偏差 > 10%: 需重做

> 详细规范请查看 [UI 验证规范](./ui/UI_VERIFICATION.md)

### 色彩规范速查

| 类型 | 颜色 | 用途 |
|------|------|------|
| 上涨/盈利 | `#CF1322` | 红色（A股习惯） |
| 下跌/亏损 | `#3F8600` | 绿色 |
| 红利模块 | `#52C41A` | 稳健收益 |
| 固收模块 | `#1890FF` | 安全信任 |
| 成长模块 | `#722ED1` | 成长潜力 |
| 全天候模块 | `#FA8C16` | 平衡多元 |

> 详细规范请查看 [UI 设计规范](./ui/CLAUDE.md)

---

**最后更新**: 2026-03-22
