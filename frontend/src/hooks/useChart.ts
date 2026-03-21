/**
 * 图表Hook
 */

import { useState, useEffect, useRef } from 'react';

export const useChart = () => {
  const [chartInstance, setChartInstance] = useState<any>(null);
  const chartRef = useRef<any>(null);

  useEffect(() => {
    return () => {
      if (chartInstance) {
        chartInstance.dispose();
      }
    };
  }, [chartInstance]);

  const updateChart = (option: any) => {
    if (chartInstance) {
      chartInstance.setOption(option, true);
    }
  };

  const resizeChart = () => {
    if (chartInstance) {
      chartInstance.resize();
    }
  };

  return {
    chartRef,
    chartInstance,
    setChartInstance,
    updateChart,
    resizeChart,
  };
};
