<template>
  <div class="post-list">
    <h3>我的动态</h3>
    
    <div v-if="loading" class="loading">
      <el-skeleton :rows="3" animated />
    </div>
    
    <div v-else-if="posts.length === 0" class="empty-posts">
      <el-empty description="暂无动态" />
    </div>
    
    <el-row v-else :gutter="20">
      <el-col v-for="post in posts" :key="post.id" :xs="24" :sm="12" :md="8">
        <el-card class="post-card">
          <div class="post-header">
            <h4>{{ post.title || '无标题' }}</h4>
            <span class="time">{{ formatTime(post.createdAt) }}</span>
          </div>
          
          <p class="post-content">{{ post.content }}</p>
          
          <div v-if="hasImages(post)" class="post-images">
            <el-image 
              v-for="url in post.mediaUrls" 
              :key="url" 
              :src="url" 
              :preview-src-list="post.mediaUrls"
              fit="cover"
            />
          </div>
          
          <video 
            v-else-if="hasVideos(post)" 
            controls 
            :src="post.mediaUrls[0]" 
            class="post-video"
          />
        </el-card>
      </el-col>
    </el-row>
    
    <div v-if="posts.length > 0" class="load-more">
      <el-button :loading="loadingMore" @click="loadMore">加载更多</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '../utils/axios';
import { ElMessage } from 'element-plus';

const posts = ref([]);
const loading = ref(true);
const loadingMore = ref(false);
const currentPage = ref(1);
const pageSize = ref(6);
const hasMore = ref(true);

// 判断是否有图片
const hasImages = (post) => {
  return post.mediaUrls && post.mediaUrls.length > 0 && 
    post.mediaUrls[0].match(/\.(jpeg|jpg|gif|png)$/i) !== null;
};

// 判断是否有视频
const hasVideos = (post) => {
  return post.mediaUrls && post.mediaUrls.length > 0 && 
    !post.mediaUrls[0].match(/\.(jpeg|jpg|gif|png)$/i);
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  
  const date = new Date(time);
  const now = new Date();
  const diff = now - date;
  
  // 如果是今天发布的，显示"xx小时前"或"xx分钟前"
  if (diff < 24 * 60 * 60 * 1000) {
    const hours = Math.floor(diff / (60 * 60 * 1000));
    if (hours > 0) {
      return `${hours}小时前`;
    }
    const minutes = Math.floor(diff / (60 * 1000));
    if (minutes > 0) {
      return `${minutes}分钟前`;
    }
    return '刚刚';
  }
  
  // 否则显示日期
  return date.toLocaleDateString();
};

// 获取动态列表
const fetchPosts = async (page = 1, append = false) => {
  if (page === 1) {
    loading.value = true;
  } else {
    loadingMore.value = true;
  }
  
  try {
    const res = await axios.get('/api/posts/my', {
      params: { page, size: pageSize.value }
    });
    
    if (res.data && res.data.code === 200) {
      // 处理媒体URL，将JSON字符串转换为数组
      const postsData = res.data.data || [];
      
      // 如果返回的数据少于pageSize，说明没有更多数据了
      if (postsData.length < pageSize.value) {
        hasMore.value = false;
      }
      
      const processedPosts = postsData.map(post => {
        try {
          if (post.mediaUrls && typeof post.mediaUrls === 'string') {
            post.mediaUrls = JSON.parse(post.mediaUrls);
          }
        } catch (e) {
          post.mediaUrls = [];
        }
        return post;
      });
      
      if (append) {
        posts.value = [...posts.value, ...processedPosts];
      } else {
        posts.value = processedPosts;
      }
    }
  } catch (error) {
    console.error('获取动态列表失败:', error);
    ElMessage.error('获取动态列表失败，请稍后重试');
  } finally {
    loading.value = false;
    loadingMore.value = false;
  }
};

// 加载更多
const loadMore = () => {
  if (hasMore.value && !loadingMore.value) {
    currentPage.value++;
    fetchPosts(currentPage.value, true);
  }
};

// 暴露方法给父组件调用
defineExpose({
  fetchPosts: () => fetchPosts(1, false)
});

onMounted(() => fetchPosts(1, false));
</script>

<style scoped>
.post-list {
  margin-top: 20px;
}

.post-list h3 {
  margin-bottom: 15px;
  color: #606266;
  font-weight: 500;
}

.loading, .empty-posts {
  padding: 20px 0;
}

.empty-posts {
  display: flex;
  justify-content: center;
}

.post-card {
  margin-bottom: 20px;
  transition: all 0.3s;
}

.post-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.post-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.time {
  font-size: 12px;
  color: #999;
}

.post-content {
  color: #606266;
  margin-bottom: 15px;
  white-space: pre-wrap;
  word-break: break-all;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 5px;
}

.el-image {
  width: 100%;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.post-video {
  width: 100%;
  max-height: 200px;
  border-radius: 4px;
}

.load-more {
  text-align: center;
  margin-top: 20px;
}
</style> 