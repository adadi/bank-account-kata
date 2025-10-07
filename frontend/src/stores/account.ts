import { defineStore } from 'pinia'
import { getAccount } from '../services/accounts'
import type { UUID } from '../services/accounts'

interface AccountState {
  accountId?: UUID
  balance?: number
  loading: boolean
  error?: string
  lastFetchedAt?: number
}

export const useAccountStore = defineStore('account', {
  state: (): AccountState => ({ loading: false }),
  actions: {
    async fetch(accountId: UUID) {
      if (!accountId) return
      this.loading = true
      this.error = undefined
      try {
        const { data } = await getAccount(accountId)
        this.accountId = data.accountId
        this.balance = data.balance
        this.lastFetchedAt = Date.now()
      } catch (e: any) {
        this.error = e?.message ?? 'Failed to fetch account'
      } finally {
        this.loading = false
      }
    },
    clear() {
      this.accountId = undefined
      this.balance = undefined
      this.error = undefined
      this.lastFetchedAt = undefined
      this.loading = false
    },
  },
})

