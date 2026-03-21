/**
 * 仪表盘页面
 */

import { useEffect } from 'react';
import { Row, Col } from 'antd';
import { useAssetStore } from '@/stores/assetStore';
import {
  TotalAssetCard,
  ModuleAllocationChart,
  RiskMetricsCard,
  PerformanceMetricsGrid,
  ModuleDetailsTable,
  AssetCurveChart,
  RebalanceSuggestions,
} from './components';

const Dashboard: React.FC = () => {
  const { overview, loading, fetchAll } = useAssetStore();

  // 初始化加载资产数据
  useEffect(() => {
    fetchAll();
  }, [fetchAll]);

  if (loading) {
    return <div>加载中...</div>;
  }

  return (
    <div style={{ padding: '0 24px' }}>
      {/* 顶部指标区域 */}
      <Row gutter={20}>
        <Col xs={24} sm={24} md={14} lg={12}>
          <TotalAssetCard
            totalValue={overview?.totalValue || 1234567.89}
            todayProfit={overview?.todayProfit || 28400}
            totalProfit={overview?.totalProfit || 234567.89}
            annualReturn={overview?.annualReturn || 15.6}
          />
        </Col>
        <Col xs={24} sm={24} md={10} lg={12}>
          <PerformanceMetricsGrid />
        </Col>
      </Row>

      {/* 主网格布局 */}
      <Row gutter={[24, 24]} style={{ marginTop: 20, alignItems: 'flex-start' }}>
        {/* 左侧边栏 */}
        <Col xs={24} sm={24} md={8} lg={8} xl={6}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 20, height: '100%' }}>
            {/* 四象限资产配置 */}
            <ModuleAllocationChart />
            {/* 风险指标 */}
            <RiskMetricsCard />
          </div>
        </Col>

        {/* 右侧主面板 */}
        <Col xs={24} sm={24} md={16} lg={16} xl={18}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
            {/* 模块详情表格 */}
            <ModuleDetailsTable />
            {/* 资产净值曲线图 */}
            <AssetCurveChart />
          </div>
        </Col>
      </Row>

      {/* 动态平衡建议 - 平铺整个页面 */}
      <div style={{ marginTop: 24 }}>
        <RebalanceSuggestions />
      </div>
    </div>
  );
};

export default Dashboard;
