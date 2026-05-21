<template>
  <div>
    <div class="page-header">
      <h2>考试管理</h2>
      <el-button type="primary" @click="showAdd">创建考试</el-button>
    </div>
    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
      <el-table-column prop="duration" label="时长(分)" width="80" />
      <el-table-column label="开始时间" width="160">
        <template #default="{row}">{{ formatDate(row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="结束时间" width="160">
        <template #default="{row}">{{ formatDate(row.endTime) }}</template>
      </el-table-column>
      <el-table-column prop="totalScore" label="总分" width="70" />
      <el-table-column label="状态" width="80">
        <template #default="{row}"><el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'进行中':'已结束' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="$router.push(`/admin/exam/${row.id}/questions`)">编排题目</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px" v-model:current-page="pageNum" v-model:page-size="pageSize"
      :page-sizes="[10,20]" :total="total" layout="total,sizes,prev,pager,next" @change="fetchList" />

    <el-dialog :title="editing?.id ? '编辑考试' : '创建考试'" v-model="dialogVisible" width="600px" destroy-on-close>
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题" required><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="时长(分钟)" required><el-input-number v-model="form.duration" :min="1" /></el-form-item>
        <el-form-item label="开始时间" required>
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="结束时间" required>
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" />
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
import { getExamList, createExam, updateExam } from '@/api/exam'
import { formatDate } from '@/utils/format'
const loading = ref(false); const dialogVisible = ref(false); const saving = ref(false)
const tableData = ref<any[]>([]); const total = ref(0); const editing = ref<any>(null)
const pageNum = ref(1); const pageSize = ref(20)
const form = reactive<any>({ title: '', description: '', duration: 120, startTime: '', endTime: '' })
onMounted(fetchList)
async function fetchList() {
  loading.value = true
  try { const res = await getExamList({ pageNum: pageNum.value, pageSize: pageSize.value }); tableData.value = res?.records || []; total.value = res?.total || 0 } finally { loading.value = false }
}
function showAdd() { editing.value = null; form.title = ''; form.description = ''; form.duration = 120; form.startTime = ''; form.endTime = ''; dialogVisible.value = true }
function showEdit(row: any) { editing.value = row; form.title = row.title; form.description = row.description || ''; form.duration = row.duration; form.startTime = row.startTime; form.endTime = row.endTime; dialogVisible.value = true }
async function handleSave() {
  saving.value = true
  try {
    if (editing.value?.id) await updateExam({ id: editing.value.id, ...form })
    else await createExam({ ...form, questions: [] })
    ElMessage.success('保存成功'); dialogVisible.value = false; fetchList()
  } finally { saving.value = false }
}
</script>
