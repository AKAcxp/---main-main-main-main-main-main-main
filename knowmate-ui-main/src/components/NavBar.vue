<template>
  <n-space align="center" justify="space-between" style="width: 100%; height: 100%">
    <n-space align="center">
      <img src="https://img.lisir.me/image/my/favicon.png" alt="Naive UI Logo" style="height: 32px; vertical-align: middle" />
      <n-h2 style="margin: 0">KnowMate</n-h2>
    </n-space>
    <n-dropdown trigger="hover" :options="options" @select="handleSelect">
      <div style="display: flex; align-items: center; height: 100%">
        <n-avatar size="medium" src="https://img.lisir.me/image/my/avatar.png" />
      </div>
    </n-dropdown>
  </n-space>
</template>

<script setup>
import { ref, h } from "vue";
import { NSpace, NAvatar, NIcon, NH2, NDropdown, useMessage } from "naive-ui";
import { PersonOutline, KeyOutline, LogOut } from "@vicons/ionicons5";
import { useRouter } from "vue-router";
import { useAuthStore } from "../store/auth";

const router = useRouter();
const authStore = useAuthStore();
const message = useMessage();

// 辅助函数：创建图标渲染函数
const renderIcon = (icon) => {
  return () => h(NIcon, null, { default: () => h(icon) });
};

// 下拉菜单选项
const options = ref([
  {
    label: "个人设置",
    key: "profile",
    icon: renderIcon(PersonOutline),
  },
  {
    label: "用户信息",
    key: "userInfo",
    icon: renderIcon(PersonOutline),
  },
  {
    label: "修改密码",
    key: "changePassword",
    icon: renderIcon(KeyOutline),
  },
  {
    type: "divider",
    key: "d1",
  },
  {
    label: "退出登录",
    key: "logout",
    icon: renderIcon(LogOut),
  },
]);

// 处理菜单项选择事件
const handleSelect = (key) => {
  console.log("Selected dropdown key:", key);
  if (key === "logout") {
    authStore.logout();
    message.success("已退出登录");
  } else if (key === "profile") {
    router.push({ name: "UserProfile" });
  } else if (key === "userInfo") {
    router.push({ name: "UserInfo" });
  } else if (key === "changePassword") {
    router.push({ name: "ChangePassword" });
  }
};
</script>

<style scoped></style>
