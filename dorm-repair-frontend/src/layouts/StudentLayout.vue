<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <h2>宿舍报修系统</h2>
        <p>Student Portal</p>
      </div>

      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/student/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <el-menu-item index="/student/my-repairs">
          <el-icon><List /></el-icon>
          <span>我的报修</span>
        </el-menu-item>

        <el-menu-item index="/student/submit-repair">
          <el-icon><Plus /></el-icon>
          <span>提交报修</span>
        </el-menu-item>

        <el-menu-item index="/student/profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>

        <el-divider style="border-color: #1f2d3d; margin: 10px 0" />

        <el-menu-item @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h3>学生报修系统</h3>
        </div>
        <div class="header-right">
          <span class="username">{{ username }}</span>
          <el-tag type="success" effect="dark" size="small">学生</el-tag>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { authService } from '../services/authService'

const route = useRoute()
const router = useRouter()

const username = ref('')

const activeMenu = computed(() => route.path)

onMounted(() => {
  username.value = localStorage.getItem('username') || 'Student'
})

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await authService.logout()

    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')

    ElMessage.success('退出成功')
    router.push('/login')
  } catch (error) {
    // User cancelled
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  color: #fff;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #1f2d3d;
}

.logo h2 {
  margin: 0;
  color: #fff;
  font-size: 18px;
}

.logo p {
  margin: 5px 0 0;
  color: #909399;
  font-size: 12px;
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  color: #606266;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>