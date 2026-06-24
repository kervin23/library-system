import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import Admin from '../views/Admin.vue'
import Manager from '../views/Manager.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Login },
    { path: '/register', component: Register },
    { path: '/home', component: Home },
    { path: '/admin', component: Admin },
    { path: '/manager', component: Manager },
  ]
})

export default router