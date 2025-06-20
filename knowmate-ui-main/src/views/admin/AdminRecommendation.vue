<template>
  <div class="admin-recommendation">
    <h1>推荐引擎监控 (Admin Recommendation Monitor)</h1>
    <n-space vertical>
      <n-button type="primary" @n-click="fetchRecommendationData" :loading="loading">刷新监控数据</n-button>
      <n-card v-if="recommendationData" title="推荐引擎状况" style="margin-top: 20px;" class="recommendation-card">
        <n-descriptions label-placement="left" :column="1">
          <n-descriptions-item label="总调用次数">
            {{ recommendationData.totalCalls || 0 }}
          </n-descriptions-item>
          <n-descriptions-item label="平均响应时间 (ms)">
            {{ recommendationData.averageResponseTimeMs !== undefined ? recommendationData.averageResponseTimeMs.toFixed(2) : 'N/A' }}
          </n-descriptions-item>
          <n-descriptions-item label="上次更新时间">
            {{ recommendationData.lastUpdated || 'N/A' }}
          </n-descriptions-item>
          <n-descriptions-item label="健康状态">
            <n-tag :type="recommendationData.status === 'OK' ? 'success' : 'error'">
              {{ recommendationData.status || 'UNKNOWN' }}
            </n-tag>
          </n-descriptions-item>
          <n-descriptions-item label="错误日志 (最新)">
            <n-code :code="recommendationData.latestErrorLog || '无'" language="text" word-wrap />
          </n-descriptions-item>
        </n-descriptions>
      </n-card>
      <n-alert v-else-if="error" type="error" title="加载失败">
        {{ error }}
      </n-alert>
      <n-empty v-else description="暂无推荐引擎监控数据" />
    </n-space>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { NButton, NSpace, NCard, NDescriptions, NDescriptionsItem, NTag, NAlert, NEmpty, NCode, useMessage } from 'naive-ui';
import axios from 'axios';
import { useThemeStore } from '@/store/theme'; // 导入主题store

const themeStore = useThemeStore(); // 使用主题store
const message = useMessage();
const recommendationData = ref(null);
const loading = ref(false);
const error = ref(null);

async function fetchRecommendationData() {
  loading.value = true;
  error.value = null;
  try {
    const response = await axios.get('/api/admin/recommendation/metrics');
    if (response.data && response.data.code === 200) {
      const backendMetrics = response.data.data;
      recommendationData.value = {
        totalCalls: backendMetrics.totalInvocations,
        averageResponseTimeMs: backendMetrics.averageResponseTimeMs,
        lastUpdated: backendMetrics.lastProcessedTimestamp,
        status: 'OK', // 假设如果能成功获取到指标，则状态为正常
        latestErrorLog: '无', // 该API不提供错误日志，暂设为"无"
      };
      message.success('推荐引擎监控数据加载成功！');
    } else {
      error.value = response.data?.message || '获取推荐引擎监控数据失败';
      message.error(error.value);
    }
  } catch (err) {
    console.error('Error fetching recommendation data:', err);
    error.value = err.response?.data?.message || err.message || '网络或服务器错误';
    message.error(`加载推荐引擎监控数据失败: ${error.value}`);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchRecommendationData();
});
</script>

<style scoped>
.admin-recommendation {
  color: var(--text-color);
}

.admin-recommendation h1 {
  color: var(--text-color);
}

.recommendation-card {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border: 1px solid var(--border-color);
}

/* 适配Naive UI组件主题 */
:deep(.n-descriptions-table-header) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
}

:deep(.n-descriptions-table-content) {
  color: var(--text-color);
}

:deep(.n-code) {
  background-color: var(--input-bg-color);
  color: var(--text-color);
}

:deep(.n-empty) {
  color: var(--text-color);
}
</style> 