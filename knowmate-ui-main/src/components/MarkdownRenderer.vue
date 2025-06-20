<template>
  <div :class="['markdown-body', themeClass]" v-html="renderedMarkdown"></div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from "vue";
import { useThemeStore } from "../store/theme";
import MarkdownIt from "markdown-it";
import "github-markdown-css/github-markdown.css";

const props = defineProps({
  source: {
    type: String,
    required: true,
    default: "",
  },
});

const md = new MarkdownIt();
const renderedMarkdown = ref("");

// 获取主题存储
const themeStore = useThemeStore();

// 计算当前主题类名
const themeClass = computed(() => {
  return themeStore.themeMode === "dark" ? "dark-theme" : "light-theme";
});

// 计算属性或 watch 来处理 source 的变化
// 使用 computed 更为推荐，因为它会自动处理依赖关系
const renderedHtml = computed(() => {
  return md.render(props.source);
});

// 使用 watch 也可以实现，但 computed 通常更简洁
watch(
  () => props.source,
  (newSource) => {
    renderedMarkdown.value = md.render(newSource);
  },
  { immediate: true }
);

onMounted(() => {
  renderedMarkdown.value = md.render(props.source);
});
</script>

<style scoped>
.dark-theme {
  background-color: #101014;
  color: #d4d4d4;
}

.light-theme {
  background-color: #ffffff;
  color: #000000;
}
</style>
