<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的报修记录</span>
          <el-button type="primary" @click="$router.push('/student/submit-repair')">
            <el-icon><Plus /></el-icon>
            提交新报修
          </el-button>
        </div>
      </template>

      <el-table :data="repairs" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip min-width="200" />

        <el-table-column label="宿舍" min-width="180">
          <template #default="{ row }">
            <el-tag v-if="row.campus || row.building || row.roomNumber || row.dormitory">
              {{ row.campus || row.dormitory?.campus }}-{{ row.building || row.dormitory?.building }}-{{ row.roomNumber || extractRoomNumber(row.description) }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <!-- NEW: Show assigned staff -->
        <el-table-column label="处理人员" min-width="120">
          <template #default="{ row }">
            <el-tag v-if="getAssignedStaff(row.id)" type="success" effect="dark">
              <el-icon><User /></el-icon>
              {{ getAssignedStaff(row.id)?.username }}
            </el-tag>
            <el-tag v-else type="info">待分配</el-tag>
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

        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="canEdit(row)"
              link
              type="primary"
              @click="openEditDialog(row)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="repairs.length === 0 && !loading" description="暂无报修记录" />
    </el-card>

    <!-- Edit Dialog -->
    <el-dialog v-model="editDialogVisible" title="编辑报修" width="500px" destroy-on-close>
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-position="top">
        <el-form-item label="标题" prop="title">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="宿舍(校区-楼栋)" prop="dormitoryId">
          <el-select
            v-model="editForm.dormitoryId"
            placeholder="选择校区和楼栋"
            style="width: 100%"
            @change="handleDormitoryChange"
          >
            <el-option
              v-for="dorm in dormitories"
              :key="dorm.id"
              :label="`${dorm.campus}-${dorm.building}`"
              :value="dorm.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="editForm.roomNumber" placeholder="请输入房间号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { repairService } from '../../services/repairService'
import { dormitoryService } from '../../services/dormitoryService'
import { assignmentService } from '../../services/assignmentService'  // NEW
import { ElMessage } from 'element-plus'

const loading = ref(false)
const submitting = ref(false)
const repairs = ref([])
const dormitories = ref([])
const myAssignments = ref([])  // NEW: Store assignments
const editDialogVisible = ref(false)
const editFormRef = ref()

const editForm = ref({
  id: null,
  title: '',
  description: '',
  dormitoryId: null,
  roomNumber: ''
})

const editRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
  dormitoryId: [{ required: true, message: '请选择宿舍', trigger: 'change' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }]
}

const getStatusType = (status) => {
  const map = {
    '待处理': 'warning',
    '处理中': 'primary',
    '已完成': 'success'
  }
  return map[status] || 'info'
}

const canEdit = (row) => {
  return row.status === '待处理'
}

const extractRoomNumber = (description) => {
  if (!description) return ''
  const match = description.match(/【[^-]+-[^-]+-([^】]+)】/)
  return match ? match[1] : ''
}

const extractCleanDescription = (description) => {
  if (!description) return ''
  return description.replace(/【[^】]+】/, '').trim()
}

// NEW: Get assigned staff for a repair order
const getAssignedStaff = (repairOrderId) => {
  const assignment = myAssignments.value.find(a => a.repairOrder?.id === repairOrderId)
  return assignment?.staff
}

const loadRepairs = async () => {
  loading.value = true
  try {
    // Load both repairs and assignments in parallel
    const [repairsData, assignmentsData] = await Promise.all([
      repairService.getMyOrders(),
      assignmentService.getMyAssignment()
    ])

    repairs.value = repairsData
    myAssignments.value = assignmentsData

  } catch (error) {
    console.error('Failed to load repairs:', error)
    ElMessage.error('加载报修记录失败')
  } finally {
    loading.value = false
  }
}

const loadDormitories = async () => {
  try {
    const data = await dormitoryService.getAllDormitories()
    dormitories.value = data
  } catch (error) {
    console.error('Failed to load dormitories:', error)
  }
}

const handleDormitoryChange = () => {
  // Optional: reset room number when dormitory changes
}

const openEditDialog = (row) => {
  editForm.value = {
    id: row.id,
    title: row.title,
    description: extractCleanDescription(row.description),
    dormitoryId: row.dormitory?.id,
    roomNumber: row.roomNumber || extractRoomNumber(row.description) || ''
  }
  editDialogVisible.value = true
}

const submitEdit = async () => {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const selectedDorm = dormitories.value.find(d => d.id === editForm.value.dormitoryId)

    await repairService.editRepair(editForm.value.id, {
      title: editForm.value.title,
      description: editForm.value.description,
      campus: selectedDorm?.campus,
      building: selectedDorm?.building,
      roomNumber: editForm.value.roomNumber,
      dormitory: {
        id: editForm.value.dormitoryId,
        campus: selectedDorm?.campus,
        building: selectedDorm?.building
      }
    })
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    loadRepairs()
  } catch (error) {
    console.error('Edit failed:', error)
    ElMessage.error('修改失败：' + (error.response?.data || error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadRepairs()
  loadDormitories()
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
</style>