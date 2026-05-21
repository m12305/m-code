<template>
  <div class="code-editor-wrapper" :style="{ height: height + 'px' }">
    <div ref="editorRef" class="editor-container"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, shallowRef } from 'vue'
import * as monaco from 'monaco-editor'
import { LanguageMonacoMap } from '@/utils/enums'

const props = defineProps<{
  modelValue: string
  language: number
  height?: number
  readOnly?: boolean
}>()

const emit = defineEmits<{ 'update:modelValue': [value: string] }>()

const editorRef = ref<HTMLDivElement>()
const editor = shallowRef<monaco.editor.IStandaloneCodeEditor>()

onMounted(() => {
  if (!editorRef.value) return
  const lang = LanguageMonacoMap[props.language] || 'plaintext'
  editor.value = monaco.editor.create(editorRef.value, {
    value: props.modelValue,
    language: lang,
    theme: 'vs-dark',
    fontSize: 14,
    lineNumbers: 'on',
    minimap: { enabled: false },
    automaticLayout: true,
    scrollBeyondLastLine: false,
    tabSize: 4,
    readOnly: props.readOnly ?? false,
  })
  editor.value.onDidChangeModelContent(() => {
    emit('update:modelValue', editor.value!.getValue())
  })
})

watch(
  () => props.language,
  (lang) => {
    if (!editor.value) return
    const monacoLang = LanguageMonacoMap[lang] || 'plaintext'
    monaco.editor.setModelLanguage(editor.value.getModel()!, monacoLang)
  },
)

watch(
  () => props.modelValue,
  (val) => {
    if (!editor.value) return
    if (editor.value.getValue() !== val) {
      editor.value.setValue(val)
    }
  },
)

onBeforeUnmount(() => {
  editor.value?.dispose()
})
</script>

<style scoped>
.editor-container {
  width: 100%;
  height: 100%;
}
</style>
