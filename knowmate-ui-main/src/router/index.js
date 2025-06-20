// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";
import Home from "../views/Home.vue"; // 导入视图组件
import About from "../views/About.vue"; // 另一个示例视图
import Auth from "../views/Auth.vue"; // Import the new Auth component
import NewPost from "../views/NewPost.vue"; // Import the new Auth component

// 假设你已经创建了这些视图组件，或者将它们指向一个通用组件
// 如果没有，你需要创建它们，例如：src/views/Courses.vue
import Recommendations from "../views/Recommendations.vue";
import Assignments from "../views/Assignments.vue";
import Students from "../views/Students.vue"; // 饮食日志组件
import Classrooms from "../views/Classrooms.vue";
import TodoList from "../views/TodoList.vue";
import UserProfile from "../views/UserProfile.vue"; // 用于 /settings/profile
import ChangePassword from "../views/ChangePassword.vue"; // 用于 /settings/password
import UserInfo from "../views/UserInfo.vue"; // 用户信息设置页面
import Statistics from "../views/Statistics.vue"; // 导入 Statistics 组件
// import Settings from '''../views/Settings.vue'''; // 如果 /settings 本身也是一个页面

// 导入管理后台组件
import AdminLayout from "../views/admin/AdminLayout.vue";
import AdminMonitor from "../views/admin/AdminMonitor.vue";
import AdminUser from "../views/admin/AdminUser.vue";
import AdminRecommendation from "../views/admin/AdminRecommendation.vue";

import { useAuthStore } from "../store/auth"; // 导入 Auth Store

const routes = [
  // 重定向根路径到 /home
  {
    path: "/",
    redirect: "/home",
  },
  {
    path: "/home",
    name: "Home",
    component: Home,
    meta: { requiresAuth: true }, // Home 页面需要认证
  },
  {
    path: "/new-post",
    name: "NewPost",
    component: NewPost,
    meta: { requiresAuth: true },
  },
  {
    path: "/about",
    name: "About",
    component: About,
    meta: { requiresAuth: true },
  },
  {
    path: "/recommendations",
    name: "Recommendations",
    component: Recommendations,
    meta: { requiresAuth: true },
  },
  {
    path: "/assignments",
    name: "Assignments",
    component: Assignments,
    meta: { requiresAuth: true },
  },
  {
    path: "/meallog", // 修改路径
    name: "MealLog", // 修改名称
    component: Students, // 组件保持不变，因为它现在是饮食日志
    meta: { requiresAuth: true },
  },
  {
    path: "/classrooms",
    name: "Classrooms",
    component: Classrooms,
    meta: { requiresAuth: true },
  },
  {
    path: "/todolist",
    name: "TodoList",
    component: TodoList,
    meta: { requiresAuth: true },
  },
  {
    path: "/dashboard", // 假设有仪表盘页
    name: "Dashboard",
    // component: () => import('''../views/Dashboard.vue'''), // 懒加载示例
    meta: { requiresAuth: true },
  },
  {
    path: "/settings", // 这是一个父路径，通常会有一个概览页面或重定向到第一个子页面
    name: "Settings",
    // component: Settings, // 如果 /settings 有自己的页面
    // redirect: '/settings/profile', // 或者重定向到第一个子路由
    meta: { requiresAuth: true },
    // 如果 /settings 本身不是一个页面，而是为了组织子路由，可以移除 component 和 redirect
    // 并确保子路由的路径是完整的，例如 /settings/profile
    children: [
      {
        path: "profile", // 相对于 /settings，所以完整路径是 /settings/profile
        name: "UserProfile",
        component: UserProfile,
        meta: { requiresAuth: true },
      },
      {
        path: "info", // 将路径修改为 /settings/info
        name: "UserInfo",
        component: UserInfo,
        meta: { requiresAuth: true },
      },
      {
        path: "password", // 完整路径是 /settings/password
        name: "ChangePassword",
        component: ChangePassword,
        meta: { requiresAuth: true },
      },
    ],
  },
  {
    path: "/auth", // New route for the combined page
    name: "Auth",
    component: Auth,
    meta: { requiresAuth: false },
  },
  {
    path: "/ai-chat",
    name: "ChatAI",
    component: () => import("@/views/classrooms.vue"),
    meta: {
      requiresAuth: true,
    },
  },
  {
    path: "/students",
    name: "Students",
    component: Students,
    meta: { requiresAuth: true, allowedRoles: ["ROLE_ADMIN", "ROLE_TESTER"] },
  },
  {
    path: "/todo",
    name: "TodoList",
    component: TodoList,
    meta: { requiresAuth: true, allowedRoles: ["ROLE_USER"] }, // 示例：只有用户角色能访问待办事项
  },
  {
    path: "/statistics", // 新增路由路径
    name: "Statistics", // 路由名称
    component: Statistics, // 对应的组件
    meta: {
      requiresAuth: true,
      allowedRoles: [
        "ROLE_USER",
        "ROLE_NUTRITIONIST",
        "ROLE_ADMIN",
        "ROLE_TESTER",
      ],
    }, // 假设所有登录用户都可以查看统计数据
  },
  // 管理后台路由
  {
    path: "/admin",
    component: AdminLayout,
    meta: { requiresAuth: true, roles: ["ROLE_ADMIN"] },
    children: [
      { path: "monitor", name: "AdminMonitor", component: AdminMonitor },
      { path: "user", name: "AdminUser", component: AdminUser },
      {
        path: "recommendation",
        name: "AdminRecommendation",
        component: AdminRecommendation,
      },
    ],
  },
  // 可以添加一个 404 页面
  // { path: "/:pathMatch(.*)*", name: "NotFound", component: NotFound },
];

const router = createRouter({
  history: createWebHistory(), // 使用 HTML5 的历史记录模式
  routes, // 将路由规则传给 router
});

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore(); // 在守卫中使用 Store
  const requiresAuth = to.meta.requiresAuth;
  const isAuthenticated = authStore.isAuthenticated; // 获取登录状态
  const userRole = authStore.role; // 获取用户角色

  console.log(
    `Navigating to: ${to.path}, requiresAuth: ${requiresAuth}, isAuthenticated: ${isAuthenticated}, userRole: ${userRole}`
  );

  // 1. 处理公共路由 (不需要认证的路由)
  if (!requiresAuth) {
    if (to.path === "/auth" && isAuthenticated) {
      // 如果已认证用户尝试访问登录/注册页，则重定向到首页
      console.log(
        "User is authenticated and trying to access public auth page, redirecting to home."
      );
      return next({ path: "/home" });
    }
    // 允许访问其他公共页面，或者未认证用户访问 /auth
    console.log("Accessing public page, allowing access.");
    return next();
  }

  // 2. 处理需要认证的路由
  if (requiresAuth) {
    if (isAuthenticated) {
      // 管理员用户特定重定向
      if (userRole === "ROLE_ADMIN" && !to.path.startsWith("/admin")) {
        console.log("Admin user logged in, redirecting to admin dashboard.");
        return next("/admin/monitor");
      }
      // 普通用户尝试访问管理页面重定向
      if (userRole === "ROLE_USER" && to.path.startsWith("/admin")) {
        console.log(
          "Regular user trying to access admin page, redirecting to home."
        );
        return next("/home");
      }

      // 检查特定角色权限 (to.meta.roles)
      if (to.meta.roles && !to.meta.roles.includes(userRole)) {
        console.log(
          "User does not have required role, redirecting to no-access."
        );
        return next("/no-access");
      }

      // 如果已认证且所有检查通过，允许访问
      console.log(
        "User is authenticated and has required role, allowing access."
      );
      return next();
    } else {
      // 用户未认证，重定向到登录页
      console.log("User is not authenticated, redirecting to login.");
      return next({
        name: "Auth",
        query: { redirect: to.fullPath },
      });
    }
  }
});

export default router;
