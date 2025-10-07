<template>
  <form class="form" @submit.prevent="onSave">
    <label for="accountId">Account ID (UUID)</label>
    <div class="row">
      <input
        id="accountId"
        v-model="accountIdInput"
        type="text"
        inputmode="text"
        autocomplete="off"
        placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
      />
      <button type="submit">Login</button>
    </div>
    <i>example :11111111-1111-1111-1111-111111111111</i>
    <p v-if="error" class="error">{{ error }}</p>
  </form>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useSettingsStore } from '../stores/settings'
import { useAccountStore } from '../stores/account'
import { getAccount } from '../services/accounts'

const settings = useSettingsStore()
const accountIdInput = ref(settings.accountId ?? '')
const error = ref('')
const saving = ref(false)

const UUID_REGEX = /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/

async function onSave() {
  const value = accountIdInput.value.trim()
  if (!UUID_REGEX.test(value)) {
    error.value = 'UUID invalide'
    return
  }
  error.value = ''
  saving.value = true
  try {
    const { data } = await getAccount(value)
    // If ok, persist id and warm the account store
    settings.setAccountId(value)
    const account = useAccountStore()
    account.accountId = data.accountId
    account.balance = data.balance
    account.lastFetchedAt = Date.now()
    account.error = undefined
  } catch (e: any) {
    if (e?.response?.status === 404) {
      error.value = 'Compte introuvable'
    } else {
      error.value = e?.message ?? 'Erreur inconnue'
    }
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.form {
  margin: 1rem auto;
  max-width: 640px;
  text-align: left;
}
.row {
  display: flex;
  gap: 0.5rem;
}
input[type="text"] {
  flex: 1;
  padding: 0.5rem 0.75rem;
  border-radius: 6px;
  border: 1px solid #ccc;
  font-size: 1rem;
}
.error {
  color: #b00020;
  margin-top: 0.5rem;
}
</style>
