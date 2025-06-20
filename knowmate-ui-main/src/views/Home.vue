<!-- src/views/Home.vue -->
<template>
  <div class="home-container">
    <n-space vertical size="large">
      <div class="custom-components">
        <h2>我的日志与动态</h2>
        <div class="debug-panel">
          <n-button @click="testMealLogAPI">测试饮食日志API</n-button>
          <n-button @click="testExerciseLogAPI">测试运动日志API</n-button>
          <n-button @click="checkToken">检查Token</n-button>
        </div>
        <my-logs />
        <post-creator @refresh="refreshPosts" />
        <post-list ref="postList" />
      </div>
      
      <n-space justify="end">
        <n-button type="primary" @click="goToCreatePost">发帖</n-button>
      </n-space>

      <n-list bordered>
        <template #header> 帖子列表 </template>

        <n-list-item v-for="post in posts" :key="post.id">
          <n-thing :title="post.title">
            <template #description>
              <n-space size="small" class="post-meta">
                <span>作者：{{ post.author }}</span>
                <span>点赞：{{ post.likes }}</span>
                <span>评论：{{ post.comments }}</span>
              </n-space>
            </template>
            <!-- Optional: Add post content snippet here -->
          </n-thing>
        </n-list-item>

        <template #footer>
          <n-space justify="center">
            <n-pagination :page="currentPage" :item-count="totalPosts" :page-size="pageSize" show-quick-jumper @update:page="handlePageChange">
              <template #prev> 上一页 </template>
              <template #next> 下一页 </template>
            </n-pagination>
          </n-space>
        </template>
      </n-list>
    </n-space>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { NButton, NList, NListItem, NThing, NPagination, NSpace, useMessage } from "naive-ui";
import MyLogs from '@/components/MyLogs.vue';
import PostCreator from '@/components/PostCreator.vue';
import PostList from '@/components/PostList.vue';
import axios from '../utils/axios';

const message = useMessage();
const router = useRouter();
const postList = ref();

const allPosts = ref([
  { id: 1, title: "【标题 1】Vue 3 Composition API 详解", author: "张三", likes: 15, comments: 5 },
  { id: 2, title: "【标题 2】Naive UI 使用心得", author: "李四", likes: 22, comments: 8 },
  { id: 3, title: "【标题 3】Vite 构建工具的优势", author: "王五", likes: 30, comments: 12 },
  { id: 4, title: "【标题 4】Pinia 状态管理实践", author: "赵六", likes: 10, comments: 3 },
  { id: 5, title: "【标题 5】前端性能优化技巧", author: "孙七", likes: 45, comments: 15 },
]);

const currentPage = ref(1);
const pageSize = ref(3);
const totalPosts = computed(() => allPosts.value.length);

const posts = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return allPosts.value.slice(start, end);
});

const goToCreatePost = () => {
  router.push('/new-post');
};

const handlePageChange = (page) => {
  currentPage.value = page;
  console.log(`Navigated to page ${page}`);
};

function refreshPosts() {
  if (postList.value) {
    postList.value.fetchPosts();
  }
}

// 测试饮食日志API
const testMealLogAPI = async () => {
  try {
    console.log('测试饮食日志API...');
    const token = localStorage.getItem('authToken');
    console.log('当前Token:', token);
    
    const response = await axios.get('/api/meal-log');
    console.log('饮食日志API响应:', response.data);
    message.success('饮食日志API测试成功');
  } catch (error) {
    console.error('饮食日志API测试失败:', error);
    message.error('饮食日志API测试失败: ' + (error.response?.data?.message || error.message));
  }
};

// 测试运动日志API
const testExerciseLogAPI = async () => {
  try {
    console.log('测试运动日志API...');
    const token = localStorage.getItem('authToken');
    console.log('当前Token:', token);
    
    const response = await axios.get('/api/exerciselog');
    console.log('运动日志API响应:', response.data);
    message.success('运动日志API测试成功');
  } catch (error) {
    console.error('运动日志API测试失败:', error);
    message.error('运动日志API测试失败: ' + (error.response?.data?.message || error.message));
  }
};

// 检查Token
const checkToken = () => {
  const token = localStorage.getItem('authToken');
  if (token) {
    try {
      const tokenParts = token.split('.');
      if (tokenParts.length === 3) {
        const payload = JSON.parse(atob(tokenParts[1]));
        console.log('Token解析结果:', payload);
        message.success('Token有效，用户ID: ' + payload.userId);
      } else {
        message.error('Token格式无效');
      }
    } catch (error) {
      console.error('Token解析失败:', error);
      message.error('Token解析失败: ' + error.message);
    }
  } else {
    message.error('未找到Token，请先登录');
  }
};

onMounted(() => {
  // 初始化时刷新一次动态列表
  refreshPosts();
});
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.custom-components {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.custom-components h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #2080f0;
  text-align: center;
}

.debug-panel {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  justify-content: center;
}
</style>
