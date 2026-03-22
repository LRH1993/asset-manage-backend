/**
 * 色彩常量
 * 遵循 ui/CLAUDE.md 设计规范
 */

/**
 * 涨跌色（A股习惯：红涨绿跌）
 * @description 用于盈亏、涨跌幅等金融数据的颜色显示
 * 使用扁平化设计，颜色更柔和
 */
export const PROFIT_COLORS = {
  /** 上涨/盈利 - 柔和红色 */
  up: '#E85D5D',
  /** 下跌/亏损 - 柔和绿色 */
  down: '#4CAF50',
  /** 平盘/无变化 - 灰色 */
  neutral: '#9E9E9E',
} as const;

/**
 * 四象限模块色
 * @description 用于资产配置模块的颜色标识
 */
export const MODULE_COLORS = {
  /** 红利模块 - 稳健收益 */
  dividend: {
    primary: '#52C41A',
    light: '#F6FFED',
  },
  /** 固收模块 - 安全信任 */
  fixed: {
    primary: '#1890FF',
    light: '#E6F7FF',
  },
  /** 成长模块 - 成长潜力 */
  growth: {
    primary: '#722ED1',
    light: '#F9F0FF',
  },
  /** 全天候模块 - 平衡多元 */
  allweather: {
    primary: '#FA8C16',
    light: '#FFF7E6',
  },
} as const;

/**
 * 功能色
 * @description 用于按钮、提示、状态等UI元素
 */
export const FUNCTIONAL_COLORS = {
  /** 主色 - 品牌蓝 */
  primary: '#1890FF',
  /** 主色悬停 */
  primaryHover: '#0050B3',
  /** 主色浅色背景 */
  primaryLight: '#E6F7FF',
  /** 成功 */
  success: '#52C41A',
  /** 成功浅色背景 */
  successLight: '#F6FFED',
  /** 警告 */
  warning: '#FAAD14',
  /** 警告浅色背景 */
  warningLight: '#FFFBE6',
  /** 错误 */
  error: '#FF4D4F',
  /** 错误浅色背景 */
  errorLight: '#FFF2F0',
  /** 信息 */
  info: '#1890FF',
  /** 信息浅色背景 */
  infoLight: '#E6F7FF',
} as const;

/**
 * 中性色
 * @description 用于文字、边框、背景等
 */
export const NEUTRAL_COLORS = {
  /** 一级标题 */
  textPrimary: '#262626',
  /** 正文内容 */
  textSecondary: '#595959',
  /** 次要文字、标签 */
  textTertiary: '#8C8C8C',
  /** 占位符、禁用 */
  textDisabled: '#BFBFBF',
  /** 边框、分割线 */
  border: '#D9D9D9',
  /** 页面背景 */
  background: '#F5F5F5',
  /** 卡片背景 */
  cardBackground: '#FFFFFF',
} as const;

/**
 * 统一色彩对象
 * @description 方便统一导入使用
 */
export const COLORS = {
  profit: PROFIT_COLORS,
  module: MODULE_COLORS,
  functional: FUNCTIONAL_COLORS,
  neutral: NEUTRAL_COLORS,
} as const;

/**
 * 模块键名类型
 */
export type ModuleKey = keyof typeof MODULE_COLORS;

/**
 * 获取盈亏颜色
 * @param value 数值
 * @returns 对应的颜色值
 */
export const getProfitColor = (value: number): string => {
  if (value > 0) return PROFIT_COLORS.up;
  if (value < 0) return PROFIT_COLORS.down;
  return PROFIT_COLORS.neutral;
};

/**
 * 获取模块主色
 * @param module 模块键名
 * @returns 主色值
 */
export const getModulePrimaryColor = (module: ModuleKey): string => {
  return MODULE_COLORS[module]?.primary || NEUTRAL_COLORS.textTertiary;
};

/**
 * 获取模块浅色
 * @param module 模块键名
 * @returns 浅色值
 */
export const getModuleLightColor = (module: ModuleKey): string => {
  return MODULE_COLORS[module]?.light || NEUTRAL_COLORS.background;
};

export default COLORS;
