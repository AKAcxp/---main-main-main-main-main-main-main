import { defineStore } from "pinia";
import { ref, computed, watch, onMounted, onUnmounted } from "vue";
import { darkTheme } from "naive-ui";

// Function to get the initial theme from localStorage or system preference
function getInitialTheme() {
  const savedTheme = localStorage.getItem("themeMode");
  if (savedTheme) {
    return savedTheme;
  }
  // Check system preference if no saved theme
  if (
    window.matchMedia &&
    window.matchMedia("(prefers-color-scheme: dark)").matches
  ) {
    return "dark";
  }
  return "light"; // Default to light
}

// 应用主题类到HTML根元素
function applyThemeClass(theme) {
  if (theme === "dark") {
    document.documentElement.classList.add("dark-theme-custom");
    document.documentElement.classList.remove("light-theme-custom");
  } else {
    document.documentElement.classList.add("light-theme-custom");
    document.documentElement.classList.remove("dark-theme-custom");
  }
  console.log(`应用主题类: ${theme}`);
}

export const useThemeStore = defineStore("theme", () => {
  // State: Initialize themeMode using the new function
  const themeMode = ref(getInitialTheme());

  // Getter: Return the Naive UI theme object based on the current mode.
  const naiveTheme = computed(() => {
    return themeMode.value === "dark" ? darkTheme : null;
  });

  // Action: Toggle the theme mode (can be triggered manually if needed)
  function toggleTheme() {
    themeMode.value = themeMode.value === "light" ? "dark" : "light";
  }

  // Watch for changes in themeMode and save to localStorage & update body class.
  watch(
    themeMode,
    (newMode) => {
      localStorage.setItem("themeMode", newMode);
      applyThemeClass(newMode);

      // 强制重新应用Element Plus组件样式
      setTimeout(() => {
        const event = new Event("themechange");
        window.dispatchEvent(event);
      }, 50);
    },
    { immediate: true }
  ); // immediate: true to apply on initial load

  // --- New code to listen to OS theme changes ---
  const matcher = window.matchMedia("(prefers-color-scheme: dark)");

  const systemThemeChangeHandler = (event) => {
    const newSystemTheme = event.matches ? "dark" : "light";
    console.log(`System theme changed to: ${newSystemTheme}`);
    if (themeMode.value !== newSystemTheme) {
      themeMode.value = newSystemTheme; // Update our store's themeMode
    }
  };

  onMounted(() => {
    // Set initial themeMode based on system preference if no localStorage value was found
    // This is already handled by getInitialTheme, but we add the listener here.
    matcher.addEventListener("change", systemThemeChangeHandler);
    console.log(
      `Initial OS theme listener added. Current OS dark mode: ${matcher.matches}`
    );

    // 确保主题类被正确应用
    applyThemeClass(themeMode.value);
  });

  onUnmounted(() => {
    matcher.removeEventListener("change", systemThemeChangeHandler);
  });
  // --- End of new code ---

  return {
    themeMode,
    naiveTheme,
    toggleTheme,
  };
});
