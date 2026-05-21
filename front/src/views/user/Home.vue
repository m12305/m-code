<template>
  <div class="page-container">
    <div class="home-banner">
      <div class="banner-content">
        <h2>欢迎回来，{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</h2>
        <p>积分：{{ userStore.userInfo?.score ?? 0 }}</p>
      </div>
    </div>

    <div class="home-grid">
      <el-card class="home-card" shadow="hover" @click="$router.push('/user/question')">
        <el-icon :size="36" color="#409eff"><Collection /></el-icon>
        <h3>题库练习</h3>
        <p>编程 / 选择 / 判断 / 简答</p>
      </el-card>
      <el-card class="home-card" shadow="hover" @click="$router.push('/user/exam')">
        <el-icon :size="36" color="#67c23a"><Tickets /></el-icon>
        <h3>参加考试</h3>
        <p>限时考试，检验水平</p>
      </el-card>
      <el-card class="home-card" shadow="hover" @click="$router.push('/user/submission')">
        <el-icon :size="36" color="#e6a23c"><Document /></el-icon>
        <h3>提交记录</h3>
        <p>查看历史提交与判题结果</p>
      </el-card>
      <el-card class="home-card" shadow="hover" @click="$router.push('/user/article')">
        <el-icon :size="36" color="#f56c6c"><Reading /></el-icon>
        <h3>知识文章</h3>
        <p>学习编程知识</p>
      </el-card>
    </div>

    <div class="home-section" v-if="recentExams.length">
      <h3>近期考试</h3>
      <el-row :gutter="16">
        <el-col v-for="exam in recentExams" :key="exam.id" :span="8">
          <el-card shadow="hover" class="exam-card" @click="$router.push(`/user/exam/${exam.id}`)">
            <h4>{{ exam.title }}</h4>
            <p>{{ exam.description }}</p>
            <div class="exam-meta">
              <span>总分：{{ exam.totalScore }}</span>
              <span>时长：{{ exam.duration }}分钟</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getExamList } from '@/api/exam'

const userStore = useUserStore()
const recentExams = ref<any[]>([])

onMounted(async () => {
  try {
    const res = await getExamList({ pageNum: 1, pageSize: 6 })
    recentExams.value = res?.records || []
  } catch { /* ignore */ }
})
</script>

<style scoped>
.home-banner {
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 24px;
  color: #fff;
}

.home-banner h2 {
  font-size: 22px;
  margin-bottom: 8px;
}

.home-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.home-card {
  cursor: pointer;
  text-align: center;
  padding: 24px 0;
  transition: transform 0.2s;
}

.home-card:hover {
  transform: translateY(-4px);
}

.home-card h3 {
  margin: 12px 0 4px;
  font-size: 16px;
}

.home-card p {
  font-size: 13px;
  color: #909399;
}

.home-section {
  margin-bottom: 24px;
}

.home-section h3 {
  font-size: 18px;
  margin-bottom: 12px;
  color: #303133;
}

.exam-card {
  cursor: pointer;
  margin-bottom: 8px;
}

.exam-card h4 {
  font-size: 15px;
  margin-bottom: 4px;
}

.exam-card p {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.exam-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #606266;
}
</style>
