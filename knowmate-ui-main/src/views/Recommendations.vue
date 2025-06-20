<template>
  <div class="recommendations-page">
    <el-row type="flex" justify="space-between" align="middle" style="margin-bottom: 20px;">
      <h1>智能饮食推荐</h1>
      <el-button 
        type="info" 
        :icon="ChatDotRound" 
        circle 
        class="chat-button" 
        @click="showChatBotDialog = true"
        aria-label="打开AI聊天助手"
      />
    </el-row>

    <div v-if="recommendation.recommendationText" class="recommendation-alert">
      <el-alert
        type="success"
        :title="recommendation.recommendationText"
        show-icon
        :closable="false"
      />
    </div>

    <el-card v-if="recommendation.suggestedFoods && recommendation.suggestedFoods.length > 0" class="foods-card">
      <template #header>
        <div class="card-header">
          <span>推荐食物列表</span>
        </div>
      </template>
      <el-table :data="recommendation.suggestedFoods" style="width: 100%" stripe border class="food-table">
        <el-table-column prop="name" label="食物名称" width="150" />
        <el-table-column prop="calories" label="热量 (kcal)" width="120" />
        <el-table-column prop="protein" label="蛋白质 (g)" width="120" />
        <el-table-column prop="fat" label="脂肪 (g)" width="120" />
        <el-table-column prop="carbohydrate" label="碳水化合物 (g)" width="140" />
        <el-table-column prop="category" label="类别" width="100" />
        <el-table-column prop="flavor" label="口味偏好" width="120" />
        <el-table-column prop="servingSize" label="份量" width="100" />
        <el-table-column prop="reason" label="推荐理由" min-width="200" />
      </el-table>
    </el-card>

    <el-empty v-else description="暂无推荐食物，请检查您的健康档案或饮食/运动日志。" />

    <!-- AI 聊天助手弹窗 -->
    <el-dialog 
      v-model="showChatBotDialog" 
      title="AI 智能助手" 
      width="50%" 
      destroy-on-close 
      :close-on-click-modal="false"
      class="ai-chat-dialog"
    >
      <ChatBot :visible="showChatBotDialog" @update:visible="showChatBotDialog = $event" :page-context="'这是智能饮食推荐页面。'" />
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
import axios from '@/utils/axios'; // Assuming axios is configured with base URL and interceptors
import { ElMessage, ElButton, ElDialog, ElRow } from 'element-plus'; // Add ElButton and ElDialog to imports
import { ChatDotRound } from '@element-plus/icons-vue'; // Import the icon
import ChatBot from '@/components/ChatBot.vue'; // Import ChatBot component
import { useThemeStore } from '@/store/theme'; // 导入主题store

const themeStore = useThemeStore(); // 使用主题store
const recommendation = ref({});
const showChatBotDialog = ref(false); // Define reactive state for dialog visibility

const fetchRecommendations = async () => {
  try {
    const res = await axios.get('/api/recommendation');
    if (res.data.code === 200) {
      recommendation.value = res.data.data; // 直接将后端返回的 RecommendationDTO 赋值给 recommendation
    } else {
      ElMessage.error('获取推荐失败：' + res.data.message);
    }
  } catch (error) {
    console.error('获取推荐请求失败：', error);
    ElMessage.error('获取推荐失败，请稍后再试或检查网络连接。');
  }
};

onMounted(() => {
  fetchRecommendations();
});
</script>

<style scoped>
.recommendations-page {
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

.recommendation-alert {
  margin-bottom: 20px;
}

.foods-card {
  margin-top: 20px;
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border: 1px solid var(--border-color);
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-color);
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

:deep(.el-alert) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border: 1px solid var(--border-color);
}

:deep(.el-alert__title) {
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
</style> 