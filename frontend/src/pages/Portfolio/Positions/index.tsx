/**
 * 持仓管理页面
 * 严格遵循 ui/prototypes/positions.html 设计规范
 */

import { useEffect, useState } from 'react';
import {
  Table, Button, Modal, Form, Input, InputNumber,
  message, Row, Col, Radio, Empty, Checkbox, Skeleton
} from 'antd';
import {
  PlusOutlined,
  UploadOutlined, DownloadOutlined,
  ArrowUpOutlined, ArrowDownOutlined
} from '@ant-design/icons';
import { usePositionStore } from '@/stores/positionStore';
import type { Position, PositionRequest } from '@/api/position';
import type { ColumnsType } from 'antd/es/table';
import { PROFIT_COLORS, NEUTRAL_COLORS } from '@/constants/colors';
import { formatMoney, getProfitColor, formatShares } from '@/utils/format';
import { PageHeader } from '@/components/common';

// 模块配置 - 符合 UI 设计规范，添加 emoji
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
}

const SummaryCard: React.FC<SummaryCardProps> = ({ icon, title, value, change, isUp }) => (
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
    <div style={{ fontSize: 28, fontWeight: 600, color: '#262626', marginBottom: 4, fontFamily: 'SF Mono, Monaco, monospace' }}>
      {value}
    </div>
    {change && (
      <div style={{
        fontSize: 12,
        display: 'flex',
        alignItems: 'center',
        gap: 4,
        color: isUp === true ? PROFIT_COLORS.up : isUp === false ? PROFIT_COLORS.down : NEUTRAL_COLORS.textTertiary,
      }}>
        {isUp === true && <ArrowUpOutlined />}
        {isUp === false && <ArrowDownOutlined />}
        <span>{change}</span>
      </div>
    )}
  </div>
);

const Positions: React.FC = () => {
  const {
    positions,
    total,
    summary,
    loading,
    fetchPositions,
    fetchSummary,
    addPosition,
    updatePosition,
    queryParams,
    setQueryParams,
  } = usePositionStore();

  const [modalVisible, setModalVisible] = useState(false);
  const [editingPosition, setEditingPosition] = useState<Position | null>(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [lastUpdate, setLastUpdate] = useState<Date>(new Date());
  const [form] = Form.useForm();

  // 初始化加载数据
  useEffect(() => {
    handleRefresh();
  }, []);

  // 刷新数据
  const handleRefresh = async () => {
    await Promise.all([fetchPositions(), fetchSummary()]);
    setLastUpdate(new Date());
  };

  // 处理搜索
  const handleSearch = (keyword: string) => {
    setQueryParams({ keyword, pageNum: 1 });
    fetchPositions({ keyword, pageNum: 1 });
  };

  // 处理模块筛选
  const handleModuleFilter = (module: string | undefined) => {
    const newParams = { module, pageNum: 1 };
    setQueryParams(newParams);
    fetchPositions(newParams);
  };

  // 处理状态筛选
  const handleStatusFilter = (status: string) => {
    const newParams = { status: status === 'all' ? undefined : status, pageNum: 1 };
    setQueryParams(newParams);
    fetchPositions(newParams);
  };

  // 处理表格变化（分页）
  const handleTableChange = (pagination: any) => {
    const newParams = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
    };
    setQueryParams(newParams);
    fetchPositions(newParams);
  };

  // 打开添加弹窗
  const handleAdd = () => {
    setEditingPosition(null);
    form.resetFields();
    form.setFieldsValue({
      market: 'a_stock',
    });
    setModalVisible(true);
  };

  // 打开编辑弹窗
  const handleEdit = (record: Position) => {
    setEditingPosition(record);
    form.setFieldsValue(record);
    setModalVisible(true);
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const data: PositionRequest = {
        ...values,
        shares: Number(values.shares),
        avgCost: Number(values.avgCost),
        currentPrice: values.currentPrice ? Number(values.currentPrice) : undefined,
        targetWeight: values.targetWeight ? Number(values.targetWeight) : undefined,
        buyPriceThreshold: values.buyPriceThreshold ? Number(values.buyPriceThreshold) : undefined,
        sellPriceThreshold: values.sellPriceThreshold ? Number(values.sellPriceThreshold) : undefined,
      };

      if (editingPosition) {
        await updatePosition(editingPosition.id, data);
        message.success('更新成功');
      } else {
        await addPosition(data);
        message.success('添加成功');
      }
      setModalVisible(false);
    } catch (error: any) {
      message.error(error.message || '操作失败');
    }
  };

  // 表格列定义
  const columns: ColumnsType<Position> = [
    {
      title: (
        <Checkbox
          checked={selectedRowKeys.length === positions.length && positions.length > 0}
          indeterminate={selectedRowKeys.length > 0 && selectedRowKeys.length < positions.length}
          onChange={(e) => {
            if (e.target.checked) {
              setSelectedRowKeys(positions.map(p => p.id));
            } else {
              setSelectedRowKeys([]);
            }
          }}
        />
      ),
      key: 'checkbox',
      width: 40,
      render: (_, record) => (
        <Checkbox
          checked={selectedRowKeys.includes(record.id)}
          onChange={(e) => {
            if (e.target.checked) {
              setSelectedRowKeys([...selectedRowKeys, record.id]);
            } else {
              setSelectedRowKeys(selectedRowKeys.filter(k => k !== record.id));
            }
          }}
        />
      ),
    },
    {
      title: '标的',
      dataIndex: 'symbol',
      key: 'symbol',
      width: 180,
      render: (text, record) => {
        const moduleConfig = MODULE_CONFIG[record.module];
        return (
          <div>
            <div style={{ fontWeight: 500, color: '#262626' }}>
              {moduleConfig?.emoji} {record.name}
            </div>
            <div style={{ fontSize: 12, color: '#8C8C8C' }}>{text}</div>
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
      title: '市场类型',
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
      title: '持仓数量',
      dataIndex: 'shares',
      key: 'shares',
      width: 100,
      align: 'right',
      render: (value) => <span style={{ fontFamily: 'SF Mono, Monaco, monospace' }}>{formatShares(value)}</span>,
    },
    {
      title: '成本价',
      dataIndex: 'avgCost',
      key: 'avgCost',
      width: 90,
      align: 'right',
      render: (value) => <span style={{ fontFamily: 'SF Mono, Monaco, monospace' }}>{value ? value.toFixed(2) : '-'}</span>,
    },
    {
      title: '现价',
      dataIndex: 'currentPrice',
      key: 'currentPrice',
      width: 90,
      align: 'right',
      render: (value) => <span style={{ fontFamily: 'SF Mono, Monaco, monospace' }}>{value ? value.toFixed(2) : '-'}</span>,
    },
    {
      title: '市值',
      dataIndex: 'currentValue',
      key: 'currentValue',
      width: 110,
      align: 'right',
      render: (value) => <span style={{ fontFamily: 'SF Mono, Monaco, monospace' }}>{formatMoney(value)}</span>,
    },
    {
      title: '今日盈亏',
      dataIndex: 'todayProfitRate',
      key: 'todayProfitRate',
      width: 90,
      align: 'right',
      render: (value) => {
        if (value === null || value === undefined) {
          return <span style={{ color: '#8C8C8C', fontFamily: 'SF Mono, Monaco, monospace' }}>--</span>;
        }
        return (
          <span style={{ color: getProfitColor(value), fontFamily: 'SF Mono, Monaco, monospace' }}>
            {value > 0 ? '+' : ''}{value.toFixed(2)}%
          </span>
        );
      },
    },
    {
      title: '累计收益',
      dataIndex: 'profitAmount',
      key: 'profitAmount',
      width: 110,
      align: 'right',
      render: (value) => (
        <span style={{ color: getProfitColor(value), fontFamily: 'SF Mono, Monaco, monospace' }}>
          {value >= 0 ? '+' : ''}{formatMoney(value)}
        </span>
      ),
    },
    {
      title: '收益率',
      dataIndex: 'profitRate',
      key: 'profitRate',
      width: 90,
      align: 'right',
      render: (value) => (
        <span style={{ color: getProfitColor(value), fontFamily: 'SF Mono, Monaco, monospace' }}>
          {value >= 0 ? '+' : ''}{value ? value.toFixed(2) : '0.00'}%
        </span>
      ),
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
              color: PROFIT_COLORS.up,
              cursor: 'pointer',
            }}
            onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(232, 93, 93, 0.1)'}
            onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
          >
            买入
          </button>
          <button
            style={{
              padding: '4px 8px',
              fontSize: 12,
              borderRadius: 4,
              border: 'none',
              background: 'transparent',
              color: PROFIT_COLORS.down,
              cursor: 'pointer',
            }}
            onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(76, 175, 80, 0.1)'}
            onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
          >
            卖出
          </button>
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
            onClick={() => handleEdit(record)}
          >
            编辑
          </button>
        </div>
      ),
    },
  ];

  return (
    <div style={{ minHeight: 'calc(100vh - 64px)' }}>
      {/* 页面标题栏 */}
      <PageHeader
        icon="💼"
        title="持仓管理"
        lastUpdate={lastUpdate}
        loading={loading}
        onRefresh={handleRefresh}
        actions={
          <>
            <Button icon={<UploadOutlined />}>导入</Button>
            <Button icon={<DownloadOutlined />}>导出</Button>
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>添加持仓</Button>
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
            icon="📦"
            title="持仓数量"
            value={summary?.positionCount || 0}
            change="个持仓标的"
          />
          <SummaryCard
            icon="💰"
            title="总市值"
            value={formatMoney(summary?.totalValue || 0)}
            change={summary?.todayProfitRate && summary.todayProfitRate > 0 ? `↑ +${(summary.todayProfitRate * 0.01).toFixed(2)}% 较昨日` : undefined}
            isUp={summary?.todayProfitRate ? summary.todayProfitRate > 0 : null}
          />
          <SummaryCard
            icon="📈"
            title="今日盈亏"
            value={summary?.todayProfit != null ? (summary.todayProfit >= 0 ? `+${formatMoney(summary.todayProfit)}` : formatMoney(summary.todayProfit)) : '--'}
            change={summary?.todayProfitRate != null ? `${summary.todayProfitRate >= 0 ? '↑' : '↓'} ${summary.todayProfitRate >= 0 ? '+' : ''}${summary.todayProfitRate.toFixed(2)}%` : '暂无数据'}
            isUp={summary?.todayProfit != null ? summary.todayProfit > 0 : null}
          />
          <SummaryCard
            icon="📊"
            title="累计收益"
            value={summary?.totalProfit ? `+${formatMoney(summary.totalProfit)}` : '-'}
            change={summary?.totalProfitRate ? `↑ +${summary.totalProfitRate.toFixed(2)}%` : undefined}
            isUp={summary?.totalProfit ? summary.totalProfit > 0 : null}
          />
          <SummaryCard
            icon="📉"
            title="平均收益率"
            value={summary?.totalProfitRate ? `+${summary.totalProfitRate.toFixed(2)}%` : '-'}
            change="跑赢基准 +3.2%"
            isUp={true}
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
        {/* 模块筛选按钮 */}
        <div style={{ display: 'flex', gap: 8 }}>
          <button
            style={{
              padding: '6px 16px',
              borderRadius: 6,
              fontSize: 13,
              cursor: 'pointer',
              border: queryParams.module === undefined ? 'none' : '1px solid #F0F0F0',
              background: queryParams.module === undefined ? '#1890FF' : '#fff',
              color: queryParams.module === undefined ? '#fff' : '#262626',
            }}
            onClick={() => handleModuleFilter(undefined)}
          >
            全部
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
              onClick={() => handleModuleFilter(key)}
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
                handleSearch((e.target as HTMLInputElement).value);
              }
            }}
          />
        </div>

        {/* 右侧筛选 */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 16, marginLeft: 'auto' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, fontSize: 13 }}>
            <span>状态:</span>
            <label style={{ display: 'flex', alignItems: 'center', gap: 4, cursor: 'pointer' }}>
              <input type="radio" name="status" checked={!queryParams.status} onChange={() => handleStatusFilter('all')} />
              全部
            </label>
            <label style={{ display: 'flex', alignItems: 'center', gap: 4, cursor: 'pointer' }}>
              <input type="radio" name="status" checked={queryParams.status === 'active'} onChange={() => handleStatusFilter('active')} />
              持仓中
            </label>
            <label style={{ display: 'flex', alignItems: 'center', gap: 4, cursor: 'pointer' }}>
              <input type="radio" name="status" checked={queryParams.status === 'sold'} onChange={() => handleStatusFilter('sold')} />
              已清仓
            </label>
          </div>
          <select
            style={{
              padding: '8px 12px',
              border: '1px solid #F0F0F0',
              borderRadius: 6,
              fontSize: 14,
              background: '#fff',
              cursor: 'pointer',
            }}
          >
            <option>市值降序</option>
            <option>市值升序</option>
            <option>收益率降序</option>
            <option>收益率升序</option>
          </select>
        </div>
      </div>

      {/* 持仓表格 */}
      <div style={{ background: '#fff', borderRadius: 12, border: '1px solid #F0F0F0', overflow: 'hidden' }}>
        {/* 表格 */}
        <Table
          columns={columns}
          dataSource={positions}
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
                    <div style={{ fontSize: 16, marginBottom: 8 }}>暂无持仓数据</div>
                    <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
                      添加第一个持仓
                    </Button>
                  </div>
                }
              />
            ),
          }}
        />
      </div>

      {/* 添加/编辑弹窗 */}
      <Modal
        title={editingPosition ? '编辑持仓' : '添加持仓'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="symbol"
                label="标的代码 *"
                rules={[{ required: true, message: '请输入标的代码' }]}
              >
                <Input placeholder="输入代码，如: SH000922" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="name" label="标的名称">
                <Input placeholder="自动填充或手动输入" />
              </Form.Item>
            </Col>
          </Row>

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
          >
            <Radio.Group>
              {Object.entries(MARKET_TYPE_CONFIG).map(([key, config]) => (
                <Radio.Button key={key} value={key}>{config.emoji} {config.name}</Radio.Button>
              ))}
            </Radio.Group>
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="shares" label="持仓数量 *" rules={[{ required: true }]}>
                <InputNumber style={{ width: '100%' }} min={0} precision={0} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="avgCost" label="成本价 *" rules={[{ required: true }]}>
                <InputNumber style={{ width: '100%' }} min={0} precision={2} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="currentPrice" label="当前价格">
                <InputNumber style={{ width: '100%' }} min={0} precision={2} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="targetWeight" label="目标权重 (%)">
                <InputNumber style={{ width: '100%' }} min={0} max={100} precision={2} />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    </div>
  );
};

export default Positions;
