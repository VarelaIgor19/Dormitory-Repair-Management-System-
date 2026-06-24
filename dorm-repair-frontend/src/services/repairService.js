import api from './api'

export const repairService = {
  // ========== STUDENT ENDPOINTS ==========

  // GET /api/repair/my-orders
  getMyOrders() {
    return api.get('/repair/my-orders')
  },

  // POST /api/repair/submit?dormitoryId={id}
  submitRepair(dormitoryId, data) {
    return api.post(`/repair/submit?dormitoryId=${dormitoryId}`, data)
  },

  // PUT /api/repair/{orderId} - Edit own pending order
  editRepair(orderId, data) {
    return api.put(`/repair/${orderId}`, data)
  },

  // ========== STAFF/ADMIN ENDPOINTS ==========

  // GET /api/repair/list
  getAllOrders() {
    return api.get('/repair/list')
  },

  // GET /api/repair/{orderId}
  getOrderById(orderId) {
    return api.get(`/repair/${orderId}`)
  },

  // GET /api/repair/status?status={status}
  getOrdersByStatus(status) {
    return api.get('/repair/status', { params: { status } })
  },

  // POST /api/repair/status?orderId={id}&status={status}
  updateStatus(orderId, status) {
    return api.post(`/repair/status?orderId=${orderId}&status=${status}`)
  },

  // DELETE /api/repair/{orderId}
  deleteOrder(orderId) {
    return api.delete(`/repair/${orderId}`)
  }
}