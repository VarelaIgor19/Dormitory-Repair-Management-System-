<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 每日报表 Tab -->
      <el-tab-pane label="每日报表" name="daily">
        <el-table :data="dailyReport" v-loading="loading">
          <el-table-column prop="date" label="日期" />
          <el-table-column prop="total" label="总数" />
          <el-table-column prop="pending" label="待处理" />
          <el-table-column prop="inProgress" label="处理中" />
          <el-table-column prop="completed" label="已完成" />
        </el-table>
      </el-tab-pane>

      <!-- UPDATED: 宿舍楼统计 Tab with 校区 column -->
      <el-tab-pane label="宿舍楼统计" name="dormitory">
        <el-table :data="dormitoryStats" v-loading="loading">
          <el-table-column prop="campus" label="校区" min-width="120" />  <!-- ADDED -->
          <el-table-column prop="building" label="楼栋" min-width="120" />
          <el-table-column prop="pending" label="待处理" />
          <el-table-column prop="inProgress" label="处理中" />
          <el-table-column prop="completed" label="已完成" />
          <el-table-column prop="total" label="总计" />
        </el-table>
      </el-tab-pane>

      <!-- 状态概览 Tab -->
      <el-tab-pane label="状态概览" name="status">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card>
              <div class="status-stat">
                <div class="number">{{ statusReport.pending }}</div>
                <div class="label">待处理</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <div class="status-stat">
                <div class="number">{{ statusReport.inProgress }}</div>
                <div class="label">处理中</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <div class="status-stat">
                <div class="number">{{ statusReport.completed }}</div>
                <div class="label">已完成</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { statisticsService } from '../../services/statisticsService'

const activeTab = ref('daily')
const loading = ref(false)
const dailyReport = ref([])
const dormitoryStats = ref([])
const statusReport = ref({ pending: 0, inProgress: 0, completed: 0 })

const loadData = async () => {
  loading.value = true
  try {
    const [daily, dormitory, status] = await Promise.all([
      statisticsService.getDailyReport(),
      statisticsService.getDormitoryStats(),
      statisticsService.getStatusReport()
    ])
    dailyReport.value = daily
    dormitoryStats.value = dormitory
    statusReport.value = status
  } catch (error) {
    console.error('Failed to load statistics:', error)
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.status-stat {
  text-align: center;
  padding: 20px;
}

.status-stat .number {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
}

.status-stat .label {
  margin-top: 10px;
  color: #909399;
}
</style>