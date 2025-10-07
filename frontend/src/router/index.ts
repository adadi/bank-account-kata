import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/settings', name: 'settings', component: () => import('../views/SettingsView.vue') },
  { path: '/deposit', name: 'deposit', component: () => import('../views/DepositView.vue') },
  { path: '/withdraw', name: 'withdraw', component: () => import('../views/WithdrawView.vue') },
  { path: '/transactions', name: 'transactions', component: () => import('../views/TransactionsView.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
