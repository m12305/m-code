<template>
  <div class="exam-score-page">
    <div v-loading="loading">
      <template v-if="record">
        <div class="score-header">
          <h2 class="page-title">考试成绩</h2>
          <el-button @click="router.push('/user/exam')">返回考试列表</el-button>
        </div>

        <el-divider />

        <!-- Exam basic info -->
        <el-descriptions :column="2" border class="info-block">
          <el-descriptions-item label="考试名称" :span="2">
            {{ examTitle || '考试 #' + examId }}
          </el-descriptions-item>
          <el-descriptions-item label="总分" :span="1">
            <span v-if="record.status === ExamRecordStatus.COMPLETED" class="final-score">
              {{ record.totalScore ?? 0 }} 分
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态" :span="1">
            <el-tag
              :type="statusTagType"
              size="default"
            >
              {{ statusText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="提交时间" :span="2">
            {{ formatDate(record.submitTime) }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- Judging in progress -->
        <div v-if="isJudging" class="judging-section">
          <el-alert
            title="判题中"
            type="info"
            :closable="false"
            show-icon
            description="系统正在判题中，请稍候..."
          />
          <div class="judging-actions">
            <el-button :loading="refreshing" @click="refreshRecord">刷新状态</el-button>
            <span class="auto-refresh-hint">每 5 秒自动刷新</span>
          </div>
        </div>

        <!-- Final score display -->
        <div v-if="record.status === ExamRecordStatus.COMPLETED" class="score-display">
          <div class="big-score">
            <span class="big-score-number">{{ record.totalScore ?? 0 }}</span>
            <span class="big-score-unit">分</span>
          </div>
          <p class="score-subtitle">最终成绩</p>
        </div>

        <!-- Answer details -->
        <div v-if="record.answers && record.answers.length > 0" class="answers-section">
          <h3>答题详情</h3>
          <div
            v-for="(ans, index) in record.answers"
            :key="ans.questionId ?? index"
            class="answer-detail-item"
          >
            <div class="answer-header">
              <span class="answer-index">第 {{ index + 1 }} 题</span>
              <el-tag
                v-if="ans.judgeStatus !== undefined"
                :type="JudgeStatusColor[ans.judgeStatus] ?? 'info'"
                size="small"
              >
                {{ JudgeStatusMap[ans.judgeStatus] ?? '未知' }}
              </el-tag>
              <span v-if="ans.score !== undefined" class="answer-score">
                {{ ans.score }} 分
              </span>
            </div>
          </div>
        </div>

        <!-- Exam rank -->
        <div class="rank-section">
          <h3>考试排名</h3>
          <el-table
            v-loading="rankLoading"
            :data="rankList"
            stripe
            style="width: 100%"
            empty-text="暂无排名数据"
          >
            <el-table-column label="排名" width="80" align="center">
              <template #default="{ $index }">
                {{ $index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="userId" label="用户ID" min-width="120" align="center" />
            <el-table-column prop="username" label="用户名" min-width="150" align="center">
              <template #default="{ row }">
                {{ row.username || row.nickname || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="totalScore" label="总分" width="120" align="center">
              <template #default="{ row }">
                <span :class="{ 'my-score-highlight': row.userId === userId }">
                  {{ row.totalScore ?? 0 }}
                </span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </template>

      <el-empty v-else-if="!loading" description="暂无考试记录" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getExamDetail, getMyExamRecord, getExamRank } from '@/api/exam'
import {
  ExamRecordStatus,
  ExamRecordStatusMap,
  JudgeStatusMap,
  JudgeStatusColor,
} from '@/utils/enums'
import { formatDate } from '@/utils/format'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const examId = route.params.id as string
const userId = computed(() => userStore.userInfo?.id)

const loading = ref(true)
const refreshing = ref(false)
const rankLoading = ref(false)

const record = ref<any>(null)
const examTitle = ref('')
const rankList = ref<any[]>([])

let pollTimer: ReturnType<typeof setInterval> | null = null

// Status helpers
const isJudging = computed(() => {
  const status = record.value?.status
  return status === ExamRecordStatus.SUBMITTED || status === ExamRecordStatus.PARTIAL_JUDGING
})

const statusText = computed(() => {
  const status = record.value?.status
  return ExamRecordStatusMap[status] ?? '未知状态'
})

const statusTagType = computed(() => {
  const status = record.value?.status
  if (status === ExamRecordStatus.COMPLETED) return 'success'
  if (status === ExamRecordStatus.SUBMITTED || status === ExamRecordStatus.PARTIAL_JUDGING) return 'warning'
  if (status === ExamRecordStatus.STARTED) return 'info'
  return 'info'
})

// Data loading
async function loadRecord() {
  try {
    const res = await getMyExamRecord(examId)
    record.value = res ?? null
  } catch {
    record.value = null
  }
}

async function loadExamTitle() {
  try {
    const res = await getExamDetail(examId)
    examTitle.value = res?.title ?? ''
  } catch {
    /* ignore */
  }
}

async function loadRank() {
  rankLoading.value = true
  try {
    const res = await getExamRank(examId)
    rankList.value = res ?? []
  } catch {
    /* ignore */
  } finally {
    rankLoading.value = false
  }
}

async function refreshRecord() {
  refreshing.value = true
  try {
    await loadRecord()
    if (record.value && (record.value.answers || record.value.answerList)) {
      // Data refreshed
    }
  } finally {
    refreshing.value = false
  }
}

function startPolling() {
  stopPolling()
  pollTimer = setInterval(async () => {
    await loadRecord()
    // Stop polling when judging completes
    if (record.value?.status === ExamRecordStatus.COMPLETED) {
      stopPolling()
      ElMessage.success('判题已完成')
      loadRank() // Refresh rank too
    }
  }, 5000)
}

function stopPolling() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

// --- Init ---
onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([
      loadRecord(),
      loadExamTitle(),
    ])

    if (record.value) {
      // Load rank regardless of status
      loadRank()

      // Start polling if status is 3 or 4
      if (isJudging.value) {
        startPolling()
      }
    }
  } finally {
    loading.value = false
  }
})

onBeforeUnmount(() => {
  stopPolling()
})
</script>

<style scoped>
.exam-score-page {
  padding: 20px;
  max-width: 900px;
}

.score-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.info-block {
  margin-bottom: 20px;
}

.final-score {
  font-size: 18px;
  font-weight: 700;
  color: #67c23a;
}

.judging-section {
  margin-bottom: 20px;
}

.judging-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
}

.auto-refresh-hint {
  font-size: 12px;
  color: #909399;
}

.score-display {
  text-align: center;
  padding: 32px 0;
  margin-bottom: 24px;
}

.big-score {
  display: inline-flex;
  align-items: baseline;
  gap: 4px;
}

.big-score-number {
  font-size: 64px;
  font-weight: 700;
  color: #409eff;
  line-height: 1;
  font-family: 'Courier New', monospace;
}

.big-score-unit {
  font-size: 24px;
  color: #909399;
}

.score-subtitle {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.answers-section {
  margin-bottom: 24px;
}

.answers-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.answer-detail-item {
  padding: 12px 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-bottom: 8px;
}

.answer-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.answer-index {
  font-weight: 600;
  color: #303133;
}

.answer-score {
  font-size: 14px;
  color: #e6a23c;
  font-weight: 500;
}

.rank-section {
  margin-bottom: 24px;
}

.rank-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.my-score-highlight {
  color: #409eff;
  font-weight: 600;
}
</style>
