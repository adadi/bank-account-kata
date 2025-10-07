<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { useHealthStore } from '../stores/health'

const store = useHealthStore()

onMounted(() => {
  if (!store.lastCheckedAt) {
    store.checkHealth()
  }
})

const statusText = computed(() => store.status)
const statusClass = computed(() => {
  return {
    UP: 'ok',
    DOWN: 'bad',
    UNKNOWN: 'unknown',
  }[store.status]
})
</script>

<template>
  <span class="badge" :class="statusClass">API: {{ statusText }}</span>
  
</template>

<style scoped>
.badge {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-weight: 600;
}
.badge.ok { background: #e6ffed; color: #0a582a; border: 1px solid #bdf0c9; }
.badge.bad { background: #ffecec; color: #7a0b0b; border: 1px solid #f3b2b2; }
.badge.unknown { background: #f6f6f6; color: #555; border: 1px solid #ddd; }
</style>

