<template>
  <div class="page-container">
    <div class="page-header">
      <h2>题库</h2>
    </div>

    <!-- Filter Bar -->
    <el-card style="margin-bottom: 16px">
      <el-form :inline="true" :model="filters">
        <el-form-item label="分类">
          <el-select
            v-model="filters.categoryId"
            placeholder="全部分类"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select
            v-model="filters.difficulty"
            placeholder="全部难度"
            clearable
            style="width: 130px"
          >
            <el-option
              v-for="(label, value) in DifficultyMap"
              :key="value"
              :label="label"
              :value="Number(value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select
            v-model="filters.type"
            placeholder="全部类型"
            clearable
            style="width: 130px"
          >
            <el-option
              v-for="(label, value) in QuestionTypeMap"
              :key="value"
              :label="label"
              :value="Number(value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="板块">
          <el-select
            v-model="filters.sectionId"
            placeholder="全部板块"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="item in sections"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon style="margin-right: 4px"><Search /></el-icon>
            搜索
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Table -->
    <el-card>
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="title" label="题目" min-width="200" show-overflow-tooltip />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            {{ QuestionTypeMap[row.type] ?? '-' }}
          </template>
        </el-table-column>
        <el-table-column label="难度" width="90" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="row.difficulty != null"
              :type="DifficultyColor[row.difficulty] as any"
              size="small"
            >
              {{ DifficultyMap[row.difficulty] ?? '-' }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="通过数" width="100" align="center">
          <template #default="{ row }">
            {{ row.acceptedCount ?? 0 }}
          </template>
        </el-table-column>
        <el-table-column label="提交数" width="100" align="center">
          <template #default="{ row }">
            {{ row.submissionCount ?? 0 }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row.id)">
              查看
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
import { getQuestionList, getCategoryList, getSectionList } from '@/api/question'
import { QuestionTypeMap, DifficultyMap, DifficultyColor } from '@/utils/enums'
import { formatDate } from '@/utils/format'

const router = useRouter()

const loading = ref(false)
const tableData = ref<any[]>([])
const categories = ref<any[]>([])
const sections = ref<any[]>([])

const filters = reactive({
  categoryId: undefined as number | undefined,
  difficulty: undefined as number | undefined,
  type: undefined as number | undefined,
  sectionId: undefined as number | undefined,
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
})

async function loadList() {
  loading.value = true
  try {
    const res = await getQuestionList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      categoryId: filters.categoryId,
      difficulty: filters.difficulty,
      type: filters.type,
      sectionId: filters.sectionId,
    })
    tableData.value = res.records ?? []
    pagination.total = res.total ?? 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.pageNum = 1
  loadList()
}

function handleView(id: number) {
  router.push(`/user/question/${id}`)
}

onMounted(async () => {
  const [catRes, secRes] = await Promise.all([getCategoryList(), getSectionList()])
  categories.value = Array.isArray(catRes) ? catRes : []
  sections.value = Array.isArray(secRes) ? secRes : []
  loadList()
})
</script>
