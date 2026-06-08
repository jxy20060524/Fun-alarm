import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('../views/LoginView.vue') },
  { path: '/alarms', component: () => import('../views/AlarmListView.vue'), meta: { auth: true } },
  { path: '/alarms/new', component: () => import('../views/AlarmEditView.vue'), meta: { auth: true } },
  { path: '/alarms/:id/edit', component: () => import('../views/AlarmEditView.vue'), meta: { auth: true } },
  { path: '/stats', component: () => import('../views/StatsView.vue'), meta: { auth: true } },
  { path: '/motivation', component: () => import('../views/MotivationView.vue'), meta: { auth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (to.meta.auth && !userStore.isLoggedIn) {
    return '/login'
  }
  if (to.path === '/login' && userStore.isLoggedIn) {
    return '/alarms'
  }
})

export default router
