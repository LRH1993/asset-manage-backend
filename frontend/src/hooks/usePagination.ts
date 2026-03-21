/**
 * 分页Hook
 */

import { useState } from 'react';

interface PaginationConfig {
  defaultPage?: number;
  defaultPageSize?: number;
  total?: number;
}

export const usePagination = (config: PaginationConfig = {}) => {
  const {
    defaultPage = 1,
    defaultPageSize = 20,
    total = 0,
  } = config;

  const [page, setPage] = useState(defaultPage);
  const [pageSize, setPageSize] = useState(defaultPageSize);

  const totalPages = Math.ceil(total / pageSize);

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  const handlePageSizeChange = (newPageSize: number) => {
    setPageSize(newPageSize);
    setPage(1); // 重置到第一页
  };

  const reset = () => {
    setPage(defaultPage);
    setPageSize(defaultPageSize);
  };

  return {
    page,
    pageSize,
    total,
    totalPages,
    handlePageChange,
    handlePageSizeChange,
    reset,
  };
};
