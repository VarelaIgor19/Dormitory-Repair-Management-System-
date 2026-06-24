<template>
  <div class="login-container">
    <!-- Left side with dormitory background -->
    <div class="login-left">
      <div class="left-content">
        <!-- University Logo and Name -->
        <div class="university-header">
          <div class="logo-container">
            <div class="university-logo">
              <img src="../assets/whut-logo.png" alt="Wuhan University of Technology" class="logo-img" />
            </div>
            <div class="university-name">
              <h1 class="chinese-name">武汉理工大学</h1>
              <p class="english-name">Wuhan University of Technology</p>
            </div>
          </div>
        </div>

        <h2 class="system-title">Dormitory Repair System</h2>
      </div>
    </div>

    <!-- Right side with login form -->
    <div class="login-right">
      <div class="login-box">
        <h2>系统登录</h2>
        <p class="subtitle">请登录您的账户</p>

        <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item label="登录角色" prop="role">
            <el-radio-group v-model="form.role">
              <el-radio label="STUDENT">学生</el-radio>
              <el-radio label="REPAIR_STAFF">维修人员</el-radio>
              <el-radio label="ADMIN">管理员</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form>

        <div class="register-link">
          <span>还没有账号？</span>
          <el-button link type="primary" @click="goToRegister">立即注册</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { authService } from '../services/authService'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  role: 'STUDENT'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await authService.login(form)

    localStorage.setItem('token', res.token)
    localStorage.setItem('role', res.role)
    localStorage.setItem('username', form.username)

    ElMessage.success('登录成功！')

    if (res.role === 'ADMIN') {
      router.push('/admin/dashboard')
    } else if (res.role === 'REPAIR_STAFF') {
      router.push('/staff/dashboard')
    } else {
      router.push('/student/dashboard')
    }
  } catch (error) {
    // Error handled by interceptor
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}

.login-left {
  flex: 1.2;
  background-image: url('../assets/dormitory-bg.jpg');
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 80px;
}

.login-left::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
}

.left-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: white;
}

.university-header {
  margin-bottom: 30px;
}

.logo-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.university-logo {
  width: 120px;
  height: 120px;
  background: white;
  border-radius: 50%;
  padding: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.logo-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 50%;
}

.university-name {
  text-align: center;
}

.chinese-name {
  font-size: 36px;
  font-weight: bold;
  margin: 0 0 10px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  letter-spacing: 6px;
}

.english-name {
  font-size: 16px;
  margin: 0;
  opacity: 0.95;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
  letter-spacing: 1px;
  font-weight: 500;
}

.system-title {
  font-size: 32px;
  font-weight: 600;
  margin-top: 50px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  letter-spacing: 1px;
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-box h2 {
  margin: 0 0 10px;
  text-align: center;
  font-size: 24px;
  color: #303133;
}

.subtitle {
  color: #909399;
  text-align: center;
  margin-bottom: 30px;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.login-form :deep(.el-form-item__label) {
  color: #606266;
  font-weight: 500;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
  background-color: #409eff;
  border-color: #409eff;
}

.login-btn:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.register-link {
  margin-top: 20px;
  text-align: center;
  color: #606266;
  font-size: 14px;
}

/* Radio button styling */
:deep(.el-radio__input.is-checked .el-radio__inner) {
  background-color: #409eff;
  border-color: #409eff;
}

:deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #409eff;
}

/* Responsive adjustments */
@media (max-width: 968px) {
  .login-left {
    display: none;
  }

  .login-right {
    flex: 1;
  }
}
</style>