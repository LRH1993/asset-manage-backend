/**
 * 四象限模块配置
 */

import type { ModuleConfig } from '@/types/common';

export const MODULES: Record<string, ModuleConfig> = {
  dividend: {
    key: 'dividend',
    name: '红利',
    nameEn: 'Dividend',
    targetWeight: 25,
    color: '#22c55e',
    icon: '🟩',
  },
  fixed: {
    key: 'fixed',
    name: '固收',
    nameEn: 'Fixed Income',
    targetWeight: 25,
    color: '#3b82f6',
    icon: '🟦',
  },
  growth: {
    key: 'growth',
    name: '成长',
    nameEn: 'Growth',
    targetWeight: 25,
    color: '#8b5cf6',
    icon: '🟪',
  },
  allweather: {
    key: 'allweather',
    name: '全天候',
    nameEn: 'All Weather',
    targetWeight: 25,
    color: '#f97316',
    icon: '🟧',
  },
};

export const MODULE_LIST = Object.values(MODULES);

export const getModuleConfig = (key: string): ModuleConfig | undefined => {
  return MODULES[key];
};

export const getModuleColor = (key: string): string => {
  return MODULES[key]?.color || '#000';
};

export const getModuleIcon = (key: string): string => {
  return MODULES[key]?.icon || '';
};

export const getModuleName = (key: string): string => {
  return MODULES[key]?.name || '';
};
