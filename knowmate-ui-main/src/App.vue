<script setup>
import NavBar from "./components/NavBar.vue";
import SideBar from "./components/SideBar.vue";
import ChatBot from "./components/ChatBot.vue"; // Import ChatBot component
import { NLayout, NLayoutHeader, NLayoutSider, NLayoutContent, NMessageProvider, NConfigProvider, NFloatButton, NIcon } from "naive-ui";
import { useRoute } from "vue-router"; // 导入 useRoute
import { computed } from "vue"; // 导入 computed
import { useThemeStore } from "./store/theme"; // Import theme store
import { SunnyOutline, MoonOutline } from "@vicons/ionicons5"; // Import theme icons

const route = useRoute(); // 获取当前路由信息
const themeStore = useThemeStore(); // Use theme store

// 计算当前是否为登录或注册路由
const isAuthRoute = computed(() => {
  return route.name === "Auth";
});

// 计算当前是否为管理后台路由
const isAdminRoute = computed(() => {
  return route.path.startsWith("/admin");
});

// Get the computed theme object for Naive UI
const currentNaiveTheme = computed(() => themeStore.naiveTheme);

// Get the current theme mode ('light' or 'dark') for icon toggling
const currentThemeMode = computed(() => themeStore.themeMode);
</script>

<template>
  <n-config-provider :theme="currentNaiveTheme">
    <n-message-provider>
      <!-- 用户界面布局：当不是登录/注册页且不是管理后台页时显示 -->
      <n-layout style="height: 100vh" v-if="!isAuthRoute && !isAdminRoute" class="app-layout">
        <!-- 导航栏 -->
        <n-layout-header style="height: 64px; padding: 0 24px; display: flex; align-items: center" bordered>
          <NavBar />
        </n-layout-header>

        <n-layout position="absolute" style="top: 64px; bottom: 0" has-sider>
          <!-- 侧边栏 -->
          <n-layout-sider bordered show-trigger collapse-mode="width" :collapsed-width="64" :native-scrollbar="false">
            <SideBar />
          </n-layout-sider>

          <!-- 内容区 -->
          <n-layout content-style="padding: 24px;" class="content-area">
            <router-view />
          </n-layout>
        </n-layout>
      </n-layout>

      <!-- 管理后台布局：当是管理后台页时显示 -->
      <n-layout style="height: 100vh" v-else-if="isAdminRoute" class="content-area app-layout">
        <router-view />
      </n-layout>

      <!-- 认证页面布局：当是登录/注册页时显示 -->
      <n-layout style="height: 100vh" v-else class="content-area app-layout">
        <router-view />
      </n-layout>

      <!-- Floating Action Button for Theme Toggle -->
      <n-float-button :right="40" :bottom="30" shape="circle" type="primary" @click="themeStore.toggleTheme" aria-label="Toggle Theme">
        <n-icon>
          <MoonOutline v-if="currentThemeMode === 'light'" />
          <SunnyOutline v-else />
        </n-icon>
      </n-float-button>
      <ChatBot />
    </n-message-provider>
  </n-config-provider>
</template>

<style>
/* 全局样式，确保主题变量应用到整个应用 */
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: background-color 0.3s ease, color 0.3s ease;
}

.content-area {
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: background-color 0.3s ease, color 0.3s ease;
}

.app-layout {
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: background-color 0.3s ease, color 0.3s ease;
}

/* 确保Element Plus组件能正确响应主题变化 */
.el-table {
  --el-table-row-hover-bg-color: var(--table-row-hover-bg-color);
  --el-table-header-bg-color: var(--table-header-bg-color);
  --el-table-border-color: var(--border-color);
  background-color: var(--card-bg-color);
}

.el-card {
  --el-card-bg-color: var(--card-bg-color);
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border-color: var(--border-color);
}

.el-dialog {
  --el-dialog-bg-color: var(--card-bg-color);
  --el-dialog-content-font-color: var(--text-color);
  --el-dialog-title-font-color: var(--text-color);
}

.el-input__inner {
  background-color: var(--input-bg-color);
  color: var(--text-color);
}

.el-form-item__label {
  color: var(--text-color);
}

/* 强制覆盖Element Plus表格样式 */
.el-table th.el-table__cell {
  background-color: var(--table-header-bg-color) !important;
  color: var(--text-color) !important;
}

.el-table td.el-table__cell {
  color: var(--text-color) !important;
}

.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background-color: var(--table-striped-row-bg-color) !important;
}

.el-table__body tr.hover-row > td.el-table__cell {
  background-color: var(--table-row-hover-bg-color) !important;
}
</style>
