<template>
  <div>
    <div class="page-header">
      <h2>用户管理</h2>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索用户名/昵称/邮箱"
        clearable
        style="width: 260px"
        @keyup.enter="handleSearch"
      />
      <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 120px">
        <el-option label="正常" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-select v-model="filterRole" placeholder="角色" clearable style="width: 120px">
        <el-option label="普通用户" :value="0" />
        <el-option label="管理员" :value="1" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- User Table -->
    <el-table :data="tableData" v-loading="loading" border stripe style="margin-top: 16px">
      <el-table-column prop="id" label="ID" width="160" align="center" />
      <el-table-column prop="username" label="用户名" width="140" />
      <el-table-column prop="nickname" label="昵称" width="140" />
      <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="score" label="积分" width="80" align="center" />
      <el-table-column label="角色" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.role === 1 ? 'danger' : 'info'" size="small">
            {{ row.role === 1 ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" width="180" align="center">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除该用户?" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="fetchList"
        @size-change="handleSizeChange"
      />
    </div>

    <!-- Edit Dialog -->
    <el-dialog title="编辑用户" v-model="dialogVisible" width="520px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input :model-value="editing?.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="form.role">
            <el-radio :value="0">普通用户</el-radio>
            <el-radio :value="1">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="form.statusBool"
            active-text="正常"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList, updateUser, deleteUser } from '@/api/user'
import { formatDate } from '@/utils/format'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const tableData = ref<any[]>([])
const editing = ref<any>(null)
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const keyword = ref('')
const filterStatus = ref<number | undefined>(undefined)
const filterRole = ref<number | undefined>(undefined)

const form = reactive<any>({
  nickname: '', email: '', phone: '',
  role: 0, statusBool: true,
})

onMounted(fetchList)

async function fetchList() {
  loading.value = true
  try {
    const res = await getUserList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      status: filterStatus.value,
      role: filterRole.value,
    })
    tableData.value = res?.records || []
    total.value = res?.total || 0
  } catch {
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  fetchList()
}

function handleSizeChange() {
  pageNum.value = 1
  fetchList()
}

function showEdit(row: any) {
  editing.value = row
  form.nickname = row.nickname || ''
  form.email = row.email || ''
  form.phone = row.phone || ''
  form.role = row.role
  form.statusBool = row.status === 1
  dialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    await updateUser(editing.value.id, {
      nickname: form.nickname,
      email: form.email,
      phone: form.phone,
      role: form.role,
      status: form.statusBool ? 1 : 0,
    })
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchList()
  } catch {
    /* ignore */
  } finally {
    saving.value = false
  }
}

async function handleDelete(id: number) {
  try {
    await deleteUser(id)
    ElMessage.success('已删除')
    fetchList()
  } catch {
    /* ignore */
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
