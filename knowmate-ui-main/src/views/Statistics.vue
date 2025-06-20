<template>
  <div class="statistics-container">
    <h1>数据统计与分析</h1>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>BMI 趋势图 (近7天)</span>
            </div>
          </template>
          <div v-loading="bmiLoading" class="chart-placeholder">
            <div v-if="bmiError" class="chart-error">{{ bmiError }}</div>
            <div v-else-if="bmiTrend && bmiTrend.dates.length > 0" id="bmi-chart" ref="bmiChartRef" class="chart"></div>
            <el-empty v-else description="暂无BMI数据"></el-empty>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>宏量营养素摄入汇总 (近7天)</span>
            </div>
          </template>
          <div v-loading="nutritionLoading" class="chart-placeholder">
            <div v-if="nutritionError" class="chart-error">{{ nutritionError }}</div>
            <div v-else-if="nutritionSummary && nutritionSummary.labels.length > 0" id="nutrition-chart" ref="nutritionChartRef" class="chart"></div>
            <el-empty v-else description="暂无营养素数据"></el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>每日卡路里摄入与消耗 (近7天)</span>
            </div>
          </template>
          <div v-loading="dailyCaloriesLoading" class="chart-placeholder">
            <div v-if="dailyCaloriesError" class="chart-error">{{ dailyCaloriesError }}</div>
            <div v-else-if="dailyCalorieSummary && dailyCalorieSummary.dates.length > 0" id="daily-calories-chart" ref="dailyCaloriesChartRef" class="chart"></div>
            <el-empty v-else description="暂无每日卡路里数据"></el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- AI 聊天助手按钮和弹窗 -->
    <el-button 
      type="primary" 
      circle 
      size="large" 
      style="position: fixed; bottom: 40px; right: 40px; z-index: 100;"
      @click="showChatBotDialog = true"
    >
      <el-icon><ChatDotRound /></el-icon>
    </el-button>

    <el-dialog 
      v-model="showChatBotDialog" 
      title="AI 聊天助手" 
      width="50%" 
      destroy-on-close 
      :close-on-click-modal="false"
      class="ai-chat-dialog"
    >
      <ChatBot />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showChatBotDialog = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watchEffect } from 'vue';
import { ElCard, ElRow, ElCol, ElMessage, ElEmpty } from 'element-plus';
import { ChatDotRound } from '@element-plus/icons-vue';
import * as echarts from 'echarts';
import api from '@/api/index.js';
import ChatBot from '../components/ChatBot.vue';
import { useThemeStore } from '@/store/theme'; // 导入主题store

const themeStore = useThemeStore(); // 使用主题store
// 监听主题变化，更新图表主题
const updateChartsTheme = () => {
  if (bmiChartInstance) {
    bmiChartInstance.setOption(getUpdatedBmiChartOption());
  }
  if (nutritionChartInstance) {
    nutritionChartInstance.setOption(getUpdatedNutritionChartOption());
  }
  if (dailyCaloriesChartInstance) {
    dailyCaloriesChartInstance.setOption(getUpdatedDailyCaloriesChartOption());
  }
};

// 监听主题变化
watchEffect(() => {
  const currentTheme = themeStore.themeMode;
  updateChartsTheme();
});

// BMI 趋势数据
const bmiLoading = ref(true);
const bmiError = ref(null);
const bmiTrend = ref(null);
const bmiChartRef = ref(null); // Ref for BMI chart DOM element
let bmiChartInstance = null;

// 营养素汇总数据
const nutritionLoading = ref(true);
const nutritionError = ref(null);
const nutritionSummary = ref(null);
const nutritionChartRef = ref(null); // Ref for Nutrition chart DOM element
let nutritionChartInstance = null;

// 每日卡路里摄入与消耗数据
const dailyCaloriesLoading = ref(true);
const dailyCaloriesError = ref(null);
const dailyCalorieSummary = ref(null);
const dailyCaloriesChartRef = ref(null); // Ref for Daily Calories chart DOM element
let dailyCaloriesChartInstance = null;

const showChatBotDialog = ref(false); // 控制 AI 聊天助手弹窗的可见性

// 获取 BMI 趋势数据
const fetchBmiTrend = async () => {
  bmiLoading.value = true;
  bmiError.value = null;
  try {
    const response = await api.get('/analytics/bmi-trend');
    if (response.data && response.data.code === 200) {
      bmiTrend.value = response.data.data;
    } else {
      throw new Error(response.data.message || '获取BMI趋势失败');
    }
  } catch (err) {
    console.error('获取BMI趋势失败:', err);
    bmiError.value = err.message || '获取BMI趋势时发生网络错误';
    ElMessage.error(bmiError.value);
  } finally {
    bmiLoading.value = false;
  }
};

// 获取更新后的BMI图表配置
const getUpdatedBmiChartOption = () => {
  if (!bmiTrend.value || bmiTrend.value.dates.length === 0) {
    return {};
  }
  
  const textColor = themeStore.themeMode === 'dark' ? '#e0e0e0' : '#333333';
  const lineColor = themeStore.themeMode === 'dark' ? '#79bbff' : '#409eff';
  
  return {
    backgroundColor: 'transparent',
    textStyle: {
      color: textColor
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: bmiTrend.value.dates,
      axisLine: {
        lineStyle: {
          color: textColor
        }
      },
      axisLabel: {
        color: textColor
      }
    },
    yAxis: {
      type: 'value',
      name: 'BMI值',
      axisLine: {
        lineStyle: {
          color: textColor
        }
      },
      axisLabel: {
        color: textColor
      },
      splitLine: {
        lineStyle: {
          color: themeStore.themeMode === 'dark' ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)'
        }
      }
    },
    series: [
      {
        name: 'BMI',
        type: 'line',
        smooth: true,
        data: bmiTrend.value.bmiValues,
        itemStyle: {
          color: lineColor
        },
        lineStyle: {
          color: lineColor
        },
        markPoint: {
          data: [
            { type: 'max', name: '最大值' },
            { type: 'min', name: '最小值' }
          ]
        },
        markLine: {
          data: [{ type: 'average', name: '平均值' }]
        }
      }
    ]
  };
};

// 渲染 BMI 折线图
const renderBmiChart = () => {
  if (!bmiChartRef.value || !bmiTrend.value || bmiTrend.value.dates.length === 0) {
    return;
  }

  if (bmiChartInstance) {
    bmiChartInstance.dispose(); // 销毁旧实例，防止重复渲染
  }
  bmiChartInstance = echarts.init(bmiChartRef.value);
  const option = getUpdatedBmiChartOption();
  bmiChartInstance.setOption(option);
};

// 获取营养素汇总数据
const fetchNutritionSummary = async () => {
  nutritionLoading.value = true;
  nutritionError.value = null;
  try {
    const response = await api.get('/analytics/nutrition-summary');
    if (response.data && response.data.code === 200) {
      nutritionSummary.value = response.data.data;
    } else {
      throw new Error(response.data.message || '获取营养素汇总失败');
    }
  } catch (err) {
    console.error('获取营养素汇总失败:', err);
    nutritionError.value = err.message || '获取营养素汇总时发生网络错误';
    ElMessage.error(nutritionError.value);
  } finally {
    nutritionLoading.value = false;
  }
};

// 获取更新后的营养素图表配置
const getUpdatedNutritionChartOption = () => {
  if (!nutritionSummary.value || nutritionSummary.value.labels.length === 0) {
    return {};
  }
  
  const textColor = themeStore.themeMode === 'dark' ? '#e0e0e0' : '#333333';
  const pieData = nutritionSummary.value.labels.map((label, index) => ({
    name: label,
    value: nutritionSummary.value.values[index]
  }));
  
  return {
    backgroundColor: 'transparent',
    textStyle: {
      color: textColor
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: nutritionSummary.value.labels,
      textStyle: {
        color: textColor
      }
    },
    series: [
      {
        name: '营养素摄入',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center',
          color: textColor
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '20',
            fontWeight: 'bold',
            color: textColor
          }
        },
        labelLine: {
          show: false
        },
        data: pieData
      }
    ]
  };
};

// 渲染营养素饼图
const renderNutritionChart = () => {
  if (!nutritionChartRef.value || !nutritionSummary.value || nutritionSummary.value.labels.length === 0) {
    return;
  }

  if (nutritionChartInstance) {
    nutritionChartInstance.dispose();
  }
  nutritionChartInstance = echarts.init(nutritionChartRef.value);
  const option = getUpdatedNutritionChartOption();
  nutritionChartInstance.setOption(option);
};

// 获取每日卡路里摄入与消耗数据
const fetchDailyCalorieSummary = async () => {
  dailyCaloriesLoading.value = true;
  dailyCaloriesError.value = null;
  try {
    const response = await api.get('/analytics/daily-calorie-summary');
    if (response.data && response.data.code === 200) {
      dailyCalorieSummary.value = response.data.data;
    } else {
      throw new Error(response.data.message || '获取每日卡路里汇总失败');
    }
  } catch (err) {
    console.error('获取每日卡路里汇总失败:', err);
    dailyCaloriesError.value = err.message || '获取每日卡路里汇总时发生网络错误';
    ElMessage.error(dailyCaloriesError.value);
  } finally {
    dailyCaloriesLoading.value = false;
  }
};

// 获取更新后的每日卡路里图表配置
const getUpdatedDailyCaloriesChartOption = () => {
  if (!dailyCalorieSummary.value || dailyCalorieSummary.value.dates.length === 0) {
    return {};
  }
  
  const textColor = themeStore.themeMode === 'dark' ? '#e0e0e0' : '#333333';
  const consumedColor = themeStore.themeMode === 'dark' ? '#79bbff' : '#409eff';
  const burnedColor = themeStore.themeMode === 'dark' ? '#95d475' : '#67c23a';
  
  return {
    backgroundColor: 'transparent',
    textStyle: {
      color: textColor
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['摄入卡路里', '消耗卡路里'],
      textStyle: {
        color: textColor
      }
    },
    xAxis: {
      type: 'category',
      data: dailyCalorieSummary.value.dates,
      axisLine: {
        lineStyle: {
          color: textColor
        }
      },
      axisLabel: {
        color: textColor
      }
    },
    yAxis: {
      type: 'value',
      name: '卡路里',
      axisLine: {
        lineStyle: {
          color: textColor
        }
      },
      axisLabel: {
        color: textColor
      },
      splitLine: {
        lineStyle: {
          color: themeStore.themeMode === 'dark' ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.1)'
        }
      }
    },
    series: [
      {
        name: '摄入卡路里',
        type: 'bar',
        stack: 'total',
        data: dailyCalorieSummary.value.consumedCalories,
        itemStyle: {
          color: consumedColor
        }
      },
      {
        name: '消耗卡路里',
        type: 'bar',
        stack: 'total',
        data: dailyCalorieSummary.value.burnedCalories,
        itemStyle: {
          color: burnedColor
        }
      }
    ]
  };
};

// 渲染每日卡路里摄入与消耗折线图/柱状图
const renderDailyCaloriesChart = () => {
  if (!dailyCaloriesChartRef.value || !dailyCalorieSummary.value || dailyCalorieSummary.value.dates.length === 0) {
    return;
  }

  if (dailyCaloriesChartInstance) {
    dailyCaloriesChartInstance.dispose();
  }
  dailyCaloriesChartInstance = echarts.init(dailyCaloriesChartRef.value);
  const option = getUpdatedDailyCaloriesChartOption();
  dailyCaloriesChartInstance.setOption(option);
};

// Watch for data changes and render charts
watchEffect(() => {
  renderBmiChart();
});

watchEffect(() => {
  renderNutritionChart();
});

watchEffect(() => {
  renderDailyCaloriesChart();
});

// 监听窗口大小变化，调整图表大小
const handleResize = () => {
  if (bmiChartInstance) bmiChartInstance.resize();
  if (nutritionChartInstance) nutritionChartInstance.resize();
  if (dailyCaloriesChartInstance) dailyCaloriesChartInstance.resize();
};

onMounted(() => {
  fetchBmiTrend();
  fetchNutritionSummary();
  fetchDailyCalorieSummary();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  if (bmiChartInstance) {
    bmiChartInstance.dispose();
  }
  if (nutritionChartInstance) {
    nutritionChartInstance.dispose();
  }
  if (dailyCaloriesChartInstance) {
    dailyCaloriesChartInstance.dispose();
  }
  window.removeEventListener('resize', handleResize);
});
</script>

<style scoped>
.statistics-container {
  padding: 20px;
  background-color: var(--bg-color);
  color: var(--text-color);
  min-height: calc(100vh - 84px); /* Adjust based on header/footer height */
  transition: background-color 0.3s ease, color 0.3s ease;
}

h1 {
  font-size: 28px;
  color: var(--text-color);
  margin-bottom: 20px;
  text-align: center;
}

.chart-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border: 1px solid var(--border-color);
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-color);
}

.chart-placeholder {
  min-height: 300px; /* Ensure charts have a minimum height */
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.chart {
  width: 100%;
  height: 300px;
}

.chart-error {
  color: #f56c6c;
  text-align: center;
  font-size: 16px;
}

/* 适配Element Plus组件主题 */
:deep(.el-card__header) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border-bottom: 1px solid var(--border-color);
}

:deep(.el-card__body) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
}

:deep(.el-empty__description) {
  color: var(--text-color);
}

:deep(.el-dialog) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
}

:deep(.el-dialog__header) {
  color: var(--text-color);
}

:deep(.el-dialog__body) {
  color: var(--text-color);
}

:deep(.el-dialog__footer) {
  color: var(--text-color);
}

/* Optional: Adjust for responsiveness */
@media (max-width: 768px) {
  .el-col {
    width: 100%;
  }
}
</style>