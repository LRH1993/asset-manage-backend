/**
 * 本地存储Hook
 */

import { useState, useEffect } from 'react';
import { getLocalStorage, setLocalStorage } from '@/utils/storage';

export const useLocalStorage = <T>(
  key: string,
  defaultValue: T
): [T, (value: T) => void] => {
  const [storedValue, setStoredValue] = useState<T>(() => {
    return getLocalStorage<T>(key, defaultValue) || defaultValue;
  });

  useEffect(() => {
    setLocalStorage(key, storedValue);
  }, [key, storedValue]);

  return [storedValue, setStoredValue];
};
