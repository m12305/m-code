<template>
  <div class="exam-detail-page">
    <div v-loading="loading">
      <template v-if="exam">
        <div class="detail-header">
          <h2 class="page-title">{{ exam.title }}</h2>
          <div class="header-actions">
            <el-button
              v-if="canStart"
              type="success"
              :loading="starting"
              @click="handleStart"
            >
              开始考试
            </el-button>
            <el-button type="warning" @click="goScore">查看成绩</el-button>
            <el-button @click="router.back()">返回</el-button>
          </div>
        </div>

        <el-divider />

        <el-descriptions :column="2" border>
          <el-descriptions-item label="考试时长" :span="1">
            {{ exam.duration }} 分钟
          </el-descriptions-item>
          <el-descriptions-item label="总分" :span="1">
            {{ exam.totalScore }} 分
          </el-descriptions-item>
          <el-descriptions-item label="开始时间" :span="1">
            {{ formatDate(exam.startTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="结束时间" :span="1">
            {{ formatDate(exam.endTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="题目数量" :span="1">
            {{ questions.length }} 题
          </el-descriptions-item>
          <el-descriptions-item label="状态" :span="1">
            <el-tag :type="exam.status === 1 ? 'success' : 'info'" size="small">
              {{ exam.status === 1 ? '进行中' : '已结束' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div class="description-section" v-if="exam.description">
          <h3>考试说明</h3>
          <div class="description-content">{{ exam.description }}</div>
        </div>
      </template>

      <el-empty v-else-if="!loading" description="考试不存在或已删除" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getExamDetail, getExamQuestions, startExam } from '@/api/exam'
import { formatDate } from '@/utils/format'

const router = useRouter()
const route = useRoute()

const examId = route.params.id as string
const loading = ref(false)
const starting = ref(false)
const exam = ref<any>(null)
const questions = ref<any[]>([])

const canStart = computed(() => {
  if (!exam.value) return false
  return exam.value.status === 1
})

async function loadDetail() {
  loading.value = true
  try {
    const [examRes, questionsRes] = await Promise.all([
      getExamDetail(examId),
      getExamQuestions(examId),
    ])
    exam.value = examRes ?? null
    questions.value = questionsRes ?? []
  } catch {
    /* error handled by interceptor */
  } finally {
    loading.value = false
  }
}

function goScore() {
  router.push(`/user/exam/${examId}/score`)
}

async function handleStart() {
  starting.value = true
  try {
    await startExam(examId)
    ElMessage.success('考试已开始')
    router.push(`/user/exam/${examId}/taking`)
  } catch {
    /* error handled by interceptor */
  } finally {
    starting.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.exam-detail-page {
  padding: 20px;
  max-width: 900px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.description-section {
  margin-top: 24px;
}

.description-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.description-content {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px 20px;
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
