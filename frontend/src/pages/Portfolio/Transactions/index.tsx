/**
 * 交易记录页面
 * 严格遵循 ui/prototypes/transactions.html 设计规范
 */

import { useEffect, useState } from 'react';
import {
  Table, Button, Modal, Form, Input, InputNumber,
  message, Select, DatePicker, Empty, Skeleton, Radio
} from 'antd';
import {
  PlusOutlined,
  UploadOutlined, DownloadOutlined,
  ArrowUpOutlined, ArrowDownOutlined
} from '@ant-design/icons';
import { getTransactions, getTransactionSummary, createTransaction, deleteTransaction } from '@/api/transaction';
import type { Transaction, TransactionQuery, TransactionRequest, TransactionSummary } from '@/types/transaction';
import type { ColumnsType } from 'antd/es/table';
import { PROFIT_COLORS } from '@/constants/colors';
import { formatMoney, getProfitColor, formatShares } from '@/utils/format';
import { PageHeader } from '@/components/common';
import dayjs from 'dayjs';

// 模块配置 - 符合 UI 设计规范
const MODULE_CONFIG: Record<string, { name: string; emoji: string; color: string; bgColor: string; borderColor: string }> = {
  dividend: { name: '红利', emoji: '🟩', color: '#52C41A', bgColor: '#F6FFED', borderColor: 'rgba(82, 196, 26, 0.3)' },
  fixed: { name: '固收', emoji: '🟦', color: '#1890FF', bgColor: '#E6F7FF', borderColor: 'rgba(24, 144, 255, 0.3)' },
  growth: { name: '成长', emoji: '🟪', color: '#722ED1', bgColor: '#F9F0FF', borderColor: 'rgba(114, 46, 209, 0.3)' },
  allweather: { name: '全天候', emoji: '🟧', color: '#FA8C16', bgColor: '#FFF7E6', borderColor: 'rgba(250, 140, 22, 0.3)' },
};

// 市场类型配置
const MARKET_TYPE_CONFIG: Record<string, { name: string; emoji: string }> = {
  a_stock: { name: 'A股', emoji: '📈' },
  etf: { name: '场内ETF', emoji: '📊' },
  fund: { name: '场外基金', emoji: '🏦' },
  hk_stock: { name: '港股', emoji: '🌏' },
  us_stock: { name: '美股', emoji: '🇺🇸' },
};

// 汇总卡片组件
interface SummaryCardProps {
  icon: string;
  title: string;
  value: string | number;
  change?: string;
  isUp?: boolean | null;
  valueColor?: string;
}

const SummaryCard: React.FC<SummaryCardProps> = ({ icon, title, value, change, isUp, valueColor }) => (
  <div style={{
    background: '#fff',
    borderRadius: 12,
    padding: 20,
    border: '1px solid #F0F0F0',
  }}>
    <div style={{ fontSize: 12, color: '#8C8C8C', marginBottom: 8, display: 'flex', alignItems: 'center', gap: 6 }}>
      <span>{icon}</span>
      <span>{title}</span>
    </div>
    <div style={{
      fontSize: 28,
      fontWeight: 600,
      color: valueColor || '#262626',
      marginBottom: 4,
      fontFamily: 'SF Mono, Monaco, monospace'
    }}>
      {value}
    </div>
    {change && (
      <div style={{
        fontSize: 12,
        display: 'flex',
        alignItems: 'center',
        gap: 4,
        color: isUp === true ? PROFIT_COLORS.up : isUp === false ? PROFIT_COLORS.down : '#8C8C8C',
      }}>
        {isUp === true && <ArrowUpOutlined />}
        {isUp === false && <ArrowDownOutlined />}
        <span>{change}</span>
      </div>
    )}
  </div>
);

const Transactions: React.FC = () => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [total, setTotal] = useState(0);
  const [summary, setSummary] = useState<TransactionSummary | null>(null);
  const [loading, setLoading] = useState(false);
  const [lastUpdate, setLastUpdate] = useState<Date>(new Date());
  const [queryParams, setQueryParams] = useState<TransactionQuery>({
    pageNum: 1,
    pageSize: 10,
  });
  const [modalVisible, setModalVisible] = useState(false);
  const [form] = Form.useForm();

  // 加载数据
  const fetchData = async () => {
    setLoading(true);
    try {
      const [transactionRes, summaryRes] = await Promise.all([
        getTransactions(queryParams),
        getTransactionSummary(),
      ]);
      setTransactions(transactionRes.list);
      setTotal(transactionRes.total);
      setSummary(summaryRes);
      setLastUpdate(new Date());
    } catch (error) {
      console.error('Failed to fetch transactions:', error);
    } finally {
      setLoading(false);
    }
  };

  // 刷新数据
  const handleRefresh = () => {
    fetchData();
  };

  useEffect(() => {
    fetchData();
  }, [queryParams]);

  // 处理筛选
  const handleFilter = (key: keyof TransactionQuery, value: any) => {
    setQueryParams(prev => ({ ...prev, [key]: value, pageNum: 1 }));
  };

  // 处理日期快捷选择
  const handleDateQuickSelect = (type: string) => {
    const today = dayjs();
    let startDate: string | undefined;
    let endDate: string | undefined = today.format('YYYY-MM-DD');

    switch (type) {
      case 'today':
        startDate = today.format('YYYY-MM-DD');
        break;
      case 'week':
        startDate = today.startOf('week').format('YYYY-MM-DD');
        break;
      case 'month':
        startDate = today.startOf('month').format('YYYY-MM-DD');
        break;
      case 'year':
        startDate = today.startOf('year').format('YYYY-MM-DD');
        break;
      default:
        startDate = undefined;
        endDate = undefined;
    }
    setQueryParams(prev => ({ ...prev, startDate, endDate, pageNum: 1 }));
  };

  // 处理表格变化
  const handleTableChange = (pagination: any) => {
    setQueryParams(prev => ({
      ...prev,
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
    }));
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const data: TransactionRequest = {
        ...values,
        transactionDate: values.transactionDate.format('YYYY-MM-DD'),
        shares: Number(values.shares),
        price: Number(values.price),
        fee: values.fee ? Number(values.fee) : 0,
      };
      await createTransaction(data);
      message.success('添加成功');
      setModalVisible(false);
      fetchData();
    } catch (error: any) {
      message.error(error.message || '操作失败');
    }
  };

  // 删除交易
  const handleDelete = async (id: number) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这条交易记录吗？',
      onOk: async () => {
        await deleteTransaction(id);
        message.success('删除成功');
        fetchData();
      },
    });
  };

  // 表格列定义
  const columns: ColumnsType<Transaction> = [
    {
      title: '交易日期',
      dataIndex: 'transactionDate',
      key: 'transactionDate',
      width: 100,
      render: (date) => <span style={{ fontFamily: 'SF Mono, Monaco, monospace' }}>{date}</span>,
    },
    {
      title: '类型',
      dataIndex: 'transactionType',
      key: 'transactionType',
      width: 80,
      align: 'center',
      render: (type) => (
        <span style={{
          display: 'inline-flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '4px 12px',
          borderRadius: 4,
          fontSize: 12,
          fontWeight: 500,
          minWidth: 48,
          background: type === 'buy' ? 'rgba(232, 93, 93, 0.1)' : 'rgba(76, 175, 80, 0.1)',
          color: type === 'buy' ? PROFIT_COLORS.up : PROFIT_COLORS.down,
        }}>
          {type === 'buy' ? '买入' : '卖出'}
        </span>
      ),
    },
    {
      title: '标的',
      dataIndex: 'symbol',
      key: 'symbol',
      width: 180,
      render: (symbol, record) => {
        const moduleConfig = record.module ? MODULE_CONFIG[record.module] : null;
        return (
          <div>
            <div style={{ fontWeight: 500, color: '#262626' }}>
              {moduleConfig?.emoji} {record.name || symbol}
            </div>
            <div style={{ fontSize: 12, color: '#8C8C8C' }}>{symbol}</div>
          </div>
        );
      },
    },
    {
      title: '模块',
      dataIndex: 'module',
      key: 'module',
      width: 80,
      align: 'center',
      render: (module) => {
        if (!module) return '-';
        const config = MODULE_CONFIG[module] || { name: module, color: '#999', bgColor: '#FAFAFA', borderColor: '#D9D9D9' };
        return (
          <span style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: 4,
            padding: '4px 10px',
            borderRadius: 4,
            fontSize: 12,
            fontWeight: 500,
            background: config.bgColor,
            color: config.color,
            border: `1px solid ${config.borderColor}`,
          }}>
            {config.name}
          </span>
        );
      },
    },
    {
      title: '市场',
      dataIndex: 'market',
      key: 'market',
      width: 100,
      align: 'center',
      render: (market) => {
        const config = MARKET_TYPE_CONFIG[market] || { name: market || 'A股', emoji: '📈' };
        return (
          <span style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: 4,
            fontSize: 13,
          }}>
            <span>{config.emoji}</span>
            <span>{config.name}</span>
          </span>
        );
      },
    },
    {
      title: '数量',
      dataIndex: 'shares',
      key: 'shares',
      width: 100,
      align: 'right',
      render: (value) => <span style={{ fontFamily: 'SF Mono, Monaco, monospace' }}>{formatShares(value)}</span>,
    },
    {
      title: '价格',
      dataIndex: 'price',
      key: 'price',
      width: 90,
      align: 'right',
      render: (value) => <span style={{ fontFamily: 'SF Mono, Monaco, monospace' }}>{value?.toFixed(2) || '-'}</span>,
    },
    {
      title: '成交金额',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      width: 110,
      align: 'right',
      render: (value, record) => (
        <span style={{
          fontFamily: 'SF Mono, Monaco, monospace',
          color: record.transactionType === 'buy' ? PROFIT_COLORS.down : PROFIT_COLORS.up,
        }}>
          {record.transactionType === 'buy' ? '-' : '+'}{formatMoney(value)}
        </span>
      ),
    },
    {
      title: '手续费',
      dataIndex: 'fee',
      key: 'fee',
      width: 90,
      align: 'right',
      render: (value) => <span style={{ fontFamily: 'SF Mono, Monaco, monospace' }}>{value?.toFixed(2) || '0.00'}</span>,
    },
    {
      title: '已实现收益',
      dataIndex: 'realizedProfit',
      key: 'realizedProfit',
      width: 120,
      align: 'right',
      render: (value, record) => {
        if (record.transactionType !== 'sell' || value === null || value === undefined) {
          return <span style={{ color: '#8C8C8C' }}>-</span>;
        }
        return (
          <span style={{ color: getProfitColor(value), fontFamily: 'SF Mono, Monaco, monospace' }}>
            {value >= 0 ? '+' : ''}{formatMoney(value)}
          </span>
        );
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      align: 'center',
      render: (_, record) => (
        <div style={{ display: 'flex', gap: 8, justifyContent: 'center' }}>
          <button
            style={{
              padding: '4px 8px',
              fontSize: 12,
              borderRadius: 4,
              border: 'none',
              background: 'transparent',
              color: '#1890FF',
              cursor: 'pointer',
            }}
            onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(24, 144, 255, 0.1)'}
            onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
          >
            详情
          </button>
          <button
            style={{
              padding: '4px 8px',
              fontSize: 12,
              borderRadius: 4,
              border: 'none',
              background: 'transparent',
              color: '#FF4D4F',
              cursor: 'pointer',
            }}
            onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(255, 77, 79, 0.1)'}
            onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
            onClick={() => handleDelete(record.id)}
          >
            删除
          </button>
        </div>
      ),
    },
  ];

  return (
    <div style={{ minHeight: 'calc(100vh - 64px)' }}>
      {/* 页面标题栏 */}
      <PageHeader
        icon="📝"
        title="交易记录"
        lastUpdate={lastUpdate}
        loading={loading}
        onRefresh={handleRefresh}
        actions={
          <>
            <Button icon={<UploadOutlined />}>导入</Button>
            <Button icon={<DownloadOutlined />}>导出</Button>
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalVisible(true)}>记录交易</Button>
          </>
        }
      />

      {/* 汇总卡片 - 5列布局 */}
      {loading && !summary ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: 16, marginBottom: 24 }}>
          {[1, 2, 3, 4, 5].map(i => (
            <div key={i} style={{ background: '#fff', borderRadius: 12, padding: 20, border: '1px solid #F0F0F0' }}>
              <Skeleton active paragraph={{ rows: 2 }} />
            </div>
          ))}
        </div>
      ) : (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(5, 1fr)',
          gap: 16,
          marginBottom: 24,
        }}>
          <SummaryCard
            icon="📊"
            title="交易次数"
            value={summary?.totalCount || 0}
            change="本月交易"
          />
          <SummaryCard
            icon="🔴"
            title="买入金额"
            value={formatMoney(summary?.buyAmount || 0)}
            change={`${summary?.buyCount || 0} 笔买入`}
            valueColor={PROFIT_COLORS.up}
          />
          <SummaryCard
            icon="🟢"
            title="卖出金额"
            value={formatMoney(summary?.sellAmount || 0)}
            change={`${summary?.sellCount || 0} 笔卖出`}
            valueColor={PROFIT_COLORS.down}
          />
          <SummaryCard
            icon="💰"
            title="手续费"
            value={summary?.totalFee?.toFixed(2) || '0.00'}
            change="佣金 + 印花税"
          />
          <SummaryCard
            icon="📈"
            title="已实现收益"
            value={summary?.realizedProfit != null ? (summary.realizedProfit >= 0 ? `+${formatMoney(summary.realizedProfit)}` : formatMoney(summary.realizedProfit)) : '-'}
            change={summary?.realizedProfitRate != null ? `${summary.realizedProfitRate >= 0 ? '↑' : '↓'} ${summary.realizedProfitRate >= 0 ? '+' : ''}${summary.realizedProfitRate.toFixed(2)}%` : undefined}
            isUp={summary?.realizedProfit != null ? summary.realizedProfit > 0 : null}
            valueColor={summary?.realizedProfit != null ? getProfitColor(summary.realizedProfit) : undefined}
          />
        </div>
      )}

      {/* 筛选栏 */}
      <div style={{
        background: '#fff',
        borderRadius: 12,
        padding: '16px 20px',
        marginBottom: 16,
        border: '1px solid #F0F0F0',
        display: 'flex',
        alignItems: 'center',
        gap: 12,
        flexWrap: 'wrap',
      }}>
        {/* 交易类型筛选 */}
        <div style={{ display: 'flex', gap: 8 }}>
          {[
            { key: '', label: '全部' },
            { key: 'buy', label: '🔴 买入', color: PROFIT_COLORS.up },
            { key: 'sell', label: '🟢 卖出', color: PROFIT_COLORS.down },
          ].map(item => (
            <button
              key={item.key}
              style={{
                padding: '6px 16px',
                borderRadius: 6,
                fontSize: 13,
                cursor: 'pointer',
                border: queryParams.transactionType === item.key ? 'none' : '1px solid #F0F0F0',
                background: queryParams.transactionType === item.key ? (item.color || '#1890FF') : '#fff',
                color: queryParams.transactionType === item.key ? '#fff' : '#262626',
              }}
              onClick={() => handleFilter('transactionType', item.key as any)}
            >
              {item.label}
            </button>
          ))}
        </div>

        {/* 模块筛选 */}
        <div style={{ display: 'flex', gap: 8 }}>
          <button
            style={{
              padding: '6px 16px',
              borderRadius: 6,
              fontSize: 13,
              cursor: 'pointer',
              border: !queryParams.module ? 'none' : '1px solid #F0F0F0',
              background: !queryParams.module ? '#1890FF' : '#fff',
              color: !queryParams.module ? '#fff' : '#262626',
            }}
            onClick={() => handleFilter('module', '')}
          >
            全部模块
          </button>
          {Object.entries(MODULE_CONFIG).map(([key, config]) => (
            <button
              key={key}
              style={{
                padding: '6px 16px',
                borderRadius: 6,
                fontSize: 13,
                cursor: 'pointer',
                border: queryParams.module === key ? 'none' : '1px solid #F0F0F0',
                background: queryParams.module === key ? config.color : '#fff',
                color: queryParams.module === key ? '#fff' : '#262626',
              }}
              onClick={() => handleFilter('module', key as any)}
            >
              {config.emoji} {config.name}
            </button>
          ))}
        </div>

        {/* 搜索框 */}
        <div style={{ flex: 1, maxWidth: 300, position: 'relative' }}>
          <span style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', fontSize: 14 }}>🔍</span>
          <input
            type="text"
            placeholder="搜索标的代码/名称..."
            style={{
              width: '100%',
              padding: '8px 12px 8px 36px',
              border: '1px solid #F0F0F0',
              borderRadius: 6,
              fontSize: 14,
              outline: 'none',
            }}
            onFocus={(e) => e.target.style.borderColor = '#1890FF'}
            onBlur={(e) => e.target.style.borderColor = '#F0F0F0'}
            onKeyDown={(e) => {
              if (e.key === 'Enter') {
                handleFilter('keyword', (e.target as HTMLInputElement).value);
              }
            }}
          />
        </div>

        {/* 日期快捷选择 */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginLeft: 'auto' }}>
          {[
            { key: 'all', label: '全部' },
            { key: 'today', label: '今日' },
            { key: 'week', label: '本周' },
            { key: 'month', label: '本月' },
            { key: 'year', label: '本年' },
          ].map(item => (
            <button
              key={item.key}
              style={{
                padding: '4px 10px',
                borderRadius: 4,
                fontSize: 12,
                cursor: 'pointer',
                border: '1px solid #F0F0F0',
                background: '#fff',
              }}
              onClick={() => handleDateQuickSelect(item.key)}
            >
              {item.label}
            </button>
          ))}
        </div>
      </div>

      {/* 交易记录表格 */}
      <div style={{ background: '#fff', borderRadius: 12, border: '1px solid #F0F0F0', overflow: 'hidden' }}>
        {/* 表格 */}
        <Table
          columns={columns}
          dataSource={transactions}
          rowKey="id"
          loading={loading}
          pagination={{
            current: queryParams.pageNum,
            pageSize: queryParams.pageSize,
            total,
            showSizeChanger: false,
            showTotal: (total) => `每页 ${queryParams.pageSize} 条，共 ${total} 条`,
            style: { padding: 16 },
          }}
          onChange={handleTableChange}
          locale={{
            emptyText: (
              <Empty
                description={
                  <div>
                    <div style={{ fontSize: 16, marginBottom: 8 }}>暂无交易记录</div>
                    <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalVisible(true)}>
                      记录第一笔交易
                    </Button>
                  </div>
                }
              />
            ),
          }}
        />
      </div>

      {/* 添加交易弹窗 */}
      <Modal
        title="记录交易"
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="transactionType"
            label="交易类型 *"
            rules={[{ required: true, message: '请选择交易类型' }]}
          >
            <Select placeholder="选择交易类型">
              <Select.Option value="buy">🔴 买入</Select.Option>
              <Select.Option value="sell">🟢 卖出</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="symbol"
            label="标的代码 *"
            rules={[{ required: true, message: '请输入标的代码' }]}
          >
            <Input placeholder="输入代码，如: 600519.SH" />
          </Form.Item>

          <Form.Item
            name="module"
            label="所属模块 *"
            rules={[{ required: true, message: '请选择模块' }]}
          >
            <Radio.Group>
              {Object.entries(MODULE_CONFIG).map(([key, config]) => (
                <Radio.Button key={key} value={key}>{config.emoji} {config.name}</Radio.Button>
              ))}
            </Radio.Group>
          </Form.Item>

          <Form.Item
            name="market"
            label="市场类型 *"
            rules={[{ required: true, message: '请选择市场类型' }]}
            initialValue="a_stock"
          >
            <Radio.Group>
              {Object.entries(MARKET_TYPE_CONFIG).map(([key, config]) => (
                <Radio.Button key={key} value={key}>{config.emoji} {config.name}</Radio.Button>
              ))}
            </Radio.Group>
          </Form.Item>

          <Form.Item name="transactionDate" label="交易日期 *" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="shares" label="交易数量 *" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} min={0} precision={0} />
          </Form.Item>

          <Form.Item name="price" label="成交价格 *" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} min={0} precision={2} />
          </Form.Item>

          <Form.Item name="fee" label="手续费">
            <InputNumber style={{ width: '100%' }} min={0} precision={2} />
          </Form.Item>

          <Form.Item name="notes" label="备注">
            <Input.TextArea rows={2} placeholder="可选" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Transactions;
