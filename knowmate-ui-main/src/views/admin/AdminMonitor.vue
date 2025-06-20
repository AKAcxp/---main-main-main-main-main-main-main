<template>
  <div class="admin-monitor">
    <h1>系统监控 (Admin Monitor)</h1>
    <n-space vertical>
      <n-button type="primary" @click="fetchMonitorData" :loading="loading">刷新监控数据</n-button>
      <n-card v-if="monitorData" title="系统健康状况" style="margin-top: 20px;" class="monitor-card">
        <n-descriptions label-placement="left" :column="1">
          <n-descriptions-item label="状态">
            <n-tag :type="monitorData.status === 'UP' ? 'success' : 'error'">
              {{ monitorData.status }}
            </n-tag>
          </n-descriptions-item>
          <n-descriptions-item label="组件">
            <n-list>
              <n-list-item v-for="(component, name) in monitorData.components" :key="name">
                <n-thing :title="name">
                  <template #description>
                    <n-tag :type="component.status === 'UP' ? 'success' : 'error'">
                      {{ component.status }}
                    </n-tag>
                    <p v-if="component.details && component.details.free !== undefined">
                      磁盘空间: {{ (component.details.free / (1024 * 1024 * 1024)).toFixed(2) }} GB 可用
                    </p>
                    <p v-if="component.details && component.details.database">
                      数据库: {{ component.details.database }}
                    </p>
                    <p v-if="component.details && component.details.validationQuery">
                      验证查询: {{ component.details.validationQuery }}
                    </p>
                    <p v-if="component.details && component.details.version">
                      版本: {{ component.details.version }}
                    </p>
                  </template>
                </n-thing>
              </n-list-item>
            </n-list>
          </n-descriptions-item>
        </n-descriptions>
      </n-card>
      <n-alert v-else-if="error" type="error" title="加载失败">
        {{ error }}
      </n-alert>
      <n-empty v-else description="暂无监控数据" />
    </n-space>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { NButton, NSpace, NCard, NDescriptions, NDescriptionsItem, NTag, NList, NListItem, NThing, NAlert, NEmpty, useMessage } from 'naive-ui';
import axios from 'axios';
import { useThemeStore } from '@/store/theme'; // 导入主题store

const themeStore = useThemeStore(); // 使用主题store
const message = useMessage();
const monitorData = ref(null);
const loading = ref(false);
const error = ref(null);

async function fetchMonitorData() {
  loading.value = true;
  error.value = null;
  try {
    const response = await axios.get('/api/admin/monitor');
    if (response.data && response.data.code === 200) {
      monitorData.value = response.data.data;
      message.success('监控数据加载成功！');
    } else {
      error.value = response.data?.message || '获取监控数据失败';
      message.error(error.value);
    }
  } catch (err) {
    console.error('Error fetching monitor data:', err);
    error.value = err.response?.data?.message || err.message || '网络或服务器错误';
    message.error(`加载监控数据失败: ${error.value}`);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchMonitorData();
});
</script>

<style scoped>
.admin-monitor {
  color: var(--text-color);
}

.admin-monitor h1 {
  color: var(--text-color);
}

.monitor-card {
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

:deep(.n-list-item) {
  color: var(--text-color);
}

:deep(.n-thing-header__title) {
  color: var(--text-color);
}

:deep(.n-thing-main) {
  color: var(--text-color);
}

:deep(.n-empty) {
  color: var(--text-color);
}
</style> 