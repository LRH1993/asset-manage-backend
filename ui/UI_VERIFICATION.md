# UI 实现规范

> 本规范旨在确保前端实现与设计稿高度一致

## 核心原则

### 1. 设计先行，实现验证
- 所有 UI 实现必须基于设计稿（`ui/` 目录下的 `.md` 文件和 `ui/prototypes/` 下的 HTML 原型）
- 实现完成后必须进行截图对比验证

### 2. 截图对比流程

**每次修改 UI 后，必须执行以下步骤：**

```bash
# 1. 启动前端开发服务器
cd frontend && npm run dev

# 2. 使用 Playwright MCP 截图
# 在 Claude Code 中请求截图对比：
# "请使用 playwright 截图对比 http://localhost:5173/positions 页面与 ui/prototypes/positions.html 设计稿"
```

### 3. 验证清单

每次 UI 实现需要验证：

| 检查项 | 说明 |
|--------|------|
| 布局结构 | 是否与设计稿一致 |
| 色彩 | 是否使用规范色彩（涨跌色、模块色等） |
| 字体大小 | 是否符合设计规范 |
| 间距 | padding/margin 是否一致 |
| 交互状态 | hover/active/disabled 状态 |
| 响应式 | 不同屏幕尺寸下的表现 |

## 设计稿位置

- **设计规范**: `ui/CLAUDE.md`
- **组件设计**: `ui/components/`
- **页面设计**: `ui/pages/`
- **HTML 原型**: `ui/prototypes/`

## 色彩规范速查

```typescript
// 涨跌色（A股习惯）
PROFIT_COLORS = {
  up: '#CF1322',      // 红涨
  down: '#3F8600',    // 绿跌
  neutral: '#8C8C8C', // 平盘
}

// 四象限模块色
MODULES = {
  dividend: '#52C41A',   // 红利 - 绿色
  fixed: '#1890FF',      // 固收 - 蓝色
  growth: '#722ED1',     // 成长 - 紫色
  allweather: '#FA8C16', // 全天候 - 橙色
}
```

## 截图对比命令

### 在 Claude Code 中请求对比

```
请使用 playwright 截图对比：
- 实现页面: http://localhost:5173/positions
- 设计稿: ui/prototypes/positions.html
- 输出差异报告
```

### Playwright MCP 可用工具

| 工具 | 说明 |
|------|------|
| `browser_navigate` | 导航到指定 URL |
| `browser_screenshot` | 截取当前页面 |
| `browser_click` | 点击元素 |
| `browser_evaluate` | 执行 JavaScript |

## 自动化验证脚本

在 `frontend/` 目录下创建 `scripts/verify-ui.ts`:

```typescript
import { chromium } from 'playwright';
import { compareImages } from './utils/compare';

const pages = [
  { name: 'dashboard', url: '/', prototype: 'ui/prototypes/dashboard.html' },
  { name: 'positions', url: '/positions', prototype: 'ui/prototypes/positions.html' },
];

async function verifyUI() {
  const browser = await chromium.launch();
  const page = await browser.newPage();

  for (const { name, url, prototype } of pages) {
    await page.goto(`http://localhost:5173${url}`);
    const screenshot = await page.screenshot();

    // 与设计稿对比
    const diff = await compareImages(screenshot, prototype);
    console.log(`${name}: ${diff.percentage}% difference`);
  }

  await browser.close();
}
```

## 惩罚机制

- 偏差超过 10%：需要重做
- 偏差 5%-10%：需要调整
- 偏差小于 5%：通过

---

**最后更新**: 2026-03-22
