import axios from "axios";
import { useAuthStore } from "@/store/auth"; // 确保这个路径相对于 src 是正确的 (e.g., src/store/auth.js)

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080", // 您的后端 API 基础 URL
  timeout: 30000,
});

// 请求拦截器：在每个请求发送前自动添加 Authorization header
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("authToken");
    console.log("请求拦截器: 获取到token:", token);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log(
        "请求拦截器: 添加Authorization header:",
        config.headers.Authorization
      );
    }
    console.log("请求拦截器: 发送请求到:", config.url);
    return config;
  },
  (error) => {
    // 对请求错误做些什么
    console.error("请求拦截器: 请求错误:", error);
    return Promise.reject(error);
  }
);

// 可选：响应拦截器，用于统一处理响应错误等
axiosInstance.interceptors.response.use(
  (response) => {
    // 对响应数据做点什么
    console.log("响应拦截器: 收到响应:", response.config.url, response.status);
    if (response.data) {
      console.log("响应拦截器: 响应数据:", response.data);
    }
    return response;
  },
  (error) => {
    // 对响应错误做点什么，例如：
    console.error("响应拦截器: 响应错误:", error);
    if (error.response) {
      console.error("响应拦截器: 错误状态码:", error.response.status);
      console.error("响应拦截器: 错误数据:", error.response.data);

      if (error.response.status === 401) {
        console.error("响应拦截器: 未授权，需要登录");
        // 处理未授权，例如跳转到登录页
        const authStore = useAuthStore();
        authStore.logout(); // 假设有 logout action
        // router.push('/login'); // 假设有 router 实例
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
