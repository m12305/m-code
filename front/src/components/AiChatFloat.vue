<template>
  <div class="ai-chat-float">
    <!-- Floating Button -->
    <div class="ai-fab" @click="togglePanel" :class="{ active: visible }">
      <el-icon :size="24" v-if="!visible"><ChatDotRound /></el-icon>
      <el-icon :size="24" v-else><Close /></el-icon>
    </div>

    <!-- Chat Panel -->
    <transition name="panel-slide">
      <div class="ai-panel" v-if="visible">
        <div class="ai-panel-header">
          <span><el-icon><ChatDotRound /></el-icon> AI 助手</span>
          <el-button text circle size="small" @click="clearChat">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>

        <div class="ai-messages" ref="msgContainer">
          <div v-if="messages.length === 0" class="ai-empty">
            <el-icon :size="40" color="#ccc"><ChatDotRound /></el-icon>
            <p>有什么可以帮你的？</p>
          </div>
          <div
            v-for="(msg, idx) in messages"
            :key="idx"
            class="ai-msg"
            :class="msg.role"
          >
            <div class="ai-msg-avatar">
              <el-avatar :size="28" v-if="msg.role === 'assistant'">AI</el-avatar>
              <el-avatar :size="28" v-else>{{ userStore.userInfo?.nickname?.charAt(0) || '我' }}</el-avatar>
            </div>
            <div class="ai-msg-content">
              <div class="ai-msg-text" v-html="renderContent(msg.content)"></div>
              <span class="ai-msg-cursor" v-if="streaming && idx === messages.length - 1 && msg.role === 'assistant'">|</span>
            </div>
          </div>
          <div v-if="streaming && messages[messages.length-1]?.role === 'user'" class="ai-msg assistant">
            <div class="ai-msg-avatar">
              <el-avatar :size="28">AI</el-avatar>
            </div>
            <div class="ai-msg-content">
              <div class="ai-msg-text">...</div>
            </div>
          </div>
        </div>

        <div class="ai-input-area">
          <el-input
            v-model="inputText"
            placeholder="输入消息，Enter 发送"
            :disabled="streaming"
            @keyup.enter="sendMessage"
          >
            <template #append>
              <el-button
                :icon="streaming ? undefined : undefined"
                @click="sendMessage"
                :disabled="streaming || !inputText.trim()"
              >
                <el-icon v-if="!streaming"><Promotion /></el-icon>
                <el-icon v-else class="loading-icon"><Loading /></el-icon>
              </el-button>
            </template>
          </el-input>
          <span class="ai-disclaimer">AI 回答仅供参考</span>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { chatStream } from '@/api/ai'

interface Message {
  role: 'user' | 'assistant'
  content: string
}

const userStore = useUserStore()
const visible = ref(false)
const inputText = ref('')
const messages = ref<Message[]>([])
const streaming = ref(false)
const msgContainer = ref<HTMLElement | null>(null)
let abortCtrl: AbortController | null = null

function togglePanel() {
  visible.value = !visible.value
}

function clearChat() {
  if (streaming.value) {
    abortCtrl?.abort()
    streaming.value = false
  }
  messages.value = []
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || streaming.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  streaming.value = true

  await nextTick()
  scrollToBottom()

  // build the conversation history to send
  const history = messages.value.map((m) => ({
    role: m.role,
    content: m.content,
  }))

  // placeholder for assistant response
  messages.value.push({ role: 'assistant', content: '' })

  abortCtrl = chatStream(
    history,
    (token) => {
      const last = messages.value[messages.value.length - 1]
      if (last && last.role === 'assistant') {
        last.content += token
        nextTick(() => scrollToBottom())
      }
    },
    () => {
      streaming.value = false
      abortCtrl = null
    },
    (err) => {
      const last = messages.value[messages.value.length - 1]
      if (last && last.role === 'assistant' && !last.content) {
        last.content = '抱歉，请求失败: ' + err.message
      }
      streaming.value = false
      abortCtrl = null
    },
  )
}

function scrollToBottom() {
  const el = msgContainer.value
  if (el) {
    el.scrollTop = el.scrollHeight
  }
}

function renderContent(text: string): string {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/\n/g, '<br>')
    .replace(/```(\w*)([\s\S]*?)```/g, '<pre><code>$2</code></pre>')
}
</script>

<style scoped>
.ai-chat-float {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 2000;
}

/* FAB */
.ai-fab {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  transition: transform 0.2s, box-shadow 0.2s;
}
.ai-fab:hover {
  transform: scale(1.08);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.55);
}
.ai-fab.active {
  background: #606266;
  box-shadow: 0 4px 12px rgba(96, 96, 96, 0.4);
}

/* Panel */
.ai-panel {
  position: absolute;
  right: 0;
  bottom: 68px;
  width: 420px;
  height: 560px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-slide-enter-active {
  transition: all 0.25s ease-out;
}
.panel-slide-leave-active {
  transition: all 0.15s ease-in;
}
.panel-slide-enter-from {
  opacity: 0;
  transform: translateY(16px) scale(0.95);
}
.panel-slide-leave-to {
  opacity: 0;
  transform: translateY(8px) scale(0.97);
}

/* Header */
.ai-panel-header {
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  flex-shrink: 0;
}
.ai-panel-header span {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* Messages */
.ai-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ai-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #aaa;
  font-size: 14px;
  gap: 8px;
}

.ai-msg {
  display: flex;
  gap: 8px;
  max-width: 90%;
}
.ai-msg.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}
.ai-msg.assistant {
  align-self: flex-start;
}

.ai-msg-avatar {
  flex-shrink: 0;
}

.ai-msg-content {
  position: relative;
}

.ai-msg-text {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}
.ai-msg.user .ai-msg-text {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}
.ai-msg.assistant .ai-msg-text {
  background: #f2f3f5;
  color: #303133;
  border-bottom-left-radius: 4px;
}

.ai-msg-text :deep(pre) {
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 10px 14px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 6px 0;
  font-size: 13px;
  line-height: 1.5;
}
.ai-msg-text :deep(code) {
  font-family: 'Consolas', 'Courier New', monospace;
}

.ai-msg-cursor {
  display: inline-block;
  animation: blink 0.8s infinite;
  color: #409eff;
  font-weight: bold;
  margin-left: 2px;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* Input */
.ai-input-area {
  padding: 12px 16px;
  border-top: 1px solid #ebeef5;
  flex-shrink: 0;
}
.ai-disclaimer {
  display: block;
  text-align: center;
  font-size: 11px;
  color: #bbb;
  margin-top: 6px;
}

.loading-icon {
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
