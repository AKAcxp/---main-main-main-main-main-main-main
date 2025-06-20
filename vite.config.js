import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path"; // ���� path ģ��

// ���� Element Plus �����Զ�������
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver({ importStyle: false })],
      // ����ѡ���Ե�Ϊ eslint ���������ļ������ʹ�� eslint
      dts: "src/auto-imports.d.ts",
    }),
    Components({
      resolvers: [ElementPlusResolver({ importStyle: false })],
      // ����ѡ���Ե�Ϊ eslint ���������ļ�
      dts: "src/components.d.ts",
    }),
  ],
  resolve: {
    // ��� resolve ����
    alias: {
      "@": path.resolve(__dirname, "./src"), // ���� @ ָ�� src Ŀ¼
    },
  },
  server: {
    port: 5173, // ָ���˿�Ϊ5173
    strictPort: false, // ����˿ڱ�ռ�ã�������һ�����ö˿�
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
    // �ų��������ᵽ������
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
