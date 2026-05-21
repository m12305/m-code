<template>
  <div class="exam-taking-page">
    <!-- Time warning bar -->
    <div
      class="timer-bar"
      :class="timerBarClass"
    >
      <span class="timer-label">剩余时间</span>
      <span class="timer-value">{{ formatDuration(remainingSeconds) }}</span>
    </div>

    <div class="exam-body" v-loading="loading">
      <template v-if="examQuestions.length > 0 && exam">
        <!-- Left sidebar: question navigation -->
        <div class="question-sidebar">
          <div
            v-for="(eq, index) in examQuestions"
            :key="eq.questionId"
            class="question-nav-btn"
            :class="{
              active: currentIndex === index,
              answered: answers.has(eq.questionId) && answers.get(eq.questionId)?.answer,
            }"
            @click="switchQuestion(index)"
          >
            {{ index + 1 }}
            <span v-if="eq.score" class="nav-score">{{ eq.score }}分</span>
          </div>
        </div>

        <!-- Right content area -->
        <div class="question-content">
          <div v-if="currentQuestionDetail" class="content-wrapper">
            <div class="question-header">
              <h3 class="question-title">
                第 {{ currentIndex + 1 }} 题
                <el-tag size="small" :type="questionTypeTagType" class="type-tag">
                  {{ QuestionTypeMap[currentQuestionDetail.type] }}
                </el-tag>
                <span v-if="currentExamQuestion?.score" class="score-badge">
                  {{ currentExamQuestion.score }} 分
                </span>
              </h3>
            </div>

            <div class="question-description">
              <MarkdownViewer :content="currentQuestionDetail.description || ''" />
            </div>

            <el-divider />

            <!-- Answer area by question type -->
            <div class="answer-area">
              <!-- PROGRAMMING -->
              <template v-if="currentQuestionDetail.type === QuestionType.PROGRAMMING">
                <div class="language-select-row">
                  <LanguageSelect
                    :model-value="currentAnswer.language"
                    @update:model-value="onLanguageChange"
                  />
                </div>
                <CodeEditor
                  :model-value="currentAnswer.answer"
                  :language="currentAnswer.language"
                  :height="350"
                  @update:model-value="onAnswerChange"
                />
              </template>

              <!-- MULTIPLE_CHOICE -->
              <template v-else-if="currentQuestionDetail.type === QuestionType.MULTIPLE_CHOICE">
                <el-radio-group
                  :model-value="currentAnswer.answer"
                  class="choice-group"
                  @update:model-value="onAnswerChange"
                >
                  <el-radio
                    v-for="(opt, optIndex) in parsedOptions"
                    :key="optIndex"
                    :value="opt.label || String.fromCharCode(65 + optIndex)"
                    class="choice-item"
                  >
                    <span class="opt-label">{{ opt.label || String.fromCharCode(65 + optIndex) }}.</span>
                    <span>{{ opt.value ?? opt }}</span>
                  </el-radio>
                </el-radio-group>
              </template>

              <!-- SHORT_ANSWER -->
              <template v-else-if="currentQuestionDetail.type === QuestionType.SHORT_ANSWER">
                <el-input
                  :model-value="currentAnswer.answer"
                  type="textarea"
                  :rows="6"
                  placeholder="请输入你的答案..."
                  @update:model-value="onAnswerChange"
                />
              </template>

              <!-- TRUE_FALSE -->
              <template v-else-if="currentQuestionDetail.type === QuestionType.TRUE_FALSE">
                <el-radio-group
                  :model-value="currentAnswer.answer"
                  class="choice-group"
                  @update:model-value="onAnswerChange"
                >
                  <el-radio value="正确" class="choice-item">
                    <span class="opt-label">A.</span> 正确
                  </el-radio>
                  <el-radio value="错误" class="choice-item">
                    <span class="opt-label">B.</span> 错误
                  </el-radio>
                </el-radio-group>
              </template>
            </div>
          </div>

          <div v-else-if="loadingQuestion" class="loading-question" v-loading="true">
            加载题目中...
          </div>

          <!-- Navigation buttons -->
          <div class="nav-buttons">
            <el-button
              :disabled="currentIndex <= 0"
              @click="switchQuestion(currentIndex - 1)"
            >
              上一题
            </el-button>
            <span class="question-progress">
              {{ currentIndex + 1 }} / {{ examQuestions.length }}
            </span>
            <el-button
              :disabled="currentIndex >= examQuestions.length - 1"
              @click="switchQuestion(currentIndex + 1)"
            >
              下一题
            </el-button>
            <el-button type="danger" class="submit-btn" @click="handleSubmit">
              交卷
            </el-button>
          </div>
        </div>
      </template>

      <el-empty v-else-if="!loading && !exam" description="考试不存在或已删除" />
      <el-empty v-else-if="!loading && exam && examQuestions.length === 0" description="该考试未配置题目，请联系管理员" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExamDetail, getExamQuestions, getMyExamRecord, startExam, submitExam } from '@/api/exam'
import { getQuestionDetail } from '@/api/question'
import { QuestionType, QuestionTypeMap } from '@/utils/enums'
import { formatDuration, formatDate } from '@/utils/format'
import CodeEditor from '@/components/code/CodeEditor.vue'
import LanguageSelect from '@/components/code/LanguageSelect.vue'
import MarkdownViewer from '@/components/markdown/MarkdownViewer.vue'

const router = useRouter()
const route = useRoute()

const examId = route.params.id as string

// Exam data
const loading = ref(true)
const exam = ref<any>(null)
const examQuestions = ref<any[]>([])

// Question detail cache
const questionDetailCache = reactive<Map<number, any>>(new Map())
const currentIndex = ref(0)
const loadingQuestion = ref(false)

// Answers: Map<questionId, { answer: string, language: number }>
const answers = reactive<Map<number, { answer: string; language: number }>>(new Map())

// Timer
const remainingSeconds = ref(0)
let timerInterval: ReturnType<typeof setInterval> | null = null
let autoSubmitted = false

// --- Computed ---
const currentExamQuestion = computed(() => {
  return examQuestions.value[currentIndex.value] ?? null
})

const currentQuestionDetail = computed(() => {
  if (!currentExamQuestion.value) return null
  return questionDetailCache.get(currentExamQuestion.value.questionId) ?? null
})

const currentAnswer = computed(() => {
  const qId = currentExamQuestion.value?.questionId
  if (!qId) return { answer: '', language: 1 }
  if (!answers.has(qId)) {
    answers.set(qId, { answer: '', language: 1 })
  }
  return answers.get(qId)!
})

const parsedOptions = computed(() => {
  const detail = currentQuestionDetail.value
  if (!detail || detail.type !== QuestionType.MULTIPLE_CHOICE) return []
  if (!detail.options) return []
  try {
    const parsed = typeof detail.options === 'string'
      ? JSON.parse(detail.options)
      : detail.options
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
})

const questionTypeTagType = computed(() => {
  const type = currentQuestionDetail.value?.type
  if (type === QuestionType.PROGRAMMING) return 'primary'
  if (type === QuestionType.MULTIPLE_CHOICE) return 'success'
  if (type === QuestionType.SHORT_ANSWER) return 'info'
  if (type === QuestionType.TRUE_FALSE) return 'warning'
  return ''
})

const timerBarClass = computed(() => {
  if (remainingSeconds.value <= 300) return 'timer-danger'
  if (remainingSeconds.value <= 600) return 'timer-warning'
  return 'timer-normal'
})

// --- Methods ---
function initAnswers() {
  for (const eq of examQuestions.value) {
    if (!answers.has(eq.questionId)) {
      answers.set(eq.questionId, { answer: '', language: 1 })
    }
  }
}

function startCountdown() {
  if (!exam.value) return
  const durationMs = (exam.value.duration ?? 0) * 60 * 1000
  const endTimeMs = Date.now() + durationMs
  const tick = () => {
    remainingSeconds.value = Math.max(0, Math.floor((endTimeMs - Date.now()) / 1000))
    if (remainingSeconds.value <= 0 && !autoSubmitted) {
      autoSubmitted = true
      stopCountdown()
      doSubmit()
    }
  }
  tick()
  timerInterval = setInterval(tick, 1000)
}

function stopCountdown() {
  if (timerInterval) {
    clearInterval(timerInterval)
    timerInterval = null
  }
}

async function loadQuestionDetail(questionId: number) {
  if (questionDetailCache.has(questionId)) return
  loadingQuestion.value = true
  try {
    const res = await getQuestionDetail(questionId)
    questionDetailCache.set(questionId, res ?? null)
    if (res?.type === QuestionType.PROGRAMMING && res?.templateCode) {
      const entry = answers.get(questionId)
      if (entry && !entry.answer) {
        entry.answer = res.templateCode
      }
    }
  } catch (e) {
    console.error('加载题目详情失败:', e)
  } finally {
    loadingQuestion.value = false
  }
}

async function switchQuestion(index: number) {
  if (index < 0 || index >= examQuestions.value.length) return
  currentIndex.value = index
  const questionId = examQuestions.value[index]?.questionId
  if (questionId) {
    await loadQuestionDetail(questionId)
  }
}

function onAnswerChange(value: string) {
  const qId = currentExamQuestion.value?.questionId
  if (!qId) return
  const entry = answers.get(qId)
  if (entry) {
    entry.answer = value
  }
}

function onLanguageChange(value: number) {
  const qId = currentExamQuestion.value?.questionId
  if (!qId) return
  const entry = answers.get(qId)
  if (entry) {
    entry.language = value
  }
}

async function doSubmit() {
  try {
    const answerList: Array<{ questionId: number; answer: string; language: number }> = []
    for (const eq of examQuestions.value) {
      const ans = answers.get(eq.questionId)
      answerList.push({
        questionId: eq.questionId,
        answer: ans?.answer ?? '',
        language: ans?.language ?? 0,
      })
    }
    await submitExam(examId, { answers: answerList })
    ElMessage.success('交卷成功')
    router.push(`/user/exam/${examId}/score`)
  } catch (e) {
    console.error('交卷失败:', e)
  }
}

async function handleSubmit() {
  // Count unanswered questions
  const unansweredCount = examQuestions.value.filter(
    eq => !answers.get(eq.questionId)?.answer,
  ).length

  let confirmMessage = '确认要交卷吗？'
  if (unansweredCount > 0) {
    confirmMessage = `还有 ${unansweredCount} 道题未作答，确认要交卷吗？`
  }

  try {
    await ElMessageBox.confirm(confirmMessage, '提示', {
      confirmButtonText: '确认交卷',
      cancelButtonText: '继续答题',
      type: 'warning',
    })
  } catch {
    return // user cancelled
  }

  stopCountdown()
  await doSubmit()
}

// --- Init ---
onMounted(async () => {
  loading.value = true
  try {
    const [examRes, questionsRes] = await Promise.all([
      getExamDetail(examId),
      getExamQuestions(examId),
    ])
    exam.value = examRes ?? null
    examQuestions.value = questionsRes ?? []

    if (!exam.value || examQuestions.value.length === 0) {
      return
    }

    // Try to resume existing record; startExam if none
    let record = null
    try {
      record = await getMyExamRecord(examId)
    } catch {
      // No record yet — call startExam (idempotent: backend checks for existing record)
    }

    if (!record || record.status !== 1) {
      await startExam(examId)
    } else if (record.answers) {
      try {
        const savedAnswers = typeof record.answers === 'string'
          ? JSON.parse(record.answers)
          : record.answers
        if (Array.isArray(savedAnswers)) {
          for (const ans of savedAnswers) {
            answers.set(ans.questionId, {
              answer: ans.answer ?? '',
              language: ans.language ?? 1,
            })
          }
        }
      } catch { /* ignore parse errors */ }
    }

    initAnswers()
    startCountdown()

    if (examQuestions.value.length > 0) {
      await loadQuestionDetail(examQuestions.value[0].questionId)
    }
  } catch (e) {
    console.error('加载考试信息失败:', e)
  } finally {
    loading.value = false
  }
})

onBeforeUnmount(() => {
  stopCountdown()
})
</script>

<style scoped>
.exam-taking-page {
  height: calc(100vh - 80px);
  display: flex;
  flex-direction: column;
}

.timer-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 10px 0;
  font-weight: 600;
  font-size: 15px;
  border-radius: 6px;
  margin-bottom: 12px;
  transition: background-color 0.3s;
}

.timer-normal {
  background-color: #ecf5ff;
  color: #409eff;
}

.timer-warning {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.timer-danger {
  background-color: #fef0f0;
  color: #f56c6c;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

.timer-label {
  font-size: 14px;
}

.timer-value {
  font-size: 18px;
  font-family: 'Courier New', monospace;
}

.exam-body {
  flex: 1;
  display: flex;
  gap: 16px;
  overflow: hidden;
}

/* Left sidebar */
.question-sidebar {
  width: 200px;
  min-width: 200px;
  overflow-y: auto;
  padding: 8px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fafafa;
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  gap: 8px;
}

.question-nav-btn {
  width: 44px;
  height: 44px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  border: 2px solid #dcdfe6;
  background: #fff;
  color: #606266;
  position: relative;
}

.question-nav-btn:hover {
  border-color: #409eff;
  color: #409eff;
}

.question-nav-btn.active {
  border-color: #409eff;
  background: #409eff;
  color: #fff;
}

.question-nav-btn.answered:not(.active) {
  border-color: #67c23a;
  background: #f0f9eb;
  color: #67c23a;
}

.nav-score {
  font-size: 10px;
  transform: scale(0.9);
  line-height: 1;
  margin-top: 1px;
}

/* Right content area */
.question-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fff;
  display: flex;
  flex-direction: column;
}

.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.question-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.question-title {
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.type-tag {
  font-size: 12px;
}

.score-badge {
  font-size: 13px;
  color: #e6a23c;
  font-weight: 500;
}

.question-description {
  flex-shrink: 0;
}

.answer-area {
  flex: 1;
}

.language-select-row {
  margin-bottom: 10px;
}

.choice-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.choice-item {
  padding: 10px 14px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  transition: border-color 0.2s;
  margin: 0;
  height: auto;
}

.choice-item:hover {
  border-color: #409eff;
}

.choice-item.is-checked {
  border-color: #409eff;
  background: #ecf5ff;
}

.opt-label {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 24px;
  margin-right: 8px;
  border-radius: 4px;
  background: #f0f2f5;
  color: #606266;
  font-weight: 600;
  font-size: 13px;
  flex-shrink: 0;
}

.choice-item.is-checked .opt-label {
  background: #409eff;
  color: #fff;
}

.loading-question {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}

/* Navigation buttons */
.nav-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.question-progress {
  font-size: 14px;
  color: #909399;
}

.submit-btn {
  margin-left: auto;
}
</style>
