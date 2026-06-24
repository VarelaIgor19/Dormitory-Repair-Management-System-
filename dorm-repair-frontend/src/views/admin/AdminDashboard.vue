<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">本月报修总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #e6a23c;">{{ stats.pending }}</div>
          <div class="stat-label">待处理</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #409eff;">{{ stats.inProgress }}</div>
          <div class="stat-label">处理中</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #67c23a;">{{ stats.completed }}</div>
          <div class="stat-label">已完成</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>问题最多宿舍楼</span>
          </template>
          <div class="worst-dorm">
            <el-icon size="60" color="#f56c6c"><WarningFilled /></el-icon>
            <h2>{{ stats.worstDormitory || '暂无数据' }}</h2>
            <p>该宿舍楼本月报修最多，请关注</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/admin/statistics')">
              <el-icon><TrendCharts /></el-icon>
              查看详细报表
            </el-button>
            <el-button type="success" @click="$router.push('/admin/dormitories')">
              <el-icon><Plus /></el-icon>
              添加宿舍楼
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { statisticsService } from '../../services/statisticsService'

const stats = ref({
  total: 0,
  pending: 0,
  inProgress: 0,
  completed: 0,
  worstDormitory: ''
})

onMounted(async () => {
  try {
    const data = await statisticsService.getMonthlyStats()
    stats.value = {
      total: data['订单总数'] || 0,
      pending: data['待处理'] || 0,
      inProgress: data['处理中'] || 0,
      completed: data['已完成'] || 0,
      worstDormitory: data['问题最多宿舍楼'] || '暂无数据'
    }
  } catch (error) {
    console.error('Failed to load statistics:', error)
  }
})
</script>

<style scoped>
.worst-dorm {
  text-align: center;
  padding: 20px;
}

.worst-dorm h2 {
  margin: 15px 0 10px;
  color: #f56c6c;
}

.worst-dorm p {
  color: #909399;
}

.quick-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  padding: 20px;
}
</style>