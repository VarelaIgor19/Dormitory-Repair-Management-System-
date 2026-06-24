<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>宿舍管理</span>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon>
            添加宿舍
          </el-button>
        </div>
      </template>

      <el-table :data="dormitories" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="campus" label="校区" min-width="120" />
        <el-table-column prop="building" label="楼栋" min-width="120" />
        <el-table-column label="完整名称" min-width="180">
          <template #default="{ row }">
            <el-tag>{{ row.campus }}-{{ row.building }}-{{ row.roomNumber }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="openEditDialog(row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="dormitories.length === 0 && !loading" description="暂无宿舍数据" />
    </el-card>

    <!-- Add Dialog - REMOVED 房间号 field, only campus and building -->
    <el-dialog v-model="addDialogVisible" title="添加宿舍" width="450px" destroy-on-close>
      <el-form :model="addForm" :rules="rules" ref="addFormRef" label-position="top">
        <el-form-item label="校区" prop="campus">
          <el-select v-model="addForm.campus" placeholder="请选择校区" style="width: 100%">
            <el-option label="南湖校区" value="南湖校区" />
            <el-option label="鉴湖校区" value="鉴湖校区" />
            <el-option label="余家头校区" value="余家头校区" />
            <el-option label="马房山校区" value="马房山校区" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋" prop="building">
          <el-input v-model="addForm.building" placeholder="如：A栋, B栋, 1号楼" />
        </el-form-item>
        <!-- 房间号 REMOVED from Add Dialog -->
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- Edit Dialog - REMOVED 房间号 field, only campus and building -->
    <el-dialog v-model="editDialogVisible" title="编辑宿舍" width="450px" destroy-on-close>
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-position="top">
        <el-form-item label="校区" prop="campus">
          <el-select v-model="editForm.campus" placeholder="请选择校区" style="width: 100%">
            <el-option label="南湖校区" value="南湖校区" />
            <el-option label="鉴湖校区" value="鉴湖校区" />
            <el-option label="余家头校区" value="余家头校区" />
            <el-option label="马房山校区" value="马房山校区" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋" prop="building">
          <el-input v-model="editForm.building" placeholder="如：A栋, B栋, 1号楼" />
        </el-form-item>
        <!-- 房间号 REMOVED from Edit Dialog -->
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
import { dormitoryService } from '../../services/dormitoryService'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const submitting = ref(false)
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const dormitories = ref([])
const addFormRef = ref()
const editFormRef = ref()

// Form data - only campus and building (no roomNumber)
const formData = ref({
  campus: '',
  building: ''
})

// Add form - without roomNumber
const addForm = ref({
  campus: '',
  building: ''
})

// Edit form - without roomNumber
const editForm = ref({
  id: null,
  campus: '',
  building: ''
})

// Validation rules - no roomNumber
const rules = {
  campus: [{ required: true, message: '请选择校区', trigger: 'change' }],
  building: [{ required: true, message: '请输入楼栋', trigger: 'blur' }]
}

const loadDormitories = async () => {
  loading.value = true
  try {
    const data = await dormitoryService.getAllDormitories()
    dormitories.value = data
    console.log('Loaded dormitories:', data)
  } catch (error) {
    console.error('Failed to load dormitories:', error)
    ElMessage.error('加载宿舍列表失败：' + (error.response?.data || error.message))
  } finally {
    loading.value = false
  }
}

// Open Add Dialog - only campus and building
const openAddDialog = () => {
  addForm.value = { campus: '', building: '' }
  addDialogVisible.value = true
}

// Submit Add - send campus, building, and auto-generated roomNumber
const submitAdd = async () => {
  const valid = await addFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // Auto-generate roomNumber based on building (e.g., building + "001")
    const generatedRoomNumber = generateRoomNumber(addForm.value.building)

    await dormitoryService.createDormitory({
      campus: addForm.value.campus,
      building: addForm.value.building,
      roomNumber: generatedRoomNumber // Auto-generated
    })

    ElMessage.success('添加成功')
    addDialogVisible.value = false
    loadDormitories()
  } catch (error) {
    console.error('Add failed:', error)
    ElMessage.error('添加失败：' + (error.response?.data || error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

// Helper function to generate roomNumber
const generateRoomNumber = (building) => {
  // Generate a simple room number based on building
  // You can customize this logic as needed
  const timestamp = Date.now().toString().slice(-3)
  return `${building}-${timestamp}`
}

// Open Edit Dialog - only campus and building
const openEditDialog = (row) => {
  editForm.value = {
    id: row.id,
    campus: row.campus || '南湖校区',
    building: row.building
    // roomNumber intentionally omitted - not editable
  }
  editDialogVisible.value = true
}

// Submit Edit - only send campus and building, preserve existing roomNumber
const submitEdit = async () => {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // Find the original dormitory to get its roomNumber
    const originalDorm = dormitories.value.find(d => d.id === editForm.value.id)

    // Send update with campus and building only, keep original roomNumber
    await dormitoryService.updateDormitory(editForm.value.id, {
      campus: editForm.value.campus,
      building: editForm.value.building,
      roomNumber: originalDorm?.roomNumber // Preserve original roomNumber
    })

    ElMessage.success('修改成功')
    editDialogVisible.value = false
    loadDormitories()
  } catch (error) {
    console.error('Update failed:', error)
    ElMessage.error('修改失败：' + (error.response?.data || error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

// Handle Delete
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除宿舍 "${row.campus}-${row.building}-${row.roomNumber}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    submitting.value = true
    try {
      await dormitoryService.deleteDormitory(row.id)
      ElMessage.success('删除成功')
      loadDormitories()
    } catch (error) {
      console.error('Delete failed:', error)
      ElMessage.error('删除失败：' + (error.response?.data || error.message || '未知错误'))
    } finally {
      submitting.value = false
    }
  } catch (error) {
    // User cancelled
    if (error !== 'cancel') {
      console.log('Delete cancelled')
    }
  }
}

onMounted(() => {
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