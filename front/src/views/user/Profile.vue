<template>
  <div class="page-container">
    <div class="page-header"><h2>个人信息</h2></div>
    <el-card style="max-width:600px">
      <el-form :model="form" label-width="80px" :disabled="saving">
        <el-form-item label="用户名">
          <el-input v-model="userStore.userInfo!.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="积分">
          <el-tag>{{ userStore.userInfo?.score ?? 0 }}</el-tag>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateProfile } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const saving = ref(false)

const form = reactive({
  nickname: '',
  email: '',
  phone: '',
})

onMounted(() => {
  const u = userStore.userInfo
  if (u) {
    form.nickname = u.nickname || ''
    form.email = u.email || ''
    form.phone = u.phone || ''
  }
})

async function handleSave() {
  saving.value = true
  try {
    await updateProfile({ ...form })
    userStore.setUserInfo({ ...userStore.userInfo!, ...form })
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}
</script>
