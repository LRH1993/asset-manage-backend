/**
 * 四象限资产配置图组件
 */

import React from 'react';
import { Card } from 'antd';
import ReactECharts from 'echarts-for-react';
import type { EChartsOption } from 'echarts';
import { MODULES } from '@/constants/modules';

const ModuleAllocationChart: React.FC = () => {
  const option: EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c} ({d}%)',
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      itemGap: 20,
      textStyle: {
        fontSize: 14,
        color: '#718096',
      },
    },
    series: [
      {
        name: '资产配置',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
        },
        emphasis: {
          label: {
            show: false,
          },
        },
        labelLine: {
          show: false,
        },
        data: [
          {
            value: 25,
            name: '成长',
            itemStyle: {
              color: MODULES.growth.color,
              borderRadius: 0,
              borderWidth: 2,
              borderColor: '#fff',
            },
          },
          {
            value: 25,
            name: '红利',
            itemStyle: {
              color: MODULES.dividend.color,
              borderRadius: 0,
              borderWidth: 2,
              borderColor: '#fff',
            },
          },
          {
            value: 25,
            name: '固收',
            itemStyle: {
              color: MODULES.fixed.color,
              borderRadius: 0,
              borderWidth: 2,
              borderColor: '#fff',
            },
          },
          {
            value: 25,
            name: '全天候',
            itemStyle: {
              color: MODULES.allweather.color,
              borderRadius: 0,
              borderWidth: 2,
              borderColor: '#fff',
            },
          },
        ],
      },
    ],
  };

  return (
    <Card
      title={
        <span style={{ fontSize: 16, fontWeight: 600, color: '#2d3748' }}>
          📊 资产配置
        </span>
      }
      style={{
        borderRadius: 16,
        boxShadow: '0 2px 12px rgba(0, 0, 0, 0.06)',
        height: '360px',
      }}
      styles={{ body: { padding: '16px', height: 'calc(100% - 56px)', display: 'flex', alignItems: 'center', justifyContent: 'center' } }}
    >
      <ReactECharts
        option={option}
        style={{ height: '100%', width: '100%' }}
        opts={{ renderer: 'canvas' }}
      />
    </Card>
  );
};

export default ModuleAllocationChart;
