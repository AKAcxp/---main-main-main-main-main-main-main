import axios from "axios";
import { useAuthStore } from "../store/auth"; // 假设你的Pinia auth store在这里
import router from "../router"; // 引入router实例，用于处理401等情况

// 创建 Axios 实例
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api", // 从 .env 文件读取后端API基础路径，默认为 /api
  timeout: 10000, // 请求超时时间
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    const token = authStore.token; // 从 Pinia store 获取 token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器（可选，但推荐用于统一错误处理）
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response) {
      console.error("API Error Response:", error.response);
      if (error.response.status === 401) {
        const authStore = useAuthStore();
        authStore.logout();
        router
          .push({
            name: "Auth",
            query: { redirect: router.currentRoute.value.fullPath },
          })
          .catch((err) => {
            if (err.name !== "NavigationDuplicated") {
              console.error("Router push error:", err);
            }
          });
        return Promise.reject(new Error("会话已过期，请重新登录。"));
      }
      const backendMessage = error.response.data && error.response.data.message;
      return Promise.reject(new Error(backendMessage || error.message));
    } else if (error.request) {
      console.error("API No Response:", error.request);
      return Promise.reject(new Error("网络请求无响应，请检查您的网络连接。"));
    } else {
      console.error("API Request Setup Error:", error.message);
      return Promise.reject(new Error("请求发送失败，请稍后再试。"));
    }
  }
);

export default api;
