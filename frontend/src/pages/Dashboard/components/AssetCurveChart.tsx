/**
 * 资产净值曲线图组件
 * 遵循 ui/CLAUDE.md 设计规范
 */

import React, { useState, useMemo } from 'react';
import { Card, Button, Space } from 'antd';
import ReactECharts from 'echarts-for-react';
import type { EChartsOption } from 'echarts';
import { FUNCTIONAL_COLORS } from '@/constants/colors';
import { getProfitColor } from '@/utils/format';
import { useAssetStore } from '@/stores/assetStore';

interface CurveData {
  date: string;
  portfolio: number;
  benchmark: number;
}

const AssetCurveChart: React.FC = () => {
  const [period, setPeriod] = useState<'7d' | '30d' | '90d' | '1y' | 'all'>('90d');
  const { returnCurve } = useAssetStore();

  // 数据由 Dashboard 页面统一加载，这里不再单独请求

  // 从store获取真实数据
  const data: CurveData[] = useMemo(() => {
    if (returnCurve && returnCurve.length > 0) {
      return returnCurve.map(item => ({
        date: item.date,
        portfolio: item.value / 1000000, // 转换为单位净值
        benchmark: item.benchmark ? item.benchmark / 1000000 : item.value * 0.96 / 1000000,
      }));
    }
    // 如果没有数据，返回模拟数据
    return generateMockData(period);
  }, [returnCurve, period]);

  // 图表配置
  const option: EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
      },
      formatter: (params: any) => {
        if (!params || params.length === 0) return '';
        const param = params[0];
        const date = param.axisValue;
        const portfolioValue = param.value;
        const benchmarkValue = params[1]?.value || 0;
        const portfolioReturn = (portfolioValue - 1.18) * 100;
        const benchmarkReturn = benchmarkValue ? (benchmarkValue - 1.175) * 100 : 0;

        // 使用正确的涨跌色（A股习惯：红涨绿跌）
        const portfolioColor = getProfitColor(portfolioReturn);
        const benchmarkColor = getProfitColor(benchmarkReturn);

        return `
          <div style="padding: 12px;">
            <div style="margin-bottom: 8px; color: #718096; font-size: 14px;">
              ${date}
            </div>
            <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 4px;">
              <div style="width: 10px; height: 10px; background: ${FUNCTIONAL_COLORS.primary}; border-radius: 50%;"></div>
              <span style="font-weight: 600; color: #2d3748; font-size: 16px;">
                组合: ¥${(portfolioValue * 1000000).toFixed(0)}
              </span>
              <span style="color: ${portfolioColor}; font-size: 13px;">
                ${portfolioReturn >= 0 ? '+' : ''}${portfolioReturn.toFixed(2)}%
              </span>
            </div>
            ${benchmarkValue ? `
              <div style="display: flex; align-items: center; gap: 8px;">
                <div style="width: 8px; height: 8px; background: #a0aec0; border-radius: 50%;"></div>
                <span style="font-size: 13px; color: #a0aec0;">基准</span>
                <span style="font-size: 14px;">
                  ¥${(benchmarkValue * 1000000).toFixed(0)}
                </span>
                <span style="color: ${benchmarkColor}; font-size: 12px;">
                  ${benchmarkReturn >= 0 ? '+' : ''}${benchmarkReturn.toFixed(2)}%
                </span>
              </div>
            ` : ''}
          </div>
        `;
      },
    },
    legend: {
      data: [
        {
          name: '我的组合',
          icon: 'circle',
          itemStyle: {
            color: '#667eea',
          },
        },
        {
          name: '沪深300基准',
          icon: 'line',
          itemStyle: {
            color: '#a0aec0',
          },
          lineStyle: {
            type: 'dashed',
            opacity: 1,
          },
        },
      ],
      bottom: 15,
      textStyle: {
        fontSize: 12,
        color: '#718096',
        fontWeight: 600,
      },
    },
    grid: {
      left: 60,
      right: 0,
      top: 40,
      bottom: 40,
      containLabel: true,
      backgroundColor: 'transparent',
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      axisLine: {
        show: false,
      },
      axisTick: {
        show: true,
        alignWithLabel: true,
      },
      axisLabel: {
        show: true,
        color: '#a0aec0',
        fontSize: 11,
      },
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: true,
        lineStyle: {
          color: '#e2e8f0',
        },
      },
      axisTick: {
        show: true,
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: '#e2e8f0',
        },
      },
      axisLabel: {
        show: true,
        formatter: (value: number) => `¥${(value * 10000).toFixed(0)}M`,
        color: '#a0aec0',
        fontSize: 11,
      },
    },
    series: [
      {
        name: '我的组合',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          width: 3,
          color: '#667eea',
        },
        itemStyle: {
          color: '#667eea',
          borderColor: '#fff',
          borderWidth: 2,
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(102, 126, 234, 0.25)' },
              { offset: 1, color: 'rgba(102, 126, 234, 0)' },
            ],
          },
        },
        data: data.map((item) => item.portfolio),
      },
      {
        name: '沪深300基准',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 4,
        lineStyle: {
          width: 2,
          color: '#a0aec0',
          type: 'dashed',
        },
        itemStyle: {
          color: '#a0aec0',
          borderColor: '#fff',
        },
        data: data.map((item) => item.benchmark),
      },
    ],
  };

  const periodLabels = {
    '7d': '7日',
    '30d': '30日',
    '90d': '90日',
    '1y': '1年',
    'all': '全部',
  };

  return (
    <Card
      title={
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontSize: 16, fontWeight: 600, color: '#262626' }}>
            📈 资产净值曲线 vs 基准
          </span>
          <Space>
            <Button
              size="small"
              type={period === '7d' ? 'primary' : 'default'}
              onClick={() => setPeriod('7d')}
            >
              {periodLabels['7d']}
            </Button>
            <Button
              size="small"
              type={period === '30d' ? 'primary' : 'default'}
              onClick={() => setPeriod('30d')}
            >
              {periodLabels['30d']}
            </Button>
            <Button
              size="small"
              type={period === '90d' ? 'primary' : 'default'}
              onClick={() => setPeriod('90d')}
            >
              {periodLabels['90d']}
            </Button>
            <Button
              size="small"
              type={period === '1y' ? 'primary' : 'default'}
              onClick={() => setPeriod('1y')}
            >
              {periodLabels['1y']}
            </Button>
            <Button
              size="small"
              type={period === 'all' ? 'primary' : 'default'}
              onClick={() => setPeriod('all')}
            >
              {periodLabels['all']}
            </Button>
          </Space>
        </div>
      }
      style={{
        borderRadius: 12,
        border: '1px solid #F0F0F0',
        height: '360px',
      }}
      styles={{ body: { padding: '16px', height: 'calc(100% - 56px)' } }}
    >
      <div style={{ position: 'relative', height: '100%' }}>
        <ReactECharts option={option} style={{ height: '100%', width: '100%' }} />
      </div>
    </Card>
  );
};

// 生成模拟数据的辅助函数
function generateMockData(period: string): CurveData[] {
  const days = period === '7d' ? 7 : period === '30d' ? 30 : period === '90d' ? 90 : period === '1y' ? 365 : 90;
  const result: CurveData[] = [];
  const today = new Date();

  for (let i = days; i >= 0; i--) {
    const date = new Date(today);
    date.setDate(date.getDate() - i);
    result.push({
      date: date.toLocaleDateString('zh-CN'),
      portfolio: 1.0 + (days - i) * 0.003 + Math.sin(i / 10) * 0.05,
      benchmark: 1.0 + (days - i) * 0.002 + Math.sin(i / 10) * 0.03,
    });
  }

  return result;
}

export default AssetCurveChart;
