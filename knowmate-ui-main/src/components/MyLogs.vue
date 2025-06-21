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
      <n-button class="retry-button" @click="addTestData">添加测试数据</n-button>
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
  
  let date;
  if (typeof time === 'string') {
    date = new Date(time);
  } else if (time instanceof Date) {
    date = time;
  } else {
    return '';
  }
  
  return `${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`;
};

// 获取当天的日期字符串，格式为YYYY-MM-DD
const getTodayDate = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');
  const todayStr = `${year}-${month}-${day}`;
  console.log('生成的今日日期字符串:', todayStr);
  return todayStr;
};

// 判断日期是否是今天
const isToday = (dateStr) => {
  if (!dateStr) return false;
  
  console.log('检查日期是否为今天:', dateStr);
  
  // 获取今天的日期字符串（YYYY-MM-DD格式）
  const today = new Date();
  const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
  console.log('今天的日期字符串:', todayStr);
  
  try {
    // 尝试多种格式解析
    let dateToCheck;
    
    // 如果是字符串类型
    if (typeof dateStr === 'string') {
      // 1. 尝试提取YYYY-MM-DD部分
      let datePart = '';
      
      // 处理ISO格式 (2023-06-01T12:00:00.000Z)
      if (dateStr.includes('T')) {
        datePart = dateStr.split('T')[0];
      } 
      // 处理标准日期格式 (2023-06-01 12:00:00)
      else if (dateStr.includes(' ')) {
        datePart = dateStr.split(' ')[0];
      }
      // 已经是日期格式 (2023-06-01)
      else if (dateStr.match(/^\d{4}-\d{2}-\d{2}$/)) {
        datePart = dateStr;
      }
      // 其他格式，尝试创建Date对象
      else {
        dateToCheck = new Date(dateStr);
        datePart = `${dateToCheck.getFullYear()}-${String(dateToCheck.getMonth() + 1).padStart(2, '0')}-${String(dateToCheck.getDate()).padStart(2, '0')}`;
      }
      
      console.log('提取的日期部分:', datePart);
      
      // 直接比较日期字符串
      if (datePart === todayStr) {
        console.log('日期匹配: 是今天');
        return true;
      }
    } 
    // 如果是Date对象
    else if (dateStr instanceof Date) {
      dateToCheck = dateStr;
      const checkStr = `${dateToCheck.getFullYear()}-${String(dateToCheck.getMonth() + 1).padStart(2, '0')}-${String(dateToCheck.getDate()).padStart(2, '0')}`;
      console.log('Date对象的日期字符串:', checkStr);
      
      if (checkStr === todayStr) {
        console.log('日期匹配: 是今天');
        return true;
      }
    }
    
    // 如果上面的方法都没有匹配，尝试更严格的比较
    if (!dateToCheck) {
      dateToCheck = new Date(dateStr);
    }
    
    // 获取今天的日期（年月日）
    const todayDate = new Date();
    todayDate.setHours(0, 0, 0, 0);
    
    // 设置要检查的日期为当天的0点
    const checkDate = new Date(dateToCheck);
    checkDate.setHours(0, 0, 0, 0);
    
    // 比较两个日期是否相同
    const result = todayDate.getTime() === checkDate.getTime();
    console.log(`严格比较结果: ${result}`);
    return result;
  } catch (e) {
    console.error('日期比较出错:', e);
    return false;
  }
};

// 添加测试数据
const addTestData = async () => {
  try {
    loading.value = true;
    
    // 添加一条饮食记录
    const now = new Date();
    const mealData = {
      foodName: '测试食物',
      calories: 500,
      mealTime: now.toISOString(),
      notes: '测试添加的饮食记录'
    };
    
    console.log('准备添加饮食记录:', mealData);
    await axios.post('/api/meal-log', mealData);
    message.success('添加测试饮食记录成功');
    
    // 添加一条运动记录
    const exerciseData = {
      activity: '测试运动',
      duration: 30,
      caloriesBurned: 300,
      exerciseTime: now.toISOString(),
      notes: '测试添加的运动记录'
    };
    
    console.log('准备添加运动记录:', exerciseData);
    await axios.post('/api/exerciselog', exerciseData);
    message.success('添加测试运动记录成功');
    
    // 重新加载数据
    await fetchLogs();
  } catch (error) {
    console.error('添加测试数据失败:', error);
    message.error('添加测试数据失败: ' + (error.message || '未知错误'));
  } finally {
    loading.value = false;
  }
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
      
      if (mealRes.data && mealRes.data.code === 200) {
        // 正确处理返回数据结构
        if (mealRes.data.data && mealRes.data.data.records) {
          mealLogs = mealRes.data.data.records;
        } else if (Array.isArray(mealRes.data.data)) {
          mealLogs = mealRes.data.data;
        } else {
          console.warn('饮食日志数据结构不符合预期:', mealRes.data);
          mealLogs = [];
        }
      } else {
        console.warn('饮食日志返回状态码非200:', mealRes.data);
        mealLogs = [];
      }
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
      
      if (exerciseRes.data && exerciseRes.data.code === 200) {
        if (Array.isArray(exerciseRes.data.data)) {
          exerciseLogs = exerciseRes.data.data;
        } else {
          console.warn('运动日志数据结构不符合预期:', exerciseRes.data);
          exerciseLogs = [];
        }
      } else {
        console.warn('运动日志返回状态码非200:', exerciseRes.data);
        exerciseLogs = [];
      }
    } catch (exerciseError) {
      console.error('获取运动日志失败:', exerciseError);
      message.error('获取运动日志失败');
      
      // 设置错误状态但不中断函数执行
      error.value = {
        title: '获取运动日志失败',
        message: exerciseError.message || '请检查网络连接或联系管理员'
      };
    }
    
    console.log('原始饮食日志数据:', mealLogs);
    console.log('原始运动日志数据:', exerciseLogs);
    
    // 处理饮食日志
    console.log('处理饮食日志数据...');
    const todayMeals = mealLogs
      .filter(meal => {
        const isTodayMeal = isToday(meal.mealTime);
        console.log(`饮食记录 ID:${meal.id}, 时间:${meal.mealTime}, 是否今天:${isTodayMeal}`);
        return isTodayMeal;
      })
      .map(meal => ({
        id: `meal-${meal.id}`,
        name: meal.foodName || '未命名食物',
        calories: meal.calories || 0,
        time: meal.mealTime,
        type: 'meal'
      }));
    console.log('今日饮食记录:', todayMeals);
    
    // 处理运动日志
    console.log('处理运动日志数据...');
    const todayExercises = exerciseLogs
      .filter(exercise => {
        const isTodayExercise = isToday(exercise.exerciseTime);
        console.log(`运动记录 ID:${exercise.id}, 时间:${exercise.exerciseTime}, 是否今天:${isTodayExercise}`);
        return isTodayExercise;
      })
      .map(exercise => ({
        id: `exercise-${exercise.id}`,
        name: exercise.activity || '未命名运动',
        calories: exercise.caloriesBurned || 0,
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
  } catch (err) {
    console.error('获取日志失败:', err);
    message.error('获取日志数据失败，请稍后重试');
    error.value = {
      title: '获取日志失败',
      message: err.message || '请检查网络连接或联系管理员'
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