<template>
  <div>
    <h2 style="margin-bottom:20px">控制台</h2>
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="题目总数" :value="stats.questionCount" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="考试总数" :value="stats.examCount" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="文章总数" :value="stats.articleCount" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="用户总数" :value="stats.userCount" />
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top:20px">
      <template #header>快捷操作</template>
      <el-space wrap>
        <el-button type="primary" @click="$router.push('/admin/question')">管理题目</el-button>
        <el-button type="success" @click="$router.push('/admin/exam')">管理考试</el-button>
        <el-button type="warning" @click="$router.push('/admin/article')">管理文章</el-button>
        <el-button @click="$router.push('/user/home')">返回用户端</el-button>
      </el-space>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { getQuestionList } from '@/api/question'
import { getExamList } from '@/api/exam'
import { getArticleList } from '@/api/knowledge'
import { getUserList } from '@/api/user'

const stats = reactive({ questionCount: 0, examCount: 0, articleCount: 0, userCount: 0 })

onMounted(async () => {
  try {
    const [qRes, eRes, aRes, uRes] = await Promise.all([
      getQuestionList({ pageSize: 1 }),
      getExamList({ pageSize: 1 }),
      getArticleList({ pageSize: 1 }),
      getUserList({ pageSize: 1 }),
    ])
    stats.questionCount = qRes?.total ?? 0
    stats.examCount = eRes?.total ?? 0
    stats.articleCount = aRes?.total ?? 0
    stats.userCount = uRes?.total ?? 0
  } catch { /* ignore */ }
})
</script>

<style scoped>
.stat-card { text-align: center; }
</style>
