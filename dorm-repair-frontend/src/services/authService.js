import api from './api'

export const authService = {
  // POST /api/auth/login
  login(data) {
    return api.post('/auth/login', data)
  },

  // POST /api/auth/register
  register(data) {
    return api.post('/auth/register', data)
  },

  // POST /api/auth/logout
  logout() {
    return api.post('/auth/logout')
  },

  // GET /api/auth/me
  getMe() {
    return api.get('/auth/me')
  }
}