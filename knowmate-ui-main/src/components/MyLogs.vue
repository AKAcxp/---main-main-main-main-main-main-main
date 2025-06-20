<template>
  <div class="my-logs">
    <h3>今日日志</h3>
    <div v-if="loading" class="loading">
      <n-skeleton text :repeat="3" />
    </div>
    <div v-else-if="error" class="error-logs">
      <n-alert type="error" :title="error.title">
        {{ error.message }}
      </n-alert>
      <n-button class="retry-button" @click="fetchLogs">重试</n-button>
    </div>
    <div v-else-if="timeline.length === 0" class="empty-logs">
      <n-empty description="今天还没有记录" />
    </div>
    <n-timeline v-else>
      <n-timeline-item
        v-for="item in timeline"
        :key="item.id"
        :time="formatTime(item.time)"
        :type="item.type === 'meal' ? 'success' : 'warning'"
        :title="item.name"
      >
        <n-card size="small">
          <div>{{ item.name }} - {{ item.calories }} kcal</div>
        </n-card>
      </n-timeline-item>
    </n-timeline>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '../utils/axios';
import { useMessage } from 'naive-ui';

const timeline = ref([]);
const loading = ref(true);
const error = ref(null);
const message = useMessage();

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  return `${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`;
};

// 获取当天的日期字符串，格式为YYYY-MM-DD
const getTodayDate = () => {
  const today = new Date();
  return today.toISOString().split('T')[0];
};

// 获取日志数据
const fetchLogs = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    console.log('开始获取日志数据');
    
    // 获取饮食日志
    console.log('获取饮食日志...');
    let mealLogs = [];
    try {
      const mealRes = await axios.get('/api/meal-log');
      console.log('饮食日志响应:', mealRes.data);
      mealLogs = mealRes.data.data.records || mealRes.data.data || [];
    } catch (mealError) {
      console.error('获取饮食日志失败:', mealError);
      message.error('获取饮食日志失败，但将继续尝试获取运动日志');
    }
    
    // 获取运动日志
    console.log('获取运动日志...');
    let exerciseLogs = [];
    try {
      const exerciseRes = await axios.get('/api/exerciselog');
      console.log('运动日志响应:', exerciseRes.data);
      exerciseLogs = exerciseRes.data.data || [];
    } catch (exerciseError) {
      console.error('获取运动日志失败:', exerciseError);
      message.error('获取运动日志失败');
      
      // 设置错误状态但不中断函数执行
      error.value = {
        title: '获取运动日志失败',
        message: exerciseError.message || '请检查网络连接或联系管理员'
      };
    }
    
    const todayDate = getTodayDate();
    console.log('今日日期:', todayDate);
    
    // 处理饮食日志
    console.log('处理饮食日志数据...');
    const todayMeals = mealLogs
      .filter(meal => {
        const isTodayMeal = meal.mealTime && meal.mealTime.startsWith(todayDate);
        console.log(`饮食记录 ID:${meal.id}, 时间:${meal.mealTime}, 是否今天:${isTodayMeal}`);
        return isTodayMeal;
      })
      .map(meal => ({
        id: `meal-${meal.id}`,
        name: meal.foodName,
        calories: meal.calories,
        time: meal.mealTime,
        type: 'meal'
      }));
    console.log('今日饮食记录:', todayMeals);
    
    // 处理运动日志
    console.log('处理运动日志数据...');
    const todayExercises = exerciseLogs
      .filter(exercise => {
        const isTodayExercise = exercise.exerciseTime && exercise.exerciseTime.startsWith(todayDate);
        console.log(`运动记录 ID:${exercise.id}, 时间:${exercise.exerciseTime}, 是否今天:${isTodayExercise}`);
        return isTodayExercise;
      })
      .map(exercise => ({
        id: `exercise-${exercise.id}`,
        name: exercise.activity,
        calories: exercise.caloriesBurned,
        time: exercise.exerciseTime,
        type: 'exercise'
      }));
    console.log('今日运动记录:', todayExercises);
    
    // 合并并按时间排序
    const allLogs = [...todayMeals, ...todayExercises].sort((a, b) => 
      new Date(a.time) - new Date(b.time)
    );
    console.log('合并后的日志:', allLogs);
    
    timeline.value = allLogs;
    
    if (allLogs.length === 0 && !error.value) {
      console.log('今天没有日志记录');
    }
  } catch (error) {
    console.error('获取日志失败:', error);
    message.error('获取日志数据失败，请稍后重试');
    error.value = {
      title: '获取日志失败',
      message: error.message || '请检查网络连接或联系管理员'
    };
    timeline.value = [];
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  console.log('MyLogs组件已挂载，开始获取日志');
  fetchLogs();
});
</script>

<style scoped>
.my-logs {
  margin-bottom: 20px;
}

.my-logs h3 {
  margin-bottom: 15px;
  color: #606266;
  font-weight: 500;
}

.loading, .empty-logs, .error-logs {
  padding: 20px 0;
}

.empty-logs, .error-logs {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.retry-button {
  margin-top: 15px;
}
</style> 