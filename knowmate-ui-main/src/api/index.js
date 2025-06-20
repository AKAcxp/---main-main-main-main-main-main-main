import axios from "axios";
import { useAuthStore } from "../store/auth"; // �������Pinia auth store������
import router from "../router"; // ����routerʵ�������ڴ���401�����

// ���� Axios ʵ��
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api", // �� .env �ļ���ȡ���API����·����Ĭ��Ϊ /api
  timeout: 10000, // ����ʱʱ��
});

// ����������
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    const token = authStore.token; // �� Pinia store ��ȡ token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// ��Ӧ����������ѡ�����Ƽ�����ͳһ������
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
        return Promise.reject(new Error("�Ự�ѹ��ڣ������µ�¼��"));
      }
      const backendMessage = error.response.data && error.response.data.message;
      return Promise.reject(new Error(backendMessage || error.message));
    } else if (error.request) {
      console.error("API No Response:", error.request);
      return Promise.reject(new Error("������������Ӧ�����������������ӡ�"));
    } else {
      console.error("API Request Setup Error:", error.message);
      return Promise.reject(new Error("������ʧ�ܣ����Ժ����ԡ�"));
    }
  }
);

export default api;
