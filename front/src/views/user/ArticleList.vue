<template>
  <div class="page-container">
    <h2 class="page-title">知识文章</h2>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <el-select
        v-model="categoryId"
        placeholder="选择分类"
        clearable
        style="width: 200px"
        :loading="categoryLoading"
      >
        <el-option
          v-for="item in categoryList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- Article Table -->
    <el-table
      :data="articleList"
      v-loading="loading"
      border
      stripe
      style="width: 100%; margin-top: 16px"
    >
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="summary" label="摘要" min-width="240" show-overflow-tooltip />
      <el-table-column prop="authorName" label="作者" width="120" align="center" />
      <el-table-column prop="viewCount" label="浏览量" width="90" align="center" />
      <el-table-column prop="likeCount" label="点赞数" width="80" align="center" />
      <el-table-column label="发布时间" width="180" align="center">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row.id)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Empty State -->
    <EmptyState v-if="!loading && articleList.length === 0" description="暂无文章" />

    <!-- Pagination -->
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="fetchList"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getKnowledgeCategoryList, getArticleList } from '@/api/knowledge'
import { formatDate } from '@/utils/format'
import EmptyState from '@/components/common/EmptyState.vue'

const router = useRouter()

const loading = ref(false)
const categoryLoading = ref(false)
const categoryList = ref<any[]>([])
const categoryId = ref<number | undefined>(undefined)
const articleList = ref<any[]>([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

onMounted(() => {
  fetchCategories()
  fetchList()
})

async function fetchCategories() {
  categoryLoading.value = true
  try {
    const res = await getKnowledgeCategoryList()
    categoryList.value = res || []
  } catch {
    /* ignore */
  } finally {
    categoryLoading.value = false
  }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getArticleList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      categoryId: categoryId.value,
    })
    articleList.value = res?.records || []
    total.value = res?.total || 0
  } catch {
    articleList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  fetchList()
}

function handleSizeChange() {
  pageNum.value = 1
  fetchList()
}

function handleView(id: number) {
  router.push(`/user/article/${id}`)
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
  margin-bottom: 16px;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
