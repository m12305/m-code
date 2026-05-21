<template>
  <div>
    <div class="page-header">
      <h2>题目管理</h2>
      <el-button type="primary" @click="showAdd">添加题目</el-button>
    </div>

    <div class="filter-bar">
      <el-select v-model="query.type" placeholder="题目类型" clearable style="width:130px">
        <el-option v-for="(v,k) in QuestionTypeMap" :key="k" :label="v" :value="Number(k)" />
      </el-select>
      <el-select v-model="query.difficulty" placeholder="难度" clearable style="width:110px">
        <el-option v-for="(v,k) in DifficultyMap" :key="k" :label="v" :value="Number(k)" />
      </el-select>
      <el-select v-model="query.categoryId" placeholder="分类" clearable style="width:130px">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-button type="primary" @click="fetchList">查询</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
      <el-table-column label="类型" width="90">
        <template #default="{row}">{{ QuestionTypeMap[(row.type as any)?.code ?? row.type] }}</template>
      </el-table-column>
      <el-table-column label="难度" width="80">
        <template #default="{row}">
          <el-tag :type="DifficultyColor[(row.difficulty as any)?.code ?? row.difficulty] as any" size="small">
            {{ DifficultyMap[(row.difficulty as any)?.code ?? row.difficulty] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="acceptedCount" label="通过" width="70" />
      <el-table-column prop="submissionCount" label="提交" width="70" />
      <el-table-column label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'上架':'下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除?" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination style="margin-top:16px" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
      :page-sizes="[10,20,50]" :total="total" layout="total,sizes,prev,pager,next" @change="fetchList" />

    <!-- 编辑/添加弹窗 -->
    <el-dialog :title="editing?.id ? '编辑题目' : '添加题目'" v-model="dialogVisible" width="700px" destroy-on-close>
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="题目类型" required>
          <el-select v-model="form.type" style="width:100%">
            <el-option v-for="(v,k) in QuestionTypeMap" :key="k" :label="v" :value="v" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度" required>
          <el-select v-model="form.difficulty" style="width:100%">
            <el-option v-for="(v,k) in DifficultyMap" :key="k" :label="v" :value="v" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" style="width:100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="板块">
          <el-select v-model="form.sectionId" style="width:100%">
            <el-option v-for="s in sections" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目描述">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="代码模板" v-if="form.type==='PROGRAMMING'">
          <el-input v-model="form.templateCode" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="测试用例" v-if="form.type==='PROGRAMMING'">
          <el-input v-model="form.testCases" type="textarea" :rows="3" placeholder="JSON格式测试用例" />
        </el-form-item>
        <el-form-item label="选项" v-if="form.type==='MULTIPLE_CHOICE'">
          <el-input v-model="form.options" type="textarea" :rows="2" placeholder="JSON格式选项" />
        </el-form-item>
        <el-form-item label="正确答案">
          <el-input v-model="form.correctAnswer" />
        </el-form-item>
        <el-form-item label="参考答案">
          <el-input v-model="form.referenceAnswer" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getQuestionList, addQuestion, updateQuestion, deleteQuestion, getCategoryList, getSectionList } from '@/api/question'
import { QuestionTypeMap, DifficultyMap, DifficultyColor } from '@/utils/enums'

const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const categories = ref<any[]>([])
const sections = ref<any[]>([])
const editing = ref<any>(null)

const query = reactive({ pageNum: 1, pageSize: 10, categoryId: undefined as any, difficulty: undefined as any, type: undefined as any })

const initForm = () => ({
  title: '', description: '', type: 'PROGRAMMING', difficulty: 'EASY',
  categoryId: undefined as any, sectionId: undefined as any,
  templateCode: '', testCases: '', options: '', correctAnswer: '', referenceAnswer: '',
})
const form = reactive(initForm())

onMounted(async () => {
  const [catRes, secRes] = await Promise.all([getCategoryList(), getSectionList()])
  categories.value = catRes || []
  sections.value = secRes || []
  fetchList()
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getQuestionList({ ...query })
    tableData.value = res?.records || []
    total.value = res?.total || 0
  } finally { loading.value = false }
}

function showAdd() {
  editing.value = null
  Object.assign(form, initForm())
  dialogVisible.value = true
}

function showEdit(row: any) {
  editing.value = row
  Object.assign(form, {
    title: row.title, description: row.description || '',
    type: QuestionTypeMap[(row.type as any)?.code ?? row.type] || Object.keys(QuestionTypeMap).find(k => QuestionTypeMap[Number(k)] === row.type) || 'PROGRAMMING',
    difficulty: DifficultyMap[(row.difficulty as any)?.code ?? row.difficulty] || Object.keys(DifficultyMap).find(k => DifficultyMap[Number(k)] === row.difficulty) || 'EASY',
    categoryId: row.categoryId, sectionId: row.sectionId,
    templateCode: row.templateCode || '', testCases: row.testCases || '',
    options: row.options || '', correctAnswer: row.correctAnswer || '', referenceAnswer: row.referenceAnswer || '',
  })
  dialogVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    if (editing.value?.id) {
      await updateQuestion({ id: editing.value.id, ...form, categoryId: form.categoryId || null, sectionId: form.sectionId || null })
    } else {
      await addQuestion({ ...form })
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchList()
  } finally { saving.value = false }
}

async function handleDelete(id: number) {
  await deleteQuestion(id)
  ElMessage.success('已删除')
  fetchList()
}
</script>
