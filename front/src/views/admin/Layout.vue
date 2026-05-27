<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="admin-sidebar">
      <div class="sidebar-header">
        <span class="logo" @click="$router.push('/admin/dashboard')">m-code 管理</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><HomeFilled /></el-icon> 控制台
        </el-menu-item>
        <el-sub-menu index="question-group">
          <template #title><el-icon><Collection /></el-icon> 题目管理</template>
          <el-menu-item index="/admin/question">题目列表</el-menu-item>
          <el-menu-item index="/admin/category">分类管理</el-menu-item>
          <el-menu-item index="/admin/tag">标签管理</el-menu-item>
          <el-menu-item index="/admin/section">板块管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="exam-group">
          <template #title><el-icon><Tickets /></el-icon> 考试管理</template>
          <el-menu-item index="/admin/exam">考试列表</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="knowledge-group">
          <template #title><el-icon><Reading /></el-icon> 知识管理</template>
          <el-menu-item index="/admin/article">文章管理</el-menu-item>
          <el-menu-item index="/admin/knowledge-category">知识分类</el-menu-item>
          <el-menu-item index="/admin/learning-path">学习路线</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="system-group">
          <template #title><el-icon><Setting /></el-icon> 系统管理</template>
          <el-menu-item index="/admin/user">用户管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理后台</el-breadcrumb-item>
            <el-breadcrumb-item v-if="pageTitle">{{ pageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="32">{{ userStore.userInfo?.nickname?.charAt(0) || 'A' }}</el-avatar>
              <span>{{ userStore.userInfo?.nickname }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-item @click="$router.push('/user/home')">返回用户端</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title as string || '')

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout { height: 100vh; }
.admin-sidebar { background: #304156; overflow-y: auto; }
.sidebar-header { padding: 16px 20px; text-align: center; border-bottom: 1px solid rgba(255,255,255,0.05); }
.sidebar-header .logo { color: #fff; font-size: 18px; font-weight: 700; cursor: pointer; }
.admin-header { background: #fff; border-bottom: 1px solid #e4e7ed; display: flex; align-items: center; justify-content: space-between; padding: 0 20px; height: 50px; }
.header-right { display: flex; align-items: center; }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; }
.admin-main { background: #f5f7fa; padding: 20px; overflow-y: auto; }
</style>
