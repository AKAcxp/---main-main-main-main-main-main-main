import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path"; // 导入 path 模块

// 引入 Element Plus 按需自动导入插件
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver({ importStyle: false })],
      // 可以选择性地为 eslint 生成声明文件，如果使用 eslint
      dts: "src/auto-imports.d.ts",
    }),
    Components({
      resolvers: [ElementPlusResolver({ importStyle: false })],
      // 可以选择性地为 eslint 生成声明文件
      dts: "src/components.d.ts",
    }),
  ],
  resolve: {
    // 添加 resolve 配置
    alias: {
      "@": path.resolve(__dirname, "./src"), // 设置 @ 指向 src 目录
    },
  },
  server: {
    port: 5173, // 指定端口为5173
    strictPort: false, // 如果端口被占用，尝试下一个可用端口
    proxy: {
      "/api": {
        target: "http://localhost:8080", // Your backend server address
        changeOrigin: true,
      },
    },
  },
  optimizeDeps: {
    include: [
      "dayjs",
      "dayjs/plugin/localeData.js",
      "dayjs/plugin/customParseFormat.js",
      "dayjs/plugin/advancedFormat.js",
      "dayjs/plugin/dayOfYear.js",
      "dayjs/plugin/isSameOrAfter.js",
      "dayjs/plugin/isSameOrBefore.js",
      "dayjs/plugin/weekOfYear.js",
      "dayjs/plugin/weekYear.js",
    ],
    // 排除报错中提到的依赖
    exclude: [
      "element-plus",
      "naive-ui",
      "@vueuse/core",
      "vue-router",
      "pinia",
    ],
  },
  build: {
    commonjsOptions: {
      include: [/node_modules/, /dayjs/],
      transformMixedEsModules: true,
      defaultIsModuleExports: "auto",
    },
  },
});
