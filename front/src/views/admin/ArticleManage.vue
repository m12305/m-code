<template>
  <div>
    <div class="page-header">
      <h2>文章管理</h2>
      <el-button type="primary" @click="showAdd">添加文章</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
      <el-table-column prop="summary" label="摘要" min-width="200" show-overflow-tooltip />
      <el-table-column prop="authorName" label="作者" width="100" />
      <el-table-column prop="viewCount" label="浏览" width="70" />
      <el-table-column label="状态" width="80">
        <template #default="{row}"><el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'已发布':'草稿' }}</el-tag></template>
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
    <el-pagination style="margin-top:16px" v-model:current-page="pageNum" v-model:page-size="pageSize"
      :page-sizes="[10,20]" :total="total" layout="total,sizes,prev,pager,next" @change="fetchList" />

    <el-dialog :title="editing?.id ? '编辑文章' : '添加文章'" v-model="dialogVisible" width="800px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题" required><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="form.content" type="textarea" :rows="12" placeholder="支持 Markdown 格式" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" style="width:100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.statusBool" active-text="发布" inactive-text="草稿" />
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
import { getArticleList, addArticle, updateArticle, deleteArticle, getKnowledgeCategoryList } from '@/api/knowledge'
import { useUserStore } from '@/stores/user'
const loading = ref(false); const dialogVisible = ref(false); const saving = ref(false)
const tableData = ref<any[]>([]); const total = ref(0); const editing = ref<any>(null)
const pageNum = ref(1); const pageSize = ref(20)
const categories = ref<any[]>([])
const userStore = useUserStore()
const form = reactive<any>({ title: '', summary: '', content: '', categoryId: undefined, statusBool: true })
onMounted(async () => { categories.value = await getKnowledgeCategoryList() || []; fetchList() })
async function fetchList() {
  loading.value = true
  try { const res = await getArticleList({ pageNum: pageNum.value, pageSize: pageSize.value }); tableData.value = res?.records || []; total.value = res?.total || 0 } finally { loading.value = false }
}
function showAdd() { editing.value = null; form.title = ''; form.summary = ''; form.content = ''; form.categoryId = undefined; form.statusBool = true; dialogVisible.value = true }
function showEdit(row: any) { editing.value = row; form.title = row.title; form.summary = row.summary || ''; form.content = row.content || ''; form.categoryId = row.categoryId; form.statusBool = row.status === 1; dialogVisible.value = true }
async function handleSave() {
  saving.value = true
  try {
    const data = { ...form, status: form.statusBool ? 1 : 0, authorId: userStore.userInfo?.id, authorName: userStore.userInfo?.nickname || userStore.userInfo?.username }
    if (editing.value?.id) await updateArticle({ id: editing.value.id, ...data })
    else await addArticle(data)
    ElMessage.success('保存成功'); dialogVisible.value = false; fetchList()
  } finally { saving.value = false }
}
async function handleDelete(id: number) { await deleteArticle(id); ElMessage.success('已删除'); fetchList() }
</script>
