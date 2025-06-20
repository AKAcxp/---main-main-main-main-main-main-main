<template>
  <div class="admin-layout">
    <header class="admin-header">
      智能饮食推荐系统 - 管理后台
    </header>
    <main class="admin-main">
      <aside class="admin-sidebar">
        <h3 class="sidebar-title">导航菜单</h3>
        <ul class="sidebar-menu">
          <li><router-link to="/admin/monitor">系统监控</router-link></li>
          <li><router-link to="/admin/user">用户管理</router-link></li>
          <li><router-link to="/admin/recommendation">推荐引擎监控</router-link></li>
          <li><router-link to="/">返回首页</router-link></li>
          <li><a href="#" @click.prevent="handleLogout">退出登录</a></li>
        </ul>
      </aside>
      <section class="admin-content">
        <router-view /> <!-- 子路由页面内容将在这里渲染 -->
      </section>
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';
import { useThemeStore } from '@/store/theme'; // 导入主题store

const authStore = useAuthStore();
const themeStore = useThemeStore(); // 使用主题store
const router = useRouter();

const handleLogout = () => {
  authStore.logout();
  router.push('/auth'); // 重定向到登录页面
};
</script>

<style scoped>
.admin-layout {
  font-family: 'Arial', sans-serif;
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: background-color 0.3s ease, color 0.3s ease;
}

.admin-header {
  background-color: var(--card-bg-color);
  padding: 15px;
  border-bottom: 1px solid var(--border-color);
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: var(--text-color);
}

.admin-main {
  display: flex;
  height: calc(100vh - 60px);
}

.admin-sidebar {
  width: 200px;
  background-color: #001529; /* 侧边栏保持深色，不随主题变化 */
  padding: 20px;
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
}

.sidebar-title {
  color: #fff;
  margin-bottom: 20px;
}

.sidebar-menu {
  list-style: none;
  padding: 0;
}

.sidebar-menu li {
  margin-bottom: 15px;
}

.sidebar-menu a {
  color: #fff;
  text-decoration: none;
  font-size: 16px;
}

.admin-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: var(--bg-color);
  color: var(--text-color);
}
</style> 