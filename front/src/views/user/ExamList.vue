<template>
  <div class="exam-list-page">
    <h2 class="page-title">考试列表</h2>

    <el-table
      v-loading="loading"
      :data="list"
      stripe
      style="width: 100%"
      empty-text="暂无考试"
    >
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="title" label="考试名称" min-width="180" show-overflow-tooltip />
      <el-table-column label="时长" width="90" align="center">
        <template #default="{ row }">
          {{ row.duration }} 分钟
        </template>
      </el-table-column>
      <el-table-column label="开始时间" width="170" align="center">
        <template #default="{ row }">
          {{ formatDate(row.startTime) }}
        </template>
      </el-table-column>
      <el-table-column label="结束时间" width="170" align="center">
        <template #default="{ row }">
          {{ formatDate(row.endTime) }}
        </template>
      </el-table-column>
      <el-table-column label="总分" width="80" align="center">
        <template #default="{ row }">
          {{ row.totalScore }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '进行中' : '已结束' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" link @click="goDetail(row.id)">
            查看
          </el-button>
          <el-button
            v-if="row.status === 1"
            size="small"
            type="success"
            link
            :loading="startingId === row.id"
            @click="handleStart(row.id)"
          >
            开始考试
          </el-button>
          <el-button size="small" type="warning" link @click="goScore(row.id)">
            查看成绩
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="fetchList"
        @size-change="fetchList"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getExamList, startExam } from '@/api/exam'
import { formatDate } from '@/utils/format'

const router = useRouter()

const loading = ref(false)
const list = ref<any[]>([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const startingId = ref<number | null>(null)

async function fetchList() {
  loading.value = true
  try {
    const res = await getExamList({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = res?.records ?? []
    total.value = res?.total ?? 0
  } catch {
    /* error handled by interceptor */
  } finally {
    loading.value = false
  }
}

function goDetail(id: number) {
  router.push(`/user/exam/${id}`)
}

function goScore(id: number) {
  router.push(`/user/exam/${id}/score`)
}

async function handleStart(examId: number) {
  startingId.value = examId
  try {
    await startExam(examId)
    ElMessage.success('考试已开始')
    router.push(`/user/exam/${examId}/taking`)
  } catch {
    /* error handled by interceptor */
  } finally {
    startingId.value = null
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.exam-list-page {
  padding: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
