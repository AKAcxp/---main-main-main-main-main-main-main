<template>
  <form @submit.prevent="handleLogin">
    <input v-model="username" placeholder="ÓÃ»§Ãû" />
    <input v-model="password" type="password" placeholder="ÃÜÂë" />
    <button type="submit">µÇÂ¼</button>
    <div v-if="error">{{ error }}</div>
  </form>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';

const username = ref('');
const password = ref('');
const error = ref('');
const router = useRouter();
const authStore = useAuthStore();

async function handleLogin() {
  console.log('Attempting to log in with username:', username.value);
  error.value = ''; // Clear previous errors
  try {
    const success = await authStore.login(username.value, password.value);

    if (success) {
      console.log('Login successful in Login.vue. Role:', authStore.role);
      // 区分跳转逻辑，现在直接从 store 中获取 role
      if (authStore.role === 'ROLE_ADMIN') {
        router.push('/admin/monitor');
      } else {
        router.push('/');
      }
    } else {
      // 登录失败，错误信息已经在 authStore 中设置
      console.error('Login failed via authStore:', authStore.loginError);
      error.value = authStore.loginError || '登录失败，请检查用户名或密码。';
    }
  } catch (err) {
    console.error('Unexpected error during login:', err);
    error.value = '发生未知错误，请重试。';
  }
}
</script> 