<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">我的报修总数</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #e6a23c;">{{ stats.pending }}</div>
          <div class="stat-label">待处理</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #67c23a;">{{ stats.completed }}</div>
          <div class="stat-label">已完成</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>最近报修</span>
        <el-button link type="primary" @click="$router.push('/student/my-repairs')">
          查看全部
        </el-button>
      </template>

      <el-table :data="recentRepairs" v-loading="loading">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ formatStatus(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" />
      </el-table>

      <el-empty v-if="recentRepairs.length === 0 && !loading" description="暂无报修记录" />
    </el-card>

    <div class="quick-action">
      <el-button type="primary" size="large" @click="$router.push('/student/submit-repair')">
        <el-icon><Plus /></el-icon>
        提交新报修
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { repairService } from '../../services/repairService'

const loading = ref(false)
const stats = ref({ total: 0, pending: 0, completed: 0 })
const recentRepairs = ref([])

const getStatusType = (status) => {
  const map = {
    '待处理': 'warning',
    '处理中': 'primary',
    '已完成': 'success'
  }
  return map[status] || 'info'
}

const formatStatus = (status) => {
  return status || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const repairs = await repairService.getMyOrders()
    recentRepairs.value = repairs.slice(0, 5) // Show only 5 recent

    stats.value = {
      total: repairs.length,
      pending: repairs.filter(r => r.status === '待处理').length,
      completed: repairs.filter(r => r.status === '已完成').length
    }
  } catch (error) {
    console.error('Failed to load repairs:', error)
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.quick-action {
  margin-top: 20px;
  text-align: center;
}
</style>