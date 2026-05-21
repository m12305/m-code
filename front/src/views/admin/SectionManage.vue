<template>
  <div>
    <div class="page-header">
      <h2>板块管理</h2>
      <el-button type="primary" @click="showAdd">添加板块</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{row}"><el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'启用':'禁用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{row}">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除?" @confirm="handleDelete(row.id)">
            <template #reference><el-button size="small" type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :title="editing?'编辑板块':'添加板块'" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.statusBool" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSectionList, addSection, updateSection, deleteSection } from '@/api/question'
const loading = ref(false); const dialogVisible = ref(false); const saving = ref(false)
const tableData = ref<any[]>([]); const editing = ref<any>(null)
const form = reactive<any>({ name: '', description: '', icon: '', sort: 0, statusBool: true })
onMounted(fetchList)
async function fetchList() { loading.value = true; try { tableData.value = await getSectionList() || [] } finally { loading.value = false } }
function showAdd() { editing.value = null; form.name = ''; form.description = ''; form.icon = ''; form.sort = 0; form.statusBool = true; dialogVisible.value = true }
function showEdit(row: any) { editing.value = row; form.name = row.name; form.description = row.description; form.icon = row.icon; form.sort = row.sort; form.statusBool = row.status === 1; dialogVisible.value = true }
async function handleSave() {
  saving.value = true
  try {
    const data = { name: form.name, description: form.description, icon: form.icon, sort: form.sort, status: form.statusBool ? 1 : 0 }
    if (editing.value) await updateSection({ id: editing.value.id, ...data })
    else await addSection(data)
    ElMessage.success('保存成功'); dialogVisible.value = false; fetchList()
  } finally { saving.value = false }
}
async function handleDelete(id: number) { await deleteSection(id); ElMessage.success('已删除'); fetchList() }
</script>
