<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>

      <!-- Avatar Upload Section -->
      <div class="avatar-section">
        <div class="avatar-wrapper" @click="triggerUpload">
          <img
            v-if="user.avatar"
            :src="user.avatar"
            class="avatar-img"
            alt="头像"
          />
          <div v-else class="avatar-placeholder">
            <el-icon :size="50"><UserFilled /></el-icon>
          </div>
          <div class="avatar-overlay">
            <el-icon :size="20"><Camera /></el-icon>
            <span>点击上传</span>
          </div>
        </div>
        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleFileChange"
        />
        <p class="avatar-hint">支持 JPG、PNG 格式，最大 2MB</p>

        <!-- Delete Button - Only show when avatar exists -->
        <el-button
          v-if="user.avatar"
          type="danger"
          size="small"
          plain
          class="delete-btn"
          @click="deleteAvatar"
        >
          <el-icon><Delete /></el-icon>
          删除头像
        </el-button>
      </div>

      <el-descriptions :column="1" border v-loading="loading">
        <el-descriptions-item label="用户ID">{{ user.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag type="warning" effect="dark">维修人员</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px;">
        <el-button type="primary" @click="refreshData">刷新</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { authService } from '../../services/authService'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const fileInput = ref(null)
const user = ref({
  id: '',
  username: '',
  role: '',
  avatar: ''
})

// Compute unique storage key based on user ID
const avatarStorageKey = computed(() => {
  return user.value.id ? `avatar_${user.value.id}` : ''
})

const loadUserInfo = async () => {
  loading.value = true
  try {
    const data = await authService.getMe()
    user.value.id = data.id
    user.value.username = data.username
    user.value.role = data.role

    // Load avatar for this specific user
    const savedAvatar = localStorage.getItem(`avatar_${data.id}`)
    user.value.avatar = savedAvatar || ''

    localStorage.setItem('userId', data.id)
    localStorage.setItem('username', data.username)
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  loadUserInfo()
}

const triggerUpload = () => {
  fileInput.value.click()
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    ElMessage.error('请上传图片文件')
    event.target.value = ''
    return
  }

  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    event.target.value = ''
    return
  }

  const reader = new FileReader()
  reader.onload = (e) => {
    user.value.avatar = e.target.result
    // Save with user-specific key
    if (user.value.id) {
      localStorage.setItem(avatarStorageKey.value, e.target.result)
    }
    ElMessage.success('头像上传成功')
  }
  reader.readAsDataURL(file)
  event.target.value = ''
}

const deleteAvatar = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要删除当前头像吗？',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    user.value.avatar = ''
    // Remove user-specific avatar
    if (avatarStorageKey.value) {
      localStorage.removeItem(avatarStorageKey.value)
    }
    ElMessage.success('头像已删除')
  } catch (error) {
    // User cancelled
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.avatar-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid #dcdfe6;
  transition: all 0.3s;
}

.avatar-wrapper:hover {
  border-color: #e6a23c;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #e4e7ed;
  color: #909399;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.3s;
  gap: 4px;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-hint {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
}

.delete-btn {
  margin-top: 10px;
}
</style>