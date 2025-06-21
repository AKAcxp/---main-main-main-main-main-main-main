<template>
  <div class="post-creator">
    <h3>发布动态</h3>
    <el-form @submit.prevent="submit">
      <el-input v-model="form.title" placeholder="请输入标题" />
      <el-input v-model="form.content" type="textarea" rows="4" placeholder="分享你的健康饮食心得..." />
      
      <div class="upload-section">
        <el-upload
          :action="uploadUrl"
          list-type="picture-card"
          :on-success="handleSuccess"
          :on-error="handleError"
          :on-remove="handleRemove"
          :before-upload="beforeUpload"
          :limit="mediaLimit"
          :headers="uploadHeaders"
          name="file"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
        <div v-if="form.mediaUrls.length > 0" class="upload-tip">
          已上传 {{ form.mediaUrls.length }}/{{ mediaLimit }} 张图片
        </div>
      </div>
      
      <el-button type="primary" @click="submit" :loading="submitting">发布</el-button>
      <el-button @click="resetForm">重置</el-button>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import axios from '../utils/axios';

const emit = defineEmits(['refresh']);

const form = reactive({
  title: '',
  content: '',
  mediaUrls: []
});

const mediaLimit = ref(9); // 最多上传9张图片
const submitting = ref(false);
const uploadUrl = import.meta.env.VITE_API_URL ? `${import.meta.env.VITE_API_URL}/api/posts/upload` : '/api/posts/upload';

// 获取上传需要的headers（包含token）
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('authToken');
  return token ? { Authorization: `Bearer ${token}` } : {};
});

function handleSuccess(response, file) {
  console.log('上传成功响应:', response);
  console.log('上传响应类型:', typeof response);
  console.log('上传文件信息:', file);
  
  // 检查响应格式，适应不同的后端返回格式
  let url = null;
  
  if (response.code === 200 && response.data) {
    // 标准格式：{ code: 200, data: 'url', message: 'success' }
    url = response.data;
    console.log('标准格式提取的URL:', url);
  } else if (response.data && response.data.url) {
    // 替代格式：{ data: { url: 'url' } }
    url = response.data.url;
    console.log('替代格式提取的URL:', url);
  } else if (typeof response === 'string') {
    // 简单字符串URL格式
    url = response;
    console.log('字符串格式URL:', url);
  } else {
    // 尝试直接获取响应中的URL
    if (typeof response === 'object') {
      // 遍历对象的所有属性，查找可能的URL
      for (const key in response) {
        if (typeof response[key] === 'string' && 
            (response[key].startsWith('/uploads/') || 
             response[key].startsWith('http'))) {
          url = response[key];
          console.log('从对象中找到URL属性:', key, url);
          break;
        }
      }
    }
  }
  
  if (url) {
    // 确保URL格式正确
    if (!url.startsWith('http://') && !url.startsWith('https://') && !url.startsWith('/uploads/')) {
      url = '/uploads/' + url;
      console.log('格式化后的URL:', url);
    }
    
    form.mediaUrls.push(url);
    console.log('已添加URL到mediaUrls:', url);
    console.log('当前mediaUrls:', form.mediaUrls);
    ElMessage.success('图片上传成功');
    
    // 尝试加载图片验证URL是否有效
    const img = new Image();
    img.onload = () => {
      console.log('图片加载成功:', url);
    };
    img.onerror = () => {
      console.error('图片加载失败:', url);
    };
    const baseUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
    const fullUrl = url.startsWith('http') ? url : (url.startsWith('/') ? baseUrl + url : baseUrl + '/' + url);
    img.src = fullUrl;
  } else {
    ElMessage.error('图片上传失败，返回格式不正确');
    console.error('上传响应格式不正确:', response);
  }
}

function handleError(err, file) {
  console.error('上传失败:', err);
  ElMessage.error('图片上传失败，请稍后重试');
}

function handleRemove(file) {
  // 从mediaUrls中移除对应的URL
  const fileUrl = file.response?.data;
  if (fileUrl) {
    const index = form.mediaUrls.indexOf(fileUrl);
    if (index !== -1) {
      form.mediaUrls.splice(index, 1);
    }
  }
}

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return false;
  }
  
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!');
    return false;
  }
  
  return true;
}

async function submit() {
  if (!form.content.trim()) {
    ElMessage.warning('请输入内容');
    return;
  }
  
  submitting.value = true;
  try {
    const response = await axios.post('/api/posts', form);
    if (response.data.code === 200) {
      ElMessage.success('动态发布成功');
      resetForm();
      emit('refresh');
    } else {
      ElMessage.error(response.data.message || '发布失败');
    }
  } catch (error) {
    console.error('发布动态出错:', error);
    ElMessage.error('发布失败，请稍后重试');
  } finally {
    submitting.value = false;
  }
}

function resetForm() {
  form.title = '';
  form.content = '';
  form.mediaUrls = [];
}
</script>

<style scoped>
.post-creator {
  margin-bottom: 30px;
}

.post-creator h3 {
  margin-bottom: 15px;
  color: #606266;
  font-weight: 500;
}

.el-form {
  margin-top: 15px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.el-input {
  margin-bottom: 15px;
}

.upload-section {
  margin: 15px 0;
}

.upload-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
}

.el-button {
  margin-top: 15px;
  margin-right: 10px;
}
</style> 