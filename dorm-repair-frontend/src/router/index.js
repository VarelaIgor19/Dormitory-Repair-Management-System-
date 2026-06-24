import { createRouter, createWebHistory } from 'vue-router'

// Public views
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'

// Layouts
import AdminLayout from '../layouts/AdminLayout.vue'
import StudentLayout from '../layouts/StudentLayout.vue'
import StaffLayout from '../layouts/StaffLayout.vue'

// Admin views
import AdminDashboard from '../views/admin/AdminDashboard.vue'
import AdminStatistics from '../views/admin/AdminStatistics.vue'
import AdminProfile from '../views/admin/AdminProfile.vue'
import AdminDormitories from '../views/admin/AdminDormitories.vue'
import AdminAssignments from '../views/admin/AdminAssignments.vue'
// NEW: Import AdminStaff
import AdminStaff from '../views/admin/AdminStaff.vue'

// Student views
import StudentDashboard from '../views/student/StudentDashboard.vue'
import StudentMyRepairs from '../views/student/StudentMyRepairs.vue'
import StudentSubmitRepair from '../views/student/StudentSubmitRepair.vue'
import StudentProfile from '../views/student/StudentProfile.vue'

// Staff views
import StaffDashboard from '../views/staff/StaffDashboard.vue'
import StaffRepairs from '../views/staff/StaffRepairs.vue'
import StaffProfile from '../views/staff/StaffProfile.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginView, name: 'Login' },
  { path: '/register', component: RegisterView, name: 'Register' },

  // Admin routes
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: AdminDashboard, name: 'AdminDashboard' },
      { path: 'statistics', component: AdminStatistics, name: 'AdminStatistics' },
      // NEW: Staff management route
      { path: 'staff', component: AdminStaff, name: 'AdminStaff' },
      { path: 'assignments', component: AdminAssignments, name: 'AdminAssignments' },
      { path: 'dormitories', component: AdminDormitories, name: 'AdminDormitories' },
      { path: 'profile', component: AdminProfile, name: 'AdminProfile' }
    ]
  },

  // Student routes
  {
    path: '/student',
    component: StudentLayout,
    meta: { requiresAuth: true, role: 'STUDENT' },
    children: [
      { path: '', redirect: '/student/dashboard' },
      { path: 'dashboard', component: StudentDashboard, name: 'StudentDashboard' },
      { path: 'my-repairs', component: StudentMyRepairs, name: 'StudentMyRepairs' },
      { path: 'submit-repair', component: StudentSubmitRepair, name: 'StudentSubmitRepair' },
      { path: 'profile', component: StudentProfile, name: 'StudentProfile' }
    ]
  },

  // Staff routes
  {
    path: '/staff',
    component: StaffLayout,
    meta: { requiresAuth: true, role: 'REPAIR_STAFF' },
    children: [
      { path: '', redirect: '/staff/dashboard' },
      { path: 'dashboard', component: StaffDashboard, name: 'StaffDashboard' },
      { path: 'repairs', component: StaffRepairs, name: 'StaffRepairs' },
      { path: 'profile', component: StaffProfile, name: 'StaffProfile' }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard (keep your existing one)
router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token')
  const savedRole = localStorage.getItem('role')

  if (!token && to.meta.requiresAuth) {
    next('/login')
    return
  }

  if (to.meta.requiresAuth && to.meta.role) {
    if (savedRole && savedRole !== to.meta.role) {
      if (savedRole === 'ADMIN') next('/admin/dashboard')
      else if (savedRole === 'REPAIR_STAFF') next('/staff/dashboard')
      else if (savedRole === 'STUDENT') next('/student/dashboard')
      else next('/login')
      return
    }
  }

  next()
})

export default router