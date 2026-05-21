<template>
  <div class="page-container">
    <div class="page-header">
      <el-button link type="primary" @click="router.push('/user/question')">
        <el-icon style="margin-right: 4px"><ArrowLeft /></el-icon>
        返回题库
      </el-button>
    </div>

    <el-card v-loading="loading">
      <template v-if="!loading && question">
        <!-- Header -->
        <div style="margin-bottom: 16px">
          <h2 style="margin: 0 0 8px 0">{{ question.title }}</h2>
          <el-space wrap>
            <el-tag
              v-if="question.difficulty != null"
              :type="DifficultyColor[question.difficulty] as any"
            >
              {{ DifficultyMap[question.difficulty] ?? '-' }}
            </el-tag>
            <el-tag
              v-if="question.type != null"
              type="info"
            >
              {{ QuestionTypeMap[question.type] ?? '-' }}
            </el-tag>
            <span style="color: #999; font-size: 13px">
              通过 {{ question.acceptedCount ?? 0 }} / 提交 {{ question.submissionCount ?? 0 }}
            </span>
          </el-space>
        </div>

        <el-divider />

        <!-- Description -->
        <div style="margin-bottom: 24px">
          <h4>题目描述</h4>
          <template v-if="question.type === QuestionType.PROGRAMMING">
            <MarkdownViewer
              v-if="question.description"
              :content="question.description"
            />
            <EmptyState v-else description="暂无题目描述" />
          </template>
          <template v-else>
            <p v-if="question.description" style="white-space: pre-wrap; line-height: 1.8">
              {{ question.description }}
            </p>
            <EmptyState v-else description="暂无题目描述" />
          </template>
        </div>

        <el-divider />

        <!-- Answer Area -->
        <div>
          <h4 style="margin-bottom: 16px">
            {{ isProgramming ? '提交代码' : '提交答案' }}
          </h4>

          <!-- Programming -->
          <template v-if="isProgramming">
            <div style="margin-bottom: 12px">
              <LanguageSelect v-model="language" />
            </div>
            <CodeEditor
              v-model="answer"
              :language="language"
              :height="400"
            />
            <div style="margin-top: 16px">
              <el-button
                type="primary"
                :loading="submitting"
                :disabled="!answer.trim()"
                @click="handleSubmit"
              >
                提交代码
              </el-button>
            </div>
          </template>

          <!-- Multiple Choice -->
          <template v-else-if="question.type === QuestionType.MULTIPLE_CHOICE">
            <el-radio-group v-model="answer" class="choice-group">
              <el-radio
                v-for="(opt, idx) in parsedOptions"
                :key="idx"
                :value="opt.label || String.fromCharCode(65 + idx)"
                class="choice-item"
              >
                <span class="opt-label">{{ opt.label || String.fromCharCode(65 + idx) }}.</span>
                <span>{{ opt.value ?? opt }}</span>
              </el-radio>
            </el-radio-group>
            <div style="margin-top: 16px">
              <el-button
                type="primary"
                :loading="submitting"
                :disabled="!answer"
                @click="handleSubmit"
              >
                提交答案
              </el-button>
            </div>
          </template>

          <!-- True / False -->
          <template v-else-if="question.type === QuestionType.TRUE_FALSE">
            <el-radio-group v-model="answer" class="choice-group">
              <el-radio value="正确" class="choice-item">
                <span class="opt-label">A.</span> 正确
              </el-radio>
              <el-radio value="错误" class="choice-item">
                <span class="opt-label">B.</span> 错误
              </el-radio>
            </el-radio-group>
            <div style="margin-top: 16px">
              <el-button
                type="primary"
                :loading="submitting"
                :disabled="!answer"
                @click="handleSubmit"
              >
                提交答案
              </el-button>
            </div>
          </template>

          <!-- Short Answer -->
          <template v-else-if="question.type === QuestionType.SHORT_ANSWER">
            <el-input
              v-model="answer"
              type="textarea"
              :rows="6"
              placeholder="请输入你的答案"
              maxlength="5000"
              show-word-limit
            />
            <div style="margin-top: 16px">
              <el-button
                type="primary"
                :loading="submitting"
                :disabled="!answer.trim()"
                @click="handleSubmit"
              >
                提交答案
              </el-button>
            </div>
          </template>

          <!-- Unknown type fallback -->
          <EmptyState v-else description="未知题目类型" />
        </div>
      </template>

      <EmptyState v-if="!loading && !question" description="题目不存在" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getQuestionDetail } from '@/api/question'
import { submitCode } from '@/api/judge'
import {
  QuestionType,
  QuestionTypeMap,
  DifficultyMap,
  DifficultyColor,
} from '@/utils/enums'
import { ElMessage } from 'element-plus'
import CodeEditor from '@/components/code/CodeEditor.vue'
import LanguageSelect from '@/components/code/LanguageSelect.vue'
import MarkdownViewer from '@/components/markdown/MarkdownViewer.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const question = ref<any>(null)
const answer = ref('')
const language = ref(1) // default JAVA

const isProgramming = computed(() => question.value?.type === QuestionType.PROGRAMMING)

const parsedOptions = computed(() => {
  if (!question.value?.options) return []
  try {
    const opts = typeof question.value.options === 'string'
      ? JSON.parse(question.value.options)
      : question.value.options
    return Array.isArray(opts) ? opts : []
  } catch {
    return []
  }
})

async function loadQuestion() {
  const id = route.params.id as string
  if (!id) return
  loading.value = true
  try {
    question.value = await getQuestionDetail(id)
    if (question.value?.type === QuestionType.PROGRAMMING && question.value?.templateCode) {
      answer.value = question.value.templateCode
    }
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  const q = question.value
  if (!q) return

  submitting.value = true
  try {
    const res = await submitCode({
      questionId: q.id,
      answer: answer.value,
      language: isProgramming.value ? language.value : 0,
    })
    ElMessage.success('提交成功')
    router.push(`/user/submission/${res.id}`)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadQuestion()
})
</script>

<style scoped>
.choice-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

.choice-item {
  display: flex;
  align-items: flex-start;
  padding: 12px 16px;
  border: 1.5px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.2s ease;
  margin: 0;
  height: auto;
  white-space: normal;
}

.choice-item:hover {
  border-color: #409eff;
  background: #f5f9ff;
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
</style>
