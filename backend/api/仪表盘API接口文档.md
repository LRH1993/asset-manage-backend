
# 仪表盘API接口文档

## 接口列表

### 1. 资产相关接口 (Asset)

#### 1.1 获取资产总览
```
GET /api/assets/overview
```

**响应数据**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalValue": 1234567.89,
    "todayProfit": 28400.00,
    "todayProfitRate": 2.35,
    "totalProfit": 234567.89,
    "totalProfitRate": 23.47,
    "annualReturn": 15.60,
    "benchmarkComparison": 5.20
  }
}
```

---

#### 1.2 获取核心指标
```
GET /api/assets/metrics
```

**响应数据**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "maxDrawdown": -8.50,
    "sharpeRatio": 1.85,
    "volatility30d": 12.30,
    "volatility90d": 14.20,
    "concentrationRisk": 0.25,
    "var95": -0.03,
    "var99": -0.05
  }
}
```

---

#### 1.3 获取资产配置（四象限）
```
GET /api/assets/allocation
```

**响应数据**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "dividend": {
      "name": "红利",
      "targetWeight": 0.25,
      "currentValue": 280000.00,
      "currentWeight": 0.23,
      "deviation": -2.00,
      "return": 12.50
    },
    "fixed": {
      "name": "固收",
      "targetWeight": 0.25,
      "currentValue": 310000.00,
      "currentWeight": 0.25,
      "deviation": 0.00,
      "return": 3.20
    },
    "growth": {
      "name": "成长",
      "targetWeight": 0.25,
      "currentValue": 320000.00,
      "currentWeight": 0.26,
      "deviation": 1.00,
      "return": 18.70
    },
    "allweather": {
      "name": "全天候",
      "targetWeight": 0.25,
      "currentValue": 324567.89,
      "currentWeight": 0.26,
      "deviation": 1.00,
      "return": 8.50
    }
  }
}
```

---

#### 1.4 获取收益曲线
```
GET /api/assets/return-curve?period=7d|30d|90d|1y|all
```

**响应数据**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "date": "2025-12-01",
      "value": 1100000.00,
      "returnRate": 10.00,
      "benchmark": 1080000.00
    },
    {
      "date": "2025-12-02",
      "value": 1105000.00,
      "returnRate": 10.45,
      "benchmark": 1085000.00
    }
  ]
}
```

---

### 2. 风险相关接口 (Risk)

#### 2.1 获取风险指标
```
GET /api/risk/metrics?date=2025-12-15
```

**响应数据**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "metricDate": "2025-12-15",
    "totalValue": 1234567.89,
    "dailyReturn": 0.0052,
    "volatility30d": 0.1230,
    "volatility90d": 0.1420,
    "maxDrawdown": -0.0850,
    "sharpeRatio": 1.85,
    "sortinoRatio": 2.30,
    "var95": -0.0300,
    "var99": -0.0500,
    "beta": 0.95,
    "alpha": 0.0250,
    "correlationMatrix": {
      "dividend": { "fixed": 0.3, "growth": 0.7, "allweather": 0.5 },
      "fixed": { "dividend": 0.3, "growth": 0.2, "allweather": 0.8 },
      "growth": { "dividend": 0.7, "fixed": 0.2, "allweather": 0.6 },
      "allweather": { "dividend": 0.5, "fixed": 0.8, "growth": 0.6 }
    },
    "sectorDistribution": {
      "金融": 0.35,
      "科技": 0.25,
      "消费": 0.20,
      "医疗": 0.10,
      "其他": 0.10
    }
  }
}
```

---

#### 2.2 获取风险仪表盘数据
```
GET /api/risk/dashboard
```

**响应数据**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "metrics": {
      "metricDate": "2025-12-15",
      "totalValue": 1234567.89,
      "dailyReturn": 0.0052,
      "volatility30d": 0.1230,
      "volatility90d": 0.1420,
      "maxDrawdown": -0.0850,
      "sharpeRatio": 1.85,
      "sortinoRatio": 2.30,
      "var95": -0.0300,
      "var99": -0.0500,
      "beta": 0.95,
      "alpha": 0.0250
    },
    "alerts": [
      {
        "id": 1,
        "alertType": "volatility",
        "severity": "warning",
        "message": "成长模块波动率偏高，当前30日波动率为18.5%",
        "currentValue": 18.5,
        "threshold": 15.0,
        "isRead": false,
        "createTime": "2025-12-15T10:30:00"
      },
      {
        "id": 2,
        "alertType": "concentration",
        "severity": "info",
        "message": "金融行业集中度为35%，建议控制在30%以内",
        "currentValue": 35.0,
        "threshold": 30.0,
        "isRead": false,
        "createTime": "2025-12-14T14:20:00"
      }
    ],
    "riskLevel": "medium"
  }
}
```

---

#### 2.3 执行压力测试
```
POST /api/risk/stress-test
```

**请求数据**
```json
{
  "id": "crisis_2008",
  "name": "2008年金融危机",
  "description": "模拟2008年金融危机场景",
  "marketDrop": -40,
  "rateChange": 2.5,
  "currencyChange": -10,
  "inflationChange": 1.5
}
```

**响应数据**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "scenarioId": "crisis_2008",
    "scenarioName": "2008年金融危机",
    "estimatedLoss": -450000.00,
    "estimatedLossRate": -36.45,
    "moduleImpact": {
      "dividend": -0.30,
      "fixed": -0.10,
      "growth": -0.50,
      "allweather": -0.25
    },
    "positionImpact": {
      "SZ159915": -0.55,
      "SH518880": -0.38,
      "SH511010": -0.08
    },
    "sensitivityAnalysis": {
      "marketSensitivity": 1.10,
      "rateSensitivity": -0.15,
      "currencySensitivity": -0.08
    }
  }
}
```

---

#### 2.4 获取相关性分析
```
GET /api/risk/correlation
```

**响应数据**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "dividend": { "fixed": 0.30, "growth": 0.70, "allweather": 0.50 },
    "fixed": { "dividend": 0.30, "growth": 0.20, "allweather": 0.80 },
    "growth": { "dividend": 0.70, "fixed": 0.20, "allweather": 0.60 },
    "allweather": { "dividend": 0.50, "fixed": 0.80, "growth": 0.60 }
  }
}
```

---

#### 2.5 获取风险预警列表
```
GET /api/risk/alerts
```

**响应数据**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "alertType": "volatility",
      "severity": "warning",
      "message": "成长模块波动率偏高，当前30日波动率为18.5%",
      "currentValue": 18.5,
      "threshold": 15.0,
      "isRead": false,
      "createTime": "2025-12-15T10:30:00"
    },
    {
      "id": 2,
      "alertType": "concentration",
      "severity": "info",
      "message": "金融行业集中度为35%，建议控制在30%以内",
      "currentValue": 35.0,
      "threshold": 30.0,
      "isRead": false,
      "createTime": "2025-12-14T14:20:00"
    }
  ]
}
```

---

### 3. 动态平衡相关接口 (Rebalance)

#### 3.1 获取平衡状态
```
GET /api/rebalance/status
```

**响应数据**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalDeviation": 5.50,
    "needsRebalance": true,
    "lastRebalanceDate": "2025-11-01"
  }
}
```

---

#### 3.2 生成平衡方案
```
POST /api/rebalance/plan
```

**请求数据**
```json
{
  "triggerReason": "季度平衡",
  "threshold": 5.0
}
```

**响应数据**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "totalValue": 1234567.89,
    "rebalanceDate": "2025-12-16",
    "triggerReason": "季度平衡",
    "actions": [
      {
        "symbol": "SZ159915",
        "actionType": "sell",
        "shares": 1000,
        "price": 2.85,
        "amount": 2850.00,
        "reason": "超配"
      },
      {
        "symbol": "SH511010",
        "actionType": "buy",
        "shares": 200,
        "price": 125.50,
        "amount": 25100.00,
        "reason": "低配"
      }
    ],
    "estimatedFee": 80.85,
    "status": "pending"
  }
}
```

---

#### 3.3 执行平衡方案
```
POST /api/rebalance/execute
```

**请求数据**
```json
{
  "id": 1,
  "totalValue": 1234567.89,
  "rebalanceDate": "2025-12-16",
  "triggerReason": "季度平衡",
  "actions": [
    {
      "symbol": "SZ159915",
      "actionType": "sell",
      "shares": 1000,
      "price": 2.85,
      "amount": 2850.00,
      "reason": "超配"
    },
    {
      "symbol": "SH511010",
      "actionType": "buy",
      "shares": 200,
      "price": 125.50,
      "amount": 25100.00,
      "reason": "低配"
    }
  ],
  "estimatedFee": 80.85,
  "status": "executed"
}
```

**响应数据**
```json
{
  "code": 200,
  "message": "平衡方案执行成功"
}
```

---

#### 3.4 获取平衡历史
```
GET /api/rebalance/history?page=1&pageSize=10
```

**响应数据**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "balanceDate": "2025-11-01",
        "totalValueBefore": 1180000.00,
        "totalValueAfter": 1200000.00,
        "triggerReason": "季度平衡",
        "actions": [
          {
            "symbol": "SH511010",
            "actionType": "buy",
            "shares": 500,
            "price": 120.00,
            "amount": 60000.00,
            "reason": "低配"
          }
        ],
        "status": "executed",
        "executedAt": "2025-11-01T10:30:00",
        "notes": "成功执行",
        "createTime": "2025-11-01T09:00:00"
      }
    ],
    "total": 10
  }
}
```

---

## 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码，200表示成功 |
| message | string | 响应消息 |
| data | object/array | 响应数据 |