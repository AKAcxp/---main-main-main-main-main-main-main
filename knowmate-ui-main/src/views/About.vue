<!-- src/views/About.vue -->
<template>
  <div class="about-container">
    <n-card>
      <template #header>
        <div class="header">
          <n-h1>关于 知友 (KnowMate)</n-h1>
        </div>
      </template>

      <n-space vertical size="large">
        <div class="section">
        <n-h2>项目简介</n-h2>
        <n-p>
            知友 (KnowMate) 是一个旨在促进知识分享与交流的问答社区平台。用户可以在这里提出问题、分享见解、参与讨论，共同构建一个互助学习的良好氛围。
        </n-p>
        </div>

        <div class="section">
          <n-h2>技术栈</n-h2>
          <n-ul>
            <n-li>
              <n-text strong>前端框架：</n-text>
              <n-text>Vue 3 + Vite</n-text>
            </n-li>
            <n-li>
              <n-text strong>UI 组件：</n-text>
              <n-text>Naive UI</n-text>
            </n-li>
            <n-li>
              <n-text strong>状态管理：</n-text>
              <n-text>Pinia</n-text>
            </n-li>
            <n-li>
              <n-text strong>路由管理：</n-text>
              <n-text>Vue Router</n-text>
            </n-li>
            <n-li>
              <n-text strong>网络请求：</n-text>
              <n-text>Axios</n-text>
            </n-li>
          </n-ul>
        </div>

        <div class="section">
          <n-h2>主要功能</n-h2>
          <n-ul>
            <n-li>用户认证与授权</n-li>
            <n-li>问题发布与管理</n-li>
            <n-li>回答与评论系统</n-li>
            <n-li>用户个人中心</n-li>
            <n-li>标签分类系统</n-li>
            <n-li>搜索功能</n-li>
          </n-ul>
        </div>

        <div class="section">
        <n-h2>联系我们</n-h2>
        <n-p>
            如果您有任何问题或建议，欢迎通过以下方式与我们联系：
        </n-p>
          <n-space vertical>
            <n-button text class="icon-button">
              <Icon :component="MailOutlined" />
              support@knowmate.com
            </n-button>
            <n-button text class="icon-button">
              <Icon :component="GithubOutlined" />
              GitHub
            </n-button>
          </n-space>
        </div>

        <div class="section">
          <n-h2>版本信息</n-h2>
          <n-descriptions bordered>
            <n-descriptions-item label="当前版本">
              v1.0.0
            </n-descriptions-item>
            <n-descriptions-item label="发布日期">
              2024-03-14
            </n-descriptions-item>
            <n-descriptions-item label="最后更新">
              2024-03-14
            </n-descriptions-item>
          </n-descriptions>
        </div>
      </n-space>
    </n-card>
  </div>
</template>

<script setup>
import { h } from 'vue'
import { NCard, NH1, NH2, NP, NSpace, NButton, NIcon, NUl, NLi, NText, NDescriptions, NDescriptionsItem } from 'naive-ui'
import { MailOutlined, GithubOutlined } from '@vicons/antd'
import { Icon } from '@vicons/utils'

// 替换原来的图标使用方式
const renderIcon = (icon) => {
  return () => h(Icon, { component: icon })
}
</script>

<style scoped>
.about-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.header {
  text-align: center;
  margin-bottom: 20px;
}

.section {
  margin-bottom: 30px;
}

.section :deep(h2) {
  margin-bottom: 16px;
  color: var(--primary-color);
}

.section :deep(p) {
  line-height: 1.6;
  margin-bottom: 16px;
}

.section :deep(ul) {
  margin-bottom: 16px;
}

.section :deep(li) {
  margin-bottom: 8px;
}

.icon-button {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-button :deep(svg) {
  width: 18px;
  height: 18px;
}
</style>
