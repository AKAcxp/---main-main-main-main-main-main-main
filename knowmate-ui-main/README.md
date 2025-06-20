# KnowMate - 前端

这是一个使用 Vue 3、Vite 和 Naive UI 构建的问答式社区平台前端项目。

## 技术栈

- **框架**: [Vue 3](https://vuejs.org/) (使用 `<script setup>`)
- **构建工具**: [Vite](https://vitejs.dev/)
- **UI 库**: [Naive UI](https://www.naiveui.com/)
- **路由**: [Vue Router](https://router.vuejs.org/)
- **状态管理**: [Pinia](https://pinia.vuejs.org/)
- **图标库**: [Vicons](https://www.vicons.org/) (集成了 Ionicons, Font Awesome 等)
- **HTTP 请求**: (推荐使用 [Axios](https://axios-http.com/) 或 Fetch API，待集成)

## 功能

- 用户认证：
  - 统一的认证页面 (`/auth`)
  - 账号密码登录
  - 手机验证码登录 (UI)
  - 手机注册 (UI)
  - 邮箱注册 (UI)
  - 通过导航守卫进行路由保护
- 基本布局：包含顶部导航栏、可折叠侧边栏和内容区域
- 主题切换：通过悬浮按钮在亮色和暗色主题间切换
- 基本视图：首页 (`/home`)、关于 (`/about`)

## 项目设置

确保你已安装 [Node.js](https://nodejs.org/) (推荐 LTS 版本) 和 npm/yarn/pnpm。

1. **克隆仓库** (如果适用)

    ```bash
    git clone <your-repository-url>
    cd knowmate-ui
    ```

2. **安装依赖**

    ```bash
    npm install
    # 或者
    yarn install
    # 或者
    pnpm install
    ```

3. **运行开发服务器**

    ```bash
    npm run dev
    # 或者
    yarn dev
    # 或者
    pnpm dev
    ```

    项目将在 `http://localhost:5173` (或指定的端口) 运行，并支持热模块替换 (HMR)。

## 构建项目

```bash
npm run build
# 或者
# yarn build
# 或者
# pnpm build
```

这将会在 `dist` 目录下生成用于生产环境的优化文件。

## 目录结构 (简化)

```txt
.knowmate-ui/
├── public/             # 静态资源 (会被直接复制)
├── src/
│   ├── assets/         # 静态资源 (会被 Vite 处理)
│   ├── components/     # 可复用 Vue 组件 (NavBar.vue, SideBar.vue)
│   ├── router/         # 路由配置 (index.js)
│   ├── store/          # Pinia 状态管理 (auth.js, theme.js)
│   ├── views/          # 页面级 Vue 组件 (Home.vue, About.vue, Auth.vue)
│   ├── App.vue         # 根组件
│   └── main.js         # 应用入口文件
├── index.html          # HTML 入口
├── package.json        # 项目依赖和脚本
├── vite.config.js      # Vite 配置文件
└── README.md           # 项目说明文档
```

## 待办事项 / 未来开发

- 实现真实的 API 调用替换模拟逻辑 (登录、注册、获取验证码等)
- 完善表单验证 (例如手机号格式、密码强度)
- 添加更详细的用户个人资料页面和设置页面
- 实现帖子发布、查看、点赞、评论等核心功能
- 添加单元测试和端到端测试
- 优化性能和代码结构
- 实现第三方登录逻辑
