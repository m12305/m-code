<template>
  <div class="page-container" v-loading="loading">
    <h2 class="page-title">学习路线</h2>

    <!-- Path Cards Grid -->
    <div class="path-grid" v-if="!loading && pathList.length > 0">
      <el-card
        v-for="item in pathList"
        :key="item.id"
        class="path-card"
        shadow="hover"
        @click="handleOpenDetail(item.id)"
      >
        <h3 class="path-card-title">{{ item.title }}</h3>
        <p class="path-card-desc">{{ item.description }}</p>
        <div class="path-card-meta">
          <el-tag size="small" type="info">{{ (item.articleIds?.length || 0) }} 篇文章</el-tag>
          <span class="path-card-order">排序 {{ item.sortOrder ?? '-' }}</span>
        </div>
      </el-card>
    </div>

    <!-- Empty State -->
    <EmptyState v-if="!loading && pathList.length === 0" description="暂无学习路线" />

    <!-- Detail Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      top="10vh"
      :close-on-click-modal="false"
    >
      <div v-loading="dialogLoading">
        <template v-if="!dialogLoading && currentPath">
          <p class="dialog-desc">{{ currentPath.description }}</p>
          <el-divider />
          <h4>包含文章 ({{ currentPath.articleIds?.length || 0 }} 篇)</h4>
          <div class="article-id-list" v-if="currentPath.articleIds?.length">
            <el-tag
              v-for="(articleId, idx) in currentPath.articleIds"
              :key="idx"
              class="article-id-tag"
              type="info"
            >
              文章 ID: {{ articleId }}
            </el-tag>
          </div>
          <span v-else class="no-articles">暂无关联文章</span>
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getLearningPathList, getLearningPathDetail } from '@/api/knowledge'
import EmptyState from '@/components/common/EmptyState.vue'

const loading = ref(false)
const pathList = ref<any[]>([])

// Dialog state
const dialogVisible = ref(false)
const dialogLoading = ref(false)
const dialogTitle = ref('')
const currentPath = ref<any>(null)

onMounted(() => {
  fetchPaths()
})

async function fetchPaths() {
  loading.value = true
  try {
    const res = await getLearningPathList()
    pathList.value = res || []
  } catch {
    pathList.value = []
  } finally {
    loading.value = false
  }
}

async function handleOpenDetail(id: number) {
  dialogVisible.value = true
  dialogLoading.value = true
  currentPath.value = null
  dialogTitle.value = '加载中...'
  try {
    const res = await getLearningPathDetail(id)
    currentPath.value = res
    dialogTitle.value = res?.title || '学习路线详情'
  } catch {
    currentPath.value = null
    dialogTitle.value = '加载失败'
  } finally {
    dialogLoading.value = false
  }
}
</script>

<style scoped>
.page-container {
  padding: 24px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
}

.path-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.path-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.path-card:hover {
  transform: translateY(-4px);
}

.path-card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.path-card-desc {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.path-card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.path-card-order {
  font-size: 12px;
  color: #c0c4cc;
}

.dialog-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.article-id-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.article-id-tag {
  cursor: default;
}

.no-articles {
  font-size: 13px;
  color: #c0c4cc;
}
</style>
