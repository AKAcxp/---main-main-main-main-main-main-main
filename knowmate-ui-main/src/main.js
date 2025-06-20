import { createApp } from "vue";
// 导入 Pinia 状态管理
import { createPinia } from "pinia";
// 导入 Naive UI
import NaiveUI from "naive-ui";
// 导入路由
import router from "./router";
// 导入 App 组件
import App from "./App.vue";
import axios from "axios";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import "@/assets/theme.css"; // Import custom theme styles
import { useAuthStore } from "./store/auth"; // 导入 useAuthStore

// 配置 axios 默认值
axios.defaults.baseURL = "http://localhost:8080"; // 设置基础URL
axios.defaults.timeout = 10000; // 设置超时时间（10秒）
axios.defaults.withCredentials = false; // 不需要携带凭证，因为我们使用JWT

// 添加请求拦截器
axios.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem("authToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 添加响应拦截器
axios.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          console.error(
            "认证失败或Token已过期，将跳转到登录页:",
            error.response.data
          );
          const authStore = useAuthStore();
          // 调用 authStore.logout() 来清除所有认证状态并处理路由跳转
          authStore.logout();

          // 提示用户（可选，如果 NavBar.vue 或 authStore 内部没有统一处理）
          // alert("登录已过期或认证失败，请重新登录。");

          // 这里的 router.push 逻辑已经由 authStore.logout() 或 NavBar.vue 统一管理
          // 避免重复跳转或竞态条件
          // if (router.currentRoute.value.name !== "Auth") {
          //   router.push("/auth");
          // }
          break;
        case 403:
          // 权限不足
          console.error("权限不足:", error.response.data);
          alert("您没有权限执行此操作。");
          break;
        case 500:
          // 服务器错误
          console.error("服务器内部错误:", error.response.data);
          alert("服务器发生错误，请稍后再试。");
          break;
        default:
          // 其他错误
          console.error(
            "请求错误:",
            error.response.data?.message || error.response.data || "未知错误"
          );
          alert(error.response.data?.message || "发生未知错误，请重试。");
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      console.error("网络请求失败，未收到响应:", error.request);
      alert("网络请求失败，请检查您的网络连接或稍后再试。");
    } else {
      // 发送请求时出了点问题
      console.error("请求配置错误:", error.message);
      alert("请求发送失败，请检查配置。");
    }
    return Promise.reject(error);
  }
);

// 初始化主题设置
function initTheme() {
  const savedTheme = localStorage.getItem("themeMode") || "light";
  if (savedTheme === "dark") {
    document.documentElement.classList.add("dark-theme-custom");
    document.documentElement.classList.remove("light-theme-custom");
  } else {
    document.documentElement.classList.add("light-theme-custom");
    document.documentElement.classList.remove("dark-theme-custom");
  }
  console.log(`初始化主题: ${savedTheme}`);
}

// 在应用挂载前初始化主题
initTheme();

// 创建应用实例
const app = createApp(App);
const pinia = createPinia();

// 使用 Pinia 状态管理
app.use(pinia);
// 使用 Naive UI
app.use(NaiveUI);
// 使用路由
app.use(router);
app.use(ElementPlus);

// 全局错误处理器示例 (您 main.js:42 的日志可能源于类似逻辑)
// 考虑将这类逻辑移到 Axios 响应拦截器或路由守卫中，以便更集中地处理
app.config.errorHandler = (err, instance, info) => {
  console.error("Global Vue error:", err, instance, info);
  // 示例：检查是否是API错误导致的跳转
  if (err && err.isAxiosError && err.response && err.response.status === 401) {
    const authStore = useAuthStore(); // 需要在 setup 函数外部能访问到 store
    if (authStore && authStore.isAuthenticated) {
      // 检查是否真的需要登出
      console.log(
        "main.js: Global error handler detected 401, attempting logout and redirect."
      );
      authStore.logout();
    }
  } else if (
    err.message &&
    err.message.includes("Failed to resolve component")
  ) {
    console.warn(
      "main.js: A component failed to resolve. Ensure it is globally registered or imported locally."
    );
  }
  // 其他全局错误处理逻辑
};

// 挂载应用实例
app.mount("#app");
