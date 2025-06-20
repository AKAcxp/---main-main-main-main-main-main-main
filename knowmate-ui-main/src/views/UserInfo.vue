<template>
  <div class="user-info-container">
    <n-card title="个人信息设置">
      <n-tabs type="line" animated default-value="basic">
        <n-tab-pane name="basic" tab="基本信息">
          <n-form
            ref="basicFormRef"
            :model="authStore.userInfo"
            :rules="basicRules"
            label-placement="left"
            label-width="auto"
            require-mark-placement="right-hanging"
            style="margin-top: 20px;"
          >
            <n-form-item label="用户名" path="username">
              <n-input 
                :value="authStore.userInfo.username" 
                @update:value="newValue => authStore.userInfo.username = newValue" 
                placeholder="请输入用户名" 
                :disabled="true" 
              />
            </n-form-item>
            <n-form-item label="年龄" path="age">
              <n-input-number
                :value="authStore.userInfo.age"
                @update:value="newValue => authStore.userInfo.age = newValue"
                placeholder="请输入您的年龄"
                :min="1"
                :max="150"
                style="width: 100%;"
              />
            </n-form-item>
            <n-form-item label="性别" path="gender">
              <n-select
                :value="authStore.userInfo.gender"
                @update:value="newValue => authStore.userInfo.gender = newValue"
                placeholder="请选择您的性别"
                :options="genderOptions"
                style="width: 100%;"
              />
            </n-form-item>
            <n-form-item label="身高 (cm)" path="height">
              <n-input-number 
                :value="authStore.userInfo.height" 
                @update:value="newValue => authStore.userInfo.height = newValue" 
                placeholder="请输入身高" 
                :min="0" 
                style="width: 100%;"
              />
            </n-form-item>
            <n-form-item label="体重 (kg)" path="weight">
              <n-input-number 
                :value="authStore.userInfo.weight" 
                @update:value="newValue => authStore.userInfo.weight = newValue" 
                placeholder="请输入体重" 
                :min="0" 
                :precision="2" 
                style="width: 100%;"
              />
            </n-form-item>
            <n-form-item label="BMI 指数" path="bmi">
              <n-input 
                :value="authStore.userInfo.bmi ? authStore.userInfo.bmi.toFixed(2) : '未计算'"
                placeholder="根据身高体重自动计算" 
                :disabled="true"
              />
            </n-form-item>
            <n-form-item label="健康状态" path="bmiStatus">
              <n-input 
                :value="authStore.userInfo.bmiStatus || '未计算'"
                placeholder="根据BMI指数自动判断" 
                :disabled="true"
              />
            </n-form-item>
            <n-form-item label="健身目标" path="goal">
              <n-select
                :value="authStore.userInfo.goal"
                @update:value="newValue => authStore.userInfo.goal = newValue"
                placeholder="请选择你的健身目标"
                :options="goalOptions"
                style="width: 100%;"
              />
            </n-form-item>
          </n-form>
        </n-tab-pane>
        <n-tab-pane name="preference" tab="饮食偏好">
          <n-form
            ref="preferenceFormRef"
            :model="authStore.userInfo"
            :rules="preferenceRules"
            label-placement="left"
            label-width="auto"
            require-mark-placement="right-hanging"
            style="margin-top: 20px;"
          >
            <n-form-item label="偏好口味" path="preference">
              <n-input 
                :value="authStore.userInfo.preference" 
                @update:value="newValue => authStore.userInfo.preference = newValue" 
                placeholder="例如：喜欢辣的、甜的" 
                type="textarea" 
              />
            </n-form-item>
            <n-form-item label="饮食禁忌" path="avoidFood">
              <n-input 
                :value="authStore.userInfo.avoidFood" 
                @update:value="newValue => authStore.userInfo.avoidFood = newValue" 
                placeholder="例如：不吃香菜、海鲜过敏" 
                type="textarea" 
              />
            </n-form-item>
          </n-form>
        </n-tab-pane>
      </n-tabs>

      <template #action>
        <n-space justify="end" style="margin-top: 20px;">
          <n-button type="primary" @click="handleSave" :loading="isSaving">
            保存修改
          </n-button>
          <n-button @click="handleReset" :disabled="isSaving">
            重置
          </n-button>
          <n-button type="info" @click="showChatBotDialog = true">
            AI 助手
          </n-button>
        </n-space>
      </template>
      <n-alert title="提示" type="error" v-if="authStore.userInfoError" closable @close="authStore.userInfoError = null">
        {{ authStore.userInfoError }}
      </n-alert>
    </n-card>

    <!-- AI 聊天助手弹窗 -->
    <el-dialog 
      v-model="showChatBotDialog" 
      title="AI 聊天助手" 
      width="50%" 
      destroy-on-close 
      :close-on-click-modal="false"
    >
      <ChatBot />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showChatBotDialog = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  NCard,
  NTabs,
  NTabPane,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NSelect,
  NButton,
  NSpace,
  useMessage,
  NAlert
} from 'naive-ui'
import { useAuthStore } from '../store/auth'
import { useRouter } from 'vue-router'
import ChatBot from '../components/ChatBot.vue'

const message = useMessage()
const authStore = useAuthStore()
const router = useRouter()

const basicFormRef = ref(null)
const preferenceFormRef = ref(null)
const isSaving = ref(false)
const showChatBotDialog = ref(false)

const goalOptions = [
  { label: '减脂', value: 'fat_loss' },
  { label: '增肌', value: 'muscle_gain' },
  { label: '塑形', value: 'body_shaping' }
]

const genderOptions = [
  { label: '男', value: 'male' },
  { label: '女', value: 'female' },
  { label: '其他', value: 'other' }
]

const basicRules = {
  username: [{ required: true, message: '请输入用户名', trigger: ['input', 'blur'] }],
  age: [{ type: 'number', required: true, message: '请输入有效年龄 (1-150)', trigger: ['input', 'blur'], min: 1, max: 150 }],
  gender: [{ required: true, message: '请选择性别', trigger: ['change', 'blur'] }],
  height: [{ type: 'number', required: true, message: '请输入有效身高 (数字)', trigger: ['input', 'blur'], min: 1 }],
  weight: [{ type: 'number', required: true, message: '请输入有效体重 (数字)', trigger: ['input', 'blur'], min: 1 }],
  goal: [{ required: true, message: '请选择健身目标', trigger: ['change', 'blur'] }]
}

const preferenceRules = {
  preference: [{ required: false }],
  avoidFood: [{ required: false }]
}

onMounted(async () => {
  if (!authStore.userInfo?.username) {
    await authStore.loadUserInfo();
    if (authStore.userInfoError) {
        message.error(`加载用户信息失败: ${authStore.userInfoError}`);
    }
  }
});

function handleReset() {
  authStore.loadUserInfo()
  message.info('表单已重置为服务器数据')
  basicFormRef.value?.restoreValidation()
  preferenceFormRef.value?.restoreValidation()
}

async function handleSave() {
  console.log('handleSave function called');
  console.log('UserInfo.vue: authStore.userInfo at start of handleSave:', JSON.parse(JSON.stringify(authStore.userInfo)));
  isSaving.value = true;
  authStore.userInfoError = null;

  let basicValid = false;
  let preferenceValid = false;

  try {
    console.log('UserInfo.vue: Attempting to validate basic form...');
    await basicFormRef.value?.validate();
    basicValid = true;
    console.log('UserInfo.vue: Basic form validation successful.');
  } catch (errors) {
    console.error('UserInfo.vue: Basic form validation failed:', errors);
    message.error('请检查基本信息的输入是否正确');
    isSaving.value = false;
    return;
  }

  try {
    console.log('UserInfo.vue: Attempting to validate preference form...');
    await preferenceFormRef.value?.validate();
    preferenceValid = true;
    console.log('UserInfo.vue: Preference form validation successful.');
  } catch (errors) {
    console.error('UserInfo.vue: Preference form validation failed:', errors);
    message.error('请检查饮食偏好信息的输入是否正确');
    isSaving.value = false;
    return;
  }

  console.log('UserInfo.vue: Checking authStore.token. Current token:', authStore.token ? 'exists' : 'does not exist');
  if (!authStore.token) {
    message.error('用户未登录，无法保存信息');
    isSaving.value = false;
    return;
  }

  const dataToSubmit = {
    username: authStore.userInfo.username,
    age: authStore.userInfo.age,
    gender: authStore.userInfo.gender,
    height: authStore.userInfo.height,
    weight: authStore.userInfo.weight,
    goal: authStore.userInfo.goal,
    preference: authStore.userInfo.preference,
    avoidFood: authStore.userInfo.avoidFood,
  };

  try {
    console.log('UserInfo.vue: Submitting data to update user info:', dataToSubmit);
    const success = await authStore.saveUserInformation(dataToSubmit);

    if (success) {
      message.success('个人信息保存成功！');
      await authStore.loadUserInfo();
      router.push('/recommendations');
    } else {
      message.error(authStore.userInfoError || '保存个人信息失败');
    }

  } catch (err) {
    console.error('UserInfo.vue: Error saving user info:', err);
    authStore.userInfoError = err.response?.data?.message || '保存个人信息失败';
    message.error(authStore.userInfoError);
  } finally {
    isSaving.value = false;
  }
}
</script>

<style scoped>
.user-info-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.n-card {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.n-form-item {
  margin-bottom: 20px;
}

.n-input-number, .n-select {
  width: 100%;
}

.dialog-footer button:first-child {
  margin-right: 10px;
}
</style> 