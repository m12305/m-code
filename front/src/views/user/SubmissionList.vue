<template>
  <div class="page-container">
    <div class="page-header">
      <h2>提交记录</h2>
    </div>

    <el-card>
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="questionId" label="题目ID" width="100" align="center" />
        <el-table-column label="语言" width="120" align="center">
          <template #default="{ row }">
            {{ LanguageMap[row.language] ?? '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.status != null"
              :type="JudgeStatusColor[row.status] as any"
              size="small"
            >
              {{ JudgeStatusMap[row.status] ?? '-' }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="耗时" width="110" align="center">
          <template #default="{ row }">
            {{ formatTime(row.timeUsed) }}
          </template>
        </el-table-column>
        <el-table-column label="内存" width="110" align="center">
          <template #default="{ row }">
            {{ formatMemory(row.memoryUsed) }}
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="170" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row.id)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="display: flex; justify-content: center; margin-top: 20px">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadList"
          @size-change="loadList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSubmissionList } from '@/api/judge'
import { LanguageMap, JudgeStatusMap, JudgeStatusColor } from '@/utils/enums'
import { formatDate, formatMemory, formatTime } from '@/utils/format'

const router = useRouter()

const loading = ref(false)
const tableData = ref<any[]>([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0,
})

async function loadList() {
  loading.value = true
  try {
    const res = await getSubmissionList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    })
    tableData.value = res.records ?? []
    pagination.total = res.total ?? 0
  } finally {
    loading.value = false
  }
}

function handleDetail(id: number) {
  router.push(`/user/submission/${id}`)
}

onMounted(() => {
  loadList()
})
</script>
