<template>
  <div>
    <div class="page-header">
      <h2>知识分类管理</h2>
      <el-button type="primary" @click="showAdd">添加分类</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="icon" label="图标" />
      <el-table-column prop="parentId" label="父分类ID" width="100" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{row}">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除?" @confirm="handleDelete(row.id)">
            <template #reference><el-button size="small" type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :title="editing?'编辑分类':'添加分类'" v-model="dialogVisible" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" /></el-form-item>
        <el-form-item label="父分类"><el-input-number v-model="form.parentId" :min="0" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
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
import { getKnowledgeCategoryList, addKnowledgeCategory } from '@/api/knowledge'
const loading = ref(false); const dialogVisible = ref(false); const saving = ref(false)
const tableData = ref<any[]>([]); const editing = ref<any>(null)
const form = reactive({ name: '', icon: '', parentId: 0, sort: 0 })
onMounted(fetchList)
async function fetchList() { loading.value = true; try { tableData.value = await getKnowledgeCategoryList() || [] } finally { loading.value = false } }
function showAdd() { editing.value = null; form.name = ''; form.icon = ''; form.parentId = 0; form.sort = 0; dialogVisible.value = true }
function showEdit(row: any) { editing.value = row; form.name = row.name; form.icon = row.icon || ''; form.parentId = row.parentId; form.sort = row.sort; dialogVisible.value = true }
async function handleSave() {
  saving.value = true
  try { await addKnowledgeCategory({ ...form }); ElMessage.success('保存成功'); dialogVisible.value = false; fetchList() } finally { saving.value = false }
}
async function handleDelete(id: number) { ElMessage.info('删除功能待实现'); fetchList() }
</script>
