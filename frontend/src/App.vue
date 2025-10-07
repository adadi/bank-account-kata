<script setup lang="ts">
import ApiStatusBanner from './components/ApiStatusBanner.vue'
import AccountIdBadge from './components/AccountIdBadge.vue'
import { computed } from 'vue'
import { useSettingsStore } from './stores/settings'

const settings = useSettingsStore()
const hasAccountId = computed(() => !!settings.accountId)
</script>

<template>
  <ApiStatusBanner />
  <header class="header">
    <nav class="nav">
      <router-link to="/">Home</router-link>
      <span> | </span>

      <template v-if="hasAccountId">
        <router-link to="/deposit">Deposit</router-link>
        <span> | </span>
        <router-link to="/withdraw">Withdraw</router-link>
        <span> | </span>
        <router-link to="/transactions">Transactions</router-link>
      </template>
      <template v-else>
        <span class="disabled">Deposit</span>
        <span> | </span>
        <span class="disabled">Withdraw</span>
        <span> | </span>
        <span class="disabled">Transactions</span>
      </template>
    </nav>
    <AccountIdBadge />
  </header>
  <main class="view">
    <router-view />
  </main>
  
</template>

<style scoped>
.header {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  justify-content: space-between;
}
.nav {
  display: flex;
  gap: 0.25rem;
  padding: 1rem 0;
}
.disabled {
  opacity: 0.55;
  pointer-events: none;
}
.view { padding-top: 0.5rem; }
</style>
