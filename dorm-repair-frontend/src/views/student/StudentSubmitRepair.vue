<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>提交报修</span>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="选择宿舍" prop="dormitoryId">
          <el-select
            v-model="form.dormitoryId"
            placeholder="请选择校区和楼栋"
            style="width: 100%;"
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

        <el-form-item label="楼层" prop="floor">
          <el-select v-model="form.floor" placeholder="请选择楼层" style="width: 100%;">
            <el-option
              v-for="floor in floorOptions"
              :key="floor"
              :label="`${floor}层`"
              :value="floor"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="房间号" prop="roomNumber">
          <el-select v-model="form.roomNumber" placeholder="请选择房间号" style="width: 100%;">
            <el-option
              v-for="room in roomOptions"
              :key="room"
              :label="`${room}室`"
              :value="room"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="报修标题" prop="title">
          <el-input v-model="form.title" placeholder="简短描述问题，如：水龙头漏水" />
        </el-form-item>

        <el-form-item label="详细描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述问题情况..."
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" @click="submitRepair" :loading="submitting">
            提交报修
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { repairService } from '../../services/repairService'
import { dormitoryService } from '../../services/dormitoryService'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const dormitories = ref([])

const form = ref({
  dormitoryId: null,
  floor: null,
  roomNumber: null,
  title: '',
  description: ''
})

const floorOptions = computed(() => {
  return Array.from({ length: 20 }, (_, i) => i + 1)
})

const roomOptions = computed(() => {
  if (!form.value.floor) return []
  const base = form.value.floor * 100
  return Array.from({ length: 50 }, (_, i) => base + i + 1)
})

const rules = {
  dormitoryId: [{ required: true, message: '请选择宿舍', trigger: 'change' }],
  floor: [{ required: true, message: '请选择楼层', trigger: 'change' }],
  roomNumber: [{ required: true, message: '请选择房间号', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}

const handleDormitoryChange = () => {
  form.value.floor = null
  form.value.roomNumber = null
}

const loadDormitories = async () => {
  try {
    const data = await dormitoryService.getAllDormitories()
    dormitories.value = data
  } catch (error) {
    console.error('Failed to load dormitories:', error)
  }
}

const submitRepair = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const selectedDorm = dormitories.value.find(d => d.id === form.value.dormitoryId)

    const fullDescription = `【${selectedDorm?.campus || ''}-${selectedDorm?.building || ''}-${form.value.roomNumber}】${form.value.description}`

    await repairService.submitRepair(form.value.dormitoryId, {
      title: form.value.title,
      description: fullDescription,
      campus: selectedDorm?.campus,
      building: selectedDorm?.building,
      roomNumber: form.value.roomNumber
    })
    ElMessage.success('报修提交成功！')
    router.push('/student/my-repairs')
  } catch (error) {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  formRef.value.resetFields()
}

onMounted(loadDormitories)
</script>