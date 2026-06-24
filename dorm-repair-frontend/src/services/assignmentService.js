import api from './api'

export const assignmentService = {
  // ========== EXISTING METHODS (keep all your current ones) ==========

  // GET /api/admin/assignments/staff - Get all repair staff
  getAllStaff() {
    return api.get('/admin/assignments/staff')
  },

  getAllStudentsWithRepairs() {
    return api.get('/admin/assignments/students')
  },

  getAllAssignments() {
    return api.get('/admin/assignments')
  },

  createAssignment(repairOrderId, staffId) {
    return api.post('/admin/assignments', { repairOrderId, staffId })
  },

  deleteAssignment(id) {
    return api.delete(`/admin/assignments/${id}`)
  },

  getStaffAssignments() {
    return api.get('/repair/staff-assignments')
  },

  getAssignedRepairs() {
    return api.get('/repair/assigned-to-me')
  },

  getMyAssignment() {
    return api.get('/repair/my-assignment')
  },

  // ========== NEW: STAFF MANAGEMENT METHODS ==========

  // GET /api/admin/staff - Get all staff members (admin only)
  getAllStaffMembers() {
    return api.get('/admin/staff')
  },

  // POST /api/admin/staff - Create new staff member
  createStaff(data) {
    return api.post('/admin/staff', data)
  },

  // PUT /api/admin/staff/{id} - Update staff info
  updateStaff(id, data) {
    return api.put(`/admin/staff/${id}`, data)
  },

  // DELETE /api/admin/staff/{id} - Delete staff member
  deleteStaff(id) {
    return api.delete(`/admin/staff/${id}`)
  }
}