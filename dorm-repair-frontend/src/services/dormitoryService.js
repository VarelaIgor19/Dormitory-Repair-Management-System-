import api from './api'

export const dormitoryService = {
  // GET /api/dormitories - Public listing
  getAllDormitories() {
    return api.get('/dormitories')
  },

  // GET /api/dormitories/{id}
  getDormitoryById(id) {
    return api.get(`/dormitories/${id}`)
  },

  // POST /api/admin/dormitory/create - Admin only
  createDormitory(data) {
    console.log('API: Creating dormitory with data:', data)
    return api.post('/admin/dormitory/create', data)
  },

  // PUT /api/admin/dormitory/{id} - Admin only (Edit)
  updateDormitory(id, data) {
    console.log('API: Updating dormitory ID:', id, 'with data:', data)
    return api.put(`/admin/dormitory/${id}`, data)
  },

  // DELETE /api/admin/dormitory/{id} - Admin only (Delete)
  deleteDormitory(id) {
    console.log('API: Deleting dormitory ID:', id)
    return api.delete(`/admin/dormitory/${id}`)
  }
}