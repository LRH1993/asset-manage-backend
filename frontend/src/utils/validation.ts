/**
 * 验证工具函数
 */

/**
 * 验证是否为空
 */
export const isEmpty = (value: any): boolean => {
  if (value === null || value === undefined) return true;
  if (typeof value === 'string') return value.trim().length === 0;
  if (Array.isArray(value)) return value.length === 0;
  if (typeof value === 'object') return Object.keys(value).length === 0;
  return false;
};

/**
 * 验证是否为数字
 */
export const isNumber = (value: any): boolean => {
  return typeof value === 'number' && !isNaN(value);
};

/**
 * 验证是否为整数
 */
export const isInteger = (value: any): boolean => {
  return isNumber(value) && Number.isInteger(value);
};

/**
 * 验证是否为正数
 */
export const isPositiveNumber = (value: any): boolean => {
  return isNumber(value) && value > 0;
};

/**
 * 验证是否为负数
 */
export const isNegativeNumber = (value: any): boolean => {
  return isNumber(value) && value < 0;
};

/**
 * 验证是否为非负数
 */
export const isNonNegativeNumber = (value: any): boolean => {
  return isNumber(value) && value >= 0;
};

/**
 * 验证是否为百分比（0-100）
 */
export const isPercentage = (value: any): boolean => {
  return isNumber(value) && value >= 0 && value <= 100;
};

/**
 * 验证是否为有效的股票代码
 */
export const isValidStockCode = (code: string): boolean => {
  if (isEmpty(code)) return false;

  // A股代码：6位数字
  if (/^\d{6}$/.test(code)) return true;

  // 美股代码：字母
  if (/^[A-Z]+$/.test(code.toUpperCase())) return true;

  // 港股代码：5位数字或字母
  if (/^\d{5}$/.test(code)) return true;

  return false;
};

/**
 * 验证是否为有效的日期
 */
export const isValidDate = (date: string): boolean => {
  if (isEmpty(date)) return false;

  const parsed = new Date(date);
  return !isNaN(parsed.getTime());
};

/**
 * 验证是否为有效的邮箱
 */
export const isValidEmail = (email: string): boolean => {
  if (isEmpty(email)) return false;

  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email);
};

/**
 * 验证是否为有效的手机号
 */
export const isValidPhone = (phone: string): boolean => {
  if (isEmpty(phone)) return false;

  const regex = /^1[3-9]\d{9}$/;
  return regex.test(phone);
};

/**
 * 验证是否为有效的URL
 */
export const isValidUrl = (url: string): boolean => {
  if (isEmpty(url)) return false;

  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
};

/**
 * 验证数值范围
 */
export const isInRange = (value: number, min: number, max: number): boolean => {
  if (!isNumber(value)) return false;
  return value >= min && value <= max;
};

/**
 * 验证密码强度
 */
export const getPasswordStrength = (password: string): {
  score: number;
  level: 'weak' | 'medium' | 'strong';
  suggestions: string[];
} => {
  const suggestions: string[] = [];
  let score = 0;

  if (isEmpty(password)) {
    return { score: 0, level: 'weak', suggestions: ['请输入密码'] };
  }

  // 长度检查
  if (password.length >= 8) {
    score += 1;
  } else {
    suggestions.push('密码长度至少8位');
  }

  if (password.length >= 12) {
    score += 1;
  }

  // 包含大写字母
  if (/[A-Z]/.test(password)) {
    score += 1;
  } else {
    suggestions.push('包含至少一个大写字母');
  }

  // 包含小写字母
  if (/[a-z]/.test(password)) {
    score += 1;
  } else {
    suggestions.push('包含至少一个小写字母');
  }

  // 包含数字
  if (/\d/.test(password)) {
    score += 1;
  } else {
    suggestions.push('包含至少一个数字');
  }

  // 包含特殊字符
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
    score += 1;
  } else {
    suggestions.push('包含至少一个特殊字符');
  }

  // 确定强度等级
  let level: 'weak' | 'medium' | 'strong';
  if (score <= 2) {
    level = 'weak';
  } else if (score <= 4) {
    level = 'medium';
  } else {
    level = 'strong';
    suggestions.length = 0;
  }

  return { score, level, suggestions };
};

/**
 * 验证用户名
 */
export const isValidUsername = (username: string): boolean => {
  if (isEmpty(username)) return false;
  if (username.length < 3 || username.length > 20) return false;

  // 只允许字母、数字、下划线、连字符
  const regex = /^[a-zA-Z0-9_-]+$/;
  return regex.test(username);
};

/**
 * 验证金额
 */
export const isValidAmount = (amount: number): boolean => {
  return isNonNegativeNumber(amount) && amount <= Number.MAX_SAFE_INTEGER;
};

/**
 * 验证股票数量
 */
export const isValidShares = (shares: number): boolean => {
  return isPositiveNumber(shares) && isInteger(shares);
};

/**
 * 验证价格
 */
export const isValidPrice = (price: number): boolean => {
  return isPositiveNumber(price);
};
