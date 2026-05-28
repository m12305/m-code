<template>
  <div class="page-container">
    <div class="page-header">
      <el-button link type="primary" @click="router.push('/user/submission')">
        <el-icon style="margin-right: 4px"><ArrowLeft /></el-icon>
        返回提交记录
      </el-button>
    </div>

    <el-card v-loading="loading">
      <template v-if="!loading && submission">
        <!-- Submission Info -->
        <h3 style="margin-top: 0">提交信息</h3>
        <el-descriptions :column="2" border style="margin-bottom: 24px">
          <el-descriptions-item label="提交ID">{{ submission.id }}</el-descriptions-item>
          <el-descriptions-item label="题目ID">
            <el-button
              link
              type="primary"
              size="small"
              @click="router.push(`/user/question/${submission.questionId}`)"
            >
              {{ submission.questionId }}
            </el-button>
          </el-descriptions-item>
          <el-descriptions-item label="编程语言">
            {{ LanguageMap[submission.language] ?? '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="判题状态">
            <el-tag
              v-if="submission.status != null"
              :type="JudgeStatusColor[submission.status] as any"
              size="small"
            >
              {{ JudgeStatusMap[submission.status] ?? '-' }}
            </el-tag>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item v-if="isShortAnswer" label="AI评分">
            <span class="ai-score-text">{{ submission.timeUsed ?? '-' }} / 10</span>
          </el-descriptions-item>
          <template v-else>
            <el-descriptions-item label="耗时">
              {{ formatTime(submission.timeUsed) }}
            </el-descriptions-item>
            <el-descriptions-item label="内存">
              {{ formatMemory(submission.memoryUsed) }}
            </el-descriptions-item>
          </template>
          <el-descriptions-item label="提交时间">
            {{ formatDate(submission.createTime) }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- User Answer -->
        <h3>提交代码</h3>
        <div style="margin-bottom: 24px">
          <template v-if="submission.language != null && submission.language !== 0">
            <CodeEditor
              :model-value="submission.answer ?? ''"
              :language="submission.language"
              :height="350"
              read-only
            />
          </template>
          <template v-else>
            <pre style="
              background: #f5f7fa;
              padding: 16px;
              border-radius: 4px;
              overflow: auto;
              max-height: 350px;
              white-space: pre-wrap;
              word-break: break-all;
              font-size: 13px;
              line-height: 1.6;
            ">{{ submission.answer ?? '(无答案)' }}</pre>
          </template>
        </div>

        <!-- Judge Results -->
        <h3 style="margin-bottom: 12px">判题详情</h3>
        <template v-if="judgeResults.length > 0">
          <!-- Short answer: show score and feedback instead of time/memory/error -->
          <template v-if="isShortAnswer">
            <el-table :data="judgeResults" stripe style="width: 100%">
              <el-table-column prop="testCaseName" label="评测项" min-width="120" />
              <el-table-column label="状态" width="120" align="center">
                <template #default="{ row }">
                  <el-tag
                    v-if="row.status != null"
                    :type="JudgeStatusColor[row.status] as any"
                    size="small"
                  >
                    {{ JudgeStatusMap[row.status] ?? '-' }}
                  </el-tag>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column label="评分" width="120" align="center">
                <template #default="{ row }">
                  <span class="ai-score-text">{{ row.timeUsed ?? '-' }} / 10</span>
                </template>
              </el-table-column>
            </el-table>
            <!-- Full feedback displayed below the table -->
            <div v-for="(row, index) in judgeResults" :key="index" class="feedback-block">
              <h4>总体评价</h4>
              <div class="feedback-content">{{ row.errorMessage || '暂无评价' }}</div>
            </div>
          </template>
          <!-- Programming / other types: show full detail table -->
          <template v-else>
            <el-table :data="judgeResults" stripe style="width: 100%">
              <el-table-column prop="testCaseName" label="测试用例" min-width="150" show-overflow-tooltip />
              <el-table-column label="状态" width="120" align="center">
                <template #default="{ row }">
                  <el-tag
                    v-if="row.status != null"
                    :type="JudgeStatusColor[row.status] as any"
                    size="small"
                  >
                    {{ JudgeStatusMap[row.status] ?? '-' }}
                  </el-tag>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column label="耗时" width="100" align="center">
                <template #default="{ row }">
                  {{ formatTime(row.timeUsed) }}
                </template>
              </el-table-column>
              <el-table-column label="内存" width="100" align="center">
                <template #default="{ row }">
                  {{ formatMemory(row.memoryUsed) }}
                </template>
              </el-table-column>
              <el-table-column label="实际输出" min-width="160">
                <template #default="{ row }">
                  <template v-if="row.actualOutput">
                    <el-popover
                      placement="top-start"
                      :width="400"
                      trigger="hover"
                    >
                      <template #reference>
                        <span class="truncated-text">{{ truncateText(row.actualOutput) }}</span>
                      </template>
                      <pre style="margin: 0; white-space: pre-wrap; word-break: break-all; max-height: 300px; overflow: auto">{{
                        row.actualOutput
                      }}</pre>
                    </el-popover>
                  </template>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column label="错误信息" min-width="160" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ row.errorMessage || '-' }}
                </template>
              </el-table-column>
            </el-table>
          </template>
        </template>
        <EmptyState v-else description="暂无判题详情" />
      </template>

      <EmptyState v-if="!loading && !submission" description="提交记录不存在" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSubmissionDetail, getJudgeResults } from '@/api/judge'
import {
  LanguageMap,
  JudgeStatusMap,
  JudgeStatusColor,
} from '@/utils/enums'
import { formatDate, formatMemory, formatTime } from '@/utils/format'
import CodeEditor from '@/components/code/CodeEditor.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submission = ref<any>(null)
const judgeResults = ref<any[]>([])

const isShortAnswer = computed(() =>
  judgeResults.value.some(r => r.testCaseName === 'AI评分')
)

// Progressive backoff: short intervals first, longer intervals later
const POLL_INTERVALS = [2000, 2000, 3000, 4000, 5000, 8000, 8000, 10000, 15000, 15000, 20000]
let pollCount = 0
let pollTimer: ReturnType<typeof setTimeout> | null = null

function isTerminal(status: number): boolean {
  // PENDING(0) and RUNNING(1) are non-terminal
  return status >= 2
}

function getPollInterval(count: number): number {
  return count < POLL_INTERVALS.length
    ? POLL_INTERVALS[count]
    : POLL_INTERVALS[POLL_INTERVALS.length - 1]
}

function truncateText(text: string, maxLen = 80): string {
  if (!text) return ''
  return text.length > maxLen ? text.slice(0, maxLen) + '...' : text
}

function stopPolling() {
  if (pollTimer) {
    clearTimeout(pollTimer)
    pollTimer = null
  }
}

function startPolling(id: string) {
  stopPolling()
  const poll = async () => {
    try {
      const [sub, results] = await Promise.all([
        getSubmissionDetail(id),
        getJudgeResults(id),
      ])
      submission.value = sub
      judgeResults.value = Array.isArray(results) ? results : []

      if (sub && isTerminal(sub.status)) {
        return
      }
    } catch {
      // Retry on error
    }

    pollCount++
    pollTimer = setTimeout(poll, getPollInterval(pollCount))
  }
  poll()
}

async function loadData() {
  const id = route.params.id as string
  if (!id) return
  loading.value = true
  try {
    const [sub, results] = await Promise.all([
      getSubmissionDetail(id),
      getJudgeResults(id),
    ])
    submission.value = sub
    judgeResults.value = Array.isArray(results) ? results : []

    if (sub && !isTerminal(sub.status)) {
      startPolling(id)
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

onBeforeUnmount(() => {
  stopPolling()
})
</script>

<style scoped>
.truncated-text {
  cursor: pointer;
  color: #409eff;
  max-width: 200px;
  display: inline-block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ai-score-text {
  color: #409eff;
  font-weight: 600;
  font-size: 15px;
}

.feedback-block {
  margin-top: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.feedback-block h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #303133;
}

.feedback-content {
  font-size: 14px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
