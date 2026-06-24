<<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>维修人员管理</span>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon>
            添加维修人员
          </el-button>
        </div>
      </template>

      <el-table :data="staffList" v-loading="loading" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="150" />
        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <el-tag type="warning">维修人员</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="当前任务数" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getAssignmentCount(row.id) > 0 ? 'success' : 'info'">
              {{ getAssignmentCount(row.id) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getAssignmentCount(row.id) > 3 ? 'danger' : 'success'">
              {{ getAssignmentCount(row.id) > 3 ? '繁忙' : '空闲' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
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

      <el-empty v-if="staffList.length === 0 && !loading" description="暂无维修人员数据" />
    </el-card>

    <!-- Add Staff Dialog -->
    <el-dialog
      v-model="addDialogVisible"
      title="添加维修人员"
      width="450px"
      destroy-on-close
    >
      <el-form
        :model="addForm"
        :rules="formRules"
        ref="addFormRef"
        label-position="top"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="addForm.username"
            placeholder="请输入用户名"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="addForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="addForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- Edit Staff Dialog -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑维修人员"
      width="450px"
      destroy-on-close
    >
      <el-form
        :model="editForm"
        :rules="editRules"
        ref="editFormRef"
        label-position="top"
      >
        <el-form-item label="用户ID">
          <el-input v-model="editForm.id" disabled />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="editForm.username"
            placeholder="请输入用户名"
          />
        </el-form-item>
        <el-form-item label="新密码（留空则不修改）" prop="password">
          <el-input
            v-model="editForm.password"
            type="password"
            placeholder="如需修改密码请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item
          v-if="editForm.password"
          label="确认新密码"
          prop="confirmPassword"
        >
          <el-input
            v-model="editForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="submitting">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { assignmentService } from '../../services/assignmentService'

const loading = ref(false)
const submitting = ref(false)
const staffList = ref([])
const assignments = ref([])
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const addFormRef = ref()
const editFormRef = ref()

const addForm = ref({
  username: '',
  password: '',
  confirmPassword: ''
})

const editForm = ref({
  id: null,
  username: '',
  password: '',
  confirmPassword: ''
})

const validateAddPass = (rule, value, callback) => {
  if (value !== addForm.value.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const validateEditPass = (rule, value, callback) => {
  if (editForm.value.password && value !== editForm.value.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 4, message: '密码至少4位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateAddPass, trigger: 'blur' }
  ]
}

const editRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { min: 4, message: '密码至少4位', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateEditPass, trigger: 'blur' }
  ]
}

const getAssignmentCount = (staffId) => {
  return assignments.value.filter(a => a.staff?.id === staffId).length
}

const loadData = async () => {
  loading.value = true
  try {
    const [staffRes, assignRes] = await Promise.all([
      assignmentService.getAllStaffMembers(),
      assignmentService.getAllAssignments()
    ])
    staffList.value = staffRes
    assignments.value = assignRes
  } catch (error) {
    console.error('Failed to load staff data:', error)
    ElMessage.error('加载数据失败：' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  addForm.value = {
    username: '',
    password: '',
    confirmPassword: ''
  }
  addDialogVisible.value = true
}

const submitAdd = async () => {
  const valid = await addFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await assignmentService.createStaff({
      username: addForm.value.username,
      password: addForm.value.password,
      role: 'REPAIR_STAFF'
    })
    ElMessage.success('添加维修人员成功')
    addDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('Add staff failed:', error)
    ElMessage.error('添加失败：' + (error.response?.data?.message || error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

const openEditDialog = (row) => {
  editForm.value = {
    id: row.id,
    username: row.username,
    password: '',
    confirmPassword: ''
  }
  editDialogVisible.value = true
}

const submitEdit = async () => {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const updateData = {
      username: editForm.value.username
    }
    if (editForm.value.password) {
      updateData.password = editForm.value.password
    }

    await assignmentService.updateStaff(editForm.value.id, updateData)
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('Edit staff failed:', error)
    ElMessage.error('修改失败：' + (error.response?.data?.message || error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除维修人员 "${row.username}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    submitting.value = true
    try {
      await assignmentService.deleteStaff(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('Delete staff failed:', error)
      ElMessage.error('删除失败：' + (error.response?.data?.message || error.message || '未知错误'))
    } finally {
      submitting.value = false
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.log('Delete cancelled')
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
</style>