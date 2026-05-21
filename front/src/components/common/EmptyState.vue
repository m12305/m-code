<template>
  <div class="empty-container">
    <el-empty :description="description" />
    <slot />
  </div>
</template>

<script setup lang="ts">
defineProps<{ description?: string }>()
</script>
