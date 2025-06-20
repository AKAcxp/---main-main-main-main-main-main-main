<template>
  <form @submit.prevent="handleRegister">
    <input v-model="username" placeholder="请输入用户名" />
    <input v-model="email" placeholder="请输入邮箱地址" />
    <input v-model="password" type="password" placeholder="请输入密码" />
    <input v-model="confirmPassword" type="password" placeholder="请再次输入密码" />
    <input v-model.number="age" type="number" placeholder="请输入年龄" />
    <select v-model="gender">
      <option disabled value="">请选择性别</option>
      <option value="MALE">男</option>
      <option value="FEMALE">女</option>
      <option value="OTHER">其他</option>
    </select>
    <input v-model.number="height" type="number" placeholder="请输入身高 (cm)" />
    <input v-model.number="weight" type="number" placeholder="请输入体重 (kg)" />
    <button type="submit">注册</button>
    <div v-if="error">{{ error }}</div>
  </form>
</template>

<script setup>
import { ref } from 'vue';
import { register } from '@/api/auth';
import { useRouter } from 'vue-router';

const username = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const age = ref(null);
const gender = ref('');
const height = ref(null);
const weight = ref(null);
const error = ref('');
const router = useRouter();

function handleRegister() {
  if (password.value !== confirmPassword.value) {
    error.value = '两次输入的密码不一致';
    return;
  }
  register(username.value, email.value, password.value, age.value, gender.value, height.value, weight.value)
    .then(() => {
      router.push('/login');
    })
    .catch(err => {
      error.value = err.response?.data || '注册失败';
    });
}
</script> 