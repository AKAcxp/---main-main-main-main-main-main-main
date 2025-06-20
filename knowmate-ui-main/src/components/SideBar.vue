<template>
  <n-menu :inverted="inverted" :collapsed-width="64" :collapsed-icon-size="22" :value="activeKey" :render-label="renderLabel" :options="menuOptions" />
</template>

<script setup>
import { ref, h, computed } from "vue";
import { NMenu, NIcon } from "naive-ui";
import { RouterLink, useRoute } from "vue-router";
import { HomeOutline, SettingsOutline, InformationCircleOutline, PersonOutline, KeyOutline, BookOutline, DocumentTextOutline, PeopleOutline, SchoolOutline, ListOutline, AnalyticsOutline } from "@vicons/ionicons5";

// Define the inverted prop with a default value
const props = defineProps({
  inverted: {
    type: Boolean,
    default: false,
  },
});

// 菜单逻辑
const route = useRoute();

// 辅助函数：创建图标渲染函数
const renderIcon = (icon) => {
  return () => h(NIcon, null, { default: () => h(icon) });
};

// 菜单项定义
const menuOptions = ref([
  {
    label: "首页",
    key: "/home",
    icon: renderIcon(HomeOutline),
  },
  {
    label: "饮食推荐",
    key: "/recommendations",
    icon: renderIcon(BookOutline),
  },
  {
    label: "运动日志",
    key: "/assignments",
    icon: renderIcon(DocumentTextOutline),
  },
  {
    label: "饮食日志",
    key: "/meallog",
    icon: renderIcon(PeopleOutline),
  },
  {
    label: "  AI助手",
    key: "/classrooms",
    icon: renderIcon(SchoolOutline),
  },
  {
    label: "待办事项",
    key: "/todolist",
    icon: renderIcon(ListOutline),
  },
  {
    label: "数据统计与分析",
    key: "/statistics",
    icon: renderIcon(AnalyticsOutline),
  },
  {
    label: "系统设置",
    key: "settings-group",
    icon: renderIcon(SettingsOutline),
    children: [
      {
        label: "个人信息",
        key: "/settings/profile",
        icon: renderIcon(PersonOutline),
      },
      {
        label: "用户信息",
        key: "/settings/info",
        icon: renderIcon(PersonOutline),
      },
      {
        label: "修改密码",
        key: "/settings/password",
        icon: renderIcon(KeyOutline),
      },
    ],
  },
  {
    label: "关于系统",
    key: "/about",
    icon: renderIcon(InformationCircleOutline),
  },
]);

// 辅助函数：创建 RouterLink 标签渲染函数
const renderLabel = (option) => {
  if (option.key && typeof option.key === "string" && !option.children) {
    return h(RouterLink, { to: option.key }, { default: () => option.label });
  }
  return option.label;
};

// 计算当前激活菜单项的 key
const activeKey = computed(() => route.path);
</script>

<style scoped></style>
