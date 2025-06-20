<template>
  <div class="admin-user">
    <h1>用户管理 (Admin User Management)</h1>
    <n-space vertical>
      <n-button type="primary" @click="fetchUsers">刷新用户列表</n-button>
      <n-data-table
        :columns="columns"
        :data="users"
        :pagination="pagination"
        :bordered="false"
        :loading="loading"
        :scroll-x="2500"
        class="user-table"
      />
    </n-space>

    <!-- 编辑用户角色对话框 -->
    <n-modal v-model="showEditRoleModal" preset="dialog" title="编辑用户角色" transform-origin="center" class="user-modal">
      <!-- 临时简化模态框内容，以排查点击问题 -->
      <p>这是一个测试按钮，请尝试点击。</p>
      <template #action>
        <n-button @click="showEditRoleModal = false">取消</n-button>
        <n-button type="primary" @click.stop.prevent="console.log('Simplified Confirm button clicked!');">确认（测试）</n-button>
      </template>
    </n-modal>
  </div>
</template>

<script setup>
import { h, ref, onMounted } from 'vue';
import { NButton, NSpace, NDataTable, NTag, useMessage, NModal } from 'naive-ui';
import axios from 'axios';
import { useThemeStore } from '@/store/theme'; // 导入主题store

const themeStore = useThemeStore(); // 使用主题store
const message = useMessage();
const users = ref([]);
const loading = ref(false);
const showEditRoleModal = ref(false);
const currentUser = ref(null);
const selectedRole = ref(null);

const roleOptions = [
  { label: 'ROLE_USER', value: 'ROLE_USER' },
  { label: 'ROLE_NUTRITIONIST', value: 'ROLE_NUTRITIONIST' },
  { label: 'ROLE_ADMIN', value: 'ROLE_ADMIN' },
  { label: 'ROLE_TESTER', value: 'ROLE_TESTER' },
];

const pagination = ref({
  pageSize: 10,
  page: 1,
  itemCount: 0,
  onChange: (page) => {
    pagination.value.page = page;
    fetchUsers();
  },
  onUpdatePageSize: (pageSize) => {
    pagination.value.pageSize = pageSize;
    pagination.value.page = 1;
    fetchUsers();
  },
});

const createColumns = ({ editRole, toggleUserStatus, deleteUser }) => {
  return [
    { title: 'ID', key: 'id', width: 60 },
    { title: '用户名', key: 'username', width: 120 },
    { title: '邮箱', key: 'email', width: 150 },
    {
      title: '角色',
      key: 'role',
      width: 120,
      render(row) {
        return h(NTag, { type: 'info', bordered: false }, { default: () => row.role });
      },
    },
    {
      title: '状态',
      key: 'isEnabled',
      width: 100,
      render(row) {
        return h(
          NTag,
          { type: row.isEnabled ? 'success' : 'error', bordered: false },
          { default: () => (row.isEnabled ? '启用' : '禁用') }
        );
      },
    },
    { title: '创建时间', key: 'createTime', width: 180 },
    { title: '更新时间', key: 'updateTime', width: 180 },
    {
      title: '操作',
      key: 'actions',
      width: 250,
      render(row) {
        return h(
          NSpace,
          {},
          {
            default: () => [
              h(
                NButton,
                {
                  size: 'small',
                  onClick: () => editRole(row),
                },
                { default: () => '编辑角色' }
              ),
              h(
                NButton,
                {
                  size: 'small',
                  type: row.isEnabled ? 'error' : 'success',
                  onClick: () => toggleUserStatus(row),
                },
                { default: () => (row.isEnabled ? '启用' : '禁用') }
              ),
              h(
                NButton,
                {
                  size: 'small',
                  type: 'error',
                  onClick: () => deleteUser(row),
                },
                { default: () => '删除' }
              ),
            ],
          }
        );
      },
    },
  ];
};

const columns = createColumns({
  editRole(user) {
    console.log('editRole called with user:', user);
    currentUser.value = user;
    selectedRole.value = user.role;
    showEditRoleModal.value = true;
  },
  async toggleUserStatus(user) {
    loading.value = true;
    try {
      const endpoint = user.isEnabled ? `/api/admin/user/disable/${user.id}` : `/api/admin/user/enable/${user.id}`;
      await axios.put(endpoint);
      message.success(`用户 ${user.username} 已成功${user.isEnabled ? '禁用' : '启用'}`);
      await fetchUsers(); // Refresh the list
    } catch (error) {
      console.error('Error toggling user status:', error);
      message.error(`操作失败: ${error.response?.data?.message || error.message}`);
    } finally {
      loading.value = false;
    }
  },
  async deleteUser(user) {
    if (!confirm(`确定要删除用户 ${user.username} 吗？`)) {
      return;
    }
    loading.value = true;
    try {
      await axios.delete(`/api/admin/user/${user.id}`);
      message.success(`用户 ${user.username} 已成功删除`);
      await fetchUsers(); // Refresh the list
    } catch (error) {
      console.error('Error deleting user:', error);
      message.error(`删除失败: ${error.response?.data?.message || error.message}`);
    } finally {
      loading.value = false;
    }
  },
});

async function confirmEditRole() {
  console.log('confirmEditRole called.');
  console.log('currentUser:', currentUser.value);
  console.log('selectedRole:', selectedRole.value);
  if (!currentUser.value || !selectedRole.value) {
    message.error('请选择一个角色。');
    return;
  }
  loading.value = true;
  try {
    console.log('Attempting to put role update for user:', currentUser.value.id, 'to role:', selectedRole.value);
    await axios.put(`/api/admin/user/role/${currentUser.value.id}`, { role: selectedRole.value });
    message.success(`用户 ${currentUser.value.username} 的角色已更新为 ${selectedRole.value}`);
    showEditRoleModal.value = false;
    await fetchUsers(); // Refresh the list
  } catch (error) {
    console.error('Error updating user role:', error);
    message.error(`更新角色失败: ${error.response?.data?.message || error.message}`);
  } finally {
    loading.value = false;
  }
}

async function fetchUsers() {
  loading.value = true;
  try {
    const response = await axios.get('/api/admin/user', {
      params: {
        pageNum: pagination.value.page,
        pageSize: pagination.value.pageSize,
      },
    });
    if (response.data && response.data.code === 200 && response.data.data) {
      users.value = response.data.data.records; // 正确提取用户列表数组
      pagination.value.itemCount = response.data.data.total; // 设置总条目数
      pagination.value.page = response.data.data.current; // 设置当前页码
      message.success('用户列表加载成功！');
    } else {
      message.error(response.data?.message || '获取用户列表失败');
    }
  } catch (error) {
    console.error('Error fetching users:', error);
    message.error(`加载用户列表失败: ${error.response?.data?.message || error.message}`);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchUsers();
});

</script>

<style scoped>
.admin-user {
  color: var(--text-color);
}

.admin-user h1 {
  color: var(--text-color);
}

/* 适配Naive UI组件主题 */
:deep(.n-data-table) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border: 1px solid var(--border-color);
}

:deep(.n-data-table-thead) {
  background-color: var(--card-bg-color);
}

:deep(.n-data-table-th) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border-bottom: 1px solid var(--border-color);
}

:deep(.n-data-table-td) {
  color: var(--text-color);
  border-bottom: 1px solid var(--border-color);
}

:deep(.n-modal-mask) {
  background-color: rgba(0, 0, 0, 0.5);
}

:deep(.n-modal-container) {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border: 1px solid var(--border-color);
}

:deep(.n-modal-header) {
  color: var(--text-color);
  border-bottom: 1px solid var(--border-color);
}

:deep(.n-modal-body) {
  color: var(--text-color);
}
</style> 