import axios from "axios";
import { useAuthStore } from "@/store/auth"; // ȷ�����·������� src ����ȷ�� (e.g., src/store/auth.js)

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080", // ���ĺ�� API ���� URL
  timeout: 30000,
});

// ��������������ÿ��������ǰ�Զ���� Authorization header
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("authToken");
    console.log("����������: ��ȡ��token:", token);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log(
        "����������: ���Authorization header:",
        config.headers.Authorization
      );
    }
    console.log("����������: ��������:", config.url);
    return config;
  },
  (error) => {
    // �����������Щʲô
    console.error("����������: �������:", error);
    return Promise.reject(error);
  }
);

// ��ѡ����Ӧ������������ͳһ������Ӧ�����
axiosInstance.interceptors.response.use(
  (response) => {
    // ����Ӧ��������ʲô
    console.log("��Ӧ������: �յ���Ӧ:", response.config.url, response.status);
    if (response.data) {
      console.log("��Ӧ������: ��Ӧ����:", response.data);
    }
    return response;
  },
  (error) => {
    // ����Ӧ��������ʲô�����磺
    console.error("��Ӧ������: ��Ӧ����:", error);
    if (error.response) {
      console.error("��Ӧ������: ����״̬��:", error.response.status);
      console.error("��Ӧ������: ��������:", error.response.data);

      if (error.response.status === 401) {
        console.error("��Ӧ������: δ��Ȩ����Ҫ��¼");
        // ����δ��Ȩ��������ת����¼ҳ
        const authStore = useAuthStore();
        authStore.logout(); // ������ logout action
        // router.push('/login'); // ������ router ʵ��
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
