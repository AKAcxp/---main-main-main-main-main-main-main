<template>
  <div class="meal-log-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>饮食日志</span>
          <el-button 
            type="info" 
            :icon="ChatDotRound" 
            circle 
            class="chat-button" 
            @click="showChatBotDialog = true"
            aria-label="打开AI聊天助手"
          />
        </div>
      </template>

      <!-- 添加饮食记录表单 -->
      <el-form :inline="true" @submit.prevent="addLog" class="meal-form">
        <el-form-item label="食物名称">
          <el-input v-model="form.food" placeholder="如：米饭" />
        </el-form-item>

        <el-form-item label="热量（kcal）">
          <el-input-number v-model="form.calories" :min="1" />
        </el-form-item>

        <el-form-item label="时间">
          <el-date-picker
            v-model="form.mealTime"
            type="datetime"
            placeholder="选择时间"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="addLog">添加记录</el-button>
        </el-form-item>
      </el-form>

      <!-- 显示已有记录 -->
      <el-table :data="logs" style="width: 100%" class="meal-table">
        <el-table-column prop="mealTime" label="时间" width="180" />
        <el-table-column prop="food" label="食物" />
        <el-table-column prop="calories" label="热量 (kcal)" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="danger" size="small" @click="delLog(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- AI 聊天助手弹窗 -->
    <el-dialog 
      v-model="showChatBotDialog" 
      title="AI 智能助手" 
      width="50%" 
      destroy-on-close 
      :close-on-click-modal="false"
      class="ai-chat-dialog"
    >
      <ChatBot :visible="showChatBotDialog" @update:visible="showChatBotDialog = $event" :page-context="'这是饮食日志页面。'" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showChatBotDialog = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElButton, ElCard, ElDialog, ElEmpty, ElMessage } from 'element-plus';
import { ChatDotRound } from '@element-plus/icons-vue';
import ChatBot from '@/components/ChatBot.vue';
import { useThemeStore } from '@/store/theme';
import axios from '@/utils/axios';

const themeStore = useThemeStore();
const showChatBotDialog = ref(false);
const logs = ref([]);
const form = ref({
  food: '',
  calories: 0,
  mealTime: ''
});

// 加载日志数据
const loadLogs = async () => {
  try {
    const res = await axios.get('/api/meallog');
    logs.value = res.data.data || [];
  } catch (e) {
    console.error('加载饮食日志失败', e);
    ElMessage.error('加载饮食日志失败');
  }
};

// 添加记录
const addLog = async () => {
  if (!form.value.food || !form.value.calories || !form.value.mealTime) {
    ElMessage.warning('请填写所有饮食记录信息');
    return;
  }

  try {
    await axios.post('/api/meallog', {
      ...form.value
    });
    form.value = { // 重置表单
      food: '',
      calories: 0,
      mealTime: ''
    };
    loadLogs(); // 重新加载数据
    ElMessage.success('饮食记录添加成功');
  } catch (error) {
    console.error('添加饮食记录失败:', error);
    ElMessage.error('添加饮食记录失败');
  }
};

// 删除记录
const delLog = async (id) => {
  try {
    await axios.delete(`/api/meallog/${id}`);
    loadLogs(); // 重新加载数据
    ElMessage.success('饮食记录删除成功');
  } catch (error) {
    console.error('删除饮食记录失败:', error);
    ElMessage.error('删除饮食记录失败');
  }
};

onMounted(() => {
  loadLogs();
});
</script>

<style scoped>
.meal-log-page {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
  background-color: var(--bg-color);
  color: var(--text-color);
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s ease, color 0.3s ease;
}

h1 {
  text-align: center;
  color: var(--text-color);
  margin-bottom: 30px;
}

.meal-form-card,
.meal-log-card {
  margin-bottom: 20px;
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border: 1px solid var(--border-color);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  color: var(--text-color);
}

.header-actions {
  display: flex;
  align-items: center;
}

.empty-data {
  padding: 30px 0;
  text-align: center;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

/* 针对表格内容的样式调整 */
:deep(.el-table) {
  --el-table-row-hover-bg-color: var(--table-row-hover-bg-color);
  --el-table-header-bg-color: var(--table-header-bg-color);
  --el-table-border-color: var(--border-color);
  background-color: var(--card-bg-color);
}

:deep(.el-table th.el-table__cell) {
  background-color: var(--table-header-bg-color) !important;
  color: var(--text-color) !important;
  border-bottom: 1px solid var(--border-color);
}

:deep(.el-table td.el-table__cell) {
  color: var(--text-color);
  border-bottom: 1px solid var(--border-color);
}

:deep(.el-table--border .el-table__inner-wrapper) {
  border-color: var(--border-color);
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background-color: var(--table-striped-row-bg-color);
}

:deep(.el-table__body tr.hover-row > td.el-table__cell) {
  background-color: var(--table-row-hover-bg-color);
}

:deep(.el-empty) {
  margin-top: 50px;
  color: var(--text-color);
}

/* 响应式布局调整 */
@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-actions {
    margin-top: 10px;
    width: 100%;
    display: flex;
    flex-direction: column;
  }
  
  .header-actions .el-date-picker {
    margin-bottom: 10px;
    margin-right: 0;
    width: 100% !important;
  }
}

.meal-log-container {
  padding: 20px;
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: background-color 0.3s ease, color 0.3s ease;
}

.box-card {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border: 1px solid var(--border-color);
}

.chat-button {
  width: 40px;
  height: 40px;
  font-size: 20px;
}

.ai-chat-dialog .el-dialog__body {
  height: 60vh; /* Adjust as needed */
  display: flex;
  flex-direction: column;
  padding: 0 20px 20px 20px;
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

:deep(.el-form-item__label) {
  color: var(--text-color);
}

:deep(.el-input__inner) {
  background-color: var(--input-bg-color);
  color: var(--text-color);
  border-color: var(--border-color);
}

:deep(.el-input-number__decrease),
:deep(.el-input-number__increase) {
  background-color: var(--input-bg-color);
  color: var(--text-color);
  border-color: var(--border-color);
}

:deep(.el-table) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border-color: var(--border-color);
}

:deep(.el-table th.el-table__cell) {
  background-color: var(--table-header-bg-color);
  color: var(--text-color);
  border-bottom: 1px solid var(--border-color);
}

:deep(.el-table tr) {
  background-color: var(--card-bg-color);
}

:deep(.el-table td.el-table__cell) {
  border-bottom: 1px solid var(--border-color);
  color: var(--text-color);
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background-color: var(--table-striped-row-bg-color);
}

:deep(.el-table__body tr.hover-row > td.el-table__cell) {
  background-color: var(--table-row-hover-bg-color);
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
</style> 