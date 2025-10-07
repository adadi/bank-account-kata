import { defineStore } from 'pinia'

export interface SettingsState {
  accountId: string
}

export const useSettingsStore = defineStore('settings', {
  state: (): SettingsState => ({
    accountId: '',
  }),
  actions: {
    setAccountId(id: string) {
      this.accountId = id
    },
    clear() {
      this.accountId = ''
    },
  },
})

