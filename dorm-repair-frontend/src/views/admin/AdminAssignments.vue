<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>人员分配管理</span>
          <el-tag type="danger" effect="dark" size="small">管理员</el-tag>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card">

        <!-- Staff List Tab -->
        <el-tab-pane label="维修人员列表" name="staff">
          <el-table :data="staffList" v-loading="loading" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" min-width="150" />
            <el-table-column label="角色" width="120">
              <template #default="{ row }">
                <el-tag type="warning">维修人员</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="当前分配任务数" width="140" align="center">
              <template #default="{ row }">
                <el-tag :type="getStaffAssignmentCount(row.id) > 0 ? 'success' : 'info'">
                  {{ getStaffAssignmentCount(row.id) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStaffAssignmentCount(row.id) > 3 ? 'danger' : 'success'">
                  {{ getStaffAssignmentCount(row.id) > 3 ? '繁忙' : '空闲' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- Student List Tab -->
        <el-tab-pane label="学生报修列表" name="students">
          <el-table :data="studentList" v-loading="loading" border>
            <el-table-column prop="id" label="学生ID" width="100" />
            <el-table-column prop="username" label="学生用户名" min-width="150" />
            <el-table-column label="报修数量" width="100" align="center">
              <template #default="{ row }">
                <el-tag type="primary">{{ row.repairOrders?.length || 0 }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="待处理" width="100" align="center">
              <template #default="{ row }">
                <el-tag type="warning">{{ getPendingCount(row.repairOrders) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button
                  link
                  type="primary"
                  @click="viewStudentRepairs(row)"
                >
                  查看报修详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- Assignment Management Tab -->
        <el-tab-pane label="分配管理" name="assignments">
          <el-row :gutter="20">
            <!-- Pending Repairs -->
            <el-col :span="12">
              <el-card shadow="never">
                <template #header>
                  <div class="section-header">
                    <span>待分配报修</span>
                    <el-tag type="warning">{{ pendingRepairs.length }}</el-tag>
                  </div>
                </template>

                <el-table :data="pendingRepairs" v-loading="loading" size="small" max-height="500">
                  <el-table-column prop="id" label="报修ID" width="80" />
                  <el-table-column prop="title" label="标题" show-overflow-tooltip />
                  <el-table-column label="学生" width="120">
                    <template #default="{ row }">
                      {{ row.user?.username || '未知' }}
                    </template>
                  </el-table-column>
                  <el-table-column label="宿舍" width="150">
                    <template #default="{ row }">
                      <el-tag size="small">
                        {{ row.campus || row.dormitory?.campus }}-{{ row.building || row.dormitory?.building }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="100" fixed="right">
                    <template #default="{ row }">
                      <el-button
                        type="primary"
                        size="small"
                        @click="openAssignDialog(row)"
                      >
                        分配
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>

                <el-empty
                  v-if="pendingRepairs.length === 0 && !loading"
                  description="暂无待分配报修"
                />
              </el-card>
            </el-col>

            <!-- Current Assignments -->
            <el-col :span="12">
              <el-card shadow="never">
                <template #header>
                  <div class="section-header">
                    <span>当前分配</span>
                    <el-tag type="success">{{ assignments.length }}</el-tag>
                  </div>
                </template>

                <el-table :data="assignments" v-loading="loading" size="small" max-height="500">
                  <el-table-column prop="id" label="分配ID" width="80" />
                  <el-table-column label="维修人员" width="120">
                    <template #default="{ row }">
                      <el-tag type="warning" size="small">
                        {{ row.staff?.username }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="学生" width="120">
                    <template #default="{ row }">
                      {{ row.student?.username }}
                    </template>
                  </el-table-column>
                  <el-table-column label="报修标题" min-width="150" show-overflow-tooltip>
                    <template #default="{ row }">
                      {{ row.repairOrder?.title }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                      <el-tag :type="getAssignmentStatusType(row.status)" size="small">
                        {{ row.status }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="80" fixed="right">
                    <template #default="{ row }">
                      <el-button
                        link
                        type="danger"
                        size="small"
                        @click="removeAssignment(row)"
                      >
                        移除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>

                <el-empty
                  v-if="assignments.length === 0 && !loading"
                  description="暂无分配记录"
                />
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- Assignment Dialog -->
    <el-dialog
      v-model="assignDialogVisible"
      title="分配维修人员"
      width="500px"
      destroy-on-close
    >
      <el-form :model="assignForm" label-position="top" ref="assignFormRef">
        <el-form-item label="报修信息">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="报修ID">{{ selectedRepair?.id }}</el-descriptions-item>
            <el-descriptions-item label="标题">{{ selectedRepair?.title }}</el-descriptions-item>
            <el-descriptions-item label="学生">{{ selectedRepair?.user?.username }}</el-descriptions-item>
            <el-descriptions-item label="宿舍">
              {{ selectedRepair?.campus || selectedRepair?.dormitory?.campus }}-{{ selectedRepair?.building || selectedRepair?.dormitory?.building }}
            </el-descriptions-item>
          </el-descriptions>
        </el-form-item>

        <el-form-item
          label="选择维修人员"
          prop="staffId"
          :rules="[{ required: true, message: '请选择维修人员', trigger: 'change' }]"
        >
          <el-select
            v-model="assignForm.staffId"
            placeholder="选择维修人员"
            style="width: 100%"
          >
            <el-option
              v-for="staff in availableStaff"
              :key="staff.id"
              :label="`${staff.username} (当前任务: ${getStaffAssignmentCount(staff.id)})`"
              :value="staff.id"
            >
              <span style="float: left">{{ staff.username }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                当前任务: {{ getStaffAssignmentCount(staff.id) }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssignment" :loading="submitting">
          确认分配
        </el-button>
      </template>
    </el-dialog>

    <!-- Student Repairs Dialog -->
    <el-dialog
      v-model="studentDialogVisible"
      title="学生报修详情"
      width="700px"
      destroy-on-close
    >
      <div v-if="selectedStudent">
        <h4>{{ selectedStudent.username }} 的报修记录</h4>
        <el-divider />
        <el-table :data="selectedStudent.repairOrders" border>
          <el-table-column prop="id" label="报修ID" width="80" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="提交时间" width="180" />
          <el-table-column label="是否已分配" width="100">
            <template #default="{ row }">
              <el-tag :type="isOrderAssigned(row.id) ? 'success' : 'info'">
                {{ isOrderAssigned(row.id) ? '已分配' : '未分配' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { assignmentService } from '../../services/assignmentService'
import { repairService } from '../../services/repairService'

const activeTab = ref('staff')
const loading = ref(false)
const submitting = ref(false)
const staffList = ref([])
const studentList = ref([])
const assignments = ref([])
const allRepairs = ref([])
const assignDialogVisible = ref(false)
const studentDialogVisible = ref(false)
const selectedRepair = ref(null)
const selectedStudent = ref(null)
const assignFormRef = ref()

const assignForm = ref({
  staffId: null
})

// Computed: Staff with less than 5 assignments are considered available
const availableStaff = computed(() => {
  return staffList.value.filter(s => getStaffAssignmentCount(s.id) < 5)
})

const loadData = async () => {
  loading.value = true
  try {
    // Load all data in parallel
    const [staffRes, studentRes, assignRes, repairsRes] = await Promise.all([
      assignmentService.getAllStaff(),
      assignmentService.getAllStudentsWithRepairs(),
      assignmentService.getAllAssignments(),
      repairService.getAllOrders()
    ])

    staffList.value = staffRes
    studentList.value = studentRes
    assignments.value = assignRes
    allRepairs.value = repairsRes

  } catch (error) {
    console.error('Failed to load data:', error)
    ElMessage.error('加载数据失败: ' + (error.response?.data || error.message))
  } finally {
    loading.value = false
  }
}

// Get count of assignments for a staff member
const getStaffAssignmentCount = (staffId) => {
  return assignments.value.filter(a => a.staff?.id === staffId).length
}

// Get pending repairs (not assigned and status is "待处理")
const pendingRepairs = computed(() => {
  const assignedOrderIds = assignments.value.map(a => a.repairOrder?.id)
  return allRepairs.value.filter(r =>
    r.status === '待处理' && !assignedOrderIds.includes(r.id)
  )
})

// Get pending count for a student
const getPendingCount = (orders) => {
  if (!orders) return 0
  return orders.filter(o => o.status === '待处理').length
}

// Check if order is already assigned
const isOrderAssigned = (orderId) => {
  return assignments.value.some(a => a.repairOrder?.id === orderId)
}

// Get status type for tags
const getStatusType = (status) => {
  const map = {
    '待处理': 'warning',
    '处理中': 'primary',
    '已完成': 'success'
  }
  return map[status] || 'info'
}

const getAssignmentStatusType = (status) => {
  const map = {
    'ASSIGNED': 'primary',
    'IN_PROGRESS': 'warning',
    'COMPLETED': 'success'
  }
  return map[status] || 'info'
}

const openAssignDialog = (repair) => {
  selectedRepair.value = repair
  assignForm.value.staffId = null
  assignDialogVisible.value = true
}

const viewStudentRepairs = (student) => {
  selectedStudent.value = student
  studentDialogVisible.value = true
}

const submitAssignment = async () => {
  const valid = await assignFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await assignmentService.createAssignment(
      selectedRepair.value.id,
      assignForm.value.staffId
    )
    ElMessage.success('分配成功！维修人员已收到任务')
    assignDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('Assignment failed:', error)
    ElMessage.error('分配失败：' + (error.response?.data?.message || error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

const removeAssignment = async (assignment) => {
  try {
    await ElMessageBox.confirm(
      `确定要移除 "${assignment.staff?.username}" 对 "${assignment.student?.username}" 的维修分配吗？`,
      '确认移除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await assignmentService.deleteAssignment(assignment.id)
    ElMessage.success('移除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Remove failed:', error)
      ElMessage.error('移除失败')
    }
  }
}

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

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-tabs__content) {
  padding: 20px 0;
}
</style>