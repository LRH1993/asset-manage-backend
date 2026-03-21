/**
 * 图表配置
 */

export const CHART_CONFIG = {
  // 通用配置
  common: {
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
      },
    },
  },

  // 折线图配置
  line: {
    smooth: true,
    lineStyle: {
      width: 2,
    },
    areaStyle: {
      opacity: 0.1,
    },
  },

  // 柱状图配置
  bar: {
    barWidth: '60%',
    itemStyle: {
      borderRadius: [4, 4, 0, 0],
    },
  },

  // 饼图配置
  pie: {
    radius: ['40%', '70%'],
    label: {
      show: true,
      formatter: '{b}: {d}%',
    },
    emphasis: {
      itemStyle: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.5)',
      },
    },
  },

  // 热力图配置
  heatmap: {
    visualMap: {
      min: -1,
      max: 1,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: '0%',
      inRange: {
        color: ['#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8', '#ffffbf', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026'],
      },
    },
    tooltip: {
      position: 'top',
      formatter: (params: any) => {
        return `${params.data[0]} x ${params.data[1]}: ${params.data[2].toFixed(4)}`;
      },
    },
  },

  // 回撤图配置
  drawdown: {
    lineStyle: {
      color: '#ef4444',
    },
    areaStyle: {
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(239, 68, 68, 0.3)' },
          { offset: 1, color: 'rgba(239, 68, 68, 0.05)' },
        ],
      },
    },
  },
};
