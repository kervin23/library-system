import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import Admin from '../views/Admin.vue'
import Manager from '../views/Manager.vue'
import Landing from '../views/Landing.vue'


const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Landing, meta: { guest: true } },
    { path: '/register', component: Register, meta: { guest: true } },
    { path: '/home', component: Home, meta: { requiresAuth: true, role: 'user' } },
    { path: '/admin', component: Admin, meta: { requiresAuth: true, role: 'admin' } },
    { path: '/manager', component: Manager, meta: { requiresAuth: true, role: 'manager' } },
    { path: '/login', component: Login, meta: { requiresAuth: false } },

  ]
})

router.beforeEach((to, from) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || '{}')

  if (to.meta.requiresAuth) {
    if (!token) {
      return '/'
    }
    if (to.meta.role && to.meta.role !== user.role && user.role !== 'admin') {
      return '/'
    }
    return true
  }

  if (to.meta.guest && token) {
    if (user.role === 'admin') return '/admin'
    if (user.role === 'manager') return '/manager'
    return '/home'
  }

  return true
})

export default router