<<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>报修处理</span>
          <div>
            <el-tag v-if="myAssignments.length > 0" type="success" style="margin-right: 10px;">
              您有 {{ myAssignments.length }} 个分配任务
            </el-tag>
            <el-radio-group v-model="filterStatus" @change="handleFilterChange" size="small">
              <el-radio-button label="">全部</el-radio-button>
              <el-radio-button label="待处理">待处理</el-radio-button>
              <el-radio-button label="处理中">处理中</el-radio-button>
              <el-radio-button label="已完成">已完成</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <!-- Alert if no assignments -->
      <el-alert
        v-if="myAssignments.length === 0 && !loading"
        title="暂无分配任务"
        description="管理员尚未为您分配维修任务。请等待管理员分配后再查看。"
        type="info"
        show-icon
        :closable="false"
        style="margin-bottom: 20px;"
      />

      <!-- Alert showing assigned repairs -->
      <el-alert
        v-if="myAssignments.length > 0"
        :title="`您当前有 ${myAssignments.length} 个维修任务待处理`"
        type="success"
        show-icon
        :closable="false"
        style="margin-bottom: 20px;"
      />

      <!-- Only show repairs that are assigned to this staff -->
      <el-table :data="assignedRepairs" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip min-width="200" />

        <el-table-column label="宿舍" min-width="180">
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

        <el-table-column label="分配状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="success">已分配</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="提交时间" width="180" />

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-select
              v-if="row.status !== '已完成'"
              v-model="row.newStatus"
              placeholder="更新状态"
              size="small"
              style="width: 120px;"
              @change="(val) => updateStatus(row, val)"
            >
              <el-option
                v-for="status in getAvailableStatuses(row.status)"
                :key="status"
                :label="status"
                :value="status"
              />
            </el-select>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="assignedRepairs.length === 0 && !loading && myAssignments.length > 0"
                description="当前筛选条件下无报修记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { repairService } from '../../services/repairService'
import { assignmentService } from '../../services/assignmentService'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const myAssignments = ref([])
const allRepairs = ref([])
const filterStatus = ref('')

// Only show repairs that are assigned to this staff
const assignedRepairs = computed(() => {
  const assignedOrderIds = myAssignments.value.map(a => a.repairOrder?.id)
  let repairs = allRepairs.value.filter(r => assignedOrderIds.includes(r.id))

  // Apply status filter if selected
  if (filterStatus.value) {
    repairs = repairs.filter(r => r.status === filterStatus.value)
  }

  return repairs.map(r => ({
    ...r,
    newStatus: '',
    dormitory: r.dormitory || {},
    user: r.user || {}
  }))
})

const getStatusType = (status) => {
  const map = {
    '待处理': 'warning',
    '处理中': 'primary',
    '已完成': 'success'
  }
  return map[status] || 'info'
}

const getAvailableStatuses = (currentStatus) => {
  if (currentStatus === '待处理') {
    return ['处理中', '已完成']
  } else if (currentStatus === '处理中') {
    return ['已完成']
  }
  return []
}

const extractRoomNumber = (description) => {
  if (!description) return ''
  const match = description.match(/【[^-]+-[^-]+-([^】]+)】/)
  return match ? match[1] : ''
}

const loadData = async () => {
  loading.value = true
  try {
    // First load my assignments
    myAssignments.value = await assignmentService.getStaffAssignments()

    // Then load all repairs (we'll filter on frontend)
    allRepairs.value = await repairService.getAllOrders()

  } catch (error) {
    console.error('Failed to load data:', error)
    // Only show error if it's not a permission issue (403)
    if (error.response?.status !== 403) {
      ElMessage.error('加载数据失败')
    }
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  // Filter is handled by computed property
}

const updateStatus = async (row, newStatus) => {
  try {
    await repairService.updateStatus(row.id, newStatus)
    ElMessage.success('状态更新成功')

    // Update local state
    row.status = newStatus

    // Also update the assignment status if needed
    const assignment = myAssignments.value.find(a => a.repairOrder?.id === row.id)
    if (assignment) {
      if (newStatus === '已完成') {
        assignment.status = 'COMPLETED'
      } else if (newStatus === '处理中') {
        assignment.status = 'IN_PROGRESS'
      }
      // Update the repairOrder status in assignment too
      if (assignment.repairOrder) {
        assignment.repairOrder.status = newStatus
      }
    }

    // Also update in allRepairs array
    const repairIndex = allRepairs.value.findIndex(r => r.id === row.id)
    if (repairIndex !== -1) {
      allRepairs.value[repairIndex].status = newStatus
    }

  } catch (error) {
    console.error('Update status failed:', error)
    if (error.response?.status !== 403) {
      ElMessage.error('状态更新失败')
    }
  }
}

onMounted(loadData)
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
</style>