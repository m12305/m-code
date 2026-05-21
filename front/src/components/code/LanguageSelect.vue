<template>
  <el-select
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    placeholder="选择语言"
    style="width: 140px"
  >
    <el-option
      v-for="lang in languages"
      :key="lang.value"
      :label="lang.label"
      :value="lang.value"
    />
  </el-select>
</template>

<script setup lang="ts">
import { LanguageMap } from '@/utils/enums'

defineProps<{ modelValue: number }>()
defineEmits<{ 'update:modelValue': [value: number] }>()

const languages = Object.entries(LanguageMap).map(([k, v]) => ({
  value: Number(k),
  label: v,
}))
</script>
