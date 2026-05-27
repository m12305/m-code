import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { UserRole } from '@/utils/enums'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/user/Login.vue'),
      meta: { title: '登录', noAuth: true },
    },
    {
      path: '/',
      redirect: '/user/home',
    },
    // 普通用户端
    {
      path: '/user',
      component: () => import('@/views/user/Layout.vue'),
      redirect: '/user/home',
      children: [
        {
          path: 'home',
          name: 'Home',
          component: () => import('@/views/user/Home.vue'),
          meta: { title: '首页' },
        },
        {
          path: 'question',
          name: 'QuestionList',
          component: () => import('@/views/user/QuestionList.vue'),
          meta: { title: '题库' },
        },
        {
          path: 'question/:id',
          name: 'QuestionDetail',
          component: () => import('@/views/user/QuestionDetail.vue'),
          meta: { title: '题目详情' },
        },
        {
          path: 'submission',
          name: 'SubmissionList',
          component: () => import('@/views/user/SubmissionList.vue'),
          meta: { title: '提交记录' },
        },
        {
          path: 'submission/:id',
          name: 'SubmissionDetail',
          component: () => import('@/views/user/SubmissionDetail.vue'),
          meta: { title: '提交详情' },
        },
        {
          path: 'exam',
          name: 'ExamList',
          component: () => import('@/views/user/ExamList.vue'),
          meta: { title: '考试列表' },
        },
        {
          path: 'exam/:id',
          name: 'ExamDetail',
          component: () => import('@/views/user/ExamDetail.vue'),
          meta: { title: '考试详情' },
        },
        {
          path: 'exam/:id/taking',
          name: 'ExamTaking',
          component: () => import('@/views/user/ExamTaking.vue'),
          meta: { title: '考试答题' },
        },
        {
          path: 'exam/:id/score',
          name: 'ExamScore',
          component: () => import('@/views/user/ExamScore.vue'),
          meta: { title: '考试成绩' },
        },
        {
          path: 'article',
          name: 'ArticleList',
          component: () => import('@/views/user/ArticleList.vue'),
          meta: { title: '知识文章' },
        },
        {
          path: 'article/:id',
          name: 'ArticleDetail',
          component: () => import('@/views/user/ArticleDetail.vue'),
          meta: { title: '文章详情' },
        },
        {
          path: 'path',
          name: 'LearningPath',
          component: () => import('@/views/user/LearningPath.vue'),
          meta: { title: '学习路线' },
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/user/Profile.vue'),
          meta: { title: '个人信息' },
        },
      ],
    },
    // 管理员端
    {
      path: '/admin',
      component: () => import('@/views/admin/Layout.vue'),
      redirect: '/admin/dashboard',
      meta: { requireAdmin: true },
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/admin/Dashboard.vue'),
          meta: { title: '管理后台' },
        },
        {
          path: 'question',
          name: 'QuestionManage',
          component: () => import('@/views/admin/QuestionManage.vue'),
          meta: { title: '题目管理' },
        },
        {
          path: 'category',
          name: 'CategoryManage',
          component: () => import('@/views/admin/CategoryManage.vue'),
          meta: { title: '分类管理' },
        },
        {
          path: 'tag',
          name: 'TagManage',
          component: () => import('@/views/admin/TagManage.vue'),
          meta: { title: '标签管理' },
        },
        {
          path: 'section',
          name: 'SectionManage',
          component: () => import('@/views/admin/SectionManage.vue'),
          meta: { title: '板块管理' },
        },
        {
          path: 'exam',
          name: 'ExamManage',
          component: () => import('@/views/admin/ExamManage.vue'),
          meta: { title: '考试管理' },
        },
        {
          path: 'exam/:id/questions',
          name: 'ExamQuestionEdit',
          component: () => import('@/views/admin/ExamQuestionEdit.vue'),
          meta: { title: '考试题目编排' },
        },
        {
          path: 'article',
          name: 'ArticleManage',
          component: () => import('@/views/admin/ArticleManage.vue'),
          meta: { title: '文章管理' },
        },
        {
          path: 'knowledge-category',
          name: 'KnowledgeCategory',
          component: () => import('@/views/admin/KnowledgeCategory.vue'),
          meta: { title: '知识分类管理' },
        },
        {
          path: 'learning-path',
          name: 'LearningPathManage',
          component: () => import('@/views/admin/LearningPathManage.vue'),
          meta: { title: '学习路线管理' },
        },
        {
          path: 'user',
          name: 'UserManage',
          component: () => import('@/views/admin/UserManage.vue'),
          meta: { title: '用户管理' },
        },
      ],
    },
  ],
})

router.beforeEach((to, _from, next) => {
  document.title = (to.meta.title as string) || 'm-code 刷题系统'
  const userStore = useUserStore()

  if (to.meta.noAuth) {
    if (userStore.isLoggedIn) {
      next(userStore.isAdmin ? '/admin/dashboard' : '/user/home')
      return
    }
    next()
    return
  }

  if (!userStore.isLoggedIn) {
    next('/login')
    return
  }

  if (to.meta.requireAdmin && !userStore.isAdmin) {
    next('/user/home')
    return
  }

  next()
})

export default router
