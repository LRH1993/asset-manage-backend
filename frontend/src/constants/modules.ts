/**
 * 四象限模块配置
 * 遵循 ui/CLAUDE.md 设计规范
 */

import { MODULE_COLORS } from './colors';
import type { ModuleConfig } from '@/types/common';

/**
 * 四象限模块配置
 * @description 定义四个投资模块的配置信息
 */
export const MODULES: Record<string, ModuleConfig> = {
  dividend: {
    key: 'dividend',
    name: '红利',
    nameEn: 'Dividend',
    targetWeight: 25,
    color: MODULE_COLORS.dividend.primary,      // #52C41A
    lightColor: MODULE_COLORS.dividend.light,   // #F6FFED
    icon: '🟩',
    description: '低波动，稳定分红',
    targetReturn: 10,
  },
  fixed: {
    key: 'fixed',
    name: '固收',
    nameEn: 'Fixed Income',
    targetWeight: 25,
    color: MODULE_COLORS.fixed.primary,         // #1890FF
    lightColor: MODULE_COLORS.fixed.light,   // #E6F7FF
    icon: '🟦',
    description: '回撤<1.5%，稳健',
    targetReturn: 3,
  },
  growth: {
    key: 'growth',
    name: '成长',
    nameEn: 'Growth',
    targetWeight: 25,
    color: MODULE_COLORS.growth.primary,        // #722ED1
    lightColor: MODULE_COLORS.growth.light,     // #F9F0FF
    icon: '🟪',
    description: '高波动，高收益',
    targetReturn: 15,
  },
  allweather: {
    key: 'allweather',
    name: '全天候',
    nameEn: 'All Weather',
    targetWeight: 25,
    color: MODULE_COLORS.allweather.primary,    // #FA8C16
    lightColor: MODULE_COLORS.allweather.light, // #FFF7E6
    icon: '🟧',
    description: '回撤~10%，平衡',
    targetReturn: 8,
  },
};

export const MODULE_LIST = Object.values(MODULES);

/**
 * 获取模块配置
 */
export const getModuleConfig = (key: string): ModuleConfig | undefined => {
  return MODULES[key];
};

/**
 * 获取模块主色
 */
export const getModuleColor = (key: string): string => {
  return MODULES[key]?.color || '#000';
};

/**
 * 获取模块图标
 */
export const getModuleIcon = (key: string): string => {
  return MODULES[key]?.icon || '';
};

/**
 * 获取模块名称
 */
export const getModuleName = (key: string): string => {
  return MODULES[key]?.name || '';
};
