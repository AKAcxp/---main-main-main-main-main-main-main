<template>
  <div class="exercise-log-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>运动日志</span>
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

      <!-- Placeholder for Exercise Log Content -->
      <div class="exercise-log-content">
        <el-empty description="此为运动日志模块，待开发中..." />
        <!-- You would integrate your exercise logging form and table here -->
      </div>
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
      <ChatBot :visible="showChatBotDialog" @update:visible="showChatBotDialog = $event" :page-context="'这是运动日志页面。'" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showChatBotDialog = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElButton, ElCard, ElDialog, ElEmpty } from 'element-plus';
import { ChatDotRound } from '@element-plus/icons-vue';
import ChatBot from '@/components/ChatBot.vue'; // Ensure the path is correct

const showChatBotDialog = ref(false);
</script>

<style scoped>
.exercise-log-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exercise-log-content {
  min-height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
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