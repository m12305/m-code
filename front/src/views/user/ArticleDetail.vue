<template>
  <div class="page-container" v-loading="loading">
    <template v-if="!loading && article">
      <!-- Title -->
      <h1 class="article-title">{{ article.title }}</h1>

      <!-- Author & Meta -->
      <div class="article-meta">
        <span class="article-author">{{ article.authorName }}</span>
        <span class="meta-divider">|</span>
        <span>{{ formatDate(article.createTime) }}</span>
        <span class="meta-divider">|</span>
        <span>{{ article.viewCount }} 次浏览</span>
        <span class="meta-divider">|</span>
        <span>{{ article.likeCount }} 次点赞</span>
      </div>

      <el-divider />

      <!-- Content -->
      <div class="article-content">
        <MarkdownViewer :content="article.content || ''" />
      </div>
    </template>

    <!-- Not Found -->
    <EmptyState v-if="!loading && !article" description="文章未找到" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleDetail } from '@/api/knowledge'
import { formatDate } from '@/utils/format'
import MarkdownViewer from '@/components/markdown/MarkdownViewer.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const route = useRoute()

const loading = ref(false)
const article = ref<any>(null)

onMounted(() => {
  fetchDetail()
})

async function fetchDetail() {
  const id = route.params.id as string
  if (!id) return

  loading.value = true
  try {
    const res = await getArticleDetail(id)
    article.value = res || null
  } catch {
    article.value = null
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page-container {
  padding: 24px;
  max-width: 900px;
  margin: 0 auto;
}

.article-title {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 12px;
}

.article-meta {
  font-size: 14px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.article-author {
  font-weight: 500;
  color: #606266;
}

.meta-divider {
  color: #dcdfe6;
}

.article-content {
  margin-top: 16px;
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
}
</style>
