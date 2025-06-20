import axios from "axios";
import { useAuthStore } from "@/store/auth"; // Import the auth store

// ��¼�ӿ�
export function login(username, password) {
  return axios.post("/api/auth/login", { username, password });
}

// ע��ӿ�
export function register(
  username,
  email,
  password,
  age,
  gender,
  height,
  weight
) {
  return axios.post("/api/auth/register", {
    username,
    email,
    password,
    age,
    gender,
    height,
    weight,
  });
}

// ����axios�������������Զ�Я��token
axios.interceptors.request.use(
  (config) => {
    // Get the store instance inside the interceptor
    // This assumes Pinia is initialized when requests are made.
    const authStore = useAuthStore();
    const token = authStore.token; // Access the token directly from the store's state

    if (token) {
      config.headers.Authorization = "Bearer " + token;
    }
    return config;
  },
  (error) => Promise.reject(error)
);
