import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useSettingsStore } from '../stores/settings'

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/deposit', name: 'deposit', component: () => import('../views/DepositView.vue') },
  { path: '/withdraw', name: 'withdraw', component: () => import('../views/WithdrawView.vue') },
  { path: '/transactions', name: 'transactions', component: () => import('../views/TransactionsView.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

const protectedNames = new Set(['deposit', 'withdraw', 'transactions'])
router.beforeEach((to) => {
  if (protectedNames.has(String(to.name ?? ''))) {
    const settings = useSettingsStore()
    if (!settings.accountId) {
      return { name: 'home' }
    }
  }
})

export default router
