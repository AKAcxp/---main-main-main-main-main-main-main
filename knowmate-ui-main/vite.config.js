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
  // 定义环境变量
  define: {
    "import.meta.env.VITE_API_URL": JSON.stringify("http://localhost:8080"),
  },
  resolve: {
    // 添加 resolve 配置
    alias: {
      "@": path.resolve(__dirname, "./src"), // 设置 @ 指向 src 目录
    },
  },
  server: {
    port: 5173, // 指定端口为5173
    strictPort: true, // 如果端口已被占用，则直接退出
    proxy: {
      // Example: simple string shorthand
      // '/foo': 'http://localhost:4567/foo',

      // Example: with options
      "/api": {
        target: "http://localhost:8080", // Your backend server address
        changeOrigin: true,
        // Uncomment the rewrite rule if your backend API paths do NOT start with /api
        // e.g., backend is /user/info, frontend calls /api/user/info
        // rewrite: (path) => path.replace(/^\/api/, '')
      },

      // Example: Proxying websockets or socket.io
      // '/socket.io': {
      //   target: 'ws://localhost:3000',
      //   ws: true
      // }
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
    exclude: [
      "element-plus",
      "naive-ui",
      "vue",
      "vue-router",
      "pinia",
      "axios",
      "@vueuse/core",
      "@element-plus/icons-vue",
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
