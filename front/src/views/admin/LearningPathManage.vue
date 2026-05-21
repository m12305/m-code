<template>
  <div>
    <div class="page-header">
      <h2>学习路线管理</h2>
      <el-button type="primary" @click="showAdd">添加路线</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="articleIds" label="关联文章" width="120">
        <template #default="{row}">{{ row.articleIds ? row.articleIds.split(',').length : 0 }} 篇</template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="60" />
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
    <el-dialog :title="editing?'编辑路线':'添加路线'" v-model="dialogVisible" width="600px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="封面图"><el-input v-model="form.coverImage" placeholder="URL" /></el-form-item>
        <el-form-item label="关联文章ID"><el-input v-model="form.articleIds" placeholder="逗号分隔，如 1,2,3" /></el-form-item>
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
import { getLearningPathList, addLearningPath } from '@/api/knowledge'
const loading = ref(false); const dialogVisible = ref(false); const saving = ref(false)
const tableData = ref<any[]>([]); const editing = ref<any>(null)
const form = reactive<any>({ title: '', description: '', coverImage: '', articleIds: '', sort: 0, statusBool: true })
onMounted(fetchList)
async function fetchList() { loading.value = true; try { tableData.value = await getLearningPathList() || [] } finally { loading.value = false } }
function showAdd() { editing.value = null; form.title = ''; form.description = ''; form.coverImage = ''; form.articleIds = ''; form.sort = 0; form.statusBool = true; dialogVisible.value = true }
function showEdit(row: any) { editing.value = row; form.title = row.title; form.description = row.description || ''; form.coverImage = row.coverImage || ''; form.articleIds = row.articleIds || ''; form.sort = row.sort; form.statusBool = row.status === 1; dialogVisible.value = true }
async function handleSave() {
  saving.value = true
  try { await addLearningPath({ ...form, status: form.statusBool ? 1 : 0 }); ElMessage.success('保存成功'); dialogVisible.value = false; fetchList() } finally { saving.value = false }
}
async function handleDelete(id: number) { ElMessage.info('删除功能待实现'); fetchList() }
</script>
