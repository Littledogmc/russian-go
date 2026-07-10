/*
 * Router config.
 * Lazy-loaded routes for all pages.
 * /study and /statistics are guarded: redirect to /login if not authenticated.
 */
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
    { path: '/study', name: 'study', component: () => import('../views/StudyView.vue') },
    {
      path: '/statistics',
      name: 'statistics',
      component: () => import('../views/StatisticsView.vue'),
    },
    { path: '/updates', name: 'updates', component: () => import('../views/UpdatesView.vue') },
    { path: '/login', name: 'login', component: () => import('../views/LoginView.vue') },
    { path: '/register', name: 'register', component: () => import('../views/RegisterView.vue') },
  ],
})

router.beforeEach((to, _from, next) => {
  const protectedRoutes = ['study', 'statistics']
  if (protectedRoutes.includes(String(to.name))) {
    const auth = useAuthStore()
    if (!auth.isLoggedIn) {
      next({ name: 'login', query: { redirect: to.fullPath } })
      return
    }
    if (auth.isBanned) {
      next({ name: 'login' })
      return
    }
  }
  next()
})

export default router
