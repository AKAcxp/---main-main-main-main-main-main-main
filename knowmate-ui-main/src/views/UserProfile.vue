<template>
  <div class="profile-display-container">
    <n-card title="个人信息概览">
      <!-- <pre>{{ authStore.userInfo }}</pre> Ensure this debug line is removed or commented out -->
      <n-descriptions label-placement="top" title="基本信息" :column="1" bordered>
        <n-descriptions-item label="用户名">
          {{ authStore.userInfo.username || '未填写' }}
        </n-descriptions-item>
        <n-descriptions-item label="年龄">
          {{ authStore.userInfo.age !== null ? authStore.userInfo.age : '未填写' }}
        </n-descriptions-item>
        <n-descriptions-item label="性别">
          {{ authStore.userInfo.gender || '未填写' }}
        </n-descriptions-item>
        <n-descriptions-item label="身高 (cm)">
          {{ authStore.userInfo.height !== null ? authStore.userInfo.height : '未填写' }}
        </n-descriptions-item>
        <n-descriptions-item label="体重 (kg)">
          {{ authStore.userInfo.weight !== null ? authStore.userInfo.weight : '未填写' }}
        </n-descriptions-item>
        <n-descriptions-item label="BMI 指数">
          {{ authStore.userInfo.bmi !== null ? authStore.userInfo.bmi.toFixed(1) : '未计算' }}
        </n-descriptions-item>
        <n-descriptions-item label="健康状态">
          {{ authStore.userInfo.bmiStatus || '未计算' }}
        </n-descriptions-item>
        <n-descriptions-item label="目标">
          {{ getGoalLabel(authStore.userInfo.goal) || '未填写' }}
        </n-descriptions-item>
      </n-descriptions>

      <n-divider />

      <!-- ECharts BMI Gauge Chart -->
      <div v-if="authStore.userInfo.bmi !== null && authStore.userInfo.bmi !== undefined" style="height: 300px; margin-top: 20px;">
        <v-chart class="chart" :option="chartOption" autoresize />
      </div>
      <n-alert v-else title="BMI 信息不足" type="info" style="margin-top: 20px;">
        当提供了身高和体重后，将在此显示BMI仪表盘。
      </n-alert>

      <n-divider />

      <n-descriptions label-placement="top" title="饮食偏好" :column="1" bordered style="margin-top: 20px;">
        <n-descriptions-item label="偏好口味">
          {{ authStore.userInfo.preference || '未填写' }}
        </n-descriptions-item>
        <n-descriptions-item label="饮食禁忌">
          {{ authStore.userInfo.avoidFood || '未填写' }}
        </n-descriptions-item>
      </n-descriptions>
      
      <n-button @click="reloadUserInfo" type="primary" style="margin-top: 20px;">重新加载数据</n-button>
      <n-button @click="showEditModal = true" type="info" style="margin-top: 20px; margin-left: 10px;">编辑信息</n-button>
      <n-alert v-if="authStore.userInfoError" title="加载错误" type="error" style="margin-top: 15px;">
        {{ authStore.userInfoError }}
      </n-alert>
    </n-card>

    <!-- Edit User Info Modal -->
    <n-modal v-model="showEditModal" title="编辑个人健康档案" positive-text="保存" negative-text="取消" @positive-click="handleSaveUserInfo" display-directive="show">
      <n-form ref="formRef" :model="formValue" :rules="rules" label-placement="left" label-width="auto" require-mark-placement="right-hanging" style="margin-top: 20px;">
        <n-form-item label="年龄" path="age">
          <n-input-number v-model="formValue.age" :min="1" :max="120" placeholder="请输入年龄" />
        </n-form-item>
        <n-form-item label="性别" path="gender">
          <n-radio-group v-model="formValue.gender" name="gender-radio-group">
            <n-space>
              <n-radio value="MALE">男</n-radio>
              <n-radio value="FEMALE">女</n-radio>
              <n-radio value="OTHER">其他</n-radio>
            </n-space>
          </n-radio-group>
        </n-form-item>
        <n-form-item label="身高 (cm)" path="height">
          <n-input-number v-model="formValue.height" :min="50" :max="300" :precision="1" placeholder="请输入身高" />
        </n-form-item>
        <n-form-item label="体重 (kg)" path="weight">
          <n-input-number v-model="formValue.weight" :min="10" :max="500" :precision="1" placeholder="请输入体重" />
        </n-form-item>
        <n-form-item label="目标" path="goal">
          <n-select v-model="formValue.goal" :options="goalOptions" placeholder="请选择健康目标" />
        </n-form-item>
        <n-form-item label="偏好口味" path="preference">
          <n-input v-model="formValue.preference" placeholder="如：清淡、麻辣、甜" />
        </n-form-item>
        <n-form-item label="饮食禁忌" path="avoidFood">
          <n-input v-model="formValue.avoidFood" placeholder="如：花生、海鲜、牛奶" />
        </n-form-item>
      </n-form>
    </n-modal>
  </div>
</template>

<script setup>
import { useAuthStore } from '../store/auth';
import { NCard, NDescriptions, NDescriptionsItem, NButton, NDivider, NAlert, NModal, NForm, NFormItem, NInput, NInputNumber, NRadioGroup, NRadio, NSpace, NSelect, useMessage } from 'naive-ui';
import { onMounted, computed, watch, ref, reactive } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { GaugeChart } from 'echarts/charts';
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components';
import VChart from 'vue-echarts';
import api from '../api'; // Ensure you have this import for axios
import { useRouter } from 'vue-router';
import { useRecommendationStore } from '../store/recommendations';

use([
  CanvasRenderer,
  GaugeChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent
]);

const authStore = useAuthStore();
const message = useMessage();
const router = useRouter(); // Initialize useRouter
const recommendationStore = useRecommendationStore(); // Initialize recommendationStore

const showEditModal = ref(false);
const formRef = ref(null);
const formValue = reactive({
  age: null,
  gender: null,
  height: null,
  weight: null,
  goal: null,
  preference: null,
  avoidFood: null
});

const rules = {
  // Define validation rules if necessary
  height: {
    type: 'number',
    required: true,
    message: '请输入身高',
    trigger: ['input', 'blur']
  },
  weight: {
    type: 'number',
    required: true,
    message: '请输入体重',
    trigger: ['input', 'blur']
  }
};

const goalOptions = [
  { label: '减脂', value: 'fat_loss' },
  { label: '增肌', value: 'muscle_gain' },
  { label: '塑形', value: 'toning' },
  { label: '保持健康', value: 'keep_fit' }
];

function getGoalLabel(value) {
  const option = goalOptions.find(opt => opt.value === value);
  return option ? option.label : value;
}

async function reloadUserInfo() {
  await authStore.loadUserInfo();
}

const chartOption = computed(() => {
  const bmi = authStore.userInfo.bmi;
  if (bmi === null || bmi === undefined) {
    return {}; // Return empty option if no BMI
  }
  return {
    series: [
      {
        type: 'gauge',
        min: 10,
        max: 40,
        splitNumber: 6, // To get 10, 15, 20, 25, 30, 35, 40
        axisLine: {
          lineStyle: {
            width: 20,
            color: [
              [ (18.5 - 10) / (40 - 10), '#FFD700' ], // Yellow for 10-18.5 (偏瘦)
              [ (24 - 10) / (40 - 10), '#91CC75' ], // Green for 18.5-24 (正常)
              [ (28 - 10) / (40 - 10), '#FAC858' ], // Orange for 24-28 (超重)
              [ 1, '#EE6666' ]                 // Red for 28-40 (肥胖)
            ]
          }
        },
        pointer: {
          itemStyle: {
            color: 'auto'
          }
        },
        axisTick: {
          distance: -20,
          length: 8,
          lineStyle: {
            color: '#fff',
            width: 2
          }
        },
        splitLine: {
          distance: -20,
          length: 20,
          lineStyle: {
            color: '#fff',
            width: 4
          }
        },
        axisLabel: {
          color: 'auto',
          distance: 25,
          fontSize: 12,
          formatter: function (value) {
            if (value === 10) return '偏瘦';
            if (value === 18.5) return '正常'; // Approx boundary
            if (value === 24) return '超重'; // Approx boundary
            if (value === 28) return '肥胖'; // Approx boundary
            return value.toFixed(0);
          }
        },
        detail: {
          valueAnimation: true,
          formatter: '{value}',
          color: 'auto',
          fontSize: 20,
          offsetCenter: [0, '70%']
        },
        data: [
          {
            value: bmi.toFixed(1) // Display BMI with one decimal place
          }
        ],
        title: {
            offsetCenter: [0, '100%'],
            fontSize: 14,
            color: '#333'
          }
      }
    ]
  };
});

// Watch for changes in authStore.userInfo and update formValue when modal is shown
watch(() => authStore.userInfo, (newUserInfo) => {
  if (newUserInfo) {
    Object.assign(formValue, {
      age: newUserInfo.age,
      gender: newUserInfo.gender,
      height: newUserInfo.height,
      weight: newUserInfo.weight,
      goal: newUserInfo.goal,
      preference: newUserInfo.preference,
      avoidFood: newUserInfo.avoidFood
    });
  }
}, { immediate: true });

async function handleSaveUserInfo() {
  try {
    await formRef.value?.validate(); // Validate form
    const dataToUpdate = { ...formValue }; // Create a copy to send
    
    // Convert nulls to undefined or remove keys if backend expects absence for non-values
    for (const key in dataToUpdate) {
      if (dataToUpdate[key] === null) {
        dataToUpdate[key] = undefined;
      }
    }

    const res = await api.put('/api/user/info', dataToUpdate);
    if (res.data.code === 200) {
      message.success('个人健康档案更新成功！');
      await reloadUserInfo(); // Reload user info to update display
      showEditModal.value = false;
      
      // Call recommendation API and navigate
      try {
        recommendationStore.setLoading(true);
        const recommendationRes = await api.get('/api/recommendation');
        if (recommendationRes.data.code === 200) {
          recommendationStore.setRecommendations(recommendationRes.data.data);
          message.success('已获取最新饮食推荐！');
          router.push('/recommendations'); // Navigate to recommendations page
        } else {
          message.error('获取饮食推荐失败：' + recommendationRes.data.message);
        }
      } catch (recommendationError) {
        console.error('Failed to get recommendations:', recommendationError);
        message.error('获取饮食推荐失败，请稍后再试。');
      } finally {
        recommendationStore.setLoading(false);
      }

    } else {
      message.error('更新失败：' + res.data.message);
    }
  } catch (error) {
    console.error('Failed to save user info:', error);
    message.error('保存失败，请检查输入或网络。');
  }
}

onMounted(() => {
  if (!authStore.userInfo?.username || authStore.userInfo.bmi === undefined) {
    authStore.loadUserInfo();
  }
});

</script>

<style scoped>
.profile-display-container {
  max-width: 600px;
  margin: 20px auto;
}
.chart {
  height: 300px;
}
</style> 