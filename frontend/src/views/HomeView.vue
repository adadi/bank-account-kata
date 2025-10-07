<template>
  <h1>Bank Account Kata</h1>

  <section v-if="!hasAccountId">
    <h2>Configure your Account</h2>
    <SettingsForm />
  </section>

  <section v-else>
    <p>Welcome! Use the navigation to operate on your account.</p>
    <div class="card">
      <h3>Account Overview</h3>
      <p><strong>ID:</strong> {{ settings.accountId }}</p>
      <p v-if="account.loading">Loading balance…</p>
      <p v-else-if="account.error" class="error">{{ account.error }}</p>
      <p v-else><strong>Balance:</strong> {{ account.balance }} EUR</p>
      <button @click="refresh" :disabled="account.loading">Refresh</button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, computed, watch } from 'vue'
import { useHealthStore } from '../stores/health'
import { useSettingsStore } from '../stores/settings'
import { useAccountStore } from '../stores/account'
import SettingsForm from '../components/SettingsForm.vue'

const health = useHealthStore()
const settings = useSettingsStore()
const account = useAccountStore()

onMounted(() => {
  if (!health.lastCheckedAt) {
    health.checkHealth()
  }
})

const hasAccountId = computed(() => !!settings.accountId)

watch(hasAccountId, (ok) => {
  if (ok && settings.accountId) {
    account.fetch(settings.accountId)
  } else {
    account.clear()
  }
}, { immediate: true })

function refresh() {
  if (settings.accountId) account.fetch(settings.accountId)
}
</script>
