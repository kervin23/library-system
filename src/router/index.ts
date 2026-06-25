import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import Admin from '../views/Admin.vue'
import Manager from '../views/Manager.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Login, meta: { guest: true } },
    { path: '/register', component: Register, meta: { guest: true } },
    { path: '/home', component: Home, meta: { requiresAuth: true, role: 'user' } },
    { path: '/admin', component: Admin, meta: { requiresAuth: true, role: 'admin' } },
    { path: '/manager', component: Manager, meta: { requiresAuth: true, role: 'manager' } },
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || '{}')

  // if route requires auth
  if (to.meta.requiresAuth) {
    if (!token) {
      next('/')
      return
    }
    // check role
    if (to.meta.role && to.meta.role !== user.role && user.role !== 'admin') {
      next('/')
      return
    }
    next()
    return
  }

  // if route is guest only (login/register) and user is already logged in
  if (to.meta.guest && token) {
    if (user.role === 'admin') next('/admin')
    else if (user.role === 'manager') next('/manager')
    else next('/home')
    return
  }

  next()
})

export default router