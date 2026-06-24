import api from './api'

export const statisticsService = {
  // GET /api/admin/statistics/monthly
  getMonthlyStats() {
    return api.get('/admin/statistics/monthly')
  },

  // GET /api/admin/statistics/daily
  getDailyReport() {
    return api.get('/admin/statistics/daily')
  },

  // GET /api/admin/statistics/dormitory
  getDormitoryStats() {
    return api.get('/admin/statistics/dormitory')
  },

  // GET /api/admin/report/status
  getStatusReport() {
    return api.get('/admin/report/status')
  }
}