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
            <div v-for="(url, index) in getValidMediaUrls(post)" :key="index">
              <el-image 
                :src="getFullImageUrl(url)" 
                fit="cover"
                :preview-src-list="getImagePreviewList(post)"
                @error="handleImageError"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                    <p>图片加载失败</p>
                  </div>
                </template>
              </el-image>
            </div>
          </div>
          
          <video 
            v-else-if="hasVideos(post)" 
            controls 
            :src="getFullImageUrl(post.mediaUrls[0])" 
            class="post-video"
          />
        </el-card>
      </el-col>
    </el-row>
    
    <div v-if="hasMore && !loading" class="load-more">
      <el-button :loading="loadingMore" @click="loadMore">加载更多</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '../utils/axios';
import { ElMessage } from 'element-plus';
import { Picture } from '@element-plus/icons-vue';

const posts = ref([]);
const loading = ref(true);
const loadingMore = ref(false);
const hasMore = ref(true);
const currentPage = ref(1);
const pageSize = ref(6);

// 获取动态列表
const fetchPosts = async (page = 1, append = false) => {
  if (page === 1) {
    loading.value = true;
  } else {
    loadingMore.value = true;
  }
  
  try {
    console.log(`开始获取动态列表，页码: ${page}, 每页数量: ${pageSize.value}`);
    const res = await axios.get('/api/posts/my', {
      params: { page, size: pageSize.value }
    });
    
    console.log('获取动态列表响应:', res.data);
    
    if (res.data && res.data.code === 200) {
      const postsData = res.data.data || [];
      console.log('原始帖子数据:', postsData);
      
      // 如果返回的数据少于pageSize，说明没有更多数据了
      if (postsData.length < pageSize.value) {
        hasMore.value = false;
      }
      
      // 处理每个帖子的mediaUrls，确保它是数组
      const processedPosts = postsData.map(post => {
        // 深拷贝帖子对象
        const processedPost = { ...post };
        
        // 确保mediaUrls是数组
        if (!Array.isArray(processedPost.mediaUrls)) {
          console.log(`帖子ID: ${processedPost.id} 的mediaUrls不是数组，设置为空数组`);
          processedPost.mediaUrls = [];
        }
        
        // 过滤掉无效的URL
        processedPost.mediaUrls = processedPost.mediaUrls.filter(url => 
          url && typeof url === 'string' && url.trim() !== ''
        );
        
        console.log(`处理后帖子ID: ${processedPost.id}, mediaUrls:`, processedPost.mediaUrls);
        return processedPost;
      });
      
      if (append) {
        posts.value = [...posts.value, ...processedPosts];
      } else {
        posts.value = processedPosts;
      }
    } else {
      ElMessage.error(res.data?.message || '获取动态列表失败');
    }
  } catch (error) {
    console.error('获取动态列表错误:', error);
    ElMessage.error('获取动态列表失败');
  } finally {
    loading.value = false;
    loadingMore.value = false;
  }
};

// 加载更多
const loadMore = () => {
  currentPage.value++;
  fetchPosts(currentPage.value, true);
};

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  const date = new Date(timeStr);
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
};

// 获取有效的媒体URL列表
const getValidMediaUrls = (post) => {
  if (!post.mediaUrls || !Array.isArray(post.mediaUrls)) {
    return [];
  }
  
  return post.mediaUrls.filter(url => 
    url && typeof url === 'string' && url.trim() !== ''
  );
};

// 判断是否有图片
const hasImages = (post) => {
  const validUrls = getValidMediaUrls(post);
  return validUrls.length > 0;
};

// 获取完整的图片URL
const getFullImageUrl = (url) => {
  if (!url || typeof url !== 'string') {
    console.log('图片URL无效:', url);
    return '';
  }
  
  console.log('处理前的原始图片URL:', url);
  
  // 如果已经是完整URL，则直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    console.log('已是完整URL，直接返回:', url);
    return url;
  }
  
  // 否则添加基础URL
  const baseUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
  console.log('使用的baseUrl:', baseUrl);
  
  // 确保URL格式正确
  let fullUrl;
  if (url.startsWith('/')) {
    fullUrl = `${baseUrl}${url}`;
  } else {
    fullUrl = `${baseUrl}/${url}`;
  }
  
  console.log('最终完整URL:', fullUrl);
  
  // 尝试访问图片并记录
  const img = new Image();
  img.onload = () => {
    console.log('图片加载成功:', fullUrl);
  };
  img.onerror = () => {
    console.error('图片加载失败:', fullUrl);
  };
  img.src = fullUrl;
  
  return fullUrl;
};

// 获取图片预览列表
const getImagePreviewList = (post) => {
  const validUrls = getValidMediaUrls(post);
  return validUrls.map(url => getFullImageUrl(url));
};

// 处理图片加载错误
const handleImageError = (e) => {
  console.error('图片加载失败:', e);
};

// 判断是否有视频
const hasVideos = (post) => {
  const validUrls = getValidMediaUrls(post);
  return validUrls.length > 0 && 
    validUrls[0].match(/\.(mp4|webm|ogg)$/i);
};

onMounted(() => {
  fetchPosts();
});

// 暴露方法给父组件
defineExpose({
  fetchPosts
});
</script>

<style scoped>
.post-list {
  padding: 20px;
}

.post-card {
  margin-bottom: 20px;
  transition: all 0.3s;
}

.post-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
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
}

.time {
  font-size: 12px;
  color: #999;
}

.post-content {
  margin-bottom: 15px;
  white-space: pre-wrap;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 10px;
  margin-top: 10px;
}

.post-images .el-image {
  width: 100%;
  height: 120px;
  border-radius: 4px;
  overflow: hidden;
}

.post-video {
  width: 100%;
  max-height: 200px;
  border-radius: 4px;
}

.load-more {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.empty-posts {
  margin: 40px 0;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
}

.image-error .el-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.image-error p {
  margin: 0;
  font-size: 12px;
}
</style> 