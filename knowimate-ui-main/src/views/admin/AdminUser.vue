<template>
  <div>
    <h1>用户管理 (Admin User Management)</h1>
    <n-space vertical>
      <n-button type="primary" @click="fetchUsers">刷新用户列表</n-button>
      <div style="overflow-x: auto;">
        <n-data-table
          :columns="columns"
          :data="users"
          :pagination="pagination"
          :bordered="false"
          :loading="loading"
          :scroll-x="1500"
        />
      </div>
    </n-space>

    <!-- 编辑用户角色对话框 -->
    // ... existing code ...

    const createColumns = ({ editRole, toggleUserStatus, deleteUser }) => {
      return [
        { title: 'ID', key: 'id', width: 60 },
        { title: '用户名', key: 'username', width: 120 },
        { title: '邮箱', key: 'email', width: 150 },
        {
          title: '角色',
          key: 'role',
          width: 120,
          // ... existing code ...
        },
        { title: '更新时间', key: 'updateTime', width: 180 },
        {
          title: '操作',
          key: 'actions',
          width: 250,
          render(row) {
            // ... existing code ...
          }
        }
      ]
    }
  </div>
</template>

const createColumns = ({ editRole, toggleUserStatus, deleteUser }) => {
  return [
    { title: 'ID', key: 'id', width: 60 },
    { title: '用户名', key: 'username', width: 120 },
    { title: '邮箱', key: 'email', width: 150 },
    {
      title: '角色',
      key: 'role',
      width: 120,
      // ... existing code ...
    },
    { title: '更新时间', key: 'updateTime', width: 180 },
    {
      title: '操作',
      key: 'actions',
      width: 250,
      render(row) {
        // ... existing code ...
      }
    }
  ]
} 