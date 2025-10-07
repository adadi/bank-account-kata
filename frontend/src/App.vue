<script setup lang="ts">
import ApiStatusBanner from './components/ApiStatusBanner.vue'
</script>

<template>
  <ApiStatusBanner />
  <nav class="nav">
    <router-link to="/">Home</router-link>
    <span> | </span>
    <router-link to="/deposit">Deposit</router-link>
    <span> | </span>
    <router-link to="/withdraw">Withdraw</router-link>
    <span> | </span>
    <router-link to="/transactions">Transactions</router-link>
    <span> | </span>
    <router-link to="/settings">Settings</router-link>
  </nav>
  <main class="view">
    <router-view />
  </main>
  
</template>

<style scoped>
.nav {
  display: flex;
  gap: 0.25rem;
  padding: 1rem 0;
}
.view { padding-top: 0.5rem; }
</style>
