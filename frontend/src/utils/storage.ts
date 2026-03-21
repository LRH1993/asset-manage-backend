/**
 * 本地存储工具函数
 */

import { CONFIG } from '@/constants/config';

const { STORAGE_PREFIX } = CONFIG;

/**
 * 设置本地存储
 */
export const setLocalStorage = (key: string, value: any): void => {
  try {
    const serialized = JSON.stringify(value);
    localStorage.setItem(`${STORAGE_PREFIX}${key}`, serialized);
  } catch (error) {
    console.error('Failed to set local storage:', error);
  }
};

/**
 * 获取本地存储
 */
export const getLocalStorage = <T>(key: string, defaultValue?: T): T | null => {
  try {
    const item = localStorage.getItem(`${STORAGE_PREFIX}${key}`);
    if (item) {
      return JSON.parse(item) as T;
    }
    return defaultValue || null;
  } catch (error) {
    console.error('Failed to get local storage:', error);
    return defaultValue || null;
  }
};

/**
 * 删除本地存储
 */
export const removeLocalStorage = (key: string): void => {
  try {
    localStorage.removeItem(`${STORAGE_PREFIX}${key}`);
  } catch (error) {
    console.error('Failed to remove local storage:', error);
  }
};

/**
 * 清空本地存储
 */
export const clearLocalStorage = (): void => {
  try {
    const keys = Object.keys(localStorage);
    keys.forEach((key) => {
      if (key.startsWith(STORAGE_PREFIX)) {
        localStorage.removeItem(key);
      }
    });
  } catch (error) {
    console.error('Failed to clear local storage:', error);
  }
};

/**
 * 设置会话存储
 */
export const setSessionStorage = (key: string, value: any): void => {
  try {
    const serialized = JSON.stringify(value);
    sessionStorage.setItem(`${STORAGE_PREFIX}${key}`, serialized);
  } catch (error) {
    console.error('Failed to set session storage:', error);
  }
};

/**
 * 获取会话存储
 */
export const getSessionStorage = <T>(key: string, defaultValue?: T): T | null => {
  try {
    const item = sessionStorage.getItem(`${STORAGE_PREFIX}${key}`);
    if (item) {
      return JSON.parse(item) as T;
    }
    return defaultValue || null;
  } catch (error) {
    console.error('Failed to get session storage:', error);
    return defaultValue || null;
  }
};

/**
 * 删除会话存储
 */
export const removeSessionStorage = (key: string): void => {
  try {
    sessionStorage.removeItem(`${STORAGE_PREFIX}${key}`);
  } catch (error) {
    console.error('Failed to remove session storage:', error);
  }
};

/**
 * 清空会话存储
 */
export const clearSessionStorage = (): void => {
  try {
    const keys = Object.keys(sessionStorage);
    keys.forEach((key) => {
      if (key.startsWith(STORAGE_PREFIX)) {
        sessionStorage.removeItem(key);
      }
    });
  } catch (error) {
    console.error('Failed to clear session storage:', error);
  }
};
