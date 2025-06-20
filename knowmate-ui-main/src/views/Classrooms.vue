<template>
  <div class="chat-page">
    <el-card class="chat-card">
      <div class="chat-window">
        <div v-for="(msg, index) in messages" :key="index" :class="['message-row', msg.role]">
          <strong>{{ msg.role === 'user' ? '你：' : 'AI：' }}</strong>
          <span>{{ msg.text }}</span>
        </div>
      </div>

      <el-input
        type="textarea"
        v-model="input"
        rows="2"
        placeholder="请输入问题，按 Enter 发送"
        @keyup.enter.native="sendMessage"
        class="chat-input"
      />
      <el-button @click="sendMessage" type="primary" style="margin-top: 10px;" class="send-button">发送</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from '@/utils/axios'
import { ElMessage } from 'element-plus'

const input = ref('')
const messages = ref([])

// 新增：监听输入框内容变化并保存到 localStorage
watch(input, (newVal) => {
  localStorage.setItem('chatDraft', newVal)
})

// 新增：获取聊天历史记录
const fetchChatHistory = async () => {
  try {
    const res = await axios.get('/api/chatlog/history')
    if (res.data.code === 200) {
      messages.value = res.data.data.map(log => ({
        role: log.isUser ? 'user' : 'ai',
        text: log.message,
        createdAt: log.createdAt
      }))
    } else {
      ElMessage.error('获取聊天历史失败：' + res.data.message)
    }
  } catch (e) {
    ElMessage.error('获取聊天历史请求失败：' + e.message)
  }
}

const sendMessage = async () => {
  if (!input.value.trim()) return

  const question = input.value
  messages.value.push({ role: 'user', text: question })
  input.value = ''

  try {
    // 保存用户消息
    await axios.post('/api/chatlog/save', { message: question, isUser: true })

    const res = await axios.post('/api/ai/chat', { prompt: question })
    const aiResponseText = res.data.answer
    messages.value.push({ role: 'ai', text: aiResponseText })

    // 保存AI回复
    await axios.post('/api/chatlog/save', { message: aiResponseText, isUser: false })

  } catch (e) {
    messages.value.push({ role: 'ai', text: '❌ 请求失败：' + e.message })
    ElMessage.error('聊天或保存消息失败：' + e.message)
  }
}

// 页面挂载时调用，获取历史记录和恢复草稿
onMounted(() => {
  fetchChatHistory()
  // 新增：恢复输入框草稿内容
  const savedDraft = localStorage.getItem('chatDraft')
  if (savedDraft) {
    input.value = savedDraft
  }
})
</script>

<style scoped>
.chat-page {
  max-width: 800px;
  margin: auto;
  padding: 30px;
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: background-color 0.3s ease, color 0.3s ease;
}

.chat-card {
  background-color: var(--card-bg-color);
  border: 1px solid var(--border-color);
}

.chat-window {
  min-height: 300px;
  margin-bottom: 10px;
  background: var(--input-bg-color);
  padding: 10px;
  border-radius: 6px;
  border: 1px solid var(--border-color);
  color: var(--text-color);
}

.message-row strong {
  color: var(--text-color);
}

.message-row.user strong,
.message-row.user span {
  color: var(--user-message-color);
}

.message-row.ai strong,
.message-row.ai span {
  color: var(--ai-message-color);
}

.chat-input ::v-deep(.el-textarea__inner) {
  background-color: var(--input-bg-color) !important;
  color: var(--text-color) !important;
  border: 1px solid var(--border-color) !important;
}

.send-button {
}

.chat-card ::v-deep(.el-card__header),
.chat-card ::v-deep(.el-card__body) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border-bottom: 1px solid var(--border-color);
}

.chat-card ::v-deep(.el-card__header) {
  border-bottom: 1px solid var(--border-color);
}
</style>