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
    strictPort: true, // ����˿��ѱ�ռ�ã���ֱ���˳�
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
    exclude: ["element-plus"],
  },
  build: {
    commonjsOptions: {
      include: [/node_modules/, /dayjs/],
      transformMixedEsModules: true,
      defaultIsModuleExports: "auto",
    },
  },
});
