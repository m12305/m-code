<template>
  <div>
    <div class="page-header">
      <h2>标签管理</h2>
      <el-button type="primary" @click="showAdd">添加标签</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="color" label="颜色">
        <template #default="{row}"><el-tag :color="row.color" size="small">{{ row.color }}</el-tag></template>
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
    <el-dialog :title="editing?'编辑标签':'添加标签'" v-model="dialogVisible" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="颜色"><el-color-picker v-model="form.color" /></el-form-item>
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
import { getTagList, addTag, updateTag, deleteTag } from '@/api/question'
const loading = ref(false); const dialogVisible = ref(false); const saving = ref(false)
const tableData = ref<any[]>([]); const editing = ref<any>(null)
const form = reactive({ name: '', color: '#409eff' })
onMounted(fetchList)
async function fetchList() { loading.value = true; try { tableData.value = await getTagList() || [] } finally { loading.value = false } }
function showAdd() { editing.value = null; form.name = ''; form.color = '#409eff'; dialogVisible.value = true }
function showEdit(row: any) { editing.value = row; form.name = row.name; form.color = row.color; dialogVisible.value = true }
async function handleSave() {
  saving.value = true
  try {
    if (editing.value) await updateTag({ id: editing.value.id, ...form })
    else await addTag({ ...form })
    ElMessage.success('保存成功'); dialogVisible.value = false; fetchList()
  } finally { saving.value = false }
}
async function handleDelete(id: number) { await deleteTag(id); ElMessage.success('已删除'); fetchList() }
</script>
