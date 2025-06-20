<template>
  <el-card>
    <h3>🥗 饮食日志</h3>
    <el-form @submit.prevent="addLog">
      <el-input v-model="form.foodName" placeholder="食物名" />
      <el-input v-model="form.calories" placeholder="热量 (kcal)" />
      <el-date-picker v-model="form.mealTime" type="datetime" placeholder="时间" />
      <el-button type="primary" @click="addLog">添加</el-button>
    </el-form>

    <el-table :data="logs" style="margin-top: 20px">
      <el-table-column prop="mealTime" label="时间" />
      <el-table-column prop="foodName" label="食物" />
      <el-table-column prop="calories" label="热量 (kcal)" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" type="danger" @click="delLog(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const logs = ref([])
const form = ref({ foodName: '', calories: '', mealTime: '' })
const router = useRouter()

const loadLogs = async () => {
  const res = await axios.get('/api/meal-log')
  logs.value = res.data.data.records
}

const addLog = async () => {
  try {
    const res = await axios.post('/api/meal-log', form.value)
    if (res.data.code === 200) {
      ElMessage.success('饮食日志添加成功！')
      form.value = { foodName: '', calories: '', mealTime: '' }
      loadLogs()

      ElMessageBox.confirm(
        '饮食日志已成功添加。是否查看今日健康分析？',
        '提示',
        {
          confirmButtonText: '立即查看',
          cancelButtonText: '稍后',
          type: 'info',
        }
      )
        .then(() => {
          router.push('/statistics')
        })
        .catch(() => {
          // User cancelled, do nothing or show a message
        })
    } else {
      ElMessage.error(res.data.message || '添加饮食日志失败')
    }
  } catch (error) {
    console.error('Error adding meal log:', error)
    ElMessage.error('添加饮食日志时发生错误，请稍后再试。')
  }
}

const delLog = async (id) => {
  await axios.delete(`/api/meal-log/${id}`)
  loadLogs()
}

onMounted(loadLogs)
</script>

<style scoped>
/* 页面特定样式 */
</style> 