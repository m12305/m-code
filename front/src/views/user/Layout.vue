<template>
  <el-container class="layout">
    <el-header class="layout-header">
      <div class="header-left">
        <span class="logo" @click="$router.push('/user/home')">m-code</span>
        <el-menu mode="horizontal" :default-active="activeMenu" router class="header-menu">
          <el-menu-item index="/user/home">首页</el-menu-item>
          <el-menu-item index="/user/question">题库</el-menu-item>
          <el-menu-item index="/user/exam">考试</el-menu-item>
          <el-menu-item index="/user/article">知识</el-menu-item>
          <el-menu-item index="/user/path">学习路线</el-menu-item>
          <el-menu-item index="/user/submission">提交记录</el-menu-item>
        </el-menu>
      </div>
      <div class="header-right">
        <el-dropdown trigger="click">
          <span class="user-info">
            <el-avatar :size="32">{{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}</el-avatar>
            <span class="nickname">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/user/profile')">个人信息</el-dropdown-item>
              <el-dropdown-item v-if="userStore.isAdmin" @click="$router.push('/admin/dashboard')">管理后台</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-main class="layout-main">
      <router-view />
    </el-main>
    <AiChatFloat v-if="showAiChat" />
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import AiChatFloat from '@/components/AiChatFloat.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const showAiChat = computed(() => !route.path.includes('/exam/') || !route.path.includes('/taking'))

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/user/submission')) return '/user/submission'
  if (path.startsWith('/user/question')) return '/user/question'
  if (path.startsWith('/user/exam')) return '/user/exam'
  if (path.startsWith('/user/article')) return '/user/article'
  if (path.startsWith('/user/path')) return '/user/path'
  return '/user/home'
})

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  height: 100vh;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 24px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  font-size: 22px;
  font-weight: 700;
  color: #409eff;
  cursor: pointer;
}

.header-menu {
  border-bottom: none !important;
}

.header-menu :deep(.el-menu-item) {
  height: 60px;
  line-height: 60px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.nickname {
  font-size: 14px;
  color: #303133;
}

.layout-main {
  background: #f5f7fa;
  height: calc(100vh - 60px);
  overflow-y: auto;
  padding: 0;
}
</style>
