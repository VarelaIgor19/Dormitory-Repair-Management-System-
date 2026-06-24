import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor - Add JWT token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// Response interceptor - Handle errors
api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
      ElMessage.error('Session expired. Please login again.')
    } else if (error.response?.status === 403) {
      ElMessage.error('Permission denied')
    } else {
      ElMessage.error(error.response?.data || 'An error occurred')
    }
    return Promise.reject(error)
  }
)

export default api