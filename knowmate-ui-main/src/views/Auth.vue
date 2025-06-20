<template>
  <div class="auth-container">
    <n-card class="auth-card">
      <!-- 显示登录表单 -->
      <div v-if="showLogin">
        <n-h1 style="text-align: center; margin-bottom: 10px">登录你的账户</n-h1>
        <n-p style="text-align: center; margin-bottom: 20px">
          没有账户？
          <n-button text type="primary" @click="switchToRegister">免费注册 <n-icon :component="ChevronForwardOutline" /></n-button>
        </n-p>

        <!-- 登录方式 Tabs: 账号密码 / 手机 -->
        <n-tabs v-model:value="loginType" default-value="password" size="large" justify-content="space-evenly" animated pane-style="padding-top: 20px;">
          <n-tab-pane name="password" tab="账号密码登录">
            <!-- Password Login Form Content -->
            <n-form ref="loginPasswordFormRef" :model="loginPasswordModel" :rules="loginPasswordRules">
              <n-form-item path="username" label="">
                <n-input v-model:value="loginPasswordModel.username" placeholder="请输入账号">
                  <template #prefix><n-icon :component="PersonOutline" /></template>
                </n-input>
              </n-form-item>
              <n-form-item path="password" label="">
                <n-input v-model:value="loginPasswordModel.password" type="password" show-password-on="click" placeholder="请输入密码">
                  <template #prefix><n-icon :component="LockClosedOutline" /></template>
                </n-input>
              </n-form-item>
              <!-- Display Login API Error -->
              <n-alert title="登录错误" type="error" v-if="loginApiError" :closable="true" @close="authStore.loginError = null" style="margin-bottom: 15px">
                {{ loginApiError }}
              </n-alert>
              <n-space justify="space-between" style="width: 100%">
                <n-checkbox v-model:checked="loginPasswordModel.rememberMe">自动登录</n-checkbox>
                <n-button text type="primary">忘记密码？</n-button>
              </n-space>
              <n-form-item>
                <n-button type="primary" block attr-type="submit" @click="handlePasswordLogin" :loading="isLoading">登录</n-button>
              </n-form-item>
            </n-form>
          </n-tab-pane>

          <n-tab-pane name="phone" tab="手机验证码登录">
            <!-- Phone Login Form Content -->
            <n-form ref="loginPhoneFormRef" :model="loginPhoneModel" :rules="loginPhoneRules">
              <n-form-item path="phone" label="">
                <n-input v-model:value="loginPhoneModel.phone" placeholder="请输入手机号码">
                  <template #prefix><n-icon :component="PhonePortraitOutline" /></template>
                </n-input>
              </n-form-item>
              <n-form-item path="code" label="">
                <n-input-group>
                  <n-input v-model:value="loginPhoneModel.code" placeholder="请输入验证码" :disabled="isCountingDown && isLoading">
                    <template #prefix><n-icon :component="ShieldCheckmarkOutline" /></template>
                  </n-input>
                  <n-button type="primary" ghost @click="handleGetCodeClick('login')" :disabled="isCountingDown" :loading="isLoading && !isCountingDown">
                    {{ getCodeButtonText }}
                  </n-button>
                </n-input-group>
                <!-- Display Code Sending Error/Success -->
                <n-alert type="error" v-if="codeApiError" :closable="true" @close="authStore.codeError = null" style="margin-top: 5px">
                  {{ codeApiError }}
                </n-alert>
              </n-form-item>
              <!-- Display Login API Error -->
              <n-alert title="登录错误" type="error" v-if="loginApiError" :closable="true" @close="authStore.loginError = null" style="margin-bottom: 15px">
                {{ loginApiError }}
              </n-alert>
              <n-form-item>
                <n-button type="primary" block attr-type="submit" @click="handlePhoneLogin" :loading="isLoading">登录</n-button>
              </n-form-item>
            </n-form>
          </n-tab-pane>
        </n-tabs>

        <n-divider title-placement="center">其他登录方式</n-divider>
        <n-space justify="center" size="large">
          <n-button text><n-icon size="24" :component="LogoWechat" /></n-button>
          <n-button text><n-icon size="24" :component="LogoGithub" /></n-button>
        </n-space>
      </div>

      <!-- 显示注册表单 -->
      <div v-else>
        <n-h1 style="text-align: center; margin-bottom: 10px">注册你的账户</n-h1>
        <n-p style="text-align: center; margin-bottom: 20px">
          已有账户？
          <n-button text type="primary" @click="switchToLogin">前去登录 <n-icon :component="ChevronForwardOutline" /></n-button>
        </n-p>

        <!-- 注册方式 Tabs: 手机 / 邮箱 -->
        <n-tabs v-model:value="registerType" default-value="email" size="large" justify-content="space-evenly" animated pane-style="padding-top: 20px;">
          <n-tab-pane name="email" tab="邮箱注册">
            <!-- Email Register Form Content -->
            <n-form ref="registerEmailFormRef" :model="registerEmailModel" :rules="registerEmailRules">
              <n-form-item path="username" label="">
                <n-input v-model:value="registerEmailModel.username" placeholder="请输入账号">
                  <template #prefix><n-icon :component="PersonOutline" /></template>
                </n-input>
              </n-form-item>
              <n-form-item path="email" label="">
                <n-input v-model:value="registerEmailModel.email" placeholder="请输入邮箱地址">
                  <template #prefix><n-icon :component="MailOutline" /></template>
                </n-input>
              </n-form-item>
              <n-form-item path="password" label="">
                <n-input v-model:value="registerEmailModel.password" type="password" show-password-on="click" placeholder="请输入密码">
                  <template #prefix><n-icon :component="LockClosedOutline" /></template>
                </n-input>
              </n-form-item>
              <n-form-item path="confirmPassword" label="">
                <n-input v-model:value="registerEmailModel.confirmPassword" type="password" show-password-on="click" placeholder="请再次输入密码">
                  <template #prefix><n-icon :component="LockClosedOutline" /></template>
                </n-input>
              </n-form-item>
              <!-- Display Register API Error -->
              <n-alert title="注册错误" type="error" v-if="registerApiError" :closable="true" @close="authStore.registerError = null" style="margin-bottom: 15px">
                {{ registerApiError }}
              </n-alert>
              <n-form-item>
                <n-button type="primary" block attr-type="submit" @click="handleEmailRegister" :loading="isLoading">注册</n-button>
              </n-form-item>
            </n-form>
          </n-tab-pane>

          <n-tab-pane name="phone" tab="手机注册">
            <!-- Phone Register Form Content -->
            <n-form ref="registerPhoneFormRef" :model="registerPhoneModel" :rules="registerPhoneRules">
              <n-form-item path="username" label="">
                <n-input v-model:value="registerPhoneModel.username" placeholder="请输入账号">
                  <template #prefix><n-icon :component="PersonOutline" /></template>
                </n-input>
              </n-form-item>
              <n-form-item path="phone" label="">
                <n-input v-model:value="registerPhoneModel.phone" placeholder="请输入手机号码">
                  <template #prefix><n-icon :component="PhonePortraitOutline" /></template>
                </n-input>
              </n-form-item>
              <n-form-item path="code" label="">
                <n-input-group>
                  <n-input v-model:value="registerPhoneModel.code" placeholder="请输入验证码" :disabled="isCountingDown && isLoading">
                    <template #prefix><n-icon :component="ShieldCheckmarkOutline" /></template>
                  </n-input>
                  <n-button type="primary" ghost @click="handleGetCodeClick('register')" :disabled="isCountingDown" :loading="isLoading && !isCountingDown">
                    {{ getCodeButtonText }}
                  </n-button>
                </n-input-group>
                <!-- Display Code Sending Error/Success -->
                <n-alert type="error" v-if="codeApiError" :closable="true" @close="authStore.codeError = null" style="margin-top: 5px">
                  {{ codeApiError }}
                </n-alert>
              </n-form-item>
              <n-form-item path="password" label="">
                <n-input v-model:value="registerPhoneModel.password" type="password" show-password-on="click" placeholder="请输入密码">
                  <template #prefix><n-icon :component="LockClosedOutline" /></template>
                </n-input>
              </n-form-item>
              <n-form-item path="confirmPassword" label="">
                <n-input v-model:value="registerPhoneModel.confirmPassword" type="password" show-password-on="click" placeholder="请再次输入密码">
                  <template #prefix><n-icon :component="LockClosedOutline" /></template>
                </n-input>
              </n-form-item>
              <!-- Display Register API Error -->
              <n-alert title="注册错误" type="error" v-if="registerApiError" :closable="true" @close="authStore.registerError = null" style="margin-bottom: 15px">
                {{ registerApiError }}
              </n-alert>
              <n-form-item>
                <n-button type="primary" block attr-type="submit" @click="handlePhoneRegister" :loading="isLoading">注册</n-button>
              </n-form-item>
            </n-form>
          </n-tab-pane>
        </n-tabs>
      </div>
    </n-card>
  </div>
</template>

<script setup>
import { ref, h, computed, onBeforeUnmount } from "vue";
import { useRouter, useRoute } from "vue-router";
import { NCard, NTabs, NTabPane, NForm, NFormItem, NInput, NButton, NCheckbox, NIcon, NH1, NP, NSpace, NInputGroup, NDivider, useMessage, NAlert } from "naive-ui";
import { PersonOutline, LockClosedOutline, ChevronForwardOutline, PhonePortraitOutline, ShieldCheckmarkOutline, LogoWechat, LogoGithub, MailOutline } from "@vicons/ionicons5";
import { useAuthStore } from "../store/auth";

const router = useRouter();
const route = useRoute();
const message = useMessage();
const authStore = useAuthStore();

const showLogin = ref(true);
const isLoading = ref(false);

// Expose store errors/messages to the template
const loginApiError = computed(() => authStore.loginError);
const registerApiError = computed(() => authStore.registerError);
const codeApiError = computed(() => authStore.codeError); // Code sending error
const codeSentMsg = computed(() => authStore.codeSentMessage); // Code sent success msg

// Login state
const loginType = ref("password");
const loginPasswordFormRef = ref(null);
const loginPasswordModel = ref({ username: "", password: "", rememberMe: true });
const loginPasswordRules = {
  username: { required: true, message: "请输入账号", trigger: "blur" },
  password: { required: true, message: "请输入密码", trigger: "blur" },
};
const loginPhoneFormRef = ref(null);
const loginPhoneModel = ref({ phone: "", code: "" });
const loginPhoneRules = {
  phone: { required: true, message: "请输入手机号码", trigger: "blur" },
  code: { required: true, message: "请输入验证码", trigger: "blur" },
};

// Register state
const registerType = ref("email");
const registerPhoneFormRef = ref(null);
const registerPhoneModel = ref({ username: "", phone: "", code: "", password: "", confirmPassword: "" });
const validatePasswordSamePhone = (rule, value) => value === registerPhoneModel.value.password;
const registerPhoneRules = {
  username: { required: true, message: "请输入账号", trigger: "blur" },
  phone: { required: true, message: "请输入手机号码", trigger: "blur" },
  code: { required: true, message: "请输入验证码", trigger: "blur" },
  password: { required: true, message: "请输入密码", trigger: "blur" },
  confirmPassword: [
    { required: true, message: "请再次输入密码", trigger: ["input", "blur"] },
    { validator: validatePasswordSamePhone, message: "两次密码输入不一致", trigger: ["blur", "password-input"] },
  ],
};
const registerEmailFormRef = ref(null);
const registerEmailModel = ref({ username: "", email: "", password: "", confirmPassword: "" });
const validatePasswordSameEmail = (rule, value) => value === registerEmailModel.value.password;
const registerEmailRules = {
  username: { required: true, message: "请输入账号", trigger: "blur" },
  email: { required: true, type: "email", message: "请输入有效的邮箱地址", trigger: "blur" },
  password: { required: true, message: "请输入密码", trigger: "blur" },
  confirmPassword: [
    { required: true, message: "请再次输入密码", trigger: ["input", "blur"] },
    { validator: validatePasswordSameEmail, message: "两次密码输入不一致", trigger: ["blur", "password-input"] },
  ],
};

// --- Get Code Logic ---
const isCountingDown = ref(false);
const countdownSeconds = ref(60);
let countdownTimer = null;

const getCodeButtonText = computed(() => {
  return isCountingDown.value ? `${countdownSeconds.value}s后重发` : "获取验证码";
});

const handleGetCodeClick = async (formType) => {
  // Add formType parameter
  let phone = "";
  if (formType === "login") {
    // Validate phone number for login form first (optional but recommended)
    const errors = await loginPhoneFormRef.value
      ?.validate(
        (valid) => {},
        (rule) => rule?.key === "phone"
      )
      .catch(() => {});
    if (errors && errors.length > 0) {
      message.error("请输入有效的手机号码");
      return;
    }
    phone = loginPhoneModel.value.phone;
  } else if (formType === "register") {
    // Validate phone number for register form first
    const errors = await registerPhoneFormRef.value
      ?.validate(
        (valid) => {},
        (rule) => rule?.key === "phone"
      )
      .catch(() => {});
    if (errors && errors.length > 0) {
      message.error("请输入有效的手机号码");
      return;
    }
    phone = registerPhoneModel.value.phone;
  }

  if (!phone) {
    // Double check just in case
    message.error("请输入手机号码");
    return;
  }

  isCountingDown.value = true;
  isLoading.value = true; // Also show general loading for API call
  authStore.codeError = null; // Clear previous error
  authStore.codeSentMessage = null; // Clear previous message

  const success = await authStore.sendVerificationCode(phone);
  isLoading.value = false;

  if (success) {
    message.success(authStore.codeSentMessage || "验证码已发送");
    // Start countdown
    countdownSeconds.value = 60;
    countdownTimer = setInterval(() => {
      countdownSeconds.value--;
      if (countdownSeconds.value <= 0) {
        clearInterval(countdownTimer);
        isCountingDown.value = false;
      }
    }, 1000);
  } else {
    message.error(authStore.codeError || "发送验证码失败");
    isCountingDown.value = false; // Stop countdown on error
  }
};

// Clear timer when component is unmounted
onBeforeUnmount(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
});

// --- Other Handlers ---
const switchToRegister = () => {
  showLogin.value = false;
  authStore.loginError = null;
  authStore.registerError = null;
};
const switchToLogin = () => {
  showLogin.value = true;
  authStore.loginError = null;
  authStore.registerError = null;
};

const handlePasswordLogin = (e) => {
  e.preventDefault();
  if (isLoading.value) return;
  loginPasswordFormRef.value?.validate(async (errors) => {
    if (!errors) {
      isLoading.value = true;
      const loggedIn = await authStore.login(loginPasswordModel.value.username, loginPasswordModel.value.password);
      isLoading.value = false;
      if (loggedIn) {
        message.success("登录成功");
        const redirectPath = route.query.redirect || "/home";
        router.push(redirectPath);
      } else {
        // Error message is now handled by the computed property loginApiError displayed in the template
        message.error(authStore.loginError || "登录失败，请检查账号或密码");
      }
    } else {
      message.error("请填写账号和密码");
    }
  });
};

const handlePhoneLogin = (e) => {
  e.preventDefault();
  if (isLoading.value) return;
  loginPhoneFormRef.value?.validate(async (errors) => {
    if (!errors) {
      isLoading.value = true;
      const loggedIn = await authStore.loginWithPhone(loginPhoneModel.value.phone, loginPhoneModel.value.code);
      isLoading.value = false;
      if (loggedIn) {
        message.success("手机验证码登录成功");
        const redirectPath = route.query.redirect || "/home";
        router.push(redirectPath);
      }
    } else {
      message.error("请填写手机号和验证码");
    }
  });
};

const handlePhoneRegister = (e) => {
  e.preventDefault();
  if (isLoading.value) return;
  registerPhoneFormRef.value?.validate(async (errors) => {
    if (!errors) {
      isLoading.value = true;
      // Prepare payload for the generic register action
      const payload = {
        username: registerPhoneModel.value.username,
        phone: registerPhoneModel.value.phone,
        code: registerPhoneModel.value.code,
        password: registerPhoneModel.value.password,
      };
      const success = await authStore.register(payload);
      isLoading.value = false;
      if (success) {
        message.success("手机注册成功！请登录。");
        showLogin.value = true;
      }
    } else {
      message.error("请填写完整信息并确保两次密码一致");
    }
  });
};

const handleEmailRegister = (e) => {
  e.preventDefault();
  if (isLoading.value) return;
  registerEmailFormRef.value?.validate(async (errors) => {
    if (!errors) {
      isLoading.value = true;
      const payload = {
        username: registerEmailModel.value.username,
        email: registerEmailModel.value.email,
        password: registerEmailModel.value.password,
      };
      const success = await authStore.register(payload);
      isLoading.value = false;
      if (success) {
        message.success("邮箱注册成功！请登录。");
        showLogin.value = true;
      }
    } else {
      message.error("请填写完整信息并确保两次密码一致");
    }
  });
};
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  padding: 20px;
  box-sizing: border-box;
}

.auth-card {
  max-width: 400px;
  width: 100%;
  border-radius: 8px;
}

a {
  text-decoration: none;
}
</style>
