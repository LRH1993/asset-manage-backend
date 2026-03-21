/**
 * UI状态管理
 */

import { create } from 'zustand';

interface UIState {
  // 主题
  theme: 'light' | 'dark';
  setTheme: (theme: 'light' | 'dark') => void;
  toggleTheme: () => void;

  // 侧边栏
  sidebarCollapsed: boolean;
  setSidebarCollapsed: (collapsed: boolean) => void;
  toggleSidebar: () => void;

  // 加载状态
  globalLoading: boolean;
  setGlobalLoading: (loading: boolean) => void;

  // 通知
  notifications: any[];
  addNotification: (notification: any) => void;
  removeNotification: (id: string) => void;
  clearNotifications: () => void;

  // 模态框
  modalVisible: boolean;
  modalContent: any;
  setModal: (visible: boolean, content?: any) => void;

  // 面包屑
  breadcrumbs: Array<{ path: string; title: string }>;
  setBreadcrumbs: (breadcrumbs: Array<{ path: string; title: string }>) => void;
}

export const useUIStore = create<UIState>((set) => ({
  // 主题
  theme: 'light',
  setTheme: (theme) => set({ theme }),
  toggleTheme: () => set((state) => ({ theme: state.theme === 'light' ? 'dark' : 'light' })),

  // 侧边栏
  sidebarCollapsed: false,
  setSidebarCollapsed: (collapsed) => set({ sidebarCollapsed: collapsed }),
  toggleSidebar: () => set((state) => ({ sidebarCollapsed: !state.sidebarCollapsed })),

  // 加载状态
  globalLoading: false,
  setGlobalLoading: (loading) => set({ globalLoading: loading }),

  // 通知
  notifications: [],
  addNotification: (notification) => set((state) => ({
    notifications: [...state.notifications, { ...notification, id: Date.now().toString() }],
  })),
  removeNotification: (id) => set((state) => ({
    notifications: state.notifications.filter((n) => n.id !== id),
  })),
  clearNotifications: () => set({ notifications: [] }),

  // 模态框
  modalVisible: false,
  modalContent: null,
  setModal: (visible, content) => set({ modalVisible: visible, modalContent: content }),

  // 面包屑
  breadcrumbs: [],
  setBreadcrumbs: (breadcrumbs) => set({ breadcrumbs }),
}));
