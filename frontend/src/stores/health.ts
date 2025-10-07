import { defineStore } from 'pinia'
import { http } from '../services/http'

export type HealthStatus = 'UP' | 'DOWN' | 'UNKNOWN'

interface HealthState {
  status: HealthStatus
  details?: unknown
  lastCheckedAt?: number
}

export const useHealthStore = defineStore('health', {
  state: (): HealthState => ({ status: 'UNKNOWN' }),
  actions: {
    async checkHealth() {
      try {
        const { data } = await http.get('/actuator/health')
        const status = (data?.status as string | undefined) ?? 'UNKNOWN'
        this.status = status === 'UP' ? 'UP' : status === 'DOWN' ? 'DOWN' : 'UNKNOWN'
        this.details = data
      } catch (e) {
        this.status = 'DOWN'
      } finally {
        this.lastCheckedAt = Date.now()
      }
    },
  },
})

