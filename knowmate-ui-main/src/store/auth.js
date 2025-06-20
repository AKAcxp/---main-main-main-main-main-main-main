import { defineStore } from "pinia";
import { ref, computed, watch } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

// Define API base URL (consider using environment variables for this)
const API_BASE_URL = "http://localhost:8080/api";

// Helper function to parse JWT and check expiration
function isTokenExpired(tokenString) {
  if (!tokenString) return true; // No token is considered expired for this check
  try {
    const parts = tokenString.split(".");
    if (parts.length !== 3) {
      console.error("AuthStore: Invalid token structure");
      return true; // Invalid structure, treat as expired
    }
    const payload = JSON.parse(atob(parts[1]));
    // exp is in seconds, Date.now() is in milliseconds
    const isExpired = payload.exp * 1000 < Date.now();
    if (isExpired) {
      console.warn(
        "AuthStore: Token is expired. Expiry:",
        new Date(payload.exp * 1000),
        "Current time:",
        new Date()
      );
    }
    return isExpired;
  } catch (e) {
    console.error("AuthStore: Failed to parse or check token expiration:", e);
    return true; // Error during parsing, treat as expired
  }
}

export const useAuthStore = defineStore("auth", () => {
  const router = useRouter();

  // 状态：尝试从 localStorage 读取 token 和 role，并检查是否过期
  const initialTokenFromStorage = localStorage.getItem("authToken");
  const initialRoleFromStorage = localStorage.getItem("role"); // 新增：从 localStorage 读取 role

  let activeToken = null;
  let activeRole = null; // 新增：用于存储 active role

  if (initialTokenFromStorage) {
    if (isTokenExpired(initialTokenFromStorage)) {
      console.warn(
        "AuthStore: Token from localStorage is expired upon initialization. Clearing."
      );
      localStorage.removeItem("authToken");
      localStorage.removeItem("role"); // 新增：清除过期的 role
    } else {
      activeToken = initialTokenFromStorage;
      activeRole = initialRoleFromStorage; // 新增：设置 active role
    }
  }
  const token = ref(activeToken);
  const role = ref(activeRole); // 新增：定义 role 状态

  const userInfo = ref({
    username: "",
    age: null,
    gender: "",
    height: null,
    weight: null,
    goal: null,
    preference: "",
    avoidFood: "",
    bmi: null,
    bmiStatus: "",
  });
  const userInfoError = ref(null); // For errors during userInfo fetching/saving

  // Getter: 判断是否登录
  const isAuthenticated = computed(() => !!token.value);

  // Add state for login errors
  const loginError = ref(null);

  // Add state for registration errors
  const registerError = ref(null);

  // Add state for code sending status
  const codeSentMessage = ref(null);

  // Add state for code sending errors
  const codeError = ref(null);

  // Action: 设置 token 和 role (登录时调用)
  function setToken(newToken, newRole) {
    // 修改：接受 newRole 参数
    if (newToken) {
      if (isTokenExpired(newToken)) {
        console.warn(
          "AuthStore: Attempted to set an already expired token. Clearing current token and not setting new one."
        );
        // Clear existing token state if any (important if called when a token might already exist)
        if (token.value) {
          localStorage.removeItem("authToken");
          localStorage.removeItem("role"); // 新增：清除 localStorage 中的 role
          token.value = null;
          role.value = null; // 新增：清除 store 中的 role
        }
        // Clear user info and set error if needed
        userInfo.value = {
          username: "",
          age: null,
          gender: "",
          height: null,
          weight: null,
          goal: null,
          preference: "",
          avoidFood: "",
          bmi: null,
          bmiStatus: "",
        };
        loginError.value = "登录失败：会话已过期或无效。"; // Inform user
        return; // Do not proceed to set the expired token
      }
      // If token is valid (not expired)
      localStorage.setItem("authToken", newToken);
      localStorage.setItem("role", newRole); // 新增：设置 localStorage 中的 role
      token.value = newToken;
      role.value = newRole; // 新增：设置 store 中的 role
      loginError.value = null;
      registerError.value = null;
    } else {
      // newToken is null (e.g., during logout or explicit clearing)
      localStorage.removeItem("authToken");
      localStorage.removeItem("role"); // 新增：清除 localStorage 中的 role
      token.value = null;
      role.value = null; // 新增：清除 store 中的 role
    }
    // Note: The global axios header setting is commented out in the original code,
    // so no changes needed there regarding that.
  }

  // Action: 清除 token (登出时调用)
  function clearToken() {
    setToken(null, null); // 修改：传递 null 给 newRole
  }

  // Action: Send Verification Code
  async function sendVerificationCode(phone) {
    codeError.value = null;
    codeSentMessage.value = null;
    try {
      const response = await axios.post(`${API_BASE_URL}/auth/code`, { phone });
      // Assuming backend returns a success message
      codeSentMessage.value = response.data?.message || "验证码已发送";
      return true;
    } catch (error) {
      console.error("Send code API error:", error.response || error.message);
      return false;
    }
  }

  // Action: Login with username and password
  async function login(username, password) {
    loginError.value = null;
    try {
      const response = await axios.post(`${API_BASE_URL}/auth/login`, {
        username: username,
        password: password,
      });

      if (response.status === 200 && response.data) {
        // 调整检查条件
        const receivedToken = response.data.token;
        const receivedRole = response.data.role; // 新增：获取 role
        console.log(
          "LOGIN SUCCESSFUL - Received Token from backend:",
          receivedToken,
          "Received Role:",
          receivedRole // 新增日志
        );
        setToken(receivedToken, receivedRole); // 修改：传递 receivedRole
        console.log(
          "LOGIN SUCCESSFUL - Token set in localStorage:",
          localStorage.getItem("authToken")
        );
        console.log(
          "LOGIN SUCCESSFUL - Role set in localStorage:", // 新增日志
          localStorage.getItem("role")
        );
        console.log(
          "LOGIN SUCCESSFUL - Token in Pinia store after setToken:",
          token.value
        );
        console.log(
          "LOGIN SUCCESSFUL - Role in Pinia store after setToken:", // 新增日志
          role.value
        );
        return true;
      } else {
        console.error(
          "Login success status but no data or unexpected data:", // 修改日志
          response.data
        );
        loginError.value = "登录响应无效或缺少数据"; // 修改错误消息
        return false;
      }
    } catch (error) {
      console.error("Login API error:", error);
      if (error.response) {
        // 后端返回了错误响应 (e.g., 400, 401, 404, 500)
        console.error("Backend error response:", error.response.data);
        // 直接使用后端返回的响应体作为错误消息 (因为它是字符串)
        loginError.value =
          error.response.data || `登录失败 (状态码：${error.response.status})`;
      } else if (error.request) {
        // 请求已发出，但没有收到响应 (网络问题、服务器无响应)
        console.error("No response received:", error.request);
        loginError.value = "无法连接到服务器，请检查网络";
      } else {
        // 设置请求时发生错误
        console.error("Error setting up request:", error.message);
        loginError.value = "请求失败，请稍后再试";
      }
      clearToken();
      return false;
    }
  }

  // Action: Login with Phone and Code
  async function loginWithPhone(phone, code) {
    loginError.value = null;
    try {
      const response = await axios.post(`${API_BASE_URL}/auth/login-phone`, {
        phone: phone,
        code: code,
      });
      if (response.status === 200 && response.data && response.data.token) {
        const receivedToken = response.data.token;
        console.log(
          "PHONE LOGIN SUCCESSFUL - Received Token from backend:",
          receivedToken
        );
        setToken(receivedToken);
        console.log(
          "PHONE LOGIN SUCCESSFUL - Token set in localStorage:",
          localStorage.getItem("authToken")
        );
        console.log(
          "PHONE LOGIN SUCCESSFUL - Token in Pinia store after setToken:",
          token.value
        );
        return true;
      } else {
        console.error(
          "Phone login success status but no token or unexpected data:",
          response.data
        );
        loginError.value = "手机登录响应无效或缺少令牌";
        return false;
      }
    } catch (error) {
      console.error("Phone Login API error:", error);
      if (error.response) {
        loginError.value =
          error.response.data?.message ||
          error.response.data ||
          `手机登录失败 (状态码：${error.response.status})`;
      } else if (error.request) {
        loginError.value = "无法连接到服务器，请检查网络";
      } else {
        loginError.value = "请求失败，请稍后再试";
      }
      clearToken();
      return false;
    }
  }

  // Action: Register user (handles different types based on input?)
  // Modify this based on how your SINGLE /api/auth/register endpoint works
  async function register(payload) {
    registerError.value = null;
    try {
      // The payload should contain necessary fields based on registration type
      // e.g., { username, password } or { email, password } or { phone, code, password }
      const response = await axios.post(
        `${API_BASE_URL}/auth/register`,
        payload
      );

      if (response.status === 201 || response.status === 200) {
        return true;
      } else {
        console.error(
          "Registration successful status but unexpected code:",
          response
        );
        registerError.value = "注册时发生未知错误";
        return false;
      }
    } catch (error) {
      console.error("Register API error:", error.response || error.message);
      if (error.response) {
        registerError.value =
          error.response.data?.message ||
          error.response.data ||
          "注册失败，请检查信息或稍后再试";
      } else {
        registerError.value = "无法连接到服务器或发生网络错误";
      }
      return false;
    }
  }

  // Action: Logout
  function logout() {
    clearToken();
    console.log("AuthStore: Logout initiated. Token after clear:", token.value);
    console.log("AuthStore: Logout initiated. Role after clear:", role.value);
    router.push({ name: "Auth" });
  }

  // Action to fetch user info from backend and update store
  async function loadUserInfo() {
    if (!token.value) {
      userInfoError.value = "用户未登录";
      return;
    }
    userInfoError.value = null;
    try {
      const response = await axios.get(`${API_BASE_URL}/user/info`, {
        headers: { Authorization: `Bearer ${token.value}` },
      });
      if (response.data && response.data.code === 200 && response.data.data) {
        const userDataFromServer = response.data.data;
        userInfo.value = {
          username: userDataFromServer.username || "",
          age: userDataFromServer.age || null,
          gender: userDataFromServer.gender || "",
          height: userDataFromServer.height || null,
          weight: userDataFromServer.weight || null,
          goal: userDataFromServer.goal || null,
          preference: userDataFromServer.preference || "",
          avoidFood: userDataFromServer.avoidFood || "",
          bmi: userDataFromServer.bmi || null,
          bmiStatus: userDataFromServer.bmiStatus || "",
        };
      } else {
        console.error(
          "Load UserInfo: Invalid response structure",
          response.data
        );
        userInfoError.value =
          response.data?.message || "获取用户信息失败：响应无效";
      }
    } catch (error) {
      console.error(
        "Load UserInfo API error:",
        error.response || error.message
      );
      if (error.response?.status === 401 || error.response?.status === 403) {
        clearToken();
        userInfoError.value = "会话已过期或无效，请重新登录。";
      } else {
        userInfoError.value =
          error.response?.data?.message || "获取用户信息时发生网络错误";
      }
    }
  }

  // Action to save user information (basic + preferences)
  async function saveUserInformation(userData) {
    if (!token.value) {
      userInfoError.value = "用户未登录，无法保存信息";
      return false;
    }
    userInfoError.value = null; // Clear previous errors

    // DEBUG: Log the token being sent
    console.log(
      "AuthStore: Token being sent in saveUserInformation:",
      token.value
    );
    try {
      const tokenParts = token.value.split(".");
      if (tokenParts.length === 3) {
        const payload = JSON.parse(atob(tokenParts[1]));
        console.log(
          "AuthStore: Decoded token payload in saveUserInformation:",
          payload
        );
        console.log(
          "AuthStore: Token expiry date in saveUserInformation:",
          new Date(payload.exp * 1000)
        );
      } else {
        console.warn(
          "AuthStore: Token in saveUserInformation does not have 3 parts."
        );
      }
    } catch (e) {
      console.error(
        "AuthStore: Error decoding token for debug in saveUserInformation:",
        e
      );
    }

    try {
      const payload = {
        username: userData.username,
        age: userData.age,
        gender: userData.gender,
        height: userData.height,
        weight: userData.weight,
        goal: userData.goal,
        preference: userData.preference === null ? "" : userData.preference,
        avoidFood: userData.avoidFood === null ? "" : userData.avoidFood,
      };
      console.log("AuthStore: Submitting to /user/info PUT:", payload);
      const response = await axios.put(`${API_BASE_URL}/user/info`, payload, {
        headers: { Authorization: `Bearer ${token.value}` },
      });

      if (response.data && response.data.code === 200) {
        if (response.data.data) {
          const updatedUserData = response.data.data;
          userInfo.value = {
            username: updatedUserData.username || userInfo.value.username,
            age:
              updatedUserData.age !== undefined
                ? updatedUserData.age
                : userInfo.value.age,
            gender:
              updatedUserData.gender !== undefined
                ? updatedUserData.gender
                : userInfo.value.gender,
            height:
              updatedUserData.height !== undefined
                ? updatedUserData.height
                : userInfo.value.height,
            weight:
              updatedUserData.weight !== undefined
                ? updatedUserData.weight
                : userInfo.value.weight,
            goal:
              updatedUserData.goal !== undefined
                ? updatedUserData.goal
                : userInfo.value.goal,
            preference:
              updatedUserData.preference !== undefined
                ? updatedUserData.preference
                : userInfo.value.preference,
            avoidFood:
              updatedUserData.avoidFood !== undefined
                ? updatedUserData.avoidFood
                : userInfo.value.avoidFood,
            bmi:
              updatedUserData.bmi !== undefined
                ? updatedUserData.bmi
                : userInfo.value.bmi,
            bmiStatus:
              updatedUserData.bmiStatus !== undefined
                ? updatedUserData.bmiStatus
                : userInfo.value.bmiStatus,
          };
        } else {
          userInfo.value = { ...userInfo.value, ...userData };
        }
        return true;
      } else {
        userInfoError.value =
          response.data?.message || "保存用户信息失败：响应无效";
        console.error(
          "Save UserInfo: Invalid response structure",
          response.data
        );
        return false;
      }
    } catch (error) {
      console.error(
        "Save UserInfo API error:",
        error.response || error.message
      );
      userInfoError.value =
        error.response?.data?.message || "保存用户信息时发生网络错误";
      return false;
    }
  }

  // Helper function to calculate BMI status
  function calculateBmiStatus() {
    const currentBmiValue = userInfo.value.bmi;
    console.log(
      `[calculateBmiStatus] Entry. BMI from store: ${currentBmiValue} (type: ${typeof currentBmiValue})`
    );

    if (currentBmiValue === null || currentBmiValue === undefined) {
      userInfo.value.bmiStatus = "未计算";
      console.log(
        `[calculateBmiStatus] BMI is null/undefined. Status set to: 未计算`
      );
      return;
    }

    const numericBmi = parseFloat(currentBmiValue);
    console.log(
      `[calculateBmiStatus] Parsed numericBMI: ${numericBmi} (type: ${typeof numericBmi})`
    );

    if (isNaN(numericBmi)) {
      userInfo.value.bmiStatus = "无法计算 (数值无效)";
      console.log(
        `[calculateBmiStatus] BMI is NaN after parseFloat. Original: ${currentBmiValue}. Status set to: 无法计算 (数值无效)`
      );
      return;
    }

    let status = "状态未知"; // Default status

    if (numericBmi < 18.5) {
      status = "偏瘦";
    } else if (numericBmi >= 18.5 && numericBmi < 24) {
      status = "正常";
    } else if (numericBmi >= 24 && numericBmi < 28) {
      status = "超重";
    } else if (numericBmi >= 28) {
      status = "肥胖";
    } else {
      // This case should ideally not be reached if numericBmi is a valid number.
      // It might indicate an issue with the number or the logic.
      status = "数据异常";
      console.error(
        `[calculateBmiStatus] LOGIC ERROR: numericBMI ${numericBmi} did not fit any standard category.`
      );
    }

    userInfo.value.bmiStatus = status;
    console.log(
      `[calculateBmiStatus] Exit. numericBMI: ${numericBmi}, Status set to: ${userInfo.value.bmiStatus}`
    );
  }

  // Watch for changes in BMI to update BMI status
  watch(
    () => userInfo.value.bmi,
    (newBmi, oldBmi) => {
      console.log(
        `AuthStore: BMI changed from ${oldBmi} to ${newBmi}. Recalculating BMI status.`
      );
      calculateBmiStatus();
    },
    { deep: true } // Use deep watch if BMI is nested, though it's top-level here
  );

  // Watch for token changes to auto-load user info or clear it
  watch(
    token,
    (newToken) => {
      if (newToken) {
        loadUserInfo();
      } else {
        // Clear userInfo when token is removed (logout)
        userInfo.value = {
          username: "",
          age: null,
          gender: "",
          height: null,
          weight: null,
          goal: null,
          preference: "",
          avoidFood: "",
          bmi: null,
          bmiStatus: "",
        };
        userInfoError.value = null;
      }
    },
    { immediate: true }
  ); // immediate: true to run on store initialization

  return {
    token,
    role, // 新增：暴露 role 状态
    userInfo,
    userInfoError,
    isAuthenticated,
    loginError,
    registerError,
    codeSentMessage,
    codeError,
    setToken,
    clearToken,
    sendVerificationCode,
    login,
    loginWithPhone,
    register,
    logout,
    loadUserInfo,
    saveUserInformation,
  };
});
