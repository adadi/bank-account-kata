<script setup lang="ts">
import { computed, watchEffect } from 'vue'
import { useRouter } from 'vue-router'
import { useSettingsStore } from '../stores/settings'
import { useAccountStore } from '../stores/account'

const settings = useSettingsStore()
const account = useAccountStore()
const router = useRouter()

const show = computed(() => !!settings.accountId)
const text = computed(() => `ID: ${settings.accountId}`)
const balanceText = computed(() => account.balance !== undefined ? `Balance: ${account.balance}` : '')

watchEffect(() => {
  if (settings.accountId && account.balance === undefined && !account.loading && !account.error) {
    account.fetch(settings.accountId)
  }
})

function clear() {
  settings.clear()
  account.clear()
  router.push({ name: 'home' })
}
</script>

<template>
  <span v-if="show" class="badge info">
    {{ text }}<template v-if="balanceText"> — {{ balanceText }}</template>
    <button class="link" type="button" @click="clear" title="Logout / Clear ID">×</button>
  </span>
</template>

<style scoped>
.badge {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-weight: 600;
  border: 1px solid transparent;
}
.badge.info {
  background: #e8f1ff;
  color: #0a2a58;
  border-color: #b7d0ff;
}
.link {
  margin-left: 0.5rem;
  border: none;
  background: transparent;
  color: inherit;
  cursor: pointer;
  font-weight: 700;
}
</style>
