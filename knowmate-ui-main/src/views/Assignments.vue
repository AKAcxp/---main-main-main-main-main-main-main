<template>
  <el-card class="assignments-card">
    <h3>🏃‍♂️ 运动日志</h3>

    <!-- 添加运动记录表单 -->
    <el-form :inline="true" @submit.prevent="addLog" style="margin-bottom: 20px">
      <el-form-item label="运动项目">
        <el-input v-model="form.activity" placeholder="如：跑步" />
      </el-form-item>

      <el-form-item label="时长（分钟）">
        <el-input-number v-model="form.duration" :min="1" />
      </el-form-item>

      <el-form-item label="消耗热量（kcal）">
        <el-input v-model="form.caloriesBurned" placeholder="如：300" />
      </el-form-item>

      <el-form-item label="时间">
        <el-date-picker
          v-model="form.exerciseTime"
          type="datetime"
          placeholder="选择时间"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="addLog">添加记录</el-button>
      </el-form-item>
    </el-form>

    <!-- 显示已有记录 -->
    <el-table :data="logs" style="width: 100%" class="exercise-table">
      <el-table-column prop="exerciseTime" label="时间" width="180" />
      <el-table-column prop="activity" label="项目" />
      <el-table-column prop="duration" label="时长 (分钟)" />
      <el-table-column prop="caloriesBurned" label="消耗热量 (kcal)" />
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button type="danger" size="small" @click="delLog(scope.row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios' // 假设您有一个配置好的axios实例
import { ElMessage } from 'element-plus'
import { useThemeStore } from '@/store/theme'; // 导入主题store

const themeStore = useThemeStore(); // 使用主题store
const logs = ref([])
const form = ref({
  activity: '',
  duration: 0,
  caloriesBurned: '', // 注意：后端可能期望数字类型
  exerciseTime: ''
})

// 加载日志数据
const loadLogs = async () => {
  try {
    // 注意：确保 token 通过 axios 拦截器被正确添加到请求头中
    const res = await axios.get('/api/exerciselog') 
    console.log('运动日志数据:', res.data)
    if (res.data && res.data.data) {
      logs.value = res.data.data // 确保正确访问data属性
    } else {
      logs.value = []
      console.warn('运动日志数据格式不符合预期', res.data)
    }
  } catch (e) {
    console.error('加载运动日志失败', e)
    ElMessage.error('加载运动日志失败')
  }
}

// 添加记录
const addLog = async () => {
  if (!form.value.activity || !form.value.duration || !form.value.caloriesBurned || !form.value.exerciseTime) {
    ElMessage.warning('请填写所有运动记录信息')
    return
  }

  try {
    const payload = {
      ...form.value,
      caloriesBurned: parseFloat(form.value.caloriesBurned) || 0, // 转换为数字
    }
    console.log('提交运动记录:', payload)
    
    await axios.post('/api/exerciselog', payload)
    
    ElMessage.success('运动记录添加成功')
    form.value = { // 重置表单
      activity: '',
      duration: 0,
      caloriesBurned: '',
      exerciseTime: ''
    }
    loadLogs() // 重新加载数据
  } catch (error) {
    console.error('添加运动记录失败:', error)
    ElMessage.error('添加运动记录失败')
  }
}

// 删除记录
const delLog = async (id) => {
  try {
    await axios.delete(`/api/exerciselog/${id}`)
    ElMessage.success('运动记录删除成功')
    loadLogs() // 重新加载数据
  } catch (error) {
    console.error('删除运动记录失败:', error)
    ElMessage.error('删除运动记录失败')
  }
}

onMounted(() => {
  console.log('运动日志组件已挂载')
  loadLogs()
})
</script>

<style scoped>
/* 主题相关样式 */
.assignments-card {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border: 1px solid var(--border-color);
  transition: background-color 0.3s ease, color 0.3s ease;
}

h3 {
  margin-bottom: 20px;
  font-size: 1.5em;
  color: var(--text-color);
}

.el-form-item {
  margin-right: 15px; /* 增加表单项之间的间距 */
}

/* 适配Element Plus组件主题 */
:deep(.el-card__header) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border-bottom: 1px solid var(--border-color);
}

:deep(.el-card__body) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
}

:deep(.el-form-item__label) {
  color: var(--text-color);
}

:deep(.el-input__inner) {
  background-color: var(--input-bg-color);
  color: var(--text-color);
  border-color: var(--border-color);
}

:deep(.el-input-number__decrease),
:deep(.el-input-number__increase) {
  background-color: var(--input-bg-color);
  color: var(--text-color);
  border-color: var(--border-color);
}

:deep(.el-table) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border-color: var(--border-color);
}

:deep(.el-table th.el-table__cell) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border-bottom: 1px solid var(--border-color);
}

:deep(.el-table tr) {
  background-color: var(--card-bg-color);
}

:deep(.el-table td.el-table__cell) {
  border-bottom: 1px solid var(--border-color);
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background-color: var(--input-bg-color);
}

:deep(.el-table__body tr.hover-row > td.el-table__cell) {
  background-color: var(--input-bg-color);
}
</style> 