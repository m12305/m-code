<template>
  <div>
    <div class="page-header">
      <h2>考试题目编排 — 考试ID: {{ examId }}</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>
    <el-card v-loading="loading">
      <div class="filter-bar">
        <el-select v-model="query.type" placeholder="题目类型" clearable style="width:130px">
          <el-option v-for="(v,k) in QuestionTypeMap" :key="k" :label="v" :value="Number(k)" />
        </el-select>
        <el-button type="primary" @click="fetchQuestions">搜索题目</el-button>
      </div>
      <el-table :data="availableQuestions" border stripe @selection-change="handleSelectionChange" ref="tableRef">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column label="类型" width="90">
          <template #default="{row}">{{ QuestionTypeMap[(row.type as any)?.code ?? row.type] }}</template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px">
        <span>已选 {{ selectedQuestions.length }} 题，每题分值：</span>
        <el-input-number v-model="defaultScore" :min="1" :max="100" style="width:120px;margin:0 8px" />
        <el-button type="primary" @click="saveExamQuestions" :loading="saving">保存题目编排</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getExamDetail, getExamQuestions, createExam, updateExam } from '@/api/exam'
import { getQuestionList } from '@/api/question'
import { QuestionTypeMap } from '@/utils/enums'

const route = useRoute()
const router = useRouter()
const examId = route.params.id as string
const loading = ref(false); const saving = ref(false)
const availableQuestions = ref<any[]>([])
const selectedQuestions = ref<any[]>([])
const existingExamQuestions = ref<any[]>([])
const defaultScore = ref(10)
const tableRef = ref()

const query = ref({ type: undefined as any, pageNum: 1, pageSize: 50 })

onMounted(async () => {
  loading.value = true
  try {
    const [exam, eqRes] = await Promise.all([getExamDetail(examId), getExamQuestions(examId)])
    existingExamQuestions.value = eqRes || []
    fetchQuestions()
  } finally { loading.value = false }
})

async function fetchQuestions() {
  availableQuestions.value = (await getQuestionList({ ...query.value }))?.records || []
}

function handleSelectionChange(rows: any[]) {
  selectedQuestions.value = rows
}

async function saveExamQuestions() {
  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请选择题目'); return
  }
  saving.value = true
  try {
    const exam = await getExamDetail(examId)
    const questions = selectedQuestions.value.map((q, idx) => ({
      questionId: q.id,
      score: defaultScore.value,
      sort: existingExamQuestions.value.length + idx + 1,
    }))
    await updateExam({ ...exam, questions: [...existingExamQuestions.value, ...questions] })
    ElMessage.success(`已添加 ${selectedQuestions.value.length} 道题目`); router.back()
  } finally { saving.value = false }
}
</script>
