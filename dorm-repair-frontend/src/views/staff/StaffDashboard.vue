<<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #e6a23c;">{{ stats.pending }}</div>
          <div class="stat-label">待处理报修</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #409eff;">{{ stats.inProgress }}</div>
          <div class="stat-label">处理中</div>
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
        <div class="card-header">
          <span>待处理报修</span>
          <el-button link type="primary" @click="$router.push('/staff/repairs')">
            查看全部
          </el-button>
        </div>
      </template>

      <el-table :data="pendingRepairs" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column label="宿舍" min-width="200">
          <template #default="{ row }">
            <el-tag v-if="row.campus || row.building || row.roomNumber || row.dormitory">
              {{ row.campus || row.dormitory?.campus || '未知校区' }}-{{ row.building || row.dormitory?.building || '未知楼栋' }}-{{ row.roomNumber || extractRoomNumber(row.description) || '未知房间' }}
            </el-tag>
            <span v-else>未分配</span>
          </template>
        </el-table-column>
        <el-table-column label="提交人" min-width="120">
          <template #default="{ row }">
            <span v-if="row.user && row.user.username">
              {{ row.user.username }}
            </span>
            <span v-else-if="row.username">
              {{ row.username }}
            </span>
            <span v-else>未知用户</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleProcess(row)">
              处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="pendingRepairs.length === 0 && !loading" description="暂无待处理报修" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, onBeforeRouteUpdate } from 'vue-router'
import { assignmentService } from '../../services/assignmentService'

const router = useRouter()
const loading = ref(false)
const stats = ref({ pending: 0, inProgress: 0, completed: 0 })
const pendingRepairs = ref([])
const myAssignments = ref([])

const extractRoomNumber = (description) => {
  if (!description) return ''
  const match = description.match(/【[^-]+-[^-]+-([^】]+)】/)
  return match ? match[1] : ''
}

// Calculate stats from staff's own assignments
const calculateStats = (assignments) => {
  const pending = assignments.filter(a =>
    a.repairOrder?.status === '待处理' || a.status === 'ASSIGNED'
  ).length

  const inProgress = assignments.filter(a =>
    a.repairOrder?.status === '处理中' || a.status === 'IN_PROGRESS'
  ).length

  const completed = assignments.filter(a =>
    a.repairOrder?.status === '已完成' || a.status === 'COMPLETED'
  ).length

  return { pending, inProgress, completed }
}

const loadData = async () => {
  loading.value = true
  try {
    // Load staff assignments (staff-only endpoint, no permission issues)
    myAssignments.value = await assignmentService.getStaffAssignments()

    // Calculate stats from assignments
    stats.value = calculateStats(myAssignments.value)

    // Get pending repairs from assignments (status '待处理')
    const pendingAssignments = myAssignments.value.filter(a =>
      a.repairOrder?.status === '待处理'
    )

    pendingRepairs.value = pendingAssignments
      .slice(0, 5)
      .map(a => ({
        ...a.repairOrder,
        dormitory: a.repairOrder?.dormitory || {},
        user: a.repairOrder?.user || {}
      }))

  } catch (error) {
    console.error('Failed to load data:', error)
    // Don't show error message - just log to console to avoid red "permission denied"
  } finally {
    loading.value = false
  }
}

const handleProcess = (row) => {
  router.push('/staff/repairs')
}

// Reload data when coming back from other pages (e.g., after updating status)
onBeforeRouteUpdate((to, from, next) => {
  if (to.path === '/staff/dashboard') {
    loadData()
  }
  next()
})

onMounted(() => {
  loadData()
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

.stat-card {
  text-align: center;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  margin-top: 10px;
  color: #909399;
  font-size: 14px;
}
</style>