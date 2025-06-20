<template>
  <div class="chatbot-inner-container">
    <div class="chat-messages" ref="messagesContainer">
      <div v-for="(message, index) in messages" :key="index" :class="['message-item', message.sender]">
        <div class="message-bubble">
          <span v-if="message.sender === 'user'">{{ message.content }}</span>
          <MarkdownRenderer v-else :source="message.content" />
        </div>
      </div>
    </div>
    <div class="chat-input-area">
      <el-input
        v-model="userPrompt"
        placeholder="和AI聊聊你的饮食健康..."
        @keyup.enter="sendMessage"
        clearable
        size="large"
      >
        <template #append>
          <el-button :icon="Promotion" @click="sendMessage" :loading="isSending" :disabled="!userPrompt.trim()"></el-button>
        </template>
      </el-input>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, watch, onMounted, computed } from 'vue';
import { ElButton, ElInput, ElMessage } from 'element-plus';
import { Promotion } from '@element-plus/icons-vue';
import axios from '@/utils/axios'; // Assuming axios is configured with base URL and interceptors
import MarkdownRenderer from './MarkdownRenderer.vue'; // Ensure this component exists for rendering AI responses

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  pageContext: {
    type: String,
    default: ''
  }
});

const emits = defineEmits(['update:visible']);

const userPrompt = ref('');
const messages = ref([]); // { sender: 'user' | 'ai', content: '...' }
const isSending = ref(false);
const messagesContainer = ref(null);

watch(() => props.visible, (newVal) => {
  if (newVal) {
    nextTick(() => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
      }
    });
  }
});

// Scroll to bottom of messages when they update
watch(messages.value, () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
}, { deep: true });

onMounted(() => {
  // Add an initial welcome message from AI
  messages.value.push({
    sender: 'ai',
    content: '你好！我是你的智能饮食助手，有什么可以帮助你的吗？'
  });
});

const sendMessage = async () => {
  const prompt = userPrompt.value.trim();
  if (!prompt) {
    ElMessage.warning('请输入您的问题！');
    return;
  }

  messages.value.push({ sender: 'user', content: prompt });
  userPrompt.value = '';
  isSending.value = true;

  try {
    const payload = {
      prompt: prompt,
      context: props.pageContext // Add page context here
    };
    const res = await axios.post('/api/ai/chat', payload);
    const aiResponseText = res.data.answer; // 确保从正确的路径获取AI回复
    messages.value.push({ sender: 'ai', content: aiResponseText });

    // 保存AI回复
    await axios.post('/api/chatlog/save', { message: aiResponseText, isUser: false });

  } catch (error) {
    console.error('AI Chat Error:', error);
    messages.value.push({ sender: 'ai', content: '与AI助手通信时发生错误，请稍后再试。' });
    ElMessage.error('与AI助手通信失败！');
  } finally {
    isSending.value = false;
  }
};
</script>

<style scoped>
.chatbot-inner-container {
  display: flex;
  flex-direction: column;
  height: 100%; /* Take full height of parent dialog */
}

.chat-messages {
  flex-grow: 1;
  overflow-y: auto;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  background-color: #f5f5f5;
  border-radius: 8px;
  min-height: 200px; /* Minimum height for scrollable area */
  max-height: calc(100vh - 200px); /* Adjust based on drawer size */
}

.message-item {
  display: flex;
  max-width: 80%;
}

.message-item.user {
  align-self: flex-end;
}

.message-item.ai {
  align-self: flex-start;
}

.message-bubble {
  padding: 10px 15px;
  border-radius: 18px;
  line-height: 1.5;
  word-wrap: break-word;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.message-item.user .message-bubble {
  background-color: #409EFF; /* El-Button primary color */
  color: white;
  border-bottom-right-radius: 2px;
}

.message-item.ai .message-bubble {
  background-color: #FFFFFF;
  color: #303133;
  border: 1px solid #EBEEF5; /* El-Card border color */
  border-bottom-left-radius: 2px;
}

.chat-input-area {
  padding-top: 15px;
  border-top: 1px solid #EBEEF5;
  background-color: #FFFFFF;
}
</style> 